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

package de.hpi.bpmn2_0.model.connector;

import de.hpi.bpmn2_0.model.Expression;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.data_object.AbstractDataObject;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * Java class for tSequenceFlow complex type.
 * <p/>
 * <p/>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tSequenceFlow">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tFlowElement">
 *       &lt;sequence>
 *         &lt;element name="conditionExpression" type="{http://www.omg.org/bpmn20}tExpression" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="sourceRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="targetRef" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="isImmediate" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "sequenceFlow")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tSequenceFlow", propOrder = {"conditionExpression"})
public class SequenceFlow extends Edge {

    /* Attributes */

    @XmlElement(name = "conditionExpression")
    protected Expression conditionExpression;
    @XmlAttribute
    protected Boolean isImmediate;

    @XmlTransient
    private boolean isDefaultSequenceFlow;

    /* Constructors */

    /**
     * Default constructor
     */
    public SequenceFlow() {
    }

    /**
     * Copy constructor
     *
     * @param seqFlow
     */
    public SequenceFlow(SequenceFlow seqFlow) {
        super(seqFlow);

        this.setConditionExpression(seqFlow.getConditionExpression());
        this.setIsImmediate(seqFlow.isImmediate);
        this.setDefaultSequenceFlow(seqFlow.isDefaultSequenceFlow());
    }

    /**
     * Transform undirected data associations into input and output
     * associations.
     */
    public void processUndirectedDataAssociations() {
        List<DataAssociation> dataAssociations = this
                .getUndirectedDataAssociations();

        for (DataAssociation dataAssociation : dataAssociations) {
            AbstractDataObject dataObject = null;
            if (dataAssociation.getSourceRef() instanceof AbstractDataObject) {
                dataObject = (AbstractDataObject) dataAssociation
                        .getSourceRef();
            } else if (dataAssociation.getTargetRef() instanceof AbstractDataObject) {
                dataObject = (AbstractDataObject) dataAssociation
                        .getTargetRef();
            } else
                continue;

            /* Prepare data input association */
            DataInputAssociation dataInputAssociation = new DataInputAssociation(
                    dataAssociation);
            dataInputAssociation.setSourceRef(dataObject);
            if (this.getTargetRef() != null
                    && this.getTargetRef() instanceof Activity) {
                dataInputAssociation.setTargetRef(this.getTargetRef());
                ((Activity) this.getTargetRef()).getDataInputAssociation().add(
                        dataInputAssociation);
            }

            /* Prepare data output association */
            DataOutputAssociation dataOutputAssociation = new DataOutputAssociation(
                    dataAssociation);
            dataOutputAssociation.setTargetRef(dataObject);
            if (this.getSourceRef() != null
                    && this.getSourceRef() instanceof Activity) {
                dataOutputAssociation.setSourceRef(this.getSourceRef());
                ((Activity) this.getSourceRef()).getDataOutputAssociation().add(
                        dataOutputAssociation);
            }
        }
    }

    /**
     * Retrieves the undirected data associations connected to the sequence
     * flow.
     *
     * @return List of {@link DataAssociation}
     */
    private List<DataAssociation> getUndirectedDataAssociations() {
        ArrayList<DataAssociation> dataAssociations = new ArrayList<DataAssociation>();

        /* Handle outgoing associations */
        for (Edge edge : this.getOutgoing()) {
            if (edge instanceof DataAssociation
                    && !(edge instanceof DataInputAssociation)
                    && !(edge instanceof DataOutputAssociation))
                dataAssociations.add((DataAssociation) edge);
        }

        /* Handle incoming associations */
        for (Edge edge : this.getIncoming()) {
            if (edge instanceof DataAssociation
                    && !(edge instanceof DataInputAssociation)
                    && !(edge instanceof DataOutputAssociation))
                dataAssociations.add((DataAssociation) edge);
        }

        return dataAssociations;
    }

    public void acceptVisitor(Visitor v) {
        v.visitSequenceFlow(this);
    }

    /* Getter & Setter */

    /**
     * Gets the value of the conditionExpression property.
     *
     * @return possible object is {@link Expression }
     */
    public Expression getConditionExpression() {
        return conditionExpression;
    }

    /**
     * Sets the value of the conditionExpression property.
     *
     * @param value allowed object is {@link Expression }
     */
    public void setConditionExpression(Expression value) {
        this.conditionExpression = value;
    }

    /**
     * Gets the value of the isImmediate property.
     *
     * @return possible object is {@link Boolean }
     */
    public boolean isIsImmediate() {
        if (isImmediate == null) {
            return true;
        } else {
            return isImmediate;
        }
    }

    /**
     * Sets the value of the isImmediate property.
     *
     * @param value allowed object is {@link Boolean }
     */
    public void setIsImmediate(Boolean value) {
        this.isImmediate = value;
    }

    /**
     * @return the isDefaultSequenceFlow
     */
    public boolean isDefaultSequenceFlow() {
        return isDefaultSequenceFlow;
    }

    /**
     * @param isDefaultSequenceFlow the isDefaultSequenceFlow to set
     */
    public void setDefaultSequenceFlow(boolean isDefaultSequenceFlow) {
        this.isDefaultSequenceFlow = isDefaultSequenceFlow;
    }

}
