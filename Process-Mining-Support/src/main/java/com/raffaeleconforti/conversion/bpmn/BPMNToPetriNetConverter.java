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

package com.raffaeleconforti.conversion.bpmn;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.*;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.models.semantics.petrinet.Marking;

import java.util.*;

/**
 * Created by Raffaele Conforti on 19/02/14.
 */

public class BPMNToPetriNetConverter {

//    public static void main(String[] args) throws Exception {
//        String fileName = "/Users/conforti/Downloads/bp.bpmn";
//        FakePluginContext context = new FakePluginContext();
//        Bpmn bpmn = (Bpmn) new BpmnImportPlugin().importFile(context, fileName);
//
//        BpmnSelectDiagramParameters parameters = new BpmnSelectDiagramParameters();
//        BpmnSelectDiagramDialog dialog = new BpmnSelectDiagramDialog(bpmn.getDiagrams(), parameters);
//        BPMNDiagram newDiagram = BPMNDiagramFactory.newBPMNDiagram("");
//        Map<String, BPMNNode> id2node = new HashMap<String, BPMNNode>();
//        Map<String, Swimlane> id2lane = new HashMap<String, Swimlane>();
//        if (parameters.getDiagram() == BpmnSelectDiagramParameters.NODIAGRAM) {
//            bpmn.unmarshall(newDiagram, id2node, id2lane);
//        } else {
//            Collection<String> elements = parameters.getDiagram().getElements();
//            bpmn.unmarshall(newDiagram, elements, id2node, id2lane);
//        }
//        Object[] object = BPMNToPetriNetConverter.convert(newDiagram);
//        Petrinet pnet = (Petrinet) object[0];
//        //Marking marking = (Marking) object[1];
//        new PnmlExportNetToPNML().exportPetriNetToPNMLFile(context, pnet, new File("/Users/conforti/Downloads/bp.pnml"));
//    }

    /**
     * Convert the given Petri net into an EPC.
     *
     * @param
     * @return Petrinet with marking.
     */
    public static Object[] convert(BPMNDiagram bpmn) {

        bpmn = splitGateways(bpmn);

        int[] counter = new int[]{0, 0};
        Marking initialMarking = new Marking();
        Marking finalMarking = new Marking();
        Petrinet petrinet = new PetrinetImpl(bpmn.getLabel());
        Set<Place> setPEnable = new UnifiedSet<Place>();

        Map<Activity, Place[]> loopActivityMap = new UnifiedMap<Activity, Place[]>();
        Map<SubProcess, Transition[]> eventSubProcessTransitionMap = new UnifiedMap<SubProcess, Transition[]>();
        Map<SubProcess, Place[]> eventSubProcessPlaceMap = new UnifiedMap<SubProcess, Place[]>();

        Map<Activity, Transition> activityTransitionMap = new UnifiedMap<Activity, Transition>();
        for (Activity activity : bpmn.getActivities()) {
            Transition t = petrinet.addTransition(activity.getLabel());
            activityTransitionMap.put(activity, t);
            if (activity.isBLooped() || activity.isBMultiinstance()) {
                Place p1 = petrinet.addPlace("p" + (counter[0]++));
                Place p2 = petrinet.addPlace("p" + (counter[0]++));
                Transition t2 = petrinet.addTransition("t" + (counter[1]++));
                t2.setInvisible(true);
                petrinet.addArc(p1, t);
                petrinet.addArc(t, p2);
                petrinet.addArc(p2, t2);
                petrinet.addArc(t2, p1);
                loopActivityMap.put(activity, new Place[]{p1, p2});
            }
        }

        Map<Event, Place> eventPlaceMap = new UnifiedMap<Event, Place>();
        for (Event event : bpmn.getEvents()) {
            String name = event.getLabel();
            if (event.getParentSubProcess() == null) {
                if (event.getEventType().equals(Event.EventType.START)) {
                    name = "START";
                } else if (event.getEventType().equals(Event.EventType.END)) {
                    name = "END";
                }
            }
            Place p = petrinet.addPlace(name);
            if (name.equals("START")) {
                initialMarking.add(p);
                p.getAttributeMap().put(AttributeMap.LABEL, "p" + counter[0]++);
            }
            if (name.equals("END")) {
                finalMarking.add(p);
//                p.getAttributeMap().put(AttributeMap.LABEL, "p"+counter[0]++);/**/
            }
            eventPlaceMap.put(event, p);
        }

        Map<Gateway, Place> xorGatewayPlaceMap = new UnifiedMap<Gateway, Place>();
        for (Gateway g : bpmn.getGateways()) {
            if (g.getGatewayType().equals(Gateway.GatewayType.DATABASED) || g.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
                Place p = petrinet.addPlace(g.getLabel());
                xorGatewayPlaceMap.put(g, p);
            }
        }

        Map<Gateway, Transition> andGatewayPlaceMap = new UnifiedMap<Gateway, Transition>();
        for (Gateway g : bpmn.getGateways()) {
            if (g.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
                Transition t = petrinet.addTransition(g.getLabel());
                t.setInvisible(true);
                andGatewayPlaceMap.put(g, t);
            }
        }

//        Map<SubProcess, Place[]> loopSubProcessMap = new UnifiedMap<>();

        Map<SubProcess, Set<Transition>> transitionsOfSubProcess = new UnifiedMap<SubProcess, Set<Transition>>();

        Map<SubProcess, List<Place>[]> subProcessPlaceMap = new UnifiedMap<SubProcess, List<Place>[]>();
        for (SubProcess s : bpmn.getSubProcesses()) {

            Set<Transition> setTransitions = new UnifiedSet<Transition>();
            transitionsOfSubProcess.put(s, setTransitions);

            List<Place> start = new ArrayList<Place>();
            List<Place> end = new ArrayList<Place>();

            for (Event e : bpmn.getEvents()) {
                if (s.equals(e.getParentSubProcess())) {
                    if (e.getEventType().equals(Event.EventType.START)) {
                        start.add(eventPlaceMap.get(e));
                    } else if (e.getEventType().equals(Event.EventType.END)) {
                        end.add(eventPlaceMap.get(e));
                    }
                }
            }

            for (Activity a : bpmn.getActivities()) {
                if (s.equals(a.getParentSubProcess())) {
                    setTransitions.add(activityTransitionMap.get(a));
                }
            }

            Place pStartSubProcess = petrinet.addPlace("p" + (counter[0]++));
            if (start.size() == 1) {
                for (Place p : start) {
                    Transition t = petrinet.addTransition("t" + (counter[1]++));
                    t.setInvisible(true);
                    petrinet.addArc(pStartSubProcess, t);
                    petrinet.addArc(t, p);
                    if (initialMarking.contains(p)) {
                        initialMarking.remove(p);
                        initialMarking.add(pStartSubProcess);
                    }
                }
            } else if (start.size() > 1) {
                Place fakeStart = petrinet.addPlace("p" + (counter[0]++));
                Transition t = petrinet.addTransition("t" + (counter[1]++));
                t.setInvisible(true);
                petrinet.addArc(pStartSubProcess, t);
                petrinet.addArc(t, fakeStart);

                for (Place p : start) {
                    Transition t1 = petrinet.addTransition("t" + (counter[1]++));
                    t1.setInvisible(true);
                    petrinet.addArc(fakeStart, t1);
                    petrinet.addArc(t, p);
                    if (initialMarking.contains(p)) {
                        initialMarking.remove(p);
                        initialMarking.add(pStartSubProcess);
                    }
                }
            }
            start.clear();
            start.add(pStartSubProcess);

            Place pEndSubProcess = petrinet.addPlace("END");
            if (end.size() == 1) {
                for (Place p : end) {
                    Transition t = petrinet.addTransition("t" + (counter[1]++));
                    t.setInvisible(true);
                    petrinet.addArc(p, t);
                    petrinet.addArc(t, pEndSubProcess);
                    if (finalMarking.contains(p)) {
                        finalMarking.remove(p);
                        finalMarking.add(pEndSubProcess);
                        if (p.getLabel().equals("END")) {
                            p.getAttributeMap().put(AttributeMap.LABEL, "p" + counter[0]++);
                        }
                    }
                }
            } else if (end.size() > 1) {
                Place fakeEnd = petrinet.addPlace("p" + (counter[0]++));
                Transition t = petrinet.addTransition("t" + (counter[1]++));
                t.setInvisible(true);
                petrinet.addArc(fakeEnd, t);
                petrinet.addArc(t, pEndSubProcess);

                for (Place p : end) {
                    Transition t1 = petrinet.addTransition("t" + (counter[1]++));
                    t1.setInvisible(true);
                    petrinet.addArc(p, t1);
                    petrinet.addArc(t1, fakeEnd);
                    if (finalMarking.contains(p)) {
                        finalMarking.remove(p);
                        finalMarking.add(pEndSubProcess);
                        if (p.getLabel().equals("END")) {
                            p.getAttributeMap().put(AttributeMap.LABEL, "p" + counter[0]++);
                        }
                    }
                }
            }
            end.clear();
            end.add(pEndSubProcess);

            if (s.isBLooped() || s.isBMultiinstance() || s.getTriggeredByEvent()) {
                Transition t = petrinet.addTransition("LoopBack" + (counter[1]++));
                t.setInvisible(true);
                petrinet.addArc(pEndSubProcess, t);
                petrinet.addArc(t, pStartSubProcess);

//                loopSubProcessMap.put(s, new Place[] {pStartSubProcess, pEndSubProcess});
            }

            subProcessPlaceMap.put(s, new List[]{start, end});
        }

        //EVENT SUBPROCESS
        for (SubProcess s : bpmn.getSubProcesses()) {

            List<Place> start = subProcessPlaceMap.get(s)[0];
            List<Place> end = subProcessPlaceMap.get(s)[1];

            if (s.getTriggeredByEvent()) {

                Transition[] transitionOrigin;

                Transition tStart;
                Transition tEnd;
                Place pStart;
                Place pEnd;

                if ((transitionOrigin = eventSubProcessTransitionMap.get(s.getParentSubProcess())) == null) {
                    pStart = petrinet.addPlace("p" + (counter[0]++));
                    pEnd = petrinet.addPlace("END");
                    tStart = petrinet.addTransition("tStartEventSubProcess" + (counter[1]++));
                    tEnd = petrinet.addTransition("tEndEventSubProcess" + (counter[1]++));
                    connectPlaceToTransition(petrinet, pStart, tStart);
                    connectTransitionToPlace(petrinet, tEnd, pEnd);

                    tStart.setInvisible(true);
                    tEnd.setInvisible(true);

                    eventSubProcessTransitionMap.put(s.getParentSubProcess(), new Transition[]{tStart, tEnd});
                    eventSubProcessPlaceMap.put(s.getParentSubProcess(), new Place[]{pStart, pEnd});
                } else {
                    tStart = transitionOrigin[0];
                    tEnd = transitionOrigin[1];
                    Place[] placeOrigin = eventSubProcessPlaceMap.get(s.getParentSubProcess());
                    pStart = placeOrigin[0];
                    pEnd = placeOrigin[1];
                }

                Place pSSkip = petrinet.addPlace("pSSkip" + (counter[0]++));
                Place pESkip = petrinet.addPlace("pESkip" + (counter[0]++));
                Transition tSkip = petrinet.addTransition("tSkip" + (counter[1]++));
                Transition tSSkip = petrinet.addTransition("tSSkip" + (counter[1]++));
                Transition tESkip = petrinet.addTransition("tESkip" + (counter[1]++));
                connectTransitionToPlace(petrinet, tStart, pSSkip);
                connectPlaceToTransition(petrinet, pESkip, tEnd);
                connectPlaceToTransition(petrinet, pSSkip, tSkip);
                connectPlaceToTransition(petrinet, pSSkip, tSSkip);
                connectTransitionToPlace(petrinet, tSkip, pESkip);
                connectTransitionToPlace(petrinet, tESkip, pESkip);

                tSkip.setInvisible(true);
                tSSkip.setInvisible(true);
                tESkip.setInvisible(true);
                if (s.getParentSubProcess() == null) {
                    Place xorEndEvents = petrinet.addPlace("pXOREndEvents" + (counter[0]++));
                    connectPlaceToTransition(petrinet, xorEndEvents, tEnd);

                    for (Place p1 : start) {
                        connectTransitionToPlace(petrinet, tSSkip, p1);
                    }

                    initialMarking.clear();
                    initialMarking.add(pStart);
                    start.clear();
                    start.add(pStart);

                    for (Place p1 : end) {
                        connectPlaceToTransition(petrinet, p1, tESkip);
                    }

                    finalMarking.clear();
                    finalMarking.add(pEnd);
                    end.clear();
                    end.add(pEnd);

                    for (Event e : bpmn.getEvents()) {
                        if (e.getEventType().equals(Event.EventType.START)) {
                            if (e.getParentSubProcess() == null) {
                                connectTransitionToPlace(petrinet, tStart, eventPlaceMap.get(e));
                            }
                        } else if (e.getEventType().equals(Event.EventType.END)) {
                            if (e.getParentSubProcess() == null) {
                                Transition t = petrinet.addTransition("t" + counter[1]++);
                                t.setInvisible(true);
                                connectPlaceToTransition(petrinet, eventPlaceMap.get(e), t);
                                connectTransitionToPlace(petrinet, t, xorEndEvents);
                                if (eventPlaceMap.get(e).getLabel().equals("END")) {
                                    eventPlaceMap.get(e).getAttributeMap().put(AttributeMap.LABEL, "p" + counter[0]++);
                                }
                            }
                        }
                    }
                } else {
                    List<Place>[] places = subProcessPlaceMap.get(s.getParentSubProcess());

                    for (Place p1 : places[0]) {
                        if (!p1.equals(pStart)) connectTransitionToPlace(petrinet, tStart, p1);
                    }
                    for (Place p1 : start) {
                        connectTransitionToPlace(petrinet, tSSkip, p1);
                    }
                    places[0].clear();
                    places[0].add(pStart);

                    for (Place p1 : places[1]) {
                        if (!p1.equals(pEnd)) connectPlaceToTransition(petrinet, p1, tEnd);
                    }
                    for (Place p1 : end) {
                        connectPlaceToTransition(petrinet, p1, tESkip);
                    }
                    places[1].clear();
                    places[1].add(pEnd);

                }
            }

            subProcessPlaceMap.put(s, new List[]{start, end});

        }

        for (Flow f : bpmn.getFlows()) {
            BPMNNode source = f.getSource();
            BPMNNode target = f.getTarget();

            Event sourceEvent = null;
            Gateway sourceGateway = null;
            Activity sourceActivity = null;
            SubProcess sourceSubProcess = null;

            if (source instanceof Event) {
                sourceEvent = (Event) source;
            } else if (source instanceof Gateway) {
                sourceGateway = (Gateway) source;
            } else if (source instanceof SubProcess) {
                sourceSubProcess = (SubProcess) source;
            } else if (source instanceof Activity) {
                sourceActivity = (Activity) source;
            }

            Event targetEvent = null;
            Gateway targetGateway = null;
            Activity targetActivity = null;
            SubProcess targetSubProcess = null;

            if (target instanceof Event) {
                targetEvent = (Event) target;
            } else if (target instanceof Gateway) {
                targetGateway = (Gateway) target;
            } else if (target instanceof SubProcess) {
                targetSubProcess = (SubProcess) target;
            } else if (target instanceof Activity) {
                targetActivity = (Activity) target;
            }

            if (sourceEvent != null) {
                connectEventTo(petrinet, sourceEvent, targetEvent, targetGateway, targetActivity, targetSubProcess, eventPlaceMap, xorGatewayPlaceMap, andGatewayPlaceMap, activityTransitionMap, loopActivityMap, subProcessPlaceMap, counter);
            } else if (sourceGateway != null) {
                connectGatewayTo(petrinet, sourceGateway, targetEvent, targetGateway, targetActivity, targetSubProcess, eventPlaceMap, xorGatewayPlaceMap, andGatewayPlaceMap, activityTransitionMap, loopActivityMap, subProcessPlaceMap, counter);
            } else if (sourceActivity != null) {
                Place[] places1;
                if ((places1 = loopActivityMap.get(sourceActivity)) != null) {
                    connectLoopActivityTo(petrinet, places1, targetEvent, targetGateway, targetActivity, targetSubProcess, eventPlaceMap, xorGatewayPlaceMap, andGatewayPlaceMap, activityTransitionMap, loopActivityMap, subProcessPlaceMap, counter);
                } else {
                    connectSimpleActivityTo(petrinet, sourceActivity, targetEvent, targetGateway, targetActivity, targetSubProcess, eventPlaceMap, xorGatewayPlaceMap, andGatewayPlaceMap, activityTransitionMap, loopActivityMap, subProcessPlaceMap, counter);
                }
            } else if (sourceSubProcess != null) {
                connectSubProcessTo(petrinet, sourceSubProcess, targetEvent, targetGateway, targetActivity, targetSubProcess, eventPlaceMap, xorGatewayPlaceMap, andGatewayPlaceMap, activityTransitionMap, loopActivityMap, subProcessPlaceMap, counter);
            }
        }

        for (SubProcess s : bpmn.getSubProcesses()) {
            if (s.getNumOfBoundaryEvents() > 0) {
                Transition tEXCP;
                Place pNOK = null;
                Place pEnable = null;
                boolean first = true;
                for (Event e : bpmn.getEvents()) {
                    if (s.equals(e.getBoundingNode())) {
                        tEXCP = petrinet.addTransition("tEXCP" + counter[1]++);
                        tEXCP.setInvisible(true);
                        if (first) {
                            Place pOK = petrinet.addPlace("pOK" + counter[0]++);
                            pNOK = petrinet.addPlace("pNOK" + counter[0]++);
                            pEnable = petrinet.addPlace("pEnabled" + counter[0]++);
                            setPEnable.add(pEnable);

                            Transition tEX = petrinet.addTransition("tEX" + counter[1]++);
                            tEX.setInvisible(true);

                            connectPlaceToTransition(petrinet, pOK, tEX);
                            connectTransitionToPlace(petrinet, tEX, pNOK);

                            for (Transition t : transitionsOfSubProcess.get(s)) {
                                Transition t1 = petrinet.addTransition("Fake " + t.getLabel());
                                t1.setInvisible(true);
                                for (Place place : getTransitionPredecessors(petrinet, t)) {
                                    connectPlaceToTransition(petrinet, place, t1);
                                }
                                for (Place place : getTransitionSuccessors(petrinet, t)) {
                                    connectTransitionToPlace(petrinet, t1, place);
                                }
                                connectTransitionToPlace(petrinet, t, pOK);
                                connectPlaceToTransition(petrinet, pOK, t);
                                connectTransitionToPlace(petrinet, t1, pNOK);
                                connectPlaceToTransition(petrinet, pNOK, t1);
                            }

                            //ADD arc from Ts to pOK
                            connectTransitionToPlace(petrinet, getPlaceSuccessors(petrinet, subProcessPlaceMap.get(s)[0].get(0)).get(0), pOK);
                            //ADD arc from pOK to Te
                            connectPlaceToTransition(petrinet, pOK, getPlacePredecessors(petrinet, subProcessPlaceMap.get(s)[1].get(0)).get(0));
                            //ADD arc from P(Gn,e) to Texcp
                            for (Place p : getTransitionPredecessors(petrinet, getPlacePredecessors(petrinet, subProcessPlaceMap.get(s)[1].get(0)).get(0))) {
                                if (p != pEnable && p != pOK) {
                                    connectPlaceToTransition(petrinet, p, tEXCP);
                                }
                            }
                            //ADD arc from pEnabled to Ts
                            connectPlaceToTransition(petrinet, pEnable, getPlaceSuccessors(petrinet, subProcessPlaceMap.get(s)[0].get(0)).get(0));
                            //ADD arc from Te to pEnabled
                            connectTransitionToPlace(petrinet, getPlacePredecessors(petrinet, subProcessPlaceMap.get(s)[1].get(0)).get(0), pEnable);
                            first = false;
                        }

                        connectPlaceToTransition(petrinet, pNOK, tEXCP);
                        connectTransitionToPlace(petrinet, tEXCP, pEnable);
                        connectTransitionToPlace(petrinet, tEXCP, eventPlaceMap.get(e));

                    }
                }
            }
        }

        Place newStart = petrinet.addPlace("START");
        Transition newTransition = petrinet.addTransition("t" + counter[1]++);
        newTransition.setInvisible(true);
        connectPlaceToTransition(petrinet, newStart, newTransition);

        Iterator<Place> it = initialMarking.iterator();
        while (it.hasNext()) {
            Place p = it.next();
            connectTransitionToPlace(petrinet, newTransition, p);
        }
        for (Place p : setPEnable) {
            connectTransitionToPlace(petrinet, newTransition, p);
        }
        initialMarking.clear();
        initialMarking.add(newStart);

        Place finalPlace = petrinet.addPlace("p" + counter[0]++);
        it = finalMarking.iterator();
        while (it.hasNext()) {
            Place p = it.next();
            Transition t = petrinet.addTransition("t" + counter[1]++);
            t.setInvisible(true);
            connectPlaceToTransition(petrinet, p, t);
            connectTransitionToPlace(petrinet, t, finalPlace);
        }

        Place newFinalPlace = petrinet.addPlace("END");
        Transition newFinalTransition = petrinet.addTransition("t" + counter[1]++);
        newFinalTransition.setInvisible(true);
        connectTransitionToPlace(petrinet, newFinalTransition, newFinalPlace);
        connectPlaceToTransition(petrinet, finalPlace, newFinalTransition);
        for (Place p : setPEnable) {
            connectPlaceToTransition(petrinet, p, newFinalTransition);
        }
        finalMarking.clear();
        finalMarking.add(newFinalPlace);

        boolean modified = true;
        boolean modified2 = true;
        while (modified || modified2) {
            modified = false;
            it = petrinet.getPlaces().iterator();
            while (it.hasNext()) {
                Place p = it.next();
                List<Transition> pre = getPlacePredecessors(petrinet, p);
                List<Transition> post = getPlaceSuccessors(petrinet, p);
                if (pre.size() == 1 && post.size() == 1) {
                    Transition preT = pre.get(0);
                    Transition postT = post.get(0);
                    List<Place> postPreT = getTransitionSuccessors(petrinet, preT);
                    List<Place> prePostT = getTransitionPredecessors(petrinet, postT);
                    if (postPreT.size() == 1 && prePostT.size() == 1 && postT.isInvisible()) {
                        petrinet.removePlace(p);
                        for (Place p1 : getTransitionSuccessors(petrinet, postT)) {
                            connectTransitionToPlace(petrinet, preT, p1);
                        }
                        petrinet.removeTransition(postT);
                        it = petrinet.getPlaces().iterator();
                        modified = true;
                    } else if (prePostT.size() == 1 && postT.isInvisible()) {
                        List<Place> postPostT = getTransitionSuccessors(petrinet, postT);
                        if (postPostT.size() == 1) {
                            petrinet.removePlace(p);
                            petrinet.removeTransition(postT);
                            connectTransitionToPlace(petrinet, preT, postPostT.get(0));
                            it = petrinet.getPlaces().iterator();
                            modified = true;
                        }
                    } else if (postPreT.size() == 1 && preT.isInvisible()) {
                        List<Place> prePreT = getTransitionPredecessors(petrinet, preT);
                        if (prePreT.size() == 1) {
                            petrinet.removePlace(p);
                            petrinet.removeTransition(preT);
                            connectPlaceToTransition(petrinet, prePreT.get(0), postT);
                            it = petrinet.getPlaces().iterator();
                            modified = true;
                        }
                    }
                }
            }

            modified2 = false;
            Iterator<Transition> it2 = petrinet.getTransitions().iterator();
            while (it2.hasNext()) {
                Transition t = it2.next();
                if (t.isInvisible()) {
                    List<Place> pre = getTransitionPredecessors(petrinet, t);
                    List<Place> post = getTransitionSuccessors(petrinet, t);
                    if (pre.size() == 1 && post.size() == 1) {
                        Place preP = pre.get(0);
                        Place postP = post.get(0);
                        List<Transition> postPreP = getPlaceSuccessors(petrinet, preP);
                        List<Transition> prePostP = getPlacePredecessors(petrinet, postP);
                        if (postPreP.size() == 1 && prePostP.size() == 1) {
                            petrinet.removeTransition(t);
                            for (Transition t1 : getPlaceSuccessors(petrinet, postP)) {
                                connectPlaceToTransition(petrinet, preP, t1);
                            }
                            petrinet.removePlace(postP);
                            it2 = petrinet.getTransitions().iterator();
                            modified2 = true;
                        }
                    }
                }
            }
        }

        for (Place p : petrinet.getPlaces()) {
            if (getPlaceSuccessors(petrinet, p).size() == 0) {
                p.getAttributeMap().put(AttributeMap.LABEL, "END");
                finalMarking.clear();
                finalMarking.add(p);
            } else {
                if (p.getLabel().equals("END")) {
                    p.getAttributeMap().put(AttributeMap.LABEL, "p" + counter[0]++);
                }
            }
        }

        return new Object[]{petrinet, initialMarking, finalMarking};
    }

    private static BPMNDiagram splitGateways(BPMNDiagram bpmn) {
        BPMNDiagram diagram = new BPMNDiagramImpl(bpmn.getLabel());

        Map<BPMNNode, BPMNNode> map = new UnifiedMap<>();
        BPMNNode node;
        for(Activity a : bpmn.getActivities()) {
            node = diagram.addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(), a.isBMultiinstance(), a.isBCollapsed());
            map.put(a, node);
        }

        for(Event e : bpmn.getEvents()) {
            node = diagram.addEvent(e.getLabel(), e.getEventType(), e.getEventTrigger(), e.getEventUse(), Boolean.parseBoolean(e.isInterrupting()), (Activity) map.get(e.getBoundingNode()));
            map.put(e, node);
        }

        for(Gateway g : bpmn.getGateways()) {
            node = diagram.addGateway(g.getLabel(), g.getGatewayType());
            map.put(g, node);
        }

        for(Flow f : bpmn.getFlows()) {
            diagram.addFlow(map.get(f.getSource()), map.get(f.getTarget()), f.getLabel());
        }

        Gateway g = null;
        Iterator<Gateway> gatewayIterator = diagram.getGateways().iterator();
        while(gatewayIterator.hasNext()) {
            g = gatewayIterator.next();

            List<Flow> input = new ArrayList<>();
            List<Flow> output = new ArrayList<>();

            for(Flow f : diagram.getFlows()) {
                if(f.getSource().equals(g)) {
                    output.add(f);
                }
                if(f.getTarget().equals(g)) {
                    input.add(f);
                }
            }

            if(input.size() > 1 && output.size() > 1) {
                Gateway g1 = diagram.addGateway(g.getLabel(), g.getGatewayType());

                diagram.addFlow(g, g1, "");
                for(Flow f : output) {
                    diagram.addFlow(g1, f.getTarget(), f.getLabel());
                    diagram.removeEdge(f);
                }

                gatewayIterator = diagram.getGateways().iterator();
            }
        }

        return diagram;
    }

    private static void connectSubProcessTo(Petrinet petrinet, SubProcess sourceSubProcess, Event targetEvent, Gateway targetGateway, Activity targetActivity, SubProcess targetSubProcess,
                                            Map<Event, Place> eventPlaceMap, Map<Gateway, Place> xorGatewayPlaceMap, Map<Gateway, Transition> andGatewayPlaceMap,
                                            Map<Activity, Transition> activityTransitionMap, Map<Activity, Place[]> loopActivityMap, Map<SubProcess, List<Place>[]> subProcessPlaceMap, int[] counter) {

        for (Place end : subProcessPlaceMap.get(sourceSubProcess)[1]) {
            if (targetEvent != null) {
                connectPlaceToPlace(petrinet, end, eventPlaceMap.get(targetEvent), counter);
            } else if (targetGateway != null) {
                connectPlaceToGateway(petrinet, end, targetGateway, xorGatewayPlaceMap, andGatewayPlaceMap, counter);
            } else if (targetActivity != null) {
                connectPlaceToActivity(petrinet, end, targetActivity, activityTransitionMap, loopActivityMap, counter);
            } else if (targetSubProcess != null) {
                for (Place start : subProcessPlaceMap.get(targetSubProcess)[0]) {
                    connectPlaceToPlace(petrinet, end, start, counter);
                }
            }
        }

    }

    private static void connectPlaceToPlace(Petrinet petrinet, Place sourcePlace, Place targetPlace, int[] counter) {

        Transition t = petrinet.addTransition("t" + (counter[1]++));
        t.setInvisible(true);
        petrinet.addArc(sourcePlace, t);
        petrinet.addArc(t, targetPlace);

    }

    private static void connectPlaceToTransition(Petrinet petrinet, Place sourcePlace, Transition targetTransition) {

        petrinet.addArc(sourcePlace, targetTransition);

    }

    private static void connectTransitionToPlace(Petrinet petrinet, Transition sourceTransition, Place targetPlace) {

        petrinet.addArc(sourceTransition, targetPlace);

    }

    private static void connectTransitionToTransition(Petrinet petrinet, Transition sourceTransition, Transition targetTransition, int[] counter) {

        Place p = petrinet.addPlace("p" + (counter[0]++));
        petrinet.addArc(sourceTransition, p);
        petrinet.addArc(p, targetTransition);

    }

    private static void connectPlaceToGateway(Petrinet petrinet, Place sourcePlace, Gateway targetGateway,
                                              Map<Gateway, Place> xorGatewayPlaceMap, Map<Gateway, Transition> andGatewayPlaceMap, int[] counter) {

        if (targetGateway.getGatewayType().equals(Gateway.GatewayType.DATABASED) || targetGateway.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
            connectPlaceToPlace(petrinet, sourcePlace, xorGatewayPlaceMap.get(targetGateway), counter);
        } else if (targetGateway.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
            connectPlaceToTransition(petrinet, sourcePlace, andGatewayPlaceMap.get(targetGateway));
        }

    }

    private static void connectSimpleActivityTo(Petrinet petrinet, Activity sourceActivity, Event targetEvent, Gateway targetGateway, Activity targetActivity, SubProcess targetSubProcess,
                                                Map<Event, Place> eventPlaceMap, Map<Gateway, Place> xorGatewayPlaceMap, Map<Gateway, Transition> andGatewayPlaceMap,
                                                Map<Activity, Transition> activityTransitionMap, Map<Activity, Place[]> loopActivityMap, Map<SubProcess, List<Place>[]> subProcessPlaceMap, int[] counter) {
        if (targetEvent != null) {
            connectSimpleActivityToEvent(petrinet, sourceActivity, targetEvent, activityTransitionMap, eventPlaceMap);
        } else if (targetGateway != null) {
            connectSimpleActivityToGateway(petrinet, sourceActivity, targetGateway, activityTransitionMap, xorGatewayPlaceMap, andGatewayPlaceMap, counter);
        } else if (targetActivity != null) {
            connectSimpleActivityToActivity(petrinet, sourceActivity, targetActivity, activityTransitionMap, loopActivityMap, counter);
        } else if (targetSubProcess != null) {
            for (Place start : subProcessPlaceMap.get(targetSubProcess)[0]) {
                connectTransitionToPlace(petrinet, activityTransitionMap.get(sourceActivity), start);
            }
        }
    }

    private static void connectSimpleActivityToActivity(Petrinet petrinet, Activity sourceActivity, Activity targetActivity,
                                                        Map<Activity, Transition> activityTransitionMap, Map<Activity, Place[]> loopActivityMap, int[] counter) {

        Place[] places2;
        if ((places2 = loopActivityMap.get(targetActivity)) != null) {
            connectTransitionToPlace(petrinet, activityTransitionMap.get(sourceActivity), places2[0]);
        } else {
            connectTransitionToTransition(petrinet, activityTransitionMap.get(sourceActivity), activityTransitionMap.get(targetActivity), counter);
        }
    }

    private static void connectSimpleActivityToGateway(Petrinet petrinet, Activity sourceActivity, Gateway targetGateway,
                                                       Map<Activity, Transition> activityTransitionMap, Map<Gateway, Place> xorGatewayPlaceMap, Map<Gateway, Transition> andGatewayPlaceMap, int[] counter) {

        if (targetGateway.getGatewayType().equals(Gateway.GatewayType.DATABASED) || targetGateway.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
            connectTransitionToPlace(petrinet, activityTransitionMap.get(sourceActivity), xorGatewayPlaceMap.get(targetGateway));
        } else if (targetGateway.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
            connectTransitionToTransition(petrinet, activityTransitionMap.get(sourceActivity), andGatewayPlaceMap.get(targetGateway), counter);
        }

    }

    private static void connectSimpleActivityToEvent(Petrinet petrinet, Activity sourceActivity, Event targetEvent, Map<Activity, Transition> activityTransitionMap, Map<Event, Place> eventPlaceMap) {

        connectTransitionToPlace(petrinet, activityTransitionMap.get(sourceActivity), eventPlaceMap.get(targetEvent));

    }

    private static void connectLoopActivityTo(Petrinet petrinet, Place[] places1, Event targetEvent, Gateway targetGateway, Activity targetActivity,
                                              SubProcess targetSubProcess, Map<Event, Place> eventPlaceMap, Map<Gateway, Place> xorGatewayPlaceMap, Map<Gateway, Transition> andGatewayPlaceMap,
                                              Map<Activity, Transition> activityTransitionMap, Map<Activity, Place[]> loopActivityMap, Map<SubProcess, List<Place>[]> subProcessPlaceMap, int[] counter) {
        if (targetEvent != null) {
            connectLoopActivityToEvent(petrinet, places1, targetEvent, eventPlaceMap, counter);
        } else if (targetGateway != null) {
            connectLoopActivityToGateway(petrinet, places1, targetGateway, xorGatewayPlaceMap, andGatewayPlaceMap, counter);
        } else if (targetActivity != null) {
            connectLoopActivityToActivity(petrinet, places1, targetActivity, activityTransitionMap, loopActivityMap, counter);
        } else if (targetSubProcess != null) {
            for (Place start : subProcessPlaceMap.get(targetSubProcess)[0]) {
                connectLoopActivityToPlace(petrinet, places1, start, counter);
            }
        }
    }

    private static void connectLoopActivityToActivity(Petrinet petrinet, Place[] places1, Activity targetActivity,
                                                      Map<Activity, Transition> activityTransitionMap, Map<Activity, Place[]> loopActivityMap, int[] counter) {

        Place[] places2;
        if ((places2 = loopActivityMap.get(targetActivity)) != null) {
            connectPlaceToPlace(petrinet, places1[1], places2[0], counter);
        } else {
            connectPlaceToTransition(petrinet, places1[1], activityTransitionMap.get(targetActivity));
        }

    }

    private static void connectLoopActivityToGateway(Petrinet petrinet, Place[] places1, Gateway targetGateway,
                                                     Map<Gateway, Place> xorGatewayPlaceMap, Map<Gateway, Transition> andGatewayPlaceMap, int[] counter) {
        if (targetGateway.getGatewayType().equals(Gateway.GatewayType.DATABASED) || targetGateway.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
            connectPlaceToPlace(petrinet, places1[1], xorGatewayPlaceMap.get(targetGateway), counter);
        } else if (targetGateway.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
            connectPlaceToTransition(petrinet, places1[1], andGatewayPlaceMap.get(targetGateway));
        }
    }

    private static void connectLoopActivityToEvent(Petrinet petrinet, Place[] places1, Event targetEvent, Map<Event, Place> eventPlaceMap, int[] counter) {

        connectLoopActivityToPlace(petrinet, places1, eventPlaceMap.get(targetEvent), counter);

    }

    private static void connectLoopActivityToPlace(Petrinet petrinet, Place[] places1, Place targetPlace, int[] counter) {

        connectPlaceToPlace(petrinet, places1[1], targetPlace, counter);

    }

    private static void connectGatewayTo(Petrinet petrinet, Gateway sourceGateway, Event targetEvent, Gateway targetGateway, Activity targetActivity,
                                         SubProcess targetSubProcess, Map<Event, Place> eventPlaceMap, Map<Gateway, Place> xorGatewayPlaceMap, Map<Gateway, Transition> andGatewayPlaceMap,
                                         Map<Activity, Transition> activityTransitionMap, Map<Activity, Place[]> loopActivityMap, Map<SubProcess, List<Place>[]> subProcessPlaceMap, int[] counter) {

        if (sourceGateway.getGatewayType().equals(Gateway.GatewayType.DATABASED) || sourceGateway.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
            if (targetEvent != null) {
                connectXORGatewayToEvent(petrinet, sourceGateway, targetEvent, xorGatewayPlaceMap, eventPlaceMap, counter);
            } else if (targetGateway != null) {
                connectXORGatewayToGateway(petrinet, sourceGateway, targetGateway, xorGatewayPlaceMap, andGatewayPlaceMap, counter);
            } else if (targetActivity != null) {
                connectXORGatewayToActivity(petrinet, sourceGateway, targetActivity, xorGatewayPlaceMap, activityTransitionMap, loopActivityMap, counter);
            } else if (targetSubProcess != null) {
                for (Place start : subProcessPlaceMap.get(targetSubProcess)[0]) {
                    connectPlaceToPlace(petrinet, xorGatewayPlaceMap.get(sourceGateway), start, counter);
                }
            }
        } else if (sourceGateway.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
            if (targetEvent != null) {
                connectANDGatewayToEvent(petrinet, sourceGateway, targetEvent, andGatewayPlaceMap, eventPlaceMap);
            } else if (targetGateway != null) {
                connectANDGatewayToGateway(petrinet, sourceGateway, targetGateway, andGatewayPlaceMap, xorGatewayPlaceMap, counter);
            } else if (targetActivity != null) {
                connectANDGatewayToActivity(petrinet, sourceGateway, targetActivity, andGatewayPlaceMap, activityTransitionMap, loopActivityMap, counter);
            } else if (targetSubProcess != null) {
                for (Place start : subProcessPlaceMap.get(targetSubProcess)[0]) {
                    connectTransitionToPlace(petrinet, andGatewayPlaceMap.get(sourceGateway), start);
                }
            }
        }

    }

    private static void connectEventTo(Petrinet petrinet, Event sourceEvent, Event targetEvent, Gateway targetGateway, Activity targetActivity, SubProcess targetSubProcess,
                                       Map<Event, Place> eventPlaceMap, Map<Gateway, Place> xorGatewayPlaceMap, Map<Gateway, Transition> andGatewayPlaceMap,
                                       Map<Activity, Transition> activityTransitionMap, Map<Activity, Place[]> loopActivityMap, Map<SubProcess, List<Place>[]> subProcessPlaceMap, int[] counter) {
        if (targetEvent != null) {
            connectEventToEvent(petrinet, sourceEvent, targetEvent, eventPlaceMap, counter);
        } else if (targetGateway != null) {
            connectEventToGateway(petrinet, sourceEvent, targetGateway, eventPlaceMap, xorGatewayPlaceMap, andGatewayPlaceMap, counter);
        } else if (targetActivity != null) {
            connectEventToActivity(petrinet, sourceEvent, targetActivity, eventPlaceMap, activityTransitionMap, loopActivityMap, counter);
        } else if (targetSubProcess != null) {
            for (Place start : subProcessPlaceMap.get(targetSubProcess)[0]) {
                connectPlaceToPlace(petrinet, eventPlaceMap.get(sourceEvent), start, counter);
            }
        }
    }

    private static void connectANDGatewayToActivity(Petrinet petrinet, Gateway sourceGateway, Activity targetActivity,
                                                    Map<Gateway, Transition> andGatewayPlaceMap, Map<Activity, Transition> activityTransitionMap, Map<Activity, Place[]> loopActivityMap, int[] counter) {

        Place[] places;
        if ((places = loopActivityMap.get(targetActivity)) != null) {
            connectTransitionToPlace(petrinet, andGatewayPlaceMap.get(sourceGateway), places[0]);
        } else {
            connectTransitionToTransition(petrinet, andGatewayPlaceMap.get(sourceGateway), activityTransitionMap.get(targetActivity), counter);
        }

    }

    private static void connectANDGatewayToGateway(Petrinet petrinet, Gateway sourceGateway, Gateway targetGateway,
                                                   Map<Gateway, Transition> andGatewayPlaceMap, Map<Gateway, Place> xorGatewayPlaceMap, int[] counter) {

        if (targetGateway.getGatewayType().equals(Gateway.GatewayType.DATABASED) || targetGateway.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
            connectTransitionToPlace(petrinet, andGatewayPlaceMap.get(sourceGateway), xorGatewayPlaceMap.get(targetGateway));
        } else if (targetGateway.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
            connectTransitionToTransition(petrinet, andGatewayPlaceMap.get(sourceGateway), andGatewayPlaceMap.get(targetGateway), counter);
        }

    }

    private static void connectANDGatewayToEvent(Petrinet petrinet, Gateway sourceGateway, Event targetEvent, Map<Gateway, Transition> andGatewayPlaceMap, Map<Event, Place> eventPlaceMap) {
        connectTransitionToPlace(petrinet, andGatewayPlaceMap.get(sourceGateway), eventPlaceMap.get(targetEvent));
    }

    private static void connectXORGatewayToActivity(Petrinet petrinet, Gateway sourceGateway, Activity targetActivity,
                                                    Map<Gateway, Place> xorGatewayPlaceMap, Map<Activity, Transition> activityTransitionMap, Map<Activity, Place[]> loopActivityMap, int[] counter) {
        Place[] places;
        if ((places = loopActivityMap.get(targetActivity)) != null) {
            connectPlaceToPlace(petrinet, xorGatewayPlaceMap.get(sourceGateway), places[0], counter);
        } else {
            connectPlaceToTransition(petrinet, xorGatewayPlaceMap.get(sourceGateway), activityTransitionMap.get(targetActivity));
        }
    }

    private static void connectXORGatewayToGateway(Petrinet petrinet, Gateway sourceGateway, Gateway targetGateway,
                                                   Map<Gateway, Place> xorGatewayPlaceMap, Map<Gateway, Transition> andGatewayPlaceMap, int[] counter) {

        if (targetGateway.getGatewayType().equals(Gateway.GatewayType.DATABASED) || targetGateway.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
            connectPlaceToPlace(petrinet, xorGatewayPlaceMap.get(sourceGateway), xorGatewayPlaceMap.get(targetGateway), counter);
        } else if (targetGateway.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
            connectPlaceToTransition(petrinet, xorGatewayPlaceMap.get(sourceGateway), andGatewayPlaceMap.get(targetGateway));
        }

    }

    private static void connectXORGatewayToEvent(Petrinet petrinet, Gateway sourceGateway, Event targetEvent,
                                                 Map<Gateway, Place> xorGatewayPlaceMap, Map<Event, Place> eventPlaceMap, int[] counter) {

        connectPlaceToPlace(petrinet, xorGatewayPlaceMap.get(sourceGateway), eventPlaceMap.get(targetEvent), counter);

    }

    private static void connectPlaceToActivity(Petrinet petrinet, Place sourcePlace, Activity targetActivity,
                                               Map<Activity, Transition> activityTransitionMap, Map<Activity, Place[]> loopActivityMap, int[] counter) {

        Place[] places;
        if ((places = loopActivityMap.get(targetActivity)) != null) {
            connectPlaceToPlace(petrinet, sourcePlace, places[0], counter);
        } else {
            connectPlaceToTransition(petrinet, sourcePlace, activityTransitionMap.get(targetActivity));
        }

    }

    private static void connectEventToActivity(Petrinet petrinet, Event sourceEvent, Activity targetActivity,
                                               Map<Event, Place> eventPlaceMap, Map<Activity, Transition> activityTransitionMap, Map<Activity, Place[]> loopActivityMap, int[] counter) {

        Place[] places;
        if ((places = loopActivityMap.get(targetActivity)) != null) {
            connectPlaceToPlace(petrinet, eventPlaceMap.get(sourceEvent), places[0], counter);
        } else {
            connectPlaceToTransition(petrinet, eventPlaceMap.get(sourceEvent), activityTransitionMap.get(targetActivity));
        }

    }

    private static void connectEventToGateway(Petrinet petrinet, Event sourceEvent, Gateway targetGateway, Map<Event, Place> eventPlaceMap,
                                              Map<Gateway, Place> xorGatewayPlaceMap, Map<Gateway, Transition> andGatewayPlaceMap, int[] counter) {

        if (targetGateway.getGatewayType().equals(Gateway.GatewayType.DATABASED) || targetGateway.getGatewayType().equals(Gateway.GatewayType.INCLUSIVE)) {
            connectPlaceToPlace(petrinet, eventPlaceMap.get(sourceEvent), xorGatewayPlaceMap.get(targetGateway), counter);
        } else if (targetGateway.getGatewayType().equals(Gateway.GatewayType.PARALLEL)) {
            connectPlaceToTransition(petrinet, eventPlaceMap.get(sourceEvent), andGatewayPlaceMap.get(targetGateway));
        }

    }

    private static void connectEventToEvent(Petrinet petrinet, Event sourceEvent, Event targetEvent, Map<Event, Place> eventPlaceMap, int[] counter) {

        connectPlaceToPlace(petrinet, eventPlaceMap.get(sourceEvent), eventPlaceMap.get(targetEvent), counter);

    }

    private static List<Place> getTransitionSuccessors(Petrinet net, Transition transition) {
        List<Place> predecessors = new ArrayList<Place>();
        for (Object node : getSuccessors(net, transition)) {
            predecessors.add((Place) node);
        }
        return predecessors;
    }

    private static List<Transition> getPlaceSuccessors(Petrinet net, Place place) {
        List<Transition> predecessors = new ArrayList<Transition>();
        for (Object node : getSuccessors(net, place)) {
            predecessors.add((Transition) node);
        }
        return predecessors;
    }

    private static List getSuccessors(Petrinet net, PetrinetNode node) {
        List successors = new ArrayList();
        DirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> graph = (DirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>>) net.getGraph();
        for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : graph.getEdges()) {
            if (edge.getSource().equals(node)) {
                successors.add(edge.getTarget());
            }
        }
        return successors;
    }

    private static List<Place> getTransitionPredecessors(Petrinet net, Transition transition) {
        List<Place> predecessors = new ArrayList<Place>();
        for (Object node : getPredecessors(net, transition)) {
            predecessors.add((Place) node);
        }
        return predecessors;
    }

    private static List<Transition> getPlacePredecessors(Petrinet net, Place place) {
        List<Transition> predecessors = new ArrayList<Transition>();
        for (Object node : getPredecessors(net, place)) {
            predecessors.add((Transition) node);
        }
        return predecessors;
    }

    private static List getPredecessors(Petrinet net, PetrinetNode node) {
        List successors = new ArrayList();
        DirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> graph = (DirectedGraph<PetrinetNode, PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>>) net.getGraph();
        for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : graph.getEdges()) {
            if (edge.getTarget().equals(node)) {
                successors.add(edge.getSource());
            }
        }
        return successors;
    }
}
