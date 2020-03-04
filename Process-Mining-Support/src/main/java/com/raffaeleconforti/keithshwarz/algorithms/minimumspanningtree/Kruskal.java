/*
 *  Copyright (C) 2018 Raffaele Conforti (www.raffaeleconforti.com)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.raffaeleconforti.keithshwarz.algorithms.minimumspanningtree;

/***************************************************************************
 * File: Kruskal.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * An implementation of Kruskal's algorithm for minimum spanning trees.
 * Kruskal's algorithm works by sorting all of the graph's edges in ascending
 * order of size, then continuously adding them one at a time back into the
 * resulting graph.  It maintains a union-find data structure to prevent
 * edge additions that would add a cycle into the resulting graph.  Using
 * a union-find structure for this gives a runtime of O(|E| lg |V|), which
 * is asymptotically worse than the O(|E| + |V| lg |V|) guarantee of Prim's
 * algorithm.  However, the asymptotically better performance of Prim's
 * algorithm comes at the cost of using the practically slower Fibonacci heap,
 * and so Kruskal's algorithm is often faster in practice.
 *
 * This implementation of Kruskal's algorithm relies on the existence of
 * a UnionFind data structure that is also available from the Archive of
 * Interesting Code.  You can find it at
 *
 *         http://keithschwarz.com/interesting/code/?dir=union-find
 */

import com.raffaeleconforti.keithshwarz.datastructure.disjointset.UnionFind;
import com.raffaeleconforti.keithshwarz.datastructure.graph.undirectedgraph.kruskal.UndirectedGraphKruskal;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.*;

public final class Kruskal {
    /**
     * Given an undirected graph with real-valued edge costs, returns a
     * spanning tree of that graph with minimum weight.
     *
     * @param graph The graph whose MST should be computed.
     * @return An MST of that graph.
     */
    public static <T> UndirectedGraphKruskal<T> mst(UndirectedGraphKruskal<T> graph) {
        /* Build up the graph that will hold the result. */
        UndirectedGraphKruskal<T> result = new UndirectedGraphKruskal<T>();

        /* Edge case - if the input graph has zero or one nodes, we're done. */
        if (graph.size() <= 1)
            return result;

        /* Begin by building up a collections of all the edges of the graph.
         * Because we are given the edges via bidirectional adjacency lists,
         * we need to do some processing for this step.
         */
        List<Edge<T>> edges = getEdges(graph);

        /* Sort the edges in ascending order of size. */
        Collections.sort(edges);

        /* Set up the partition of nodes in a union-find structure. */
        UnionFind<T> unionFind = new UnionFind<T>();
        for (T node : graph)
            unionFind.add(node);

        /* Add each node to the resulting graph. */
        for (T node : graph)
            result.addNode(node);

        /* Count how many edges have been added; when this hits n - 1,
         * we're done.
         */
        int numEdges = 0;

        /* Now, sweep over the edges, adding each edge if its endpoints aren't
         * in the same partition.
         */
        for (Edge<T> edge: edges) {
            /* If the endpoints are connected, skip this edge. */
            if (unionFind.find(edge.start) == unionFind.find(edge.end))
                continue;

            /* Otherwise, add the edge. */
            result.addEdge(edge.start, edge.end, edge.cost);

            /* Link the endpoints together. */
            unionFind.union(edge.start, edge.end);

            /* If we've added enough edges already, we can quit. */
            if (++numEdges == graph.size()) break;
        }

        /* Hand back the generated graph. */
        return result;
    }

    /**
     * Utility function which, given an undirected graph, returns a list of
     * the edges in that graph.
     *
     * @param graph The graph whose edges should be stored.
     * @return A List of the edges in the graph.
     */
    private static <T> List<Edge<T>> getEdges(UndirectedGraphKruskal<T> graph) {
        /* Because the graph is represented as a double-counting adjacency
         * list, we'll maintain the list of edges along with a set of used
         * sources.  We'll add edges to the list as long as the endpoints
         * aren't in the "used sources" list.
         */
        Set<T> used = new UnifiedSet<T>();
        List<Edge<T>> result = new ArrayList<Edge<T>>();

        /* Scan over each node adding edges. */
        for (T node : graph) {
            /* Consider all outgoing nodes, but be sure to check them before
             * adding anything.
             */
            for (Map.Entry<T, Double> entry : graph.edgesFrom(node).entrySet()) {
                /* If we've seen this endpoint, it means that the edge was
                 * added in the opposite direction when we considered that
                 * endpoint.
                 */
                if (used.contains(entry.getKey())) continue;

                /* Otherwise, add the edge. */
                result.add(new Edge<T>(node, entry.getKey(), entry.getValue()));
            }

            /* Mark this node as visited. */
            used.add(node);
        }

        return result;
    }

    /**
     * A utility class storing an edge in the graph.
     *
     */
    private static final class Edge<T> implements Comparable<Edge<T>> {
        public final T start, end;  // The edge's endpoints
        public final double cost;   // The edge's cost

        /* When sorting edges, we need some way to break ties if two edges
         * have the same cost.  This value, the "tiebreaker" is unique for
         * each edge and serves solely to give some way to distinguish
         * between edges.
         */
        public final int tiebreaker;
        public static int nextTiebreaker = 0;

        /**
         * Constructs a new Edge with the given cost.
         *
         * @param start The start point of the edge.
         * @param end The end point of the edge.
         * @param cost The cost of the edge.
         */
        public Edge(T start, T end, double cost) {
            /* Set fields appropriately. */
            this.start = start;
            this.end = end;
            this.cost = cost;

            /* Use the next tiebreaker here. */
            tiebreaker = nextTiebreaker++;
        }

        /**
         * Compares two edges first by their cost, then by their tiebreaker.
         * Because this class is only used internally, we don't need to worry
         * about the other fields.  They aren't relevant for the comparison.
         *
         * @param other The object to compare to.
         * @return How this object compares to the other.
         */
        public int compareTo(Edge<T> other) {
            /* Check how the costs compare. */
            if (cost < other.cost) return -1;
            if (cost > other.cost) return +1;

            /* If they have equal costs, use the tiebreaker to make the
             * decision.
             */
            return tiebreaker - other.tiebreaker;
        }
    }
}
