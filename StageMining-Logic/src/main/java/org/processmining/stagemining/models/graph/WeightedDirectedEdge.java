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

import org.jbpt.graph.DirectedEdge;
import org.jbpt.graph.abs.AbstractMultiDirectedGraph;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;

public class WeightedDirectedEdge<V extends IVertex> extends DirectedEdge {
	private float weight;
	private EdgeTypeEnum type = EdgeTypeEnum.TREE;
	
	public WeightedDirectedEdge(AbstractMultiDirectedGraph<?, Vertex> g, Vertex source, Vertex target, float weight) {
		 super(g, source, target);
		 this.weight = weight;
	}
	
	public void setWeight(float weight) {
		this.weight = weight;
	}
	
	public float getWeight() {
		return this.weight;
	}
	
	public void setEdgeType(EdgeTypeEnum type) {
		this.type = type;
	}
	
	public EdgeTypeEnum getEdgeType() {
		return this.type;
	}
	
	@Override
	public String toString() {
		return String.format("%s->%s(%s)", this.source, this.target, this.weight);
	}
}
