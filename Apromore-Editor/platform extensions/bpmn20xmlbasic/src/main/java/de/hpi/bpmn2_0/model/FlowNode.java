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

package de.hpi.bpmn2_0.model;

import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.connector.Association;
import de.hpi.bpmn2_0.model.connector.AssociationDirection;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.data_object.AbstractDataObject;
import de.hpi.bpmn2_0.model.data_object.Message;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;
import de.hpi.bpmn2_0.model.event.CompensateEventDefinition;
import de.hpi.bpmn2_0.model.event.Event;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * Java class for tFlowNode complex type.
 * <p/>
 * <p/>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p/>
 * <pre>
 * &lt;complexType name=&quot;tFlowNode&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{http://www.omg.org/bpmn20}tFlowElement&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;incoming&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}QName&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;outgoing&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}QName&quot; maxOccurs=&quot;unbounded&quot; minOccurs=&quot;0&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tFlowNode")
@XmlSeeAlso({Event.class,
        Message.class,
// ChoreographyActivity.class,
        Gateway.class, Activity.class, AbstractDataObject.class})
public abstract class FlowNode extends FlowElement {

    /* Attributes */

    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    @XmlElement(name = "incoming")
    protected List<SequenceFlow> _incomingSequenceFlows;

    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    @XmlElement(name = "outgoing")
    protected List<SequenceFlow> _outgoingSequenceFlows;
    
    protected Object nameRef = null;

    /**
     * Default constructor
     */
    public FlowNode() {

    }

    /**
     * Copy constructor
     *
     * @param flowNode The {@link FlowNode} to copy
     */
    public FlowNode(FlowNode flowNode) {
        super(flowNode);
    }

    public void acceptVisitor(Visitor v) {
        v.visitBaseElement(this);
    }

    /**
     * Convenience method to retrieve all incoming {@link SequenceFlow}
     * <p/>
     * Changes to that list have no influence to the result other callers get.
     *
     * @return The list of {@link SequenceFlow}
     */
    public List<SequenceFlow> getIncomingSequenceFlows() {
        ArrayList<SequenceFlow> incomingSeq = new ArrayList<SequenceFlow>();

        for (FlowElement node : this.getIncoming()) {
            /* Determine if type of sequence flow */
            if (node instanceof SequenceFlow) {
                incomingSeq.add((SequenceFlow) node);
            }
        }

        return incomingSeq;
    }

    /**
     * The {@link Marshaller} invokes this method right before marshaling to
     * XML. Add sequenceflow to the reference list.
     *
     * @param marshaller The marshaling context
     */
    public void beforeMarshal(Marshaller marshaller) {
        /* Incoming sequence flows */
        for (Edge edge : this.getIncoming()) {
            if (edge instanceof SequenceFlow) {
                get_incomingSequenceFlows().add((SequenceFlow) edge);
            }
        }

        /* Outgoing sequence flows */
        for (Edge edge : this.getOutgoing()) {
            if (edge instanceof SequenceFlow) {
                get_outgoingSequenceFlows().add((SequenceFlow) edge);
            }
        }
    }

    /**
     * Convenience method to retrieve all outgoing {@link SequenceFlow}
     * <p/>
     * Changes to that list have no influence to the result other callers get.
     *
     * @return The list of {@link SequenceFlow}
     */
    public List<SequenceFlow> getOutgoingSequenceFlows() {
        ArrayList<SequenceFlow> outgoingSeq = new ArrayList<SequenceFlow>();

        for (FlowElement node : this.getOutgoing()) {
            /* Determine if type of sequence flow */
            if (node instanceof SequenceFlow) {
                outgoingSeq.add((SequenceFlow) node);
            }
        }

        return outgoingSeq;
    }

    /**
     * @return The incoming compensation Flow.
     */
    public List<Association> getIncomingCompensationFlows() {
        ArrayList<Association> compensationFlows = new ArrayList<Association>();

        /* Find incomming compensation flow */
        for (FlowElement edge : this.getIncoming()) {
            if (edge instanceof Association
                    && ((Association) edge).getAssociationDirection().equals(
                    AssociationDirection.ONE)
                    && ((Association) edge).getSourceRef() instanceof BoundaryEvent
                    && (((BoundaryEvent) ((Association) edge).getSourceRef())
                    .getEventDefinition().size() == 1 && (((BoundaryEvent) ((Association) edge)
                    .getSourceRef()).getEventDefinition().get(0) instanceof CompensateEventDefinition))) {
                compensationFlows.add((Association) edge);
            }
        }

        return compensationFlows;
    }

    /**
     * @return The outcoming compensation Flow.
     */
    public List<Association> getOutgoingCompensationFlows() {
        ArrayList<Association> compensationFlows = new ArrayList<Association>();

        /* Find outgoing compensation flow */
        for (FlowElement edge : this.getOutgoing()) {
            if (edge instanceof Association
                    && ((Association) edge).getAssociationDirection().equals(
                    AssociationDirection.ONE)
                    && ((Association) edge).getSourceRef() instanceof BoundaryEvent
                    && (((BoundaryEvent) ((Association) edge).getSourceRef())
                    .getEventDefinition().size() == 1 && (((BoundaryEvent) ((Association) edge)
                    .getSourceRef()).getEventDefinition().get(0) instanceof CompensateEventDefinition))) {
                compensationFlows.add((Association) edge);
            }
        }

        return compensationFlows;
    }

    public List<SequenceFlow> get_incomingSequenceFlows() {
        if (_incomingSequenceFlows == null) {
            _incomingSequenceFlows = new ArrayList<SequenceFlow>();
        }

        return _incomingSequenceFlows;
    }

    public List<SequenceFlow> get_outgoingSequenceFlows() {
        if (_outgoingSequenceFlows == null) {
            _outgoingSequenceFlows = new ArrayList<SequenceFlow>();
        }

        return _outgoingSequenceFlows;
    }
    
    public Object getNameRef() {
        return nameRef;
    }
    
    public void setNameRef(Object nameRef) {
        this.nameRef = nameRef;
    }    
}
