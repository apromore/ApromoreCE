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

package com.raffaeleconforti.bpmnminer.preprocessing.synchtracegeneration;

import org.deckfour.xes.model.XAttribute;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.Iterator;

public class Event {
    private String name;
    private UnifiedMap<String, XAttribute> data;

    public Event(String n, UnifiedMap<String, XAttribute> d) {
        this.name = n;
        this.data = d;
    }

    public String getName() {
        return name;
    }

    public UnifiedMap<String, XAttribute> getData() {
        return data;
    }

    public String getConcept() {
        return name;
    }

    public XAttribute getTimestamp() {
        Iterator<String> dataItr = data.keySet().iterator();
        XAttribute timestamp = null;
        while (dataItr.hasNext()) {
            String dataAttr = dataItr.next();
            if (name.equals(dataAttr)) {
                timestamp = data.get(dataAttr);
            }
        }
        return timestamp;
    }
}
