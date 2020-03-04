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

package com.raffaeleconforti.conversion.petrinet;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramFactory;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.models.semantics.petrinet.Marking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Raffaele Conforti on 19/02/14.
 */

public class PetriNetToBPMNConverter {

    private static Map<Transition, Activity> transitionActivityUnifiedMap = null;
    private static Map<Place, Gateway> placeXORSplitGatewayUnifiedMap = null;
    private static Map<Place, Gateway> placeXORJoinGatewayUnifiedMap = null;
    private static Map<Transition, Gateway> transitionANDSplitGatewayUnifiedMap = null;
    private static Map<Transition, Gateway> transitionANDJoinGatewayUnifiedMap = null;
    private static Map<BPMNNode, Set<BPMNNode>> connected = null;

    /**
     * Convert the given Petri net into an EPC.
     *
     * @param net1 PetriNet The given Petri net.
     * @return EPC The constructed EPC.
     */
    public static BPMNDiagram convert(Petrinet net1, Marking initialMarking1, Marking finalMarking1, boolean clean) {
        Object[] init = initializeCloneOfNet(net1, initialMarking1, finalMarking1);

        Petrinet net = (Petrinet) init[0];
        Marking initialMarking = (Marking) init[1];
        Marking finalMarking = (Marking) init[2];

        transitionActivityUnifiedMap = new UnifiedMap<Transition, Activity>();
        placeXORSplitGatewayUnifiedMap = new UnifiedMap<Place, Gateway>();
        placeXORJoinGatewayUnifiedMap = new UnifiedMap<Place, Gateway>();
        transitionANDSplitGatewayUnifiedMap = new UnifiedMap<Transition, Gateway>();
        transitionANDJoinGatewayUnifiedMap = new UnifiedMap<Transition, Gateway>();
        connected = new UnifiedMap<BPMNNode, Set<BPMNNode>>();

        net = isolateTransitions(net);
        net = insertTransitionsToClarifySplits(net);
        net = insertPlacesToClarifySplits(net);
        net = insertPlacesAndTransitionsToClarifySplits(net);

        if(initialMarking == null) initialMarking = guessInitialMarking(net);
        if(finalMarking == null) finalMarking = guessFinalMarking(net);

        if(initialMarking != null && initialMarking.size() > 0) {
            Place start = net.addPlace("REAL_START");
            Transition t1 = net.addTransition("INVISIBLE");
            t1.setInvisible(true);
            net.addArc(start, t1);
            net.addArc(t1, (Place) initialMarking.toArray()[0]);
            initialMarking.remove(initialMarking.toArray()[0]);
            initialMarking.add(start);
        }

        if(finalMarking == null || finalMarking.size() == 0) {
            if(finalMarking == null) finalMarking = new Marking();
            for(Transition t : net.getTransitions()) {
                if(t.getLabel().contains("ArtificialEndEvent")) {
                    Place end = net.addPlace("REAL_END");
                    net.addArc(t, end);
                    finalMarking.add(end);
                    break;
                }
            }
        }

        if(finalMarking != null && finalMarking.size() > 0) {
            Place end = net.addPlace("REAL_END");
            Transition t2 = net.addTransition("INVISIBLE");
            t2.setInvisible(true);
            net.addArc((Place) finalMarking.toArray()[0], t2);
            net.addArc(t2, end);
            finalMarking.remove(finalMarking.toArray()[0]);
            finalMarking.add(end);
        }

        BPMNDiagram diagram = BPMNDiagramFactory.newBPMNDiagram("");

        for (Transition transition : net.getTransitions()) {
            if (!transition.isInvisible()) {
                transitionActivityUnifiedMap.put(transition, diagram.addActivity(transition.getLabel(), false, false, false, false, false));
            }else {
                transitionActivityUnifiedMap.put(transition, diagram.addActivity("INVISIBLE", false, false, false, false, false));
            }
        }

        initializeANDSplitGateways(net, diagram);
        initializeANDJoinGateways(net, diagram);

        createStartEvent(net, initialMarking, diagram);
        createEndEvent(net, finalMarking, diagram);

        createXORSplitGateways(net, diagram);
        createXORJoinGateways(net, diagram);

        finalizeANDSplitGateways(net, diagram);
        finalizeANDJoinGateways(net, diagram);

        connectSingleOuputTransitions(net, diagram);
        removeInvisibleActivities(diagram);

        return diagram;
    }

    private static Object[] initializeCloneOfNet(Petrinet net1, Marking initialMarking1, Marking finalMarking1) {
        Petrinet net = new PetrinetImpl("");
        Map<Place, Place> places = new UnifiedMap<Place, Place>();
        Map<Transition, Transition> transitions = new UnifiedMap<Transition, Transition>();

        for(Place place : net1.getPlaces()) {
            Place p = net.addPlace(place.getLabel());
            places.put(place, p);
        }

        for(Transition transition : net1.getTransitions()) {
            Transition t = net.addTransition(transition.getLabel());
            t.setInvisible(transition.isInvisible());
            transitions.put(transition, t);
        }

        for(PetrinetEdge edge: net1.getEdges()) {
            if(edge.getSource() instanceof Place) {
                net.addArc(places.get(edge.getSource()), transitions.get(edge.getTarget()));
            }else {
                net.addArc(transitions.get(edge.getSource()), places.get(edge.getTarget()));
            }
        }

        Marking initialMarking = null;
        Marking finalMarking = null;
        Marking m = new Marking();
        if(initialMarking1 != null) {
            for (Place p : initialMarking1) {
                m.add(places.get(p));
            }
            initialMarking = m;
        }

        if(finalMarking1 != null) {
            m = new Marking();
            for (Place p : finalMarking1) {
                m.add(places.get(p));
            }
            finalMarking = m;
        }
        return new Object[] {net, initialMarking, finalMarking};
    }

    public static Petrinet convert1(Petrinet net1, Marking initialMarking, Marking finalMarking, boolean clean) {
        Petrinet net = new PetrinetImpl("");
        Map<Place, Place> places = new UnifiedMap<Place, Place>();
        Map<Transition, Transition> transitions = new UnifiedMap<Transition, Transition>();

        for(Place place : net1.getPlaces()) {
            Place p = net.addPlace(place.getLabel());
            places.put(place, p);
        }
        for(Transition transition : net1.getTransitions()) {
            Transition t = net.addTransition(transition.getLabel());
            transitions.put(transition, t);
        }
        for(PetrinetEdge edge: net1.getEdges()) {
            if(edge.getSource() instanceof Place) {
                net.addArc(places.get(edge.getSource()), transitions.get(edge.getTarget()));
            }else {
                net.addArc(transitions.get(edge.getSource()), places.get(edge.getTarget()));
            }
        }
        Marking m = new Marking();
        if(initialMarking != null) {
            for (Place p : initialMarking) {
                m.add(places.get(p));
            }
            initialMarking = m;
        }

        if(finalMarking != null) {
            m = new Marking();
            for (Place p : finalMarking) {
                m.add(places.get(p));
            }
            finalMarking = m;
        }

        transitionActivityUnifiedMap = new UnifiedMap<Transition, Activity>();
        placeXORSplitGatewayUnifiedMap = new UnifiedMap<Place, Gateway>();
        placeXORJoinGatewayUnifiedMap = new UnifiedMap<Place, Gateway>();
        transitionANDSplitGatewayUnifiedMap = new UnifiedMap<Transition, Gateway>();
        transitionANDJoinGatewayUnifiedMap = new UnifiedMap<Transition, Gateway>();
        connected = new UnifiedMap<BPMNNode, Set<BPMNNode>>();

        net = isolateTransitions(net);
        net = insertTransitionsToClarifySplits(net);
        net = insertPlacesToClarifySplits(net);
        net = insertPlacesAndTransitionsToClarifySplits(net);

        if(initialMarking == null) initialMarking = guessInitialMarking(net);
        if(finalMarking == null) finalMarking = guessFinalMarking(net);

        if(initialMarking != null && initialMarking.size() > 0) {
            Place start = net.addPlace("REAL_START");
            Transition t1 = net.addTransition("INVISIBLE");
            t1.setInvisible(true);
            net.addArc(start, t1);
            net.addArc(t1, (Place) initialMarking.toArray()[0]);
            initialMarking.remove(initialMarking.toArray()[0]);
            initialMarking.add(start);
        }

        if(finalMarking != null && finalMarking.size() > 0) {
            Place end = net.addPlace("REAL_END");
            Transition t2 = net.addTransition("INVISIBLE");
            t2.setInvisible(true);
            net.addArc((Place) finalMarking.toArray()[0], t2);
            net.addArc(t2, end);
            finalMarking.remove(finalMarking.toArray()[0]);
            finalMarking.add(end);
        }

        BPMNDiagram diagram = BPMNDiagramFactory.newBPMNDiagram("");

        for (Transition transition : net.getTransitions()) {
            if (!transition.isInvisible()) {
                transitionActivityUnifiedMap.put(transition, diagram.addActivity(transition.getLabel(), false, false, false, false, false));
            }else {
                transitionActivityUnifiedMap.put(transition, diagram.addActivity("INVISIBLE", false, false, false, false, false));
            }
        }

        initializeANDSplitGateways(net, diagram);
        initializeANDJoinGateways(net, diagram);

        createStartEvent(net, initialMarking, diagram);
        createEndEvent(net, finalMarking, diagram);

        createXORSplitGateways(net, diagram);
        createXORJoinGateways(net, diagram);

        finalizeANDSplitGateways(net, diagram);
        finalizeANDJoinGateways(net, diagram);

        connectSingleOuputTransitions(net, diagram);
        removeInvisibleActivities(diagram);

//        BPMNCleaner.clean(diagram);
        return net;
    }

    public static Marking guessInitialMarking(Petrinet net) {
        List<Place> place = new ArrayList<Place>();
        for(Place p : net.getPlaces()) {
            boolean found = false;
            for(PetrinetEdge edge : net.getEdges()) {
                if(edge.getTarget().equals(p)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                place.add(p);
                return new Marking(place);
            }
        }
        return null;
    }

    public static Marking guessFinalMarking(Petrinet net) {
        List<Place> place = new ArrayList<Place>();
        for(Place p : net.getPlaces()) {
            boolean found = false;
            for(PetrinetEdge edge : net.getEdges()) {
                if(edge.getSource().equals(p)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                place.add(p);
                return new Marking(place);
            }
        }
        return null;
    }

    private static void createStartEvent(Petrinet net, Marking initialMarking, BPMNDiagram diagram) {
        Event start = diagram.addEvent("start", Event.EventType.START, Event.EventTrigger.NONE, Event.EventUse.CATCH, true, null);
        if(initialMarking != null && initialMarking.size() > 0) {
            Place p = initialMarking.toArray(new Place[1])[0];
            for (PetrinetEdge edge : net.getEdges()) {
                if (edge.getSource().equals(p)) {
                    BPMNNode target = null;
                    if (transitionActivityUnifiedMap.containsKey(edge.getTarget())) {
                        target = transitionActivityUnifiedMap.get(edge.getTarget());
                    } else if (transitionANDJoinGatewayUnifiedMap.containsKey(edge.getTarget())) {
                        target = transitionANDJoinGatewayUnifiedMap.get(edge.getTarget());
                    } else if (transitionANDSplitGatewayUnifiedMap.containsKey(edge.getTarget())) {
                        target = transitionANDSplitGatewayUnifiedMap.get(edge.getTarget());
                    }

                    if (!areConnected(start, target)) {
                        connect(diagram, start, target);
                    }
                }
            }
        }
    }

    private static void createEndEvent(Petrinet net, Marking finalMarking, BPMNDiagram diagram) {
        Event end = diagram.addEvent("end", Event.EventType.END, Event.EventTrigger.NONE, Event.EventUse.CATCH, true, null);
        if(finalMarking != null && finalMarking.size() > 0) {
            Place p = finalMarking.toArray(new Place[1])[0];
            for (PetrinetEdge edge : net.getEdges()) {
                if (edge.getTarget().equals(p)) {
                    BPMNNode target = null;
                    if (transitionActivityUnifiedMap.containsKey(edge.getSource())) {
                        target = transitionActivityUnifiedMap.get(edge.getSource());
                    } else if (transitionANDJoinGatewayUnifiedMap.containsKey(edge.getSource())) {
                        target = transitionANDJoinGatewayUnifiedMap.get(edge.getSource());
                    } else if (transitionANDSplitGatewayUnifiedMap.containsKey(edge.getSource())) {
                        target = transitionANDSplitGatewayUnifiedMap.get(edge.getSource());
                    }

                    if (!areConnected(target, end)) {
                        connect(diagram, target, end);
                    }
                }
            }
        }
    }

    private static boolean areConnected(BPMNNode source, BPMNNode target) {
        if(source == null || target == null) return true;
        Set<BPMNNode> value;
        if((value = connected.get(source)) != null) {
            return value.contains(target);
        }
        return false;
    }

    private static void connect(BPMNDiagram diagram, BPMNNode source, BPMNNode target) {
        Set<BPMNNode> value;
        if((value = connected.get(source)) == null) {
            value = new UnifiedSet<BPMNNode>();
            connected.put(source, value);
        }
        value.add(target);
        diagram.addFlow(source, target, "");
    }

    private static void removeInvisibleActivities(BPMNDiagram diagram) {
        List<Activity> activities = new ArrayList<Activity>();
        for(Activity activity : diagram.getActivities()) {
            if(activity.getLabel().equalsIgnoreCase("INVISIBLE") || activity.getLabel().equalsIgnoreCase("TAU")) {
                activities.add(activity);
            }
        }

        for(Activity activity : activities) {
            BPMNNode source = null;
            BPMNNode target = null;
            for(Flow flow : diagram.getFlows()) {
                if(flow.getTarget().equals(activity)) {
                    source = flow.getSource();
                }else if(flow.getSource().equals(activity)) {
                    target = flow.getTarget();
                }
            }
            if(source != null && target != null) {
                diagram.removeNode(activity);
                if(!areConnected(source, target)) {
                    connect(diagram, source, target);
                }
            }
        }
    }

    private static Petrinet isolateTransitions(Petrinet net) {
        boolean added = true;
        while(added) {
            added = false;
            for(Transition t : net.getTransitions()) {
                if(!t.isInvisible()) {
                    List<PetrinetEdge> outgoing = new ArrayList<PetrinetEdge>();
                    List<PetrinetEdge> incoming = new ArrayList<PetrinetEdge>();
                    for (PetrinetEdge edge : net.getEdges()) {
                        if (edge.getSource().equals(t)) {
                            outgoing.add(edge);
                        }
                        if (edge.getTarget().equals(t)) {
                            incoming.add(edge);
                        }
                    }

                    if (outgoing.size() > 1) {
                        Transition t1 = net.addTransition("INVISIBLE");
                        t1.setInvisible(true);
                        Place p1 = net.addPlace("");
                        net.addArc(t, p1);
                        net.addArc(p1, t1);
                        for(PetrinetEdge edge : outgoing) {
                            net.addArc(t1, (Place) edge.getTarget());
                            net.removeEdge(edge);
                        }
                        added = true;
                        break;
                    }

                    if (incoming.size() > 1) {
                        Transition t1 = net.addTransition("INVISIBLE");
                        t1.setInvisible(true);
                        Place p1 = net.addPlace("");
                        net.addArc(t1, p1);
                        net.addArc(p1, t);
                        for(PetrinetEdge edge : incoming) {
                            net.addArc((Place) edge.getSource(), t1);
                            net.removeEdge(edge);
                        }
                        added = true;
                        break;
                    }
                }
            }
        }
        return net;
    }

    private static Petrinet insertTransitionsToClarifySplits(Petrinet net) {
        List<Place> places = new ArrayList<Place>();
        for(Place place : net.getPlaces()) {
            int input = 0;
            int output = 0;
            for(PetrinetEdge edge : net.getEdges()) {
                if(edge.getSource().equals(place)) {
                    output++;
                }else if(edge.getTarget().equals(place)) {
                    input++;
                }
            }
            if(input > 1 && output > 1) {
                places.add(place);
            }
        }

        for(Place place : places) {
            List<Transition> incomings = new ArrayList<Transition>();
            List<Transition> outgoings = new ArrayList<Transition>();
            for(PetrinetEdge edge : net.getEdges()) {
                if(edge.getSource().equals(place)) {
                    outgoings.add((Transition) edge.getTarget());
                }else if(edge.getTarget().equals(place)) {
                    incomings.add((Transition) edge.getSource());
                }
            }

            Place input = net.addPlace("");
            Place output = net.addPlace("");
            Transition transition = net.addTransition("INVISIBLE");
            transition.setInvisible(true);
            net.removeNode(place);

            for(Transition transition1 : incomings) {
                net.addArc(transition1, input);
            }
            for(Transition transition1 : outgoings) {
                net.addArc(output, transition1);
            }
            net.addArc(input, transition);
            net.addArc(transition, output);
        }
        return net;
    }

    private static Petrinet insertPlacesToClarifySplits(Petrinet net) {
        List<Transition> transitions = new ArrayList<Transition>();
        for(Transition transition : net.getTransitions()) {
            int input = 0;
            int output = 0;
            for(PetrinetEdge edge : net.getEdges()) {
                if(edge.getSource().equals(transition)) {
                    output++;
                }else if(edge.getTarget().equals(transition)) {
                    input++;
                }
            }
            if(input > 1 && output > 1) {
                transitions.add(transition);
            }
        }

        for(Transition transition : transitions) {
            List<Place> incomings = new ArrayList<Place>();
            List<Place> outgoings = new ArrayList<Place>();
            for(PetrinetEdge edge : net.getEdges()) {
                if(edge.getSource().equals(transition)) {
                    outgoings.add((Place) edge.getTarget());
                }else if(edge.getTarget().equals(transition)) {
                    incomings.add((Place) edge.getSource());
                }
            }

            Transition input = net.addTransition("INVISIBLE");
            input.setInvisible(true);
            Transition output = net.addTransition("INVISIBLE");
            output.setInvisible(true);
            Place place = net.addPlace("");
            if(!transition.isInvisible()) System.out.println("ERROR 520!");
            net.removeNode(transition);

            for(Place place1 : incomings) {
                net.addArc(place1, input);
            }
            for(Place place1 : outgoings) {
                net.addArc(output, place1);
            }
            net.addArc(input, place);
            net.addArc(place, output);
        }
        return net;
    }

    private static Petrinet insertPlacesAndTransitionsToClarifySplits(Petrinet net) {
        boolean added = true;
        while(added) {
            added = false;
            PetrinetEdge first = null;
            for (PetrinetEdge edge1 : net.getEdges()) {
                if(edge1.getSource() instanceof Place) {
                    for (PetrinetEdge edge2 : net.getEdges()) {
                        if (edge1.getSource().equals(edge2.getTarget()) && edge1.getTarget().equals(edge2.getSource())) {
                            first = edge1;
                            break;
                        }
                    }
                }
            }
            if(first != null) {
                Place p = (Place) first.getSource();
                Transition t = (Transition) first.getTarget();
                net.removeEdge(first);
                Transition t1 = net.addTransition("INVISIBLE");
                t1.setInvisible(true);
                Place p1 = net.addPlace("p1");
                net.addArc(p, t1);
                net.addArc(t1, p1);
                net.addArc(p1, t);
                added = true;
            }
        }
        return net;
    }

    private static void connectSingleOuputTransitions(Petrinet net, BPMNDiagram diagram) {
        for(Transition transition : net.getTransitions()) {
            Place place = null;
            int count = 0;
            for(PetrinetEdge edge : net.getEdges()) {
                if(edge.getSource().equals(transition)) {
                    place = (Place) edge.getTarget();
                    count++;
                }
            }
            if(count == 1) {
                BPMNNode source;
                BPMNNode target = null;

                if(placeXORJoinGatewayUnifiedMap.containsKey(place)) {
                    source = null;
                }else if(!transitionANDJoinGatewayUnifiedMap.containsKey(transition)) {
                    source = transitionActivityUnifiedMap.get(transition);
                }else {
                    source = transitionANDJoinGatewayUnifiedMap.get(transition);
                }

                for(PetrinetEdge edge : net.getEdges()) {
                    if (edge.getSource().equals(place)) {
                        if(hasFollowingTransitionsInXOR(net, place)) {
                            target = placeXORSplitGatewayUnifiedMap.get(edge.getSource());
                        }else if (transitionANDSplitGatewayUnifiedMap.containsKey(edge.getTarget())) {
                            target = transitionANDSplitGatewayUnifiedMap.get(edge.getTarget());
                        }else if(hasPrecedingTransitionsInXOR(net, place)) {
                            target = placeXORJoinGatewayUnifiedMap.get(edge.getSource());
                        }else if (transitionANDJoinGatewayUnifiedMap.containsKey(edge.getTarget())) {
                            target = null;
                        }else {
                            target = transitionActivityUnifiedMap.get(edge.getTarget());
                        }
                    }

                    if(source != null && target != null) {
                        if(!areConnected(source, target)) {
                            connect(diagram, source, target);
                        }
                        break;
                    }
                }
            }

            place = null;
            count = 0;
            for(PetrinetEdge edge : net.getEdges()) {
                if(edge.getTarget().equals(transition)) {
                    place = (Place) edge.getSource();
                    count++;
                }
            }
            if(count == 1) {
                BPMNNode source = null;
                BPMNNode target;

                if(hasPrecedingTransitionsInXOR(net, place)) {
                    source = placeXORJoinGatewayUnifiedMap.get(place);
                }else if(hasFollowingTransitionsInXOR(net, place)){
                    source = placeXORSplitGatewayUnifiedMap.get(place);
                }

                for(PetrinetEdge edge : net.getEdges()) {
                    if(edge.getTarget().equals(place)) {
                        if(transitionANDSplitGatewayUnifiedMap.containsKey(transition)) {
                            target = transitionANDSplitGatewayUnifiedMap.get(transition);
                        }else if(transitionANDJoinGatewayUnifiedMap.containsKey(transition)) {
                            target = transitionANDJoinGatewayUnifiedMap.get(transition);
                        }else {
                            target = transitionActivityUnifiedMap.get(transition);
                        }

                        if(source != null && target != null) {
                            if(!areConnected(source, target)) {
                                connect(diagram, source, target);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void createXORSplitGateways(Petrinet net, BPMNDiagram diagram) {
        for(Place place : net.getPlaces()) {
            if(hasFollowingTransitionsInXOR(net, place)) {
                List<Transition> followers = getFollowingTransitionsInXOR(net, place);
                Gateway xor = diagram.addGateway("XOR", Gateway.GatewayType.DATABASED);
                placeXORSplitGatewayUnifiedMap.put(place, xor);
                for(Transition follower : followers) {
                    BPMNNode target;
                    if(transitionANDJoinGatewayUnifiedMap.containsKey(follower)) {
                        target = transitionANDJoinGatewayUnifiedMap.get(follower);
                    }else if(transitionANDSplitGatewayUnifiedMap.containsKey(follower)) {
                        target = transitionANDSplitGatewayUnifiedMap.get(follower);
                    }else {
                        target = transitionActivityUnifiedMap.get(follower);
                    }
                    if(!areConnected(xor, target)) {
                        connect(diagram, xor, target);
                    }
                }
            }
        }
    }

    private static boolean hasFollowingTransitionsInXOR(Petrinet net, Place place) {
        List<PetrinetEdge> edges = new ArrayList<PetrinetEdge>();
        for(PetrinetEdge edge : net.getEdges()) {
            if(edge.getSource().equals(place)) {
                edges.add(edge);
            }
        }
        return edges.size() > 1;
    }

    private static List<Transition> getFollowingTransitionsInXOR(Petrinet net, Place place) {
        List<Transition> transitions = new ArrayList<Transition>();
        for(PetrinetEdge edge : net.getEdges()) {
            if(edge.getSource().equals(place)) {
                transitions.add((Transition) edge.getTarget());
            }
        }
        return transitions;
    }

    private static void createXORJoinGateways(Petrinet net, BPMNDiagram diagram) {
        for(Place place : net.getPlaces()) {
            if(hasPrecedingTransitionsInXOR(net, place)) {
                List<Transition> followers = getPrecedingTransitionsInXOR(net, place);
                if(followers.size() > 1) {
                    Gateway xor = diagram.addGateway("XOR", Gateway.GatewayType.DATABASED);
                    placeXORJoinGatewayUnifiedMap.put(place, xor);
                    for(Transition follower : followers) {
                        BPMNNode source;
                        if(transitionANDSplitGatewayUnifiedMap.containsKey(follower)) {
                            source = transitionANDSplitGatewayUnifiedMap.get(follower);
                        }else if(transitionANDJoinGatewayUnifiedMap.containsKey(follower)) {
                            source = transitionANDJoinGatewayUnifiedMap.get(follower);
                        }else {
                            source = transitionActivityUnifiedMap.get(follower);
                        }
                        if(!areConnected(source, xor)) {
                            connect(diagram, source, xor);
                        }
                    }
                }
            }
        }
    }

    private static boolean hasPrecedingTransitionsInXOR(Petrinet net, Place place) {
        List<PetrinetEdge> edges = new ArrayList<PetrinetEdge>();
        for(PetrinetEdge edge : net.getEdges()) {
            if(edge.getTarget().equals(place)) {
                edges.add(edge);
            }
        }
        return edges.size() > 1;
    }

    private static List<Transition> getPrecedingTransitionsInXOR(Petrinet net, Place place) {
        List<Transition> transitions = new ArrayList<Transition>();
        for(PetrinetEdge edge : net.getEdges()) {
            if(edge.getTarget().equals(place)) {
                transitions.add((Transition) edge.getSource());
            }
        }
        return transitions;
    }

    private static void initializeANDSplitGateways(Petrinet net, BPMNDiagram diagram) {
        String label = null;
        Gateway.GatewayType gType = null;
        for(Transition transition : net.getTransitions()) {
            if(hasFollowingPlacesInAND(net, transition)) {
                List<Place> followers = getFollowingPlacesInAND(net, transition);
                if(followers.size() > 1) {
                    if(transition.getLabel().equalsIgnoreCase("ORSPLIT")) {
                        label = "OR";
                        gType = Gateway.GatewayType.INCLUSIVE;
                    } else {
                        label = "AND";
                        gType = Gateway.GatewayType.PARALLEL;
                    }
                    Gateway and = diagram.addGateway(label, gType);
                    if(!transition.isInvisible()) {
                        if(!areConnected(transitionActivityUnifiedMap.get(transition), and)) {
                            connect(diagram, transitionActivityUnifiedMap.get(transition), and);
                        }
                    }else {
                        diagram.removeNode(transitionActivityUnifiedMap.get(transition));
                        transitionActivityUnifiedMap.remove(transition);
                    }
                    transitionANDSplitGatewayUnifiedMap.put(transition, and);
                }
            }
        }
    }

    private static void finalizeANDSplitGateways(Petrinet net, BPMNDiagram diagram) {
        for(Transition transition : net.getTransitions()) {
            if(transitionANDSplitGatewayUnifiedMap.containsKey(transition)) {
                List<Place> followers = getFollowingPlacesInAND(net, transition);
                Gateway and = transitionANDSplitGatewayUnifiedMap.get(transition);
                for(Place follower : followers) {
                    BPMNNode target;
                    if(placeXORSplitGatewayUnifiedMap.containsKey(follower)) {
                        target = placeXORSplitGatewayUnifiedMap.get(follower);
                    }else if(placeXORJoinGatewayUnifiedMap.containsKey(follower)) {
                        target = placeXORJoinGatewayUnifiedMap.get(follower);
                    }else {
                        if(transitionANDJoinGatewayUnifiedMap.containsKey(getFollower(net, follower))) {
                            target = transitionANDJoinGatewayUnifiedMap.get(getFollower(net, follower));
                        }else if(transitionANDSplitGatewayUnifiedMap.containsKey(getFollower(net, follower))) {
                            target = transitionANDSplitGatewayUnifiedMap.get(getFollower(net, follower));
                        }else {
                            target = transitionActivityUnifiedMap.get(getFollower(net, follower));
                        }
                    }

                    if(target != null) {
                        if(!areConnected(and, target)) {
                            connect(diagram, and, target);
                        }
                    }
                }
            }
        }
    }

    private static boolean hasFollowingPlacesInAND(Petrinet net, Transition transition) {
        List<PetrinetEdge> edges = new ArrayList<PetrinetEdge>();
        for(PetrinetEdge edge : net.getEdges()) {
            if(edge.getSource().equals(transition)) {
                edges.add(edge);
            }
        }
        return edges.size() > 1;
    }

    private static List<Place> getFollowingPlacesInAND(Petrinet net, Transition transition) {
        List<Place> places = new ArrayList<Place>();
        for(PetrinetEdge edge : net.getEdges()) {
            if(edge.getSource().equals(transition)) {
                places.add((Place) edge.getTarget());
            }
        }
        return places;
    }

    private static void initializeANDJoinGateways(Petrinet net, BPMNDiagram diagram) {
        String label = null;
        Gateway.GatewayType gType = null;
        for(Transition transition : net.getTransitions()) {
            if(hasPrecedingPlacesInAND(net, transition)) {
                List<Place> preceders = getPrecedingPlacesInAND(net, transition);
                if(preceders.size() > 1) {
                    if(transition.getLabel().equalsIgnoreCase("ORJOIN")) {
                        label = "OR";
                        gType = Gateway.GatewayType.INCLUSIVE;
                    } else {
                        label = "AND";
                        gType = Gateway.GatewayType.PARALLEL;
                    }
                    Gateway and = diagram.addGateway(label, gType);
                    if(!transition.isInvisible()) {
                        if(!areConnected(and, transitionActivityUnifiedMap.get(transition))) {
                            connect(diagram, and, transitionActivityUnifiedMap.get(transition));
                        }
                    }else {
                        diagram.removeNode(transitionActivityUnifiedMap.get(transition));
                        transitionActivityUnifiedMap.remove(transition);
                    }
                    transitionANDJoinGatewayUnifiedMap.put(transition, and);
                }
            }
        }
    }

    private static void finalizeANDJoinGateways(Petrinet net, BPMNDiagram diagram) {
        for(Transition transition : net.getTransitions()) {
            if(transitionANDJoinGatewayUnifiedMap.containsKey(transition)) {
                List<Place> preceders = getPrecedingPlacesInAND(net, transition);
                Gateway and = transitionANDJoinGatewayUnifiedMap.get(transition);
                for(Place preceder : preceders) {
                    BPMNNode source;
                    if(placeXORSplitGatewayUnifiedMap.containsKey(preceder)) {
                        source = placeXORSplitGatewayUnifiedMap.get(preceder);
                    }else if(placeXORJoinGatewayUnifiedMap.containsKey(preceder)) {
                        source = placeXORJoinGatewayUnifiedMap.get(preceder);
                    }else {
                        if(transitionANDJoinGatewayUnifiedMap.containsKey(getAncestor(net, preceder))) {
                            source = transitionANDJoinGatewayUnifiedMap.get(getAncestor(net, preceder));
                        }else if(transitionANDSplitGatewayUnifiedMap.containsKey(getAncestor(net, preceder))) {
                            source = transitionANDSplitGatewayUnifiedMap.get(getAncestor(net, preceder));
                        }else {
                            source = transitionActivityUnifiedMap.get(getAncestor(net, preceder));
                        }
                    }

                    if(source!= null) {
                        if(!areConnected(source, and)) {
                            connect(diagram, source, and);
                        }
                    }
                }
            }
        }
    }

    private static boolean hasPrecedingPlacesInAND(Petrinet net, Transition transition) {
        List<PetrinetEdge> edges = new ArrayList<PetrinetEdge>();
        for(PetrinetEdge edge : net.getEdges()) {
            if(edge.getTarget().equals(transition)) {
                edges.add(edge);
            }
        }
        return edges.size() > 1;
    }

    private static List<Place> getPrecedingPlacesInAND(Petrinet net, Transition transition) {
        List<Place> places = new ArrayList<Place>();
        for(PetrinetEdge edge : net.getEdges()) {
            if(edge.getTarget().equals(transition)) {
                places.add((Place) edge.getSource());
            }
        }
        return places;
    }

    private static Transition getFollower(Petrinet net, Place place) {
        for(PetrinetEdge edge : net.getEdges()) {
            if(edge.getSource().equals(place)) {
                return (Transition) edge.getTarget();
            }
        }
        return null;
    }

    private static Transition getAncestor(Petrinet net, Place place) {
        for(PetrinetEdge edge : net.getEdges()) {
            if(edge.getTarget().equals(place)) {
                return (Transition) edge.getSource();
            }
        }
        return null;
    }

    public static BPMNDiagram visualizeSelfLoops(BPMNDiagram diagram) {
        int size = 0;
        while(size != (diagram.getGateways().size() + diagram.getFlows().size())) {
            Gateway entry;
            Gateway exit;
            Activity activity;
            for (Flow flow : diagram.getFlows()) {
                size = (diagram.getGateways().size() + diagram.getFlows().size());
                if (isXORGateway(flow.getSource()) && isXORGateway(flow.getTarget())) {
                    exit = (Gateway) flow.getSource();
                    entry = (Gateway) flow.getTarget();
                    if (isXORJoin(diagram, entry) && isXORSplit(diagram, exit) && (activity = areConnectedThroughActivity(diagram, entry, exit)) != null) {
                        activity.setBLooped(true);
                        List<BPMNNode> inputs = getPrecedingElements(diagram, entry);
                        inputs.remove(exit);

                        List<BPMNNode> outputs = getFollowingElements(diagram, exit);
                        outputs.remove(entry);

                        if (inputs.size() == 1 && outputs.size() == 1) {
                            diagram.removeNode(entry);
                            diagram.removeNode(exit);
                            connect(diagram, inputs.get(0), activity);
                            connect(diagram, activity, outputs.get(0));
                        }else {
                            diagram.removeEdge(flow);
                        }
                        break;
                    }
                }
            }
        }
        return diagram;
    }

    private static List<BPMNNode> getPrecedingElements(BPMNDiagram diagram, BPMNNode node) {
        List<BPMNNode> inputs = new ArrayList<BPMNNode>();
        for(Flow flow : diagram.getFlows()) {
            if(flow.getTarget().equals(node)) inputs.add(flow.getSource());
        }
        return inputs;
    }

    private static List<BPMNNode> getFollowingElements(BPMNDiagram diagram, BPMNNode node) {
        List<BPMNNode> inputs = new ArrayList<BPMNNode>();
        for(Flow flow : diagram.getFlows()) {
            if(flow.getSource().equals(node)) inputs.add(flow.getTarget());
        }
        return inputs;
    }

    private static Activity areConnectedThroughActivity(BPMNDiagram diagram, Gateway entry, Gateway exit) {
        for(Flow flow1 : diagram.getFlows()) {
            if(flow1.getSource().equals(entry)) {
                for(Flow flow2 : diagram.getFlows()) {
                    if(flow2.getSource().equals(flow1.getTarget()) && flow2.getTarget().equals(exit)) {
                        if(flow1.getTarget() instanceof Activity) {
                            return (Activity) flow1.getTarget();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean isXORGateway(BPMNNode node) {
        if(node instanceof Gateway) {
            Gateway gateway = (Gateway) node;
            return gateway.getGatewayType().equals(Gateway.GatewayType.DATABASED);
        }
        return false;
    }

    public static boolean isXORJoin(BPMNDiagram diagram, Gateway gateway) {
        int input = 0;
        for(Flow flow : diagram.getFlows()) {
            if(flow.getTarget().equals(gateway)) input++;
        }
        return input > 1;
    }

    public static boolean isXORSplit(BPMNDiagram diagram, Gateway gateway) {
        int output = 0;
        for(Flow flow : diagram.getFlows()) {
            if(flow.getSource().equals(gateway)) output++;
        }
        return output > 1;
    }
}