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

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.*;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.Map;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 16/02/15.
 */
public class LogCloner {

    private final Map<String, String> cachedXAttributeName = new UnifiedMap<>();
    private final Map<String, UnifiedMap<String, String>> cachedLiteralAttribute = new UnifiedMap<>();
//    private final Map<String, UnifiedMap<Date, Date>> cachedTimestampAttribute = new UnifiedMap<>();
    private final Map<String, UnifiedMap<Double, Double>> cachedContinuousAttribute = new UnifiedMap<>();
    private final Map<String, UnifiedMap<Long, Long>> cachedDiscreteAttribute = new UnifiedMap<>();
    private final XFactory factory;

    public LogCloner() {
//        factory = new XFactoryMemoryImpl();
        factory = new XFactoryNaiveImpl();
    }

    public LogCloner(XFactory factory) {
        this.factory = factory;
    }

    public XLog cloneLog(XLog log) {
        XLog newLog = factory.createLog(log.getAttributes());
        for (XTrace trace : log) {
            XTrace newTrace = getXTrace(trace);
            newLog.add(newTrace);
        }

        return newLog;
    }

    public String getXAttributeName(String attributeName) {
        String attName = attributeName;
        if ((attName = cachedXAttributeName.get(attName)) == null) {
            attName = attributeName;
            cachedXAttributeName.put(attName, attName);
        }
        return attName;
    }

    public XAttribute getXAttribute(String attributeName, XAttribute attribute) {

        String attName = getXAttributeName(attributeName);

        XAttribute att;
        if (attribute instanceof XAttributeLiteral) {
            String attValue = ((XAttributeLiteral) attribute).getValue();
            UnifiedMap<String, String> map;
            if ((map = cachedLiteralAttribute.get(attName)) == null) {
                map = new UnifiedMap<>();
                cachedLiteralAttribute.put(attName, map);
            }
            if ((attValue = map.get(attValue)) == null) {
                attValue = ((XAttributeLiteral) attribute).getValue();
                map.put(attValue, attValue);
            }
            att = factory.createAttributeLiteral(attName, attValue, null);
//        } else if (attribute instanceof XAttributeTimestamp) {
//            Date attValue = ((XAttributeTimestamp) attribute).getValue();
//            UnifiedMap<Date, Date> map;
//            if ((map = cachedTimestampAttribute.get(attName)) == null) {
//                map = new UnifiedMap<>();
//                cachedTimestampAttribute.put(attName, map);
//            }
//            if ((attValue = map.get(attValue)) == null) {
//                attValue = ((XAttributeTimestamp) attribute).getValue();
//                map.put(attValue, attValue);
//            }
//            att = new XAttributeTimestampImpl(attName, attValue);
        } else if (attribute instanceof XAttributeContinuous) {
            Double attValue = ((XAttributeContinuous) attribute).getValue();
            UnifiedMap<Double, Double> map;
            if ((map = cachedContinuousAttribute.get(attName)) == null) {
                map = new UnifiedMap<>();
                cachedContinuousAttribute.put(attName, map);
            }
            if ((attValue = map.get(attValue)) == null) {
                attValue = ((XAttributeContinuous) attribute).getValue();
                map.put(attValue, attValue);
            }
            att = factory.createAttributeContinuous(attName, attValue, null);
        } else if (attribute instanceof XAttributeDiscrete) {
            Long attValue = ((XAttributeDiscrete) attribute).getValue();
            UnifiedMap<Long, Long> map;
            if ((map = cachedDiscreteAttribute.get(attName)) == null) {
                map = new UnifiedMap<>();
                cachedDiscreteAttribute.put(attName, map);
            }
            if ((attValue = map.get(attValue)) == null) {
                attValue = ((XAttributeDiscrete) attribute).getValue();
                map.put(attValue, attValue);
            }
            att = factory.createAttributeDiscrete(attName, attValue, null);
        } else {
            att = attribute;
        }

        return att;

    }

    public XEvent getXEvent(XEvent event) {
        XEvent newEvent = factory.createEvent();
        for (Map.Entry<String, XAttribute> entry : event.getAttributes().entrySet()) {
            String attName = getXAttributeName(entry.getKey());

            XAttribute att = getXAttribute(entry.getKey(), entry.getValue());

            newEvent.getAttributes().put(attName, att);
        }

        return newEvent;
    }

    public XTrace getXTrace(XTrace trace) {
        XTrace newTrace = factory.createTrace(trace.getAttributes());
        for (XEvent event : trace) {
            XEvent newEvent = getXEvent(event);
            newTrace.add(newEvent);
        }
        return newTrace;
    }

}
