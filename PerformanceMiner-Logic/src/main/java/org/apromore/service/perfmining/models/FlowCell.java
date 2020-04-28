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
package org.apromore.service.perfmining.models;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;

public class FlowCell {
	private final DateTime timePoint;
	Map<String, Double> characteristicMap = new HashMap<String, Double>();

	public FlowCell(DateTime timePoint) {
		this.timePoint = timePoint;
	}

	public DateTime getTimePoint() {
		return timePoint;
	}

	public Double getCharacteristic(String characteristicCode) {
		if (characteristicMap.containsKey(characteristicCode)) {
			return characteristicMap.get(characteristicCode);
		} else {
			return 0.0;
		}
	}

	public void setCharacteristic(String characteristicCode, Double newValue) {
		characteristicMap.put(characteristicCode, newValue);
	}

	public void clear() {
		characteristicMap.clear();
	}
}
