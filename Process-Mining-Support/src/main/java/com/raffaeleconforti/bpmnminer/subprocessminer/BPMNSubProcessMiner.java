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

package com.raffaeleconforti.bpmnminer.subprocessminer;

import com.raffaeleconforti.bpmn.util.BPMNAnalizer;
import com.raffaeleconforti.bpmn.util.BPMNModifier;
import com.raffaeleconforti.bpmn.util.BPMNSimplifier;
import com.raffaeleconforti.bpmnminer.exception.ExecutionCancelledException;
import com.raffaeleconforti.bpmnminer.foreignkeynoicetollerant.ForeignKeySelector;
import com.raffaeleconforti.bpmnminer.subprocessminer.selection.SelectMinerResult;
import com.raffaeleconforti.datastructures.Hierarchy;
import com.raffaeleconforti.datastructures.Tree;
import com.raffaeleconforti.datastructures.conversion.Converter;
import com.raffaeleconforti.datastructures.exception.EmptyLogException;
import com.raffaeleconforti.foreignkeydiscovery.DatabaseCreator;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.Attribute;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.ConceptualModel;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.Entity;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.ForeignKey;
import com.raffaeleconforti.foreignkeydiscovery.databasestructure.PrimaryKey;
import com.raffaeleconforti.foreignkeydiscovery.grouping.Group;
import com.raffaeleconforti.foreignkeydiscovery.util.EntityNameExtractor;
import com.raffaeleconforti.foreignkeydiscovery.util.EntityPrimaryKeyConverter;
import com.raffaeleconforti.log.util.LogCloner;
import com.raffaeleconforti.log.util.LogModifier;
import com.raffaeleconforti.log.util.LogOptimizer;
import com.raffaeleconforti.logextractor.LogExtractor;
import com.raffaeleconforti.wrappers.settings.MiningSettings;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.*;

import java.util.*;

public class BPMNSubProcessMiner {

    private final XEventClassifier xEventClassifier = new XEventNameClassifier();
    private static final XFactory factory = new XFactoryNaiveImpl();
    private final XConceptExtension xce = XConceptExtension.instance();
    private final BPMNAnalizer bpmnAnalizer = new BPMNAnalizer(xce);
    private final XTimeExtension xte = XTimeExtension.instance();
    private LogModifier logMod = null;
    private SubProcessTypeDiscoverer subProcessTypeDiscoverer = null;
    private final BPMNMiner bpmnMiner = new BPMNMiner();
    private final LogCloner logCloner = new LogCloner();
    private final Converter<Entity> converter = new Converter<>();
    private UIPluginContext pluginContext;
    private boolean sortLog = false;
    private Map<Entity, Map<String, Double>> entityCertancy = new UnifiedMap<Entity, Map<String, Double>>();
    private Tree<Entity> parentTree = null;
    private Random r = new Random(123456789);

    public BPMNSubProcessMiner() {}

    public BPMNSubProcessMiner(UIPluginContext context) {
        pluginContext = context;
    }

    public BPMNDiagram mineBPMNModel(final UIPluginContext context, XLog rawlog, boolean sortLog, SelectMinerResult guiResult, int algorithm,
                                     EntityDiscoverer entityDiscoverer, ConceptualModel concModel, List<Entity> groupEntities,
                                     List<Entity> candidatesEntities, List<Entity> selectedEntities, boolean commandline) throws ExecutionCancelledException {

        pluginContext = context;
        LogOptimizer logOptimizer = new LogOptimizer();
        XLog optimizedLog = logOptimizer.optimizeLog(rawlog);
        rawlog = optimizedLog;

        logMod = new LogModifier(factory, xce, xte, logOptimizer);
        subProcessTypeDiscoverer = new SubProcessTypeDiscoverer(bpmnAnalizer, entityDiscoverer, xce, xte, logOptimizer, sortLog);

        LogExtractor logExtractor = new LogExtractor(logOptimizer);

        int selectedAlgorithm = guiResult.getSelectedAlgorithm();
        MiningSettings params = guiResult.getMiningSettings();
        double timerEventPercentage = guiResult.getTimerEventPercentage();
        double timerEventTolerance = guiResult.getTimerEventTolerance();
        double interruptingEventTolerance = guiResult.getInterruptingEventTolerance();
        double multiInstancePercentage = guiResult.getMultiInstancePercentage();
        double multiInstanceTolerance = guiResult.getMultiInstanceTolerance();
        double noiseThreshold = guiResult.getNoiseThreshold();

        //discover the ER model
        if(concModel == null) {
            long time = System.nanoTime();
            rawlog = logMod.insertArtificialStartAndEndEvent(rawlog);
            BPMNDiagram model = bpmnMiner.mineBPMNDiagram(context, rawlog, null, selectedAlgorithm, params, false, commandline, xEventClassifier);
            System.out.println("Mining Time = " + (System.nanoTime() - time));
            model = BPMNModifier.removeUnecessaryLabels(model);
            return model;
        }

        long time = System.nanoTime();

        boolean repeat = false;
        do {
            if (algorithm == 2) {
                groupEntities = new ArrayList<Entity>();
                groupEntities.addAll(concModel.getTopEntities());
            }

            Set<Group> artifacts = null;
            Set<Entity> entities = new UnifiedSet<Entity>();

            EntityPrimaryKeyConverter entityPrimaryKeyConverter = new EntityPrimaryKeyConverter();

            DatabaseCreator databaseCreator = new DatabaseCreator(logOptimizer);
            databaseCreator.generateSetOfTables(optimizedLog, groupEntities);

            if (algorithm == 1) {
                System.out.println("Generating artifactGroup...");
                artifacts = entityDiscoverer.generateArtifactGroup(groupEntities, candidatesEntities, selectedEntities);
                System.out.println("artifactGroup generated");

                System.out.println("Discovering entities...");
                entities.addAll(groupEntities);
                System.out.println("Entities Discovered");
            } else if (algorithm == 2) {
                System.out.println("Discovering entities...");
                ForeignKeySelector foreignKeySelector = new ForeignKeySelector();
                artifacts = foreignKeySelector.selectForeignKeys(databaseCreator, entityPrimaryKeyConverter, logOptimizer, groupEntities, selectedEntities, candidatesEntities, entities, entityCertancy);
                System.out.println("Entities Discovered");
            }

            System.out.println("Discovering principal PrimaryKey...");
            Entity rootPrimaryKey = entityDiscoverer.automaticDiscoverRootProcessKey(entities, rawlog);
            System.out.println("Discovered");

            System.out.println("Generating hierarchy...");
            Hierarchy<Entity> hierarchy = new Hierarchy<Entity>(entities.size());
            hierarchy = discoverSubProcessHierarchy(entities, artifacts, hierarchy, rootPrimaryKey);
            for (Map.Entry<Entity, Set<Entity>> entry : hierarchy.entrySet()) {
                entry.getValue().remove(rootPrimaryKey);
                if (entityCertancy.get(entry.getKey()) != null) {
                    entityCertancy.get(entry.getKey()).remove(rootPrimaryKey.getName());
                    Iterator<Entity> iterator = entry.getValue().iterator();
                    while (iterator.hasNext()) {
                        Entity e = iterator.next();
                        for (Attribute a : e.getKeys()) {
                            if (!entityCertancy.get(entry.getKey()).containsKey(a.getName())) {
                                iterator.remove();
                                break;
                            }
                        }
                    }
                }
            }

            Hierarchy<Entity> hierarchyClone = new Hierarchy<Entity>(entities.size());
            for (Map.Entry<Entity, Set<Entity>> entry : hierarchy.entrySet()) {
                hierarchyClone.add(entry.getKey(), new UnifiedSet<Entity>(entry.getValue()));
            }

            int countChanges = 0;
            boolean execute = true;
            while (execute) {
                execute = false;
                try {
                    Tree<Entity> tree = converter.convertHierarchyToTree(hierarchy, rootPrimaryKey, entityCertancy, algorithm);

                    if (parentTree == null) {
                        parentTree = tree;
                    }
                    System.out.println("Hierarchy generated");
                    System.out.println(tree);
                    System.out.println();

                    //generate artifact logs
                    System.out.println("Generating artifactLog...");
                    Iterator<Entity> entityIt2 = entities.iterator();

                    UnifiedMap<PrimaryKey, Entity> primaryKeyEntityUnifiedMap = new UnifiedMap<PrimaryKey, Entity>();
                    Set<PrimaryKey> primaryKeySet = new UnifiedSet<PrimaryKey>();
                    Set<PrimaryKey> primaryKeyToConsiderSet = new UnifiedSet<PrimaryKey>();
                    while (entityIt2.hasNext()) {
                        Entity entity = entityIt2.next();

                        PrimaryKey p = entityPrimaryKeyConverter.getPrimaryKey(databaseCreator, entity);

                        Tree<Entity>.Node<Entity> node = tree.findNode(entity);
                        if (node != null) {
                            List<Tree<Entity>.Node<Entity>> children = node.getChildren();
                            if (!(children != null && children.size() > 0)) {
                                primaryKeyToConsiderSet.add(p);
                            }

                            primaryKeySet.add(p);
                            primaryKeyEntityUnifiedMap.put(p, entity);
                        }
                    }
                    Map<PrimaryKey, XLog> mapLogs = logExtractor.extractLogFromPrimarykeys(optimizedLog, primaryKeySet, (Tree<Entity>) tree.clone(), true, noiseThreshold);
                    if (sortLog) {
                        for (PrimaryKey p : primaryKeySet) {
                            mapLogs.put(p, logMod.sortLog(mapLogs.get(p)));
                        }
                    }

                    System.out.println("artifactLog generated");

                    boolean skip = false;
                    if(!skip) {
                        List<BPMNDiagram> minerResults = new ArrayList<BPMNDiagram>();

                        System.out.println("Generating Synch logs...");
                        Entity main;
                        Entity secondary;
                        UnifiedMap<Entity[], XLog> synchLogs = new UnifiedMap<Entity[], XLog>();
                        for (Group art : artifacts) {
                            for (Group art2 : artifacts) {

                                if (art.equals(art2)) continue;
                                main = art.getMainEntity();
                                secondary = art2.getMainEntity();

                                Tree.Node node = tree.findNode(secondary);
                                if (node != null) {
                                    if (node.getParent() == null || !node.getParent().getData().equals(main)) continue;

                                    //synchronization logs
                                    System.out.print("Saving synch log for " + main.getLabel() + " - " + secondary.getLabel() + "...");
                                    for (PrimaryKey p : primaryKeySet) {
                                        if (primaryKeyEntityUnifiedMap.get(p).equals(main)) {
                                            synchLogs.put(new Entity[]{main, secondary}, mapLogs.get(p));
                                            System.out.println("Log saved");
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        pluginContext.getProgress().inc();

                        //generate synchronization conditions for each synchronization log
                        System.out.println("Synch logs generated");

                        Map<Entity, Set<String>> mappingEntityArtifact = new UnifiedMap<Entity, Set<String>>(entities.size());
                        Map<Set<String>, Integer> mappingSubprocesses = new UnifiedMap<Set<String>, Integer>(entities.size());

                        System.out.println("Generating mapMainKeyXLog...");
                        generateMappingSubProcessArtifact(synchLogs, candidatesEntities, selectedEntities, mappingEntityArtifact);
                        System.out.println("mapMainKeyXLog generated");

                        UnifiedMap<BPMNDiagram, Entity> bpmnDiagramEntityUnifiedMap = new UnifiedMap<BPMNDiagram, Entity>(entities.size());

                        System.out.println("Generating Logs for Mining...");
                        Map<Entity, XLog> logs = new UnifiedMap<Entity, XLog>();//generateLogsForMining(hierarchy.keySet(), synchLogs, artLogs);
                        Map<Entity, XLog> originalLogs = new UnifiedMap<Entity, XLog>(logs.size());
                        Map<Entity, XLog> originalLogs2 = new UnifiedMap<Entity, XLog>(logs.size());
                        for (Map.Entry<PrimaryKey, XLog> entry : mapLogs.entrySet()) {
                            logs.put(primaryKeyEntityUnifiedMap.get(entry.getKey()), logCloner.cloneLog(entry.getValue()));
                            XLog newLog = logCloner.cloneLog(entry.getValue());
                            XLog newLog2 = logCloner.cloneLog(entry.getValue());
                            originalLogs.put(primaryKeyEntityUnifiedMap.get(entry.getKey()), newLog);
                            originalLogs2.put(primaryKeyEntityUnifiedMap.get(entry.getKey()), newLog2);
                        }


                        System.out.println("Logs for Mining Generated");

                        System.out.println("Generating Process Models...");
                        generateProcessModels(mappingSubprocesses, mappingEntityArtifact, (Tree) tree.clone(), minerResults, logs, bpmnDiagramEntityUnifiedMap, selectedAlgorithm, params, commandline);
                        System.out.println("Process Models Generated");

                        BPMNDiagram mainModel = null;
                        for (Map.Entry<BPMNDiagram, Entity> entry : bpmnDiagramEntityUnifiedMap.entrySet()) {
                            if (entry.getValue().equals(rootPrimaryKey)) {
                                mainModel = entry.getKey();
                            }
                        }

                        subProcessTypeDiscoverer.generateCompleteRootLog((Tree) tree.clone(), originalLogs2);

                        Map<String, Map<String, Boolean>> interruptingEvents = new UnifiedMap<String, Map<String, Boolean>>();
                        System.out.println("Updating Parent Process...");
                        updateParentModelWithSubProcess(null, null, mainModel, mainModel, minerResults, interruptingEvents, bpmnDiagramEntityUnifiedMap, logs, originalLogs, originalLogs2, new UnifiedMap<BPMNDiagram, Set<String>>(), timerEventPercentage, timerEventTolerance, interruptingEventTolerance, multiInstancePercentage, multiInstanceTolerance);//logs.get(primaryKey));
                        System.out.println("Parent Process Updated");

                        System.out.println("Rendering Interrupting Events...");
                        updateParentModelWithInterruptingEvents(mainModel, interruptingEvents, algorithm);//logs.get(primaryKey));
                        System.out.println("Interrupting Events Rendered");

                        String label = null;
                        if (selectedAlgorithm == SelectMinerResult.ALPHAPOS) {
                            label = "BPMNModel based on AlphaAlgorithm";
                        } else if (selectedAlgorithm == SelectMinerResult.ILPPOS) {
                            label = "BPMNModel based on ILPMiner";
                        } else if (selectedAlgorithm == SelectMinerResult.IMPOS) {
                            label = "BPMNModel based on InductiveMiner";
                        } else if (selectedAlgorithm == SelectMinerResult.HMPOS5) {
                            label = "BPMNModel based on HeuristicsMiner ProM5.2 without useless arcs";
                        } else if (selectedAlgorithm == SelectMinerResult.SMPOS) {
                            label = "BPMNModel based on HeuristicsMiner ProM5.2 with useless arcs";
                        } else if (selectedAlgorithm == SelectMinerResult.HMPOS6) {
                            label = "BPMNModel based on HeuristicsMiner ProM6";
                        }
                        if (label == null) label = xce.extractName(rawlog);
                        else label = label + " " + xce.extractName(rawlog);

                        mainModel.getAttributeMap().put(AttributeMap.LABEL, label);
                        int oldSize = 0;
                        int size = mainModel.getActivities().size() + mainModel.getEvents().size() + mainModel.getGateways().size() + mainModel.getFlows().size();
                        while (oldSize != size) {
                            oldSize = size;
                            mainModel = BPMNSimplifier.simplify(mainModel);
                            size = mainModel.getActivities().size() + mainModel.getEvents().size() + mainModel.getGateways().size() + mainModel.getFlows().size();
                        }
                        System.out.println("Mining Time = " + (System.nanoTime() - time));
                        mainModel = BPMNModifier.removeUnecessaryLabels(mainModel);
                        return mainModel;
                    }
                } catch (EmptyLogException emptyLogException) {
                    if(algorithm == 1) {

                        repeat = true;
                        algorithm = 2;

                    }else {

                        List<Entity> entities1 = emptyLogException.getEntities();
                        if(entities1 != null && entities1.size() > 0) {
                            Set<Entity> done = new UnifiedSet<Entity>();
                            for (Entity toRemove : entities1) {
                                boolean tryagain = true;

                                while (tryagain) {
                                    boolean exit = false;
                                    boolean restart = false;
                                    Entity key = rootPrimaryKey;

                                    if (toRemove != null && parentTree.findNode(toRemove).getParent() != null) {
                                        key = toRemove;
                                        System.out.println("To remove "+key.getName());
                                    }else {
                                        System.out.println("To remove random");
                                        while (key.equals(rootPrimaryKey)) {
                                            key = hierarchy.keySet().toArray(new Entity[hierarchy.size()])[r.nextInt(hierarchy.size() - 1)];
                                        }
                                    }

                                    Tree<Entity>.Node<Entity> node = parentTree.findNode(key);

                                    Entity parent = node.getParent().getData();
                                    for (Map.Entry<Entity, Set<Entity>> entry : hierarchy.entrySet()) {
                                        if (!entry.getKey().equals(parent) && entry.getValue().contains(key)) {
                                            exit = true;
                                            break;
                                        }
                                    }

                                    if (exit) {
                                        hierarchy.get(parent).remove(key);
                                        System.out.println("Remove " + key.getName() + " as foreignkey from " + parent.getName());

                                        Tree<Entity> tree = new Tree(rootPrimaryKey);
                                        try {
                                            tree = converter.convertHierarchyToTree(hierarchy, rootPrimaryKey, entityCertancy, algorithm);
                                            if (tree.toList().size() < entities.size()) {
                                                restart = true;
                                            } else {
                                                boolean changeTree = true;
                                                for (Entity entity : entities1) {
                                                    if (entity != key && !done.contains(entity)) {
                                                        if (!parentTree.findNode(entity).getParent().equals(tree.findNode(entity).getParent())) {
                                                            changeTree = false;
                                                        }
                                                    }
                                                }
                                                if (changeTree) {
                                                    parentTree = tree;
                                                }
                                                if (key == toRemove) done.add(toRemove);
                                            }
                                        } catch (EmptyLogException ele) {
                                            restart = true;
                                        }
                                        System.out.println("Hierarchy generated");

                                        tryagain = false;
                                    }

                                    countChanges++;

                                    if (countChanges > entities.size() * 2 || restart) {
                                        hierarchy = new Hierarchy<>(entities.size());
                                        for (Map.Entry<Entity, Set<Entity>> entry : hierarchyClone.entrySet()) {
                                            hierarchy.add(entry.getKey(), new UnifiedSet<Entity>(entry.getValue()));
                                        }
                                        countChanges = 0;
                                        done = new UnifiedSet<Entity>();
                                    }
                                }
                            }
                        }else {
                            boolean tryagain = true;

                            while (tryagain) {
                                boolean exit = false;
                                boolean restart = false;
                                Entity key = rootPrimaryKey;

                                while (key.equals(rootPrimaryKey)) {
                                    key = hierarchy.keySet().toArray(new Entity[hierarchy.size()])[r.nextInt(hierarchy.size() - 1)];
                                }

                                Tree<Entity>.Node<Entity> node = parentTree.findNode(key);

                                Entity parent = node.getParent().getData();
                                for (Map.Entry<Entity, Set<Entity>> entry : hierarchy.entrySet()) {
                                    if (!entry.getKey().equals(parent) && entry.getValue().contains(key)) {
                                        exit = true;
                                        break;
                                    }
                                }
                                if (exit) {
                                    hierarchy.get(parent).remove(key);
                                    System.out.println("Remove " + key.getName() + " as foreignkey from " + parent.getName());

                                    Tree<Entity> tree = new Tree(rootPrimaryKey);
                                    try {
                                        tree = converter.convertHierarchyToTree(hierarchy, rootPrimaryKey, entityCertancy, algorithm);
                                        if (tree.toList().size() < entities.size()) {
                                            restart = true;
                                        }
                                    } catch (EmptyLogException ele) {
                                        restart = true;
                                    }
                                    System.out.println("Hierarchy generated");

                                    tryagain = false;
                                }

                                countChanges++;

                                if (countChanges > entities.size() * 2 || restart) {
                                    hierarchy = new Hierarchy<>(entities.size());
                                    for (Map.Entry<Entity, Set<Entity>> entry : hierarchyClone.entrySet()) {
                                        hierarchy.add(entry.getKey(), new UnifiedSet<Entity>(entry.getValue()));
                                    }
                                    countChanges = 0;
                                }
                            }
                        }

                        execute = true;
                    }
                }
            }
        }while (repeat);

        throw new NullPointerException();

    }

    private void updateParentModelWithInterruptingEvents(BPMNDiagram mainModel, Map<String, Map<String, Boolean>> interruptingEvents, int algorithm) throws EmptyLogException {

        System.out.println("build hierarchy");
        Hierarchy<String> hierarchy = new Hierarchy<String>(interruptingEvents.size() + 1);
        for (Map.Entry<String, Map<String, Boolean>> entry : interruptingEvents.entrySet()) {
            hierarchy.add(entry.getKey(), entry.getValue());
        }
        hierarchy.add("root", hierarchy.keySet());

        System.out.println("convert hierarchy");

        Converter<String> converter1 = new Converter<>();
        Tree<String> tree = new Tree<String>("root");
        tree = converter1.convertHierarchyToTree(hierarchy, "root", null, algorithm);
        List<String> orderedList = tree.toList();
        orderedList.remove(0);

        System.out.println("fix model");
        for (String key : orderedList) {
            SubProcess source = null;
            for (SubProcess subProcess : mainModel.getSubProcesses()) {
                if (subProcess.getLabel().equals(key)) {
                    source = subProcess;
                    break;
                }
            }
            for (Map.Entry<String, Boolean> entry2 : interruptingEvents.get(key).entrySet()) {
                String targetName = entry2.getKey();
                SubProcess target = null;
                for (SubProcess subProcess : mainModel.getSubProcesses()) {
                    if (subProcess.getLabel().equals(targetName)) {
                        target = subProcess;
                        subProcess.setParentSubprocess(source.getParentSubProcess());
                        break;
                    }
                }
                Event.EventTrigger trigger = entry2.getValue() ? Event.EventTrigger.TIMER : Event.EventTrigger.MESSAGE;
                Event e = mainModel.addEvent(key + targetName + "BoundaryEvent", Event.EventType.INTERMEDIATE, trigger, Event.EventUse.CATCH, true, source);
                e.setParentSubprocess(source.getParentSubProcess());
                if (source != null && target != null)
                    mainModel.addFlow(e, target, "");
                Event end = mainModel.addEvent(key + targetName + "EndEvent", Event.EventType.END, null, null, true, null);
                end.setParentSubprocess(source.getParentSubProcess());
                mainModel.addFlow(target, end, "");
            }
        }

    }

    private BPMNDiagram updateParentModelWithSubProcess(Activity parentActivity, Map<SubProcess, Map<BPMNDiagram, Boolean>> parentSubProcessMap,
                                                        BPMNDiagram grandParentDiagram, BPMNDiagram parentDiagram, List<BPMNDiagram> minerResults,
                                                        Map<String, Map<String, Boolean>> interruptingEvents, Map<BPMNDiagram, Entity> bpmnDiagramEntityUnifiedMap,
                                                        Map<Entity, XLog> logs, Map<Entity, XLog> originalLogs, Map<Entity, XLog> originalLogs2,
                                                        Map<BPMNDiagram, Set<String>> hasBoundaryEvent, double timerEventPercentage, double timerEventTolerance,
                                                        double interruptingEventTolerance, double multiInstancePercentage, double multiInstanceTolerance) {//XLog nonModifiedLog) {

        UnifiedMap<Activity, SubProcess> mapping = new UnifiedMap<Activity, SubProcess>();

        ArrayList<Activity> removeAct = new ArrayList<Activity>();

        List<Flow> remove = new LinkedList<Flow>();
        Map<BPMNNode, Set<BPMNNode>> addToParent = new UnifiedMap<BPMNNode, Set<BPMNNode>>();

        Map<SubProcess, Map<BPMNDiagram, Boolean>> processSubprocessMap = new UnifiedMap<SubProcess, Map<BPMNDiagram, Boolean>>();

        Set<SubProcess> eventSubProcesses = new UnifiedSet<SubProcess>();
        Map<String, Boolean> interruptingSubProcesses = new UnifiedMap<String, Boolean>();

        parentDiagram = BPMNSimplifier.removeArtificialNodes(parentDiagram);

        Set<String> childrenBoundaryEvents = new UnifiedSet<String>();
        Set<String> stringSet;
        Set<BPMNNode> bpmnNodeSet;
        Map<BPMNDiagram, Boolean> map;

        Set<BPMNNode> sources;
        Set<BPMNNode> targets;

        Set<BPMNNode> addSources;
        Set<BPMNNode> addTargets;

        for (Activity act : parentDiagram.getActivities()) {

            String label = act.getLabel();

            if (label.startsWith("SubProcess") && !(act instanceof SubProcess)) {

                String number = label.substring(label.indexOf("SubProcess ") + 11);
                if (number.contains("+")) {
                    number = number.substring(0, number.indexOf("+"));
                }
                int pos = Integer.parseInt(number);
                BPMNDiagram subProcess = minerResults.get(pos - 1);

                subProcess = updateParentModelWithSubProcess(act, processSubprocessMap, parentDiagram, subProcess, minerResults, interruptingEvents, bpmnDiagramEntityUnifiedMap, logs, originalLogs, originalLogs2, hasBoundaryEvent, timerEventPercentage, timerEventTolerance, interruptingEventTolerance, multiInstancePercentage, multiInstanceTolerance);//, mappingSubProcesses);

                if (hasBoundaryEvent.get(subProcess) != null && hasBoundaryEvent.get(subProcess).size() > 0) {
                    subProcess = removeRedundantPathToEnd(subProcess, logs.get(bpmnDiagramEntityUnifiedMap.get(subProcess)), hasBoundaryEvent.get(subProcess));
                    childrenBoundaryEvents.add(bpmnAnalizer.extractActivityLabel(act));
                }

                boolean isLoop = false;
                boolean isLooped = subProcessTypeDiscoverer.isLoop(subProcess, originalLogs.get(bpmnDiagramEntityUnifiedMap.get(parentDiagram)), bpmnDiagramEntityUnifiedMap.get(parentDiagram), bpmnDiagramEntityUnifiedMap.get(subProcess));
                boolean isMultiInstance = subProcessTypeDiscoverer.isMultiInstance(subProcess, originalLogs.get(bpmnDiagramEntityUnifiedMap.get(parentDiagram)), bpmnDiagramEntityUnifiedMap.get(parentDiagram), bpmnDiagramEntityUnifiedMap.get(subProcess), multiInstancePercentage, multiInstanceTolerance);
                boolean isEventSubProcess = subProcessTypeDiscoverer.isEventSubProcess(parentDiagram, act, isLooped);
                boolean isInterruptingEvent = subProcessTypeDiscoverer.isInterruptingEventSubProcess(parentDiagram, subProcess, act, originalLogs2, bpmnDiagramEntityUnifiedMap.get(parentDiagram), interruptingEventTolerance);

                if (isInterruptingEvent) {
                    if ((stringSet = hasBoundaryEvent.get(parentDiagram)) == null) {
                        stringSet = new UnifiedSet<String>();
                        hasBoundaryEvent.put(parentDiagram, stringSet);
                    }
                    stringSet.add(bpmnAnalizer.extractActivityLabel(act));
                }

                if (parentSubProcessMap == null) isInterruptingEvent = false;
                if (!isInterruptingEvent) {
                    if (!isEventSubProcess) {
                        if (!isMultiInstance) {
                            isLoop = isLooped;
                        }
                    }
                } else {
                    if (!isMultiInstance) {
                        isLoop = isLooped;
                    }
                }

                if (isLoop) {
                    BPMNSimplifier.removeExternalLoop(subProcess);
                }

                SubProcess sub;
                if (!isInterruptingEvent) {
                    sub = parentDiagram.addSubProcess(label, isLoop, act.isBAdhoc(), act.isBCompensation(), isMultiInstance, act.isBCollapsed(), isEventSubProcess);
                    if ((map = processSubprocessMap.get(sub)) == null) {
                        map = new UnifiedMap<BPMNDiagram, Boolean>();
                        processSubprocessMap.put(sub, map);
                    }
                    map.put(subProcess, false);
                    mapping.put(act, sub);
                } else {
                    sub = grandParentDiagram.addSubProcess(label, isLoop, act.isBAdhoc(), act.isBCompensation(), isMultiInstance, act.isBCollapsed());
                    if ((map = parentSubProcessMap.get(sub)) == null) {
                        map = new UnifiedMap<BPMNDiagram, Boolean>();
                        parentSubProcessMap.put(sub, map);
                    }
                    map.put(subProcess, true);
                    boolean b = subProcessTypeDiscoverer.isTimerEvent(grandParentDiagram, subProcess, parentActivity, originalLogs2.get(bpmnDiagramEntityUnifiedMap.get(grandParentDiagram)), timerEventPercentage, timerEventTolerance);
                    interruptingSubProcesses.put(label, b);
                }

                if (isEventSubProcess) {
                    eventSubProcesses.add(sub);
                }

                BPMNSimplifier.removeArtificialNodes(subProcess);

                if (!isInterruptingEvent) {
                    for (Flow flow : parentDiagram.getFlows()) {
                        if (flow.getSource().equals(act)) {
                            if ((bpmnNodeSet = addToParent.get(sub)) == null) {
                                bpmnNodeSet = new UnifiedSet<BPMNNode>();
                                addToParent.put(sub, bpmnNodeSet);
                            }
                            bpmnNodeSet.add(flow.getTarget());
                            remove.add(flow);
                        }
                        if (flow.getTarget().equals(act)) {
                            if ((bpmnNodeSet = addToParent.get(flow.getSource())) == null) {
                                bpmnNodeSet = new UnifiedSet<BPMNNode>();
                                addToParent.put(flow.getSource(), bpmnNodeSet);
                            }
                            bpmnNodeSet.add(sub);
                            remove.add(flow);
                        }
                    }

                    removeAct.add(act);

                } else {
                    sources = new UnifiedSet<BPMNNode>();
                    targets = new UnifiedSet<BPMNNode>();

                    addSources = new UnifiedSet<BPMNNode>();
                    addTargets = new UnifiedSet<BPMNNode>();
                    for (Flow flow : parentDiagram.getFlows()) {
                        if (flow.getSource().equals(act)) {
                            remove.add(flow);
                            targets.add(flow.getTarget());
                        }
                        if (flow.getTarget().equals(act)) {
                            remove.add(flow);
                            sources.add(flow.getSource());
                        }
                    }

                    for (BPMNNode node : sources) {
                        int count = 0;
                        for (Flow flow : parentDiagram.getFlows()) {
                            if (flow.getSource().equals(node)) {
                                count++;
                            }
                        }
                        if (count < 2) addSources.add(node);
                    }

                    for (BPMNNode node : targets) {
                        int count = 0;
                        for (Flow flow : parentDiagram.getFlows()) {
                            if (flow.getTarget().equals(node)) {
                                count++;
                            }
                        }
                        if (count < 2) addTargets.add(node);
                    }

                    for (BPMNNode nodeS : addSources) {
                        addToParent.put(nodeS, addTargets);
                    }

                    removeAct.add(act);

                }

            } else if (act.isBLooped()) {
                boolean isMultiInstance = subProcessTypeDiscoverer.isMultiInstanceActivity(act, logs.get(bpmnDiagramEntityUnifiedMap.get(parentDiagram)));
                if (isMultiInstance) {
                    act.setBLooped(false);
                    act.setBMultiinstance(true);
                }
            }

        }

        if (parentActivity != null) {
            interruptingEvents.put(parentActivity.getLabel(), interruptingSubProcesses);
        } else {
            interruptingEvents.put("root", interruptingSubProcesses);
        }

        Set<Flow> insertedArcs = new UnifiedSet<Flow>();
        for (Map.Entry<BPMNNode, Set<BPMNNode>> entry : addToParent.entrySet()) {
            BPMNNode source = entry.getKey();
            if (mapping.get(source) != null) source = mapping.get(source);

            for (BPMNNode target : entry.getValue()) {
                if (mapping.get(target) != null) target = mapping.get(target);
                boolean alreadyInserted = false;
                for (Flow f : insertedArcs) {
                    if (f.getSource().equals(source) && f.getTarget().equals(target)) {
                        alreadyInserted = true;
                        break;
                    }
                }
                if (!alreadyInserted) {
                    Flow f = parentDiagram.addFlow(source, target, "");
                    insertedArcs.add(f);
                }
            }
        }

        parentDiagram = removeRedundantPathToEndAfterActivities(parentDiagram, logs.get(bpmnDiagramEntityUnifiedMap.get(parentDiagram)), childrenBoundaryEvents);

        for (Flow flow : remove) {
            parentDiagram.removeEdge(flow);
        }

        for (Activity act : removeAct) {
            parentDiagram.removeActivity(act);
        }

        for (Map.Entry<SubProcess, Map<BPMNDiagram, Boolean>> entry : processSubprocessMap.entrySet()) {
            for (Map.Entry<BPMNDiagram, Boolean> entry1 : entry.getValue().entrySet()) {
                BPMNModifier.insertSubProcess(parentDiagram, entry1.getKey(), entry.getKey());
            }
        }

        fixEventSubProcesses(parentDiagram, eventSubProcesses);

        return parentDiagram;
    }

    private BPMNDiagram removeRedundantPathToEnd(BPMNDiagram subProcess, XLog log, Set<String> bpmnDiagrams) {
        Set<String> possibleActivitiesToCheck = new UnifiedSet<String>();

        for (XTrace trace : log) {
            XEvent last = null;
            for (XEvent event : trace) {
                if (last != null) {
                    String name = xce.extractName(event);
                    if (bpmnDiagrams.contains(name)) {
                        possibleActivitiesToCheck.add(xce.extractName(last));
                        break;
                    }
                }
                last = event;
            }
        }

        return removePath(subProcess, possibleActivitiesToCheck);
    }

    private BPMNDiagram removeRedundantPathToEndAfterActivities(BPMNDiagram subProcess, XLog log, Set<String> bpmnDiagrams) {
        Set<String> possibleActivitiesToCheck = new UnifiedSet<String>();

        for (XTrace trace : log) {
            XEvent last = null;
            for (XEvent event : trace) {
                if (last != null) {
                    String name = xce.extractName(event);
                    if (bpmnDiagrams.contains(name)) {
                        possibleActivitiesToCheck.add(xce.extractName(event));
                        break;
                    }
                }
                last = event;
            }
        }

        return removePath(subProcess, possibleActivitiesToCheck);

    }


    private BPMNDiagram removePath(BPMNDiagram subProcess, Set<String> possibleActivitiesToCheck) {

        Map<BPMNNode, Set<List<Flow>>> startAct = new UnifiedMap<BPMNNode, Set<List<Flow>>>();
        Set<List<Flow>> pathsToEnd;
        List<Event> ends = bpmnAnalizer.discoverEndEvents(subProcess);

        for (Flow f : subProcess.getFlows()) {
            if (f.getSource() instanceof Activity) {
                String actName = bpmnAnalizer.extractActivityLabel((Activity) f.getSource());
                if (possibleActivitiesToCheck.contains(actName)) {
                    if ((pathsToEnd = startAct.get(f.getSource())) == null) {
                        pathsToEnd = new UnifiedSet<List<Flow>>();
                        startAct.put(f.getSource(), pathsToEnd);
                    }
                    List<Flow> path = bpmnAnalizer.discoverPathToEnd(subProcess, f.getSource());
                    List<Flow> reduntantFlows = new ArrayList<Flow>();
                    for (Flow flow : path) {
                        BPMNNode start = flow.getSource();
                        BPMNNode end = flow.getTarget();

                        int outputS = 0;
                        int inputT = 0;

                        for (Flow f1 : subProcess.getFlows()) {
                            if (f1.getSource().equals(start)) {
                                outputS++;
                            }
                            if (f1.getTarget().equals(end)) {
                                inputT++;
                            }
                        }

                        if (outputS > 1 && inputT > 1) {
                            reduntantFlows.add(flow);
                        }
                    }
                    int found = 0;
                    for(Event end : ends) {
                        List<Flow> path2 = bpmnAnalizer.discoverPath(subProcess, f.getSource(), end, reduntantFlows);
                        if(path2 != null && path2.size() > 0) {
                            found++;
                        }
                    }
                    if(found > 0) {
                        pathsToEnd.add(path);
                    }
                }
            }
        }

        List<Flow> reduntantFlows;
        for (Map.Entry<BPMNNode, Set<List<Flow>>> entry : startAct.entrySet()) {
            for (List<Flow> flows : entry.getValue()) {
                reduntantFlows = new ArrayList<Flow>();
                for (Flow f : flows) {
                    BPMNNode start = f.getSource();
                    BPMNNode end = f.getTarget();

                    int outputS = 0;
                    int inputT = 0;

                    for (Flow f1 : subProcess.getFlows()) {
                        if (f1.getSource().equals(start)) {
                            outputS++;
                        }
                        if (f1.getTarget().equals(end)) {
                            inputT++;
                        }
                    }

                    if (outputS > 1 && inputT > 1) {
                        reduntantFlows.add(f);
                    }
                }
                if (!isEssentialForOtherActivities(subProcess, flows, reduntantFlows, entry.getKey())) {
                    for (Flow f : flows) {
                        BPMNNode start = f.getSource();
                        BPMNNode end = f.getTarget();

                        int outputS = 0;
                        int inputT = 0;

                        for (Flow f1 : subProcess.getFlows()) {
                            if (f1.getSource().equals(start)) {
                                outputS++;
                            }
                            if (f1.getTarget().equals(end)) {
                                inputT++;
                            }
                        }

                        if (outputS > 1 && inputT > 1) {
                            subProcess.removeEdge(f);
                        }
                    }
                }
            }
        }

        return subProcess;
    }

    private boolean isEssentialForOtherActivities(BPMNDiagram subProcess, List<Flow> pathToEnd, List<Flow> excludedFlow, BPMNNode start) {
        Set<BPMNNode> visitedNode = new UnifiedSet<BPMNNode>();
        Deque<BPMNNode> toVisit = new ArrayDeque<BPMNNode>();
        Set<Activity> activities = new UnifiedSet<Activity>();
        toVisit.add(start);

        Set<BPMNNode> visitedNode2;
        Deque<BPMNNode> toVisit2;

        while (toVisit.size() > 0) {
            BPMNNode node = toVisit.removeFirst();
            for (Flow f : pathToEnd) {
                if (f.getSource().equals(node)) {
                    if (!visitedNode.contains(f.getTarget())) {
                        toVisit.add(f.getTarget());
                    }
                }
            }

            visitedNode.add(node);
            if (node instanceof Activity && !node.equals(start)) {
                activities.add((Activity) node);
            } else if (node instanceof Gateway) {
                visitedNode2 = new UnifiedSet<BPMNNode>();
                toVisit2 = new ArrayDeque<BPMNNode>();
                toVisit2.add(node);
                while (toVisit2.size() > 0) {
                    BPMNNode node2 = toVisit2.removeFirst();
                    if (!(node2 instanceof Activity)) {
                        for (Flow f : subProcess.getFlows()) {
                            if (f.getTarget().equals(node2) && !f.getSource().equals(start) && !visitedNode2.contains(f.getSource())) {
                                toVisit2.add(f.getSource());
                            }
                        }
                    }

                    visitedNode2.add(node2);
                    if (node2 instanceof Activity && !node2.equals(start)) {
                        activities.add((Activity) node2);
                    }
                }
            }
        }

        for (Activity act : activities) {
            if (!reachable(subProcess, act, excludedFlow)) {
                return true;
            }
        }

        return false;
    }

    private boolean reachable(BPMNDiagram subProcess, Activity act, List<Flow> excludedFlow) {
        Event startEvent = null;

        for (Event e : subProcess.getEvents()) {
            if (e.getEventType().equals(Event.EventType.START)) {
                startEvent = e;
                break;
            }
        }

        List<Flow> path = bpmnAnalizer.discoverPath(subProcess, startEvent, act, excludedFlow);
        return (path != null && path.size() > 0);
    }

    private void fixEventSubProcesses(BPMNDiagram diagram, Set<SubProcess> eventSubProcesses) {
        Set<Flow> removeFlow = new UnifiedSet<Flow>();
        Set<Gateway> removeGateway = new UnifiedSet<Gateway>();

        for (SubProcess subProcess : eventSubProcesses) {
            subProcess.setBMultiinstance(false);
            subProcess.setBLooped(false);
            for (Flow f : diagram.getFlows()) {
                if (f.getSource().equals(subProcess) && f.getTarget() instanceof Gateway) {
                    removeFlow.add(f);
                    if (((Gateway) f.getTarget()).getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                        removeGateway.add((Gateway) f.getTarget());
                    }
                } else if (f.getTarget().equals(subProcess) && f.getSource() instanceof Gateway) {
                    removeFlow.add(f);
                    if (((Gateway) f.getSource()).getGatewayType().equals(Gateway.GatewayType.DATABASED)) {
                        removeGateway.add((Gateway) f.getSource());
                    }
                }
            }
        }

        for (Flow f : removeFlow) {
            diagram.removeEdge(f);
        }

        for (Gateway g : removeGateway) {
            diagram.removeGateway(g);
        }

        removeFlow.clear();

        BPMNSimplifier.removeLoopedXOR(diagram);
        BPMNSimplifier.removeGatewaysUseless(diagram, diagram.getGateways());

    }

    private Map<Entity, XLog> generateLogsForMining(Set<Entity> entities, UnifiedMap<Entity[], XLog> synchLogs, UnifiedMap<Entity, XLog> artLog) {
        Map<Entity, XLog> result = new UnifiedMap<Entity, XLog>(entities.size());

        Map<Entity, XLog> relativeLogs = new UnifiedMap<Entity, XLog>();

        XLog[] logs;
        Entity[] entities1;

        Comparator<Entity> entityComparator = new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                return o1.compareTo(o2);
            }
        };

        List<String> traceName;
        List<String> correctKey;

        for (Entity entity : entities) {

            XLog main = null;

            relativeLogs.clear();

            for (Map.Entry<Entity[], XLog> entry : synchLogs.entrySet()) {
                Entity a = entry.getKey()[0];
                Entity b = entry.getKey()[1];

                if (a.equals(entity)) {
                    relativeLogs.put(b, entry.getValue());
                }
            }

            if (relativeLogs.size() == 0) {
                relativeLogs.put(entity, artLog.get(entity));
            }

            logs = new XLog[relativeLogs.size()];
            entities1 = relativeLogs.keySet().toArray(new Entity[relativeLogs.keySet().size()]);
            Arrays.sort(entities1, entityComparator);
            for (int i = 0; i < logs.length; i++) {
                logs[i] = relativeLogs.get(entities1[i]);
            }

            for (XLog log : logs) {
                if (main == null) {

                    main = logCloner.cloneLog(log);

                } else {

                    for (XTrace trace : log) {

                        traceName = new ArrayList<String>();
                        correctKey = new ArrayList<String>();
                        for (String key : EntityNameExtractor.getEntityName(entity)) {//evTypeNames(child)) {
                            traceName.add(logMod.getAttributeValue(trace.getAttributes().get(key)));
                            correctKey.add(key);
                        }

                        boolean traceFound = false;

                        for (XEvent event : trace) {

                            boolean eventFound = false;

                            for (XTrace t : main) {

                                boolean sameTrace = false;
                                for (int z = 0; z < traceName.size(); z++) {
                                    String key = correctKey.get(z);
                                    String name = traceName.get(z);
                                    if (name.equals(logMod.getAttributeValue(t.getAttributes().get(key)))) {//xce.extractName(t))) {
                                        sameTrace = true;
                                    } else {
                                        sameTrace = false;
                                        break;
                                    }
                                }

                                if (sameTrace) {
                                    traceFound = true;
                                    for (XEvent e : t) {
                                        if (logMod.sameEvent(event, e, true)) {
                                            eventFound = true;
                                            break;
                                        }
                                    }
                                    if (!eventFound) {
                                        t.add(event);
                                        eventFound = true;
                                    }
                                }
                                if (eventFound) break;
                            }
                        }
                        if (!traceFound) {
                            main.add(trace);
                        }

                    }

                }
            }

            if (sortLog) {
                main = logMod.sortLog(main);
            }
            result.put(entity, main);

        }

        return result;

    }

    private void generateProcessModels(Map<Set<String>, Integer> mappingSubprocesses, Map<Entity, Set<String>> mappingEntityArtifact, Tree tree,
                                       List<BPMNDiagram> minerResults, Map<Entity, XLog> logs, Map<BPMNDiagram, Entity> bpmnDiagramEntityUnifiedMap, int selectedAlgorithm, MiningSettings params, boolean commandline) throws EmptyLogException, ExecutionCancelledException {

        List<Tree.Node> leaves;
        int subprocessPos = 1;
        while ((leaves = (List<Tree.Node>) tree.findLeaves()).size() > 0) {

            Tree.Node node = leaves.remove(0);
            Entity entity = (Entity) node.getData();

            XLog log = logs.get(entity);
            if(log.size() > 0) {
                log = logMod.insertArtificialStartAndEndEvent(log);

                log = cleanLog(log, node, logs.keySet(), bpmnDiagramEntityUnifiedMap.keySet());
                logs.put(entity, log);

                if(log.size() > 0) {

                    System.out.println("Starting mining...");
                    BPMNDiagram result = bpmnMiner.mineBPMNDiagram(pluginContext, log, "SubProcess " + subprocessPos, selectedAlgorithm, params, true, commandline, xEventClassifier);
                    System.out.println("Mining completed");

                    minerResults.add(result);
                    bpmnDiagramEntityUnifiedMap.put(result, entity);

                    if (node != tree.getRoot()) {
                        updateParentLog((Entity) node.getParent().getData(), entity, logs, "SubProcess ", subprocessPos, mappingEntityArtifact.get(entity), mappingSubprocesses);
                    }

                    subprocessPos++;

                }else {

                    List<Entity> entities = new ArrayList<Entity>();
                    entities.add(entity);
                    tree.removeLeave(node);

                    while ((leaves = (List<Tree.Node>) tree.findLeaves()).size() > 0) {

                        Tree.Node node1 = leaves.remove(0);
                        Entity entity1 = (Entity) node1.getData();

                        XLog log1 = logs.get(entity1);
                        if (log1.size() > 0) {
                            log1 = logMod.insertArtificialStartAndEndEvent(log1);

                            log1 = cleanLog(log1, node1, logs.keySet(), bpmnDiagramEntityUnifiedMap.keySet());
                            logs.put(entity1, log1);

                            if (log1.size() == 0) {
                                entities.add(entity1);
                            }
                        }else {
                            entities.add(entity1);
                        }

                        tree.removeLeave(node1);
                    }

                    System.out.println("Problem Log Empty for Subprocess B " + subprocessPos);
                    throw new EmptyLogException(entities);
                }

            }else {
                List<Entity> entities = new ArrayList<Entity>();
                entities.add(entity);
                tree.removeLeave(node);

                while ((leaves = (List<Tree.Node>) tree.findLeaves()).size() > 0) {

                    Tree.Node node1 = leaves.remove(0);
                    Entity entity1 = (Entity) node1.getData();

                    XLog log1 = logs.get(entity1);
                    if (log1.size() > 0) {
                        log1 = logMod.insertArtificialStartAndEndEvent(log1);

                        log1 = cleanLog(log1, node1, logs.keySet(), bpmnDiagramEntityUnifiedMap.keySet());
                        logs.put(entity1, log1);

                        if (log1.size() == 0) {
                            entities.add(entity1);
                        }
                    }else {
                        entities.add(entity1);
                    }

                    tree.removeLeave(node1);
                }

                System.out.println("Problem Log Empty for Subprocess A " + subprocessPos);
                throw new EmptyLogException(entities);
            }

            tree.removeLeave(node);

        }
    }

    private void updateParentLog(Entity parent, Entity child, Map<Entity, XLog> logs, String subProcessName, int subProcessPos, Set<String> activities, Map<Set<String>, Integer> mappingSubprocesses) {

        XLog temp = logs.get(parent);
        XLog newLog = factory.createLog(temp.getAttributes());

        String subProcessNamePos = subProcessName + subProcessPos;

        mappingSubprocesses.put(activities, subProcessPos);

        UnifiedMap<String, Set<String>> visitedForward;
        UnifiedMap<String, Set<String>> visitedBackward;
        Set<String> valueVisited;

        XTrace newTrace;
        XAttribute attribute;

        String name;
        String value;

        for (XTrace trace : temp) {

            newTrace = factory.createTrace(trace.getAttributes());

            visitedForward = new UnifiedMap<String, Set<String>>();
            visitedBackward = new UnifiedMap<String, Set<String>>();

            for (XEvent eventLog : trace) {
                eventLog = (XEvent) eventLog.clone();
                boolean removeEvent = false;

                name = xce.extractName(eventLog);
                if (activities.contains(name)) {
                    for (String key : EntityNameExtractor.getEntityName(child)) {//evTypeNames(child)) {
                        attribute = eventLog.getAttributes().get(key);
                        value = logMod.getAttributeValue(attribute);

                        if ((valueVisited = visitedForward.get(key)) == null) {
                            valueVisited = new UnifiedSet<String>();
                            visitedForward.put(key, valueVisited);
                        }

                        if (valueVisited.contains(value)) {
                            removeEvent = true;
                        } else {
                            valueVisited.add(value);
                            xce.assignName(eventLog, subProcessNamePos);
                        }

                    }
                }

                if (!removeEvent) {
                    newTrace.add(eventLog);
                }

            }
            Set<String> setLabels = new UnifiedSet<String>();
            for(XEvent event : newTrace) {
                setLabels.add(xce.extractName(event));
            }
            if(setLabels.size() > 1) {
                newLog.add(newTrace);
            }else if(setLabels.size() == 1) {
                String label = setLabels.iterator().next();
                if(!label.contains(subProcessName)) {
                    newLog.add(newTrace);
                }
            }

            XTrace newTrace2 = factory.createTrace(trace.getAttributes());

            XEvent eventLog;
            for (int i = trace.size() - 1; i >= 0; i--) {
                eventLog = (XEvent) trace.get(i).clone();
                boolean removeEvent = false;

                name = xce.extractName(eventLog);
                if (activities.contains(name)) {
                    for (String key : EntityNameExtractor.getEntityName(child)) {//evTypeNames(child)) {
                        attribute = eventLog.getAttributes().get(key);
                        value = logMod.getAttributeValue(attribute);

                        if ((valueVisited = visitedBackward.get(key)) == null) {
                            valueVisited = new UnifiedSet<String>();
                            visitedBackward.put(key, valueVisited);
                        }

                        if (valueVisited.contains(value)) {
                            removeEvent = true;
                        } else {
                            valueVisited.add(value);
                            xce.assignName(eventLog, subProcessNamePos);
                        }

                    }

                }

                if (!removeEvent) {
                    newTrace2.add(0, eventLog);
                }
            }

            setLabels = new UnifiedSet<String>();
            for(XEvent event : newTrace2) {
                setLabels.add(xce.extractName(event));
            }
            if(setLabels.size() > 1) {
                newLog.add(newTrace2);
            }else {
                String label = setLabels.iterator().next();
                if(!label.contains(subProcessName)) {
                    newLog.add(newTrace2);
                }
            }

        }

        if(sortLog) {
            newLog = logMod.sortLog(newLog);
        }
        logs.put(parent, newLog);

    }

    private XLog cleanLog(XLog log, Tree<Entity>.Node<Entity> node, Set<Entity> entities, Set<BPMNDiagram> bpmnDiagrams) throws EmptyLogException {
        Iterator<XTrace> iteratorTrace = log.iterator();

        int min = node.getData().getNumKeys();

        if(node.getParent() != null) {
            min += node.getParent().getData().getNumKeys();
        }

        while(iteratorTrace.hasNext()) {
            XTrace trace = iteratorTrace.next();
            Iterator<XEvent> iteratorEvent = trace.iterator();
            boolean removeTrace = false;

            while(iteratorEvent.hasNext()) {
                XEvent event = iteratorEvent.next();
                String name = xce.extractName(event);
                boolean found = false;
                int attributeNumber = 0;
                for(Map.Entry<String, XAttribute> entry : event.getAttributes().entrySet()) {
                    if(!found) {
                        for (Entity e : entities) {
                            if (!ancestor(node, e)) {
                                if(e != null) {
                                    for (Attribute a : e.getKeys()) {
                                        if (a.getName().equals(entry.getKey())) {
                                            found = true;
                                            break;
                                        }
                                    }
                                }else {
                                    throw new EmptyLogException();
                                }
                                if (found) break;
                            }
                        }
                    }
                    for(Entity e : entities) {
                        if(!e.equals(node.getData())) {
                            for (Attribute a : e.getKeys()) {
                                if (a.getName().equals(entry.getKey())) {
                                    attributeNumber++;
                                    break;
                                }
                            }
                        }
                    }
                }
                for(BPMNDiagram diagram : bpmnDiagrams) {
                    for(Activity activity : diagram.getActivities()) {
                        if(activity.getLabel().startsWith(name)) {
                            found = true;
                            break;
                        }
                    }
                }
                //NEEDS FIX
                if((attributeNumber > min) && node.getChildren() != null && node.getChildren().size() > 0) {
                    found = true;
                }
                if(found && !name.contains("SubProcess")) {
                    if(trace.size() > 3) {
                        iteratorEvent.remove();
                    }else {
                        removeTrace = true;
                    }
                }
            }
            if(removeTrace) {
                iteratorTrace.remove();
            }
        }
        return log;
    }

    private boolean ancestor(Tree<Entity>.Node<Entity> node, Entity e) {
        List<Tree.Node> toVisit = new ArrayList<Tree.Node>();
        toVisit.add(node);
        while(!toVisit.isEmpty()) {
            Tree<Entity>.Node<Entity> n = toVisit.remove(0);
            if(n.getData().equals(e)) {
                return true;
            }else if(n.getParent() != null) {
                toVisit.add(n.getParent());
            }
        }
        return false;
    }

    private  boolean descendant(Tree<Entity>.Node<Entity> node, Entity e) {
        List<Tree.Node> toVisit = new ArrayList<Tree.Node>();
        toVisit.addAll(node.getChildren());
        while(!toVisit.isEmpty()) {
            Tree<Entity>.Node<Entity> n = toVisit.remove(0);
            if(n.getData().equals(e)) {
                return true;
            }else if(n.getChildren() != null) {
                toVisit.addAll(n.getChildren());
            }
        }
        return false;
    }

    private UnifiedMap<Entity, Set<Entity>> discoverSubProcessHierarchy(UnifiedMap<Entity, XLog> artLogs, UnifiedMap<Entity[], XLog> synchLogs, UnifiedMap<Entity, Set<Entity>> hierarchy, Entity rootEntity) {

        for (Entity ent : artLogs.keySet()) {
            hierarchy.put(ent, new UnifiedSet<Entity>());
        }

        Set<Entity> set;
        Entity a;
        Entity b;

        for (Entity[] entArr : synchLogs.keySet()) {

            a = entArr[0];
            b = entArr[1];

            boolean aPointToB = false;
            for (ForeignKey fk : a.getForeignKeys()) {
                if (fk.getEntity().equals(b)) {
                    aPointToB = true;
                    break;
                }
            }

            boolean bPointToA = false;
            for (ForeignKey fk : b.getForeignKeys()) {
                if (fk.getEntity().equals(a)) {
                    bPointToA = true;
                    break;
                }
            }

            if (aPointToB && a != rootEntity) {
                set = hierarchy.get(b);
                set.add(a);
            }

            if (bPointToA && b != rootEntity) {
                set = hierarchy.get(a);
                set.add(b);
            }
        }

        return hierarchy;
    }

    private Hierarchy<Entity> discoverSubProcessHierarchy(Set<Entity> entities, Set<Group> artifacts, Hierarchy<Entity> hierarchy, Entity rootEntity) {

        Set<Entity> set;
        for (Entity ent : entities) {
            hierarchy.add(ent, new UnifiedSet<Entity>());
        }

        Entity a;
        Entity b;
        for (Group art : artifacts) {
            for (Group art2 : artifacts) {
                if (!art.equals(art2)) {

                    a = art.getMainEntity();
                    b = art2.getMainEntity();

                    boolean bPointToA = false;
                    for (ForeignKey fk : b.getForeignKeys()) {
                        if (fk.getEntity().equals(a)) {
                            bPointToA = true;
                            break;
                        }
                    }

                    if (bPointToA && b != rootEntity) {
                        set = hierarchy.get(a);
                        set.add(b);
                    }
                }
            }
        }

        return hierarchy;
    }

    private void generateMappingSubProcessArtifact(UnifiedMap<Entity[], XLog> synchLogs, List<Entity> candidatesEntities,
                                                   List<Entity> selectedEntities, Map<Entity, Set<String>> mappingEntityArtifact) {

        Entity secondary;
        Entity e;
        Set<String> secArt;
        Set<String> midArtifact;

        for (Entity[] entities : synchLogs.keySet()) {

            secondary = entities[1];

            //secondary artifact identifier (key) set
            secArt = new UnifiedSet<String>();
            int key = 0;
            while (key < secondary.getNumKeys()) {
                secArt.add(secondary.getKeys().get(key).getName());
                key++;
            }

            //get the set of event type names for secondary artifact and its associated entities
            midArtifact = new UnifiedSet<String>();
            midArtifact.addAll(EntityNameExtractor.evTypeNames(secondary));
            for (int i = 0; i < candidatesEntities.size(); i++) {
                if (selectedEntities.get(i).equals(secArt)) {
                    e = candidatesEntities.get(i);
                    midArtifact.addAll(EntityNameExtractor.evTypeNames(e));
                }
            }
            mappingEntityArtifact.put(secondary, midArtifact);

        }

    }

}