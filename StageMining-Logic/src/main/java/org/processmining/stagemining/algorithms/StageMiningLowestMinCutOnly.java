/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Bruce Nguyen, Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * Copyright (C) 2020 Apromore Pty Ltd.
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
package org.processmining.stagemining.algorithms;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.deckfour.xes.model.XLog;
import org.jbpt.hypergraph.abs.IVertex;
import org.processmining.stagemining.models.DecompositionTree;
import org.processmining.stagemining.models.graph.Vertex2;
import org.processmining.stagemining.models.graph.WeightedDirectedGraph;
import org.processmining.stagemining.utils.GraphUtils;

import com.aliasi.cluster.LinkDendrogram;

/**
 * 1st argument: log file
 * 2nd argument: minimum stage size
 * 3rd argument: the fullname of the class to return the ground truth from the input log file
 * @param args
 */
public class StageMiningLowestMinCutOnly extends AbstractStageMining {
	
	@Override
    public DecompositionTree mine(XLog log, int minStageSize) throws Exception {
		
		//-------------------------------
		// Build graph from log
		//-------------------------------
		if (this.debug) System.out.println("Build graph from log");
		WeightedDirectedGraph graph = null;
		try {
			graph = GraphUtils.buildGraph(log);
			if (graph==null) {
				System.out.println("Bulding graph from log failed.");
				//return null;
			}			
			GraphUtils.removeSelfLoops(graph);
		} catch (ParseException e) {
			e.printStackTrace();
			//return null;
		}
		
		//-------------------------------
		// Build decomposition tree based on graph cuts
		//-------------------------------
		if (this.debug) System.out.println("Build dendrograms");
		
		// Initialize the decomposition tree
		DecompositionTree tree = new DecompositionTree(graph);
		LinkDendrogram<IVertex> root = new LinkDendrogram<IVertex>(null, new HashSet<IVertex>(graph.getVertices()), graph.getSource(), graph.getSink(), 0);
		tree.setRoot(root);
		List<LinkDendrogram<IVertex>> rootLevel = new ArrayList<LinkDendrogram<IVertex>>();
		rootLevel.add(root);
		tree.addBottomLevel(rootLevel, 0.0);
		
		// Compute min-cut for all vertices in the graph
		// NOTE: the candidate set has been sorted in ascending order of min-cut
		SortedSet<Vertex2> candidates = graph.searchCutPoints();
		if (this.debug) System.out.println("Candidate cut points sorted by min-cut: " + candidates.toString());
		
		for (Vertex2 v : candidates) {
			LinkDendrogram<IVertex> selected = null;
			if (this.debug) System.out.println("Check candidate node: " + v.getName());
			
			// Select a dendrogram with min cut at the bottom level
			for (LinkDendrogram<IVertex> d : tree.getBottomLevel()) {
				if (d.getMemberSet().contains(v)) {
					selected = d;
					break;
				}
			}
			if (selected == null) {
				throw new Exception("Cannot find a containing cluster at the bottom level of the decomposition tree for node " + v.getName());
			}
			
			//Perform graph cut
			List<Set<IVertex>> cutComponents = graph.cut(v);
	    	Set<IVertex> cluster0 = new HashSet<IVertex>(selected.getMemberSet());
	    	cluster0.retainAll(cutComponents.get(0));
	    	cluster0.add(v);
	    	
	    	Set<IVertex> cluster1 = new HashSet<IVertex>(selected.getMemberSet());
	    	cluster1.removeAll(cluster0);
	    	if (this.debug) System.out.println("Cluster1.size=" + cluster0.size() + ". Cluster2.size=" + cluster1.size());

	    	//Check the min stage size
	    	if ((cluster0.size()+1) < minStageSize || (cluster1.size()) < minStageSize) {
	    		if (this.debug) System.out.println("Cluster size is too small for cut-point = " + v.getName());
	    		if (this.debug) System.out.println("Cluster1=" + cluster0.toString());
	    		if (this.debug) System.out.println("Cluster2=" + cluster1.toString());
	    	}
	    	else {
		    	//Create the new bottom level and attach it to the decomposition tree
		    	LinkDendrogram<IVertex> dendro1 = new LinkDendrogram<IVertex>(selected, cluster0, selected.getSource(), v, v.getMinCut());
				LinkDendrogram<IVertex> dendro2 = new LinkDendrogram<IVertex>(selected, cluster1, v, selected.getSink(), v.getMinCut());
		    	List<LinkDendrogram<IVertex>> bottomLevelTemp = new ArrayList<LinkDendrogram<IVertex>>(tree.getBottomLevel());
		    	int index = bottomLevelTemp.indexOf(selected);
		    	bottomLevelTemp.remove(selected);
		    	bottomLevelTemp.add(index, dendro1);
		    	bottomLevelTemp.add(index+1, dendro2);	
		    	
		    	double mod = tree.computeModularity(bottomLevelTemp);
		    	tree.addBottomLevel(bottomLevelTemp, mod);
	    	}
		}
		
		return tree;
	}
	
	
	
	
	
}
