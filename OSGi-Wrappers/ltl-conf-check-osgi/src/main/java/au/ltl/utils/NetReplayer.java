/*-
 * #%L
 * This file is part of "Apromore Community".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package au.ltl.utils;

import hub.top.petrinet.Node;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import org.jbpt.pm.FlowNode;

import au.ltl.domain.Constants;
import au.ltl.domain.Subnet;
import au.ltl.domain.Trace;

/**
 * Created by armascer on 9/11/2017.
 */
public class NetReplayer {
    private HashSet<Execution> executions;
    PetriNet net;
    HashMap<Node, Node> mapSub2Unf;
    ModelAbstractions model;
    HashSet<String> silent ;
    Constants constant;

    public NetReplayer(Subnet subnet, ModelAbstractions model,HashSet<String> silent, Constants constant){
        this.net = subnet.getNet();
        this.mapSub2Unf = subnet.getMap();
        this.model = model;
        this.silent = silent;
        this.constant = constant;
    }

    public void getTraces() {
        Execution exec = new Execution();
        exec.setMarking(getInitialMarking());
        Queue<Execution> toAnalyze = new LinkedList<>();
        toAnalyze.add(exec);
        HashSet<LinkedList<String>> traces = new HashSet<>();

        while(!toAnalyze.isEmpty()){
            Execution current = toAnalyze.remove();
            HashSet<Transition> enabled = getEnabledTs(current.marking);
            if(enabled.isEmpty()){
               // traces.add(current.getTrace());
            	
    			Trace trace= new Trace ("Trace#"+constant.getNumberOfTraces());
    			Vector<String> trace_alphabet_vector= new Vector<String>();
    			Vector<String> original_trace_content_vector= new Vector<String>();
    			Vector<String> original_transaction_id= new Vector<String>();
    			
                LinkedList<String> trace_string= current.getTrace();
                LinkedList<Transition> firingSeq= current.firingSeq;
                Iterator <String> it_trace= trace_string.iterator();
                Iterator <Transition> it_tr=firingSeq.iterator();
                while(it_trace.hasNext()){
                	String task_string= it_trace.next();
                	Transition transition= it_tr.next();
                	if(!silent.contains(task_string)){
                		Node unfolded_node= mapSub2Unf.get(transition);
                		Node petrinet_node= model.getMapBP2Net().get(unfolded_node);
                		FlowNode bpmn_original_task= model.getMapTasks2TransReverse().get(petrinet_node);

                		if(bpmn_original_task == null)
                		    continue;

                		if(!trace_alphabet_vector.contains(task_string))
    						trace_alphabet_vector.add(task_string);
    					original_trace_content_vector.add(task_string);
    					original_transaction_id.add(bpmn_original_task.getId());
                		
                	}
                }
    			trace.setOriginalTraceContent_vector(original_trace_content_vector);
    			trace.setTraceAlphabet_vector(trace_alphabet_vector);
    			trace.setOriginal_transaction_id(original_transaction_id);
    			constant.getTraces().add(trace);
            }

            for(Transition t : enabled){
                Execution c1 = current.clone();
                c1.addFired(t);
                toAnalyze.add(c1);
            }

        }
    }
    
    
    private HashSet<Transition> getEnabledTs(LinkedList<Place> marking) {
        HashSet<Transition> enabled = new HashSet<>();
        for(Transition t : net.getTransitions())
            if(marking.containsAll(t.getPreSet()))
                enabled.add(t);

        return enabled;
    }

    private LinkedList<Place> getInitialMarking() {
        LinkedList<Place> marking = new LinkedList<>();

        for(Place p : net.getPlaces())
            if(p.getPreSet().isEmpty())
                marking.add(p);

        return marking;
    }
}
