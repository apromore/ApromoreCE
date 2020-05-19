/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * Copyright (C) 2016 Adriano Augusto
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

package org.apromore.service.metrics.impl;

import au.edu.qut.bpmn.metrics.ComplexityCalculator;

import de.hpi.bpt.graph.DirectedEdge;
import de.hpi.bpt.graph.DirectedGraph;
import de.hpi.bpt.graph.abs.IDirectedGraph;
import de.hpi.bpt.graph.algo.rpst.RPST;
import de.hpi.bpt.graph.algo.rpst.RPSTNode;
import de.hpi.bpt.graph.algo.tctree.TCType;
import de.hpi.bpt.hypergraph.abs.IVertex;
import de.hpi.bpt.hypergraph.abs.Vertex;

import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.NodeTypeEnum;
import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.service.metrics.MetricsService;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.*;

import static org.apromore.graph.canonical.NodeTypeEnum.*;

/**
 * Created by Adriano Augusto on 18/04/2016.
 */
@Service
public class MetricsServiceImpl extends DefaultParameterAwarePlugin implements MetricsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsServiceImpl.class);
    private Canonical diagram;
    private String bonds;
    private String rigids;

    @Override
    public Map<String, String> computeMetrics(BPMNDiagram model, boolean size, boolean cfc, boolean acd,
                                              boolean mcd, boolean cnc, boolean density,
                                              boolean structuredness, boolean separability,
                                              boolean duplicates)
    {
        ComplexityCalculator cc = new ComplexityCalculator();
        return cc.computeComplexity( model, size, cfc, acd, mcd, cnc, density,
                                     structuredness, separability, duplicates);
    }

    @Override
    public Map<String, String> computeCanonicalMetrics(Canonical diagram) {
        Map<String, String> result = new HashMap<>();

        this.diagram = diagram;

        result.put("Size", computeSize());
        result.put("CFC", computeCFC());
        result.put("ACD", computeACD());
        result.put("MCD", computeMCD());
        result.put("CNC", computeCNC());
        result.put("Density",  computeDensity());
        result.put("Structuredness",  computeStructuredness());
        result.put("Bonds", this.bonds);
        result.put("Rigids", this.rigids);
        result.put("Separability", computeSeparability());
        result.put("Duplicates", computeDuplicates());

        this.diagram = null;

        return result;
    }

    @Override
    public Map<String, String> computeMetrics(XLog log) {
        Map<String, String> result = new HashMap<>();

        Set<String> uniqueTraces = new HashSet<>();
        Map<String, Integer> labels = new HashMap<>();
        Set<String> resources = new HashSet<>();
        List<Long> durations = new ArrayList<>(log.size());

        int events = 0;

        Date start = null;
        Date end = null;

        XConceptExtension xce = XConceptExtension.instance();
        XTimeExtension xte = XTimeExtension.instance();
        XOrganizationalExtension xor = XOrganizationalExtension.instance();

        for(XTrace trace : log) {
            StringBuilder traceBuilder = new StringBuilder();
            durations.add(xte.extractTimestamp(trace.get(trace.size() - 1)).getTime() - xte.extractTimestamp(trace.get(0)).getTime());
            for(XEvent event : trace) {
                String label = xce.extractName(event);
                if(!labels.containsKey(label)) labels.put(label, labels.size());
                traceBuilder.append(labels.get(label) + ",");

                resources.add(xor.extractResource(event));
                Date d = xte.extractTimestamp(event);
                if(start == null || d.before(start)) start = d;
                if(end == null || d.after(end)) end = d;
                events++;
            }
            uniqueTraces.add(traceBuilder.toString());
        }

        Long[] dur = durations.toArray(new Long[durations.size()]);
        Arrays.sort(dur);

        double shortest = Double.MAX_VALUE;
        double longhest = 0;
        double median = dur[dur.length / 2] / 3600000;
        double mean = 0;
        for(Long l : dur) {
            mean += l / 3600000;
            if(shortest > l) shortest = l;
            if(longhest < l) longhest = l;
        }
        mean = mean / dur.length;
        shortest = shortest / 3600000;
        longhest = longhest / 3600000;

        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        result.put("Number of Traces", Integer.toString(log.size()));
        result.put("Number of Activities", Integer.toString(labels.size()));
        result.put("Number of Events", Integer.toString(events));
        result.put("Number of Resources", Integer.toString(resources.size()));
        result.put("Number of UniqueTraces", Integer.toString(uniqueTraces.size()));
        result.put("Log StartTime", dateFormat.format(start));
        result.put("Log EndTime", dateFormat.format(end));
        result.put("Mean duration in hours",  new DecimalFormat("#.##").format(mean));
        result.put("Median duration in hours",  new DecimalFormat("#.##").format(median));
        result.put("Min duration in hours",  new DecimalFormat("#.##").format(shortest));
        result.put("Max duration in hours",  new DecimalFormat("#.##").format(longhest));

        return result;
    }

    private String computeSize() {
        int size = 0;
        if(diagram == null) return "n/a";

        size += diagram.getNodes().size();

        return Integer.toString(size);
    }

    private String computeCFC() {
        int cfc = 0;
        if(diagram == null) return "n/a";

        for(CPFNode node : diagram.getNodes() )
            switch( node.getNodeType() ) {
                case XORSPLIT:
                    cfc += diagram.getPostset(node).size();
                    break;
                case ORSPLIT:
                    //case OR
                    cfc += (Math.pow(2.0, diagram.getPostset(node).size()) - 1);
                    break;
                case ANDSPLIT:
                    //case AND
                    cfc += 1;
                    break;
                default: continue;
            }

        return Integer.toString(cfc);
    }

    private String computeACD() {
        double acd = 0;
        double gates = 0;
        if(diagram == null) return "n/a";

        for(CPFNode node : diagram.getNodes() )
            switch( node.getNodeType() ) {
                case JOIN:
                case SPLIT:
                case XORJOIN:
                case XORSPLIT:
                case ORJOIN:
                case ORSPLIT:
                case ANDJOIN:
                case ANDSPLIT:
                    acd += (diagram.getPostset(node).size() + diagram.getPreset(node).size());
                    gates++;
                    break;
                default: continue;
            }

        if( acd == 0 ) return "0";    //this means no gateways!

        acd = acd / gates;
        return String.format("%.3f", acd);
    }

    private String computeMCD() {
        int mcd = 0;
        int tmp;
        if(diagram == null) return "n/a";

        for(CPFNode node : diagram.getNodes() )
            switch( node.getNodeType() ) {
                case JOIN:
                case SPLIT:
                case XORJOIN:
                case XORSPLIT:
                case ORJOIN:
                case ORSPLIT:
                case ANDJOIN:
                case ANDSPLIT:
                    if( mcd < (tmp = (diagram.getPostset(node).size() + diagram.getPreset(node).size())) ) mcd = tmp;
                    break;
                default: continue;
            }

        return Integer.toString(mcd);
    }

    private String computeCNC() {
        int nodes;
        double cnc;
        if(diagram == null) return "n/a";

        nodes = diagram.getNodes().size();
        if(nodes == 0) return "n/a";

        cnc = (double)diagram.getEdges().size() / (double)nodes;
        return String.format( "%.3f", cnc);
    }

    private String computeDensity() {
        int nodes = 0;
        double density;
        if(diagram == null) return "n/a";

        nodes = diagram.getNodes().size() - diagram.getSinkNodes().size() - diagram.getSourceNodes().size();

        if(nodes == 1 || nodes == 0) return "n/a";

        density = (double) diagram.getEdges().size() / (double) (nodes * (nodes - 1));
        return String.format( "%.3f", density);
    }


    private String computeStructuredness() {
        double structuredness;
        double nodes = 0;
        HashSet<RPSTNode> rigids = new HashSet<>();
        HashSet<RPSTNode> bonds = new HashSet<>();

        this.rigids = "n/a";
        this.bonds = "n/a";

        if(diagram == null) return "n/a";

        try {
            HashMap<CPFNode, Vertex> mapping = new HashMap<>();
            HashMap<String, CPFNode> gates = new HashMap<>();
            HashSet<String> removed = new HashSet<>();

            IDirectedGraph<DirectedEdge, Vertex> graph = new DirectedGraph();
            Vertex src;
            Vertex tgt;

            for (CPFEdge f : diagram.getEdges()) {
                if (!mapping.containsKey(f.getSource())) {
                    src = new Vertex(f.getSource().getId().toString());
                    if( isGateway(f.getSource()) ) gates.put(f.getSource().getId().toString(), f.getSource());
                    mapping.put(f.getSource(), src);
                } else src = mapping.get(f.getSource());

                if (!mapping.containsKey(f.getTarget())) {
                    tgt = new Vertex(f.getTarget().getId().toString());
                    if( isGateway(f.getTarget()) ) gates.put(f.getTarget().getId().toString(), f.getTarget());
                    mapping.put(f.getTarget(), tgt);
                } else tgt = mapping.get(f.getTarget());

                graph.addEdge(src, tgt);
            }

            RPST rpst = new RPST(graph);

            RPSTNode root = rpst.getRoot();
            LinkedList<RPSTNode> toAnalize = new LinkedList<RPSTNode>();
            toAnalize.add(root);

            boolean count = true;

            HashSet<Vertex> rChildren = new HashSet<>();
            HashSet<Vertex> bChildren = new HashSet<>();

            while (toAnalize.size() != 0) {

                root = toAnalize.pollFirst();

                if (!count && (root.getType() == TCType.P) && (rpst.getParent(root).getType() == TCType.B)) {
                    try {
                        CPFNode entry = gates.get(rpst.getParent(root).getEntry().getName());
                        CPFNode exit = gates.get(rpst.getParent(root).getExit().getName());
                        count = ( (entry != null) && (exit != null) && matchingGates(entry.getNodeType(), exit.getNodeType()) );
                    } catch (ClassCastException cce) {
                        count = false;
                    }
                }

                for (RPSTNode n : new HashSet<RPSTNode>(rpst.getChildren(root))) {
                    switch (n.getType()) {
                        case R:
                            toAnalize.add(n);
                            rigids.add(n);
                            break;
                        case T:
                            if ((root != rpst.getRoot()) && (root.getType() == TCType.P) && (rpst.getParent(root).getType() == TCType.R)) {
                                rChildren.add((Vertex) n.getEntry());
                                rChildren.add((Vertex) n.getExit());
                            }
                            if ((root != rpst.getRoot()) && (root.getType() == TCType.P) && (rpst.getParent(root).getType() == TCType.B)) {
                                bChildren.add((Vertex) n.getEntry());
                                bChildren.add((Vertex) n.getExit());
                            }
                            if (count) {
                                src = (Vertex) n.getEntry();
                                tgt = (Vertex) n.getExit();
                                if( !gates.containsKey(src.getName()) ) removed.add(src.getName());
                                if( !gates.containsKey(tgt.getName()) ) removed.add(tgt.getName());
                            }
                            break;
                        case P:
                            toAnalize.add(n);
                            break;
                        case B:
                            removed.add(n.getEntry().getName());
                            removed.add(n.getExit().getName());
                            toAnalize.add(n);
                            bonds.add(n);
                            break;
                        default:
                    }
                }

                count = false;
                toAnalize.remove(root);
            }

            nodes = diagram.getNodes().size();
            structuredness = 1 - ((nodes - removed.size()) / nodes);

        } catch (Exception e) {
            return "n/a";
        }

        this.rigids = Integer.toString(rigids.size());
        this.bonds = Integer.toString(bonds.size());

        return String.format( "%.3f", structuredness);
    }

    private String computeSeparability() {
        double separability;
        double nodes;
        if(diagram == null) return "n/a";

        try {
            HashMap<CPFNode, Vertex> mapping = new HashMap<CPFNode, Vertex>();
            IDirectedGraph<DirectedEdge, Vertex> graph = new DirectedGraph();
            Vertex src;
            Vertex tgt;

            for (CPFEdge f : diagram.getEdges()) {
                if (!mapping.containsKey(f.getSource())) {
                    src = new Vertex(f.getSource().getLabel());
                    mapping.put(f.getSource(), src);
                } else src = mapping.get(f.getSource());

                if (!mapping.containsKey(f.getTarget())) {
                    tgt = new Vertex(f.getTarget().getLabel());
                    mapping.put(f.getTarget(), tgt);
                } else tgt = mapping.get(f.getTarget());

                graph.addEdge(src, tgt);
            }

            RPST rpst = new RPST(graph);
            RPSTNode root = rpst.getRoot();
            HashSet<IVertex> articulationPoints = new HashSet<IVertex>();

            for (RPSTNode n : new HashSet<RPSTNode>(rpst.getChildren(root))) {
                switch (n.getType()) {
                    case R:
                        articulationPoints.add(n.getEntry());
                        articulationPoints.add(n.getExit());
                        break;
                    case T:
                        articulationPoints.add(n.getEntry());
                        articulationPoints.add(n.getExit());
                        break;
                    case P:
                        articulationPoints.add(n.getEntry());
                        articulationPoints.add(n.getExit());
                        break;
                    case B:
                        articulationPoints.add(n.getEntry());
                        articulationPoints.add(n.getExit());
                        break;
                    default:
                        //context.log("found something weird.");
                }
            }

            nodes = diagram.getNodes().size();

            separability = (articulationPoints.size() - 2) / (nodes - 2);
        } catch( Exception e ) {
            return "n/a";
        }

        return String.format( "%.3f", separability);
    }

    private String computeDuplicates() {
        int duplicates = 0;
        HashSet<String> nodes = new HashSet<>();
        String label;
        if(diagram == null) return "n/a";

        for( CPFNode node : diagram.getNodes() ) {
            label = node.getLabel();
            if (label == null) label = ""; // Bruce fixed 30.1.2019 to compute metrics in case of empty labels
            if( nodes.contains(label) && !label.isEmpty() && !isGateway(node) ) duplicates++;
            else nodes.add(label);
        }

        return Integer.toString(duplicates);
    }

    private boolean isGateway(CPFNode node) {
        switch( node.getNodeType() ) {
            case JOIN:
            case SPLIT:
            case XORJOIN:
            case XORSPLIT:
            case ORJOIN:
            case ORSPLIT:
            case ANDJOIN:
            case ANDSPLIT:
                return true;
            default: return false;
        }
    }

    private boolean matchingGates(NodeTypeEnum splitType, NodeTypeEnum joinType){
        if( splitType == SPLIT && joinType == JOIN ) return true;
        if( splitType == XORSPLIT && joinType == XORJOIN ) return true;
        if( splitType == ANDSPLIT && joinType == ANDJOIN ) return true;
        if( splitType == ORSPLIT && joinType == ORJOIN ) return true;

        return false;
    }

}
