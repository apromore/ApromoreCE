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
import de.hpi.bpmn2_0.factory.BPMNElement;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FormalExpression;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.activity.misc.Operation;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.data_object.Message;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;
import de.hpi.bpmn2_0.model.event.CancelEventDefinition;
import de.hpi.bpmn2_0.model.event.CompensateEventDefinition;
import de.hpi.bpmn2_0.model.event.ConditionalEventDefinition;
import de.hpi.bpmn2_0.model.event.ErrorEventDefinition;
import de.hpi.bpmn2_0.model.event.Escalation;
import de.hpi.bpmn2_0.model.event.EscalationEventDefinition;
import de.hpi.bpmn2_0.model.event.Event;
import de.hpi.bpmn2_0.model.event.IntermediateCatchEvent;
import de.hpi.bpmn2_0.model.event.LinkEventDefinition;
import de.hpi.bpmn2_0.model.event.MessageEventDefinition;
import de.hpi.bpmn2_0.model.event.SignalEventDefinition;
import de.hpi.bpmn2_0.model.event.TerminateEventDefinition;
import de.hpi.bpmn2_0.model.event.TimerEventDefinition;
import de.hpi.bpmn2_0.model.misc.Error;
import de.hpi.bpmn2_0.model.misc.Signal;
import de.hpi.diagram.SignavioUUID;

/**
 * Factory to create intermediate catching Events
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 * 
 */
@StencilId( { "IntermediateMessageEventCatching", "IntermediateTimerEvent",
		"IntermediateEscalationEvent", "IntermediateConditionalEvent",
		"IntermediateLinkEventCatching", "IntermediateErrorEvent",
		"IntermediateCancelEvent", "IntermediateCompensationEventCatching",
		"IntermediateSignalEventCatching", "IntermediateMultipleEventCatching",
		"IntermediateParallelMultipleEventCatching" })
public class IntermediateCatchEventFactory extends AbstractShapeFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected BaseElement createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		try {
			IntermediateCatchEvent icEvent = (IntermediateCatchEvent) this
					.invokeCreatorMethod(shape);
			icEvent.setId(shape.getResourceId());
			icEvent.setName(shape.getProperty("name"));

			
			icEvent.setCancelActivity(shape
					.getProperty("boundarycancelactivity2"));

			return icEvent;
		} catch (Exception e) {
			/* Wrap exceptions into specific BPMNConverterException */
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}

	}

	/* Creator methods for different event definitions */

	@StencilId("IntermediateCompensationEventCatching")
	public IntermediateCatchEvent createCompensateEvent(GenericShape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();
		CompensateEventDefinition compEvDef = new CompensateEventDefinition();
		
		/* Activity Reference */
		String activityRef = shape.getProperty("activityref");
		if(activityRef != null && !(activityRef.length() == 0)) {
			Task taskRef = new Task();
			taskRef.setId(activityRef);
			compEvDef.setActivityRef(taskRef);
		}
		
		/* Wait for Completion */
		String waitForCompletion = shape.getProperty("waitforcompletion");
		if(waitForCompletion != null && waitForCompletion.equals("false")) {
			compEvDef.setWaitForCompletion(false);
		} else {
			compEvDef.setWaitForCompletion(true);
		}
		
		icEvent.getEventDefinition().add(compEvDef);
		return icEvent;
	}

	@StencilId("IntermediateTimerEvent")
	public IntermediateCatchEvent createTimerEvent(GenericShape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();

		TimerEventDefinition timerEvDef = new TimerEventDefinition();
		
		/* Time Date */
		String timeDate = shape.getProperty("timedate");
		if(timeDate != null && !(timeDate.length() == 0)) {
			FormalExpression expr = new FormalExpression(timeDate);
			timerEvDef.setTimeDate(expr);
		}
		
		/* Time Cycle */
		String timeCycle = shape.getProperty("timecycle");
		if(timeCycle != null && !(timeCycle.length() == 0)) {
			FormalExpression expr = new FormalExpression(timeCycle);
			timerEvDef.setTimeCycle(expr);
		}
		
		/* Time Duration */
		String timeDuration = shape.getProperty("timeduration");
		if(timeDuration != null && !(timeDuration.length() == 0)) {
			FormalExpression expr = new FormalExpression(timeDuration);
			timerEvDef.setTimeDuration(expr);
		}
		
		icEvent.getEventDefinition().add(timerEvDef);

		return icEvent;
	}

	@StencilId("IntermediateMessageEventCatching")
	public IntermediateCatchEvent createMessageEvent(GenericShape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();

		MessageEventDefinition messageEvDef = new MessageEventDefinition();
		
		
		/* Message name */
		String messageName = shape.getProperty("messagename");
		if(messageName != null && !(messageName.length() == 0)) {
			Message message = new Message();
			message.setName(messageName);
			messageEvDef.setMessageRef(message);
		}
		
		/* Operation name */
		String operationName = shape.getProperty("operationname");
		if(operationName != null && !(operationName.length() == 0)) {
			Operation operation = new Operation();
			operation.setName(operationName);
			messageEvDef.setOperationRef(operation);
		}
		
		icEvent.getEventDefinition().add(messageEvDef);

		return icEvent;
	}

	@StencilId("IntermediateEscalationEvent")
	public IntermediateCatchEvent createEscalationEvent(GenericShape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();

		EscalationEventDefinition escalDef = new EscalationEventDefinition();
		
		Escalation escalation = new Escalation();
		
		/* Escalation name */
		String escalationName = shape.getProperty("escalationname");
		if(escalationName != null && !(escalationName.length() == 0)) {
			escalation.setName(escalationName);
		}
		
		/* Escalation code */
		String escalationCode = shape.getProperty("escalationcode");
		if(escalationCode != null && !(escalationCode.length() == 0)) {
			escalation.setEscalationCode(escalationCode);
		}
		
		escalDef.setEscalationRef(escalation);
		icEvent.getEventDefinition().add(escalDef);

		return icEvent;
	}

	@StencilId("IntermediateConditionalEvent")
	public IntermediateCatchEvent createConditionalEvent(GenericShape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();

		ConditionalEventDefinition conDef = new ConditionalEventDefinition();

		/* Set condition attribute as FormalExpression */
		String condition = shape.getProperty("condition");
		if (condition != null && !(condition.length() == 0))
			conDef.setCondition(new FormalExpression(condition));

		icEvent.getEventDefinition().add(conDef);

		return icEvent;
	}

	@StencilId("IntermediateLinkEventCatching")
	public IntermediateCatchEvent createLinkEvent(GenericShape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();

		LinkEventDefinition linkDef = new LinkEventDefinition();

		/* Set required name attribute */
		String name = shape.getProperty("name");
		if (name != null && !(name.length() == 0))
			linkDef.setName(name);
		
		/* Set source reference */
		String sourceEntry = shape.getProperty("entry");
		if(sourceEntry != null && sourceEntry.length() != 0) {
			linkDef.getSource().add(sourceEntry);
		}
		
		
		icEvent.getEventDefinition().add(linkDef);

		return icEvent;
	}

	@StencilId("IntermediateErrorEvent")
	public IntermediateCatchEvent createErrorEvent(GenericShape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();

		ErrorEventDefinition errorDef = new ErrorEventDefinition();
		
		Error error = new Error();
		
		/* Error name */
		String errorName = shape.getProperty("errorname");
		if(errorName != null && !(errorName.length() == 0)) {
			error.setName(errorName);
		}
		
		/* Error code */
		String errorCode = shape.getProperty("errorcode");
		if(errorCode != null && !(errorCode.length() == 0)) {
			error.setErrorCode(errorCode);
		}
		
		errorDef.setErrorRef(error);
		
		icEvent.getEventDefinition().add(errorDef);

		return icEvent;
	}

	@StencilId("IntermediateCancelEvent")
	public IntermediateCatchEvent createCancelEvent(GenericShape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();

		CancelEventDefinition cancelDef = new CancelEventDefinition();
		icEvent.getEventDefinition().add(cancelDef);

		return icEvent;
	}

	@StencilId("IntermediateSignalEventCatching")
	public IntermediateCatchEvent createSignalEvent(GenericShape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();

		SignalEventDefinition signalDef = new SignalEventDefinition();
		
		Signal signal = new Signal();
		
		/* Signal ID */
		signal.setId(SignavioUUID.generate());
		
		/* Signal name */
		String signalName = shape.getProperty("signalname");
		if(signalName != null && !(signalName.length() == 0)) {
			signal.setName(signalName);
		}
		
		signalDef.setSignalRef(signal);
		icEvent.getEventDefinition().add(signalDef);

		return icEvent;
	}

	@StencilId("IntermediateMultipleEventCatching")
	public IntermediateCatchEvent createMultipleEvent(GenericShape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();
		
		icEvent.getEventDefinition().add(new CancelEventDefinition());
		icEvent.getEventDefinition().add(new TerminateEventDefinition());
		
		icEvent.setParallelMultiple(false);

		return icEvent;
	}

	@StencilId("IntermediateParallelMultipleEventCatching")
	public IntermediateCatchEvent createParallelMultipleEvent(GenericShape shape) {
		IntermediateCatchEvent icEvent = new IntermediateCatchEvent();
		
		icEvent.getEventDefinition().add(new CancelEventDefinition());
		icEvent.getEventDefinition().add(new TerminateEventDefinition());
		
		icEvent.setParallelMultiple(true);

		return icEvent;
	}

	public static void changeToBoundaryEvent(BPMNElement activity,
			BPMNElement event) {
		if (!(activity.getNode() instanceof Activity)
				|| !(event.getNode() instanceof IntermediateCatchEvent)) {
			System.err.println("changeToBoundaryEvent - ERROR: activity(" + (activity.getNode() instanceof Activity) + ") : event(" + (event.getNode() instanceof IntermediateCatchEvent) + ")");
			return;
		}

                Activity a = (Activity) activity.getNode();
		IntermediateCatchEvent ice = (IntermediateCatchEvent) event.getNode();
		BoundaryEvent bEvent = new BoundaryEvent();

		/* duplicating the intermediate catch event into the boundary event */

		bEvent.setId(ice.getId());
		bEvent.setName(ice.getName());
		bEvent.getEventDefinition().addAll(ice.getEventDefinition());
		bEvent.setAttachedToRef(a);
		a.getBoundaryEventRefs().add(bEvent);
		bEvent.getAttachedToRef().getAttachedBoundaryEvents().addAll(bEvent.getAttachedToRef().getBoundaryEventRefs());
		bEvent.setCancelActivity(ice.getCancelActivity());
		bEvent.setParallelMultiple(ice.isParallelMultiple());

		for(de.hpi.bpmn2_0.model.connector.Edge e : ice.getOutgoing()) bEvent.getOutgoing().add(e);
		bEvent.setSubProcess(a.getSubProcess());

		bEvent.setSubChoreography(ice.getSubChoreography());
		bEvent.setProcessid(a.getProcessid());
		bEvent.setProcess(a.getProcess());
		for(Object o : ice.getAny()) bEvent.getAny().add(o);
		for(de.hpi.bpmn2_0.model.extension.PropertyListItem p : ice.getAdditionalProperties()) bEvent.getAdditionalProperties().add(p);
		for(de.hpi.bpmn2_0.model.connector.SequenceFlow f : ice.get_outgoingSequenceFlows()) bEvent.get_outgoingSequenceFlows().add(f);

		event.setNode(bEvent);

		if(event.getShape() instanceof BPMNShape) ((BPMNShape) event.getShape()).setBpmnElement(bEvent);

		/* Handle boundary events as child elements of a lane */
		if (ice.getLane() != null) {
			/* Exchange intermediate event with boundary event */
			bEvent.setLane(ice.getLane());
			int index = bEvent.getLane().getFlowNodeRef().indexOf(ice);
			bEvent.getLane().getFlowNodeRef().remove(ice);
			if(index != -1) {
				bEvent.getLane().getFlowNodeRef().add(index, bEvent);
			} else {
				bEvent.getLane().getFlowNodeRef().add(bEvent);
			}
		}
	}
}
