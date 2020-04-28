/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Alireza Ostovar.
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
package org.apromore.prodrift.util;


//import org.jbpt.graph.DirectedEdge;
//import org.jbpt.graph.DirectedGraph;
//import org.jbpt.hypergraph.abs.Vertex;

public class GraphUtils {

	public static void transitiveClosure(boolean[][] m) {
		int n = m.length;

		for (int k = 0; k < n; k++)
			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++)
					m[i][j] |= (m[i][k] & m[k][j]);
	}

	public static boolean[][] transitiveReduction(boolean[][] m) {
		int n = m.length;

		boolean[][] originalMatrix = new boolean[n][n];
		copyMatrix(m, originalMatrix);

		for (int j = 0; j < n; ++j)
			for (int i = 0; i < n; ++i)
				if (originalMatrix[i][j])
					for (int k = 0; k < n; ++k)
						if (originalMatrix[j][k])
							m[i][k] = false;
		
		return originalMatrix;
	}

	private static void copyMatrix(boolean[][] m, boolean[][] copyOfM) {
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m.length; j++)
				copyOfM[i][j] = m[i][j];
		
	}

//	public static boolean[][] getAdjacencyMatrix(DirectedGraph g,
//			Map<Vertex, Integer> map, List<Vertex> vertices) {
//		for (Vertex v : g.getVertices()) {
//			map.put(v, vertices.size());
//			vertices.add(v);
//		}
//
//		boolean[][] m = new boolean[vertices.size()][vertices.size()];
//
//		for (DirectedEdge e : g.getEdges()) {
//			int src = map.get(e.getSource());
//			int tgt = map.get(e.getTarget());
//			m[src][tgt] = true;
//		}
//
//		return m;
//	}
//
//	public static DirectedGraph getDirectedGraph(boolean[][] m,
//			List<Vertex> vertices) {
//		DirectedGraph g = new DirectedGraph();
//		for (int i = 0; i < m.length; i++) {
//			Vertex src = vertices.get(i);
//			for (int j = 0; j < m.length; j++)
//				if (m[i][j]) {
//					Vertex tgt = vertices.get(j);
//					g.addEdge(src, tgt);
//				}
//		}
//		return g;
//	}

}
