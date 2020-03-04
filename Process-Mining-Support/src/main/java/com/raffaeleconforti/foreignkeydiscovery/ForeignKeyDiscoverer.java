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

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.raffaeleconforti.foreignkeydiscovery.databasestructure.*;
import com.raffaeleconforti.foreignkeydiscovery.histogram.Histogram;
import com.raffaeleconforti.foreignkeydiscovery.histogram.impl.DistributionHistogram;
import com.raffaeleconforti.foreignkeydiscovery.histogram.impl.QuantileHistogram;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.*;

/**
 * Created by Raffaele Conforti on 14/10/14.
 */
public class ForeignKeyDiscoverer {

    public ArrayList<Couple<ForeignKey, Double>> discoverForeignKey(Set<Table> setOfTables, Set<Column> setOfColumns, Set<PrimaryKey> singleColumnPrimaryKeys, Set<PrimaryKey> multiColumnPrimaryKeys,
                                                                    double inclusionThreshold, int k, int numberOfQuantiles) {

        TreeSet<ForeignKey> singleColumnCandidateForeignKeys = new TreeSet<ForeignKey>();
        TreeSet<ForeignKey> multiColumnCandidateForeignKeys = new TreeSet<ForeignKey>();

        Map<Set<Column>, Histogram> quantileDistributionHistograms = new UnifiedMap<Set<Column>, Histogram>();

        com.google.common.collect.Table<PrimaryKey, Column, Set<Column>> setForeignS = com.google.common.collect.Tables.newCustomTable(
                com.google.common.collect.Maps.<PrimaryKey, Map<Column, Set<Column>>>newHashMap(),
                new Supplier<Map<Column, Set<Column>>>() {
                    public Map<Column, Set<Column>> get() {
                        return Maps.newHashMap();
                    }
                });

        Set<PrimaryKey> setOfPrimaryKeys = new UnifiedSet<PrimaryKey>(singleColumnPrimaryKeys);
        if(multiColumnPrimaryKeys != null) {
            setOfPrimaryKeys.addAll(multiColumnPrimaryKeys);
        }

        //Phase 1
        Map<Set<Column>, BottomKSketch> bottomKSketches = generateBottomKSketches(setOfColumns, k);

        TreeSet<Column> setF = new TreeSet<Column>();
        TreeSet<Column> setP = new TreeSet<Column>();

        for(PrimaryKey primaryKey : setOfPrimaryKeys) {
            for(Column cp : primaryKey.getColumns()) {
                for(Column cf : setOfColumns) {

                    setF.clear();
                    setF.add(cf);
                    setP.clear();
                    setP.add(cp);

                    if(computeInclusionCoefficient(bottomKSketches.get(setF), bottomKSketches.get(setP), k) >= inclusionThreshold) {
                        if(primaryKey.getColumnsSize() == 1) {
                            singleColumnCandidateForeignKeys.add(new ForeignKey(setP, setF));
                        }else if(primaryKey.getColumnsSize() > 1) {
                            Set<Column> set = null;
                            if((set = setForeignS.get(primaryKey, cp)) == null) {
                                set = new UnifiedSet<Column>();
                                setForeignS.put(primaryKey, cp, set);
                            }
                            set.add(cf);
                        }
                    }
                }
            }

            if(primaryKey.getColumnsSize() > 1) {
                bottomKSketches.put(primaryKey.getColumns(), new BottomKSketch(primaryKey.getColumns()));
            }

            quantileDistributionHistograms.put(primaryKey.getColumns(), computeQuantileHistogram(primaryKey.getColumns(), numberOfQuantiles));

        }

        //Phase 2
        if(multiColumnPrimaryKeys != null) {
            for (PrimaryKey primaryKey : multiColumnPrimaryKeys) {
                for (Table t : setOfTables) {
                    multiColumnCandidateForeignKeys.addAll(selectForeignKeys(setForeignS, t, primaryKey));
                    Iterator<ForeignKey> it = multiColumnCandidateForeignKeys.iterator();
                    while (it.hasNext()) {
                        ForeignKey foreignKey = it.next();

                        BottomKSketch fSketch = new BottomKSketch(foreignKey.getColumnsF());

                        if (computeInclusionCoefficient(fSketch, bottomKSketches.get(primaryKey.getColumns()), k) >= inclusionThreshold) {
                            QuantileHistogram quantileHistogram = (QuantileHistogram) computeQuantileHistogram(primaryKey.getColumns(), numberOfQuantiles);
                            quantileDistributionHistograms.put(primaryKey.getColumns(), quantileHistogram);
                            quantileDistributionHistograms.put(foreignKey.getColumnsF(), computeDistributionHistogram(quantileHistogram, foreignKey.getColumnsF()));
                        } else {
                            it.remove();
                        }
                    }
                }
            }
        }

        ArrayList<Couple<ForeignKey, Double>> list = new ArrayList<Couple<ForeignKey, Double>>();
        
        for(ForeignKey foreignKey : singleColumnCandidateForeignKeys) {
            quantileDistributionHistograms.put(foreignKey.getColumnsF(), computeDistributionHistogram(quantileDistributionHistograms.get(foreignKey.getColumnsP()), foreignKey.getColumnsF()));
            list.add(new Couple<ForeignKey, Double>(foreignKey, computeEMDn(foreignKey, quantileDistributionHistograms)));
        }

        for(ForeignKey foreignKey : multiColumnCandidateForeignKeys) {
            list.add(new Couple<ForeignKey, Double>(foreignKey, computeEMDn(foreignKey, quantileDistributionHistograms)));
        }

        Collections.sort(list, new Comparator<Couple<ForeignKey, Double>>() {
            @Override
            public int compare(Couple<ForeignKey, Double> o1, Couple<ForeignKey, Double> o2) {
                return o1.getSecondElement().compareTo(o2.getSecondElement());
            }
        });

        return list;

    }

    private Histogram computeQuantileHistogram(TreeSet<Column> columns, int numbersOfQuantiles) {
        return new QuantileHistogram(columns, numbersOfQuantiles);
    }

    private Histogram computeDistributionHistogram(Histogram quantileHistogram, TreeSet<Column> columns) {
        return new DistributionHistogram(quantileHistogram, columns);
    }

    private double computeEMDn(ForeignKey foreignKey, Map<Set<Column>, Histogram> quantileDistributionHistograms) {

        Histogram h1 = quantileDistributionHistograms.get(foreignKey.getColumnsF());

        Histogram h2 = quantileDistributionHistograms.get(foreignKey.getColumnsP());

        double[][] matrix = buildMatrix(h1, h2);

        HungarianAlgorithm hungarianAlgorithm = new HungarianAlgorithm(matrix);
        int[] result = hungarianAlgorithm.execute();

//        for(int i = 0; i < matrix.length; i++) {
//            for(int j = 0; j < matrix[i].length; j++) {
//                System.out.print(matrix[i][j]+" ");
//            }
//            System.out.println();
//        }
//        System.out.println(Arrays.toString(result));

        double cost = 0.0;
        for(int i = 0; i < result.length; i++) {
            cost += (result[i] > -1)?matrix[i][result[i]]:0.0;
        }

        return cost;
    }

    private double[][] buildMatrix(Histogram h1, Histogram h2) {
        Set<Cell> var1 = h1.getHistogram().keySet();
        Cell[] n = var1.toArray(new Cell[var1.size()]);
        Arrays.sort(n);
        Set<Cell> var2 = h2.getHistogram().keySet();
        Cell[] m = var2.toArray(new Cell[var2.size()]);
        Arrays.sort(m);

        double[][] matrix = new double[n.length][m.length];
        for(int i = 0; i < n.length; i++) {
            for(int j = 0; j < m.length; j++) {
                if(h1.getHistogram().get(n[i]) > 0 && h2.getHistogram().get(m[j]) > 0) {
                    int result = 0;
                    for(int cellPos = 0; cellPos < n[i].getCellPosition().length; cellPos++) {
                        result += Math.abs(n[i].getCellPosition()[cellPos] - m[j].getCellPosition()[cellPos]);
                    }
                    matrix[i][j] = result;
                }else {
                    if(h1.getHistogram().get(n[i]) > 0) {
                        int result = 0;
                        for(int cellPos = 0; cellPos < n[i].getCellPosition().length; cellPos++) {
                            result += n[i].getCellPosition()[cellPos];
                        }
                        matrix[i][j] = result;
                    }else {
                        int result = 0;
                        for(int cellPos = 0; cellPos < m[j].getCellPosition().length; cellPos++) {
                            result += m[j].getCellPosition()[cellPos];
                        }
                        matrix[i][j] = result;
                    }
                }
            }
        }

        return matrix;
    }

    private Collection<? extends ForeignKey> selectForeignKeys(com.google.common.collect.Table<PrimaryKey, Column, Set<Column>> setForeignS, Table t, PrimaryKey primaryKey) {

        Set<Column>[] setOfSetOfColumns = new Set[primaryKey.getColumns().size()];
        int i = 0;
        for(Column c_i : primaryKey.getColumns()) {
            setOfSetOfColumns[i] = new UnifiedSet<Column>();
            for(Column c_i_prime : setForeignS.get(primaryKey, c_i)) {
                if(t.getSetOfColumns().contains(c_i_prime)) {
                    setOfSetOfColumns[i].add(c_i_prime);
                }
            }
            i++;
        }

        int combinations = 1;
        ArrayList<Column>[] listOfSetOfColumns = new ArrayList[primaryKey.getColumns().size()];
        for(i = 0; i < setOfSetOfColumns.length; i++) {
            listOfSetOfColumns[i] = new ArrayList<Column>(setOfSetOfColumns[i]);
            combinations *= listOfSetOfColumns[i].size();
        }

        Set<ForeignKey> setCandidateForeignKeys = new UnifiedSet<ForeignKey>();
        int pos = 0;
        int[] counter = new int[listOfSetOfColumns.length];
        while(pos < combinations) {
            TreeSet<Column> innerSet = new TreeSet<Column>();
            for (i = 0; i < counter.length; i++) {
                innerSet.add(listOfSetOfColumns[i].get(counter[i]));
            }
            increaseCounter(counter, listOfSetOfColumns);

            boolean add = true;
            int size = -1;
            for(Column c : innerSet) {
                if(size == -1) size = c.getColumnValues().getValues().length;
                else if(size != c.getColumnValues().getValues().length) {
                    add = false;
                    break;
                }
            }
            if(add && primaryKey.getColumns().size() == innerSet.size()) {
                setCandidateForeignKeys.add(new ForeignKey(primaryKey.getColumns(), innerSet));
            }
            pos++;
        }

        return setCandidateForeignKeys;
    }

    private void increaseCounter(int[] counter, ArrayList<Column>[] listOfSetOfColumns) {
        boolean inc = true;
        int pos = 0;
        while(inc && pos < counter.length) {
            if(inc) {
                counter[pos]++;
                inc = false;
            }
            if(counter[pos] == listOfSetOfColumns[pos].size()) {
                counter[pos] = 0;
                inc = true;
                pos++;
            }
        }
    }

    private double computeInclusionCoefficient(BottomKSketch bottomKSketch1, BottomKSketch bottomKSketch2, int k) {
        int jaccardCoefficient1 = JaccardCoefficientEstimator.computeEstimator(bottomKSketch1, bottomKSketch2, k);

        BottomKSketch bottomKSketch3 = new BottomKSketch(bottomKSketch1, bottomKSketch2);

        int jaccardCoefficient2 = JaccardCoefficientEstimator.computeEstimator(bottomKSketch3, bottomKSketch1, k);

        return ((double) jaccardCoefficient1) / ((double) jaccardCoefficient2);
    }

    private Map<Set<Column>, BottomKSketch> generateBottomKSketches(Set<Column> setOfColumns, int k){
        Map<Set<Column>, BottomKSketch> bottomKSketches = new UnifiedMap<Set<Column>, BottomKSketch>();
        for(Column c : setOfColumns) {
            Set<Column> set = new UnifiedSet<Column>();
            set.add(c);
            bottomKSketches.put(set, new BottomKSketch(set));
        }
        return  bottomKSketches;
    }

    public static void main(String[] args) {

        String[] value1 = new String[]{"1", "1", "1", "2", "3"};
        ColumnValues columnValues1 = new ColumnValues(ColumnType.OTHER, value1);
        Column column1 = new Column("ID", columnValues1, "T");
        TreeSet<Column> set1 = new TreeSet<Column>();
        set1.add(column1);
        Table table1 = new Table("Trace", set1);
        PrimaryKey primaryKey1 = new PrimaryKey(set1);

        String[] value2 = new String[]{"1", "2", "3", "4", "5"};
        ColumnValues columnValues2 = new ColumnValues(ColumnType.OTHER, value2);
        Column column2 = new Column("MID", columnValues2, "A");
        String[] value3 = new String[]{"1", "1", "1", "2", "3"};
        ColumnValues columnValues3 = new ColumnValues(ColumnType.OTHER, value3);
        Column column3 = new Column("ID", columnValues3, "A");
        String[] value4 = new String[]{"a", "b", "c", "d", "d"};
        ColumnValues columnValues4 = new ColumnValues(ColumnType.OTHER, value4);
        Column column4 = new Column("data", columnValues4, "A");
        TreeSet<Column> set2 = new TreeSet<Column>();
        set2.add(column2);
        set2.add(column3);
        set2.add(column4);
        TreeSet<Column> set3 = new TreeSet<Column>();
        set3.add(column2);
        Table table2 = new Table("test", set2);
        PrimaryKey primaryKey2 = new PrimaryKey(set3);

        Set<Table> setOfTables = new TreeSet<Table>();
        setOfTables.add(table1);
        setOfTables.add(table2);

        Set<Column> setOfColumns = new TreeSet<Column>();
        setOfColumns.add(column1);
        setOfColumns.add(column2);
        setOfColumns.add(column3);
        setOfColumns.add(column4);

        Set<PrimaryKey> setOfPrimaryKeys = new TreeSet<PrimaryKey>();
        setOfPrimaryKeys.add(primaryKey1);
        setOfPrimaryKeys.add(primaryKey2);

        ForeignKeyDiscoverer fkd = new ForeignKeyDiscoverer();
        System.out.println(fkd.discoverForeignKey(setOfTables, setOfColumns, setOfPrimaryKeys, null, 0.9, 256, 3));

    }

}
