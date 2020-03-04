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

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by conforti on 5/02/2016.
 */
public class TraceToString {

    private static final XTimeExtension xte = XTimeExtension.instance();

    public static String convertXTraceToString(XTrace trace, NameExtractor nameExtractor, String skipLabel, Comparator<String> comparator) {
        List<String> labels = new ArrayList<>(trace.size());
        for (int i = 0; i < trace.size(); i++) {
            String name = nameExtractor.getEventName(trace.get(i));
            if(!name.equals(skipLabel)) {
                labels.add(name);
            }
        }
        labels = sort(labels, comparator);

        return listToString(labels);
    }

    public static String convertXTraceToString(XTrace trace, NameExtractor nameExtractor, String skipLabel, Map<String, Set<String>> parallel) {
        List<String> labels = new ArrayList<>(trace.size());
        for (int i = 0; i < trace.size(); i++) {
            String name = nameExtractor.getEventName(trace.get(i));
            if(!name.equals(skipLabel)) {
                labels.add(name);
            }
        }
        labels = sort(labels, parallel);

        return listToString(labels);
    }

    public static String convertXTraceToString(XTrace trace, NameExtractor nameExtractor, Comparator<String> comparator) {
        List<String> labels = new ArrayList<>(trace.size());
        for (int i = 0; i < trace.size(); i++) {
            labels.add(nameExtractor.getEventName(trace.get(i)));
        }
        labels = sort(labels, comparator);

        return listToString(labels);
    }

    public static String convertXTraceToString(XTrace trace, NameExtractor nameExtractor, Map<String, Set<String>> parallel) {
        List<String> labels = new ArrayList<>(trace.size());
        for (int i = 0; i < trace.size(); i++) {
            labels.add(nameExtractor.getEventName(trace.get(i)));
        }
        labels = sort(labels, parallel);

        return listToString(labels);
    }

    private static List sort(List<String> list, Comparator<String> comparator) {
        List<String> sorted = new ArrayList<>(list.size());
        int last = 0;
        for(int i = 0; i < list.size(); i++) {
            sorted.add(list.get(i));
            if(i != 0) {
                if(comparator.compare(sorted.get(last), sorted.get(i)) == 0) {
                    if(last != i - 1) {
                        Collections.sort(sorted.subList(last, i), comparator);
                    }
                    last = i;
                }
            }
        }
        return sorted;
    }

    private static List sort(List<String> list, Map<String, Set<String>> parallel) {
        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Set<String> concurrent = null;
                if((concurrent = parallel.get(o1)) != null) {
                    if(concurrent.contains(o2)) {
                        return o1.compareTo(o2);
                    }
                }
                return 0;
            }
        };

        return sort(list, comparator);
    }

    public static String convertXTraceToString(XTrace trace, NameExtractor nameExtractor) {
        List<String> labels = new ArrayList<>(trace.size());
        for (int i = 0; i < trace.size(); i++) {
            labels.add(nameExtractor.getEventName(trace.get(i)));
        }
        return listToString(labels);
    }

    public static String[] convertXTraceToListOfString(XTrace trace, NameExtractor nameExtractor) {
        String[] labels = new String[trace.size()];
        for (int i = 0; i < trace.size(); i++) {
            labels[i] = nameExtractor.getEventName(trace.get(i));
        }
        return labels;
    }

    public static String convertXTraceToString(XTrace trace, Map<String, String> nameMap, NameExtractor nameExtractor) {
        List<String> labels = new ArrayList<>(trace.size());
        for (int i = 0; i < trace.size(); i++) {
            labels.add(nameMap.get(nameExtractor.getEventName(trace.get(i))));
        }
        return listToString(labels);
    }

    public static String[] convertXTraceToListOfString(XTrace trace, Map<String, String> nameMap, NameExtractor nameExtractor) {
        String[] labels = new String[trace.size()];
        for (int i = 0; i < trace.size(); i++) {
            labels[i] = nameMap.get(nameExtractor.getEventName(trace.get(i)));
        }
        return labels;
    }

    public static String convertXTraceToStringWithTimestamp(XTrace trace, NameExtractor nameExtractor, SimpleDateFormat simpleDateFormat) {
        List<String> labels = new ArrayList<>(trace.size());
        for (int i = 0; i < trace.size(); i++) {
            labels.add(nameExtractor.getEventName(trace.get(i)) + simpleDateFormat.format(xte.extractTimestamp(trace.get(i))));
        }
        return  listToString(labels);
    }

    public static String listToString(IntArrayList list, Comparator<Integer> comparator) {
        List<Integer> labels = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            labels.add(list.get(i));
        }
        labels = sortIntList(labels, comparator);

        return intListToString(labels);
    }

    public static String listToString(IntArrayList list, int skipLabel, Comparator<Integer> comparator) {
        List<Integer> labels = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            int name = list.get(i);
            if(name != skipLabel) {
                labels.add(name);
            }
        }
        labels = sortIntList(labels, comparator);

        return intListToString(labels);
    }

    public static String intListToString(List<Integer> list) {
        StringBuffer sb = new StringBuffer();
        for(int event : list) {
            sb.append(event).append(", ");
        }
        return sb.toString();
    }

    public static String listToString(List<String> list) {
        StringBuffer sb = new StringBuffer();
        for(String event : list) {
            sb.append(event).append(", ");
        }
        return sb.toString();
    }

    private static List sortIntList(List<Integer> list, Comparator<Integer> comparator) {
        List<Integer> sorted = new ArrayList<>(list.size());
        int last = 0;
        for(int i = 0; i < list.size(); i++) {
            sorted.add(list.get(i));
            if(i != 0) {
                if(comparator.compare(sorted.get(last), sorted.get(i)) == 0) {
                    if(last != i - 1) {
                        Collections.sort(sorted.subList(last, i), comparator);
                    }
                    last = i;
                }
            }
        }
        return sorted;
    }
}
