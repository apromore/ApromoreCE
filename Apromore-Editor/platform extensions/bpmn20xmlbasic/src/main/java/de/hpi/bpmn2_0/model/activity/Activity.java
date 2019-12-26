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

import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.activity.loop.LoopCharacteristics;
import de.hpi.bpmn2_0.model.activity.loop.MultiInstanceLoopCharacteristics;
import de.hpi.bpmn2_0.model.activity.loop.StandardLoopCharacteristics;
import de.hpi.bpmn2_0.model.activity.resource.ActivityResource;
import de.hpi.bpmn2_0.model.activity.resource.HumanPerformer;
import de.hpi.bpmn2_0.model.activity.resource.Performer;
import de.hpi.bpmn2_0.model.activity.resource.PotentialOwner;
import de.hpi.bpmn2_0.model.connector.DataInputAssociation;
import de.hpi.bpmn2_0.model.connector.DataOutputAssociation;
import de.hpi.bpmn2_0.model.data_object.*;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;
import de.hpi.bpmn2_0.model.extension.PropertyListItem;
import de.hpi.bpmn2_0.model.misc.IoOption;
import de.hpi.bpmn2_0.model.misc.Property;
import de.hpi.bpmn2_0.transformation.Visitor;
import de.hpi.diagram.SignavioUUID;

import javax.xml.bind.annotation.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * <p>Java class for tActivity complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tActivity">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tFlowNode">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}ioSpecification" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}property" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}dataInputAssociation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}dataOutputAssociation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}activityResource" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}loopCharacteristics" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="isForCompensation" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="startQuantity" type="{http://www.w3.org/2001/XMLSchema}integer" default="1" />
 *       &lt;attribute name="completionQuantity" type="{http://www.w3.org/2001/XMLSchema}integer" default="1" />
 *       &lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tActivity", propOrder = {
        "ioSpecification",
        "property",
        "dataInputAssociation",
        "dataOutputAssociation",
        "activityResource",
        "loopCharacteristics",
        "additionalProperties"
})
@XmlSeeAlso({
        SubProcess.class,
        Transaction.class,
        Task.class,
        CallActivity.class
})
public abstract class Activity
        extends FlowNode {

    protected InputOutputSpecification ioSpecification;
    protected List<Property> property;

    @XmlElement(name = "dataInputAssociation", type = DataInputAssociation.class)
    protected List<DataInputAssociation> dataInputAssociation;

    @XmlElement(name = "dataOutputAssociation", type = DataOutputAssociation.class)
    protected List<DataOutputAssociation> dataOutputAssociation;

    @XmlElementRefs({
            @XmlElementRef(type = ActivityResource.class),
            @XmlElementRef(type = Performer.class),
            @XmlElementRef(type = HumanPerformer.class),
            @XmlElementRef(type = PotentialOwner.class)
    })
    protected List<ActivityResource> activityResource;

    @XmlElementRefs({
            @XmlElementRef(type = StandardLoopCharacteristics.class),
            @XmlElementRef(type = MultiInstanceLoopCharacteristics.class)
    })
    protected LoopCharacteristics loopCharacteristics;

    //	@XmlIDREF
//	@XmlElement(name = "boundaryEventRef", type = BoundaryEvent.class)
    @XmlTransient
    protected List<BoundaryEvent> boundaryEventRefs;

    @XmlAttribute
    protected Boolean isForCompensation;

    @XmlAttribute
    protected BigInteger startQuantity;

    @XmlAttribute
    protected BigInteger completionQuantity;

    @XmlAttribute(name = "default")
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object _default;

    @XmlElementRef
    protected List<PropertyListItem> additionalProperties;

    @XmlTransient
    private List<HashMap<String, IoOption>> inputSetInfo;

    @XmlTransient
    private List<HashMap<String, IoOption>> outputSetInfo;

    @XmlTransient
    private List<BoundaryEvent> attachedBoundaryEvents;

    /**
     * Default constructor
     */
    public Activity() {

    }

    /**
     * Copy constructor
     *
     * @param act The {@link Activity} to copy
     */
    public Activity(Activity act) {
        super(act);

        if (act.getProperty().size() > 0)
            this.getProperty().addAll(act.getProperty());

        if (act.getDataInputAssociation().size() > 0)
            this.getDataInputAssociation().addAll(act.getDataInputAssociation());

        if (act.getDataOutputAssociation().size() > 0)
            this.getDataOutputAssociation().addAll(act.getDataOutputAssociation());

        if (act.getActivityResource().size() > 0)
            this.getActivityResource().addAll(act.getActivityResource());

        if (act.getBoundaryEventRefs().size() > 0)
            this.getBoundaryEventRefs().addAll(act.getBoundaryEventRefs());

        if (act.getInputSetInfo().size() > 0)
            this.getInputSetInfo().addAll(act.getInputSetInfo());

        if (act.getOutputSetInfo().size() > 0)
            this.getOutputSetInfo().addAll(act.getOutputSetInfo());

        if (act.getAdditionalProperties().size() > 0) {
            this.getAdditionalProperties().addAll(act.getAdditionalProperties());
        }

        this.setIoSpecification(act.getIoSpecification());
        this.setLoopCharacteristics(act.getLoopCharacteristics());
        this.setIsForCompensation(act.isForCompensation);
        this.setStartQuantity(act.getStartQuantity());
        this.setCompletionQuantity(act.getCompletionQuantity());
        this.setDefault(act.getDefault());
    }

    /* Transformation logic methods */

    /**
     * Determines and sets the {@link InputOutputSpecification} of an activity.
     * <p/>
     * Per default there exists exactly one {@link InputSet} and one {@link OutputSet}.
     * All input and output data objects are associated by theses sets. Both
     * sets are linked towards each other to define a default IORule.
     */
    public void determineIoSpecification() {

        /* Process data inputs */
        InputSet inputSet = new InputSet();
        inputSet.setName("DefaultInputSet");
        inputSet.setId(SignavioUUID.generate());
        for (DataInputAssociation dia : this.getDataInputAssociation()) {

            if (dia.getSourceRef() instanceof DataInput) {
                DataInput input = (DataInput) dia.getSourceRef();

                for (HashMap<String, IoOption> inputSetDesc : this.getInputSetInfo()) {
                    IoOption opt = inputSetDesc.get(input.getName());
                    if (opt != null) {
                        /* Append to appropriate list of data inputs */
                        inputSet.getDataInputRefs().add(input);

                        if (opt.isOptional())
                            inputSet.getOptionalInputRefs().add(input);

                        if (opt.isWhileExecuting())
                            inputSet.getWhileExecutingInputRefs().add(input);
                    }
                }
            }
        }

        /* Process data outputs */
        OutputSet outputSet = new OutputSet();
        outputSet.setName("DefaultOutputSet");
        outputSet.setId(SignavioUUID.generate());
        for (DataOutputAssociation dia : this.getDataOutputAssociation()) {

            if (dia.getTargetRef() instanceof DataOutput) {
                DataOutput output = (DataOutput) dia.getTargetRef();

                for (HashMap<String, IoOption> outputSetDesc : this.getOutputSetInfo()) {
                    IoOption opt = outputSetDesc.get(output.getName());
                    if (opt != null) {
                        /* Append to appropriate list of data inputs */
                        outputSet.getDataOutputRefs().add(output);

                        if (opt.isOptional())
                            outputSet.getOptionalOutputRefs().add(output);

                        if (opt.isWhileExecuting())
                            outputSet.getWhileExecutingOutputRefs().add(output);
                    }
                }
            }
        }

        /* Link both sets against each other to specifies a default IORule and
           * dependency between them. */

        inputSet.getOutputSetRefs().add(outputSet);
        outputSet.getInputSetRefs().add(inputSet);

        /* Add input set to specification */
        if (inputSet.getDataInputRefs().size() > 0 && outputSet.getDataOutputRefs().size() > 0) {
            InputOutputSpecification ioSpec = new InputOutputSpecification();
            ioSpec.setId(SignavioUUID.generate());
            ioSpec.getInputSet().add(inputSet);
            ioSpec.getOutputSet().add(outputSet);
            ioSpec.getDataInput();
            ioSpec.getDataOutput();
            this.setIoSpecification(ioSpec);
        }
    }


    /* Getter & Setter */

    public List<PropertyListItem> getAdditionalProperties() {
        if (additionalProperties == null) {
            additionalProperties = new ArrayList<PropertyListItem>();
        }
        return additionalProperties;
    }

    /**
     * @return The list of boundary event references
     */
    public List<BoundaryEvent> getBoundaryEventRefs() {
        if (this.boundaryEventRefs == null) {
            this.boundaryEventRefs = new ArrayList<BoundaryEvent>();
        }
        return this.boundaryEventRefs;
    }

    /**
     * Gets the value of the ioSpecification property.
     *
     * @return possible object is
     *         {@link InputOutputSpecification }
     */
    public InputOutputSpecification getIoSpecification() {
        return ioSpecification;
    }

    /**
     * Sets the value of the ioSpecification property.
     *
     * @param value allowed object is
     *              {@link InputOutputSpecification }
     */
    public void setIoSpecification(InputOutputSpecification value) {
        this.ioSpecification = value;
    }

    /**
     * Gets the value of the property property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the property property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProperty().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link Property }
     */
    public List<Property> getProperty() {
        if (property == null) {
            property = new ArrayList<Property>();
        }
        return this.property;
    }

    /**
     * Gets the value of the dataInputAssociation property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataInputAssociation property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataInputAssociation().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link DataInputAssociation }
     */
    public List<DataInputAssociation> getDataInputAssociation() {
        if (dataInputAssociation == null) {
            dataInputAssociation = new ArrayList<DataInputAssociation>();
        }
        return this.dataInputAssociation;
    }

    /**
     * Gets the value of the dataOutputAssociation property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataOutputAssociation property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataOutputAssociation().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link DataOutputAssociation }
     */
    public List<DataOutputAssociation> getDataOutputAssociation() {
        if (dataOutputAssociation == null) {
            dataOutputAssociation = new ArrayList<DataOutputAssociation>();
        }
        return this.dataOutputAssociation;
    }

    /**
     * Gets the value of the activityResource property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the activityResource property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActivityResource().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@code <}{@link HumanPerformer }{@code >}
     * {@code <}{@link Performer }{@code >}
     * {@code <}{@link PotentialOwner }{@code >}
     * {@code <}{@link ActivityResource }{@code >}
     */
    public List<ActivityResource> getActivityResource() {
        if (activityResource == null) {
            activityResource = new ArrayList<ActivityResource>();
        }
        return this.activityResource;
    }

    /**
     * Gets the value of the loopCharacteristics property.
     *
     * @return possible object is
     *         {@ link MultiInstanceLoopCharacteristics }
     *         {@link LoopCharacteristics }
     *         {@link StandardLoopCharacteristics }
     */
    public LoopCharacteristics getLoopCharacteristics() {
        return loopCharacteristics;
    }

    /**
     * Sets the value of the loopCharacteristics property.
     *
     * @param value allowed object is
     *              {@ link MultiInstanceLoopCharacteristics }
     *              {@link LoopCharacteristics }
     *              {@link StandardLoopCharacteristics }
     */
    public void setLoopCharacteristics(LoopCharacteristics value) {
        this.loopCharacteristics = value;
    }

    /**
     * Gets the value of the isForCompensation property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public boolean isForCompensation() {
        if (isForCompensation == null) {
            return false;
        } else {
            return isForCompensation;
        }
    }

    /**
     * Sets the value of the isForCompensation property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setIsForCompensation(Boolean value) {
        this.isForCompensation = value;
    }

    /**
     * Gets the value of the startQuantity property.
     *
     * @return possible object is
     *         {@link BigInteger }
     */
    public BigInteger getStartQuantity() {
        if (startQuantity == null) {
            return new BigInteger("1");
        } else {
            return startQuantity;
        }
    }

    /**
     * Sets the value of the startQuantity property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setStartQuantity(BigInteger value) {
        this.startQuantity = value;
    }

    /**
     * Gets the value of the completionQuantity property.
     *
     * @return possible object is
     *         {@link BigInteger }
     */
    public BigInteger getCompletionQuantity() {
        if (completionQuantity == null) {
            return new BigInteger("1");
        } else {
            return completionQuantity;
        }
    }

    /**
     * Sets the value of the completionQuantity property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setCompletionQuantity(BigInteger value) {
        this.completionQuantity = value;
    }

    /**
     * Gets the value of the default property.
     *
     * @return possible object is
     *         {@link Object }
     */
    public Object getDefault() {
        return _default;
    }

    /**
     * Sets the value of the default property.
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setDefault(Object value) {
        this._default = value;
    }


    /**
     * @return the inputSetInfo
     */
    public List<HashMap<String, IoOption>> getInputSetInfo() {
        if (this.inputSetInfo == null)
            this.inputSetInfo = new ArrayList<HashMap<String, IoOption>>();
        return inputSetInfo;
    }


    /**
     * @return the outputSetInfo
     */
    public List<HashMap<String, IoOption>> getOutputSetInfo() {
        if (this.outputSetInfo == null)
            this.outputSetInfo = new ArrayList<HashMap<String, IoOption>>();
        return outputSetInfo;
    }

    public List<BoundaryEvent> getAttachedBoundaryEvents() {
        if (attachedBoundaryEvents == null) {
            attachedBoundaryEvents = new ArrayList<BoundaryEvent>();
        }

        return attachedBoundaryEvents;
    }

    public void acceptVisitor(Visitor v) {
        v.visitActivity(this);
    }

}
