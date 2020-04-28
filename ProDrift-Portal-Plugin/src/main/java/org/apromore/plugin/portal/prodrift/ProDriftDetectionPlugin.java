/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.plugin.portal.prodrift;

// Java 2 Standard Edition packages

import org.apromore.model.LogSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;
import org.apromore.service.prodrift.ProDriftDetectionService;
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Messagebox;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

// Java 2 Enterprise Edition packages
// Third party packages

/**
 * A user interface to the process drift detection service.
 */
@Component("plugin")
public class ProDriftDetectionPlugin extends DefaultPortalPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProDriftDetectionPlugin.class);

    private final ProDriftDetectionService proDriftDetectionService;
    private final EventLogService eventLogService;

    private String label = "Detect process drifts (beta)";
    private String groupLabel = "Analyze";

    @Inject
    public ProDriftDetectionPlugin(final ProDriftDetectionService proDriftDetectionService, final EventLogService eventLogService) {
        this.proDriftDetectionService = proDriftDetectionService;
        this.eventLogService = eventLogService;
    }

    @Override
    public String getLabel(Locale locale) {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return groupLabel;
    }

    public void setGroupLabel(String groupLabel) {
        this.groupLabel = groupLabel;
    }

    @Override
    public void execute(PortalContext portalContext) {

        Map<SummaryType, List<VersionSummaryType>> elements = portalContext.getSelection().getSelectedProcessModelVersions();
        Set<LogSummaryType> selectedLogSummaryType = new HashSet<>();
        for(Map.Entry<SummaryType, List<VersionSummaryType>> entry : elements.entrySet())
        {
            if(entry.getKey() instanceof LogSummaryType)
            {
                selectedLogSummaryType.add((LogSummaryType) entry.getKey());
            }
        }


        LOGGER.debug("Executed process drift detection plug-in!");

        try {

            new ProDriftController(portalContext, this.proDriftDetectionService, eventLogService, selectedLogSummaryType);
        } catch (IOException | SuspendNotAllowedException e) {
            Messagebox.show(e.getMessage(), "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }



}
