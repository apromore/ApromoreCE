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
package org.apromore.plugin.portal.stagemining;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jbpt.hypergraph.abs.IVertex;
import org.jfree.data.time.TimeTableXYDataset;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.processmining.stagemining.models.DecompositionTree;
import org.processmining.stagemining.utils.GraphUtils;

/**
 *
 * @author Administrator
 */
public class Visualization {
    public static JSONObject createJson(DecompositionTree tree) throws JSONException, Exception {
        List<Set<IVertex>> stages = new ArrayList<>();
        
        Set<IVertex> start = new HashSet<IVertex>();
        start.add(tree.getGraph().getSource());
        stages.add(start);
        
        Set<IVertex> end = new HashSet<IVertex>();
        end.add(tree.getGraph().getSink());
        stages.add(end);
        
        stages.addAll(tree.getStageList(tree.getMaxLevelIndex()));
        
        double[][] adjMatrix = new double[stages.size()][stages.size()];
        double maxWeight = 0.0;
        for (int i=0;i<adjMatrix.length;i++) {
            for (int j=0;j<adjMatrix.length;j++) {
                if (i==j) continue;
                
                if (stages.get(i).contains(tree.getGraph().getSource())) {
                    adjMatrix[i][j] = GraphUtils.getDirectedConnectionWeightFromSource(
                                                    tree.getGraph(), 
                                                    tree.getGraph().getSource(), 
                                                    stages.get(j));
                }
                else if (stages.get(j).contains(tree.getGraph().getSink())) {
                    adjMatrix[i][j] = GraphUtils.getDirectedConnectionWeightToSink(
                                                    tree.getGraph(), 
                                                    stages.get(i),
                                                    tree.getGraph().getSink());
                }
                else {
                    adjMatrix[i][j] = GraphUtils.getDirectedConnectionWeight(
                                                    tree.getGraph(), 
                                                    stages.get(i), 
                                                    stages.get(j));
                }
                if (adjMatrix[i][j] > maxWeight) maxWeight = adjMatrix[i][j];
            }
        }
        
        JSONObject json = new JSONObject();
        
        //-----------------------------------------
        // For node array
        //-----------------------------------------
        JSONArray jsonNodeArray = new JSONArray();
        
        for (int i=0;i<stages.size();i++) {
            JSONObject jsonOneNode = new JSONObject();
            jsonOneNode.put("key", i);
            String nodeName = "";
            for (IVertex v : stages.get(i)) {
                if (nodeName.equals("")) {
                    nodeName = v.getName();
                }
                else {
                    nodeName += (", " + v.getName());
                }
            }
            jsonOneNode.put("text", nodeName);
            jsonNodeArray.put(jsonOneNode);
        }
        json.put("nodeDataArray", jsonNodeArray);
        
        //-----------------------------------------
        // For link array
        //-----------------------------------------
        JSONArray jsonLinkArray = new JSONArray();
        for (int i=0;i<adjMatrix.length;i++) {
            for (int j=0;j<adjMatrix.length;j++) {
                if (adjMatrix[i][j] > 0) {
                    JSONObject jsonOneLink = new JSONObject();
                    jsonOneLink.put("from", i);
                    jsonOneLink.put("to", j);
                    jsonOneLink.put("strokeWidth", adjMatrix[i][j]*20/maxWeight);
                    jsonOneLink.put("text", adjMatrix[i][j]);
                    jsonLinkArray.put(jsonOneLink);
                }
            }
        }
        json.put("linkDataArray", jsonLinkArray);
        
        return json;
    }
}
