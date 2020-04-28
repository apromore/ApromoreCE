/*-
 * #%L
 * This file is part of "Apromore Community".
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
package org.apromore.processdiscoverer.dfg.abstraction;

import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.dfg.TraceDFG;
import org.apromore.processdiscoverer.dfg.vis.BPMNDiagramLayouter;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;

/**
 * 
 * @author Bruce Nguyen
 *
 */
public class TraceAbstraction extends AbstractAbstraction {
	private TraceDFG traceDfg;
	
	public TraceAbstraction(TraceDFG traceDfg, AbstractionParams params) {
		super(traceDfg.getLogDFG(), params);
		this.traceDfg = traceDfg;
		this.diagram = traceDfg.getDFG(params);
		this.updateWeights(params);
		this.layout = BPMNDiagramLayouter.layout(this.diagram, false);
	}
	
	public TraceDFG getTraceDFG() {
		return this.traceDfg;
	}
	
	@Override
	protected void updateNodeWeights(AbstractionParams params) {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void updateArcWeights(AbstractionParams params) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateWeights(AbstractionParams params) {
		for(BPMNNode node : diagram.getNodes()) {
			String node_name = node.getLabel();
            if (node_name.contains("\\n")) {
                node_name = node_name.substring(0, node_name.indexOf("\\n"));
                String value = node.getLabel().substring(node.getLabel().indexOf("[") + 1, node.getLabel().length() - 1);
                nodePrimaryWeights.put(node, Double.parseDouble(value));
            }else {
            	nodePrimaryWeights.put(node, 0.0);
            }
            nodeSecondaryWeights.put(node, 1.0);
		}
		
		for (BPMNEdge edge: diagram.getEdges()) {
			String mainNumber = edge.getLabel();
            String secondaryNumber= "";
            if(mainNumber.contains("[")) {
                if(mainNumber.contains("\n")) {
                    secondaryNumber = mainNumber.substring(mainNumber.indexOf("\n"), mainNumber.length() - 1);
                    mainNumber = mainNumber.substring(1, mainNumber.indexOf("\n"));
                }else mainNumber = mainNumber.substring(1, mainNumber.length() - 1);
            }else {
                mainNumber = "1";
                secondaryNumber = "1";
            }
            mainNumber = fixNumber(mainNumber);
            arcPrimaryWeights.put(edge, Double.parseDouble(mainNumber));
            arcSecondaryWeights.put(edge, secondaryNumber.isEmpty() ? 0 : Double.parseDouble(secondaryNumber));
		}
		
	}


}
