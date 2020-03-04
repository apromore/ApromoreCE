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

package com.raffaeleconforti.wrappers.impl.alpha;

import com.raffaeleconforti.conversion.petrinet.PetriNetToBPMNConverter;
import com.raffaeleconforti.marking.MarkingDiscoverer;
import com.raffaeleconforti.wrappers.LogPreprocessing;
import com.raffaeleconforti.wrappers.MiningAlgorithm;
import com.raffaeleconforti.wrappers.PetrinetWithMarking;
import com.raffaeleconforti.wrappers.settings.MiningSettings;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.alphaminer.algorithms.AlphaMinerFactory;
import org.processmining.alphaminer.parameters.AlphaMinerParameters;
import org.processmining.alphaminer.parameters.AlphaVersion;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.processtree.ProcessTree;

/**
 * Created by conforti on 20/02/15.
 */
@Plugin(name = "Alpha Plus Wrapper", parameterLabels = {"Log"},
        returnLabels = {"PetrinetWithMarking"},
        returnTypes = {PetrinetWithMarking.class})
public class AlphaClassicWrapper implements MiningAlgorithm {

    @UITopiaVariant(affiliation = UITopiaVariant.EHV,
            author = "Raffaele Conforti",
            email = "raffaele.conforti@unimelb.edu.au",
            pack = "Noise Filtering")
    @PluginVariant(requiredParameterLabels = {0})
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

        // Call the miner
        AlphaMinerParameters parameters = new AlphaMinerParameters(AlphaVersion.CLASSIC);
        AlphaMinerFactory factory = new AlphaMinerFactory();
        Pair<Petrinet, Marking> pair = AlphaMinerFactory.createAlphaMiner(log, xEventClassifier, parameters).run();
        logPreprocessing.removedAddedElements(pair.getFirst());

        MarkingDiscoverer.createInitialMarkingConnection(context, pair.getFirst(), pair.getSecond());
        return new PetrinetWithMarking(pair.getFirst(), pair.getSecond(), MarkingDiscoverer.constructFinalMarking(context, pair.getFirst()));
    }

    @Override
    public BPMNDiagram mineBPMNDiagram(UIPluginContext context, XLog log, boolean structure, MiningSettings params, XEventClassifier xEventClassifier) {
        PetrinetWithMarking petrinetWithMarking = minePetrinet(context, log, structure, params, xEventClassifier);
        return PetriNetToBPMNConverter.convert(petrinetWithMarking.getPetrinet(), petrinetWithMarking.getInitialMarking(), petrinetWithMarking.getFinalMarking(), true);
    }

    @Override
    public String getAlgorithmName() {
        return "Alpha Miner Classic";
    }

    @Override
    public String getAcronym() { return "AM";}
}
