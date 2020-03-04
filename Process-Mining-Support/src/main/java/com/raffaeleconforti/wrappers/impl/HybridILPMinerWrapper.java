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

import com.raffaeleconforti.conversion.petrinet.PetriNetToBPMNConverter;
import com.raffaeleconforti.wrappers.MiningAlgorithm;
import com.raffaeleconforti.wrappers.PetrinetWithMarking;
import com.raffaeleconforti.wrappers.settings.MiningSettings;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.causalactivitygraphcreator.algorithms.DiscoverCausalActivityGraphAlgorithm;
import org.processmining.causalactivitygraphcreator.parameters.DiscoverCausalActivityGraphParameters;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.hybridilpminer.parameters.*;
import org.processmining.hybridilpminer.plugins.HybridILPMinerPlugin;
import org.processmining.lpengines.interfaces.LPEngine;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.processtree.ProcessTree;

import java.util.HashSet;

/**
 * Created by Adriano on 7/12/2016.
 */
public class HybridILPMinerWrapper implements MiningAlgorithm {

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
        PetrinetWithMarking petrinet = null;

        try {
            Object result[];

            LPFilter lpFilter = new LPFilter();
            lpFilter.setFilterType(LPFilterType.NONE);

            XEventClassifier eventClassifier = xEventClassifier;

            DiscoveryStrategy discoveryStrategy = new DiscoveryStrategy(DiscoveryStrategyType.CAUSAL);
            DiscoverCausalActivityGraphParameters graphParams = new DiscoverCausalActivityGraphParameters(log);

            graphParams.setClassifier(eventClassifier);
            graphParams.setMiner("Midi");
            graphParams.setConcurrencyRatio(0);
            graphParams.setIncludeThreshold(0);
            graphParams.setZeroValue(0);
            graphParams.setShowClassifierPanel(false);

            discoveryStrategy.setCausalActivityGraphParameters(graphParams);

//            ConvertCausalActivityMatrixToCausalActivityGraphPlugin causalGraphPlugin = new ConvertCausalActivityMatrixToCausalActivityGraphPlugin();
//            CausalActivityMatrix matrix = new CausalActivityMatrixFactory().createCausalActivityMatrix();
//            matrix.init("causal-matrix", );
//            CausalActivityGraph causalActivityGraph = causalGraphPlugin.run(context, matrix, graphParams);

            DiscoverCausalActivityGraphAlgorithm algorithm = new DiscoverCausalActivityGraphAlgorithm();
            discoveryStrategy.setCausalActivityGraph(algorithm.apply(context, log, graphParams));

            HashSet<LPConstraintType> lpConstraints = new HashSet<LPConstraintType>();
            lpConstraints.add(LPConstraintType.NO_TRIVIAL_REGION);
            lpConstraints.add(LPConstraintType.THEORY_OF_REGIONS);

            XLogHybridILPMinerParametersImpl iParams = new XLogHybridILPMinerParametersImpl( context,
                                                                                            LPEngine.EngineType.LPSOLVE,
                                                                                            discoveryStrategy,
                                                                                            NetClass.PT_NET,
                                                                                            lpConstraints,
                                                                                            LPObjectiveType.WEIGHTED_ABSOLUTE_PARIKH,
                                                                                            LPVariableType.DUAL,
                                                                                            lpFilter,
                                                                                            true,
                                                                                            log,
                                                                                            eventClassifier);

            result = HybridILPMinerPlugin.applyParams(context, log, iParams);
            System.out.println("DEBUG - trying to set petrinet: " + result);
            if( (result[0] instanceof Petrinet) && (result[1] instanceof Marking) ) {
                petrinet = new PetrinetWithMarking( (Petrinet)result[0], (Marking)result[1], (Marking)result[1]);
            } else {
                System.out.println("ERROR - wrong parameter returned by the Hybrid ILP Miner");
            }

        } catch (Exception e) {
            System.out.print(e.getMessage());
            e.printStackTrace();
            System.out.println("ERROR - Hybrid ILP Miner failed");
            return petrinet;
        }

        return petrinet;
    }

    @Override
    public BPMNDiagram mineBPMNDiagram(UIPluginContext context, XLog log, boolean structure, MiningSettings params, XEventClassifier xEventClassifier) {
        PetrinetWithMarking petrinetWithMarking = minePetrinet(context, log, structure, params, xEventClassifier);
        return PetriNetToBPMNConverter.convert(petrinetWithMarking.getPetrinet(), petrinetWithMarking.getInitialMarking(), petrinetWithMarking.getFinalMarking(), true);
    }

    @Override
    public String getAlgorithmName() {
        return "Hybrid ILP Miner";
    }

    @Override
    public String getAcronym() { return "HILP";}
}
