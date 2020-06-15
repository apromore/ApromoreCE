/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Bruce Nguyen.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * Copyright (C) 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin.portal.perfmining;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.perfmining.util.LogUtilites;
import org.apromore.plugin.portal.perfmining.view.ResultWindowController;
import org.apromore.service.perfmining.PerfMiningService;
import org.apromore.service.perfmining.filter.TraceAttributeFilterParameters;
import org.apromore.service.perfmining.models.SPF;
import org.apromore.service.perfmining.models.SPFManager;
import org.apromore.service.perfmining.parameters.SPFConfig;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.joda.time.DateTime;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import org.zkoss.zul.ext.Selectable;


public class PerfMiningController {
    private final PortalContext portalContext;
    private final PerfMiningService perfMiningService;
    
    private Window licenseW;
    private Button licenseOKbutton;
    private Button licenseCancelButton;
    
    private Window configW;
    private Button configPreviousButton;
    private Button configOKbutton;
    private Button configCancelButton;
    private Combobox configTimeZoneCombo;
    private Listbox configExitStatusListbox;
    private Checkbox hasStartEndEventCheckbox;
    
    private XLog log = null;
    
    private SPFConfig config = new SPFConfig();

    /**
     * @throws IOException if the <code>perfmining.zul</code> template can't be read from the classpath
     */
    public PerfMiningController(PortalContext portalContext, PerfMiningService perfMiningService, Map<XLog, String> logs) throws IOException {
        this.portalContext = portalContext;
        this.perfMiningService = perfMiningService;
        
        if(logs.size() > 0) {
            if(logs.size() > 1) {
                showError("Please select only one log!");
                return;
            } 
            else {
                this.log = logs.keySet().iterator().next();
            }
        }
        else {
            showError("Please select one log!");
            return;
        }
        
        //-----------------------------------------------------------------
        // INITIALIZE COMPONENTS
        //-----------------------------------------------------------------
        this.licenseW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/license.zul", null, null);
        licenseOKbutton = (Button) this.licenseW.getFellow("OKButton");
        licenseCancelButton = (Button) this.licenseW.getFellow("CancelButton");
        
        this.configW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/configuration.zul", null, null);
        this.configW.setTitle("Performance Mining Parameters");
        configPreviousButton = (Button) this.configW.getFellow("PreviousButton");
        configOKbutton = (Button) this.configW.getFellow("OKButton");
        configCancelButton = (Button) this.configW.getFellow("CancelButton");
        configTimeZoneCombo = (Combobox) this.configW.getFellow("TimeZoneCombo");
        configExitStatusListbox = (Listbox) this.configW.getFellow("ExitStatusListBox");
        hasStartEndEventCheckbox = (Checkbox) this.configW.getFellow("hasStartEndEvents");

        this.licenseW.doModal();
        
        //-----------------------------------------------------------------
        // EVENT LISTENERS
        //-----------------------------------------------------------------

       licenseOKbutton.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                licenseW.detach();
                SPFManager.getInstance().clear(); //start over for a new SPF
                readData(log, config, SPFManager.getInstance());
                config.setCheckStartCompleteEvents(true);
                initializeTimeZoneBox();
                initializeCaseStatusList();
                configW.doModal();
                configPreviousButton.setVisible(false);
            }
        });      
       
        licenseCancelButton.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                licenseW.detach();
                configW.detach();
            }
        });
        
        configPreviousButton.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                configW.setVisible(false);
            }
        });
        
        
        configOKbutton.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                TraceAttributeFilterParameters filter = new TraceAttributeFilterParameters();
                filter.setName("Full SPF");
                minePerformance(log, config, filter);
                configW.detach();
            }
        });
        
        configCancelButton.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                configW.detach();
            }
        });
        
        configTimeZoneCombo.addEventListener("onSelect", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                config.setTimeZone(TimeZone.getTimeZone(configTimeZoneCombo.getSelectedItem().getValue().toString()));
            }
        });     
        
        configExitStatusListbox.addEventListener("onSelect", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                java.util.Set selection = ((Selectable)configExitStatusListbox.getModel()).getSelection();
                config.setExitTypeList(new ArrayList<String>(selection));
            }
        });     
        
        hasStartEndEventCheckbox.addEventListener("onCheck", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                config.setCheckStartCompleteEvents(hasStartEndEventCheckbox.isChecked());
            }
        });  
    }
    
    public void showError(String error) {
        Messagebox.show(error, "Error", 0, Messagebox.ERROR);
//        Label errorLabel = (Label) this.importW.getFellow("errorLabel");
//        errorLabel.setValue(error);
    }
    
    private void initializeTimeZoneBox() {
        TimeZone defaultTZ = TimeZone.getTimeZone("Europe/Amsterdam");
        String[] ids = TimeZone.getAvailableIDs();
        Comboitem defaultItem = null;
        for (int i = 0; i < ids.length; i++) {
                TimeZone timeZone = TimeZone.getTimeZone(ids[i]);
                Comboitem item = configTimeZoneCombo.appendItem(getTimeZoneString(timeZone));
                String test = timeZone.getID();
                if (timeZone.getID().equals(defaultTZ.getID())) {
                    defaultItem = item;
                }
        }
        configTimeZoneCombo.setSelectedItem(defaultItem);
        config.setTimeZone(defaultTZ);
    }
    
    /**
     * Only call method when SPFConfig has been loaded with data from the log.
     */
    private void initializeCaseStatusList() {
        java.util.List<String> caseStatusList = config.getCaseStatusList();
        ListModelList<String> statusListModel = new ListModelList<String>(caseStatusList);
        configExitStatusListbox.setModel(statusListModel);
        ((Selectable)configExitStatusListbox.getModel()).setMultiple(true);
        configExitStatusListbox.setCheckmark(true);
    }

    protected void minePerformance(XLog log, SPFConfig config, TraceAttributeFilterParameters filter) {
        try {
            SPF result = perfMiningService.mine(log, config, filter);
            showResults(result);

        } catch (Exception e) {
            String message = "PerfMining failed (" + e.getMessage() + ")";
            showError(message);
        }
    }
    
    protected void showConfig(XLog log) {
        
    }
    
    protected void showResults(SPF result) {
        try {
//            new PerfMiningShowResult(portalContext, result, importW);
            new ResultWindowController(portalContext, result);

        } catch (IOException | SuspendNotAllowedException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

        /**
     * This method reads the log file and populate data to the Config and
     * BPFManager object
     * 
     * @param log
     * @param config
     * @param bpfManager
     */
    private static void readData(XLog log, SPFConfig config, SPFManager bpfManager) {
            new HashMap<String, Map<String, String>>();
            config.setXLog(log);

            for (XTrace trace : log) {
                    DateTime traceStart = new DateTime(9999, 12, 1, 1, 0);
                    DateTime traceEnd = new DateTime(0);

                    //---------------------------------------
                    // Populate the Config object
                    //---------------------------------------
                    for (XEvent event : trace) {
                            String eventName = LogUtilites.getConceptName(event).toLowerCase();
                            String stage = LogUtilites.getValue(event.getAttributes().get("stage")).toLowerCase();
                            //					String type = LogUtilites.getValue(event.getAttributes().get("type")).toLowerCase();
                            String transitiontype = LogUtilites.getLifecycleTransition(event).toLowerCase();
                            DateTime eventTime = LogUtilites.getTimestamp(event);

                            if (!config.getStageList().contains(stage)) {
                                    config.getStageList().add(stage);
                            }

                            if (!config.getEventStageMap().containsKey(eventName)) {
                                    config.getEventStageMap().put(eventName, stage);
                            }

                            //					if (type.equals("activity")) {
                            //						config.getActivityList().add(eventName);
                            //					}
                            //					else if (type.equals("gate")) {
                            //						config.getGateList().add(eventName);
                            //					}

                            if (traceStart.isAfter(eventTime)
                                            && (transitiontype.equals("start") || transitiontype.equals("complete"))) {
                                    traceStart = eventTime;
                            }

                            if (traceEnd.isBefore(eventTime)
                                            && (transitiontype.equals("start") || transitiontype.equals("complete"))) {
                                    traceEnd = eventTime;
                            }
                    }

                    String traceStatus = LogUtilites.getValue(trace.getAttributes().get("status")).toLowerCase();
                    if (!config.getCaseStatusList().contains(traceStatus)) {
                            config.getCaseStatusList().add(traceStatus);
                    }

                    //---------------------------------------
                    // Populate the BPFManager object
                    //---------------------------------------
                    String curTraceID = LogUtilites.getConceptName(trace);
                    Map<String, String> casePropertyMap = new HashMap<String, String>();
                    Iterator<XAttribute> attIterator = trace.getAttributes().values().iterator();

                    while (attIterator.hasNext()) {
                            XAttribute att = attIterator.next();
                            if (!att.getKey().equals("concept:name")) {
                                    casePropertyMap.put(att.getKey(), LogUtilites.getValue(att));
                            }
                    }

                    casePropertyMap.put(SPF.CASE_START_TIME, String.valueOf(traceStart.getMillis()));
                    casePropertyMap.put(SPF.CASE_END_TIME, String.valueOf(traceEnd.getMillis()));
                    casePropertyMap.put(SPF.CASE_EVENT_ACOUNT, String.valueOf(trace.size()));

                    bpfManager.getCaseAttributeMap().put(curTraceID, casePropertyMap);
            }
    }
    
    /**
     * Based on https://www.mkyong.com/java/java-display-list-of-timezone-with-gmt/
    */
    private String getTimeZoneString(TimeZone tz) {

            long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset())
                              - TimeUnit.HOURS.toMinutes(hours);
            // avoid -4:-30 issue
            minutes = Math.abs(minutes);

            String result = "";
            if (hours > 0) {
                    result = String.format("(GMT+%d:%02d) %s", hours, minutes, tz.getID());
            } else {
                    result = String.format("(GMT%d:%02d) %s", hours, minutes, tz.getID());
            }

            return result;

    }
}
