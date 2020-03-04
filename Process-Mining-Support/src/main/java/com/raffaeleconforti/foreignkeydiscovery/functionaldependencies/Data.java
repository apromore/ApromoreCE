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

package com.raffaeleconforti.foreignkeydiscovery.functionaldependencies;


import org.deckfour.xes.model.XAttribute;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.ArrayList;


/**
 * Data structure for moving data from parser to tane algorithm
 * Contains the data for one event type
 *
 * @author Tanel Teinemaa, Viara Popova
 */
public class Data {

    public String title;

    public String dataType;

    public String[] columnTitles;

    public ArrayList<XAttribute> timestamps;

    public ArrayList<UnifiedMap<String, XAttribute>> table;

    public ArrayList<UnifiedSet<String>> keys;

    public UnifiedSet<String> primaryKey;

    public Data() {
        table = new ArrayList<UnifiedMap<String, XAttribute>>();
        timestamps = new ArrayList<XAttribute>();
    }

    public String getDataType() {
        return dataType;
    }

}
