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

package com.raffaeleconforti.keithshwarz.algorithms.maximummatchings.edmonds;

/**
 * Created by conforti on 26/11/14.
 */

public class Edge implements Comparable<Edge> {

    final Node from;
    final Node to;
    final int weight;

    public Edge(final Node argFrom, final Node argTo, final int argWeight){
        from = argFrom;
        to = argTo;
        weight = argWeight;
    }

    public int compareTo(final Edge argEdge){
        return weight - argEdge.weight;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }
}