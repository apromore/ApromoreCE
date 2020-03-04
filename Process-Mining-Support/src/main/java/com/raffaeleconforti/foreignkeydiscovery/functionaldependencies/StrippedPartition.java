
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

package com.raffaeleconforti.foreignkeydiscovery.functionaldependencies;

import org.deckfour.xes.model.XAttribute;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;


/**
 * Stripped partions are partions with equivalence classes of size one removed
 * This class builds such a partitions by using hashtables as propopsed in "TANE, AN EFFICIENT
 * ALGORITHM FOR DISCOVERING FUNCTIONAL DEPENDENCIES AND APPROXIMATE DEPENDENCIES
 *
 * @author Tobias
 */

public class StrippedPartition implements Serializable {
    private static final long serialVersionUID = 1L;
    private int elements[];
    private int error;
    private int noofsets = 0;    //number of equivalence classes in a parition
    private int noofelements = 0; //sum of sizes of equivalence classes

    /* if partitions are stored on disk, we need to store its seek position*/
    private long position = 0;    //file seek position
    private boolean isInMemory = false;


    /**
     * This constructor expects a Resultset in order to initialize the class
     *
     * @throws SQLException - if the an error arises during processing the resultset
     */
    public StrippedPartition(Data rs, String col, int numberOfRows) {
        Hashtable<Object, ArrayList<Integer>> table = new Hashtable<Object, ArrayList<Integer>>(numberOfRows);
        Object data;
        int i = 1;    //represents the tuple ID

		/*Create Equivalence classes from scratch */
        //go through all rows
        for (UnifiedMap<String, XAttribute> aTable : rs.table) {
            data = aTable.get(col);
            if (data == null)    //if a NULL value occurs
                data = "NULL";
            // if data is already in hashtable we have to modify the value
            if (table.containsKey(data)) {
                ArrayList<Integer> set = table.get(data);
                set.add(i);
                table.put(data, set);
            } else {
                EquivalenceClass set = new EquivalenceClass();
                set.add(i);
                table.put(data, set);
            }
            i++;
        }
        /*We use an array of the primitive type int to represent partitions
         *  --> performance accelleration
		 * ArrayList<ArrayList<Integer>> ist to slow
		 * hashtable contains the equivalence classes
		 */

        int arraySize = 0; //the size of parition[] --> not neccessarliy equal to elements.length

        int p[] = new int[numberOfRows + 1];
        for (ArrayList<Integer> eq : table.values()) {
            //We only consider equivalence class of size > 1 -->StrippedPartitions
            if (eq.size() > 1) {
                for (int k = 0; k < eq.size(); k++) {
                    p[arraySize] = eq.get(k);

                    if (k == eq.size() - 1) {
                        //set bit 31 to true -->endmarker
                        p[arraySize] = Bits.setBit(p[arraySize], 31);
                    }
                    arraySize++;
                }
                noofelements += eq.size();
                noofsets++;
            }
        }
        /*
         * the array p might be larger than neccessary.
		 * we copy all elemnts of p into elements
		 */
        elements = new int[arraySize];
        for (int k = 0; k < arraySize; k++) {
            elements[k] = p[k];
            p[k] = 0;
        }
        isInMemory = true;
        //calculating the error
        /*System.out.println("SumofSizes: "+ sumnofSizes);
        System.out.println("NoofSets: "+ noofEqClasses);
		*/
        error = noofelements - noofsets;    //we do not neccessarliy need to divide

        table.clear();    //clear table to save memory

    }

    /**
     * Initializes a stripped partition, its elements are known.
     *
     * @param newelements  - the elements of the partition
     * @param noofelements - sum of sizes of the equivalence classes
     * @param noofsets     -number of equivalence classes
     */
    public StrippedPartition(int[] newelements, int noofelements, int noofsets) {
        this.elements = newelements;
        this.noofelements = noofelements;
        this.noofsets = noofsets;

        error = noofelements - noofsets;
    }

    /**
     * Calculated the error e(X) of a given elements by equation (1) of the paper
     *
     * @return double - the error the elements
     */
    public double error() {
        return error;
    }

    /**
     * Prints the stripped partition. Each line illustrates an equivalence class.
     */
    public void print() {
        if (elements == null) return;

        for (int element : elements) {
            if (Bits.testBit(element, 31)) {
                System.out.println(Bits.clearBit(element, 31));
            } else {
                System.out.print(element + " ");

            }
        }
    }

    /**
     * Deletes the elements of the partition.
     */
    public void clear() {
        elements = null;
        isInMemory = false;

    }

    /**
     * Returns the elements of the parition
     *
     * @return int[] -the int array holding the partition
     */
    public int[] getElements() {
        return elements;
    }

    /**
     * Sets the elements of the stripped partition.
     *
     * @param elements - The elements the partition contains
     */
    public void setElements(int[] elements) {
        this.elements = elements;
        isInMemory = elements != null;
    }

    /**
     * This method is only useful when the partition is stored in a file.
     * It returns the exact position of the stripped partition in the file.
     * To access the partition the method seek() in AccessRandomFile is appropriate.
     * The getObject() of ObjectStore is the only method makeing use of
     * this method.
     *
     * @return long - the position of the stripped partition in the file
     */
    public long getPosition() {
        return position;
    }

    /**
     * Sets the position in the swapped file, the partition can be found.
     *
     * @param position - the position in the file
     */
    public void setPosition(long position) {
        this.position = position;
    }

    /**
     * Returns if a partition is hold in memory. That is the case, if and only
     * if the partition has not been cleared by using the clear() method or setElements method.
     *
     * @return
     */
    public boolean isInMemory() {
        return isInMemory;
    }

    /**
     * Returns the sum of sizes of the equivalence classes.
     *
     * @return int - the sum of sizes
     */
    public int getNoofelements() {
        return noofelements;
    }


}

