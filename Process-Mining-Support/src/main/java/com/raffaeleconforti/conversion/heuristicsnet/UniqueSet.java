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

package com.raffaeleconforti.conversion.heuristicsnet;

import org.processmining.framework.models.heuristics.HNSubSet;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 9/03/2016.
 */
public class UniqueSet {
    private HNSubSet set;
    private int id;
    private boolean in;

    public UniqueSet(HNSubSet set, int id, boolean in) {
        this.set = set;
        this.id = id;
        this.in = in;
    }

    public boolean equals(Object o) {
        if (!(o instanceof UniqueSet)) {
            return false;
        }

        UniqueSet s = (UniqueSet) o;
        return (s.set.equals(set)) && (s.id == id) && (s.in == in);

    }

    public String toString() {
        return set.toString();
    }

}