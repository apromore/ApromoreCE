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

package org.apromore.plugin.portal.prodrift;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.apromore.model.LogSummaryType;
import org.apromore.prodrift.config.DriftDetectionSensitivity;
import org.apromore.prodrift.driftdetector.ControlFlowDriftDetector_EventStream;
import org.apromore.prodrift.model.ProDriftDetectionResult;
import org.apromore.prodrift.util.LogStreamer;
import org.apromore.prodrift.util.XLogManager;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;
import org.apromore.service.prodrift.ProDriftDetectionException;
import org.apromore.service.prodrift.ProDriftDetectionService;
import org.deckfour.xes.model.XLog;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class ProDriftController {

    private static final long serialVersionUID = 1L;
    private final PortalContext portalContext;
    private final ProDriftDetectionService proDriftDetectionService;
    private Window proDriftW;

    private Button logFileUpload;
    private Listbox driftDetMechLBox;
    private Checkbox gradDriftCBox;
    private Intbox winSizeIntBox;
    private Listbox fWinOrAwinLBox;
    private Doublespinner noiseFilterSpinner;
    private Button OKbutton;
    private Button cancelButton;

    //    private byte[] logByteArray = null;
    private XLog xlog = null;
    private XLog eventStream = null;
    private String logFileName = null;

    private int caseCount = 0;
    private int eventCount = 0;
    private int activityCount = 0;

    private int defaultWinSizeRuns = 100;
    private int defaultWinSizeEvents = 5000;
    private int winSizeDividedBy = 10;

    private EventLogService eventLogService = null;
    private LogSummaryType logSummaryType = null;

    private boolean isRunning = false;

    private MutableBoolean subLogsSaved = new MutableBoolean(false);


    /**
     * @throws IOException if the <code>prodrift.zul</code> template can't be read from the classpath
     */
    public ProDriftController(PortalContext portalContext, ProDriftDetectionService proDriftDetectionService,
                              EventLogService eventLogService, Set<LogSummaryType> selectedLogSummaryType) throws IOException {
        this.portalContext = portalContext;
        this.proDriftDetectionService = proDriftDetectionService;

        this.proDriftW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/prodrift.zul", null, null);
        this.proDriftW.setTitle("ProDrift: Set Parameters.");

        Intbox maxWinValueRunsIntBox = (Intbox) proDriftW.getFellow("maxWinValueRuns");
        maxWinValueRunsIntBox.setValue(defaultWinSizeRuns);

        Intbox maxWinValueEventsSynIntBox = (Intbox) proDriftW.getFellow("maxWinValueEvents");
        maxWinValueEventsSynIntBox.setValue(defaultWinSizeEvents);

        Intbox winSizeCoefficientIntBoX = (Intbox) proDriftW.getFellow("winSizeCoefficient");
        winSizeCoefficientIntBoX.setValue(ControlFlowDriftDetector_EventStream.winSizeCoefficient);

        this.driftDetMechLBox = (Listbox) proDriftW.getFellow("driftDetMechLBox");
        this.gradDriftCBox = (Checkbox) proDriftW.getFellow("gradDriftCBox");
        this.winSizeIntBox = (Intbox) proDriftW.getFellow("winSizeIntBox");
        this.fWinOrAwinLBox = (Listbox) proDriftW.getFellow("fWinOrAwinLBox");
        this.noiseFilterSpinner = (Doublespinner) proDriftW.getFellow("noiseFilterSpinner");

        this.logFileUpload = (Button) this.proDriftW.getFellow("logFileUpload");


        this.eventLogService = eventLogService;
        Map<XLog, String> logs = new HashMap<>();

        for(LogSummaryType logType : selectedLogSummaryType)
        {
            logs.put(eventLogService.getXLog(logType.getId()), logType.getName());
            logSummaryType = logType;
        }

        if(logs.size() > 0)
        {

//            Row logUploadRow = (Row) this.proDriftW.getFellow("logF");
//            logUploadRow.setVisible(false);


            if(logs.size() > 1)
            {

                showError("Please select only one log!");

            }else
            {

                this.logFileUpload.setVisible(false);

                Map.Entry<XLog, String> xl_entry = logs.entrySet().iterator().next();
                String xl_name = xl_entry.getValue() + ".xes.gz";
                XLog xl = xl_entry.getKey();

                initializeLogVars(xl, null, xl_name);

            }

        }

        this.logFileUpload.addEventListener("onUpload", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                UploadEvent uEvent = (UploadEvent) event;
                org.zkoss.util.media.Media logFileMedia = uEvent.getMedia();

                initializeLogVars(null, logFileMedia.getStreamData(), logFileMedia.getName());

            }
        });


//        this.winSize = (Row) this.proDriftW.getFellow("winsize");
//        Row fWinOrAwinChoiceR = (Row) this.proDriftW.getFellow("fWinOrAwinChoice");
//        this.fWinOrAwin = (Listbox) fWinOrAwinChoiceR.getFirstChild().getNextSibling();
//        Listitem listItem = new Listitem();
//        listItem.setLabel("Adaptive Window");
//        this.fWinOrAwin.appendChild(listItem);
//        listItem.setSelected(true);
//        listItem = new Listitem();
//        listItem.setLabel("Fixed Window");
//        this.fWinOrAwin.appendChild(listItem);

        this.OKbutton = (Button) this.proDriftW.getFellow("proDriftOKButton");
        this.cancelButton = (Button) this.proDriftW.getFellow("proDriftCancelButton");

        this.OKbutton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                proDriftDetector();
            }
        });
        this.OKbutton.addEventListener("onOK", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                proDriftDetector();
            }
        });
        this.cancelButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                cancel();
            }
        });


//        Popup popup = (Popup) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/prodriftcharacterizationpopup.zul", null, null);
//
//        ListModel<String> cs = new ListModelList<String>();
//
//        ((ListModelList<String>) cs).add("gggggggggg gggggggggggg wwwwww wwwwww wwww wwwwww wwww");
//
//        ((ListModelList<String>) cs).add("gggggggggg gggggggggggg wwwwww wwwwww wwww wwwwww wwww");
////
////        Grid grid = (Grid) popup.getFellow("myGrid");
////        grid.setModel(cs);
////
//        Listbox grid = (Listbox) popup.getFellow("myBox");
//        grid.setModel(cs);
//
//        cancelButton.setPopup(popup);



        this.proDriftW.doModal();
    }

    private void initializeLogVars(XLog xl, InputStream is, String logName) {

        final Label l = (Label) this.proDriftW.getFellow("fileName");
//        boolean valild = false;
        xlog = xl;

        int winSize_timeBased = 0;

        if(is != null) {
            try {
                xlog = XLogManager.validateLog(is, logName);
            } catch (org.apromore.prodrift.exception.ProDriftDetectionException e) {
                l.setStyle("color: red");
                l.setValue("Unacceptable Log Format.");
            }
        }

        if (xlog == null) {

            l.setStyle("color: red");
            l.setValue("Unacceptable Log Format.");

//                    showError("Please select a log file(.xml, .mxml, .xes, .mxml.gz, .xes.gz)");

        } else {

            try {
                StringBuilder activityCountStr = new StringBuilder();
                StringBuilder winSizeStr = new StringBuilder();

                this.eventStream = LogStreamer.logStreamer(xlog, activityCountStr, winSizeStr, logName);

                caseCount = xlog.size();
                eventCount = eventStream.size();
                activityCount = Integer.parseInt(activityCountStr.toString());

                winSize_timeBased = Integer.parseInt(winSizeStr.toString());

            }catch (NumberFormatException ex) {}

            l.setStyle("color: blue");
            l.setValue(logName + " (Cases=" + caseCount + ", Activities=" + activityCount + ", Events~" + eventCount + ")");

//            if(xl != null)
//                xlog = xl;

            logFileName = logName;

            setDefaultWinSizes(winSize_timeBased);

        }

    }

    private void setDefaultWinSizes(int winSize_timeBased) {

        Intbox maxWinValueRunsIntBoX = (Intbox) proDriftW.getFellow("maxWinValueRuns");
        Intbox maxWinValueEventsIntBoX = (Intbox) proDriftW.getFellow("maxWinValueEvents");

        Intbox activityCountIntBoX = (Intbox) proDriftW.getFellow("activityCount");
        activityCountIntBoX.setValue(activityCount);

        if (winSize_timeBased < 100)
            this.defaultWinSizeEvents = Math.max(winSize_timeBased, activityCount * activityCount * 5);
        else
            this.defaultWinSizeEvents = winSize_timeBased;

        if(defaultWinSizeRuns > caseCount / 4)
            defaultWinSizeRuns = caseCount / 4;

        if(defaultWinSizeEvents > eventCount / 4)
            defaultWinSizeEvents = eventCount / 4;

//        if (caseCount / winSizeDividedBy < defaultWinSizeRuns) {
//
//            maxWinValueRunsIntBoX.setValue(roundNum(caseCount / winSizeDividedBy));
//
//        } else {
//
            maxWinValueRunsIntBoX.setValue(defaultWinSizeRuns);
//
//        }

//        if ((eventCount / winSizeDividedBy) < desiredWinSizeEvents) {
//
//            maxWinValueEventsIntBoX.setValue(roundNum(eventCount / winSizeDividedBy));
//
//        } else {
//
            maxWinValueEventsIntBoX.setValue(defaultWinSizeEvents);
//        }


        Listbox driftDetMechLBox = (Listbox) proDriftW.getFellow("driftDetMechLBox");
        boolean isEventBased = driftDetMechLBox.getSelectedItem().getLabel().startsWith("E") ? true : false;

//        boolean isSynthetic = true;
//        if(activityCount < activityLimit)
//            isSynthetic = true;
//        else
//            isSynthetic = false;


        Intbox winSizeIntBox = (Intbox) proDriftW.getFellow("winSizeIntBox");

        if (isEventBased)
        {
            /*if (isSynthetic) {
                ((Listitem)proDriftW.getFellow("synLog")).setSelected(true);

                ((Listitem)proDriftW.getFellow("ADWIN")).setSelected(true);
                *//*if((activityCount * activityCount * ControlFlowDriftDetector_EventStream.winSizeCoefficient)
                        > maxWinValueEventsSynIntBoX.getValue())
                    ((Listitem)proDriftW.getFellow("FWIN")).setSelected(true);
                else
                    ((Listitem)proDriftW.getFellow("ADWIN")).setSelected(true);*//*

                ((Doublespinner) proDriftW.getFellow("noiseFilterSpinner")).setValue(5.0);
                winSizeIntBox.setValue(maxWinValueEventsSynIntBoX.getValue());
            }else {*/
//                ((Listitem)proDriftW.getFellow("reLog")).setSelected(true);
            ((Listitem) proDriftW.getFellow("ADWIN")).setSelected(true);
            ((Doublespinner) proDriftW.getFellow("noiseFilterSpinner")).setValue(10.0);
            winSizeIntBox.setValue(maxWinValueEventsIntBoX.getValue());
//            }
        }else
            winSizeIntBox.setValue(maxWinValueRunsIntBoX.getValue());

        }

    int roundNum(int a)
    {

        return (a/100)*100 > 0 ? (a/100)*100 : 1;

    }

    public void showError(String error) {
        //portalContext.getMessageHandler().displayInfo(error);
        //Label errorLabel = (Label) this.proDriftW.getFellow("errorLabel");
        //errorLabel.setValue(error);

        Clients.showNotification(error, "error", proDriftW, "top_left", 3000, true);
    }

    protected void cancel() {

        this.proDriftW.detach();
        if(subLogsSaved.isTrue())
            portalContext.refreshContent();

    }

    protected void proDriftDetector() throws InterruptedException, ProDriftDetectionException {

        if (xlog != null)
        {

            int winSize = winSizeIntBox.getValue();

            boolean isEventBased = driftDetMechLBox.getSelectedItem().getLabel().startsWith("E") ? true : false;
            Session sess = Sessions.getCurrent();
            sess.setAttribute("isEventBased", isEventBased);

            if((isEventBased && winSize <= eventCount / 2) || (!isEventBased && winSize <= caseCount / 2))
            {


                boolean withGradual = gradDriftCBox.isChecked() ? true : false;

                boolean isAdwin = fWinOrAwinLBox.getSelectedItem().getLabel().startsWith("A") ? true : false;

                float noiseFilterPercentage = (float) noiseFilterSpinner.getValue().doubleValue();

                boolean withConflict = /*isSynthetic ? true : */false;

                Checkbox withCharacterizationCBox = (Checkbox) proDriftW.getFellow("withCharacterizationCBox");
                boolean withCharacterization = withCharacterizationCBox.isChecked() ? true : false;

                Spinner cummulativeChangeSpinner = (Spinner) proDriftW.getFellow("cummulativeChangeSpinner");
                int cummulativeChange = cummulativeChangeSpinner.getValue().intValue();

//                    Rengine engineR = null;
//                    Object obj = sess.getAttribute("engineR");
//                    if(obj == null) {
//                        engineR = new Rengine(new String[]{"--no-save"}, false, null);
//                        sess.setAttribute("engineR", engineR);
//                    }else
//                        engineR = (Rengine) obj;


                OKbutton.setDisabled(true);

                ProDriftDetectionResult result = proDriftDetectionService.proDriftDetector(xlog, eventStream, logFileName,
                        isEventBased, withGradual, winSize, activityCount, isAdwin, noiseFilterPercentage, DriftDetectionSensitivity.Low,
                        withConflict, withCharacterization, cummulativeChange);

                proDriftShowResults_(result, isEventBased, xlog, logFileName, withCharacterization, cummulativeChange);

                OKbutton.setDisabled(false);

            }else
            {

                if(isEventBased)
                    showError("Window size cannot be bigger than " + eventCount / 2);
                else
                    showError("Window size cannot be bigger than " + caseCount / 2);

            }
        }else
        {
            showError("Please select a log file first.");
        }

    }


    protected void proDriftShowResults_(ProDriftDetectionResult result, boolean isEventBased, XLog xlog, String logFileName,
                                        boolean withCharacterization, int cummulativeChange) {
        try {

            new ProDriftShowResult(portalContext, result, isEventBased, xlog, logFileName, withCharacterization, cummulativeChange,
                    eventLogService, logSummaryType, subLogsSaved);

        } catch (IOException | SuspendNotAllowedException e) {
            showError(e.getMessage());
        }
    }

}
