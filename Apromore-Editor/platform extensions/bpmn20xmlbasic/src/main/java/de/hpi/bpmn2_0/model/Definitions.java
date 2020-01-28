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

import de.hpi.bpmn2_0.annotations.ContainerElement;
import de.hpi.bpmn2_0.model.bpmndi.BPMNDiagram;
import de.hpi.bpmn2_0.model.bpmndi.BPMNPlane;
import de.hpi.bpmn2_0.model.choreography.Choreography;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.conversation.Conversation;
import de.hpi.bpmn2_0.model.conversation.GlobalCommunication;
import de.hpi.bpmn2_0.model.data_object.Message;
import de.hpi.bpmn2_0.model.event.*;
import de.hpi.bpmn2_0.model.extension.Extension;
import de.hpi.bpmn2_0.model.misc.Signal;
import de.hpi.diagram.SignavioUUID;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p/>
 * Java class for tDefinitions complex type.
 * <p/>
 * <p/>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p/>
 * <pre>
 * &lt;complexType name="tDefinitions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}import" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}extension" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}rootElement" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/spec/BPMN/20100524/DI}diagram" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}relationship" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="targetNamespace" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="expressionLanguage" type="{http://www.w3.org/2001/XMLSchema}anyURI" default="http://www.w3.org/1999/XPath" />
 *       &lt;attribute name="typeLanguage" type="{http://www.w3.org/2001/XMLSchema}anyURI" default="http://www.w3.org/2001/XMLSchema" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "definitions")
@XmlType(name = "tDefinitions", propOrder = {
        "extension",
        "targetNamespace",
        "expressionLanguage",
        "typeLanguage",
        "rootElement",
        "diagram"
})
public class Definitions {

    // @XmlElement(name = "import")
    // protected List<TImport> _import;
    protected List<Extension> extension;

    @XmlElementRefs({@XmlElementRef(type = Process.class),
            @XmlElementRef(type = Choreography.class),
            @XmlElementRef(type = Collaboration.class),
            @XmlElementRef(type = Conversation.class),
            @XmlElementRef(type = Signal.class),
            @XmlElementRef(type = Message.class)})
    protected List<BaseElement> rootElement;

    @XmlElementRef
    protected List<BPMNDiagram> diagram;
    // protected List<TRelationship> relationship;

    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    @XmlAttribute
    protected String name;

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anyURI")
    protected String targetNamespace;

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String expressionLanguage;

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String typeLanguage;

    @XmlAttribute(name = "exporter")
    protected String exporter;

    @XmlAttribute(name = "exporterVersion")
    protected String exporterVersion;

    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    @XmlTransient
    private Map<String, String> namespaces;

    @XmlTransient
    public List<String> unusedNamespaceDeclarations;

    @XmlTransient
    public Map<String, String> externalNSDefs;

    /**
     * The {@link Marshaller} invokes this method right before marshaling to
     * XML. The namespace are added as attributes to the definitions element.
     *
     * @param marshaller The marshaling context
     */
    public void beforeMarshal(Marshaller marshaller) {
        for (String prefix : this.getNamespaces().keySet()) {
            QName namespacePrefix = new QName("xmlns:" + prefix);
            this.getOtherAttributes().put(namespacePrefix,
                    this.getNamespaces().get(prefix));
        }
    }

//	public void afterMarshal(Marshaller source) {
//		
//		
//		
//	}

    /* Getter & Setter */

    /**
     * Gets the value of the import property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the import property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getImport().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link TImport }
     *
     *
     */
    // public List<TImport> getImport() {
    // if (_import == null) {
    // _import = new ArrayList<TImport>();
    // }
    // return this._import;
    // }

    /**
     * Gets the value of the extension property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the extension property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <p/>
     * <pre>
     * getExtension().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link TExtension }
     */
    public List<Extension> getExtension() {
        if (extension == null) {
            extension = new ArrayList<Extension>();
        }
        return this.extension;
    }

    /**
     * Gets the value of the rootElement property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the rootElement property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <p/>
     * <pre>
     * getRootElement().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link TMessageEventDefinition }{@code >}
     * {@link JAXBElement }{@code <}{@link TGlobalBusinessRuleTask }{@code >}
     * {@link JAXBElement }{@code <}{@link ErrorEventDefinition }{@code >}
     * {@link JAXBElement }{@code <}{@link Collaboration }{@code >}
     * {@link JAXBElement }{@code <}{@link Conversation }{@code >}
     * {@link JAXBElement }{@code <}{@link ConditionalEventDefinition }{@code >}
     * {@link JAXBElement }{@code <}{@link Process }{@code >} {@link JAXBElement }
     * {@code <}{@link Category }{@code >} {@link JAXBElement }{@code <}
     * {@link TItemDefinition }{@code >} {@link JAXBElement }{@code <}
     * {@link TEscalationEventDefinition }{@code >} {@link JAXBElement }{@code <}
     * {@link Message }{@code >} {@link JAXBElement }{@code <}
     * {@link CompensateEventDefinition }{@code >} {@link JAXBElement }{@code <}
     * {@link TCorrelationProperty }{@code >} {@link JAXBElement }{@code <}
     * {@link TEscalation }{@code >} {@link JAXBElement }{@code <}
     * {@link TInterface }{@code >} {@link JAXBElement }{@code <}
     * {@link TTimerEventDefinition }{@code >} {@link JAXBElement }{@code <}
     * {@link TGlobalUserTask }{@code >} {@link JAXBElement }{@code <}
     * {@link GlobalCommunication }{@code >} {@link JAXBElement }{@code <}
     * {@link Choreography }{@code >} {@link JAXBElement }{@code <}
     * {@link TResource }{@code >} {@link JAXBElement }{@code <}
     * {@link SignalEventDefinition }{@code >} {@link JAXBElement }{@code <}
     * {@link TEndPoint }{@code >} {@link JAXBElement }{@code <}
     * {@link RootElement }{@code >} {@link JAXBElement }{@code <}
     * {@link TPartnerEntity }{@code >} {@link JAXBElement }{@code <}
     * {@link TGlobalManualTask }{@code >} {@link JAXBElement }{@code <}
     * {@link TGlobalScriptTask }{@code >} {@link JAXBElement }{@code <}
     * {@link EventDefinition }{@code >} {@link JAXBElement }{@code <}
     * {@link TError }{@code >} {@link JAXBElement }{@code <}
     * {@link LinkEventDefinition }{@code >} {@link JAXBElement }{@code <}
     * {@link CancelEventDefinition }{@code >} {@link JAXBElement }{@code <}
     * {@link TPartnerRole }{@code >} {@link JAXBElement }{@code <}
     * {@link TGlobalTask }{@code >} {@link JAXBElement }{@code <}{@link TSignal }
     * {@code >} {@link JAXBElement }{@code <}{@link TTerminateEventDefinition }
     * {@code >} {@link JAXBElement }{@code <}{@link TGlobalChoreographyTask }
     * {@code >}
     */
    public List<BaseElement> getRootElement() {
        if (rootElement == null) {
            rootElement = new ArrayList<BaseElement>();
        }
        return this.rootElement;
    }

    /**
     * This Method return a List containing all edges within the diagram
     *
     * @return List<Edge>
     */
    public List<Edge> getEdges() {
        if (this.rootElement == null) {
            return new ArrayList<Edge>();
        }

        ArrayList<Edge> edges = new ArrayList<Edge>();

        for (BaseElement rootElement : this.rootElement) {

            if (rootElement instanceof Process) {

                for (FlowElement flowElement : ((Process) rootElement)
                        .getFlowElement()) {

                    if (flowElement instanceof Edge) {
                        edges.add((Edge) flowElement);
                    }

                    // Consider edges in container elements
                    if (flowElement instanceof ContainerElement) {
                        edges.addAll(((ContainerElement) flowElement).getChildEdges());
                    }
                }
            } else if (rootElement instanceof Collaboration) {

                for (MessageFlow messageFlow : ((Collaboration) rootElement)
                        .getMessageFlow())
                    edges.add(messageFlow);

            } else if (rootElement instanceof Choreography) {

                for (FlowElement flowElement : ((Choreography) rootElement)
                        .getFlowElement()) {

                    if (flowElement instanceof Edge) {
                        edges.add((Edge) flowElement);
                    }

                    // Consider edges in container elemetns
                    if (flowElement instanceof ContainerElement) {
                        edges.addAll(((ContainerElement) flowElement).getChildEdges());
                    }
                }
            }
        }

        return edges;
    }

    /**
     * Gets the value of the diagram property.
     * <p/>
     * <p/>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the diagram property.
     * <p/>
     * <p/>
     * For example, to add a new item, do as follows:
     * <p/>
     * <pre>
     * getDiagram().add(newItem);
     * </pre>
     * <p/>
     * <p/>
     * <p/>
     * Objects of the following type(s) are allowed in the list
     * {@link BpmnDiagram }
     */
    public List<BPMNDiagram> getDiagram() {
        if (diagram == null) {
            diagram = new ArrayList<>();
        }
        return this.diagram;
    }

    /**
     * Returns the first {@link BPMNPlane} or creates one if none exists.
     *
     * @return
     */
    public BPMNPlane getFirstPlane() {
        if (this.getDiagram().size() == 0) {
            BPMNDiagram diagram = new BPMNDiagram();
            diagram.setId(SignavioUUID.generate());
            this.getDiagram().add(diagram);
        }

        BPMNDiagram diagram = this.getDiagram().get(0);
        if (diagram.getBPMNPlane() == null) {
            diagram.setBPMNPlane(new BPMNPlane());
        }

        return diagram.getBPMNPlane();
    }

    /**
     * Gets the value of the relationship property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a
     * snapshot. Therefore any modification you make to the returned list will
     * be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the relationship property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getRelationship().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRelationship }
     *
     *
     */
    // public List<TRelationship> getRelationship() {
    // if (relationship == null) {
    // relationship = new ArrayList<TRelationship>();
    // }
    // return this.relationship;
    // }

    /**
     * Gets the value of the id property.
     *
     * @return possible object is {@link String }
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is {@link String }
     */
    public void setId(String value) {
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the value of the targetNamespace property.
     *
     * @return possible object is {@link String }
     */
    public String getTargetNamespace() {
        return targetNamespace;
    }

    /**
     * Sets the value of the targetNamespace property.
     *
     * @param value allowed object is {@link String }
     */
    public void setTargetNamespace(String value) {
        this.targetNamespace = value;
    }

    /**
     * Gets the value of the expressionLanguage property.
     *
     * @return possible object is {@link String }
     */
    public String getExpressionLanguage() {
        if (expressionLanguage == null) {
            return "http://www.w3.org/1999/XPath";
        } else {
            return expressionLanguage;
        }
    }

    /**
     * Sets the value of the expressionLanguage property.
     *
     * @param value allowed object is {@link String }
     */
    public void setExpressionLanguage(String value) {
        this.expressionLanguage = value;
    }

    /**
     * Gets the value of the typeLanguage property.
     *
     * @return possible object is {@link String }
     */
    public String getTypeLanguage() {
        if (typeLanguage == null) {
            return "http://www.w3.org/2001/XMLSchema";
        } else {
            return typeLanguage;
        }
    }

    /**
     * Sets the value of the typeLanguage property.
     *
     * @param value allowed object is {@link String }
     */
    public void setTypeLanguage(String value) {
        this.typeLanguage = value;
    }

    public String getExporter() {
        return exporter;
    }

    public void setExporter(String exporter) {
        this.exporter = exporter;
    }

    public String getExporterVersion() {
        return exporterVersion;
    }

    public void setExporterVersion(String exporterVersion) {
        this.exporterVersion = exporterVersion;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed
     * property on this class.
     * <p/>
     * <p/>
     * the map is keyed by the name of the attribute and the value is the string
     * value of the attribute.
     * <p/>
     * the map returned by this method is live, and you can add new attribute by
     * updating the map directly. Because of this design, there's no setter.
     *
     * @return always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    /**
     * The namespaces property contains an arbitrary set of namespace prefixes
     * their related URLs referenced inside the diagram.
     *
     * @return the namespaces property. always non-null
     */
    public Map<String, String> getNamespaces() {
        if (this.namespaces == null) {
            this.namespaces = new HashMap<String, String>();
        }

        return namespaces;
    }

}
