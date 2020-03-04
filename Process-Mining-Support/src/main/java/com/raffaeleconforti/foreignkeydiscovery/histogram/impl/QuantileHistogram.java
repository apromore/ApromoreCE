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

package com.raffaeleconforti.foreignkeydiscovery.histogram.impl;

import com.raffaeleconforti.foreignkeydiscovery.Cell;
import com.raffaeleconforti.foreignkeydiscovery.Couple;
import com.raffaeleconforti.foreignkeydiscovery.databasestructure.Column;
import com.raffaeleconforti.foreignkeydiscovery.databasestructure.Tuple;
import com.raffaeleconforti.foreignkeydiscovery.histogram.Histogram;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.*;

/**
 * Created by Raffaele Conforti on 14/10/14.
 */
public class QuantileHistogram implements Histogram {

    private TreeSet<Column> columns;
    private int numbersOfQuantiles;
    private UnifiedMap<Cell, Set<Tuple<String>>> quantileDistributionHistogram = new UnifiedMap<Cell, Set<Tuple<String>>>();
    private UnifiedMap<Cell, Double> histogram = null;
    private Integer hashCode;

    public QuantileHistogram(TreeSet<Column> columns, int numbersOfQuantiles) {
        this.columns = columns;
        this.numbersOfQuantiles = numbersOfQuantiles;
        int[][] quantileValue = new int[columns.size()][numbersOfQuantiles];

        UnifiedMap<String, Integer> columnNames = new UnifiedMap<String, Integer>();
        Integer columnPos = 0;
        for(Column c : columns) {
            columnNames.put(c.getColumnName(), columnPos);
            columnPos++;
        }

        int[] dimension;
        int column = 0;

        ArrayList<Couple<Integer, Integer>[]> list = new ArrayList<Couple<Integer, Integer>[]>();

        Comparator<Couple> comparator = new Comparator<Couple>() {
            @Override
            public int compare(Couple o1, Couple o2) {
                return o1.getSecondElement().compareTo(o2.getSecondElement());
            }
        };

        for(Column c : columns) {
            dimension = c.getColumnValues().getHashCodes();
            Couple<Integer, Integer>[] arrayCouples = new Couple[dimension.length];
            ArrayList<Couple<Integer, Integer>> couples = new ArrayList<Couple<Integer, Integer>>();
            Set<Integer> dimensionProjection = new UnifiedSet<Integer>();
            int count = 0;
            for(int i = 0; i < dimension.length; i++) {
                arrayCouples[i] = new Couple<Integer, Integer>(count, dimension[i]);
                if(!dimensionProjection.contains(dimension[i])) {
                    couples.add(arrayCouples[i]);
                    dimensionProjection.add(dimension[i]);
                    count++;
                }
            }

            Collections.sort(couples, comparator);
            Arrays.sort(arrayCouples, comparator);

            list.add(arrayCouples);

            for(int i = 0; i < numbersOfQuantiles; i++) {
                int pos = (int) (((double) dimensionProjection.size()/(double) numbersOfQuantiles) * (i+1)) -1;
                quantileValue[column][i] = couples.get((int) (((double) dimensionProjection.size()/(double) numbersOfQuantiles) * (i+1)) -1).getSecondElement();
            }
            column++;
        }

        ArrayList<ArrayList<Set<Integer>>> setColumnsElements = new ArrayList<ArrayList<Set<Integer>>>();
        for(int j = 0; j < columns.size(); j++) {
            ArrayList<Set<Integer>> setElements = new ArrayList<Set<Integer>>();
            int lastPos = 0;
            for(int k = 0; k < numbersOfQuantiles; k++) {
                Set<Integer> elements = new UnifiedSet<Integer>();
                for(int l = lastPos; l < list.get(j).length; l++) {
                    if(list.get(j)[l].getSecondElement() <= quantileValue[j][k]) {
                        elements.add(list.get(j)[l].getFirstElement());
                    }else {
                        lastPos = l;
                        break;
                    }
                }

                setElements.add(elements);
            }

            setColumnsElements.add(setElements);

        }

        int numberOfCells = (int) Math.pow(numbersOfQuantiles, columns.size());
        int[] currentCell = new int[columns.size()];
        for(int i = 0; i < numberOfCells; i++) {
            TreeSet<Integer> set = new TreeSet<Integer>();
            TreeSet<Tuple<String>> setTuple = new TreeSet<Tuple<String>>();
            for(int j = 0; j < currentCell.length; j++) {
                if(j == 0) {
                    set = new TreeSet<Integer>(setColumnsElements.get(0).get(currentCell[j]));
                }else {
                    TreeSet<Integer> set1 = new TreeSet<Integer>(setColumnsElements.get(j).get(currentCell[j]));
                    set = intersect(set, set1);
                }
            }

            for (int columnRow : set) {
                String[] row = new String[columnNames.size()];
                for (Column c : columns) {
                    for(int pos = 0; pos < columnNames.size(); pos++) {
                        row[pos] = c.getColumnValues().getValues()[columnRow];
                    }
                }
                Tuple<String> tuple = new Tuple<String>(row);
                setTuple.add(tuple);
            }

            quantileDistributionHistogram.put(new Cell(currentCell), setTuple);
            increaseCellPosition(currentCell, numbersOfQuantiles);
        }
    }

    private TreeSet<Integer> intersect(TreeSet<Integer> set1, TreeSet<Integer> set2) {
        TreeSet<Integer> set = new TreeSet<Integer>();
        for(Integer i : set1) {
            if(set2.contains(i)) {
                set.add(i);
            }
        }
        return set;
    }

    private void increaseCellPosition(int[] currentCell, int numbersOfQuantiles) {
        boolean inc = true;
        int pos = 0;
        while(inc && pos < currentCell.length) {
            if(inc) {
                currentCell[pos]++;
                inc = false;
            }
            if(currentCell[pos] == numbersOfQuantiles) {
                currentCell[pos] = 0;
                inc = true;
                pos++;
            }
        }
    }

    public TreeSet<Column> getColumns() {
        return columns;
    }

    public UnifiedMap<Cell, Set<Tuple<String>>> getQuantileHistogram() {
        return quantileDistributionHistogram;
    }

    public UnifiedMap<Cell, Double> getHistogram() {
        if(histogram == null) {
            histogram = new UnifiedMap<Cell, Double>();
            for(Map.Entry<Cell, Set<Tuple<String>>> entry : quantileDistributionHistogram.entrySet()) {
                histogram.put(entry.getKey(), ((double) entry.getValue().size()));
            }
        }
        return histogram;
    }

    @Override
    public int getQuantiles() {
        return numbersOfQuantiles;
    }

    @Override
    public int hashCode() {
        if(hashCode == null) {
            hashCode = quantileDistributionHistogram.hashCode();
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof QuantileHistogram) {
            QuantileHistogram dh = (QuantileHistogram) o;
            return dh.quantileDistributionHistogram.equals(quantileDistributionHistogram);
        }
        return false;
    }

    @Override
    public String toString() {
        return quantileDistributionHistogram.toString();
    }
}
