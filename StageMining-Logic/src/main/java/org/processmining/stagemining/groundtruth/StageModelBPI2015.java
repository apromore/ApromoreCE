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

public class StageModelBPI2015 extends ExampleClass {
	public List<Set<String>> getGroundTruth(XLog log) throws Exception {
		
		Map<String,Set<String>> mapMilestonePhase = new HashMap<String, Set<String>>();
		//mapMilestonePhase.put("01_hoofd_0", new HashSet<String>());
		mapMilestonePhase.put("01_hoofd_1", new HashSet<String>());
		mapMilestonePhase.put("01_hoofd_2", new HashSet<String>());
		mapMilestonePhase.put("01_hoofd_3", new HashSet<String>());
		mapMilestonePhase.put("01_hoofd_4", new HashSet<String>());
		
		List<Set<String>> phaseModel = new ArrayList<Set<String>>();
		//phaseModel.add(mapMilestonePhase.get("01_hoofd_0"));
		phaseModel.add(mapMilestonePhase.get("01_hoofd_1"));
		phaseModel.add(mapMilestonePhase.get("01_hoofd_2"));
		phaseModel.add(mapMilestonePhase.get("01_hoofd_3"));
		phaseModel.add(mapMilestonePhase.get("01_hoofd_4"));
		
		for (XTrace trace : log) {
			String traceID = LogUtilites.getConceptName(trace);
			for (XEvent event : trace) {
				String eventName = LogUtilites.getConceptName(event).toLowerCase();
				if (eventName.equals("start") || eventName.equals("end")) continue;
				
				String eventStageName = eventName.substring(0,10);
				if (mapMilestonePhase.keySet().contains(eventStageName)) {
					mapMilestonePhase.get(eventStageName).add(eventName);
				}
				else {
					throw new Exception("Cannot find a stage name " + eventStageName + 
								" in the predefined set of stage names " + mapMilestonePhase.keySet().toString() + 
								". TraceID = " + traceID +  ". Event name = " + eventName);
				}
			}
		}
		
		return phaseModel;
	}
}
