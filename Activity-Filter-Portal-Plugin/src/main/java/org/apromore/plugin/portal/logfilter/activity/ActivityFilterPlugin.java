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

package org.apromore.plugin.portal.logfilter.activity;

// Java 2 Standard Edition packages
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

// Java 2 Enterprise Edition packages
import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

// Third party packages
import org.apromore.model.LogSummaryType;
import org.apromore.model.SummaryType;
// Local packages
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.custom.gui.plugin.PluginCustomGui;
import org.apromore.service.EventLogService;
import org.apromore.service.logfilter.activity.ActivityFilterService;
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Window;

/**
 * Metrics service. Created by Raffaele Conforti 18/04/2016
 */
@Component("plugin")
public class ActivityFilterPlugin extends PluginCustomGui {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityFilterPlugin.class);

    @Inject private EventLogService eventLogService;
    @Inject private ActivityFilterService activityFilterService;

    private XLog log;
    private Window window;
    private Listbox event_types;
    private Slider threshold;
    private Button cancelButton;
    private Button okButton;

    @Override
    public String getLabel(Locale locale) {
        return "Filter out infrequent activities";
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return "Discover";
    }

    @Override
    public void execute(final PortalContext portalContext) {

        Map<SummaryType, List<VersionSummaryType>> elements = portalContext.getSelection().getSelectedProcessModelVersions();
        final Set<LogSummaryType> selectedLogSummaryType = new HashSet<>();
        for(Map.Entry<SummaryType, List<VersionSummaryType>> entry : elements.entrySet()) {
            if(entry.getKey() instanceof LogSummaryType) {
                selectedLogSummaryType.add((LogSummaryType) entry.getKey());
            }
        }

        // At least 2 process versions must be selected. Not necessarily of different processes
        if (selectedLogSummaryType.size() == 0) {

        }else if (selectedLogSummaryType.size() == 1) {
            retreiveLog(selectedLogSummaryType);
            String[] classes = activityFilterService.getLifecycleClasses(log);

            try {
                this.window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/activityFilter.zul", null, null);

                this.event_types = (Listbox) this.window.getFellow("activityFilterList");
                for(String candidate : classes) {
                    Listitem listItem = new Listitem();
                    listItem.setLabel(candidate);
                    this.event_types.appendChild(listItem);
                    listItem.setSelected(false);
                }

                this.threshold = (Slider) this.window.getFellow("activityFilterThreshold");
                this.cancelButton = (Button) this.window.getFellow("activityFilterCancelButton");
                this.okButton = (Button) this.window.getFellow("activityFilterOKButton");

                this.cancelButton.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        cancel();
                    }
                });
                this.okButton.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        runComputation(portalContext, selectedLogSummaryType, threshold.getCurpos());
                    }
                });
                this.window.doModal();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            Messagebox.show("Select one log for process discovery.");
            return;
        }
    }

    private void retreiveLog(Set<LogSummaryType> selectedLogSummaryType) {
        LogSummaryType logST= selectedLogSummaryType.iterator().next();
        log = eventLogService.getXLog(logST.getId());
    }

    protected void cancel() {
        this.window.detach();
    }

    protected void runComputation(PortalContext portalContext, Set<LogSummaryType> selectedLogSummaryType, int percentage) {
        LogSummaryType logST = selectedLogSummaryType.iterator().next();

        String[] remove = new String[event_types.getItemCount() - event_types.getSelectedCount()];
        int i = 0;
        for(Listitem item : event_types.getItems()) {
            if(!item.isSelected()) {
                remove[i] = item.getLabel();
                i++;
            }
        }

        XLog xlog = activityFilterService.filterLog(log, remove, percentage);

        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            eventLogService.exportToStream(outputStream, xlog);

            int folderId = portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId();

            eventLogService.importLog(portalContext.getCurrentUser().getUsername(), folderId,
                    logST.getName() + "_activity_filtered", new ByteArrayInputStream(outputStream.toByteArray()), "xes.gz",
                    logST.getDomain(), DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString(),
                    false);

            window.detach();
            portalContext.refreshContent();
        } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
