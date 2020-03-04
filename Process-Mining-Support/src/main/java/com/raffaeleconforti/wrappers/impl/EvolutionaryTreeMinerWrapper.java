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
import com.raffaeleconforti.marking.MarkingDiscoverer;
import com.raffaeleconforti.wrappers.LogPreprocessing;
import com.raffaeleconforti.wrappers.MiningAlgorithm;
import com.raffaeleconforti.wrappers.PetrinetWithMarking;
import com.raffaeleconforti.wrappers.settings.MiningSettings;
import nl.tue.astar.AStarThread.Canceller;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.plugins.etm.CentralRegistry;
import org.processmining.plugins.etm.ETM;
import org.processmining.plugins.etm.factory.TreeFactoryAbstract;
import org.processmining.plugins.etm.factory.TreeFactoryCoordinator;
import org.processmining.plugins.etm.fitness.metrics.*;
import org.processmining.plugins.etm.logging.EvolutionLogger;
import org.processmining.plugins.etm.model.narytree.NAryTree;
import org.processmining.plugins.etm.model.narytree.conversion.NAryTreeToProcessTree;
import org.processmining.plugins.etm.mutation.GuidedTreeMutationCoordinator;
import org.processmining.plugins.etm.mutation.TreeCrossover;
import org.processmining.plugins.etm.mutation.TreeMutationCoordinator;
import org.processmining.plugins.etm.mutation.mutators.*;
import org.processmining.plugins.etm.mutation.mutators.maikelvaneck.IntelligentTreeFactory;
import org.processmining.plugins.etm.mutation.mutators.maikelvaneck.MutateSingleNodeGuided;
import org.processmining.plugins.etm.mutation.mutators.maikelvaneck.ReplaceTreeBySequenceMutation;
import org.processmining.plugins.etm.parameters.ETMParam;
import org.processmining.plugins.etm.termination.ExternalTerminationCondition;
import org.processmining.plugins.etm.termination.ProMCancelTerminationCondition;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.conversion.ProcessTree2Petrinet;
import org.uncommons.maths.random.Probability;
import org.uncommonseditedbyjoosbuijs.watchmaker.framework.TerminationCondition;
import org.uncommonseditedbyjoosbuijs.watchmaker.framework.selection.SigmaScaling;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * Created by conforti on 9/02/2016.
 */
@Plugin(name = "Evolutionary Tree Miner Wrapper", parameterLabels = {"Log"},
        returnLabels = {"PetrinetWithMarking"},
        returnTypes = {PetrinetWithMarking.class})
public class EvolutionaryTreeMinerWrapper implements MiningAlgorithm {

    @UITopiaVariant(affiliation = UITopiaVariant.EHV,
            author = "Raffaele Conforti",
            email = "raffaele.conforti@unimelb.edu.au",
            pack = "Noise Filtering")
    @PluginVariant(variantLabel = "Evolutionary Tree Miner Wrapper", requiredParameterLabels = {0})
    public PetrinetWithMarking minePetrinet(UIPluginContext context, XLog log) {
        return minePetrinet(context, log, false, null, new XEventNameClassifier());
    }

    @Override
    public boolean canMineProcessTree() {
        return true;
    }

    @Override
    public ProcessTree mineProcessTree(UIPluginContext context, XLog log, boolean structure, MiningSettings params, XEventClassifier xEventClassifier) {
        LogPreprocessing logPreprocessing = new LogPreprocessing();
        log = logPreprocessing.preprocessLog(context, log);

//            System.setOut(new PrintStream(new OutputStream() {
//                @Override
//                public void write(int b) throws IOException {}
//            }));

        ProcessTree processTree;
//            if(context instanceof FakePluginContext) {
//                UIContext uiContext = new UIContext();
//                PluginManagerImpl.initialize(UIPluginContext.class);
//                uiContext.initialize();
//                PluginContext pluginContext = uiContext.getMainPluginContext();

        XEventClassifier classifier = xEventClassifier;

        Random rng = new Random(123456);
        CentralRegistry registry = new CentralRegistry(context, log, classifier, rng);

        ETMParam iParams = new ETMParam(registry, null, null, 20, 5);
        iParams.addTerminationCondition(new ExternalTerminationCondition());

//                params.setFactory(new SequenceFactory(registry));
        Map<TreeFactoryAbstract, Double> otherFactories = new HashMap<TreeFactoryAbstract, Double>();
        otherFactories.put(new IntelligentTreeFactory(registry), 0.05);
        iParams.setFactory(new TreeFactoryCoordinator(registry, 0.95, otherFactories));

        ArrayList evolutionObservers = new ArrayList();
        evolutionObservers.add(new EvolutionLogger(context,registry, false));
        iParams.setEvolutionObservers(evolutionObservers);

        Canceller canceller = ProMCancelTerminationCondition.buildDummyCanceller();

        FitnessReplay fr = new FitnessReplay(registry, canceller, 1D, -1.0D);
        PrecisionEscEdges pe = new PrecisionEscEdges(registry);
        Generalization ge = new Generalization(registry);
        SimplicityUselessNodes su = new SimplicityUselessNodes();
        LinkedHashMap weightedFitnessAlg = new LinkedHashMap();

        weightedFitnessAlg.put(fr, Double.valueOf(10));
        weightedFitnessAlg.put(pe, Double.valueOf(5));
        weightedFitnessAlg.put(ge, Double.valueOf(1));
        weightedFitnessAlg.put(su, Double.valueOf(1));

        OverallFitness of = new OverallFitness(registry, weightedFitnessAlg);
        iParams.setMaxThreads(Runtime.getRuntime().availableProcessors());
        iParams.setFitnessEvaluator(new MultiThreadedFitnessEvaluator(registry, of, iParams.getMaxThreads()));

        ArrayList evolutionaryOperators = new ArrayList();
        evolutionaryOperators.add(new TreeCrossover(1, new Probability(0.25D)));

        LinkedHashMap smartMutators = new LinkedHashMap();
        smartMutators.put(new MutateSingleNodeGuided(registry), Double.valueOf(0.25D));
        smartMutators.put(new InsertActivityGuided(registry), 1.);
        smartMutators.put(new MutateLeafClassGuided(registry), 1.);
        smartMutators.put(new MutateOperatorTypeGuided(registry), 1.);
        smartMutators.put(new RemoveActivityGuided(registry), 1.);

        LinkedHashMap dumbMutators = new LinkedHashMap();
        dumbMutators.put(new ReplaceTreeBySequenceMutation(registry), Double.valueOf(0.25)); //random tree creation
        dumbMutators.put(new AddNodeRandom(registry), Double.valueOf(1.0D)); //random node addition
        dumbMutators.put(new RemoveSubtreeRandom(registry), Double.valueOf(1.0D)); //random node removal
        dumbMutators.put(new MutateSingleNodeRandom(registry), Double.valueOf(1.0D)); //random node mutation
        dumbMutators.put(new NormalizationMutation(registry), Double.valueOf(0.1D)); //normalization
        dumbMutators.put(new RemoveUselessNode(registry), Double.valueOf(0.1D)); //useless node removal

        TreeMutationCoordinator dumbCoordinator = new TreeMutationCoordinator(dumbMutators, false);
        GuidedTreeMutationCoordinator smartCoordinator = new GuidedTreeMutationCoordinator(registry, 0.25D, true, smartMutators, dumbCoordinator);
        evolutionaryOperators.add(smartCoordinator);
        iParams.setEvolutionaryOperators(evolutionaryOperators);

        iParams.setSelectionStrategy(new SigmaScaling());
        iParams.addTerminationConditionMaxGen(1000);
        iParams.addTerminationConditionTargetFitness(1, iParams.getFitnessEvaluator().isNatural());

        iParams.addTerminationConditionMaxDuration(3600000); //1 hour

        ETM etm = new ETM(iParams);
        etm.run();
        List stopped = etm.getSatisfiedTerminationConditions();
        Iterator tree = stopped.iterator();

        while(tree.hasNext()) {
            TerminationCondition cond = (TerminationCondition)tree.next();
            System.out.println(cond.toString());
        }

        NAryTree tree1 = etm.getResult();
        processTree = NAryTreeToProcessTree.convert(iParams.getCentralRegistry().getEventClasses(), tree1, "Process tree discovered by the ETM algorithm");

        logPreprocessing.removedAddedElements(processTree);

        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

        return processTree;
    }

    @Override
    public PetrinetWithMarking minePetrinet(UIPluginContext context, XLog log, boolean structure, MiningSettings params, XEventClassifier xEventClassifier) {
        try {
            LogPreprocessing logPreprocessing = new LogPreprocessing();
            log = logPreprocessing.preprocessLog(context, log);

//            System.setOut(new PrintStream(new OutputStream() {
//                @Override
//                public void write(int b) throws IOException {}
//            }));

            ProcessTree processTree;
//            if(context instanceof FakePluginContext) {
//                UIContext uiContext = new UIContext();
//                PluginManagerImpl.initialize(UIPluginContext.class);
//                uiContext.initialize();
//                PluginContext pluginContext = uiContext.getMainPluginContext();

                XEventClassifier classifier = xEventClassifier;

                Random rng = new Random(123456);
                CentralRegistry registry = new CentralRegistry(context, log, classifier, rng);

                ETMParam iParams = new ETMParam(registry, null, null, 20, 5);
                iParams.addTerminationCondition(new ExternalTerminationCondition());

//                params.setFactory(new SequenceFactory(registry));
                Map<TreeFactoryAbstract, Double> otherFactories = new HashMap<TreeFactoryAbstract, Double>();
                otherFactories.put(new IntelligentTreeFactory(registry), 0.05);
                iParams.setFactory(new TreeFactoryCoordinator(registry, 0.95, otherFactories));

                ArrayList evolutionObservers = new ArrayList();
                evolutionObservers.add(new EvolutionLogger(context,registry, false));
                iParams.setEvolutionObservers(evolutionObservers);

                Canceller canceller = ProMCancelTerminationCondition.buildDummyCanceller();

                FitnessReplay fr = new FitnessReplay(registry, canceller, 1D, -1.0D);
                PrecisionEscEdges pe = new PrecisionEscEdges(registry);
                Generalization ge = new Generalization(registry);
                SimplicityUselessNodes su = new SimplicityUselessNodes();
                LinkedHashMap weightedFitnessAlg = new LinkedHashMap();

                weightedFitnessAlg.put(fr, Double.valueOf(10));
                weightedFitnessAlg.put(pe, Double.valueOf(5));
                weightedFitnessAlg.put(ge, Double.valueOf(1));
                weightedFitnessAlg.put(su, Double.valueOf(1));

                OverallFitness of = new OverallFitness(registry, weightedFitnessAlg);
                iParams.setMaxThreads(Runtime.getRuntime().availableProcessors());
                iParams.setFitnessEvaluator(new MultiThreadedFitnessEvaluator(registry, of, iParams.getMaxThreads()));

                ArrayList evolutionaryOperators = new ArrayList();
                evolutionaryOperators.add(new TreeCrossover(1, new Probability(0.25D)));

                LinkedHashMap smartMutators = new LinkedHashMap();
                smartMutators.put(new MutateSingleNodeGuided(registry), Double.valueOf(0.25D));
                smartMutators.put(new InsertActivityGuided(registry), 1.);
                smartMutators.put(new MutateLeafClassGuided(registry), 1.);
                smartMutators.put(new MutateOperatorTypeGuided(registry), 1.);
                smartMutators.put(new RemoveActivityGuided(registry), 1.);

                LinkedHashMap dumbMutators = new LinkedHashMap();
                dumbMutators.put(new ReplaceTreeBySequenceMutation(registry), Double.valueOf(0.25)); //random tree creation
                dumbMutators.put(new AddNodeRandom(registry), Double.valueOf(1.0D)); //random node addition
                dumbMutators.put(new RemoveSubtreeRandom(registry), Double.valueOf(1.0D)); //random node removal
                dumbMutators.put(new MutateSingleNodeRandom(registry), Double.valueOf(1.0D)); //random node mutation
                dumbMutators.put(new NormalizationMutation(registry), Double.valueOf(0.1D)); //normalization
                dumbMutators.put(new RemoveUselessNode(registry), Double.valueOf(0.1D)); //useless node removal

                TreeMutationCoordinator dumbCoordinator = new TreeMutationCoordinator(dumbMutators, false);
                GuidedTreeMutationCoordinator smartCoordinator = new GuidedTreeMutationCoordinator(registry, 0.25D, true, smartMutators, dumbCoordinator);
                evolutionaryOperators.add(smartCoordinator);
                iParams.setEvolutionaryOperators(evolutionaryOperators);

                iParams.setSelectionStrategy(new SigmaScaling());
                iParams.addTerminationConditionMaxGen(1000);
                iParams.addTerminationConditionTargetFitness(1, iParams.getFitnessEvaluator().isNatural());

                iParams.addTerminationConditionMaxDuration(3600000); //1 hour

                ETM etm = new ETM(iParams);
                etm.run();
                List stopped = etm.getSatisfiedTerminationConditions();
                Iterator tree = stopped.iterator();

                while(tree.hasNext()) {
                    TerminationCondition cond = (TerminationCondition)tree.next();
                    System.out.println(cond.toString());
                }

                NAryTree tree1 = etm.getResult();
                processTree = NAryTreeToProcessTree.convert(iParams.getCentralRegistry().getEventClasses(), tree1, "Process tree discovered by the ETM algorithm");
//            }else {;
//                ETMPlugin etmPlugin = new ETMPlugin();
//                processTree = etmPlugin.withoutSeed(context, log);
//            }

//            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
            ProcessTree2Petrinet.PetrinetWithMarkings petrinetWithMarkings = ProcessTree2Petrinet.convert(processTree, true);

            logPreprocessing.removedAddedElements(petrinetWithMarkings.petrinet);

            MarkingDiscoverer.createInitialMarkingConnection(context, petrinetWithMarkings.petrinet, petrinetWithMarkings.initialMarking);
            MarkingDiscoverer.createFinalMarkingConnection(context, petrinetWithMarkings.petrinet, petrinetWithMarkings.finalMarking);
            return new PetrinetWithMarking(petrinetWithMarkings.petrinet, petrinetWithMarkings.initialMarking, petrinetWithMarkings.finalMarking);
        } catch (ProcessTree2Petrinet.InvalidProcessTreeException e) {
            e.printStackTrace();
        } catch (ProcessTree2Petrinet.NotYetImplementedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public BPMNDiagram mineBPMNDiagram(UIPluginContext context, XLog log, boolean structure, MiningSettings params, XEventClassifier xEventClassifier) {
        PetrinetWithMarking petrinetWithMarking = minePetrinet(context, log, structure, params, xEventClassifier);
        return PetriNetToBPMNConverter.convert(petrinetWithMarking.getPetrinet(), petrinetWithMarking.getInitialMarking(), petrinetWithMarking.getFinalMarking(), true);
    }

    @Override
    public String getAlgorithmName() {
        return "Evolutionary Tree Miner";
    }

    @Override
    public String getAcronym() { return "ETM";}
}
