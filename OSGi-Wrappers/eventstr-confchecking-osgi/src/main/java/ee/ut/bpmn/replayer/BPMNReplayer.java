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

package ee.ut.bpmn.replayer;

import com.google.common.collect.BiMap;
import ee.ut.eventstr.PESSemantics;
import hub.top.petrinet.Node;
import org.jbpt.pm.Activity;
import org.jbpt.pm.AndGateway;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.XorGateway;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.bpmn.BpmnControlFlow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class BPMNReplayer {

	private Bpmn<BpmnControlFlow<FlowNode>, FlowNode> model;
	private HashSet<String> observable;
	private PESSemantics<Integer> es;

	public BPMNReplayer(Bpmn<BpmnControlFlow<FlowNode>, FlowNode> model, HashSet<String> observable, PESSemantics<Integer> esA) {
		this.model = model;
		this.observable = observable;
		this.es = esA;
	}

	public HashMap<String, String> spotTask(String label) {
		HashMap<String, String> colors = new HashMap<>(); 
		
		for(FlowNode node : model.getFlowNodes())
			if(node.getName().equals(label))
				colors.put(node.getId(), "red");
		return colors;
	}
	
	public HashMap<String, String> executeC(String end1, String end2, Pomset conf, HashMap<String, Integer> repetitions, BiMap<Node, Node> mapping) {
		HashMap<String, String> exec = execute(end1 + "xyz123", conf, repetitions, mapping);
		
		for(String key : exec.keySet()){
			FlowNode node = getActivity(key);
			if(node != null && (node.getName().equals(end1) || node.getName().equals(end2)))
				exec.put(key, "red");
		}
			
		return exec;
	}
	
	private FlowNode getActivity(String key) {
		for(FlowNode node : model.getActivities())
			if(node.getId().equals(key))
				return node;
		
		return null;
	}

	public HashMap<String, String> execute(String end, Pomset conf, HashMap<String, Integer> repetitions, BiMap<Node, Node> mapping) {
		HashSet<FlowNode> marking = getMarking();
		HashSet<QueueEntry> visited = new HashSet<QueueEntry>();
		PriorityQueue<QueueEntry> queue = new PriorityQueue<>();
		queue.add(new QueueEntry(marking, conf));
		System.out.println("IN: ");
		
		while (!queue.isEmpty()) {
			QueueEntry current = queue.remove();
			visited.add(current);

			System.out.print(current.pomset.getSize()+", ");
			//System.out.println(current.pomset.toDOT());

			if (current.getNext().size() == 0) {
				repetitions.putAll(current.repetitions);
				return current.colors;
			}

			for (FlowNode f : current.candidates) {
				if (f instanceof AndGateway) {
					boolean join = model.getIncomingControlFlow(f).size() > 1 ? true : false;

					if (!join || current.history.containsAll(model.getDirectPredecessors(f))) {
						QueueEntry copy = current.clone();
						copy.add2History(f);
						copy.candidates.remove(f);
						for (FlowNode s : model.getDirectSuccessors(f))
//							if(!copy.history.contains(s))
								copy.add2Candidates(s);

						if (!queue.contains(copy) && !visited.contains(copy))
							queue.add(copy);

						copy.add2Counter(f);
						copy.add2Colors(f, "green");
					}
				} else if (f instanceof XorGateway) {
					// boolean join = model.getIncomingControlFlow(f).size() > 1
					// ? true : false;

					for (FlowNode s : model.getDirectSuccessors(f)) {
						QueueEntry copy = current.clone();
						copy.add2History(f);
						copy.candidates.remove(f);
						copy.add2Candidates(s);

//						if(!copy.history.contains(s))
						{
							if (!queue.contains(copy) && !visited.contains(copy))
									queue.add(copy);
							
							copy.add2Counter(f);
							copy.add2Colors(f, "green");
						}
					}
					
					current.add2Colors(f, "green");
				} else {
					if (f instanceof Activity && observable.contains(f.getName())){
						if (!es.getLabels(current.getNext()).contains(f.getName()))
							continue;
						
						Integer v = getVertex(current, f.getName());
						QueueEntry copy = current.clone(v);
						copy.add2History(f);
						copy.candidates.remove(f);
						copy.addMapping(v, f);

						copy.add2Counter(f);

						for (FlowNode s : model.getDirectSuccessors(f))
//							if(!copy.history.contains(s))
								copy.add2Candidates(s);

						if (copy.getNext().isEmpty() && f.getName().equals(end)) {
							copy.add2Colors(f, "red");
							repetitions.putAll(copy.repetitions);
							System.out.println();
							return copy.colors;
						} else {
							if (mapping == null || (mapping != null && !(mapping.containsKey(v) || mapping.containsValue(v))))
								copy.add2Colors(f, "green");
							else if (mapping != null && (mapping.containsKey(v) || mapping.containsValue(v)))
								copy.add2Colors(f, "green");

							if (!queue.contains(copy) && !visited.contains(copy))
								queue.add(copy);
						}
					} else {
						// fix bug .... when a silent task is in a cycle (it will always add it again)
//						if(!current.contains(f.getName()))
						{
							QueueEntry copy = null;
							if(current.containsPO(f.getName())){
								Integer v = getVertex(current, f.getName());
								copy = current.clone(v);
							}else
								copy = current.clone();
							copy.add2History(f);
							copy.candidates.remove(f);
	
							for (FlowNode s : model.getDirectSuccessors(f))
								if(!s.equals(f))
								copy.add2Candidates(s);
	
							copy.add2Counter(f);
							copy.add2Colors(f, "green");
	
							if (!queue.contains(copy) && !visited.contains(copy))
								queue.add(copy);
						}
					}
				}
			}
		}

		return new HashMap<String, String>();
	}
	
	private Integer getVertex(QueueEntry current, String name) {
		Set<Integer> next = current.getNext();
		
		for(Integer n : next)
			if(es.getLabel(n).equals(name))
				return n;
		
		return null;
	}

	private HashSet<FlowNode> getMarking() {
		HashSet<FlowNode> m = new HashSet<FlowNode>();

		for (FlowNode f : model.getFlowNodes())
			if (model.getDirectPredecessors(f) == null || model.getDirectPredecessors(f).isEmpty())
				m.add(f);

		return m;
	}
}
