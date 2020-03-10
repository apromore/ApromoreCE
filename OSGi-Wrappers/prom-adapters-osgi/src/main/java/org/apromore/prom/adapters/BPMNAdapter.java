package org.apromore.prom.adapters;

import java.util.Map;

import org.apache.commons.collections15.map.HashedMap;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;

/**
 * This class is used to convert the BPMN diagram classes between apromore namespace and ProM namespace.
 * For example, org.apromore.processmining.models.graphbased.directed.bpmn.* and 
 * org.processmining.models.graphbased.directed.bpmn
 * 
 * Many process mining libraries have been created in ProM and they are used in Apromore by
 * including all related ProM libraries in Apromore. In doing that quickly, they remain under ProM namespace, 
 * i.e. org.processmining. Many of them are currently embedded in raffaeleconforti-osgi.
 * 
 * Ideally, each ProM module should be made OSGi-compabile bundles under Apromore namespace but it takes time to do. 
 * While doing it incrementally, there will be libraries in Apromore under both ProM and Apromore namespace.
 * 
 * The purpose of adapter classes in this pacakge is to serve as a bridge between libraries under Apromore and 
 * ProM namespaces when some functions need to use both namespaces. For example, ProcessDiscoverer calls to SplitMiner 
 * to mine process models, and also calls to the Alignment library in ProM to calculate model-log fitness because that ProM
 * module has not been brought under Apromore namespace. 
 * 
 * Note that these adapter classes are only temporary. Once more process mining libraries are brought under Apromore
 * namspace, there will be less need for adapter classes.
 * 
 * @author Bruce Nguyen
 *
 */
public class BPMNAdapter {
    public static org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram convert(BPMNDiagram diagram) {
    	Map<BPMNNode, org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode> mapping = new HashedMap<>();
    	org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram d = new org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl(diagram.getLabel());
    	
    	for (BPMNNode node : diagram.getNodes()) {
    		org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode newNode = null;
    		if (node instanceof Activity) {
    			Activity a = (Activity)node;
    			newNode = d.addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(), 
    					a.isBMultiinstance(), a.isBCollapsed());
    		}
    		else if (node instanceof Event && ((Event)node).getEventType() == EventType.START) {
    			Event e = (Event)node;
    			newNode = d.addEvent(e.getLabel(),
    					org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType.START, 
    					org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger.NONE, 
    					org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse.CATCH, 
    					false, null);
    		}
    		else if (node instanceof Event && ((Event)node).getEventType() == EventType.END) {
    			Event e = (Event)node;
    			newNode = d.addEvent(e.getLabel(),
    					org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType.END, 
    					org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger.NONE, 
    					org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse.CATCH, 
    					false, null);
    		}    
    		else {
    			// Unsupported model elements
    		}
    		
    		if (newNode != null) {
    			mapping.put(node, newNode);
    		}
    	}
    	
    	for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : diagram.getEdges()) {
    		if (mapping.containsKey(edge.getSource()) && mapping.containsKey(edge.getTarget())) {
    			d.addFlow(mapping.get(edge.getSource()), mapping.get(edge.getTarget()), edge.getLabel());
    		}
    	}
    	
    	return d;
    }
    
    public static BPMNDiagram convertReverse(org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram diagram) {
    	Map<org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode, BPMNNode> mappingReverse = new HashedMap<>();
    	BPMNDiagram d = new BPMNDiagramImpl(diagram.getLabel());
    	
    	for (org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode node : diagram.getNodes()) {
    		BPMNNode newNode = null;
    		if (node instanceof org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity) {
    			org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity a = (org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity)node;
    			newNode = d.addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(), 
    					a.isBMultiinstance(), a.isBCollapsed());
    		}
    		else if (node instanceof org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event && 
    				((org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event)node).getEventType() == 
    						org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType.START) {
    			org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event e = (org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event)node;
    			newNode = d.addEvent(e.getLabel(),
    					Event.EventType.START, 
    					Event.EventTrigger.NONE, 
    					Event.EventUse.CATCH, 
    					false, null);
    		}
    		else if (node instanceof org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event && 
    				((org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event)node).getEventType() == 
    						org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType.END) {
    			org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event e = (org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event)node;
    			newNode = d.addEvent(e.getLabel(),
    					Event.EventType.END, 
    					Event.EventTrigger.NONE, 
    					Event.EventUse.CATCH, 
    					false, null);
    		}    
    		else {
    			// Unsupported model elements
    		}
    		
    		if (newNode != null) {
    			mappingReverse.put(node, newNode);
    		}
    	}
    	
    	for (org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge edge : diagram.getEdges()) {
    		if (mappingReverse.containsKey(edge.getSource()) && mappingReverse.containsKey(edge.getTarget())) {
    			d.addFlow(mappingReverse.get(edge.getSource()), mappingReverse.get(edge.getTarget()), edge.getLabel());
    		}
    	}
    	
    	return d;
    }
}
