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

package com.raffaeleconforti.foreignkeydiscovery;

import com.raffaeleconforti.foreignkeydiscovery.databasestructure.Column;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

/**
 * Created by Raffaele Conforti on 14/10/14.
 */
public class BottomKSketch {

    private Couple<String, Integer>[] couples;
    private Integer hashCode;

    public BottomKSketch(BottomKSketch bottomKSketch1, BottomKSketch bottomKSketch2) {
        Set<Couple<String, Integer>> set = new UnifiedSet<Couple<String, Integer>>();

        Collections.addAll(set, bottomKSketch1.couples);
        Collections.addAll(set, bottomKSketch2.couples);

        Couple<String, Integer>[] couples = set.toArray(new Couple[set.size()]);

        Arrays.sort(couples, new Comparator<Couple>() {
            @Override
            public int compare(Couple o1, Couple o2) {
                return o1.getSecondElement().compareTo(o2.getSecondElement());
            }
        });

        this.couples = couples;
    }

    public BottomKSketch(Set<Column> setOfColumns) {
        Column[] columns = setOfColumns.toArray(new Column[setOfColumns.size()]);
        int n = columns[0].getColumnValues().getValues().length;

        Couple<String, Integer>[] couples = new Couple[n];

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < n; i++) {

            sb.delete(0, sb.length());

            for(int j = 0; j < columns.length; j++) {
                sb.append(columns[j].getColumnValues().getValues()[i]);
                if(j < columns.length - 1) {
                    sb.append("|");
                }
            }

            couples[i] = new Couple<String, Integer>(sb.toString(), sb.toString().hashCode());
        }

        Arrays.sort(couples, new Comparator<Couple>() {
            @Override
            public int compare(Couple o1, Couple o2) {
                return o1.getSecondElement().compareTo(o2.getSecondElement());
            }
        });

        this.couples = couples;

    }

    public Set<Couple<String, Integer>> getRankSketches(int k) {
        Couple<String, Integer> current = couples[0];
        Set<Couple<String, Integer>> setOfCouples = new UnifiedSet<Couple<String, Integer>>();
        int inserted = 0;
        for(Couple c : couples) {
            if(c != current) {
                setOfCouples.add(c);
                current = c;
                if(!c.equals(current) && !c.getFirstElement().equals(current.getFirstElement()) && !c.getSecondElement().equals(current.getSecondElement())) {
//                if(!c.getFirstElement().equals(current.getFirstElement()) && !c.getSecondElement().equals(current.getSecondElement())) {
                    inserted++;
                }
            }else {
                setOfCouples.add(c);
            }
            if(inserted == k) {
                break;
            }
        }

        return setOfCouples;
    }

    public Integer getRankOfPlus1Sketch(int k) {
        Couple<String, Integer> current = couples[0];
        int inserted = 0;
        for(Couple c : couples) {
            if(c != current) {
                current = c;
                if(!c.equals(current) && !c.getFirstElement().equals(current.getFirstElement()) && !c.getSecondElement().equals(current.getSecondElement())) {
//                if(!c.getFirstElement().equals(current.getFirstElement()) && !c.getSecondElement().equals(current.getSecondElement())) {
                    inserted++;
                }
            }
            if(inserted == k) {
                current = c;
                break;
            }
        }

        return current.getSecondElement();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Couple c : couples) {
            sb.append(c.toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof BottomKSketch) {
            BottomKSketch bottomKSketch = (BottomKSketch) o;
            return Arrays.equals(bottomKSketch.couples, couples);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if(hashCode == null) {
            hashCode = Arrays.hashCode(couples);
        }
        return hashCode;
    }
}
