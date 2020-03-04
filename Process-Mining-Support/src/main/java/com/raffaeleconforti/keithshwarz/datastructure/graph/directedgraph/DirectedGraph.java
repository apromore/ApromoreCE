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

package com.raffaeleconforti.keithshwarz.datastructure.graph.directedgraph;

/*****************************************************************************
 * File: DirectedGraph.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * A class representing a directed graph where each edge has an associated
 * real-valued length.  Internally, the class is represented by an adjacency
 * list.
 */

import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public final class DirectedGraph<T> implements Iterable<T>, Cloneable {
    /* A map from nodes in the graph to sets of outgoing edges.  Each
     * set of edges is represented by a map from edges to doubles.
     */
    private final Map<T, Map<T, Double>> mGraph = new UnifiedMap<T, Map<T, Double>>();

    @Override
    public  DirectedGraph<T> clone() {
        DirectedGraph<T> clone = new DirectedGraph<T>();

        for(Map.Entry<T, Map<T, Double>> entry : mGraph.entrySet()) {
            clone.mGraph.put(entry.getKey(), new UnifiedMap<T, Double>(entry.getValue()));
        }

        return clone;
    }

    /**
     * Adds a new node to the graph.  If the node already exists, this
     * function is a no-op.
     *
     * @param node The node to add.
     * @return Whether or not the node was added.
     */
    public boolean addNode(T node) {
        /* If the node already exists, don't do anything. */
        if (mGraph.containsKey(node))
            return false;

        /* Otherwise, add the node with an empty set of outgoing edges. */
        mGraph.put(node, new UnifiedMap<T, Double>());
        return true;
    }

    /**
     * Given a start node, destination, and length, adds an arc from the
     * start node to the destination of the length.  If an arc already
     * existed, the length is updated to the specified value.  If either
     * endpoint does not exist in the graph, throws a NoSuchElementException.
     *
     * @param start The start node.
     * @param dest The destination node.
     * @param length The length of the edge.
     * @throws NoSuchElementException If either the start or destination nodes
     *                                do not exist.
     */
    public void addEdge(T start, T dest, double length) {
        /* Confirm both endpoints exist. */
        if (!mGraph.containsKey(start) || !mGraph.containsKey(dest))
            throw new NoSuchElementException("Both nodes must be in the graph.");

        /* Add the edge. */
        mGraph.get(start).put(dest, length);
    }

    /**
     * Removes the edge from start to dest from the graph.  If the edge does
     * not exist, this operation is a no-op.  If either endpoint does not
     * exist, this throws a NoSuchElementException.
     *
     * @param start The start node.
     * @param dest The destination node.
     * @throws NoSuchElementException If either node is not in the graph.
     */
    public void removeEdge(T start, T dest) {
        /* Confirm both endpoints exist. */
        if (!mGraph.containsKey(start) || !mGraph.containsKey(dest))
            throw new NoSuchElementException("Both nodes must be in the graph.");

        mGraph.get(start).remove(dest);
    }

    /**
     * Given a node in the graph, returns an immutable view of the edges
     * leaving that node, as a map from endpoints to costs.
     *
     * @param node The node whose edges should be queried.
     * @return An immutable view of the edges leaving that node.
     * @throws NoSuchElementException If the node does not exist.
     */
    public Map<T, Double> edgesFrom(T node) {
        /* Check that the node exists. */
        Map<T, Double> arcs = mGraph.get(node);
        if (arcs == null)
            throw new NoSuchElementException("Source node does not exist.");

        return Collections.unmodifiableMap(arcs);
    }

    /**
     * Returns an iterator that can traverse the nodes in the graph.
     *
     * @return An iterator that traverses the nodes in the graph.
     */
    public Iterator<T> iterator() {
        return mGraph.keySet().iterator();
    }
}
