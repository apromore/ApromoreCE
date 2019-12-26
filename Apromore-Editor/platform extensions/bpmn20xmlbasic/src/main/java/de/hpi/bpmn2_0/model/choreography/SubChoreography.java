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

package de.hpi.bpmn2_0.model.choreography;

import de.hpi.bpmn2_0.annotations.CallingElement;
import de.hpi.bpmn2_0.annotations.ContainerElement;
import de.hpi.bpmn2_0.factory.BPMNElement;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.activity.AdHocSubProcess;
import de.hpi.bpmn2_0.model.activity.CallActivity;
import de.hpi.bpmn2_0.model.activity.Transaction;
import de.hpi.bpmn2_0.model.activity.type.*;
import de.hpi.bpmn2_0.model.artifacts.Artifact;
import de.hpi.bpmn2_0.model.bpmndi.di.DiagramElement;
import de.hpi.bpmn2_0.model.connector.Association;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.data_object.Message;
import de.hpi.bpmn2_0.model.participant.Participant;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;
import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * <p>Java class for tChoreographySubProcess complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tChoreographySubProcess">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tChoreographyActivity">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}flowElement" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}artifact" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tSubChoreography", propOrder = {
        "flowElement",
        "artifact"
})
public class SubChoreography
        extends ChoreographyActivity implements ContainerElement, CallingElement {

    @XmlElementRef(type = FlowElement.class)
    protected List<FlowElement> flowElement;

    @XmlElementRef(type = Artifact.class)
    protected List<Artifact> artifact;

    @XmlTransient
    public List<DiagramElement> _diagramElements = new ArrayList<DiagramElement>();

    public void addChild(BaseElement child) {
        if (child instanceof Participant) {
            this.getParticipantRef().add((Participant) child);
            if (((Participant) child).isInitiating()) {
                this.setInitiatingParticipantRef((Participant) child);
            }
        } else if (child instanceof Artifact) {
            this.getArtifact().add((Artifact) child);
            ((Artifact) child).setSubChoreography(this);
        } else if (child instanceof FlowElement) {
            this.getFlowElement().add((FlowElement) child);
        }

        /* Set parent relation */
        if (child instanceof FlowElement) {
            ((FlowElement) child).setSubChoreography(this);
        }
    }


    /**
     * Copies all participant references of sub-choreographies recursively to
     * the choreography and creates the message flow of the choreography tasks.
     *
     * @param choreography
     */
    public void setParticipantsAndMessageFlows(Choreography choreography, Map<String, BPMNElement> bpmnElements, Diagram2BpmnConverter converter) {

        List<Message> messagesToRemove = new ArrayList<Message>();
        List<Association> associationsToRemove = new ArrayList<Association>();

        /* Handle child subchoreographies and tasks */
        for (FlowElement flowEl : this.getFlowElement()) {
            if (flowEl instanceof Association) {
                Association association = (Association) flowEl;

                /* Check whether the association depicts a message to the
                     * choreography activity */
                if ((association.getSourceRef() instanceof ChoreographyActivity
                        && association.getTargetRef() instanceof Message)
                        || (association.getSourceRef() instanceof Message
                        && association.getTargetRef() instanceof ChoreographyActivity)) {
                    converter.handleMessageAssociationOnChoreographyActivity(association);
                }
            }

            /* Remove message objects */
            if (flowEl instanceof Message) {
                messagesToRemove.add((Message) flowEl);
                Association msgAssociation = ((Message) flowEl).getDataConnectingAssociation();
                if (msgAssociation != null) {
                    associationsToRemove.add(msgAssociation);
                }
            }

            /* Add participants of child elements */
            if (flowEl instanceof ChoreographyActivity) {
                choreography.getParticipant().addAll(((ChoreographyActivity) flowEl).getParticipantRef());
            }

            /* MessageFlows of choreography task */
            if (flowEl instanceof ChoreographyTask) {
                ((ChoreographyTask) flowEl).createMessageFlows(choreography);
            }

            /* Subchoreography */
            else if (flowEl instanceof SubChoreography) {
                ((SubChoreography) flowEl).setParticipantsAndMessageFlows(choreography, bpmnElements, converter);
            }
        }

        this.getFlowElement().removeAll(messagesToRemove);
        for (Association msgAssociation : associationsToRemove) {
            bpmnElements.remove(msgAssociation.getId());
        }
        this.getFlowElement().removeAll(associationsToRemove);
    }

    public List<String> getIdsOfDiagramElements() {
        List<String> idList = new ArrayList<String>();

        /* FlowElements except edges */
        for (FlowElement flowEl : this.getFlowElement()) {
            if (!(flowEl instanceof Edge)) {
                idList.add(flowEl.getId());
            }

            /* Insert participant band elements */
            if (flowEl instanceof ChoreographyActivity) {
                for (Participant participant : ((ChoreographyActivity) flowEl).getParticipantRef()) {
                    idList.add(participant.getId());
                }
            }

            /* Child SubChoreographis */
            if (flowEl instanceof SubChoreography) {
                idList.addAll(((SubChoreography) flowEl).getIdsOfDiagramElements());
            }
        }

        /* Artifacts */
        for (Artifact artifact : getArtifact()) {
            idList.add(artifact.getId());
        }

        return idList;
    }

    /**
     * Retrieves {@link SubChoreography} recursively.
     *
     * @return
     */
    public List<SubChoreography> getSubChoreographies() {
        List<SubChoreography> subchoreographies = new ArrayList<SubChoreography>();

        for (FlowElement flowEle : getFlowElement()) {
            /* Subchoreography */
            if (flowEle instanceof SubChoreography) {
                subchoreographies.add((SubChoreography) flowEle);
                subchoreographies.addAll(((SubChoreography) flowEle).getSubChoreographies());
            }
        }

        return subchoreographies;
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
        v.visitSubChoreography(this);
    }

    /* Getter & Setter */

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
     * {@link JAXBElement }{@code <}{@link TDataObject }{@code >}
     * {@link JAXBElement }{@code <}{@link TEvent }{@code >}
     * {@link JAXBElement }{@code <}{@link ServiceTask }{@code >}
     * {@link JAXBElement }{@code <}{@link TChoreographyTask }{@code >}
     * {@link JAXBElement }{@code <}{@link TDataStore }{@code >}
     * {@link JAXBElement }{@code <}{@link TSubProcess }{@code >}
     * {@link JAXBElement }{@code <}{@link TIntermediateThrowEvent }{@code >}
     * {@link JAXBElement }{@code <}{@link UserTask }{@code >}
     * {@link JAXBElement }{@code <}{@link TSequenceFlow }{@code >}
     * {@link JAXBElement }{@code <}{@link TEventBasedGateway }{@code >}
     * {@link JAXBElement }{@code <}{@link AdHocSubProcess }{@code >}
     * {@link JAXBElement }{@code <}{@link SendTask }{@code >}
     * {@link JAXBElement }{@code <}{@link ChoreographySubProcess }{@code >}
     * {@link JAXBElement }{@code <}{@link ReceiveTask }{@code >}
     * {@link JAXBElement }{@code <}{@link TImplicitThrowEvent }{@code >}
     * {@link JAXBElement }{@code <}{@link TParallelGateway }{@code >}
     * {@link JAXBElement }{@code <}{@link TTask }{@code >}
     */
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
     * {@link JAXBElement }{@code <}{@link TArtifact }{@code >}
     * {@link JAXBElement }{@code <}{@link TAssociation }{@code >}
     * {@link JAXBElement }{@code <}{@link TGroup }{@code >}
     * {@link JAXBElement }{@code <}{@link TTextAnnotation }{@code >}
     */
    public List<Artifact> getArtifact() {
        if (artifact == null) {
            artifact = new ArrayList<Artifact>();
        }
        return this.artifact;
    }


    public List<DiagramElement> _getDiagramElements() {
        return _diagramElements;
    }


    public List<BaseElement> getCalledElements() {
        List<BaseElement> calledElements = new ArrayList<BaseElement>();

        for (FlowElement flowEl : getFlowElement()) {
            if (flowEl instanceof CallingElement) {
                calledElements.addAll(((CallingElement) flowEl).getCalledElements());
            }
        }

        return calledElements;
    }

}
