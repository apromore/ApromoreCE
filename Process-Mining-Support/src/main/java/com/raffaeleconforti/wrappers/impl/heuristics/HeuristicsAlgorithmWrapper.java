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

import com.raffaeleconforti.context.FakePluginContext;
import com.raffaeleconforti.conversion.petrinet.PetriNetToBPMNConverter;
import com.raffaeleconforti.marking.MarkingDiscoverer;
import com.raffaeleconforti.wrappers.LogPreprocessing;
import com.raffaeleconforti.wrappers.MiningAlgorithm;
import com.raffaeleconforti.wrappers.PetrinetWithMarking;
import com.raffaeleconforti.wrappers.settings.MiningSettings;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.heuristics.HeuristicsNet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.heuristicsnet.miner.heuristics.converter.HeuristicsNetToPetriNetConverter;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.FlexibleHeuristicsMinerPlugin;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.gui.ParametersPanel;
import org.processmining.plugins.heuristicsnet.miner.heuristics.miner.settings.HeuristicsMinerSettings;
import org.processmining.processtree.ProcessTree;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by conforti on 20/02/15.
 */
@Plugin(name = "Heuristics Miner Wrapper", parameterLabels = {"Log"},
        returnLabels = {"PetrinetWithMarking"},
        returnTypes = {PetrinetWithMarking.class})
public class HeuristicsAlgorithmWrapper implements MiningAlgorithm {

    HeuristicsMinerSettings settings;

    @UITopiaVariant(affiliation = UITopiaVariant.EHV,
            author = "Raffaele Conforti",
            email = "raffaele.conforti@unimelb.edu.au",
            pack = "Noise Filtering")
    @PluginVariant(variantLabel = "Heuristics Miner Wrapper", requiredParameterLabels = {0})
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

        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {}
        }));

        if(context instanceof FakePluginContext) {
            Collection<XEventClassifier> classifiers = new HashSet();
            classifiers.add(xEventClassifier);
            ParametersPanel parameters = new ParametersPanel(classifiers);
            settings = parameters.getSettings();
        }else {
            Collection<XEventClassifier> classifiers = new HashSet();
            classifiers.add(xEventClassifier);
            ParametersPanel parameters = new ParametersPanel(classifiers);
            parameters.removeAndThreshold();

            context.showConfiguration("Heuristics Miner Parameters", parameters);
            settings = parameters.getSettings();
        }

        if( params != null ) {
            if( params.containsParam("dependencyThresholdHM6") && params.getParam("dependencyThresholdHM6") instanceof Double )
            settings.setDependencyThreshold((Double) params.getParam("dependencyThresholdHM6"));

            if( params.containsParam("L1lThresholdHM6") && params.getParam("L1lThresholdHM6") instanceof Double )
            settings.setL1lThreshold((Double) params.getParam("L1lThresholdHM6"));

            if( params.containsParam("L2lThresholdHM6") && params.getParam("L2lThresholdHM6") instanceof Double )
            settings.setL2lThreshold((Double) params.getParam("L2lThresholdHM6"));

            if( params.containsParam("longDepThresholdHM6") && params.getParam("longDepThresholdHM6") instanceof Double )
            settings.setLongDistanceThreshold((Double) params.getParam("longDepThresholdHM6"));

            if( params.containsParam("relativeToBestThresholdHM6") && params.getParam("relativeToBestThresholdHM6") instanceof Double )
            settings.setRelativeToBestThreshold((Double) params.getParam("relativeToBestThresholdHM6"));

            if( params.containsParam("allConnectedHM6") && params.getParam("allConnectedHM6") instanceof Boolean )
            settings.setUseAllConnectedHeuristics((Boolean) params.getParam("allConnectedHM6"));

            if( params.containsParam("longDependencyHM6") && params.getParam("longDependencyHM6") instanceof Boolean )
            settings.setUseLongDistanceDependency((Boolean) params.getParam("longDependencyHM6"));
        }

        HeuristicsNet heuristicsNet = FlexibleHeuristicsMinerPlugin.run(context, log, settings);
        Object[] result = HeuristicsNetToPetriNetConverter.converter(context, heuristicsNet);
        logPreprocessing.removedAddedElements((Petrinet) result[0]);

        if(result[1] == null) result[1] = MarkingDiscoverer.constructInitialMarking(context, (Petrinet) result[0]);
        else MarkingDiscoverer.createInitialMarkingConnection(context, (Petrinet) result[0], (Marking) result[1]);

        Marking finalMarking = MarkingDiscoverer.constructFinalMarking(context, (Petrinet) result[0]);
        Set<Marking> finalMarkings = MarkingDiscoverer.constructFinalMarkings(context, (Petrinet) result[0]);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        if(finalMarkings.size() > 1) {
            return new PetrinetWithMarking((Petrinet) result[0], (Marking) result[1], finalMarkings);
        }else {
            return new PetrinetWithMarking((Petrinet) result[0], (Marking) result[1], finalMarking);
        }
    }

    @Override
    public BPMNDiagram mineBPMNDiagram(UIPluginContext context, XLog log, boolean structure, MiningSettings params, XEventClassifier xEventClassifier) {
        PetrinetWithMarking petrinetWithMarking = minePetrinet(context, log, structure, params, xEventClassifier);
        return PetriNetToBPMNConverter.convert(petrinetWithMarking.getPetrinet(), petrinetWithMarking.getInitialMarking(), petrinetWithMarking.getFinalMarking(), true);
    }

    @Override
    public String getAlgorithmName() {
        return "Heuristics Miner ProM6";
    }

    @Override
    public String getAcronym() { return "HM6";}

}
