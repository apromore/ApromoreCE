/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package ee.ut.eventstr.comparison;

import com.google.common.collect.*;
import ee.ut.bpmn.BPMNReader;
import ee.ut.bpmn.replayer.Pomset;
import ee.ut.bpmn.replayer.Trace;
import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.Op;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.Operation;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.State;
import ee.ut.eventstr.comparison.differences.*;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.org.processmining.framework.util.Pair;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.graph.DirectedGraph;
import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.pm.*;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.bpmn.BpmnControlFlow;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.Map.Entry;

public class DiffMLGraphicalVerbalizer {
	private long totalStartTime;

	private DifferencesML differences;

	// Model abstractions
	private ModelAbstractions model;
	public NewUnfoldingPESSemantics<Integer> pes2;
	private PetriNet net;
	private ExpandedPomsetPrefix<Integer> expprefix;

	// Log abstractions
	public PrimeEventStructure<Integer> logpes;
	private PESSemantics<Integer> pes1;

	private HashSet<String> commonLabels;
	private BPMNReader loader;
    // private BPMNReplayerML replayer;

	private List<List<Operation>> opSeqs;
	private Set<Operation> lhideOps;
	private Set<Operation> rhideOps;
	private Map<Operation, Operation> predMatch;
	private Map<Operation, Operation> succMatch;
	private Table<BitSet, Multiset<Integer>, Map<Multiset<String>, State>> stateSpace;
	private State root;
	private Multimap<State, Operation> descendants;
	private Multimap<State, State> ancestors;

	private Table<State, Operation, Pair<State, Operation>> confMismatches;
	private Map<Pair<Integer, Integer>, Multimap<State, Pair<Integer, Integer>>> conflictMismatches;
	private Map<Pair<Integer, Integer>, Multimap<State, Pair<Integer, Integer>>> causalityConcurrencyMismatches;
	private Map<Pair<Integer, Integer>, Set<State>> eventSubstitutionMismatches;
	private Table<State, Operation, Pair<Integer,Integer>> confTableBridge;

	private Set<Operation> operations;

	private Map<State, Operation> lastMatchMap;

	public static final boolean DEBUG = false;


	public DiffMLGraphicalVerbalizer(ModelAbstractions model, XLog log, HashSet<String> silents) throws Exception{
		this.model = model;
		this.net = model.getNet();
        silents.add("_0_");
		silents.add("_1_");

        for(Transition t : net.getTransitions())
            if(!model.getLabels().contains(t.getName()))
				silents.add(t.getName());

        // Compute the PES of the model
		this.pes2 = model.getUnfoldingPESSemantics(model.getNet(), silents);
		this.expprefix = new ExpandedPomsetPrefix<Integer>(pes2);

        // Compute the PES of the log
		this.logpes = getLogPES(log);
		this.pes1 = new PESSemantics<Integer>(logpes);

		this.commonLabels = new HashSet<String>();
		this.commonLabels.addAll(model.getLabels());
		this.commonLabels.addAll(pes1.getLabels());
		this.commonLabels.removeAll(silents);

		this.differences = new DifferencesML();
		this.loader = model.getReader();

		this.opSeqs = new ArrayList<>();
		this.lhideOps = new HashSet<>();
		this.rhideOps = new HashSet<>();
		this.predMatch = new HashMap<>();
		this.succMatch = new HashMap<>();
		this.descendants = HashMultimap.create();
		this.ancestors = HashMultimap.create();

		this.stateSpace = HashBasedTable.create();
		this.root = new State(new BitSet(), HashMultiset.<String> create(), HashMultiset.<Integer> create());

		this.confMismatches = HashBasedTable.create();
		this.causalityConcurrencyMismatches = new HashMap<>();
		this.conflictMismatches = new HashMap<>();
		this.eventSubstitutionMismatches = new HashMap<>();
		this.confTableBridge = HashBasedTable.create();
		this.lastMatchMap = new HashMap<>();
	}

	private PrimeEventStructure<Integer> getLogPES(XLog log) throws Exception {
		totalStartTime = System.nanoTime();

		AlphaRelations alphaRelations = new AlphaRelations(log);
		PORuns runs = new PORuns();
		Set<Integer> eventlength = new HashSet<Integer>();
		Set<Integer> succ;
		int i = 0;

		for (XTrace trace : log) {
			PORun porun = new PORun(alphaRelations, trace, (i++) + "");
			runs.add(porun);

			succ = new HashSet<Integer>(porun.asSuccessorsList().values());
			eventlength.add(succ.size());

			runs.add(porun);
		}

		runs.mergePrefix();
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(runs, "LOGPES");

		return pes;
	}

	public void addPSP(List<Operation> opSeq) {
		opSeqs.add(opSeq);
	}

    public void countMultMatching(){
		HashMap<Multiset<Integer>, Integer> counter = new HashMap<>();

        for (Operation op: descendants.values()) {
            State key = op.nextState;
            if (!descendants.containsKey(key)) {
                if (!counter.containsKey(key.c2))
                    counter.put(key.c2, 0);

                counter.put(key.c2, counter.get(key.c2) + 1);
            }
        }

        for(Entry<Multiset<Integer>, Integer> entry : counter.entrySet()){
            if(entry.getValue() > 1)
                System.out.println("\t" + entry.getKey() + " : " + entry.getValue());
        }

        System.out.println("-------");
        System.out.println();
    }

	public void verbalize() {
		for (List<Operation> opSeq: opSeqs)
			addPSPBranchToGlobalPSP(opSeq);
		prune();

        Map<Pair<Operation, Operation>, Pair<State, State>> pending = new HashMap<>();
		findCausalityConcurrencyMismatches(root, new HashSet<Pair<State,Operation>>(), new HashSet<State>(), new LinkedList<Operation>(), pending);

//		if (!pending.isEmpty())
//			throw new RuntimeException("Something wrong with some causality/concurrency mismatching events: " + pending);
		findConflictMismatches(root, new HashSet<Pair<State,Operation>>(), new HashSet<Pair<State,Operation>>(), new HashSet<State>(), new LinkedList<Operation>());
		markExpandedPrefix(root, HashMultimap.<String, Operation> create(), HashMultimap.<String, Operation> create(), new HashSet<State>(), null);
		findSkipMismatches(root, new HashMap<Integer, Pair<State, Operation>>(), new HashMap<Integer, Pair<State, Operation>>(), new HashSet<State>());
		verbalizeAdditionalModelBehavior();
        // TO IMPLEMENT
        // verbalizeOptionalModelBehavior();

		for (Pair<Integer, Integer> p: causalityConcurrencyMismatches.keySet()) {
			Map<Pair<Integer, Integer>, State> map = new HashMap<>();
			for (Entry<State, Pair<Integer, Integer>> ctx : causalityConcurrencyMismatches.get(p).entries()) {
				State state = ctx.getKey();
				Pair<Integer,Integer> cfpair = ctx.getValue();
				State exstate = map.get(cfpair);
				if (exstate == null || state.c1.cardinality() <= exstate.c1.cardinality() && state.c2.size() <= exstate.c2.size())
					map.put(cfpair, state);
			}

			for (Entry<Pair<Integer,Integer>, State> ctx: map.entrySet()) {
                if (DEBUG)
                    System.out.printf(">> Causality/Concurrency mismatch (%s, %s, %s, %s, %s)\n", ctx.getValue(),
                            translate(pes1, p.getFirst()), translate(pes1, ctx.getKey().getFirst()),
                            translate(pes2, p.getSecond()), translate(pes2, ctx.getKey().getSecond()));

				Integer event1 = ctx.getKey().getFirst();
				Integer event2 = p.getFirst();

                Integer event1A = ctx.getKey().getSecond();
                Integer event2A = p.getSecond();

				if(pes1.getBRelation(event1, event2) == BehaviorRelation.INV_CAUSALITY){
					Integer event3 = event1;
					event1 = event2;
					event2 = event3;

                    Integer event3A = event1A;
                    event1A = event2A;
                    event2A = event3A;
				}

                //// ==============================
                ////   Verbalization
                //// ==============================
                if (pes2.getBRelation(event1A, event2A).equals(BehaviorRelation.CONCURRENCY) &&
                        (pes1.getBRelation(event1, event2) == BehaviorRelation.CAUSALITY || pes1.getBRelation(event1, event2) == BehaviorRelation.INV_CAUSALITY)){
                    Pair context = (Pair<Integer, Integer>) lastMatchMap.get(ctx.getValue()).target;

                    if(event1.intValue() == ((Integer)context.getFirst()).intValue()) {
                        State ancestor = ancestors.get((State) lastMatchMap.get(ctx.getValue()).nextState).iterator().next();
                        context = (Pair<Integer, Integer>)  lastMatchMap.get(ancestor).target;
                    }

//                    while(ctx.getKey().getFirst().intValue() == ((Integer)context.getFirst()).intValue())
//                        context = (Pair<Integer, Integer>) lastMatchMap.get(ancestors.get((State)context.getFirst()).iterator().next());

                    String sentence = String.format("In the log, after '%s', '%s' occurs before '%s', while in the model they are concurrent",
                            translate(pes1, (Integer) context.getFirst()),
                            translate(pes1, event1), translate(pes1, event2));

                    // Start ranking
                    float counter = Math.min(logpes.getEventOccurrenceCount(event1), logpes.getEventOccurrenceCount(event2));
                    float ranking = counter/ (float)logpes.getTotalTraceCount();
                    // End ranking


                    System.out.println(sentence);
                    DifferenceML diff = print2TasksLog(event1A, event2A, pes2, net, loader, sentence, ranking);

                    if(diff != null) {
						diff.setSentence(sentence);
                        diff.setType("CAUSCONC1");
                        differences.add(diff);
                    }
                }
                else if(pes1.getBRelation(event1, event2).equals(BehaviorRelation.CONCURRENCY) &&
                        (pes2.getBRelation(event1A, event2A) == BehaviorRelation.CAUSALITY || pes2.getBRelation(event1A, event2A) == BehaviorRelation.INV_CAUSALITY)){
                    Integer context = ((Pair<Integer, Integer>) lastMatchMap.get(ctx.getValue()).target).getSecond();
                    String contextString = translate(pes2, context);
                    String sentence = String.format("In the model, after '%s', '%s' occurs before '%s', while in the log they are concurrent",
                            contextString, translate(pes2, ctx.getKey().getSecond()), translate(pes2, p.getSecond()));

                    // Start ranking
                    float counter = Math.min(logpes.getEventOccurrenceCount(p.getFirst()), logpes.getEventOccurrenceCount(ctx.getKey().getFirst()));
                    float ranking = counter/ (float)logpes.getTotalTraceCount();
                    // End ranking

                    DifferenceML diff = print2Tasks(context, ctx.getKey().getSecond(), p.getSecond(), pes2, net, loader, sentence, ranking);
                    if(diff != null) {
                        diff.setType("CAUSCONC2");
                        differences.add(diff);
                    }
                } else if (pes1.getBRelation(event1, event2).equals(BehaviorRelation.CAUSALITY) && pes2.getBRelation(event1A, event2A).equals(BehaviorRelation.INV_CAUSALITY)){
                    System.out.println(pes2.getDirectSuccessors(ctx.getKey().getSecond()).size());
                    Integer context = -1;
                    if(pes2.getDirectPredecessors(event2A).size() > 0)
                            context = pes2.getDirectPredecessors(event2A).iterator().next();
                    String contextString = "<start event>";
                    if(context > -1)
                        contextString = translate(pes2, context);

                        String sentence = String.format("In the log, after '%s', '%s' occurs before '%s', while in the model they occur in the reverse order",
                                contextString, translate(pes2, ctx.getKey().getSecond()), translate(pes2, p.getSecond()));

                        // Start ranking
                        float counter = Math.min(logpes.getEventOccurrenceCount(p.getFirst()), logpes.getEventOccurrenceCount(ctx.getKey().getFirst()));
                        float ranking = counter/ (float)logpes.getTotalTraceCount();
                        // End ranking

                        DifferenceML diff = print2Tasks(context, ctx.getKey().getSecond(), p.getSecond(), pes2, net, loader, sentence, ranking);
                        if(diff != null) {
                            diff.setType("TASKSWAP");
                            differences.add(diff);
                        }
                }
			}
		}

		for (Pair<Integer, Integer> p: eventSubstitutionMismatches.keySet()) {
			State enablingState = null;
			for (State state : eventSubstitutionMismatches.get(p)) {
				if (enablingState == null || state.c1.cardinality() <= enablingState.c1.cardinality() && state.c2.size() <= enablingState.c2.size())
					enablingState = state;
			}
			if (DEBUG)
				System.out.printf(">> Event substition (%s, %s, %s)\n", enablingState,
						translate(pes1, p.getFirst()), translate(pes2, p.getSecond()));

			String sentence = String.format("In the log, after '%s', '%s' is substituted by '%s'",
					translate(pes1, ((Pair<Integer, Integer>)lastMatchMap.get(enablingState).target).getFirst()),
					translate(pes2, p.getSecond()),
					translate(pes1, p.getFirst()));

            List<Integer> singleton= new LinkedList<>();
            singleton.add(p.getSecond());

            // Start ranking
            float counter = logpes.getEventOccurrenceCount(p.getFirst());
            float ranking = counter/ (float)logpes.getTotalTraceCount();
            // End ranking

            DifferenceML diff = printTasksGO(singleton, pes2, net, loader, sentence, ranking);
            if(diff != null) {
                diff.setType("TASKSUB");

                // Add new task
                List<String> newTasks = new ArrayList<>();
                newTasks.add(translate(pes1, p.getFirst()));
                diff.setNewTasks(newTasks);

                differences.add(diff);
            }
		}

		for (Pair<Integer, Integer> p: conflictMismatches.keySet()) {
			Map<Pair<Integer, Integer>, State> map = new HashMap<>();
			for (Entry<State, Pair<Integer, Integer>> ctx : conflictMismatches.get(p).entries()) {
				State state = ctx.getKey();
				Pair<Integer,Integer> cfpair = ctx.getValue();
				State exstate = map.get(cfpair);
				if (exstate == null || state.c1.cardinality() <= exstate.c1.cardinality() && state.c2.size() <= exstate.c2.size())
					map.put(cfpair, state);
			}

			for (Entry<Pair<Integer,Integer>, State> ctx: map.entrySet()) {
                if (DEBUG)
                    System.out.printf(">> Conflict mismatch (%s, %s, %s, %s, %s)\n", ctx.getValue(),
                            translate(pes1, p.getFirst()), translate(pes1, ctx.getKey().getFirst()),
                            translate(pes2, p.getSecond()), translate(pes2, ctx.getKey().getSecond()));

                if (pes1.getBRelation(p.getFirst(), ctx.getKey().getFirst()) == BehaviorRelation.CONCURRENCY) {
                    String sentence = String.format("In the log, after '%s', '%s' and '%s' are concurrent, while in the model they are mutually exclusive",
							//translate(pes1, p.getFirst()),
							translate(pes1, ((Pair<Integer, Integer>)lastMatchMap.get(ctx.getValue()).target).getFirst()),
							translate(pes1, ctx.getKey().getFirst()),
							translate(pes1, p.getFirst()));

                    // Start ranking
                    float counter = Math.min(logpes.getEventOccurrenceCount(p.getFirst()), logpes.getEventOccurrenceCount(ctx.getKey().getFirst()));
                    float ranking = counter/ (float)logpes.getTotalTraceCount();
                    // End ranking

                    Integer context = ((Pair<Integer, Integer>)lastMatchMap.get(ctx.getValue()).target).getSecond();
                    DifferenceML diff = print2Tasks(context, ctx.getKey().getSecond(), p.getSecond(), pes2, net, loader, sentence, ranking);
                    if (diff != null) {
                        diff.setType("CONFLICT1");
                        differences.add(diff);
                    }

                } else if (pes2.getBRelation(p.getSecond(), ctx.getKey().getSecond()) == BehaviorRelation.CONCURRENCY) {
                    String firstEvent, secondEvent;
                    firstEvent = translate(pes2, ctx.getKey().getSecond());
                    secondEvent = translate(pes2, p.getSecond());

                    if (firstEvent.compareTo(secondEvent) < 0) {
                        String temp = firstEvent;
                        firstEvent = secondEvent;
                        secondEvent = temp;
                    }

                    Integer context = ((Pair<Integer, Integer>)lastMatchMap.get(ctx.getValue()).target).getSecond();
                    String contextString = translate(pes2, context);

                    String sentence = String.format("In the model, after '%s', '%s' and '%s' are concurrent, while in the log they are mutually exclusive",
                            contextString,  firstEvent, secondEvent);

                    // Start ranking
                    float counter = logpes.getEventOccurrenceCount(p.getFirst()) + logpes.getEventOccurrenceCount(ctx.getKey().getFirst());
                    float ranking = counter/ (float)logpes.getTotalTraceCount();
                    // End ranking

                    DifferenceML diff = print2Tasks(context, ctx.getKey().getSecond(), p.getSecond(), pes2, net, loader, sentence, ranking);
                    if (diff != null) {
                        diff.setType("CONFLICT2");
                        differences.add(diff);
                    }

                } else if (pes1.getBRelation(ctx.getKey().getFirst(), p.getFirst()) == BehaviorRelation.CAUSALITY){
					String sentence = String.format("In the log, after '%s', '%s' occurs before '%s', while in the model they are mutually exclusive",
							translate(pes1, ((Pair<Integer, Integer>)lastMatchMap.get(ctx.getValue()).target).getFirst()),
							translate(pes1, ctx.getKey().getFirst()),
							translate(pes1, p.getFirst()));

                    // Start ranking
                    float counter = Math.min(logpes.getEventOccurrenceCount(p.getFirst()), logpes.getEventOccurrenceCount(ctx.getKey().getFirst()));
                    float ranking = counter/ (float)logpes.getTotalTraceCount();
                    // End ranking

                    Integer context = ((Pair<Integer, Integer>)lastMatchMap.get(ctx.getValue()).target).getSecond();
                    DifferenceML diff = print2Tasks(context, ctx.getKey().getSecond(), p.getSecond(), pes2, net, loader, sentence, ranking);

					if (diff != null) {
                        diff.setType("CONFLICT3");
                        differences.add(diff);
                    }
                }else if (pes2.getBRelation(ctx.getKey().getSecond(), p.getSecond()) == BehaviorRelation.CAUSALITY) {
                    Integer context = ((Pair<Integer, Integer>) lastMatchMap.get(ctx.getValue()).target).getSecond();
                    String contextString = translate(pes2, context);
                    String sentence = String.format("In the model, after '%s', '%s' occurs before '%s', while in the log they are mutually exclusive",
                            contextString, translate(pes2, ctx.getKey().getSecond()), translate(pes2, p.getSecond()));

                    // Start ranking
                    float counter = logpes.getEventOccurrenceCount(p.getFirst())+ logpes.getEventOccurrenceCount(ctx.getKey().getFirst());
                    float ranking = counter/ (float)logpes.getTotalTraceCount();
                    // End ranking

                    DifferenceML diff = print2Tasks(context, p.getSecond(), ctx.getKey().getSecond(), pes2, net, loader, sentence, ranking);
                    if (diff != null) {
                        diff.setType("CONFLICT4");
                        differences.add(diff);
                    }
                }
			}
		}

//        System.out.println(DifferenceML.toJSON(differences));
	}

	private void prune() {
		Set<State> sinks = new HashSet<>(ancestors.keys());
		sinks.removeAll(ancestors.values());

		Set<State> visited = new HashSet<>();
		operations = new HashSet<>();
		LinkedList<State> open = new LinkedList<>(sinks);
		while (!open.isEmpty()) {
			State curr = open.pop();
			visited.add(curr);
			if (root.equals(curr)) continue;

			State pred = ancestors.get(curr).iterator().next();
			if (pred != null) {
				for (Operation op: descendants.get(pred))
					if (op.nextState.equals(curr)) {
						operations.add(op);
						break;
					}
				if (!visited.contains(pred) && !open.contains(pred))
					open.push(pred);
			}
		}


		for (State s: visited) {
			Set<Operation> toDelete = new HashSet<>();
			for (Operation o: descendants.get(s))
				if (!operations.contains(o))
					toDelete.add(o);
			for (Operation o: toDelete)
				descendants.remove(s, o);
		}

		lhideOps.retainAll(operations);
		rhideOps.retainAll(operations);
	}

	public DifferencesML getDifferences(){
		return differences;
	}

	private void verbalizeAdditionalModelBehavior() {
		for (Entry<State, List<Integer>> entry:	expprefix.getAdditionalAcyclicIntervals().entries()) {
			if (DEBUG)
				System.out.printf("In the log, %s do(es) not occur after %s\n", translate(entry.getValue()), entry.getKey());

			String sentence = String.format("In the log, the interval %s does not occur after '%s'",
					translate(entry.getValue()), translate(pes1, ((Pair<Integer,Integer>)lastMatchMap.get(entry.getKey()).target).getFirst()));

            // Start ranking
            float counter = logpes.getEventOccurrenceCount(((Pair<Integer,Integer>)lastMatchMap.get(entry.getKey()).target).getFirst());
            float ranking = counter/ (float)logpes.getTotalTraceCount();
            // End ranking

			DifferenceML diff = printTasksGO(entry.getValue(), pes2, net, loader, sentence, ranking);
            if(diff != null) {
                diff.setType("UNOBSACYCLICINTER");
                differences.add(diff);
            }
		}
		for (Entry<State, Multiset<Integer>> entry:	expprefix.getAdditionalCyclicIntervals().entries()) {
            Set<String> cycleTask = translate(entry.getValue());

            if(cycleTask.size() == 0)
                continue;

			if (DEBUG)
				System.out.printf("In the log, the cycle involving %s does not occur after %s\n", translate(entry.getValue()), entry.getKey());
			String sentence = String.format("In the log, the cycle involving %s does not occur after '%s'",
					translate(entry.getValue()), translate(pes1, ((Pair<Integer,Integer>)lastMatchMap.get(entry.getKey()).target).getFirst()));

            // Start ranking
            float ranking = logpes.getEventOccurrenceCount(((Pair<Integer,Integer>)lastMatchMap.get(entry.getKey()).target).getFirst())/(float)logpes.getTotalTraceCount();

            // End ranking

			DifferenceML diff = printTasksHL2(((Pair<Integer,Integer>)lastMatchMap.get(entry.getKey()).target).getFirst(), new ArrayList<Integer>(entry.getValue()), pes2, net, loader, sentence, ranking);
            if(diff != null) {
                diff.setType("UNOBSCYCLICINTER");
                differences.add(diff);
            }
		}
	}

	private Set<String> translate(Collection<Integer> multiset) {
		Set<String> set = new HashSet<>();
		for (Integer ev: multiset)
			if (!pes2.getInvisibleEvents().contains(ev))
				set.add(translate(pes2, ev));
		return set;
	}

	private void markExpandedPrefix(State curr, Multimap<String, Operation> lpending, Multimap<String, Operation> rpending, Set<State> visited, Operation lastMatch) {
		visited.add(curr);
		lastMatchMap.put(curr, lastMatch);

		for (Operation op: descendants.get(curr)) {
			expprefix.mark(op.nextState, op);

			if (lhideOps.contains(op)) {
				if (curr.labels.contains(op.label)) {
					if (DEBUG)
						System.out.printf("In the log, '%s' is repeated after '%s'\n", translate(pes1, (Integer) op.target), curr);

                    if(translate(pes1, (Integer) op.target).equals("_1_") || translate(pes1, (Integer) op.target).equals("_0_"))
                        break;

					String sentence = String.format("In the log, '%s' is repeated after '%s'", translate(pes1, (Integer) op.target),
							translate(pes1, ((Pair<Integer, Integer>)lastMatch.target).getFirst()));

					Integer startEvent =  ((Pair<Integer, Integer>)lastMatch.target).getSecond();

                    Integer toFind = findEvent(translate(pes1, (Integer) op.target));
                    List<Integer> singleton = new LinkedList<>();
                    singleton.add(toFind);

                    // Start ranking
                    float ranking = logpes.getEventOccurrenceCount((Integer) op.target)/(float) logpes.getTotalTraceCount();
                    // End ranking

                    DifferenceML diff = printTasksHLRep(startEvent, singleton, pes2, net, loader, sentence, ranking);
                    if(diff != null) {
                        diff.setType("UNMREPETITION");
                        differences.add(diff);
                    }
				} else
					lpending.put(op.label, op);
			} else if (rhideOps.contains(op)) {
				if (curr.labels.contains(op.label)) {
                    int startEvent = ((Pair<Integer, Integer>)lastMatch.target).getSecond();
                    String start = translate(pes2, startEvent);
                    if(start.equals("_0_")) {
                        startEvent = -2;
                        start = "<start state>";
                    }

					if (DEBUG)
						System.out.printf("In the model, '%s' is repeated after '%s'\n", translate(pes2, (Integer) op.target), curr);

                    if(translate(pes2, (Integer) op.target).equals("_1_") || translate(pes2, (Integer) op.target).equals("_0_"))
                        break;

                    String sentence = String.format("In the model, '%s' is repeated after '%s'",
                            translate(pes2, (Integer) op.target), start);

                    List<Integer> singleton = new LinkedList<>();
                    singleton.add((Integer) op.target);

                    // Ranking
                    float counter = 0;
                    for(int i = 0; i < pes1.getLabels().size(); i++)
                        if(pes1.getLabel(i).equals(translate(pes2, (Integer) op.target)))
                            counter++;
                    float ranking = counter/(float)logpes.getTotalTraceCount();
                    // End ranking

                    DifferenceML diff = printTasksHL(startEvent, singleton, pes2, net, loader, sentence, ranking);
                    if(diff != null) {
                        diff.setType("UNOBSCYCLICINTER");
                        differences.add(diff);
                    }
				} else
					rpending.put(op.label, op);
			}

			if (op.op == Op.MATCH || op.op == Op.MATCHNSHIFT) {
				for (Operation h: rpending.values())
					if (!succMatch.containsKey(h))
						succMatch.put(h, op);
				for (Operation h: lpending.values())
					if (!succMatch.containsKey(h))
						succMatch.put(h, op);

				if (!visited.contains(op.nextState))
					markExpandedPrefix(op.nextState, lpending, rpending, visited, op);

			} else {
				if (!predMatch.containsKey(op))
					predMatch.put(op, lastMatch);
				if (!visited.contains(op.nextState))
					markExpandedPrefix(op.nextState, lpending, rpending, visited, lastMatch);
			}

			if (lpending.containsEntry(op.label, op)) {
				Collection<Operation> right = rpending.get(op.label);

				if (lpending.get(op.label).size() == 1 && right.size() == 1) {
					Operation pred = predMatch.get(right.iterator().next());
					Operation succ = succMatch.get(op);
					if (DEBUG)
						System.out.printf("In the log, \"%s\" occurs after \"%s\" instead of \"%s\"\n", translate(pes1, (Integer) op.target),
								pred == null || pred.label.equals("_0_") ? "<start state>": pred.label, //String.format("%s%s", pred.label, pred.target),
								succ == null || succ.label.equals("_1_") ? "<end state>": succ.label); //String.format("%s%s", succ.label, succ.target));

                    if(translate(pes1, (Integer) op.target).equals("_1_") || translate(pes1, (Integer) op.target).equals("_0_"))
                        break;

					String sentence = String.format("In the log, '%s' occurs after '%s' instead of '%s'",
							translate(pes1, (Integer) op.target),
							pred == null || pred.label.equals("_0_") ? "<start state>" :
									translate(pes1, ((Pair<Integer, Integer>)pred.target).getFirst()),
                                    // translate(pes2, ((Pair<Integer, Integer>)pred.target).getSecond()), // Original
							succ == null || succ.label.equals("_1_") ? "<end state>" :
									translate(pes2, ((Pair<Integer, Integer>)succ.target).getSecond()));


                    List<Integer> singleton = new LinkedList<>();
                    singleton.add(((Pair<Integer, Integer>)lastMatch.target).getSecond());

                    List<Integer> singletonPast = new LinkedList<>();
                    singletonPast.add(((Pair<Integer, Integer>)pred.target).getSecond());

                    // Start ranking
                    float counter = logpes.getEventOccurrenceCount((Integer) op.target);
                    float ranking = counter/ (float)logpes.getTotalTraceCount();
                    // End ranking

                    DifferenceML diff = printTasksGOHL(singletonPast, singleton, pes2, net, loader, sentence, ranking);
                    if(diff != null) {
                        diff.setType("TASKRELOC");

                        // Add new task
                        List<String> newTasks = new ArrayList<>();
                        newTasks.add(translate(pes1, ((Pair<Integer, Integer>)pred.target).getFirst()));
                        diff.setNewTasks(newTasks);

                        differences.add(diff);
                    }

					rpending.removeAll(op.label);
				} else {
					Operation pred = predMatch.get(op);
					Operation succ = succMatch.get(op);

                    if(translate(pes1, (Integer) op.target).equals("_1_"))
                        continue;

                    Integer before = pred == null || pred.label.equals("_0_") ? -2 :
                            ((Pair<Integer, Integer>)pred.target).getFirst();
                    Integer after = succ == null || succ.label.equals("_1_") ? -1 :
                            ((Pair<Integer, Integer>)succ.target).getFirst();

                    String bS = before == -2 ? "<start state>" : translate(pes1, before);
                    String aS = after == -1 ? "<end state>" : translate(pes1, after);

					if (DEBUG)
						System.out.printf("In the log, \"%s\" occurs after \"%s\" and before \"%s\"\n", translate(pes1, (Integer) op.target),
								before, after);

                    if(translate(pes1, (Integer) op.target).equals("_1_") || translate(pes1, (Integer) op.target).equals("_0_"))
                        break;

					String sentence = String.format("In the log, '%s' occurs after '%s' and before '%s'",
							translate(pes1, (Integer) op.target), bS, aS);

                    if(before != -2)
					    before = findEvent(bS);
                    if(before != -1)
					    after = findEvent(aS);

                    // Start ranking
                    float ranking = (float)logpes.getEventOccurrenceCount((Integer) op.target)/(float) logpes.getTotalTraceCount();
                    // End ranking

                    DifferenceML diff = printTasksHLstEnd(before, after, pes2, net, loader, sentence, ranking);
                    if(diff != null) {
                        diff.setType("TASKABSLog");
                        List<String> newTaks = new ArrayList<>();
                        newTaks.add(translate(pes1, (Integer) op.target));
                        diff.setNewTasks(newTaks);

                        differences.add(diff);
                    }
                }
				lpending.remove(op.label, op);
			} else if (rpending.containsEntry(op.label, op)) {
				Collection<Operation> left = lpending.get(op.label);

				if (rpending.get(op.label).size() == 1 && left.size() == 1) {
					Operation succ = predMatch.get(left.iterator().next());
//					Operation succ = succMatch.get(op);
                    Operation pred = predMatch.get(op);

					if (DEBUG)
						System.out.printf("In the model, \"%s\" occurs after \"%s\" instead of \"%s\"\n", translate(pes2, (Integer) op.target),
								pred == null || pred.label.equals("_0_") ? "<start state>": pred.label, //String.format("%s%s", pred.label, pred.target),
								succ == null || succ.label.equals("_1_") ? "<end state>": succ.label); //String.format("%s%s", succ.label, succ.target));

                    if(translate(pes2, (Integer) op.target).equals("_1_") || translate(pes2, (Integer) op.target).equals("_0_"))
                        break;

					String sentence = String.format("In the model, '%s' occurs after '%s' instead of '%s'",
							translate(pes2, (Integer) op.target),
							pred == null || pred.label.equals("_0_") ? "<start state>" :
									translate(pes2, ((Pair<Integer, Integer>)pred.target).getSecond()),
							succ == null || succ.label.equals("_1_") ? "<end state>" :
									translate(pes2, ((Pair<Integer, Integer>)succ.target).getSecond()));

                    List<Integer> singleton = new LinkedList<>();
                    singleton.add((Integer) op.target);

                    List<Integer> singletonPast = new LinkedList<>();
                    singletonPast.add(((Pair<Integer, Integer>)pred.target).getSecond());

                    // Start ranking
                    float ranking = 0;
                    if(pes1.getLabels().contains(translate(pes2, (Integer) op.target)))
                        ranking = 1.0f;
                    // End ranking

                    DifferenceML diff = printTasksGOHL(singletonPast, singleton, pes2, net, loader, sentence, ranking);
                    if(diff != null) {
                        diff.setType("TASKRELOC");

                        // Add new task
                        List<String> newTasks = new ArrayList<>();
                        newTasks.add(translate(pes1, ((Pair<Integer, Integer>)pred.target).getFirst()));
                        diff.setNewTasks(newTasks);

                        differences.add(diff);
                    }

                    lpending.removeAll(op.label);
				} else {
					Operation pred = predMatch.get(op);
					Operation succ = succMatch.get(op);

                    Integer before = pred == null || pred.label.equals("_0_") ? -2 :
                            ((Pair<Integer, Integer>)pred.target).getSecond();
                    Integer after = succ == null || succ.label.equals("_1_") ? -1 :
                            ((Pair<Integer, Integer>)succ.target).getSecond();

                    String bS = before == -2 ? "<start state>" : translate(pes2, before);
                    String aS = after == -1 ? "<end state>" : translate(pes2, after);

					if (DEBUG)
						System.out.printf("In the model, '%s' occurs after '%s' and before '%s'\n", translate(pes2, (Integer) op.target), bS, aS);

                    if(translate(pes2, (Integer) op.target).equals("_1_") || translate(pes2, (Integer) op.target).equals("_0_"))
                        break;

					String sentence = String.format("In the model, '%s' occurs after '%s' and before '%s'", translate(pes2, (Integer) op.target), bS, aS);

                    List<Integer> targets = new LinkedList<>();
                    targets.add((Integer) op.target);

                    // Start ranking
                    float ranking = 0;
                    if(!pes1.getLabels().contains(translate(pes2, (Integer) op.target)))
                        ranking = 1.0f;
                    // End ranking

                    //System.out.println("Targets = " +  targets);
                    DifferenceML diff = printTasksGO2(before, targets, after, pes2, net, loader, sentence, ranking);
//					DifferenceML diff = printTasksHL2(before, targets, after, pes2, net, loader, sentence, ranking);

                    if(diff != null) {
                        diff.setType("TASKABSModel");
                        differences.add(diff);
                    }
				}
				rpending.remove(op.label, op);
			}
		}
	}

    private Integer findEvent(String label) {
       for(int i = 0; i < pes2.getLabels().size(); i++)
            if(pes2.getLabel(i).equals(label))
                return i;

        return -1;
    }

    private void findSkipMismatches(State curr, Map<Integer, Pair<State, Operation>> ltargets, Map<Integer, Pair<State, Operation>> rtargets, Set<State> visited) {
		visited.add(curr);
		Map<Integer, Pair<Integer, Operation>> lhides = new HashMap<>();
		Map<Integer, Pair<Integer, Operation>> rhides = new HashMap<>();

		for (Operation op: confMismatches.row(curr).keySet()) {
			Pair<State, Operation> pair = confMismatches.get(curr, op);
			if (op.op == Op.LHIDE) {
				Integer target = ((Pair<Integer,Integer>)pair.getSecond().target).getFirst();
				lhides.put((Integer)op.target, new Pair<>(target, op));
			} else {
				Integer target = ((Pair<Integer,Integer>)pair.getSecond().target).getSecond();
				rhides.put((Integer)op.target, new Pair<>(target, op));
			}
		}

		for (Operation op: descendants.get(curr)) {
			if (op.op == Op.MATCH || op.op == Op.MATCHNSHIFT) {
				Pair<Integer,Integer> deltaEvents = (Pair)op.target;
				Integer e = deltaEvents.getFirst();
				Integer f = deltaEvents.getSecond();

				if (lhides.containsKey(e)) {
					Integer target = lhides.get(e).getFirst();
					ltargets.put(target, new Pair<>(curr, lhides.get(e).getSecond()));
					if (!visited.contains(op.nextState))
						findSkipMismatches(op.nextState, ltargets, rtargets, visited);
					ltargets.remove(target);
				} else if (rhides.containsKey(f)) {
					Integer target = rhides.get(f).getFirst();
					rtargets.put(target, new Pair<>(curr, rhides.get(f).getSecond()));
					if (!visited.contains(op.nextState))
						findSkipMismatches(op.nextState, ltargets, rtargets, visited);
					rtargets.remove(target);
				} else {
					if (ltargets.containsKey(e)) {
                        Integer start = ((Pair<Integer,Integer>)lastMatchMap.get(ltargets.get(e).getFirst()).target).getFirst();
                        String eventLabel = translate(pes1, start);

                        if(eventLabel.equals("_0_")) {
                            eventLabel = "<start state>";
                            start = -2;
                        }

                        if (DEBUG)
							System.out.printf("In the model, after '%s', '%s' is optional\n",
									eventLabel, translate(pes1, (Integer)ltargets.get(e).getSecond().target));
						String sentence = String.format("In the model, after '%s', '%s' is optional",
								eventLabel, translate(pes1, (Integer)ltargets.get(e).getSecond().target));

						// Remove the corresponding conflict mismatch
						conflictMismatches.remove(confTableBridge.get(ltargets.get(e).getFirst(), ltargets.get(e).getSecond()));
						ltargets.remove(e);

                        List<Integer> singleton = new LinkedList<>();
                        singleton.add((Integer)rtargets.get(f).getSecond().target);

                        // Start ranking
                        float ranking = logpes.getEventOccurrenceCount(f)/(float) logpes.getTotalTraceCount();
                        // End ranking

                        DifferenceML diff = printTasksHL(start, singleton, pes2, net, loader, sentence, ranking);
                        if(diff != null) {
                            diff.setType("TASKSKIP2");
                            differences.add(diff);
                        }

					} else if (rtargets.containsKey(f)) {
                        Integer start = ((Pair<Integer, Integer>) lastMatchMap.get(rtargets.get(f).getFirst()).target).getSecond();
                        String eventLabel = translate(pes2, start);

                        if (eventLabel.equals("_0_")) {
                            eventLabel = "<start state>";
                            start = -2;
                        }

                        String intervalLabels = "[";

                        HashSet<Integer> interval = new HashSet<>();
                        interval.add((Integer) rtargets.get(f).getSecond().target);

                        State nextSt = rtargets.get(f).getSecond().nextState;
                        while (nextSt != null) {
                            if (descendants.get(nextSt).size() != 1)
                                break;

                            Operation newOp = descendants.get(nextSt).iterator().next();
                            if (newOp.op.equals(Op.MATCH))
                                break;

                            Integer deltaEvents2 = (Integer) newOp.target;
                            interval.add(deltaEvents2);

                            nextSt = newOp.nextState;
                        }

                        int i = 0;
                        for (Integer eventInt : interval) {
                            intervalLabels += translate(pes2, eventInt);
                            if (i < interval.size() - 1)
                                intervalLabels += ", ";
                            i++;
                        }

                        intervalLabels += "]";

                        String quant = "";
                        if (interval.size() == 1)
                            quant += "is";
                        else
                            quant += "are";

                        if (DEBUG)
                            System.out.printf("In the log, after '%s', '%s' %s optional\n",
                                    eventLabel, intervalLabels);

                        // remove statement from model perspective that reflects the optional event (which is verbalized here)

                        for(Integer eventInt : interval) {
                            String toRemove = String.format("In the model, '%s' occurs after '%s' and before '%s'", translate(pes2, eventInt),
                                    translate(pes2, ((Pair<Integer, Integer>) lastMatchMap.get(rtargets.get(f).getFirst()).target).getSecond()), op.label);
                            for (DifferenceML diff2Remove : this.differences.getDifferences()) {
                                String norm1 = diff2Remove.getSentence().replaceAll("'", "").replaceAll("\"", "");
                                String norm2 = toRemove.replaceAll("'", "").replaceAll("\"", "");
                                if (norm1.equals(norm2) || toRemove.equals(diff2Remove.getSentence()))
                                    differences.remove(diff2Remove);
                            }
                        }

						String sentence = String.format("In the log, after '%s', '%s' %s optional", eventLabel, intervalLabels, quant);

//                        List<Integer> singleton = new LinkedList<>();
//                        singleton.add((Integer)rtargets.get(f).getSecond().target);

                        // Start ranking
                        float ranking = logpes.getEventOccurrenceCount(e)/(float) logpes.getTotalTraceCount();
                        // End ranking

                        DifferenceML diff = printTasksHL(start, new LinkedList<Integer>(interval), pes2, net, loader, sentence, ranking);
                        if(diff != null) {
                            diff.setType("TASKSKIP1");
                            differences.add(diff);
                        }

						// Remove the corresponding conflict mismatch
                        conflictMismatches.remove(confTableBridge.get(rtargets.get(f).getFirst(), rtargets.get(f).getSecond()));
                        rtargets.remove(f);
					}
					if (!visited.contains(op.nextState))
						findSkipMismatches(op.nextState, ltargets, rtargets, visited);
				}
			} else if (!visited.contains(op.nextState))
				findSkipMismatches(op.nextState, ltargets, rtargets, visited);
		}
	}

    public Set<Pair<State, Operation>> getSimilarOps(Set<Pair<State, Operation>> chs, Op operation){
        Set<Pair<State, Operation>> similar = new HashSet<>();

        for(Pair<State, Operation> pair : chs)
            if(pair.getSecond().op.equals(operation))
                similar.add(pair);

        return similar;
    }

	private void findConflictMismatches(State sigma, Set<Pair<State, Operation>> cms, Set<Pair<State, Operation>> chs, Set<State> visited, LinkedList<Operation> stack) {
		visited.add(sigma);
		for (Operation op: descendants.get(sigma)) {
			stack.push(op);
			Pair<Integer,Integer> deltaEvents = getDeltaEvents(op);
			Integer e = deltaEvents.getFirst();
			Integer f = deltaEvents.getSecond();

			if ((op.op == Op.RHIDE || op.op == Op.RHIDENSHIFT) && pes2.getInvisibleEvents().contains(f)) {
				if (!visited.contains(op.nextState)) {
					findConflictMismatches(op.nextState, cms, chs, visited, stack);
					stack.pop();
				}
				continue;
			}

			Integer ep = null, fp = null;
			Set<Pair<State, Operation>> n_cms = retainCommutative(cms, e, f);
			Set<Pair<State, Operation>> n_chs = retainCommutative(chs, e, f);
            n_chs.addAll(getSimilarOps(chs, op.op));
//			System.out.println("**" + op);
			switch (op.op) {
				case LHIDE:
					Pair<Pair<State, Operation>, Integer> tuple = findConflictingMatchForLHide(cms, e);
					//System.out.println("the tuple is " + tuple);
					if (tuple != null) {
						Pair<State, Operation> pair = tuple.getFirst();
						Pair<Integer, Integer> p = (Pair)pair.getSecond().target;
						f = tuple.getSecond();
						ep = p.getFirst();
						fp = p.getSecond();

						State enablingState = findEnablingState(stack, op, pair.getSecond());
						System.out.printf("Conflict related mismatch %s enabling state: %s\n", tuple, enablingState);

						assertConflictMismatch(enablingState, e, ep, f, fp);

						lhideOps.remove(op);
						n_cms.remove(pair);
					} else
						n_chs.add(new Pair<>(sigma, op));
					break;
				case RHIDE:
				case RHIDENSHIFT:
					Pair<Pair<State, Operation>, Integer> tuplep = findConflictingMatchForRHide(cms, f);
					if (tuplep != null) {
						Pair<State, Operation> pair = tuplep.getFirst();
						Pair<Integer, Integer> p = (Pair)pair.getSecond().target;
						e = tuplep.getSecond();
						ep = p.getFirst();
						fp = p.getSecond();

						State enablingState = findEnablingState(stack, pair.getSecond(), op);
						System.out.printf("Conflict related mismatch %s enabling state: %s\n", tuplep, enablingState);

						assertConflictMismatch(enablingState, e, ep, f, fp);

						rhideOps.remove(op);
						n_cms.remove(pair);
					} else
						n_chs.add(new Pair<>(sigma, op));
					break;
				case MATCH:
				case MATCHNSHIFT:
					Pair<Pair<State, Operation>, Pair<Integer,Integer>> tupleq = findConflictingHideforMatch(chs, e, f);
					if (tupleq != null) {
						Pair<State, Operation> pair = tupleq.getFirst();
						Operation hideOp = pair.getSecond();
						ep = tupleq.getSecond().getFirst();
						fp = tupleq.getSecond().getSecond();

						State enablingState = null;
						if (hideOp.op == Op.LHIDE)
							enablingState = findEnablingState(stack, hideOp, op);
						else
							enablingState = findEnablingState(stack, op, hideOp);

						System.out.printf("Conflict related mismatch %s enabling state: %s\n", tupleq, enablingState);

						// Here, (e,f) refer to the matched events. That is why I changed the order of the parameters
						assertConflictMismatch(enablingState, ep, e, fp, f);

						if (hideOp.op == Op.LHIDE)
							lhideOps.remove(hideOp);
						else
							lhideOps.remove(hideOp);

						////// ===================================
						confMismatches.put(pair.getFirst(), pair.getSecond(), new Pair<>(sigma, op));
						confTableBridge.put(pair.getFirst(), pair.getSecond(), new Pair<Integer,Integer>(ep,fp));
						////// ===================================

						n_chs.remove(pair);
					} else
						n_cms.add(new Pair<>(sigma, op));
			}
			if (!visited.contains(op.nextState))
				findConflictMismatches(op.nextState, n_cms, n_chs, visited, stack);
			stack.pop();
		}
	}

	private Pair<Pair<State, Operation>, Pair<Integer, Integer>> findConflictingHideforMatch(Set<Pair<State, Operation>> chs, Integer e, Integer f) {

		for (Pair<State, Operation> pair: chs) {
			Operation op = pair.getSecond();
			if (op.op == Op.LHIDE) {
				Integer ep = (Integer)op.target;

				BitSet _fCauses = pes2.getCausesOf(f);
				Multiset<Integer> fCauses = HashMultiset.create();
				for (int ev = _fCauses.nextSetBit(0); ev >= 0; ev = _fCauses.nextSetBit(ev + 1))
					fCauses.add(ev);

				for (Integer fp: pes2.getPossibleExtensions(fCauses)) {
					// Immediate conflict: Event enabled by the same causes of f' which is in conflict with f'
					if (pes2.getBRelation(fp, f) == BehaviorRelation.CONFLICT &&
							pes2.getLabel(fp).equals(pes1.getLabel(ep)))
						return new Pair<>(pair, new Pair<>(ep, fp));
				}

			} else {
				Integer fp = (Integer)op.target;

				BitSet eCauses = pes1.getLocalConfiguration(e);
				eCauses.clear(e);
				BitSet dconf = (BitSet)pes1.getPossibleExtensions(eCauses).clone();
				dconf.and(pes1.getConflictSet(e));

				for (int ep = dconf.nextSetBit(0); ep >= 0; ep = dconf.nextSetBit(ep + 1))
					if (pes1.getLabel(ep).equals(pes2.getLabel(fp)))
						return new Pair<>(pair, new Pair<>(ep, fp));
			}
		}

		return null;
	}

	private Pair<Pair<State, Operation>, Integer> findConflictingMatchForLHide(Set<Pair<State, Operation>> cms, Integer e) {
		for (Pair<State, Operation> pair: cms) {
			Operation op = pair.getSecond();
			Pair<Integer,Integer> p = (Pair)op.target;
			Integer ep = p.getFirst();
			Integer fp = p.getSecond();

			BitSet _fpCauses = pes2.getCausesOf(fp);
			Multiset<Integer> fpCauses = HashMultiset.create();
			for (int ev = _fpCauses.nextSetBit(0); ev >= 0; ev = _fpCauses.nextSetBit(ev + 1))
				fpCauses.add(ev);

			for (Integer pe: pes2.getPossibleExtensions(fpCauses)) {
				// Immediate conflict: Event enabled by the same causes of f' which is in conflict with f'
				if (pes2.getBRelation(pe, fp) == BehaviorRelation.CONFLICT &&
						pes2.getLabel(pe).equals(pes1.getLabel(e)))
					return new Pair<>(pair, pe);
			}
		}
		return null;
	}

	private Pair<Pair<State, Operation>, Integer> findConflictingMatchForRHide(
			Set<Pair<State, Operation>> cms, Integer f) {
		for (Pair<State, Operation> pair: cms) {
			Operation op = pair.getSecond();
			Pair<Integer,Integer> p = (Pair)op.target;
			Integer ep = p.getFirst();
			Integer fp = p.getSecond();

			BitSet epCauses = pes1.getLocalConfiguration(ep);
			epCauses.clear(ep);
			BitSet dconf = (BitSet)pes1.getPossibleExtensions(epCauses).clone();
			dconf.and(pes1.getConflictSet(ep));

			for (int ev = dconf.nextSetBit(0); ev >= 0; ev = dconf.nextSetBit(ev + 1))
				if (pes1.getLabel(ev).equals(pes2.getLabel(f)))
					return new Pair<>(pair, ev);
		}
		return null;
	}

	private void findCausalityConcurrencyMismatches(State sigma, Set<Pair<State, Operation>> chs, Set<State> visited, LinkedList<Operation> stack, Map<Pair<Operation, Operation>, Pair<State, State>> pending) {
		visited.add(sigma);
		for (Operation op: descendants.get(sigma)) {
			stack.push(op);
			Pair<Integer,Integer> deltaEvents = getDeltaEvents(op);
			Integer e = deltaEvents.getFirst();
			Integer f = deltaEvents.getSecond();
			Set<Pair<State, Operation>> n_chs = new HashSet<>(chs);

//            if(pes1.getLabel(e).equals("_1_") || pes1.getLabel(e).equals("_0_")
//                    || pes2.getLabel(f).equals("_1_") || pes2.getLabel(f).equals("_0_"))
//                continue;

			switch (op.op) {
				case LHIDE:
					Pair<State, Operation> pair = findRHide(chs, e);
					if (pair != null) {
						f = (Integer)pair.getSecond().target;

						HashSet<Pair<List<Integer>,List<Integer>>> tuplePairs = findCausalInconsistency(sigma, e, f, stack);

						for(Pair<List<Integer>,List<Integer>> tuplePair : tuplePairs){
							if (tuplePair != null) {
								List<Integer> tuple = tuplePair.getFirst();

								State enablingState = findEnablingState(stack, op, pair.getSecond());
								System.out.printf("Causality/Concurrency mismatch %s enabling state: %s\n", tuple, enablingState);
								//						assertCausalityConcurrencyMismatch(pair.getFirst(), e, tuple.get(1), tuple.get(2), tuple.get(3));
								assertCausalityConcurrencyMismatch(enablingState, e, tuple.get(1), tuple.get(2), tuple.get(3));

								lhideOps.remove(op);
								rhideOps.remove(pair.getSecond());
								n_chs.remove(pair);
							} else
								pending.put(new Pair<>(op, pair.getSecond()), new Pair<>(sigma, pair.getFirst()));
						}
					} else
						n_chs.add(new Pair<>(sigma, op));
					break;
				case RHIDE:
				case RHIDENSHIFT:
					// Line 14:
					Pair<State, Operation> pairp = findLHide(chs, f);
					if (pairp != null) {
						e = (Integer)pairp.getSecond().target;

						// Line 15:
						HashSet<Pair<List<Integer>,List<Integer>>> tuplePairs = findCausalInconsistency(sigma, e, f, stack);
						for(Pair<List<Integer>,List<Integer>> tuplePair : tuplePairs){
							if (tuplePair != null) {
								List<Integer> tuple = tuplePair.getFirst();

								State enablingState = findEnablingState(stack, pairp.getSecond(), op);
								System.out.printf("Causality/Concurrency mismatch %s enabling state: %s\n", tuple, enablingState);
//						assertCausalityConcurrencyMismatch(pairp.getFirst(), e, tuple.get(1), tuple.get(2), tuple.get(3));
								assertCausalityConcurrencyMismatch(enablingState, e, tuple.get(1), tuple.get(2), tuple.get(3));

								rhideOps.remove(op);
								lhideOps.remove(pairp.getSecond());
								n_chs.remove(pairp);
							} else
								pending.put(new Pair<>(pairp.getSecond(), op), new Pair<>(pairp.getFirst(), sigma));
						}
					} else
						n_chs.add(new Pair<>(sigma, op));
					break;
				default:
					List<Pair<Operation, Operation>> toRemove = new ArrayList<>();
					for (Pair<Operation, Operation> opPair: pending.keySet()) {
						Integer ep = (Integer)opPair.getFirst().target;
						Integer fp = (Integer)opPair.getSecond().target;
						HashSet<Pair<List<Integer>,List<Integer>>> tuplePairs = findCausalInconsistency(sigma, ep, fp, stack);
						for(Pair<List<Integer>,List<Integer>> tuplePair : tuplePairs){
							if (tuplePair != null) {
								List<Integer> tuple = tuplePair.getFirst();

								Pair<State, State> states = pending.get(opPair);
								State leftState = states.getFirst(), rightState = states.getSecond();

								State enablingState = findEnablingState(stack, opPair.getFirst(), opPair.getSecond());
								System.out.printf("Causality/Concurrency mismatch %s enabling state: %s\n", tuple, enablingState);

								assertCausalityConcurrencyMismatch(enablingState, tuple.get(0), tuple.get(1), tuple.get(2), tuple.get(3));
								toRemove.add(opPair);
								n_chs.remove(new Pair<>(leftState, opPair.getFirst()));
								chs.remove(new Pair<>(leftState, opPair.getFirst()));
								n_chs.remove(new Pair<>(rightState, opPair.getSecond()));
								chs.remove(new Pair<>(rightState, opPair.getSecond()));
							} else {
//						throw new RuntimeException("Something wrong with a Causality/Concurrency mismatch" + opPair);
							}
						}
					}
					for (Pair<Operation, Operation> opPair: toRemove)
						pending.remove(opPair);

                    // TO CHECK
//					n_chs = retainCommutative(chs, e, f);

					Map<Integer, Pair<State, Operation>> lhides = new HashMap<>();
					Map<Integer, Pair<State, Operation>> rhides = new HashMap<>();
					for (Pair<State, Operation> p: chs) {
						if (!n_chs.contains(p)) {
							Operation oper = p.getSecond();
							Integer ev = (Integer)oper.target;
							if (oper.op == Op.LHIDE)
								lhides.put(ev, p);
							else if (!pes2.getInvisibleEvents().contains(ev))
								rhides.put(ev, p);
						}
					}
//				System.out.println("Left: " + lhides);
//				System.out.println("Right: " + rhides);

					while (!lhides.isEmpty() && !rhides.isEmpty()) {
						Set<Integer> left = new HashSet<>(lhides.keySet());
						TreeMultimap<String, Integer> lmap = TreeMultimap.create();
						for (Integer ev: lhides.keySet()) {
							BitSet dpred = pes1.getDirectPredecessors(ev);
							boolean found = false;
							for (int dp = dpred.nextSetBit(0); dp >= 0; dp = dpred.nextSetBit(dp + 1))
								if (left.contains(dp)) {
									found = true;
									break;
								}
							if (!found)
								lmap.put(pes1.getLabel(ev), ev);
						}

//					System.out.println("Left cand: " + lmap);

						Set<Integer> right = new HashSet<>(rhides.keySet());
						TreeMultimap<String, Integer> rmap = TreeMultimap.create();
						for (Integer ev: rhides.keySet()) {
							Collection<Integer> dpred = pes2.getDirectPredecessors(ev);
							boolean found = false;
							for (Integer dp: dpred)
								if (right.contains(dp)) {
									found = true;
									break;
								}
							if (!found)
								rmap.put(pes2.getLabel(ev), ev);
						}
//					System.out.println("Right cand: " + rmap);

						while (!lmap.isEmpty() && !rmap.isEmpty()) {
							Entry<String, Integer> lentry = lmap.entries().iterator().next();
							Entry<String, Integer> rentry = rmap.entries().iterator().next();
							lmap.remove(lentry.getKey(), lentry.getValue());
							rmap.remove(rentry.getKey(), rentry.getValue());

							Pair<State, Operation> lpair = lhides.get(lentry.getValue());
							Pair<State, Operation> rpair = rhides.get(rentry.getValue());

							lhides.remove(lentry.getValue());
							rhides.remove(rentry.getValue());

							State enablingState = null;

							if (lpair.getFirst().c1.cardinality() <= rpair.getFirst().c1.cardinality() &&
									lpair.getFirst().c2.size() <= rpair.getFirst().c2.size())
								enablingState = lpair.getFirst();
							else
								enablingState = rpair.getFirst();



							Pair<Integer, Integer> ef = new Pair<>(lentry.getValue(), rentry.getValue());
							Set<State> set = eventSubstitutionMismatches.get(ef);
							if (set == null)
								eventSubstitutionMismatches.put(ef, set = new HashSet<>());
							set.add(enablingState);

							rhideOps.remove(rpair.getSecond());
							lhideOps.remove(lpair.getSecond());
						}
					}
					break;
			}
			if (!visited.contains(op.nextState))
				findCausalityConcurrencyMismatches(op.nextState, n_chs, visited, stack, pending);
			stack.pop();
		}
	}

	private State findEnablingState(LinkedList<Operation> stack, Operation lop, Operation rop) {
		Integer e = null;
		if (lop.op == Op.LHIDE)
			e = (Integer) lop.target;
		else
			e = ((Pair<Integer,Integer>)lop.target).getFirst();

		for (int i = 0; i < stack.size(); i++) {
			Operation op = stack.get(i);
			State state = op.nextState;
			if (op.equals(lop) || op.equals(rop))
				continue;
			switch (op.op) {
				case MATCH:
				case MATCHNSHIFT:
					Pair<Integer, Integer> pair = (Pair)op.target;
					if (pes1.getBRelation(pair.getFirst(), e).equals(BehaviorRelation.CAUSALITY))
						return op.nextState;
				default:
					break;
			}
		}
		return null;
	}
	private void assertCausalityConcurrencyMismatch(State enablingState, Integer e, Integer ep, Integer f, Integer fp) {
		assertElementaryMismatch(causalityConcurrencyMismatches, enablingState, e, ep, f, fp);
	}
	private void assertConflictMismatch(State enablingState, Integer e, Integer ep, Integer f, Integer fp) {
		assertElementaryMismatch(conflictMismatches, enablingState, e, ep, f, fp);
	}
	private void assertElementaryMismatch(Map<Pair<Integer, Integer>, Multimap<State, Pair<Integer, Integer>>> mismatches, State enablingState, Integer e, Integer ep, Integer f, Integer fp) {
		Pair<Integer, Integer> ef = new Pair<>(e, f);
		Multimap<State, Pair<Integer, Integer>> mmap = mismatches.get(ef);
		if (mmap == null)
			mismatches.put(ef, mmap = HashMultimap.create());
		mmap.put(enablingState, new Pair<>(ep, fp));
	}

	private Set<Pair<State, Operation>> retainCommutative(Set<Pair<State, Operation>> set, Integer e, Integer f) {
		Set<Pair<State, Operation>> result = new HashSet<Pair<State,Operation>>();
		for (Pair<State, Operation> pair: set) {
			Operation oper = pair.getSecond();
			switch (oper.op) {
				case LHIDE:
					if (areCommutative(e, (Integer)oper.target, f, null))
						result.add(pair);
					break;
				case RHIDE:
				case RHIDENSHIFT:
					if (areCommutative(e, null, f, (Integer)oper.target))
						result.add(pair);
					break;
				default:
					Pair<Integer, Integer> p = (Pair)oper.target;
					if (areCommutative(e, p.getFirst(), f, p.getSecond()))
						result.add(pair);
					break;
			}
		}
		return result;
	}

	private boolean areCommutative(Integer e, Integer ep, Integer f, Integer fp) {
		return (e == null || ep == null || pes1.getBRelation(e, ep) == BehaviorRelation.CONCURRENCY) &&
				(f == null || fp == null || pes2.getBRelation(f, fp) == BehaviorRelation.CONCURRENCY);
	}
	private String translate(NewUnfoldingPESSemantics<Integer> pes, Integer f) {
		if (DEBUG)
			return String.format("%s(%d)", f != null ? pes.getLabel(f) : null, f);
		else
			return String.format("%s", f != null ? pes.getLabel(f) : null);
	}
	private String translate(PESSemantics<Integer> pes, Integer e) {
		if (DEBUG)
			return String.format("%s(%d)", pes.getLabel(e), e);
		else
			return String.format("%s", pes.getLabel(e));
	}

	private Pair<State, Operation> findRHide(Set<Pair<State, Operation>> chs, Integer e) {
		for (Pair<State, Operation> pair: chs) {
			Operation op = pair.getSecond();
			if (op.op == Op.RHIDE || op.op == Op.RHIDENSHIFT) {
				Integer f = (Integer) op.target;
				if (pes1.getLabel(e).equals(pes2.getLabel(f)))
					return pair;
			}
		}
		return null;
	}

	private Pair<State, Operation> findLHide(Set<Pair<State, Operation>> chs, Integer f) {
		for (Pair<State, Operation> pair: chs) {
			Operation op = pair.getSecond();
			if (op.op == Op.LHIDE) {
				Integer e = (Integer) op.target;
				if (pes1.getLabel(e).equals(pes2.getLabel(f)))
					return pair;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Pair<Integer, Integer> getDeltaEvents(Operation op) {
		if (op.op == Op.MATCH || op.op == Op.MATCHNSHIFT)
			return (Pair<Integer,Integer>)op.target;
		else if (op.op == Op.LHIDE)
			return new Pair<Integer,Integer>((Integer)op.target, null);
		else
			return new Pair<Integer,Integer>(null, (Integer)op.target);
	}

	public HashSet<Pair<List<Integer>, List<Integer>>> findCausalInconsistency(State sigma, Integer e, Integer f, LinkedList<Operation> stack) {
//		BitSet epred = pes1.getDirectPredecessors(e);
//		BitSet fpred = new BitSet();
//		for (Integer pred: pes2.getDirectPredecessors(f))
//			fpred.set(pred);

		HashSet<Pair<List<Integer>, List<Integer>>> pairDiff = new HashSet<>();

		BitSet epred = (BitSet) pes1.getStrictCausesOf(e).clone();
		BitSet fpred = (BitSet) pes2.getCausesOf(f).clone();

		List<Integer> cutoffs = new ArrayList<>();
		Integer localF = f;

		for (int i = 0; i < stack.size(); i++) {
			Operation op_i = stack.get(i);

			Pair<Integer, Integer> pair = getDeltaEvents(op_i);
			Integer ep = pair.getFirst();
			Integer fp = pair.getSecond();

			switch (op_i.op) {
				case MATCH:
					epred.clear(ep);
					fpred.clear(fp);

//                    if(pes2.getBRelation(localF, fp).equals(BehaviorRelation.INV_CAUSALITY)){
//                        Integer temporal = localF;
//                        localF = fp;
//                        fp = temporal;
//                    }
//
//                    if(pes1.getBRelation(e, ep).equals(BehaviorRelation.INV_CAUSALITY)){
//                        Integer temporal = ep;
//                        ep = e;
//                        e = temporal;
//                    }

					if (!pes1.getBRelation(e, ep).equals(BehaviorRelation.CONFLICT) && !pes2.getBRelation(localF, fp).equals(BehaviorRelation.CONFLICT)
//							&& (pes1.getBRelation(e, ep).equals(BehaviorRelation.CONCURRENCY) || pes2.getBRelation(localF, fp).equals(BehaviorRelation.CONCURRENCY))
                            && !causallyConsistent(e, ep, localF, fp))
						pairDiff.add(new Pair<>(Arrays.asList(e,ep,localF,fp), cutoffs));
//						return new Pair<>(Arrays.asList(e,ep,localF,fp), cutoffs);

					break;
				case LHIDE:
//					epred.or(pes1.getDirectPredecessors(ep));
					epred.or(pes1.getStrictCausesOf(ep));
					epred.clear(ep);
					break;
				case RHIDE:
//					for (Integer pred: pes2.getDirectPredecessors(fp))
//						fpred.set(pred);
					fpred.or(pes2.getCausesOf(f));
					fpred.clear(fp);
					break;
				case MATCHNSHIFT:
					epred.clear(ep);

					int corrFp = pes2.getCorresponding(fp);

					for (Integer pred: pes2.getDirectPredecessors(fp))
						fpred.set(pred);
					fpred.andNot(pes2.getLocalConfiguration(corrFp));

					cutoffs.add(fp);

					localF = pes2.unshift(localF, fp);

//                    if(pes2.getBRelation(localF, fp).equals(BehaviorRelation.INV_CAUSALITY)){
//                        Integer temporal = localF;
//                        localF = fp;
//                        fp = temporal;
//                    }
//
//                    if(pes1.getBRelation(e, ep).equals(BehaviorRelation.INV_CAUSALITY)){
//                        Integer temporal = ep;
//                        ep = e;
//                        e = temporal;
//                    }

                    if (!pes1.getBRelation(e, ep).equals(BehaviorRelation.CONFLICT) && !pes2.getBRelation(localF, fp).equals(BehaviorRelation.CONFLICT)
//                            && (pes1.getBRelation(e, ep).equals(BehaviorRelation.CONCURRENCY) || pes2.getBRelation(localF, fp).equals(BehaviorRelation.CONCURRENCY))
                            && !causallyConsistent(e, ep, localF, fp))
                        pairDiff.add(new Pair<>(Arrays.asList(e,ep,localF,fp), cutoffs));
//						return new Pair<>(Arrays.asList(e,ep,localF,fp), cutoffs);
					break;
				case RHIDENSHIFT:
					localF = pes2.unshift(localF, fp);

					int corrFpp = pes2.getCorresponding(fp);
					for (Integer pred: pes2.getDirectPredecessors(fp))
						fpred.set(pred);
					fpred.andNot(pes2.getLocalConfiguration(corrFpp));

					cutoffs.add(fp);
					break;
			}
			if (epred.isEmpty() && fpred.isEmpty())
				break;
		}
		return pairDiff;
	}

	private boolean causallyConsistent(Integer e, Integer ep, Integer f, Integer fp) {
		return pes1.getBRelation(e, ep) == pes2.getBRelation(f, fp);
	}

	private void addPSPBranchToGlobalPSP(List<Operation> opSeq) {
		State pred = root;

		for (int i = 0; i < opSeq.size(); i++) {
            Operation curr = opSeq.get(i);

            if (curr.op == Op.RHIDE || curr.op == Op.RHIDENSHIFT){
                if (!pes2.getInvisibleEvents().contains(curr.target))
                    rhideOps.add(curr);
            }else if (curr.op == Op.LHIDE)
                lhideOps.add(curr);

			State state = curr.nextState;
			Map<Multiset<String>, State> map = stateSpace.get(state.c1, state.c2);
			if (map == null)
				stateSpace.put(state.c1, state.c2, map = new HashMap<>());
			if (map.containsKey(state.labels)) {
				state = map.get(state.labels);
				curr.nextState = state;
			} else
				map.put(state.labels, state);

			boolean found = false;
			for (Operation desc: descendants.get(pred))
				if (desc.op == curr.op) {
					if (curr.op == Op.MATCH || curr.op == Op.MATCHNSHIFT) {
						Pair<Integer, Integer> pair1 = (Pair)curr.target;
						Pair<Integer, Integer> pair2 = (Pair)desc.target;
						if (pair1.equals(pair2)) {
							found = true;
							break;
						}
					} else {
						Integer ev1 = (Integer)curr.target;
						Integer ev2 = (Integer)desc.target;
						if (ev1.equals(ev2)) {
							found = true;
							break;
						}
					}
				}
			if (!found) {
				descendants.put(pred, curr);
				ancestors.put(curr.nextState, pred);
			}
			pred = state;
		}
	}

	public String toDot() {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);

		out.println("digraph G {");

		out.println("\tnode[shape=box];");
		int i = 0;

		out.printf("\tn%d [label=\"%s\"];\n", root.hashCode(), i++);

		for (Operation op: descendants.values())
			out.printf("\tn%d [label=\"%s\"];\n", op.nextState.hashCode(), op.nextState);

		for (Entry<State,Operation> entry: descendants.entries())
			out.printf("\tn%d -> n%d [label=\"%s\"];\n", entry.getKey().hashCode(), entry.getValue().nextState.hashCode(), entry.getValue());

		out.println("}");

		return str.toString();
	}

    public void printModels(String prefix, String suffix, PetriNet net, BPMNReader loader, HashMap<Object, String> colorsPN,
                            HashMap<String, String> colorsBPMN1, HashMap<String, Integer> repetitions1, HashMap<String, Integer> repetitions2) {
        Random r = new Random();
        int rand = r.nextInt();

        try {
            PrintStream out = new PrintStream("target/tex/difference" + prefix
                    + "-" + rand + "-" + suffix + ".dot");

            if (colorsPN != null) {
                out.print(net.toDot(colorsPN));
                out.close();
            }

            out = new PrintStream("target/tex/difference" + prefix + "-" + rand
                    + "-" + suffix + "BPMN.dot");
            @SuppressWarnings("unchecked")
            String modelColor = printBPMN2DOT(colorsBPMN1, (Bpmn<BpmnControlFlow<FlowNode>, FlowNode>)loader.getModel(), loader, repetitions1, repetitions2);
            out.print(modelColor);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String printBPMN2DOT(HashMap<String, String> colorsUnf,
                                 Bpmn<BpmnControlFlow<FlowNode>, FlowNode> model,
                                 BPMNReader loader, HashMap<String, Integer> repetitions1,
                                 HashMap<String, Integer> repetitions2) {
        String result = "";

        if (repetitions2 == null)
            repetitions2 = new HashMap<String, Integer>();

        result += "digraph G {\n";
        result += "rankdir=LR \n";

        for (Event e : model.getEvents()) {
            if (colorsUnf.containsKey(e.getId())) {
                result += String
                        .format("  n%s[shape=ellipse,label=\"%s(x %s)(x%s)\", color=\"%s\"];\n",
                                e.getId().replace("-", ""), e.getName(),
                                "",
                                "",
                                colorsUnf.get(e.getId()));
            } else
                result += String.format("  n%s[shape=ellipse,label=\"%s\"];\n",
                        e.getId().replace("-", ""), e.getName());
        }
        result += "\n";

        for (Activity a : model.getActivities()) {
            if (colorsUnf.containsKey(a.getId()))
                result += String
                        .format("  n%s[shape=box,label=\"%s(x%s)(x%s)\",color=\"%s\"];\n",
                                a.getId().replace("-", ""), a.getName(),
                                "",
                                "",
                                colorsUnf.get(a.getId()));
            else
                result += String.format("  n%s[shape=box,label=\"%s\"];\n", a
                        .getId().replace("-", ""), a.getName());
        }
        result += "\n";

        for (Gateway g : model.getGateways(AndGateway.class)) {
            if (colorsUnf.containsKey(g.getId()))
                result += String
                        .format("  n%s[shape=diamond,label=\"%s(x%s)(x%s)\", color=\"%s\"];\n",
                                g.getId().replace("-", ""), "AND",
                                "",
                                "",
                                colorsUnf.get(g.getId()));
            else
                result += String.format("  n%s[shape=diamond,label=\"%s\"];\n",
                        g.getId().replace("-", ""), "AND");
        }
        for (Gateway g : model.getGateways(XorGateway.class)) {
            if (colorsUnf.containsKey(g.getId()))
                result += String
                        .format("  n%s[shape=diamond,label=\"%s(x%s)(x%s)\", color=\"%s\"];\n",
                                g.getId().replace("-", ""), "XOR",
                                "",
                                "",
                                colorsUnf.get(g.getId()));
            else
                result += String.format("  n%s[shape=diamond,label=\"%s\"];\n",
                        g.getId().replace("-", ""), "XOR");
        }
        for (Gateway g : model.getGateways(OrGateway.class)) {
            if (colorsUnf.containsKey(g.getId()))
                result += String
                        .format("  n%s[shape=diamond,label=\"%s(x%s)(x%s)\", color=\"%s\"];\n",
                                g.getId().replace("-", ""), "OR",
                                "",
                                "",
                                colorsUnf.get(g.getId()));
            else
                result += String.format("  n%s[shape=diamond,label=\"%s\"];\n",
                        g.getId().replace("-", ""), "OR");
        }
        for (Gateway g : model.getGateways(AlternativGateway.class))
            result += String.format("  n%s[shape=diamond,label=\"%s\"];\n", g
                    .getId().replace("-", ""), "?");
        result += "\n";

        for (DataNode d : model.getDataNodes()) {
            result += String.format("  n%s[shape=note,label=\"%s\"];\n", d
                            .getId().replace("-", ""),
                    d.getName().concat(" [" + d.getState() + "]"));
        }
        result += "\n";

        for (ControlFlow<FlowNode> cf : model.getControlFlow()) {
            if (cf.getLabel() != null && cf.getLabel() != "")
                result += String.format("  n%s->n%s[label=\"%s\"];\n", cf
                        .getSource().getId().replace("-", ""), cf.getTarget()
                        .getId().replace("-", ""), cf.getLabel());
            else
                result += String.format("  n%s->n%s;\n", cf.getSource().getId()
                                .replace("-", ""),
                        cf.getTarget().getId().replace("-", ""));
        }
        result += "\n";

        for (Activity a : model.getActivities()) {
            for (IDataNode d : a.getReadDocuments()) {
                result += String.format("  n%s->n%s;\n",
                        d.getId().replace("-", ""), a.getId().replace("-", ""));
            }
            for (IDataNode d : a.getWriteDocuments()) {
                result += String.format("  n%s->n%s;\n",
                        a.getId().replace("-", ""), d.getId().replace("-", ""));
            }
        }
        result += "}";

        return result;
    }

	private DifferenceML printTasksHL(Integer startEvent, List<Integer> events, NewUnfoldingPESSemantics<Integer> pes, PetriNet net, BPMNReader loader, String sentence, float ranking) {
		List<String> start = new ArrayList<>();
		List<String> a = new ArrayList<>();
		List<String> end = new ArrayList<>();
		List<String> greys = new ArrayList<>();

        boolean markEnd = false;

		BitSet inter = null;
		BitSet union = null;

//        HashMap<String, String> aColors = new HashMap<>();

		for(Integer event : events){
            if(event == -1) {
                markEnd = true;
                continue;
            }

            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

			BitSet conf1 = pes.getLocalConfiguration(event);
			Trace<Integer> trace = new Trace<>();
			trace.addAllStrongCauses(pes.getEvents(conf1));

            FlowNode task = model.getTaskFromEvent(event);
            a.add(task.getId());

			if(inter == null)
				inter = (BitSet) conf1.clone();
			else
				inter.and(conf1);

			if(union == null)
				union = (BitSet) conf1.clone();
			else
				union.or(conf1);
		}

        for(Integer event : events)
            if(event >= 0)
                inter.set(event, false);

//		Pomset pomset = pes.getPomset(inter, commonLabels);
//		DirectedGraph g = pomset.getGraph();
//		HashSet<Integer> sinks = new HashSet<>();
//		for(Vertex v : g.getVertices())
//			if(g.getEdgesWithSource(v).isEmpty())
//				sinks.add(pomset.getMap().get(v));

        HashMap<String, String> startColors = new HashMap<>();

        if(startEvent == -2){
            FlowNode task = model.getStart();
            start.add(task.getId());
        } else if(commonLabels.contains(pes.getLabel(startEvent))){
			BitSet conf1 = pes.getLocalConfiguration(startEvent);
			Trace<Integer> trace = new Trace<>();
			trace.addAllStrongCauses(pes.getEvents(conf1));

            FlowNode task = model.getTaskFromEvent(startEvent);
            if(model.getBpmnModel().getDirectSuccessors(task).size() == 1 &&
                    (model.getBpmnModel().getDirectSuccessors(task).iterator().next() instanceof AndGateway ||
                    model.getBpmnModel().getDirectSuccessors(task).iterator().next() instanceof XorGateway))
                start.add(model.getBpmnModel().getDirectSuccessors(task).iterator().next().getId());
            else
                start.add(task.getId());
		}

        HashSet<FlowNode> tasks = new HashSet<>();
        for(Integer node : events)
            tasks.add(model.getTaskFromEvent(node));
        Pair<FlowNode, HashSet<FlowNode>> nextCommon = getNextCommon(tasks);
        end.add(nextCommon.getFirst().getId());

//        Queue<Multiset<Integer>> queue = new LinkedList<>();
//        Multiset ms = getMultiset(union);
//        queue.offer(ms);
//        HashSet<Multiset<Integer>> visited = new HashSet<>();
//        visited.add(ms);
//
//        HashMap<String, String> endColors = new HashMap<>();
//
//        while(!queue.isEmpty()) {
//            Multiset<Integer> current = queue.poll();
//            Set<Integer> extensions = pes.getPossibleExtensions(current);
//            endColors = new HashMap<>();
//
//            for (Integer event : extensions) {
//                if (!commonLabels.contains(pes.getLabel(event)))
//                    continue;
//
//                BitSet conf1 = pes.getLocalConfiguration(event);
//                Trace<Integer> trace = new Trace<>();
//                trace.addAllStrongCauses(pes.getEvents(conf1));
//
//                FlowNode task = model.getTaskFromEvent(event);
//                end.add(task.getId());
//            }
//
//            if(end.isEmpty()) {
//                for(Integer ext : extensions) {
//                    Multiset<Integer> copy = HashMultiset.<Integer> create(current);
//                    copy.add(ext);
//                    if(!visited.contains(copy)){
//                        queue.add(copy);
//                        visited.add(copy);
//                    }
//                }
//            }else
//                break;
//        }

//        for(String element : aColors.keySet())
//            if(!startColors.containsKey(element) && !start.contains(element))
//                greys.add(element);

        for(FlowNode element : nextCommon.getSecond())
            if(!startColors.containsKey(element.getId()) && !start.contains(element.getId()) && !a.contains(element.getId()))
                greys.add(element.getId());

        HashSet<String> allReleventEdges = new HashSet<>();
        allReleventEdges.addAll(start);
        allReleventEdges.addAll(end);
        allReleventEdges.addAll(a);
        allReleventEdges.addAll(greys);
        HashSet<String> flows = getEdgesBetween(allReleventEdges);
        for(Integer n1 : events)
            for(Integer n2 : events){
                FlowNode n1Node = model.getTaskFromEvent(n1);
                FlowNode n2Node = model.getTaskFromEvent(n2);
                if(model.getBpmnModel().getDirectedEdge(n1Node, n2Node)!= null)
                    flows.remove(model.getBpmnModel().getDirectedEdge(n1Node, n2Node).getId());
                if(model.getBpmnModel().getDirectedEdge(n2Node, n1Node)!= null)
                    flows.remove(model.getBpmnModel().getDirectedEdge(n2Node, n1Node).getId());
            }
        greys.addAll(flows);
        for(FlowNode nodeModel : model.getBpmnModel().getFlowNodes())
            if(nodeModel instanceof Activity && greys.contains(nodeModel.getId())) {
                for (ControlFlow flow : model.getBpmnModel().getIncomingEdges(nodeModel))
                    greys.add(flow.getId());

                for (ControlFlow flow : model.getBpmnModel().getOutgoingEdges(nodeModel))
                    greys.add(flow.getId());
            }


		DifferenceML diff = new DifferenceML(ranking);
		diff.setSentence(sentence);
		diff.setA(a);
		diff.setStart(start);
		diff.setEnd(end);
        diff.setGreys(greys);

        // For testing
        HashMap<String, String> newColorsBP = new HashMap<>();
        for (String s : a)
            newColorsBP.put(s, "red");

        for (String s : start)
            newColorsBP.put(s, "blue");

        for (String s : end)
            newColorsBP.put(s, "blue");

        for (String s : greys)
            newColorsBP.put(s, "gray");

//        printModels("m", "1", net, loader, null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

		return diff;
	}

    public Pair<FlowNode, HashSet<FlowNode>> getNextCommon(HashSet<FlowNode> originators){
        Queue<Pair<FlowNode, FlowNode>> queue = new LinkedList<>();
        for(FlowNode node : originators)
            queue.add(new Pair<FlowNode, FlowNode>(node, node));

        HashMap<FlowNode, HashSet<FlowNode>> marked = new HashMap<>();
        while(!queue.isEmpty()){
            Pair<FlowNode, FlowNode> pair = queue.poll();

            if(!marked.containsKey(pair.getSecond()))
                marked.put(pair.getSecond(), new HashSet<FlowNode>());

            marked.get(pair.getSecond()).add(pair.getFirst());
            if(marked.get(pair.getSecond()).containsAll(originators) && !originators.contains(pair.getSecond())) {
                HashSet<FlowNode> visited = new HashSet<>(marked.keySet());
                visited.remove(pair.getSecond());
                return new Pair(pair.getSecond(), visited);
            }

            for(FlowNode successor : model.getBpmnModel().getDirectSuccessors(pair.getSecond()))
                queue.add(new Pair<FlowNode, FlowNode>(pair.getFirst(), successor));
        }

        return null;
    }

    private DifferenceML printTasksHLRep(Integer startEvent, List<Integer> events, NewUnfoldingPESSemantics<Integer> pes, PetriNet net, BPMNReader loader, String sentence, float ranking) {
        List<String> start = new ArrayList<>();
        List<String> end = new ArrayList<>();
        List<String> greys = new ArrayList<>();

        boolean markEnd = false;

        BitSet inter = null;
        BitSet union = null;
        HashMap<String, String> startColors = new HashMap<>();

//        for(Integer event : events){
//            if(event == -1) {
//                markEnd = true;
//                continue;
//            }
//
//            if(!commonLabels.contains(pes.getLabel(event)))
//                continue;
//
//            BitSet conf1 = pes.getLocalConfiguration(event);
//            Trace<Integer> trace = new Trace<>();
//            trace.addAllStrongCauses(pes.getEvents(conf1));

            FlowNode task = model.getTaskFromEvent(startEvent);
            start.add(task.getId());

//            if(inter == null)
//                inter = (BitSet) conf1.clone();
//            else
//                inter.and(conf1);
//
//            if(union == null)
//                union = (BitSet) conf1.clone();
//            else
//                union.or(conf1);
//        }

//        Queue<Multiset<Integer>> queue = new LinkedList<>();
//        Multiset ms = getMultiset(union);
//        queue.offer(ms);
//        HashSet<Multiset<Integer>> visited = new HashSet<>();
//        visited.add(ms);

        HashMap<String, String> endColors = new HashMap<>();

//        while(!queue.isEmpty()) {
//            Multiset<Integer> current = queue.poll();
//            Set<Integer> extensions = pes.getPossibleExtensions(current);
//            endColors = new HashMap<>();

            for (Integer event : pes.getDirectSuccessors(startEvent)) {
                if (!commonLabels.contains(pes.getLabel(event)))
                    continue;

                BitSet conf1 = pes.getLocalConfiguration(event);
                Trace<Integer> trace = new Trace<>();
                trace.addAllStrongCauses(pes.getEvents(conf1));

                FlowNode task2Add = model.getTaskFromEvent(event);
                end.add(task2Add.getId());
            }


            if(end.isEmpty()) {
                end.add(model.getEnd().getId());
//                for(Integer ext : extensions) {
//                    Multiset<Integer> copy = HashMultiset.<Integer> create(current);
//                    copy.add(ext);
//                    if(!visited.contains(copy)){
//                        queue.add(copy);
//                        visited.add(copy);
//                    }
//                }
//            }else
//                break;
        }

//        for(String element : startColors.keySet())
//            if(!startColors.containsKey(element) && !start.contains(element))
//                greys.add(element);
//
//        for(String element : endColors.keySet())
//            if(!startColors.containsKey(element) && !start.contains(element) && !start.contains(element))
//                greys.add(element);

        HashSet<String> allReleventEdges = new HashSet<>();
        allReleventEdges.addAll(start);
        allReleventEdges.addAll(end);
//        allReleventEdges.addAll(greys);
        HashSet<String> flows = getEdgesBetween(allReleventEdges);
        greys.addAll(flows);

        List<String> newTasks = new LinkedList<>();
        for(Integer event : events) {
            if (event == -1) {
                markEnd = true;
                continue;
            }
            if (commonLabels.contains(pes.getLabel(event)))
                newTasks.add(pes.getLabel(event));
        }

        for(FlowNode nodeModel : model.getBpmnModel().getFlowNodes())
            if(nodeModel instanceof Activity && greys.contains(nodeModel.getId())) {
                for (ControlFlow flow : model.getBpmnModel().getIncomingEdges(nodeModel))
                    greys.add(flow.getId());

                for (ControlFlow flow : model.getBpmnModel().getOutgoingEdges(nodeModel))
                    greys.add(flow.getId());
            }

        DifferenceML diff = new DifferenceML(ranking);
        diff.setSentence(sentence);
        diff.setStart(start);
        diff.setEnd(end);
        diff.setGreys(greys);
        diff.setNewTasks(newTasks);

        // For testing
        HashMap<String, String> newColorsBP = new HashMap<>();

        for (String s : start)
            newColorsBP.put(s, "blue");

        for (String s : end)
            newColorsBP.put(s, "blue");

        for (String s : greys)
            newColorsBP.put(s, "gray");

//        printModels("m", "1", net, loader, null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

        return diff;
    }

	private DifferenceML printTasksHL2(Integer startEvt, List<Integer> events, NewUnfoldingPESSemantics<Integer> pes, PetriNet net, BPMNReader loader, String sentence, float ranking) {
		List<String> start = new ArrayList<>();
		List<String> a = new ArrayList<>();
		List<String> end = new ArrayList<>();
		List<String> greys = new ArrayList<>();

        // Order events
        int[] causesCard = new int[events.size()];
        int l = 0;
        for(Integer ev : events) {
            causesCard[l] = pes.getCausesOf(ev).cardinality();
            l++;
        }

        Arrays.sort(causesCard);
        List<Integer> eventsOrd = new LinkedList<>();
        for(int j = 0; j < causesCard.length; j++){
            for (int k = 0; k < events.size(); k++){
                if(events.get(k) > -1 && pes.getCausesOf(events.get(k)).cardinality() == causesCard[j]){
                    eventsOrd.add(events.get(k));
                    events.set(k,-1);
                    break;
                }
            }
        }
        // finish ordering events

		BitSet inter = null;
		BitSet union = null;

		List<Integer> visibleEvents = new ArrayList<>();
		List<String> visibleLabels = new ArrayList<>();
		for(Integer ev : eventsOrd)
			if(model.getLabels().contains(pes.getLabel(ev))) {
				visibleEvents.add(ev);
				visibleLabels.add(pes.getLabel(ev));
			}

//		HashMap<String, String> aColors = new HashMap<>();

		for(Integer event : visibleEvents){
			if(!commonLabels.contains(pes.getLabel(event)))
				continue;

			BitSet conf1 = pes.getLocalConfiguration(event);
			Trace<Integer> trace = new Trace<>();
			trace.addAllStrongCauses(pes.getEvents(conf1));

            FlowNode task = model.getTaskFromEvent(event);
            a.add(task.getId());

			if(inter == null)
				inter = (BitSet) conf1.clone();
			else
				inter.and(conf1);

			if(union == null)
				union = (BitSet) conf1.clone();
			else
				union.or(conf1);

            inter.set(event, false);
		}

        if(inter == null)
            return null;

        for(Integer event : events)
            if(event > -1)
                inter.set(event, false);

        Pomset pomset = pes.getPomset(inter, commonLabels);
        DirectedGraph g = pomset.getGraph();
        HashSet<Integer> sinks = new HashSet<>();
        for(Vertex v : g.getVertices())
            if(g.getEdgesWithSource(v).isEmpty())
                sinks.add(pomset.getMap().get(v));

        HashMap<String, String> startColors = new HashMap<>();
        List<Integer> startEvts = new ArrayList<>();

        HashSet<FlowNode> history = new HashSet<>();
        for(Integer event : events)
            if(event > -1)
                history.addAll(model.getTasksFromConf(pes.getLocalConfiguration(event)));

        for(Integer event : sinks){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            start.add(model.getTaskFromEvent(event).getId());
            history.removeAll(model.getTasksFromConf(pes.getLocalConfiguration(event)));
        }

        for(FlowNode element : history)
            if(!start.contains(element.getId()) && !a.contains(element.getId()))
                greys.add(element.getId());

		Set<String> futureLabels =  null;

		for(Integer event : eventsOrd){
			if(futureLabels == null)
				futureLabels = pes.getPossibleFutureAsLabels(getMultiset(pes.getLocalConfiguration(event)));
			else
				futureLabels.retainAll(pes.getPossibleFutureAsLabels(getMultiset(pes.getLocalConfiguration(event))));
		}

		Set<String> visibleFinalEvents = new HashSet<>();
		for(String ev : futureLabels)
			if(model.getLabels().contains(ev) && !visibleLabels.contains(ev))
				visibleFinalEvents.add(ev);

		HashMap<String, String> endColors = new HashMap<>();
		HashMap<String, String> colorsBPMN = getEnd(visibleEvents.get(0), visibleEvents.get(0), visibleFinalEvents);
		for (Entry<String, String> entry : colorsBPMN.entrySet()) {
			if (entry.getValue().equals("red"))
				end.add(entry.getKey());
			else if(!start.contains(entry.getKey()) && !a.contains(entry.getKey()) && !end.contains(entry.getKey()))
				endColors.put(entry.getKey(), "green");
		}

		for(String element : endColors.keySet())
			if(!startColors.containsKey(element) && !start.contains(element) && !a.contains(element))
				greys.add(element);

		HashSet<String> allReleventEdges = new HashSet<>();
		allReleventEdges.addAll(start);
		allReleventEdges.addAll(end);
		allReleventEdges.addAll(a);
		allReleventEdges.addAll(greys);
		HashSet<String> flows = getEdgesBetween(allReleventEdges);
		greys.addAll(flows);

        for(FlowNode nodeModel : model.getBpmnModel().getFlowNodes())
            if(nodeModel instanceof Activity && greys.contains(nodeModel.getId())) {
                for (ControlFlow flow : model.getBpmnModel().getIncomingEdges(nodeModel))
                    greys.add(flow.getId());

                for (ControlFlow flow : model.getBpmnModel().getOutgoingEdges(nodeModel))
                    greys.add(flow.getId());
            }

		DifferenceML diff = new DifferenceML(ranking);
		diff.setSentence(sentence);
		diff.setA(a);
		diff.setStart(start);
		diff.setEnd(end);
		diff.setGreys(greys);

		// For testing
		HashMap<String, String> newColorsBP = new HashMap<>();
		for (String s : start)
			newColorsBP.put(s, "blue");

		for (String s : end)
			newColorsBP.put(s, "blue");

		for (String s : greys)
			newColorsBP.put(s, "gray");

		for (String s : a)
			newColorsBP.put(s, "red");

//        printModels("m", "1", net, loader, null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

		return diff;
	}

    private DifferenceML printTasksHLstEnd(Integer startEvt, Integer endEvt, NewUnfoldingPESSemantics<Integer> pes, PetriNet net, BPMNReader loader, String sentence, float ranking) {
        List<String> start = new ArrayList<>();
        List<String> end = new ArrayList<>();

        BitSet inter = null;
        BitSet union = null;

        HashMap<String, String> startColors = new HashMap<>();
        List<Integer> startEvts = new ArrayList<>();
        startEvts.add(startEvt);


        for(Integer event : startEvts){
            if(event < -1) {
                startColors.put(model.getStart().getId(), "green");
                continue;
            }

            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            BitSet conf1 = pes.getLocalConfiguration(event);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf1));

            FlowNode task = model.getTaskFromEvent(event);
            start.add(task.getId());
        }

        HashMap<String, String> endColors = new HashMap<>();
        List<Integer> endEvts = new ArrayList<>();
        endEvts.add(endEvt);

        for(Integer event : endEvts) {
            if (event < 0) {
                endColors.put(model.getEnd().getId(), "green");
                end.add(model.getEnd().getId());
                continue;
            }

            if (!commonLabels.contains(pes.getLabel(event)))
                continue;


            BitSet conf1 = pes.getLocalConfiguration(event);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf1));

            FlowNode task = model.getTaskFromEvent(event);
            end.add(task.getId());
        }

        List<String> greys = new ArrayList<>();
        HashSet<String> allReleventEdges = new HashSet<>();
        allReleventEdges.addAll(start);
        allReleventEdges.addAll(end);
        HashSet<String> flows = getEdgesBetween(allReleventEdges);
        greys.addAll(flows);

        for(FlowNode nodeModel : model.getBpmnModel().getFlowNodes())
            if(nodeModel instanceof Activity && greys.contains(nodeModel.getId())) {
                for (ControlFlow flow : model.getBpmnModel().getIncomingEdges(nodeModel))
                    greys.add(flow.getId());

                for (ControlFlow flow : model.getBpmnModel().getOutgoingEdges(nodeModel))
                    greys.add(flow.getId());
            }

        DifferenceML diff = new DifferenceML(ranking);
        diff.setSentence(sentence);
        diff.setStart(start);
        diff.setEnd(end);
        diff.setGreys(greys);

        // For testing
        HashMap<String, String> newColorsBP = new HashMap<>();
        for (String s : start)
            newColorsBP.put(s, "blue");

        for (String s : end)
            newColorsBP.put(s, "blue");

        for (String s : greys)
            newColorsBP.put(s, "gray");

//        printModels("m", "1", net, loader, null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

        return diff;
    }

    private DifferenceML print2Tasks(Integer context, Integer event1, Integer event2, NewUnfoldingPESSemantics<Integer> pes, PetriNet net, BPMNReader loader, String sentence, float ranking) {
        if(!commonLabels.contains(pes.getLabel(event1)) && !commonLabels.contains(pes.getLabel(event2)))
            return null;

        List<String> start = new ArrayList<>();
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        List<String> end = new ArrayList<>();
        List<String> greys = new ArrayList<>();

        a.add(model.getTaskFromEvent(event1).getId());
        b.add(model.getTaskFromEvent(event2).getId());

        if(a.get(0).contains("sid-6EAB3821-2A7B-4CD0-B8A1-83B6653983C5") || b.get(0).contains("sid-6EAB3821-2A7B-4CD0-B8A1-83B6653983C5")){
            System.out.println("Error in my canonizer!");
            model.getTaskFromEvent(event1);
            model.getTaskFromEvent(event2);
        }

        BitSet conf1 = pes.getLocalConfiguration(event1);
        BitSet conf2 = pes.getLocalConfiguration(event2);

        HashSet<String> matched = getMatchedActivities();

//        BitSet inter = (BitSet) conf1.clone();
//        inter.and((BitSet) conf2.clone());
//
//        inter.set(event1, false);
//        inter.set(event2, false);

//        Pomset pomset = pes.getPomset(inter, commonLabels);
//        DirectedGraph g = pomset.getGraph();
//        HashSet<Integer> sinks = new HashSet<>();
//        for(Vertex v : g.getVertices())
//            if(g.getEdgesWithSource(v).isEmpty())
//                sinks.add(pomset.getMap().get(v));

        HashSet<FlowNode> history1 = model.getTasksFromConf(pes.getLocalConfiguration(event1));
        HashSet<FlowNode> history2 = model.getTasksFromConf(pes.getLocalConfiguration(event2));

//        for(Integer event : sinks)
        {
//            if(!commonLabels.contains(pes.getLabel(context)))
//                continue;
            if(context == -1)
                start.add(model.getStart().getId());
            else
                start.add(model.getTaskFromEvent(context).getId());
            history1.removeAll(model.getTasksFromConf(pes.getLocalConfiguration(context)));
            history2.removeAll(model.getTasksFromConf(pes.getLocalConfiguration(context)));
        }

        Set<String> future1 = pes.getPossibleFutureAsLabels(getMultiset(conf1));
        Set<String> future2 = pes.getPossibleFutureAsLabels(getMultiset(conf2));
        future1.retainAll(future2);

		HashSet<FlowNode> tasks = new HashSet<>();
		tasks.add(model.getTaskFromEvent(event1));
		tasks.add(model.getTaskFromEvent(event2));

        HashMap<String, String> endColors = new HashMap<>();
        HashMap<String, String> colorsBPMN = getEnd(event1, event2, future1);
        for (Entry<String, String> entry : colorsBPMN.entrySet()) {
            if (entry.getValue().equals("red"))
                end.add(entry.getKey());
            else if(!start.contains(entry.getKey()) && !a.contains(entry.getKey()) && !end.contains(entry.getKey()))
                endColors.put(entry.getKey(), "green");
        }

//        Pair<FlowNode, HashSet<FlowNode>> nextCommon = getNextCommon(tasks);
//        end.add(nextCommon.getFirst().getId());

        for(String element : endColors.keySet())
            if(!start.contains(element) && !a.contains(element) && !b.contains(element) && !matched.contains(element))
                greys.add(element);

//		Pair<FlowNode, HashSet<FlowNode>> nextCommon = getNextCommon(tasks);
//		end.add(nextCommon.getFirst().getId());
//		for(FlowNode element : nextCommon.getSecond())
//			if(!start.contains(element.getId()) && !a.contains(element.getId()) && !b.contains(element.getId()))
//				greys.add(element.getId());

        for(FlowNode element : history1)
            if(!start.contains(element.getId()) && !a.contains(element.getId()) && !b.contains(element.getId()) && !matched.contains(element.getId()))
                greys.add(element.getId());

        for(FlowNode element : history2)
            if(!start.contains(element.getId()) && !a.contains(element.getId()) && !b.contains(element.getId()) && !matched.contains(element.getId()))
                greys.add(element.getId());

        HashSet<String> allReleventEdges = new HashSet<>();
        allReleventEdges.addAll(start);
        allReleventEdges.addAll(end);
        allReleventEdges.addAll(b);
        allReleventEdges.addAll(a);
        allReleventEdges.addAll(greys);
        HashSet<String> flows = getEdgesBetween(allReleventEdges);
        greys.addAll(flows);

        for(FlowNode nodeModel : model.getBpmnModel().getFlowNodes())
            if(nodeModel instanceof Activity && greys.contains(nodeModel.getId())) {
                for (ControlFlow flow : model.getBpmnModel().getIncomingEdges(nodeModel))
                    greys.add(flow.getId());

                for (ControlFlow flow : model.getBpmnModel().getOutgoingEdges(nodeModel))
                    greys.add(flow.getId());
            }

        DifferenceML diff = new DifferenceML(ranking);
        diff.setSentence(sentence);
        diff.setA(a);
        diff.setB(b);
        diff.setStart(start);
        diff.setEnd(end);
        diff.setGreys(greys);

        // For testing
        HashMap<String, String> newColorsBP = new HashMap<>();
        for (String s : a)
            newColorsBP.put(s, "red");

        for (String s : b)
            newColorsBP.put(s, "red");

        for (String s : start)
            newColorsBP.put(s, "blue");

        for (String s : end)
            newColorsBP.put(s, "blue");

        for (String s : greys)
            newColorsBP.put(s, "gray");

//        printModels("m", "1", net, loader, null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

        return diff;
    }

    public HashSet<String> getMatchedActivities(){
        HashSet<String> matchedAct = new HashSet<>();

        for(Entry<State, Operation> entry : lastMatchMap.entrySet()){
            if(entry.getValue() == null)
                continue;
            Pair<Integer, Integer> pair = (Pair<Integer, Integer>) entry.getValue().target;
            if(pes2.getLabel(pair.getSecond()).equals("_0_") || pes2.getLabel(pair.getSecond()).equals("_1_"))
                continue;

            matchedAct.add(model.getTaskFromEvent(pair.getSecond()).getId());
        }

        return matchedAct;
    }


    private DifferenceML print2TasksLog(Integer event1, Integer event2, NewUnfoldingPESSemantics<Integer> pes, PetriNet net, BPMNReader loader, String sentence, float ranking) {
        if(!commonLabels.contains(pes.getLabel(event1)) && !commonLabels.contains(pes.getLabel(event2)))
            return null;

        List<String> start = new ArrayList<>();
        List<String> a = new ArrayList<>();
        List<String> b = new ArrayList<>();
        List<String> end = new ArrayList<>();
        List<String> greys = new ArrayList<>();

        a.add(model.getTaskFromEvent(event1).getId());
        b.add(model.getTaskFromEvent(event2).getId());

        BitSet conf1 = pes.getLocalConfiguration(event1);
        BitSet conf2 = pes.getLocalConfiguration(event2);

        BitSet inter = (BitSet) conf1.clone();
        inter.and((BitSet) conf2.clone());

        inter.set(event1, false);
        inter.set(event2, false);

        Pomset pomset = pes.getPomset(inter, commonLabels);
        DirectedGraph g = pomset.getGraph();
        HashSet<Integer> sinks = new HashSet<>();
        for(Vertex v : g.getVertices())
            if(g.getEdgesWithSource(v).isEmpty())
                sinks.add(pomset.getMap().get(v));

        HashSet<FlowNode> history1 = model.getTasksFromConf(pes.getLocalConfiguration(event1));
        HashSet<FlowNode> history2 = model.getTasksFromConf(pes.getLocalConfiguration(event2));

        for(Integer context : sinks)
        {
            if(!commonLabels.contains(pes.getLabel(context)))
                continue;

            start.add(model.getTaskFromEvent(context).getId());
            history1.removeAll(model.getTasksFromConf(pes.getLocalConfiguration(context)));
            history2.removeAll(model.getTasksFromConf(pes.getLocalConfiguration(context)));
        }

        Set<String> future1 = pes.getPossibleFutureAsLabels(getMultiset(conf1));
        Set<String> future2 = pes.getPossibleFutureAsLabels(getMultiset(conf2));
        future1.retainAll(future2);

        HashSet<FlowNode> tasks = new HashSet<>();
        tasks.add(model.getTaskFromEvent(event1));
        tasks.add(model.getTaskFromEvent(event2));
        HashMap<String, String> endColors = new HashMap<>();

//        HashSet<FlowNode> tasks = new HashSet<>();
//        tasks.add(model.getTaskFromEvent(event1));
//        tasks.add(model.getTaskFromEvent(event2));
//        Pair<FlowNode, HashSet<FlowNode>> nextCommon = getNextCommon(tasks);
//        end.add(nextCommon.getFirst().getId());

        HashMap<String, String> colorsBPMN = getEnd(event1, event2, future1);
        for (Entry<String, String> entry : colorsBPMN.entrySet()) {
            if (entry.getValue().equals("red"))
                end.add(entry.getKey());
            else if(!start.contains(entry.getKey()) && !a.contains(entry.getKey()) && !end.contains(entry.getKey()))
                endColors.put(entry.getKey(), "green");
        }

        for(String element : endColors.keySet())
            if(!start.contains(element) && !a.contains(element) && !b.contains(element))
                greys.add(element);

//		Pair<FlowNode, HashSet<FlowNode>> nextCommon = getNextCommon(tasks);
//		end.add(nextCommon.getFirst().getId());
//		for(FlowNode element : nextCommon.getSecond())
//			if(!start.contains(element.getId()) && !a.contains(element.getId()) && !b.contains(element.getId()))
//				greys.add(element.getId());

        for(FlowNode element : history1)
            if(!start.contains(element.getId()) && !a.contains(element.getId()) && !b.contains(element.getId()))
                greys.add(element.getId());

        for(FlowNode element : history2)
            if(!start.contains(element.getId()) && !a.contains(element.getId()) && !b.contains(element.getId()))
                greys.add(element.getId());

        HashSet<String> allReleventEdges = new HashSet<>();
        allReleventEdges.addAll(start);
        allReleventEdges.addAll(end);
        allReleventEdges.addAll(b);
        allReleventEdges.addAll(a);
        allReleventEdges.addAll(greys);
        HashSet<String> flows = getEdgesBetween(allReleventEdges);
        greys.addAll(flows);

        for(FlowNode nodeModel : model.getBpmnModel().getFlowNodes())
            if(nodeModel instanceof Activity && greys.contains(nodeModel.getId())) {
                for (ControlFlow flow : model.getBpmnModel().getIncomingEdges(nodeModel))
                    greys.add(flow.getId());

                for (ControlFlow flow : model.getBpmnModel().getOutgoingEdges(nodeModel))
                    greys.add(flow.getId());
            }

        DifferenceML diff = new DifferenceML(ranking);
        diff.setSentence(sentence);
        diff.setA(a);
        diff.setB(b);
        diff.setStart(start);
        diff.setEnd(end);
        diff.setGreys(greys);

        // For testing
        HashMap<String, String> newColorsBP = new HashMap<>();
        for (String s : a)
            newColorsBP.put(s, "red");

        for (String s : b)
            newColorsBP.put(s, "red");

        for (String s : start)
            newColorsBP.put(s, "blue");

        for (String s : end)
            newColorsBP.put(s, "blue");

        for (String s : greys)
            newColorsBP.put(s, "gray");

//        printModels("m", "1", net, loader, null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

        return diff;
    }

    public HashSet<String> getEdgesBetween(HashSet<String> nodes){
        HashSet<String> edgesBT = new HashSet<>();

        for(ControlFlow flow : model.getBpmnModel().getControlFlow())
            if(nodes.contains(flow.getSource().getId()) && nodes.contains(flow.getTarget().getId())){
                ControlFlow edge = model.getBpmnModel().getDirectedEdge((FlowNode) flow.getSource(), (FlowNode) flow.getTarget());
                edgesBT.add(edge.getId());
            }

        return edgesBT;
    }

    private HashMap<String, String> getEnd(int a, int b, Set<String> labels2Spot){
            HashMap<String, String> allColors = new HashMap<>();

            FlowNode task1 = model.getTaskFromEvent(a);
            FlowNode task2 = model.getTaskFromEvent(b);

            HashSet<FlowNode> visited1 = new HashSet<>();
            visited1.add(task1);
            HashSet<FlowNode> visited2 = new HashSet<>();
            visited2.add(task2);

            HashSet<FlowNode> observed1 = new HashSet<>();
            HashSet<FlowNode> observed2 = new HashSet<>();

            while(!visited1.isEmpty() || !visited2.isEmpty()){
                HashSet<FlowNode> visited1New = new HashSet<>();
                HashSet<FlowNode> visited2New = new HashSet<>();

                for(FlowNode n : visited1)
                    if(!observed1.contains(n)) {
                        observed1.add(n);
                        visited1New.addAll(model.getBpmnModel().getDirectSuccessors(n));
//                        if(visited2.contains(n))
                            allColors.put(n.getId(), "green");
                    }

                for(FlowNode n : visited2)
                    if(!observed2.contains(n)) {
                        observed2.add(n);
                        visited2New.addAll(model.getBpmnModel().getDirectSuccessors(n));
//                        if(visited1.contains(n))
                            allColors.put(n.getId(), "green");
                    }

                HashSet<FlowNode> intersect = new HashSet<>(observed1);
                intersect.retainAll(observed2);

                if(!intersect.isEmpty()) {
                    HashMap<String, String> map = new HashMap<>();
                    for (FlowNode node : intersect) {
                        if (labels2Spot.contains(node.getName()) && !map.values().contains("red")) {
                            map.put(node.getId(), "red");

                            for(FlowNode succ : model.getBpmnModel().getDirectSuccessors(node))
                                if(allColors.containsKey(succ.getId()))
                                    allColors.remove(succ.getId());
                        }
                    }

                    if(map.size() > 0) {
                        for(Map.Entry<String, String> entry : allColors.entrySet())
                            if(!map.containsKey(entry.getKey()))
                                map.put(entry.getKey(), entry.getValue());

                        return map;
                    }
                }

                visited1 = new HashSet<>(visited1New);
                visited2 = new HashSet<>(visited2New);
            }

            return new HashMap<>();
    }

    private DifferenceML printTasksGO(List<Integer> events, NewUnfoldingPESSemantics<Integer> pes, PetriNet net, BPMNReader loader, String sentence, float ranking) {
        List<String> start = new ArrayList<>();
        List<String> a = new ArrayList<>();
        List<String> end = new ArrayList<>();
        List<String> greys = new ArrayList<>();

        BitSet inter = null;
        BitSet union = null;

        HashMap<String, String> aColors = new HashMap<>();

        for(Integer event : events){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            BitSet conf1 = pes.getLocalConfiguration(event);

            FlowNode task = model.getTaskFromEvent(event);
            a.add(task.getId());

            if(inter == null)
                inter = (BitSet) conf1.clone();
            else
                inter.and(conf1);

            if(union == null)
                union = (BitSet) conf1.clone();
            else
                union.or(conf1);
        }

        if(inter == null)
            return null;

        for(Integer event : events)
            inter.set(event, false);

        Pomset pomset = pes.getPomset(inter, commonLabels);
        DirectedGraph g = pomset.getGraph();
        HashSet<Integer> sinks = new HashSet<>();
        for(Vertex v : g.getVertices())
            if(g.getEdgesWithSource(v).isEmpty())
                sinks.add(pomset.getMap().get(v));

        HashSet<FlowNode> history = new HashSet<>();
        for(Integer event : events)
            history.addAll(model.getTasksFromConf(pes.getLocalConfiguration(event)));

        for(Integer event : sinks){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            start.add(model.getTaskFromEvent(event).getId());
            history.removeAll(model.getTasksFromConf(pes.getLocalConfiguration(event)));
        }

        for(FlowNode element : history)
            if(!start.contains(element.getId()) && !a.contains(element.getId()))
                greys.add(element.getId());

        Queue<Multiset<Integer>> queue = new LinkedList<>();
        Multiset ms = getMultiset(union);
        queue.offer(ms);
        HashSet<Multiset<Integer>> visited = new HashSet<>();
        visited.add(ms);

        HashMap<String, String> endColors = new HashMap<>();

        while(!queue.isEmpty()) {
            Multiset<Integer> current = queue.poll();
            Set<Integer> extensions = pes.getPossibleExtensions(current);
            endColors = new HashMap<>();

            for (Integer event : extensions) {
                if (!commonLabels.contains(pes.getLabel(event)))
                    continue;

                BitSet conf1 = pes.getLocalConfiguration(event);
                Trace<Integer> trace = new Trace<>();
                trace.addAllStrongCauses(pes.getEvents(conf1));

                FlowNode task = model.getTaskFromEvent(event);
                end.add(task.getId());
            }

            if(end.isEmpty()) {
                for(Integer ext : extensions) {
                    Multiset<Integer> copy = HashMultiset.<Integer> create(current);
                    copy.add(ext);
                    if(!visited.contains(copy)){
                        queue.add(copy);
                        visited.add(copy);
                    }
                }
            }else
                break;
        }

        for(String element : aColors.keySet())
            if(!start.contains(element))
                greys.add(element);

        for(String element : endColors.keySet())
            if(!start.contains(element) && !a.contains(element))
                greys.add(element);

		greys.addAll(a);
		a.clear();

        HashSet<String> allReleventEdges = new HashSet<>();
        allReleventEdges.addAll(start);
        allReleventEdges.addAll(end);
        allReleventEdges.addAll(greys);
        HashSet<String> flows = getEdgesBetween(allReleventEdges);
        greys.addAll(flows);

        for(FlowNode nodeModel : model.getBpmnModel().getFlowNodes())
            if(nodeModel instanceof Activity && greys.contains(nodeModel.getId())) {
                for (ControlFlow flow : model.getBpmnModel().getIncomingEdges(nodeModel))
                    greys.add(flow.getId());

                for (ControlFlow flow : model.getBpmnModel().getOutgoingEdges(nodeModel))
                    greys.add(flow.getId());
            }

        DifferenceML diff = new DifferenceML(ranking);
        diff.setSentence(sentence);
        diff.setA(a);
        diff.setStart(start);
        diff.setEnd(end);
        diff.setGreys(greys);

        // For testing
        HashMap<String, String> newColorsBP = new HashMap<>();
        for (String s : a)
            newColorsBP.put(s, "gray");

        for (String s : start)
            newColorsBP.put(s, "blue");

        for (String s : end)
            newColorsBP.put(s, "blue");

        for (String s : greys)
            newColorsBP.put(s, "gray");

//        printModels("m", "1", net, loader, null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

        return diff;
    }


    private DifferenceML printTasksGO2(Integer startN, List<Integer> events, Integer endN, NewUnfoldingPESSemantics<Integer> pes, PetriNet net, BPMNReader loader, String sentence, float ranking) {
        List<String> start = new ArrayList<>();
        List<String> a = new ArrayList<>();
        List<String> end = new ArrayList<>();
        List<String> greys = new ArrayList<>();

        BitSet inter = null;
        BitSet union = null;

        HashMap<String, String> aColors = new HashMap<>();

        for(Integer event : events){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            BitSet conf1 = pes.getLocalConfiguration(event);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf1));

            FlowNode task = model.getTaskFromEvent(event);
            a.add(task.getId());

            if(inter == null)
                inter = (BitSet) conf1.clone();
            else
                inter.and(conf1);

            if(union == null)
                union = (BitSet) conf1.clone();
            else
                union.or(conf1);
        }

        if(inter == null)
            return null;

        HashSet<FlowNode> history = new HashSet<>();
        for(Integer event : events)
            history.addAll(model.getTasksFromConf(pes.getLocalConfiguration(event)));

        if(startN == -2) {
            FlowNode task = model.getStart();
            start.add(task.getId());
        }else if(commonLabels.contains(pes.getLabel(startN))){
            BitSet conf1 = pes.getLocalConfiguration(startN);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf1));

            FlowNode task = model.getTaskFromEvent(startN);

//            if(model.getBpmnModel().getDirectSuccessors(task).size() == 1 &&
//                    (model.getBpmnModel().getDirectSuccessors(task).iterator().next() instanceof AndGateway ||
//                            model.getBpmnModel().getDirectSuccessors(task).iterator().next() instanceof XorGateway))
//                start.add(model.getBpmnModel().getDirectSuccessors(task).iterator().next().getId());
//            else
                start.add(task.getId());
            history.removeAll(model.getTasksFromConf(pes.getLocalConfiguration(startN)));
        }

        HashMap<String, String> endColors = new HashMap<>();

        if(endN < 0){
            endColors.put(model.getEnd().getId(), "green");
            end.add(model.getEnd().getId());
        }else if(commonLabels.contains(pes.getLabel(endN))){
            BitSet conf1 = pes.getLocalConfiguration(endN);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf1));

            FlowNode task = model.getTaskFromEvent(endN);
            end.add(task.getId());
        }

//        for(FlowNode element : history)
//            if(!start.contains(element.getId()))
//                greys.add(element.getId());

        for(String element : endColors.keySet())
            if(!start.contains(element) && !endColors.containsKey(element) && !end.contains(element) && !a.contains(element))
                greys.add(element);

        greys.addAll(a);

        HashSet<String> allReleventEdges = new HashSet<>();
        allReleventEdges.addAll(start);
        allReleventEdges.addAll(end);
        allReleventEdges.addAll(greys);
        HashSet<String> flows = getEdgesBetween(allReleventEdges);
        greys.addAll(flows);

        for(FlowNode nodeModel : model.getBpmnModel().getFlowNodes())
            if(nodeModel instanceof Activity && greys.contains(nodeModel.getId())) {
                for (ControlFlow flow : model.getBpmnModel().getIncomingEdges(nodeModel))
                    greys.add(flow.getId());

                for (ControlFlow flow : model.getBpmnModel().getOutgoingEdges(nodeModel))
                    greys.add(flow.getId());
            }

        DifferenceML diff = new DifferenceML(ranking);
        diff.setSentence(sentence);
        diff.setStart(start);
        diff.setEnd(end);
        diff.setGreys(greys);

        // For testing
        HashMap<String, String> newColorsBP = new HashMap<>();
        for (String s : a)
            newColorsBP.put(s, "gray");

        for (String s : start)
            newColorsBP.put(s, "blue");

        for (String s : end)
            newColorsBP.put(s, "blue");

        for (String s : greys)
            newColorsBP.put(s, "gray");

//        printModels("m", "1", net, loader, null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

        return diff;
    }

	private DifferenceML printTasksHL2(Integer startN, List<Integer> events, Integer endN, NewUnfoldingPESSemantics<Integer> pes, PetriNet net, BPMNReader loader, String sentence, float ranking) {
		List<String> start = new ArrayList<>();
		List<String> a = new ArrayList<>();
		List<String> end = new ArrayList<>();
		List<String> greys = new ArrayList<>();

		BitSet inter = null;
		BitSet union = null;

		HashMap<String, String> aColors = new HashMap<>();

		for(Integer event : events){
			if(!commonLabels.contains(pes.getLabel(event)))
				continue;

			BitSet conf1 = pes.getLocalConfiguration(event);
			Trace<Integer> trace = new Trace<>();
			trace.addAllStrongCauses(pes.getEvents(conf1));

			FlowNode task = model.getTaskFromEvent(event);
			a.add(task.getId());

			if(inter == null)
				inter = (BitSet) conf1.clone();
			else
				inter.and(conf1);

			if(union == null)
				union = (BitSet) conf1.clone();
			else
				union.or(conf1);
		}

		if(inter == null)
			return null;

		HashSet<FlowNode> history = new HashSet<>();
		for(Integer event : events)
			history.addAll(model.getTasksFromConf(pes.getLocalConfiguration(event)));

		if(startN == -2) {
			FlowNode task = model.getStart();
			start.add(task.getId());
		}else if(commonLabels.contains(pes.getLabel(startN))){
			BitSet conf1 = pes.getLocalConfiguration(startN);
			Trace<Integer> trace = new Trace<>();
			trace.addAllStrongCauses(pes.getEvents(conf1));

			FlowNode task = model.getTaskFromEvent(startN);
			start.add(task.getId());
			history.removeAll(model.getTasksFromConf(pes.getLocalConfiguration(startN)));
		}

		HashMap<String, String> endColors = new HashMap<>();

		if(endN < 0){
			endColors.put(model.getEnd().getId(), "green");
			end.add(model.getEnd().getId());
		}else if(commonLabels.contains(pes.getLabel(endN))){
			BitSet conf1 = pes.getLocalConfiguration(endN);
			Trace<Integer> trace = new Trace<>();
			trace.addAllStrongCauses(pes.getEvents(conf1));

			FlowNode task = model.getTaskFromEvent(endN);
			end.add(task.getId());
		}

		for(String element : endColors.keySet())
			if(!start.contains(element) && !endColors.containsKey(element) && !end.contains(element) && !a.contains(element))
				greys.add(element);

//		greys.addAll(a);

		HashSet<String> allReleventEdges = new HashSet<>();
		allReleventEdges.addAll(start);
		allReleventEdges.addAll(end);
		allReleventEdges.addAll(greys);
		HashSet<String> flows = getEdgesBetween(allReleventEdges);
		greys.addAll(flows);

		for(FlowNode nodeModel : model.getBpmnModel().getFlowNodes())
			if(nodeModel instanceof Activity && greys.contains(nodeModel.getId())) {
				for (ControlFlow flow : model.getBpmnModel().getIncomingEdges(nodeModel))
					greys.add(flow.getId());

				for (ControlFlow flow : model.getBpmnModel().getOutgoingEdges(nodeModel))
					greys.add(flow.getId());
			}

		DifferenceML diff = new DifferenceML(ranking);
		diff.setSentence(sentence);
		diff.setStart(start);
        diff.setStart(a);
		diff.setEnd(end);
		diff.setGreys(greys);

		// For testing
		HashMap<String, String> newColorsBP = new HashMap<>();
		for (String s : a)
			newColorsBP.put(s, "gray");

		for (String s : start)
			newColorsBP.put(s, "blue");

		for (String s : end)
			newColorsBP.put(s, "blue");

		for (String s : greys)
			newColorsBP.put(s, "gray");

//        printModels("m", "1", net, loader, null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

		return diff;
	}

    private DifferenceML addTasks(List<Integer> events, NewUnfoldingPESSemantics<Integer> pes, PetriNet net, BPMNReader loader, String sentence, float ranking) {
        List<String> start = new ArrayList<>();
        List<String> a = new ArrayList<>();
        List<String> end = new ArrayList<>();
        List<String> greys = new ArrayList<>();

        BitSet inter = null;
        BitSet union = null;

        HashMap<String, String> aColors = new HashMap<>();

        for(Integer event : events){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            BitSet conf1 = pes.getLocalConfiguration(event);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf1));

            FlowNode task = model.getTaskFromEvent(event);
            a.add(task.getId());

//            HashMap<String, String> colorsBPMN = replayerBPMN.execute(pes.getLabel(event), pes.getPomset(conf1, commonLabels), new HashMap<String, Integer>(), null);
//
//            for(Entry<String, String> entry : colorsBPMN.entrySet())
//                if(entry.getValue().equals("red"))
//                    a.add(entry.getKey());
//                else if(!a.contains(entry.getKey()))
//                    aColors.put(entry.getKey(), "green");

            if(inter == null)
                inter = (BitSet) conf1.clone();
            else
                inter.and(conf1);

            if(union == null)
                union = (BitSet) conf1.clone();
            else
                union.or(conf1);
        }

        if(inter == null)
            return null;

        for(Integer event : events)
            inter.set(event, false);

        Pomset pomset = pes.getPomset(inter, commonLabels);
        DirectedGraph g = pomset.getGraph();
        HashSet<Integer> sinks = new HashSet<>();
        for(Vertex v : g.getVertices())
            if(g.getEdgesWithSource(v).isEmpty())
                sinks.add(pomset.getMap().get(v));

        HashMap<String, String> startColors = new HashMap<>();

        for(Integer event : sinks){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            BitSet conf1 = pes.getLocalConfiguration(event);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf1));

            FlowNode task = model.getTaskFromEvent(event);
            start.add(task.getId());
        }


        Queue<Multiset<Integer>> queue = new LinkedList<>();
        Multiset ms = getMultiset(union);
        queue.offer(ms);
        HashSet<Multiset<Integer>> visited = new HashSet<>();
        visited.add(ms);

        HashMap<String, String> endColors = new HashMap<>();

        while(!queue.isEmpty()) {
            Multiset<Integer> current = queue.poll();
            Set<Integer> extensions = pes.getPossibleExtensions(current);
            endColors = new HashMap<>();

            for (Integer event : extensions) {
                if (!commonLabels.contains(pes.getLabel(event)))
                    continue;

                BitSet conf1 = pes.getLocalConfiguration(event);
                Trace<Integer> trace = new Trace<>();
                trace.addAllStrongCauses(pes.getEvents(conf1));

                FlowNode task = model.getTaskFromEvent(event);
                end.add(task.getId());
            }

            if(end.isEmpty()) {
                for(Integer ext : extensions) {
                    Multiset<Integer> copy = HashMultiset.<Integer> create(current);
                    copy.add(ext);
                    if(!visited.contains(copy)){
                        queue.add(copy);
                        visited.add(copy);
                    }
                }
            }else
                break;
        }

        for(String element : aColors.keySet())
            if(!startColors.containsKey(element) && !start.contains(element))
                greys.add(element);

        for(String element : endColors.keySet())
            if(!startColors.containsKey(element) && !start.contains(element) && !a.contains(element))
                greys.add(element);

        HashSet<String> allReleventEdges = new HashSet<>();
        allReleventEdges.addAll(start);
        allReleventEdges.addAll(end);
        allReleventEdges.addAll(a);
        allReleventEdges.addAll(greys);
        HashSet<String> flows = getEdgesBetween(allReleventEdges);
        greys.addAll(flows);

        for(FlowNode nodeModel : model.getBpmnModel().getFlowNodes())
            if(nodeModel instanceof Activity && greys.contains(nodeModel.getId())) {
                for (ControlFlow flow : model.getBpmnModel().getIncomingEdges(nodeModel))
                    greys.add(flow.getId());

                for (ControlFlow flow : model.getBpmnModel().getOutgoingEdges(nodeModel))
                    greys.add(flow.getId());
            }

        DifferenceML diff = new DifferenceML(ranking);
        diff.setSentence(sentence);
        diff.setA(a);
        diff.setStart(start);
        diff.setEnd(end);
        diff.setGreys(greys);

        // For testing
        HashMap<String, String> newColorsBP = new HashMap<>();
        for (String s : a)
            newColorsBP.put(s, "gray");

        for (String s : start)
            newColorsBP.put(s, "blue");

        for (String s : end)
            newColorsBP.put(s, "blue");

        for (String s : greys)
            newColorsBP.put(s, "gray");

//        printModels("m", "1", net, loader, null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

        return diff;
    }

    private DifferenceML printTasksGOHL(List<Integer> eventsGO,List<Integer> eventsHL, NewUnfoldingPESSemantics<Integer> pes, PetriNet net, BPMNReader loader, String sentence, float ranking) {
        List<String> start = new ArrayList<>();
        List<String> end = new ArrayList<>();
        List<String> greys = new ArrayList<>();

        BitSet inter = null;
        BitSet union = null;

        for(Integer event : eventsGO){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            BitSet conf1 = pes.getLocalConfiguration(event);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf1));

            FlowNode task = model.getTaskFromEvent(event);
            greys.add(task.getId());

            if(inter == null)
                inter = (BitSet) conf1.clone();
            else
                inter.and(conf1);

            if(union == null)
                union = (BitSet) conf1.clone();
            else
                union.or(conf1);
        }

        if(inter == null)
            return null;

        for(Integer event : eventsGO)
            inter.set(event, false);

        HashMap<String, String> endColors = new HashMap<>();

        for(Integer event : eventsHL){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            BitSet conf1 = pes.getLocalConfiguration(event);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf1));

            FlowNode task = model.getTaskFromEvent(event);
            end.add(task.getId());
        }

        Pomset pomset = pes.getPomset(inter, commonLabels);
        DirectedGraph g = pomset.getGraph();
        HashSet<Integer> sinks = new HashSet<>();
        for(Vertex v : g.getVertices())
            if(g.getEdgesWithSource(v).isEmpty())
                sinks.add(pomset.getMap().get(v));

        HashMap<String, String> startColors = new HashMap<>();

        for(Integer event : sinks){
            if(!commonLabels.contains(pes.getLabel(event)))
                continue;

            BitSet conf1 = pes.getLocalConfiguration(event);
            Trace<Integer> trace = new Trace<>();
            trace.addAllStrongCauses(pes.getEvents(conf1));

            FlowNode task = model.getTaskFromEvent(event);
            start.add(task.getId());
        }


        Queue<Multiset<Integer>> queue = new LinkedList<>();
        Multiset ms = getMultiset(union);
        queue.offer(ms);
        HashSet<Multiset<Integer>> visited = new HashSet<>();
        visited.add(ms);

        for(String element : endColors.keySet())
            if(!startColors.containsKey(element) && !start.contains(element))
                greys.add(element);

        HashSet<String> allReleventEdges = new HashSet<>();
        allReleventEdges.addAll(start);
        allReleventEdges.addAll(end);
        allReleventEdges.addAll(greys);
        HashSet<String> flows = getEdgesBetween(allReleventEdges);
        greys.addAll(flows);

        for(FlowNode nodeModel : model.getBpmnModel().getFlowNodes())
            if(nodeModel instanceof Activity && greys.contains(nodeModel.getId())) {
                for (ControlFlow flow : model.getBpmnModel().getIncomingEdges(nodeModel))
                    greys.add(flow.getId());

                for (ControlFlow flow : model.getBpmnModel().getOutgoingEdges(nodeModel))
                    greys.add(flow.getId());
            }

        DifferenceML diff = new DifferenceML(ranking);
        diff.setSentence(sentence);
        diff.setEnd(end);
        diff.setStart(start);
        diff.setGreys(greys);

        // For testing
        HashMap<String, String> newColorsBP = new HashMap<>();
        for (String s : end)
            newColorsBP.put(s, "blue");

        for (String s : start)
            newColorsBP.put(s, "blue");

        for (String s : greys)
            newColorsBP.put(s, "gray");

//        printModels("m", "1", net, loader, null, newColorsBP, new HashMap<String, Integer>(), new HashMap<String, Integer>());

        return diff;
    }

	public Multiset<Integer> getMultiset(BitSet bs){
		Multiset<Integer> multiset = HashMultiset. create();
		for (int event = bs.nextSetBit(0); event >= 0; event = bs.nextSetBit(event+1))
			multiset.add(event);

		return multiset;
	}

	private Runs printTask(Integer event1, PESSemantics<Integer> pes, BPMNReader loader, String sentence) {
		Runs runs = new Runs();

        FlowNode task = model.getTaskFromEvent(event1);
        HashMap<String, String> colorsBPMN = new HashMap<>();
        colorsBPMN.put(task.getId(), "red");
        HashMap<String, Integer> repetitions = new HashMap<String, Integer>();

		// All configurations
		BitSet conf1 = pes.getLocalConfiguration(event1);

		runs.addRun(new Run(colorsBPMN, repetitions, new HashMap<String, Integer>(), loader, sentence, pes.getPomset(conf1,model.getLabels())));

		return runs;
	}

	public void printStatements(){
		for(DifferenceML diff : differences.getDifferences())
			System.out.println(diff.getSentence());
	}
}
