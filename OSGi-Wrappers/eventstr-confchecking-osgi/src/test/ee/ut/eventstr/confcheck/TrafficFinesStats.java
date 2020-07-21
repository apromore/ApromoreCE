/*-
 * #%L
 * This file is part of "Apromore Community".
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
package ee.ut.eventstr.confcheck;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.junit.Test;

import ee.ut.mining.log.XLogReader;

public class TrafficFinesStats {
	String logfilename = 
			"RoadFines_real"
			;
		
	String logfiletemplate = 
			"models/pnml/%s.xes.gz"
			;

	@Test
	public void computeStats() throws Exception {
		XLog log = XLogReader.openLog(String.format(logfiletemplate, logfilename));

		System.out.println(getDistinctTraceCount(log));
	}
	
	private int getDistinctTraceCount(XLog log) {
		Set<List<String>> traces = new HashSet<List<String>>();
		
		for (XTrace trace: log) {
			traces.add(getActivities(trace));
		}
		
		return traces.size();
	}
	
	private List<String> getActivities(XTrace trace) { 
 		List<String> traceActivities = new ArrayList<String>(); 
 		XConceptExtension conceptExt = XConceptExtension.instance();
 		XLifecycleExtension lifecycleExt = XLifecycleExtension.instance();
 		
 		for (XEvent event : trace) { 
 			String actName = ""; 
 			actName += conceptExt.extractName(event); 
 			String trans = lifecycleExt.extractTransition(event); 
 			if (trans != null) { 
 				actName += " " + trans; 
 			} 
 			traceActivities.add(actName); 
 		} 
 		return traceActivities; 
 	} 

}
