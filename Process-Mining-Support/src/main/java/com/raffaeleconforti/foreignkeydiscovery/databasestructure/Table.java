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
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Raffaele Conforti on 14/10/14.
 */
public class Table implements Comparable<Table> {

    String tableName;
    TreeSet<Column> setOfColumns;
    Column[] arrayColumns;
    Integer hashCode;

    public Table(String tableName, Set<Column> setOfColumns) {
        this.tableName = tableName;
        this.setOfColumns = new TreeSet<Column>(setOfColumns);
        arrayColumns = this.setOfColumns.toArray(new Column[this.setOfColumns.size()]);
    }

    public TreeSet<Column> getSetOfColumns() {
        return setOfColumns;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Table) {
            Table t = (Table) o;
            if(t.tableName.equals(tableName) && t.setOfColumns.size() == setOfColumns.size()) {
                for(int i = 0; i < setOfColumns.size(); i++) {
                    if(!t.arrayColumns[i].equals(arrayColumns[i])) {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        if(hashCode == null) {
            hashCode = (tableName+Arrays.toString(arrayColumns)).hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TableName: ");
        sb.append(tableName);
        sb.append("\nColumns: ");
        for(Column c : setOfColumns) {
            sb.append(c.toString());
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public int compareTo(Table table) {
        if(tableName.equals(table.tableName)) {
            if(setOfColumns.size() == table.setOfColumns.size()) {
                for(int i = 0; i < setOfColumns.size(); i++) {
                    int comp = arrayColumns[i].compareTo(table.arrayColumns[i]);
                    if(comp != 0) return comp;
                }
                return 0;
            }
            return Integer.valueOf(setOfColumns.size()).compareTo(table.setOfColumns.size());
        }
        return tableName.compareTo(table.tableName);

    }

}
