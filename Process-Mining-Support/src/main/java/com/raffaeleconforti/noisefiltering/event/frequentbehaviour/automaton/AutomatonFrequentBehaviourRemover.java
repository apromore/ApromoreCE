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

package com.raffaeleconforti.noisefiltering.event.frequentbehaviour.automaton;

import com.raffaeleconforti.automaton.Automaton;
import nl.tue.astar.AStarException;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.alignment.plugin.AStarPlugin;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

import java.util.List;
import java.util.Map;

/**
 * Created by conforti on 14/02/15.
 */
public class AutomatonFrequentBehaviourRemover {

    private static final XFactory factory = new XFactoryNaiveImpl();

    public static XLog removeFrequentBehaviour(PluginContext context, XLog log, Automaton<String> automaton, boolean excludeTraces) {
        XLog result = filter(context, log, automaton, excludeTraces);

        return result;
    }

    public static XLog filter1(PluginContext context, XLog log, Automaton<String> automaton, String label) {
        PetrinetReplayerWithILP replayer = new PetrinetReplayerWithILP();
//        AStarPlugin replayer = new AStarPlugin();
        Petrinet petrinet = automaton.getPetrinet();

        Map<Transition, Integer> transitions2costs = new UnifiedMap<Transition, Integer>();
        for(Transition t : petrinet.getTransitions()) {
            if(t.isInvisible()) {
                transitions2costs.put(t, 0);
            }else {
                transitions2costs.put(t, 100000000);
            }
        }

        XEventClass dummyEvClass = new XEventClass("DUMMY",99999);
        XEventClassifier eventClassifier = XLogInfoImpl.NAME_CLASSIFIER;

        Map<XEventClass, Integer> events2costs = constructMOTCostFunction(log, eventClassifier, dummyEvClass);
        IPNReplayParameter parameters = new CostBasedCompleteParam(events2costs, transitions2costs);

        Marking initialMarking = new Marking();
        Marking finalMarking = new Marking();
        for(Place p : petrinet.getPlaces()) {
            if(p.getLabel().startsWith("source")) {
                initialMarking.add(p);
            }
            if(p.getLabel().startsWith("sink")) {
                finalMarking.add(p);
            }
        }

        parameters.setInitialMarking(initialMarking);
        parameters.setFinalMarkings(finalMarking);
        parameters.setGUIMode(false);
        parameters.setCreateConn(false);
        ((CostBasedCompleteParam) parameters).setMaxNumOfStates(Integer.MAX_VALUE);

        XLog sameBehaviurLog;
        XLog differentBehaviurLog;
        XLog sharedBehaviourLog;
        XLog finalLog;
        PNRepResult replayResults = null;

        XConceptExtension xce = XConceptExtension.instance();
        Map<String, String> map = new UnifiedMap<String, String>();

        try {
//            XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, eventClassifier);
//            TransEvClassMapping transEvClassMapping = constructMapping(petrinet, log, dummyEvClass, eventClassifier);
//            ReplayerParameters parameters1 = new ReplayerParameters.Default((int) Math.ceil(Runtime.getRuntime().availableProcessors() / 2), ReplayAlgorithm.Debug.NONE);
//            Replayer replayer = new Replayer(parameters1, petrinet, initialMarking, finalMarking, log, logInfo.getEventClasses(), transitions2costs, events2costs, transEvClassMapping);
//            replayResults = replayer.computePNRepResult(new Progress() {
//            });
            replayResults = replayer.replayLog(context, petrinet, log, constructMapping(petrinet, log, dummyEvClass, eventClassifier), parameters);
        } catch (AStarException e) {
            e.printStackTrace();
        }

        sameBehaviurLog = factory.createLog(log.getAttributes());
        differentBehaviurLog = factory.createLog(log.getAttributes());
        sharedBehaviourLog = factory.createLog(log.getAttributes());
        finalLog = factory.createLog(log.getAttributes());
        for (SyncReplayResult replayResult : replayResults) {
            List<Object> nodeInstance = replayResult.getNodeInstance();
            List<StepTypes> stepTypes = replayResult.getStepTypes();

            boolean tryAll = false;
            for (int i = 0; i < nodeInstance.size(); i++) {
                StepTypes type = stepTypes.get(i);
                if (type == StepTypes.MREAL) {
                    tryAll = true;
                    break;
                }
            }
            for (Integer tracePos : replayResult.getTraceIndex()) {
                int pos = 0;

                XTrace trace = factory.createTrace(log.get(tracePos).getAttributes());
                if (!tryAll) {
                    for (int i = 0; i < nodeInstance.size(); i++) {
                        StepTypes type = stepTypes.get(i);
                        if (type == StepTypes.LMGOOD) {
                            trace.add(log.get(tracePos).get(pos));
                            pos++;
                        } else if (type == StepTypes.L) {
                            XEvent event = factory.createEvent(log.get(tracePos).get(pos).getAttributes());
                            String name = xce.extractName(event);
                            String newname;
                            if((newname = map.get(name)) == null) {
                                newname = label + name;
                                map.put(name, newname);
                            }
                            xce.assignName(event, newname);
                            trace.add(event);
                            pos++;
                        }
                    }
                }
                if(trace.size() > 2) {
                    boolean save1 = false;
                    boolean save2 = false;
                    for(XEvent event : trace) {
                        if(!xce.extractName(event).startsWith(label)) {
                            save1 = true;
                        }else {
                            save2 = true;
                        }
                    }
                    if(save1 && !save2) {
                        sameBehaviurLog.add(trace);
                    }else if(!save1 && save2) {
                        differentBehaviurLog.add(trace);
                    }else if(save1 && save2) {
                        sharedBehaviourLog.add(trace);
                    }
                    finalLog.add(trace);
                }
            }
        }

//        if(sameBehaviurLog.size() > 0) {
//            context.getProvidedObjectManager().createProvidedObject("SameLog", sameBehaviurLog, XLog.class, context);
//            ((UIPluginContext) context).getGlobalContext().getResourceManager().getResourceForInstance(sameBehaviurLog).setFavorite(true);
//        }
//
//        if(differentBehaviurLog.size() > 0) {
//            context.getProvidedObjectManager().createProvidedObject("DifferentLog", differentBehaviurLog, XLog.class, context);
//            ((UIPluginContext) context).getGlobalContext().getResourceManager().getResourceForInstance(differentBehaviurLog).setFavorite(true);
//        }
//
//        if(sharedBehaviourLog.size() > 0) {
//            context.getProvidedObjectManager().createProvidedObject("SharedLog", sharedBehaviourLog, XLog.class, context);
//            ((UIPluginContext) context).getGlobalContext().getResourceManager().getResourceForInstance(sharedBehaviourLog).setFavorite(true);
//        }

        return finalLog;
    }

    private static XLog filter(PluginContext context, XLog log, Automaton<String> automaton, boolean excludeTraces) {
        PetrinetReplayerWithILP replayer = new PetrinetReplayerWithILP();
//        AStarPlugin replayer = new AStarPlugin();

        Petrinet petrinet = automaton.getPetrinet();

        Map<Transition, Integer> transitions2costs = new UnifiedMap<Transition, Integer>();
        for(Transition t : petrinet.getTransitions()) {
            if(t.isInvisible()) {
                transitions2costs.put(t, 0);
            }else {
                transitions2costs.put(t, 100000000);
            }
        }

        XEventClass dummyEvClass = new XEventClass("DUMMY",99999);
        XEventClassifier eventClassifier = XLogInfoImpl.NAME_CLASSIFIER;

        Map<XEventClass, Integer> events2costs = constructMOTCostFunction(log, eventClassifier, dummyEvClass);
        IPNReplayParameter parameters = new CostBasedCompleteParam(events2costs, transitions2costs);

        Marking initialMarking = new Marking();
        Marking finalMarking = new Marking();
        for(Place p : petrinet.getPlaces()) {
            if(p.getLabel().startsWith("source")) {
                initialMarking.add(p);
            }
            if(p.getLabel().startsWith("sink")) {
                finalMarking.add(p);
            }
        }

        parameters.setInitialMarking(initialMarking);
        parameters.setFinalMarkings(finalMarking);
        parameters.setGUIMode(false);
        parameters.setCreateConn(false);
        ((CostBasedCompleteParam) parameters).setMaxNumOfStates(Integer.MAX_VALUE);

        XLog finalLog = null;
        PNRepResult replayResults = null;

        boolean loop;
        do {
            loop = false;
            try {
//                System.out.println("Starting alignment...");
                replayResults = replayer.replayLog(context, petrinet, log, constructMapping(petrinet, log, dummyEvClass, eventClassifier), parameters);
//                System.out.println("Alignment completed");
            } catch (AStarException e) {
                e.printStackTrace();
            }

            finalLog = factory.createLog(log.getAttributes());
            for (SyncReplayResult replayResult : replayResults) {
                List<Object> nodeInstance = replayResult.getNodeInstance();
                List<StepTypes> stepTypes = replayResult.getStepTypes();

                boolean tryAll = false;
                for (int i = 0; i < nodeInstance.size(); i++) {
                    StepTypes type = stepTypes.get(i);
                    if (type == StepTypes.MREAL) {
                        tryAll = true;
                        break;
                    }
                }
                for (Integer tracePos : replayResult.getTraceIndex()) {
                    XTrace trace = factory.createTrace(log.get(tracePos).getAttributes());
                    int pos = 0;

                    if (!tryAll) {
//                        boolean skip = false;
//                        boolean firstSkip = true;
                        for (int i = 0; i < nodeInstance.size(); i++) {
                            StepTypes type = stepTypes.get(i);
//                            if (type == StepTypes.LMGOOD) {
//                                if (firstSkip && skip) {
//                                    firstSkip = false;
//                                    skip = false;
//                                } else {
//                                    trace.add(noisefiltering.get(tracePos).get(pos));
//                                }
//                                pos++;
//                            } else if (type != StepTypes.L && type != StepTypes.MINVI) {
//                                System.out.println("PROBLEM");
//                                skip = true;
//                                loop = true;
//                            } else if (type == StepTypes.L) {
//                                pos++;
//                            }
                            if (!(type == StepTypes.LMGOOD || type == StepTypes.MINVI)) {
                                for (XEvent event : log.get(tracePos)) {
                                    trace.add(event);
                                }
                                break;
                            }
                        }
                    }else {
                        if(!excludeTraces) {
                            for (XEvent event : log.get(tracePos)) {
                                trace.add(event);
                            }
                        }
                    }

                    if (trace.size() > 0) finalLog.add(trace);

                }
            }
            log = finalLog;
        }while (loop);

        return finalLog;
    }

    private static Map<XEventClass, Integer> constructMOTCostFunction(XLog log, XEventClassifier eventClassifier, XEventClass dummyEvClass) {
        Map<XEventClass,Integer> costMOT = new UnifiedMap<XEventClass,Integer>();
        XLogInfo summary = XLogInfoFactory.createLogInfo(log, eventClassifier);

        for (XEventClass evClass : summary.getEventClasses().getClasses()) {
            costMOT.put(evClass, 1);
        }

        costMOT.put(dummyEvClass, 1);

        return costMOT;
    }

    private static TransEvClassMapping constructMapping(Petrinet net, XLog log, XEventClass dummyEvClass, XEventClassifier eventClassifier) {
        TransEvClassMapping mapping = new TransEvClassMapping(eventClassifier, dummyEvClass);

        XLogInfo summary = XLogInfoFactory.createLogInfo(log,eventClassifier);

        for (Transition t : net.getTransitions()) {
            boolean mapped = false;

            for (XEventClass evClass : summary.getEventClasses().getClasses()) {
                String id = evClass.getId();

                if (t.getLabel().equals(id)) {
                    mapping.put(t, evClass);
                    mapped = true;
                    break;
                }
            }

            if (!mapped) {
                mapping.put(t, dummyEvClass);
            }

        }

        return mapping;
    }

}
