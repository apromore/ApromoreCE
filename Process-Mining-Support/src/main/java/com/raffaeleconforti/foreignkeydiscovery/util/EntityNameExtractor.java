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

package com.raffaeleconforti.foreignkeydiscovery.util;

import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.Attribute;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.Entity;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 9/03/2016.
 */
public class EntityNameExtractor {

    public static Set<String> getEntityName(Entity entity) {

        String name = entity.getName();

        name = name.replace(" ", "");
        name = name.substring(1, name.length() - 1);

        Set<String> result = new UnifiedSet<String>();

        StringTokenizer st = new StringTokenizer(name, ",;");
        if (name.contains(",") || name.contains(";")) {
            while (st.hasMoreTokens()) {
                result.add(st.nextToken());
            }
        } else {
            result.add(name);
        }

        return result;

    }

    public static Set<String> evTypeNames(Entity e) {
        Set<String> names = new UnifiedSet<String>();
        List<Attribute> TS = e.getTimestamps();
        for (Attribute ts : TS) {
            names.add(ts.getName());
        }
        return names;
    }
}
