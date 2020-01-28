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

package de.hpi.bpmn2_0.factory.node;

import java.util.Map;

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.factory.BPMNElement;
import de.hpi.bpmn2_0.factory.configuration.Configuration;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.artifacts.Artifact;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.choreography.CallChoreography;
import de.hpi.bpmn2_0.model.choreography.Choreography;
import de.hpi.bpmn2_0.model.choreography.ChoreographyActivity;
import de.hpi.bpmn2_0.model.choreography.ChoreographyLoopType;
import de.hpi.bpmn2_0.model.choreography.ChoreographyTask;
import de.hpi.bpmn2_0.model.choreography.SubChoreography;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioMetaData;

/**
 * Factory that creates elements of a choreography diagram.
 * 
 * @author Sven Wagner-Boysen
 *
 */
@StencilId({
	"ChoreographyTask",
	"ChoreographySubprocessCollapsed",
	"ChoreographySubprocessExpanded"
})
public class ChoreographyActivityFactory extends AbstractShapeFactory {

	public BPMNElement createBpmnElement(GenericShape shape, Configuration configuration, State state) throws BpmnConverterException {
		BPMNElement bpmnElement = super.createBpmnElement(shape, configuration, state);
		
		if(bpmnElement != null) {
			handleLinkedDiagrams(bpmnElement.getNode(), shape, configuration);			
		}
		
		return bpmnElement;
	}
	
	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected ChoreographyActivity createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		try {
			ChoreographyActivity activity = (ChoreographyActivity) this.invokeCreatorMethod(shape);
			activity.setId(shape.getResourceId());
			activity.setName(shape.getProperty("name"));
			
			/* Call choreography */
			String isCallActivity = shape.getProperty("callacitivity");
			if(isCallActivity != null && isCallActivity.equals("true")) {
				activity = new CallChoreography(activity);
			}
			
			/* Loop type */
			String loopType = shape.getProperty("looptype");
			if(loopType != null) {
				if(loopType.equalsIgnoreCase("None")) {
					activity.setLoopType(ChoreographyLoopType.NONE);
				}
				else if(loopType.equalsIgnoreCase("Standard")) {
					activity.setLoopType(ChoreographyLoopType.STANDARD);
				}
				else if(loopType.equalsIgnoreCase("MultiInstance")) {
					activity.setLoopType(ChoreographyLoopType.MULTI_INSTANCE_PARALLEL);
				} 
				else if(loopType.equalsIgnoreCase("Sequential")) {
					activity.setLoopType(ChoreographyLoopType.MULTI_INSTANCE_SEQUENTIAL);
				}
			}
			
			return activity;
		} catch (Exception e) {
			/* Wrap exceptions into specific BPMNConverterException */
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}
	
	/**
	 * Creator method for a choreography task.
	 * 
	 * @param shape
	 * 		The resource shape
	 * @return
	 * 		the {@link ChoreographyTask}
	 */
	@StencilId("ChoreographyTask")
	public ChoreographyTask createChoreographyTask(GenericShape shape) {
		return new ChoreographyTask();
	}
	
	/**
	 * Creator method for a collapsed choreography subprocess.
	 * 
	 * @param shape
	 * 		The resource shape
	 * @return
	 * 		the {@link SubChoreography}
	 */
	@StencilId({
		"ChoreographySubprocessCollapsed",
		"ChoreographySubprocessExpanded"
	})
	public SubChoreography createChoreographySubprocessCollapsed(GenericShape shape) {
		return new SubChoreography();
	}

	// @Override
	protected BPMNShape createDiagramElement(GenericShape shape) {
		BPMNShape diagramElement = super.createDiagramElement(shape);
		
		/* Expanded subprocess */
		if(shape.getStencilId().equals("ChoreographySubprocessExpanded")) {
			diagramElement.setIsExpanded(Boolean.TRUE);
		}
		/* Collapsed subprocess */
		else if(shape.getStencilId().equals("ChoreographySubprocessCollapsed")) {
			diagramElement.setIsExpanded(Boolean.FALSE);
		}
		
		return diagramElement;
	}
	
	/**
	 * Transforms linked diagrams of collapsed subprocess and event subprocess.
	 * 
	 * @param baseElement
	 * @param shape
	 * @param config
	 */
	private void handleLinkedDiagrams(BaseElement baseElement, GenericShape shape, Configuration config) {
		if(baseElement == null || !(baseElement instanceof SubChoreography) || !shape.getStencilId().matches(".*Collapsed.*")) {
			return;
		}
		
		/*
		 * Diagram Link
		 */
		String entry = shape.getProperty("entry");
		if(entry == null || entry.length() == 0) {
			return;
		}
		
		SignavioMetaData metaData = new SignavioMetaData("entry", entry);
		baseElement.getOrCreateExtensionElements().add(metaData);
		
		Definitions linkedDiagram = SubprocessFactory.retrieveDefinitionsOfLinkedDiagram(entry, config);
		
		if(linkedDiagram == null || linkedDiagram.getRootElement().size() == 0) {
			return;
		}
		
		for(BaseElement rootEl : linkedDiagram.getRootElement()) {
			if(rootEl instanceof Choreography) {
				Choreography linkedChoreo = (Choreography) rootEl;
				
				/* Sub choreography */
				if(baseElement instanceof SubChoreography) {
					SubChoreography subChoreography = (SubChoreography) baseElement;
					
					/* 
					 * Add flow elements and artifacts including their diagram
					 * elements
					 */
					for(FlowElement flowEl : linkedChoreo.getFlowElement()) {
						subChoreography.getFlowElement().add(flowEl);
						subChoreography._diagramElements.add(flowEl._diagramElement);
					}
					
					for(Artifact artifact : linkedChoreo.getArtifact()) {
						subChoreography.getArtifact().add(artifact);
						subChoreography._diagramElements.add(artifact._diagramElement);
					}
					
				}
				
				/* Call choreography */
				else if(baseElement instanceof CallChoreography) {
					CallChoreography callChoreography = (CallChoreography) baseElement;
					callChoreography.setCalledChoreographyRef(linkedChoreo);
					
					for(BaseElement baseEl : linkedChoreo.getChilds()) {
						callChoreography._diagramElements.add(baseEl._diagramElement);
					}
					
				}
			} 
		}
	}
}
