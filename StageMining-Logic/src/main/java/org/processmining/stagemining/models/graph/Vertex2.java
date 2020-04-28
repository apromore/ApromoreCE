/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Bruce Nguyen, Queensland University of Technology.
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
package org.processmining.stagemining.models.graph;

import java.util.HashSet;
import java.util.Set;

import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;

public class Vertex2 extends Vertex {
	private int discoveredTime = -1;
	private int finishedTime = -1;
	private double minCut  = 0.0;
	private Set<WeightedDirectedEdge<IVertex>> minCutSet = new HashSet<WeightedDirectedEdge<IVertex>>();
	private Set<Set<IVertex>> danglingComponents = new HashSet<Set<IVertex>>();
	private VertexColorEnum color = VertexColorEnum.BLACK;
	
	public Vertex2(String name) {
		super(name);
	}
	
	public int getDiscoverdTime() {
		return discoveredTime;
	}
	
	public void setDiscoveredTime(int discoveredTime) {
		this.discoveredTime = discoveredTime;
	}
	
	public int getFinishedTime() {
		return finishedTime;
	}
	
	public void setFinishedTime(int finishedTime) {
		this.finishedTime = finishedTime;
	}
	
	public VertexColorEnum getColor() {
		return color;
	}
	
	public void setColor(VertexColorEnum color) {
		this.color = color;
	}
	
	public double getMinCut() {
		return minCut;
	}
	
	public void setMinCut(double minCut) {
		this.minCut = minCut;
	}
	
	/**
	 * Note that the min cutset can be empty if this vertex
	 * is the single connection point between two phases.
	 * @return
	 */
	public Set<WeightedDirectedEdge<IVertex>> getMinCutSet() {
		return minCutSet;
	}
	
	public void setMinCutSet(Set<WeightedDirectedEdge<IVertex>> cutSet) {
		this.minCutSet = cutSet;
	}
	
	public Set<Set<IVertex>> getDanglingComponents() {
		return danglingComponents;
	}
}
