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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.comparison.LogBasedPartialSynchronizedProduct.Op;
import ee.ut.eventstr.comparison.LogBasedPartialSynchronizedProduct.Operation;
import ee.ut.eventstr.comparison.LogBasedPartialSynchronizedProduct.State;
import ee.ut.org.processmining.framework.util.Pair;
import org.eclipse.collections.impl.bag.mutable.primitive.IntHashBag;

import java.util.*;

/**
 * @authors Nick van Beest, Luciano Garcia-Banuelos
 * @date 23/11/2016
 */
public class DiffLLVerbalizer <T> {
	private PESSemantics<T> pes1;
	private PESSemantics<T> pes2;
	
	private Set<Integer> unobservedEvents;
	private Set<Integer> eventsConsideredByConflictRelation;
	private List<List<Operation>> opSeqs;
	
//	private Table<BitSet, BitSet, Map<Multiset<String>, State>> stateSpace;
//	private Table<BitSet, BitSet, Map<Multiset<Integer>, State>> stateSpace;
	private Table<BitSet, BitSet, Map<IntHashBag, State>> stateSpace;
	private Multimap<State, Operation> descendants;
	private State root;
	
	private Set<String> statements;
	
	private Table<BitSet, BitSet, Map<Integer, int[]>> globalDiffs;
	
	public DiffLLVerbalizer(PESSemantics<T> pes1, PESSemantics<T> pes2) {
		this.pes1 = pes1;
		this.pes2 = pes2;
		this.unobservedEvents = new HashSet<>();
		this.eventsConsideredByConflictRelation = new HashSet<>();
		this.opSeqs = new ArrayList<>();
		this.stateSpace = HashBasedTable.create();
		this.descendants = HashMultimap.create();
//		this.root = new State(new BitSet(), HashMultiset.create(), new BitSet());
		this.root = new State(new BitSet(), new IntHashBag(), new BitSet());
		this.globalDiffs = HashBasedTable.create();
		
		this.statements = new HashSet<String>();
	}
	
	public void addPSP(List<Operation> opSeq) {
		opSeqs.add(opSeq);
	}

	public Set<String> verbalize() {
		List<List<int[]>> lofl = new LinkedList<>();
		for (List<Operation> opSeq: opSeqs) {
			lofl.add(getADiffContexts(opSeq));
		}
		
		for (int index = 0; index < opSeqs.size(); index++) {
			List<Operation> opSeq = opSeqs.get(index);
			List<int[]> diffIndexesList = lofl.get(index);
						
			verbalizeDifferences(opSeq, diffIndexesList, index);
		}
		
		return statements;
	}
	
	private void verbalizeDifferences(List<Operation> opSeq, List<int[]> diffIndexesList, int index) {
		int[] diffIndexes;

		for (int di = 0; di < diffIndexesList.size(); di++) {
			diffIndexes = diffIndexesList.get(di);
			Operation firstMatching = opSeq.get(diffIndexes[0]);
			Operation secondMatching = opSeq.get(diffIndexes[2]);
			Operation firstHiding = opSeq.get(diffIndexes[1]);
						
			Pair<Integer, Integer> firstMatchingEventPair = (Pair)firstMatching.target;
			Pair<Integer, Integer> secondMatchingEventPair = (Pair)secondMatching.target;

			BitSet context1 = (BitSet)secondMatching.nextState.c1.clone();
			context1.andNot(firstMatching.nextState.c1);
			context1.clear(secondMatchingEventPair.getFirst());
			
			BitSet context2 = (BitSet)secondMatching.nextState.c2.clone();
			context2.andNot(firstMatching.nextState.c2);
			context2.clear(secondMatchingEventPair.getSecond());
			
			String firstHidingLabel = firstHiding.getLabel();
			int firstHidingInt = firstHiding.label;

            if(firstMatching.getLabel().equals("_0_") || firstHidingLabel.equals("_0_") ||
                    firstMatching.getLabel().equals("_1_") || firstHidingLabel.equals("_1_"))
                continue;

			if (firstHiding.op == Op.LHIDE) {
				Pair<Operation, Boolean> pair = findRHide(opSeq, diffIndexes, firstHidingLabel);
				Operation secondHiding = pair.getFirst();

				// Found a corresponding RHIDE
				if (pair.getSecond()) {
					context1.set(firstMatchingEventPair.getFirst());
					context2.set(firstMatchingEventPair.getSecond());

					if (!globalDiffs.contains(context1, context2)) {
						verbalizeBehDiffFromModelPerspective(firstMatchingEventPair.getFirst(), firstMatching.getLabel(), (Integer)firstHiding.target, firstHidingLabel,
								firstMatchingEventPair.getSecond(), firstMatching.getLabel(), (Integer)secondHiding.target, secondHiding.getLabel());
					}
				} 
				else if (secondHiding != null) {
					// ========= Symmetric  <<==
					if (!globalDiffs.contains(context1, context2)) {
//						System.out.printf("In log 2, after the occurrence of %s(%d), %s(%d) is substituted by %s(%d)\n",
//								firstMatching.getLabel(), firstMatchingEventPair.getFirst(),
//								firstHiding.getLabel(), (Integer)firstHiding.target,
//								secondHiding.getLabel(), (Integer)secondHiding.target);
						statements.add(
								String.format("In log 2, after the occurrence of %s, %s is substituted by %s",
										getCorrectContext(firstMatching.getLabel()),
										firstHiding.getLabel(),
										secondHiding.getLabel())
						);
					}
				} 
				else {
					// No RHIDE found within difference context
					if (firstMatching.nextState.labels.contains(firstHidingInt)) {
						if (!globalDiffs.contains(context1, context2)) {
							int c = 1;
							
							BitSet interval = new BitSet();
							BitSet past = new BitSet();
							past.set(diffIndexes[0], diffIndexes[1]);
							
							if ((diffIndexes[2] - diffIndexes[1]) > 1) {
								while ((pes2.getLabels().contains(opSeq.get(diffIndexes[1]).getLabel()) == pes2.getLabels().contains(opSeq.get(diffIndexes[1] + c).getLabel())) &&
										(diffIndexes[1] + c < diffIndexes[2])) {
									c++;
								}
								
								interval.set(diffIndexes[1], diffIndexes[1] + c);
								
								if (diffIndexes[1] + c == diffIndexes[2]) {
									if ((opSeq.get(diffIndexes[2]).op == Op.MATCH) && 
											(pes1.getLabel(context1.previousSetBit(context1.length())).equals(opSeq.get(diffIndexes[2]).getLabel()))) {
										interval.set(diffIndexes[2]);
									}
									c--;
								}
								if (!pes2.getLabels().contains(opSeq.get(diffIndexes[1] + c).getLabel())) {
									int[] ndiffInd = {diffIndexes[0], diffIndexes[1] + c, diffIndexes[2]};
									diffIndexesList.add(ndiffInd);
								}
							}
							else {
								interval.set(diffIndexes[1]);
							}
							
//							System.out.printf("In log 1, after the occurrence of %s, %s is repeated, while in log 2 it is not\n",
//									translate(past, opSeq),
//									translate(interval, opSeq)
//									);	
							statements.add(
									String.format("In log 1, after the occurrence of %s, %s is repeated, while in log 2 it is not",
											translate(past, opSeq),
											translate(interval, opSeq)
									)
							);	
						}
					} 
					else {
						Integer e2 = firstMatchingEventPair.getSecond();
						BitSet dconflict = pes2.getDirectConflictSet(e2);						
						boolean found = false;
						Integer e2p = null;

						for (Integer pe = dconflict.nextSetBit(0); pe >= 0; pe = dconflict.nextSetBit(pe + 1)) {
							if (!pe.equals(e2) && pes2.getBRelation(e2, pe) == BehaviorRelation.CONFLICT
									&& firstHidingLabel.equals(pes2.getLabel(pe))) {
								eventsConsideredByConflictRelation.add(pe);
								found = true;
								e2p = pe;
								break;
							}
						}


                        if(firstMatching.getLabel().equals("_0_") || firstHiding.getLabel().equals("_0_") ||
                                firstMatching.getLabel().equals("_1_") || firstHiding.getLabel().equals("_1_"))
                            continue;


						if (found) {
//							context1.set(firstMatchingEventPair.getFirst());							
//							context2.set(firstMatchingEventPair.getSecond());
//							context2.set(e2);
							context2.or(dconflict);
							context2.set(e2);
							context1.set(firstMatchingEventPair.getFirst());

                            if(firstMatching.getLabel().equals("_0_") || firstHiding.getLabel().equals("_0_") ||
                                    firstMatching.getLabel().equals("_1_") || firstHiding.getLabel().equals("_1_"))
                                continue;

							if (!globalDiffs.contains(context1, context2)) {

                                verbalizeBehDiffFromModelPerspective(
										firstMatchingEventPair.getFirst(), firstMatching.getLabel(), (Integer)firstHiding.target, firstHiding.getLabel(), 
										firstMatchingEventPair.getSecond(), firstMatching.getLabel(), e2p, pes2.getLabel(e2p));
							}							
						} 
						else {
							found = false;
							e2p = null;
							Pair<Integer, Integer> secondMatchingPair = (Pair)secondMatching.target;
							
							e2 = secondMatchingPair.getSecond(); // maybe second
							dconflict = pes2.getDirectConflictSet(e2);

							for (Integer pe = dconflict.nextSetBit(0); pe >= 0; pe = dconflict.nextSetBit(pe + 1)) {
								if (!pe.equals(e2) && pes2.getBRelation(e2, pe) == BehaviorRelation.CONFLICT && firstHidingLabel.equals(pes2.getLabel(pe))) {
									eventsConsideredByConflictRelation.add(pe);
									found = true;
									e2p = pe;
									break;
								}
							}

							if (found) {
								found = false;

								for (Operation succ: descendants.get(firstMatching.nextState)) {
									if (succ.op == Op.MATCH || succ.op == Op.MATCHNSHIFT) {
										found = matchFirst(succ.nextState, context1, secondMatchingPair.getFirst()); // maybe second
										if (found) {
											break;
										}
									}
								}

								if (found) {
									if (!globalDiffs.contains(context1, context2)) {
//										System.out.printf("In log 2, %s(%s) can be skipped, while in log 1 it cannot\n",
//												translate(context1, 1), context1);
										statements.add(		
												String.format("In log 2, %s can be skipped, while in log 1 it cannot",
														translate(context1, 1)
												)
										);
									}
								}
								else {
									context2.or(dconflict);
									context2.set(e2);
									context1.set(firstMatchingEventPair.getFirst());

//									if (!globalDiffs.contains(context1, context2)) {
//										verbalizeBehDiffFromModelPerspective(
//											firstMatchingEventPair.getFirst(), firstMatching.getLabel(), (Integer)firstHiding.target, firstHiding.getLabel(), 
//											firstMatchingEventPair.getSecond(), firstMatching.getLabel(), e2p, pes2.getLabel(e2p));
//									}	
								}
							}
							else {
								e2 = firstMatchingEventPair.getSecond();
								BitSet succs2 = pes2.getDirectSuccessors(e2);

								succs2.andNot(context2);
								
								found = false;
								e2p = null;
								
								for (int ev = succs2.nextSetBit(0); ev >= 0; ev = succs2.nextSetBit(ev + 1)) {
									if (firstHidingLabel.equals(pes2.getLabel(ev))) {
										found = true;
										e2p = ev;
										break;
									}
								}

                                if(secondMatching.getLabel().equals("_0_") || firstHiding.getLabel().equals("_0_") ||
                                        secondMatching.getLabel().equals("_1_") || firstHiding.getLabel().equals("_1_"))
                                    continue;
								
								if (found) {
									context1.set(secondMatchingEventPair.getFirst());
									context2.set(e2p);
									context2.set(secondMatchingEventPair.getSecond());

									if (!globalDiffs.contains(context1, context2)) {

										verbalizeBehDiffFromModelPerspective(
												secondMatchingEventPair.getFirst(), secondMatching.getLabel(), (Integer)firstHiding.target, firstHiding.getLabel(), 
												secondMatchingEventPair.getSecond(), secondMatching.getLabel(), e2p, pes2.getLabel(e2p));
									}
								} 
								else {
									found = false;
									e2p = null;
									BitSet succs2nd = pes2.getDirectSuccessors(secondMatchingEventPair.getSecond());

									for (int ev = succs2nd.nextSetBit(0); ev >= 0; ev = succs2nd.nextSetBit(ev + 1)) {
										if (firstHidingLabel.equals(pes2.getLabel(ev))) {
											found = true;
											e2p = ev;
											break;
										}
									}
									
									if (found) {
										context1.set(secondMatchingEventPair.getFirst());
										context2.set(e2p);
										context2.set(secondMatchingEventPair.getSecond());								

										if (!globalDiffs.contains(context1, context2)) {
											// task relocation: this is the statement
											verbalizeBehDiffFromModelPerspective(
													secondMatchingEventPair.getFirst(), secondMatching.getLabel(), (Integer)firstHiding.target, firstHiding.getLabel(), 
													secondMatchingEventPair.getSecond(), secondMatching.getLabel(), e2p, pes2.getLabel(e2p));
										}
									} 
									else {
										found = false;
										e2p = null;
										for (int i = diffIndexes[0]; i > 0; i--) {
											if (opSeq.get(i).op == Op.RHIDE) {
												Integer hiddenEvent = (Integer)opSeq.get(i).target;
												if (firstHidingLabel.equals(pes2.getLabel(hiddenEvent))) {
													found = true;
													e2p = hiddenEvent;
													break;
												}
											}
										}
										if (found) {
											context1.set(firstMatchingEventPair.getFirst());
											context2.set(firstMatchingEventPair.getSecond());
											context2.set(e2p);

                                            if(firstMatching.getLabel().equals("_0_") || firstHiding.getLabel().equals("_0_") ||
                                                    firstMatching.getLabel().equals("_1_") || firstHiding.getLabel().equals("_1_"))
                                                continue;

											if (!globalDiffs.contains(context1, context2)) {
												
												verbalizeBehDiffFromModelPerspective(
														firstMatchingEventPair.getFirst(), firstMatching.getLabel(), (Integer)firstHiding.target, firstHiding.getLabel(), 
														firstMatchingEventPair.getSecond(), firstMatching.getLabel(), e2p, pes2.getLabel(e2p));
											}
										} 
										else {
											context1.set(firstMatchingEventPair.getFirst());
											
											if (!globalDiffs.contains(context1, context2)) {												
												// first, check the entire context to see if there are to identify intervals of consecutive repeated events 
												// and intervals of consecutive new events (i.e. events not occurring in 1 of the 2 logs)
												int c = 1;

												BitSet interval = new BitSet();
												BitSet past = new BitSet();
												past.set(diffIndexes[0], diffIndexes[1]);
												
												if ((diffIndexes[2] - diffIndexes[1]) > 1) {
													while ((pes2.getLabels().contains(opSeq.get(diffIndexes[1]).getLabel()) == pes2.getLabels().contains(opSeq.get(diffIndexes[1] + c).getLabel())) &&
															(diffIndexes[1] + c < diffIndexes[2])) {
														c++;
													}
													
													interval.set(diffIndexes[1], diffIndexes[1] + c);
													// if it concerns an interval of new events, add the new set of diffIndexes to the main loop, to process it in the next iteration 
													if (diffIndexes[1] + c == diffIndexes[2]) {
														c--;
													}
													if (pes2.getLabels().contains(opSeq.get(diffIndexes[1] + c).getLabel())) {
														int[] ndiffInd = {diffIndexes[0], diffIndexes[1] + c, diffIndexes[2]};
														diffIndexesList.add(ndiffInd);
													}
												}
												else {
													interval.set(diffIndexes[1]);
												}
												
//												System.out.printf("In log 1, %s occurs after %s, while in log 2 it does not\n",
//														translate(interval, opSeq),
//														translate(past, opSeq)
//													);
												statements.add(
														String.format("In log 1, %s occurs after %s, while in log 2 it does not",
																translate(interval, opSeq),
																translate(past, opSeq)
														)
												);
											}
										}
									}
								}
							}
						}
					}
				}

			} 
			else {
				Pair<Operation, Boolean> pair = findLHide(opSeq, diffIndexes, firstHidingLabel);
				Operation secondHiding = pair.getFirst();
				
				// Found an LHIDE on an event with the same label
				if (pair.getSecond()) {
					context1.set(firstMatchingEventPair.getFirst());
					context2.set(firstMatchingEventPair.getSecond());

                    if(firstMatching.getLabel().equals("_0_") || secondHiding.getLabel().equals("_0_") ||
                            firstMatching.getLabel().equals("_1_") || secondHiding.getLabel().equals("_1_"))
                        continue;

					if (!globalDiffs.contains(context1, context2)) {
						
						verbalizeBehDiffFromModelPerspective(
								firstMatchingEventPair.getFirst(), firstMatching.getLabel(), (Integer)secondHiding.target, secondHiding.getLabel(),
								firstMatchingEventPair.getSecond(), firstMatching.getLabel(), (Integer)firstHiding.target, firstHiding.getLabel());
					}					
				} 
				else if (secondHiding != null) {
					// ========= Symmetric <<==
					if (!globalDiffs.contains(context1, context2)) {
//						System.out.printf("In log 2, after the occurrence of %s(%d), %s(%d) is substituted by %s(%d)\n",
//								firstMatching.getLabel(), firstMatchingEventPair.getFirst(),
//								firstHiding.getLabel(), (Integer)firstHiding.target,
//								secondHiding.getLabel(), (Integer)secondHiding.target);
						statements.add(
								String.format("In log 2, after the occurrence of %s, %s is substituted by %s",
										firstMatching.getLabel(), 
										firstHiding.getLabel(), 
										secondHiding.getLabel()
								)
						);
					}
				} 
				else {
					// No LHIDE found within this Difference Context
					if (firstMatching.nextState.labels.contains(firstHidingInt)) {
						if (!globalDiffs.contains(context1, context2)) {
							
							// first, check the entire context to see if there are to identify intervals of consecutive repeated events 
							// and intervals of consecutive new events (i.e. events not occurring in 1 of the 2 logs)		
							int c = 1;
							
							BitSet interval = new BitSet();
							BitSet past = new BitSet();
							past.set(diffIndexes[0], diffIndexes[1]);
							
							if ((diffIndexes[2] - diffIndexes[1]) > 1) {
								
								while ((pes1.getLabels().contains(opSeq.get(diffIndexes[1]).getLabel()) == pes1.getLabels().contains(opSeq.get(diffIndexes[1] + c).getLabel())) &&
										(diffIndexes[1] + c < diffIndexes[2])) {
									c++;
								}
								
								interval.set(diffIndexes[1], diffIndexes[1] + c);
								
								if (diffIndexes[1] + c == diffIndexes[2]) {
									if ((opSeq.get(diffIndexes[2]).op == Op.MATCH) && 
											(pes2.getLabel(context2.previousSetBit(context2.length())).equals(opSeq.get(diffIndexes[2]).getLabel()))) {
										interval.set(diffIndexes[2]);
									}
									c--;
								}
								if (!pes1.getLabels().contains(opSeq.get(diffIndexes[1] + c).getLabel())) {
									int[] ndiffInd = {diffIndexes[0], diffIndexes[1] + c, diffIndexes[2]};									
									diffIndexesList.add(ndiffInd);
								}
							}
							else {
								interval.set(diffIndexes[1]);
							}
							
//							System.out.printf("In log 2, after the occurrence of %s, %s is repeated, while in log 1 it is not\n",
//									translate(past, opSeq),
//									translate(interval, opSeq)
//									);
							statements.add(
									String.format("In log 2, after the occurrence of %s, %s is repeated, while in log 1 it is not",
											translate(past, opSeq),
											translate(interval, opSeq)
									)
							);
						}
					} 
					else {
						Integer e1 = firstMatchingEventPair.getFirst();
						BitSet dconflict = pes1.getDirectConflictSet(e1);
						boolean found = false;
						Integer e1p = null;
												
						for (Integer pe = dconflict.nextSetBit(0); pe >= 0; pe = dconflict.nextSetBit(pe + 1)) {
							if (!pe.equals(e1) && pes1.getBRelation(e1, pe) == BehaviorRelation.CONFLICT
									&& firstHidingLabel.equals(pes1.getLabel(pe))) {
								eventsConsideredByConflictRelation.add(pe);
								found = true;
								e1p = pe;
								break;
							}
						}
						
						if (found) {
							context1.or(dconflict);
							context1.set(e1);
							context2.set(firstMatchingEventPair.getSecond());

							if(firstMatching.getLabel().equals("_0_") || pes1.getLabel(e1p).equals("_0_") ||
                                    firstMatching.getLabel().equals("_1_") || pes1.getLabel(e1p).equals("_1_"))
                                continue;

							if (!globalDiffs.contains(context1, context2)) {
								
								verbalizeBehDiffFromModelPerspective(
										firstMatchingEventPair.getFirst(), firstMatching.getLabel(), (Integer)e1p, pes1.getLabel(e1p), 
										firstMatchingEventPair.getSecond(), firstMatching.getLabel(), (Integer)firstHiding.target, firstHiding.getLabel());

							}
						} 
						else {
							found = false;
							e1p = null;
							Pair<Integer, Integer> secondMatchingPair = (Pair)secondMatching.target;
							
							e1 = secondMatchingPair.getFirst();
							dconflict = pes1.getDirectConflictSet(e1);
							
							for (Integer pe = dconflict.nextSetBit(0); pe >= 0; pe = dconflict.nextSetBit(pe + 1)) {
								if (!pe.equals(e1) && pes1.getBRelation(e1, pe) == BehaviorRelation.CONFLICT
										&& firstHidingLabel.equals(pes1.getLabel(pe))) {
									eventsConsideredByConflictRelation.add(pe);
									found = true;
									e1p = pe;
									break;
								}
							}
							if (found) {
								found = false;
								
								for (Operation succ: descendants.get(firstMatching.nextState)) {
									if (succ.op == Op.MATCH || succ.op == Op.MATCHNSHIFT) {
										found = matchSecond(succ.nextState, context2, secondMatchingPair.getSecond());
										if (found) {
											break;
										}
									}
								}
								
								if (found) {
									if (!globalDiffs.contains(context1, context2)) {
//										System.out.printf("In log 1, %s(%s) can be skipped, while in log 2 it cannot\n",
//												translate(context2), context2);
										statements.add(
												String.format("In log 1, %s can be skipped, while in log 2 it cannot",
														translate(context2)
												)
										);
									}
								} 
								else {
									context1.set(e1p);
									context1.set(secondMatchingEventPair.getFirst());									
									context2.set(secondMatchingEventPair.getSecond());

                                    if(pes1.getLabel(e1p).equals("_0_") || secondMatching.getLabel().equals("_0_") ||
                                            pes1.getLabel(e1p).equals("_1_") || secondMatching.getLabel().equals("_1_"))
                                        continue;

									if (!globalDiffs.contains(context1, context2)) {

										verbalizeBehDiffFromModelPerspective(
												(Integer)e1p, pes1.getLabel(e1p), secondMatchingEventPair.getFirst(), secondMatching.getLabel(), 
												(Integer)firstHiding.target, firstHiding.getLabel(), secondMatchingEventPair.getSecond(), secondMatching.getLabel());
									}
								}
							} 
							else {
								e1 = secondMatchingEventPair.getFirst();
								BitSet preds1 = (BitSet)pes1.getDirectPredecessors(e1).clone();
								
								preds1.andNot(context1);
								
								found = false;
								e1p = null;
								for (Integer ev = preds1.nextSetBit(0); ev >= 0; ev = preds1.nextSetBit(ev + 1)) {
									if (firstHidingLabel.equals(pes1.getLabel(ev))) {
										found = true;
										e1p = ev;
										break;
									}
								}
								
								if (found) {
									context1.set(e1p);
									context1.set(firstMatchingEventPair.getFirst());									
									context2.set(firstMatchingEventPair.getSecond());

                                    if(firstMatching.getLabel().equals("_0_") || pes1.getLabel(e1p).equals("_0_") ||
                                            firstMatching.getLabel().equals("_1_") || pes1.getLabel(e1p).equals("_1_"))
                                        continue;

									if (!globalDiffs.contains(context1, context2)) {

										verbalizeBehDiffFromModelPerspective(
												firstMatchingEventPair.getFirst(), firstMatching.getLabel(), (Integer)e1p, pes1.getLabel(e1p), 
												firstMatchingEventPair.getSecond(), firstMatching.getLabel(), (Integer)firstHiding.target, firstHiding.getLabel());
									}									
								} 
								else {
									
									found = false;
									e1p = null;
									
									BitSet succs = pes1.getDirectSuccessors(firstMatchingEventPair.getFirst());
									for (int ev = succs.nextSetBit(0); ev >= 0; ev = succs.nextSetBit(ev + 1)) {
										if (firstHidingLabel.equals(pes1.getLabel(ev))) {
											found = true;
											e1p = ev;
											break;
										}
									}
									if (found) {
										context1.set(e1p);
										context1.set(secondMatchingEventPair.getFirst());									
										context2.set(secondMatchingEventPair.getSecond());

                                        if(pes1.getLabel(e1p).equals("_0_") || secondMatching.getLabel().equals("_0_") ||
                                                pes1.getLabel(e1p).equals("_1_") || secondMatching.getLabel().equals("_1_"))
                                            continue;

										if (!globalDiffs.contains(context1, context2)) {

											verbalizeBehDiffFromModelPerspective(
													(Integer)e1p, pes1.getLabel(e1p), secondMatchingEventPair.getFirst(), secondMatching.getLabel(),
													(Integer)firstHiding.target, firstHiding.getLabel(), secondMatchingEventPair.getSecond(), secondMatching.getLabel());
										}									

									} 
									else { 
										context2.set((Integer)firstHiding.target);
										if (!globalDiffs.contains(context1, context2)) {											
											// first, check the entire context to see if there are to identify intervals of consecutive repeated events 
											// and intervals of consecutive new events (i.e. events not occurring in 1 of the 2 logs)
											int c = 1;
											
											BitSet interval = new BitSet();
											BitSet past = new BitSet();
											past.set(diffIndexes[0], diffIndexes[1]);

											if ((diffIndexes[2] - diffIndexes[1]) > 1) {
												while ((pes1.getLabels().contains(opSeq.get(diffIndexes[1]).getLabel()) == pes1.getLabels().contains(opSeq.get(diffIndexes[1] + c).getLabel())) &&
														(diffIndexes[1] + c < diffIndexes[2])) {
													c++;
												}
												
												interval.set(diffIndexes[1], diffIndexes[1] + c);
												// if it concerns an interval of new events, add the new set of diffIndexes to the main loop, to process it in the next iteration 
												if (diffIndexes[1] + c == diffIndexes[2]) {
													c--;
												}
												if (pes1.getLabels().contains(opSeq.get(diffIndexes[1] + c).getLabel())) {
													int[] ndiffInd = {diffIndexes[0], diffIndexes[1] + c, diffIndexes[2]};
													diffIndexesList.add(ndiffInd);
												}
											}
											else {
												interval.set(diffIndexes[1]);
											}
											
//											System.out.printf("In log 2, %s occurs after %s, while in log 1 it does not\n",
//													translate(interval, opSeq),
//													translate(past, opSeq)
//												);
											statements.add(
													String.format("In log 2, %s occurs after %s, while in log 1 it does not",
															translate(interval, opSeq),
															translate(past, opSeq)
													)
											);
										}
									}
								}
							}
						}
					}
				}
			}
			

			Map<Integer, int[]> diffs = globalDiffs.get(context1, context2);
			if (diffs == null) {
//				if ((diffIndexes[2] - diffIndexes[1]) == 1) {
//					globalDiffs.put(context1, context2, diffs = new HashMap<>()); // this was the original line
//				}
//				else {
					diffs = new HashMap<>();
//				}
			}
			if (!diffs.containsKey(index)) {
				diffs.put(index, diffIndexes);
			}
		}
	}

	private String getCorrectContext(String label) {
		if(label.equals("_0_"))
			return "the start of the trace";
		else if(label.equals("_1_"))
			return "the end of the trace";
		return label;
	}

	private void verbalizeBehDiffFromModelPerspective(Integer e1,
			String e1l, Integer e1p, String e1pl,
			Integer e2, String e2l, Integer e2p, String e2pl) {
//		System.out.printf("In log 1, %s(%d) %s %s(%d), while in log 2 %s(%d) %s %s(%d)\n",
//				e1l, e1, verbalizeBRel(pes1.getBRelation(e1, e1p)), e1pl, e1p,
//				e2l, e2, verbalizeBRel(pes2.getBRelation(e2, e2p)), e2pl, e2p);

        if(pes1.getBRelation(e1, e1p).equals(pes2.getBRelation(e2, e2p)))
            return;

		String br1 = verbalizeBRel(pes1.getBRelation(e1, e1p));
		String br2 = verbalizeBRel(pes2.getBRelation(e2, e2p));
		
		String temp;
		if (pes1.getBRelation(e1, e1p).equals(BehaviorRelation.CONCURRENCY) || pes1.getBRelation(e1, e1p).equals(BehaviorRelation.CONFLICT)) {
			if (e1l.compareTo(e1pl) > 0) {
				temp = e1l;
				e1l = e1pl;
				e1pl = temp;
			}
		}
		
		if (pes2.getBRelation(e2, e2p).equals(BehaviorRelation.CONCURRENCY) || pes2.getBRelation(e2, e2p).equals(BehaviorRelation.CONFLICT)) {
			if (e2l.compareTo(e2pl) > 0) {
				temp = e2l;
				e2l = e2pl;
				e2pl = temp;
			}
		}
		
		statements.add(
				String.format("In log 1, %s %s %s, while in log 2 %s %s %s",
						e1l, br1, e1pl,
						e2l, br2, e2pl
				)
		);		
	}

	private String verbalizeBRel(BehaviorRelation bRelation) {
		switch (bRelation) {
		case CAUSALITY: return "occurs before";
		case INV_CAUSALITY: return "occurs after";
		case CONCURRENCY: return "occurs concurrently to";
		case CONFLICT: return "cannot occur in the same run with";
		default:
			break;
		}
		return null;
	}
		
	private boolean matchFirst(State curr, BitSet context1, Integer ev1) {
		boolean result = false;
		for (Operation op: descendants.get(curr)) {
			switch (op.op) {
			case MATCH:
			case MATCHNSHIFT:
				Pair<Integer, Integer> pair = (Pair)op.target;
				if (context1.get(pair.getFirst())) {
					result = matchFirst(op.nextState, context1, ev1);
				}
				else if (pair.getFirst().equals(ev1)) {
					return true;
				}
				break;
			case RHIDE:
			case RHIDENSHIFT:
//				Pair<Integer, Integer> pair = (Pair)op.target;
				if (context1.get((Integer)op.target)) {
					result = matchFirst(op.nextState, context1, ev1);
				}
				break;
			default:
				break;			
			}
		}
		return result;
	}

	private boolean matchSecond(State curr, BitSet context2, Integer ev2) {
		boolean result = false;
		for (Operation op: descendants.get(curr)) {
			switch (op.op) {
			case MATCH:
			case MATCHNSHIFT:
				Pair<Integer, Integer> pair = (Pair)op.target;
				if (context2.get(pair.getSecond())) {
					result = matchSecond(op.nextState, context2, ev2);
				}
				else if (pair.getSecond().equals(ev2)) {
					return true;
				}
				break;
			case RHIDE:
			case RHIDENSHIFT:
//				Pair<Integer, Integer> pair = (Pair)op.target;
				if (context2.get((Integer)op.target)) {
					result = matchSecond(op.nextState, context2, ev2);
				}
				break;
			default:
				break;			
			}
		}
		return result;
	}
	
	private Pair<Operation, Boolean> findLHide(List<Operation> opSeq, int[] diffIndexes, String firstHidingLabel) {
		Operation firstRHiding = null;
		for (int i = diffIndexes[1] + 1; i < diffIndexes[2]; i++) {
			Operation secondHidingOperation = opSeq.get(i);
			if (secondHidingOperation.op == Op.LHIDE) {
				if (firstHidingLabel.equals(secondHidingOperation.getLabel())) {
//					System.out.println("Found a matching for hidden event: " + secondHidingOperation.target);
					return new Pair<>(secondHidingOperation, true);
				} 
				else if (firstRHiding == null) {
					firstRHiding = secondHidingOperation;
				}
			}
		}
		return new Pair<>(firstRHiding, false);
	}

	private Pair<Operation, Boolean> findRHide(List<Operation> opSeq, int[] diffIndexes, String firstHidingLabel) {
		Operation firstRHiding = null;
		for (int i = diffIndexes[1] + 1; i < diffIndexes[2]; i++) {
			Operation secondHidingOperation = opSeq.get(i);
			if (secondHidingOperation.op == Op.RHIDE || secondHidingOperation.op == Op.RHIDENSHIFT) {
				if (firstHidingLabel.equals(secondHidingOperation.getLabel())) {
//					System.out.println("Found a matching for hidden event: " + secondHidingOperation.target);
					return new Pair<>(secondHidingOperation, true);
				} 
				else if (firstRHiding == null) {
					firstRHiding = secondHidingOperation;
				}
			}
		}
		return new Pair<>(firstRHiding, false);
	}

	private List<int[]> getADiffContexts(List<Operation> opSeq) {
		List<int[]> differences = new ArrayList<>();
		int[] diffIndexes = null;
		boolean visibleEventHasBeenHidden = false;
		
		State pred = root;
		
		for (int i = 0; i < opSeq.size(); i++) {
			Operation curr = opSeq.get(i);
						
			State state = curr.nextState;
//			Map<Multiset<String>, State> map = stateSpace.get(state.c1, state.c2);
//			Map<Multiset<Integer>, State> map = stateSpace.get(state.c1, state.c2);
			Map<IntHashBag, State> map = stateSpace.get(state.c1, state.c2);
			if (map == null) {
				stateSpace.put(state.c1, state.c2, map = new HashMap<>());
			}
			if (map.containsKey(state.labels)) {
				state = map.get(state.labels);
				curr.nextState = state;
			} 
			else {
				map.put(state.labels, state);
			}
			
			boolean found = false;
			for (Operation desc: descendants.get(pred)) {
				if (desc.op == curr.op) {
					if (curr.op == Op.MATCH || curr.op == Op.MATCHNSHIFT) {
						Pair<Integer, Integer> pair1 = (Pair)curr.target;
						Pair<Integer, Integer> pair2 = (Pair)desc.target;
						if (pair1.equals(pair2)) {
							found = true;
							break;
						}
					} 
					else {
						Integer ev1 = (Integer)curr.target;
						Integer ev2 = (Integer)desc.target;
						if (ev1.equals(ev2)) {
							found = true;
							break;
						}
					}
				}
			}
			if (!found) {
				descendants.put(pred, curr);
			}
			pred = state;

			
			if (curr.op == Op.MATCH || curr.op == Op.MATCHNSHIFT) {
				unobservedEvents.remove((Integer)((Pair)curr.target).getSecond());
			}
			else if (curr.op != Op.LHIDE) {
				unobservedEvents.remove((Integer)curr.target);
			}
			
			if (diffIndexes == null) {
				if (curr.op == Op.LHIDE) {
//					System.out.println("Found earliest discrepancy (LHIDE): " + curr);
					diffIndexes = new int[3];
					diffIndexes[0] = i - 1;
					diffIndexes[1] = i;
					visibleEventHasBeenHidden = true;
				} 
				else if (curr.op == Op.RHIDE || curr.op == Op.RHIDENSHIFT) {
					Integer hiddenEvent = (Integer)curr.target;
//					System.out.println("Found earliest discrepancy (RHIDE): " + curr);
					diffIndexes = new int[3];
					diffIndexes[0] = i - 1;

					visibleEventHasBeenHidden = true;// !pes2.getInvisibleEvents().contains(hiddenEvent);
					if (visibleEventHasBeenHidden)
						diffIndexes[1] = i;
				}
			} 
			else {
				if (curr.op == Op.MATCH || curr.op == Op.MATCHNSHIFT) {
					if (visibleEventHasBeenHidden) {
//						System.out.println("==> Context: " + opSeq.subList(diffIndexes[0], i+1));
						diffIndexes[2] = i;
						differences.add(diffIndexes);
					} 
					else {
//						System.out.println("==> Context discarded: No visible event has been hidden");
					}
					diffIndexes = null;
				} 
				else {
					if (!visibleEventHasBeenHidden) {
						if (curr.op == Op.LHIDE) {
							diffIndexes[1] = i;
							visibleEventHasBeenHidden = true;
						} 
						else {
							visibleEventHasBeenHidden = true; //!pes2.getInvisibleEvents().contains((Integer)curr.target);
							if (visibleEventHasBeenHidden)
								diffIndexes[1] = i;
						}
					}
				}
			}
		}
		return differences;
	}
	
	private Object translate(BitSet multiset) {
		Set<String> set = new HashSet<String>();
		
		for (int ev = multiset.nextSetBit(0); ev >= 0; ev = multiset.nextSetBit(ev + 1)) {
			set.add(pes2.getLabel(ev));
		}
		return set;
	}
	
	private Object translate(BitSet multiset, int pes) {
		List<String> set = new ArrayList<String>();
		
		for (int ev = multiset.nextSetBit(0); ev >= 0; ev = multiset.nextSetBit(ev + 1)) {
			if (pes == 1) {
				set.add(pes1.getLabel(ev));
			}
			else {
				set.add(pes2.getLabel(ev));
			}
		}
		return set;
	}
	
	private Object translate(BitSet multiset, List<Operation> opSeq) {
		List<String> set = new ArrayList<String>();
		
		for (int ev = multiset.nextSetBit(0); ev >= 0; ev = multiset.nextSetBit(ev + 1)) {
			set.add(getCorrectContext(opSeq.get(ev).getLabel()));
		}
		return set;
	}
	
}
