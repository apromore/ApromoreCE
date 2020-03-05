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

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by conforti on 25/11/14.
 */
public class BPMNCleaner {

    private static BPMNAnalizer analizer = new BPMNAnalizer(null);

    public static BPMNDiagram clean(BPMNDiagram bpmn) {

        boolean modified;
        do {
            modified = connectDisconnectedActivities(bpmn);

            if(!modified) {
                modified = connectNoInputActivities(bpmn);
            }

            if(!modified) {
                modified = connectNoOutputActivities(bpmn);
            }

            if(!modified) {
                modified = fixMultipleIncomingActivity(bpmn);
            }

            if(!modified) {
                modified = simplifySplits(bpmn);
            }

            if(!modified) {
                modified = simplifyJoins(bpmn);
            }

//            if(!modified) {
//                modified = removeXORAND(bpmn);
//            }
//
//            if(!modified) {
//                modified = removeANDXOR(bpmn);
//            }
//
//            if(!modified) {
//                modified = removeANDActivityXORLoop(bpmn);
//            }

//            if(!modified) {
//                modified = removeXORActivityANDLoop(bpmn);
//            }
//
//            if(!modified) {
//                modified = removeXORActivityXORXORLoop(bpmn);
//            }
//
//            if(!modified) {
//                modified = removeXORActivityANDXORLoop(bpmn);
//            }
//
//            if(!modified) {
//                modified = removeXORANDActivityXORLoop(bpmn);
//            }
//
//            if(!modified) {
//                modified = removeANDActivityANDXOREscape(bpmn);
//            }
//
//            if(!modified) {
//                modified = removeXORXORActivityAND(bpmn);
//            }

            if(!modified) {
                modified = removeDuplicatedArcs(bpmn);
            }

//            if(!modified) {
//                modified = fixLongANDXOR(bpmn);
//            }
//
//            if(!modified) {
//                modified = fixLongXORAND(bpmn);
//            }

            BPMNSimplifier.simplify(bpmn);
        }while (modified);

        return bpmn;
    }

    private static boolean simplifySplits(BPMNDiagram bpmn) {
        for(Gateway gateway1 : bpmn.getGateways()) {
            if(isSplit(bpmn, gateway1)) {
                for(Flow gateway1_gateway2 : bpmn.getFlows()) {
                    if(gateway1_gateway2.getSource().equals(gateway1) && gateway1_gateway2.getTarget() instanceof Gateway) {
                        Gateway gateway2 = (Gateway) gateway1_gateway2.getTarget();
                        if (gateway2.getGatewayType().equals(gateway1.getGatewayType()) && isSplit(bpmn, gateway2)) {
                            List<BPMNNode> targets = new ArrayList<BPMNNode>();
                            for(Flow gateway2_x : bpmn.getFlows()) {
                                if(gateway2_x.getSource().equals(gateway2) && !gateway2_x.getTarget().equals(gateway2)) {
                                    targets.add(gateway2_x.getTarget());
                                }
                            }
                            bpmn.removeGateway(gateway2);
                            for(BPMNNode target : targets) {
                                bpmn.addFlow(gateway1, target, "");
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean simplifyJoins(BPMNDiagram bpmn) {
        for(Gateway gateway1 : bpmn.getGateways()) {
            if(isJoin(bpmn, gateway1)) {
                for(Flow gateway1_gateway2 : bpmn.getFlows()) {
                    if(gateway1_gateway2.getSource().equals(gateway1) && gateway1_gateway2.getTarget() instanceof Gateway) {
                        Gateway gateway2 = (Gateway) gateway1_gateway2.getTarget();
                        if (gateway2.getGatewayType().equals(gateway1.getGatewayType()) && isJoin(bpmn, gateway2)) {
                            List<BPMNNode> sources = new ArrayList<BPMNNode>();
                            for(Flow x_gateway1 : bpmn.getFlows()) {
                                if(x_gateway1.getTarget().equals(gateway1) && !x_gateway1.getSource().equals(gateway1)) {
                                    sources.add(x_gateway1.getSource());
                                }
                            }
                            bpmn.removeGateway(gateway1);
                            for(BPMNNode source : sources) {
                                bpmn.addFlow(source, gateway2, "");
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean fixLongANDXOR(BPMNDiagram bpmn) {
        for(Gateway and : bpmn.getGateways()) {
            if(and.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                int output = 0;
                for(Flow flow : bpmn.getFlows()) {
                    if(flow.getSource().equals(and)) {
                        output++;
                    }
                }
                if(output > 1) {
                    for (Gateway xor : bpmn.getGateways()) {
                        if (xor.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                            int input = 0;
                            for(Flow flow : bpmn.getFlows()) {
                                if(flow.getTarget().equals(xor)) {
                                    input++;
                                }
                            }
                            if(output == input) {
                                List<Flow> allPath = new ArrayList<Flow>();
                                List<Gateway> allowed = new ArrayList<Gateway>(2);
                                allowed.add(and);
                                allowed.add(xor);
                                boolean ok = true;

                                for(int i = 0; i < input; i++) {
                                    List<Flow> path = analizer.discoverPath(bpmn, and, xor, allPath);
                                    if(path == null || path.size() == 0 || containGateways(path, allowed)) {
                                        ok = false;
                                    }else {
                                        allPath.addAll(path);
                                    }
                                }

                                if(ok) {
                                    xor.setGatewayType(Gateway.GatewayType.PARALLEL);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean fixLongXORAND(BPMNDiagram bpmn) {
        for(Gateway xor : bpmn.getGateways()) {
            if(xor.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                int output = 0;
                for(Flow flow : bpmn.getFlows()) {
                    if(flow.getSource().equals(xor)) {
                        output++;
                    }
                }
                if(output > 1) {
                    for (Gateway and : bpmn.getGateways()) {
                        if (and.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                            int input = 0;
                            for(Flow flow : bpmn.getFlows()) {
                                if(flow.getTarget().equals(and)) {
                                    input++;
                                }
                            }
                            if(output == input) {
                                if(output == input) {
                                    List<Flow> allPath = new ArrayList<Flow>();
                                    List<Gateway> allowed = new ArrayList<Gateway>(2);
                                    allowed.add(and);
                                    allowed.add(xor);
                                    boolean ok = true;

                                    for(int i = 0; i < input; i++) {
                                        List<Flow> path = analizer.discoverPath(bpmn, xor, and, allPath);
                                        if(path == null || path.size() == 0 || containGateways(path, allowed)) {
                                            ok = false;
                                        }else {
                                            allPath.addAll(path);
                                        }
                                    }

                                    if(ok) {
                                        and.setGatewayType(Gateway.GatewayType.DATABASED);
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean connectNoInputActivities(BPMNDiagram bpmn) {
        List<Activity> activities = new ArrayList<Activity>();
        for (Activity activity : bpmn.getActivities()) {
            boolean connected = false;
            for (Flow flow : bpmn.getFlows()) {
                if (flow.getTarget().equals(activity)) {
                    connected = true;
                    break;
                }
            }
            if (!connected) activities.add(activity);
        }

        if(activities.size() > 0) {
            List<Gateway> xorSplits = new ArrayList<Gateway>();
            for (Activity activity : activities) {
                Gateway xorSplit = bpmn.addGateway("XOR", Gateway.GatewayType.DATABASED);
                xorSplits.add(xorSplit);

                Gateway xorJoin = bpmn.addGateway("XOR", Gateway.GatewayType.DATABASED);

                Flow f = null;
                for (Flow flow : bpmn.getFlows()) {
                    if (flow.getSource().equals(activity)) {
                        f = flow;
                        break;
                    }
                }

                bpmn.addFlow(xorSplit, activity, "");
                bpmn.addFlow(activity, xorJoin, "");
                bpmn.addFlow(xorSplit, xorJoin, "");
                bpmn.addFlow(xorJoin, f.getTarget(), "");
                bpmn.removeEdge(f);
                activity.setBLooped(true);
            }

            Gateway andSplit = bpmn.addGateway("AND", Gateway.GatewayType.PARALLEL);

            for (Gateway xorSplit : xorSplits) {
                bpmn.addFlow(andSplit, xorSplit, "");
            }

            Event start = null;
            for (Event event : bpmn.getEvents()) {
                if (event.getEventType().equals(Event.EventType.START)) {
                    start = event;
                }
            }

            Flow toRemove1 = null;
            for (Flow flow : bpmn.getFlows()) {
                if (flow.getSource().equals(start)) {
                    toRemove1 = flow;
                }
            }

            bpmn.addFlow(andSplit, toRemove1.getTarget(), "");
            bpmn.addFlow(start, andSplit, "");

            bpmn.removeEdge(toRemove1);

            return true;
        }
        return false;
    }

    private static boolean connectNoOutputActivities(BPMNDiagram bpmn) {
        List<Activity> activities = new ArrayList<Activity>();
        for (Activity activity : bpmn.getActivities()) {
            boolean connected = false;
            for (Flow flow : bpmn.getFlows()) {
                if (flow.getSource().equals(activity)) {
                    connected = true;
                    break;
                }
            }
            if (!connected) activities.add(activity);
        }

        if(activities.size() > 0) {
            List<Gateway> xorJoins = new ArrayList<Gateway>();
            for (Activity activity : activities) {
                Gateway xorSplit = bpmn.addGateway("XOR", Gateway.GatewayType.DATABASED);
                Gateway xorJoin = bpmn.addGateway("XOR", Gateway.GatewayType.DATABASED);
                xorJoins.add(xorJoin);

                Flow f = null;
                for (Flow flow : bpmn.getFlows()) {
                    if (flow.getTarget().equals(activity)) {
                        f = flow;
                        break;
                    }
                }

                bpmn.addFlow(xorSplit, activity, "");
                bpmn.addFlow(activity, xorJoin, "");
                bpmn.addFlow(xorSplit, xorJoin, "");
                bpmn.addFlow(f.getSource(), xorSplit, "");
                bpmn.removeEdge(f);
                activity.setBLooped(true);
            }

            Gateway andJoin = bpmn.addGateway("AND", Gateway.GatewayType.PARALLEL);

            for (Gateway xorJoin : xorJoins) {
                bpmn.addFlow(xorJoin, andJoin, "");
            }

            Event end = null;
            for (Event event : bpmn.getEvents()) {
                if (event.getEventType().equals(Event.EventType.END)) {
                    end = event;
                }
            }

            Flow toRemove2 = null;
            for (Flow flow : bpmn.getFlows()) {
                if (flow.getTarget().equals(end)) {
                    toRemove2 = flow;
                }
            }

            bpmn.addFlow(toRemove2.getSource(), andJoin, "");
            bpmn.addFlow(andJoin, end, "");

            bpmn.removeEdge(toRemove2);

            return true;
        }
        return false;
    }

    private static boolean connectDisconnectedActivities(BPMNDiagram bpmn) {
        List<Activity> activities = new ArrayList<Activity>();
        for (Activity activity : bpmn.getActivities()) {
            boolean connected = false;
            for (Flow flow : bpmn.getFlows()) {
                if (flow.getTarget().equals(activity) || flow.getSource().equals(activity)) {
                    connected = true;
                    break;
                }
            }
            if (!connected) activities.add(activity);
        }

        if(activities.size() > 0) {
            List<Gateway> xorSplits = new ArrayList<Gateway>();
            List<Gateway> xorJoins = new ArrayList<Gateway>();
            for (Activity activity : activities) {
                System.out.println("Connecting");
                Gateway xorSplit = bpmn.addGateway("XOR", Gateway.GatewayType.DATABASED);
                xorSplits.add(xorSplit);

                Gateway xorJoin = bpmn.addGateway("XOR", Gateway.GatewayType.DATABASED);
                xorJoins.add(xorJoin);

                bpmn.addFlow(xorSplit, activity, "");
                bpmn.addFlow(activity, xorJoin, "");
                bpmn.addFlow(xorSplit, xorJoin, "");
                activity.setBLooped(true);
            }

            Gateway andSplit = bpmn.addGateway("AND", Gateway.GatewayType.PARALLEL);
            Gateway andJoin = bpmn.addGateway("AND", Gateway.GatewayType.PARALLEL);

            for (Gateway xorSplit : xorSplits) {
                bpmn.addFlow(andSplit, xorSplit, "");
            }
            for (Gateway xorJoin : xorJoins) {
                bpmn.addFlow(xorJoin, andJoin, "");
            }

            Event start = null;
            Event end = null;
            for (Event event : bpmn.getEvents()) {
                if (event.getEventType().equals(Event.EventType.START)) {
                    start = event;
                } else if (event.getEventType().equals(Event.EventType.END)) {
                    end = event;
                }
            }

            Flow toRemove1 = null;
            Flow toRemove2 = null;
            for (Flow flow : bpmn.getFlows()) {
                if (flow.getSource().equals(start)) {
                    toRemove1 = flow;
                }
                if (flow.getTarget().equals(end)) {
                    toRemove2 = flow;
                }
            }

            bpmn.addFlow(andSplit, toRemove1.getTarget(), "");
            bpmn.addFlow(toRemove2.getSource(), andJoin, "");
            bpmn.addFlow(start, andSplit, "");
            bpmn.addFlow(andJoin, end, "");

            bpmn.removeEdge(toRemove1);
            bpmn.removeEdge(toRemove2);

            return true;
        }
        return false;
    }

    public static boolean removeDuplicatedArcs(BPMNDiagram bpmn) {
        boolean changed = false;
        Iterator<Gateway> iterator = bpmn.getGateways().iterator();
        while (iterator.hasNext()) {
            Gateway g = iterator.next();
            Flow remove = null;
            for(Flow f1 : bpmn.getFlows()) {
                if(f1.getSource().equals(g)) {
                    for(Flow f2 : bpmn.getFlows()) {
                        if(f1 != f2 && f1.getSource().equals(f2.getSource()) && f1.getTarget().equals(f2.getTarget())) {
                            remove = f2;
                            break;
                        }
                    }
                    if(remove != null) {
                        break;
                    }
                }
            }
            if(remove != null) {
                bpmn.removeEdge(remove);
                int outcome = 0;
                for (Flow f : bpmn.getFlows()) {
                    if (f.getSource().equals(g)) {
                        outcome++;
                    }
                }
                if (outcome == 1 && !g.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                    g.setGatewayType(Gateway.GatewayType.DATABASED);
                    changed = true;
                }
                iterator = bpmn.getGateways().iterator();
            }
        }
        return changed;
    }

    /**
     * Fix problem when an AND Split follows activity and the AND if followed by and XOR Split which loops back into the activity
     * @param bpmn
     * @return
     */
    public static boolean removeXORActivityANDLoop(BPMNDiagram bpmn) {
        for (Activity a : bpmn.getActivities()) {
            for (Flow a_and : bpmn.getFlows()) {
                if (a_and.getSource().equals(a) && a_and.getTarget() instanceof Gateway) {
                    Gateway and = (Gateway) a_and.getTarget();
                    if (and.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                        for (Flow and_xor : bpmn.getFlows()) {
                            if (and_xor.getSource().equals(and) && and_xor.getTarget() instanceof Gateway) {
                                Gateway xor = (Gateway) and_xor.getTarget();
                                if (xor.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                                    for (Flow xor_a : bpmn.getFlows()) {
                                        if (xor_a.getSource().equals(xor) && xor_a.getTarget().equals(a)) {
                                            and.setGatewayType(Gateway.GatewayType.DATABASED);
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Fix problem when an AND Split follows activity and the AND if followed by and XOR Split which loops back into the activity
     * @param bpmn
     * @return
     */
    public static boolean removeANDActivityXORLoop(BPMNDiagram bpmn) {
        for (Activity a : bpmn.getActivities()) {
            for (Flow a_xor : bpmn.getFlows()) {
                if (a_xor.getSource().equals(a) && a_xor.getTarget() instanceof Gateway) {
                    Gateway xor = (Gateway) a_xor.getTarget();
                    if (xor.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                        for (Flow xor_and : bpmn.getFlows()) {
                            if (xor_and.getSource().equals(xor) && xor_and.getTarget() instanceof Gateway) {
                                Gateway and = (Gateway) xor_and.getTarget();
                                if (and.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                                    for (Flow and_a : bpmn.getFlows()) {
                                        if (and_a.getSource().equals(and) && and_a.getTarget().equals(a)) {
                                            xor.setGatewayType(Gateway.GatewayType.DATABASED);
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Fix problem when an AND Split follows activity and the AND if followed by and XOR Split which loops back into the activity
     * @param bpmn
     * @return
     */
    public static boolean removeXORActivityXORXORLoop(BPMNDiagram bpmn) {
        for (Activity a : bpmn.getActivities()) {
            for (Flow a_xor1 : bpmn.getFlows()) {
                if (a_xor1.getSource().equals(a) && a_xor1.getTarget() instanceof Gateway) {
                    Gateway xor1 = (Gateway) a_xor1.getTarget();
                    if (xor1.getGatewayType().equals(Gateway.GatewayType.DATABASED) && isSplit(bpmn, xor1)) {
                        for (Flow xor1_xor2 : bpmn.getFlows()) {
                            if (xor1_xor2.getSource().equals(xor1) && xor1_xor2.getTarget() instanceof Gateway) {
                                Gateway xor2 = (Gateway) xor1_xor2.getTarget();
                                if (xor2.getGatewayType().equals(Gateway.GatewayType.DATABASED) && isJoin(bpmn, xor2)) {
                                    for (Flow xor2_xor3 : bpmn.getFlows()) {
                                        if (xor2_xor3.getSource().equals(xor2) && xor2_xor3.getTarget() instanceof Gateway) {
                                            Gateway xor3 = (Gateway) xor2_xor3.getTarget();
                                            if (xor3.getGatewayType().equals(Gateway.GatewayType.DATABASED) && isJoin(bpmn, xor3)) {
                                                for (Flow xor3_a : bpmn.getFlows()) {
                                                    if (xor3_a.getSource().equals(xor3) && xor3_a.getTarget().equals(a)) {
                                                        List<BPMNNode> inputs = new ArrayList<BPMNNode>();
                                                        List<Flow> toRemove = new ArrayList<Flow>();
                                                        for(Flow x_xor2 : bpmn.getFlows()) {
                                                            if(x_xor2.getTarget().equals(xor2) && !x_xor2.getSource().equals(xor1)) {
                                                                inputs.add(x_xor2.getSource());
                                                                toRemove.add(x_xor2);
                                                            }
                                                        }
                                                        for(Flow flow : toRemove) {
                                                            bpmn.removeEdge(flow);
                                                        }
                                                        for(BPMNNode input : inputs) {
                                                            bpmn.addFlow(input, xor3, "");
                                                        }
                                                        return true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Fix problem when an AND Split follows activity and the AND if followed by and XOR Split which loops back into the activity
     * @param bpmn
     * @return
     */
    public static boolean removeXORActivityANDXORLoop(BPMNDiagram bpmn) {
        for (Activity a : bpmn.getActivities()) {
            for (Flow a_and : bpmn.getFlows()) {
                if (a_and.getSource().equals(a) && a_and.getTarget() instanceof Gateway) {
                    Gateway and = (Gateway) a_and.getTarget();
                    if (and.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                        for (Flow and_xor1 : bpmn.getFlows()) {
                            if (and_xor1.getSource().equals(and) && and_xor1.getTarget() instanceof Gateway) {
                                Gateway xor1 = (Gateway) and_xor1.getTarget();
                                if (xor1.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                                    for (Flow xor1_xor2 : bpmn.getFlows()) {
                                        if (xor1_xor2.getSource().equals(xor1) && xor1_xor2.getTarget() instanceof Gateway) {
                                            Gateway xor2 = (Gateway) xor1_xor2.getTarget();
                                            if (xor2.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                                                for (Flow xor2_a : bpmn.getFlows()) {
                                                    if (xor2_a.getSource().equals(xor2) && xor2_a.getTarget().equals(a)) {
                                                        and.setGatewayType(Gateway.GatewayType.DATABASED);
                                                        return true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Fix problem when an activity follows an AND Join and the AND follows XOR Join which loops back from the activity
     * @param bpmn
     * @return
     */
    public static boolean removeXORANDActivityXORLoop(BPMNDiagram bpmn) {
        for (Activity a : bpmn.getActivities()) {
            for (Flow and_a : bpmn.getFlows()) {
                if (and_a.getTarget().equals(a) && and_a.getSource() instanceof Gateway) {
                    Gateway and = (Gateway) and_a.getSource();
                    if (and.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                        for (Flow xor1_and : bpmn.getFlows()) {
                            if (xor1_and.getTarget().equals(and) && xor1_and.getSource() instanceof Gateway) {
                                Gateway xor1 = (Gateway) xor1_and.getSource();
                                if (xor1.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                                    for (Flow xor2_xor1 : bpmn.getFlows()) {
                                        if (xor2_xor1.getTarget().equals(xor1) && xor2_xor1.getSource() instanceof Gateway) {
                                            Gateway xor2 = (Gateway) xor2_xor1.getSource();
                                            if (xor2.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                                                for (Flow a_xor2 : bpmn.getFlows()) {
                                                    if (a_xor2.getTarget().equals(xor2) && a_xor2.getSource().equals(a)) {
                                                        and.setGatewayType(Gateway.GatewayType.DATABASED);
                                                        return true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Fix problem when and AND Split is followed by an Activity and the Activity by an AND Join, but one of the branches of the AND Split lead to an XOR
     * @param bpmn
     * @return
     */
    public static boolean removeANDActivityANDXOREscape(BPMNDiagram bpmn) {
        for (Gateway and1 : bpmn.getGateways()) {
            if (and1.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                for (Gateway and2 : bpmn.getGateways()) {
                    if (and2.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                        List<Flow> pathAND1_AND2 = analizer.discoverPath(bpmn, and1, and2, new ArrayList<Flow>());
                        if (pathAND1_AND2 != null && pathAND1_AND2.size() > 0) {
                            for (Gateway xor : bpmn.getGateways()) {
                                if (xor.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                                    List<Flow> pathAND1_XOR = analizer.discoverPath(bpmn, and1, xor, new ArrayList<Flow>());
                                    if (pathAND1_XOR != null && pathAND1_XOR.size() > 0) {
                                        List<Flow> pathXOR_AND2 = analizer.discoverPath(bpmn, xor, and2, new ArrayList<Flow>());
                                        if (pathXOR_AND2 != null && pathXOR_AND2.size() == 1) {
                                            List<Flow> excludedPath = new ArrayList<Flow>(pathAND1_XOR.size() + pathXOR_AND2.size());
                                            excludedPath.addAll(pathAND1_XOR);
                                            excludedPath.addAll(pathXOR_AND2);
                                            List<Flow> alternativePathAND1_AND2 = analizer.discoverPath(bpmn, and1, and2, excludedPath);
                                            if (alternativePathAND1_AND2 != null && alternativePathAND1_AND2.size() > 0) {
                                                and1.setGatewayType(Gateway.GatewayType.DATABASED);
                                                and2.setGatewayType(Gateway.GatewayType.DATABASED);
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private static boolean containGateways(List<Flow> flows, List<Gateway> allowed) {
        for(Flow flow : flows) {
            if((flow.getSource() instanceof Gateway && !allowed.contains(flow.getSource())) || (flow.getTarget() instanceof Gateway && !allowed.contains(flow.getTarget()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Fix problem when all branches of an XOR Split lead to an AND Join
     * @param bpmn
     * @return
     */
    public static boolean removeXORAND(BPMNDiagram bpmn) {
        for (Flow xor_and : bpmn.getFlows()) {
            if (xor_and.getSource() instanceof  Gateway && xor_and.getTarget() instanceof Gateway) {
                Gateway xor = (Gateway) xor_and.getSource();
                Gateway and = (Gateway) xor_and.getTarget();
                if (xor.getGatewayType().equals(Gateway.GatewayType.DATABASED) && and.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                    List<Flow> excludedFlow = new ArrayList<Flow>();
                    excludedFlow.add(xor_and);
                    List<Flow> discoveredFlow = analizer.discoverPath(bpmn, xor, and, excludedFlow);
                    if(discoveredFlow != null && discoveredFlow.size() > 0) {
                        bpmn.removeEdge(xor_and);
                        and.setGatewayType(Gateway.GatewayType.DATABASED);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Fix problem when an XOR Split is followed by an Activity on one branch and by and XOR on another branch and both lead to an AND Join
     * @param bpmn
     * @return
     */
    public static boolean removeXORXORActivityAND(BPMNDiagram bpmn) {
        for (Activity a : bpmn.getActivities()) {
            for (Flow a_and : bpmn.getFlows()) {
                if (a_and.getSource().equals(a) && a_and.getTarget() instanceof Gateway) {
                    Gateway and = (Gateway) a_and.getTarget();
                    if (and.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                        for (Flow xor1_a : bpmn.getFlows()) {
                            if (xor1_a.getTarget().equals(a) && xor1_a.getSource() instanceof Gateway) {
                                Gateway xor1 = (Gateway) xor1_a.getSource();
                                if (xor1.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                                    for (Flow xor1_xor2 : bpmn.getFlows()) {
                                        if (xor1_xor2.getSource().equals(xor1) && xor1_xor2.getTarget() instanceof Gateway) {
                                            Gateway xor2 = (Gateway) xor1_xor2.getTarget();
                                            if (xor2.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                                                for (Flow xor2_and : bpmn.getFlows()) {
                                                    if (xor2_and.getSource().equals(xor2) && xor2_and.getTarget().equals(and)) {
                                                        int incoming = 0;
                                                        for(Flow x_xor2 : bpmn.getFlows()) {
                                                            if(x_xor2.getTarget().equals(xor2)) {
                                                                incoming++;
                                                            }
                                                        }
                                                        if(incoming > 1) {
                                                            bpmn.removeEdge(xor1_xor2);
                                                            return true;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean removeANDXOR(BPMNDiagram bpmn) {
        for (Flow xor_and : bpmn.getFlows()) {
            if (xor_and.getSource() instanceof Gateway && xor_and.getTarget() instanceof Gateway) {
                Gateway and = (Gateway) xor_and.getSource();
                Gateway xor = (Gateway) xor_and.getTarget();
                if (and.getGatewayType().equals(Gateway.GatewayType.PARALLEL) && xor.getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                    int g1Split = 0;
                    int g2Join = 0;
                    for(Flow f2 : bpmn.getFlows()) {
                        if(f2.getTarget().equals(xor)) {
                            g2Join++;
                        }
                        if(f2.getSource().equals(and)) {
                            g1Split++;
                        }
                    }
                    if(g1Split > 1 && g2Join > 1) {
                        List<Flow> excludedFlow = new ArrayList<Flow>();
                        excludedFlow.add(xor_and);
                        List<Flow> discoveredFlow = analizer.discoverPath(bpmn, xor, and, excludedFlow);
                        if (discoveredFlow != null && discoveredFlow.size() > 0) {
                            bpmn.removeEdge(xor_and);
                            and.setGatewayType(Gateway.GatewayType.DATABASED);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean fixMultipleIncomingActivity(BPMNDiagram bpmn) {
        for(Activity a : bpmn.getActivities()) {
            List<Flow> flows = new ArrayList<Flow>();
            for(Flow f : bpmn.getFlows()) {
                if(f.getTarget().equals(a)) {
                    flows.add(f);
                }
            }
            if(flows.size() > 1) {
                Gateway XOR = bpmn.addGateway("XOR_" + a.getLabel(), Gateway.GatewayType.DATABASED);
                for(Flow f : flows) {
                    bpmn.addFlow(f.getSource(), XOR, "");
                }
                bpmn.addFlow(XOR, a, "");
                for(Flow f : flows) {
                    bpmn.removeEdge(f);
                }
                return true;
            }
        }
        return false;
    }

    private static boolean isSplit(BPMNDiagram bpmn, Gateway gateway) {
        int output = 0;
        for(Flow flow : bpmn.getFlows()) {
            if(flow.getSource().equals(gateway)) {
                output++;
            }
        }
        return output > 1;
    }

    private static boolean isJoin(BPMNDiagram bpmn, Gateway gateway) {
        int input = 0;
        for(Flow flow : bpmn.getFlows()) {
            if(flow.getTarget().equals(gateway)) {
                input++;
            }
        }
        return input > 1;
    }
}

