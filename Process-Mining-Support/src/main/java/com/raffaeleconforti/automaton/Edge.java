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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by conforti on 14/02/15.
 */
public class Edge<T> {

    private static AtomicInteger count = new AtomicInteger(0);
    private int id;
    private Node<T> source;
    private Node<T> target;
    private boolean infrequent = false;
    private Double frequency = null;

    public Edge(Node<T> source, Node<T> target) {
        id = count.getAndIncrement();
        this.source = source;
        this.target = target;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setInfrequent(boolean infrequent) {
        this.infrequent = infrequent;
    }

    public int getId() {
        return id;
    }

    public boolean isInfrequent() {
        return infrequent;
    }

    public Node<T> getSource() { return source; }

    public Node<T> getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (source != null ? !source.equals(edge.source) : edge.source != null) return false;
        return target != null ? target.equals(edge.target) : edge.target == null;
    }

    @Override
    public int hashCode() {
        int result = source != null ? source.hashCode() : 0;
        result = 31 * result + (target != null ? target.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return source.toString() + " -> " + target.toString();
    }
}
