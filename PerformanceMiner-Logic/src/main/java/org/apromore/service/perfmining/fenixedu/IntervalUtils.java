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
package org.apromore.service.perfmining.fenixedu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class IntervalUtils {
	/**
	 * Merges a set of interval lists into a single list of non-intersecting
	 * intervals by merging all overlaping intervals into one.
	 * 
	 * @param a
	 *            set of lists of {@link Interval}s
	 * @return a list of {@link Interval}s
	 */
	public static List<Interval> mergeIntervalLists(List<Interval>... lists) {
		SortedSet<Interval> totalSpace = new TreeSet<Interval>(START_TIME_INTERVAL_COMPARATOR);
		for (List<Interval> list : lists) {
			totalSpace.addAll(list);
		}
		return mergeIntervalSortedSet(totalSpace);
	}

	public static List<Interval> mergeIntervalLists(Interval... intervals) {
		SortedSet<Interval> totalSpace = new TreeSet<Interval>(START_TIME_INTERVAL_COMPARATOR);
		for (Interval interval : intervals) {
			totalSpace.add(interval);
		}
		return mergeIntervalSortedSet(totalSpace);
	}

	private static List<Interval> mergeIntervalSortedSet(SortedSet<Interval> totalSpace) {
		List<Interval> result = new ArrayList<Interval>();
		Interval current = null;
		for (Interval interval : totalSpace) {
			if (current == null) {
				current = interval;
			} else if (!current.overlaps(interval)) {
				result.add(current);
				current = interval;
			} else {
				current = mergeIntervals(current, interval);
			}
		}
		if (current != null) {
			result.add(current);
		}
		return result;
	}

	/*
	 * Returns the smallest interval that can contain both interval arguments.
	 * 
	 * @return An {@link Interval}
	 */
	public static Interval mergeIntervals(Interval i1, Interval i2) {
		DateTime start = i1.getStart().isBefore(i2.getStart()) ? i1.getStart() : i2.getStart();
		DateTime end = i1.getEnd().isAfter(i2.getEnd()) ? i1.getEnd() : i2.getEnd();
		return new Interval(start, end);
	}

	private static final Comparator<Interval> START_TIME_INTERVAL_COMPARATOR = new Comparator<Interval>() {
		@Override
		public int compare(Interval i1, Interval i2) {
			return i1.getStart().equals(i2.getStart()) ? i1.getEnd().compareTo(i2.getEnd()) : i1.getStart().compareTo(
					i2.getStart());
		}
	};
}