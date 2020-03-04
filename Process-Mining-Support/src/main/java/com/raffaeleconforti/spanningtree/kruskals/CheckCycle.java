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

package com.raffaeleconforti.spanningtree.kruskals;

import java.util.Stack;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 16/02/2016.
 */
public class CheckCycle {
    private Stack<Integer> stack;

    public CheckCycle()
    {
        stack = new Stack<Integer>();
    }

    public boolean checkCycle(int adjacency_matrix[][], int source)
    {
        boolean cyclepresent = false;
        int number_of_nodes = adjacency_matrix[source].length - 1;

        int[][] adjacencyMatrix = new int[number_of_nodes + 1][number_of_nodes + 1];
        for (int sourcevertex = 1; sourcevertex <= number_of_nodes; sourcevertex++)
        {
            System.arraycopy(adjacency_matrix[sourcevertex], 1, adjacencyMatrix[sourcevertex], 1, number_of_nodes);
        }

        int visited[] = new int[number_of_nodes + 1];
        int element = source;
        int i = source;
        visited[source] = 1;
        stack.push(source);

        while (!stack.isEmpty())
        {
            element = stack.peek();
            i = element;
            while (i <= number_of_nodes)
            {
                if (adjacencyMatrix[element][i] >= 1 && visited[i] == 1)
                {
                    if (stack.contains(i))
                    {
                        cyclepresent = true;
                        return cyclepresent;
                    }
                }
                if (adjacencyMatrix[element][i] >= 1 && visited[i] == 0)
                {
                    stack.push(i);
                    visited[i] = 1;
                    adjacencyMatrix[element][i] = 0;// mark as labelled;
                    adjacencyMatrix[i][element] = 0;
                    element = i;
                    i = 1;
                    continue;
                }
                i++;
            }
            stack.pop();
        }
        return cyclepresent;
    }
}
