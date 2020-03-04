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

package com.raffaeleconforti.foreignkeydiscovery.databasestructure;

import java.util.Arrays;

/**
 * Created by Raffaele Conforti on 14/10/14.
 */
public class ColumnValues implements Comparable<ColumnValues> {

    private ColumnType columnType;
    private String[] values;
    private int[] hashCodes;
    private Integer hashCode;
    private String toString;

    public ColumnValues(ColumnType columnType, String[] values) {
        this.columnType = columnType;
        this.values = values;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public String[] getValues() {
        return values;
//        return Arrays.copyOf(values, values.length);
    }

    public int[] getHashCodes() {
        if(hashCodes == null) {
            this.hashCodes = new int[values.length];
            for(int i = 0; i < values.length; i++) {
                hashCodes[i] = values[i].hashCode();
            }
        }
        return Arrays.copyOf(hashCodes, hashCodes.length);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof ColumnValues) {
            ColumnValues cv = (ColumnValues) o;
            if(this.hashCode() == cv.hashCode()) return Arrays.equals(values, cv.getValues());
            else return false;
        }
        return false;
    }

    @Override
    public String toString() {
        if(toString == null) {
            toString = "ColumnType: " + columnType + "\nValues: \n" + Arrays.toString(values);
        }
        return toString;
    }

    @Override
    public int hashCode() {
        if(hashCode == null) {
            hashCode = Arrays.hashCode(values);
        }
        return hashCode;
    }

    @Override
    public int compareTo(ColumnValues o) {
        if(this == o) return 0;
        if(o.getHashCodes().length == this.getHashCodes().length) {
            for(int i = 0; i < this.getHashCodes().length; i++) {
                if(getHashCodes()[i] < o.getHashCodes()[i]) return -1;
                else if(getHashCodes()[i] > o.getHashCodes()[i]) return 1;
            }
            return 0;
        }else if(getHashCodes().length < o.getHashCodes().length) return -1;
        else return 1;
    }
}
