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

package com.raffaeleconforti.wrappers.impl;

import au.edu.qut.processmining.miners.splitminer.SplitMiner;
import au.edu.qut.processmining.miners.splitminer.ui.dfgp.DFGPUIResult;
import au.edu.qut.processmining.miners.splitminer.ui.miner.SplitMinerUIResult;
import au.edu.qut.promplugins.SplitMinerPlugin;
import com.raffaeleconforti.context.FakePluginContext;
import com.raffaeleconforti.conversion.bpmn.BPMNToPetriNetConverter;
import com.raffaeleconforti.conversion.petrinet.PetriNetToBPMNConverter;
import com.raffaeleconforti.marking.MarkingDiscoverer;
import com.raffaeleconforti.wrappers.MiningAlgorithm;
import com.raffaeleconforti.wrappers.PetrinetWithMarking;
import com.raffaeleconforti.wrappers.settings.MiningSettings;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.bpmn.plugins.BpmnExportPlugin;
import org.processmining.processtree.ProcessTree;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Created by Adriano on 17/01/2017.
 */
@Plugin(name = "Split Miner Wrapper",
        parameterLabels = {"Event Log"},
        returnLabels = {"PetrinetWithMarking"},
        returnTypes = {PetrinetWithMarking.class})
public class SplitMinerWrapper implements MiningAlgorithm {

    @UITopiaVariant(affiliation = UITopiaVariant.EHV,
            author = "Adriano Augusto",
            email = "adriano.august@ut.ee",
            pack = "bpmntk-osgi")
    @PluginVariant(variantLabel = "Split Miner Wrapper", requiredParameterLabels = {0})
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
        BPMNDiagram diagram = this.mineBPMNDiagram(context, log, structure, params, xEventClassifier);

        Object[] result = BPMNToPetriNetConverter.convert(diagram);

        if(result[1] == null) result[1] = PetriNetToBPMNConverter.guessInitialMarking((Petrinet) result[0]);
        if(result[2] == null) result[2] = PetriNetToBPMNConverter.guessFinalMarking((Petrinet) result[0]);

        if(result[1] == null) result[1] = MarkingDiscoverer.constructInitialMarking(context, (Petrinet) result[0]);
        else MarkingDiscoverer.createInitialMarkingConnection(context, (Petrinet) result[0], (Marking) result[1]);

        if(result[2] == null) result[2] = MarkingDiscoverer.constructFinalMarking(context, (Petrinet) result[0]);
        else MarkingDiscoverer.createFinalMarkingConnection(context, (Petrinet) result[0], (Marking) result[1]);
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        return new PetrinetWithMarking((Petrinet) result[0], (Marking) result[1], (Marking) result[2]);
    }

    public BPMNDiagram mineBPMNDiagram(UIPluginContext context, XLog log, boolean structure, MiningSettings params, XEventClassifier xEventClassifier) {
        BPMNDiagram output;
        Double eta = SplitMinerUIResult.FREQUENCY_THRESHOLD;
        Double epsilon = SplitMinerUIResult.PARALLELISMS_THRESHOLD;
        Boolean replaceORs = true;
        Boolean removeLoopActivities = false;
        Boolean parallelismsFirst = false;

        if( params != null ) {
            if( params.containsParam("epsilonSM") && params.getParam("epsilonSM") instanceof Double )
                epsilon = (Double) params.getParam("epsilonSM");
            if( params.containsParam("etaSM") && params.getParam("etaSM") instanceof Double )
                eta = (Double) params.getParam("etaSM");
            if( params.containsParam("replaceORsSM") && params.getParam("replaceORsSM") instanceof Boolean )
                replaceORs = (Boolean) params.getParam("replaceORsSM");
            if( params.containsParam("removeSelfLoops") && params.getParam("removeSelfLoops") instanceof Boolean )
                removeLoopActivities = (Boolean) params.getParam("removeSelfLoops");
            if (params.containsParam("parallelismsFirst") && params.getParam("parallelismsFirst") instanceof Boolean)
                removeLoopActivities = (Boolean) params.getParam("parallelismsFirst");
        }

        if(context instanceof FakePluginContext) {
            SplitMiner yam = new SplitMiner();
            output = yam.mineBPMNModel(log, xEventClassifier, eta, epsilon, DFGPUIResult.FilterType.WTH, parallelismsFirst, replaceORs, removeLoopActivities, SplitMinerUIResult.StructuringTime.NONE);
//            export(output, "log_"+System.currentTimeMillis());
        } else {
            output = SplitMinerPlugin.discoverBPMNModelWithSplitMiner(context, log);
        }
        return output;
    }

    @Override
    public String getAlgorithmName() {
        return "Split Miner";
    }

    @Override
    public String getAcronym() {
        return "SM";
    }

    private void export(BPMNDiagram diagram, String name) {
        BpmnExportPlugin bpmnExportPlugin = new BpmnExportPlugin();
        UIContext context = new UIContext();
        UIPluginContext uiPluginContext = context.getMainPluginContext();
        try {
            bpmnExportPlugin.export(uiPluginContext, diagram, new File(name + ".bpmn"));
        } catch (Exception e) { System.out.println("ERROR - impossible to export .bpmn result of split-miner"); }
    }
}
