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
package org.processmining.stagemining.groundtruth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.hypergraph.abs.IVertex;
import org.processmining.stagemining.utils.LogUtilites;

public class StageModelSim1 extends ExampleClass {
	public List<Set<String>> getGroundTruth(XLog log) throws Exception {
		
		//Phase Model 8-2
		
		List<Set<String>> phaseModel = new ArrayList<Set<String>>();

		
		Set<String> P1 = new HashSet<String>();
		P1.add("a");
		P1.add("b");
		P1.add("c");
		P1.add("x");
		phaseModel.add(P1);
		
		Set<String> P2 = new HashSet<String>();
		P2.add("d");
		P2.add("e");
		P2.add("f");
		P2.add("y");
		phaseModel.add(P2);
		
		Set<String> P3 = new HashSet<String>();
		P3.add("i");
		P3.add("g");
		P3.add("h");
		P3.add("z");
		phaseModel.add(P3);		
		
		return phaseModel;
	}
}
