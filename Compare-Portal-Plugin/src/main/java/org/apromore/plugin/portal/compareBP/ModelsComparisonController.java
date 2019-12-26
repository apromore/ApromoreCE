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
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.compareBP;

// Java 2 Standard packages
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

// Third party packages
import org.apromore.plugin.editor.EditorPlugin;
import org.apromore.portal.context.EditorPluginResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

// Local packages
import org.apromore.model.EditSessionType;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.PluginMessages;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.ConfigBean;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.dto.SignavioSession;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.util.StreamUtil;

import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.MainController;

/**
 * Controller for a pair of signavio editor windows.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class ModelsComparisonController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelsComparisonController.class.getCanonicalName());

    private MainController mainC;

    private EditSessionType editSession1, editSession2;
    private ProcessSummaryType process, process2;
    private VersionSummaryType version, version2;
    private Set<RequestParameterType<?>> params;
    private int m1PESSize, m2PESSize;
    private JSONObject differences;

    public ModelsComparisonController() {
        super();

        String id = Executions.getCurrent().getParameter("id");
        if (id != null) {
            SignavioSession session = UserSessionManager.getEditSession(id);
            if (session == null) {
                throw new AssertionError("No edit session associated with id " + id);
            }

            editSession1 = session.getEditSession();
            editSession2 = session.getEditSession2();
            mainC = session.getMainC();
            process = session.getProcess();
            version = session.getVersion();
            process2 = session.getProcess2();
            version2 = session.getVersion2();
            params =  session.getParams();
        }

        Map<String, Object> param = new HashMap<>();
        try {
            String title = editSession1.getProcessName() + " (" + editSession1.getNativeType() + ")";
            if (editSession2 != null) {
                title += " vs " + editSession2.getProcessName() + " (" + editSession2.getNativeType() + ")";
            }
            this.setTitle(title);

            ExportFormatResultType exportResult1 =
                    getService().exportFormat(editSession1.getProcessId(),
                            editSession1.getProcessName(),
                            editSession1.getOriginalBranchName(),
                            editSession1.getCurrentVersionNumber(),
                            editSession1.getNativeType(),
                            editSession1.getAnnotation(),
                            editSession1.isWithAnnotation(),
                            editSession1.getUsername(),
                            params);

            title = editSession1.getProcessName();
            PluginMessages pluginMessages = exportResult1.getMessage();

            String JSON_DATA = "jsonData";
            String data1 = StreamUtil.convertStreamToString(exportResult1.getNative().getInputStream());
            param.put(JSON_DATA, data1.replace("\n", " ").replace("'", "\\u0027").trim());
            param.put("url", getURL(editSession1.getNativeType()));
            param.put("importPath", getImportPath(editSession1.getNativeType()));
            param.put("exportPath", getExportPath(editSession1.getNativeType()));
            param.put("editor", config.getSiteEditor());

            if (editSession2 != null) {
                ExportFormatResultType exportResult2 =
                    getService().exportFormat(editSession2.getProcessId(),
                            editSession2.getProcessName(),
                            editSession2.getOriginalBranchName(),
                            editSession2.getCurrentVersionNumber(),
                            editSession2.getNativeType(),
                            editSession2.getAnnotation(),
                            editSession2.isWithAnnotation(),
                            editSession2.getUsername(),
                            params);

                String data2 = StreamUtil.convertStreamToString(exportResult2.getNative().getInputStream());
                param.put("jsonData2", data2.replace("\n", " ").replace("'", "\\u0027").trim());

                title += " differences with " + editSession2.getProcessName();
                pluginMessages = null;
                if (exportResult1.getMessage() != null) {
                    pluginMessages = new PluginMessages();
                    pluginMessages.getMessage().addAll(exportResult1.getMessage().getMessage());
                }
                if (exportResult2.getMessage() != null) {
                    if (pluginMessages == null) {
                        pluginMessages = new PluginMessages();
                    }
                    pluginMessages.getMessage().addAll(exportResult2.getMessage().getMessage());
                }
            }

            this.setTitle(title);
//            mainC.showPluginMessages(pluginMessages);
            if (mainC != null) {
                mainC.showPluginMessages(pluginMessages);
            }
//            mainC.showPluginMessages(pluginMessages);

            for (RequestParameterType<?> requestParameter: params) {
                switch (requestParameter.getId()) {
                case "m1_differences_json":
                    param.put("differences", (String) requestParameter.getValue());
                    this.differences = new JSONObject((String) requestParameter.getValue());
                    //LOGGER.info("Request parameter \"differences\" set to " + requestParameter.getValue());
                    break;
                case "m1_pes_size":
                    this.m1PESSize = (Integer) requestParameter.getValue();
                    break;
                case "m2_pes_size":
                    this.m2PESSize = (Integer) requestParameter.getValue();
                    break;
                default:
                    LOGGER.info("Unsupported request parameter \"" + requestParameter.getId() + "\" with value " + requestParameter.getValue());
                }
            }

//            if (editSession1.getAnnotation() == null) {
//                param.put("doAutoLayout", "true");
//            } else if (process.getOriginalNativeType() != null && process.getOriginalNativeType().equals(editSession1.getNativeType())) {
                param.put("doAutoLayout", "false");
//            } else {
//                if (editSession1.isWithAnnotation()) {
//                    param.put("doAutoLayout", "false");
//                } else {
//                    param.put("doAutoLayout", "true");
//                }
//            }


            List<EditorPlugin> editorPlugins = EditorPluginResolver.resolve();
            param.put("plugins", editorPlugins);

            Executions.getCurrent().pushArg(param);

        } catch (Exception e) {
            LOGGER.error("",e);
            e.printStackTrace();
        }

/*
        this.addEventListener("onSave", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws InterruptedException {
                try {
                    new SaveAsDialogController(process, version, editSession1, true, eventToString(event));
                } catch (ExceptionFormats exceptionFormats) {
                    LOGGER.error("Error saving model.", exceptionFormats);
                }
            }
        });
        this.addEventListener("onSaveAs", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws InterruptedException {
                try {
                    new SaveAsDialogController(process, version, editSession1, false, eventToString(event));
                } catch (ExceptionFormats exceptionFormats) {
                    LOGGER.error("Error saving model.", exceptionFormats);
                }
            }
        });
*/
    }

    public void onCreate() throws InterruptedException {
//        Treechildren treechildren = (Treechildren) this.getFellowIfAny("differences");
        Toolbar treechildren = (Toolbar) this.getFellowIfAny("differences");
        if (treechildren != null) {
            Component parent = (Component) treechildren.getParent();

            // Remove any pre-extant list items
            Component sibling = treechildren.getNextSibling();
            while (sibling != null) {
                parent.removeChild(sibling);
                sibling = treechildren.getNextSibling();
            }

            Label m1Label = (Label) this.getFellow("m1-pes-size");
            m1Label.setValue(Integer.toString(m1PESSize));

            Label m2Label = (Label) this.getFellow("m2-pes-size");
            m2Label.setValue(Integer.toString(m2PESSize));

            // Add the current differences
            try {
                final DecimalFormat rankingFormat = new DecimalFormat("##%");

                JSONArray array = this.differences.getJSONArray("differences");
                for (int i=0; i < array.length(); i++) {
                    JSONObject difference = array.getJSONObject(i);

                    // Add UI for this difference
                    String clean1 = "oryxEditor1.cleanDifferences()";
                    String clean2 = "oryxEditor2.cleanDifferences()";

//                    Treeitem item = new Treeitem();
//
//                    Treerow row = new Treerow();
//                    item.appendChild(row);
//
//                    Treecell cell = new Treecell(difference.getString("sentence"));
//                    row.appendChild(cell);
//
                    String sent1 = onCreateSentence("model 1", difference.optJSONObject("runsM1"), "oryxEditor1");
                    String sent2 = onCreateSentence("model 2", difference.optJSONObject("runsM2"), "oryxEditor2");

//                    if(sent1.length() > 0 && sent2.length() > 0)
//                        cell.setWidgetListener("onClick", clean1 + ";" + clean2 + ";" + sent1 + ";"+ sent2);
//                    else if(sent1.length() > 0)
//                        cell.setWidgetListener("onClick", clean1 + ";" + clean2 + ";" + sent1);
//                    else if(sent2.length() > 0)
//                        cell.setWidgetListener("onClick", clean1 + ";" + clean2 + ";" + sent2);
//
//                    treechildren.appendChild(item);

                    // Add UI for this difference
                    final Button button = new Button(difference.getString("sentence"));
                    button.setStyle(buttonCSSStyle(false));
                    button.addEventListener("onClick", new EventListener<Event>() {
                        public void onEvent(Event event) throws Exception {
                            for (Component component: button.getParent().getChildren()) {
                                if (component instanceof Button) {
                                    Button b = (Button) component;
                                    b.setStyle(buttonCSSStyle(button == b));
                                }
                            }
                        }
                    });
                    if(sent1.length() > 0 && sent2.length() > 0)
                        button.setWidgetListener("onClick", clean1 + ";" + clean2 + ";" + sent1 + ";"+ sent2);
                    else if(sent1.length() > 0)
                        button.setWidgetListener("onClick", clean1 + ";" + clean2 + ";" + sent1);
                    else if(sent2.length() > 0)
                        button.setWidgetListener("onClick", clean1 + ";" + clean2 + ";" + sent2);

//                    button.setWidgetListener("onClick", differenceToJavascript(i, difference));

                    parent.appendChild(button);
                }

            } catch (JSONException e) {
                InterruptedException ie = new InterruptedException("Unable to parse differences JSON");
                ie.initCause(e);
                ie.printStackTrace();
                throw ie;
            }
//            try {
//                JSONArray array = this.differences.getJSONArray("differences");
//                for (int i=0; i < array.length(); i++) {
//                    JSONObject difference = array.getJSONObject(i);
//
//                    // Add UI for this difference
//                    Treeitem item = new Treeitem();
//
//                    Treerow row = new Treerow();
//                    item.appendChild(row);
//
//                    Treecell cell = new Treecell(difference.getString("sentence"));
//                    row.appendChild(cell);
//
//                    String clean1 = "oryxEditor1.cleanDifferences()";
//                    String clean2 = "oryxEditor2.cleanDifferences()";
//
//                    String sent1 = onCreateSentence("model 1", difference.optJSONObject("runsM1"), "oryxEditor1");
//                    String sent2 = onCreateSentence("model 2", difference.optJSONObject("runsM2"), "oryxEditor2");
//
//                    if(sent1.length() > 0 && sent2.length() > 0)
//                        cell.setWidgetListener("onClick", clean1 + ";" + clean2 + ";" + sent1 + ";"+ sent2);
//                    else if(sent1.length() > 0)
//                        cell.setWidgetListener("onClick", clean1 + ";" + clean2 + ";" + sent1);
//                    else if(sent2.length() > 0)
//                        cell.setWidgetListener("onClick", clean1 + ";" + clean2 + ";" + sent2);
//
//                    treechildren.appendChild(item);
//                }
//            } catch (JSONException e) {
//                InterruptedException ie = new InterruptedException("Unable to parse differences JSON");
//                ie.initCause(e);
//                ie.printStackTrace();
//                throw ie;
//            }
        }
    }

    private String differenceToJavascript(int buttonIndex, JSONObject difference) throws JSONException {
        LOGGER.info("differenceToJavascript: " + difference);

        return "oryxEditor1.displayMLDifference(" +
                buttonIndex + "," +
                "\"" + difference.optString("type") + "\"," +
                difference.optJSONArray("start")    + "," +
                difference.optJSONArray("a")        + "," +
                difference.optJSONArray("b")        + "," +
                difference.optJSONArray("newTasks") + "," +
                difference.optJSONArray("end")      + "," +
                difference.optJSONArray("greys")    + ")";
    }

    private String buttonCSSStyle(boolean isSelected) {
        return "background: " + (isSelected ? "#DDEEFF" : "inherit") + "; " +
                "border: " + (isSelected ? "1px solid red" : "none") + "; " +
                "margin: 5px; text-align: initial; white-space: normal";
    }

    private String onCreateRuns(final String modelName, JSONObject runsM1, Treeitem item, String oryxEditor) throws JSONException {
        String sentence = "";
        if (runsM1 != null) {
            // Add UI for these runs
            Treechildren m1children = item.getTreechildren();
            if (m1children == null) {
                m1children = new Treechildren();
                item.appendChild(m1children);
            }
            assert m1children != null;

            Treeitem m1item = new Treeitem();
            m1children.appendChild(m1item);

            Treerow m1row = new Treerow();
            m1item.appendChild(m1row);

            Treecell m1cell = new Treecell("Runs in " + modelName);
            m1row.appendChild(m1cell);

            LOGGER.info("About to fetch runs");
            JSONArray runs = runsM1.getJSONArray("runs");
            LOGGER.info("Runs " + runs);
            for (int j=0; j < runs.length(); j++) {
                LOGGER.info("About to fetch run " + j);
                JSONObject run = runs.getJSONObject(j);
                LOGGER.info("Run " + j + " " + run);

                // Add UI for this run
                Treechildren runChildren = m1item.getTreechildren();
                if (runChildren == null) {
                    runChildren = new Treechildren();
                    m1item.appendChild(runChildren);
                }
                assert runChildren != null;

                Treeitem runItem = new Treeitem();
                runChildren.appendChild(runItem);

                Treerow runRow = new Treerow();
                runItem.appendChild(runRow);

                Treecell runCell = new Treecell();
                runRow.appendChild(runCell);


                Button button = new Button("Run " + j);
                final int jj = j;
                button.setWidgetListener("onClick", oryxEditor + ".highlightDifferences('Run " + jj + "'," + run.getJSONObject("colorsBPMN") + ")");
                sentence = oryxEditor + ".highlightDifferences('Run " + jj + "'," + run.getJSONObject("colorsBPMN") + ")";
                runCell.appendChild(button);

                /*
                LOGGER.info("About to fetch colorsBPMN");
                final JSONObject colorsBPMN = run.getJSONObject("colorsBPMN");
                LOGGER.info("Fetched colorsBPMN");
                for (String colorBPMN: new Iterable<String>(){ public Iterator<String> iterator() { return colorsBPMN.keys(); }}) {
                    LOGGER.info("  " + colorBPMN + " -> " + colorsBPMN.getString(colorBPMN));
                }
                LOGGER.info("Logged colorsBPMN");
                */
            }
            LOGGER.info("Fetched runs");
        }

        return sentence;
    }

    private String onCreateSentence(final String modelName, JSONObject runsM1, String oryxEditor) throws JSONException {
        String sentence = "";
        if (runsM1 != null) {
            LOGGER.info("About to fetch runs");
            JSONArray runs = runsM1.getJSONArray("runs");
            LOGGER.info("Runs " + runs);
            for (int j=0; j < runs.length(); j++) {
                LOGGER.info("About to fetch run " + j);
                JSONObject run = runs.getJSONObject(j);
                LOGGER.info("Run " + j + " " + run);

                final int jj = j;
                sentence = oryxEditor + ".highlightDifferences('Run " + jj + "'," + run.getJSONObject("colorsBPMN") + ")";

                /*
                LOGGER.info("About to fetch colorsBPMN");
                final JSONObject colorsBPMN = run.getJSONObject("colorsBPMN");
                LOGGER.info("Fetched colorsBPMN");
                for (String colorBPMN: new Iterable<String>(){ public Iterator<String> iterator() { return colorsBPMN.keys(); }}) {
                    LOGGER.info("  " + colorBPMN + " -> " + colorsBPMN.getString(colorBPMN));
                }
                LOGGER.info("Logged colorsBPMN");
                */
            }
            LOGGER.info("Fetched runs");
        }

        return sentence;
    }

    /**
     * YAWL models package their event data as an array of {@link String}s, EPML packages it as a {@link String}; this function
     * hides the difference.
     *
     * @param event ZK event
     * @throws RuntimeException if the data associated with <var>event</var> is neither a {@link String} nor an array of {@link String}s
     */
    private static String eventToString(final Event event) {
        if (event.getData() instanceof String[]) {
            return ((String[]) event.getData())[0];
        }
        if (event.getData() instanceof String) {
            return (String) event.getData();
        }

        throw new RuntimeException("Unsupported class of event data: " + event.getData());
    }

}
