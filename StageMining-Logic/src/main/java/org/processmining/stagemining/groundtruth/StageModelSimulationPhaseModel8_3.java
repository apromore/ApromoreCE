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

public class StageModelSimulationPhaseModel8_3 extends ExampleClass {
	public List<Set<String>> getGroundTruth(XLog log) throws Exception {
		
		Map<String,Set<String>> mapMilestonePhase = new HashMap<String, Set<String>>();
		
		mapMilestonePhase.put("X", new HashSet<String>());
		mapMilestonePhase.put("Y", new HashSet<String>());
		mapMilestonePhase.put("Z", new HashSet<String>());
		
		mapMilestonePhase.get("X").add("a");
		mapMilestonePhase.get("X").add("b");
		mapMilestonePhase.get("X").add("c");
		mapMilestonePhase.get("X").add("x");
		
		mapMilestonePhase.get("Y").add("d");
		mapMilestonePhase.get("Y").add("e");
		mapMilestonePhase.get("Y").add("f");
		mapMilestonePhase.get("Y").add("y");
		
		mapMilestonePhase.get("Z").add("g");
		mapMilestonePhase.get("Z").add("h");
		mapMilestonePhase.get("Z").add("i");
		
		List<Set<String>> phaseModel = new ArrayList<Set<String>>();
		phaseModel.add(mapMilestonePhase.get("X"));
		phaseModel.add(mapMilestonePhase.get("Y"));
		phaseModel.add(mapMilestonePhase.get("Z"));
		
		return phaseModel;
	}
}
