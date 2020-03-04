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
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 16/02/15.
 */
public class NameExtractor {

    private final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
    private final XConceptExtension xce = XConceptExtension.instance();
    private final XEventClassifier xEventClassifier;

    public NameExtractor(XEventClassifier xEventClassifier) {
        this.xEventClassifier = xEventClassifier;
    }

    public String getEventName(XEvent event) {
        return getString(xEventClassifier.getClassIdentity(event));
    }

    public String getTraceName(XTrace trace) {
        return getString(xce.extractName(trace));
    }

    private String getString(String o) {
        String result;
        if((result = map.get(o)) == null) {
            map.put(o, o);
            result = o;
        }
        return result;
    }
}
