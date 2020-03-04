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

package com.raffaeleconforti.automaton;

import com.raffaeleconforti.keithshwarz.algorithms.dijkstra.Dijkstra;
import com.raffaeleconforti.keithshwarz.datastructure.graph.directedgraph.DirectedGraph;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;

import java.util.Map;
import java.util.Set;

/**
 * Created by conforti on 14/02/15.
 */
public class Automaton<T> implements Cloneable{

    private Set<Node<T>> start = null;
    private Set<Node<T>> end = null;

    private Map<Node<T>, Double> nodes = new UnifiedMap<Node<T>, Double>();
    private Map<Edge<T>, Double> edges = new UnifiedMap<Edge<T>, Double>();

    private DirectedGraph directedGraph = null;

    public Set<Node<T>> getNodes() {
        return nodes.keySet();
    }

    public Set<Edge<T>> getEdges() {
        return edges.keySet();
    }

    public double getNodeFrequency(Node<T> node) {
        Double val;
        if((val = nodes.get(node)) == null) {
            return 0.0;
        }
        return val;
    }

    public double getEdgeFrequency(Edge<T> edge) {
        return getEdgeFrequency(edge.getSource(), edge.getTarget());
    }

    public double getEdgeFrequency(Node<T> source, Node<T> target) {
        Edge<T> edge = new Edge<T>(source, target);
        Double val;
        if((val = edges.get(edge)) == null) {
            return 0.0;
        }
        return val;
    }

    public void addNode(Node<T> node) {
        Double val;
        if((val = nodes.get(node)) == null) {
            val = 0.0;
        }
        val++;
        nodes.put(node, val);
        node.setFrequency(val);
        if(directedGraph != null) directedGraph.addNode(node);
    }

    public void addEdge(Edge<T> edge) {
        addEdge(edge.getSource(), edge.getTarget());
    }

    public void addEdge(Node<T> source, Node<T> target) {
        Edge<T> edge = new Edge<T>(source, target);
        for(Node<T> n : nodes.keySet()) {
            if(n.equals(source)) {
                source = n;
                break;
            }
        }

        for(Node<T> n : nodes.keySet()) {
            if(n.equals(target)) {
                target = n;
                break;
            }
        }

        Double val;
        if((val = edges.get(edge)) == null) {
            val = 0.0;
        }
        val++;
        edges.put(edge, val);
        if(directedGraph != null) directedGraph.addEdge(source, target, 1);

    }

    public void addNode(Node<T> node, double frequency) {
        nodes.put(node, frequency);
        node.setFrequency(frequency);
        if(directedGraph != null) directedGraph.addNode(node);
    }

    public void addEdge(Edge<T> edge, double frequency) {
        addEdge(edge.getSource(), edge.getTarget(), frequency);
    }

    public void addEdge(Node<T> source, Node<T> target, double frequency) {
        Edge<T> edge = new Edge<T>(source, target);
        for(Node<T> n : nodes.keySet()) {
            if(n.equals(source)) {
                source = n;
                break;
            }
        }

        for(Node<T> n : nodes.keySet()) {
            if(n.equals(target)) {
                target = n;
                break;
            }
        }

        edges.put(edge, frequency);
        if(directedGraph != null) directedGraph.addEdge(source, target, 1);
    }

    public void removeNode(Node<T> node) {
        Double val;
        if((val = nodes.get(node)) == null) {
            return;
        }
        val--;
        if(val <= 0.0) {
            nodes.remove(node);
            return;
        }
        nodes.put(node, val);
    }

    public void removeEdge(Edge<T> edge) {
//        removeEdge(edge.getSource(), edge.getTarget());
        Double val;
        if((val = edges.get(edge)) == null) {
            return;
        }
        val--;
        if(val <= 0.0) {
            for(Node<T> n : nodes.keySet()) {
//                if(n.equals(source)) {
                if(n.equals(edge.getSource())) {
                    break;
                }
            }

            for(Node<T> n : nodes.keySet()) {
//                if(n.equals(target)) {
                if(n.equals(edge.getTarget())) {
                    break;
                }
            }
            edges.remove(edge);
            if(directedGraph != null) directedGraph.removeEdge(edge.getSource(), edge.getTarget());
            return;
        }
        edges.put(edge, val);
//        if(directedGraph != null) directedGraph.removeEdge(source, target);
        if(directedGraph != null) directedGraph.removeEdge(edge.getSource(), edge.getTarget());
    }

    public void removeEdge(Node<T> source, Node<T> target) {
        Edge<T> edge = new Edge<T>(source, target);
        removeEdge(edge);
//        Double val;
//        if((val = edges.get(edge)) == null) {
//            return;
//        }
//        val--;
//        if(val <= 0.0) {
//            for(Node<T> n : nodes.keySet()) {
//                if(n.equals(source)) {
//                    break;
//                }
//            }
//
//            for(Node<T> n : nodes.keySet()) {
//                if(n.equals(target)) {
//                    break;
//                }
//            }
//            edges.remove(edge);
//            return;
//        }
//        edges.put(edge, val);
//        if(directedGraph != null) directedGraph.removeEdge(source, target);
    }

    public Double removeNodeTotal(Node<T> node) {
        return nodes.remove(node);
    }

    public Double removeEdgeTotal(Edge<T> edge) {
        return removeEdgeTotal(edge.getSource(), edge.getTarget());
    }

    public Double removeEdgeTotal(Node<T> source, Node<T> target) {
        Edge<T> edge = new Edge<T>(source, target);
        for(Node<T> n : nodes.keySet()) {
            if(n.equals(source)) {
                source = n;
                break;
            }
        }

        for(Node<T> n : nodes.keySet()) {
            if(n.equals(target)) {
                target = n;
                break;
            }
        }

        if(directedGraph != null) directedGraph.removeEdge(source, target);

        return edges.remove(edge);
    }

    public Set<Node<T>> getAutomatonStart() {
        if(start == null) {
            start = new UnifiedSet<Node<T>>();
            for (Node<T> node : nodes.keySet()) {
                int input = 0;
                for (Edge<T> edge : edges.keySet()) {
                    if (edge.getTarget().equals(node)) {
                        input++;
                        break;
                    }
                }
                if (input == 0) {
                    start.add(node);
                }
            }
        }

        return start;
    }

    public Set<Node<T>> getAutomatonEnd() {
        if(end == null) {
            end = new UnifiedSet<Node<T>>();
            for (Node<T> node : nodes.keySet()) {
                int output = 0;
                for (Edge<T> edge : edges.keySet()) {
                    if (edge.getSource().equals(node)) {
                        output++;
                        break;
                    }
                }
                if (output == 0) {
                    end.add(node);
                }
            }
        }

        return end;
    }

    public boolean reachable(Set<Node<T>> source, Set<Node<T>> target) {
        for(Node<T> node : source) {
            if(!reachable(node, target)) {
                return false;
            }
        }
        return true;
    }

    public boolean reachable(Node<T> source, Set<Node<T>> target) {
        for(Node<T> node : target) {
            if(!reachable(source, node)) {
                return false;
            }
        }
        return true;
    }

    public boolean reachable(Set<Node<T>> source, Node<T> target) {
        for(Node<T> node : source) {
            if(!reachable(node, target)) {
                return false;
            }
        }
        return true;
    }

    public void createDirectedGraph() {
        if(directedGraph == null) {
            directedGraph = new DirectedGraph();

            for (Node<T> node : nodes.keySet()) {
                directedGraph.addNode(node);
            }

            for (Edge<T> edge : edges.keySet()) {
                directedGraph.addEdge(edge.getSource(), edge.getTarget(), 1);
            }
        }
    }

    public DirectedGraph createDirectedGraph(Set<Node<T>> exclusion) {
        DirectedGraph directedGraph = new DirectedGraph();

        for (Node<T> node : nodes.keySet()) {
            if(!exclusion.contains(node)) {
                directedGraph.addNode(node);
            }
        }

        for (Edge<T> edge : edges.keySet()) {
            if(!exclusion.contains(edge.getSource()) && !exclusion.contains(edge.getTarget())) {
                directedGraph.addEdge(edge.getSource(), edge.getTarget(), 1);
            }
        }
        return directedGraph;
    }

    public boolean reachable(Node<T> source, Node<T> target) {
        if(directedGraph == null) {
            createDirectedGraph();
        }

        Map<Node<T>, Double> paths = Dijkstra.shortestPaths(directedGraph, source);
        return (paths.get(target) < Double.POSITIVE_INFINITY);
    }

    public boolean reachable(Node<T> source, Node<T> target, Set<Node<T>> exclusion) {
        DirectedGraph directedGraph = createDirectedGraph(exclusion);

        Map<Node<T>, Double> paths = Dijkstra.shortestPaths(directedGraph, source);
        return (paths.get(target) < Double.POSITIVE_INFINITY);
    }

    public Petrinet getPetrinet() {
        int placeCount = 1;
        int transitionCount = 1;
        Petrinet petrinet = new PetrinetImpl("Petrinet from Automaton");

        Map<Node, Transition> nodeTransitionMap = new UnifiedMap<Node, Transition>();
        Map<Node, Place> nodeStartPlace = new UnifiedMap<Node, Place>();
        Map<Node, Place> nodeEndPlace = new UnifiedMap<Node, Place>();

        for(Node<T> node : nodes.keySet()) {
            Transition t = petrinet.addTransition(node.toString());
            Place p1 = petrinet.addPlace("p" + placeCount++);
            Place p2 = petrinet.addPlace("p" + placeCount++);

            petrinet.addArc(p1, t);
            petrinet.addArc(t, p2);

            nodeTransitionMap.put(node, t);
            nodeStartPlace.put(node, p1);
            nodeEndPlace.put(node, p2);
        }

        for(Edge<T> edge : edges.keySet()) {
            Node<T> source = edge.getSource();
            Node<T> target = edge.getTarget();

            Transition t = petrinet.addTransition("t" + transitionCount++);
            t.setInvisible(true);

            petrinet.addArc(nodeEndPlace.get(source), t);
            petrinet.addArc(t, nodeStartPlace.get(target));
        }

//        int source = 1;
//        int sink = 1;
//        for(Place p : petrinet.getPlaces()) {
//            int input = 0;
//            int output = 0;
//
//            for(PetrinetEdge e : petrinet.getEdges()) {
//                if(e.getTarget().equals(p)) {
//                    input++;
//                }
//                if(e.getSource().equals(p)) {
//                    output++;
//                }
//            }
//            if(input == 0) {
//                p.getAttributeMap().put(AttributeMap.LABEL, "source" + source++);
//            }
//            if(output == 0) {
//                p.getAttributeMap().put(AttributeMap.LABEL, "sink" + sink++);
//            }
//        }
        nodeStartPlace.get(start.toArray()[0]).getAttributeMap().put(AttributeMap.LABEL, "source" + 1);
        nodeEndPlace.get(end.toArray()[0]).getAttributeMap().put(AttributeMap.LABEL, "sink" + 1);



        while (removeUselessTauTransitions(petrinet));

        return petrinet;
    }

    private boolean removeUselessTauTransitions(Petrinet petrinet) {
        Place source = null;
        Place target = null;
        Transition t1 = null;
        Place p1 = null;
        Transition transitionToRemove = null;
        Place placeToRemove  = null;
        boolean order = true;
        for(Transition t : petrinet.getTransitions()) {
            if(t.isInvisible()) {
                int input = 0;
                int output = 0;
                for (PetrinetEdge edge : petrinet.getEdges()) {
                    if (edge.getSource().equals(t)) {
                        output++;
                        target = (Place) edge.getTarget();
                        if (output > 1) {
                            break;
                        }
                    }
                    if (edge.getTarget().equals(t)) {
                        input++;
                        source = (Place) edge.getSource();
                        if (input > 1) {
                            break;
                        }
                    }
                }
                if (input == 1 && output == 1) {
                    if (checkSESE(source, petrinet)) {
                        transitionToRemove = t;
                        placeToRemove = source;
                        t1 = (Transition) getInput(source, petrinet);
                        p1 = target;
                        order = true;
                        break;
                    } else if (checkSESE(target, petrinet)) {
                        transitionToRemove = t;
                        placeToRemove = target;
                        t1 = (Transition) getOutput(target, petrinet);
                        p1 = source;
                        order = false;
                        break;
                    }
                }
            }
        }
        if(transitionToRemove != null) {
            petrinet.removeTransition(transitionToRemove);
            petrinet.removePlace(placeToRemove);
            if(order) petrinet.addArc(t1, p1);
            else petrinet.addArc(p1, t1);
            return true;
        }
        return false;
    }

    private boolean checkSESE(PetrinetNode node, Petrinet petrinet) {
        int input = 0;
        int output = 0;
        for(PetrinetEdge edge : petrinet.getEdges()) {
            if(edge.getSource().equals(node)) {
                output++;
                if(output > 1) {
                    break;
                }
            }
            if(edge.getTarget().equals(node)) {
                input++;
                if(input > 1) {
                    break;
                }
            }
        }
        return (input == 1 && output == 1);
    }

    private PetrinetNode getInput(PetrinetNode node, Petrinet petrinet) {
        for(PetrinetEdge edge : petrinet.getEdges()) {
            if (edge.getTarget().equals(node)) {
                return (PetrinetNode) edge.getSource();
            }
        }
        return null;
    }

    private PetrinetNode getOutput(PetrinetNode node, Petrinet petrinet) {
        for(PetrinetEdge edge : petrinet.getEdges()) {
            if (edge.getSource().equals(node)) {
                return (PetrinetNode) edge.getTarget();
            }
        }
        return null;
    }

    @Override
    public Object clone() {
        Automaton<T> clone = new Automaton<T>();
        clone.nodes = new UnifiedMap<Node<T>, Double>(nodes);
        clone.edges = new UnifiedMap<Edge<T>, Double>(edges);

        if(start != null) clone.start = new UnifiedSet<Node<T>>(start);
        if(end != null) clone.end = new UnifiedSet<Node<T>>(end);
        if(directedGraph != null) clone.directedGraph = directedGraph.clone();
        return clone;
    }

    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<Edge<T>, Double> entry : edges.entrySet()) {
            sb.append(entry.getKey().toString()).append(entry.getValue());
        }
        for(Map.Entry<Node<T>, Double> entry : nodes.entrySet()) {
            sb.append(entry.getKey().toString()).append(entry.getValue());
        }
        return sb.toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Automaton) {
            Automaton<T> a = (Automaton) o;
            if(nodes.size() == a.nodes.size() && edges.size() == a.edges.size()) {
                for(Map.Entry<Node<T>, Double> entry : nodes.entrySet()) {
                    if(a.getNodeFrequency(entry.getKey()) != entry.getValue()) {
                        return false;
                    }
                }

                for(Map.Entry<Edge<T>, Double> entry : edges.entrySet()) {
                    if(a.getEdgeFrequency(entry.getKey()) != entry.getValue()) {
                        return false;
                    }
                }


                for(Map.Entry<Node<T>, Double> entry : a.nodes.entrySet()) {
                    if(getNodeFrequency(entry.getKey()) != entry.getValue()) {
                        return false;
                    }
                }

                for(Map.Entry<Edge<T>, Double> entry : a.edges.entrySet()) {
                    if(getEdgeFrequency(entry.getKey()) != entry.getValue()) {
                        return false;
                    }
                }

                return true;
            }return false;
        }
        return false;
    }

}
