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

package com.raffaeleconforti.foreignkeydiscovery.histogram;

import com.raffaeleconforti.foreignkeydiscovery.Cell;
import com.raffaeleconforti.foreignkeydiscovery.databasestructure.Tuple;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.Set;

/**
 * Created by Raffaele Conforti on 17/10/14.
 */
public interface Histogram {

    UnifiedMap<Cell, Double> getHistogram();
    UnifiedMap<Cell, Set<Tuple<String>>> getQuantileHistogram();
    int getQuantiles();

}
