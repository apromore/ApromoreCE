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

import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.*;

public class AdjacencyList {

    private Map<Node, ArrayList<Edge>> adjacencies = new UnifiedMap<Node, ArrayList<Edge>>();

    public void addEdge(Node source, Node target, int weight){
        ArrayList<Edge> list;
        if(!adjacencies.containsKey(source)){
            list = new ArrayList<Edge>();
            adjacencies.put(source, list);
        }else{
            list = adjacencies.get(source);
        }
        list.add(new Edge(source, target, weight));
    }

    public ArrayList<Edge> getAdjacent(Node source){
        return adjacencies.get(source);
    }

    public void reverseEdge(Edge e){
        adjacencies.get(e.from).remove(e);
        addEdge(e.to, e.from, e.weight);
    }

    public void reverseGraph(){
        adjacencies = getReversedList().adjacencies;
    }

    public AdjacencyList getReversedList(){
        AdjacencyList newlist = new AdjacencyList();
        for(ArrayList<Edge> edges : adjacencies.values()){
            for(Edge e : edges){
                newlist.addEdge(e.to, e.from, e.weight);
            }
        }
        return newlist;
    }

    public Set<Node> getSourceNodeSet(){
        return adjacencies.keySet();
    }

    public Collection<Edge> getAllEdges(){
        ArrayList<Edge> edges = new ArrayList<Edge>();
        for(List<Edge> e : adjacencies.values()){
            edges.addAll(e);
        }
        return edges;
    }
}