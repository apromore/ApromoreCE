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

public class StageModelBPI2013 extends ExampleClass{
	public List<Set<String>> getGroundTruth(XLog log) throws Exception {
		
		Map<String,Set<String>> mapMilestonePhase = new HashMap<String, Set<String>>();
		mapMilestonePhase.put("1st", new HashSet<String>());
		mapMilestonePhase.put("2nd", new HashSet<String>());
		mapMilestonePhase.put("3rd", new HashSet<String>());
		
		List<Set<String>> phaseModel = new ArrayList<Set<String>>();
		phaseModel.add(mapMilestonePhase.get("1st"));
		phaseModel.add(mapMilestonePhase.get("2nd"));
		phaseModel.add(mapMilestonePhase.get("3rd"));
		
		for (XTrace trace : log) {
			String traceID = LogUtilites.getConceptName(trace);
			for (XEvent event : trace) {
				String eventName = LogUtilites.getConceptName(event).toLowerCase();
				if (eventName.equals("start") || eventName.equals("end")) continue;
				
				String groupName = LogUtilites.getValue(event.getAttributes().get("org:group")).toLowerCase();
				String line = "";
				if (groupName.contains("2nd")) {
					line = "2nd";
				}
				else if (groupName.contains("3rd")) {
					line = "3rd";
				}
				else {
					line = "1st";
				}
				
				if (mapMilestonePhase.keySet().contains(line)) {
					mapMilestonePhase.get(line).add(eventName);
				}
				else {
					throw new Exception("Cannot find line name '" + line + "' in the predefined line list " + 
							mapMilestonePhase.keySet().toString() + ". TraceID = " + traceID + 
							". Event name = " + eventName);
				}
			}
		}
		
		return phaseModel;
	}
}
