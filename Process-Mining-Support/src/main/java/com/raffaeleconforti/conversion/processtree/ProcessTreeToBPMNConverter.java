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

package com.raffaeleconforti.conversion.processtree;

import com.raffaeleconforti.conversion.petrinet.PetriNetToBPMNConverter;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.processtree.Block;
import org.processmining.processtree.Event;
import org.processmining.processtree.Event.Message;
import org.processmining.processtree.Event.TimeOut;
import org.processmining.processtree.Node;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.impl.AbstractBlock;
import org.processmining.processtree.impl.AbstractTask;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Adriano on 12/04/2017.
 */

@Plugin(name = "Convert Process Tree to BPMN",
        returnLabels = { "BPMN Model" },
        returnTypes = { BPMNDiagram.class },
        parameterLabels = { "Process Tree" },
        userAccessible = true)

public class ProcessTreeToBPMNConverter {

    public static AtomicInteger placeCounter = new AtomicInteger();


    @UITopiaVariant(affiliation = "University of Tartu",
            author = "Adriano Augusto",
            email = "adriano.augusto@ut.ee")
    @PluginVariant(variantLabel = "Convert Process Tree to BPMN Model, default", requiredParameterLabels = { 0 })
    public static BPMNDiagram convert(PluginContext context, ProcessTree tree)
            throws ProcessTreeToBPMNConverter.NotYetImplementedException,
            ProcessTreeToBPMNConverter.InvalidProcessTreeException {
        BPMNDiagram output;

        Petrinet pn = convert(tree, false);
        output = PetriNetToBPMNConverter.convert(pn, null, null, false);

        return output;
    }

    public static Petrinet convert(ProcessTree tree, boolean keepStructure)
            throws ProcessTreeToBPMNConverter.NotYetImplementedException, ProcessTreeToBPMNConverter.InvalidProcessTreeException {
        Petrinet petrinet = new PetrinetImpl(tree.getName());
        Place source = petrinet.addPlace("source " + placeCounter.incrementAndGet());
        Place sink = petrinet.addPlace("sink " + placeCounter.incrementAndGet());
        Marking initialMarking = new Marking();
        initialMarking.add(source);
        Marking finalMarking = new Marking();
        finalMarking.add(sink);

        Map<ProcessTreeToBPMNConverter.UnfoldedNode, Set<Transition>> mapPath2Transitions = new HashMap<ProcessTreeToBPMNConverter.UnfoldedNode, Set<Transition>>();
        Map<Transition, ProcessTreeToBPMNConverter.UnfoldedNode> mapTransition2Path = new HashMap<Transition, ProcessTreeToBPMNConverter.UnfoldedNode>();
        ProcessTreeToBPMNConverter.UnfoldedNode root = new ProcessTreeToBPMNConverter.UnfoldedNode(tree.getRoot());

        convertNode(root, source, sink, petrinet, false, keepStructure, mapPath2Transitions, mapTransition2Path);

        return petrinet;
    }

    private static void convertNode(ProcessTreeToBPMNConverter.UnfoldedNode unode, Place source, Place sink, Petrinet petrinet,
                                    boolean forbiddenToPutTokensInSource, boolean keepStructure,
                                    Map<ProcessTreeToBPMNConverter.UnfoldedNode, Set<Transition>> mapPath2Transitions, Map<Transition, ProcessTreeToBPMNConverter.UnfoldedNode> mapTransition2Path)
            throws ProcessTreeToBPMNConverter.NotYetImplementedException, ProcessTreeToBPMNConverter.InvalidProcessTreeException {
        Node node = unode.getNode();
        if (node instanceof AbstractTask.Automatic) {
            convertTau(unode, source, sink, petrinet, forbiddenToPutTokensInSource, keepStructure, mapPath2Transitions,
                    mapTransition2Path);
        } else if (node instanceof AbstractTask.Manual) {
            convertTask(unode, source, sink, petrinet, forbiddenToPutTokensInSource, keepStructure,
                    mapPath2Transitions, mapTransition2Path);
        } else if (node instanceof AbstractBlock.And) {
            convertAnd(unode, source, sink, petrinet, forbiddenToPutTokensInSource, keepStructure, mapPath2Transitions,
                    mapTransition2Path);
        } else if (node instanceof AbstractBlock.Seq) {
            convertSeq(unode, source, sink, petrinet, forbiddenToPutTokensInSource, keepStructure, mapPath2Transitions,
                    mapTransition2Path);
        } else if (node instanceof AbstractBlock.Xor) {
            convertXor(unode, source, sink, petrinet, forbiddenToPutTokensInSource, keepStructure, mapPath2Transitions,
                    mapTransition2Path);
        } else if (node instanceof AbstractBlock.XorLoop) {
            convertXorLoop(unode, source, sink, petrinet, forbiddenToPutTokensInSource, keepStructure,
                    mapPath2Transitions, mapTransition2Path);
        } else if (node instanceof AbstractBlock.Or) {
            convertOr(unode, source, sink, petrinet, forbiddenToPutTokensInSource, keepStructure, mapPath2Transitions,
                    mapTransition2Path);
        } else if (node instanceof AbstractBlock.Def) {
            convertDeferredChoice(unode, source, sink, petrinet, forbiddenToPutTokensInSource, keepStructure,
                    mapPath2Transitions, mapTransition2Path);
        } else if (node instanceof AbstractBlock.DefLoop) {
            convertDeferredLoop(unode, source, sink, petrinet, forbiddenToPutTokensInSource, keepStructure,
                    mapPath2Transitions, mapTransition2Path);
        } else {
            debug("operator of node " + node.getName() + " not supported in translation");
            throw new ProcessTreeToBPMNConverter.NotYetImplementedException();
        }
    }

    private static void convertTau(ProcessTreeToBPMNConverter.UnfoldedNode unode, Place source, Place sink, Petrinet petrinet,
                                   boolean forbiddenToPutTokensInSource, boolean keepStructure,
                                   Map<ProcessTreeToBPMNConverter.UnfoldedNode, Set<Transition>> mapPath2Transitions, Map<Transition, ProcessTreeToBPMNConverter.UnfoldedNode> mapTransition2Path) {
        Transition t = petrinet.addTransition("tau from tree");
        addTransition(unode, t, mapPath2Transitions, mapTransition2Path);
        t.setInvisible(true);
        petrinet.addArc(source, t);
        petrinet.addArc(t, sink);
    }

    private static void convertTask(ProcessTreeToBPMNConverter.UnfoldedNode unode, Place source, Place sink, Petrinet petrinet,
                                    boolean forbiddenToPutTokensInSource, boolean keepStructure,
                                    Map<ProcessTreeToBPMNConverter.UnfoldedNode, Set<Transition>> mapPath2Transitions, Map<Transition, ProcessTreeToBPMNConverter.UnfoldedNode> mapTransition2Path) {
        Transition t = petrinet.addTransition(unode.getNode().getName());
        addTransition(unode, t, mapPath2Transitions, mapTransition2Path);
        petrinet.addArc(source, t);
        petrinet.addArc(t, sink);
    }

    private static void convertXor(ProcessTreeToBPMNConverter.UnfoldedNode unode, Place source, Place sink, Petrinet petrinet,
                                   boolean forbiddenToPutTokensInSource, boolean keepStructure,
                                   Map<ProcessTreeToBPMNConverter.UnfoldedNode, Set<Transition>> mapPath2Transitions, Map<Transition, ProcessTreeToBPMNConverter.UnfoldedNode> mapTransition2Path)
            throws ProcessTreeToBPMNConverter.NotYetImplementedException, ProcessTreeToBPMNConverter.InvalidProcessTreeException {
        Block node = unode.getBlock();
        for (Node child : node.getChildren()) {
            convertNode(unode.unfoldChild(child), source, sink, petrinet, true, keepStructure, mapPath2Transitions,
                    mapTransition2Path);
        }
    }

    private static void convertSeq(ProcessTreeToBPMNConverter.UnfoldedNode unode, Place source, Place sink, Petrinet petrinet,
                                   boolean forbiddenToPutTokensInSource, boolean keepStructure,
                                   Map<ProcessTreeToBPMNConverter.UnfoldedNode, Set<Transition>> mapPath2Transitions, Map<Transition, ProcessTreeToBPMNConverter.UnfoldedNode> mapTransition2Path)
            throws ProcessTreeToBPMNConverter.NotYetImplementedException, ProcessTreeToBPMNConverter.InvalidProcessTreeException {
        Block node = unode.getBlock();
        int last = node.getChildren().size();
        int i = 0;
        Place lastSink = source;
        for (Node child : node.getChildren()) {
            Place childSink;
            if (i == last - 1) {
                childSink = sink;
            } else {
                childSink = petrinet.addPlace("sink " + placeCounter.incrementAndGet());
            }

            convertNode(unode.unfoldChild(child), lastSink, childSink, petrinet, false, keepStructure,
                    mapPath2Transitions, mapTransition2Path);
            lastSink = childSink;
            i++;
        }
    }

    private static void convertAnd(ProcessTreeToBPMNConverter.UnfoldedNode unode, Place source, Place sink, Petrinet petrinet,
                                   boolean forbiddenToPutTokensInSource, boolean keepStructure,
                                   Map<ProcessTreeToBPMNConverter.UnfoldedNode, Set<Transition>> mapPath2Transitions, Map<Transition, ProcessTreeToBPMNConverter.UnfoldedNode> mapTransition2Path)
            throws ProcessTreeToBPMNConverter.NotYetImplementedException, ProcessTreeToBPMNConverter.InvalidProcessTreeException {
        //add split tau
        Transition t1 = petrinet.addTransition("ORSPLIT");
        addTransition(unode, t1, mapPath2Transitions, mapTransition2Path);
        t1.setInvisible(true);
        petrinet.addArc(source, t1);

        //add join tau
        Transition t2 = petrinet.addTransition("ORJOIN");
        addTransition(unode, t2, mapPath2Transitions, mapTransition2Path);
        t2.setInvisible(true);
        petrinet.addArc(t2, sink);

        //add for each child a source and sink place
        for (Node child : unode.getBlock().getChildren()) {
            Place childSource = petrinet.addPlace("source " + placeCounter.incrementAndGet());
            petrinet.addArc(t1, childSource);

            Place childSink = petrinet.addPlace("sink " + placeCounter.incrementAndGet());
            petrinet.addArc(childSink, t2);

            convertNode(unode.unfoldChild(child), childSource, childSink, petrinet, false, keepStructure,
                    mapPath2Transitions, mapTransition2Path);
        }
    }

    private static void convertXorLoop(ProcessTreeToBPMNConverter.UnfoldedNode unode, Place source, Place sink, Petrinet petrinet,
                                       boolean forbiddenToPutTokensInSource, boolean keepStructure,
                                       Map<ProcessTreeToBPMNConverter.UnfoldedNode, Set<Transition>> mapPath2Transitions, Map<Transition, ProcessTreeToBPMNConverter.UnfoldedNode> mapTransition2Path)
            throws ProcessTreeToBPMNConverter.NotYetImplementedException, ProcessTreeToBPMNConverter.InvalidProcessTreeException {
        if (unode.getBlock().getChildren().size() != 3) {
            //a loop must have precisely three children: body, redo and exit
            throw new ProcessTreeToBPMNConverter.InvalidProcessTreeException();
        }

        Place middlePlace = petrinet.addPlace("middle " + placeCounter.incrementAndGet());
        if (forbiddenToPutTokensInSource || keepStructure) {
            //add an extra tau
            Transition t = petrinet.addTransition("tau start");
            addTransition(unode, t, mapPath2Transitions, mapTransition2Path);
            t.setInvisible(true);
            petrinet.addArc(source, t);
            //replace the source
            source = petrinet.addPlace("replacement source " + placeCounter.incrementAndGet());
            petrinet.addArc(t, source);
        }

        //body
        convertNode(unode.unfoldChild(unode.getBlock().getChildren().get(0)), source, middlePlace, petrinet, true,
                keepStructure, mapPath2Transitions, mapTransition2Path);
        //redo
        convertNode(unode.unfoldChild(unode.getBlock().getChildren().get(1)), middlePlace, source, petrinet, true,
                keepStructure, mapPath2Transitions, mapTransition2Path);
        //exit
        convertNode(unode.unfoldChild(unode.getBlock().getChildren().get(2)), middlePlace, sink, petrinet, true,
                keepStructure, mapPath2Transitions, mapTransition2Path);
    }

    private static void convertOr(ProcessTreeToBPMNConverter.UnfoldedNode unode, Place source, Place sink, Petrinet petrinet,
                                  boolean forbiddenToPutTokensInSource, boolean keepStructure,
                                  Map<ProcessTreeToBPMNConverter.UnfoldedNode, Set<Transition>> mapPath2Transitions, Map<Transition, ProcessTreeToBPMNConverter.UnfoldedNode> mapTransition2Path)
            throws ProcessTreeToBPMNConverter.NotYetImplementedException, ProcessTreeToBPMNConverter.InvalidProcessTreeException {

        //add split tau
        Transition t1 = petrinet.addTransition("tau split");
        addTransition(unode, t1, mapPath2Transitions, mapTransition2Path);
        t1.setInvisible(true);
        petrinet.addArc(source, t1);

        //add join tau
        Transition t2 = petrinet.addTransition("tau join");
        addTransition(unode, t2, mapPath2Transitions, mapTransition2Path);
        t2.setInvisible(true);
        petrinet.addArc(t2, sink);

        //add for each child a source and sink place
        for (Node child : unode.getBlock().getChildren()) {
            Place childSource = petrinet.addPlace("source " + placeCounter.incrementAndGet());
            petrinet.addArc(t1, childSource);

            Place childSink = petrinet.addPlace("sink " + placeCounter.incrementAndGet());
            petrinet.addArc(childSink, t2);

            convertNode(unode.unfoldChild(child), childSource, childSink, petrinet, false, keepStructure,
                    mapPath2Transitions, mapTransition2Path);
        }
    }

    @SuppressWarnings("unused")
    private static String getEventLabel(Event e) throws ProcessTreeToBPMNConverter.NotYetImplementedException {
        if (e instanceof Message) {
            return "message " + e.getMessage();
        } else if (e instanceof TimeOut) {
            return "time out " + e.getMessage();
        }
        throw new ProcessTreeToBPMNConverter.NotYetImplementedException();
    }

    private static void convertDeferredChoice(ProcessTreeToBPMNConverter.UnfoldedNode unode, Place source, Place sink, Petrinet petrinet,
                                              boolean forbiddenToPutTokensInSource, boolean keepStructure,
                                              Map<ProcessTreeToBPMNConverter.UnfoldedNode, Set<Transition>> mapPath2Transitions, Map<Transition, ProcessTreeToBPMNConverter.UnfoldedNode> mapTransition2Path)
            throws ProcessTreeToBPMNConverter.NotYetImplementedException, ProcessTreeToBPMNConverter.InvalidProcessTreeException {
        for (Node c : unode.getBlock().getChildren()) {
            if (!(c instanceof Event)) {
                //a deferred choice can only have events as its children
                throw new ProcessTreeToBPMNConverter.InvalidProcessTreeException();
            }

            Event child = (Event) c;

            if (child.getChildren().size() != 1) {
                //an event can only have one child
                throw new ProcessTreeToBPMNConverter.InvalidProcessTreeException();
            }

            //create a tau-transition/event and sink for each event
            Transition t = petrinet.addTransition("tau");
            addTransition(unode, t, mapPath2Transitions, mapTransition2Path);
            t.setInvisible(true);
            Place childSource = petrinet.addPlace("child sink " + placeCounter.incrementAndGet());
            petrinet.addArc(source, t);
            petrinet.addArc(t, childSource);

            //convert the child of the event
            convertNode(unode.unfoldChild(child.getChildren().get(0)), childSource, sink, petrinet, false,
                    keepStructure, mapPath2Transitions, mapTransition2Path);
        }
    }

    private static void convertDeferredLoop(ProcessTreeToBPMNConverter.UnfoldedNode unode, Place source, Place sink, Petrinet petrinet,
                                            boolean forbiddenToPutTokensInSource, boolean keepStructure,
                                            Map<ProcessTreeToBPMNConverter.UnfoldedNode, Set<Transition>> mapPath2Transitions, Map<Transition, ProcessTreeToBPMNConverter.UnfoldedNode> mapTransition2Path)
            throws ProcessTreeToBPMNConverter.NotYetImplementedException, ProcessTreeToBPMNConverter.InvalidProcessTreeException {
        if (unode.getBlock().getChildren().size() != 3) {
            //a loop must have precisely three children: body, redo and exit
            throw new ProcessTreeToBPMNConverter.InvalidProcessTreeException();
        }
        if (!(unode.getBlock().getChildren().get(1) instanceof Event)
                || !(unode.getBlock().getChildren().get(2) instanceof Event)) {
            //children two and three should be events
            throw new ProcessTreeToBPMNConverter.InvalidProcessTreeException();
        }

        Event redoEvent = (Event) unode.getBlock().getChildren().get(1);
        if (redoEvent.getChildren().size() != 1) {
            //an event should have precisely one child
            throw new ProcessTreeToBPMNConverter.InvalidProcessTreeException();
        }

        Event exitEvent = (Event) unode.getBlock().getChildren().get(2);
        if (exitEvent.getChildren().size() != 1) {
            //an event should have precisely one child
            throw new ProcessTreeToBPMNConverter.InvalidProcessTreeException();
        }

        Place middlePlace = petrinet.addPlace("middle " + placeCounter.incrementAndGet());
        if (forbiddenToPutTokensInSource || keepStructure) {
            //add an extra tau
            Transition t = petrinet.addTransition("tau start");
            addTransition(unode, t, mapPath2Transitions, mapTransition2Path);
            t.setInvisible(true);
            petrinet.addArc(source, t);
            //replace the source
            source = petrinet.addPlace("replacement source " + placeCounter.incrementAndGet());
            petrinet.addArc(t, source);
        }

        //body
        convertNode(unode.unfoldChild(unode.getBlock().getChildren().get(0)), source, middlePlace, petrinet, true,
                keepStructure, mapPath2Transitions, mapTransition2Path);

        //redo
        Transition tRedoEvent = petrinet.addTransition("tau");
        addTransition(unode, tRedoEvent, mapPath2Transitions, mapTransition2Path);
        tRedoEvent.setInvisible(true);
        petrinet.addArc(middlePlace, tRedoEvent);
        Place redoSource = petrinet.addPlace("redo source " + placeCounter.incrementAndGet());
        petrinet.addArc(tRedoEvent, redoSource);
        convertNode(unode.unfoldChild(redoEvent.getChildren().get(0)), redoSource, source, petrinet, false,
                keepStructure, mapPath2Transitions, mapTransition2Path);

        //exit
        Transition tExitEvent = petrinet.addTransition("tau");
        addTransition(unode, tExitEvent, mapPath2Transitions, mapTransition2Path);
        tExitEvent.setInvisible(true);
        Place exitSource = petrinet.addPlace("exit source " + placeCounter.incrementAndGet());
        petrinet.addArc(middlePlace, tExitEvent);
        petrinet.addArc(tExitEvent, exitSource);
        convertNode(unode.unfoldChild(exitEvent.getChildren().get(0)), exitSource, sink, petrinet, false,
                keepStructure, mapPath2Transitions, mapTransition2Path);
    }

    protected static void addTransition(ProcessTreeToBPMNConverter.UnfoldedNode unode, Transition t,
                                        Map<ProcessTreeToBPMNConverter.UnfoldedNode, Set<Transition>> mapPath2Transitions, Map<Transition, ProcessTreeToBPMNConverter.UnfoldedNode> mapTransition2Path) {
        if (mapPath2Transitions.get(unode) == null) {
            mapPath2Transitions.put(unode, new HashSet<Transition>());
        }
        mapPath2Transitions.get(unode).add(t);

        mapTransition2Path.put(t, unode);
    }


    public static class UnfoldedNode {
        public final List<Node> path;

        public UnfoldedNode(Node root) {
            path = new LinkedList<Node>();
            path.add(root);
        }

        public UnfoldedNode(ProcessTreeToBPMNConverter.UnfoldedNode nodePath, Node child) {
            path = new LinkedList<Node>(nodePath.path);
            path.add(child);
        }

        public ProcessTreeToBPMNConverter.UnfoldedNode unfoldChild(Node child) {
            return new ProcessTreeToBPMNConverter.UnfoldedNode(this, child);
        }

        public Node getNode() {
            return path.get(path.size() - 1);
        }

        public Block getBlock() {
            if (getNode() instanceof Block) {
                return (Block) getNode();
            }
            return null;
        }

        public String getId() {
            StringBuilder result = new StringBuilder();
            for (Node node : path) {
                result.append(" ");
                result.append(node.getID());
            }
            return result.toString();
        }

        public List<Node> getPath() {
            return Collections.unmodifiableList(path);
        }

        public ProcessTreeToBPMNConverter.UnfoldedNode getRoot() {
            return new ProcessTreeToBPMNConverter.UnfoldedNode(path.get(0));
        }

//        public String toString() {
//            return getNode().toString();
//        }

        @Override
        public int hashCode() {
            return path.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof ProcessTreeToBPMNConverter.UnfoldedNode) && ((ProcessTreeToBPMNConverter.UnfoldedNode) obj).path.equals(path);
        }
    }

    public static class NotYetImplementedException extends Exception {
        private static final long serialVersionUID = 5670717125585354907L;
    }

    public static class InvalidProcessTreeException extends Exception {
        private static final long serialVersionUID = 4973293024906004929L;
    }

    protected static void debug(String x) {
        //        System.out.println(x);
    }
}