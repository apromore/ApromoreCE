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

package com.raffaeleconforti.noisefiltering.event.noise;

import com.raffaeleconforti.log.util.LogCloner;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by conforti on 8/02/15.
 */

public class NoiseGenerator {

    private final XLog log;
    private String[] labels;
    private int numberOfEvents = 0;
    private final XConceptExtension xce = XConceptExtension.instance();
    private final XTimeExtension xte = XTimeExtension.instance();
    private final XLifecycleExtension xle = XLifecycleExtension.instance();
    private final XFactory factory = new XFactoryNaiveImpl();
    private final Map<String, Set<String>> directDependencies = new UnifiedMap<String, Set<String>>();
    private final Random random = new Random(123456789);

    public NoiseGenerator(XLog log) {
        this.log = log;
        discoverDependencies(log);
        populateLabels(log);
    }

    private void discoverDependencies(XLog log) {
        for(XTrace trace : log) {
            for(int i = 0; i < trace.size(); i++) {
                String name = xce.extractName(trace.get(i));
                if(i + 1 < trace.size()) {
                    String successor = xce.extractName(trace.get(i + 1));
                    Set<String> successors = null;
                    if((successors = directDependencies.get(name)) == null) {
                        successors = new UnifiedSet<String>();
                        directDependencies.put(name, successors);
                    }
                    successors.add(successor);
                }
            }
        }
    }

    private void populateLabels(XLog log) {
        Set<String> set = new UnifiedSet<String>();
        for(XTrace trace : log) {
            for(XEvent event : trace) {
                set.add(xce.extractName(event));
                numberOfEvents++;
            }
        }
        labels = set.toArray(new String[set.size()]);
    }

    public XLog insertNoise(double percentageNoise) {
        LogCloner logCloner = new LogCloner();
        XLog result = logCloner.cloneLog(log);

        int inserted = 0;
        long needed = Math.round((numberOfEvents * percentageNoise) / (1.0 - percentageNoise));
        
        while(inserted < needed) {
            int tracePos = random.nextInt(result.size());
            XTrace trace = result.get(tracePos);

            if(trace.size() > 0) {
                int eventPos = 0;
                while(eventPos == 0) {
                    eventPos = random.nextInt(trace.size());
                }

                int label = random.nextInt(labels.length);

                XEvent event = factory.createEvent();
                xce.assignName(event, labels[label]);

                event.getAttributes().put("noise", factory.createAttributeBoolean("noise", true, null));
                xle.assignStandardTransition(event, XLifecycleExtension.StandardModel.COMPLETE);

                boolean pre = false;
                if(eventPos - 1 < trace.size() && eventPos > 0) {
                    String name = xce.extractName(trace.get(eventPos - 1));
                    if (directDependencies.get(name) != null && !directDependencies.get(name).contains(labels[label])) {
                        pre = true;
                    }
                }

                boolean post = false;
                String name = xce.extractName(trace.get(eventPos));
                if (directDependencies.get(labels[label]) != null && !directDependencies.get(labels[label]).contains(name)) {
                    post = true;
                }

                if(pre || post) {
                    Date date1 = xte.extractTimestamp(trace.get(eventPos - 1));
                    Date date2 = xte.extractTimestamp(trace.get(eventPos));
                    Date date3 = new Date(date2.getTime() - date1.getTime());
                    xte.assignTimestamp(event, date3);
                    trace.add(eventPos, event);
                    inserted++;
                }
            }
        }
        
        return result;
    }

    public XLog fixDate() {
        LogCloner logCloner = new LogCloner();
        XLog result = logCloner.cloneLog(log);

        for(int tracePos = 0; tracePos < result.size(); tracePos++) {
            XTrace trace = result.get(tracePos);
            for(int eventPos = 0; eventPos < trace.size(); eventPos++) {
                XEvent event = trace.get(eventPos);
                if(xte.extractTimestamp(event) == null) {
                    Date date1;
                    if(eventPos > 0) {
                        date1 = xte.extractTimestamp(trace.get(eventPos - 1));
                        if (date1 == null) {
                            date1 = fixDate(trace, eventPos - 1, eventPos - 2, eventPos);
                        }
                    }else {
                        date1 = new Date(0);
                    }

                    Date date2;
                    if(eventPos < trace.size() - 1) {
                        date2 = xte.extractTimestamp(trace.get(eventPos + 1));
                        if (date2 == null) {
                            date2 = fixDate(trace, eventPos + 1, eventPos, eventPos + 2);
                        }
                    }else {
                        date2 = new Date(Long.MAX_VALUE);
                    }
                    Date date3 = new Date((date2.getTime() + date1.getTime())/ 2);
                    xte.assignTimestamp(event, date3);
                }
            }
        }

        return result;
    }

    public Date fixDate(XTrace trace, int eventPos, int prePos, int postPos) {
        XEvent event = trace.get(eventPos);
        Date date1;
        if(prePos >= 0) {
            date1 = xte.extractTimestamp(trace.get(prePos));
            if (date1 == null) {
                date1 = fixDate(trace, prePos, prePos - 1, postPos);
            }
        }else {
            date1 = new Date(0);
        }

        Date date2;
        if(postPos < trace.size()) {
            date2 = xte.extractTimestamp(trace.get(postPos));
            if (date2 == null) {
                date2 = fixDate(trace, postPos, prePos, postPos + 1);
            }
        }else {
            date2 = new Date(Long.MAX_VALUE);
        }
        Date date3 = new Date((date2.getTime() + date1.getTime())/ 2);
        xte.assignTimestamp(event, date3);
        return date3;
    }
}
