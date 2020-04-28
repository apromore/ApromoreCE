/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Bruce Nguyen, Queensland University of Technology.
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
package org.processmining.stagemining.algorithms;
import org.processmining.stagemining.models.DecompositionTree;
import org.processmining.stagemining.groundtruth.ExampleClass;
import org.processmining.stagemining.utils.LogUtilites;
import org.processmining.stagemining.utils.Measure;
import org.processmining.stagemining.utils.OpenLogFilePlugin;
import org.deckfour.xes.model.XLog;
//import com.rapidminer.RapidMiner;

/**
 * This class mine phase models based on min-cut
 * @author Bruce
 *
 */
public class StageMiningHighestModularityNoStageSize {
	/**
	 * 1st argument: log file
	 * 2nd argument: minimum stage size (NOT USED IN THIS CLASS)
	 * 3rd argument: the fullname of the class to return the ground truth from the input log file
	 * @param args
	 */
	public static void main(String[] args) {
		OpenLogFilePlugin logImporter = new OpenLogFilePlugin();
		try {
			System.out.println("Import log file");
			XLog log = (XLog)logImporter.importFile(System.getProperty("user.dir") + "\\" + args[0]);
			LogUtilites.addStartEndEvents(log);
			
		    System.out.println("Start phase mining");
		    AbstractStageMining miner = new StageMiningHighestModularity();
			miner.setDebug(false);
			
			double bestMod = 0;
			DecompositionTree bestTree = null;
			int bestStageSize = 0;
			for (int k=2;k<(LogUtilites.getDistinctEventClassCount(log) / 2);k++) {
				System.out.println();
				System.out.println("STAGE MINING FOR minStageSize = " + k);
				
				DecompositionTree ktree = miner.mine(log,k);
				
				if (ktree != null) {
					//ktree.print();
					double mod = ktree.getModularity(ktree.getBestLevelIndex());
					int bestLevelIndex = ktree.getBestLevelIndex();
					ExampleClass example = (ExampleClass)Class.forName(args[2]).newInstance();
//					System.out.println("Best Level Index = " + bestLevelIndex);
					System.out.println("Transition nodes from beginning: " + ktree.getTransitionNodes(bestLevelIndex));
					System.out.println("Stages = " + ktree.getActivityLabelSets(bestLevelIndex).toString());
//					System.out.println("Ground Truth = " + example.getGroundTruth(log).toString());
					System.out.println("Modularity by creation order: " + ktree.getModularitiesByCreationOrder());
					System.out.println("Best Modularity = " + mod);
					
//					double randIndex = Measure.computeMeasure(ktree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 1);
					double fowlkes = Measure.computeMeasure(ktree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 2);
//					double jaccard = Measure.computeMeasure(ktree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 3);
//					System.out.println("Rand Index = " + randIndex);
					System.out.println("Fowlkes–Mallows Index = " + fowlkes);
//					System.out.println("Jaccard Index = " + jaccard);
					
					if (mod > bestMod) {
						bestMod = mod;
						bestTree = ktree;
						bestStageSize = k;
					}
				}
			}
			
			if (bestTree != null) {
				System.out.println("");
				System.out.println("BEST STAGE DECOMPOSITION");
				//-------------------------------
				// Print the result
				//-------------------------------
				//bestTree.print();
				
				//-------------------------------
				// Calculate Rand index
				//-------------------------------
				int bestLevelIndex = bestTree.getBestLevelIndex();
				ExampleClass example = (ExampleClass)Class.forName(args[2]).newInstance();
				System.out.println("Best Level Index = " + bestLevelIndex);
				System.out.println("Best stage size = " + bestStageSize);
				System.out.println("Transition nodes from beginning: " + bestTree.getTransitionNodes(bestLevelIndex));
				System.out.println("Transition nodes by creation order: " + bestTree.getTransitionNodesByCreationOrder(bestLevelIndex));
				System.out.println("Modularity by creation order: " + bestTree.getModularitiesByCreationOrder());
				System.out.println("Stages = " + bestTree.getActivityLabelSets(bestLevelIndex).toString());
				System.out.println("Ground Truth = " + example.getGroundTruth(log).toString());
				
//				double randIndex = Measure.computeMeasure(bestTree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 1);
				double fowlkes = Measure.computeMeasure(bestTree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 2);
//				double jaccard = Measure.computeMeasure(bestTree.getActivityLabelSets(bestLevelIndex), example.getGroundTruth(log), 3);
//				System.out.println("Rand Index = " + randIndex);
				System.out.println("Fowlkes–Mallows Index = " + fowlkes);
//				System.out.println("Jaccard Index = " + jaccard);
				
				System.out.println("Finish phase mining");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
