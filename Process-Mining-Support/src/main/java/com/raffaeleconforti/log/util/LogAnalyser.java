/*
 *  Copyright (C) 2018 Raffaele Conforti (www.raffaeleconforti.com)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.raffaeleconforti.log.util;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 20/03/2016.
 */
public class LogAnalyser {

    private static final XConceptExtension xce = XConceptExtension.instance();
    private static final XLifecycleExtension xle = XLifecycleExtension.instance();
    private static final XOrganizationalExtension xoe = XOrganizationalExtension.instance();
    private static final XTimeExtension xte = XTimeExtension.instance();

    public static final int SECONDS = 0;
    public static final int MINUTES = 1;
    public static final int HOURS = 2;
    public static final int DAYS = 3;
    public static final int WEEKS = 4;
    public static final int MONTHS = 5;
    public static final int YEARS = 6;

    public static double[] measureTimePerformance(Collection<XTrace> traces, int scale) {
        double[] performance = new double[traces.size()];

        int pos = 0;
        for (XTrace trace : traces) {
            long start = Long.MAX_VALUE;
            long end = Long.MIN_VALUE;

//            for(XEvent event : trace) {
            start = Math.min(start, xte.extractTimestamp(trace.get(0)).getTime());
            end = Math.max(end, xte.extractTimestamp(trace.get(trace.size() - 1)).getTime());
//            }

            performance[pos] = scale(end - start, scale);
            pos++;
        }

        return performance;
    }

    public static String getScale(int scale) {
        if (scale == SECONDS) return "seconds";
        else if (scale == MINUTES) return "minutes";
        else if (scale == HOURS) return "hours";
        else if (scale == DAYS) return "days";
        else if (scale == WEEKS) return "weeks";
        else if (scale == MONTHS) return "months";
        else if (scale == YEARS) return "years";
        return "";
    }

    private static double scale(long l, int scale) {
        double scaling = 1;

        if (scale == SECONDS) scaling = 1000;
        else if (scale == MINUTES) scaling = 60 * 1000;
        else if (scale == HOURS) scaling = 60 * 60 * 1000;
        else if (scale == DAYS) scaling = 24 * 60 * 60 * 1000;
        else if (scale == WEEKS) scaling = 7 * 24 * 60 * 60 * 1000;
        else if (scale == MONTHS) scaling = 4 * 7 * 24 * 60 * 60 * 1000;
        else if (scale == YEARS) scaling = 12 * 4 * 7 * 24 * 60 * 60 * 1000;

        return l / scaling;
    }

    public static int countEvents(XLog log) {
        int events = 0;
        for(XTrace trace : log) {
            events += countEvents(trace);
        }
        return events;
    }

    public static int countEvents(XTrace trace) {
        return trace.size();
    }

    public static int countTraces(XLog log) {
       return log.size();
    }

    public static int countUniqueActivities(XLog log, XEventClassifier eventClassifier) {
        return getUniqueActivities(log, eventClassifier).size();
    }

    public static Map<String, Integer> getFinalActivityFriquencies(XLog log) {
        Map<String, Integer> finalActivityFrequencies = new UnifiedMap<>();
        for(XTrace trace : log) {
            String activity = getFinalActivity(trace);
            Integer frequency;
            if((frequency = finalActivityFrequencies.get(activity)) == null) {
                frequency = 0;
            }
            frequency++;
            finalActivityFrequencies.put(activity, frequency);
        }
        return finalActivityFrequencies;
    }

    public static Set<String> getFinalActivities(XLog log) {
        return getFinalActivityFriquencies(log).keySet();
    }

    public static String getFinalActivity(XTrace trace) {
        return xce.extractName(trace.get(trace.size() - 1));
    }

    public static Map<String, Integer> getInitialActivityFriquencies(XLog log) {
        Map<String, Integer> initialActivityFrequencies = new UnifiedMap<>();
        for(XTrace trace : log) {
            String activity = getInitialActivity(trace);
            Integer frequency;
            if((frequency = initialActivityFrequencies.get(activity)) == null) {
                frequency = 0;
            }
            frequency++;
            initialActivityFrequencies.put(activity, frequency);
        }
        return initialActivityFrequencies;
    }

    public static Set<String> getInitialActivities(XLog log) {
        return getInitialActivityFriquencies(log).keySet();
    }

    public static String getInitialActivity(XTrace trace) {
        return xce.extractName(trace.get(0));
    }

    public static Set<String> getUniqueActivities(XLog log, XEventClassifier eventClassifier) {
        Set<String> uniqueActivities = new UnifiedSet<>();
        for(XTrace trace : log) {
            uniqueActivities.addAll(getUniqueActivities(trace, eventClassifier));
        }
        return uniqueActivities;
    }

    public static Set<String> getUniqueActivities(XTrace trace, XEventClassifier eventClassifier) {
        Set<String> uniqueActivities = new UnifiedSet<>();
        for(XEvent event : trace) {
            uniqueActivities.add(eventClassifier.getClassIdentity(event));
        }
        return uniqueActivities;
    }

}
