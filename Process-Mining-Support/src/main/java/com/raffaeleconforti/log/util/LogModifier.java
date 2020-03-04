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

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.*;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.*;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 16/02/15.
 */
public class LogModifier {

    private XFactory factory = null;
    private XConceptExtension xce = null;
    private XTimeExtension xte = null;
    private Map<Object, Object> map = null;

    public static void main(String[] args) throws Exception {
        XFactory factory = new XFactoryNaiveImpl();
        XLog log = LogImporter.importFromFile(factory, "/Volumes/Data/SharedFolder/Logs/Sepsis Cases.xes.gz");

        for(XTrace trace : log) {
            boolean violation = checkViolation(trace);
            XEvent event = factory.createEvent();
            String name;
            if(violation) {
                name = "Bad";
            }else {
                name = "Good";
            }

            XConceptExtension.instance().assignName(event, name);
            trace.add(event);
        }

        LogImporter.exportToFile("/Volumes/Data/SharedFolder/Logs/Sepsis Cases_label.xes.gz", log);
    }

    private static boolean checkViolation(XTrace trace) {
        boolean tested = false;
        boolean tested_positive = false;
        boolean treated = false;
        for (int i = 0; i < trace.size(); i++) {
            XEvent current_event = trace.get(i);
            String current_name = XConceptExtension.instance().extractName(current_event);
            if ("Leucocytes".equalsIgnoreCase(current_name)) {
                tested = true;
                XAttribute attribute = current_event.getAttributes().get("Leucocytes");
                if(attribute != null) {
                    Double leucocyte = ((XAttributeContinuous) attribute).getValue();
                    if (leucocyte < 20 || leucocyte > 40) {
                        tested_positive = true;
                    }
                }
            }else if ("CRP".equalsIgnoreCase(current_name)) {
                tested = true;
                XAttribute attribute = current_event.getAttributes().get("CRP");
                if(attribute != null) {
                    Double crp = ((XAttributeContinuous) attribute).getValue();
                    if (crp > 100) {
                        tested_positive = true;
                    }
                }
            }else if ("LacticAcid".equalsIgnoreCase(current_name)) {
                tested = true;
                XAttribute attribute = current_event.getAttributes().get("LacticAcid");
                if(attribute != null) {
                    Double lactic_acid = ((XAttributeContinuous) attribute).getValue();
                    if (lactic_acid > 4) {
                        tested_positive = true;
                    }
                }
            }else {
                if("IV Liquid".equalsIgnoreCase(current_name) ||
                        "IV Antibiotics".equalsIgnoreCase(current_name) ||
                        "Admission IC".equalsIgnoreCase(current_name) ||
                        "Admission NC".equalsIgnoreCase(current_name)
                ) {
                    treated = true;
                }
            }
        }
        return (tested_positive && !treated) || (tested && !tested_positive) || (!tested_positive && treated);
    }

    public static void main1(String[] args) throws Exception {
        XFactory factory = new XFactoryNaiveImpl();
        XLog log = LogImporter.importFromFile(factory, "/Volumes/Data/SharedFolder/Logs/SIAE.xes.gz");

        for(XTrace trace : log) {
            boolean violation = checkViolation(trace);
            XEvent event = factory.createEvent();
            String name;
            if(violation) {
                name = "Bad";
            }else {
                name = "Good";
            }

            XConceptExtension.instance().assignName(event, name);
            trace.add(event);
        }

        LogImporter.exportToFile("/Volumes/Data/SharedFolder/Logs/SIAE_label.xes.gz", log);
    }

    private static boolean checkViolation1(XTrace trace) {
        for(int i = 0; i < trace.size() - 1; i++) {
            String current_name = XConceptExtension.instance().extractName(trace.get(i));
            String next_name = XConceptExtension.instance().extractName(trace.get(i + 1));
            if("Annullamento Permesso".equalsIgnoreCase(current_name)) {
                if("Associazione Evento a Permesso".equalsIgnoreCase(next_name)) {
                    return true;
                }
            }
            if("Associazione Evento a Permesso".equalsIgnoreCase(current_name)) {
                if("Validazione Dichiarazione".equalsIgnoreCase(next_name)) {
                    return true;
                }else if("Validazione Distinta".equalsIgnoreCase(next_name)) {
                    return true;
                }
            }
            if("Validazione Dichiarazione".equalsIgnoreCase(current_name)) {
                if("Associazione Evento a Gratuito".equalsIgnoreCase(next_name)) {
                    return true;
                }
            }
            if("Validazione Gratuito".equalsIgnoreCase(current_name)) {
                if("Associazione Evento a Dichiarazione".equalsIgnoreCase(next_name)) {
                    return true;
                }
            }
        }
        return false;
    }

    private final Comparator<XEvent> comparatorXEvent = new Comparator<XEvent>() {
        @Override
        public int compare(XEvent o1, XEvent o2) {
            Date d1 = xte.extractTimestamp(o1);
            Date d2 = xte.extractTimestamp(o2);
            return d1.compareTo(d2);
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    };

    private Comparator<XTrace> comparatorXTrace = new Comparator<XTrace>() {
        @Override
        public int compare(XTrace o1, XTrace o2) {
            String s1 = xce.extractName(o1);
            String s2 = xce.extractName(o2);
            return s1.compareTo(s2);
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    };

    public LogModifier(XFactory factory, XConceptExtension xce, XTimeExtension xte, LogOptimizer logOptimizer) {
        this.factory = factory;
        this.xce = xce;
        this.xte = xte;
        map = logOptimizer.getReductionMap();
    }

    public XLog insertArtificialStartAndEndEvent(XLog log) {

        for (XTrace trace : log) {
            XEvent start = factory.createEvent();
            xce.assignName(start, "ArtificialStartEvent");
            xte.assignTimestamp(start, new Date(Long.MIN_VALUE));

            XEvent end = factory.createEvent();
            xce.assignName(end, "ArtificialEndEvent");
            xte.assignTimestamp(end, new Date(Long.MAX_VALUE));

            trace.add(0, start);
            trace.add(trace.size(), end);
        }
        return log;

    }

    public XLog removeArtificialStartAndEndEvent(XLog log) {

        for (XTrace trace : log) {
            Set<XEvent> remove = new UnifiedSet<>();
            for (XEvent event : trace) {
                if (xce.extractName(event).contains("ArtificialStartEvent")) {
                    remove.add(event);
                }

                if (xce.extractName(event).contains("ArtificialEndEvent")) {
                    remove.add(event);
                }
            }

            for (XEvent event : remove) {
                trace.remove(event);
            }
        }
        return log;

    }

    public XLog sortLog(XLog log) {
        XLog newLog = factory.createLog(log.getAttributes());
        List<XEvent> events;
        for (XTrace trace : log) {
            events = new ArrayList<>();
            for (XEvent event : trace) {
                events.add(event);
            }
            Collections.sort(events, comparatorXEvent);
            XTrace newTrace = factory.createTrace(trace.getAttributes());

            for (XEvent event : events) {
                newTrace.add(event);
            }
            newLog.add(newTrace);
        }
        return newLog;
    }

    public boolean sameEvent(XEvent event, XEvent e, boolean logOptimizerUsed) {
        for (Map.Entry<String, XAttribute> entry : event.getAttributes().entrySet()) {
            if(logOptimizerUsed) {
                if (entry.getValue() != e.getAttributes().get(entry.getKey())) {
                    return false;
                }
            }else {
                if (!getAttributeValue(entry.getValue()).equals(getAttributeValue(e.getAttributes().get(entry.getKey())))) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getAttributeValue(XAttribute attribute) {
        String value = null;
        if (attribute instanceof XAttributeBoolean) {
            value = Boolean.toString(((XAttributeBoolean) attribute).getValue());
        } else if (attribute instanceof XAttributeContinuous) {
            value = Double.toString(((XAttributeContinuous) attribute).getValue());
        } else if (attribute instanceof XAttributeDiscrete) {
            value = Long.toString(((XAttributeDiscrete) attribute).getValue());
        } else if (attribute instanceof XAttributeLiteral) {
            value = ((XAttributeLiteral) attribute).getValue();
        } else if (attribute instanceof XAttributeTimestamp) {
            value = ((XAttributeTimestamp) attribute).getValue().toString();
        }
        return getObject(value);
    }


    private String getObject(String o) {
        String result = null;
        if(o != null && (result = (String) map.get(o)) == null) {
            map.put(o, o);
            result = o;
        }
        return result;
    }

}
