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

/**
 * Objects of this class hold are functionale dependency. The class is used by
 * algorithms in TANEjava.java to calculate the cover and closure for a set F
 * of functional dependencies.
 *
 * @author Tobias
 */
public class FunctionalDependency implements Comparable<FunctionalDependency> {
    private ComparableSet<String> X = new ComparableSet<String>();
    private ComparableSet<String> Y = new ComparableSet<String>();

    /**
     * Returns the left-hand-side of a functional dependency
     *
     * @return ComparableSet<String> - the the left-hand-side attributes
     */
    public ComparableSet<String> getX() {
        return X;
    }

    /**
     * Returns the right-hand-side of a functional dependency
     *
     * @return ComparableSet<String> - the the right-hand-side attributes
     */
    public ComparableSet<String> getY() {
        return Y;
    }

    /**
     * Adds a new attribue to the left-hand-side
     *
     * @param attribute - the left-hand-side atttibute
     */
    public void addX(String attribute) {
        X.add(attribute);
    }

    /**
     * Adds an attribue set to the left-hand-side
     *
     * @param attribute - the left-hand-side atttibutes
     */
    public void addX(ComparableSet<String> attribute) {
        X.addAll(attribute);
    }

    /**
     * Adds an attribue to the right-hand-side
     *
     * @param attribute - the right-hand-side atttibute
     */
    public void addY(String attribute) {
        Y.add(attribute);
    }

    /**
     * Adds an attribue set to the right-hand-side
     *
     * @param attribute - the right-hand-side atttibutes
     */
    public void addY(ComparableSet<String> attribute) {
        Y.addAll(attribute);
    }

    public int compareTo(FunctionalDependency o) {
        int cmp = X.compareTo(o.X);

        //Wenn erster Paar gleich ist, dann entscheide anhand vom Zweiten
        if (cmp == 0) {
            cmp = Y.compareTo(o.Y);
        }
        return cmp;
    }

    /**
     * Prints a functional dependency.
     */
    public String toString() {
        return X + "->" + Y;
    }

    /**
     * Clears the attribues of the RHS and LHS candidates
     */
    public void clear() {
        X.clear();
        Y.clear();
    }
}
