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

package com.raffaeleconforti.bpmn.util;

import de.vogella.algorithms.dijkstra.engine.DijkstraAlgorithm;
import de.vogella.algorithms.dijkstra.model.Edge;
import de.vogella.algorithms.dijkstra.model.Graph;
import de.vogella.algorithms.dijkstra.model.Vertex;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;

import java.util.*;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 16/02/15.
 */
public class BPMNAnalizer {

    private XConceptExtension xce = null;

    public BPMNAnalizer(XConceptExtension xce) {
        this.xce = xce;
    }

    public String extractActivityLabel(Activity activity) {
        String label = activity.getLabel();
        if (label.contains("+")) {
            label = label.substring(0, label.indexOf("+"));
        }
        return label;
    }

    public int findPositionOfEventOfSubProcess(BPMNDiagram process, XTrace trace, int startPos) {

        if (startPos == -1) startPos = 0;

        for (int i = startPos; i < trace.size(); i++) {
            XEvent event = trace.get(i);
            String eventName = xce.extractName(event);

            for (Activity activity : process.getActivities()) {
                String label = extractActivityLabel(activity);

                if (label.equals(eventName)) {
                    return i;
                }

            }

        }

        return -1;

    }

    public int findPositionOfEventOfActivity(Activity activity, XTrace trace, int startPos) {

        if (startPos == -1) startPos = 0;

        for (int i = startPos; i < trace.size(); i++) {
            XEvent event = trace.get(i);
            String eventName = xce.extractName(event);

            String label = extractActivityLabel(activity);

            if (label.equals(eventName)) {
                return i;
            }

        }

        return -1;

    }

    public int findPositionOfEventOfActivity(String activity, XTrace trace, int startPos) {

        if (startPos == -1) startPos = 0;

        for (int i = startPos; i < trace.size(); i++) {
            XEvent event = trace.get(i);
            String eventName = xce.extractName(event);

            if (activity.equals(eventName)) {
                return i;
            }

        }

        return -1;

    }

    public Set<Activity> findLastActivities(BPMNDiagram diagram) {
        Set<Activity> result = new UnifiedSet<>();
        Deque<BPMNNode> endPoints = new ArrayDeque<>();
        List<BPMNNode> visistedEndPoints = new ArrayList<>();

        for (Event e : diagram.getEvents()) {
            if (e.getEventType().equals(Event.EventType.END)) {
                endPoints.add(e);
            }
        }

        BPMNNode target;
        while (endPoints.size() > 0) {
            target = endPoints.removeFirst();

            for (Flow f : diagram.getFlows()) {
                if (f.getTarget().equals(target)) {
                    if (f.getSource() instanceof Activity) {
                        result.add((Activity) f.getSource());
                    } else {
                        if (!visistedEndPoints.contains(f.getSource())) {
                            endPoints.add(f.getSource());
                            visistedEndPoints.add(f.getSource());
                        }
                    }
                }
            }
        }

        return result;
    }

    public Set<Activity> findLastActivitiesExcludeANDGateway(BPMNDiagram diagram) {
        Set<Activity> result = new UnifiedSet<>();
        Deque<BPMNNode> endPoints = new ArrayDeque<>();
        List<BPMNNode> visistedEndPoints = new ArrayList<>();

        for (Event e : diagram.getEvents()) {
            if (e.getEventType().equals(Event.EventType.END)) {
                endPoints.add(e);
            }
        }

        BPMNNode target;
        while (endPoints.size() > 0) {
            target = endPoints.removeFirst();

            for (Flow f : diagram.getFlows()) {
                if (f.getTarget().equals(target)) {
                    if (f.getSource() instanceof Activity) {
                        result.add((Activity) f.getSource());
                    } else {
                        if (!visistedEndPoints.contains(f.getSource()) && (f.getSource() instanceof Gateway && !((Gateway) f.getSource()).getGatewayType().equals(Gateway.GatewayType.PARALLEL))) {
                            endPoints.add(f.getSource());
                            visistedEndPoints.add(f.getSource());
                        }
                    }
                }
            }
        }

        return result;
    }

    public boolean sameFlowWithSkipActivity(List<Flow> flows1, List<Flow> flows2) {
        if(flows1.size() >= flows2.size()) {
            Set<BPMNNode> flow1Nodes = new UnifiedSet<>();
            for(Flow flow : flows1) {
                flow1Nodes.add(flow.getSource());
                flow1Nodes.add(flow.getTarget());
            }

            Set<BPMNNode> flow2Nodes = new UnifiedSet<>();
            for(Flow flow : flows2) {
                flow2Nodes.add(flow.getSource());
                flow2Nodes.add(flow.getTarget());
            }

            return flow1Nodes.containsAll(flow2Nodes);
        }else {
            return sameFlowWithSkipActivity(flows2, flows1);
        }
    }

    public boolean isWithoutExit(BPMNDiagram diagram, Gateway gateway) {
        List<BPMNNode> in = new ArrayList<>();
        List<BPMNNode> out = new ArrayList<>();
        for(Flow flow : diagram.getFlows()) {
            if(flow.getTarget().equals(gateway)) {
                in.add(flow.getSource());
            }
            if(flow.getSource().equals(gateway)) {
                out.add(flow.getTarget());
            }
        }
        return in.containsAll(out);
    }

    public List<Flow> discoverPath(BPMNDiagram diagram, BPMNNode source, BPMNNode target, List<Flow> excludedFlow) {
        Set<BPMNNode> visited = new UnifiedSet<>();
        Deque<BPMNNode> toVisit = new ArrayDeque<>();
        List<Flow> paths = new ArrayList<>();

        toVisit.add(source);
        visited.add(source);
        while (toVisit.size() > 0) {
            BPMNNode node = toVisit.removeFirst();
            for (Flow f : diagram.getFlows()) {
                if (f.getSource().equals(node)) {
                    if (!visited.contains(f.getTarget())) {
                        visited.add(f.getTarget());
                        toVisit.add(f.getTarget());
                    }
                }
            }
        }

        ArrayList<Vertex<BPMNNode>> nodes = new ArrayList<>(visited.size());
        ArrayList<Edge> edges = new ArrayList<>();

        Map<BPMNNode, Integer> map = new UnifiedMap<>(visited.size());

        int pos = 0;
        int start = -1;
        int end = -1;
        Vertex<BPMNNode> v;
        for (BPMNNode node : visited) {
            v = new Vertex(node.getLabel() + pos, node.getLabel(), node);
            nodes.add(v);
            map.put(node, pos);

            if (node.equals(source)) {
                start = pos;
            }
            if (node.equals(target)) {
                end = pos;
            }

            pos++;
        }

        pos = 0;
        Edge e;
        for (Flow f : diagram.getFlows()) {
            if (excludedFlow == null || !excludedFlow.contains(f)) {
                if (visited.contains(f.getSource()) && visited.contains(f.getTarget())) {
                    e = new Edge("Edge_" + pos, nodes.get(map.get(f.getSource())), nodes.get(map.get(f.getTarget())), 0);
                    edges.add(e);
                    pos++;
                }
            }
        }

        Graph graph = new Graph(nodes, edges);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(nodes.get(start));

        List<Vertex> path = null;
        if (end > -1) path = dijkstra.getPath(nodes.get(end));

        BPMNNode s = source;
        BPMNNode t;
        if (path != null) {
            for (Vertex aPath : path) {
                v = aPath;
                t = v.getObject();
                for (Flow f : diagram.getFlows()) {
                    if (f.getSource().equals(s) && f.getTarget().equals(t)) {
                        paths.add(f);
                        s = t;
                        break;
                    }
                }
            }
        }

        return paths;
    }

    public List<Event> discoverEndEvents(BPMNDiagram diagram) {
        List<Event> ends = new ArrayList<>();
        for (Event e : diagram.getEvents()) {
            if(e.getEventType().equals(Event.EventType.END)) {
                ends.add(e);
            }
        }
        return ends;
    }

    public List<Event> discoverStartEvents(BPMNDiagram diagram) {
        List<Event> starts = new ArrayList<>();
        for (Event e : diagram.getEvents()) {
            if(e.getEventType().equals(Event.EventType.START)) {
                starts.add(e);
            }
        }
        return starts;
    }

    public List<Flow> discoverPathToEnd(BPMNDiagram diagram, BPMNNode source) {
        Set<BPMNNode> visited = new UnifiedSet<>();
        Deque<BPMNNode> toVisit = new ArrayDeque<>();
        List<Flow> paths = new ArrayList<>();

        int existEndEvent = 0;

        toVisit.add(source);
        visited.add(source);
        while (toVisit.size() > 0) {
            BPMNNode node = toVisit.removeFirst();
            for (Flow f : diagram.getFlows()) {
                if (f.getSource().equals(node) && !(f.getTarget() instanceof Activity)) {
                    if (!visited.contains(f.getTarget())) {
                        visited.add(f.getTarget());
                        toVisit.add(f.getTarget());
                        if (f.getTarget() instanceof Event && ((Event) f.getTarget()).getEventType().equals(Event.EventType.END)) {
                            existEndEvent++;
                        }
                    }
                }
            }
        }

        if (existEndEvent > 0) {
            ArrayList<Vertex<BPMNNode>> nodes = new ArrayList<>(visited.size());
            ArrayList<Edge<BPMNNode>> edges = new ArrayList<>();

            Map<BPMNNode, Integer> map = new UnifiedMap<>(visited.size());

            int pos = 0;
            int start = -1;
            int[] end = new int[existEndEvent];
            int endCount = 0;
            Vertex<BPMNNode> v;
            for (BPMNNode node : visited) {
                v = new Vertex(node.getLabel() + pos, node.getLabel(), node);
                nodes.add(v);
                map.put(node, pos);

                if (node.equals(source)) {
                    start = pos;
                }
                if (node instanceof Event && ((Event) node).getEventType().equals(Event.EventType.END)) {
                    end[endCount] = pos;
                    endCount++;
                }

                pos++;
            }

            pos = 0;
            Edge e;
            for (Flow f : diagram.getFlows()) {
                if (visited.contains(f.getSource()) && visited.contains(f.getTarget())) {
                    e = new Edge("Edge_" + pos, nodes.get(map.get(f.getSource())), nodes.get(map.get(f.getTarget())), 0);
                    edges.add(e);
                    pos++;
                }
            }

            Graph<BPMNNode> graph = new Graph(nodes, edges);
            DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
            dijkstra.execute(nodes.get(start));

            List<Vertex> best = null;
            for (int i : end) {
                List<Vertex> path = dijkstra.getPath(nodes.get(i));
                if (best == null || best.size() > path.size()) {
                    best = path;
                }
            }

            BPMNNode s = source;
            BPMNNode t;
            if (best != null) {
                for (Vertex aBest : best) {
                    v = aBest;
                    t = v.getObject();
                    for (Flow f : diagram.getFlows()) {
                        if (f.getSource().equals(s) && f.getTarget().equals(t)) {
                            paths.add(f);
                            s = t;
                            break;
                        }
                    }
                }
            }
        }

        return paths;

    }

    public Set<Activity> findPreviousActivities(BPMNDiagram diagram, Activity activity) {
        Deque<BPMNNode> nodes = new ArrayDeque<>();
        Set<BPMNNode> nodesVisited = new UnifiedSet<>();
        Set<Activity> activities = new UnifiedSet<>();

        for (Flow f : diagram.getFlows()) {
            if (f.getTarget().equals(activity)) {
                if (f.getSource() instanceof Activity) {
                    activities.add((Activity) f.getSource());
                } else {
                    if (!nodesVisited.contains(f.getSource())) {
                        nodes.add(f.getSource());
                        nodesVisited.add(f.getSource());
                    }
                }
            }
        }

        while (nodes.size() > 0) {
            BPMNNode node = nodes.removeFirst();
            for (Flow f : diagram.getFlows()) {
                if (f.getTarget().equals(node)) {
                    if (f.getSource() instanceof Activity) {
                        activities.add((Activity) f.getSource());
                    } else {
                        if (!nodesVisited.contains(f.getSource())) {
                            nodes.add(f.getSource());
                            nodesVisited.add(f.getSource());
                        }
                    }
                }
            }
        }

        return activities;

    }

    public Set<Activity> findFirstActivities(BPMNDiagram diagram) {
        Set<Activity> activities = new UnifiedSet<>();
        Deque<BPMNNode> nodes = new ArrayDeque<>();
        Set<BPMNNode> nodesVisited = new UnifiedSet<>();

        for (Flow f : diagram.getFlows()) {
            if (f.getSource() instanceof Event && ((Event) f.getSource()).getEventType().equals(Event.EventType.START)) {
                if (f.getTarget() instanceof Activity) {
                    activities.add((Activity) f.getTarget());
                } else {
                    if (!nodesVisited.contains(f.getTarget())) {
                        nodes.add(f.getTarget());
                        nodesVisited.add(f.getTarget());
                    }
                }
            }
        }

        while (nodes.size() > 0) {
            BPMNNode node = nodes.removeFirst();
            for (Flow f : diagram.getFlows()) {
                if (f.getSource().equals(node)) {
                    if (f.getTarget() instanceof Activity) {
                        activities.add((Activity) f.getTarget());
                    } else {
                        if (!nodesVisited.contains(f.getTarget())) {
                            nodes.add(f.getTarget());
                            nodesVisited.add(f.getTarget());
                        }
                    }
                }
            }
        }

        return activities;
    }
}

