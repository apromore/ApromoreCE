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

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.participant.Lane;
import de.hpi.bpmn2_0.model.participant.LaneSet;
import de.hpi.bpmn2_0.model.participant.Participant;
import de.hpi.bpmn2_0.model.participant.ParticipantMultiplicity;
import de.hpi.diagram.SignavioUUID;

/**
 * Factory to create lanes and pools
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 * 
 */
@StencilId( { "CollapsedPool", "VerticalPool", "CollapsedVerticalPool", "Pool", "Lane", "VerticalLane" })
public class LaneFactory extends AbstractShapeFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected BaseElement createProcessElement(GenericShape shape)
			throws BpmnConverterException {

		if (shape.getStencilId().equals("CollapsedPool") 
				|| shape.getStencilId().equals("Pool")
				|| shape.getStencilId().equals("CollapsedVerticalPool")
				|| shape.getStencilId().equals("VerticalPool")) {
			Participant participant = new Participant();
			
			/* Set name attribute */
			String name = shape.getProperty("name");
			if(name != null && !(name.length() == 0))
				participant.setName(name);
			
			participant.setId(shape.getResourceId());
			
			/* Participant Multiplicity */
			String isMultipleParticipant = shape.getProperty("multiinstance");
			if(isMultipleParticipant != null && isMultipleParticipant.equals("true")) {
				ParticipantMultiplicity multiplicit = new ParticipantMultiplicity();
				
				/* Maximum */
				String maximum = shape.getProperty("maximum");
				if(maximum != null) {
					multiplicit.setMaximum(Integer.valueOf(maximum));
				}
				
				/* Minimum */
				String minimum = shape.getProperty("minimum");
				if(minimum != null) {
					multiplicit.setMinimum(Integer.valueOf(minimum));
				}
				
				participant.setParticipantMultiplicity(multiplicit);
			}
			
			/* Process type */
			String processType = shape.getProperty("processtype");
			if(processType != null && !(processType.length() == 0)) {
				participant._processType = processType;
			}
			
			/* Process isClosed */
			String isClosed = shape.getProperty("isclosed");
			if(isClosed != null && !(isClosed.length() == 0))
				participant._isClosed = isClosed;
			
			/* Process isExecutable */
			String isExecutable = shape.getProperty("isexecutable");
			if(isExecutable != null && !(isExecutable.length() == 0))
				participant._isExecutable = isExecutable;
			
			this.setCommonAttributes(participant, shape);
			return participant;
		}

		if (shape.getStencilId().equals("Pool")) {
//			LaneSet poolLaneSet = new LaneSet();
//			this.setCommonAttributes(poolLaneSet, shape);
//			poolLaneSet.setId(shape.getResourceId());
//			
//			/* Name */
//			String name = shape.getProperty("name");
//			if(name != null && !(name.length() == 0)) {
//				poolLaneSet.setName(name);
//			}
//			
//			/* Process type */
//			String processType = shape.getProperty("processtype");
//			if(processType != null && !(processType.length() == 0)) {
//				poolLaneSet._processType = processType;
//			}
//			
//			/* Process isClosed */
//			String isClosed = shape.getProperty("isclosed");
//			if(isClosed != null && !(isClosed.length() == 0))
//				poolLaneSet._isClosed = isClosed;
//	
//			return poolLaneSet;
		}

		Lane lane = new Lane();
		this.setCommonAttributes(lane, shape);
		lane.setId(shape.getResourceId());
		
		/* Set name attribute */
		String name = shape.getProperty("name");
		if(name != null && !(name.length() == 0))
			lane.setName(name);
		
		lane.setLane(lane);

		if (this.hasChildLanes(shape)) {
			LaneSet laneSet = new LaneSet();
			laneSet.setParentLane(lane);
			laneSet.setId(SignavioUUID.generate());
			lane.setChildLaneSet(laneSet);
		}

		return lane;
	}

	private boolean hasChildLanes(GenericShape<?,?> shape) {
		for (GenericShape childShape : shape.getChildShapesReadOnly()) {
			if (childShape.getStencilId().endsWith("Lane")) {
				return true;
			}
		}
		return false;
	}
	
	// @Override
	protected BPMNShape createDiagramElement(GenericShape shape) {
		BPMNShape swimLaneShape = super.createDiagramElement(shape);
		if(shape.getStencilId().equals("Pool")
			|| shape.getStencilId().equals("CollapsedPool")
			|| shape.getStencilId().equals("Lane")) {
			swimLaneShape.setIsHorizontal(true);
		} else {
			swimLaneShape.setIsHorizontal(false);
		}
				
		return swimLaneShape;
	}
}
