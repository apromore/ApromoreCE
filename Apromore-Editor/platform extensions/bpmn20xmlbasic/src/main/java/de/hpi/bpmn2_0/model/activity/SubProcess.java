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

package de.hpi.bpmn2_0.model.activity;

import de.hpi.bpmn2_0.annotations.CallingElement;
import de.hpi.bpmn2_0.annotations.ChildElements;
import de.hpi.bpmn2_0.annotations.ContainerElement;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.activity.type.*;
import de.hpi.bpmn2_0.model.artifacts.Artifact;
import de.hpi.bpmn2_0.model.artifacts.TextAnnotation;
import de.hpi.bpmn2_0.model.bpmndi.di.DiagramElement;
import de.hpi.bpmn2_0.model.choreography.ChoreographyTask;
import de.hpi.bpmn2_0.model.connector.Association;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.data_object.DataObject;
import de.hpi.bpmn2_0.model.data_object.DataStore;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;
import de.hpi.bpmn2_0.model.gateway.ParallelGateway;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * Java class for tSubProcess complex type.
 * <p/>
 * <p/>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tSubProcess">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tActivity">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}flowElement" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}artifact" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="triggeredByEvent" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tSubProcess", propOrder = {"flowElement", "artifact"})
@XmlSeeAlso({AdHocSubProcess.class, Transaction.class})
public class SubProcess extends Activity implements ContainerElement,
        CallingElement {

    @XmlElementRef(type = FlowElement.class)
    protected List<FlowElement> flowElement;

    @XmlElementRef(type = Artifact.class)
    protected List<Artifact> artifact;

    @XmlAttribute
    protected Boolean triggeredByEvent;

    @XmlTransient
    public List<DiagramElement> _diagramElements = new ArrayList<DiagramElement>();

    /**
     * Adds the child to the list of {@link FlowElement} of the subprocess.
     */
    public void addChild(BaseElement child) {
        /* Set sub process reference */
        if (child instanceof FlowElement) {
            ((FlowElement) child).setSubProcess(this);

            /* Boundary events of the children are also within the subprocess */
            if (child instanceof Activity) {
                for (BoundaryEvent boundaryEvent: ((Activity) child).getBoundaryEventRefs()) {
                    boundaryEvent.setSubProcess(this);
                }
            }
        }

        /* Insert into appropriate list */
        if (child instanceof Artifact) {
            this.getArtifact().add((Artifact) child);
            ((Artifact) child).setSubProcess(this);
        } else if (child instanceof FlowElement) {
            this.getFlowElement().add((FlowElement) child);
        }
    }

    /**
     * Remove the child element from the sub process.
     *
     * @param child Child element to remove.
     */
    public void removeChild(BaseElement child) {
        this.getArtifact().remove(child);

        this.getFlowElement().remove(child);
    }

    /**
     * Retrieve all subprocesses and child subprocesses recursively.
     *
     * @return A flat list of the contained subprocesses.
     */
    public List<SubProcess> getSubprocessList() {
        List<SubProcess> subprocesses = new ArrayList<SubProcess>();

        for (FlowElement flowEle : getFlowElement()) {
            if (flowEle instanceof SubProcess) {
                subprocesses.add((SubProcess) flowEle);
                subprocesses.addAll(((SubProcess) flowEle).getSubprocessList());
            }
        }

        return subprocesses;
    }

    public List<Edge> getChildEdges() {
        List<Edge> edgeList = new ArrayList<Edge>();

        for (FlowElement fe : this.getFlowElement()) {
            if (fe instanceof Edge) {
                edgeList.add((Edge) fe);
            } else if (fe instanceof ContainerElement) {
                edgeList.addAll(((ContainerElement) fe).getChildEdges());
            }
        }

        return edgeList;
    }

    public void acceptVisitor(Visitor v) {
        v.visitSubProcess(this);
    }

    /* Getter & Setter */

    /**
     * Gets the value of the flowElement property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the flowElement property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <p/>
     * <pre>
     * getFlowElement().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link ManualTask }{@code >}
     * {@link JAXBElement }{@code <}{@link TCallChoreographyActivity }{@code >}
     * {@link JAXBElement }{@code <}{@link Transaction }{@code >}
     * {@link JAXBElement }{@code <}{@link TEndEvent }{@code >}
     * {@link JAXBElement }{@code <}{@link TIntermediateCatchEvent }{@code >}
     * {@link JAXBElement }{@code <}{@link TFlowElement }{@code >}
     * {@link JAXBElement }{@code <}{@link CallActivity }{@code >}
     * {@link JAXBElement }{@code <}{@link TComplexGateway }{@code >}
     * {@link JAXBElement }{@code <}{@link TBoundaryEvent }{@code >}
     * {@link JAXBElement }{@code <}{@link TStartEvent }{@code >}
     * {@link JAXBElement }{@code <}{@link TExclusiveGateway }{@code >}
     * {@link JAXBElement }{@code <}{@link BusinessRuleTask }{@code >}
     * {@link JAXBElement }{@code <}{@link ScriptTask }{@code >}
     * {@link JAXBElement }{@code <}{@link TInclusiveGateway }{@code >}
     * {@link JAXBElement }{@code <}{@link DataObject }{@code >}
     * {@link JAXBElement }{@code <}{@link TEvent }{@code >} {@link JAXBElement }
     * {@code <}{@link ServiceTask }{@code >} {@link JAXBElement }{@code <}
     * {@link ChoreographyTask }{@code >} {@link JAXBElement }{@code <}
     * {@link DataStore }{@code >} {@link JAXBElement }{@code <}{@link SubProcess }
     * {@code >} {@link JAXBElement }{@code <}{@link TIntermediateThrowEvent }
     * {@code >} {@link JAXBElement }{@code <}{@link UserTask }{@code >}
     * {@link JAXBElement }{@code <}{@link TSequenceFlow }{@code >}
     * {@link JAXBElement }{@code <}{@link TEventBasedGateway }{@code >}
     * {@link JAXBElement }{@code <}{@link AdHocSubProcess }{@code >}
     * {@link JAXBElement }{@code <}{@link SendTask }{@code >} {@link JAXBElement }
     * {@code <}{@link ChoreographySubProcess }{@code >} {@link JAXBElement }
     * {@code <}{@link ReceiveTask }{@code >} {@link JAXBElement }{@code <}
     * {@link TImplicitThrowEvent }{@code >} {@link JAXBElement }{@code <}
     * {@link ParallelGateway }{@code >} {@link JAXBElement }{@code <}
     * {@link TTask }{@code >}
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
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the artifact property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <p/>
     * <pre>
     * getArtifact().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Artifact }{@code >} {@link JAXBElement }
     * {@code <}{@link Association }{@code >} {@link JAXBElement }{@code <}
     * {@link TGroup }{@code >} {@link JAXBElement }{@code <}
     * {@link TextAnnotation }{@code >}
     */
    @ChildElements
    public List<Artifact> getArtifact() {
        if (artifact == null) {
            artifact = new ArrayList<Artifact>();
        }
        return this.artifact;
    }

    /**
     * Gets the value of the triggeredByEvent property.
     *
     * @return possible object is {@link Boolean }
     */
    public boolean isTriggeredByEvent() {
        if (triggeredByEvent == null) {
            return false;
        } else {
            return triggeredByEvent;
        }
    }

    /**
     * Sets the value of the triggeredByEvent property.
     *
     * @param value allowed object is {@link Boolean }
     */
    public void setTriggeredByEvent(Boolean value) {
        this.triggeredByEvent = value;
    }

    public List<DiagramElement> _getDiagramElements() {
        return _diagramElements;
    }

    public List<BaseElement> getCalledElements() {
        List<BaseElement> calledElements = new ArrayList<BaseElement>();

        for (FlowElement flowEl : getFlowElement()) {
            if (flowEl instanceof CallingElement) {
                calledElements.addAll(((CallingElement) flowEl)
                        .getCalledElements());
            }
        }

        return calledElements;
    }
}
