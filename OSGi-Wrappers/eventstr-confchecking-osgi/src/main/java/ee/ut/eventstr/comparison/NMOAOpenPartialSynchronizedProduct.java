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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

//import de.hpi.bpt.utils.IOUtils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;

import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.PESSemantics;
import ee.ut.org.processmining.framework.util.Pair;

public class NMOAOpenPartialSynchronizedProduct<T> {
	
	public static class State implements Comparable<State> {
		BitSet c1;
		Multiset<Integer> c2;
		Multiset<String> labels;
		short cost = 0;
		BitSet targets = new BitSet();
		
		State(BitSet c1, Multiset<String> labels, Multiset<Integer> c2) {
			this.c1 = c1; this.c2 = c2; this.labels = labels;
		}
		
		public String toString() {
			return String.format("<%s,%s,%s,%d>", c1, labels, c2, cost);
		}

		public int compareTo(State o) {
			return Short.compare(this.cost, o.cost);
		}
	}

	enum StateHint {CREATED, MERGED, DISCARDED};
	enum Op {MATCH, LHIDE, RHIDE, MATCHNSHIFT, RHIDENSHIFT};
	static class Operation {
		Op op;
		String label;
		State nextState;
		Object target;
		
		private Operation(State state, Op op, Object target, String label) {
			this.nextState = state; this.target = target;
			this.op = op; this.label = label;
		}
		static Operation match(State state, Pair<Integer, Integer> target, String label) {
			return new Operation(state, Op.MATCH, target, label);
		}
		static Operation lhide(State state, Integer target, String label) {
			return new Operation(state, Op.LHIDE, target, label);
		}
		static Operation rhide(State state, Integer target, String label) {
			return new Operation(state, Op.RHIDE, target, label);
		}
		static Operation rhidenshift(State state, Integer target, String label) {
			return new Operation(state, Op.RHIDENSHIFT, target, label);
		}
		static Operation matchnshift(State state, Pair<Integer, Integer> target, String label) {
			return new Operation(state, Op.MATCHNSHIFT, target, label);
		}
		
		public String toString() {
			return String.format("%s(%s[%s])", op.toString().toLowerCase(), label, target);
		}
	}
	
	private PESSemantics<T> pes1;
	private NewUnfoldingPESSemantics<T> pes2;
	private int numberOfTargets;
	private BitSet[] maxConfs1;
	private State[] matchings;

	private Multimap<State, Operation> descendants;
	private Multimap<State, State> ancestors;
	private State root;
	private Table<BitSet, Multiset<Integer>, Map<Multiset<String>, State>> stateSpaceTable;

	private List<State> states = new ArrayList<>();
	private Set<State> relevantStates;

	public NMOAOpenPartialSynchronizedProduct(PESSemantics<T> pes1, NewUnfoldingPESSemantics<T> pes2) {
		this.pes1 = pes1;
		this.pes2 = pes2;
		this.descendants = HashMultimap.create();
		this.ancestors = HashMultimap.create();
		this.stateSpaceTable = HashBasedTable.create();
		
		this.numberOfTargets = pes1.getMaxConf().size();
		this.maxConfs1 = new BitSet[numberOfTargets];
		this.matchings = new State[numberOfTargets];
		int i = 0;
		for (BitSet conf: pes1.getMaxConf())
			this.maxConfs1[i++] = conf;
	}
	
	public NMOAOpenPartialSynchronizedProduct<T> perform() {
		Queue<State> open = new PriorityQueue<>();
		
		BitSet targets = new BitSet();
		targets.flip(0, numberOfTargets);
		root = getState(new BitSet(), HashMultiset.<String> create(), HashMultiset.<Integer> create(), targets).getSecond();
		
		open.offer(root);

		while (!open.isEmpty()) {
			State s = open.poll();
						
			if (isCandidate(s)) {
				BitSet lpe = pes1.getPossibleExtensions(s.c1);				
				Set<Integer> rpe = pes2.getPossibleExtensions(s.c2);
				
				if (lpe.isEmpty() && rpe.isEmpty()) {
					int target = s.targets.nextSetBit(0);
					matchings[target] = s;
					continue;
				}
				
				for (int e1 = lpe.nextSetBit(0); e1 >= 0; e1 = lpe.nextSetBit(e1+1)) {
					String label1 = pes1.getLabel(e1);
					BitSet c1p = (BitSet)s.c1.clone();
					c1p.set(e1);
					
					for (Integer e2: rpe) {
						if (label1.equals(pes2.getLabel(e2)) && isOrderPreserving(s, e1, e2)) {
							Pair<Multiset<Integer>, Boolean> extPair = pes2.extend(s.c2, e2);
							Multiset<String> labels = HashMultiset.create(s.labels);
							labels.add(label1);
							
							Pair<StateHint, State> pair = getState(c1p, labels, extPair.getFirst(), s.targets);
							State nstate = pair.getSecond();
							nstate.targets = updateTargets(s.targets, e1);
							nstate.cost = s.cost; // A matching operation does not change the current cost
									
							switch (pair.getFirst()) {
							case CREATED:
								open.offer(nstate);
								ancestors.put(nstate, s);
							case MERGED:
								if (extPair.getSecond())
									descendants.put(s, Operation.matchnshift(nstate, new Pair<>(e1, e2), label1));
								else
									descendants.put(s, Operation.match(nstate, new Pair<>(e1, e2), label1));
							default:
							}
							
//							IOUtils.toFile("psp.dot", toDot());
						}
					}
				}
				
				for (Integer e2: rpe) {
					Pair<Multiset<Integer>, Boolean> extPair = pes2.extend(s.c2, e2);
					Pair<StateHint, State> pair = getState(s.c1, s.labels, extPair.getFirst(), s.targets);
					
					State nstate = pair.getSecond();
					computeCost(nstate);

					switch (pair.getFirst()) {
					case CREATED:
						open.offer(nstate);
						ancestors.put(nstate, s);
					case MERGED:
						if (extPair.getSecond())
							descendants.put(s, Operation.rhidenshift(nstate, e2, pes2.getLabel(e2)));
						else
							descendants.put(s, Operation.rhide(nstate, e2, pes2.getLabel(e2)));
					default:
					}
					
//					IOUtils.toFile("psp.dot", toDot());
				}
				
				for (int e1 = lpe.nextSetBit(0); e1 >= 0; e1 = lpe.nextSetBit(e1+1)) {
					BitSet c1p = (BitSet)s.c1.clone();
					c1p.set(e1);
					Pair<StateHint, State> pair = getState(c1p, s.labels, s.c2, s.targets);
					State nstate = pair.getSecond();
					nstate.targets = updateTargets(s.targets, e1);
					computeCost(nstate);

					switch (pair.getFirst()) {
					case CREATED:
						open.offer(nstate);
						ancestors.put(nstate, s);
					case MERGED:
						descendants.put(s, Operation.lhide(nstate, e1, pes1.getLabel(e1)));
					default:
					}
					
//					IOUtils.toFile("psp.dot", toDot());
				}
			}
		}
		return this;
	}
	
	private BitSet updateTargets(BitSet targets, int e1) {
		BitSet ntargets = new BitSet();
		for (int index = targets.nextSetBit(0); index >= 0; index = targets.nextSetBit(index + 1))
			if (maxConfs1[index].get(e1))
				ntargets.set(index);
		return ntargets;
	}

	private boolean isOrderPreserving(State s, int e1, Integer e2) {
		BitSet e1dpred = (BitSet)pes1.getDirectPredecessors(e1).clone();
		Set<Integer> e2dpred = new HashSet<>(pes2.getDirectPredecessors(e2));
		
		Stack<State> open = new Stack<>();
		Set<State> visited = new HashSet<>();
		open.push(s);
		
		BitSet e1causes = pes1.getLocalConfiguration(e1);
		BitSet e2causes = pes2.getLocalConfiguration(e2);
		
		while (!open.isEmpty()) {
			if (e1dpred.isEmpty() && e2dpred.isEmpty())
				break;
			State curr = open.pop();
			visited.add(curr);
			
			for (State ancestor: ancestors.get(curr)) {
				if (visited.contains(ancestor) || open.contains(ancestor)) continue;
				for (Operation op: descendants.get(ancestor))
					if (op.nextState.equals(curr)) {
//						System.out.println(">> " + op);
						if (op.op == Op.MATCH) {
							@SuppressWarnings("unchecked")
							Pair<Integer, Integer> matchedEvents = (Pair<Integer,Integer>)op.target;
							e1dpred.clear(matchedEvents.getFirst());
							e2dpred.remove(matchedEvents.getSecond());
							
							if (!(e1causes.get(matchedEvents.getFirst()) == e2causes.get(matchedEvents.getSecond()))) {
//								System.out.println("====== It is not order preserving!");
								return false;
							}

						} else if (op.op == Op.MATCHNSHIFT) {
							@SuppressWarnings("unchecked")
							Pair<Integer, Integer> matchedEvents = (Pair<Integer,Integer>)op.target;
							e1dpred.clear(matchedEvents.getFirst());
							e2dpred.remove(matchedEvents.getSecond());
							
//							System.out.println("Performed inverse shift (+match): " + matchedEvents.getSecond());
							if (pes2.getBRelation(e2, matchedEvents.getSecond()) != BehaviorRelation.CONCURRENCY) {
								e2causes = pes2.unshift(e2causes, matchedEvents.getSecond());
//								e2causes = pes2.getLocalConfiguration(matchedEvents.getSecond());
							}
							
							if (!(e1causes.get(matchedEvents.getFirst()) == e2causes.get(matchedEvents.getSecond()))) {
//								System.out.println("====== It is not order preserving! (after inverse shift)");
								return false;
							}
						} else if (op.op == Op.RHIDENSHIFT || op.op == Op.RHIDE) {
							Integer hiddenEvent = (Integer)op.target;
//							if (e2dpred.contains(hiddenEvent)) {
								e2dpred.remove(hiddenEvent);
								e2dpred.addAll(pes2.getDirectPredecessors(hiddenEvent));
								if (op.op == Op.RHIDENSHIFT && pes2.getBRelation(e2, hiddenEvent) != BehaviorRelation.CONCURRENCY) {
//									System.out.println("Performed inverse shift: " + hiddenEvent);
									e2causes = pes2.unshift(e2causes, hiddenEvent);
//									e2causes.clear(hiddenEvent);
	//								e2causes = pes2.getLocalConfiguration(hiddenEvent);
								}
//							}
						} else {
							Integer hiddenEvent = (Integer)op.target;
							e1dpred.clear(hiddenEvent);
							e1dpred.or(pes1.getDirectPredecessors(hiddenEvent));
						}
					}
				open.push(ancestor);
			}
		}
		return true;
	}

	
	private Pair<StateHint,State> getState(BitSet c1, Multiset<String> labels, Multiset<Integer> c2, BitSet targets) {
		State newState = new State(c1, labels, c2);
		states.add(newState);

		newState.targets = (BitSet)targets.clone();
		StateHint action = StateHint.CREATED;
		
		if (stateSpaceTable.contains(c1, c2)) {
			Map<Multiset<String>, State> map = stateSpaceTable.get(c1, c2);
			if (map.containsKey(labels))
				action = StateHint.MERGED;
			else
				map.put(labels, newState);
		} else {
			Map<Multiset<String>, State> map = new HashMap<>();
			map.put(labels, newState);
			stateSpaceTable.put(c1, c2, map);
		}
		return new Pair<>(action, newState);
	}


	private boolean isCandidate(State s) {
		for (int target = s.targets.nextSetBit(0); target >= 0; target = s.targets.nextSetBit(target + 1))
			if (matchings[target] == null)
				return true;
		return false;
	}
	
	public void computeCost(State s) {
		Multiset<Integer> c2copy = HashMultiset.create(s.c2);
		c2copy.removeAll(pes2.getInvisibleEvents());
		s.cost = (short)(
				g(s.c1, c2copy, s.labels)
				+ h(s)
				);
	}
		
	public int g(BitSet c1, Multiset<Integer> c2, Multiset<String> labels) {
		return (c1.cardinality() + c2.size() - labels.size() * 2);
	}
	
	public int h(State s) {
		int value = Short.MAX_VALUE;
		Set<String> pf2 = pes2.getPossibleFutureAsLabels(s.c2);
		
		for (int index = s.targets.nextSetBit(0); index >= 0; index = s.targets.nextSetBit(index + 1)) {
			BitSet maxConf = maxConfs1[index];
			
			BitSet future = (BitSet)maxConf.clone();
			future.andNot(s.c1);
			Set<String> diff = translate(future);
			diff.removeAll(pf2);
			value = Math.min(value, diff.size());
		}
		
		return value;
	}
	
	private Set<String> translate(BitSet bitset) {
		Set<String> set = new LinkedHashSet<>();
		for (int ev = bitset.nextSetBit(0); ev >= 0; ev = bitset.nextSetBit(ev+1)) {
			set.add(pes1.getLabel(ev));
		}
		return set;
	}
	
	public NMOAOpenPartialSynchronizedProduct<T> prune() {
		Set<State> gvisited = new HashSet<>();
		Stack<State> open = new Stack<>();
		
		for (int i = 0; i < numberOfTargets; i++) {
			State s = matchings[i];
			if (s == null) continue;
			open.push(s);
			Set<State> visited = new HashSet<>();
			while (!open.isEmpty()) {
				State curr = open.pop();
				visited.add(curr);
				
				for (State pred: ancestors.get(curr))
					if (!visited.contains(pred) && !open.contains(pred))
						open.push(pred);
			}
			

			gvisited.addAll(visited);
		}
		
		this.relevantStates = gvisited;
		
		//System.out.println("Number of relevant states: " + relevantStates.size());
		
		return this;
	}


	public String toDot() {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);
		
		out.println("digraph G {\n\t node [shape=box];");
		Map<State, Integer> rstates = new HashMap<>();
		
		for (int i = 0; i < states.size(); i ++) {
			State s = states.get(i);
			if (relevantStates == null || relevantStates.contains(s)) {
				rstates.put(s, i);
//				if (matchings.containsValue(s))
//					out.printf("\tn%d [label=\"%s,%s\\n%s\\n%3.2f\",color=blue];\n", i, s.c1, s.c2, s.labels, s.weight);
//				else
					out.printf("\tn%d [label=\"%s,%s\\n%s\\n%d,%s\"];\n", i, s.c1, s.c2, s.labels, s.cost, s.targets);
			}
		}

		Collection<State> lstates = relevantStates != null ? relevantStates : states; 
		
		for (State s: lstates) {
			Integer src = rstates.get(s);
			for (Operation op: descendants.get(s)) {
				if (relevantStates == null || relevantStates.contains(op.nextState)) {
					Integer tgt = rstates.get(op.nextState);
					out.printf("\tn%d -> n%d [label=\"%s\"];\n", src, tgt, op);
				}
			}
		}
		out.println("}");
		return str.toString();
	}
}
