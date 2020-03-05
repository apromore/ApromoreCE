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

import org.deckfour.xes.extension.std.XConceptExtension;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.*;

import java.util.*;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 16/02/15.
 */
public class BPMNSimplifier {

    public static BPMNDiagram simplify(BPMNDiagram diagram) {
        int oldSize = 0;
        int size = diagram.getActivities().size() + diagram.getEvents().size() + diagram.getGateways().size() + diagram.getFlows().size();
        while (oldSize != size) {
            oldSize = size;
            BPMNSimplifier.removeConnectedStartEndEvent(diagram);
            BPMNSimplifier.removeHTMLfromAllActivitiesProcess(diagram);
            BPMNSimplifier.insertStartAndEndEventsIfMissing(diagram);
            BPMNSimplifier.removeArtificialNodes(diagram);
            BPMNSimplifier.removeLoopedXOR(diagram);
//            BPMNSimplifier.replaceShortLoopsWithSelfLoops(diagram);
            BPMNSimplifier.fixANDGateway(diagram);
            BPMNSimplifier.removeUselessSubProcesses(diagram);
            BPMNSimplifier.removeGatewaysUseless(diagram, diagram.getGateways());
            BPMNSimplifier.removeDuplicateArcs(diagram);
            BPMNSimplifier.removeEmptyActivities(diagram);
            size = diagram.getActivities().size() + diagram.getEvents().size() + diagram.getGateways().size() + diagram.getFlows().size();
        }
        return diagram;

    }

    public static BPMNDiagram basicSimplification(BPMNDiagram diagram) {
        int oldSize = 0;
        int size = diagram.getActivities().size() + diagram.getEvents().size() + diagram.getGateways().size() + diagram.getFlows().size();
        while (oldSize != size) {
            oldSize = size;
            BPMNSimplifier.removeConnectedStartEndEvent(diagram);
            BPMNSimplifier.removeHTMLfromAllActivitiesProcess(diagram);
            BPMNSimplifier.insertStartAndEndEventsIfMissing(diagram);
            BPMNSimplifier.removeArtificialNodes(diagram);
            BPMNSimplifier.fixANDGateway(diagram);
            BPMNSimplifier.removeGatewaysUseless(diagram, diagram.getGateways());
            size = diagram.getActivities().size() + diagram.getEvents().size() + diagram.getGateways().size() + diagram.getFlows().size();
        }
        return diagram;

    }

    public static BPMNDiagram removeArtificialNodes(BPMNDiagram process) {

        List<Flow> remove = new ArrayList<>();

        Iterator<Activity> iterator = process.getActivities().iterator();
        while (iterator.hasNext()) {
            Activity act = iterator.next();
            if (act.getLabel().startsWith("Artificial")) {

                List<Set<BPMNNode>[]> flows = new ArrayList<>();

                Set<BPMNNode> from;
                Set<BPMNNode> to;

                from = new UnifiedSet<>();
                to = new UnifiedSet<>();
                for (Flow f : process.getFlows()) {
                    if (f.getSource().equals(act)) {
                        to.add(f.getTarget());
                        remove.add(f);
                    }
                    if (f.getTarget().equals(act)) {
                        from.add(f.getSource());
                        remove.add(f);
                    }
                }

                flows.add(new Set[]{from, to});

                for (Flow f : remove) {
                    process.removeEdge(f);
                }
                remove.clear();

                process.removeActivity(act);

                for (Set[] fromTo : flows) {
                    from = fromTo[0];
                    to = fromTo[1];
                    for (BPMNNode nodeFrom : from) {
                        for (BPMNNode nodeTo : to) {
                            process.addFlow(nodeFrom, nodeTo, "");
                        }
                    }
                }
                iterator = process.getActivities().iterator();
            }
        }

        return process;
    }

    public static BPMNDiagram removeGatewaysUseless(BPMNDiagram diagram, Collection<Gateway> gateway) {
        Set<Flow> removeFlow = new UnifiedSet<>();
        Set<Gateway> removeGateway = new UnifiedSet<>();

        Set<Gateway> checkGateway = new UnifiedSet<>(gateway.size());
        for (Gateway g : gateway) {
            checkGateway.add(g);
        }

        boolean removed = true;

        List<Gateway> listRemoveGateway;
        while (removed) {
            removed = false;
            for (Gateway g : checkGateway) {
                int sources = 0;
                int targets = 0;
                for (Flow flow : diagram.getFlows()) {
                    if (flow.getSource().equals(g) && !removeGateway.contains(flow.getSource())) {
                        targets++;
                    } else if (flow.getTarget().equals(g) && !removeGateway.contains(flow.getTarget())) {
                        sources++;
                    }
                }
                if (sources + targets < 3) {
                    removeGateway.add(g);
                }
            }

            listRemoveGateway = new ArrayList<>();
            listRemoveGateway.addAll(removeGateway);

            for (Gateway g : listRemoveGateway) {
                BPMNNode source = null;
                BPMNNode target = null;

                for (Flow flow : diagram.getFlows()) {
                    if (flow.getSource().equals(g)) {
                        removeFlow.add(flow);
                        target = flow.getTarget();
                    }
                    if (flow.getTarget().equals(g)) {
                        removeFlow.add(flow);
                        source = flow.getSource();
                    }
                }

                if (source != null && target != null) {
                    diagram.addFlow(source, target, "");
                }

                for (Flow flow : removeFlow) {
                    diagram.removeEdge(flow);
                }
                removeFlow.clear();

                diagram.removeGateway(g);
                checkGateway.remove(g);

                removed = true;

            }

            removeGateway.clear();
        }

        return diagram;
    }

    public static BPMNDiagram removeLoopedXOR(BPMNDiagram diagram) {
        Iterator<Flow> it = diagram.getFlows().iterator();
        while (it.hasNext()) {
            Flow f = it.next();
            if (f.getSource() instanceof Gateway && ((Gateway) f.getSource()).getGatewayType().equals(Gateway.GatewayType.DATABASED) &&
                    f.getTarget() instanceof Gateway && ((Gateway) f.getTarget()).getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                Gateway g1 = (Gateway) f.getSource();
                Gateway g2 = (Gateway) f.getTarget();

                int g1count = 0;
                int g2count = 0;
                boolean found = false;
                for (Flow f1 : diagram.getFlows()) {
                    if (f1.getTarget().equals(g1)) {
                        g1count++;
                        if (f1.getSource().equals(g2)) {
                            found = true;
                        }
                    }
                    if (f1.getSource().equals(g2)) {
                        g2count++;
                    }
                }

                if (g1count == 2 && g2count == 2 && found) {
                    BPMNNode start = null;
                    BPMNNode end = null;
                    Set<Flow> remove = new UnifiedSet<>();
                    for (Flow f1 : diagram.getFlows()) {
                        if (f1.getTarget().equals(g1)) {
                            remove.add(f1);
                            if (!f1.getSource().equals(g2)) {
                                start = f1.getSource();
                            }
                        } else if (f1.getSource().equals(g2)) {
                            remove.add(f1);
                            if (!f1.getTarget().equals(g1)) {
                                end = f1.getTarget();
                            }
                        }
                    }
                    for (Flow f1 : remove) {
                        diagram.removeEdge(f1);
                    }
                    diagram.removeGateway(g1);
                    diagram.removeGateway(g2);
                    diagram.addFlow(start, end, "");
                    it = diagram.getFlows().iterator();
                }

            }
        }

        return diagram;
    }

    public static BPMNDiagram removeConnectedStartEndEvent(BPMNDiagram diagram) {
        Set<Event> startEvents = new UnifiedSet<>();
        Set<Event> endEvents = new UnifiedSet<>();

        for (Event event : diagram.getEvents()) {
            if (event.getEventType().equals(Event.EventType.START)) {
                startEvents.add(event);
            }
            if (event.getEventType().equals(Event.EventType.END)) {
                endEvents.add(event);
            }
        }

        Set<Flow> removeFlow = new UnifiedSet<>();
        for (Flow f : diagram.getFlows()) {
            if (startEvents.contains(f.getSource()) && endEvents.contains(f.getTarget())) {
                removeFlow.add(f);
            }
        }

        for (Flow f : removeFlow) {
            diagram.removeEdge(f);
        }

        Set<Event> usedEvent = new UnifiedSet<>();
        for (Flow f : diagram.getFlows()) {
            if (f.getSource() instanceof Event) {
                usedEvent.add((Event) f.getSource());
            }
            if (f.getTarget() instanceof Event) {
                usedEvent.add((Event) f.getTarget());
            }
        }

        Set<Event> unusedEvent = new UnifiedSet<>();
        for (Event e : diagram.getEvents()) {
            if (!usedEvent.contains(e)) {
                unusedEvent.add(e);
            }
        }

        for (Event e : unusedEvent) {
            diagram.removeEvent(e);
        }

        return diagram;
    }

    public static BPMNDiagram renameStartAndEndEvents(BPMNDiagram diagram, String preLabel) {
        String pLabel;
        String preLabelStart = preLabel + "StartEvent";
        String preLabelEnd = preLabel + "EndEvent";
        for (Event event : diagram.getEvents()) {
            if (event.getEventType().equals(Event.EventType.START) || event.getEventType().equals(Event.EventType.END)) {
                if (event.getEventType().equals(Event.EventType.START)) {
                    pLabel = preLabelStart;
                } else {
                    pLabel = preLabelEnd;
                }
                String label = pLabel + event.getLabel();
                event.getAttributeMap().put(AttributeMap.LABEL, label);
            }
        }
        return diagram;
    }

    public static BPMNDiagram renameGateways(BPMNDiagram diagram, String preLabel) {
        int i = 1;
        for (Gateway gateway : diagram.getGateways()) {
            if(gateway.getLabel().isEmpty()) {
                gateway.getAttributeMap().put(AttributeMap.LABEL, preLabel + gateway.getLabel() + "_" + i);
                i++;
            }else {
                gateway.getAttributeMap().put(AttributeMap.LABEL, preLabel + gateway.getLabel());
            }
        }
        return diagram;
    }

    public static BPMNDiagram removePendingActivities(BPMNDiagram diagram) {
        Iterator<Activity> activityIterator = diagram.getActivities().iterator();
        while(activityIterator.hasNext()) {
            Activity a = activityIterator.next();
            boolean found = false;
            for(Flow f : diagram.getFlows()) {
                if(f.getSource().equals(a) || f.getTarget().equals(a)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                diagram.removeActivity(a);
            }
        }
        return diagram;
    }

    public static BPMNDiagram removePendingGateways(BPMNDiagram diagram) {
        Iterator<Gateway> gatewayIterator = diagram.getGateways().iterator();
        while(gatewayIterator.hasNext()) {
            Gateway g = gatewayIterator.next();
            boolean found = false;
            for(Flow f : diagram.getFlows()) {
                if(f.getSource().equals(g) || f.getTarget().equals(g)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                diagram.removeGateway(g);
            }
        }
        return diagram;
    }

    public static BPMNDiagram removePendingEvents(BPMNDiagram diagram) {
        Iterator<Event> eventIterator = diagram.getEvents().iterator();
        while(eventIterator.hasNext()) {
            Event e = eventIterator.next();
            boolean found = false;
            for(Flow f : diagram.getFlows()) {
                if(f.getSource().equals(e) || f.getTarget().equals(e)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                diagram.removeEvent(e);
            }
        }
        return diagram;
    }

    public static BPMNDiagram removePendingElements(BPMNDiagram diagram) {

        diagram = removePendingActivities(diagram);
        diagram = removePendingGateways(diagram);
        diagram = removePendingEvents(diagram);

        Iterator<SubProcess> subProcessIterator = diagram.getSubProcesses().iterator();
        while(subProcessIterator.hasNext()) {
            SubProcess s = subProcessIterator.next();
            boolean found = false;
            if(!s.getTriggeredByEvent() || s.getChildren().size() == 0) {
                for(Flow f : diagram.getFlows()) {
                    if(f.getSource().equals(s) || f.getTarget().equals(s)) {
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    removeSubProcess(diagram, s);
                    subProcessIterator.remove();
                }
            }else {
                Iterator<SubProcess> subProcessIterator2 = diagram.getSubProcesses().iterator();
                while (subProcessIterator2.hasNext()) {
                    SubProcess s1 = subProcessIterator2.next();
                    if(s != s1 && s.getLabel().equals(s1.getLabel()) && s1.getChildren().size() > 0) {
                       found = true;
                    }
                    if(found) {
                        removeSubProcess(diagram, s);
                        subProcessIterator.remove();
                        break;
                    }
                }
            }
        }

        return diagram;
    }

    private static void removeSubProcess(BPMNDiagram diagram, SubProcess s) {
        for(ContainableDirectedGraphElement n : s.getChildren()) {
            if(n instanceof DirectedGraphNode) {
                if(n instanceof SubProcess) {
                    removeSubProcess(diagram, (SubProcess) n);
                }
                diagram.removeNode((DirectedGraphNode) n);
            }
        }
    }

    public static BPMNDiagram renameActivities(BPMNDiagram diagram, String preLabel) {
        String pre = "Sub"+preLabel;
        for (Activity activity : diagram.getActivities()) {
            if(!activity.getLabel().contains("Artificial")) {
                activity.getAttributeMap().put(AttributeMap.LABEL, pre + activity.getLabel());
            }
        }
        return diagram;
    }

    public static BPMNDiagram removeHTMLfromAllActivitiesProcess(BPMNDiagram diagram) {
        for (Activity act : diagram.getActivities()) {
            removeHTMLfromActivityLabel(act);
        }
        return diagram;
    }

    public static Activity removeHTMLfromActivityLabel(Activity act) {
        String label = act.getLabel();
        label = label.replace("<html><span style='font-size: 70%;'>", "");
        label = label.replace("</span></html>", "");
        act.getAttributeMap().put(AttributeMap.LABEL, label);

        return act;
    }

    public static BPMNDiagram replaceShortLoopsWithSelfLoops(BPMNDiagram diagram) {

        Set<Flow> checkFlow = new UnifiedSet<>();

        for (Flow f : diagram.getFlows()) {
            if (f.getSource() instanceof Gateway && f.getTarget() instanceof Gateway) {
                Gateway g1 = (Gateway) f.getSource();
                Gateway g2 = (Gateway) f.getTarget();
                if (g1.getGatewayType().equals(Gateway.GatewayType.DATABASED) && g2.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                    checkFlow.add(f);
                }
            }
        }

        Set<Activity> aTargets;
        Set<Activity> bSources;
        Set<BPMNNode> bTargets;
        for (Flow flow : checkFlow) {
            Gateway a = (Gateway) flow.getTarget();
            aTargets = new UnifiedSet<>();
            Gateway b = (Gateway) flow.getSource();
            bSources = new UnifiedSet<>();
            bTargets = new UnifiedSet<>();

            for (Flow f : diagram.getFlows()) {
                if (f.getSource().equals(a) && f.getTarget() instanceof Activity) {
                    aTargets.add((Activity) f.getTarget());
                }
                if (f.getTarget().equals(b) && f.getSource() instanceof Activity) {
                    bSources.add((Activity) f.getSource());
                }
                if (f.getSource().equals(b)) {
                    bTargets.add(f.getTarget());
                }
            }

            if (bTargets.size() > 1) {
                for (Activity node : aTargets) {
                    if (bSources.contains(node)) {
                        node.setBLooped(true);
                        diagram.removeEdge(flow);
                    }
                }
            }
        }

        return removeGatewaysUseless(diagram, diagram.getGateways());

    }

    public static BPMNDiagram removeIntermediateEvents(BPMNDiagram diagram, Collection<Event> events) {
        Set<Flow> removeFlow = new UnifiedSet<>();
        Set<Event> removeGateway = new UnifiedSet<>();
        for (Event g : events) {
            int sources = 0;
            int targets = 0;
            for (Flow flow : diagram.getFlows()) {
                if (flow.getSource().equals(g)) {
                    targets++;
                } else if (flow.getTarget().equals(g)) {
                    sources++;
                }
            }
            if (sources == 1 && targets == 1) {
                removeGateway.add(g);
            }
        }

        Deque<Event> listRemoveGateway = new ArrayDeque<>();
        listRemoveGateway.addAll(removeGateway);

        while (listRemoveGateway.size() > 0) {
            Event g = listRemoveGateway.removeFirst();
            BPMNNode source = null;
            BPMNNode target = null;

            for (Flow flow : diagram.getFlows()) {
                if (flow.getSource().equals(g)) {
                    removeFlow.add(flow);
                    target = flow.getTarget();
                }
                if (flow.getTarget().equals(g)) {
                    removeFlow.add(flow);
                    source = flow.getSource();
                }
            }

            diagram.addFlow(source, target, "");

            for (Flow flow : removeFlow) {
                diagram.removeEdge(flow);
            }
            removeFlow.clear();

            diagram.removeEvent(g);

        }

        return diagram;
    }

    public static void removeMultipleStartAndEndEvents(BPMNDiagram bpmn, int[] counter, int[] eventCounter) {

        Map<Event, Set<BPMNNode>> startEvents = new UnifiedMap<>();
        Map<Event, Set<BPMNNode>> endEvents = new UnifiedMap<>();
        Set<Flow> removedFlow = new UnifiedSet<>();

        Set<BPMNNode> set;
        for (Flow f : bpmn.getFlows()) {
            if (f.getSource() instanceof Event && ((Event) f.getSource()).getEventType().equals(Event.EventType.START)) {
                if ((set = startEvents.get(f.getSource())) == null) {
                    set = new UnifiedSet<>();
                    startEvents.put((Event) f.getSource(), set);
                }
                set.add(f.getTarget());
                removedFlow.add(f);
            }
            if (f.getTarget() instanceof Event && ((Event) f.getTarget()).getEventType().equals(Event.EventType.END)) {
                if ((set = endEvents.get(f.getTarget())) == null) {
                    set = new UnifiedSet<>();
                    endEvents.put((Event) f.getTarget(), set);
                }
                set.add(f.getSource());
                removedFlow.add(f);
            }
        }

        if (startEvents.size() > 1) {

            Gateway orSplit = addGateway(bpmn, "OR", Gateway.GatewayType.PARALLEL, counter);
            Event start = addStartEvent(bpmn, "START", eventCounter);

            BPMNModifier.addFlow(bpmn, start, orSplit);

            for (Map.Entry<Event, Set<BPMNNode>> entry : startEvents.entrySet()) {
                for (BPMNNode node : entry.getValue()) {
                    BPMNModifier.addFlow(bpmn, orSplit, node);
                }
            }

        }

        if (endEvents.size() > 1) {

            Gateway orJoin = addGateway(bpmn, "OR", Gateway.GatewayType.PARALLEL, counter);
            Event end = addEndEvent(bpmn, "END", eventCounter);

            BPMNModifier.addFlow(bpmn, orJoin, end);

            for (Map.Entry<Event, Set<BPMNNode>> entry : endEvents.entrySet()) {
                for (BPMNNode node : entry.getValue()) {
                    BPMNModifier.addFlow(bpmn, node, orJoin);
                }
            }

        }

        for (Flow f : removedFlow) {
            bpmn.removeEdge(f);
        }

        if (startEvents.size() > 1) {
            for (Event event : startEvents.keySet()) {
                bpmn.removeEvent(event);
            }
        }

        if (endEvents.size() > 1) {
            for (Event event : endEvents.keySet()) {
                bpmn.removeEvent(event);
            }
        }

    }

    public static void connectPendingElements(BPMNDiagram bpmn, int[] counter, int[] eventCounter) {
        Set<BPMNNode> nodesTarget = new UnifiedSet<>();
        Set<BPMNNode> nodesSource = new UnifiedSet<>();

        Set<BPMNNode> noInputFlowNodes = new UnifiedSet<>();
        Set<BPMNNode> noOutputFlowNodes = new UnifiedSet<>();

        for (Flow f : bpmn.getFlows()) {
            if (f.getSource() instanceof Gateway || f.getSource() instanceof Activity) {
                nodesSource.add(f.getSource());
            }
            if (f.getTarget() instanceof Gateway || f.getTarget() instanceof Activity) {
                nodesTarget.add(f.getTarget());
            }
        }

        for (BPMNNode node : bpmn.getNodes()) {
            if (node instanceof Gateway || node instanceof Activity) {
                if (!nodesTarget.contains(node)) {
                    noInputFlowNodes.add(node);
                }
                if (!nodesSource.contains(node)) {
                    noOutputFlowNodes.add(node);
                }
            }
        }

        Set<Flow> removedFlow = new UnifiedSet<>();

        if (noOutputFlowNodes.size() > 0) {
            Gateway orJoin = addGateway(bpmn, "AND", Gateway.GatewayType.PARALLEL, counter);
            Map<Event, Set<BPMNNode>> endEvents = new UnifiedMap<>();

            for (BPMNNode node : noOutputFlowNodes) {
                BPMNModifier.addFlow(bpmn, node, orJoin);
            }

            Set<BPMNNode> set;
            for (Flow f : bpmn.getFlows()) {
                if (f.getTarget() instanceof Event && ((Event) f.getTarget()).getEventType().equals(Event.EventType.END)) {
                    if ((set = endEvents.get(f.getTarget())) == null) {
                        set = new UnifiedSet<>();
                        endEvents.put((Event) f.getTarget(), set);
                    }
                    set.add(f.getSource());
                    removedFlow.add(f);
                }
            }

            if (endEvents.size() > 0) {
                for (Map.Entry<Event, Set<BPMNNode>> entry : endEvents.entrySet()) {
                    BPMNModifier.addFlow(bpmn, orJoin, entry.getKey());
                    for (BPMNNode node : entry.getValue()) {
                        BPMNModifier.addFlow(bpmn, node, orJoin);
                    }
                }
            } else {
                Event end = addEndEvent(bpmn, "End", eventCounter);
                BPMNModifier.addFlow(bpmn, orJoin, end);
            }

        }

        if (noInputFlowNodes.size() > 0) {
            Gateway orSplit = addGateway(bpmn, "AND", Gateway.GatewayType.PARALLEL, counter);
            Map<Event, Set<BPMNNode>> startEvents = new UnifiedMap<>();

//            Gateway xorSplit, xorJoin, andSplit;
            for (BPMNNode node : noInputFlowNodes) {
                BPMNModifier.addFlow(bpmn, orSplit, node);
//                xorSplit = addGateway(bpmn, "XORSplit", Gateway.GatewayType.DATABASED, counter);
//                xorJoin = addGateway(bpmn, "XORJoin", Gateway.GatewayType.DATABASED, counter);
//
//                andSplit = addGateway(bpmn, "AND", Gateway.GatewayType.PARALLEL, counter);
//                Set<Flow> remove = new UnifiedSet<>();
//                for(BPMNNode suc : getDirectSuccessors(bpmn, node)) {
//                    for(Flow f : bpmn.getFlows()) {
//                        if(f.getSource().equals(node) && f.getTarget().equals(suc)) {
//                            remove.add(f);
//                            break;
//                        }
//                    }
//                    BPMNModifier.addFlow(bpmn, andSplit, suc);
//                }
//                for(Flow f : remove) {
//                    bpmn.removeEdge(f);
//                }
//
//                BPMNModifier.addFlow(bpmn, orSplit, xorSplit);
//                BPMNModifier.addFlow(bpmn, xorSplit, node);
//                BPMNModifier.addFlow(bpmn, xorSplit, xorJoin);
//                BPMNModifier.addFlow(bpmn, node, xorJoin);
//                BPMNModifier.addFlow(bpmn, xorJoin, andSplit);
            }

            Set<BPMNNode> set;
            for (Flow f : bpmn.getFlows()) {
                if (f.getSource() instanceof Event && ((Event) f.getSource()).getEventType().equals(Event.EventType.START)) {
                    if ((set = startEvents.get(f.getSource())) == null) {
                        set = new UnifiedSet<>();
                        startEvents.put((Event) f.getSource(), set);
                    }
                    set.add(f.getTarget());
                    removedFlow.add(f);
                }
            }

            if (startEvents.size() > 0) {
                for (Map.Entry<Event, Set<BPMNNode>> entry : startEvents.entrySet()) {
                    BPMNModifier.addFlow(bpmn, entry.getKey(), orSplit);
                    for (BPMNNode node : entry.getValue()) {
                        BPMNModifier.addFlow(bpmn, orSplit, node);
                    }
                }
            } else {
                Event start = addStartEvent(bpmn, "Start", eventCounter);
                BPMNModifier.addFlow(bpmn, start, orSplit);
            }

        }

        for (BPMNNode node : noInputFlowNodes) {
            if (node instanceof Activity && noOutputFlowNodes.contains(node)) {
                ((Activity) node).setBLooped(true);
            }
        }

        for (Flow f : removedFlow) {
            bpmn.removeEdge(f);
        }

    }

    public static Gateway addGateway(BPMNDiagram diagram, String name, Gateway.GatewayType type, int[] counter) {
        if (type.equals(Gateway.GatewayType.DATABASED)) {
            counter[0]++;
            return BPMNModifier.addGateway(diagram, name + counter[0], type);
        } else if (type.equals(Gateway.GatewayType.PARALLEL)) {
            counter[1]++;
            return BPMNModifier.addGateway(diagram, name + counter[1], type);
        } else if (type.equals(Gateway.GatewayType.INCLUSIVE)) {
            counter[2]++;
            return BPMNModifier.addGateway(diagram, name + counter[2], type);
        } else {
            return BPMNModifier.addGateway(diagram, name, type);
        }
    }

    private static Event addStartEvent(BPMNDiagram model, String name, int[] counter) {
        counter[0]++;
        return BPMNModifier.addStartEvent(model, name + counter[0]);
    }

    private static Event addEndEvent(BPMNDiagram model, String name, int[] counter) {
        counter[0]++;
        return BPMNModifier.addEndEvent(model, name + counter[0]);
    }

    public static void insertStartAndEndEventsIfMissing(BPMNDiagram bpmn) {
        boolean startMissing = true;
        boolean endMissing = true;
        for (Event event : bpmn.getEvents()) {
            if (event.getEventType().equals(Event.EventType.START)) {
                startMissing = false;
            }
            if (event.getEventType().equals(Event.EventType.END)) {
                endMissing = false;
            }
        }

        if (startMissing) {
            for (Activity activity : bpmn.getActivities()) {
                if (activity.getLabel().contains("Artificial")) {
                    boolean foundStart = true;
                    boolean foundEnd = true;
                    for(Flow f : bpmn.getFlows()) {
                        if(f.getTarget().equals(activity)) {
                            foundStart = false;
                        }
                        if(f.getSource().equals(activity)) {
                            foundEnd = false;
                        }
                    }
                    if(foundStart) {
                        Event e = BPMNModifier.addStartEvent(bpmn, "Start");
                        BPMNModifier.addFlow(bpmn, e, activity);
                    }
                    if(foundEnd && endMissing) {
                        Event e = BPMNModifier.addEndEvent(bpmn, "End");
                        BPMNModifier.addFlow(bpmn, activity, e);
                    }
                }
            }
        }
    }

    public static void insertStartAndEndEventsIfMissing(BPMNDiagram bpmn, int[] eventCounter) {
        boolean startMissing = true;
        boolean endMissing = true;
        for (Flow f : bpmn.getFlows()) {
            if (f.getSource() instanceof Event && ((Event) f.getSource()).getEventType().equals(Event.EventType.START)) {
                startMissing = false;
            }
            if (f.getTarget() instanceof Event && ((Event) f.getTarget()).getEventType().equals(Event.EventType.END)) {
                endMissing = false;
            }
        }

        if (startMissing) {
            addStartEvent(bpmn, "Start", eventCounter);
        }

        if (endMissing) {
            addEndEvent(bpmn, "End", eventCounter);
        }
    }

    public static void removeActivities(BPMNDiagram bpmn, String activityName) {
        Map<BPMNNode, Set<BPMNNode>> output = new UnifiedMap<>();
        Map<BPMNNode, Set<BPMNNode>> input = new UnifiedMap<>();
        Set<Flow> removedFlow = new UnifiedSet<>();
        Set<Activity> removeActivity = new UnifiedSet<>();

        Set<BPMNNode> set;
        for (Flow f : bpmn.getFlows()) {
            if (f.getSource().getLabel().startsWith(activityName)) {
                if ((set = output.get(f.getSource())) == null) {
                    set = new UnifiedSet<>();
                    output.put(f.getSource(), set);
                }
                set.add(f.getTarget());
                removedFlow.add(f);
                removeActivity.add((Activity) f.getSource());
            }
            if (f.getTarget().getLabel().startsWith(activityName)) {
                if ((set = input.get(f.getTarget())) == null) {
                    set = new UnifiedSet<>();
                    input.put(f.getTarget(), set);
                }
                set.add(f.getSource());
                removedFlow.add(f);
                removeActivity.add((Activity) f.getTarget());
            }
        }

        for (Flow f : removedFlow) {
            bpmn.removeEdge(f);
        }

        for (Map.Entry<BPMNNode, Set<BPMNNode>> entry : input.entrySet()) {
            for (BPMNNode source : entry.getValue()) {
                for (BPMNNode target : output.get(entry.getKey())) {
                    if (!containFlow(bpmn, source, target)) {
                        BPMNModifier.addFlow(bpmn, source, target);
                        System.out.println("ADDED " + source.getLabel() + " " + target.getLabel());
                    }
                }
            }
        }

        for (Activity node : removeActivity) {
            bpmn.removeActivity(node);
        }
    }

    private static boolean containFlow(BPMNDiagram bpmn, BPMNNode source, BPMNNode target) {
        for (Flow f : bpmn.getFlows()) {
            if (f.getSource().equals(source) && f.getTarget().equals(target)) {
                return true;
            }
        }
        return false;
    }

    public static void removeUselessEvents(BPMNDiagram bpmn) {
        Set<Event> useFull = new UnifiedSet<>();
        Set<Event> useLess = new UnifiedSet<>();

        for (Flow f : bpmn.getFlows()) {
            if (f.getSource() instanceof Event) {
                useFull.add((Event) f.getSource());
            }
            if (f.getTarget() instanceof Event) {
                useFull.add((Event) f.getTarget());
            }
        }

        for (Event useless : bpmn.getEvents()) {
            if (!useFull.contains(useless)) {
                useLess.add(useless);
            }
        }

        for (Event useless : useLess) {
            bpmn.removeEvent(useless);
        }
    }

    public static void removeDuplicateArcs(BPMNDiagram bpmn) {
        Map<BPMNNode, Map<BPMNNode, Integer>> map = new UnifiedMap<>();

        Iterator<Flow> it = bpmn.getFlows().iterator();
        Map<BPMNNode, Integer> innerMap;
        while (it.hasNext()) {
            Flow f = it.next();
            if ((innerMap = map.get(f.getSource())) == null) {
                innerMap = new UnifiedMap<>();
                map.put(f.getSource(), innerMap);
            }
            Integer count = innerMap.get(f.getTarget());
            if (count == null) {
                count = 0;
            }
            count++;
            if (count == 2) {
                bpmn.removeEdge(f);
                it = bpmn.getFlows().iterator();
                map.clear();
            }else {
                innerMap.put(f.getTarget(), count);
            }
        }

    }

    public static void removeExternalLoop(BPMNDiagram diagram) {
        Map<Event, Set<Gateway>> start = new UnifiedMap<>();
        Map<Event, Set<Gateway>> end = new UnifiedMap<>();

        Set<Gateway> startGateway;
        Set<Gateway> endGateway;
        for (Flow f : diagram.getFlows()) {
            if (f.getSource() instanceof Event && ((Event) f.getSource()).getEventType().equals(Event.EventType.START)
                    && f.getTarget() instanceof Gateway && ((Gateway) f.getTarget()).getGatewayType().equals(Gateway.GatewayType.DATABASED)) {

                if ((startGateway = start.get(f.getSource())) == null) {
                    startGateway = new UnifiedSet<>();
                    start.put((Event) f.getSource(), startGateway);
                }
                startGateway.add((Gateway) f.getTarget());
            } else if (f.getSource() instanceof Gateway && ((Gateway) f.getSource()).getGatewayType().equals(Gateway.GatewayType.DATABASED)
                    && f.getTarget() instanceof Event && ((Event) f.getTarget()).getEventType().equals(Event.EventType.END)) {

                if ((endGateway = end.get(f.getTarget())) == null) {
                    endGateway = new UnifiedSet<>();
                    end.put((Event) f.getTarget(), endGateway);
                }
                endGateway.add((Gateway) f.getSource());
            }
        }

        Set<Flow> remove = new UnifiedSet<>();
        for (Flow f : diagram.getFlows()) {
            for (Map.Entry<Event, Set<Gateway>> entryS : start.entrySet()) {
                for (Gateway gS : entryS.getValue()) {
                    for (Map.Entry<Event, Set<Gateway>> entryE : end.entrySet()) {
                        for (Gateway gE : entryE.getValue()) {
                            if (f.getSource().equals(gE) && f.getTarget().equals(gS)) {
                                remove.add(f);
                            }
                        }
                    }
                }
            }
        }

        for (Flow f : remove) {
            diagram.removeEdge(f);
        }

        removeGatewaysUseless(diagram, diagram.getGateways());
    }

    public static BPMNDiagram fixANDGateway(BPMNDiagram bpmn) {
        BPMNAnalizer bpmnAnalizer = new BPMNAnalizer(XConceptExtension.instance());
        List<Flow> excludedFlow;
        List<Flow> path;
        for (Gateway g : bpmn.getGateways()) {
            if (g.getGatewayType().equals(Gateway.GatewayType.PARALLEL) || g.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
                Iterator<Flow> it = bpmn.getFlows().iterator();
                while (it.hasNext()) {
                    Flow f = it.next();
                    if (f.getSource().equals(g) && f.getTarget() instanceof Gateway) {
                        Gateway g1 = (Gateway) f.getTarget();
                        if (g1.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                            int incoming = 0;
                            int outgoing = 0;
                            for (Flow f1 : bpmn.getFlows()) {
                                if (f1.getTarget().equals(g1)) {
                                    incoming++;
                                }
                                if (f1.getSource().equals(g1)) {
                                    outgoing++;
                                }
                            }
                            if (incoming > 1 && outgoing == 1) {
                                excludedFlow = new ArrayList<>();
                                excludedFlow.add(f);
                                path = bpmnAnalizer.discoverPath(bpmn, g, g1, excludedFlow);
                                if (path != null && path.size() > 0) {
                                    bpmn.removeEdge(f);
                                    it = bpmn.getFlows().iterator();
                                }
                            }
                        }
                    }
                }
            }
        }

        return bpmn;
    }

    public static BPMNDiagram removeUselessSubProcesses(BPMNDiagram bpmn) {
        UnifiedMap<SubProcess, Integer> map = new UnifiedMap<>();
        for (SubProcess s : bpmn.getSubProcesses()) {
            if (!s.getTriggeredByEvent()) {
                map.put(s, 0);
            }
        }

        for (Activity a : bpmn.getActivities()) {
            Integer i;
            if ((i = map.get(a.getParentSubProcess())) != null) {
                i++;
                map.put(a.getParentSubProcess(), i);
            }
        }

        for (SubProcess s : bpmn.getSubProcesses()) {
            Integer i;
            if ((i = map.get(s.getParentSubProcess())) != null) {
                i++;
                map.put(s.getParentSubProcess(), i);
            }
        }

        Set<Flow> removeFlow = new UnifiedSet<>();
        Set<Event> removeEvent = new UnifiedSet<>();
        for (Map.Entry<SubProcess, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                BPMNNode start = null;
                BPMNNode end = null;
                for (Flow f : bpmn.getFlows()) {
                    if (f.getTarget().equals(entry.getKey())) {
                        start = f.getSource();
                        removeFlow.add(f);
                    } else if (f.getSource().equals(entry.getKey())) {
                        end = f.getTarget();
                        removeFlow.add(f);
                    }
                }
                if (start != null && end != null) {
                    for (Event e : bpmn.getEvents()) {
                        if (entry.getKey().equals(e.getParentSubProcess())) {
                            removeEvent.add(e);
                        }
                    }
                    for (Activity a : bpmn.getActivities()) {
                        if (entry.getKey().equals(a.getParentSubProcess())) {
                            a.setBLooped(entry.getKey().isBLooped());
                            a.setBMultiinstance(entry.getKey().isBMultiinstance());
                            a.setParentSubprocess(entry.getKey().getParentSubProcess());
                            bpmn.addFlow(start, a, "");
                            bpmn.addFlow(a, end, "");
                        }
                    }
                    for (Flow f : removeFlow) {
                        bpmn.removeEdge(f);
                    }
                    removeFlow.clear();
                    for (Event e : removeEvent) {
                        bpmn.removeEvent(e);
                    }
                    removeEvent.clear();
                    if(entry.getKey().getParentSubProcess() != null) {
                        entry.getKey().getParentSubProcess().getChildren().remove(entry.getKey());
                    }
                    bpmn.removeSubProcess(entry.getKey());
                }
            }
        }
        return bpmn;
    }

    private static List<BPMNNode> getDirectSuccessors(BPMNDiagram model, BPMNNode node) {
        List<BPMNNode> list = new ArrayList<>();
        for (Flow f : model.getFlows()) {
            if (f.getSource().equals(node)) {
                list.add(f.getTarget());
            }
        }
        return list;
    }

    public static void removeEmptyActivities(BPMNDiagram diagram) {
        Iterator<Activity> it = diagram.getActivities().iterator();
        while(it.hasNext()) {
            Activity a = it.next();
            if(a.getLabel().isEmpty()) {
                List<BPMNNode> sources = new ArrayList<>();
                List<BPMNNode> targets = new ArrayList<>();
                for(Flow f : diagram.getFlows()) {
                    if(f.getSource().equals(a)) {
                        targets.add(f.getTarget());
                    }
                    if(f.getTarget().equals(a)) {
                        sources.add(f.getSource());
                    }
                }
                for(BPMNNode source : sources) {
                    for(BPMNNode target : targets) {
                        diagram.addFlow(source, target, "");
                    }
                }
                diagram.removeActivity(a);
                it = diagram.getActivities().iterator();
            }
        }
    }
}
