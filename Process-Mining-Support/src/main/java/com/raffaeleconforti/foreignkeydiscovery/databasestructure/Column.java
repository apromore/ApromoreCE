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

/**
 * Created by Raffaele Conforti on 14/10/14.
 */
public class Column implements Comparable<Column>{

    private String table;
    private String columnName;
    private ColumnValues columnValues;
    private Integer hashCode;
    private String toString;

    public Column(String columnName, ColumnValues columnValues, String table) {
        this.columnName = columnName;
        this.columnValues = columnValues;
        this.table = table;
    }

    public String getTable() {return  table;}

    public String getColumnName() {
        return columnName;
    }

    public ColumnValues getColumnValues() {
        return columnValues;
    }

    public String toString() {
        if(toString == null) {
            toString = "Colomn Name: " + columnName + "\nTable:\n" + table + "\nColumn Values:\n" + columnValues.toString();
        }
        return toString;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Column) {
            Column c = (Column) o;
            if(this.hashCode() == c.hashCode()) return table.equals(c.table) && columnName.equals(c.columnName) && columnValues.equals(c.columnValues);
            return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if(hashCode == null) {
            hashCode = (table + "\n" + columnName + "\n" + columnValues.toString()).hashCode();
        }
        return hashCode;
    }

    @Override
    public int compareTo(Column o) {

        if(this == o) return 0;
        if(table.equals(o.table)) {
            if (columnName.equals(o.columnName)) {
                return columnValues.compareTo(o.columnValues);
            }
            return columnName.compareTo(o.columnName);
        }
        return table.compareTo(o.table);
    }
}
