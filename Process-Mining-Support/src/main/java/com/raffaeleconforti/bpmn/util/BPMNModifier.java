/*
 *  Copyright (C) 2018 Raffaele Conforti (www.raffaeleconforti.com)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.raffaeleconforti.bpmn.util;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.*;

import java.util.Map;
import java.util.Set;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 16/02/15.
 */
public class BPMNModifier {

    public static BPMNDiagram removeUnecessaryLabels(BPMNDiagram diagram) {
        for(Event event : diagram.getEvents()) {
            if(event.getEventType().equals(Event.EventType.START) || event.getEventType().equals(Event.EventType.END)) {
                event.getAttributeMap().put(AttributeMap.LABEL, "");
            }
        }
        for(Gateway gateway : diagram.getGateways()) {
            gateway.getAttributeMap().put(AttributeMap.LABEL, "");
        }
        return diagram;
    }

    public static BPMNDiagram insertSubProcess(BPMNDiagram mainProcess, BPMNDiagram subProcessDiagram, SubProcess subProcess) {

        Set<SubProcess> eventSub = new UnifiedSet<>();

        Map<SubProcess, SubProcess> mappingSubProcesses = new UnifiedMap<>(subProcessDiagram.getSubProcesses().size());
        for (SubProcess subSub : subProcessDiagram.getSubProcesses()) {
            SubProcess sub;
            if (subSub.getTriggeredByEvent()) {
                sub = mainProcess.addSubProcess(subSub.getLabel(), false, false, false, false, subSub.isBCollapsed(), subSub.getTriggeredByEvent());
                eventSub.add(sub);
            } else {
                sub = mainProcess.addSubProcess(subSub.getLabel(), subSub.isBLooped(), subSub.isBAdhoc(), subSub.isBCompensation(), subSub.isBMultiinstance(), subSub.isBCollapsed(), subSub.getTriggeredByEvent());
            }

            if (subSub.getParentSubProcess() == null) {
                sub.setParentSubprocess(subProcess);
            } else {
                sub.setParentSubprocess(mappingSubProcesses.get(subSub.getParentSubProcess()));
            }
            mappingSubProcesses.put(subSub, sub);

        }
        for (Activity actSub : subProcessDiagram.getActivities()) {
            Activity act = mainProcess.addActivity(actSub.getLabel(), actSub.isBLooped(), actSub.isBAdhoc(), actSub.isBCompensation(), actSub.isBMultiinstance(), actSub.isBCollapsed());

            if (actSub.getParentSubProcess() == null) {
                act.setParentSubprocess(subProcess);
            } else {
                act.setParentSubprocess(mappingSubProcesses.get(actSub.getParentSubProcess()));
            }
        }
        for (Event evtSub : subProcessDiagram.getEvents()) {
            Activity activity = null;
            Activity boundary = evtSub.getBoundingNode();
            if (boundary != null) {
                for (SubProcess subProcess1 : subProcessDiagram.getSubProcesses()) {
                    if (boundary.getLabel().equals(subProcess1.getLabel())) {
                        activity = subProcess1;
                        break;
                    }
                }
            }

            Event evt;
            if (evtSub.getEventType().equals(Event.EventType.START) && evtSub.getParentSubProcess() != null && eventSub.contains(mappingSubProcesses.get(evtSub.getParentSubProcess()))) {
                evt = mainProcess.addEvent(evtSub.getLabel(), evtSub.getEventType(), evtSub.getEventTrigger(), evtSub.getEventUse(), false, activity);
            } else if (evtSub.getEventType().equals(Event.EventType.START) && evtSub.getParentSubProcess() == null && subProcess.getTriggeredByEvent()) {
                evt = mainProcess.addEvent(evtSub.getLabel(), evtSub.getEventType(), evtSub.getEventTrigger(), evtSub.getEventUse(), false, activity);
            } else {
                evt = mainProcess.addEvent(evtSub.getLabel(), evtSub.getEventType(), evtSub.getEventTrigger(), evtSub.getEventUse(), true, activity);
            }

            if (evtSub.getParentSubProcess() == null) {
                evt.setParentSubprocess(subProcess);
            } else {
                evt.setParentSubprocess(mappingSubProcesses.get(evtSub.getParentSubProcess()));
            }
        }
        for (Gateway gatSub : subProcessDiagram.getGateways()) {
            Gateway gat;
            BPMNNode node = getNodeWithEqualLabel(mainProcess, gatSub);
            if (node == null) {
                gat = mainProcess.addGateway(gatSub.getLabel(), gatSub.getGatewayType());
            } else {
                gat = (Gateway) node;
            }

            if (gatSub.getParentSubProcess() == null) {
                gat.setParentSubprocess(subProcess);
            } else {
                gat.setParentSubprocess(mappingSubProcesses.get(gatSub.getParentSubProcess()));
            }
        }

        for (Flow flow : subProcessDiagram.getFlows()) {
            mainProcess.addFlow(getNodeWithEqualLabel(mainProcess, flow.getSource()), getNodeWithEqualLabel(mainProcess, flow.getTarget()), "");
        }

        return mainProcess;
    }

    public static BPMNNode getNodeWithEqualLabel(BPMNDiagram mainProcess, BPMNNode node) {
        for (BPMNNode actSub : mainProcess.getNodes()) {
            if (actSub.getLabel().equals(node.getLabel())) return actSub;
        }
        return null;
    }

    public static Gateway addGateway(BPMNDiagram diagram, String name, Gateway.GatewayType type) {
        return diagram.addGateway(name, type);
    }

    public static Flow addFlow(BPMNDiagram model, BPMNNode source, BPMNNode target) {
        Flow flow = null;
        for (Flow f : model.getFlows()) {
            if (f.getSource().equals(source) && f.getTarget().equals(target)) {
                flow = f;
                break;
            }
        }
        if (flow == null) {
            flow = model.addFlow(source, target, "");
        }
        return flow;
    }

    public static Activity addActivity(BPMNDiagram model, String name) {
        return model.addActivity(name, false, false, false, false, false);
    }

    public static Event addEvent(BPMNDiagram model, String name) {
        return model.addEvent(name, Event.EventType.INTERMEDIATE, Event.EventTrigger.NONE, null, true, null);
    }

    public static Event addStartEvent(BPMNDiagram model, String name) {
        return model.addEvent(name, Event.EventType.START, Event.EventTrigger.NONE, null, true, null);
    }

    public static Event addEndEvent(BPMNDiagram model, String name) {
        return model.addEvent(name, Event.EventType.END, Event.EventTrigger.NONE, null, true, null);
    }

}
