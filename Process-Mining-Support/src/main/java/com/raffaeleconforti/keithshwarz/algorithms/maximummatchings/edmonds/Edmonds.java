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

package com.raffaeleconforti.keithshwarz.algorithms.maximummatchings.edmonds;

/**
 * Created by conforti on 26/11/14.
 */

import java.util.ArrayList;
import java.util.Iterator;

public class Edmonds {

    private ArrayList<Node> cycle;

    public AdjacencyList getMinBranching(Node root, AdjacencyList list){
        AdjacencyList reverse = list.getReversedList();
        // remove all edges entering the root
        if(reverse.getAdjacent(root) != null){
            reverse.getAdjacent(root).clear();
        }
        AdjacencyList outEdges = new AdjacencyList();
        // for each node, select the edge entering it with smallest weight
        for(Node n : reverse.getSourceNodeSet()){
            ArrayList<Edge> inEdges = reverse.getAdjacent(n);
            if(inEdges.isEmpty()) continue;
            Edge min = inEdges.get(0);
            for(Edge e : inEdges){
                if(e.weight < min.weight){
                    min = e;
                }
            }
            outEdges.addEdge(min.to, min.from, min.weight);
        }

        // detect cycles
        ArrayList<ArrayList<Node>> cycles = new ArrayList<ArrayList<Node>>();
        cycle = new ArrayList<Node>();
        getCycle(root, outEdges);
        cycles.add(cycle);
        for(Node n : outEdges.getSourceNodeSet()){
            if(!n.visited){
                cycle = new ArrayList<Node>();
                getCycle(n, outEdges);
                cycles.add(cycle);
            }
        }

        // for each cycle formed, modify the path to merge it into another part of the graph
        AdjacencyList outEdgesReverse = outEdges.getReversedList();

        for(ArrayList<Node> x : cycles){
            if(x.contains(root)) continue;
            mergeCycles(x, list, reverse, outEdges, outEdgesReverse);
        }
        return outEdges;
    }

    private void mergeCycles(ArrayList<Node> cycle, AdjacencyList list, AdjacencyList reverse, AdjacencyList outEdges, AdjacencyList outEdgesReverse){
        ArrayList<Edge> cycleAllInEdges = new ArrayList<Edge>();
        Edge minInternalEdge = null;
        // find the minimum internal edge weight
        for(Node n : cycle){
            for(Edge e : reverse.getAdjacent(n)){
                if(cycle.contains(e.to)){
                    if(minInternalEdge == null || minInternalEdge.weight > e.weight){
                        minInternalEdge = e;
                    }
                }else{
                    cycleAllInEdges.add(e);
                }
            }
        }
        // find the incoming edge with minimum modified cost
        Edge minExternalEdge = null;
        int minModifiedWeight = 0;
        for(Edge e : cycleAllInEdges){
            int w = e.weight - (outEdgesReverse.getAdjacent(e.from).get(0).weight - minInternalEdge.weight);
            if(minExternalEdge == null || minModifiedWeight > w){
                minExternalEdge = e;
                minModifiedWeight = w;
            }
        }
        // add the incoming edge and remove the inner-circuit incoming edge
        Edge removing = outEdgesReverse.getAdjacent(minExternalEdge.from).get(0);
        outEdgesReverse.getAdjacent(minExternalEdge.from).clear();
        outEdgesReverse.addEdge(minExternalEdge.to, minExternalEdge.from, minExternalEdge.weight);
        ArrayList<Edge> adj = outEdges.getAdjacent(removing.to);
        for(Iterator<Edge> i = adj.iterator(); i.hasNext(); ){
            if(i.next().to == removing.from){
                i.remove();
                break;
            }
        }
        outEdges.addEdge(minExternalEdge.to, minExternalEdge.from, minExternalEdge.weight);
    }

    private void getCycle(Node n, AdjacencyList outEdges){
        n.visited = true;
        cycle.add(n);
        if(outEdges.getAdjacent(n) == null) return;
        for(Edge e : outEdges.getAdjacent(n)){
            if(!e.to.visited){
                getCycle(e.to, outEdges);
            }
        }
    }
}
