/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.APM.compliance;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import ee.ut.eventstr.comparison.differences.ModelAbstractions;
import hub.top.petrinet.PetriNet;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.dialogController.SelectDynamicListController;
import org.apromore.service.apm.compliance.APMService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.*;

/**
 * Created by conforti on 10/04/15.
 */
public class APMController {

    private Window resultsWin;
    private Button closeButton;
    private Rows rows;
    private Grid grid;
    private String nativeType = "BPMN 2.0";

    private PetriNet net;

    private APMService apmService;

    private PortalContext portalContext;
    private Window enterLogWin;
    private Button uploadLog;
    private Button cancelButton;
    private Button okButton;
    private SelectDynamicListController domainCB;

    private Textbox inputText;

    private Label l;

    private String specificationString = null;
    private String specificationFileName = null;

    private org.zkoss.util.media.Media specificationFile = null;

    public APMController(PortalContext portalContext, APMService apmService) {
        this.apmService = apmService;
        this.portalContext = portalContext;
    }

    public void apmVerify(PetriNet net) {
        this.net = net;
        popupXML();
    }

    public void makeResultWindows(String[] results){
        try {
            this.resultsWin = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/results.zul", null, null);
            this.closeButton = (Button) this.resultsWin.getFellow("closeBtn");

            this.closeButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    closeResults();
                }
            });

            this.grid = (Grid) this.resultsWin.getFellow("gridMD");
            this.rows = this.grid.getRows();

            ArrayList<String> gridData = new ArrayList<String>();

            for(int i =0; i < results.length; i++)
                gridData.add(results[i]);

            ListModelList listModel = new ListModelList(gridData);
            RowRenderer rowRenderer = new SimpleRenderer();

            grid.setRowRenderer(rowRenderer);
            grid.setModel(listModel);

            this.resultsWin.doModal();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void verify(){
        if(specificationString == null){
            String text = this.inputText.getValue();
            StringTokenizer token = new StringTokenizer(text, "\n");
            String[] specifications = new String[token.countTokens()];
            int i = 0;
            while(token.hasMoreTokens()) {
                String tok= token.nextToken();
                System.out.println(tok);
                specifications[i++] = tok;
            }

            System.out.println(" ----- " + specifications.length);

            System.out.println(Arrays.asList(specifications).toString());
            makeResultWindows(apmService.getVerification(this.net, specifications));
        }
        else
            makeResultWindows(apmService.getVerification(this.net, specificationString));

    }

    public void popupXML() {
        try {
            List<String> domains = new ListModelList<>();
            this.domainCB = new SelectDynamicListController(domains);
            this.domainCB.setReference(domains);
            this.domainCB.setAutodrop(true);
            this.domainCB.setWidth("85%");
            this.domainCB.setHeight("100%");
            this.domainCB.setAttribute("hflex", "1");

            this.enterLogWin = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/compare.zul", null, null);

            this.l = (Label) this.enterLogWin.getFellow("fileName");
            this.uploadLog = (Button) this.enterLogWin.getFellow("specificationUpload");
            this.inputText = (Textbox) this.enterLogWin.getFellow("specificationTB");
            this.cancelButton = (Button) this.enterLogWin.getFellow("cancelButton");
            this.okButton = (Button) this.enterLogWin.getFellow("oKButton");

            this.uploadLog.addEventListener("onUpload", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    uploadFile((UploadEvent) event);
                }
            });

            this.cancelButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    cancel();
                }
            });
            this.okButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    verify();
                    cancel();
                }
            });
            this.enterLogWin.doModal();
        }catch (IOException e) {
            Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    protected void cancel() {
        this.enterLogWin.detach();
    }

    protected void closeResults(){
        this.resultsWin.detach();
    }

//    protected void downloadResults(String xmlString){
//        Filedownload.save(xmlString.getBytes(Charset.forName("UTF-8")), "text/plain",
//                "feedback.txt");
//    }

    private void uploadFile(UploadEvent event) {
        specificationFile = event.getMedia();
        l.setStyle("color: blue");
        l.setValue(specificationFile.getName());
        byte[] byteStr = specificationFile.getByteData();
        try {
            specificationString = new String(byteStr, "UTF-8");
        }catch(Exception e){
            e.printStackTrace();
        }

        specificationFileName = specificationFile.getName();
    }

    public class SimpleRenderer implements RowRenderer<String> {

        public void render(Row row, String data, int index) {
            // the data append to each row with simple label
            row.appendChild(new Label(Integer.toString(index)));
            row.appendChild(new Label(data));

            if(data.contains("is always") || data.contains("exists a path") || data.contains("never occur") || data.contains("are concurrent"))
                row.setStyle("background:#C4E0B2;!important;Border: #E3E3E3");
            else
                row.setStyle("background:#f0997c;!important;Border: #E3E3E3");

            // we create a thumb up/down comment to each row
        }
    }
}
