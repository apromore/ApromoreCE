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

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 16/02/2016.
 */
/**************************************************************************
 * File: Prim.java
 * Author: Keith Schwarz (htiek@cs.stanford.edu)
 *
 * An implementation of Prim's minimum spanning tree algorithm.  The algorithm
 * takes as input a weighted, undirected graph and returns a new graph that
 * is a tree on the original nodes of minimum weight.
 *
 * Prim's algorithm is in many ways similar to Dijkstra's algorithm.  Starting
 * at an arbitrary node in the graph, we grow the spanning tree outward one
 * edge at a time by adding the cheapest outgoing edge from the spanned nodes
 * to a node not in the spanning tree.  The key difference between Dijkstra's
 * algorithm and Prim's algorithm is that Dijkstra's algorithm creates a
 * shortest-path tree from the source node, while Prim's algorithm builds an
 * MST from the source node.
 *
 * The main advantage of Prim's algorithm is that it can be made to run in
 * O(|E| + |V| lg |V|) using some clever optimizations and a Fibonacci heap.
 * The main algorithm is as follows.  First, as in Dijkstra's algorithm,
 * create a Fibonacci heap and assign each node infinite priority.  Next,
 * pick some arbitrary node to use as the source node, set its priority to
 * zero, and then decrease the key of each connected node to the cost of the
 * edge connecting that node to the source node.  Now, we repeat the following
 * procedure until a tree is found:
 *
 * 1. Dequeue some node from the priority queue.  This node will be the node
 *    connected to the existing MST by the least-cost edge.
 * 2. Scan over the edges leaving this node and find the minimum-cost node
 *    connecting it to the existing MST nodes.  This is the node that caused
 *    the node to have its priority.
 * 3. Add this edge to the MST.
 *
 * The runtime of this algorithm can be shown to be O(|E| + |V| lg |V|) using
 * a Fibonacci heap as follows.  First, we do O(|V|) insertions into the heap,
 * which takes O(|V|) time.  We then do O(|V|) dequeues (since we only want
 * a total of |V| - 1 edges).  These dequeues take a total of O(|V| lg |V)
 * time, though any one dequeue might take much more than that.  Finally, on
 * each dequeue, we scan all of the outgoing edges from the dequeued node.
 * Since we never consider the same node twice, the total number of edges
 * visited by all iterations of this step must be twice the number of edges
 * in the graph, since each edge will be visited once from each endpoint.
 * This contributes the final O(|E|) term to the runtime, for a net of an
 * elegant O(|E| + |V| lg |V|).
 *
 * This implementation relies on the existence of a FibonacciHeap class, also
 * from the Archive of Interesting Code.  You can find it online at
 *
 *         http://keithschwarz.com/interesting/code/?dir=fibonacci-heap
 */

import com.raffaeleconforti.keithshwarz.datastructure.fibonacciheap.FibonacciHeap;
import com.raffaeleconforti.keithshwarz.datastructure.graph.undirectedgraph.kruskal.UndirectedGraphKruskal;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.Map;

public final class Prim {
    /**
     * Given a connected undirected graph with real-valued edge costs,
     * returns an MST of that graph.
     *
     * @param graph The graph from which to compute an MST.
     * @return A spanning tree of the graph with minimum total weight.
     */
    public static <T> UndirectedGraphKruskal<T> mst(UndirectedGraphKruskal<T> graph) {
        /* The Fibonacci heap we'll use to select nodes efficiently. */
        FibonacciHeap<T> pq = new FibonacciHeap<T>();

        /* This Fibonacci heap hands back internal handles to the nodes it
         * stores.  This map will associate each node with its entry in the
         * Fibonacci heap.
         */
        Map<T, FibonacciHeap.Entry<T>> entries = new UnifiedMap<T, FibonacciHeap.Entry<T>>();

        /* The graph which will hold the resulting MST. */
        UndirectedGraphKruskal<T> result = new UndirectedGraphKruskal<T>();

        /* As an edge case, if the graph is empty, just hand back the empty
         * graph.
         */
        if (graph.isEmpty())
            return result;

        /* Pick an arbitrary starting node. */
        T startNode = graph.iterator().next();

        /* Add it as a node in the graph.  During this process, we'll use
         * whether a node is in the result graph or not as a sentinel of
         * whether it's already been picked.
         */
        result.addNode(startNode);

        /* Begin by adding all outgoing edges of this start node to the
         * Fibonacci heap.
         */
        addOutgoingEdges(startNode, graph, pq, result, entries);

        /* Now, until we have added |V| - 1 edges to the graph, continously
         * pick a node and determine which edge to add.
         */
        for (int i = 0; i < graph.size() - 1; ++i) {
            /* Grab the cheapest node we can add. */
            T toAdd = pq.dequeueMin().getValue();

            /* Determine which edge we should pick to add to the MST.  We'll
             * do this by getting the endpoint of the edge leaving the current
             * node that's of minimum cost and that enters the visited edges.
             */
            T endpoint = minCostEndpoint(toAdd, graph, result);

            /* Add this edge to the graph. */
            result.addNode(toAdd);
            result.addEdge(toAdd, endpoint, graph.edgeCost(toAdd, endpoint));

            /* Explore outward from this node. */
            addOutgoingEdges(toAdd, graph, pq, result, entries);
        }

        /* Hand back the generated graph. */
        return result;
    }

    /**
     * Given a node in the source graph and a set of nodes that we've visited
     * so far, returns the minimum-cost edge from that node to some node that
     * has been visited before.
     *
     * @param node The node that has not been considered yet.
     * @param graph The original graph whose MST is being computed.
     * @param result The resulting graph, used to check what has been visited
     *               so far.
     */
    private static <T> T minCostEndpoint(T node, UndirectedGraphKruskal<T> graph,
                                         UndirectedGraphKruskal<T> result) {
        /* Track the best endpoint so far and its cost, initially null and
         * +infinity.
         */
        T endpoint = null;
        double leastCost = Double.POSITIVE_INFINITY;

        /* Scan each node, checking whether it's a candidate. */
        for (Map.Entry<T, Double> entry : graph.edgesFrom(node).entrySet()) {
            /* If the endpoint isn't in the nodes constructed so far, don't
             * consider it.
             */
            if (!result.containsNode(entry.getKey())) continue;

            /* If the edge costs more than what we know, skip it. */
            if (entry.getValue() >= leastCost) continue;

            /* Otherwise, update our guess to be this node. */
            endpoint = entry.getKey();
            leastCost = entry.getValue();
        }

        /* Hand back the result.  We're guaranteed to have found something,
         * since otherwise we couldn't have dequeued this node.
         */
        return endpoint;
    }

    /**
     * Given a node in the graph, updates the priorities of adjacent nodes to
     * take these edges into account.  Due to some optimizations we make, this
     * step takes in several parameters beyond what might seem initially
     * required.  They are explained in the param section below.
     *
     * @param node The node to explore outward from.
     * @param graph The graph whose MST is being computed, used so we can
     *              get the edges to consider.
     * @param pq The Fibonacci heap holding each endpoint.
     * @param result The result graph.  We need this information so that we
     *               don't try to update information on a node that has
     *               already been considered and thus isn't in the queue.
     * @param entries A map from nodes to their corresponding heap entries.
     *                We need this so we can call decreaseKey on the correct
     *                elements.
     */
    private static <T> void addOutgoingEdges(T node, UndirectedGraphKruskal<T> graph,
                                             FibonacciHeap<T> pq,
                                             UndirectedGraphKruskal<T> result,
                                             Map<T, FibonacciHeap.Entry<T>> entries ) {
        /* Start off by scanning over all edges emanating from our node. */
        for (Map.Entry<T, Double> arc : graph.edgesFrom(node).entrySet()) {
            /* Given this arc, there are four possibilities.
             *
             * 1. This endpoint has already been added to the graph.  If so,
             *    we ignore the edge since it would form a cycle.
             * 2. This endpoint is not in the graph and has never been in
             *    the heap.  Then we add it to the heap.
             * 3. This endpoint is in the graph, but this is a better edge.
             *    Then we use decreaseKey to update its priority.
             * 4. This endpoint is in the graph, but there is a better edge
             *    to it.  In that case, we similarly ignore it.
             */
            if (result.containsNode(arc.getKey())) continue; // Case 1

            if (!entries.containsKey(arc.getKey())) { // Case 2
                entries.put(arc.getKey(), pq.enqueue(arc.getKey(), arc.getValue()));
            }
            else if (entries.get(arc.getKey()).getPriority() > arc.getValue()) { // Case 3
                pq.decreaseKey(entries.get(arc.getKey()), arc.getValue());
            }

            // Case 4 handled implicitly by doing nothing.
        }
    }
}