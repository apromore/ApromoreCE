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
import java.util.Random;
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
 * 2nd argument: the fullname of the class to return the ground truth from the input log file
 * 3rd argument: minimum stage size
 * @author Bruce
 *
 */
public class StageMiningRandomCutPoint extends AbstractStageMining {


	@Override
    public DecompositionTree mine(XLog log, int minStageSize) throws Exception {
		
		//-------------------------------
		// Build graph from log
		//-------------------------------
		System.out.println("Build graph from log");
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
		// Compute candidate list
		//-------------------------------
		System.out.println("Search for candidate cut-points");
		SortedSet<Vertex2> candidates = graph.searchCutPoints();
		System.out.println("Candidate cut points sorted by min-cut: " + candidates.toString());
		
		//-------------------------------
		// Take recursive graph cuts
		//-------------------------------
		System.out.println("Build dendrograms");
		DecompositionTree tree = new DecompositionTree(graph);
		
		LinkDendrogram<IVertex> root = new LinkDendrogram<IVertex>(null, new HashSet<IVertex>(graph.getActivityVertices()), graph.getSource(), graph.getSink(), 0);
		tree.setRoot(root);
		List<LinkDendrogram<IVertex>> rootLevel = new ArrayList<LinkDendrogram<IVertex>>();
		rootLevel.add(root);
		tree.addBottomLevel(rootLevel, 0.0);
		List<LinkDendrogram<IVertex>> SD_Best = tree.getBottomLevel();
		double SD_Best_Mod = tree.getModularity(SD_Best);
		Set<Vertex2> selectedNodes = new HashSet<Vertex2>();
		
		List<Vertex2> currentCandidates = new ArrayList<Vertex2>(candidates);
		while (!currentCandidates.isEmpty()) {
			// Randomly select the next node from the current candidates
			
			Random rand = new Random();
			int  randIndex = rand.nextInt(currentCandidates.size());
			
			if (this.debug) System.out.println();
			if (this.debug) System.out.println("Random number = " + randIndex + " for range from 0 to " + candidates.size());
			Vertex2 v = currentCandidates.get(randIndex); //pick up one node randomly
			if (this.debug) System.out.println("Check candidate node: " + v.getName());
			
			// Find a stage containing the node to cut
			LinkDendrogram<IVertex> selected = null;
			for (LinkDendrogram<IVertex> d : tree.getBottomLevel()) {
				if (d.getMemberSet().contains(v)) {
					selected = d;
					break;
				}
			}
			if (selected == null) {
				throw new Exception("Cannot find a containg cluster at the bottom level of the decomposition tree for node " + v.getName());
			}
			
			// Take graph cut
			List<Set<IVertex>> cutResult = tree.graphCut(v, selected);
			Set<IVertex> stage1 = cutResult.get(0);
			Set<IVertex> stage2 = cutResult.get(1);
			if (this.debug) System.out.println("Stage1.size = " + stage1.size() + ", Stage2.size = " + stage2.size());
			
			if (stage1.size() >= minStageSize && stage2.size() >= minStageSize) { 
		    	//Create the new bottom level
		    	LinkDendrogram<IVertex> dendro1 = new LinkDendrogram<IVertex>(selected, stage1, selected.getSource(), v, v.getMinCut());
				LinkDendrogram<IVertex> dendro2 = new LinkDendrogram<IVertex>(selected, stage2, v, selected.getSink(), v.getMinCut());
		    	List<LinkDendrogram<IVertex>> bottomLevelTemp = new ArrayList<LinkDendrogram<IVertex>>(tree.getBottomLevel());
		    	int index = bottomLevelTemp.indexOf(selected);
		    	bottomLevelTemp.remove(selected);
		    	bottomLevelTemp.add(index, dendro1);
		    	bottomLevelTemp.add(index+1, dendro2);	    	
				
				//Compute modularity and the best bottom level
				double newMod = tree.computeModularity(bottomLevelTemp);
				if (newMod > SD_Best_Mod) {
					tree.addBottomLevel(bottomLevelTemp, newMod);
					SD_Best_Mod = newMod;
				}
				else {
					//break;
				}
	    	}
			else {
				if (this.debug) {
					System.out.println("Cluster size is smaller than the minimum stage size!");
//		    		System.out.println("Cluster1=" + stage1.toString());
//		    		System.out.println("Cluster2=" + stage2.toString());
				}
	    	}
			
			selectedNodes.add(v);
			currentCandidates = new ArrayList<Vertex2>(candidates);
			currentCandidates.removeAll(selectedNodes);
		}
		
		return tree;
	}
	
	
	
}
