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

import com.raffaeleconforti.conversion.bpmn.BPMNToPetriNetConverter;
import com.raffaeleconforti.conversion.heuristicsnet.HNNetToBPMNConverter;
import com.raffaeleconforti.conversion.petrinet.PetriNetToBPMNConverter;
import com.raffaeleconforti.heuristicsminer.HeuristicsMiner;
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
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.log.LogFile;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.mining.heuristicsmining.HeuristicsNetResult;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.processtree.ProcessTree;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by conforti on 20/02/15.
 */
@Plugin(name = "Heuristics Miner 5.2 Wrapper", parameterLabels = {"Log"},
        returnLabels = {"PetrinetWithMarking"},
        returnTypes = {PetrinetWithMarking.class})
public class Heuristics52AlgorithmWrapper implements MiningAlgorithm {

    @UITopiaVariant(affiliation = UITopiaVariant.EHV,
            author = "Raffaele Conforti",
            email = "raffaele.conforti@unimelb.edu.au",
            pack = "Noise Filtering")
    @PluginVariant(variantLabel = "Heuristics Miner 5.2 Wrapper", requiredParameterLabels = {0})
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

        HeuristicsMiner miner = new HeuristicsMiner();

        try {
            LogImporter.exportToFile("", "tmpLog.mxml.gz", log);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogFile lf = LogFile.getInstance("tmpLog.mxml.gz");

        HeuristicsNetResult hNet = null;
        try {
            hNet = (HeuristicsNetResult) miner.mine(LogReaderClassic.createInstance(null, lf), false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        BPMNDiagram diagram = HNNetToBPMNConverter.convert(hNet.getHeuriticsNet());
        for(Activity activity : diagram.getActivities()) {
            if(activity.getLabel().contains("+unknown:")) {
                activity.getAttributeMap().put(AttributeMap.LABEL, activity.getLabel().replace("+unknown:", "+"));
            }
        }

        Object[] result = BPMNToPetriNetConverter.convert(diagram);
        logPreprocessing.removedAddedElements((Petrinet) result[0]);

        if(result[1] == null) result[1] = MarkingDiscoverer.constructInitialMarking(context, (Petrinet) result[0]);
        else MarkingDiscoverer.createInitialMarkingConnection(context, (Petrinet) result[0], (Marking) result[1]);

        if(result[2] == null) result[2] = MarkingDiscoverer.constructFinalMarking(context, (Petrinet) result[0]);
        else MarkingDiscoverer.createFinalMarkingConnection(context, (Petrinet) result[0], (Marking) result[1]);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        return new PetrinetWithMarking((Petrinet) result[0], (Marking) result[1], (Marking) result[2]);
    }

    @Override
    public BPMNDiagram mineBPMNDiagram(UIPluginContext context, XLog log, boolean structure, MiningSettings params, XEventClassifier xEventClassifier) {
        PetrinetWithMarking petrinetWithMarking = minePetrinet(context, log, structure, params, xEventClassifier);
        return PetriNetToBPMNConverter.convert(petrinetWithMarking.getPetrinet(), petrinetWithMarking.getInitialMarking(), petrinetWithMarking.getFinalMarking(), true);
    }

    @Override
    public String getAlgorithmName() {
        return "Heuristics Miner ProM5.2";
    }

    @Override
    public String getAcronym() { return "HM";}
}
