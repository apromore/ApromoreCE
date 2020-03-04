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

package com.raffaeleconforti.bpmnminer.subprocessminer;

import com.raffaeleconforti.bpmn.util.BPMNAnalizer;
import com.raffaeleconforti.datastructures.Tree;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.Entity;
import com.raffaeleconforti.foreignkeydiscovery.util.EntityNameExtractor;
import com.raffaeleconforti.log.util.LogModifier;
import com.raffaeleconforti.log.util.LogOptimizer;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Raffaele Conforti on 20/02/14.
 */
public class SubProcessTypeDiscoverer {

    BPMNAnalizer bpmnAnalizer = null;
    EntityDiscoverer entityDiscoverer = null;
    XConceptExtension xce = null;
    XTimeExtension xte = null;
    LogModifier logMod = null;
    boolean sortLog = false;

    public SubProcessTypeDiscoverer(BPMNAnalizer bpmnAnalizer, EntityDiscoverer entityDiscoverer, XConceptExtension xce, XTimeExtension xte, LogOptimizer logOptimizer, boolean sortLog) {
        this.bpmnAnalizer = bpmnAnalizer;
        this.entityDiscoverer = entityDiscoverer;
        this.xce = xce;
        this.xte = xte;
        this.sortLog = sortLog;
        this.logMod = new LogModifier(XFactoryRegistry.instance().currentDefault(), xce, xte, logOptimizer);
    }

    public boolean isInterruptingEventSubProcess(BPMNDiagram process, BPMNDiagram subProcessDiagram, Activity subProcess, Map<Entity, XLog> logs,
                                                 Entity parentEntity, double tolerance) {

        Set<Activity> pre = bpmnAnalizer.findLastActivitiesExcludeANDGateway(process);

        boolean checkStart = false;
        if (!pre.contains(subProcess)) return false;
        else if (pre.size() > 1) checkStart = true;

        if (checkStart) {
            Set<Activity> last = bpmnAnalizer.findLastActivities(subProcessDiagram);
            Set<String> eventNames = new UnifiedSet<String>(last.size());
            for (Activity activity : last) {
                eventNames.add(bpmnAnalizer.extractActivityLabel(activity));
            }

            return percentageProcessInstanceEnd(parentEntity, eventNames, logs) >= 1 - tolerance;
        }

        return false;
    }

    private boolean doesProcessInstanceAlwaysEnd(Entity grandParent, Set<String> eventNames, UnifiedMap<Entity, XLog> logs) {
        XLog log = logs.get(grandParent);

        boolean correct = false;
        for (XTrace trace : log) {
            boolean occurs = false;
            for (XEvent event : trace) {
                if (eventNames.contains(xce.extractName(event))) {
                    occurs = true;
                    correct = true;
                    break;
                }
            }
            if (occurs && !eventNames.contains(xce.extractName(trace.get(trace.size() - 1)))) {
                correct = false;
                break;
            }
        }
        return correct;
    }

    private double percentageProcessInstanceEnd(Entity grandParent, Set<String> eventNames, Map<Entity, XLog> logs) {

        double terminateWell = 0;
        double notTerminateWell = 0;

        XLog log = logs.get(grandParent);

        for (XTrace trace : log) {
            boolean occurs = false;
            boolean notLast = false;
            for (XEvent event : trace) {
                if (eventNames.contains(xce.extractName(event)) && !occurs) {
                    occurs = true;
                }

                if (!eventNames.contains(xce.extractName(event)) && occurs) {
                    notLast = true;
                    break;
                }
            }
            if (occurs) {
                if (notLast) {
                    notTerminateWell++;
                } else {
                    terminateWell++;
                }
            }
        }

        if (terminateWell == 0 && notTerminateWell == 0) return 0;
        return terminateWell / (notTerminateWell + terminateWell);
    }

    public Map<Entity, XLog> generateCompleteRootLog(Tree tree, Map<Entity, XLog> logs) {

        List<Tree.Node> leaves;
        while ((leaves = tree.findLeaves()).size() > 0) {

            Tree.Node node = leaves.remove(0);
            Entity entity = (Entity) node.getData();

            if (node != tree.getRoot()) {
                updateParentLog((Entity) node.getParent().getData(), entity, logs);
            }

            tree.removeLeave(node);
        }

        return logs;

    }

    private void updateParentLog(Entity parent, Entity child, Map<Entity, XLog> logs) {

        List<XLog> set = new ArrayList<XLog>();
        set.add(logs.get(parent));
        set.add(logs.get(child));
        XLog newLog = mergeHierarchicalLogs(set, parent);
        logs.put(parent, newLog);

    }

    private XLog mergeHierarchicalLogs(List<XLog> relativeLogs, Entity parent) {

        XFactory factory = XFactoryRegistry.instance().currentDefault();
        XLog main;

        XLog log = relativeLogs.get(0);

        main = factory.createLog(log.getAttributes());
        for (XTrace trace : log) {
            XTrace newTrace = factory.createTrace(trace.getAttributes());
            for (XEvent event : trace) {
                XEvent newEvent = factory.createEvent(event.getAttributes());
                newTrace.add(newEvent);
            }
            main.add(newTrace);
        }

        log = relativeLogs.get(1);

        for (XTrace trace : log) {

            List<String> traceName = new ArrayList<String>();
            List<String> correctKey = new ArrayList<String>();
            for (String key : EntityNameExtractor.getEntityName(parent)) {//evTypeNames(child)) {
                traceName.add(logMod.getAttributeValue(trace.get(0).getAttributes().get(key)));
                correctKey.add(key);
            }

            boolean traceFound = false;

            for (XEvent event : trace) {

                boolean eventFound = false;

                for (XTrace t : main) {

                    boolean sameTrace = false;
                    for (int z = 0; z < traceName.size(); z++) {
                        String key = correctKey.get(z);
                        String name = traceName.get(z);
                        String val = logMod.getAttributeValue(t.get(0).getAttributes().get(key));
                        if (val != null && name != null && name.equals(val)) {
                            sameTrace = true;
                            break;
                        }
                    }

                    if (sameTrace) {
                        traceFound = true;
                        for (XEvent e : t) {
                            if (logMod.sameEvent(event, e, false)) {
                                eventFound = true;
                                break;
                            }
                        }
                        if (!eventFound) {
                            t.add(event);
                            eventFound = true;
                        }
                    }
                    if (eventFound) break;
                }
            }
            if (!traceFound) {
                main.add(trace);
            }

        }

        main = logMod.removeArtificialStartAndEndEvent(main);
        if (sortLog) {
            main = logMod.sortLog(main);
        }

        return main;

    }

    public boolean isEventSubProcess(BPMNDiagram process, Activity subProcess, boolean isLoop) {

        if (!isLoop) return false;

        Map<Gateway, Gateway> extremes = new UnifiedMap<Gateway, Gateway>();
        Map<Gateway, Gateway> skip = new UnifiedMap<Gateway, Gateway>();

        for (Flow flow : process.getFlows()) {

            if (flow.getTarget().equals(subProcess) && flow.getSource() instanceof Gateway) {
                Gateway g1 = (Gateway) flow.getSource();
                if (g1.getGatewayType().equals(Gateway.GatewayType.PARALLEL) || g1.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {

                    for (Flow flow1 : process.getFlows()) {
                        if (flow1.getSource().equals(subProcess) && flow1.getTarget() instanceof Gateway) {
                            Gateway g2 = (Gateway) flow1.getTarget();
                            if (g1.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                                if (g2.getGatewayType().equals(Gateway.GatewayType.PARALLEL) || g2.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
                                    extremes.put(g1, g2);
                                }
                            } else if (g1.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
                                if (g2.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
                                    extremes.put(g1, g2);
                                }
                            }
                        }
                    }

                }

                if (g1.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                    for (Flow flow1 : process.getFlows()) {
                        if (flow1.getSource().equals(subProcess) && flow1.getTarget() instanceof Gateway) {
                            Gateway g2 = (Gateway) flow1.getTarget();
                            if (g2.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                                for (Flow flow2 : process.getFlows()) {
                                    if ((flow2.getSource().equals(g1) && flow2.getTarget().equals(g2)) || (flow2.getSource().equals(g2) && flow2.getTarget().equals(g1))) {
                                        skip.put(g1, g2);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for (Map.Entry<Gateway, Gateway> entry : skip.entrySet()) {
            for (Flow flow : process.getFlows()) {

                if (flow.getTarget().equals(entry.getKey()) && flow.getSource() instanceof Gateway && !flow.getTarget().equals(entry.getValue())) {
                    Gateway g1 = (Gateway) flow.getSource();
                    if (g1.getGatewayType().equals(Gateway.GatewayType.PARALLEL) || g1.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {

                        for (Flow flow1 : process.getFlows()) {
                            if (flow1.getSource().equals(entry.getValue()) && flow1.getTarget() instanceof Gateway && !flow1.getTarget().equals(entry.getKey())) {
                                Gateway g2 = (Gateway) flow1.getTarget();
                                if (g1.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                                    if (g2.getGatewayType().equals(Gateway.GatewayType.PARALLEL) || g2.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
                                        extremes.put(g1, g2);
                                    }
                                } else if (g1.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
                                    if (g2.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
                                        extremes.put(g1, g2);
                                    }
                                }
                            }
                        }

                    }
                }

                if (flow.getSource().equals(entry.getKey()) && flow.getTarget() instanceof Gateway && !flow.getTarget().equals(entry.getValue())) {
                    Gateway g2 = (Gateway) flow.getTarget();
                    if (g2.getGatewayType().equals(Gateway.GatewayType.PARALLEL) || g2.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {

                        for (Flow flow1 : process.getFlows()) {
                            if (flow1.getTarget().equals(entry.getValue()) && flow1.getSource() instanceof Gateway && !flow1.getSource().equals(entry.getKey())) {
                                Gateway g1 = (Gateway) flow1.getSource();
                                if (g1.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                                    if (g2.getGatewayType().equals(Gateway.GatewayType.PARALLEL) || g2.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
                                        extremes.put(g1, g2);
                                    }
                                } else if (g1.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
                                    if (g2.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
                                        extremes.put(g1, g2);
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }

        for (Map.Entry<Gateway, Gateway> gatewayGatewayEntry : extremes.entrySet()) {
            int count1 = 0;
            int count2 = 0;
            boolean isAfterStart = false;
            boolean isBeforeEnd = false;
            for (Flow flow : process.getFlows()) {
                if (flow.getSource().equals(gatewayGatewayEntry.getKey())) {
                    count1++;
                }
                if (flow.getTarget().equals(gatewayGatewayEntry.getKey()) && flow.getSource() instanceof Event) {
                    Event e = (Event) flow.getSource();
                    if (e.getEventType().equals(Event.EventType.START)) isAfterStart = true;
                }
            }

            if (count1 > 1 && isAfterStart) {
                Gateway g2 = gatewayGatewayEntry.getValue();
                for (Flow flow : process.getFlows()) {
                    if (flow.getTarget().equals(g2)) {
                        count2++;
                    }
                    if (flow.getSource().equals(g2) && flow.getTarget() instanceof Event) {
                        Event e = (Event) flow.getTarget();
                        if (e.getEventType().equals(Event.EventType.END)) isBeforeEnd = true;
                    }
                }
                if (count2 > 1 && isBeforeEnd) return true;
            }
        }

        return false;
    }

    public boolean isMultiInstance(BPMNDiagram process, XLog log, Entity primary, Entity secondary, double percentage, double tolerance) {

        int multi = 0;
        int tracesMulti = 0;
        int tracesNonMulti = 0;
        log = logMod.removeArtificialStartAndEndEvent(log);
        for (XTrace trace : log) {
            boolean hasSeveralInstances = false;
            long start = xte.extractTimestamp(trace.get(0)).getTime();
            long end = xte.extractTimestamp(trace.get(trace.size() - 1)).getTime();

            int pos = -1;
            XEvent firstEvent = null;
            XEvent secondEvent;

            int newPos = 0;
            while (newPos >= 0) {
                newPos = bpmnAnalizer.findPositionOfEventOfSubProcess(process, trace, newPos);

                if (newPos == -1) break;
                if (pos == -1) {
                    pos = newPos;
                    firstEvent = trace.get(pos);
                } else if (newPos > -1) {
                    secondEvent = trace.get(newPos);

                    if (isSameSubProcessInstance(primary, secondary, firstEvent, secondEvent)) {
                        pos = newPos;
                        firstEvent = secondEvent;
                    } else {
                        hasSeveralInstances = true;
                        if (!isXEventFromLastActivity(process, firstEvent) || isSameTimeStamp(firstEvent, secondEvent, end - start, percentage)) {
                            multi++;
                            break;
                        }
                    }
                } else {
                    break;
                }

                newPos++;
            }

            if (hasSeveralInstances) {
                if ((multi > 0)) {
                    tracesMulti++;
                } else {
                    tracesNonMulti++;
                }
            }
            multi = 0;
        }

//        return (multi + nonMulti > 0) && (nonMulti <= ((multi + nonMulti) * tolerance));
        return (tracesMulti + tracesNonMulti > 0) && (tracesNonMulti <= ((tracesMulti + tracesNonMulti) * tolerance));

    }

    private int[] discoverMaxMultiInstances(BPMNDiagram process, XLog log, Entity primary, Entity secondary) {
        int max = 0;
        int min = Integer.MAX_VALUE;

        log = logMod.removeArtificialStartAndEndEvent(log);
        for (XTrace trace : log) {
            int count = 0;

            UnifiedMap<Set<String>, Set<Set<String>>> map = new UnifiedMap<Set<String>, Set<Set<String>>>();

            int pos = -1;
            XEvent firstEvent = null;
            XEvent secondEvent;

            int newPos = 0;
            while (newPos >= 0) {
                newPos = bpmnAnalizer.findPositionOfEventOfSubProcess(process, trace, newPos);

                if (newPos == -1) break;
                if (pos == -1) {
                    pos = newPos;
                    firstEvent = trace.get(pos);
                } else if (newPos > -1) {
                    secondEvent = trace.get(newPos);

                    if (isSameSubProcessInstance(primary, secondary, firstEvent, secondEvent)) {
                        pos = newPos;
                        firstEvent = secondEvent;
                    } else {
                        Set<String> key = entityValue(secondary, firstEvent);

                        Set<Set<String>> value;
                        if ((value = map.get(key)) == null) {
                            value = new UnifiedSet<Set<String>>();
                            map.put(key, value);
                        }

                        Set<String> secondKey = entityValue(secondary, secondEvent);
                        if (!value.contains(secondKey)) {
                            value.add(secondKey);

                            Set<Set<String>> value1;
                            if ((value1 = map.get(secondKey)) == null) {
                                value1 = new UnifiedSet<Set<String>>();
                                map.put(secondKey, value1);
                            }
                            value1.add(key);

                            count++;
                        }
                    }
                } else {
                    break;
                }

                newPos++;
            }

            max = Math.max(max, count);
            min = Math.min(min, count);
        }

        return new int[]{min, max};

    }

    private boolean isSameTimeStamp(XEvent firstEvent, XEvent secondEvent, double total, double acceptableTimeDifference) {
        return (xte.extractTimestamp(secondEvent).getTime() - xte.extractTimestamp(firstEvent).getTime() <= (total * acceptableTimeDifference));
    }

    public boolean isMultiInstanceActivity(Activity activity, XLog log) {

        for (XTrace trace : log) {

            int pos = -1;
            XEvent firstEvent = null;
            XEvent secondEvent;

            int newPos = 0;
            while (newPos >= 0) {
                newPos = bpmnAnalizer.findPositionOfEventOfActivity(activity, trace, newPos);

                if (newPos == -1) break;
                if (pos == -1) {
                    pos = newPos;
                    firstEvent = trace.get(pos);
                } else if (newPos > -1 && pos != newPos) {
                    secondEvent = trace.get(newPos);

                    long t1 = xte.extractTimestamp(firstEvent).getTime();
                    long t2 = xte.extractTimestamp(secondEvent).getTime();
                    if (t1 == t2) {
                        return true;
                    }
                } else if (newPos <= -1) {
                    break;
                }

                newPos++;
            }
        }

        return false;

    }

    public boolean isLoop(BPMNDiagram diagram, XLog log, Entity primary, Entity secondary) {

        for (XTrace trace : log) {

            int pos = -1;
            XEvent firstEvent = null;
            XEvent secondEvent;

            int newPos = 0;
            while (newPos >= 0) {
                newPos = bpmnAnalizer.findPositionOfEventOfSubProcess(diagram, trace, newPos);

                if (newPos == -1) break;
                if (pos == -1) {
                    pos = newPos;
                    firstEvent = trace.get(pos);
                } else if (newPos > -1) {
                    secondEvent = trace.get(newPos);

                    if (isSameSubProcessInstance(primary, secondary, firstEvent, secondEvent)) {
                        pos = newPos;
                        firstEvent = secondEvent;
                    } else {
                        return true;
                    }
                } else {
                    break;
                }

                newPos++;
            }
        }
        return false;
    }

    private boolean isSameSubProcessInstance(Entity primary, Entity secondary, XEvent firstEvent, XEvent secondEvent) {

        Set<String> primaryEntities = EntityNameExtractor.getEntityName(primary);//new UnifiedSet<String>();
        Set<String> secondaryEntities = EntityNameExtractor.getEntityName(secondary);//new UnifiedSet<String>();

        return isSameAttributeForEntity(primaryEntities, firstEvent, secondEvent) && isSameAttributeForEntity(secondaryEntities, firstEvent, secondEvent);

    }

    private Set<String> entityValue(Entity entity, XEvent event) {

        Set<String> entityNames = EntityNameExtractor.getEntityName(entity);

        Set<String> result = new UnifiedSet<String>();
        for (String name : entityNames) {
            result.add(logMod.getAttributeValue(event.getAttributes().get(name)));
        }

        return result;

    }

    private boolean isSameAttributeForEntity(Set<String> entity, XEvent firstEvent, XEvent secondEvent) {

        for (String sk : entity) {

            String fValue = logMod.getAttributeValue(firstEvent.getAttributes().get(sk));
            String sValue = logMod.getAttributeValue(secondEvent.getAttributes().get(sk));

            if (fValue != null || sValue != null) {
                if (fValue == null || sValue == null) return false;
                else if (!fValue.equals(sValue)) return false;
            }

        }
        return true;
    }


    private boolean isXEventFromLastActivity(BPMNDiagram process, XEvent event) {

        boolean found = false;
        for (Event e : process.getEvents()) {
            if (e.getEventType().equals(Event.EventType.END)) {
                for (Activity activity : getPreviousActivities(process, e, null)) {
                    if (bpmnAnalizer.extractActivityLabel(activity).equals(xce.extractName(event))) {
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
        }

        return found;
    }

    private List<Activity> getPreviousActivities(BPMNDiagram process, BPMNNode node, Set<BPMNNode> visited) {

        if (visited == null) visited = new UnifiedSet<BPMNNode>();
        List<Activity> previous = new ArrayList<Activity>();

        for (Flow flow : process.getFlows()) {
            if (flow.getTarget().equals(node)) {
                if (!visited.contains(flow.getSource())) {
                    visited.add(flow.getSource());
                    if (flow.getSource() instanceof Activity) {
                        previous.add((Activity) flow.getSource());
                    } else {
                        previous.addAll(getPreviousActivities(process, flow.getSource(), visited));
                    }
                }
            }
        }

        return previous;

    }

    public boolean isTimerEvent(BPMNDiagram parentDiagram, BPMNDiagram diagram, Activity activity, XLog log, double percentage, double tolerance) {
        Set<Activity> previousActivities = bpmnAnalizer.findPreviousActivities(parentDiagram, activity);
        Set<Activity> firstActivities = bpmnAnalizer.findFirstActivities(diagram);

        List<Long> diffTimes = new ArrayList<Long>();

        for (XTrace trace : log) {
            Long min = Long.MAX_VALUE;
            for (Activity previousActivity : previousActivities) {
                int previousActivityPos = bpmnAnalizer.findPositionOfEventOfActivity(previousActivity, trace, -1);

                if (previousActivityPos > -1) {
                    for (Activity firstActivity : firstActivities) {
                        int firstActivityPos = bpmnAnalizer.findPositionOfEventOfActivity(firstActivity, trace, -1);

                        if (firstActivityPos > -1) {
                            XEvent event1 = trace.get(previousActivityPos);
                            XEvent event2 = trace.get(firstActivityPos);
                            min = Math.min(min, xte.extractTimestamp(event2).getTime() - xte.extractTimestamp(event1).getTime());
                        }
                    }
                }
            }
            if (min.compareTo(Long.MAX_VALUE) != 0) {
                diffTimes.add(min);
            }
        }

        double average = 0L;
        for (long l : diffTimes) {
            average += l;
        }
        average /= diffTimes.size();

        double threshold = average * percentage;
        int wrongValue = 0;
        for (long l : diffTimes) {
            if (Math.abs(average - l) > threshold) {
                wrongValue++;
            }
        }

        return (wrongValue <= (diffTimes.size() * tolerance));

    }
}
