/*-
 * #%L
 * This file is part of "Apromore Community".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package ee.ut.eventstr.comparison.differences;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.Collections;
import java.util.LinkedList;


/**
 * Collection of differences to be retrieved by the 
 * REST service. This class can be seen as a wrapper
 * for generating the JSON file retrieved by the 
 * REST service. 
 */

public class DifferencesML {
    private LinkedList<DifferenceML> differences;
    private int numberOfDifferences;
    private int commonLabels;

    public DifferencesML() {
        differences = new LinkedList<DifferenceML>();
        setNumberOfDifferences(0);
        commonLabels = 0;
    }

    public void add(DifferenceML diff) {
        if (!contains(diff)) {
            differences.add(diff);
            setNumberOfDifferences(differences.size());
            Collections.sort(differences);
        }
    }

    public LinkedList<DifferenceML> getDifferences() {
        return differences;
    }

    public void remove(DifferenceML diff2Remove) {
        differences.remove(diff2Remove);
        setNumberOfDifferences(differences.size());
    }

	public void setDifferences(LinkedList<DifferenceML> differences) {
		this.differences = differences;
		setNumberOfDifferences(differences.size());
	}

	public String toString() {
		String result = "";

		for (DifferenceML dif : differences)
			result += dif.getSentence() + "\n";

		return result;
	}

	public static String toJSON(DifferencesML diffs) {
		try {
			ObjectMapper mapper = new ObjectMapper();

			return mapper.writeValueAsString(diffs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void setCommonLabels(int commonLabels) {
		this.commonLabels = commonLabels;
	}

	public int getCommonLabels() {
		return commonLabels;
	}

	public int getNumberOfDifferences() {
		return numberOfDifferences;
	}

	public void setNumberOfDifferences(int numberOfDifferences) {
		this.numberOfDifferences = numberOfDifferences;
	}

	public boolean contains(DifferenceML difference) {
		for (DifferenceML diff : differences)
			if (diff.getSentence().equals(difference.getSentence()))
				return true;

		return false;
	}

	public DifferencesML clone() {
		DifferencesML diff = new DifferencesML();
		diff.setCommonLabels(commonLabels);

		for (DifferenceML d : differences)
			diff.add(d);

		return diff;
	}

	public void addAll(LinkedList<DifferenceML> differences) {
		for (DifferenceML d : differences)
			add(d);
	}
}
