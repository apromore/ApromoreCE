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

package de.hpi.bpmn2_0.model.participant;

import de.hpi.bpmn2_0.annotations.ChildElements;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.transformation.Visitor;
import de.hpi.diagram.SignavioUUID;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tLane complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tLane">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element name="partitionElement" type="{http://www.omg.org/bpmn20}tBaseElement" minOccurs="0"/>
 *         &lt;element name="flowElementRef" type="{http://www.w3.org/2001/XMLSchema}IDREF" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="childLaneSet" type="{http://www.omg.org/bpmn20}tLaneSet" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="partitionElementRef" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tLane", propOrder = {
        "partitionElement",
        "flowNodeRef",
//    "laneSet",
        "childLaneSet"
})
public class Lane
        extends FlowElement {

    protected BaseElement partitionElement;

    @XmlIDREF
//	@XmlElements({
//		/* Events */
//		@XmlElement(type = StartEvent.class),
//		@XmlElement(type = EndEvent.class),
//		
//		/* Activities */
//		@XmlElement(type = Task.class),
//		
//		/* Gateways */
//		@XmlElement(type = ExclusiveGateway.class),
//		@XmlElement(type = ParallelGateway.class),
//		
//		/* Edges */
//		@XmlElement(type = SequenceFlow.class),
//		
//		/* Artifacts / Data elements */
//		@XmlElement(type = DataObject.class),
//		@XmlElement(type = TextAnnotation.class),
//		
//		/* Partner */
//		@XmlElement(type = Participant.class)
//	})
    @XmlElement(type = FlowNode.class)
    protected List<FlowNode> flowNodeRef;

    @XmlElement(type = LaneSet.class)
    protected LaneSet childLaneSet;

    //	@XmlIDREF
//	@XmlAttribute
//	@XmlSchemaType(name = "IDREF")
//	@XmlElementRef(type = LaneSet.class)
    @XmlTransient
    protected LaneSet laneSet;

    @XmlAttribute
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object partitionElementRef;

    /*
     * Constructor
     */

    /**
     * Default constructor
     */
    public Lane() {
        super();
    }

    /**
     * Copy constructor
     *
     * @param l template {@link Lane}
     */
    public Lane(Lane l) {
        super(l);

        this.setPartitionElement(l.getPartitionElement());
        this.getFlowNodeRef().addAll(l.getFlowNodeRef());
        this.setChildLaneSet(l.childLaneSet);
        this.setLaneSet(l.getLaneSet());
        this.setPartitionElementRef(l.getPartitionElementRef());
    }

    /* Methods */

    /**
     * Retrieves all child lane.
     */
    public List<Lane> getLaneList() {
        List<Lane> laneList = new ArrayList<Lane>();
        if (getChildLaneSet(false) == null)
            return laneList;

        laneList.addAll(getChildLaneSet(false).getAllLanes());

        return laneList;
    }

    /**
     * Adds the child to the lane's flow elements if possible.
     */
    public void addChild(BaseElement child) {
        if (child instanceof Lane) {
            this.getChildLaneSet(true).getLanes().add((Lane) child);
            ((Lane) child).setLaneSet(this.getChildLaneSet(true));
        } else if (!(child instanceof Edge)) {
            this.getFlowNodeRef().add((FlowNode) child);
        }
    }

    public void acceptVisitor(Visitor v) {
        v.visitLane(this);
    }

    /* Getter & Setter */

    /**
     * Gets the value of the partitionElement property.
     *
     * @return possible object is
     *         {@link TBaseElement }
     */
    public BaseElement getPartitionElement() {
        return partitionElement;
    }

    /**
     * Sets the value of the partitionElement property.
     *
     * @param value allowed object is
     *              {@link TBaseElement }
     */
    public void setPartitionElement(BaseElement value) {
        this.partitionElement = value;
    }

    /**
     * Returns a LaneSet, containing sub-Lanes (even if it is only one). Not to be confused with {@link #getLane()}, which returns the <b> containing </b> lane.
     *
     * @return the laneSet
     */
    public LaneSet getLaneSet() {
        return laneSet;
    }

    /**
     * @param laneSet the laneSet to set
     */
    public void setLaneSet(LaneSet laneSet) {
        this.laneSet = laneSet;
    }

    /**
     * Gets the value of the flowElementRef property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the flowElementRef property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFlowElementRef().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     */
    @ChildElements
    public List<FlowNode> getFlowNodeRef() {
        if (flowNodeRef == null) {
            flowNodeRef = new ArrayList<FlowNode>();
        }
        return this.flowNodeRef;
    }

    /**
     * Gets the value of the childLaneSet property.
     * <p/>
     * If createIfMissing is set to true, an childLaneSet is created on demand.
     *
     * @return possible object is
     *         {@link LaneSet }
     */
    @ChildElements
    public LaneSet getChildLaneSet(boolean createIfMissing) {
        if (childLaneSet == null && createIfMissing) {
            childLaneSet = new LaneSet();
            childLaneSet.setId(SignavioUUID.generate());
            childLaneSet.setParentLane(this);
        }
        return childLaneSet;
    }

    /**
     * Sets the value of the childLaneSet property.
     *
     * @param value allowed object is
     *              {@link LaneSet }
     */
    public void setChildLaneSet(LaneSet value) {
        this.childLaneSet = value;
    }

    /**
     * Gets the value of the partitionElementRef property.
     *
     * @return possible object is
     *         {@link Object }
     */
    public Object getPartitionElementRef() {
        return partitionElementRef;
    }

    /**
     * Sets the value of the partitionElementRef property.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setPartitionElementRef(Object value) {
        this.partitionElementRef = value;
    }

}
