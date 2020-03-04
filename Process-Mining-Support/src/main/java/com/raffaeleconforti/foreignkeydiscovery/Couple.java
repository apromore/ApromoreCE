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

/**
 * Created by Raffaele Conforti on 14/10/14.
 */
public class Couple<T extends Comparable, D extends Comparable> {

    private T firstElement;
    private D secondElement;
    private Integer hashCode;

    public Couple(T firstElement, D secondElement) {
        this.firstElement = firstElement;
        this.secondElement = secondElement;
    }

    public T getFirstElement() {
        return firstElement;
    }

    public D getSecondElement() {
        return secondElement;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Couple) {
            Couple c = (Couple) o;
            if(this.hashCode() == c.hashCode()) {
                return c.firstElement.equals(firstElement) && c.secondElement.equals(secondElement);
            }
            return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if(hashCode == null) {
            if(firstElement instanceof String && secondElement instanceof String) {
                hashCode = (firstElement + " " + secondElement).hashCode();
            }else {
                hashCode = firstElement.hashCode() + secondElement.hashCode();
            }
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return "Element 1 "+firstElement+" Element 2 "+secondElement;
    }

}
