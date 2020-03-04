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
public class Tuple<T extends Comparable> implements Comparable<Tuple<T>>{

    private T[] elements;
    private Integer hashCode;

    public Tuple(T[] elements) {
        this.elements = elements;
    }

    public T[] getElements() {
        return elements;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Tuple) {
            Tuple c = (Tuple) o;
            if (c.elements.length == elements.length) {
                for (int i = 0; i < elements.length; i++) {
                    if(!c.elements[i].equals(elements[i])) return false;
                }
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if(hashCode == null) {
            hashCode = Arrays.hashCode(elements);
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return Arrays.toString(elements);
    }

    @Override
    public int compareTo(Tuple<T> o) {
        for(int i = 0; i < elements.length; i++) {
            int result = elements[i].compareTo(o.elements[i]);
            if(result != 0) {
                return result;
            }
        }
        return 0;
    }

}
