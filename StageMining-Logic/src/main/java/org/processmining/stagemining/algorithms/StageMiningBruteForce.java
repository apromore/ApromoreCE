/*-
 * #%L
 * This file is part of "Apromore Community".
 * 
 * Copyright (C) 2017 Bruce Nguyen, Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.math3.util.Combinations;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.deckfour.xes.model.XLog;
import org.jbpt.hypergraph.abs.IVertex;
import org.processmining.stagemining.models.DecompositionTree;
import org.processmining.stagemining.models.graph.Vertex2;
import org.processmining.stagemining.models.graph.WeightedDirectedGraph;
import org.processmining.stagemining.utils.GraphUtils;

import com.aliasi.cluster.LinkDendrogram;
//import com.rapidminer.RapidMiner;

/**
 * This class mine phase models based on min-cut
 * @author Bruce
 *
 */
public class StageMiningBruteForce extends AbstractStageMining {

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
		// Compute candidate list
		//-------------------------------
		SortedSet<Vertex2> candidates = graph.searchCutPoints();
		if (this.debug) System.out.println("Candidate cut points (sorted): " + candidates.toString());
		Vertex2[] candidateArray = new Vertex2[candidates.size()];
		candidateArray = candidates.toArray(candidateArray);
		if (this.debug) System.out.println("Candidate array for combinations: " + candidateArray.toString());
		if (this.debug) System.out.println("Number of candidate nodes: " + candidateArray.length);
		long totalCombi = 0;
		int kMax = graph.getActivityVertices().size()/minStageSize; // ONLY take maximum kMax from n, others > kMax will return stage size < minStageSize
		for (int k=1;k<=kMax;k++) {
			totalCombi += CombinatoricsUtils.binomialCoefficient(candidateArray.length, k);
		}
		if (this.debug) System.out.println("Total number of combinations: " + totalCombi);
		
		//-------------------------------
		// Take recursive graph cuts
		//-------------------------------
		DecompositionTree bestTree = null;
		double bestMod = 0.0;
		long counter = 1;
		for (int k=1; k<=kMax; k++) {
			
			// Get all combinations of k-size from the candidate list
			Iterator<int[]> combinations = new Combinations(candidateArray.length, k).iterator();
			if (this.debug) System.out.println();
			if (this.debug) System.out.println("================= COMBINATION OF " + k + " size: " + 
							CombinatoricsUtils.binomialCoefficient(candidateArray.length, k) + " combinations");
				
			// For each combination of k-size: find a stage decomposition
			while (combinations.hasNext()) {
				int[] nodes = combinations.next();
				if (this.debug) System.out.println();
				if (this.debug) System.out.print("COMBINATION " + (counter++) + ": ");
				for (int i=0;i<nodes.length;i++) {
					if (this.debug) System.out.print(candidateArray[nodes[i]].getName() + ",");
				}
				if (this.debug) System.out.println();
				
				// Create a decomposition tree for one combination of nodes
				DecompositionTree tree = new DecompositionTree(graph);
				LinkDendrogram<IVertex> root = new LinkDendrogram<IVertex>(null, 
													new HashSet<IVertex>(graph.getActivityVertices()), graph.getSource(), 
																			graph.getSink(), 0);
				tree.setRoot(root);
				List<LinkDendrogram<IVertex>> rootLevel = new ArrayList<LinkDendrogram<IVertex>>();
				rootLevel.add(root);
				tree.addBottomLevel(rootLevel, 0.0);
				
				for (int i=0;i<nodes.length;i++) {
					Vertex2 v = candidateArray[nodes[i]]; // each node from a combination as cut-point
					
					// Select the cluster that contains v
					LinkDendrogram<IVertex> selected = null;
					//if (this.debug) System.out.println("");
					//if (this.debug) System.out.println("Check candidate node: " + v.getName());
					for (LinkDendrogram<IVertex> d : tree.getBottomLevel()) {
						if (d.getMemberSet().contains(v)) {
							selected = d;
							break;
						}
					}
					if (selected == null) {
						throw new Exception("Cannot find a containing cluster at the bottom level of the decomposition tree for node " + v.getName());
					}
					
					//Perform graph cut on the selected node
					List<Set<IVertex>> cutResult = tree.graphCut(v, selected);
					Set<IVertex> stage1 = cutResult.get(0);
					Set<IVertex> stage2 = cutResult.get(1);
					//if (this.debug) System.out.println("Stage1.size=" + stage1.size() + ". Stage2.size=" + stage2.size());

			    	//Check min stage size
			    	if (stage1.size() >= minStageSize && stage2.size() >= minStageSize) {
				    	//Create the new bottom level
				    	LinkDendrogram<IVertex> dendro1 = new LinkDendrogram<IVertex>(selected, stage1, selected.getSource(), v, v.getMinCut());
						LinkDendrogram<IVertex> dendro2 = new LinkDendrogram<IVertex>(selected, stage2, v, selected.getSink(), v.getMinCut());
				    	List<LinkDendrogram<IVertex>> bottomLevelTemp = new ArrayList<LinkDendrogram<IVertex>>(tree.getBottomLevel());
				    	int index = bottomLevelTemp.indexOf(selected);
				    	bottomLevelTemp.remove(selected);
				    	bottomLevelTemp.add(index, dendro1);
				    	bottomLevelTemp.add(index+1, dendro2);	 
				    	
				    	double mod = tree.computeModularity(bottomLevelTemp);
				    	tree.addBottomLevel(bottomLevelTemp, mod);
			    	}	
			    	else {
			    		if (this.debug) System.out.println("Stop decomposing as stage size is smaller than " + minStageSize);
			    		break; // break here because the other nodes in the combination will have the same small stage size 
			    	}
			    	
			    	
				} // end a combination
				
				// If all nodes in the combination are used as cut-points
				if (tree.getMaxLevelIndex() == nodes.length) {
					double mod = tree.getModularity(tree.getMaxLevelIndex()); // get the modularity of the bottom level
					if (this.debug) System.out.print("Compare modularity with current best SD: ");
					if (mod > bestMod) {
						if (this.debug) System.out.println("Better - accepted");
						bestMod = mod;
						bestTree = tree;
					}
					else {
						if (this.debug) System.out.println("No better - rejected");
						tree.clear();
					}
				}
			}
		}
		
		return bestTree;
	}
	
}
