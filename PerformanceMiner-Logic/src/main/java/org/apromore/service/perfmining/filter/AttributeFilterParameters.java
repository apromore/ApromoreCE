/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Bruce Nguyen.
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
package org.apromore.service.perfmining.filter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

public class AttributeFilterParameters {

	protected Map<String, Set<String>> filter;
	protected String name;

	public AttributeFilterParameters() {
		filter = new HashMap<String, Set<String>>();
		name = "";
	}

	public AttributeFilterParameters(XLog log) {
		filter = new HashMap<String, Set<String>>();
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				for (String key : event.getAttributes().keySet()) {
					if (!filter.containsKey(key)) {
						filter.put(key, new HashSet<String>());
					}
					filter.get(key).add(event.getAttributes().get(key).toString());
				}
			}
			//			context.getProgress().inc();
		}
		name = XConceptExtension.instance().extractName(log);
	}

	public void setFilter(Map<String, Set<String>> filter) {
		this.filter = filter;
	}

	public Map<String, Set<String>> getFilter() {
		return filter;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
