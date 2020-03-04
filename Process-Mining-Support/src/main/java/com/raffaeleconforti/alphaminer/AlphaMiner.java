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

package com.raffaeleconforti.alphaminer;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.connections.logmodel.LogPetrinetConnectionImpl;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginExecutionResult;
import org.processmining.framework.plugin.PluginParameterBinding;
import org.processmining.framework.plugin.Progress;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.framework.util.Pair;
import org.processmining.framework.util.search.MultiThreadedSearcher;
import org.processmining.framework.util.search.NodeExpander;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.log.logabstraction.LogAbstractionConnection;
import org.processmining.plugins.log.logabstraction.LogRelations;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class AlphaMiner implements NodeExpander<Tuple> {

    private LogRelations relations;
    private List<XEventClass> eventClasses;

    //****************************************************************//

    private Object[] doAlphaMiningPrivate(PluginContext context, XLog log, XLogInfo summary)
            throws CancellationException, InterruptedException, ExecutionException {

        // First check if a connection providing log ralations exists.
        try {
            LogAbstractionConnection logAbstractionConnection = context.getConnectionManager().getFirstConnection(LogAbstractionConnection.class, context, log);
            if (logAbstractionConnection != null) {
                LogRelations logRelations = logAbstractionConnection.getRelations();
                if (logRelations != null)
                    return doAlphaMiningPrivateWithRelations(context, summary, logRelations);
            }
        } catch (ConnectionCannotBeObtained e) {
            // Ignore, we try another way later
        }

        // No log relations are specified, so find a plugin that can construct them.
        // This is done, by asking the plugin manager for a plugin, that:
        // 1) It's a plugin (i.e. the annotation is Plugin.class),
        // 2) It returns LogRelations (i.e. one of the return types is LogRelations.class or any subclass thereof),
        // 3) It can be executed in a child of this context, which is of type context.getPluginContextType(),
        // 4) It can be executed on the given input (i.e. no extra input is needed, and all input is used),
        // 5) It accepts the input in any given order (i.e. not in the specified order),
        // 6) It does not have to be user visible
        // 7) It can use objects given by log and summary (i.e. types log.getClass() and summary.getClass()).
        Collection<Pair<Integer, PluginParameterBinding>> plugins = context.getPluginManager().find(Plugin.class,
                LogRelations.class, context.getPluginContextType(), true, false, false, XLog.class, summary.getClass());

        if (plugins.isEmpty()) {
            context.log("No plugin found to create log relations, please specify relations manually",
                    MessageLevel.ERROR);
            return null;
        }
        // Let's just take the first available plugin for the job of constructing log abstractions
        Pair<Integer, PluginParameterBinding> plugin = plugins.iterator().next();

        // Now, the binding can be executed on the log and the summary
        // FIrst, we instantiate a new context for this plugin, which is a child context of the current context.
        PluginContext c2 = context.createChildContext("Log Relation Constructor");

        // Let's notify our lifecyclelisteners about the fact that we created a new context. this is
        // optional, but if this is not done, then the user interface doesn't show it (if there is a ui).
        context.getPluginLifeCycleEventListeners().firePluginCreated(c2);

        // At this point, we execute the binding to get the LogRelations. For this, we call the invoke method
        // on the PluginParameterBinding stored in the plugin variable. The return type is LogRelations.class and
        // as input we give the new context c2, the log and the summary. Note that the plugin might return mulitple
        // objects, hence we extract the object with number x, where x is stored as the first element of the plugin
        // variable.

        PluginExecutionResult pluginResult = plugin.getSecond().invoke(c2, log, summary);
        pluginResult.synchronize();
        LogRelations relations = pluginResult.getResult(plugin.getFirst());

        // Now we have the relations and we can continue with the mining.
        return doAlphaMiningPrivateWithRelations(context, relations.getSummary(), relations);
    }

    //  ========= LUCIANO: Promote the following method to be public
    public Object[] doAlphaMiningPrivateWithRelations(PluginContext context, XLogInfo summary, LogRelations relations)
            throws InterruptedException, ExecutionException {
        this.relations = relations;
        eventClasses = new ArrayList<XEventClass>(summary.getEventClasses().size());
        eventClasses.addAll(summary.getEventClasses().getClasses());
        eventClasses.removeAll(relations.getLengthOneLoops().keySet());
        final Progress progress = context.getProgress();

        final Stack<Tuple> stack = new Stack<Tuple>();

        // Initialize the tuples to the causal depencencies in the log
        Tuple tuple;
        for (Pair<XEventClass, XEventClass> causal : relations.getCausalDependencies().keySet()) {
            if (!eventClasses.contains(causal.getFirst()) || !eventClasses.contains(causal.getSecond())) {
                continue;
            }
            tuple = new Tuple();
            tuple.leftPart.add(causal.getFirst());
            tuple.rightPart.add(causal.getSecond());
            tuple.maxRightIndex = eventClasses.indexOf(causal.getSecond());
            tuple.maxLeftIndex = eventClasses.indexOf(causal.getFirst());
            stack.push(tuple);
        }

        // Expand the tuples
        final List<Tuple> result = new ArrayList<Tuple>();

        // ========= LUCIANO: I forced the implementation to use a single thread !!!
        MultiThreadedSearcher<Tuple> searcher = new MultiThreadedSearcher<Tuple>(1, this,
                MultiThreadedSearcher.BREADTHFIRST);

        searcher.addInitialNodes(stack);
        searcher.startSearch(context.getExecutor(), progress, result);

        // Add transitions
        Map<XEventClass, Transition> class2transition = new UnifiedMap<XEventClass, Transition>();
        Petrinet net = PetrinetFactory.newPetrinet("Petrinet from "
                + XConceptExtension.instance().extractName(relations.getLog()) + " , mined with AlphaMiner");

        // ========= LUCIANO: No future results are handled ... therefore, the following two lines were commented out
//		context.getFutureResult(0).setLabel(net.getLabel());
//		context.getFutureResult(1).setLabel("Initial Marking of " + net.getLabel());

        for (XEventClass eventClass : summary.getEventClasses().getClasses()) {
            Transition transition = net.addTransition(eventClass.toString());
            class2transition.put(eventClass, transition);
        }

        Map<Tuple, Place> tuple2place = new UnifiedMap<Tuple, Place>();
        // Add places for each tuple
        for (Tuple tuple1 : result) {
            Place p = net.addPlace(tuple1.toString());
            for (XEventClass eventClass : tuple1.leftPart) {
                net.addArc(class2transition.get(eventClass), p);
            }
            for (XEventClass eventClass : tuple1.rightPart) {
                net.addArc(p, class2transition.get(eventClass));
            }
            tuple2place.put(tuple1, p);
        }

        Marking m = new Marking();

        // Add initial and final place
        Place pstart = net.addPlace("Start");
        for (XEventClass eventClass : relations.getStartTraceInfo().keySet()) {
            net.addArc(pstart, class2transition.get(eventClass));
        }
        m.add(pstart);

        Place pend = net.addPlace("End");
        for (XEventClass eventClass : relations.getEndTraceInfo().keySet()) {
            net.addArc(class2transition.get(eventClass), pend);
        }

        // Connect length-1 loops
        Tuple t;
        for (XEventClass oneLoop : relations.getLengthOneLoops().keySet()) {
            t = new Tuple();
            for (Pair<XEventClass, XEventClass> causal : relations.getCausalDependencies().keySet()) {
                if (causal.getFirst().equals(oneLoop)) {
                    t.leftPart.add(causal.getSecond());
                }
                if (causal.getSecond().equals(oneLoop)) {
                    t.rightPart.add(causal.getFirst());
                }
            }
            for (Tuple existing : result) {
                if (existing.isSmallerThan(t)) {
                    net.addArc(tuple2place.get(existing), class2transition.get(oneLoop));
                    net.addArc(class2transition.get(oneLoop), tuple2place.get(existing));
                }
            }
        }

        context.addConnection(new InitialMarkingConnection(net, m));
        context.addConnection(new LogPetrinetConnectionImpl(summary.getLog(), summary.getEventClasses(), net, reverse(class2transition)));
        return new Object[]{net, m};
    }

    /**
     * Flip the mapping given around (so values map to keys); handles multiple values with same key.
     *
     * @param class2transition
     * @return
     */
    protected Collection<Pair<Transition, XEventClass>> reverse(Map<XEventClass, Transition> class2transition) {
        List<Pair<Transition, XEventClass>> result = new ArrayList<Pair<Transition, XEventClass>>(class2transition.size());
        for (Entry<XEventClass, Transition> entry : class2transition.entrySet()) {
            result.add(new Pair<Transition, XEventClass>(entry.getValue(), entry.getKey()));
        }
        return result;
    }

    private boolean canExpandLeft(Tuple toExpand, XEventClass toAdd) {
        // Check if the event class in toAdd has a causal depencendy
        // with all elements of the rightPart of the tuple.
        for (XEventClass right : toExpand.rightPart) {
            if (!hasCausalRelation(toAdd, right)) {
                return false;
            }
        }

        // Check if the event class in toAdd does not have a relation
        // with any of the elements of the leftPart of the tuple.
        for (XEventClass left : toExpand.leftPart) {
            if (hasRelation(toAdd, left)) {
                return false;
            }
        }

        return true;
    }

    private boolean canExpandRight(Tuple toExpand, XEventClass toAdd) {
        // Check if the event class in toAdd has a causal depencendy
        // from all elements of the leftPart of the tuple.
        for (XEventClass left : toExpand.leftPart) {
            if (!hasCausalRelation(left, toAdd)) {
                return false;
            }
        }

        // Check if the event class in toAdd does not have a relation
        // with any of the elements of the rightPart of the tuple.
        for (XEventClass right : toExpand.rightPart) {
            if (hasRelation(right, toAdd)) {
                return false;
            }
        }

        return true;
    }

    private boolean hasRelation(XEventClass from, XEventClass to) {
        if (!from.equals(to)) {
            if (hasCausalRelation(from, to)) {
                return true;
            }
            if (hasCausalRelation(to, from)) {
                return true;
            }
        }
        return relations.getParallelRelations().containsKey(new Pair<XEventClass, XEventClass>(from, to));

    }

    private boolean hasCausalRelation(XEventClass from, XEventClass to) {
        return relations.getCausalDependencies().containsKey(new Pair<XEventClass, XEventClass>(from, to));

    }

    public Collection<Tuple> expandNode(Tuple toExpand, Progress progress, Collection<Tuple> resultsSoFar) {
        Collection<Tuple> tuples = new UnifiedSet<Tuple>();

        int startIndex = toExpand.maxLeftIndex + 1;
        for (int i = startIndex; i < eventClasses.size(); i++) {

            if (progress.isCancelled()) {
                return tuples;
            }

            XEventClass toAdd = eventClasses.get(i);

            if (canExpandLeft(toExpand, toAdd)) {
                // Ok, it is safe to add toAdd
                // to the left part of the tuple
                Tuple newTuple = toExpand.clone();
                newTuple.leftPart.add(toAdd);
                newTuple.maxLeftIndex = i;
                tuples.add(newTuple);
            }
        }

        startIndex = toExpand.maxRightIndex + 1;
        for (int i = startIndex; i < eventClasses.size(); i++) {

            if (progress.isCancelled()) {
                return tuples;
            }

            XEventClass toAdd = eventClasses.get(i);

            if (canExpandRight(toExpand, toAdd)) {
                // Ok, it is safe to add toAdd
                // to the right part of the tuple
                Tuple newTuple = toExpand.clone();
                newTuple.rightPart.add(toAdd);
                newTuple.maxRightIndex = i;
                tuples.add(newTuple);
            }

        }

        return tuples;
    }

    public void processLeaf(Tuple toAdd, Progress progress, Collection<Tuple> resultCollection) {
        synchronized (resultCollection) {
            Iterator<Tuple> it = resultCollection.iterator();
            boolean largerFound = false;
            while (!largerFound && it.hasNext()) {
                Tuple t = it.next();
                if (t.isSmallerThan(toAdd)) {
                    it.remove();
                    continue;
                }
                largerFound = toAdd.isSmallerThan(t);
            }
            if (!largerFound) {
                resultCollection.add(toAdd);
            }
        }
    }

}
