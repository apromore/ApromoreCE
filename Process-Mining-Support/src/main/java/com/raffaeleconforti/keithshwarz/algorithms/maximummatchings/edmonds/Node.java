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

public class Node implements Comparable<Node> {

    final int name;
    boolean visited = false;   // used for Kosaraju's algorithm and Edmonds's algorithm
    int lowlink = -1;          // used for Tarjan's algorithm
    int index = -1;            // used for Tarjan's algorithm

    public Node(final int argName) {
        name = argName;
    }

    public int compareTo(final Node argNode) {
        return argNode == this ? 0 : -1;
    }
}