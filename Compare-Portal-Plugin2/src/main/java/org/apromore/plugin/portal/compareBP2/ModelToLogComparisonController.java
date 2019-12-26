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

package org.apromore.plugin.portal.compareBP2;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

// Third party packages
import ee.ut.eventstr.comparison.differences.*;
import org.apromore.plugin.editor.EditorPlugin;
import org.apromore.portal.context.EditorPluginResolver;
import org.deckfour.xes.model.XLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;

// Local packages
import org.apromore.model.EditSessionType;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.PluginMessages;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.UserType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.ConfigBean;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.dto.SignavioSession;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.util.StreamUtil;
import org.apromore.service.compare.CompareService;

import javax.inject.Inject;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.SaveAsDialogController;

public class ModelToLogComparisonController extends BaseController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ModelToLogComparisonController.class.getCanonicalName());

	private MainController mainC;

	private EditSessionType editSession1;
	private ProcessSummaryType process;
	private VersionSummaryType version;
	private Set<RequestParameterType<?>> params;
	private XLog log;
	private JSONObject differences;
	private int currentDiffIndex = -1;

	private CompareService compareService;
	@Inject
	private UserSessionManager userSessionManager;

	public ModelToLogComparisonController() {
		super();

		compareService = (CompareService) SpringUtil.getBean("compareService");

		if (userSessionManager.getCurrentUser() == null) {
			LOGGER.warn("Faking user session with admin(!)");
			UserType user = new UserType();
			user.setId("8");
			user.setUsername("admin");
			userSessionManager.setCurrentUser(user);
		}

		String id = Executions.getCurrent().getParameter("id");
		if (id != null) {
			SignavioSession session = userSessionManager.getEditSession(id);
			if (session == null) {
				throw new AssertionError("No edit session associated with id " + id);
			}

			editSession1 = session.getEditSession();
			mainC = session.getMainC();
			process = session.getProcess();
			version = session.getVersion();
			params = session.getParams();
			log = session.getLog();
		}

		Map<String, Object> param = new HashMap<>();
		try {
			//String title = editSession1.getProcessName() + " (" + editSession1.getNativeType() + ")";
			//this.setTitle(title);

			ExportFormatResultType exportResult1 = getService().exportFormat(editSession1.getProcessId(),
					editSession1.getProcessName(), editSession1.getOriginalBranchName(),
					editSession1.getCurrentVersionNumber(), editSession1.getNativeType(), editSession1.getAnnotation(),
					editSession1.isWithAnnotation(), editSession1.getUsername(), params);

			String data1 = StreamUtil.convertStreamToString(exportResult1.getNative().getInputStream());
			param.put("bpmnXML", data1.replace("\n", " ").replace("'", "\\u0027").trim());
			param.put("url", getURL(editSession1.getNativeType()));
			param.put("importPath", getImportPath(editSession1.getNativeType()));
			param.put("exportPath", getExportPath(editSession1.getNativeType()));
			param.put("editor", "bpmneditor");

			if (mainC != null) {
				PluginMessages pluginMessages = exportResult1.getMessage();
				mainC.showPluginMessages(pluginMessages);
			}

			String logName = "";
			for (RequestParameterType<?> requestParameter : params) {
				switch (requestParameter.getId()) {
					case "m1_differences_json":
						param.put("differences", (String) requestParameter.getValue());
						this.differences = new JSONObject((String) requestParameter.getValue());
						break;
					case "log_name":
						logName = (String) requestParameter.getValue();
						break;
					default:
						LOGGER.warn("Unsupported request parameter \"" + requestParameter.getId() + "\" with value "
								+ requestParameter.getValue());
				}
			}
			this.setTitle("Model:'" + editSession1.getProcessName() + "' vs. " + "Log:'" + logName + "'");

			if (editSession1.getAnnotation() == null) {
				param.put("doAutoLayout", "true");
			} else if (process.getOriginalNativeType() != null
					&& process.getOriginalNativeType().equals(editSession1.getNativeType())) {
				param.put("doAutoLayout", "false");
			} else {
				if (editSession1.isWithAnnotation()) {
					param.put("doAutoLayout", "false");
				} else {
					param.put("doAutoLayout", "true");
				}
			}

			List<EditorPlugin> editorPlugins = EditorPluginResolver.resolve();
			param.put("plugins", editorPlugins);

			Executions.getCurrent().pushArg(param);

		} catch (Exception e) {
			LOGGER.error("", e);
			e.printStackTrace();
		}

		this.addEventListener("onRecompare", new EventListener<Event>() {
			@Override
			public void onEvent(final Event event) throws InterruptedException {
				ModelToLogComparisonController.this.reCompare(event);
			}
		});
		
		this.addEventListener("onDiffSelection", new EventListener<Event>() {
			@Override
			public void onEvent(final Event event) throws InterruptedException {
				org.zkoss.json.JSONObject jsonObject = (org.zkoss.json.JSONObject)event.getData();
				currentDiffIndex = (Integer)jsonObject.get("diffIndex");
				Component buttonContainer = (Component) getFellowIfAny("buttons");
				int buttonNums = buttonContainer.getChildren().size();
				for (int i=0;i<buttonNums;i++) {
					Toolbarbutton button = (Toolbarbutton)buttonContainer.getChildren().get(i);
					if (currentDiffIndex >= 0 && i == currentDiffIndex) {
						button.setStyle(buttonCSSStyle(true));
						button.setChecked(true);
					}
					else {
						button.setStyle(buttonCSSStyle(false));
						button.setChecked(false);
					}
				}
			}
		});
		
//		this.addEventListener("onClearDifferences", new EventListener<Event>() {
//			@Override
//			public void onEvent(final Event event) throws InterruptedException {
//				Component buttonContainer = (Component) getFellowIfAny("buttons");
//				buttonContainer.getChildren().clear();
//			}
//		});
		
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
	}

	private String buttonCSSStyle(boolean isSelected) {
		return "background: " + (isSelected ? "#DDEEFF" : "inherit") + "; " + "border: "
				+ (isSelected ? "1px solid red" : "none") + "; "
				+ "margin: 5px; text-align: initial; white-space: normal";
	}

	/*
	 * diffIndex: the index of the difference being highlighted.
	 */
	public void onCreate() throws InterruptedException {

		Toolbar hbox = (Toolbar) this.getFellowIfAny("differences");
		if (hbox != null) {
			//Component parent = (Component) hbox.getParent();
			Component buttonContainer = (Component) getFellowIfAny("buttons");
			buttonContainer.getChildren().clear();
			// Remove any pre-extant list items
//			Component sibling = hbox.getNextSibling();
//			while (sibling != null) {
//				buttonContainer.removeChild(sibling);
//				sibling = hbox.getNextSibling();
//			}

			// Add the current differences
			try {
				final DecimalFormat rankingFormat = new DecimalFormat("##%");

				JSONArray array = this.differences.getJSONArray("differences");
				for (int i = 0; i < array.length(); i++) {
					JSONObject difference = array.getJSONObject(i);

					// Add UI for this difference
					final Toolbarbutton button = new Toolbarbutton(difference.getString("sentence") + " ("
							+ rankingFormat.format(difference.getDouble("ranking")) + " of traces)");
					if (currentDiffIndex >= 0 && i == currentDiffIndex) {
						button.setStyle(buttonCSSStyle(true));
						button.setChecked(true);
					}
					else {
						button.setStyle(buttonCSSStyle(false));
						button.setChecked(false);
					}
					
					button.addEventListener("onClick", new EventListener<Event>() {
						public void onEvent(Event event) throws Exception {
							for (Component component : button.getParent().getChildren()) {
								if (component instanceof Toolbarbutton) {
									Toolbarbutton b = (Toolbarbutton) component;
									b.setStyle(buttonCSSStyle(button == b));
									b.setChecked(button == b);
								}
							}
						}
					});
					button.setWidgetListener("onClick", differenceToJavascript(i, difference));

					buttonContainer.appendChild(button);
				}

			} catch (JSONException e) {
				InterruptedException ie = new InterruptedException("Unable to parse differences JSON");
				ie.initCause(e);
				ie.printStackTrace();
				throw ie;
			}
		}

		Button applyButton = (Button) this.getFellowIfAny("apply");
		if (applyButton != null) {
			// The repairMLDifference function will send an onRepair event to
			// the ZK asynchronous updater when it completes
			applyButton.setWidgetListener("onClick", "comparePlugin.applyDifference()");
		}

		Button recompareButton = (Button) this.getFellowIfAny("recompare");
		if (recompareButton != null) {
			// The repairMLDifference function will send an onRepair event to
			// the ZK asynchronous updater when it completes
			recompareButton.setWidgetListener("onClick", "comparePlugin.reCompare()");
		}
	}

	private String differenceToJavascript(int buttonIndex, JSONObject difference) throws JSONException {
		LOGGER.info("differenceToJavascript: " + difference);

		return "comparePlugin.highlightDifference" + "(" + buttonIndex + "," + "\"" + difference.optString("type") + "\","
				+ difference.optJSONArray("start") + "," + difference.optJSONArray("a") + ","
				+ difference.optJSONArray("b") + "," + difference.optJSONArray("newTasks") + ","
				+ difference.optJSONArray("end") + "," + difference.optJSONArray("start2") + ","
				+ difference.optJSONArray("end2") + "," + difference.optJSONArray("greys") + ","
				+ difference.optJSONArray("annotations") + ")";
	}
	
	private void reCompare(final Event event) throws InterruptedException {
		org.zkoss.json.JSONObject jsonObject = (org.zkoss.json.JSONObject)event.getData();
		String bpmnString = (String)jsonObject.get("bpmnXML");
		currentDiffIndex = (Integer)jsonObject.get("diffIndex");
		try {
			ModelAbstractions model = new ModelAbstractions(bpmnString.getBytes(Charset.forName("UTF-8")));
			DifferencesML differencesML = compareService.discoverBPMNModel(model, log, new HashSet<String>());
			differences = new JSONObject(DifferencesML.toJSON(differencesML));
			LOGGER.info("Obtained differences: " + differences);
			onCreate();

		} catch (Exception e) {
			LOGGER.error("Unable to obtain differences", e);
		}
	}

	/**
	 * YAWL models package their event data as an array of {@link String}s, EPML
	 * packages it as a {@link String}; this function hides the difference.
	 *
	 * @param event
	 *            ZK event
	 * @throws RuntimeException
	 *             if the data associated with <var>event</var> is neither a
	 *             {@link String} nor an array of {@link String}s
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
