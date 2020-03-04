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

package com.raffaeleconforti.alphadollar.alphamminer;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.framework.log.*;
import org.processmining.framework.log.rfb.AuditTrailEntryImpl;
import org.processmining.framework.log.rfb.AuditTrailEntryListImpl;
import org.processmining.framework.models.ModelGraphVertex;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Place;
import org.processmining.framework.models.petrinet.Transition;

import java.io.IOException;
import java.util.*;

public class ModifiedAlphaPPProcessMiner {
    ArrayList alT_log = new ArrayList();
    ArrayList alL1L = new ArrayList();
    ArrayList alT_prime = new ArrayList();
    ArrayList alT_I = new ArrayList();
    ArrayList alT_O = new ArrayList();
    RelationMatrix rmRelation = new RelationMatrix();
    //containers for length-one-loop places
    L1LPlaces lpL_W;
    //for theorem one
    ArrayList alOrJoinDiffOut = new ArrayList();
    //for theorem two
    ArrayList alAndSplit = new ArrayList();
    ArrayList alAndJoin = new ArrayList();
    ArrayList alOrSplit = new ArrayList();
    ArrayList alOrJoin = new ArrayList();
    //the set of implicit dependencies one
    ArrayList alID_W1 = new ArrayList();
    IDWManager alID_W2 = new IDWManager();
    PlacesWithIDTwo alPlacesIDTwo = new PlacesWithIDTwo();
    IDWManager alID_W3 = new IDWManager();
    //which tasks need to compute input places again
    ArrayList alIDW1Task = new ArrayList();
    ArrayList alAlpha = new ArrayList();
    
    ArrayList<InvTask> invTasks = new ArrayList<InvTask>();
	private ArrayList<AXYB> invTaskAXYB;
	private UnifiedMap<String,ArrayList<AXYB>> invTaskWithAXYB;

    public PetriNet mine(LogReader log)
    {
        clearAll();
        PetriNet result = mineIt(log);
        return result;
    }

    /**
     * clearAll
     */
    private void clearAll() {
        alT_log.clear();
        alL1L.clear();
        alT_prime.clear();
        alT_I.clear();
        alT_O.clear();
        rmRelation.clear();
        alOrJoinDiffOut.clear();
        alAndSplit.clear();
        alAndJoin.clear();
        alOrSplit.clear();
        alOrJoin.clear();
        alID_W1.clear();
        alID_W2.clear();
        alPlacesIDTwo.clear();
        alID_W3.clear();
        alIDW1Task.clear();
        alAlpha.clear();
    }

    private PetriNet mineIt(LogReader log) {
        //scan the workflow log and computer T_log, L1L and T_prime (steps 1 to 3)
        computeTlogL1LTprime(log);

        //compute L_W (steps 4 and 5)
        computeL_W();

        //eliminate all event who's task is in L1L (steps 6 and 7)
        eliminateL1L();

        //induce *t, t*, ||, |>, <| from current relation matrix
        rmRelation.induceAdvOrder();

        //induce >> from log
        induceReachable(log);

        //apply Theorem 1 (step 8)
        applyingTheoremOne();

        //mining WF-net using alpha algorithm (step 9)
        miningUsingAlpha();

        //patch places for these tasks in ID_W1
        patchIDW1();

        //apply theorem 2 (step 10)
        applyingTheoremTwo();

        //eliminate redundant dependency by rule 1 (step 11)
        eliminateRDByRule1();

        //derive places involving implicit dependency two (steps 12 and 13)
        derivePlacesForIDTwo();

        //apply theorem 3 (step 14)
        applyingTheoremThree();

        //eliminate redundant implicit dependency (step 15)
        eliminateRDByRule2();

        //derive places involving implicit dependency three (steps 16 and 17)
        derivePlacesForIDThree();

        //gather all the places of the mined WF-net (step 18)
        addShortLoopPlaces();

        //return the final result (steps 19-21)
        PetriNet mr = constructWFNet(log);

        return mr;
    }

    /**
     * addShortLoopPlaces
     */
    private void addShortLoopPlaces() {
        TripleSets[] arTs = lpL_W.getL1LPlaces();
        for (int i = 0; i < arTs.length; i++) {
            TripleSets ts = arTs[i];
            int idx = alAlpha.indexOf(ts);
            if (idx >= 0) {
                DoubleSets ds = (DoubleSets) alAlpha.get(idx);
                ds.addToA(ts.alC);
                ds.addToB(ts.alC);
            }else {
                DoubleSets ds = new DoubleSets();
                ds.addToA(ts.alA);
                ds.addToA(ts.alC);
                ds.addToB(ts.alB);
                ds.addToB(ts.alC);
                alAlpha.add(ds);
            }
        }
    }

    /**
     * constructWFNet
     */
    private PetriNet constructWFNet(LogReader log) {
        Hashtable htTrans = new Hashtable();
        //generate the final petri net from the mined transitions, places and arcs
        PetriNet petrinet = new PetriNet();
        //all transitions
        for (int i = 0; i < alT_log.size(); i++) {
            String strT_type = (String)alT_log.get(i);
            int idx = strT_type.lastIndexOf(0);
            String strT = strT_type.substring(0, idx);
            String strType = strT_type.substring(idx+1);
            Transition t = new Transition(new LogEvent(strT, strType), petrinet);
            t = petrinet.addTransition(t);
            htTrans.put(strT_type, t);
        }
        //source place
        Place psrc = petrinet.addPlace("psource");
        //source place to first transitions
        for (int i = 0; i < alT_I.size(); i++) {
            petrinet.addEdge(psrc, (Transition)htTrans.get(alT_I.get(i)));
        }
        //sink place
        Place psink = petrinet.addPlace("psink");
        //last transitions to sink place
        for (int i = 0; i < alT_O.size(); i++) {
            petrinet.addEdge((Transition)htTrans.get(alT_O.get(i)), psink);
        }
        //all mined places and arcs
        for (int i = 0; i < alAlpha.size(); i++) {
            DoubleSets ds = (DoubleSets) alAlpha.get(i);
            //mined place
            Place p = petrinet.addPlace("p" + (i + 1));
            //incoming arcs
            for (int j = 0; j < ds.alA.size(); j++) {
                petrinet.addEdge((Transition)htTrans.get(ds.alA.get(j)), p);
            }
            //outgoing arcs
            for (int j = 0; j < ds.alB.size(); j++) {
                petrinet.addEdge(p,(Transition)htTrans.get(ds.alB.get(j)));
            }
        }
        
        
        //TODO
        //1. remove the log event of the invisible task
        removeInvTaskLogEvent(petrinet);
        //2. get the relation between the invtask and axyb
        getInvTaskAXYB(petrinet);
        
//        //2.combine invisible task        
//        combineInvisibleTask(petrinet);
        
        //2.5 combine places        
//        //3.co-exist
        checkCoexist(petrinet, log);
        adjustNonfreeChoiceInvisibleTask(petrinet);
//        combinePlace(petrinet);
//        combineInvisibleTask(petrinet);
//        //combinePlace(petrinet);
//        combineInvisibleTaskSup(petrinet);
        //return the petri net to the user
        //3.5 one more time
        combinePlace(petrinet);
        petrinet.makeClusters();

        return petrinet;
    }

    /**
     * t is a invisible task.
     * the x and y is the tasks that may be skipped by the transition t,
     * @param t
     * @param availableX
     * @param availableY
     */
    private void getAvailableXY(Transition t, Set<String> availableX, Set<String> availableY, Set<String> availableA, Set<String> availableB) {
    	HashSet<Place> prePlaces = t.getPredecessors();
    	HashSet<Place> succPlaces = t.getSuccessors();
    	
    	for (Place prePlace	:	prePlaces) {
    		HashSet<Transition> xs = prePlace.getSuccessors();
    		HashSet<Transition> as = prePlace.getPredecessors();
    		for (Transition x : xs) {
    			if (x.equals(t))
    				continue;
    			availableX.add(x.getIdentifier());
    		}
    		for (Transition a : as) {
    			if (a.equals(t))
    				continue;
    			availableA.add(a.getIdentifier());
    		}
    	}
    	
    	for (Place succPlace : succPlaces) {
    		HashSet<Transition> ys = succPlace.getPredecessors();
    		HashSet<Transition> bs = succPlace.getSuccessors();
    		for (Transition y : ys) {
    			if (y.equals(t))
    				continue;
    			availableY.add(y.getIdentifier());    		
    		}
    		for (Transition b : bs) {
    			if (b.equals(t))
    				continue;
    			availableB.add(b.getIdentifier());    		
    		}
    	}
    }
    
    private void getInvTaskAXYB(PetriNet petrinet) {
    	invTaskWithAXYB = new UnifiedMap<String, ArrayList<AXYB>>();
    	for (Transition transition : petrinet.getTransitions()) {
    		if (transition.getLogEvent() != null)
    			continue;
			Set<String> availableX = new UnifiedSet<String>();
			Set<String> availableY = new UnifiedSet<String>();
			Set<String> availableA = new UnifiedSet<String>();
			Set<String> availableB = new UnifiedSet<String>();
    		getAvailableXY(transition, availableX, availableY,availableA,availableB);
    		ArrayList<AXYB> _axybs = new ArrayList<AXYB>();
    		for (AXYB axyb : invTaskAXYB) {
    			switch (axyb.invTasktype) {
    				case AXYB.START:
    					if (availableA.size() == 0)	{
    						if (availableB.contains(axyb.b))
    							_axybs.add(axyb);			
    					}
    					break;
    				case AXYB.END:
    					if (availableB.size() == 0) {
    						if (availableA.contains(axyb.a))
    							_axybs.add(axyb);
    					}
        				break;
    				case AXYB.SKIP:
    	    			String xName = axyb.x;
    	    			String yName = axyb.y;
    	    			String aName = axyb.a;
    	    			String bName = axyb.b;
    	    			if (availableX.contains(xName) 
    	    					&& availableY.contains(yName)
    	    					&& availableA.contains(aName)
    	    					&& availableB.contains(bName))
    	    				_axybs.add(axyb);
        				break;
    				case AXYB.REDO:
    	    			xName = axyb.x;
    	    			yName = axyb.y;
    	    			aName = axyb.a;
    	    			bName = axyb.b;
    	    			if (availableX.contains(xName) 
    	    					&& availableY.contains(yName)
    	    					&& availableA.contains(aName)
    	    					&& availableB.contains(bName))
    	    				_axybs.add(axyb);
        				break;
    				case AXYB.SWITCH:
    	    			xName = axyb.x;
    	    			yName = axyb.y;
    	    			aName = axyb.a;
    	    			bName = axyb.b;
    	    			if (availableX.contains(xName) 
    	    					&& availableY.contains(yName)
    	    					&& availableA.contains(aName)
    	    					&& availableB.contains(bName))
    	    				_axybs.add(axyb);
        				break;    				
    			}    			    				    			    		
    		}
    		invTaskWithAXYB.put(transition.getIdentifier(), _axybs);
    	}
		
	}

    private boolean isCheckInterSequence(Transition t1, Transition t2) {
		Set<Place> preT1 = t1.getPredecessors();
		Set<Place> succT1 = t1.getSuccessors();
		Set<Place> preT2 = t2.getPredecessors();
		Set<Place> succT2 = t2.getSuccessors();
		Set<Transition> preT1ts = new UnifiedSet<Transition>();
		Set<Transition> succT1ts = new UnifiedSet<Transition>();
		Set<Transition> preT2ts = new UnifiedSet<Transition>();
		Set<Transition> succT2ts = new UnifiedSet<Transition>();
    	
    	for (Place place : preT1) {
    		HashSet<Transition> ts = place.getPredecessors();
    		preT1ts.addAll(ts);
    	}
    	
    	for (Place place : succT1) {
    		HashSet<Transition> ts = place.getSuccessors();
    		succT1ts.addAll(ts);
    	}
    	
    	for (Place place : preT2) {
    		HashSet<Transition> ts = place.getPredecessors();
    		preT2ts.addAll(ts);
    	}
    	
    	for (Place place	:	succT2) {
    		HashSet<Transition> ts = place.getSuccessors();
    		succT2ts.addAll(ts);
    	}
    	//donnot take care of the common part.
		Set<Transition> joinPre = AlphaMMinerDataUtil.join(preT1ts, preT2ts);
    	preT1ts = AlphaMMinerDataUtil.except(preT1ts, joinPre);
    	preT2ts = AlphaMMinerDataUtil.except(preT2ts, joinPre);

		Set<Transition> joinSucc = AlphaMMinerDataUtil.join(succT1ts, succT2ts);
    	succT1ts = AlphaMMinerDataUtil.except(succT1ts, joinSucc);
    	succT2ts = AlphaMMinerDataUtil.except(succT2ts, joinSucc);
    	
    	for (Transition	ta	:	preT1ts) {
    		if (ta.getLogEvent() == null)
    			continue;
    		for (Transition tb	:	succT2ts) {
    			if (tb.getLogEvent() == null)
    				continue;
    			String taskNameA = ta.getLogEvent().getModelElementName()+"\0"+ta.getLogEvent().getEventType();
    			String taskNameB = tb.getLogEvent().getModelElementName()+"\0"+tb.getLogEvent().getEventType();
    			if (!rmRelation.isBefore(taskNameA, taskNameB))
    				return false;   
    			
    		}
    	}
    	
    	for (Transition	ta	:	preT2ts) {
    		if (ta.getLogEvent() == null)
    			continue;
    		for (Transition tb	:	succT1ts) {
    			if (tb.getLogEvent() == null)
    				continue;
    			String taskNameA = ta.getLogEvent().getModelElementName()+"\0"+ta.getLogEvent().getEventType();
    			String taskNameB = tb.getLogEvent().getModelElementName()+"\0"+tb.getLogEvent().getEventType();
    			if (rmRelation.isNormPara(taskNameA, taskNameB))
    				return false;    				
    		}
    	}
    	return true;
    }
    
    private void checkCoexist(PetriNet petrinet,LogReader log) {
		// TODO Auto-generated method stub
    	ArrayList<CoExistAutomaton> automatons = new ArrayList<CoExistAutomaton>();
		for (int i=0; i<petrinet.getTransitions().size(); i++) {
			Transition t1 = petrinet.getTransitions().get(i);
			if (t1.getLogEvent() !=null)
				continue;
			for (int j=i+1; j<petrinet.getTransitions().size(); j++) {
				Transition t2 = petrinet.getTransitions().get(j);
				if(t2.getLogEvent() != null)
					continue;
				String t1Name = t1.getIdentifier() +"\0auto";
				String t2Name = t2.getIdentifier() +"\0auto";
				if (rmRelation.isNormPara(t1Name, t2Name)) {
					ArrayList<String> _ASet = new ArrayList<String>();
					ArrayList<String> _BSet = new ArrayList<String>();
					ArrayList<AXYB> t1axybs = invTaskWithAXYB.get(t1.getIdentifier());
					ArrayList<AXYB> t2axybs = invTaskWithAXYB.get(t2.getIdentifier());
					for (AXYB axyb	:	t1axybs) {
						if (axyb.invTasktype == AXYB.SKIP) {
							if (!_ASet.contains(axyb.x))
								_ASet.add(axyb.x);
							if (!_ASet.contains(axyb.y))
								_ASet.add(axyb.y);
						}
						else if (axyb.invTasktype == AXYB.REDO) {
							if (!_ASet.contains(axyb.a))
								_ASet.add(axyb.a);
							if (!_ASet.contains(axyb.b))
								_ASet.add(axyb.b);
						}							
					}
					for (AXYB axyb	:	t2axybs) {
						if (axyb.invTasktype == AXYB.SKIP) {
							if (!_BSet.contains(axyb.x))
								_BSet.add(axyb.x);
							if (!_BSet.contains(axyb.y))
								_BSet.add(axyb.y);
						}
						else if (axyb.invTasktype == AXYB.REDO) {
							if (!_BSet.contains(axyb.a))
								_BSet.add(axyb.a);
							if (!_BSet.contains(axyb.b))
								_BSet.add(axyb.b);
						}
					}

					if (!isCheckInterSequence(t1,t2) && AlphaMMinerDataUtil.join(_ASet,	_BSet).size() > 0)
						continue;
					CoExistAutomaton ceAutomaton = new CoExistAutomaton(_ASet, _BSet, t1,t2);
					automatons.add(ceAutomaton);
				}
			}
		}

		for (ProcessInstance processInstance	:	log.getInstances()) {
			AuditTrailEntryList ateList =  processInstance.getAuditTrailEntryList();
			for (int i=0; i<ateList.size(); i++) {
				try {
					AuditTrailEntry ate = ateList.get(i);
					for (CoExistAutomaton automaton	:	automatons) {
						automaton.add(ate.getName());
					}					
				} catch (IndexOutOfBoundsException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			for (CoExistAutomaton automaton	:	automatons) {
				automaton.add("newtrace");
			}	
		}

		Iterator it = automatons.iterator();
		UnifiedMap<Transition,Integer> disjointSet = new UnifiedMap<Transition, Integer>();
		int setCount = 0;
		while (it.hasNext()) {
			CoExistAutomaton automaton = (CoExistAutomaton) it.next();
			if (!automaton.isCoexist)
				continue;
			Transition t1 = automaton.t1;
			Transition t2 = automaton.t2;
			
			Integer i1,i2;
			i1 = disjointSet.get(t1);
			i2 = disjointSet.get(t2);
			if (i1 == null && i2 == null) {
				setCount++;
				disjointSet.put(t1, setCount);
				disjointSet.put(t2, setCount);
			}else if (i1 != null && i2 == null) {
				disjointSet.put(t2,setCount);
			}else if (i1 == null && i2 != null) {
				disjointSet.put(t1,setCount);
			}else if (i1 != null && i2 != null) {
				if (i1.equals(i2))
					continue;
				for (Transition transition	:	disjointSet.keySet()) {
					if (disjointSet.get(transition).equals(i2))
						disjointSet.put(transition, i1);
				}
			}
		}
		ArrayList<ArrayList<Transition>> transitionSets = new ArrayList<ArrayList<Transition>>();
		UnifiedMap<Integer, Integer> index = new UnifiedMap<Integer,Integer>();
		int count =0;
		for (Transition t	:	disjointSet.keySet()) {
			Integer i = disjointSet.get(t);
			if (index.containsKey(i)) {
				int o = index.get(i);
				transitionSets.get(o).add(t);
			}else {
				index.put(i, count);
				ArrayList<Transition> transitions = new ArrayList<Transition>();
				transitions.add(t);
				transitionSets.add(transitions);
				count++;
			}
		}
		
		//for each set, combine it.
		for (int i=0; i<transitionSets.size(); i++) {
			Set<ModelGraphVertex> prePlaces = new UnifiedSet<ModelGraphVertex>();
			Set<ModelGraphVertex> postPlaces = new UnifiedSet<ModelGraphVertex>();
			ArrayList<Transition> transitionSet = transitionSets.get(i);
			Transition first = transitionSet.get(0);
			for (int j=1; j<transitionSet.size(); j++) {
				Transition t = transitionSet.get(j);
				Set<ModelGraphVertex> pre = t.getPredecessors();
				Set<ModelGraphVertex> post = t.getSuccessors();
				prePlaces.addAll(pre);
				postPlaces.addAll(post);
				petrinet.delTransition(t);
			}
			for (ModelGraphVertex pre	:	prePlaces) {
				if (petrinet.getEdgesBetween(pre, first).size() == 0)
					petrinet.addEdge((Place) pre, first);
			}
			for (ModelGraphVertex post	:	postPlaces) {
				if (petrinet.getEdgesBetween(first, post).size() == 0)
					petrinet.addEdge(first, (Place) post);
			}
		}
	}
    
    
    private void removeInvTaskLogEvent(PetriNet petrinet) {
		ArrayList<Transition> invisibleTasks = new ArrayList<Transition>();
		for (Transition transition	:	petrinet.getTransitions()) {
			boolean isInv = false;
			for (InvTask invTask	:	invTasks)
				if (invTask.taskName.equals(transition.getIdentifier())) {
					isInv = true;
					break;
				}
			if (!isInv)
				continue;
			//1.logevent
			transition.setLogEvent(null);	
			invisibleTasks.add(transition);
		}
	}

	private void combinePlace(PetriNet petriNet) {
    	UnifiedMap<Set<Transition>, UnifiedMap<Set<Transition>,ArrayList<Place>>> places =
    			new UnifiedMap<Set<Transition>, UnifiedMap<Set<Transition>,ArrayList<Place>>>();
    	for(Place place	:	petriNet.getPlaces()) {
			Set<Transition> preTransitions = place.getPredecessors();
			Set<Transition> postTransitions = place.getSuccessors();
    		UnifiedMap<Set<Transition>, ArrayList<Place>> singleLine = places.get(preTransitions);
    		if (singleLine == null) {
    			singleLine = new UnifiedMap<Set<Transition>, ArrayList<Place>>();
    			places.put(preTransitions, singleLine);
    		}
    		ArrayList<Place> line = singleLine.get(postTransitions);
    		if (line == null) {
    			line = new ArrayList<Place>();
    			singleLine.put(postTransitions, line);    			
    		}
    		line.add(place);
    	}
    	for (UnifiedMap<Set<Transition>, ArrayList<Place>> singleLine : places.values()) {
    		for (ArrayList<Place> line	:	singleLine.values()) {
    			if (line.size() > 1) {
    				for(int i=1; i < line.size(); i++)
    				    petriNet.delPlace(line.get(i));
    			}
    		}
    	}
    }
	
	private void adjustNonfreeChoiceInvisibleTask(PetriNet petriNet) {
		int count = 0;
		ArrayList<Transition> invTransitions = new ArrayList<Transition>();
		for (Transition transition	:	petriNet.getTransitions()) {
			if (transition.getLogEvent() != null)
				continue;
			invTransitions.add(transition);
		}
		for (Transition transition	:	invTransitions) {
			ArrayList<AXYB> tAXYBs = invTaskWithAXYB.get(transition.getIdentifier());
			ArrayList<AXYB> needToAdjustAXYBs  = getNeedtoAjustAXYBs(tAXYBs,alID_W3);
			int num = needToAdjustAXYBs.size();
			if (num == 0) {
				continue;
			}else if (num == 1) {
				AXYB axyb = needToAdjustAXYBs.get(0);
				adjustSingleTransition(axyb,transition,petriNet);
			}
			else if (num > 1) {
				for (int i=0; i<num; i++) {
					AXYB axyb = needToAdjustAXYBs.get(i);
					Place prePlace= null, succPlace = null;
					if (axyb.invTasktype == AXYB.SKIP) {
						Set<Place> prePlaces = transition.getPredecessors();
						Set<Place> succPlaces = transition.getSuccessors();
						for (Place p	:	prePlaces) {
							Set<Transition> prePlaceSuccT = p.getSuccessors();
							for (Transition t	:	prePlaceSuccT) {
								if (t.getIdentifier().equals(axyb.x))
									prePlace = p;				
							}
						}
						for (Place p	:	succPlaces) {
							Set<Transition> succPlacePreT = p.getPredecessors();
							for (Transition t	:	succPlacePreT) {
								if (t.getIdentifier().equals(axyb.y))
									succPlace = p;
							}
						}				
					}else if (axyb.invTasktype == AXYB.REDO) {
						Set<Place> prePlaces = transition.getPredecessors();
						Set<Place> succPlaces = transition.getSuccessors();
						for (Place p	:	prePlaces) {
							Set<Transition> prePlacePreT = p.getPredecessors();
							for (Transition t	:	prePlacePreT) {
								if (t.getIdentifier().equals(axyb.a))
									prePlace = p;				
							}
						}
						for (Place p	:	succPlaces) {
							Set<Transition> succPlacesuccT = p.getSuccessors();
							for (Transition t	:	succPlacesuccT) {
								if (t.getIdentifier().equals(axyb.b))
									succPlace = p;
							}
						}					
					}else if (axyb.invTasktype == AXYB.START) {
						Set<Place> succPlaces = transition.getSuccessors();
						for (Place p	:	succPlaces) {
							Set<Transition> succPlacepreT = p.getPredecessors();
							for (Transition t	:	succPlacepreT) {
								if (t.getIdentifier().equals(axyb.y))
									succPlace = p;
							}
						}
						prePlace = (Place) transition.getPredecessors().iterator().next();
					}else if (axyb.invTasktype == AXYB.END) {

						Set<Place> prePlaces = transition.getPredecessors();
						for (Place p	:	prePlaces) {
							Set<Transition> prePlaceSuccT = p.getSuccessors();
							for (Transition t	:	prePlaceSuccT) {
								if (t.getIdentifier().equals(axyb.x))
									prePlace = p;				
							}
						}
						succPlace = (Place) transition.getSuccessors().iterator().next();

					}
					if (prePlace == null || succPlace == null)
						continue;
					
					Transition t;
					if (i<num -1) {
						t = new Transition(petriNet);
						t.setIdentifier("ADDTOSKIP" + (count++));
						t.setLogEvent(null);
						petriNet.addTransition(t);
						petriNet.addEdge(prePlace, t);
						petriNet.addEdge(t, succPlace);
					}else {
                        t = transition;
                    }
					adjustSingleTransition(axyb, t, petriNet);
				}
			}
		}
	}

	private void adjustSingleTransition(AXYB axyb, Transition transition, PetriNet petriNet) {				
		if (axyb.invTasktype == AXYB.SKIP)
		{
			Transition xTransition = null;
			Transition yTransition = null;
			Set<Place> prePlaces = transition.getPredecessors();
			Set<Place> succPlaces = transition.getSuccessors();
			for (Place p	:	prePlaces)
			{
				Set<Transition> prePlaceSuccT = p.getSuccessors();
				for (Transition t	:	prePlaceSuccT)
				{
					if (t.getIdentifier().equals(axyb.x))
						xTransition = t;				
				}
			}
			for (Place p	:	succPlaces)
			{
				Set<Transition> succPlacePreT = p.getPredecessors();
				for (Transition t	:	succPlacePreT)
				{
					if (t.getIdentifier().equals(axyb.y))
						yTransition = t;
				}
			}
			if (xTransition == null  || yTransition == null)
				return;
			for (Place p	:	(Set<Place>) xTransition.getPredecessors())
			{
				if (!transition.getPredecessors().contains(p))				
					petriNet.addEdge(p, transition);
			}
			for (Place p	:	(Set<Place>) yTransition.getSuccessors())
			{
				if (!transition.getSuccessors().contains(p))
					petriNet.addEdge(transition, p);
			}
		}
		else if (axyb.invTasktype == AXYB.REDO)
		{
			Transition aTransition = null;
			Transition bTransition = null;
			Set<Place> prePlaces = transition.getPredecessors();
			Set<Place> succPlaces = transition.getSuccessors();
			for (Place p	:	prePlaces)
			{
				Set<Transition> prePlaceSuccT = p.getSuccessors();
				for (Transition t	:	prePlaceSuccT)
				{
					if (t.getIdentifier().equals(axyb.b))
						bTransition = t;				
				}
			}
			for (Place p	:	succPlaces)
			{
				Set<Transition> succPlacePreT = p.getPredecessors();
				for (Transition t	:	succPlacePreT)
				{
					if (t.getIdentifier().equals(axyb.a))
						aTransition = t;
				}
			}	
			if (aTransition == null || bTransition == null)
				return;
			for (Place p	:	(Set<Place>) aTransition.getSuccessors())
			{
				if (!transition.getPredecessors().contains(p))
					petriNet.addEdge(p, transition);
			}
			for (Place p	:	(Set<Place>) bTransition.getPredecessors())
			{
				if (!transition.getSuccessors().contains(p))
					petriNet.addEdge(transition, p);
			}			
		}	
		else if (axyb.invTasktype == AXYB.START)
		{			
			Transition yTransition = null;
			Set<Place> prePlaces = transition.getPredecessors();
			Set<Place> succPlaces = transition.getSuccessors();
			for (Place p	:	succPlaces)
			{
				Set<Transition> succPlacePreT = p.getPredecessors();
				for (Transition t	:	succPlacePreT)
				{
					if (t.getIdentifier().equals(axyb.y))
						yTransition = t;
				}
			}
			if (yTransition == null)
				return;			
			for (Place p	:	(Set<Place>) yTransition.getSuccessors())
			{
				if (!transition.getSuccessors().contains(p))
					petriNet.addEdge(transition, p);
			}
		}
		else if (axyb.invTasktype == AXYB.END)
		{
			Transition xTransition = null;
			Set<Place> prePlaces = transition.getPredecessors();
			Set<Place> succPlaces = transition.getSuccessors();
			for (Place p	:	prePlaces)
			{
				Set<Transition> prePlaceSuccT = p.getSuccessors();
				for (Transition t	:	prePlaceSuccT)
				{
					if (t.getIdentifier().equals(axyb.x))
						xTransition = t;				
				}
			}
			if (xTransition == null)
				return;
			for (Place p	:	(Set<Place>) xTransition.getPredecessors())
			{
				if (!transition.getPredecessors().contains(p))				
					petriNet.addEdge(p, transition);
			}
		}
	}
	
	
	private ArrayList<AXYB> getNeedtoAjustAXYBs(ArrayList<AXYB> tAXYBs,
                                                IDWManager alID_W32) {
		ArrayList<AXYB> result = new ArrayList<AXYB>();
		for (AXYB axyb	:	tAXYBs)
		{
			if (axyb.invTasktype == AXYB.SKIP || axyb.invTasktype == AXYB.END || axyb.invTasktype == AXYB.START)
			{
				String xName = axyb.x;
				String yName = axyb.y;
				boolean tag1 = false;
				boolean tag2 = false;
				for (String s1 : (Collection<String>) alID_W32.htSucc.keySet())
				{
					if (s1.startsWith(axyb.x))
						tag1 = true;
				}
				for (String s1 : (Collection<String>) alID_W32.htPred.keySet())
				{
					if (s1.startsWith(axyb.y))
						tag2 = true;
				}	 						
				if (tag1 || tag2)
					result.add(axyb);				
			}
			else if (axyb.invTasktype == AXYB.REDO)
			{
				String aName = axyb.a;
				String bName = axyb.b;
				boolean tag1 = false;
				boolean tag2 = false;
				for (String s1 : (Collection<String>) alID_W32.htSucc.keySet())
				{
					if (s1.startsWith(axyb.b))
						tag1 = true;
				}
				for (String s1 : (Collection<String>) alID_W32.htPred.keySet())
				{
					if (s1.startsWith(axyb.a))
						tag2 = true;
				}	 					
				if (tag1 || tag2)
					result.add(axyb);
			}				
		}
		return result;
	}

	/**
     * derivePlacesForIDThree
     */
    private void derivePlacesForIDThree()
    {
        //Convert |->W3 to ->
        convertIDW3();

        DoubleSets[] arNewPlaces = alID_W3.derive(rmRelation);
        //add new places
        for (int i = 0; i < arNewPlaces.length; i++)
        {
            alAlpha.add(arNewPlaces[i]);
        }
    }

    /**
     * applyTheoremThree
     */
    private void applyingTheoremThree()
    {
        //eliminate the effects of theorem one
        unConvertIDW1();

        //add the effects of short loops
        addEffectsOfShortLoop();

        ArrayList alPassed = new ArrayList();
        TaskDivider tdA = new TaskDivider(rmRelation);
        TaskDivider tdB = new TaskDivider(rmRelation);
        //for any a|>b
        for (int i = 0; i < alOrJoin.size(); i++)
        {
            String a = (String) alOrJoin.get(i);
            String[] arCommSucc = rmRelation.getCommSucc(a);
            for (int j = 0; j < arCommSucc.length; j++)
            {
                //reset to be used again
                tdA.clear();
                tdB.clear();

                String b = arCommSucc[j];
                //test if b has been accessed
                if (alPassed.contains(b))
                {
                    continue;
                }
                //enumerate all tasks ai<|bj, divide all these tasks in A and B by ||
                for (int k = 0; k < alOrSplit.size(); k++)
                {
                    String t = (String) alOrSplit.get(k);
                    /*
                    if(t.equals(a) || t.equals(b))
                        continue;
                    */
                    boolean canAReach = rmRelation.isReachable(a, t);
                    boolean canBReach = rmRelation.isReachable(b, t);
                    if (canAReach && !canBReach)
                    {
                        tdA.add(t);
                    }
                    else if (!canAReach && canBReach)
                    {
                        tdB.add(t);
                    }
                }
                if (tdA.isEmpty() || tdB.isEmpty())
                {
                    continue;
                }
                //try to combine A and B
                ArrayList[] alGroupA = tdA.getGroups();
                ArrayList[] alGroupB = tdB.getGroups();
                for (int k = 0; k < alGroupA.length; k++)
                {
                    //A and its input tasks
                    ArrayList alA = alGroupA[k];
                    ArrayList alInputOfA = rmRelation.calculateInput(alA);
                    for (int m = 0; m < alGroupB.length; m++)
                    {
                        //B and its input tasks
                        ArrayList alB = alGroupB[m];
                        ArrayList alInputOfB = rmRelation.calculateInput(alB);
                        //find fitable A and B
                        if (rmRelation.isFitableAB(alA, alB))
                        {
                            //A' and its input tasks
                            ArrayList alAp = rmRelation.calculatePrime(alA, alB);
                            ArrayList alInputOfAp = rmRelation.calculateInput(
                                alAp);
                            alInputOfAp.addAll(alInputOfA);
                            //B' and its input tasks
                            ArrayList alBp = rmRelation.calculatePrime(alB, alA);
                            ArrayList alInputOfBp = rmRelation.calculateInput(
                                alBp);
                            alInputOfBp.addAll(alInputOfB);
                            //applying theorem 3
                            for (int n = 0; n < alA.size(); n++)
                            {
                                String ai = (String) alA.get(n);
                                ArrayList alInputOfAi = rmRelation.
                                    calculateInput(ai);
                                if (alInputOfBp.containsAll(alInputOfAi))
                                {
                                    alID_W3.add(a, ai);
                                }
                            }
                            for (int n = 0; n < alB.size(); n++)
                            {
                                String bj = (String) alB.get(n);
                                ArrayList alInputOfBj = rmRelation.
                                    calculateInput(bj);
                                if (alInputOfAp.containsAll(alInputOfBj))
                                {
                                    alID_W3.add(b, bj);
                                }
                            }
                        }
                    }
                }
            }

            //record passed tasks
            alPassed.add(a);
        }
    }

    /**
     * addEffectsOfShortLoop
     */
    private void addEffectsOfShortLoop()
    {
        TripleSets[] arTs = lpL_W.getL1LPlaces();
        for (int i = 0; i < arTs.length; i++)
        {
            TripleSets ts = arTs[i];
            for (int j = 0; j < ts.alC.size(); j++)
            {
                String pred = (String) ts.alC.get(j);
                for (int k = 0; k < ts.alB.size(); k++)
                {
                    String succ = (String) ts.alB.get(k);
                    rmRelation.addBefore(succ, pred);
                }
            }
        }
    }

    /**
     * derivePlacesForIDTwo
     */
    private void derivePlacesForIDTwo()
    {
        //remove old places
        DoubleSets[] arOldPlaces = alPlacesIDTwo.get();
        for (int i = 0; i < arOldPlaces.length; i++)
        {
            alAlpha.remove(arOldPlaces[i]);
        }
        //Convert |->W2 to ->
        convertIDW2();
        //add new places
        DoubleSets[] arNewPlaces = alPlacesIDTwo.derive(rmRelation);
        for (int i = 0; i < arNewPlaces.length; i++)
        {
            alAlpha.add(arNewPlaces[i]);
        }

        //add new <| and |> relations
        alPlacesIDTwo.addNewComm(rmRelation, alOrSplit, alOrJoin);
    }

    /**
     * eliminateRDByRule1
     */
    private void eliminateRDByRule1()
    {
        alID_W2.eliminateByRuleOne(rmRelation, alPlacesIDTwo);

    }

    private void eliminateRDByRule2()
    {
        alID_W3.eliminateByRuleTwo(rmRelation);

    }

    /**
     * applyingTheoremTwo
     */
    private void applyingTheoremTwo()
    {
        //find all output places of tasks in alAndSplit
        if (alAndSplit.size() > 0)
        {
            //enumerate all the places of the mined WF-net
            for (int i = 0; i < alAlpha.size(); i++)
            {
                DoubleSets ds = (DoubleSets) alAlpha.get(i);
                //enumerate all the and split tasks
                for (int j = 0; j < alAndSplit.size(); j++)
                {
                    String t = (String) alAndSplit.get(j);
                    //find an output place of t
                    if (ds.containedInA(t))
                    {
                        //test if Theorem 2 can hold
                        String[] arY = ds.getB();
                        //enumerate all the task pairs that have <| relations
                        for (int k = 0; k < alOrSplit.size(); k++)
                        {
                            String a = (String) alOrSplit.get(k);
                            String[] arB = rmRelation.getCommPred(a);
                            for (int l = 0; l < arB.length; l++)
                            {
                                String b = arB[l];
                                //test if all tasks in arY and a,b can satisfy Theorem 2
                                boolean isExistA = false;
                                boolean isExistB = false;
                                for (int m = 0; m < arY.length; m++)
                                {
                                    String y = arY[m];
                                    //y || a or y->a or y>>a?
                                    if (!isExistA &&
                                        rmRelation.isParaOrFollow(y, a))
                                    {
                                        isExistA = true;
                                    }
                                    //y || b or y->b or y>>b?
                                    if (!isExistB &&
                                        rmRelation.isParaOrFollow(y, b))
                                    {
                                        isExistB = true;
                                    }
                                    if (isExistA && isExistB)
                                    {
                                        break;
                                    }
                                }
                                if (isExistA && !isExistB ||
                                    !isExistA && isExistB)
                                {
                                    String succ = "";
                                    if (isExistA && !isExistB)
                                    {
                                        succ = b;
                                    }
                                    else if (!isExistA && isExistB)
                                    {
                                        succ = a;
                                    }
                                    //test if t>>succ
                                    if (rmRelation.isReachable(t, succ))
                                    {
                                        alID_W2.add(t, succ);
                                        alPlacesIDTwo.add(ds, t, succ);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        //find all input places of tasks in alAndJoin
        if (alAndJoin.size() > 0)
        {
            //enumerate all the places of the mined WF-net
            for (int i = 0; i < alAlpha.size(); i++)
            {
                DoubleSets ds = (DoubleSets) alAlpha.get(i);
                //enumerate all the and join tasks
                for (int j = 0; j < alAndJoin.size(); j++)
                {
                    String t = (String) alAndJoin.get(j);
                    //find an input place of t
                    if (ds.containedInB(t))
                    {
                        //test if Theorem 2 can hold
                        String[] arY = ds.getA();
                        //enumerate all the task pairs that have |> relations
                        for (int k = 0; k < alOrJoin.size(); k++)
                        {
                            String a = (String) alOrJoin.get(k);
                            String[] arB = rmRelation.getCommSucc(a);
                            for (int l = 0; l < arB.length; l++)
                            {
                                String b = arB[l];
                                //test if all tasks in arY and a,b can satisfy Theorem 2
                                boolean isExistA = false;
                                boolean isExistB = false;
                                for (int m = 0; m < arY.length; m++)
                                {
                                    String y = arY[m];
                                    //y || a or y->a or y>>a?
                                    if (!isExistA &&
                                        rmRelation.isParaOrFollow(a, y))
                                    {
                                        isExistA = true;
                                    }
                                    //y || b or y->b or y>>b?
                                    if (!isExistB &&
                                        rmRelation.isParaOrFollow(b, y))
                                    {
                                        isExistB = true;
                                    }
                                    if (isExistA && isExistB)
                                    {
                                        break;
                                    }
                                }
                                if (isExistA && !isExistB ||
                                    !isExistA && isExistB)
                                {
                                    String pred = "";
                                    if (isExistA && !isExistB)
                                    {
                                        pred = b;
                                    }
                                    else if (!isExistA && isExistB)
                                    {
                                        pred = a;
                                    }
                                    //test if t>>succ
                                    if (rmRelation.isReachable(pred, t))
                                    {
                                        alID_W2.add(pred, t);
                                        alPlacesIDTwo.add(ds, pred, t);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private void patchIDW1()
    {
        //|->W1 to ->
        convertIDW1();

        //generate input places for tasks in IDW1
        for (int i = 0; i < alIDW1Task.size(); i++)
        {
            String task = (String) alIDW1Task.get(i);

            DoubleSets[] arPlaces = genInputPlaces(task);

            for (int j = 0; j < arPlaces.length; j++)
            {
                if (!alAlpha.contains(arPlaces[j]))
                {
                    alAlpha.add(arPlaces[j]);
                }
            }
        }

    }

    /**
     * miningUsingAlpha
     */
    private void miningUsingAlpha()
    {
        //mining WF-net using alpha algorithm
        ArrayList alTSucc = new ArrayList(alT_prime);
        alTSucc.removeAll(alT_I);
        //need to be patched
        alTSucc.removeAll(alIDW1Task);
        //all the middle places except i and o
        for (int i = 0; i < alTSucc.size(); i++)
        {
            String task = (String) alTSucc.get(i);
            DoubleSets[] arPlaces = genInputPlaces(task);

            for (int j = 0; j < arPlaces.length; j++)
            {
                //eliminate these place that are input places of any task in IDW1
                boolean isIncluded = false;
                for (int k = 0; k < alIDW1Task.size(); k++)
                {
                    if (arPlaces[j].containedInB( (String) alIDW1Task.get(k)))
                    {
                        isIncluded = true;
                        break;
                    }
                }
                if (isIncluded)
                {
                    continue;
                }
                //add places
                if (!alAlpha.contains(arPlaces[j]))
                {
                    alAlpha.add(arPlaces[j]);
                }
            }
        }

    }

    private void convertIDW1()
    {
        //change a|->W1b to a->b
        for (int i = 0; i < alID_W1.size(); i++)
        {
            ImplicitDependency id = (ImplicitDependency) alID_W1.get(i);
            rmRelation.addRelation(id.getPred(), id.getSucc(),
                                   Relation.SUCCESSIVE);
            rmRelation.addAfter(id.getPred(), id.getSucc());
            rmRelation.addBefore(id.getSucc(), id.getPred());
        }
    }

    private void unConvertIDW1()
    {
        //remove a->b because of a|->W1b
        for (int i = 0; i < alID_W1.size(); i++)
        {
            ImplicitDependency id = (ImplicitDependency) alID_W1.get(i);
            rmRelation.removeRelation(id.getPred(), id.getSucc(),
                                      Relation.SUCCESSIVE);
            rmRelation.removeAfter(id.getPred(), id.getSucc());
            rmRelation.removeBefore(id.getSucc(), id.getPred());
        }
    }

    private void convertIDW2()
    {
        //change a|->W2b to a->b
        alID_W2.convert(rmRelation);
    }

    private void convertIDW3()
    {
        //change a|->W3b to a->b
        alID_W3.convert(rmRelation);
    }

    /**
     * applyingTheoremOne
     */
    private void applyingTheoremOne()
    {
        for (int i = 0; i < alOrJoinDiffOut.size(); i++)
        {
            String t = (String) alOrJoinDiffOut.get(i);
            DoubleSets[] arPlaces = genInputPlaces(t);
            //applying theorem one
            for (int j = 0; j < arPlaces.length; j++)
            {
                //Place (A1, B1)
                DoubleSets dsA1B1 = arPlaces[j];
                for (int k = j + 1; k < arPlaces.length; k++)
                {
                    //Place (A2, B2)
                    DoubleSets dsA2B2 = arPlaces[k];

                    genIDW1(dsA1B1, dsA2B2, t);
                    genIDW1(dsA2B2, dsA1B1, t);
                }
            }
        }

    }

    private DoubleSets[] genInputPlaces(String t)
    {
        PlaceForestBuilder pfb = new PlaceForestBuilder(rmRelation);
        //*t
         String[] arPred = rmRelation.getBefore(t);
        //for any b in ((*t)*-{t}), b#t
        ArrayList alSucc = new ArrayList();
        for (int j = 0; j < arPred.length; j++)
        {
            String a = arPred[j];
            String[] arSucc = rmRelation.getSucc(a);
            for (int k = 0; k < arSucc.length; k++)
            {
                String b = arSucc[k];
                if (!b.equals(t) && rmRelation.isDependent(b, t) &&
                    !alSucc.contains(b))
                {
                    alSucc.add(b);
                }
            }
        }
        //add t to Succ
        alSucc.add(0, t);
        //construct places (A, B) such that A<=*t, t in B, a in A and b in B=>a->b, a1,a2 in A=>a1#a2, b1,b2 in B=>b1#b2
        for (int j = 0; j < arPred.length; j++)
        {
            String pred = arPred[j];
            for (int k = 0; k < alSucc.size(); k++)
            {
                String succ = (String) alSucc.get(k);
                if (rmRelation.isNormSucc(pred, succ))
                {
                    pfb.add(pred, succ);
                }
            }
        }

        //get all the input places of t
        DoubleSets[] arPlaces = pfb.getPlaces();
        return arPlaces;
    }

    private void genIDW1(DoubleSets dsA1B1, DoubleSets dsA2B2, String depTask)
    {
        //for any a in A1
        String[] arA1 = dsA1B1.getA();
        String[] arA2 = dsA2B2.getA();
        for (int l = 0; l < arA1.length; l++)
        {
            String a = arA1[l];
            //a is not in A2
            if (!dsA2B2.containedInA(a))
            {
                //exist a' such that a'||a or (a'->a or a'>>a)?
                boolean isExist = false;
                for (int m = 0; m < arA2.length; m++)
                {
                    if (rmRelation.isNormPara(arA2[m], a))
                    //if (rmRelation.isNormPara(arA2[m], a) || rmRelation.isFollowedBy(arA2[m], a))
                    {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist)
                {
                    //implicit dependency |->W1
                    ArrayList alB2 = new ArrayList(dsA2B2.alB);
                    String[] arSuccA = rmRelation.getSucc(a);
                    for (int m = 0; m < arSuccA.length; m++)
                    {
                        alB2.remove(arSuccA[m]);
                    }
                    for (int m = 0; m < alB2.size(); m++)
                    {
                        String b_prime = (String) alB2.get(m);
                        if (rmRelation.isNormPara(b_prime, a))
                        	continue;
                        ImplicitDependency ido = new ImplicitDependency(a, b_prime);
                        //surely distinguish
                        if (!alID_W1.contains(ido))
                        {
                            alID_W1.add(ido);
                        }
                        if (!alIDW1Task.contains(depTask))
                        {
                            alIDW1Task.add(depTask);
                        }
                    }
                }
            }
        }
    }

    /**
     * induceReachable
     */
    private void induceReachable(LogReader log)
    {
        /*
                 Which tasks need to identify reachable relations?
                 There are five case listed below now.
         */

        //enumerate all tasks in T'
        for (int i = 0; i < alT_prime.size(); i++)
        {
            String t = (String) alT_prime.get(i);
            int type = rmRelation.getType(t);
            //And Split
            if ( (type & Relation.ANDSPLIT) > 0 && !alAndSplit.contains(t))
            {
                alAndSplit.add(t);
            }
            //And Join
            if ( (type & Relation.ANDJOIN) > 0 && !alAndJoin.contains(t))
            {
                alAndJoin.add(t);
            }
            //Or Split
            if ( (type & Relation.ORSPLIT) > 0 && !alOrSplit.contains(t))
            {
                alOrSplit.add(t);
            }
            //Or Join
            if ( (type & Relation.ORJOIN) > 0 && !alOrJoin.contains(t))
            {
                alOrJoin.add(t);
            }
            //Or Join with different output
            if ( (type & Relation.ORJOINDIFFOUT) > 0 && !alOrJoinDiffOut.contains(t))
            {
                alOrJoinDiffOut.add(t);
            }
        }

        //construct bi-directional queue
        ReachableManager rm = new ReachableManager(rmRelation);

        //case 1, and split <-> or split
        for (int i = 0; i < alAndSplit.size(); i++)
        {
            String pred = (String) alAndSplit.get(i);
            for (int j = 0; j < alOrSplit.size(); j++)
            {
                String succ = (String) alOrSplit.get(j);
                if (/*!pred.equals(succ) && */!rmRelation.isBefore(pred, succ))
                {
                    //pred itself
                    rm.add(pred, succ);
                    //pred's successor
                    String[] arSucc = rmRelation.getSucc(pred);
                    for (int k = 0; k < arSucc.length; k++)
                    {
                        if (!rmRelation.isBefore(arSucc[k], succ))
                        {
                            rm.add(arSucc[k], succ);
                        }
                    }
                }
            }
        }

        //case 2, 3, 4 and 6
        for (int i = 0; i < alOrJoin.size(); i++)
        {
            String pred = (String) alOrJoin.get(i);
            //case 2, or join <-> and join
            for (int j = 0; j < alAndJoin.size(); j++)
            {
                String succ = (String) alAndJoin.get(j);
                if (/*!pred.equals(succ) && */!rmRelation.isBefore(pred, succ))
                {
                    //succ itself
                    rm.add(pred, succ);
                    //succ's predecessor
                    String[] arPred = rmRelation.getBefore(succ);
                    for (int k = 0; k < arPred.length; k++)
                    {
                        if (!rmRelation.isBefore(pred, arPred[k]))
                        {
                            rm.add(pred, arPred[k]);
                        }
                    }
                }
            }
            //case 3, or join <-> or split
            for (int j = 0; j < alOrSplit.size(); j++)
            {
                String succ = (String) alOrSplit.get(j);
                if (/*!pred.equals(succ) && */!rmRelation.isBefore(pred, succ))
                {
                    //pred itself
                    rm.add(pred, succ);
                }
            }
            //case 4, or join <-> or join
            for (int j = 0; j != i && j < alOrJoin.size(); j++)
            {
                String succ = (String) alOrJoin.get(j);
                if (/*!pred.equals(succ) && */!rmRelation.isBefore(pred, succ))
                {
                    rm.add(pred, succ);
                }
            }
            //case 6, or join <-> and split
            for (int j = 0; j < alAndSplit.size(); j++)
            {
                String task = (String) alAndSplit.get(j);
                String[] arSucc = rmRelation.getSucc(task);
                for (int k = 0; k < arSucc.length; k++)
                {
                    String succ = arSucc[k];
                    if (/*!pred.equals(succ) && */!rmRelation.isBefore(pred, succ))
                    {
                        rm.add(pred, succ);
                    }
                }
            }
        }

        //case 5, or split <-> or split
        for (int i = 0; i < alOrSplit.size(); i++)
        {
            String pred = (String) alOrSplit.get(i);
            for (int j = 0; j != i && j < alOrSplit.size(); j++)
            {
                String succ = (String) alOrSplit.get(j);
                if (/*!pred.equals(succ) && */!rmRelation.isBefore(pred, succ))
                {
                    rm.add(pred, succ);
                }
            }
        }

        //case 7, and join <-> or split
        for (int i = 0; i < alAndSplit.size(); i++)
        {
            String task = (String) alAndSplit.get(i);
            String[] arPred = rmRelation.getBefore(task);
            for (int j = 0; j < arPred.length; j++)
            {
                String pred = arPred[j];
                for (int k = 0; k < alOrSplit.size(); k++)
                {
                    String succ = (String) alOrSplit.get(k);
                    if (/*!pred.equals(succ) && */!rmRelation.isBefore(pred, succ))
                    {
                        rm.add(pred, succ);
                    }
                }
            }
        }
        UnifiedMap<String, UnifiedMap<String,ArrayList<InvTask>>> invisibleTaskMap = new UnifiedMap<String, UnifiedMap<String, ArrayList<InvTask>>>();	//The beginInvisibleTask, theEndInvisibleTask ==>InvisibleTask;
        beginAdjustInvisibleTask(invisibleTaskMap);
        //enumate all process instances
        int np = log.numberOfInstances();
        for(int i=0; i<np && !rm.isFinished(); i++)
        {
            ProcessInstance pi = log.getInstance(i);
            AuditTrailEntryList atel = pi.getAuditTrailEntryList();
            //TODO 修改啊 修改成为一个符合条件的invisibleTask都要个来一遍的那种啊
            
            ArrayList<AuditTrailEntryList> atels = adjustInvisibleTask(atel,invisibleTaskMap);
            for (AuditTrailEntryList newAtel	:	atels)
            {
	            //atel = adjustInvisibleTask(atel);
	            for(int j=0; j<atel.size(); j++)
	            {
	                try
	                {
	                    AuditTrailEntry ate = newAtel.get(j);
	                    String task = ate.getElement() + "\0" + ate.getType();
	                    //scan this task
	                    rm.scan(task);
	                }
	                catch (IOException ex)
	                {
	                }
	                catch (IndexOutOfBoundsException ex)
	                {
	                }
	            }
            }
            //reset ReachableManager
            rm.reset();
        }
    }

    private void beginAdjustInvisibleTask(UnifiedMap<String, UnifiedMap<String,ArrayList<InvTask>>> invisibleTaskMap)
    {
    	
    	for (InvTask invTask	:	invTasks)
    	{
    		ArrayList<String> preTasks = invTask.pre;
    		ArrayList<String> sucTasks = invTask.suc;
    		if (invTask.type == InvTask.START)
    		{
    			preTasks = new ArrayList<String>();
    			preTasks.add(InvTask.TRACE_BEGIN_TAG);
    		}
    		else if (invTask.type == InvTask.END)
    		{
    			sucTasks = new ArrayList<String>();
    			sucTasks.add(InvTask.TRACE_END_TAG);
    		}
    		for (String preTask	:	preTasks)
    			for (String sucTask	:	sucTasks)
    			{
    				UnifiedMap<String, ArrayList<InvTask>> singleLine = invisibleTaskMap.get(preTask);
    				if (singleLine == null)
    				{
    					singleLine = new UnifiedMap<String, ArrayList<InvTask>>();
    					invisibleTaskMap.put(preTask, singleLine);
    				}
    				ArrayList<InvTask> line = singleLine.get(sucTask);
    				if (line == null)
    				{
    					line = new ArrayList<InvTask>();
    					singleLine.put(sucTask,line);
    				}
    				line.add(invTask);
    			}
    	}    	
    }
    
    private ArrayList<AuditTrailEntryList> adjustInvisibleTask(AuditTrailEntryList atel, UnifiedMap<String, UnifiedMap<String,ArrayList<InvTask>>> invisibleTaskMap)
    {
    	ArrayList<AuditTrailEntryList> result = new ArrayList<AuditTrailEntryList>();
    	//遍历atel中的每一个位置，判断这个位置是不是需要添加一个invisibleTask,如果需要添加，那么需要判断一下这个invisibletask.
    	UnifiedMap<Integer, ArrayList<InvTask>> posInvisibleTask = new UnifiedMap<Integer,ArrayList<InvTask>>();	//each pos correspond with a set of invisible tasks. that the set of the invisible task can be matched with the trace; between [pos, pos +1] should be the invisible tasks.
    	//1.判断所有可能添加invisibleTask的位置,存到posInvisibleTask里面
    	try 
    	{
    		//start
			String begin = atel.get(0).getName();
			UnifiedMap<String, ArrayList<InvTask>> singleLine = invisibleTaskMap.get(InvTask.TRACE_BEGIN_TAG);
			if (singleLine != null)
			{
				ArrayList<InvTask> line = singleLine.get(begin);
				if (line != null)
				{
					posInvisibleTask.put(-1,line);
				}
			}
			//end
			String end = atel.get(atel.size()-1).getName();
			singleLine = invisibleTaskMap.get(end);
			if (singleLine != null)
			{
				ArrayList<InvTask> line = singleLine.get(InvTask.TRACE_END_TAG);
				if (line != null)
				{
					posInvisibleTask.put(atel.size()-1,line);
				}
			}
			//inner
			for (int i=0; i<atel.size()-1; i++)
			{
				begin = atel.get(i).getName();
				end = atel.get(i+1).getName();
				singleLine = invisibleTaskMap.get(begin);
				if (singleLine != null)
				{
					ArrayList<InvTask> line = singleLine.get(end);
					if (line != null)
					{
						posInvisibleTask.put(i,line);
					}
				}
			}
	    	//根据所有可以产生的位置，添加不可见任务啊
	    	if (posInvisibleTask.size() == 0)	//no matching invisible task
	    	{
	    		result.add(atel);
	    	}
	    	else								//there exisits matchings..
	    	{
	    		Set<Integer> availablePoss = posInvisibleTask.keySet();
	    		Integer[] sortedPos = availablePoss.toArray(new Integer[0]);
	    		Arrays.sort(sortedPos);
	    		AuditTrailEntryList nowList = new AuditTrailEntryListImpl();
	    		getAllTraces(-1,0,sortedPos,result,nowList, atel,posInvisibleTask);		//递归来找所有的可能的trace啊
	    	}
		} 
		catch (IndexOutOfBoundsException | IOException e)
		{			
			e.printStackTrace();
		}    	      	
    	return result;    	
    }
    
    private void getAllTraces(int lastStep, int step, Integer[] sortedPos,
                              ArrayList<AuditTrailEntryList> result, AuditTrailEntryList nowList, AuditTrailEntryList basicList, UnifiedMap<Integer, ArrayList<InvTask>> posInvisibleTask) throws IndexOutOfBoundsException, IOException {
    	
    	//already done the available task, finish it.
    	if (step == sortedPos.length)
    	{
    		for (int i=lastStep; i< basicList.size(); i++)
    		{
    			AuditTrailEntry ate = basicList.get(i);
    			nowList.append(ate);    			
    		}
    		result.add(nowList);
    	}
    	
    	int nowPos = sortedPos[step];
    	//1.加入basic的元素
    	for (int i=lastStep; i<nowPos; i++)
    	{
    		AuditTrailEntry ate = basicList.get(i);
    		nowList.append(ate);
    	}
    	int nowListSize = nowList.size();
    	//2.循环加入元素
    	ArrayList<InvTask> availableTask = posInvisibleTask.get(nowPos);
    	for (InvTask invTask	:	availableTask)
    	{
    		//remove the more tasks..
    		for (int i=nowList.size()-1; i>=nowListSize; i++)
    			nowList.remove(i);
    		AuditTrailEntry ate = new AuditTrailEntryImpl();
    		ate.setName(invTask.taskName);
    		ate.setType("auto");
    		nowList.append(ate);
    		
    		//递归的做
    		getAllTraces(nowPos+1, step+1, sortedPos, result, nowList, basicList, posInvisibleTask);
    	}		
	}
    
	/**
     * eliminate any event who's task is in L1L
     */
    private void eliminateL1L()
    {
        if (alL1L.size() == 0)
        {
            return;
        }
        for (int i = 0; i < alT_log.size(); i++)
        {
            String t = (String) alT_log.get(i);
            rmRelation.removeBefore(t, alL1L);
            rmRelation.removeAfter(t, alL1L);
        }
    }

    /**
     * computeL_W
     */
    private void computeL_W()
    {
        lpL_W = new L1LPlaces(rmRelation, alL1L);
        lpL_W.compute();
    }

    private void computeTlogL1LTprime(LogReader log)
    {
        //enumate all process instances
        int np = log.numberOfInstances();
        for(int i=0; i<np; i++)
        {
            ProcessInstance pi = log.getInstance(i);
            AuditTrailEntryList atel = pi.getAuditTrailEntryList();

            //enumerate all events in one process instance
            String last2Task = "";
            String last1Task = "";
            boolean isFirst = true;
            for(int j=0; j<atel.size(); j++)
            {
                try
                {
                    //next event
                    AuditTrailEntry ate = atel.get(j);
                    String task = ate.getElement() + "\0" + ate.getType();

                    //T_I
                    if (isFirst)
                    {
                        if (!alT_I.contains(task))
                        {
                            alT_I.add(task);
                        }
                        isFirst = false;
                    }

                    //T_log
                    if (!alT_log.contains(task))
                    {
                        alT_log.add(task);
                    }
                    //L1L��aa
                    if (task.equals(last1Task) && !alL1L.contains(task))
                    {
                        alL1L.add(task);
                    }

                    //a>b
                    if (!last1Task.equals("") && !last1Task.equals(task))
                    {
                        //> relation
                        rmRelation.addRelation(last1Task, task, Relation.BEFORE);
                        //before task
                        rmRelation.addBefore(task, last1Task);
                    }
                    //aba
                    if (last2Task.equals(task) && !task.equals(last1Task))
                    {
                        rmRelation.addRelation(last2Task, last1Task,
                                               Relation.SHORTLOOP);
                    }

                    //go ahead
                    last2Task = last1Task;
                    last1Task = task;
                }
                catch (IOException ex)
                {
                }
                catch (IndexOutOfBoundsException ex)
                {
                }
            }

            //last task
            if (!alT_O.contains(last1Task))
            {
                alT_O.add(last1Task);
            }
        }

        //T_prime
        alT_prime.addAll(alT_log);
        alT_prime.removeAll(alL1L);
    }

	public PetriNet mineAlphPPInfo(LogReader log, AlphaPPData alphaPPData) {
	    alT_log =alphaPPData.alT_log;
	    alL1L = alphaPPData.alL1L;			//长度为1的循环中的任务，
	    alT_prime = alphaPPData.alT_prime;
	    alT_I = alphaPPData.alT_I;
	    alT_O = alphaPPData.alT_O;
	    rmRelation = alphaPPData.rmRelation;
	    lpL_W = alphaPPData.lpL_W;
	    invTasks = alphaPPData.invTasks;
	    invTaskAXYB = alphaPPData.invTaskAXYB;


		//3.做剩下的事情，挖出模型
	    //find l1l
	    Date d1 = new Date();
	    findL1L();	    
	    Date d2 = new Date();
//	    System.out.println("findL1L"+(d2.getTime() - d1.getTime()));
	    computeL_W();
	    Date d3 = new Date();
//	    System.out.println("computeL_W"+(d3.getTime() - d2.getTime()));
	    eliminateL1L();
	    Date d4 = new Date();
//	    System.out.println("eliminateL1Lv"+(d4.getTime() - d3.getTime()));
	    //在L1L中，因为	    
	    //induce >> from log
        
        rmRelation.induceCommOrder();
        induceReachable(log);        
        
        Date d5 = new Date();
//        System.out.println("induceReachable"+(d5.getTime() - d4.getTime()));
        //apply Theorem 1 (step 8)
       
        applyingTheoremOne();
        Date d6 = new Date();
//        System.out.println("applyingTheoremOne"+(d6.getTime() - d5.getTime()));
        //mining WF-net using alpha algorithm (step 9)
        
        miningUsingAlpha();
        Date d7 = new Date();
//        System.out.println("miningUsingAlpha"+(d7.getTime() - d6.getTime()));
        //patch places for these tasks in ID_W1
       
        patchIDW1();
        Date d8 = new Date();
//        System.out.println("patchIDW1"+(d8.getTime() - d7.getTime()));
        //apply theorem 2 (step 10)
        
        applyingTheoremTwo();
        		        
        Date d9 = new Date();
//        System.out.println("applyingTheoremTwo"+(d9.getTime() - d8.getTime()));
        //eliminate redundant dependency by rule 1 (step 11)
        
        eliminateRDByRule1();
        Date d10 = new Date();
//        System.out.println("eliminateRDByRule1"+(d10.getTime() - d9.getTime()));
        //derive places involving implicit dependency two (steps 12 and 13)
        
        derivePlacesForIDTwo();
        Date d11 = new Date();
//        System.out.println("derivePlacesForIDTwo"+(d11.getTime() - d10.getTime()));
        //apply theorem 3 (step 14)
        
        applyingTheoremThree();
        Date d12 = new Date();
//        System.out.println("applyingTheoremThree"+(d12.getTime() - d11.getTime()));
        //eliminate redundant implicit dependency (step 15)
        
        eliminateRDByRule2();
        Date d13 = new Date();
//        System.out.println("eliminateRDByRule2"+(d13.getTime() - d12.getTime()));
        //derive places involving implicit dependency three (steps 16 and 17)
        
        derivePlacesForIDThree();
        Date d14 = new Date();
//        System.out.println("derivePlacesForIDThree"+(d14.getTime() - d13.getTime()));
        //gather all the places of the mined WF-net (step 18)
        
        addShortLoopPlaces();
        
        Date d15 = new Date();
//        System.out.println("addShortLoopPlaces"+(d15.getTime() - d14.getTime()));
        //return the final result (steps 19-21)

        PetriNet mr = constructWFNet(log);
        Date d16 = new Date();
//        System.out.println("constructWFNet"+(d16.getTime() - d15.getTime()));
        return mr;		
	}

	private void findL1L() {
		
		
		for (Object task	:	alT_log)
			if (rmRelation.isBefore((String)task, (String)task))
			{
				if (!alL1L.contains(task))
					alL1L.add(task);
			}
		//alL1L.clear();
		//TODO  check each task in L1L, determine whether it should be removed like alpha++ paper 1st of "not-done" 
		checkL1L();
		alT_prime.clear();
		alT_prime.addAll(alT_log);
		alT_prime.removeAll(alL1L);
	}
	
	private void checkL1L()
	{
		ArrayList<String> needToDelete = new ArrayList<String>();
		//first kind (->)
		for (String s	:	(ArrayList<String>) alL1L)
		{
			
			//check whether this l1l is applicable to the therom 2.
			//to apply with the therom 2, there should be the following conditions:			
			String[] presArray = rmRelation.getBefore(s);
			ArrayList<String> pres = new ArrayList<String>();
			for (String pre	:	presArray)
				if (!pre.equals(s))
					pres.add(pre);
//			if (pres.size() != 1)
//				continue;
			String[] succsArray = rmRelation.getSucc(s);
			ArrayList<String> succs = new ArrayList<String>();
			for (String succ	:	succsArray)
				if (!succ.equals(s))
					succs.add(succ);
//			if (succs.size() !=1)
//				continue;
			boolean isExistPreSucc = false;
			for (String pre	:	pres)
				for (String succ	:	succs)
				{
					
//					String pre = pres.get(0);
//					String succ = succs.get(0);
					//2. the pre and succ should be in para.
					if (!rmRelation.isLoopPara(pre, succ))
						continue;
					//3. the pre and succ should share the common pred.
					ArrayList<String> commPreds = AlphaMMinerDataUtil.join(rmRelation.getBefore(pre), rmRelation.getBefore(succ));
					if (commPreds.size() == 0)
						continue;
					//4.there should another task sharing com Pre with s,  
					String[] preSuccs = rmRelation.getSucc(pre);
					if (preSuccs.length == 1)	//1 means the pre only has one succ, namely the s.
						continue;	
					//5. iterate on the preSucc, check whether there a preSucc, such as follows 
					//5.1 preSucc # s, (in order to make sure s and preSucc share the same input place.)
					//5.2 whether the presucc can be reached by pre, while the s is not reachable with the d.
					boolean isExist = false;
					for (String preSucc	:	preSuccs)
					{						
//						if (!rmRelation.isDependent(s, preSucc))
//							continue;	//preSucc is not # with the s..huohuo
						
						//TODO 
						//we should check >> relation for each succ preSucc..hehe			
						if (!rmRelation.isParaOrFollow(succ, preSucc))
							continue;
						if (rmRelation.isParaOrFollow(succ, s))
							continue;
						isExist = true;
					}					
					if (!isExist)
						continue;	
					isExistPreSucc = true;
				}				
			if (isExistPreSucc)
			{
				//remove the l1l relation.
				rmRelation.removeBefore(s, s);
				rmRelation.removeAfter(s, s);
				rmRelation.removeRelation(s, s, rmRelation.getRelation(s, s));
				needToDelete.add(s);
				//TODO
				//需要提出F#D,D#B,C#H,H#G
				adjustSharpRelation(pres,succs,s);
			}			
		}		
		
		//second kind(<-)
		for (String s	:	 (ArrayList<String>) alL1L)
		{
			if (needToDelete.contains(s))
				continue;
			//check whether this l1l is applicable to the therom 2.
			//to apply with the therom 2, there should be the following conditions:						
			String[] presArray = rmRelation.getBefore(s);
			ArrayList<String> pres = new ArrayList<String>();
			for (String pre	:	presArray)
				if (!pre.equals(s))
					pres.add(pre);
			String[] succsArray = rmRelation.getSucc(s);
			ArrayList<String> succs = new ArrayList<String>();
			for (String succ	:	succsArray)
				if (!succ.equals(s))
					succs.add(succ);

			boolean isExistPreSucc = false;
			for (String pre	:	pres)
				for (String succ	:	succs)
				{
					
//					String pre = pres.get(0);
//					String succ = succs.get(0);
					//2. the pre and succ should be in para.
					if (!rmRelation.isLoopPara(pre, succ))
						continue;
					//3. the pre and succ should share the common succ.
					ArrayList<String> commSuccs = AlphaMMinerDataUtil.join(rmRelation.getSucc(pre), rmRelation.getSucc(succ));
					if (commSuccs.size() == 0)
						continue;
					//4.there should another task sharing com Succ with s,  
					String[] succPres = rmRelation.getBefore(succ);
					if (succPres.length == 1)	//1 means the pre only has one succ, namely the s.
						continue;	
					//5. iterate on the succPre, check whether there a succPre, such as follows 
					//5.1 succPre # s, (in order to make sure s and preSucc share the same input place.)
					//5.2 whether the succPre can  reach to pre, while the s is not reachable with the pre.
					boolean isExist = false;
					for (String succPre	:	succPres)
					{
						
//						if (!rmRelation.isDependent(s, succPre))
//							continue;	//preSucc is not # with the s..huohuo

						//we should check >> relation for each succ preSucc..hehe			
						if (!rmRelation.isParaOrFollow(succPre, pre))
							continue;
						if (rmRelation.isParaOrFollow(s, pre))
							continue;
						isExist = true;
					}					
					if (!isExist)
						continue;	
					isExistPreSucc = true;
				}				
			if (isExistPreSucc)
			{
				//remove the l1l relation.
				rmRelation.removeBefore(s, s);
				rmRelation.removeAfter(s, s);
				rmRelation.removeRelation(s, s, rmRelation.getRelation(s, s));
				needToDelete.add(s);
				//TODO
				//需要提出F#D,D#B,C#H,H#G
				adjustSharpRelation(pres,succs,s);
			}	
			
		}
		
		alL1L.removeAll(needToDelete);
		
	}

	private void adjustSharpRelation(ArrayList<String> pres,
                                     ArrayList<String> succs, String s)
	{
		for (String pre	:	pres)
			for (String succ	:	succs)
			{
				if (rmRelation.isBefore(pre, succ))
				{
					Set<String> preRelation = new UnifiedSet<String>();
					Set<String> succRelation = new UnifiedSet<String>();
					
					preRelation.add(pre);
					preRelation.add(s);
					
					succRelation.add(succ);
					succRelation.add(s);
					rmRelation.adjustSharpRelation.add(preRelation);
					rmRelation.adjustSharpRelation.add(succRelation);
				}
			}			
	}
}


class DoubleSets
{
    ArrayList alA = new ArrayList();
    ArrayList alB = new ArrayList();

    public DoubleSets()
    {
    }

    public void addToA(String a)
    {
        if (!alA.contains(a))
        {
            alA.add(a);
        }
    }

    public void addToA(ArrayList alTask)
    {
        for (int i = 0; i < alTask.size(); i++)
        {
            String t = (String) alTask.get(i);
            if (!alA.contains(t))
            {
                alA.add(t);
            }
        }
    }

    public void addToB(String b)
    {
        if (!alB.contains(b))
        {
            alB.add(b);
        }
    }

    public void addToB(ArrayList alTask)
    {
        for (int i = 0; i < alTask.size(); i++)
        {
            String t = (String) alTask.get(i);
            if (!alB.contains(t))
            {
                alB.add(t);
            }
        }
    }

    public String[] getA()
    {
        String[] arA = new String[alA.size()];
        alA.toArray(arA);
        return arA;
    }

    public String[] getB()
    {
        String[] arB = new String[alB.size()];
        alB.toArray(arB);
        return arB;
    }

    public boolean containedInA(String a)
    {
        return alA.contains(a);
    }

    public boolean containedInB(String b)
    {
        return alB.contains(b);
    }

    public boolean equals(Object o)
    {
        DoubleSets ds = (DoubleSets) o;

        return alA.containsAll(ds.alA) && alB.containsAll(ds.alB);
    }

    public String toString()
    {
        return "{" + alA.toString() + "->" + alB.toString() + "}";
    }
}

class TripleSets
    extends DoubleSets
{
    ArrayList alC = new ArrayList();

    public TripleSets()
    {
    }

    public void addToC(String c)
    {
        if (!alC.contains(c))
        {
            alC.add(c);
        }
    }

    public String[] getC()
    {
        String[] arC = new String[alC.size()];
        alC.toArray(arC);
        return arC;
    }

    public boolean containedInC(String c)
    {
        return alC.contains(c);
    }

    public boolean contains(TripleSets ts)
    {
        return alA.containsAll(ts.alA) && alB.containsAll(ts.alB);
    }
}

class L1LPlaces
{
    ArrayList alPlaces;
    RelationMatrix rm;
    ArrayList alL1L;

    public L1LPlaces(RelationMatrix rm, ArrayList alL1L)
    {
        alPlaces = new ArrayList();
        this.rm = rm;
        this.alL1L = alL1L;
    }

    //clear the useless relation
    private void clear()
    {
        //remove all tasks in L1L
        for (int i = 0; i < alL1L.size(); i++)
        {
            String t = (String) alL1L.get(i);
            rm.removeBefore(t, alL1L);
            rm.removeAfter(t, alL1L);
        }

        //remove all tasks has relation aba
        ArrayList alTemp = new ArrayList();
        for (int i = 0; i < alL1L.size(); i++)
        {
            //before
            String t = (String) alL1L.get(i);
            String[] arBefore = rm.getBefore(t);
            for (int j = 0; j < arBefore.length; j++)
            {
                if (rm.isShortLoop(t, arBefore[j]))
                {
                    alTemp.add(arBefore[j]);
                }
            }

            rm.removeBefore(t, alTemp);
            alTemp.clear();

            //after relation
            rm.removeShortLoop(t);
        }
    }

    //generate all the L1L places
    public void compute()
    {
        clear();

        //compute
        generate();
    }

    private void generate()
    {
        //store the connecting places (using ArrayList) of each element in L1L
        Hashtable htL1LPlaces = new Hashtable();

        //divide
        for (int i = 0; i < alL1L.size(); i++)
        {
            String c = (String) alL1L.get(i);
            String[] arA = rm.getBefore(c);
            String[] arB = rm.getAfter(c);
            ArrayList alPlacesOfTc = new ArrayList();
            for (int j = 0; j < arA.length; j++)
            {
                String a = arA[j];
                for (int k = 0; k < arB.length; k++)
                {
                    String b = arB[k];
                    //not parallel
                    if (!rm.isLoopPara(a, b))
                    {
                        boolean isUnited = false;
                        for (int l = 0; l < alPlacesOfTc.size(); l++)
                        {
                            //try to unit ({a}, {b}, {c}) to exitstant places
                            TripleSets ts = (TripleSets) alPlacesOfTc.get(l);
                            if (ts.containedInA(a) && ts.containedInB(b))
                            {
                                //nothing to do
                                isUnited = true;
                            }
                            else if (ts.containedInA(a))
                            {
                                //for any b' in B: b'#b
                                String[] arBprime = ts.getB();
                                boolean isDependent = true;
                                for (int n = 0; n < arBprime.length; n++)
                                {
                                    String b_prime = arBprime[n];
                                    if (!rm.isDependent(b, b_prime))
                                    {
                                        isDependent = false;
                                        break;
                                    }
                                }
                                //# is satisfied, add b to current B
                                if (isDependent)
                                {
                                    ts.addToB(b);
                                    isUnited = true;
                                }
                            }
                            else if (ts.containedInB(b))
                            {
                                //for any a' in A: a'#a
                                String[] arAprime = ts.getA();
                                boolean isDependent = true;
                                for (int n = 0; n < arAprime.length; n++)
                                {
                                    String a_prime = arAprime[n];
                                    if (!rm.isDependent(a, a_prime))
                                    {
                                        isDependent = false;
                                        break;
                                    }
                                }
                                //# is satisfied, add a to current A
                                if (isDependent)
                                {
                                    ts.addToA(a);
                                    isUnited = true;
                                }
                            }
                            else
                            {
                                boolean isSatisfied = true;

                                //any a' in A: not(a'||b) and a'#a
                                String[] arAprime = ts.getA();
                                for (int n = 0; n < arAprime.length; n++)
                                {
                                    String a_prime = arAprime[n];
                                    if (rm.isLoopPara(a_prime, b) ||
                                        !rm.isDependent(a_prime, a))
                                    {
                                        isSatisfied = false;
                                        break;
                                    }
                                }

                                if (!isSatisfied)
                                {
                                    continue;
                                }

                                //any b' in B: not(b'||b) and b'#b
                                String[] arBprime = ts.getB();
                                for (int n = 0; n < arBprime.length; n++)
                                {
                                    String b_prime = arBprime[n];
                                    if (rm.isLoopPara(b_prime, a) ||
                                        !rm.isDependent(b_prime, b))
                                    {
                                        isSatisfied = false;
                                        break;
                                    }
                                }

                                if (isSatisfied)
                                {
                                    ts.addToA(a);
                                    ts.addToB(b);
                                    isUnited = true;
                                }
                            }
                        }

                        //if not united, then create a new triple set for it
                        if (!isUnited)
                        {
                            TripleSets ts = new TripleSets();
                            ts.addToA(a);
                            ts.addToB(b);
                            ts.addToC(c);

                            alPlacesOfTc.add(ts);
                        }
                    }
                }
            }

            htL1LPlaces.put(c, alPlacesOfTc);
        }

        //conquer
        Enumeration keysL1L = htL1LPlaces.keys();
        while (keysL1L.hasMoreElements())
        {
            String c = (String) keysL1L.nextElement();
            ArrayList alPlacesOfTc = (ArrayList) htL1LPlaces.get(c);
            for (int i = 0; i < alPlacesOfTc.size(); i++)
            {
                TripleSets tsTc = (TripleSets) alPlacesOfTc.get(i);
                boolean isUnited = false;
                for (int j = 0; j < alPlaces.size(); j++)
                {
                    TripleSets ts = (TripleSets) alPlaces.get(j);
                    if (ts.contains(tsTc) && tsTc.contains(ts))
                    {
                        isUnited = true;
                        ts.addToC(c);
                    }
                }
                if (!isUnited)
                {
                    alPlaces.add(tsTc);
                }
            }
        }
    }

    //return all the L1L places
    public TripleSets[] getL1LPlaces()
    {
        TripleSets[] arPlaces = new TripleSets[alPlaces.size()];
        alPlaces.toArray(arPlaces);
        return arPlaces;
    }
}

class ReachableUnit
{
    boolean isOpened = false;
    ArrayList alUnreachable = new ArrayList();

    public ReachableUnit()
    {
    }

    public void open()
    {
        isOpened = true;
    }

    public void close()
    {
        isOpened = false;
    }

    public boolean isOpened()
    {
        return isOpened;
    }

    public void add(String task)
    {
        if (!alUnreachable.contains(task))
        {
            alUnreachable.add(task);
        }
    }

    public void reach(String task)
    {
        if (alUnreachable.contains(task))
        {
            alUnreachable.remove(task);
        }
    }

    public boolean isFinished()
    {
        return alUnreachable.size() == 0;
    }

    public String[] getUnreachable()
    {
        String[] arUnreachable = new String[alUnreachable.size()];
        alUnreachable.toArray(arUnreachable);
        return arUnreachable;
    }
}

class ReachableManager
{
    Hashtable htPred = new Hashtable();
    Hashtable htSucc = new Hashtable();
    RelationMatrix rm;

    public ReachableManager(RelationMatrix rm)
    {
        this.rm = rm;
    }

    public void add(String pred, String succ)
    {
        //store pred's succ
        ReachableUnit ruPred = (ReachableUnit) htPred.get(pred);
        if (ruPred == null)
        {
            ruPred = new ReachableUnit();
            htPred.put(pred, ruPred);
        }
        ruPred.add(succ);

        //store succ's pred
        ReachableUnit ruSucc = (ReachableUnit) htSucc.get(succ);
        if (ruSucc == null)
        {
            ruSucc = new ReachableUnit();
            htSucc.put(succ, ruSucc);
        }
        ruSucc.add(pred);
    }

    public boolean isFinished()
    {
        return htPred.isEmpty() || htSucc.isEmpty();
    }

    public void scan(String task)
    {
        //handle task's predecessors
        ReachableUnit ruSucc = (ReachableUnit) htSucc.get(task);
        if (ruSucc != null)
        {
            String[] arPred = ruSucc.getUnreachable();
            for (int i = 0; i < arPred.length; i++)
            {
                ReachableUnit ruPred = (ReachableUnit) htPred.get(arPred[i]);
                if (ruPred.isOpened())
                {
                    //record >> relation
                    rm.addRelation(arPred[i], task, Relation.REACHABLE);
                    //notify pred reach
                    ruPred.reach(task);
                    //remove finished Pred ReachableUnit
                    if (ruPred.isFinished())
                    {
                        htPred.remove(arPred[i]);
                    }
                    //notify succ reach
                    ruSucc.reach(arPred[i]);
                    //remove finished Succ ReachableUnit
                    if (ruSucc.isFinished())
                    {
                        htSucc.remove(task);
                    }
                }
            }
        }

        //open task itself
        ReachableUnit ruTask = (ReachableUnit) htPred.get(task);
        if (ruTask != null)
        {
            ruTask.open();
        }

        //close task's Common Predecessor and Common Successor
        String[] arComm = rm.getComm(task);
        for (int i = 0; i < arComm.length; i++)
        {
            ReachableUnit ru = (ReachableUnit) htPred.get(arComm[i]);
            if (ru != null && ru.isOpened())
            {
                //��Ҫ��ӿɴ��ϵ

                //�ر�
                ru.close();
            }
        }
    }

    /**
     * close all opened preds
     */
    public void reset()
    {
        Enumeration elems = htPred.elements();
        while (elems.hasMoreElements())
        {
            ReachableUnit ru = (ReachableUnit) elems.nextElement();
            ru.close();
        }
    }
}

class PlaceForestBuilder
{
    ArrayList alForest = new ArrayList();
    RelationMatrix rm;

    public PlaceForestBuilder(RelationMatrix rm)
    {
        this.rm = rm;
    }

    public void add(String pred, String succ)
    {
        boolean isUnited = false;
        int oldSize = alForest.size();
        for (int i = alForest.size() - 1; i >= 0; i--)
        {
            DoubleSets ds = (DoubleSets) alForest.get(i);
            boolean isInA = ds.containedInA(pred);
            boolean isInB = ds.containedInB(succ);
            if (isInA && isInB)
            {
                //to put pred->succ to a proper position recursively
                isUnited = true;
            }
            else if (isInA)
            {
                boolean isStopped = false;
                //test if for each a in A such that a->succ
                String[] arA = ds.getA();
                for (int j = 0; j < arA.length; j++)
                {
                    if (!rm.isNormSucc(arA[j], succ))
                    {
                        isStopped = true;
                        break;
                    }
                }
                //test if for each b in B such that b#succ
                if (!isStopped)
                {
                    String[] arB = ds.getB();
                    for (int j = 0; j < arB.length; j++)
                    {
                        if (!rm.isDependent(arB[j], succ))
                        {
                            isStopped = true;
                            break;
                        }
                    }
                    if (!isStopped)
                    {
                        //generate a new tree
                        ds.addToB(succ);

                        isUnited = true;
                    }
                }
            }
            else if (isInB)
            {
                boolean isStopped = false;
                //test if for each b in B such that pred->b
                String[] arB = ds.getB();
                for (int j = 0; j < arB.length; j++)
                {
                    if (!rm.isNormSucc(pred, arB[j]))
                    {
                        isStopped = true;
                        break;
                    }
                }
                //test if for each a in A such that a#pred
                if (!isStopped)
                {
                    String[] arA = ds.getA();
                    for (int j = 0; j < arA.length; j++)
                    {
                        if (!rm.isDependent(arA[j], pred))
                        {
                            isStopped = true;
                            break;
                        }
                    }
                    if (!isStopped)
                    {
                        //generate a new tree
                        ds.addToA(pred);

                        isUnited = true;
                    }
                }
            }
            else
            {
                boolean isStopped = false;
                String[] arA = ds.getA();
                for (int j = 0; j < arA.length; j++)
                {
                    //test if for each a in A such that a->succ
                    if (!rm.isNormSucc(arA[j], succ))
                    {
                        isStopped = true;
                        break;
                    }
                    //test if for each a in A such that a#pred
                    if (!rm.isDependent(arA[j], pred))
                    {
                        isStopped = true;
                        break;
                    }
                }
                if (!isStopped)
                {
                    String[] arB = ds.getB();
                    for (int j = 0; j < arB.length; j++)
                    {
                        //test if for each b in B such that pred->b
                        if (!rm.isNormSucc(pred, arB[j]))
                        {
                            isStopped = true;
                            break;
                        }
                        //test if for each b in B such that b#succ
                        if (!rm.isDependent(succ, arB[j]))
                        {
                            isStopped = true;
                            break;
                        }
                    }
                    if (!isStopped)
                    {
                        //generate a new tree
                        ds.addToA(pred);
                        ds.addToB(succ);

                        isUnited = true;
                    }
                }
            }
        }

        if (!isUnited)
        {
            for (int i = alForest.size() - 1; i >= 0; i--)
            {
                DoubleSets ds = (DoubleSets) alForest.get(i);
                boolean isInA = ds.containedInA(pred);
                boolean isInB = ds.containedInB(succ);
                DoubleSets dsNew = new DoubleSets();
                if (isInA)
                {
                    //test if for each a in A such that a->succ
                    String[] arA = ds.getA();
                    for (int j = 0; j < arA.length; j++)
                    {
                        if (rm.isNormSucc(arA[j], succ))
                        {
                            dsNew.addToA(arA[j]);
                        }
                    }
                    //test if for each b in B such that b#succ
                    String[] arB = ds.getB();
                    for (int j = 0; j < arB.length; j++)
                    {
                        if (rm.isDependent(arB[j], succ))
                        {
                            dsNew.addToB(arB[j]);
                        }
                    }
                }
                else if (isInB)
                {
                    //test if for each b in B such that pred->b
                    String[] arB = ds.getB();
                    for (int j = 0; j < arB.length; j++)
                    {
                        if (rm.isNormSucc(pred, arB[j]))
                        {
                            dsNew.addToB(arB[j]);
                        }
                    }
                    //test if for each a in A such that a#pred
                    String[] arA = ds.getA();
                    for (int j = 0; j < arA.length; j++)
                    {
                        if (rm.isDependent(arA[j], pred))
                        {
                            dsNew.addToA(arA[j]);
                        }
                    }
                }
                else
                {
                    String[] arA = ds.getA();
                    for (int j = 0; j < arA.length; j++)
                    {
                        //test if for each a in A such that a->succ, a#pred
                        if (rm.isNormSucc(arA[j], succ) &&
                            rm.isDependent(arA[j], pred))
                        {
                            dsNew.addToA(arA[j]);
                        }
                    }
                    String[] arB = ds.getB();
                    for (int j = 0; j < arB.length; j++)
                    {
                        //test if for each b in B such that pred->b, b#succ
                        if (rm.isNormSucc(pred, arB[j]) &&
                            rm.isDependent(succ, arB[j]))
                        {
                            dsNew.addToB(arB[j]);
                        }
                    }
                }
                dsNew.addToA(pred);
                dsNew.addToB(succ);
                boolean isFound = false;
                int newSize = alForest.size();
                for (int k = newSize - 1; k >= oldSize; k--)
                {
                    DoubleSets dsOld = (DoubleSets) alForest.get(k);
                    if (dsNew.equals(dsOld))
                    {
                        alForest.remove(k);
                    }
                    else if (dsOld.equals(dsNew))
                    {
                        isFound = true;
                        break;
                    }
                }
                if (!isFound)
                {
                    alForest.add(dsNew);
                }
            }

            if (alForest.isEmpty())
            {
                DoubleSets dsNew = new DoubleSets();
                dsNew.addToA(pred);
                dsNew.addToB(succ);
                alForest.add(dsNew);
            }
        }
    }

    //get all the maximal places of tas t
    public DoubleSets[] getPlaces()
    {
        DoubleSets[] arPlaces = new DoubleSets[alForest.size()];
        for (int i = 0; i < alForest.size(); i++)
        {
            arPlaces[i] = (DoubleSets) alForest.get(i);
        }

        return arPlaces;
    }

    /**
     * clear
     */
    protected void clear()
    {
        alForest.clear();
    }
}

class ImplicitDependency
{
    String pred;
    String succ;

    public ImplicitDependency(String pred, String succ)
    {
        this.pred = pred;
        this.succ = succ;
    }

    public String getPred()
    {
        return pred;
    }

    public String getSucc()
    {
        return succ;
    }

    public boolean equals(Object o)
    {
        ImplicitDependency id = (ImplicitDependency) o;
        return pred.equals(id.pred) && succ.equals(id.succ);
    }

    public String toString()
    {
        return pred + "|->" + succ;
    }
}

class IDWManager
{
    Hashtable htPred = new Hashtable();
    Hashtable htSucc = new Hashtable();

    public IDWManager()
    {
    }

    public void clear()
    {
        htPred.clear();
        htSucc.clear();
    }

    public void add(String pred, String succ)
    {
        //added to Pred table
        ArrayList alIDW2 = (ArrayList) htPred.get(pred);
        if (alIDW2 == null)
        {
            alIDW2 = new ArrayList();
            htPred.put(pred, alIDW2);
        }
        if (!alIDW2.contains(succ))
        {
            alIDW2.add(succ);
        }

        //added to Succ table
        alIDW2 = (ArrayList) htSucc.get(succ);
        if (alIDW2 == null)
        {
            alIDW2 = new ArrayList();
            htSucc.put(succ, alIDW2);
        }
        if (!alIDW2.contains(pred))
        {
            alIDW2.add(pred);
        }
    }

    public void remove(String pred, String succ)
    {
        removeFromPred(pred, succ);

        removeFromSucc(pred, succ);
    }

    private void removeFromPred(String pred, String succ)
    {
        //removed from Pred table
        ArrayList alIDW2 = (ArrayList) htPred.get(pred);
        if (alIDW2 != null)
        {
            alIDW2.remove(succ);
            if (alIDW2.isEmpty())
            {
                htPred.remove(alIDW2);
            }
        }
    }

    private void removeFromSucc(String pred, String succ)
    {
        //removed from Succ table
        ArrayList alIDW2 = (ArrayList) htSucc.get(succ);
        if (alIDW2 != null)
        {
            alIDW2.remove(pred);
            if (alIDW2.isEmpty())
            {
                htSucc.remove(alIDW2);
            }
        }
    }

    //eliminate redundant implicit dependency
    public void eliminateByRuleOne(RelationMatrix rm, PlacesWithIDTwo alIDTwo)
    {
        Enumeration keys = htPred.keys();
        ArrayList alTemp = new ArrayList();
        while (keys.hasMoreElements())
        {
            String a = (String) keys.nextElement();
            ArrayList alSucc = (ArrayList) htPred.get(a);
            for (int i = 0; i < alSucc.size(); i++)
            {
                String b = (String) alSucc.get(i);
                for (int j = i + 1; j < alSucc.size(); j++)
                {
                    String c = (String) alSucc.get(j);
                    if (rm.isFollowedBy(b, c) && !alTemp.contains(c))
                    {
                        alTemp.add(c);
                        alIDTwo.remove(a, c);
                    }
                    else if (rm.isFollowedBy(c, b) && !alTemp.contains(b))
                    {
                        alTemp.add(b);
                        alIDTwo.remove(a, b);
                    }
                }
            }
            remove(a, alTemp);
            alTemp.clear();
        }
    }

    /**
     * remove
     *
     * @param alSucc ArrayList
     */
    private void remove(String pred, ArrayList alSucc)
    {
        //removed from Pred table
        ArrayList alIDW2 = (ArrayList) htPred.get(pred);
        if (alIDW2 != null)
        {
            alIDW2.removeAll(alSucc);
            if (alIDW2.isEmpty())
            {
                htPred.remove(pred);
            }
        }

        for (int i = 0; i < alSucc.size(); i++)
        {
            String succ = (String) alSucc.get(i);
            removeFromSucc(pred, succ);
        }
    }

    public String toString()
    {
        return htPred.toString();
    }

    /**
     * convert
     *
     * @param rmRelation RelationMatrix
     */
    protected void convert(RelationMatrix rmRelation)
    {
        Enumeration keys = htPred.keys();
        while (keys.hasMoreElements())
        {
            String pred = (String) keys.nextElement();
            ArrayList alSucc = (ArrayList) htPred.get(pred);
            for (int i = 0; i < alSucc.size(); i++)
            {
                String succ = (String) alSucc.get(i);
                rmRelation.addRelation(pred, succ, Relation.SUCCESSIVE);
                rmRelation.addAfter(pred, succ);
                rmRelation.addBefore(succ, pred);
            }
        }
    }

    /**
     * eliminateByRuleTwo
     *
     * @param rmRelation RelationMatrix
     */
    protected void eliminateByRuleTwo(RelationMatrix rmRelation)
    {
        //enumerate all the third kind of implicit dependencies
        Enumeration keys = htPred.keys();
        Hashtable htComposite = new Hashtable();
        while (keys.hasMoreElements())
        {
            String pred = (String) keys.nextElement();
            //get the array of successors
            ArrayList alSucc = (ArrayList) htPred.get(pred);
            //recursively call
            for (int i = 0; i < alSucc.size(); i++)
            {
                String succ = (String) alSucc.get(i);
                ArrayList<String> alVisited = new ArrayList<String>();
                composite(pred, succ, htComposite, alVisited);
            }
        }

        //remove all implicit dependencies in htComposite from htPred and htSucc
        keys = htComposite.keys();
        while (keys.hasMoreElements())
        {
            String pred = (String) keys.nextElement();
            ArrayList alSucc = (ArrayList) htComposite.get(pred);
            remove(pred, alSucc);
        }
    }

    /**
     * composite
     *
     * @param start String
     * @param end String
     * @param alVisited ArrayList
     */
    private void composite(String start, String end, Hashtable htComposite, ArrayList<String> alVisited)
    {
        ArrayList alSucc = (ArrayList) htPred.get(end);
        if (alSucc == null)
        {
            return;
        }
        alVisited.add(end);
        for (int i = 0; i < alSucc.size(); i++)
        {
            String succ = (String) alSucc.get(i);
            if(!start.equals(succ) && !alVisited.contains(succ))
            {
                ArrayList alStart = (ArrayList) htComposite.get(start);
                if (alStart == null)
                {
                    alStart = new ArrayList();
                    htComposite.put(start, alStart);
                }
                alStart.add(succ);

                composite(start, succ, htComposite, alVisited);
            }
        }
    }

    /**
     * derive
     *
     * @param rmRelation RelationMatrix
     * @return DoubleSets[]
     */
    protected DoubleSets[] derive(RelationMatrix rmRelation)
    {
        PlaceForestBuilder pfb = new PlaceForestBuilder(rmRelation);
        Enumeration keys = htPred.keys();
        while (keys.hasMoreElements())
        {
            String pred = (String) keys.nextElement();
            ArrayList alSucc = (ArrayList) htPred.get(pred);
            for (int i = 0; i < alSucc.size(); i++)
            {
                String succ = (String) alSucc.get(i);
                pfb.add(pred, succ);
            }
        }

        return pfb.getPlaces();
    }
}

class PlacesWithIDTwo
{
    Hashtable htPlaces = new Hashtable();

    public PlacesWithIDTwo()
    {
    }

    public void add(DoubleSets ds, String pred, String succ)
    {
        //place as key and array of implicit dependency as value
        ArrayList alPlaces = (ArrayList) htPlaces.get(ds);
        if (alPlaces == null)
        {
            alPlaces = new ArrayList();

            htPlaces.put(ds, alPlaces);
        }
        ImplicitDependency id = new ImplicitDependency(pred, succ);
        if (!alPlaces.contains(id))
        {
            alPlaces.add(id);
        }
    }

    /**
     * remove
     *
     * @param pred String
     * @param succ String
     */
    protected void remove(String pred, String succ)
    {
        Enumeration keys = htPlaces.keys();
        while (keys.hasMoreElements())
        {
            DoubleSets ds = (DoubleSets) keys.nextElement();
            ArrayList alID = (ArrayList) htPlaces.get(ds);
            for (int i = 0; i < alID.size(); i++)
            {
                ImplicitDependency id = (ImplicitDependency) alID.get(i);
                if (id.pred.equals(pred) && id.succ.equals(succ))
                {
                    alID.remove(id);
                    break;
                }
            }
        }
    }

    /**
     * get
     *
     * @return DoubleSets[]
     */
    protected DoubleSets[] get()
    {
        Set keys = htPlaces.keySet();
        DoubleSets[] arPlaces = new DoubleSets[keys.size()];
        keys.toArray(arPlaces);
        return arPlaces;
    }

    /**
     * derive
     *
     * @param rmRelation RelationMatrix
     * @return DoubleSets[]
     */
    protected DoubleSets[] derive(RelationMatrix rmRelation)
    {
        ArrayList alPlaces = new ArrayList();
        PlaceForestBuilder pfb = new PlaceForestBuilder(rmRelation);
        Enumeration keys = htPlaces.keys();
        while (keys.hasMoreElements())
        {
            DoubleSets ds = (DoubleSets) keys.nextElement();
            ArrayList alID = (ArrayList) htPlaces.get(ds);
            for (int i = 0; i < alID.size(); i++)
            {
                ImplicitDependency id = (ImplicitDependency) alID.get(i);
                pfb.add(id.pred, id.succ);
            }
            //derive new places
            DoubleSets[] arPlaces = pfb.getPlaces();
            for (int i = 0; i < arPlaces.length; i++)
            {
                arPlaces[i].addToA(ds.alA);
                arPlaces[i].addToB(ds.alB);

                alPlaces.add(arPlaces[i]);
            }

            //to be used again
            pfb.clear();
        }

        DoubleSets[] arPlaces = new DoubleSets[alPlaces.size()];
        alPlaces.toArray(arPlaces);
        return arPlaces;
    }

    /**
     * addNewComm
     *
     * @param rmRelation RelationMatrix
     */
    protected void addNewComm(RelationMatrix rmRelation, ArrayList alOrSplit,
                              ArrayList alOrJoin)
    {
        Enumeration keys = htPlaces.keys();
        while (keys.hasMoreElements())
        {
            DoubleSets ds = (DoubleSets) keys.nextElement();
            String[] arA = ds.getA();
            String[] arB = ds.getB();
            ArrayList alID = (ArrayList) htPlaces.get(ds);
            for (int i = 0; i < alID.size(); i++)
            {
                ImplicitDependency id = (ImplicitDependency) alID.get(i);
                if (ds.containedInA(id.pred))
                {
                    for (int j = 0; j < arB.length; j++)
                    {
                        rmRelation.addRelation(arB[j], id.succ,
                                               Relation.COMMPRED);
                        rmRelation.addRelation(id.succ, arB[j],
                                               Relation.COMMPRED);
                        if (!alOrSplit.contains(arB[j]))
                        {
                            alOrSplit.add(arB[j]);
                        }
                    }
                }
                else if (ds.containedInB(id.succ))
                {
                    for (int j = 0; j < arA.length; j++)
                    {
                        rmRelation.addRelation(arA[j], id.pred,
                                               Relation.COMMSUCC);
                        rmRelation.addRelation(id.pred, arA[j],
                                               Relation.COMMSUCC);
                        if (!alOrJoin.contains(arA[j]))
                        {
                            alOrJoin.add(arA[j]);
                        }
                    }
                }
            }
        }
    }

    /**
     * clear
     */
    public void clear()
    {
        htPlaces.clear();
    }
}

class TaskDivider
{
    RelationMatrix rm;
    ArrayList alGroups = new ArrayList();

    public TaskDivider(RelationMatrix rm)
    {
        this.rm = rm;
    }

    public void add(String task)
    {
        boolean isFound = false;
        for (int i = 0; i < alGroups.size(); i++)
        {
            ArrayList aGroup = (ArrayList) alGroups.get(i);
            String t = (String) aGroup.get(0);
            if (rm.isNormPara(t, task))
            {
                isFound = true;
                aGroup.add(task);
                break;
            }
        }

        if (!isFound)
        {
            ArrayList aGroup = new ArrayList();
            aGroup.add(task);
            alGroups.add(aGroup);
        }
    }

    public void clear()
    {
        alGroups.clear();
    }

    public ArrayList[] getGroups()
    {
        ArrayList[] arGroups = new ArrayList[alGroups.size()];
        alGroups.toArray(arGroups);
        return arGroups;
    }

    public boolean isEmpty()
    {
        return alGroups.isEmpty();
    }
}

class Relation
{
    //a>b
    public static int BEFORE = 1;
    //a^b (aba)
    public static int SHORTLOOP = 2;

    //a->b
    public static int SUCCESSIVE = 4;
    //a||b
    public static int PARALLEL = 8;

    //a<|b
    public static int COMMPRED = 16;
    //a|>b
    public static int COMMSUCC = 32;
    //a>>b
    public static int REACHABLE = 64;

    //And Split
    public static int ANDSPLIT = 1;
    //And Join
    public static int ANDJOIN = 2;
    //Or Split
    public static int ORSPLIT = 4;
    //Or Join
    public static int ORJOIN = 8;
    //Or Join with Different output tasks
    public static int ORJOINDIFFOUT = 16;
}

class RelationMatrix
{
    //hash table to store all task units
    Hashtable htRelations;
	Set<Set<String>> adjustSharpRelation;
    //constructor
    public RelationMatrix()
    {
        htRelations = new Hashtable();
        adjustSharpRelation = new UnifiedSet<Set<String>>();
    }

    public void addTask(String taskName)
    {
    	TaskUnit tuT = (TaskUnit)htRelations.get(taskName);
    	if (tuT == null)
    	{
    		tuT = new TaskUnit();
    	}
    	htRelations.put(taskName, tuT);
    }
    
    public void induceCommOrder() {
        Enumeration keys = htRelations.keys();
        while(keys.hasMoreElements())
        {
            String t = (String)keys.nextElement();
            TaskUnit tu = (TaskUnit)htRelations.get(t);            
            
            String[] arPred = tu.getPred();
            String[] arSucc = tu.getRela();
            

            //step 2: <| and |>
            //common successor
            arPred = tu.getPred();
            for(int i=0; i<arPred.length; i++)
            {
                for(int j=i+1; j<arPred.length; j++)
                {
                    //#
                    if (isDependent(arPred[i], arPred[j]))
                    {
                        //increase |> tasks
                        addRelation(arPred[i], arPred[j], Relation.COMMSUCC);
                        addRelation(arPred[j], arPred[i], Relation.COMMSUCC);
                    }
                }
            }
            //common predecessor
            arSucc = tu.getSucc();
            for(int i=0; i<arSucc.length; i++)
            {
                for(int j=i+1; j<arSucc.length; j++)
                {
                    //#
                    if (isDependent(arSucc[i], arSucc[j]))
                    {
                        //increase <| tasks
                        addRelation(arSucc[i], arSucc[j], Relation.COMMPRED);
                        addRelation(arSucc[j], arSucc[i], Relation.COMMPRED);
                    }
                }
            }
        }
	}

	public void clear()
    {
        htRelations.clear();
        adjustSharpRelation.clear();
    }

    //get the routing type of task t
    public int getType(String t)
    {
        int type = 0;

        TaskUnit tuT = (TaskUnit)htRelations.get(t);
        String[] arPred = tuT.getPred();
        String[] arSucc = tuT.getSucc();

        //And Split
        for(int i=0; i<arSucc.length; i++)
        {
            String ti = arSucc[i];
            for(int j=i+1; j<arSucc.length; j++)
            {
                String tj = arSucc[j];
                int rel = getRelation(ti, tj);
                if((rel & Relation.PARALLEL) > 0)
                {
                    type |= Relation.ANDSPLIT;
                }
            }
        }

        //And Join/Or Join with Different Output Tasks
        for(int i=0; i<arPred.length; i++)
        {
            String ti = arPred[i];
            for(int j=i+1; j<arPred.length; j++)
            {
                String tj = arPred[j];
                int rel = getRelation(ti, tj);
                //And Join
                if((rel & Relation.PARALLEL) > 0)
                {
                    type |= Relation.ANDJOIN;
                }
                //Or Join with Different Output Tasks
                if((rel & Relation.COMMSUCC) > 0 && hasDiffOut(ti, tj))
                {
                    type |= Relation.ORJOINDIFFOUT;
                }
            }
        }

        String[] arRela = tuT.getRela();
        for(int i=0; i<arRela.length; i++)
        {
            //Or Split
            if((tuT.getRela(arRela[i]) & Relation.COMMPRED) > 0)
            {
                type |= Relation.ORSPLIT;
            }
            //Or Join
            if((tuT.getRela(arRela[i]) & Relation.COMMSUCC) > 0)
            {
                type |= Relation.ORJOIN;
            }
        }

        return type;
    }

    /**
     * hasDiffOut
     *
     * @param ti String
     * @param tj String
     * @return boolean
     */
    private boolean hasDiffOut(String ti, String tj)
    {
        String[] arSuccTi = getSucc(ti);
        String[] arSuccTj = getSucc(tj);
        ArrayList alUnion = new ArrayList();
        for(int i=0; i<arSuccTi.length; i++)
        {
            if(!alUnion.contains(arSuccTi[i]))
            {
                alUnion.add(arSuccTi[i]);
            }
        }
        for(int i=0; i<arSuccTj.length; i++)
        {
            if(!alUnion.contains(arSuccTj[i]))
            {
                alUnion.add(arSuccTj[i]);
            }
        }
        return alUnion.size() != arSuccTi.length || alUnion.size() != arSuccTj.length;

    }

    //add a before task t2 of t1
    public void addBefore(String t1, String t2)
    {
        TaskUnit tuT1 = (TaskUnit)htRelations.get(t1);
        if(tuT1 == null)
        {
            tuT1 = new TaskUnit();
            htRelations.put(t1, tuT1);
        }
        tuT1.addPred(t2);
    }

    //add a after task t2 of t1
    public void addAfter(String t1, String t2)
    {
        TaskUnit tuT1 = (TaskUnit)htRelations.get(t1);
        if(tuT1 == null)
        {
            tuT1 = new TaskUnit();
            htRelations.put(t1, tuT1);
        }
        tuT1.addSucc(t2);
    }

    //remove a after task t2 of t1
    public void removeAfter(String t1, String t2)
    {
        TaskUnit tuT1 = (TaskUnit)htRelations.get(t1);
        if(tuT1 != null)
        {
            tuT1.removeSucc(t2);
        }
    }

    //remove a before task t2 of t1;
    public void removeBefore(String t1, String t2)
    {
        TaskUnit tuT1 = (TaskUnit)htRelations.get(t1);
        if(tuT1 != null)
        {
            tuT1.removePred(t2);
        }
    }

    //remove all befores included in an ArrayList
    public void removeBefore(String t1, ArrayList alBefore)
    {
        TaskUnit tuT1 = (TaskUnit)htRelations.get(t1);
        if(tuT1 != null)
        {
            tuT1.removePred(alBefore);
        }
    }

    //remove all successive tasks in an ArrayList
    public void removeAfter(String t1, ArrayList alRelation)
    {
        TaskUnit tuT1 = (TaskUnit)htRelations.get(t1);
        if(tuT1 != null)
        {
            tuT1.removeRela(alRelation);
        }
    }

    //remove all successive tasks with shortloop relation
    public void removeShortLoop(String t1)
    {
        TaskUnit tuT1 = (TaskUnit)htRelations.get(t1);
        if(tuT1 != null)
        {
            tuT1.removeShortLoop();
        }
    }

    //get output task set of one self loop task
    public String[] getAfter(String t1)
    {
        TaskUnit tuT1 = (TaskUnit)htRelations.get(t1);
        if(tuT1 == null)
        {
            return new String[0];
        }
        else
        {
            return tuT1.getRela();
        }
    }

    //get output task set of one self loop task
    public String[] getSucc(String t1)
    {
        TaskUnit tuT1 = (TaskUnit)htRelations.get(t1);
        if(tuT1 == null)
        {
            return new String[0];
        }
        else
        {
            return tuT1.getSucc();
        }
    }

    //get input task set of one task
    public String[] getBefore(String t1)
    {
        TaskUnit tuT1 = (TaskUnit)htRelations.get(t1);
        if(tuT1 == null)
        {
            return new String[0];
        }
        else
        {
            return tuT1.getPred();
        }
    }

    //add a kind of relation between t1 and t2
    public void addRelation(String t1, String t2, int rel)
    {
        TaskUnit tuT1 = (TaskUnit)htRelations.get(t1);
        if(tuT1 == null)
        {
            tuT1 = new TaskUnit();
            htRelations.put(t1, tuT1);
        }
        tuT1.addRela(t2, rel);
    }

    public void removeRelation(String t1, String t2, int rel)
    {
        TaskUnit tuT1 = (TaskUnit)htRelations.get(t1);
        if(tuT1 != null)
        {
            tuT1.removeRela(t2, rel);
        }
    }

    //get the relation between t1 and t2
    public int getRelation(String t1, String t2)
    {
        TaskUnit tuT1 = (TaskUnit)htRelations.get(t1);
        if(tuT1 == null)
        {
            return 0;
        }
        return tuT1.getRela(t2);
    }

    //a>b
    public boolean isBefore(String t1, String t2)
    {
        return (getRelation(t1, t2) & Relation.BEFORE) > 0;
    }

    //aa
    public boolean isSelfLoop(String t)
    {
        return (getRelation(t, t) & Relation.BEFORE) > 0;
    }

    //a^b (aba)
    public boolean isShortLoop(String t1, String t2)
    {
        return (getRelation(t1, t2) & Relation.SHORTLOOP) > 0;
    }

    //a->b, self loop
    public boolean isLoopSucc(String t1, String t2)
    {
        return isBefore(t1, t2) && !isBefore(t2, t1) || isShortLoop(t1, t2) || isShortLoop(t2, t1);
    }

    //a->b, normal
    public boolean isNormSucc(String t1, String t2)
    {
        return (getRelation(t1, t2) & Relation.SUCCESSIVE) > 0;
    }

    //a#b
    public boolean isDependent(String t1, String t2)
    {
//    	if ((t1.equals("B\0complete") && t2.equals("D\0complete")) ||
//        		(t1.equals("D\0complete") && t2.equals("B\0complete")) ||
//        		(t1.equals("F\0complete") && t2.equals("D\0complete")) ||
//        		(t1.equals("D\0complete") && t2.equals("F\0complete")) || 
//        		(t1.equals("C\0complete") && t2.equals("H\0complete")) ||
//        		(t1.equals("H\0complete") && t2.equals("C\0complete")) ||
//        		(t1.equals("H\0complete") && t2.equals("G\0complete")) ||
//        		(t1.equals("G\0complete") && t2.equals("H\0complete"))  
//        			)
//        		return true;
		Set<String> test = new UnifiedSet<String>();
    	test.add(t1);
    	test.add(t2);
    	if (adjustSharpRelation.contains(test))
    		return true;
        return !(isBefore(t1, t2) || isBefore(t2, t1));
    }

    //a||b, self loop
    public boolean isLoopPara(String t1, String t2)
    {
        return isBefore(t1, t2) && isBefore(t2, t1) && !(isShortLoop(t1, t2) || isShortLoop(t2, t1));
    }

    //a||b, normal
    public boolean isNormPara(String t1, String t2)
    {
        return (getRelation(t1, t2) & Relation.PARALLEL) > 0;
    }

    //a<|b
    public boolean isCommPred(String t1, String t2)
    {
        return (getRelation(t1, t2) & Relation.COMMPRED) > 0;
    }

    //a|>b
    public boolean isCommSucc(String t1, String t2)
    {
        return (getRelation(t1, t2) & Relation.COMMSUCC) > 0;
    }

    //a>>b
    public boolean isReachable(String t1, String t2)
    {
        int rel = getRelation(t1, t2);
        return (rel & Relation.BEFORE) == 0 && (rel & Relation.REACHABLE) > 0;
    }

    //a || b or a->b or a>>b
    public boolean isParaOrFollow(String t1, String t2)
    {
        int rel = getRelation(t1, t2);
        return (rel & (Relation.PARALLEL | Relation.SUCCESSIVE | Relation.REACHABLE)) > 0;
    }

    //a->b or a>>b
    public boolean isFollowedBy(String t1, String t2)
    {
        return isNormSucc(t1, t2) || isReachable(t1, t2);
    }

    public String[] getComm(String task)
    {
        TaskUnit tuT1 = (TaskUnit)htRelations.get(task);
        if(tuT1 == null)
        {
            return new String[0];
        }
        else
        {
            return tuT1.getComm();
        }
    }

    public String[] getCommPred(String task)
    {
        TaskUnit tuT1 = (TaskUnit)htRelations.get(task);
        if(tuT1 == null)
        {
            return new String[0];
        }
        else
        {
            return tuT1.getCommPred();
        }
    }

    public String[] getCommSucc(String task)
    {
        TaskUnit tuT1 = (TaskUnit)htRelations.get(task);
        if(tuT1 == null)
        {
            return new String[0];
        }
        else
        {
            return tuT1.getCommSucc();
        }
    }

    /**
     * *t, t*, ->, ||, <| and |>
     */
    public void induceAdvOrder()
    {
        //enumerate all the tasks in hashtable
        Enumeration keys = htRelations.keys();
        while(keys.hasMoreElements())
        {
            String t = (String)keys.nextElement();
            TaskUnit tu = (TaskUnit)htRelations.get(t);

            //step 1: *t, t*, ->, ||

            //decrease input tasks and increase parallel tasks
            String[] arPred = tu.getPred();
            for(int i=0; i<arPred.length; i++)
            {
                //||
                if(isLoopPara(arPred[i], t))
                {
                    //decrease input tasks
                    tu.removePred(arPred[i]);
                    //increase parallel tasks
                    tu.addPara(arPred[i]);
                    //record || relation
                    tu.addRela(arPred[i], Relation.PARALLEL);
                }
            }

            //increase output tasks and increase parallel tasks
            String[] arSucc = tu.getRela();
            for(int i=0; i<arSucc.length; i++)
            {
                //||
                if(isLoopPara(t, arSucc[i]))
                {
                    //increase parallel tasks
                    tu.addPara(arSucc[i]);
                    //record || relation
                    tu.addRela(arSucc[i], Relation.PARALLEL);
                }
                else if(isBefore(t, arSucc[i]))
                {
                    //increase output tasks
                    tu.addSucc(arSucc[i]);
                    //record -> relation
                    tu.addRela(arSucc[i], Relation.SUCCESSIVE);
                }
            }

            //step 2: <| and |>
            //common successor
            arPred = tu.getPred();
            for(int i=0; i<arPred.length; i++)
            {
                for(int j=i+1; j<arPred.length; j++)
                {
                    //#
                    if (isDependent(arPred[i], arPred[j]))
                    {
                        //increase |> tasks
                        addRelation(arPred[i], arPred[j], Relation.COMMSUCC);
                        addRelation(arPred[j], arPred[i], Relation.COMMSUCC);
                    }
                }
            }
            //common predecessor
            arSucc = tu.getSucc();
            for(int i=0; i<arSucc.length; i++)
            {
                for(int j=i+1; j<arSucc.length; j++)
                {
                    //#
                    if (isDependent(arSucc[i], arSucc[j]))
                    {
                        //increase <| tasks
                        addRelation(arSucc[i], arSucc[j], Relation.COMMPRED);
                        addRelation(arSucc[j], arSucc[i], Relation.COMMPRED);
                    }
                }
            }
        }
    }
    
    /**
     * *t, t*, ->, ||
     */
    public void induceSucParOrder()
    {
        //enumerate all the tasks in hashtable
        Enumeration keys = htRelations.keys();
        while(keys.hasMoreElements())
        {
            String t = (String)keys.nextElement();
            TaskUnit tu = (TaskUnit) htRelations.get(t);

            //step 1: *t, t*, ->, ||

            //decrease input tasks and increase parallel tasks
            String[] arPred = tu.getPred();
            for(int i=0; i<arPred.length; i++)
            {
                //||
                if(isLoopPara(arPred[i], t))
                {
                    //decrease input tasks
                    tu.removePred(arPred[i]);
                    //increase parallel tasks
                    tu.addPara(arPred[i]);
                    //record || relation
                    tu.addRela(arPred[i], Relation.PARALLEL);
                }
            }

            //increase output tasks and increase parallel tasks
            String[] arSucc = tu.getRela();
            for(int i=0; i<arSucc.length; i++)
            {
                //||
                if(isLoopPara(t, arSucc[i]))
                {
                    //increase parallel tasks
                    tu.addPara(arSucc[i]);
                    //record || relation
                    tu.addRela(arSucc[i], Relation.PARALLEL);
                }
                else if(isBefore(t, arSucc[i]))
                {
                    //increase output tasks
                    tu.addSucc(arSucc[i]);
                    //record -> relation
                    tu.addRela(arSucc[i], Relation.SUCCESSIVE);
                }
            }
          
        }
    }

    /**
     * isFitableAB
     *
     * @param alA ArrayList
     * @param alB ArrayList
     * @return boolean
     */
    public boolean isFitableAB(ArrayList alA, ArrayList alB)
    {
        ArrayList alCommPredA = getCommPred(alA);
        ArrayList alCommPredB = getCommPred(alB);
        for(int i=0; i<alA.size(); i++)
        {
            String a = (String)alA.get(i);
            if(!alCommPredB.contains(a))
            {
                return false;
            }
        }
        for(int i=0; i<alB.size(); i++)
        {
            String b = (String)alB.get(i);
            if(!alCommPredA.contains(b))
            {
                return false;
            }
        }

        return true;
    }

    private ArrayList getCommPred(ArrayList alTask)
    {
        ArrayList alCommPred = new ArrayList();
        for(int i=0; i<alTask.size(); i++)
        {
            String t = (String)alTask.get(i);
            TaskUnit tu = (TaskUnit)htRelations.get(t);
            alCommPred.addAll(tu.getCommPredAsArray());
        }

        return alCommPred;
    }

    /**
     * calculatePrime
     *
     * @param alA ArrayList
     * @param alB ArrayList
     * @return ArrayList
     */
    public ArrayList calculatePrime(ArrayList alA, ArrayList alB)
    {
        ArrayList alAprime = new ArrayList();
        ArrayList alCommPredB = getCommPred(alB);
        for(int i=0; i<alCommPredB.size(); i++)
        {
            //t<|bj
            String t = (String)alCommPredB.get(i);
            //t not in A
            if(!alA.contains(t))
            {
                //exist ai->t or ai>>t
                for(int j=0; j<alA.size(); j++)
                {
                    String ai = (String)alA.get(j);
                    if(isFollowedBy(ai, t))
                    {
                        alAprime.add(t);
                    }
                }
            }
        }

        return alAprime;
    }

    /**
     * calculateInput
     *
     * @param alTask ArrayList
     * @return ArrayList the set of input tasks
     */
    public ArrayList calculateInput(ArrayList alTask)
    {
        ArrayList alInput = new ArrayList();
        for(int i=0; i<alTask.size(); i++)
        {
            String t = (String)alTask.get(i);
            String[] arInput = getBefore(t);
            for(int j=0; j<arInput.length; j++)
            {
                if(!alInput.contains(arInput[j]))
                {
                    alInput.add(arInput[j]);
                }
            }
        }

        return alInput;
    }

    /**
     * calculateInput
     *
     * @return ArrayList
     */
    public ArrayList calculateInput(String task)
    {
        ArrayList alTask = new ArrayList();
        alTask.add(task);
        return calculateInput(alTask);
    }

	public ArrayList<String> getAllTasks() {
		ArrayList<String> result = new ArrayList<String>();
		Enumeration<String> en = htRelations.keys();
		while (en.hasMoreElements())
		{
			result.add(en.nextElement());
		}
		return result;
	}
}

class TaskUnit
{
    //input task set
    ArrayList alPred = new ArrayList();
    //output task set
    ArrayList alSucc = new ArrayList();
    //parallel task set
    ArrayList alPara = new ArrayList();
    //relation vector
    Hashtable htRela = new Hashtable();

    public TaskUnit()
    {
    }

    public void addPred(String predTask)
    {
        if(!alPred.contains(predTask))
        {
            alPred.add(predTask);
        }
    }

    public void removePred(String predTask)
    {
        alPred.remove(predTask);
    }

    public void removePred(ArrayList alPredTask)
    {
        alPred.removeAll(alPredTask);
    }

    public void removeRela(ArrayList alRelaTask)
    {
        for(int i=0; i<alRelaTask.size(); i++)
        {
            htRela.remove(alRelaTask.get(i));
        }
    }

    public void removeShortLoop()
    {
        Enumeration keys = htRela.keys();
        while(keys.hasMoreElements())
        {
            String t = (String)keys.nextElement();
            Integer rel = (Integer)htRela.get(t);
            if((rel.intValue() & Relation.SHORTLOOP) > 0)
            {
                htRela.remove(t);
            }
        }
    }

    public String[] getRela()
    {
        ArrayList alAfter = new ArrayList();
        Enumeration keys = htRela.keys();
        while(keys.hasMoreElements())
        {
            alAfter.add(keys.nextElement());
        }

        String[] arAfter = new String[alAfter.size()];
        alAfter.toArray(arAfter);
        return arAfter;
    }

    public String[] getComm()
    {
        ArrayList alComm = new ArrayList();
        Enumeration keys = htRela.keys();
        while(keys.hasMoreElements())
        {
            String t = (String)keys.nextElement();
            Integer rel = (Integer)htRela.get(t);
            if((rel.intValue() & (Relation.COMMPRED | Relation.COMMSUCC)) > 0)
            {
                alComm.add(t);
            }
        }

        String[] arComm = new String[alComm.size()];
        alComm.toArray(arComm);
        return arComm;
    }

    public String[] getCommPred()
    {
        ArrayList alComm = getCommPredAsArray();

        String[] arComm = new String[alComm.size()];
        alComm.toArray(arComm);
        return arComm;
    }

    public ArrayList getCommPredAsArray()
    {
        ArrayList alComm = new ArrayList();
        Enumeration keys = htRela.keys();
        while(keys.hasMoreElements())
        {
            String t = (String)keys.nextElement();
            Integer rel = (Integer)htRela.get(t);
            if((rel.intValue() & (Relation.COMMPRED)) > 0)
            {
                alComm.add(t);
            }
        }

        return alComm;
    }

    public String[] getCommSucc()
    {
        ArrayList alComm = new ArrayList();
        Enumeration keys = htRela.keys();
        while(keys.hasMoreElements())
        {
            String t = (String)keys.nextElement();
            Integer rel = (Integer)htRela.get(t);
            if((rel.intValue() & (Relation.COMMSUCC)) > 0)
            {
                alComm.add(t);
            }
        }

        String[] arComm = new String[alComm.size()];
        alComm.toArray(arComm);
        return arComm;
    }

    public String[] getPred()
    {
        String[] arPred = new String[alPred.size()];
        alPred.toArray(arPred);
        return arPred;
    }

    public String[] getSucc()
    {
        String[] arSucc = new String[alSucc.size()];
        alSucc.toArray(arSucc);
        return arSucc;
    }

    public void addRela(String task, int rel)
    {
        Integer oldRel = (Integer)htRela.get(task);
        if(oldRel == null)
        {
            oldRel = new Integer(0);
        }
        oldRel = new Integer(oldRel.intValue() | rel);
        //store oldRel
        htRela.put(task, oldRel);
    }

    public void removeRela(String task, int rel)
    {
        Integer oldRel = (Integer)htRela.get(task);
        if(oldRel != null)
        {
            int iRel = oldRel.intValue();
            iRel -= rel;
            if(iRel == 0)
            {
                htRela.remove(task);
            }
            else
            {
                oldRel = new Integer(iRel);
                //store oldRel
                htRela.put(task, oldRel);
            }
        }
    }

    public int getRela(String task)
    {
        Integer oldRel = (Integer)htRela.get(task);
        if(oldRel == null)
        {
            return 0;
        }
        else
        {
            return oldRel.intValue();
        }
    }

    public void addPara(String paraTask)
    {
        if(!alPara.contains(paraTask))
        {
            alPara.add(paraTask);
        }
    }

    public void addSucc(String succTask)
    {
        if(!alSucc.contains(succTask))
        {
            alSucc.add(succTask);
        }
    }

    /**
     * removeSucc
     *
     * @param succ String
     */
    protected void removeSucc(String succ)
    {
        alSucc.remove(succ);
    }
}
