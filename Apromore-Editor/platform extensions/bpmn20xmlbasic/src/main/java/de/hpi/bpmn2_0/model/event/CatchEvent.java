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

package de.hpi.bpmn2_0.model.event;

import de.hpi.bpmn2_0.transformation.Visitor;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for tCatchEvent complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tCatchEvent">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tEvent">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}dataOutput" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}dataOutputAssociation" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}outputSet" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}eventDefinition" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="eventDefinitionRef" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="parallelMultiple" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tCatchEvent", propOrder = {
//    "dataOutput",
//    "dataOutputAssociation",
//    "outputSet",
//    "eventDefinition",
//    "eventDefinitionRef"
})
@XmlSeeAlso({
        StartEvent.class,
        IntermediateCatchEvent.class
})
public abstract class CatchEvent
        extends Event {

    //    protected List<DataOutput> dataOutput;
//    protected List<DataOutputAssociation> dataOutputAssociation;
//    protected TOutputSet outputSet;
    @XmlAttribute
    protected Boolean parallelMultiple;

    /**
     * Gets the value of the dataOutput property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataOutput property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataOutput().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataOutput }
     *
     *
     */
//    public List<DataOutput> getDataOutput() {
//        if (dataOutput == null) {
//            dataOutput = new ArrayList<DataOutput>();
//        }
//        return this.dataOutput;
//    }

    /**
     * Gets the value of the dataOutputAssociation property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dataOutputAssociation property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDataOutputAssociation().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DataOutputAssociation }
     *
     *
     */
//    public List<DataOutputAssociation> getDataOutputAssociation() {
//        if (dataOutputAssociation == null) {
//            dataOutputAssociation = new ArrayList<DataOutputAssociation>();
//        }
//        return this.dataOutputAssociation;
//    }

    /**
     * Gets the value of the outputSet property.
     *
     * @return
     *     possible object is
     *     {@link TOutputSet }
     *
     */
//    public TOutputSet getOutputSet() {
//        return outputSet;
//    }

    /**
     * Sets the value of the outputSet property.
     *
     * @param value
     *     allowed object is
     *     {@link TOutputSet }
     *
     */
//    public void setOutputSet(TOutputSet value) {
//        this.outputSet = value;
//    }


    /**
     * Gets the value of the parallelMultiple property.
     *
     * @return possible object is
     *         {@link Boolean }
     */
    public boolean isParallelMultiple() {
        if (parallelMultiple == null) {
            return false;
        } else {
            return parallelMultiple;
        }
    }

    /**
     * Sets the value of the parallelMultiple property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setParallelMultiple(Boolean value) {
        this.parallelMultiple = value;
    }

    public void acceptVisitor(Visitor v) {
        v.visitCatchEvent(this);
    }


}
