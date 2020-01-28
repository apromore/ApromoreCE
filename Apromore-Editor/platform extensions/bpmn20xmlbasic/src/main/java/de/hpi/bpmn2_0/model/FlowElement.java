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

import de.hpi.bpmn2_0.model.activity.SubProcess;
import de.hpi.bpmn2_0.model.choreography.SubChoreography;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.data_object.DataStoreReference;
import de.hpi.bpmn2_0.model.misc.Auditing;
import de.hpi.bpmn2_0.model.misc.Monitoring;
import de.hpi.bpmn2_0.model.participant.Lane;
import de.hpi.bpmn2_0.transformation.Visitor;
import de.hpi.bpmn2_0.util.EscapingStringAdapter;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * Java class for tFlowElement complex type.
 * <p/>
 * <p/>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tFlowElement">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tBaseElement">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}auditing" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}monitoring" minOccurs="0"/>
 *         &lt;element name="categoryValue" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tFlowElement", propOrder = {"auditing", "monitoring",
        "categoryValue"
// "incoming",
// "outgoing"
// "process"
})
@XmlSeeAlso({
// SequenceFlow.class,
// DataObject.class,
// DataStore.class,
        Lane.class, FlowNode.class, DataStoreReference.class})
public abstract class FlowElement extends BaseElement {

    protected Auditing auditing;
    protected Monitoring monitoring;
    protected List<QName> categoryValue;

    @XmlAttribute
    @XmlJavaTypeAdapter(EscapingStringAdapter.class)
    protected String name;

    // @XmlIDREF
    // @XmlSchemaType(name = "IDREF")
    // @XmlElement(name = "incoming", type = Edge.class)
    @XmlTransient
    protected List<Edge> incoming;

    // @XmlIDREF
    // @XmlSchemaType(name = "IDREF")
    // @XmlElement(name = "outgoing", type = Edge.class)
    @XmlTransient
    protected List<Edge> outgoing;

    /* The process the element belongs to */
    // @XmlIDREF
    // @XmlAttribute
    // @XmlSchemaType(name = "IDREF")
    @XmlTransient
    protected Process process;

    @XmlTransient
    protected SubProcess subProcess;
    @XmlTransient
    protected SubChoreography subChoreography;
    @XmlTransient
    protected String processid;

    /**
     * Default constructor
     */
    public FlowElement() {

    }

    /**
     * Copy constructor
     */
    public FlowElement(FlowElement flowEl) {
        super(flowEl);

        if (flowEl.getCategoryValue().size() > 0)
            this.getCategoryValue().addAll(flowEl.getCategoryValue());

        if (flowEl.getIncoming().size() > 0)
            this.getIncoming().addAll(flowEl.getIncoming());

        if (flowEl.getOutgoing().size() > 0)
            this.getOutgoing().addAll(flowEl.getOutgoing());

        this.setAuditing(flowEl.getAuditing());
        this.setMonitoring(flowEl.getMonitoring());

        this.setProcess(flowEl.getProcess());
        this.setName(flowEl.getName());
    }

    //TODO move to visitor?

    /**
     * Another helper for the import. If the element is of fixed size, then it
     * may have to be adjusted after import from other tools.
     */
    public boolean isElementWithFixedSize() {
        return false;
    }

    public void acceptVisitor(Visitor v) {
        v.visitFlowElement(this);
    }

    public void afterUnmarshal(Unmarshaller u, Object parent) {
        if (parent != null && parent instanceof SubProcess) {
            this.subProcess = (SubProcess) parent;
        }

        if (parent != null && parent instanceof SubChoreography) {
            this.subChoreography = (SubChoreography) parent;
        }

        if (parent != null && parent instanceof Process) {
            this.process = (Process) parent;
        }
    }

    /* Getter & Setter */

    /**
     * Gets the value of the incoming property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the incoming property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <p/>
     * <pre>
     * getIncoming().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list {@link QName }
     */
    public List<Edge> getIncoming() {
        if (incoming == null) {
            incoming = new ArrayList<Edge>();
        }
        return this.incoming;
    }

    /**
     * Gets the value of the outgoing property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the outgoing property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <p/>
     * <pre>
     * getOutgoing().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list {@link QName }
     */
    public List<Edge> getOutgoing() {
        if (outgoing == null) {
            outgoing = new ArrayList<Edge>();
        }
        return this.outgoing;
    }

    /**
     * Gets the value of the auditing property.
     *
     * @return possible object is {@link Auditing }
     */
    public Auditing getAuditing() {
        return auditing;
    }

    /**
     * Sets the value of the auditing property.
     *
     * @param value allowed object is {@link Auditing }
     */
    public void setAuditing(Auditing value) {
        this.auditing = value;
    }

    /**
     * Gets the value of the monitoring property.
     *
     * @return possible object is {@link Monitoring }
     */
    public Monitoring getMonitoring() {
        return monitoring;
    }

    /**
     * Sets the value of the monitoring property.
     *
     * @param value allowed object is {@link Monitoring }
     */
    public void setMonitoring(Monitoring value) {
        this.monitoring = value;
    }

    /**
     * Gets the value of the categoryValue property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the categoryValue property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <p/>
     * <pre>
     * getCategoryValue().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list {@link QName }
     */
    public List<QName> getCategoryValue() {
        if (categoryValue == null) {
            categoryValue = new ArrayList<QName>();
        }
        return this.categoryValue;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * @return the process
     */
    public Process getProcess() {
        return process;
    }

    /**
     * @param process the process to set
     */
    public void setProcess(Process process) {
        this.process = process;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    public SubProcess getSubProcess() {
        return subProcess;
    }

    public void setSubProcess(SubProcess subProcess) {
        this.subProcess = subProcess;
    }

    public SubChoreography getSubChoreography() {
        return subChoreography;
    }

    public void setSubChoreography(SubChoreography subChoreography) {
        this.subChoreography = subChoreography;
    }

    public String getProcessid() {
        return processid;
    }

    public void setProcessid(String processid) {
        this.processid = processid;
    }

    public boolean hasValidRoundTripProcessId() {
        String processId = this.getProcessid();

        return processId != null && processId.length() > 0
                && processId.matches("^\\D([^\\s])*");
    }
}
