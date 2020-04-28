/*-
 * #%L
 * This file is part of "Apromore Community".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.processdiscoverer;

import java.util.Collections;
import java.util.Set;

import org.apromore.processdiscoverer.dfg.ArcType;
import org.apromore.processdiscoverer.dfg.abstraction.DFGAbstraction;
import org.apromore.processdiscoverer.logprocessors.EventClassifier;

import  org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

/**
 * 
 * @author Bruce Nguyen
 *
 */
public class AbstractionParams {
	private String attribute;
	private EventClassifier classifier;
	private double activities;
	private double arcs;
	private double parallelism;
	private boolean prioritizeParallelism;
	private boolean preserve_connectivity;
	private boolean inverted_nodes;
	private boolean inverted_arcs;
	private VisualizationType fixedType;
	private VisualizationAggregation fixedAggregation;
	private VisualizationType primaryType;
	private VisualizationAggregation primaryAggregation;
	boolean secondary;
	private VisualizationType secondaryType;
	private VisualizationAggregation secondaryAggregation;
	private Set<ArcType> arcTypes;
	private DFGAbstraction correspondingDFG; 
	
	public AbstractionParams(String attribute, double activities, double arcs, double parallelism, 
							boolean prioritizeParallelism, boolean preserve_connectivity, 
							boolean inverted_nodes, boolean inverted_arcs, boolean secondary, VisualizationType fixedType, 
							VisualizationAggregation fixedAggregation, VisualizationType primaryType, 
							VisualizationAggregation primaryAggregation, VisualizationType secondaryType, 
							VisualizationAggregation secondaryAggregation, 
							Set<ArcType> arcTypes,
							DFGAbstraction correspondingDFG) {
		this.attribute = attribute;
		this.classifier = new EventClassifier(attribute);
		this.activities = activities;
		this.arcs = arcs;
		this.parallelism = parallelism;
		this.prioritizeParallelism = prioritizeParallelism;
		this.preserve_connectivity = preserve_connectivity;
		this.inverted_nodes = inverted_nodes;
		this.inverted_arcs = inverted_arcs;
		this.fixedType = fixedType;
		this.fixedAggregation = fixedAggregation;
		this.primaryType = primaryType;
		this.primaryAggregation = primaryAggregation;
		this.secondaryType = secondaryType;
		this.secondaryAggregation= secondaryAggregation;
		this.secondary = secondary;
		this.arcTypes = arcTypes;
		this.correspondingDFG = correspondingDFG;
	}
	
	public String getAttribute() {
		return this.attribute;
	}
	
	public EventClassifier getClassifier() {
		return classifier;
	}
	
	public double getActivityLevel() {
		return this.activities;
	}
	
	public double getArcLevel() {
		return this.arcs;
	}
	
	public double getParallelismLevel() {
		return this.parallelism;
	}
	
	public boolean prioritizeParallelism() {
		return this.prioritizeParallelism;
	}
	
	public boolean preserveConnectivity() {
		return this.preserve_connectivity;
	}
	
	public boolean invertedNodes() {
		return this.inverted_nodes;
	}
	
	public boolean invertedArcs() {
		return this.inverted_arcs;
	}
	
	public VisualizationType getFixedType() {
		return this.fixedType;
	}
	
	public VisualizationAggregation getFixedAggregation() {
		return this.fixedAggregation;
	}
	
	public VisualizationType getPrimaryType() {
		return this.primaryType;
	}
	
	public VisualizationAggregation getPrimaryAggregation() {
		return this.primaryAggregation;
	}
	
	public VisualizationType getSecondaryType() {
		return this.secondaryType;
	}
	
	public VisualizationAggregation getSecondaryAggregation() {
		return this.secondaryAggregation;
	}
	
	public boolean getSecondary() {
		return this.secondary;
	}
	
	public Set<ArcType> getArcTypes() {
		return Collections.unmodifiableSet(this.arcTypes);
	}
	
	public DFGAbstraction getCorrepondingDFG() {
		return this.correspondingDFG;
	}
}
