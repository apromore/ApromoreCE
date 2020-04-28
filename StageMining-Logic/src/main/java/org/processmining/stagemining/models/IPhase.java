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
package org.processmining.stagemining.models;

import java.util.ArrayList;
import java.util.Set;

import org.jbpt.hypergraph.abs.IVertex;
import org.processmining.stagemining.models.graph.WeightedDirectedEdge;

public interface IPhase {
	
//	public PhaseModel getPhaseModel();
//	
//	//Indicate this is a source phase (a phase containing only the source vertex)
//	public boolean isSource();
//	
//	///Indicate this is a sink phase (a phase containing only the sink vertex)
//	public boolean isSink();
//	
//	public IPhase getNext();
//	
//	public IPhase getPrevious();	
//	
//	public Set<IVertex> getVertices();
//	
//	public Set<WeightedDirectedEdge<IVertex>> getEdges();
//	
//	// Check if this phase is trivial (too simple)
//	public boolean isTrivial();
//	
//	
//	//Cohesion based on the density of edges connecting different vertices
//	public double getControlFlowCohesion() throws Exception;
//	
//	//Cohesion based on the semantic similarity of activity labels within one phase
//	public double getActLabelCohesion() throws Exception;
//	
//	//public double getResourcePropertyCohesion();
//	
//	public ArrayList<IPhase> getSubPhases();
//	
////	public IVertex getEntry();
////	
////	public IVertex getExit();
//	
//	public IVertex getEndingVertex();
//	
//	public void setEndingVertex(IVertex endVertex);
//	
//	public void print() throws Exception;
}
