/*-
 * #%L
 * This file is part of "Apromore Community".
 * 
 * Copyright (C) 2017 Bruce Nguyen, Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.processmining.stagemining.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;
import org.processmining.stagemining.models.graph.WeightedDirectedGraph;

public class ConnectedComponentSearch {
	private WeightedDirectedGraph g = null;
	private Map<IVertex,Boolean> visitedMap = new HashMap<IVertex,Boolean>(); 
	private Set<IVertex> vertexSetOneTraversal = new HashSet<IVertex>();
	private Set<Set<IVertex>> connectedComponents = new HashSet<Set<IVertex>>();
	
	public ConnectedComponentSearch(WeightedDirectedGraph g) {
		this.g = g;
	}
	
	/**
	 * Find connected components in a graph with DFS
	 * @param g
	 * @return
	 */
	public Set<Set<IVertex>> findConnectedComponents() {
		for (IVertex vertex : g.getVertices()) {
			visitedMap.put(vertex, false);
		}
		for (IVertex v : g.getVertices()) {
			if (!visitedMap.get(v)) {
				vertexSetOneTraversal = new HashSet<IVertex>();
				dfsToFindCC(v);
				connectedComponents.add(vertexSetOneTraversal);
			}
		}
		
		return connectedComponents;
	}
	
	private void dfsToFindCC(IVertex v) {
		visitedMap.put(v, true);
		vertexSetOneTraversal.add(v);
		Set<Vertex> adjacents = new HashSet<Vertex>(g.getDirectSuccessors((Vertex)v));
		adjacents.addAll(new HashSet<Vertex>(g.getDirectPredecessors((Vertex)v)));
		adjacents.remove(v);
		for (IVertex adjacent : adjacents) {
			if (!visitedMap.get(adjacent)) {
				dfsToFindCC(adjacent);
			}
		}
	}
}
