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

package com.raffaeleconforti.noisefiltering.event.infrequentbehaviour.automaton;

import com.raffaeleconforti.automaton.Automaton;
import com.raffaeleconforti.automaton.exception.HighThresholdException;
import nl.tue.astar.AStarException;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.processmining.alignment.plugin.AStarPlugin;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
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
public class AutomatonInfrequentBehaviourRemover {

    private static final XFactory factory = new XFactoryNaiveImpl();

    public static XLog removeInfrequentBehaviour(PluginContext context, XEventClassifier xEventClassifier, XLog log, Automaton<String> automaton, double lowerbound, double upperbound, double exception, boolean excludeTraces, boolean deviance) throws HighThresholdException {

        double originalEvents = countEvents(log);

        XLog result = filter(context, xEventClassifier, log, automaton, excludeTraces, deviance);
//        XLog result = filter(context, xEventClassifier, log, automaton, false);
        double resultEvents = countEvents(result);

        if (exception > 0.0) {
            if (resultEvents > 0.0) {
                if (resultEvents / originalEvents < exception && lowerbound < upperbound) {
                    System.out.println("error1");
                    throw new HighThresholdException();
                }
                return result;
            } else {
                throw new HighThresholdException();
            }
        }else {
            return result;
        }

    }

    private static int countEvents(XLog log) {
        int count = 0;
        for(XTrace trace : log) {
            if(trace.size() > 1) {
                count += trace.size();
            }
        }
        return count;
    }

    private static XLog filter(PluginContext context, XEventClassifier xEventClassifier, XLog log, Automaton<String> automaton, boolean excludeTraces, boolean deviance) {
        PetrinetReplayerWithILP replayer = new PetrinetReplayerWithILP();
//        AStarPlugin replayer = new AStarPlugin();

        Petrinet petrinet = automaton.getPetrinet();

        context.getProvidedObjectManager().createProvidedObject("Automaton", petrinet, Petrinet.class, context);

        Map<Transition, Integer> transitions2costs = new UnifiedMap<Transition, Integer>();
        for(Transition t : petrinet.getTransitions()) {
            if(t.isInvisible()) {
                transitions2costs.put(t, 0);
            }else {
                transitions2costs.put(t, 100000000);
            }
        }

        XEventClass dummyEvClass = new XEventClass("DUMMY",99999);
        XEventClassifier eventClassifier = xEventClassifier;

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
//                System.out.println(replayResults.getInfo());
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
                    if (type == StepTypes.MREAL || (deviance && type == StepTypes.L)) {
                        tryAll = true;
                        break;
                    }
                }
                for (Integer tracePos : replayResult.getTraceIndex()) {
                    XTrace trace = factory.createTrace(log.get(tracePos).getAttributes());
                    int pos = 0;

                    if (!tryAll) {
                        boolean skip = false;
                        boolean firstSkip = true;
                        for (int i = 0; i < nodeInstance.size(); i++) {
                            StepTypes type = stepTypes.get(i);
                            if (type == StepTypes.LMGOOD) {
                                if (firstSkip && skip) {
                                    firstSkip = false;
                                    skip = false;
                                } else {
                                    trace.add(log.get(tracePos).get(pos));
                                }
                                pos++;
                            } else if (type != StepTypes.L && type != StepTypes.MINVI) {
                                System.out.println("PROBLEM");
                                skip = true;
                                loop = true;
                            } else if (type == StepTypes.L) {
                                pos++;
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

        XLogInfo summary = XLogInfoFactory.createLogInfo(log, eventClassifier);

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
