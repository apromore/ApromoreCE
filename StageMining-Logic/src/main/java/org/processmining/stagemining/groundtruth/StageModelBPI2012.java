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

public class StageModelBPI2012 extends ExampleClass {
	public List<Set<String>> getGroundTruth(XLog log) throws Exception {
		
		Map<String,Set<String>> mapMilestonePhase = new HashMap<String, Set<String>>();
		mapMilestonePhase.put("S1", new HashSet<String>());
		mapMilestonePhase.put("S2", new HashSet<String>());
		mapMilestonePhase.put("S3", new HashSet<String>());
		mapMilestonePhase.put("S4", new HashSet<String>());
		
		List<Set<String>> phaseModel = new ArrayList<Set<String>>();
		phaseModel.add(mapMilestonePhase.get("S1"));
		phaseModel.add(mapMilestonePhase.get("S2"));
		phaseModel.add(mapMilestonePhase.get("S3"));
		phaseModel.add(mapMilestonePhase.get("S4"));
		
		Set<String> eventSet = new HashSet<String>();
		for (XTrace trace : log) {
			eventSet.clear();
			for (XEvent event : trace) {
				String eventName = LogUtilites.getConceptName(event).toLowerCase();
				String stageName = LogUtilites.getConceptName(event).substring(0,2);
				if (eventName.equals("start") || eventName.equals("end")) continue;
				if (mapMilestonePhase.keySet().contains(stageName)) {
					mapMilestonePhase.get(stageName).add(eventName);
				}
				else {
					throw new Exception("Cannot find stage name: '" + stageName + "'");
				}
			}
		}
		
		return phaseModel;
	}
}
