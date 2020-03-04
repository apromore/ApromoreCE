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

import java.util.Iterator;
import java.util.TreeSet;

/**
 * A ComparableSet extends the java.logextractor.TreeSet. Normal TreeSets are not
 * 'comparable' among others and therfore sets of sets are not supported out of
 * the box in Java. Using this class it is possible to define e.g:
 * ComparableSet<ComparableSet<String>>
 *
 * @param <T> T must be class which has implemented the Comparable interface
 *            (e.g. String, Integer, Date,...)
 * @author Tobias
 */
public class ComparableSet<T extends Comparable<? super T>> extends TreeSet<T> implements Comparable<ComparableSet<T>> {
    private static final long serialVersionUID = 1L;

    /**
     * Standard constructor
     */
    public ComparableSet() {

    }

    /**
     * Overloaded constructor. Initalizes the ComparableSet with all elements of
     * the TreeSet
     *
     * @param set - a TreeSet as argument
     */
    public ComparableSet(TreeSet<T> set) {
        addAll(set);
    }

    /**
     * Return a TreeSet
     *
     * @return a TreeSet
     */
    public TreeSet<T> getTreeSet() {
        return this;
    }

    /**
     * Makes a copy of the ComparableSet
     *
     * @return
     */
    public ComparableSet<T> deepCopy() {
        return new ComparableSet<T>(new TreeSet<T>(this));
    }

    /**
     * Cheks if a element is in the ComparableSet
     *
     * @param element
     * @return
     */
    public boolean member(T element) {
        return contains(element);
    }

    /**
     * Returns true if the overgiven set is a subset the set
     *
     * @param set
     * @return
     */
    public boolean isSubset(ComparableSet<T> set) {
        return set.getTreeSet().containsAll(this);
    }

    public int compareTo(ComparableSet<T> comparableSet) {
        TreeSet<T> set = comparableSet.getTreeSet();
        Iterator<T> iterFirst = iterator();
        Iterator<T> iterSecond = set.iterator();

        while (iterFirst.hasNext() && iterSecond.hasNext()) {
            T first = iterFirst.next();
            T second = iterSecond.next();
            int cmp = first.compareTo(second);
            if (cmp == 0) {
                continue;
            }
            return cmp;
        }
        if (iterFirst.hasNext()) {
            return 1;
        }
        if (iterSecond.hasNext()) {
            return -1;
        }
        return 0;
    }

    /**
     * Returns the mathematical union of two sets
     *
     * @param comparableSet
     * @return - the
     */
    public ComparableSet<T> union(ComparableSet<T> comparableSet) {
        TreeSet<T> union = new TreeSet<T>(this);
        union.addAll(comparableSet.getTreeSet());
        return new ComparableSet<T>(union);
    }

    /**
     * Returns the mathematical intersection of two sets
     *
     * @param comparableSet
     * @return
     */
    public ComparableSet<T> intersection(ComparableSet<T> comparableSet) {
        TreeSet<T> intersection = new TreeSet<T>(this);
        intersection.retainAll(comparableSet.getTreeSet());
        return new ComparableSet<T>(intersection);
    }

    /**
     * Returns the mathematical difference of two sets
     *
     * @param comparableSet
     * @return
     */
    public ComparableSet<T> difference(ComparableSet<T> comparableSet) {
        TreeSet<T> difference = new TreeSet<T>(this);
        difference.removeAll(comparableSet.getTreeSet());
        return new ComparableSet<T>(difference);
    }

    /**
     * Removes one element of the set
     */
    public ComparableSet<T> without(T element) {
        TreeSet<T> without = new TreeSet<T>(this);
        //if set is empty and remove is called we get an 'NoSuchElementException' exception
        if (!without.isEmpty())
            without.remove(element);
        return new ComparableSet<T>(without);
    }

    /**
     * Return a String representaion of the set e.g: if the ComparableSet
     * contains [A,B,C] it returns "ABC"
     *
     * @return
     */
    public String serialize() {

        Iterator<T> itThis = iterator();

        StringBuilder result = new StringBuilder();
        while (itThis.hasNext()) {
            result.append(itThis.next());
        }
        return result.toString();
    }

    /**
     * Returns a set representation of the set without the brackets e.g: if the
     * ComparableSet contains [A,B,C] it returns "A,B,C"
     *
     * @return
     */
    public String serializeWithoutBrackets() {

        Iterator<T> itThis = iterator();

        StringBuilder result = new StringBuilder();
        while (itThis.hasNext()) {
            result.append(itThis.next()).append(",");
        }
        result.deleteCharAt(result.length() - 1); // remove last ','
        return result.toString();

    }
}
