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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.*;

import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.org.processmining.framework.util.Pair;
import org.eclipse.collections.impl.bag.mutable.primitive.IntHashBag;
import org.eclipse.collections.impl.block.comparator.primitive.IntFunctionComparator;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.stack.mutable.ArrayStack;

import static ee.ut.eventstr.comparison.LogBasedPartialSynchronizedProduct.StateHint.CREATED;
import static ee.ut.eventstr.comparison.LogBasedPartialSynchronizedProduct.StateHint.MERGED;

public class LogBasedPartialSynchronizedProduct<T> implements Comparable<LogBasedPartialSynchronizedProduct<T>>{

	public static class State implements Comparable<State> {
		BitSet c1;
		BitSet c2;

        IntHashBag labels;
		StateHint action;
		public short cost = 0;

		int hashcode = -1;

        State(BitSet c1, IntHashBag labels, BitSet c2) {
			this.c1 = c1;
			this.c2 = c2;
			this.labels = labels;
		}

		public int hashCode() {
		    if(hashcode == -1) hashcode = c1.hashCode() + c2.hashCode() + labels.hashCode();
		    return hashcode;
        }

        public boolean equals(Object o) {
		    if(o instanceof State) {
		        State s = (State) o;
		        return (labels.equals(s.labels) && c1.equals(s.c1) && c2.equals(s.c2));
            }
            return false;
        }

		public String toString() {
			return String.format("<%s,%s,%s,%d>", c1, labels, c2, cost);
		}

		public int compareTo(State o) {
			int compare = Short.compare(this.cost, o.cost);
			if(compare == 0) compare = bitsetCompare(this.c1, o.c1);
            if(compare == 0) compare = bitsetCompare(this.c2, o.c2);
            return compare;
		}

		private int bitsetCompare(BitSet lhs, BitSet rhs) {
            if (lhs.equals(rhs)) return 0;
            BitSet xor = (BitSet)lhs.clone();
            xor.xor(rhs);
            int firstDifferent = xor.length()-1;
            if(firstDifferent==-1)
                return 0;

            return rhs.get(firstDifferent) ? 1 : -1;
        }
	}

	enum StateHint {
		CREATED, MERGED, DISCARDED
	}

	public enum Op {
		MATCH, LHIDE, RHIDE, MATCHNSHIFT, RHIDENSHIFT
	}

	public static class Operation {
		Op op;
		String labelString;
        int label;
		State nextState;
		Object target;

		int hashcode = -1;

        private Operation(State state, Op op, Object target, int label, String labelString) {
			this.nextState = state;
			this.target = target;
			this.op = op;
			this.label = label;
			this.labelString = labelString;
		}

		static Operation match(State state, Pair<Integer, Integer> target, int label, String labelString) {
			return new Operation(state, Op.MATCH, target, label, labelString);
		}

        static Operation lhide(State state, Integer target, int label, String labelString) {
			return new Operation(state, Op.LHIDE, target, label, labelString);
		}

        static Operation rhide(State state, Integer target, int label, String labelString) {
			return new Operation(state, Op.RHIDE, target, label, labelString);
		}

        static Operation rhidenshift(State state, Integer target, int label, String labelString) {
			return new Operation(state, Op.RHIDENSHIFT, target, label, labelString);
		}

        static Operation matchnshift(State state, Pair<Integer, Integer> target, int label, String labelString) {
			return new Operation(state, Op.MATCHNSHIFT, target, label, labelString);
		}

        String getLabel() {
            return labelString;
        }

        public int hashCode() {
		    if(hashcode == -1) hashcode = nextState.hashcode + target.hashCode() + op.hashCode() + label;
		    return hashcode;
        }

        public boolean equals(Object o) {
		    if(o instanceof Operation) {
                Operation ope = (Operation) o;
                return op.equals(ope.op) && label == ope.label && nextState.equals(ope.nextState) && target.equals(ope.target);
            }
            return false;
        }

		public String toString() {
			return String.format("%s(%s[%s])", op.toString().toLowerCase(), label, target);
		}
	}

	private SinglePORunPESSemantics<T> pes1;
	private SinglePORunPESSemantics<T> pes2;
	private int numberOfTargets;
	private BitSet maxConf1;
	public State matchings;

    private UnifiedSetMultimap<State, Operation> descendants;
    private UnifiedSetMultimap<State, State> ancestors;
	private State root;
    private Table<BitSet, BitSet, Set<IntHashBag>> stateSpaceTable;

	private List<State> states = new ArrayList<>();
	private Set<State> relevantStates;
	private LinkedList<Operation> opSeq;

    private UnifiedSetMultimap<State, Operation> operations;
    private ObjectIntHashMap<String> labelMap = new ObjectIntHashMap();
    private IntObjectHashMap<String> reverseLabelMap = new IntObjectHashMap();
    private UnifiedMap<BitSet, int[]> bitSetToArrayMap = new UnifiedMap<>();

    Queue<State> open;
    private Comparator<Operation> comparator;

    private BitSet lpe, rpe, pruned1, pruned2, kept1, kept2;
    int[] lpea, rpea;
    private State s;
    int sink, guess;
    int cost;

	public LogBasedPartialSynchronizedProduct(SinglePORunPESSemantics<T> pes1, SinglePORunPESSemantics<T> pes2, int sink, int guess) {
	    this.sink = sink;
	    this.guess = guess;

		this.pes1 = pes1;
		this.pes2 = pes2;
        this.descendants = UnifiedSetMultimap.newMultimap();
        this.operations = UnifiedSetMultimap.newMultimap();
        this.ancestors = UnifiedSetMultimap.newMultimap();
        this.stateSpaceTable = HashBasedTable.create();

		this.numberOfTargets = pes1.getMaxConf().size();
		this.maxConf1 = pes1.getMaxConf().iterator().next();
		this.matchings = null;

        open = new PriorityQueue<>();
	}

    public LogBasedPartialSynchronizedProduct<T> perform(int optimalCost) {
        Queue<State> open = new PriorityQueue<>();

        root = getState(new BitSet(), new IntHashBag(), new BitSet());

        open.offer(root);

        Comparator<Operation> comparator = new Comparator<Operation>() {
            @Override
            public int compare(Operation o1, Operation o2) {
                int costCValue = Short.compare(o1.nextState.cost, o2.nextState.cost);
                if (costCValue != 0)
                    return costCValue;
                else
                    return Integer.compare(o1.label, o2.label);
            }
        };

        while (!open.isEmpty()) {
            s = open.poll();
            if(s.cost > optimalCost) {
                return null;
            }

            lpe = pes1.getPossibleExtensions(s.c1);
            rpe = pes2.getPossibleExtensions(s.c2);

            if (lpe.isEmpty() && rpe.isEmpty()) {
                matchings = s;
                cost = s.cost;
                return this;
            }

            lpea = getArray(lpe);
            rpea = getArray(rpe);

            pruned1 = new BitSet();
            pruned2 = new BitSet();
            kept1 = new BitSet();
            kept2 = new BitSet();

            List<Operation> matchCandidates = generateMatchCandidates(pruned1, pruned2, lpea, rpea, s);

            Collections.sort(matchCandidates, comparator);

            nextCandidate:
            for (Operation operation : matchCandidates) {
                Pair<Integer, Integer> pair = (Pair) operation.target;
                int e1 = pair.getFirst();
                int e2 = pair.getSecond();
                for (int e1p = kept1.nextSetBit(0); e1p >= 0; e1p = kept1.nextSetBit(e1p + 1)) {
                    if (pes1.getBRelation(e1, e1p) == BehaviorRelation.CONCURRENCY) {
                        continue nextCandidate;
                    }
                }
                for (int e2p = kept2.nextSetBit(0); e2p >= 0; e2p = kept2.nextSetBit(e2p + 1)) {
                    if (pes2.getBRelation(e2, e2p) == BehaviorRelation.CONCURRENCY) {
                        continue nextCandidate;
                    }
                }
                kept1.set(e1);
                kept2.set(e2);

                updateQueueAncestorsAndDescendants(open, s, operation.nextState, operation);
                // IOUtils.toFile("psp.dot", toDot());
            }

            pruned1.andNot(kept1);
            pruned2.andNot(kept2);

            nextCandidate2:
            for (int j = 0; j < rpea.length; j++) {
                int e2 = rpea[j];
                if (pruned2.get(e2) || kept2.get(e2)) {
                    continue;
                }

                for (int e2p = kept2.nextSetBit(0); e2p >= 0; e2p = kept2.nextSetBit(e2p + 1)) {
                    if (pes2.getBRelation(e2, e2p) == BehaviorRelation.CONCURRENCY) {
                        continue nextCandidate2;
                    }
                }

                BitSet c2p = (BitSet) s.c2.clone();
                c2p.set(e2);
                State nstate = getState(s.c1, s.labels, c2p);
                computeCost(nstate);

                updateQueueAncestorsAndDescendants(open, s, nstate, Operation.rhide(nstate, e2, getLabel(pes2.getLabel(e2)), getReverseLabel(getLabel(pes2.getLabel(e2)))));
                // IOUtils.toFile("psp.dot", toDot());
            }

            for (int i = 0; i < lpea.length; i++) {
                int e1 = lpea[i];
                if (pruned1.get(e1) || kept1.get(e1)) {
                    continue;
                }

                BitSet c1p = (BitSet) s.c1.clone();
                c1p.set(e1);
                State nstate = getState(c1p, s.labels, s.c2);
                computeCost(nstate);

                updateQueueAncestorsAndDescendants(open, s, nstate, Operation.lhide(nstate, e1, getLabel(pes1.getLabel(e1)), getReverseLabel(getLabel(pes1.getLabel(e1)))));
                // IOUtils.toFile("psp.dot", toDot());
            }
        }
        return this;
    }

    public int compareTo(LogBasedPartialSynchronizedProduct<T> o) {
	    State s1;
	    if(this.matchings != null) s1 = this.matchings;
        else s1 = this.open.element();

        State s2;
        if(o.matchings != null) s2 = o.matchings;
        else s2 = o.open.element();

        int c = Short.compare(s1.cost, s2.cost);
        if(c == 0) return Integer.compare(this.guess, o.guess);
        return c;
    }

	public LogBasedPartialSynchronizedProduct<T> perform2(int optimalCost) {
		initializeAStar();
        while (!open.isEmpty()) {
            if(analyzeNextAStarState(optimalCost) == null) return null;
        }
		return this;
	}

    public void initializeAStar() {
        root = getState(new BitSet(), new IntHashBag(), new BitSet());

        open.offer(root);

        comparator = new Comparator<Operation>() {
            @Override
            public int compare(Operation o1, Operation o2) {
                int costCValue = Short.compare(o1.nextState.cost, o2.nextState.cost);
                if (costCValue != 0) return costCValue;
                else return Integer.compare(o1.label, o2.label);
            }
        };
    }

    public LogBasedPartialSynchronizedProduct<T> analyzeNextAStarState(int optimalCost) {
        s = open.poll();
        if(s.cost > optimalCost) {
            return null;
        }

        lpe = pes1.getPossibleExtensions(s.c1);
        rpe = pes2.getPossibleExtensions(s.c2);

        if (lpe.isEmpty() && rpe.isEmpty()) {
            open.clear();
            matchings = s;
            cost = s.cost;
            return this;
        }

        lpea = getArray(lpe);
        rpea = getArray(rpe);

        pruned1 = new BitSet();
        pruned2 = new BitSet();
        kept1 = new BitSet();
        kept2 = new BitSet();

        List<Operation> matchCandidates = generateMatchCandidates(pruned1, pruned2, lpea, rpea, s);

        Collections.sort(matchCandidates, comparator);

        nextCandidate:
        for (Operation operation : matchCandidates) {
            Pair<Integer, Integer> pair = (Pair) operation.target;
            int e1 = pair.getFirst();
            int e2 = pair.getSecond();
            for (int e1p = kept1.nextSetBit(0); e1p >= 0; e1p = kept1.nextSetBit(e1p + 1)) {
                if (pes1.getBRelation(e1, e1p) == BehaviorRelation.CONCURRENCY) {
                    continue nextCandidate;
                }
            }
            for (int e2p = kept2.nextSetBit(0); e2p >= 0; e2p = kept2.nextSetBit(e2p + 1)) {
                if (pes2.getBRelation(e2, e2p) == BehaviorRelation.CONCURRENCY) {
                    continue nextCandidate;
                }
            }
            kept1.set(e1);
            kept2.set(e2);

            updateQueueAncestorsAndDescendants(open, s, operation.nextState, operation);
            // IOUtils.toFile("psp.dot", toDot());
        }

        pruned1.andNot(kept1);
        pruned2.andNot(kept2);

        nextCandidate2:
        for (int j = 0; j < rpea.length; j++) {
            int e2 = rpea[j];
            if (pruned2.get(e2) || kept2.get(e2)) {
                continue;
            }

            for (int e2p = kept2.nextSetBit(0); e2p >= 0; e2p = kept2.nextSetBit(e2p + 1)) {
                if (pes2.getBRelation(e2, e2p) == BehaviorRelation.CONCURRENCY) {
                    continue nextCandidate2;
                }
            }

            BitSet c2p = (BitSet) s.c2.clone();
            c2p.set(e2);
            State nstate = getState(s.c1, s.labels, c2p);
            computeCost(nstate);

            updateQueueAncestorsAndDescendants(open, s, nstate, Operation.rhide(nstate, e2, getLabel(pes2.getLabel(e2)), getReverseLabel(getLabel(pes2.getLabel(e2)))));
            // IOUtils.toFile("psp.dot", toDot());
        }

        for (int i = 0; i < lpea.length; i++) {
            int e1 = lpea[i];
            if (pruned1.get(e1) || kept1.get(e1)) {
                continue;
            }

            BitSet c1p = (BitSet) s.c1.clone();
            c1p.set(e1);
            State nstate = getState(c1p, s.labels, s.c2);
            computeCost(nstate);

            updateQueueAncestorsAndDescendants(open, s, nstate, Operation.lhide(nstate, e1, getLabel(pes1.getLabel(e1)), getReverseLabel(getLabel(pes1.getLabel(e1)))));
            // IOUtils.toFile("psp.dot", toDot());
        }
        return this;
    }

	private void updateQueueAncestorsAndDescendants(Queue<State> open, State state, State next_state, Operation operation) {
        switch (next_state.action) {
            case CREATED:
                open.offer(next_state);
                ancestors.put(next_state, state);
                operations.put(operation.nextState, operation);
            case MERGED:
                descendants.put(state, operation);
            default:
        }
    }

    private int[] getArray(BitSet bitSet) {
        int[] array = bitSetToArrayMap.get(bitSet);
        if(array == null) {
            array = new int[bitSet.cardinality()];
            int bit = bitSet.nextSetBit(0);
            for (int i = 0; i < array.length; i++) {
                array[i] = bit;
                bit = bitSet.nextSetBit(bit + 1);
            }
            bitSetToArrayMap.put(bitSet, array);
        }
        return array;
    }

    private List<Operation> generateMatchCandidates(BitSet pruned1, BitSet pruned2, int[] lpea, int[] rpea, State s) {
        List<Operation> candidates = new ArrayList<>();
        for (int i = 0; i < lpea.length; i++) {
            int e1 = lpea[i];
            int label1 = getLabel(pes1.getLabel(e1));

            BitSet c1p = (BitSet) s.c1.clone();
            c1p.set(e1);

            for (int j = 0; j < rpea.length; j++) {
                int e2 = rpea[j];
                if (label1 == getLabel(pes2.getLabel(e2)) && isOrderPreserving(s, e1, e2)) {
                    pruned1.set(e1);
                    pruned2.set(e2);

                    BitSet c2p = (BitSet) s.c2.clone();
                    c2p.set(e2);
                    IntHashBag labels = IntHashBag.newBag(s.labels);
                    labels.add(label1);

                    State nstate = getState(c1p, labels, c2p);
                    nstate.cost = s.cost; // A matching operation does not change the current cost

                    Operation operation = Operation.match(nstate, new Pair<>(e1, e2), label1, getReverseLabel(label1));

                    candidates.add(operation);
                    // IOUtils.toFile("psp.dot", toDot());
                }
            }
        }
        return candidates;
    }

    private int getLabel(String label) {
        int label1 = labelMap.get(label);
        if(label1 == 0) {
            label1 = labelMap.size() + 1;
            labelMap.put(label, label1);
            reverseLabelMap.put(label1, label);
        }
        return label1;
    }

    private String getReverseLabel(int label) {
        return reverseLabelMap.get(label);
    }

    public List<State> getStates() {
		return states;
	}

    private boolean isOrderPreserving(State s, int e1, Integer e2) {
        BitSet e1dpred = (BitSet) pes1.getDirectPredecessors(e1).clone();
        BitSet e2dpred = (BitSet) pes2.getDirectPredecessors(e2).clone();

        ArrayStack<State> open = new ArrayStack<>();
        Set<State> visited = new UnifiedSet<>();
        Set<State> openSet = new UnifiedSet<>();
        open.push(s);
        openSet.add(s);

        BitSet e1causes = pes1.getLocalConfiguration(e1);
        BitSet e2causes = pes2.getLocalConfiguration(e2);

        Pair<Integer, Integer> matchedEvents;
        Integer hiddenEvent;
        State curr;
        while (!open.isEmpty()) {
            if (e1dpred.isEmpty() && e2dpred.isEmpty())
                break;
            curr = open.pop();
            openSet.remove(curr);
            visited.add(curr);

            for (Operation op : operations.get(curr)) {
                // System.out.println(">> " + op);
                if (op.op == Op.MATCH) {
                    matchedEvents = (Pair<Integer, Integer>) op.target;
                    e1dpred.clear(matchedEvents.getFirst());
                    e2dpred.clear(matchedEvents.getSecond());

                    if (e1causes.get(matchedEvents.getFirst()) != e2causes.get(matchedEvents.getSecond())) {
                        return false;
                    }
                } else if (op.op == Op.RHIDE) {
                    hiddenEvent = (Integer) op.target;
                    e2dpred.clear(hiddenEvent);
                    e2dpred.or(pes2.getDirectPredecessors(hiddenEvent));
                } else {
                    hiddenEvent = (Integer) op.target;
                    e1dpred.clear(hiddenEvent);
                    e1dpred.or(pes1.getDirectPredecessors(hiddenEvent));
                }
            }
            for (State ancestor : ancestors.get(curr)) {
                if (visited.contains(ancestor) || openSet.contains(ancestor)) {
                    continue;
                }
                open.push(ancestor);
                openSet.add(ancestor);
            }
        }
        return true;
    }

    private State getState(BitSet c1, IntHashBag labels, BitSet c2) {
		State newState = new State(c1, labels, c2);
		states.add(newState);

		newState.action = CREATED;

        Set<IntHashBag> set;
        if ((set = stateSpaceTable.get(c1, c2)) != null) {
			if (set.contains(labels)) {
                newState.action = MERGED;
            }else {
                set.add(labels);
            }
		} else {
            set = new UnifiedSet<>();
			set.add(labels);
			stateSpaceTable.put(c1, c2, set);
		}
		return newState;
	}

	public void computeCost(State s) {
	    s.cost = (short) (g(s.c1, s.c2, s.labels) + h(s));
	}

    public int g(BitSet c1, BitSet c2, IntHashBag labels) {
		return (c1.cardinality() + c2.cardinality() - labels.size() * 2);
	}

	public int h(State s) {
        Multiset<String> pf1 = pes1.getPossibleFutureAsMultisetLabels(s.c1);
        Multiset<String> pf2 = pes2.getPossibleFutureAsMultisetLabels(s.c2);
        int size = Multisets.union(Multisets.difference(pf1, pf2), Multisets.difference(pf2, pf1)).size();
        return size;
    }

	public int h1(State s) {
		Set<String> pf2 = pes2.getPossibleFutureAsLabels(s.c2);

		BitSet future = (BitSet) maxConf1.clone();
		future.andNot(s.c1);
		Set<String> diff = translate(future);
		diff.removeAll(pf2);

		return diff.size();
	}

	private Set<String> translate(BitSet bitset) {
		Set<String> set = new LinkedHashSet<>();
		for (int ev = bitset.nextSetBit(0); ev >= 0; ev = bitset.nextSetBit(ev + 1)) {
			set.add(pes1.getLabel(ev));
		}
		return set;
	}

	public LogBasedPartialSynchronizedProduct<T> prune() {
		Set<State> gvisited = new HashSet<>();
		Stack<State> open = new Stack<>();
		this.opSeq = new LinkedList<>();

		for (int i = 0; i < numberOfTargets; i++) {
			State s = matchings;
			if (s == null) continue;
			open.push(s);
			Set<State> visited = new HashSet<>();
			while (!open.isEmpty()) {
				State curr = open.pop();
				visited.add(curr);

				for (State pred : ancestors.get(curr)) {
					if (!visited.contains(pred) && !open.contains(pred)) {
						open.push(pred);
						for (Operation op : descendants.get(pred)) {
							if (op.nextState.equals(curr)) {
								opSeq.addFirst(op);
								break;
							}
						}
					}
				}
			}

			gvisited.addAll(visited);
		}

		this.relevantStates = gvisited;
		cleanDataTables();

		return this;
	}

	private void cleanDataTables() {
        labelMap.clear();
        reverseLabelMap.clear();
        bitSetToArrayMap.clear();
    }

	public String toDot() {
		StringWriter str = new StringWriter();
		PrintWriter out = new PrintWriter(str);

		out.println("digraph G {\n\t node [shape=box];");
		Map<State, Integer> rstates = new HashMap<>();

		for (int i = 0; i < states.size(); i++) {
			State s = states.get(i);
			if (relevantStates == null || relevantStates.contains(s)) {
				rstates.put(s, i);
				// if (matchings.containsValue(s))
				// out.printf("\tn%d
				// [label=\"%s,%s\\n%s\\n%3.2f\",color=blue];\n", i, s.c1, s.c2,
				// s.labels, s.weight);
				// else
				if (matchings.equals(s))
					out.printf("\tn%d [label=\"%s,%s\\n%s\\n%d\", color=red];\n", i, s.c1, s.c2, s.labels, s.cost);
				else
					out.printf("\tn%d [label=\"%s,%s\\n%s\\n%d\"];\n", i, s.c1, s.c2, s.labels, s.cost);
			}
		}

		Collection<State> lstates = relevantStates != null ? relevantStates : states;

		for (State s : lstates) {
			Integer src = rstates.get(s);
			for (Operation op : descendants.get(s)) {
				if (relevantStates == null || relevantStates.contains(op.nextState)) {
					Integer tgt = rstates.get(op.nextState);
					out.printf("\tn%d -> n%d [label=\"%s\"];\n", src, tgt, op);
				}
			}
		}
		out.println("}");
		return str.toString();
	}

	public List<Operation> getOperationSequence() {
		if (opSeq == null)
			prune();
		return opSeq;
	}
}
