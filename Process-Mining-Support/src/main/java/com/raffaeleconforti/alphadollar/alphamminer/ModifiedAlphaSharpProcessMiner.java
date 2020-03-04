/***********************************************************
 *      This software is part of the ProM package          *
 *             http://www.processmining.org/               *
 *                                                         *
 *            Copyright (c) 2003-2006 TU/e Eindhoven       *
 *                and is licensed under the                *
 *            Common Public License, Version 1.0           *
 *        by Eindhoven University of Technology            *
 *           Department of Information Systems             *
 *                 http://is.tm.tue.nl                     *
 *                                                         *
 **********************************************************/

package com.raffaeleconforti.alphadollar.alphamminer;

import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import com.raffaeleconforti.heuristicsdollarminer.HeuristicsRelations;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.framework.log.*;
import org.processmining.framework.log.rfb.AuditTrailEntryImpl;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.framework.models.petrinet.Transition;
import org.processmining.mining.logabstraction.LogAbstraction;
import org.processmining.mining.logabstraction.LogAbstractionImpl;
import org.processmining.mining.logabstraction.LogRelations;
import org.processmining.mining.logabstraction.LogRelationsImpl;

import java.io.IOException;
import java.util.*;

public class ModifiedAlphaSharpProcessMiner {
    private LogRelations relations;
    private int nme; //Number of model elements
    private int nme_old; //old number of model elements
    
    private ArrayList<InvTask> invTasks; //Invisible tasks.
    
    private IntervalTasksSet intervalTasksSet;	//Pre post set for each task. 

    private NotNormalInvTaskSet notNormalInvTaskSet;		//to save the false dependencies  that are not normal. now only save the false relationship with the
    
    private ArrayList<AXYB> invTaskAXYB;				//The medecious relation founded in the project.
    
    private Map<String, Set<String>> reachableTasks;		//
    
    public String getName()
    {
        return "Alpha# algorithm plugin";
    }

    public PetriNet mine(LogReader log, LogRelations theRelations, boolean heuristics) {
        PetriNet petrinet;

        if (theRelations == null)
            return null;
        relations = theRelations;
        nme = relations.getLogEvents().size();
        nme_old = nme;

        //enumate all process instances
        addLoopRelation(log, 0);        //1.set relation.parallel based on the loop   //

        //add begin/end task
        addBeginEnd();                    //to find the begin / end invisible transitions.

        //find skip/redo/switch tasks
        ArrayList alFalseDep = new ArrayList();
        ArrayList alRedunDep = getFalseDep(alFalseDep, log);

        //add skip_redo_switch invisible tasks
        ArrayList alInvTask = new ArrayList();
        addSkipRedo(alFalseDep, alRedunDep, alInvTask);

        if (heuristics) {
            relations = HeuristicsRelations.simplifyRelations(log, relations);
        }

        ArrayList tuples = new ArrayList();
        for (int i = 0; i < nme; i++)
        {
            for (int j = 0; j < nme; j++)
            {
                if (relations.getCausalFollowerMatrix().get(i, j) == 0)
                    continue;

                IntArrayList A = new IntArrayList();
                A.add(i);

                // j is a causal follower of i
                IntArrayList B = new IntArrayList();
                B.add(j);
                // Now, we have a startingpoint to expand the tree,
                // since {i} -> {j}
                ExpandTree(tuples, A, B, 0, 0);
            }
            // In tuples, we now have a collection of ArrayList[2]'s each of which
            // contains information to build the places
        }

        petrinet = new PetriNet();
        // First, we can write all transitions
        for (int i = 0; i < nme; i++)
        {
            LogEvent e = relations.getLogEvents().getEvent(i);
            Transition t = new Transition(e, petrinet);
            petrinet.addTransition(t);
        }

        // Second, we can write all places (check for duplicates)
        RemoveDuplicates(tuples);
        for (int i = 0; i < tuples.size(); i++)
        {
            petrinet.addPlace("p" + i);
        }
        petrinet.addPlace("pstart");
        petrinet.addPlace("pend");

        // Third, we write all arcs not for one loops
        for (int i = 0; i < tuples.size(); i++)
        {
            IntArrayList[] tuple = ( (IntArrayList[]) (tuples.get(i)));
            for (int j = 0; j < tuple[0].size(); j++)
            {
                petrinet.addEdge(petrinet.findRandomTransition(relations.getLogEvents().getEvent(
                    tuple[0].
                    get(j))),
                                 petrinet.findPlace("p" + i));
            }
            for (int j = 0; j < tuple[1].size(); j++)
            {
                petrinet.addEdge(petrinet.findPlace("p" + i),
                                 petrinet.findRandomTransition(relations.getLogEvents().getEvent(
                                     tuple[1].
                                     get(j))));
            }
        }

        for (int i = 0; i < nme; i++)
        {
            if (relations.getStartInfo().get(i) == 0)
                continue;
            petrinet.addEdge(petrinet.findPlace("pstart"),
                             petrinet.findRandomTransition(relations.getLogEvents().getEvent(i)));
        }
        for (int i = 0; i < nme; i++)
        {
            if (relations.getEndInfo().get(i) == 0)
                continue;
            petrinet.addEdge(petrinet.findRandomTransition(relations.getLogEvents().getEvent(i)),
                             petrinet.findPlace("pend"));
        }
        for (int i = nme_old; i < nme; i++)
        {
            Transition t = petrinet.findRandomTransition(relations.getLogEvents().getEvent(i));
            if(i >= nme_old)
                t.setLogEvent(null);
        }

        // Now write clusters.
        petrinet.makeClusters();

        for (int i = relations.getLogEvents().size() - 1; i >= nme_old; i--)
            relations.getLogEvents().remove(i);

        return petrinet;
    }

    
    private void induceReachable(LogReader log) 
    {
    	reachableTasks = new UnifiedMap<String, Set<String>>();
    	for (int i=0; i<relations.getLogEvents().size(); i++)
    	{
    		String taskName = relations.getLogEvents().get(i).getModelElementName();
    		Set<String> set = new UnifiedSet<String>();
    		set.add(taskName);
    		reachableTasks.put(taskName, set);
    	}    	    	
    	try {
			for (ProcessInstance processInstance	:	log.getInstances())
			{			
				ArrayList<AuditTrailEntryList> ateLists = addAvailableBeginEndInvTask(processInstance.getAuditTrailEntryList());
				
				for (AuditTrailEntryList ateList	:	ateLists)
				{						
					for (int i=0; i<ateList.size()-1; i++)
					{
						try 
						{
							String taskName = ateList.get(i).getName();
							Set<String> set = reachableTasks.get(taskName);
							for (int j=i+1; j<ateList.size(); j++)
							{
								String taskName2 = ateList.get(j).getName();
								set.add(taskName2);
							}
						} 
						catch (IndexOutOfBoundsException e)
						{				
							e.printStackTrace();
						} 
						catch (IOException e)
						{				
							e.printStackTrace();
						}
						
					}
				}
			}
    	} catch (IndexOutOfBoundsException | IOException e)
    	{    		
			e.printStackTrace();
		}
	}
	
    /**
     * 修改一个trace,使其包含begin/end的invTask的信息(可能要修改成多个)
     * @param auditTrailEntryList
     * @return
     * @throws IOException
     * @throws IndexOutOfBoundsException
     */
	private ArrayList<AuditTrailEntryList> addAvailableBeginEndInvTask(
			AuditTrailEntryList auditTrailEntryList) throws IndexOutOfBoundsException, IOException {
		ArrayList<AuditTrailEntryList> result = new ArrayList<AuditTrailEntryList>();
		
		ArrayList<InvTask> availableBeginTask = new ArrayList<InvTask>();
		ArrayList<InvTask> availableEndTask = new ArrayList<InvTask>();
		int size = auditTrailEntryList.size();
		String beginName = auditTrailEntryList.get(0).getName() + "\0" + auditTrailEntryList.get(0).getType();
		String endName = auditTrailEntryList.get(size-1).getName() + "\0" + auditTrailEntryList.get(size-1).getType();
		for (InvTask invTask	:	invTasks)
		{
			if (invTask.type == InvTask.START)
			{
				if (invTask.suc.contains(beginName))
					availableBeginTask.add(invTask);
			}
			else
			{
				if (invTask.pre.contains(endName))
					availableEndTask.add(invTask);
			}
		}
		if (availableBeginTask.size() == 0)
		{
			InvTask a = new InvTask(null, -1);
			availableBeginTask.add(a);
		}
		if (availableEndTask.size() == 0)
		{
			InvTask b = new InvTask(null, -1);
			availableEndTask.add(b);
		}
		for (InvTask beginTask	:	availableBeginTask)
			for (InvTask endTask	:	availableEndTask)
			{
				AuditTrailEntryList ateListNew = auditTrailEntryList.cloneInstance();
				if (beginTask.type != -1)
				{
					AuditTrailEntry ateBeginNew = new AuditTrailEntryImpl();
					ateBeginNew.setName(beginTask.taskName);
					ateBeginNew.setType("auto");
					ateListNew.insert(ateBeginNew, 0);
				}
				
				if (endTask.type != -1)
				{
					AuditTrailEntry ateEndNew = new AuditTrailEntryImpl();
					ateEndNew.setName(endTask.taskName);
					ateEndNew.setType("auto");
					ateListNew.append(ateEndNew);
				}				
				result.add(ateListNew);
			}
		return result;
	}

	private void addSkipRedo(ArrayList alFalseDep, ArrayList alRedunDep, ArrayList alInvTask)
    {
        //add skip_redo invisible tasks
        Hashtable htSucc = new Hashtable();
        Hashtable htPred = new Hashtable();
        for (int i = 0; i < alFalseDep.size(); i++)
        {
            PredSucc ps = (PredSucc) alFalseDep.get(i);
            //construct all the succ places associated with invisible tasks
            if (htSucc.get("" + ps.getSucc()) == null)
            {
                ArrayList alSuccPred = genInputPlaces(ps.getSucc());
                htSucc.put("" + ps.getSucc(), alSuccPred);
            }
            //construct all the pred places associated with invisible tasks
            if (htPred.get("" + ps.getPred()) == null)
            {
                ArrayList sarPredSucc = genOutputPlaces(ps.getPred());
                htPred.put("" + ps.getPred(), sarPredSucc);
            }
        }
        for (int i = 0; i < alRedunDep.size(); i++)
        {
            PredSucc ps = (PredSucc) alRedunDep.get(i);
            //construct all the succ places associated with invisible tasks
            if (htSucc.get("" + ps.getSucc()) == null)
            {
                ArrayList alSuccPred = genInputPlaces(ps.getSucc());
                htSucc.put("" + ps.getSucc(), alSuccPred);
            }
            //construct all the pred places associated with invisible tasks
            if (htPred.get("" + ps.getPred()) == null)
            {
                ArrayList sarPredSucc = genOutputPlaces(ps.getPred());
                htPred.put("" + ps.getPred(), sarPredSucc);
            }
        }
        int nInvNum = 0;
        //construct all the invsible tasks for normal false dependencies
        for (int i = 0; i < alFalseDep.size(); i++)
        {
        	//Let'check whether a falsedep is not turned into a inv task.
        	//we should revert a invisible task into its falsedep,hehe.
        	boolean isTurned = false;
            PredSucc ps = (PredSucc) alFalseDep.get(i);
            ArrayList sarPred = (ArrayList) htPred.get("" + ps.getPred());
            ArrayList sarSucc = (ArrayList) htSucc.get("" + ps.getSucc());
            for (int j = 0; j < sarPred.size(); j++)
            {
                DoubleSet dsPred = (DoubleSet) sarPred.get(j);
                for (int k = 0; k < sarSucc.size(); k++)
                {
                    DoubleSet dsSucc = (DoubleSet) sarSucc.get(k);
                    //create a new invisible task
                    IntArrayList sarPredSucc = dsPred.getB();
                    IntArrayList sarSuccPred = dsSucc.getA();
                    IntArrayList sarPredPred = dsPred.getA();
                    IntArrayList sarSuccSucc = dsSucc.getB();
                    //should create a new invisible task
                    if (!existPara(sarPredSucc, sarSuccPred) && !existPara(sarPredPred, sarSuccSucc))
                    {
                    	if (notNormalInvTaskSet.contains(ps))
                    	{
                    		int a,b,x,y;
                    		a = notNormalInvTaskSet.get(ps).getA();
                    		b = notNormalInvTaskSet.get(ps).getB();
                    		x = notNormalInvTaskSet.get(ps).getX();
                    		y = notNormalInvTaskSet.get(ps).getY();
                    		if ((!sarPredPred.contains(a) && sarPredPred.size() != 0) ||
                    			(!sarPredSucc.contains(x)  && sarPredSucc.size() != 0)||
                    			(!sarSuccPred.contains(y)  && sarSuccPred.size() != 0)||
                    			(!sarSuccSucc.contains(b) && sarSuccSucc.size() != 0))
                    			continue;
                    	}
                    	
                        String t = "__skip_redo_" + (nInvNum + 1) + "__";
                        InvisibleTask it = new InvisibleTask(t);
                        it.addPred(dsPred);
                        it.addSucc(dsSucc);
                        isTurned = true;
                        //record the new invisible task
                        if (addInvTask(alInvTask, it, alFalseDep))
                        {
                        	InvTask invTask = new InvTask(t,InvTask.SKIP_REDO_SWITCH);
                        	for (int m=0; m<sarPredPred.size(); m++)
                        	{
                        		int pos = sarPredPred.get(m);
                            	String preTaskName = relations.getLogEvents().get(pos).getModelElementName() +"\0" + relations.getLogEvents().get(pos).getEventType() ;
                            	invTask.addPreTask(preTaskName);
                        	}
                        	for (int m=0; m<sarSuccSucc.size(); m++)
                        	{
                        		int pos = sarSuccSucc.get(m);
                            	String sucTaskName = relations.getLogEvents().get(pos).getModelElementName() +"\0" + relations.getLogEvents().get(pos).getEventType() ;
                            	invTask.addSucTask(sucTaskName);
                        	}
                        	invTasks.add(invTask);
                        	
                            nInvNum++;                            
                        }
                        
                    }
                }
            }
            if (!isTurned)
            {            	
        	
                relations.getCausalFollowerMatrix().set(ps.pred, ps.succ, 1);
                if(ps.pred == ps.succ)
                {
                    relations.getOneLengthLoopsInfo().set(ps.pred, 1);
                    relations.getParallelMatrix().set(ps.pred, ps.pred, 1);
                }
            }
        }
        //add invisible tasks for parallel redundant false dependency
        for (int i = 0; i < alRedunDep.size(); i++)
        {
            PredSucc ps = (PredSucc) alRedunDep.get(i);
            ArrayList sarPred = (ArrayList) htPred.get("" + ps.getPred());
            ArrayList sarSucc = (ArrayList) htSucc.get("" + ps.getSucc());
            for (int j = 0; j < sarPred.size(); j++)
            {
                DoubleSet dsPred = (DoubleSet) sarPred.get(j);
                for (int k = 0; k < sarSucc.size(); k++)
                {
                    DoubleSet dsSucc = (DoubleSet) sarSucc.get(k);
                    //create a new invisible task
                    IntArrayList sarPredPred = dsPred.getA();
                    IntArrayList sarPredSucc = dsPred.getB();
                    IntArrayList sarSuccPred = dsSucc.getA();
                    IntArrayList sarSuccSucc = dsSucc.getB();
                    //should create a new invisible task
                    if (!existPara(sarPredSucc, sarSuccPred) && !existPara(sarPredPred, sarSuccPred))
                    {
                    	if (notNormalInvTaskSet.contains(ps))
                    	{
                    		int a,b,x,y;
                    		a = notNormalInvTaskSet.get(ps).getA();
                    		b = notNormalInvTaskSet.get(ps).getB();
                    		x = notNormalInvTaskSet.get(ps).getX();
                    		y = notNormalInvTaskSet.get(ps).getY();
                    		if ((!sarPredPred.contains(a) && sarPredPred.size() != 0) ||
                    			(!sarPredSucc.contains(x) && sarPredPred.size() != 0) ||
                    			(!sarSuccPred.contains(y) && sarPredPred.size() != 0) ||
                    			(!sarSuccSucc.contains(b) && sarPredPred.size() != 0))
                    			continue;
                    	}
                        String t = "__skip_redo_" + (nInvNum + 1) + "__";
                        InvisibleTask it = new InvisibleTask(t);
                        it.addPred(dsPred);
                        it.addSucc(dsSucc);
                        //if exist a path between two places dsPred and dsSucc
                        if (!existPath(it, alInvTask))
                        {
                            //record the new invisible task
                            if (addInvTask(alInvTask, it, alFalseDep))
                            {
                            	InvTask invTask = new InvTask(t,InvTask.SKIP_REDO_SWITCH);
                            	for (int m=0; m<sarPredPred.size(); m++)
                            	{
                            		int pos = sarPredPred.get(m);
                                	String preTaskName = relations.getLogEvents().get(pos).getModelElementName() +"\0" + relations.getLogEvents().get(pos).getEventType() ;
                                	invTask.addPreTask(preTaskName);
                            	}
                            	for (int m=0; m<sarSuccSucc.size(); m++)
                            	{
                            		int pos = sarSuccSucc.get(m);
                                	String sucTaskName = relations.getLogEvents().get(pos).getModelElementName() +"\0" + relations.getLogEvents().get(pos).getEventType() ;
                                	invTask.addSucTask(sucTaskName);
                            	}
                            	invTasks.add(invTask);
                                nInvNum++;
                            }
                        }
                    }
                }
            }
        }
        
        //TODO at here we should check whether a pair of inv task is swtich parallel branches
        alInvTask = removeInvTaskForParallelBranches(alInvTask);
        
        //add successive relations between invisible tasks and visible tasks
        if (alInvTask.size() > 0)
        {
            int nmeNew = nme + alInvTask.size();
            DoubleMatrix1D dmOneLoop = DoubleFactory1D.sparse.make(nmeNew, 0);
            for (int i = 0; i < nme; i++)
                dmOneLoop.set(i, relations.getOneLengthLoopsInfo().get(i));
            DoubleMatrix2D dmCausal = DoubleFactory2D.sparse.make(nmeNew, nmeNew, 0);
            for (int i = 0; i < nme; i++)
                for (int j = 0; j < nme; j++)
                    dmCausal.set(i, j, relations.getCausalFollowerMatrix().get(i, j));
            DoubleMatrix2D dmParallel = DoubleFactory2D.sparse.make(nmeNew, nmeNew, 0);
            for (int i = 0; i < nme; i++)
                for (int j = 0; j < nme; j++)
                    dmParallel.set(i, j, relations.getParallelMatrix().get(i, j));
            DoubleMatrix1D dmStart = DoubleFactory1D.sparse.make(nmeNew, 0);
            for (int i = 0; i < nme; i++)
                dmStart.set(i, relations.getStartInfo().get(i));
            DoubleMatrix1D dmEnd = DoubleFactory1D.sparse.make(nmeNew, 0);
            for (int i = 0; i < nme; i++)
                dmEnd.set(i, relations.getEndInfo().get(i));
            LogEvents leEvents = relations.getLogEvents();

            //sequence relations between invisible tasks and visible tasks
            for (int i = 0; i < alInvTask.size(); i++)
            {
                InvisibleTask it = (InvisibleTask) alInvTask.get(i);
                String t = it.getName();
                int id = nme + i;
                it.setId(id);

                //add to log events
                LogEvent le = new LogEvent(t, "auto");
                leEvents.add(le);

                //add to relation matrix
                IntArrayList sarPred = it.getPredPred();
                for (int j = 0; j < sarPred.size(); j++)
                    dmCausal.set(sarPred.get(j), id, 1);
                IntArrayList sarSucc = it.getSuccSucc();
                for (int j = 0; j < sarSucc.size(); j++)
                    dmCausal.set(id, sarSucc.get(j), 1);
            }
            //create a totally new log relations object
            nme = nmeNew;
            relations = new LogRelationsImpl(dmCausal, dmParallel, dmEnd, dmStart, dmOneLoop, leEvents);
        }
        //add successive relations between invisible tasks
        for (int i = 0; i < alInvTask.size(); i++)
        {
            InvisibleTask iti = (InvisibleTask) alInvTask.get(i);
            int ti = iti.getId();
            for (int j = 0; j < alInvTask.size(); j++)
            {
                if (i == j)
                    continue;
                InvisibleTask itj = (InvisibleTask) alInvTask.get(j);
                int tj = itj.getId();
                DoubleSet[] sarPred = itj.getPred();
                for (int k = 0; k < sarPred.length; k++)
                {
                    if (iti.containedInSucc(sarPred[k]))
                    {
                        relations.getCausalFollowerMatrix().set(ti, tj, 1);
                        break;
                    }
                }
            }
        }
        //add parallel relations between invisible tasks
        for (int i = 0; i < alInvTask.size(); i++)
        {
            InvisibleTask iti = (InvisibleTask) alInvTask.get(i);
            DoubleSet[] sarPredI = iti.getPred();
            for (int j = i + 1; j < alInvTask.size(); j++)
            {
                InvisibleTask itj = (InvisibleTask) alInvTask.get(j);
                DoubleSet[] sarPredJ = itj.getPred();
                boolean isParaPred = true;
                boolean isParaSucc = true;
                for (int m = 0; m < sarPredI.length; m++)
                {
                    for (int n = 0; n < sarPredJ.length; n++)
                    {
                        if (isParaPred && !existPara(sarPredI[m].getA(), sarPredJ[n].getA()))
                            isParaPred = false;
                        if (isParaSucc && !existPara(sarPredI[m].getB(), sarPredJ[n].getB()))
                            isParaSucc = false;
                    }
                }
                if (isParaPred || isParaSucc)
                {
                    relations.getParallelMatrix().set(iti.id, itj.id, 1);
                    relations.getParallelMatrix().set(itj.id, iti.id, 1);
                }
            }
        }
        //add parallel relations between invisible tasks and visible tasks
        for (int i = 0; i < alInvTask.size(); i++)
        {
            InvisibleTask iti = (InvisibleTask) alInvTask.get(i);
            DoubleSet[] sarPredI = iti.getPred();
            //enumerate all tasks except invisible tasks
            for (int k = 0; k < nme - alInvTask.size(); k++)
            {
                IntArrayList ialT = new IntArrayList();
                ialT.add(k);
                boolean isParaPred = true;
                boolean isParaSucc = true;
                for (int m = 0; m < sarPredI.length; m++)
                {
                    if (isParaPred && !existPara(sarPredI[m].getA(), ialT))
                        isParaPred = false;
                    if (isParaSucc && !existPara(sarPredI[m].getB(), ialT))
                        isParaSucc = false;
                }
                if (isParaPred || isParaSucc)
                {
                    relations.getParallelMatrix().set(iti.id, k, 1);
                    relations.getParallelMatrix().set(k, iti.id, 1);
                }
            }
        }
    }
	
    private ArrayList<InvisibleTask> removeInvTaskForParallelBranches(ArrayList<InvisibleTask> alInvTask)
    {
    	Set<InvisibleTask> toDelete = new UnifiedSet<InvisibleTask>();
    	//1. find need-to-delete invisible task
    	for (int i=0; i<alInvTask.size(); i++)
    		for (int j=i+1; j<alInvTask.size(); j++)
    		{
    			InvisibleTask it1 = alInvTask.get(i);
    			InvisibleTask it2 = alInvTask.get(j);
    			//parallel criteria
    			//1.it1.getsucc == it2.getpre
    			IntArrayList a = null, b = null;
    			if (AlphaMMinerDataUtil.isSame(it1.getSucc(),it2.getPred()))
    			{
    				a = it1.getPredPred();
    				b = it2.getSuccSucc();
    			}
    			else if (AlphaMMinerDataUtil.isSame(it1.getPred(), it2.getSucc()))
    			{
    				a = it1.getSuccSucc();
    				b = it2.getPredPred();
    			}
    			if (a == null || b == null)
    				continue;    			    			
    			//2.it1.getprepre || it2.getsuccsucc
    			boolean isPar = true;
    			for (int m=0; m < a.size(); m++)
    				for (int n=0; n < b.size(); n++)
    				{
    					int am = a.get(m);
    					int bn = b.get(n);
    					if (relations.getParallelMatrix().get(am, bn) == 0)
    					{
    						isPar = false;
    						break;
    					}
    				}
    			if (isPar)
    			{
    				toDelete.add(it1);
    				toDelete.add(it2);
    			}    			
    		}    	
    	//2.1 delete invTasks
    	ArrayList<InvTask> toDeleteInv = new ArrayList<InvTask>();
    	for (InvisibleTask invisibleTask	:	toDelete)    	
    		for (InvTask invTask	:	invTasks)    		
    			if (invTask.taskName.equals(invisibleTask.getName()))    			
    				toDeleteInv.add(invTask);    			    		
    	
    	invTasks.removeAll(toDeleteInv);
    	//2.2 restore the casual..hehe
    	ArrayList<AXYB> toRestoreAXYB = new ArrayList<AXYB>();
    	for (InvTask invTask	:	toDeleteInv)
    	{
    		ArrayList<String> preNames = invTask.pre;
    		ArrayList<String> succNames = invTask.suc;
    		for (AXYB axyb	:	invTaskAXYB)
    		{
    			boolean isFoundA = false;
    			boolean isFoundB = false;
    			
    			for (String s	:	preNames)
    				if (s.startsWith(axyb.a))
    					isFoundA = true;
    			for (String s	:	succNames)
    				if (s.startsWith(axyb.b))
    					isFoundB = true;
    			if (isFoundA && isFoundB)
    				toRestoreAXYB.add(axyb);    				
    		}    			
    	}
    	UnifiedMap<String, Integer> namePos = new UnifiedMap<String, Integer>();
    	for(int i=0; i<relations.getLogEvents().size(); i++)
    	{
    		String taskName = relations.getLogEvents().get(i).getModelElementName();
    		namePos.put(taskName, i);
    	}
    	for (AXYB axyb	:	toRestoreAXYB)
    	{    		
    		int a = -1;
    		int b = -1;
    		a = namePos.get(axyb.a);
    		b = namePos.get(axyb.b);
    		if (a == -1 || b == -1)
    			continue;
    		
            relations.getCausalFollowerMatrix().set(a, b, 1);
            if(a == b)
            {
                relations.getOneLengthLoopsInfo().set(a, 1);
                relations.getParallelMatrix().set(a, a, 1);
            }            
    	}
    	//2.3 delete alInvTask
    	alInvTask.removeAll(toDelete);
    	return alInvTask;
	}

	private boolean existPath(InvisibleTask it, ArrayList alInvTask)
    {
        DoubleSet dsPred = it.getPred()[0];
        DoubleSet dsSucc = it.getSucc()[0];

        //test whether there is a path from dsPred to dsSucc
        ArrayList alPred = new ArrayList();
        alPred.add(dsPred);
        //record all the visited start place
        ArrayList alVisited = new ArrayList();
        while (alPred.size() != 0)
        {
            dsPred = (DoubleSet) alPred.remove(0);
            alVisited.add(dsPred);
            for (int i = 0; i < alInvTask.size(); i++)
            {
                InvisibleTask iti = (InvisibleTask) alInvTask.get(i);
                if (iti.containedInPred(dsPred))
                {
                    if (iti.containedInSucc(dsSucc))
                        return true;
                    DoubleSet[] sarSucc = iti.getSucc();
                    for (int j = 0; j < sarSucc.length; j++)
                        if (!alVisited.contains(sarSucc[j]))
                            alPred.add(sarSucc[j]);
                }
            }
        }

        return false;
        
    }

    private ArrayList genInputPlaces(int ith)
    {
        ArrayList tuples = new ArrayList();
        //get all predecessors
        IntArrayList ialPred = getPred(ith, false);
        //enumerate all the relevant causual dependencies
        for (int i = 0; i < ialPred.size(); i++)
        {
            int pred = ialPred.get(i);
            IntArrayList ialSucc = getSucc(pred, false);
            for (int j = 0; j < ialSucc.size(); j++)
            {
                int succ = ialSucc.get(j);
                IntArrayList A = new IntArrayList();
                A.add(pred);
                IntArrayList B = new IntArrayList();
                B.add(succ);
                ExpandTree(tuples, A, B, 0, 0);
            }
        }

        return tuple2Place(ith, tuples, 1);
    }

    private ArrayList genOutputPlaces(int ith)
    {
        ArrayList tuples = new ArrayList();
        //get all successors
        IntArrayList ialSucc = getSucc(ith, false);
        //enumerate all the relevant causual dependencies
        for (int i = 0; i < ialSucc.size(); i++)
        {
            int succ = ialSucc.get(i);
            IntArrayList ialPred = getPred(succ, false);
            for (int j = 0; j < ialPred.size(); j++)
            {
                int pred = ialPred.get(j);
                IntArrayList A = new IntArrayList();
                A.add(pred);
                IntArrayList B = new IntArrayList();
                B.add(succ);
                ExpandTree(tuples, A, B, 0, 0);
            }
        }

        return tuple2Place(ith, tuples, 0);
    }

    private ArrayList tuple2Place(int ith, ArrayList tuples, int pos)
    {
        RemoveDuplicates(tuples);
        ArrayList alPlaces = new ArrayList();
        for (int i = 0; i < tuples.size(); i++)
        {
            IntArrayList[] place = (IntArrayList[]) tuples.get(i);
            if (!place[pos].contains(ith))
                continue;
            DoubleSet ds = new DoubleSet();
            ds.addToA(place[0]);
            ds.addToB(place[1]);
            alPlaces.add(ds);
        }
        if(tuples.size() == 0 && pos == 0)//successor
        {
            DoubleSet ds = new DoubleSet();
            ds.addToA(ith);
            alPlaces.add(ds);
        }
        else if(tuples.size() ==0 && pos == 1)//predecessor
        {
            DoubleSet ds = new DoubleSet();
            ds.addToB(ith);
            alPlaces.add(ds);
        }
        return alPlaces;
    }

    private boolean checkISYCausalXAsequenceB(int a, int x, int y, int b, Set<Integer> misc,
    											ArrayList<ProcessInstance> selectedLogs,
    											ArrayList<Integer> selectedInfereneTask,
    											LogReader log    											
    											)
    {
    	boolean isYcasualX, isAsequenceB;
		// if y\>x, check whether paralle mislead this.    					
		if (relations.getCausalFollowerMatrix().get(y, x) == 0 &&
                   relations.getParallelMatrix().get(y, x) == 0)
		{
			
			isYcasualX = false;
			
			// format: y->b --> a->x, the a redo to b.        					
			Set<Integer> betweenYX, betweenBA;
			betweenYX = intervalTasksSet.getBetween(y, x);
			betweenBA = intervalTasksSet.getBetween(b, a);
			//betweenYX = AlphaMMinerDataUtil.removeParallel(betweenYX,y,x,relations);
			betweenBA = AlphaMMinerDataUtil.removeParallel(betweenBA,b,a,relations);
			//1.betweenBA should belong to YX
			if (AlphaMMinerDataUtil.belongs(betweenBA,betweenYX))
			{
				misc = AlphaMMinerDataUtil.except(betweenYX,betweenBA);	//misc is the set of tasks which may be another parallel branch
				//make sure a,b should not be contained in the misc
				if (misc.contains(a))
					misc.remove(a);
				if (misc.contains(b))
					misc.remove(b);
				//2.check whether misc parallel with the betweenBA
				boolean checkPar;
				Set<Set<Integer>> content = intervalTasksSet.getContent(y,x);
				checkPar = checkParallel(misc,betweenBA, b, a, content);        						
				if (checkPar)
				{
					//3.check whether exist a path from y to x, it exists, then isYcasualX = true..
					boolean checkPath;
					selectedLogs = new ArrayList<ProcessInstance>();
					selectedInfereneTask = new ArrayList<Integer>();
					checkPath = checkExistPath(y,x,misc, log,selectedLogs,selectedInfereneTask);
					if (checkPath)
					{
						isYcasualX = true;		
						normalInvTag = false;
					}
				}
			}        					        					
		}    					
		else
		{
			isYcasualX = true;
		}    					
		// if a->b,check whether paralle mislead this
		
		if (relations.getCausalFollowerMatrix().get(a, b) ==0)
		{
			isAsequenceB = false;
			
			if (relations.getParallelMatrix().get(a, b) == 0)  // if parallel mislead this, the a and b should not be in the parallel relation.
			{
				
				// format: a->x --> y->b, the a skip to b.        					
				Set<Integer> betweenXY, betweenAB;
				betweenXY = intervalTasksSet.getBetween(x, y);
				betweenAB = intervalTasksSet.getBetween(a, b);
				betweenXY = AlphaMMinerDataUtil.removeParallel(betweenXY, y, x, relations);
				//betweenAB = AlphaMMinerDataUtil.removeParallel(betweenAB, a, b, relations);
				//1.betweenXY should belong to AB
				
				if (AlphaMMinerDataUtil.belongs(betweenXY,betweenAB))
				{
					misc = AlphaMMinerDataUtil.except(betweenAB,betweenXY);	//misc is the set of tasks which may be another parallel branch
					//make sure x,y should not be in the misc
					if (misc.contains(x))
						misc.remove(x);
					if (misc.contains(y))
						misc.remove(y);
					//2.check whether misc parallel with the betweenBA
					boolean checkPar;					
					checkPar = checkParallel(misc,betweenXY,x,y,null);        						
					if (checkPar)
					{
						//3.check whether exist a path from a to b, it exists, then isYcasualX = true..
						boolean checkPath;	
						selectedLogs = new ArrayList<ProcessInstance>();
						selectedInfereneTask = new ArrayList<Integer>();
						checkPath = checkExistPath(a,b,misc, log, selectedLogs,selectedInfereneTask);
						if (checkPath)
						{							
							isAsequenceB = true;
							normalInvTag = false;
						}
							
					}
				}
				
			}
		}
		else
		{
			isAsequenceB = true;
		} 
		
		//new added:
		// if the relation is REDO, then the x should be reachable with y.
		String taskNameA =  relations.getLogEvents().get(a).getModelElementName();
		String taskNameX =  relations.getLogEvents().get(x).getModelElementName();
		String taskNameY =  relations.getLogEvents().get(y).getModelElementName();
		String taskNameB =  relations.getLogEvents().get(b).getModelElementName();
		if (reachableTasks.get(taskNameB).contains(taskNameA) && !reachableTasks.get(taskNameY).contains(taskNameX))
			return false;
        return isAsequenceB && !isYcasualX;
    }

    boolean normalInvTag = true;
    
    private ArrayList getFalseDep(ArrayList alFalseDep, LogReader log)
    {
    	notNormalInvTaskSet = new NotNormalInvTaskSet(); 
    	Set<Integer> misc = new UnifiedSet<Integer>();
    	ArrayList<ProcessInstance> selectedLogs = new ArrayList<ProcessInstance>();
    	ArrayList<Integer> selectedInfereneTask = new ArrayList<Integer>();
    	for (int a=0; a<nme; a++)
    		for (int b=0;b<nme; b++)
    		{
    			IntArrayList ialSucc = getSucc(a, false);
    			IntArrayList iblPred = getPred(b, false);
    			for (int ax=0; ax<ialSucc.size(); ax++)    				
    				for (int by=0; by<iblPred.size(); by++)
    				{
    					//boolean normalInvTag = true;
    					int x = ialSucc.get(ax);
    					int y = iblPred.get(by);    					    					
    					//check whether a\||x, y\||b
    					if (relations.getParallelMatrix().get(a, y) != 0 ||
    						relations.getParallelMatrix().get(x, b) != 0)
    						continue;    					
    					if (a == y && x == b)
    						continue;
    					if (a == 3 && b == 4 && x == 0 && y == 6)
    					{
    						int ccc;
    						ccc = 1;
    					}

    					//TODO 如果a>>y, x\>>y,找到a->x',x'>>y,判断(a,x',y,b)是否是一组,避免过多的invisible task
    					String taskNameA = relations.getLogEvents().get(a).getModelElementName();
    					String taskNameX = relations.getLogEvents().get(x).getModelElementName();
    					String taskNameY = relations.getLogEvents().get(y).getModelElementName();
    					boolean isExistReplaceInvisibleTask = false;    					
    					if (reachableTasks.get(taskNameA).contains(taskNameY) && !reachableTasks.get(taskNameX).contains(taskNameY))
    					{    						
    						for (int i=0; i<relations.getLogEvents().size(); i++)
    						{
    							if (relations.getCausalFollowerMatrix().get(a, i) == 1)
    							{
    								//make sure the x and i share the same place.
    								if (relations.getParallelMatrix().get(i, x) == 1)    									
    									continue;
    								String taskNameI = relations.getLogEvents().get(i).getModelElementName();
    								//1.check i is reachable to y
    								//2.ai yb is ok
    								if (!reachableTasks.get(taskNameI).contains(taskNameY))
    									continue;
    								normalInvTag = true;
    								if (checkISYCausalXAsequenceB(a,i,y,b,misc,selectedLogs,selectedInfereneTask,log))
    								{
    									isExistReplaceInvisibleTask = true;
    									PredSucc ps = new PredSucc(a, b);    						
    		                             if (!alFalseDep.contains(ps))
    		                             {
    		                                 alFalseDep.add(ps); 
    		                                 //not normal
    		                                 if(normalInvTag == false)		//this one is normal, as this is done by elem
    		                                 {
    		                                	 ArrayList<Integer> beforeCausal= new ArrayList<Integer>();
    		                                	 ArrayList<Integer> endCausal = new ArrayList<Integer>();
    		                                	 ArrayList<Integer> sharpRelation = new ArrayList<Integer>();
    		                                	 getMiscRelation(a,b,selectedInfereneTask,selectedLogs,beforeCausal, endCausal,sharpRelation);
    		                                	 notNormalInvTaskSet.addNotNormalInvTask(ps, a, b, x, y,beforeCausal,endCausal,sharpRelation);
    		                                 }
    		                             }
    								}
    							}
    						}
    					}
    					//since exist replace invisible task, so we should not add the original a,x,y,b
    					if (isExistReplaceInvisibleTask)
    						continue;
    					//1.check isYcausalX, isAsequenceB   
    					normalInvTag = true;
    					boolean checkYcausalXAsequenceBOK = checkISYCausalXAsequenceB(a,x,y,b,misc,selectedLogs,selectedInfereneTask,log);	
    					//2.if check is ok, put into the result    					    					     					
    					if (checkYcausalXAsequenceBOK)
    					{
    						 PredSucc ps = new PredSucc(a, b);    
    						 //add the a,x,y,b into the invTaskAXYB
    						 
    						 String taskName_A = relations.getLogEvents().get(a).getModelElementName();
    						 String taskName_X = relations.getLogEvents().get(x).getModelElementName();
    						 String taskName_Y = relations.getLogEvents().get(y).getModelElementName();
    						 String taskName_B = relations.getLogEvents().get(b).getModelElementName();
    						 int type = getInvTaskType(a,x,y,b,log);
    						 AXYB axyb = new AXYB(taskName_A, taskName_X, taskName_Y, taskName_B,type);
    						 invTaskAXYB.add(axyb);
    						 
                             if (!alFalseDep.contains(ps))
                             {
                                 alFalseDep.add(ps); 
                                 if(normalInvTag == false)		//this one is normal, as this is done by elem
                                 {
                                	 ArrayList<Integer> beforeCausal= new ArrayList<Integer>();
                                	 ArrayList<Integer> endCausal = new ArrayList<Integer>();
                                	 ArrayList<Integer> sharpRelation = new ArrayList<Integer>();
                                	 getMiscRelation(a,b,selectedInfereneTask,selectedLogs,beforeCausal, endCausal,sharpRelation);
                                	 notNormalInvTaskSet.addNotNormalInvTask(ps, a, b, x, y,beforeCausal,endCausal,sharpRelation);
                                 }
                             }
    					}    					
    				}    			    			    		
    		}
    	//TODO
    	//we should check whether there exist a pair of invisible tasks, such that a parallel branch swich to the other parallel branch..
    	//if so, we should remove this pair of invisible tasks. since they could lead to the dead-lock(transfer tokens from one branch to the other one)
    	
    	//alFalseDep = reMoveInvisibleTaskForDeadLock(alFalseDep);
    	//finally, i give up remove the invisible task here, since the may be many false dependencies could combine into on inv task, so it is too early to remove the false dep here.
    	//we remove the invTask at the function addskipredo().
    	
        //remove all false dependencies due to skip/redo/switch invisible tasks
        for (int i = 0; i < alFalseDep.size(); i++)
        {
            PredSucc ps = (PredSucc) alFalseDep.get(i);
            relations.getCausalFollowerMatrix().set(ps.getPred(), ps.getSucc(), 0);
            if(ps.pred == ps.succ)
            {
                relations.getOneLengthLoopsInfo().set(ps.pred, 0);
                relations.getParallelMatrix().set(ps.pred, ps.pred, 0);
            }
        }
        //identify all the redundant false dependencies
        ArrayList alRedunDep = new ArrayList();
        for (int i = 0; i < alFalseDep.size(); i++)
        {
            PredSucc psi = (PredSucc) alFalseDep.get(i);
            IntArrayList ialPred = getPred(psi.getSucc(), false);
            for (int j = 0; j < ialPred.size(); j++)
            {
                int tj = ialPred.get(j);
                for (int k = 0; k < alFalseDep.size(); k++)
                {
                    PredSucc psk = (PredSucc) alFalseDep.get(k);
                    if (tj == psk.getPred())
                    {
                        PredSucc psNew = new PredSucc(psi.getPred(), psk.getSucc());
                        if (!alRedunDep.contains(psNew))
                        {
                            alRedunDep.add(psNew);
                        }
                    }
                }
                for (int k = alRedunDep.size() - 1; k >= 0; k--)
                {
                    PredSucc psk = (PredSucc) alRedunDep.get(k);
                    if (tj == psk.getPred())
                    {
                        PredSucc psNew = new PredSucc(psi.getPred(), psk.getSucc());
                        if (!alRedunDep.contains(psNew))
                        {
                            alRedunDep.add(psNew);
                        }
                    }
                }
            }
        }
                        
        //remove all the redundant false dependencies
        alFalseDep.removeAll(alRedunDep);                
        
        //fix parallel matrix about length-one-loop tasks
        for(int i=0; i<nme; i++)
        {
            if(relations.getOneLengthLoopsInfo().get(i) > 0)
                relations.getParallelMatrix().set(i, i, 0);
        }
       
        return alRedunDep;
    }
    
    private ArrayList<PredSucc> reMoveInvisibleTaskForDeadLock(ArrayList<PredSucc> alFalseDep)
    {    	
    	Set<PredSucc> needToDelete = new UnifiedSet<PredSucc>();
    	//1. find pairs should be delete.
		int size = alFalseDep.size();
		for (int i=0; i<size; i++)
			for (int j=i+1; j<size; j++)
			{
				PredSucc ps1 = alFalseDep.get(i);
				PredSucc ps2 = alFalseDep.get(j);
				
				if (ps1.getPred() == ps1.getSucc() || ps2.getPred() == ps2.getSucc())
					continue;
				
				if (ps1.getPred() == ps2.getSucc() && ps2.getPred() == ps1.getSucc())
					continue;
				
				int a = -1, b =-1;
				if (ps1.getSucc() == ps2.getPred())
				{
					a = ps1.getPred();
					b = ps2.getSucc();					
				}
				else if (ps1.getPred() == ps2.getSucc())
				{
					a = ps1.getSucc();
					b = ps2.getSucc();
				}
				if (a == -1 || b == -1)
					continue;
				if (relations.getParallelMatrix().get(a, b) == 1)
				{
					needToDelete.add(ps1);
					needToDelete.add(ps2);
				}					
			}	
		//2. delete.
		//2.1 delete in notNormalInvTaskSet
		for (PredSucc ps	:	needToDelete)
		{
			notNormalInvTaskSet.deleteNotNormalInvTask(ps);
		}
		//2.2 delete ps in alFalseDep
		alFalseDep.removeAll(needToDelete);
		return alFalseDep;
	}

	private int getInvTaskType(int a, int x, int y, int b, LogReader log) {
    	List<ProcessInstance> processInstances = log.getInstances();
    	String taskNameA = relations.getLogEvents().get(a).getModelElementName();
    	String taskNameX = relations.getLogEvents().get(x).getModelElementName();
    	String taskNameY = relations.getLogEvents().get(y).getModelElementName();
    	String taskNameB = relations.getLogEvents().get(b).getModelElementName();
    	    	
    	int notBegin = -1;
    	int begin = 0;    	
    	int end = 1;
    	
    	int baStatus = notBegin;
    	int xyStatus = notBegin;
    	
    	for (ProcessInstance processInstance	:	processInstances)
    	{
    		AuditTrailEntryList ateList = processInstance.getAuditTrailEntryList();    		
    		for (int i=0; i<ateList.size(); i++)
    		{
    			try 
    			{
					AuditTrailEntry ate = ateList.get(i);					
					if (ate.getName().equals(taskNameA))
					{						
						if (baStatus == begin)
							baStatus = end;
					}
					if (ate.getName().equals(taskNameX))
					{
						if(xyStatus == notBegin)
							xyStatus = begin;
					}
					if (ate.getName().equals(taskNameY))
					{
						if (xyStatus == begin)
							xyStatus = end;
					}
					if (ate.getName().equals(taskNameB))
					{
						if (baStatus == notBegin)
							baStatus = begin;
					}
				} 
    			catch (IndexOutOfBoundsException | IOException e)
    			{				
					e.printStackTrace();
				}
    		}
    		if (baStatus == end || xyStatus == end)
    			break;
    		baStatus = notBegin;
    		xyStatus = notBegin;
    	}
    	
    	if (baStatus == end)
    	{
    		return AXYB.REDO;
    	}
    	else if (xyStatus == end)
    	{
    		return AXYB.SKIP;
    	}
    	return AXYB.SWITCH;
	}

	/*
     * To determine whether the situation meets d in non-free choice.
     */
    private void getMiscRelation(int a, int b,
                                 ArrayList<Integer> infereSet,
                                 ArrayList<ProcessInstance> logs, ArrayList<Integer> beforeCausal,
                                 ArrayList<Integer> afterCausal,
                                 ArrayList<Integer> sharpRelation) {
    	
    	int size = relations.getLogEvents().size();
    	int[][] relationmap;
    	relationmap = new int[size][];
    	for (int i=0; i<size; i++)
    	{
    		relationmap[i] = new int[size];
    		Arrays.fill(relationmap[i], 0);
    	}
    	Set<Integer> allTasks = new UnifiedSet<Integer>();
    	for (int i=0; i<relations.getLogEvents().size(); i++)
    		allTasks.add(i);
    	Set<Integer> availableTask = new UnifiedSet<Integer>();
    	
    	for (ProcessInstance processInstance	:	logs)
    	{
    		AuditTrailEntryList atelist = processInstance.getAuditTrailEntryList();
    		for (int i=0; i<atelist.size()-1; i++)
    		{
    			try 
    			{
					String taskName1 = atelist.get(i).getName();
					String taskName2 = atelist.get(i+1).getName();
					int taskPos1 =-1;
					int taskPos2 =-1;
					for (int j=0; j<relations.getLogEvents().size(); j++)
					{
						if (relations.getLogEvents().get(j).getModelElementName().equals(taskName1))
						{
							taskPos1 = j;							
						}
						if (relations.getLogEvents().get(j).getModelElementName().equals(taskName2))
						{
							taskPos2 = j;							
						}
					}
					if (taskPos1 == -1 || taskPos2 == -1)
						continue;
					relationmap[taskPos1][taskPos2] = 1;	
					availableTask.add(taskPos1);
					availableTask.add(taskPos2);
				} 
    			catch (IndexOutOfBoundsException e)
    			{				
					e.printStackTrace();
				} 
    			catch (IOException e)
    			{				
					e.printStackTrace();
				}    			    			
    		}
    	}
		Set<Integer> sharps = AlphaMMinerDataUtil.except(allTasks, availableTask);
		for (Integer i	:	sharps)
			sharpRelation.add(i);
    	for (Integer i	:	infereSet)
    	{
    		if (relations.getParallelMatrix().get(i, a) == 1)
    		{
    			//如果i,a并行，并且在日志里面IA并不并行，那么就需
    			if (relationmap[i][a] == 1 && relationmap[a][i] == 1)
    			{    				
    			}
    			else if (relationmap[i][a] == 1 && relationmap[a][i] == 0)
    			{    				
    			}
    			else if (relationmap[i][a] == 0 && relationmap[a][i] == 1)
    			{
    				afterCausal.add(i);
    			}
    			else if (relationmap[i][a] == 0 && relationmap[a][i] == 0)
    			{    				
    			}
    		}
    		if (relations.getParallelMatrix().get(i, b) == 1)
    		{
    			if (relationmap[i][b] == 1 && relationmap[b][i] == 1)
    			{    				
    			}
    			else if (relationmap[i][b] == 1 && relationmap[b][i] == 0)
    			{    		
    				beforeCausal.add(i);
    			}
    			else if (relationmap[i][b] == 0 && relationmap[b][i] == 1)
    			{    				
    			}
    			else if (relationmap[i][b] == 0 && relationmap[b][i] == 0)
    			{    				
    			}
    		}
    	}
	}

	private boolean checkExistPath(int a, int b, Set<Integer> misc, LogReader log, ArrayList<ProcessInstance> selectedProcess, ArrayList<Integer> selectedInterfereTask) {
		//search with deep first search;
    	int nobegin = 0;
		int begin = 1;
		int end = 2;
		ArrayList<Integer> traceTask = new ArrayList<Integer>();
		for (ProcessInstance processInstance: log.getInstances())
		{
			AuditTrailEntryList ateList	 = processInstance.getAuditTrailEntryList();		
			int tag = nobegin;
			traceTask.clear();		//in each trace
			for (int i=0; i<ateList.size(); i++)
			{
				try 
				{
					String posName = ateList.get(i).getName();
					int pos = -1;
					for (int m=0; m<relations.getNumberElements(); m++)
						if (relations.getLogEvents().get(m).getModelElementName().equals(posName))
						{
							pos = m;
							break;									
						}
					if (pos == -1)
						continue;
					if (pos == a)
					{
						tag = begin;
					} 					
					else if(tag == begin && pos == b)
					{
						tag = end;						
					}
					else if (tag == begin && !misc.contains(pos))
					{
						tag = nobegin;
						traceTask.clear();
					}
					else if (tag == begin && misc.contains(pos))
					{
						traceTask.add(pos);
					}
				} 
				catch (IndexOutOfBoundsException | IOException e)
				{
					e.printStackTrace();
				}						
			}
			if (tag == end)
			{
				selectedProcess.add(processInstance);
				selectedInterfereTask.addAll(traceTask);
			}							
		}
        return selectedProcess.size() > 0;
    }

	private boolean checkParallel(Set<Integer> misc, Set<Integer> betweenXY,
                                  int x, int y, Set<Set<Integer>> content) {
		//for each task in a misc, check whether a parallel with betweenXY,x,y
		Set<Integer> parallelTasks = new UnifiedSet<Integer>();
    	for (Integer task	:	misc)
    	{
    		if (relations.getParallelMatrix().get(task, x) == 0)
    			continue;
    		if (relations.getParallelMatrix().get(task, y) == 0)
    			continue;
    		for (Integer taskXY	:	betweenXY)
    			if (relations.getParallelMatrix().get(task, taskXY) == 0)
    				continue;
    		parallelTasks.add(task);
    	}
    	if (content == null)
    	{
            return misc.size() == parallelTasks.size();
    	}
    	else
    	{
	    	for (Set<Integer> set	:	content)
	    	{
	    		if (parallelTasks.containsAll(set))
	    			return true;
	    	}
	    	return false;
    	}    	
	}

	private void addLoopRelation(LogReader log, int minValue)
    {
        LogAbstraction abstraction = new LogAbstractionImpl(log);
        DoubleMatrix2D directSuccession, twoStepCloseIn;
        
		try {
			directSuccession = abstraction.getFollowerInfo(1);
			twoStepCloseIn = abstraction.getCloseInInfo(2);
			// First, build causal relations
			for (int i = 0; i < nme; i++) {
				for (int j = 0; j < nme; j++) {
					if (i == j) {
						continue;
					}
					if (relations.getOneLengthLoopsInfo().get(i) == 0 &&
							relations.getOneLengthLoopsInfo().get(j) == 0) {
						continue;
					}
					// No loop of length two:
					if ((directSuccession.get(i, j) > minValue) &&
							(directSuccession.get(j, i) <= minValue)) {
						relations.getCausalFollowerMatrix().set(i, j, 1);
					}
					// Loop of length two:
					if ((directSuccession.get(i, j) > minValue) &&
							(directSuccession.get(j, i) > minValue) &&
							((twoStepCloseIn.get(i, j) > 0) && (twoStepCloseIn.get(j, i) > 0))) {
						relations.getCausalFollowerMatrix().set(i, j, 1);
					}
				}
			}
			// Now, rebuild parallel relations
			for (int i = 0; i < nme; i++) {
				for (int j = 0; j < nme; j++) {
					if (i == j) {
						continue;
					}
					if (relations.getOneLengthLoopsInfo().get(i) == 0 &&
							relations.getOneLengthLoopsInfo().get(j) == 0) {
						continue;
					}
					//clear to 0
					relations.getParallelMatrix().set(i, j, 0);
					//reset the parallel relation
					if ((directSuccession.get(i, j) > minValue) &&
							(directSuccession.get(j, i) > minValue) &&
							((twoStepCloseIn.get(i, j) == 0) || (twoStepCloseIn.get(j, i) == 0))) {
						if (relations.getOneLengthLoopsInfo().get(i) > 0 &&
								relations.getOneLengthLoopsInfo().get(j) > 0) {
							relations.getCausalFollowerMatrix().set(i, j, 1);
						} else {
							relations.getParallelMatrix().set(i, j, 1);
						}
					}
				}
			}
			//fix causal relation
			for (int i = 0; i < nme; i++) {
				for (int j = 0; j < nme; j++) {
					if (i == j) {
						continue;
					}
					if ((directSuccession.get(i, j) > minValue) &&
							(directSuccession.get(j, i) > minValue) &&
							((twoStepCloseIn.get(i, j) > 0) && (twoStepCloseIn.get(j, i) > 0))) {
						if (relations.getOneLengthLoopsInfo().get(i) > 0 &&
								relations.getOneLengthLoopsInfo().get(j) > 0) {
							boolean isParallel = false;
							IntArrayList alSucc = getSucc(i, false);
							for (int k = 0; k < alSucc.size(); k++) {
								if (j != alSucc.get(k) && relations.getParallelMatrix().get(j,
										alSucc.get(k)) > 0) {
									isParallel = true;
									break;
								}
							}
							if (isParallel) {
								relations.getCausalFollowerMatrix().set(i, j, 0);
								relations.getParallelMatrix().set(i, j, 1);

								continue;
							}
							IntArrayList alPred = getPred(i, false);
							for (int k = 0; k < alPred.size(); k++) {
								if (alPred.get(k) != j &&
										relations.getParallelMatrix().get(alPred.get(k), j) > 0) {
									isParallel = true;
									break;
								}
							}
							if (isParallel) {
								relations.getCausalFollowerMatrix().set(i, j, 0);
								relations.getParallelMatrix().set(i, j, 1);
							}
						}
					}
				}
			}

			DoubleMatrix1D startInfo = abstraction.getStartInfo();
			DoubleMatrix1D endInfo = abstraction.getEndInfo();
			for (int i = 0; i < nme; i++) {
				if (relations.getOneLengthLoopsInfo().get(i) > 0) {
					relations.getCausalFollowerMatrix().set(i, i, 1);
				}

				//start tasks
				if (startInfo.get(i) > 0) {
					relations.getStartInfo().set(i, startInfo.get(i));
				}
				//end tasks
				if (endInfo.get(i) > 0) {
					relations.getEndInfo().set(i, endInfo.get(i));
				}
			}
		} catch (IOException ex) {
		}
    }

	private void addBeginEnd()
    {
        DoubleMatrix1D startInfo = relations.getStartInfo();
        DoubleMatrix1D endInfo = relations.getEndInfo();
        DoubleMatrix1D loopInfo = relations.getOneLengthLoopsInfo();
        DoubleMatrix2D causalInfo = relations.getCausalFollowerMatrix();
        DoubleMatrix2D parallelInfo = relations.getParallelMatrix();

        ArrayList alFirstGroup = new ArrayList();
        for (int i = 0; i < nme; i++)
        {
            if (startInfo.get(i) == 0)
                continue;
            IntArrayList ialPred = new IntArrayList();
            for(int j=0; j<nme; j++)
            {
                if(j == i)
                    continue;
                if(causalInfo.get(j, i) > 0 || parallelInfo.get(j, i) > 0)
                    ialPred.add(j);
            }
            if (loopInfo.get(i) > 0 && !ialPred.contains(i))
                ialPred.add(i);
            if(ialPred.size() == 0)
                continue;
            boolean isFound = false;
            for (int j = 0; j < alFirstGroup.size(); j++)
            {
                DoubleSet ds = (DoubleSet) alFirstGroup.get(j);
                if (ds.equalsA(ialPred) || existPara(ds.alA, ialPred))
                {
                    ds.addToB(i);
                    isFound = true;
                    break;
                }
            }
            if (!isFound)
            {
                DoubleSet ds = new DoubleSet();
                ds.addToA(ialPred);
                ds.addToB(i);
                alFirstGroup.add(ds);                
            }
        }
        ArrayList alLastGroup = new ArrayList();
        for (int i = 0; i < nme; i++)
        {
            if(endInfo.get(i) == 0)
                continue;
            IntArrayList ialSucc = new IntArrayList();
            for(int j=0; j<nme; j++)
            {
                if(j == i)
                    continue;
                if(causalInfo.get(i, j) > 0 || parallelInfo.get(i, j) > 0)
                    ialSucc.add(j);
            }
            if (loopInfo.get(i) > 0 && !ialSucc.contains(i))
                ialSucc.add(i);
            if(ialSucc.size() == 0)
                continue;
            boolean isFound = false;
            for (int j = 0; j < alLastGroup.size(); j++)
            {
                DoubleSet ds = (DoubleSet) alLastGroup.get(j);
                if (ds.equalsB(ialSucc) || existPara(ds.alB, ialSucc))
                {
                    ds.addToA(i);
                    isFound = true;
                    break;
                }
            }
            if (!isFound)
            {
                DoubleSet ds = new DoubleSet();
                ds.addToA(i);
                ds.addToB(ialSucc);
                alLastGroup.add(ds);
            }
        }
        
        
        //add the alFirstGroup/ alLastGroup to the invAXYB
        for (DoubleSet ds	:	(ArrayList<DoubleSet>)alFirstGroup)
        {
        	IntArrayList preds = ds.getA();
        	IntArrayList succs = ds.getB();
        	for (int i=0; i<preds.size(); i++)
        		for (int j=0; j<succs.size(); j++)
        		{
        			int pred = preds.get(i);
        			int succ = succs.get(j);
        			AXYB axyb;
                	String taskNameA = AXYB.start_a_tag;
                	String taskNameX = AXYB.start_x_tag;
                	String taskNameY = relations.getLogEvents().get(pred).getModelElementName();
                	String taskNameB = relations.getLogEvents().get(succ).getModelElementName();
                	axyb = new AXYB(taskNameA, taskNameX, taskNameY, taskNameB, AXYB.START);
                	invTaskAXYB.add(axyb);
        		}        	         			
        }
        
        for (DoubleSet ds	:	(ArrayList<DoubleSet>) alLastGroup)
        {
        	IntArrayList preds = ds.getA();
        	IntArrayList succs = ds.getB();
        	for (int i=0; i<preds.size(); i++)
        		for (int j=0; j<succs.size(); j++)
        		{
        			int pred = preds.get(i);
        			int succ = succs.get(j);
        			AXYB axyb;
                	String taskNameA = relations.getLogEvents().get(pred).getModelElementName();
                	String taskNameX = relations.getLogEvents().get(succ).getModelElementName();
                	String taskNameY = AXYB.end_y_tag;
                	String taskNameB = AXYB.end_y_tag;
                	axyb = new AXYB(taskNameA, taskNameX, taskNameY, taskNameB, AXYB.END);
                	invTaskAXYB.add(axyb);
        		}        	         			
        }
        
        //add invisible tasks of begin/end type
        if (alFirstGroup.size() > 0 || alLastGroup.size() > 0)
        {
            int nmeNew = nme + alFirstGroup.size() + alLastGroup.size();
            DoubleMatrix1D dmOneLoop = DoubleFactory1D.sparse.make(nmeNew, 0);
            for (int i = 0; i < nme; i++)
                dmOneLoop.set(i, relations.getOneLengthLoopsInfo().get(i));
            DoubleMatrix2D dmCausal = DoubleFactory2D.sparse.make(nmeNew, nmeNew, 0);
            for (int i = 0; i < nme; i++)
                for (int j = 0; j < nme; j++)
                    dmCausal.set(i, j, relations.getCausalFollowerMatrix().get(i, j));
            DoubleMatrix2D dmParallel = DoubleFactory2D.sparse.make(nmeNew, nmeNew, 0);
            for (int i = 0; i < nme; i++)
                for (int j = 0; j < nme; j++)
                    dmParallel.set(i, j, relations.getParallelMatrix().get(i, j));
            DoubleMatrix1D dmStart = DoubleFactory1D.sparse.make(nmeNew, 0);
            for (int i = 0; i < nme; i++)
                dmStart.set(i, relations.getStartInfo().get(i));
            DoubleMatrix1D dmEnd = DoubleFactory1D.sparse.make(nmeNew, 0);
            for (int i = 0; i < nme; i++)
                dmEnd.set(i, relations.getEndInfo().get(i));
            LogEvents leEvents = relations.getLogEvents();

            for (int i = 0; i < alFirstGroup.size(); i++)
            {
                DoubleSet ds = (DoubleSet) alFirstGroup.get(i);
                int id = nme + i;
                String t = "__begin_" + (i + 1) + "__";

                //add to log events
                LogEvent le = new LogEvent(t, "auto");
                leEvents.add(le);

                //add to relation matrix
                IntArrayList sarSucc = ds.getB();
                for (int j = 0; j < sarSucc.size(); j++)
                {
                    dmCausal.set(id, sarSucc.get(j), 1);
                    dmStart.set(sarSucc.get(j), 0);
                }

                dmStart.set(id, 1);
                
                //Add the Inv task
                InvTask invTask = new InvTask(t,InvTask.START);
                for (int j = 0; j < sarSucc.size(); j++)
                {
                	int pos = sarSucc.get(j);
                	String sucTaskName = leEvents.get(pos).getModelElementName() +"\0" + leEvents.get(pos).getEventType() ;
                	invTask.addSucTask(sucTaskName);
                }
                invTasks.add(invTask);
            }
            for (int i = 0; i < alLastGroup.size(); i++)
            {
                DoubleSet ds = (DoubleSet) alLastGroup.get(i);
                int id = nme + alFirstGroup.size() + i;
                String t = "__end_" + (i + 1) + "__";

                //add to log events
                LogEvent le = new LogEvent(t, "auto");
                leEvents.add(le);

                //add to relation matrix
                IntArrayList sarPred = ds.getA();
                for (int j = 0; j < sarPred.size(); j++)
                {
                    dmCausal.set(sarPred.get(j), id, 1);
                    dmEnd.set(sarPred.get(j), 0);
                }

                dmEnd.set(id, 1);
                
                InvTask invTask = new InvTask(t,InvTask.END);
                for (int j = 0; j < sarPred.size(); j++)
                {
                	int pos = sarPred.get(j);
                	String preTaskName = leEvents.get(pos).getModelElementName() +"\0" + leEvents.get(pos).getEventType();
                	invTask.addPreTask(preTaskName);
                }
                invTasks.add(invTask);
            }

            //create a totally new log relations object
            nme = nmeNew;
            relations = new LogRelationsImpl(dmCausal, dmParallel, dmEnd, dmStart, dmOneLoop, leEvents);
        }
    }
	
	
	
    private void RemoveDuplicates(ArrayList tuples)
    {
        int i = 0;
        while (i < tuples.size())
        {

            IntArrayList[] tuple_i = ( (IntArrayList[]) (tuples.get(i)));
            int j = -1;
            while (j < tuples.size() - 1)
            {
                j++;
                if (i == j)
                    continue;
                IntArrayList[] tuple_j = ( (IntArrayList[]) (tuples.get(j)));

                // Now check whether j is a subset of i
                if (tuple_i[0].toList().containsAll(tuple_j[0].toList()) &&
                    tuple_i[1].toList().containsAll(tuple_j[1].toList()))
                {

                    // tuple_i contains tuple_j
                    tuples.remove(j);
                    if (j < i)
                        i--;
                    j--;
                }
            }
            i++;
        }
    }

    private boolean ExpandTree(ArrayList tuples, IntArrayList A, IntArrayList B, int sA, int sB)
    {
        boolean expanded = false;

        int s = sA;
        if (sB < s)
        {
            s = sB;
            // Look for an element that can be added to A, such that
            // it has no relation with any task in A, and is a causal predecessor of all tasks in B
        }
        for (int i = s; i < nme; i++)
        {
            boolean c = (i >= sA) && !A.contains(i);
            if (c)
            {
                for (int j = 0; j < A.size(); j++)
                {
                    c = c && (relations.getCausalFollowerMatrix().get(i, A.get(j)) == 0 || relations.getCausalFollowerMatrix().get(i, A.get(j)) > 0 && relations.getOneLengthLoopsInfo().get(A.get(j)) > 0)
                        && (relations.getCausalFollowerMatrix().get(A.get(j), i) == 0 || relations.getCausalFollowerMatrix().get(A.get(j), i) > 0 && relations.getOneLengthLoopsInfo().get(i) > 0)
                        && (relations.getParallelMatrix().get(i, A.get(j)) == 0);
                    // c == i does not have a relation with any element of A
                }
            }
            if (c)
            {
                for (int j = 0; j < B.size(); j++)
                {
                    c = c && (relations.getCausalFollowerMatrix().get(i, B.get(j)) > 0);
                    // c == i is a causal predecessor of all elements of B
                }
            }
            boolean d = (i >= sB) && !B.contains(i);
            if (d)
            {
                for (int j = 0; j < B.size(); j++)
                {
                    d = d && (relations.getCausalFollowerMatrix().get(i, B.get(j)) == 0 || relations.getCausalFollowerMatrix().get(i, B.get(j)) > 0 && relations.getOneLengthLoopsInfo().get(i) > 0)
                        && (relations.getCausalFollowerMatrix().get(B.get(j), i) == 0 || relations.getCausalFollowerMatrix().get(B.get(j), i) > 0 && relations.getOneLengthLoopsInfo().get(B.get(j)) > 0)
                        && (relations.getParallelMatrix().get(i, B.get(j)) == 0);
                    // d == i does not have a relation with any element of B
                }
            }
            if (d)
            {
                for (int j = 0; j < A.size(); j++)
                {
                    d = d && (relations.getCausalFollowerMatrix().get(A.get(j), i) > 0);
                    // d == i is a causal successor of all elements of A
                }
            }
            IntArrayList tA = (IntArrayList) A.clone();
            IntArrayList tB = (IntArrayList) B.clone();

            if (c)
            {
                // i can be added to A
                A.add(i);
                expanded = ExpandTree(tuples, A, B, i + 1, sB);
                A = tA;
            }
            if (d)
            {
                // i can be added to B
                B.add(i);
                expanded = ExpandTree(tuples, A, B, sA, i + 1);
                B = tB;
            }
        }
        if (!expanded)
        {
            IntArrayList[] t = new IntArrayList[2];
            t[0] = (IntArrayList) A.clone();
            t[1] = (IntArrayList) B.clone();
            tuples.add(t);
            expanded = true;
        }
        return expanded;
    }

    public String getHtmlDescription()
    {
        return "<h1>" + getName() + "</h1>";
    }

    //get any task's predecessor
    private IntArrayList getPred(int col, boolean noLoop)
    {
        IntArrayList ialPred = new IntArrayList();
        DoubleMatrix1D dm = relations.getCausalFollowerMatrix().viewColumn(col);
        for (int i = 0; i < nme; i++)
        {
            if (noLoop && relations.getOneLengthLoopsInfo().get(i) > 0)
                continue;
            if (dm.get(i) > 0)
                ialPred.add(i);
        }
        return ialPred;
    }

    //get any task's successor
    private IntArrayList getSucc(int row, boolean noLoop)
    {
        IntArrayList ialPred = new IntArrayList();
        DoubleMatrix1D dm = relations.getCausalFollowerMatrix().viewRow(row);
        for (int i = 0; i < nme; i++)
        {
            if (noLoop && relations.getOneLengthLoopsInfo().get(i) > 0)
                continue;
            if (dm.get(i) > 0)
                ialPred.add(i);
        }
        return ialPred;
    }

    private boolean existPara(IntArrayList sarI, IntArrayList sarJ)
    {
        for (int i = 0; i < sarI.size(); i++)
            for (int j = 0; j < sarJ.size(); j++)
                if (relations.getParallelMatrix().get(sarI.get(i), sarJ.get(j)) > 0)
                    return true;
        return false;
    }
    
    private boolean addInvTask(ArrayList alInvTask, InvisibleTask it, ArrayList alFalseDep)
    {
        DoubleSet dsPred = it.getPred()[0];
        DoubleSet dsSucc = it.getSucc()[0];
        //can be combined into any existing invisible tasks?
        int nCombined = 0;
        for (int i = 0; i < alInvTask.size(); i++)
        {
            InvisibleTask iti = (InvisibleTask) alInvTask.get(i);
            boolean isInPred = iti.containedInPred(dsPred);
            boolean isInSucc = iti.containedInSucc(dsSucc);
            //case 1
            if (isInPred && isInSucc)
            {
                nCombined++;
            }
            else if (isInPred)
            {
                boolean isCombined = isCombinable(alFalseDep, iti.getPred(), it.getSucc(), iti.getSucc(), it.getSucc());
                //combined into this invisible task
                if (isCombined)
                {
                    iti.addSucc(dsSucc);
                    nCombined++;
                    InvTask invTask = null;
                    for (InvTask ii	:	invTasks)
                    {
                    	if (iti.getName().equals(i))
                    	{
                    		invTask = ii;
                    		break;
                    	}
                    }
                    if (invTask != null)
                    {
                    	IntArrayList sarSuccSucc = dsSucc.getB();
                    	for (int m=0; m<sarSuccSucc.size(); m++)
                    	{
                    		int pos = sarSuccSucc.get(m);
                        	String sucTaskName = relations.getLogEvents().get(pos).getModelElementName() +"\0" + relations.getLogEvents().get(pos).getEventType() ;
                        	invTask.addSucTask(sucTaskName);
                    	}
                    }
                    
                }
            }
            else if (isInSucc)
            {
                boolean isCombined = isCombinable(alFalseDep, it.getPred(), iti.getSucc(), it.getPred(), iti.getPred());
                //combined into this invisible task
                if (isCombined)
                {
                    iti.addPred(dsPred);                                        
                    nCombined++;
                    
                    InvTask invTask = null;
                    for (InvTask ii	:	invTasks)
                    {
                    	if (iti.getName().equals(i))
                    	{
                    		invTask = ii;
                    		break;
                    	}
                    }
                    if (invTask != null)
                    {
                    	IntArrayList sarPredPred = dsPred.getA();
                    	for (int m=0; m<sarPredPred.size(); m++)
                    	{
                    		int pos = sarPredPred.get(m);
                        	String preTaskName = relations.getLogEvents().get(pos).getModelElementName() +"\0" + relations.getLogEvents().get(pos).getEventType() ;
                        	invTask.addPreTask(preTaskName);
                    	}
                    }
                    
                }
            }
            else
            {
                boolean isCombined = isCombinable(alFalseDep, iti.getPred(), it.getSucc(), iti.getSucc(), it.getSucc());
                isCombined = isCombined && isCombinable(alFalseDep, it.getPred(), iti.getSucc(), it.getPred(), iti.getPred());
                //combined into this invisible task
                if (isCombined)
                {
                    iti.addPred(dsPred);
                    iti.addSucc(dsSucc);                                                         
                    nCombined++;
                    
                    InvTask invTask = null;
                    for (InvTask ii	:	invTasks)
                    {
                    	if (iti.getName().equals(i))
                    	{
                    		invTask = ii;
                    		break;
                    	}
                    }
                    if (invTask != null)
                    {
                    	IntArrayList sarPredPred = dsPred.getA();
                    	IntArrayList sarSuccSucc = dsSucc.getB();
                    	for (int m=0; m<sarPredPred.size(); m++)
                    	{
                    		int pos = sarPredPred.get(m);
                        	String preTaskName = relations.getLogEvents().get(pos).getModelElementName() +"\0" + relations.getLogEvents().get(pos).getEventType() ;
                        	invTask.addPreTask(preTaskName);
                    	}                    	
                    	for (int m=0; m<sarSuccSucc.size(); m++)
                    	{
                    		int pos = sarSuccSucc.get(m);
                        	String sucTaskName = relations.getLogEvents().get(pos).getModelElementName() +"\0" + relations.getLogEvents().get(pos).getEventType() ;
                        	invTask.addSucTask(sucTaskName);
                    	}
                    }
                }
            }
        }

        if (nCombined == 0)
        {
            //add a new invisible task
            alInvTask.add(it);
            return true;
        }
        else
        {
            //combined into existing invisible tasks
            return false;
        }
    }

    private boolean isCombinable(ArrayList alFalseDep, DoubleSet[] sarIPred, DoubleSet[] sarSucc, DoubleSet[] sarISucc, DoubleSet[] sarPred)
    {
        //I's Pred'pred vs Succ'succ
        for (int j = 0; j < sarIPred.length; j++)
        {
            IntArrayList sarIPredPred = sarIPred[j].getA();
            for (int k = 0; k < sarIPredPred.size(); k++)
            {
                for(int n=0; n<sarSucc.length; n++)
                {
                    IntArrayList sarSuccSucc = sarSucc[n].getB();
                    for (int m = 0; m < sarSuccSucc.size(); m++)
                    {
                        PredSucc ps = new PredSucc(sarIPredPred.get(k), sarSuccSucc.get(m));
                        if (!alFalseDep.contains(ps))
                            return false;
                    }
                }
            }
        }
        //I's Pred'succ vs Succ'pred
        for (int j = 0; j < sarIPred.length; j++)
            for(int k=0; k<sarSucc.length; k++)
                if (existPara(sarIPred[j].getB(), sarSucc[k].getA()))
                    return false;
        //I's Succ vs Succ
        for (int j = 0; j < sarISucc.length; j++)
            for(int k=0; k<sarPred.length; k++)
                if (! (existPara(sarISucc[j].getA(), sarPred[k].getA()) ||
                       existPara(sarISucc[j].getB(), sarPred[k].getB())))
                    return false;
        return true;
    }

	public void mineSpecialRelation(LogReader log, AlphaPPData alphaPPData, LogEvents leEvents) {
		// TODO Auto-generated method stub
			AlphaSharpData alphaSharpData = new AlphaSharpData();
			alphaSharpData = AlphaMMinerDataUtil.convertRelation(alphaPPData, leEvents);
			relations = alphaSharpData.theRelations;
		    nme = alphaSharpData.nme; //Number of model elements
		    nme_old = nme; //old number of model elements

	        //add begin/end task
	        addBeginEnd();					//to find the begin / end invisible transitions. 

	        //find skip/redo/switch tasks
	        ArrayList alFalseDep = new ArrayList();
	        ArrayList alRedunDep = getFalseDep(alFalseDep, log);

	        //add skip_redo_switch invisible tasks
	        ArrayList alInvTask = new ArrayList();
	        addSkipRedo(alFalseDep, alRedunDep, alInvTask);
	        alphaSharpData.theRelations = relations;
	        alphaSharpData.nme = nme;
	        alphaSharpData.nme_old = nme_old;
	        AlphaMMinerDataUtil.addRelation(alphaPPData, alphaSharpData);	        
	}

	public void searchPrePostSet(LogReader log)
	{		
		intervalTasksSet = new IntervalTasksSet(relations.getNumberElements());
		intervalTasksSet.beginAdd();
		for (ProcessInstance processInstance	:	log.getInstances())
		{
			AuditTrailEntryList ates = processInstance.getAuditTrailEntryList();
			
			intervalTasksSet.addTrace(ates,relations.getLogEvents());
			
		}
		intervalTasksSet.endAdd();
	}
	
	public AlphaPPData mineAlphaSharpInfo(LogReader log, LogRelations theRelations, boolean heuristics) {
		AlphaPPData alphaPPData = new AlphaPPData();
		relations = theRelations;
        nme = relations.getLogEvents().size();
        nme_old = nme;
        invTasks = new ArrayList<InvTask>();
        invTaskAXYB = new ArrayList<AXYB>();

        searchPrePostSet(log);			//0.search the log, to acquire each task's pre set and post set.

        if (heuristics) {
            relations = HeuristicsRelations.simplifyRelations(log, relations);
        }

        addLoopRelation(log, 0);		//1.set relation.parallel based on the loop   //

        if (heuristics) {
            relations = HeuristicsRelations.simplifyRelations(log, relations);
        }

        addBeginEnd();					//to find the begin / end invisible transitions.

        if (heuristics) {
            relations = HeuristicsRelations.simplifyRelations(log, relations);
        }

        induceReachable(log);

        if (heuristics) {
            relations = HeuristicsRelations.simplifyRelations(log, relations);
        }
        
        ArrayList alFalseDep = new ArrayList();
        ArrayList alRedunDep = getFalseDep(alFalseDep,log);

        //add skip_redo_switch invisible tasks

        if (heuristics) {
            relations = HeuristicsRelations.simplifyRelations(log, relations);
        }
        
        ArrayList alInvTask = new ArrayList();
        
        
        addSkipRedo(alFalseDep, alRedunDep, alInvTask);

        if (heuristics) {
            relations = HeuristicsRelations.simplifyRelations(log, relations);
        }

        adjustL1LRelation(log);

        if (heuristics) {
            relations = HeuristicsRelations.simplifyRelations(log, relations);
        }

		AlphaSharpData alphaSharpData = new AlphaSharpData();
        alphaSharpData.theRelations = relations;
        alphaSharpData.nme = nme;
        alphaSharpData.nme_old = nme_old;    
        alphaSharpData.invTasks = invTasks;
        alphaSharpData.invTaskAXYB = invTaskAXYB;        
        AlphaMMinerDataUtil.addRelation(alphaPPData, alphaSharpData);	       
		return alphaPPData;
	}

	private void adjustL1LRelation(LogReader log) {
		// TODO Auto-generated method stub
		DoubleMatrix1D l1l = relations.getOneLengthLoopsInfo();		
		int size = relations.getLogEvents().size();
		
        LogAbstraction abstraction = new LogAbstractionImpl(log);
        DoubleMatrix2D directSuccession, twoStepCloseIn;
		try {
			directSuccession = abstraction.getFollowerInfo(1);
			twoStepCloseIn = abstraction.getCloseInInfo(2);
			
			for (int i=0; i<l1l.size(); i++)		
				if (l1l.get(i) > 0)
				{									
					for (int j=0; j<size; j++)
					{
//						if (i == j)
//							continue;
						//casual
//						if (relations.getCausalFollowerMatrix().get(i, j)>0 && twoStepCloseIn.get(i, j) >0)
//							relations.getCausalFollowerMatrix().set(i, j, 0);
						
						//没想清楚原因,但是的确只需要修改掉并行的部分就可以了擦
						//parallel
						if (relations.getParallelMatrix().get(i, j)>0 && twoStepCloseIn.get(i, j) >0)
						{
							relations.getParallelMatrix().set(i, j, 0);
							relations.getParallelMatrix().set(j, i, 0);
						}
					}
					
				}
		} 
		catch (IOException e)
		{			
			e.printStackTrace();
		}
		
	}
}

class PredSucc
{
    int pred;
    int succ;
    int x;
    int y;

    public PredSucc(int pred, int succ)
    {
        this.pred = pred;
        this.succ = succ;        
    }

    public boolean equals(Object o)
    {
        PredSucc ps = (PredSucc) o;
        return pred == ps.getPred() && succ == ps.getSucc() && x  == ps.getX() && y == ps.getY();
    }

    public int getSucc()
    {
        return succ;
    }

    public int getPred()
    {
        return pred;
    }
    
    public int getX()
    {
        return x;
    }
    
    public int getY()
    {
        return y;
    }

    public String toString()
    {
        return pred + "-->" + succ + ",X:"+x+",Y:"+y;
    }
}

class DoubleSet
{
    IntArrayList alA = new IntArrayList();
    IntArrayList alB = new IntArrayList();

    public DoubleSet()
    {
    }

    public boolean isEmptyAB()
    {
        return alA.isEmpty() || alB.isEmpty();
    }

    public boolean isEmptyA()
    {
        return alA.isEmpty();
    }

    public boolean isEmptyB()
    {
        return alB.isEmpty();
    }

    public void addToA(int a)
    {
        if (!alA.contains(a))
        {
            alA.add(a);
        }
    }

    public void clearA()
    {
        alA.clear();
    }

    public void clearB()
    {
        alB.clear();
    }

    public void addToA(IntArrayList alTask)
    {
        for (int i = 0; i < alTask.size(); i++)
        {
            int t = alTask.get(i);
            if (!alA.contains(t))
            {
                alA.add(t);
            }
        }
    }

    public void addToB(int b)
    {
        if (!alB.contains(b))
        {
            alB.add(b);
        }
    }

    public void removeFromA(IntArrayList alFalseA)
    {
        alA.removeAll(alFalseA);
    }

    public void removeFromB(IntArrayList alFalseB)
    {
        alB.removeAll(alFalseB);
    }

    public void addToB(IntArrayList alTask)
    {
        for (int i = 0; i < alTask.size(); i++)
        {
            int t = alTask.get(i);
            if (!alB.contains(t))
            {
                alB.add(t);
            }
        }
    }

    public IntArrayList getA()
    {
        return alA;
    }

    public IntArrayList getB()
    {
        return alB;
    }

    public boolean containedInA(int a)
    {
        return alA.contains(a);
    }

    public boolean containedInA(IntArrayList ialA)
    {
        IntArrayList alAcpy = alA.copy();
        alAcpy.removeAll(ialA);
        return alAcpy.size() == alA.size() - ialA.size();
    }

    public boolean equalsA(IntArrayList ialA)
    {
        if (alA.size() != ialA.size())
        {
            return false;
        }
        IntArrayList alCpy = alA.copy();
        alCpy.removeAll(ialA);
        return alCpy.size() == 0;
    }

    public boolean containedInB(int b)
    {
        return alB.contains(b);
    }

    public boolean containedInB(IntArrayList ialB)
    {
        IntArrayList alBcpy = alB.copy();
        alBcpy.removeAll(ialB);
        return alBcpy.size() == alB.size() - ialB.size();
    }

    public boolean equalsB(IntArrayList ialB)
    {
        if (alB.size() != ialB.size())
        {
            return false;
        }
        IntArrayList alCpy = alB.copy();
        alCpy.removeAll(ialB);
        return alCpy.size() == 0;
    }

    public boolean equals(Object o)
    {
        DoubleSet ds = (DoubleSet) o;
        return equalsA(ds.alA) && equalsB(ds.alB);
    }

    public String toString()
    {
        return "{" + alA.toString() + "->" + alB.toString() + "}";
    }
}

class InvisibleTask
{
    String t;
    int id;
    ArrayList alPred = new ArrayList();
    ArrayList alSucc = new ArrayList();

        
    public InvisibleTask(String t)
    {
        this.t = t;
    }

    public String getName()
    {
        return t;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void addPred(DoubleSet ds)
    {
        if (!alPred.contains(ds))
        {
            alPred.add(ds);
        }
    }

    public void addSucc(DoubleSet ds)
    {
        if (!alSucc.contains(ds))
        {
            alSucc.add(ds);
        }
    }

    public DoubleSet[] getPred()
    {
        DoubleSet[] sarPlace = new DoubleSet[alPred.size()];
        alPred.toArray(sarPlace);
        return sarPlace;
    }

    public DoubleSet[] getSucc()
    {
        DoubleSet[] sarPlace = new DoubleSet[alSucc.size()];
        alSucc.toArray(sarPlace);
        return sarPlace;
    }

    public boolean containedInPred(DoubleSet ds)
    {
        return alPred.contains(ds);
    }

    public boolean containedInSucc(DoubleSet ds)
    {
        return alSucc.contains(ds);
    }

    public IntArrayList getPredPred()
    {
        IntArrayList alTask = new IntArrayList();
        for (int i = 0; i < alPred.size(); i++)
        {
            DoubleSet ds = (DoubleSet) alPred.get(i);
            IntArrayList sarPred = ds.getA();
            for (int j = 0; j < sarPred.size(); j++)
            {
                if (!alTask.contains(sarPred.get(j)))
                {
                    alTask.add(sarPred.get(j));
                }
            }
        }

        return alTask;
    }

    public IntArrayList getSuccSucc()
    {
        IntArrayList alTask = new IntArrayList();
        for (int i = 0; i < alSucc.size(); i++)
        {
            DoubleSet ds = (DoubleSet) alSucc.get(i);
            IntArrayList sarSucc = ds.getB();
            for (int j = 0; j < sarSucc.size(); j++)
            {
                if (!alTask.contains(sarSucc.get(j)))
                {
                    alTask.add(sarSucc.get(j));
                }
            }
        }

        return alTask;
    }
}
