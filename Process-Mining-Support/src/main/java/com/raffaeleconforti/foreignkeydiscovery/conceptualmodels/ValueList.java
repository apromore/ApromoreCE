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

package com.raffaeleconforti.foreignkeydiscovery.conceptualmodels;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Defines a list of values belonging to the same attribute with a pointer
 * to the current value which is used in testing inclusion dependencies.
 *
 * @author Viara Popova
 * @author Modified by Raffaele Conforti
 */
public class ValueList {
    private int current = 0;
    private ArrayList<String> values;

    public ValueList() {
        values = new ArrayList<String>();
    }

    public void addValue(String val) {

        int i = 0;
        if (values.size() == 0) {
            values.add(val);
        } else {
            ListIterator<String> itr = values.listIterator();
            while (itr.hasNext()) {
                String v = itr.next();
                int comp = v.compareTo(val); //compares lexicographically
                if (comp > 0) {    //insert value before current element
                    values.add(i, val);
                    break;
                }
                if (comp == 0) {
                    break; //value already in the array
                }
                if (!itr.hasNext()) {
                    values.add(val);
                    break;
                }
                i++;
            }
        }
    }

    public Boolean movePointer() {
        if (current < values.size() - 1) {
            current++;
            return true;
        } else return false;
    }

    public void resetPointer() {
        current = 0;
    }

    public Boolean lastPlace() {
        return current == values.size();
    }

    public String getValue() {
        return values.get(current);
    }

    public int getSize() {
        return values.size();
    }

}
