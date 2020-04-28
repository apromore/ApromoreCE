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
import org.apromore.processdiscoverer.VisualizationAggregation;
import org.apromore.processdiscoverer.VisualizationType;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;

/**
 * Abstraction represents an abstraction of log or trace.
 * It can be a directly-follows graph or a BPMN diagram model
 * In addition, it is affected by different abstraction parameters
 * The DFG or BPMN model is stored in a diagram.
 * The nodes and edges on the diagram can be two types of weights: primary and secondary
 * and they can be displayed at the same time 
 * @author Bruce Nguyen
 *
 */
public interface Abstraction {
	AbstractionParams getAbstractionParams();
	BPMNDiagram getDiagram();
	double getNodePrimaryWeight(BPMNNode node);
	double getNodeSecondaryWeight(BPMNNode node);
	double getArcPrimaryWeight(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge);
	double getArcSecondaryWeight(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge);
}
