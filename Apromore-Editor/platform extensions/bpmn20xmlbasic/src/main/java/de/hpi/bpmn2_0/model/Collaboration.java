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

import de.hpi.bpmn2_0.model.artifacts.Artifact;
import de.hpi.bpmn2_0.model.choreography.Choreography;
import de.hpi.bpmn2_0.model.connector.Association;
import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.conversation.ConversationLink;
import de.hpi.bpmn2_0.model.conversation.ConversationNode;
import de.hpi.bpmn2_0.model.conversation.CorrelationKey;
import de.hpi.bpmn2_0.model.participant.Participant;
import de.hpi.bpmn2_0.util.EscapingStringAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for tCollaboration complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tCollaboration">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tRootElement">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}participant" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}messageFlow" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}artifact" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}conversation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}conversationAssociation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}participantAssociation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}messageFlowAssociation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isClosed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="choreographyRef" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCollaboration", propOrder = {
        "participant",
        "messageFlow",
        "artifact",
        "association",
        "conversationNode",
//	    "conversationAssociation",
//	    "participantAssociation",
//	    "messageFlowAssociation",
        "correlationKey",
        "choreographyRef",
        "conversationLink"
})
@XmlSeeAlso({
        Choreography.class
})
public class Collaboration
        extends RootElement {

    @XmlElement(type = Participant.class)
    protected List<Participant> participant;
    protected List<MessageFlow> messageFlow;
    @XmlElementRef
    protected List<Artifact> artifact;
    @XmlElementRef
    protected List<ConversationNode> conversationNode;
    @XmlElementRef
    protected List<Association> association;
    //    protected List<ConversationAssociation> conversationAssociation;
//    protected List<ParticipantAssociation> participantAssociation;
//    protected List<MessageFlowAssociation> messageFlowAssociation;
    protected List<CorrelationKey> correlationKey;

    @XmlIDREF
    @XmlElement
    protected List<Choreography> choreographyRef;
    protected List<ConversationLink> conversationLink;

    @XmlAttribute(name = "name")
    @XmlJavaTypeAdapter(EscapingStringAdapter.class)
    protected String name;
    @XmlAttribute(name = "isClosed")
    protected Boolean isClosed;

    /* Constructors */

    /**
     * Default constructor
     */
    public Collaboration() {
        super();
    }

    public Collaboration(Collaboration collaboration) {
        super(collaboration);

        this.getParticipant().addAll(collaboration.getParticipant());
        this.getMessageFlow().addAll(collaboration.getMessageFlow());
        this.getArtifact().addAll(collaboration.getArtifact());
        this.getConversationNode().addAll(collaboration.getConversationNode());
        this.getAssociation().addAll(collaboration.getAssociation());
        this.getCorrelationKey().addAll(collaboration.getCorrelationKey());
        this.getChoreographyRef().addAll(collaboration.getChoreographyRef());

        this.setName(collaboration.getName());
        this.setIsClosed(collaboration.isIsClosed());

    }

    public List<BaseElement> getChilds() {
        List<BaseElement> childs = super.getChilds();

        childs.addAll(this.getParticipant());
        childs.addAll(this.getMessageFlow());
        childs.addAll(this.getArtifact());
        childs.addAll(this.getConversationNode());
        childs.addAll(this.getConversationLink());
        childs.addAll(this.getAssociation());
        childs.addAll(this.getCorrelationKey());

        return childs;
    }

    /* Getter & Setter */


    /**
     * Gets the value of the participant property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the participant property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParticipant().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link TParticipant }
     */
    public List<Participant> getParticipant() {
        if (participant == null) {
            participant = new ArrayList<Participant>();
        }
        return this.participant;
    }

    /**
     * Gets the value of the messageFlow property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the messageFlow property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMessageFlow().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link TMessageFlow }
     */
    public List<MessageFlow> getMessageFlow() {
        if (messageFlow == null) {
            messageFlow = new ArrayList<MessageFlow>();
        }
        return this.messageFlow;
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

    /**
     * Gets the value of the conversation property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the conversation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConversation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Conversation }
     *
     *
     */
//    public List<Conversation> getConversation() {
//        if (conversation == null) {
//            conversation = new ArrayList<Conversation>();
//        }
//        return this.conversation;
//    }

    /**
     * Gets the value of the conversationAssociation property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the conversationAssociation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConversationAssociation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TConversationAssociation }
     *
     *
     */
//    public List<TConversationAssociation> getConversationAssociation() {
//        if (conversationAssociation == null) {
//            conversationAssociation = new ArrayList<TConversationAssociation>();
//        }
//        return this.conversationAssociation;
//    }

    /**
     * Gets the value of the participantAssociation property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the participantAssociation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParticipantAssociation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TParticipantAssociation }
     *
     *
     */
//    public List<TParticipantAssociation> getParticipantAssociation() {
//        if (participantAssociation == null) {
//            participantAssociation = new ArrayList<TParticipantAssociation>();
//        }
//        return this.participantAssociation;
//    }

    /**
     * Gets the value of the messageFlowAssociation property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the messageFlowAssociation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMessageFlowAssociation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TMessageFlowAssociation }
     *
     *
     */
//    public List<TMessageFlowAssociation> getMessageFlowAssociation() {
//        if (messageFlowAssociation == null) {
//            messageFlowAssociation = new ArrayList<TMessageFlowAssociation>();
//        }
//        return this.messageFlowAssociation;
//    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     *         {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

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

    /**
     * Gets the value of the choreographyRef property.
     *
     * @return possible object is
     *         {@link Choreography }
     */
    public List<Choreography> getChoreographyRef() {
        if (choreographyRef == null)
            choreographyRef = new ArrayList<Choreography>();
        return choreographyRef;
    }

    public List<ConversationNode> getConversationNode() {
        if (conversationNode == null)
            conversationNode = new ArrayList<ConversationNode>();
        return conversationNode;
    }

    public List<CorrelationKey> getCorrelationKey() {
        if (correlationKey == null)
            correlationKey = new ArrayList<CorrelationKey>();
        return correlationKey;
    }

    public List<ConversationLink> getConversationLink() {
        if (conversationLink == null)
            conversationLink = new ArrayList<ConversationLink>();
        return conversationLink;
    }

    public List<Association> getAssociation() {
        if (association == null)
            association = new ArrayList<Association>();
        return association;
    }
}
