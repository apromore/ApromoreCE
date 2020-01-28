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

package de.hpi.bpmn2_0.transformation;

import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.activity.AdHocSubProcess;
import de.hpi.bpmn2_0.model.activity.CallActivity;
import de.hpi.bpmn2_0.model.activity.SubProcess;
import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.activity.Transaction;
import de.hpi.bpmn2_0.model.activity.type.BusinessRuleTask;
import de.hpi.bpmn2_0.model.activity.type.ManualTask;
import de.hpi.bpmn2_0.model.activity.type.ReceiveTask;
import de.hpi.bpmn2_0.model.activity.type.ScriptTask;
import de.hpi.bpmn2_0.model.activity.type.SendTask;
import de.hpi.bpmn2_0.model.activity.type.ServiceTask;
import de.hpi.bpmn2_0.model.activity.type.UserTask;
import de.hpi.bpmn2_0.model.artifacts.Artifact;
import de.hpi.bpmn2_0.model.artifacts.Group;
import de.hpi.bpmn2_0.model.artifacts.TextAnnotation;
import de.hpi.bpmn2_0.model.bpmndi.BPMNEdge;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.bpmndi.di.DiagramElement;
import de.hpi.bpmn2_0.model.bpmndi.di.LabeledEdge;
import de.hpi.bpmn2_0.model.bpmndi.di.LabeledShape;
import de.hpi.bpmn2_0.model.bpmndi.di.Node;
import de.hpi.bpmn2_0.model.bpmndi.di.Shape;
import de.hpi.bpmn2_0.model.choreography.CallChoreography;
import de.hpi.bpmn2_0.model.choreography.ChoreographyActivity;
import de.hpi.bpmn2_0.model.choreography.ChoreographyTask;
import de.hpi.bpmn2_0.model.choreography.SubChoreography;
import de.hpi.bpmn2_0.model.connector.Association;
import de.hpi.bpmn2_0.model.connector.DataAssociation;
import de.hpi.bpmn2_0.model.connector.DataInputAssociation;
import de.hpi.bpmn2_0.model.connector.DataOutputAssociation;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.conversation.CallConversation;
import de.hpi.bpmn2_0.model.conversation.Conversation;
import de.hpi.bpmn2_0.model.conversation.ConversationLink;
import de.hpi.bpmn2_0.model.conversation.ConversationNode;
import de.hpi.bpmn2_0.model.conversation.SubConversation;
import de.hpi.bpmn2_0.model.data_object.AbstractDataObject;
import de.hpi.bpmn2_0.model.data_object.DataInput;
import de.hpi.bpmn2_0.model.data_object.DataObject;
import de.hpi.bpmn2_0.model.data_object.DataObjectReference;
import de.hpi.bpmn2_0.model.data_object.DataOutput;
import de.hpi.bpmn2_0.model.data_object.DataStoreReference;
import de.hpi.bpmn2_0.model.data_object.Message;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;
import de.hpi.bpmn2_0.model.event.CatchEvent;
import de.hpi.bpmn2_0.model.event.EndEvent;
import de.hpi.bpmn2_0.model.event.Event;
import de.hpi.bpmn2_0.model.event.ImplicitThrowEvent;
import de.hpi.bpmn2_0.model.event.IntermediateCatchEvent;
import de.hpi.bpmn2_0.model.event.IntermediateThrowEvent;
import de.hpi.bpmn2_0.model.event.StartEvent;
import de.hpi.bpmn2_0.model.event.ThrowEvent;
import de.hpi.bpmn2_0.model.gateway.ComplexGateway;
import de.hpi.bpmn2_0.model.gateway.EventBasedGateway;
import de.hpi.bpmn2_0.model.gateway.ExclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.model.gateway.GatewayWithDefaultFlow;
import de.hpi.bpmn2_0.model.gateway.InclusiveGateway;
import de.hpi.bpmn2_0.model.gateway.ParallelGateway;
import de.hpi.bpmn2_0.model.participant.Lane;
import de.hpi.bpmn2_0.model.participant.Participant;

/**
 * Implementation of @link Visitor which does nothing upon visiting each element.
 *
 * Each visit method does nothing except recursively call the visit methods of <var>that</var>'s superclasses.
 * @link BaseElement and @link DiagramElement are considered root elements and have no superclasses to class.
 *
 * Note that the knowledge about the class hierarchy hardcoded into this class needs to be manually updated whenever the class hierarchy is changed.
 *
 * @author Simon Raboczi
 */
public abstract class AbstractVisitor implements Visitor {
	
	public void visitDiagramElement(DiagramElement that) { /* No superclass call -- top of the diagram element hierarchy */ }
	
	public void visitBpmnEdge(BPMNEdge that) { visitLabeledEdge(that); }
	
	public void visitBpmnShape(BPMNShape that) { visitLabeledShape(that); }
	
	public void visitAbstractDataObject(AbstractDataObject that) { visitFlowElement(that); }

	public void visitActivity(Activity that) { visitFlowNode(that); }

	public void visitAdHocSubProcess(AdHocSubProcess that) { visitSubProcess(that); }

	public void visitArtifact(Artifact that) { visitFlowNode(that); }

	public void visitAssociation(Association that) { visitEdge(that); }

	public void visitBaseElement(BaseElement that) { /* No superclass call -- top of the process element hierarchy */ }

	public void visitBoundaryEvent(BoundaryEvent that) { visitIntermediateCatchEvent(that); }

	public void visitBusinessRuleTask(BusinessRuleTask that) { visitTask(that); }

	public void visitCallActivity(CallActivity that) { visitActivity(that); }

	public void visitCallChoreography(CallChoreography that) { visitChoreographyActivity(that); }
	
	public void visitCallConversation(CallConversation that) { visitConversationNode(that); }

	public void visitCatchEvent(CatchEvent that) { visitEvent(that); }

	public void visitChoreographyActivity(ChoreographyActivity that) { visitActivity(that); }

	public void visitChoreographyTask(ChoreographyTask that) { visitChoreographyActivity(that); }

	public void visitComplexGateway(ComplexGateway that) { visitGatewayWithDefaultFlow(that); }

	public void visitConversation(Conversation that) { visitConversationNode(that); }

	public void visitConversationLink(ConversationLink that) { visitEdge(that); }

	public void visitConversationNode(ConversationNode that) { visitFlowNode(that); }

	public void visitDataAssociation(DataAssociation that) { visitEdge(that); }

	public void visitDataInput(DataInput that) { visitAbstractDataObject(that); }

	public void visitDataInputAssociation(DataInputAssociation that) { visitDataAssociation(that); }

	public void visitDataObject(DataObject that) { visitAbstractDataObject(that); }
	
	public void visitDataObjectReference(DataObjectReference that) { visitAbstractDataObject(that); }

	public void visitDataOutput(DataOutput that) { visitAbstractDataObject(that); }

	public void visitDataOutputAssociation(DataOutputAssociation that) { visitDataAssociation(that); }

	public void visitDataStoreReference(DataStoreReference that) { visitAbstractDataObject(that); }

	public void visitEdge(Edge that) { visitFlowElement(that); }

	public void visitEndEvent(EndEvent that) { visitThrowEvent(that); }

	public void visitEvent(Event that) { visitFlowNode(that); }

	public void visitEventBasedGateway(EventBasedGateway that) { visitGateway(that); }

	public void visitExclusiveGateway(ExclusiveGateway that) { visitGatewayWithDefaultFlow(that); }

	public void visitFlowElement(FlowElement that) { visitBaseElement(that); }

	public void visitFlowNode(FlowNode that) { visitFlowElement(that); }

	public void visitGateway(Gateway that) { visitFlowNode(that); }

	public void visitGatewayWithDefaultFlow(GatewayWithDefaultFlow that) { visitGateway(that); }

	public void visitGroup(Group that) { visitArtifact(that); }

	public void visitImplicitThrowEvent(ImplicitThrowEvent that) { visitThrowEvent(that); }
	
	public void visitInclusiveGateway(InclusiveGateway that) { visitGatewayWithDefaultFlow(that); }

	public void visitIntermediateCatchEvent(IntermediateCatchEvent that) { visitCatchEvent(that); }

	public void visitIntermediateThrowEvent(IntermediateThrowEvent that) { visitThrowEvent(that); }

	public void visitLane(Lane that) { visitFlowElement(that); }

	public void visitManualTask(ManualTask that) { visitTask(that); }

	public void visitMessageFlow(MessageFlow that) { visitEdge(that); }

	public void visitParallelGateway(ParallelGateway that) { visitGateway(that); }

	public void visitParticipant(Participant that) { visitFlowNode(that); }

	public void visitReceiveTask(ReceiveTask that) { visitTask(that); }

	public void visitScriptTask(ScriptTask that) { visitTask(that); }

	public void visitSendTask(SendTask that) { visitTask(that); }

	public void visitSequenceFlow(SequenceFlow that) { visitEdge(that); }

	public void visitServiceTask(ServiceTask that) { visitTask(that); }

	public void visitStartEvent(StartEvent that) { visitCatchEvent(that); }

	public void visitSubChoreography(SubChoreography that) { visitChoreographyActivity(that); }

	public void visitSubConversation(SubConversation that) { visitConversationNode(that); }

	public void visitSubProcess(SubProcess that) { visitActivity(that); }

	public void visitTask(Task that) { visitActivity(that); }

	public void visitTextAnnotation(TextAnnotation that) { visitArtifact(that); }

	public void visitThrowEvent(ThrowEvent that) { visitEvent(that); }

	public void visitTransaction(Transaction that) { visitSubProcess(that); }

	public void visitUserTask(UserTask that) { visitTask(that); }

	public void visitMessage(Message that) { visitFlowNode(that); }


	// Additional methods for DiagramElements which have an acceptVisitor method, but don't have corresponding methods in Visitor

	public void visitDiagramEdge(de.hpi.bpmn2_0.model.bpmndi.di.Edge that) { visitDiagramElement(that); }

	public void visitLabeledEdge(LabeledEdge that) { visitDiagramEdge(that); }

	public void visitLabeledShape(LabeledShape that) { visitShape(that); }

	public void visitNode(Node that) { visitDiagramElement(that); }

	public void visitShape(Shape that) { visitNode(that); }
}
