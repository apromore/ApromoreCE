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

package org.apromore.plugin.portal.bpmnminer;

// Java 2 Standard Edition packages
import java.util.Locale;

// Java 2 Enterprise Edition packages
import javax.inject.Inject;

// Third party packages
import org.apromore.service.EventLogService;
import org.apromore.service.logfilter.behaviour.InfrequentBehaviourFilterService;
import org.springframework.stereotype.Component;

// Local packages
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.bimp_annotation.BIMPAnnotationService;
import org.apromore.service.bpmnminer.BPMNMinerService;
import org.apromore.service.CanoniserService;
import org.apromore.service.DomainService;
import org.apromore.service.ProcessService;
import org.apromore.service.helper.UserInterfaceHelper;

/**
 * A user interface to the BPMN miner service.
 */
@Component("plugin")
public class BPMNMinerPlugin extends DefaultPortalPlugin {

    private final BIMPAnnotationService bimpAnnotationService;
    private final BPMNMinerService    bpmnMinerService;
    private final CanoniserService    canoniserService;
    private final DomainService       domainService;
    private final ProcessService      processService;
    private final EventLogService     eventLogService;
    private final InfrequentBehaviourFilterService infrequentBehaviourFilterService;
    private final UserInterfaceHelper userInterfaceHelper;

    @Inject
    public BPMNMinerPlugin(final BIMPAnnotationService bimpAnnotationService,
                           final BPMNMinerService bpmnMinerService,
                           final CanoniserService canoniserService,
                           final DomainService domainService,
                           final ProcessService processService,
                           final EventLogService eventLogService,
                           final InfrequentBehaviourFilterService infrequentBehaviourFilterService,
                           final UserInterfaceHelper userInterfaceHelper) {

        this.bimpAnnotationService              = bimpAnnotationService;
        this.bpmnMinerService                   = bpmnMinerService;
        this.canoniserService                   = canoniserService;
        this.domainService                      = domainService;
        this.processService                     = processService;
        this.eventLogService                    = eventLogService;
        this.infrequentBehaviourFilterService   = infrequentBehaviourFilterService;
        this.userInterfaceHelper = userInterfaceHelper;
    }

    @Override
    public String getLabel(Locale locale) {
        return "Discover model (advanced)";
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return "Discover";
    }

    @Override
    public void execute(PortalContext context) {
        new BPMNMinerController(context, bimpAnnotationService, bpmnMinerService, canoniserService, domainService, processService, eventLogService, infrequentBehaviourFilterService, userInterfaceHelper);
    }

}
