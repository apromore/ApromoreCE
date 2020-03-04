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

package com.raffaeleconforti.spanningtree;

/**
 * Created by conforti on 26/11/14.
 */


import com.raffaeleconforti.spanningtree.kruskals.CheckCycle;
import com.raffaeleconforti.spanningtree.kruskals.Edge;
import com.raffaeleconforti.spanningtree.kruskals.EdgeComparator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class KruskalAlgorithm
{
    private List<Edge> edges;
    private int numberOfVertices;
    public static final int MAX_VALUE = 999;
    private int visited[];
    private int spanning_tree[][];

    public KruskalAlgorithm(int numberOfVertices)
    {
        this.numberOfVertices = numberOfVertices;
        edges = new LinkedList<Edge>();
        visited = new int[this.numberOfVertices + 1];
        spanning_tree = new int[numberOfVertices + 1][numberOfVertices + 1];
    }

    public void kruskalAlgorithm(int adjacencyMatrix[][])
    {
        boolean finished = false;
        for (int source = 1; source <= numberOfVertices; source++)
        {
            for (int destination = 1; destination <= numberOfVertices; destination++)
            {
                if (adjacencyMatrix[source][destination] != MAX_VALUE && source != destination)
                {
                    Edge edge = new Edge();
                    edge.setSourcevertex(source);
                    edge.setDestinationvertex(destination);
                    edge.setWeight(adjacencyMatrix[source][destination]);
                    adjacencyMatrix[destination][source] = MAX_VALUE;
                    edges.add(edge);
                }
            }
        }
        Collections.sort(edges, new EdgeComparator());
        CheckCycle checkCycle = new CheckCycle();
        for (Edge edge : edges)
        {
            spanning_tree[edge.getSourcevertex()][edge.getDestinationvertex()] = edge.getWeight();
            spanning_tree[edge.getDestinationvertex()][edge.getSourcevertex()] = edge.getWeight();
            if (checkCycle.checkCycle(spanning_tree, edge.getSourcevertex()))
            {
                spanning_tree[edge.getSourcevertex()][edge.getDestinationvertex()] = 0;
                spanning_tree[edge.getDestinationvertex()][edge.getSourcevertex()] = 0;
                edge.setWeight(-1);
                continue;
            }
            visited[edge.getSourcevertex()] = 1;
            visited[edge.getDestinationvertex()] = 1;
            for (int i = 0; i < visited.length; i++)
            {
                if (visited[i] == 0)
                {
                    finished = false;
                    break;
                } else
                {
                    finished = true;
                }
            }
            if (finished)
                break;
        }
    }

    public String printTree(int[][] spanning_tree) {
        StringBuilder sb = new StringBuilder();
        sb.append("The spanning tree is \n");
        for (int i = 1; i < spanning_tree.length; i++)
            sb.append("\t").append(i);
        sb.append("\n");
        for (int source = 1; source < spanning_tree.length; source++)
        {
            sb.append(source).append("\t");
            for (int destination = 1; destination < spanning_tree.length; destination++)
            {
                sb.append(spanning_tree[source][destination]).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String... arg)
    {
        int adjacency_matrix[][];
        int number_of_vertices;

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter the number of vertices");
        number_of_vertices = scan.nextInt();
        adjacency_matrix = new int[number_of_vertices + 1][number_of_vertices + 1];

        System.out.println("Enter the Weighted Matrix for the graph");
        for (int i = 1; i <= number_of_vertices; i++)
        {
            for (int j = 1; j <= number_of_vertices; j++)
            {
                adjacency_matrix[i][j] = scan.nextInt();
                if (i == j)
                {
                    adjacency_matrix[i][j] = 0;
                    continue;
                }
                if (adjacency_matrix[i][j] == 0)
                {
                    adjacency_matrix[i][j] = MAX_VALUE;
                }
            }
        }
        KruskalAlgorithm kruskalAlgorithm = new KruskalAlgorithm(number_of_vertices);
        kruskalAlgorithm.kruskalAlgorithm(adjacency_matrix);
        scan.close();
    }

    public int[][] getSpanningTree() {
        return spanning_tree;
    }

    public int[] getVisited() {
        return visited;
    }
}

