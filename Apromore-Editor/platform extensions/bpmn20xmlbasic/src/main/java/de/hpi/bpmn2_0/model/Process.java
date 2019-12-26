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

import de.hpi.bpmn2_0.annotations.ChildElements;
import de.hpi.bpmn2_0.model.activity.*;
import de.hpi.bpmn2_0.model.activity.type.*;
import de.hpi.bpmn2_0.model.artifacts.Artifact;
import de.hpi.bpmn2_0.model.artifacts.Group;
import de.hpi.bpmn2_0.model.artifacts.TextAnnotation;
import de.hpi.bpmn2_0.model.choreography.ChoreographyActivity;
import de.hpi.bpmn2_0.model.choreography.ChoreographyTask;
import de.hpi.bpmn2_0.model.choreography.SubChoreography;
import de.hpi.bpmn2_0.model.connector.Association;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.data_object.DataObject;
import de.hpi.bpmn2_0.model.data_object.DataStore;
import de.hpi.bpmn2_0.model.data_object.Message;
import de.hpi.bpmn2_0.model.event.*;
import de.hpi.bpmn2_0.model.gateway.*;
import de.hpi.bpmn2_0.model.misc.ProcessType;
import de.hpi.bpmn2_0.model.participant.Lane;
import de.hpi.bpmn2_0.model.participant.LaneSet;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tProcess complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tProcess">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tCallableElement">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}auditing" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}monitoring" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}property" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}laneSet" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}flowElement" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}artifact" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="supports" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="processType" type="{http://www.omg.org/bpmn20}tProcessType" default="none" />
 *       &lt;attribute name="isClosed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="definitionalCollaborationRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement(name = "process")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tProcess", propOrder = {
//    "auditing",
//    "monitoring",
//    "property",
        "laneSet",
        "flowElement",
        "artifact",
        "supports",
        "isClosed",
        "isExecutable"
})
public class Process
        extends CallableElement {

    //    protected TAuditing auditing;
//    protected TMonitoring monitoring;
//    protected List<TProperty> property;
//	@XmlElementRefs({
//		/* Events */
//		@XmlElementRef(type = StartEvent.class),
//		@XmlElementRef(type = EndEvent.class),
//		@XmlElementRef(type = IntermediateThrowEvent.class),
//		@XmlElementRef(type = IntermediateCatchEvent.class),
//		
//		/* Activities */
//		@XmlElementRef(type = Task.class),
//		@XmlElementRef(type = ReceiveTask.class),
//		@XmlElementRef(type = ManualTask.class),
//		@XmlElementRef(type = ScriptTask.class),
//		@XmlElementRef(type = SendTask.class),
//		@XmlElementRef(type = ServiceTask.class),
//		@XmlElementRef(type = UserTask.class),
//		@XmlElementRef(type = BusinessRuleTask.class),
//		@XmlElementRef(type = SubProcess.class),
//		
//		/* Gateways */
//		@XmlElementRef(type = ExclusiveGateway.class),
//		@XmlElementRef(type = ParallelGateway.class),
//		@XmlElementRef(type = ComplexGateway.class),
//		@XmlElementRef(type = EventBasedGateway.class),
//		@XmlElementRef(type = InclusiveGateway.class),
//		
//		/* Edges */
//		@XmlElementRef(type = SequenceFlow.class),
//		
//		/* Artifacts / Data elements */
//		@XmlElementRef(type = DataObject.class),
//		@XmlElementRef(type = TextAnnotation.class),
//		@XmlElementRef(type = ITSystem.class),
//		
//		/* Partner */
//		@XmlElementRef(type = Participant.class)
//	})
    @XmlElementRef
    protected List<FlowElement> flowElement;
    @XmlElementRef
    protected List<Artifact> artifact;
    protected List<QName> supports;
    @XmlAttribute
    protected ProcessType processType;
    @XmlAttribute
    protected Boolean isClosed;
    @XmlAttribute
    protected boolean isExecutable;
    @XmlAttribute
    protected QName definitionalCollaborationRef;

    @XmlElement(type = LaneSet.class)
    protected List<LaneSet> laneSet;

    /**
     * Adds the child to the process's flow elements if possible.
     */
    public void addChild(BaseElement child) {
        if (child instanceof Artifact) {
            this.getArtifact().add((Artifact) child);
        } else if (child instanceof FlowElement) {
            this.getFlowElement().add((FlowElement) child);
        }

        if (child instanceof FlowElement) {
            ((FlowElement) child).setProcess(this);
        }
    }

    /**
     * Remove the child element from the process.
     *
     * @param child Child element to remove.
     */
    public void removeChild(BaseElement child) {
        this.getArtifact().remove(child);

        this.getFlowElement().remove(child);

        removeFromLaneSet(child);
    }

    /**
     * Remove the element recursively from the lane set.
     */
    private void removeFromLaneSet(BaseElement child) {
        if (this.laneSet != null) {
            for (LaneSet laneSet : this.getLaneSet()) {
                laneSet.removeChild(child);
            }
        }
    }


    /**
     * Determines whether the process contains choreograhy elements.
     *
     * @return {@code true} if a {@link ChoreographyActivity} is contained
     *         <br />
     *         {@code false} otherwise.
     */
    public boolean isChoreographyProcess() {
        for (FlowElement flowEle : this.getFlowElement()) {
            if (flowEle instanceof ChoreographyActivity)
                return true;
        }

        return false;
    }

    public List<FlowElement> getFlowElementsForChoreography() {
        ArrayList<FlowElement> elements = new ArrayList<FlowElement>();
        for (FlowElement flowEle : this.getFlowElement()) {
            elements.add(flowEle);

            /* Retrieve by associations connected messages */
            for (Edge e : flowEle.getOutgoing()) {
                if (e.getTargetRef() instanceof Message) {
                    elements.add(e);
                    elements.add(e.getTargetRef());
                }
            }

            for (Edge e : flowEle.getIncoming()) {
                if (e.getSourceRef() instanceof Message) {
                    elements.add(e);
                    elements.add(e.getSourceRef());
                }
            }
        }

        return elements;
    }

    /**
     * Retrieve all subprocesses and child subprocesses recursively.
     *
     * @return A flat list of the contained subprocesses.
     */
    public List<SubProcess> getSubprocessList() {
        List<SubProcess> subprocesses = new ArrayList<SubProcess>();

        for (FlowElement flowEle : getFlowElement()) {
            /* Process subprocess */
            if (flowEle instanceof SubProcess) {
                subprocesses.add((SubProcess) flowEle);
                subprocesses.addAll(((SubProcess) flowEle).getSubprocessList());
            }
        }

        return subprocesses;
    }

    /**
     * Retrieves a list of subchoreographies contained in the process including
     * children.
     *
     * @return
     */
    public List<SubChoreography> getSubChoreographyList() {
        List<SubChoreography> subchoreographies = new ArrayList<SubChoreography>();

        for (FlowElement flowEle : getFlowElement()) {
            /* Subchoreography */
            if (flowEle instanceof SubChoreography) {
                subchoreographies.add((SubChoreography) flowEle);

            }
        }

        return subchoreographies;
    }

    /**
     * Returns a list of {@link Lane} participating in this process.
     *
     * @return
     */
    public List<Lane> getAllLanes() {
        List<Lane> laneList = new ArrayList<Lane>();

        if (this.getLaneSet() == null) {
            return laneList;
        }

        for (LaneSet laneSet : getLaneSet()) {
            laneList.addAll(laneSet.getAllLanes());
        }

        return laneList;
    }

    public boolean hasId() {
        return this.getId() != null && this.getId().length() > 0;
    }

    /* Getter & Setter */


    public List<LaneSet> getLaneSet() {
        if (this.laneSet == null) {
            this.laneSet = new ArrayList<LaneSet>();
        }
        return this.laneSet;
    }

    /**
     * Gets the value of the auditing property.
     *
     * @return
     *     possible object is
     *     {@link TAuditing }
     *
     */
//    public TAuditing getAuditing() {
//        return auditing;
//    }

    /**
     * Sets the value of the auditing property.
     *
     * @param value
     *     allowed object is
     *     {@link TAuditing }
     *
     */
//    public void setAuditing(TAuditing value) {
//        this.auditing = value;
//    }

    /**
     * Gets the value of the monitoring property.
     *
     * @return
     *     possible object is
     *     {@link TMonitoring }
     *
     */
//    public TMonitoring getMonitoring() {
//        return monitoring;
//    }

    /**
     * Sets the value of the monitoring property.
     *
     * @param value
     *     allowed object is
     *     {@link TMonitoring }
     *
     */
//    public void setMonitoring(TMonitoring value) {
//        this.monitoring = value;
//    }

    /**
     * Gets the value of the property property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the property property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProperty().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TProperty }
     *
     *
     */
//    public List<TProperty> getProperty() {
//        if (property == null) {
//            property = new ArrayList<TProperty>();
//        }
//        return this.property;
//    }

    /**
     * Gets the value of the laneSet property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the laneSet property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLaneSet().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LaneSet }
     *
     *
     */
//    public List<LaneSet> getLaneSet() {
//        if (laneSet == null) {
//            laneSet = new ArrayList<LaneSet>();
//        }
//        return this.laneSet;
//    }

    /**
     * Gets the value of the flowElement property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the flowElement property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFlowElement().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link ManualTask }{@code >}
     * {@link JAXBElement }{@code <}{@link TCallChoreographyActivity }{@code >}
     * {@link JAXBElement }{@code <}{@link Transaction }{@code >}
     * {@link JAXBElement }{@code <}{@link EndEvent }{@code >}
     * {@link JAXBElement }{@code <}{@link IntermediateCatchEvent }{@code >}
     * {@link JAXBElement }{@code <}{@link FlowElement }{@code >}
     * {@link JAXBElement }{@code <}{@link CallActivity }{@code >}
     * {@link JAXBElement }{@code <}{@link ComplexGateway }{@code >}
     * {@link JAXBElement }{@code <}{@link BoundaryEvent }{@code >}
     * {@link JAXBElement }{@code <}{@link StartEvent }{@code >}
     * {@link JAXBElement }{@code <}{@link ExclusiveGateway }{@code >}
     * {@link JAXBElement }{@code <}{@link BusinessRuleTask }{@code >}
     * {@link JAXBElement }{@code <}{@link ScriptTask }{@code >}
     * {@link JAXBElement }{@code <}{@link InclusiveGateway }{@code >}
     * {@link JAXBElement }{@code <}{@link DataObject }{@code >}
     * {@link JAXBElement }{@code <}{@link Event }{@code >}
     * {@link JAXBElement }{@code <}{@link ServiceTask }{@code >}
     * {@link JAXBElement }{@code <}{@link ChoreographyTask }{@code >}
     * {@link JAXBElement }{@code <}{@link DataStore }{@code >}
     * {@link JAXBElement }{@code <}{@link SubProcess }{@code >}
     * {@link JAXBElement }{@code <}{@link IntermediateThrowEvent }{@code >}
     * {@link JAXBElement }{@code <}{@link UserTask }{@code >}
     * {@link JAXBElement }{@code <}{@link SequenceFlow }{@code >}
     * {@link JAXBElement }{@code <}{@link EventBasedGateway }{@code >}
     * {@link JAXBElement }{@code <}{@link AdHocSubProcess }{@code >}
     * {@link JAXBElement }{@code <}{@link SendTask }{@code >}
     * {@link JAXBElement }{@code <}{@link ChoreographySubProcess }{@code >}
     * {@link JAXBElement }{@code <}{@link ReceiveTask }{@code >}
     * {@link JAXBElement }{@code <}{@link TImplicitThrowEvent }{@code >}
     * {@link JAXBElement }{@code <}{@link ParallelGateway }{@code >}
     * {@link JAXBElement }{@code <}{@link Task }{@code >}
     */
    @ChildElements
    public List<FlowElement> getFlowElement() {
        if (flowElement == null) {
            flowElement = new ArrayList<FlowElement>();
        }
        return this.flowElement;
    }

    /**
     * Gets the value of the artifact property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the artifact property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArtifact().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Artifact }{@code >}
     * {@link JAXBElement }{@code <}{@link Association }{@code >}
     * {@link JAXBElement }{@code <}{@link Group }{@code >}
     * {@link JAXBElement }{@code <}{@link TextAnnotation }{@code >}
     */
    public List<Artifact> getArtifact() {
        if (artifact == null) {
            artifact = new ArrayList<Artifact>();
        }
        return this.artifact;
    }

    /**
     * Gets the value of the supports property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supports property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupports().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link QName }
     */
    public List<QName> getSupports() {
        if (supports == null) {
            supports = new ArrayList<QName>();
        }
        return this.supports;
    }

    /**
     * Gets the value of the processType property.
     *
     * @return
     *     possible object is
     *     {@link TProcessType }
     *
     */
//    public TProcessType getProcessType() {
//        if (processType == null) {
//            return TProcessType.NONE;
//        } else {
//            return processType;
//        }
//    }

    /**
     * Sets the value of the processType property.
     *
     * @param value
     *     allowed object is
     *     {@link TProcessType }
     *
     */
//    public void setProcessType(TProcessType value) {
//        this.processType = value;
//    }

    /**
     * Gets the value of the isClosed property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public boolean isIsClosed() {
        if (isClosed == null) {
            return false;
        } else {
            return isClosed;
        }
    }

    /**
     * Sets the value of the isClosed property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setIsClosed(Boolean value) {
        this.isClosed = value;
    }

    public boolean isExecutable() {
        return isExecutable;
    }

    public void setExecutable(boolean isExecutable) {
        this.isExecutable = isExecutable;
    }

    /**
     * Gets the value of the definitionalCollaborationRef property.
     *
     * @return possible object is
     *         {@link QName }
     */
    public QName getDefinitionalCollaborationRef() {
        return definitionalCollaborationRef;
    }

    /**
     * Sets the value of the definitionalCollaborationRef property.
     *
     * @param value allowed object is
     *              {@link QName }
     */
    public void setDefinitionalCollaborationRef(QName value) {
        this.definitionalCollaborationRef = value;
    }

    /**
     * @return the processType
     */
    public ProcessType getProcessType() {
        /* None as default value */
        if (this.processType == null)
            this.processType = ProcessType.NONE;

        return processType;
    }

    /**
     * @param processType the processType to set
     */
    public void setProcessType(ProcessType processType) {
        this.processType = processType;
    }

}
