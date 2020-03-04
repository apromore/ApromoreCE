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

package com.raffaeleconforti.automaton;

/**
 * Created by conforti on 14/02/15.
 */
public class Node<T> {

    private T data;
    private double frequency;

    public Node(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getFrequency() {
        return frequency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        return data != null ? data.equals(node.data) : node.data == null;
    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        return result;
    }

    @Override
    public String toString() {
        return data.toString();
    }

}
