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

import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 16/02/15.
 */
public class BPMNGatewayMerger {

    public static BPMNDiagram mergeDiagram(BPMNDiagram diagram) {
        diagram = separateSplitFromJoin(diagram);
        diagram = removeXORtoAND(diagram);
        diagram = removeConnectedGateways(diagram);

        int curr = diagram.getNodes().size() + diagram.getFlows().size();
        int size = 0;
        while (size != curr) {
            mergeActivitiesAfterXOR(diagram);
            mergeActivitiesBeforeXOR(diagram);
            size = curr;
            curr = diagram.getNodes().size() + diagram.getFlows().size();
        }

        return diagram;
    }

    private static BPMNDiagram mergeActivitiesBeforeXOR(BPMNDiagram diagram) {
        boolean repeat = true;
        loops:
        while (repeat) {
            repeat = false;
            List<Gateway> gateways = getXORGateways(diagram);
            for(Gateway gateway : gateways) {
                List<Activity> activities = getIncomingActivities(diagram, gateway);
                for(int i = 0; i < activities.size(); i++) {
                    String label = activities.get(i).getLabel();
                    for(int j = i+1; j < activities.size(); j++) {
                        if(activities.get(j).getLabel().equals(label)) {
                            diagram = mergeActivitiesBeforeXOR(diagram, gateway, activities.get(i), activities.get(j));
                            diagram = separateSplitFromJoin(diagram);
                            diagram = removeXORtoAND(diagram);
                            diagram = removeConnectedGateways(diagram);
                            diagram = BPMNSimplifier.removeGatewaysUseless(diagram, diagram.getGateways());
                            repeat = true;
                            continue loops;
                        }
                    }
                }
            }
        }

        return diagram;
    }

    private static BPMNDiagram mergeActivitiesAfterXOR(BPMNDiagram diagram) {
        boolean repeat = true;
        loops:
        while (repeat) {
            repeat = false;
            List<Gateway> gateways = getXORGateways(diagram);
            for(Gateway gateway : gateways) {
                List<Activity> activities = getOutgoingActivities(diagram, gateway);
                for(int i = 0; i < activities.size(); i++) {
                    String label = activities.get(i).getLabel();
                    for(int j = i+1; j < activities.size(); j++) {
                        if(activities.get(j).getLabel().equals(label)) {
                            diagram = mergeActivitiesAfterXOR(diagram, gateway, activities.get(i), activities.get(j));
                            diagram = separateSplitFromJoin(diagram);
                            diagram = removeXORtoAND(diagram);
                            diagram = removeConnectedGateways(diagram);
                            diagram = BPMNSimplifier.removeGatewaysUseless(diagram, diagram.getGateways());
                            repeat = true;
                            continue loops;
                        }
                    }
                }
            }
        }

        return diagram;
    }

    private static BPMNDiagram removeXORtoAND(BPMNDiagram diagram) {
        List<Gateway> XOR = getXORGateways(diagram);
        List<Gateway> AND = getANDGateways(diagram);
        for(Gateway gateway1 : XOR) {
            for(Gateway gateway2 : AND) {
                List<Flow> flows = new ArrayList<>();
                for(Flow flow : diagram.getFlows()) {
                    if(flow.getSource().equals(gateway1) && flow.getTarget().equals(gateway2)) {
                        flows.add(flow);
                    }
                }
                if(flows.size() > 1) {
                    for(int i = 1; i < flows.size(); i++) {
                        diagram.removeEdge(flows.get(i));
                    }
                }
            }
        }

        return diagram;
    }

    private static BPMNDiagram separateSplitFromJoin(BPMNDiagram diagram) {
        Iterator<Gateway> iterator = getXORGateways(diagram).iterator();
        while(iterator.hasNext()) {
            Gateway gateway = iterator.next();
            if(isSplit(diagram, gateway) && isJoin(diagram, gateway)) {
                List<BPMNNode> incoming = getIncomingNodes(diagram, gateway);
                List<BPMNNode> outgoing = getOutgoingNodes(diagram, gateway);

                Gateway split = diagram.addGateway("", Gateway.GatewayType.DATABASED);
                Gateway join = diagram.addGateway("", Gateway.GatewayType.DATABASED);

                for(BPMNNode node : incoming) {
                    diagram.addFlow(node, join, "");
                }
                for(BPMNNode node : outgoing) {
                    diagram.addFlow(split, node, "");
                }
                diagram.addFlow(join, split, "");
                diagram.removeGateway(gateway);
                iterator = getXORGateways(diagram).iterator();
            }
        }

        return diagram;
    }

    private static BPMNDiagram removeConnectedGateways(BPMNDiagram diagram) {
        List<Gateway> XOR = getXORGateways(diagram);
        Iterator<Flow> iterator = diagram.getFlows().iterator();

        while(iterator.hasNext()) {
            Flow flow = iterator.next();
            if(XOR.contains(flow.getSource()) && XOR.contains(flow.getTarget())
                    && isSplit(diagram, flow.getSource()) && isSplit(diagram, flow.getTarget())) {
                if(!flow.getSource().equals(flow.getTarget())) {
                    List<Flow> flows = getOutgoingFlows(diagram, flow.getTarget());
                    for (Flow flow1 : flows) {
                        diagram.addFlow(flow.getSource(), flow1.getTarget(), "");
                    }
                    diagram.removeGateway((Gateway) flow.getTarget());
                    XOR = getXORGateways(diagram);
                }else {
                    diagram.removeEdge(flow);
                }
                iterator = diagram.getFlows().iterator();
            }
        }

        return diagram;
    }

    private static boolean isJoin(BPMNDiagram diagram, BPMNNode node) {
        return getIncomingFlows(diagram, node).size() > 1;
    }

    private static boolean isSplit(BPMNDiagram diagram, BPMNNode node) {
        return getOutgoingFlows(diagram, node).size() > 1;
    }

    private static BPMNDiagram mergeActivitiesBeforeXOR(BPMNDiagram diagram, Gateway gateway, Activity activity1, Activity activity2) {
        List<Flow> incomingFlows1 = getIncomingFlows(diagram, activity1);
        List<Flow> incomingFlows2 = getIncomingFlows(diagram, activity2);
        List<Flow> incomingFlows3 = getIncomingFlows(diagram, gateway);
        List<Flow> outgoingFlows = getOutgoingFlows(diagram, gateway);

        String label = activity1.getLabel();
        Activity activity = diagram.addActivity(label, false, false, false, false, false);

        Set<BPMNNode> incomingNodes1 = new UnifiedSet<>();
        for(Flow flow : incomingFlows1) {
            incomingNodes1.add(flow.getTarget());
        }
        for(Flow flow : incomingFlows2) {
            incomingNodes1.add(flow.getTarget());
        }

        if(incomingNodes1.size() > 1) {
            Gateway gateway1 = diagram.addGateway("", Gateway.GatewayType.DATABASED);

            Set<BPMNNode> incomingNodes = new UnifiedSet<>();
            for (Flow flow : incomingFlows1) {
                if (!incomingNodes.contains(flow.getSource())) {
                    incomingNodes.add(flow.getSource());
                    diagram.addFlow(flow.getSource(), gateway1, "");
                }
            }

            for (Flow flow : incomingFlows2) {
                if (!incomingNodes.contains(flow.getSource())) {
                    incomingNodes.add(flow.getSource());
                    diagram.addFlow(flow.getSource(), gateway1, "");
                }
            }

            diagram.addFlow(gateway1, activity, "");
        }else if(incomingNodes1.size() > 0){
            diagram.addFlow(incomingNodes1.toArray(new BPMNNode[1])[0], activity, "");
        }

        diagram.removeActivity(activity1);
        diagram.removeActivity(activity2);

        if(incomingFlows3.size() == 2) {
            for (Flow flow : outgoingFlows) {
                diagram.addFlow(activity, flow.getTarget(), "");
            }
            diagram.removeGateway(gateway);
        }else {
            diagram.addFlow(activity, gateway, "");
        }
//            if(outgoingNodes.size() == 1) {
//                BPMNNode node = outgoingNodes.toArray(new BPMNNode[1])[0];
//                if(node instanceof Gateway) {
//                    Gateway gateway2 = (Gateway) node;
//                    if(gateway2.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
//                        List<BPMNNode> list = new ArrayList<BPMNNode>();
//                        for(Flow flow: diagram.getFlows()) {
//                            if(flow.getSource().equals(gateway2)) {
//                                list.add(flow.getTarget());
//                            }
//                        }
//                        for(BPMNNode node1 : list) {
//                            diagram.addFlow(activity, node1, "");
//                        }
//                        diagram.removeGateway(gateway1);
//                        diagram.removeGateway(gateway2);
//                    }
//                }
//            }
//        }

        return diagram;
    }

    private static BPMNDiagram mergeActivitiesAfterXOR(BPMNDiagram diagram, Gateway gateway, Activity activity1, Activity activity2) {
        List<Flow> outgoingFlows1 = getOutgoingFlows(diagram, activity1);
        List<Flow> outgoingFlows2 = getOutgoingFlows(diagram, activity2);
        List<Flow> outgoingFlows3 = getOutgoingFlows(diagram, gateway);
        List<Flow> incomingFlows = getIncomingFlows(diagram, gateway);

        String label = activity1.getLabel();
        Activity activity = diagram.addActivity(label, false, false, false, false, false);

        Set<BPMNNode> outgoingNodes1 = new UnifiedSet<>();
        for(Flow flow : outgoingFlows1) {
            outgoingNodes1.add(flow.getTarget());
        }
        for(Flow flow : outgoingFlows2) {
            outgoingNodes1.add(flow.getTarget());
        }

        if(outgoingNodes1.size() > 1) {
            Gateway gateway1 = diagram.addGateway("", Gateway.GatewayType.DATABASED);

            Set<BPMNNode> outgoingNodes = new UnifiedSet<>();
            for (Flow flow : outgoingFlows1) {
                if (!outgoingNodes.contains(flow.getTarget())) {
                    outgoingNodes.add(flow.getTarget());
                    diagram.addFlow(gateway1, flow.getTarget(), "");
                }
            }

            for (Flow flow : outgoingFlows2) {
                if (!outgoingNodes.contains(flow.getTarget())) {
                    outgoingNodes.add(flow.getTarget());
                    diagram.addFlow(gateway1, flow.getTarget(), "");
                }
            }

            diagram.addFlow(activity, gateway1, "");
        }else if(outgoingNodes1.size() > 0){
            diagram.addFlow(activity, outgoingNodes1.toArray(new BPMNNode[1])[0], "");
        }

        diagram.removeActivity(activity1);
        diagram.removeActivity(activity2);

        if(outgoingFlows3.size() == 2) {
            for (Flow flow : incomingFlows) {
                diagram.addFlow(flow.getSource(), activity, "");
            }
            diagram.removeGateway(gateway);
        }else {
            diagram.addFlow(gateway, activity, "");
        }
//            if(outgoingNodes.size() == 1) {
//                BPMNNode node = outgoingNodes.toArray(new BPMNNode[1])[0];
//                if(node instanceof Gateway) {
//                    Gateway gateway2 = (Gateway) node;
//                    if(gateway2.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
//                        List<BPMNNode> list = new ArrayList<BPMNNode>();
//                        for(Flow flow: diagram.getFlows()) {
//                            if(flow.getSource().equals(gateway2)) {
//                                list.add(flow.getTarget());
//                            }
//                        }
//                        for(BPMNNode node1 : list) {
//                            diagram.addFlow(activity, node1, "");
//                        }
//                        diagram.removeGateway(gateway1);
//                        diagram.removeGateway(gateway2);
//                    }
//                }
//            }
//        }

        return diagram;
    }

    private static List<Activity> getIncomingActivities(BPMNDiagram diagram, BPMNNode bpmnNode) {
        List<Activity> flows = new ArrayList<>();
        for(Flow flow : diagram.getFlows()) {
            if(flow.getTarget().equals(bpmnNode) && flow.getSource() instanceof Activity) {
                flows.add((Activity) flow.getSource());
            }
        }
        return flows;
    }

    private static List<BPMNNode> getIncomingNodes(BPMNDiagram diagram, BPMNNode bpmnNode) {
        List<BPMNNode> flows = new ArrayList<>();
        for(Flow flow : diagram.getFlows()) {
            if(flow.getTarget().equals(bpmnNode)) {
                flows.add(flow.getSource());
            }
        }
        return flows;
    }

    private static List<Flow> getIncomingFlows(BPMNDiagram diagram, BPMNNode bpmnNode) {
        List<Flow> flows = new ArrayList<>();
        for(Flow flow : diagram.getFlows()) {
            if(flow.getTarget().equals(bpmnNode)) {
                flows.add(flow);
            }
        }
        return flows;
    }

    private static List<Activity> getOutgoingActivities(BPMNDiagram diagram, BPMNNode bpmnNode) {
        List<Activity> flows = new ArrayList<>();
        for(Flow flow : diagram.getFlows()) {
            if(flow.getSource().equals(bpmnNode) && flow.getTarget() instanceof Activity) {
                flows.add((Activity) flow.getTarget());
            }
        }
        return flows;
    }

    private static List<BPMNNode> getOutgoingNodes(BPMNDiagram diagram, BPMNNode bpmnNode) {
        List<BPMNNode> flows = new ArrayList<>();
        for(Flow flow : diagram.getFlows()) {
            if(flow.getSource().equals(bpmnNode)) {
                flows.add(flow.getTarget());
            }
        }
        return flows;
    }

    private static List<Flow> getOutgoingFlows(BPMNDiagram diagram, BPMNNode bpmnNode) {
        List<Flow> flows = new ArrayList<>();
        for(Flow flow : diagram.getFlows()) {
            if(flow.getSource().equals(bpmnNode)) {
                flows.add(flow);
            }
        }
        return flows;
    }

    private static List<Gateway> getXORGateways(BPMNDiagram diagram) {
        List<Gateway> gateways = new ArrayList<>();
        for(Gateway gateway : diagram.getGateways()) {
            if(gateway.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                gateways.add(gateway);
            }
        }
        return gateways;
    }

    private static List<Gateway> getANDGateways(BPMNDiagram diagram) {
        List<Gateway> gateways = new ArrayList<>();
        for(Gateway gateway : diagram.getGateways()) {
            if(gateway.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                gateways.add(gateway);
            }
        }
        return gateways;
    }
}
