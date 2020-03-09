package org.apromore.processdiscoverer.dfg.abstraction;

import java.awt.Point;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.VisualizationAggregation;
import org.apromore.processdiscoverer.VisualizationType;
import org.apromore.processdiscoverer.dfg.Arc;
import org.apromore.processdiscoverer.dfg.LogDFG;
import org.apromore.processdiscoverer.dfg.collectors.ArcInfoCollector;
import org.apromore.processdiscoverer.dfg.vis.BPMNDiagramBuilder;
import org.apromore.processdiscoverer.dfg.vis.BPMNDiagramLayouter;
import org.apromore.processdiscoverer.logprocessors.LogUtils;
import org.apromore.processdiscoverer.logprocessors.SimplifiedLog;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import java.util.HashSet;

/**
 * This class represents the BPMN abstraction of logs
 * It has a corresponding DFGAbstraction with the same type of nodes/arcs and weights.
 * This DFGAbstraction is used to calculate the weights on the model in matching them
 * with the weights on the DFG. 
 * In addition, it has another corresponding DFGAbstraction where weights of nodes
 * and arcs are frequency. This DFGAbstraction is needed for SplitMiner to discover 
 * a BPMN model. 
 * @author Bruce Nguyen
 *
 */
public class BPMNAbstraction extends AbstractAbstraction {
	private DFGAbstraction dfgAbstraction;
	private DFGAbstraction dfgAbsFreq;
	
	/**
	 * Create a new BPMNAbstraction of logs.
	 * @param logDfg
	 * @param params
	 * @param dfgAbstraction: the corresponding DFGAbstraction with the same types of nodes and weights
	 * @throws Exception
	 */
	public BPMNAbstraction(LogDFG logDfg, AbstractionParams params, DFGAbstraction dfgAbstraction) throws Exception {
		super(logDfg, params);
		this.dfgAbstraction = dfgAbstraction;
		this.dfgAbsFreq = logDfg.getFrequencyBasedDFGAbstraction(dfgAbstraction);
		this.diagram = logDfg.getBPMN(params, dfgAbsFreq);
		this.updateWeights(params);
		this.layout = BPMNDiagramLayouter.layout(this.diagram, true);
	}
	
	public DFGAbstraction getDFGAbstraction() {
		return this.dfgAbstraction;
	}
	
	public DFGAbstraction getFrequencyBasedDFGAbstraction() {
		return this.dfgAbsFreq;
	}
	
    /**
     * This method calculates weights on the BPMN model based on the current LogDFG
     * Mainly it aims to create a mapping from an edge on the model to a set of arcs on the DFG
     * Therefore, the weight on the edge is the sum of those weights on the arcs.
     * TODO: this mapping is inaccurate as it does not consider all the semantics of BPMN gateways.
     * At the moment, it only considers all gateways as XORs. 
     * @param params
     */
	@Override
	protected void updateArcWeights(AbstractionParams params) {
		arcPrimaryWeights.clear();
		arcSecondaryWeights.clear();
		
		if (params.getPrimaryType() == VisualizationType.FREQUENCY) {
			updateArcFrequencyWeights(params, nodePrimaryWeights, arcPrimaryWeights);
			if (params.getSecondary()) {
				updateArcDurationWeightFromDFG(params, arcSecondaryWeights);
			}
		}
		else {
			updateArcDurationWeightFromDFG(params, arcPrimaryWeights);
			if (params.getSecondary()) {
				updateArcFrequencyWeights(params, nodeSecondaryWeights, arcSecondaryWeights);
			}
		}
	}
	
	private void updateArcDurationWeightFromDFG(AbstractionParams params,
											Map<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>,Double> arcWeights) {
        //----------------------------------------------------
        // The arcs on the discovered BPMN diagram are mapped with 
        // the arcs in the ArcInfoCollector to compute its weight.
        // It concludes that if there is an arc from A to B on the model,
        // it corresponds to a set of arcs on the DFG with source=A and target=B
        //----------------------------------------------------
    	for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : this.diagram.getEdges()) {
            BPMNNode target = edge.getTarget();
            
            if (!(target instanceof Activity)) {
            	arcWeights.put(edge, 0d);
            }
            else {
            	arcWeights.put(edge, this.getEdgeWeightFromDFG(edge, params, arcWeights==this.arcSecondaryWeights));
            }
        }
    }
	
	/**
	 * Compute edge weight by aligning it to the DFG.
	 * This alignment is approximate only.
	 * @param edge
	 * @param params
	 * @param secondary
	 * @return
	 */
	private double getEdgeWeightFromDFG(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge, AbstractionParams params,
										boolean secondary) {
		Set<BPMNNode> sources = BPMNDiagramBuilder.getSources(this.diagram, edge.getSource(), new UnifiedSet<>());
        Set<BPMNNode> targets = BPMNDiagramBuilder.getTargets(this.diagram, edge.getTarget(), new UnifiedSet<>());
        double weight = 0d;
        int count = 0;
        for (BPMNNode s : sources) {
            for (BPMNNode t : targets) {
            	Double dfgWeight = this.dfgAbstraction.getEdgeWeight(s.getLabel(), t.getLabel(), secondary);
            	if (dfgWeight != null) {
            		count++;
                	weight += dfgWeight;
            	}
            }
        }
        if (count > 0) {
        	return Math.ceil(1.0*weight/count);
        }
        else {
        	return 0d;
        }
        
	}
	
	/**
	 * Update arc frequency weights.
	 * Note that the weight of single output edge from the start event should be equal to
	 * the total weights of corresponding edges on the corresponding DFG. It could be less
	 * than the total starting weight of the start event on the corresponding DFG.
	 * @param params: abstraction parameters
	 * @param nodeWeights: contains node weights already calculated
	 * @param arcWeights: the arc weights to be updated
	 */
	private void updateArcFrequencyWeights(AbstractionParams params, 
										Map<BPMNNode,Double> nodeWeights,
										Map<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>,Double> edgeWeights) {
		Event startEvent = null, endEvent = null;
		for (Event event : diagram.getEvents()) {
			if (event.getEventType() == Event.EventType.START) {
				startEvent = event;
			}
			else if (event.getEventType() == Event.EventType.END) {
				endEvent = event;
			}
		}
		
		// Update edges based on the corresponding DFG
		// This is a rough alignment by matching an edge on the model with
		// a set of edges on the DFG
		// Note: this update ignores BPMN gateway semantics. All gateways
		// are considered as XOR.
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : this.diagram.getEdges()) {
			edgeWeights.put(edge, this.getEdgeWeightFromDFG(edge, params, edgeWeights==this.arcSecondaryWeights));
		}
		
		// Adjust weight of edges adjacent to activity nodes as the previous 
		// step does not consider matching to activity frequency leading to 
		// weight of edges could be higher than weight of the adjacent activities.
		// Note that weight of edges is always less than weight of nodes on the 
		// corresponding DFG due to arc filtering
		for (BPMNNode node : diagram.getNodes()) {
			if (node instanceof Activity) {
				BPMNEdge<? extends BPMNNode, ? extends BPMNNode> inEdge = diagram.getInEdges(node).iterator().next();
				BPMNEdge<? extends BPMNNode, ? extends BPMNNode> outEdge = diagram.getOutEdges(node).iterator().next();
				if (edgeWeights.containsKey(inEdge) && edgeWeights.get(inEdge) > nodeWeights.get(node)) {
					edgeWeights.put(inEdge, nodeWeights.get(node));
				}
				if (edgeWeights.containsKey(outEdge) && edgeWeights.get(outEdge) > nodeWeights.get(node)) {
					edgeWeights.put(outEdge, nodeWeights.get(node));
				}
			}
		}
		
		
		// Fix the merging or splitting edge of gateways to tally with the total weight on the branching edges
		// This is because the previous step ignores the semantics of BPMN gateways
		// Using bread-first search to do in order from the start event down to the end event. 
		// This is to prioritize fixing the early gateways in the order first.
		// If this step makes the input/output edges of activity nodes higher weight than the node itself,
		// it's acceptable because the reason could be the DFG does not show full arcs or the model does not
		// have fitness of 1.
		List<BPMNNode> bfsGateways = this.getNodesByBFS((BPMNNode)startEvent, new HashSet<BPMNNode>(diagram.getGateways()));
		for (BPMNNode node: bfsGateways) {
			double totalBranch = this.getBranchTotalSemanticWeight((Gateway)node, edgeWeights);		
			BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge = 
						(diagram.getInEdges(node).size() == 1) ? diagram.getInEdges(node).iterator().next() : diagram.getOutEdges(node).iterator().next();
			if (edgeWeights.get(edge) != totalBranch) {
				updateGatewayEdgeFrequency(edge, (Gateway)node, edgeWeights);
			}
		}
		
		// Finally, fix the gateway adjacent to the end event, if exist
		// This is to make the end event look nice
		BPMNEdge<? extends BPMNNode, ? extends BPMNNode> endEdge = diagram.getInEdges(endEvent).iterator().next();
		if (endEdge.getSource() instanceof Gateway) {
			updateGatewayEdgeFrequency(endEdge, (Gateway)endEdge.getSource(), edgeWeights);
		}
	}
		
	
	/**
	 * Update arc frequency weights.
	 * @param params: abstraction parameters
	 * @param nodeWeights: contains node weights already calculated
	 * @param arcWeights: the arc weights to be updated
	 */
	private void updateArcFrequencyWeights1(AbstractionParams params, 
										Map<BPMNNode,Double> nodeWeights,
										Map<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>,Double> edgeWeights) {
		
		//Update input and output edges of all activity nodes first
		for (BPMNNode node : diagram.getNodes()) {
			if (node instanceof Activity) {
				BPMNEdge<? extends BPMNNode, ? extends BPMNNode> inEdge = diagram.getInEdges(node).iterator().next();
				BPMNEdge<? extends BPMNNode, ? extends BPMNNode> outEdge = diagram.getOutEdges(node).iterator().next();
				if (!edgeWeights.containsKey(inEdge)) {
					edgeWeights.put(inEdge, nodeWeights.get(node));
				}
				if (!edgeWeights.containsKey(outEdge)) {
					edgeWeights.put(outEdge, nodeWeights.get(node));
				}
			}
		}
		
		// Update single edge adjacent to gateways not updated while all other edges of that gateway have been updated
		boolean edgeUpdated = true;
		while (edgeUpdated) {
			edgeUpdated = false;
			for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge: diagram.getEdges()) {
				if (!edgeWeights.containsKey(edge) && (edge.getSource() instanceof Gateway || edge.getTarget() instanceof Gateway)) {
					BPMNNode node = checkEdgeUpdate(edge, edgeWeights);
					if (node != null) {
						updateGatewayEdgeFrequency(edge, (Gateway)node, edgeWeights);
						edgeUpdated = true;
					}
				}
			}
		}
		
		// Update remaining edges based on the corresponding DFG
		// This is because some edges cannot be updated in the previous steps 
		// E.g. edges between nested gateways
		// Note: this update ignores BPMN gateway semantics
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : this.diagram.getEdges()) {
			if (!edgeWeights.containsKey(edge)) {
				edgeWeights.put(edge, this.getEdgeWeightFromDFG(edge, params, edgeWeights==this.arcSecondaryWeights));
			}
		}
		
		// Fix the merging or splitting edge of gateways to tally with the total weight on the branching edges
		// This is because the previous step ignores the semantics of BPMN gateways
		// Note that this step avoid edges adjacent to an activity
//		for (BPMNNode node: diagram.getNodes()) {
//			if (node instanceof Gateway) {
//				double totalBranch = this.getBranchTotalSemanticWeight((Gateway)node, edgeWeights);		
//				BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge = 
//							(diagram.getInEdges(node).size() == 1) ? diagram.getInEdges(node).iterator().next() : diagram.getOutEdges(node).iterator().next();
//				if (edgeWeights.get(edge) != totalBranch && 
//						!(edge.getSource() instanceof Activity) &&
//						!(edge.getTarget() instanceof Activity)) { 
//					updateGatewayEdgeFrequency(edge, (Gateway)node, edgeWeights);
//				}
//			}
//		}
		
		// Fix branch edge of a gateway to make it tally with other edges in the gateway
		// This is because the previous step does not update the merging/spliting edge 
		// if it's adjacent to an activity
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge: diagram.getEdges()) {
			if (edge.getSource() instanceof Gateway && edge.getTarget() instanceof Gateway) {
				updateGatewayEdgeFrequency(edge, (Gateway)edge.getSource(), edgeWeights);
				updateGatewayEdgeFrequency(edge, (Gateway)edge.getSource(), edgeWeights);
			}
		}
		
		// Fix like above for the gateway edge connecting with the start or end event
		// This is performed last to make the two ends of the diagram look reasonable
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge: diagram.getEdges()) {
			if (edge.getSource() instanceof Event && edge.getTarget() instanceof Gateway) {
				updateGatewayEdgeFrequency(edge, (Gateway)edge.getTarget(), edgeWeights);
			}
			else if (edge.getSource() instanceof Gateway && edge.getTarget() instanceof Event) {
				updateGatewayEdgeFrequency(edge, (Gateway)edge.getSource(), edgeWeights);
			}
		}
	}
	
	// Return the semantic total weight of branch edges of a gateway
	// If AND: returns the largest weight among branch edges
	// If XOR/OR: returns the total weight 
	private double getBranchTotalSemanticWeight(Gateway node, Map<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>,Double> edgeWeights) {
		Collection<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edges;
		edges = (diagram.getInEdges(node).size() >= 2) ? diagram.getInEdges(node) : diagram.getOutEdges(node);
		double total = 0d;
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : edges) {
			if (node.getGatewayType() == Gateway.GatewayType.PARALLEL) {
				total = Math.max(total, edgeWeights.get(edge));
			}
			else {
				total += edgeWeights.get(edge);
			}
		}
		return total;
	}
	
	private BPMNNode checkEdgeUpdate(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge,
									Map<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>,Double> edgeWeights) {
		
		if (!edgeWeights.containsKey(edge)) {
			Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edges = new HashSet<>(diagram.getInEdges(edge.getSource()));
			edges.addAll(diagram.getOutEdges(edge.getSource()));
			edges.remove(edge);
			
			//If all other edges have been updated except the input edge
			//avoid edge to be the output from start event or input to end event
			if (!edges.isEmpty() && edgeWeights.keySet().containsAll(edges)) { 
				return edge.getSource();
			}
			else {
				edges = new HashSet<>(diagram.getInEdges(edge.getTarget()));
				edges.addAll(diagram.getOutEdges(edge.getTarget()));
				edges.remove(edge);
				if (!edges.isEmpty() && edgeWeights.keySet().containsAll(edges)) {
					return edge.getTarget();
				}
			}
		}
		
		return null;
		
	}
	
	/**
	 * Update the weight an edge adjacent to a gateway node given that
	 * all other edges in that gateway have been updated
	 * @param targetEdge: the target edge to be updated 
	 * @param gateway: the node (one of two ends) of the edge
	 * @param arcWeights: the map to be updated
	 */
	private void updateGatewayEdgeFrequency(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> targetEdge, Gateway node,
												Map<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>,Double> edgeWeights) {
		// the target edge is a splitting or merging edge
		if ((diagram.getInEdges(node).contains(targetEdge) && diagram.getInEdges(node).size() == 1) ||
				(diagram.getOutEdges(node).contains(targetEdge) && diagram.getOutEdges(node).size() == 1)) { 
			Collection<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edges = 
					(diagram.getInEdges(node).size() == 1) ? diagram.getOutEdges(node) : diagram.getInEdges(node);
					
			//double maxWeight = this.dfgAbstraction.getTotalStartEventWeight(edgeWeights == this.arcSecondaryWeights);
			double total = this.getBranchTotalSemanticWeight(node, edgeWeights);
			//if (total > maxWeight) total = maxWeight;
			edgeWeights.put(targetEdge, total);
		}
		// the target edge is a branch edge in a split gateway
		else if (diagram.getInEdges(node).size() == 1) { 
			BPMNEdge<? extends BPMNNode, ? extends BPMNNode> inEdge = diagram.getInEdges(node).iterator().next();
			double inWeight = edgeWeights.get(inEdge);
			if (node.getGatewayType() == Gateway.GatewayType.PARALLEL) {
				edgeWeights.put(targetEdge, inWeight);
			}
			else { //XOR and OR
				for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : diagram.getOutEdges(node)) {
					if (edge != targetEdge) inWeight -= edgeWeights.get(edge);
				}
				if (inWeight < 0) inWeight = 0d;
				edgeWeights.put(targetEdge, inWeight);
			}
		}
		// the target edge is a branch edge in a merge gateway
		else if (diagram.getOutEdges(node).size() == 1) { // merging gateway
			BPMNEdge<? extends BPMNNode, ? extends BPMNNode> outEdge = diagram.getOutEdges(node).iterator().next();
			double outWeight = edgeWeights.get(outEdge);
			if (node.getGatewayType() == Gateway.GatewayType.PARALLEL) {
				edgeWeights.put(targetEdge, outWeight);
			}
			else { //XOR and OR
				for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : diagram.getInEdges(node)) {
					if (edge != targetEdge) outWeight -= edgeWeights.get(edge);
				}
				if (outWeight < 0) outWeight = 0d;
				edgeWeights.put(targetEdge, outWeight);
			}
		}
	}
	
	private String getNodeLabel(BPMNNode node, boolean startcode) {
		if (node instanceof Activity) {
    		return node.getLabel() + (startcode ? LogUtils.PLUS_START_CODE.toString() : LogUtils.PLUS_COMPLETE_CODE.toString());
    	}
    	else if (node instanceof Event && ((Event) node).getEventType() == Event.EventType.START) {
    		return SimplifiedLog.START_NAME;
    	}
    	else if (node instanceof Event && ((Event) node).getEventType() == Event.EventType.END) {
    		return SimplifiedLog.END_NAME;
    	}
    	else {
    		return node.getLabel();
    	}
	}
    
    private double computeCost(Set<Arc> arcs, BPMNNode source, BPMNNode target, VisualizationType type, VisualizationAggregation aggregation) {
        double cost = 0;
//        IntHashSet sources = new IntHashSet();
//        IntHashSet targets = new IntHashSet();
//        for(Arc arc : arcs) {
//            sources.add(arc.getSource());
//            targets.add(arc.getTarget());
//        }
        ArcInfoCollector arcInfoCollector = this.logDfg.getArcInfoCollector();
        if(source instanceof Activity && target instanceof Activity) {
            for(Arc arc : arcs)
                cost += arcInfoCollector.getArcInfo(arc, type, aggregation);
            cost /= arcs.size();
        }else {
            double source_cost = computeCost(arcs, source, type, aggregation);
            double target_cost = computeCost(arcs, target, type, aggregation);
            cost = Math.max(source_cost, target_cost);
        }

        return cost;
    }
    
    private double computeCost(Set<Arc> arcs, BPMNNode node, VisualizationType type, VisualizationAggregation aggregation) {
        double cost = 0;
        ArcInfoCollector arcInfoCollector = this.logDfg.getArcInfoCollector();
        if(node instanceof Gateway) {
            if (((Gateway) node).getGatewayType() == Gateway.GatewayType.PARALLEL) {
                for (Arc arc : arcs) cost = Math.max(cost, arcInfoCollector.getArcInfo(arc, type, aggregation));
            } else {
                for (Arc arc : arcs) cost += arcInfoCollector.getArcInfo(arc, type, aggregation);
            }
        }else {
            for (Arc arc : arcs) cost += arcInfoCollector.getArcInfo(arc, type, aggregation);
        }
        return cost;
    }

}
