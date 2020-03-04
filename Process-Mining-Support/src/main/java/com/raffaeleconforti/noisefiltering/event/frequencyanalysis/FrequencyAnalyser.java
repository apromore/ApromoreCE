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

package com.raffaeleconforti.noisefiltering.event.frequencyanalysis;

import nl.tue.astar.AStarException;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
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
 * Created by conforti on 13/01/2016.
 */
public class FrequencyAnalyser {

    private Petrinet petrinet = null;
    private XLog log = null;
    private UIPluginContext context = null;
    private final UnifiedMap<String, Integer> frequency = new UnifiedMap<>();
    private final XEventClassifier xEventClassifier;
    private int max = 0;

    public FrequencyAnalyser(Petrinet petrinet, XLog log, XEventClassifier xEventClassifier, UIPluginContext context) {
        this.petrinet = petrinet;
        this.log = log;
        this.context = context;
        this.xEventClassifier = xEventClassifier;
    }

    public int getFrequency(String activity) {
        Integer freq = frequency.get(activity);
        if(freq == null) return 0;
        return freq;
    }

    public int getMax() {
        return max;
    }

    public void analyse() {
        PetrinetReplayerWithILP replayer = new PetrinetReplayerWithILP();
//        AStarPlugin replayer = new AStarPlugin();

        Map<Transition, Integer> transitions2costs = new UnifiedMap<>();
        for(Transition t : petrinet.getTransitions()) {
            if(t.isInvisible()) {
                transitions2costs.put(t, 0);
            }else {
                transitions2costs.put(t, 100000000);
            }
        }

        XEventClass dummyEvClass = new XEventClass("DUMMY",99999);

        Map<XEventClass, Integer> events2costs = constructMOTCostFunction(log, dummyEvClass);
        IPNReplayParameter parameters = new CostBasedCompleteParam(events2costs, transitions2costs);

        Marking initialMarking = new Marking();
        Marking finalMarking = new Marking();
        for(Place p : petrinet.getPlaces()) {
            boolean source = true;
            for(PetrinetEdge edge : petrinet.getEdges()) {
                if (edge.getTarget().equals(p)) {
                    source = false;
                    break;
                }
            }
            if(source) {
                initialMarking.add(p);
                break;
            }
        }

        for(Place p : petrinet.getPlaces()) {
            boolean sink = true;
            for(PetrinetEdge edge : petrinet.getEdges()) {
                if (edge.getSource().equals(p)) {
                    sink = false;
                    break;
                }
            }
            if(sink) {
                finalMarking.add(p);
                break;
            }
        }

        parameters.setInitialMarking(initialMarking);
        parameters.setFinalMarkings(finalMarking);
        parameters.setGUIMode(false);
        parameters.setCreateConn(false);
        ((CostBasedCompleteParam) parameters).setMaxNumOfStates(Integer.MAX_VALUE);
        PNRepResult replayResults = null;

        XConceptExtension xce = XConceptExtension.instance();
        try {
            replayResults = replayer.replayLog(context, petrinet, log, constructMapping(petrinet, log, dummyEvClass), parameters);
        } catch (AStarException e) {
            e.printStackTrace();
        }

        for (SyncReplayResult replayResult : replayResults) {
            List<Object> nodeInstance = replayResult.getNodeInstance();
            List<StepTypes> stepTypes = replayResult.getStepTypes();

            for (Integer tracePos : replayResult.getTraceIndex()) {
                int pos = 0;

                for (int i = 0; i < nodeInstance.size(); i++) {
                    StepTypes type = stepTypes.get(i);
                    if (type == StepTypes.LMGOOD) {
                        XEvent e = log.get(tracePos).get(pos);
                        String label = xce.extractName(e);
                        if(label.contains("#")) {
                            Integer d = frequency.get(label);
                            if (d == null) d = 0;
                            d += 1;
                            frequency.put(label, d);
                            if (d > max) max = d;
                        }
                        pos++;
                    } else if (type == StepTypes.L) {
                        pos++;
                    }
                }
            }
        }
    }

    private Map<XEventClass, Integer> constructMOTCostFunction(XLog log, XEventClass dummyEvClass) {
        Map<XEventClass,Integer> costMOT = new UnifiedMap<>();
        XLogInfo summary = XLogInfoFactory.createLogInfo(log, xEventClassifier);

        for (XEventClass evClass : summary.getEventClasses().getClasses()) {
            costMOT.put(evClass, 1);
        }

        costMOT.put(dummyEvClass, 1);

        return costMOT;
    }

    private TransEvClassMapping constructMapping(Petrinet net, XLog log, XEventClass dummyEvClass) {
        TransEvClassMapping mapping = new TransEvClassMapping(xEventClassifier, dummyEvClass);

        XLogInfo summary = XLogInfoFactory.createLogInfo(log, xEventClassifier);

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
