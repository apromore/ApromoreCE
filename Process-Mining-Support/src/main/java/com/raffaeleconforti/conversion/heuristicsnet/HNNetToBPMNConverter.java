/***********************************************************
 *      This software is part of the ProM package          *
 *             http://www.processmining.org/               *
 *                                                         *
 *            Copyright (c) 2003-2006 TU/e Eindhoven       *
 *                and is licensed under the                *
 *            Common Public License, Version 1.0           *
 *        by Eindhoven University of Technology            *
 *           Department of Information Systems             *
 *                 http://is.tm.tue.nl                     *
 *                                                         *
 **********************************************************/

package com.raffaeleconforti.conversion.heuristicsnet;

import com.raffaeleconforti.bpmn.util.BPMNSimplifier;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.framework.log.LogEvent;
import org.processmining.framework.models.heuristics.HNSet;
import org.processmining.framework.models.heuristics.HNSubSet;
import org.processmining.framework.models.heuristics.HeuristicsNet;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;

import java.util.*;


/**
 * Adapted from ProM's 5 HNNetToEPCConverter but using jBPT's EPC.
 */

public class HNNetToBPMNConverter {
    private static final String EVENTSTRING = "Status change to \\n";

    public static BPMNDiagram convert(HeuristicsNet hNet) {

        if (hNet == null) {
            return null;
        }

        int[] counter = new int[]{0, 0};

//		ProcessModel epc = new Epc();
        BPMNDiagram bpmn = BPMNDiagramFactory.newBPMNDiagram("");

        // Because we may have duplicates, the arrays "functions" and
        // "events" keep track of the epc functions and events
        // for every task in the HeuristicsNet "hNet".
//		Function[] functions = new Function[hNet.getDuplicatesMapping().length];
//		Event[] events = new Event[hNet.getDuplicatesMapping().length];
        Activity[] activities = new Activity[hNet.getDuplicatesMapping().length];
        Event[] events = new Event[hNet.getDuplicatesMapping().length];

        // creating and connecting the events to functions
        for (int index = 0; index < hNet.getDuplicatesMapping().length; index++) {

            LogEvent le = hNet.getLogEvents().getEvent(
                    hNet.getDuplicatesMapping()[index]);
            // Skip those tasks that do not have arcs and are not the initial or
            // final task
//			functions[index] = new Function(le.getModelElementName() + " " + le.getEventType());
//			events[index] = new Event(EVENTSTRING + le.getModelElementName()
//					+ " " + le.getEventType());
//			epc.addControlFlow(events[index], functions[index]);
            activities[index] = bpmn.addActivity(le.getModelElementName() + "+" + le.getEventType(), false, false, false, false, false);
            events[index] = bpmn.addEvent(EVENTSTRING + le.getModelElementName() + "+" + le.getEventType(), Event.EventType.INTERMEDIATE, Event.EventTrigger.NONE, null, true, null);
//            bpmn.addFlow(events[index], activities[index], "");
            addFlow(bpmn, events[index], activities[index]);
        }

//		UnifiedMap<UniqueSet, FlowNode> mapping = new UnifiedMap<UniqueSet, FlowNode>();
        Map<UniqueSet, BPMNNode> mapping = new UnifiedMap<UniqueSet, BPMNNode>();

//		Event initialEvent = new Event("fictive start");
//		XorConnector initialConn = new XorConnector("XOR");
        Event initialEvent = bpmn.addEvent("fictive start", Event.EventType.START, Event.EventTrigger.NONE, null, true, null);
        Gateway initialConn = addGateway(bpmn, "XOR", Gateway.GatewayType.DATABASED, counter);

//		epc.addEdge(initialEvent, initialConn);
//        bpmn.addFlow(initialEvent, initialConn, "");
        addFlow(bpmn, initialEvent, initialConn);

        for (int i = 0; i < hNet.getInputSets().length; i++) {
            // Look at the AND-set
            HNSet andSet = hNet.getInputSets()[i];

//			FlowNode andConn = events[i];
            BPMNNode andConn = events[i];
            if (andConn == null) {
                continue;
            }

            if (andSet.size() == 0) {
//				epc.removeFlowNode(andConn);
//				epc.addEdge(initialConn, functions[i]);
//				andConn = initialConn;
                bpmn.removeNode(andConn);
//				bpmn.addFlow(initialConn, activities[i], "");
                addFlow(bpmn, initialConn, activities[i]);
                andConn = initialConn;
            }

            if (andSet.size() > 1) {
//				AndConnector c = new AndConnector("AND");
//				epc.addEdge(c, andConn);
//				andConn = c;
                Gateway c = addGateway(bpmn, "AND", Gateway.GatewayType.PARALLEL, counter);
//                bpmn.addFlow(c, andConn, "");
                addFlow(bpmn, c, andConn);
                andConn = c;
            }

            for (int orSetIt = 0; orSetIt < andSet.size(); orSetIt++) {
                HNSubSet orSet = andSet.get(orSetIt);
//				FlowNode xorConn = andConn;
                BPMNNode xorConn = andConn;
                if (orSet.size() > 1) {
//					xorConn = new XorConnector("XOR");
//                  epc.addEdge(xorConn, andConn);
                    xorConn = addGateway(bpmn, "XOR", Gateway.GatewayType.DATABASED, counter);
//                    bpmn.addFlow(xorConn, andConn, "");
                    addFlow(bpmn, xorConn, andConn);
                }
                mapping.put(new UniqueSet(orSet, i, true), xorConn);
            }
        }
//		if (epc.getDirectSuccessors(initialConn).size() == 1) {
//			epc.addEdge(initialEvent, epc.getDirectSuccessors(initialConn).iterator().next());
//			epc.removeFlowNode(initialConn);
//		}
        if (getDirectSuccessors(bpmn, initialConn).size() == 1) {
//            bpmn.addFlow(initialEvent, getDirectSuccessors(bpmn, initialConn).iterator().next(), "");
            addFlow(bpmn, initialEvent, getDirectSuccessors(bpmn, initialConn).iterator().next());
            bpmn.removeNode(initialConn);
        }

//		Event finalEvent = new Event("fictive end");
//		XorConnector finalConn = new XorConnector("XOR");
        Event finalEvent = bpmn.addEvent("fictive end", Event.EventType.END, Event.EventTrigger.NONE, null, true, null);
        Gateway finalConn = addGateway(bpmn, "XOR", Gateway.GatewayType.DATABASED, counter);

//		epc.addEdge(finalConn, finalEvent);
//        bpmn.addFlow(finalConn, finalEvent, "");
        addFlow(bpmn, finalConn, finalEvent);

        for (int i = 0; i < hNet.getOutputSets().length; i++) {
            // Look at the AND-set
            HNSet andSet = hNet.getOutputSets()[i];

//			FlowNode andConn = functions[i];
            BPMNNode andConn = activities[i];
            if (andConn == null) {
                continue;
            }

            if (andSet.size() == 0) {
//				epc.addEdge(andConn, finalConn);
//                bpmn.addFlow(andConn, finalConn, "");
                addFlow(bpmn, andConn, finalConn);
            }

            if (andSet.size() > 1) {
//				AndConnector c = new AndConnector("AND");
//				epc.addEdge(andConn, c);
//				andConn = c;
                Gateway c = addGateway(bpmn, "AND", Gateway.GatewayType.PARALLEL, counter);
//                bpmn.addFlow(andConn, c, "");
                addFlow(bpmn, andConn, c);
                andConn = c;
            }

            for (int orSetIt = 0; orSetIt < andSet.size(); orSetIt++) {
                HNSubSet orSet = andSet.get(orSetIt);
//				FlowNode xorConn = andConn;
                BPMNNode xorConn = andConn;
                if (orSet.size() > 1) {
//					xorConn = new XorConnector("XOR");
//					epc.addEdge(andConn, xorConn);
                    xorConn = addGateway(bpmn, "XOR", Gateway.GatewayType.DATABASED, counter);
//                    bpmn.addFlow(andConn, xorConn, "");
                    addFlow(bpmn, andConn, xorConn);
                }
                mapping.put(new UniqueSet(orSet, i, false), xorConn);
            }
        }

//		if (epc.getDirectPredecessors(finalConn).size() == 1) {
//			epc.addControlFlow( epc.getDirectPredecessors(finalConn).iterator()
//					.next(), finalEvent);
//			epc.removeFlowNode(finalConn);
//		}
        if (getDirectPredecessors(bpmn, finalConn).size() == 1) {
//            bpmn.addFlow(getDirectPredecessors(bpmn, finalConn).iterator().next(), finalEvent, "");
            addFlow(bpmn, getDirectPredecessors(bpmn, finalConn).iterator().next(), finalEvent);
            bpmn.removeNode(finalConn);
        }

        // Every Function is now an atomic connected part of the graph. It has
        // a number of input connectors and output connectors, each of which
        // correspond to a specific inSet or outSet

        // Now, look at all the edges that have to be made.
        for (int i = 0; i < hNet.getOutputSets().length; i++) {
            HNSet andSet = hNet.getOutputSets()[i];

            for (int j = 0; j < andSet.size(); j++) {

                for (int orSetIt = 0; orSetIt < andSet.size(); orSetIt++) {
                    HNSubSet orSet = andSet.get(orSetIt);
                    for (int destIt = 0; destIt < orSet.size(); destIt++) {
                        int k = orSet.get(destIt);
                        // There is a connection between epc.getFunctions.get(i)
                        // -->
                        // epc.getFunctions.get(k);
                        // This edge corresponds to orSet --> some orSet in the
                        // InSet collections of k

                        HNSubSet t = null;
                        HNSet inSet = hNet.getInputSet(k);
                        for (int inSetIt = 0; inSetIt < inSet.size(); inSetIt++) {
                            if ((t == null)) {
                                HNSubSet t2 = inSet.get(inSetIt);
                                if (t2.contains(i)) {
                                    t = t2;
                                }
                            } else {
                                break;
                            }
                        }

                        if (t != null) {
                            UniqueSet s1;
                            UniqueSet s2 = new UniqueSet(orSet, i, false);

                            Iterator<UniqueSet> keys = mapping.keySet().iterator();
                            do {
                                s1 = keys.next();
                            } while (!s1.equals(s2));
//							FlowNode o1 = (FlowNode) mapping.get(s1);
                            BPMNNode o1 = mapping.get(s1);

                            s2 = new UniqueSet(t, k, true);
                            keys = mapping.keySet().iterator();
                            do {
                                s1 = keys.next();
                            } while (!s1.equals(s2));
//							FlowNode o2 = mapping.get(s1);
                            BPMNNode o2 = mapping.get(s1);

//							epc.addEdge(o1, o2);
//                            bpmn.addFlow(o1, o2, "");
                            addFlow(bpmn, o1, o2);
                        }
                    }
                }
            }

        }

        for (int i = hNet.getInputSets().length - 1; i > 0; i--) {
            if ((hNet.getInputSets()[i].size() == 0 && !hNet.getStartTasks()
                    .contains(i))
                    && (hNet.getOutputSets()[i].size() == 0 && !hNet
                    .getEndTasks().contains(i))) {
                // remove this event and function
//				epc.removeFlowNode(functions[i]);
//				epc.removeFlowNode(events[i]);
                bpmn.removeNode(activities[i]);
                bpmn.removeNode(events[i]);
            }
        }

        // In the original code, the class "ConnectorStructureExtractor" was used
        // for removing all disconnected nodes and spurious connectors (?)
        // jBPT allows one to remove disconnected nodes, and that is why
        // I am not using "ConnectorStructureExtractor"
//		Collection<FlowNode> disconnectedNodes = epc.getDisconnectedVertices();
//		epc.removeVertices(disconnectedNodes);

        removeUselessEvents(bpmn);
        BPMNSimplifier.basicSimplification(bpmn);

        return bpmn;
    }

    private static void removeUselessEvents(BPMNDiagram bpmn) {

        Set<Flow> removeFlow = new UnifiedSet<Flow>();
        Set<Event> remove = new UnifiedSet<Event>();
        Map<BPMNNode, Set<BPMNNode>> add = new UnifiedMap<BPMNNode, Set<BPMNNode>>();

        for (Flow flow : bpmn.getFlows()) {

            Set<BPMNNode> set;

            if (flow.getTarget() instanceof Event) {
                if (((Event) flow.getTarget()).getEventType() == Event.EventType.INTERMEDIATE) {
                    if ((set = add.get(flow.getSource())) == null) {
                        set = new UnifiedSet<BPMNNode>();
                        add.put(flow.getSource(), set);
                    }

                    for (Flow flow2 : bpmn.getFlows()) {
                        if (flow2.getSource().equals(flow.getTarget())) {
                            set.add(flow2.getTarget());
                            removeFlow.add(flow2);
                        }
                    }
                    removeFlow.add(flow);
                    remove.add((Event) flow.getTarget());
                }

            }
        }

        for (Map.Entry<BPMNNode, Set<BPMNNode>> entry : add.entrySet()) {
            for (BPMNNode target : entry.getValue()) {
//                bpmn.addFlow(entry.getKey(), target, "");
                addFlow(bpmn, entry.getKey(), target);
            }
        }

        for (Flow flow : removeFlow) {
            bpmn.removeEdge(flow);
        }

        for (Event event : remove) {
            bpmn.removeEvent(event);
        }
    }

    private static List<BPMNNode> getDirectSuccessors(BPMNDiagram model, BPMNNode node) {
        List<BPMNNode> list = new ArrayList<BPMNNode>();
        for (Flow f : model.getFlows()) {
            if (f.getSource().equals(node)) {
                list.add(f.getTarget());
            }
        }
        return list;
    }

    private static List<BPMNNode> getDirectPredecessors(BPMNDiagram model, BPMNNode node) {
        List<BPMNNode> list = new ArrayList<BPMNNode>();
        for (Flow f : model.getFlows()) {
            if (f.getTarget().equals(node)) {
                list.add(f.getSource());
            }
        }
        return list;
    }

    private static Flow addFlow(BPMNDiagram model, BPMNNode source, BPMNNode target) {
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

    private static Gateway addGateway(BPMNDiagram diagram, String name, Gateway.GatewayType type, int[] counter) {
        if (type.equals(Gateway.GatewayType.DATABASED)) {
            counter[0]++;
            return diagram.addGateway(name + counter[0], type);
        } else if (type.equals(Gateway.GatewayType.PARALLEL)) {
            counter[1]++;
            return diagram.addGateway(name + counter[1], type);
        } else {
            return diagram.addGateway(name, type);
        }
    }

}
