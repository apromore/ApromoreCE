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

package com.raffaeleconforti.wrappers.impl.heuristics;

import com.raffaeleconforti.conversion.petrinet.PetriNetToBPMNConverter;
import com.raffaeleconforti.heuristicsdollarminer.HeuristicsDollarMiner;
import com.raffaeleconforti.log.util.LogImporter;
import com.raffaeleconforti.log.util.LogReaderClassic;
import com.raffaeleconforti.marking.MarkingDiscoverer;
import com.raffaeleconforti.wrappers.LogPreprocessing;
import com.raffaeleconforti.wrappers.MiningAlgorithm;
import com.raffaeleconforti.wrappers.PetrinetWithMarking;
import com.raffaeleconforti.wrappers.settings.MiningSettings;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.log.LogFile;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.processtree.ProcessTree;

/**
 * Created by conforti on 20/02/15.
 */
@Plugin(name = "Heuristics Dollar Algorithm Wrapper", parameterLabels = {"Log"},
        returnLabels = {"PetrinetWithMarking"},
        returnTypes = {PetrinetWithMarking.class})
public class HeuristicsDollarAlgorithmWrapper implements MiningAlgorithm {

    @UITopiaVariant(affiliation = UITopiaVariant.EHV,
            author = "Raffaele Conforti",
            email = "raffaele.conforti@unimelb.edu.au",
            pack = "Noise Filtering")
    @PluginVariant(variantLabel = "Heuristics Dollar Algorithm Wrapper", requiredParameterLabels = {0})
    public PetrinetWithMarking minePetrinet(UIPluginContext context, XLog log) {
        return minePetrinet(context, log, false, null, new XEventNameClassifier());
    }

    @Override
    public boolean canMineProcessTree() {
        return false;
    }

    @Override
    public ProcessTree mineProcessTree(UIPluginContext context, XLog log, boolean structure, MiningSettings params, XEventClassifier xEventClassifier) {
        return null;
    }

    @Override
    public PetrinetWithMarking minePetrinet(UIPluginContext context, XLog log, boolean structure, MiningSettings params, XEventClassifier xEventClassifier) {
        LogPreprocessing logPreprocessing = new LogPreprocessing();
        log = logPreprocessing.preprocessLog(context, log);

        try {
            LogImporter.exportToFile("", "tmpLog.mxml.gz", log);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogFile lf = LogFile.getInstance("tmpLog.mxml.gz");
        PetriNet result = new HeuristicsDollarMiner().mine((LogReaderClassic) LogReaderClassic.createInstance(null, lf));
        Petrinet petrinet = getPetrinet(result);
        logPreprocessing.removedAddedElements(petrinet);

        return new PetrinetWithMarking(petrinet, MarkingDiscoverer.constructInitialMarking(context, petrinet), MarkingDiscoverer.constructFinalMarking(context, petrinet));
    }

    @Override
    public BPMNDiagram mineBPMNDiagram(UIPluginContext context, XLog log, boolean structure, MiningSettings params, XEventClassifier xEventClassifier) {
        PetrinetWithMarking petrinetWithMarking = minePetrinet(context, log, structure, params, xEventClassifier);
        return PetriNetToBPMNConverter.convert(petrinetWithMarking.getPetrinet(), petrinetWithMarking.getInitialMarking(), petrinetWithMarking.getFinalMarking(), true);
    }

    private Petrinet getPetrinet(PetriNet result) {
        Petrinet petrinet = new PetrinetImpl("Alpha Dollar");
        UnifiedMap<org.processmining.framework.models.petrinet.Transition, Transition> transitionUnifiedMap = new UnifiedMap<>();
        UnifiedMap<org.processmining.framework.models.petrinet.Place, Place> placeUnifiedMap = new UnifiedMap<>();

        for(org.processmining.framework.models.petrinet.Transition t : result.getTransitions()) {
            Transition transition = petrinet.addTransition(t.getLogEvent().getModelElementName());
            transition.setInvisible(t.isInvisibleTask());
            transitionUnifiedMap.put(t, transition);
        }

        for(org.processmining.framework.models.petrinet.Place p : result.getPlaces()) {
            Place place = petrinet.addPlace(p.getName());
            placeUnifiedMap.put(p, place);
        }

        for(org.processmining.framework.models.petrinet.Transition t : result.getTransitions()) {
            for(org.processmining.framework.models.petrinet.Place p : result.getPlaces()) {
                Transition transition = transitionUnifiedMap.get(t);
                Place place = placeUnifiedMap.get(p);
                if(result.findEdge(t, p) != null) {
                    petrinet.addArc(transition, place);
                }
                if(result.findEdge(p, t) != null) {
                    petrinet.addArc(place, transition);
                }
            }
        }

        return petrinet;
    }

    @Override
    public String getAlgorithmName() {
        return "Heuristics Dollar";
    }

    @Override
    public String getAcronym() { return "HM$";}
}
