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

import java.util.TreeSet;

/**
 * Created by Raffaele Conforti on 14/10/14.
 */
public class PrimaryKey implements Comparable<PrimaryKey>{

    private String name;
    private TreeSet<Column> columns;
    private Integer hashCode;

    public PrimaryKey(TreeSet<Column> columns){
        this.columns = columns;
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for(Column c : columns) {
            sb.append(c.getColumnName());
            sb.append(", ");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        sb.append(")");
        name = sb.toString();
    }

    public String getName() {
        return name;
    }

    public TreeSet<Column> getColumns() {
        return columns;
    }

    public Column getFirstColumn() {
        return columns.first();
    }

    public int getColumnsSize() {
        if(columns != null) return columns.size();
        else return 0;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof PrimaryKey) {
            PrimaryKey pk = (PrimaryKey) o;
            return columns.equals(pk.columns);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if(hashCode == null) {
            hashCode = columns.hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString() {

        return "P \n" + columns.toString();
    }

    @Override
    public int compareTo(PrimaryKey o) {
        if(columns.size() == o.columns.size()) {
            Column[] columns1 = columns.toArray(new Column[columns.size()]);
            Column[] columns2 = o.columns.toArray(new Column[o.columns.size()]);
            for(int i = 0; i < columns1.length; i++) {
                int compare = columns1[i].compareTo(columns2[i]);
                if(compare != 0) return compare;
            }
            return 0;
        }
        return Integer.valueOf(columns.size()).compareTo(o.columns.size());
    }
}
