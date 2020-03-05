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

package com.raffaeleconforti.datastructures.conversion;

import com.raffaeleconforti.datastructures.Hierarchy;
import com.raffaeleconforti.datastructures.Tree;
import com.raffaeleconforti.datastructures.exception.EmptyLogException;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.Attribute;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.Entity;
import com.raffaeleconforti.keithshwarz.algorithms.maximummatchings.edmonds.AdjacencyList;
import com.raffaeleconforti.keithshwarz.algorithms.maximummatchings.edmonds.Edge;
import com.raffaeleconforti.keithshwarz.algorithms.maximummatchings.edmonds.Edmonds;
import com.raffaeleconforti.keithshwarz.algorithms.maximummatchings.edmonds.Node;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.*;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 10/03/2016.
 */
public class Converter<T> {

    public Tree<T> convertHierarchyToTree(Hierarchy<T> hierarchy, T rootEntity, Map<T, Map<String, Double>> entityCertancy, int algorithm) throws EmptyLogException {

            Tree<T> tree = new Tree<T>(rootEntity);

            if(algorithm == 1) {
                Deque<Map.Entry<T, Set<T>>> list = new ArrayDeque<Map.Entry<T, Set<T>>>();

                for (Map.Entry<T, Set<T>> entry : hierarchy.entrySet()) {
                    list.add(entry);
                }

                UnifiedMap<T, Tree.Node> insertedEnt = new UnifiedMap<T, Tree.Node>();

                int loop = 0;
                while (list.size() > 0 && loop < hierarchy.size()*100) {
                    Map.Entry<T, Set<T>> entry = list.removeFirst();
                    boolean inserted = false;
                    for (T entity : entry.getValue()) {
                        Tree<T>.Node<T> nodeParent;
                        Tree<T>.Node<T> existingNode;
                        if ((nodeParent = tree.findNode(entry.getKey())) != null) {
                            if ((existingNode = insertedEnt.get(entity)) != null) {
                                if (nodeParent.getLevel() + 1 > existingNode.getLevel()) {
                                    Tree<T>.Node<T> child = nodeParent.addNode(entity);
                                    insertedEnt.put(entity, child);
                                    for(Tree<T>.Node<T> child2 : existingNode.getChildren()) {
                                        if(child != child2) {
                                            child.getChildren().add(child2);
                                        }
                                    }
                                    for (Tree<T>.Node<T> grandchild : child.getChildren()) {
                                        grandchild.setParent(child);
                                    }
                                    existingNode.getParent().getChildren().remove(existingNode);
                                } else {
                                    continue;
                                }
                            } else {
                                Tree<T>.Node<T> child = nodeParent.addNode(entity);
                                insertedEnt.put(entity, child);
                            }
                            inserted = true;
                        }
                    }
                    if (!inserted && entry.getValue().size() > 0) {
                        list.add(entry);
                    }
                    loop++;
                }
            }

            if(tree.toList().size() < hierarchy.size()) tree = convertHierarchyToTreeUsingSpanningTreeEdmonds(tree, hierarchy, entityCertancy);

            return tree;
    }

    private Tree<T> convertHierarchyToTreeUsingSpanningTreeEdmonds(Tree<T> tree, Hierarchy<T> hierarchy, Map<T, Map<String, Double>> entityCertancy) throws EmptyLogException {

        T rootEntity = tree.getRoot().getData();
        tree.vertices = (T[]) hierarchy.keySet().toArray(new Object[hierarchy.size()]);
        Node[] nodes = new Node[tree.vertices.length];

        Node root = null;

        for(int i = 0; i < tree.vertices.length; i++) {
            nodes[i] = new Node(i);
            if (tree.vertices[i].equals(rootEntity)) {
                root = nodes[i];
            }
        }

        AdjacencyList adjacencyList = new AdjacencyList();

        for (int i = 0; i < tree.vertices.length; i++) {
            for (int j = 0; j < tree.vertices.length; j++) {
                if(hierarchy.get(tree.vertices[i]).contains(tree.vertices[j])) {
                    int val = 0;
                    boolean exit = false;
                    if(rootEntity instanceof Entity) {
                        Entity entity = null;
                        for (Attribute a : entity.getKeys()) {
                            if (entityCertancy.get(tree.vertices[i]) != null) {
                                if (entityCertancy.get(tree.vertices[i]).get(a.getName()) != null) {
                                    val += (int) ((double) entityCertancy.get(tree.vertices[i]).get(a.getName()));
                                } else {
                                    exit = true;
                                }
                            }else {
                                val += 1;
                            }
                        }
                    }else {
                        val = hierarchy.get(tree.vertices[i]).size();
                    }
                    if(!exit) {
                        if(rootEntity instanceof Entity) {
                            if (!tree.vertices[j].equals(rootEntity)) {
                                adjacencyList.addEdge(nodes[i], nodes[j], tree.vertices[i].equals(rootEntity) ? val + 1 : val + 3);
                            }
                        }else {
                            if (!tree.vertices[j].equals(rootEntity)) {
                                adjacencyList.addEdge(nodes[i], nodes[j], tree.vertices[i].equals(rootEntity) ? val + 3 : val + 1);
                            }
                        }
                    }
                }
            }
        }

        Edmonds edmonds = new Edmonds();
        try {
            AdjacencyList result = edmonds.getMinBranching(root, adjacencyList);

            List<Node> toVisit = new ArrayList<Node>();
            toVisit.add(root);

            Tree<T> tree_new = new Tree<T>(rootEntity);
            while (!toVisit.isEmpty()) {
                Node node = toVisit.remove(0);
                for (int i = 0; i < tree_new.vertices.length; i++) {
                    if (nodes[i].equals(node)) {
                        Tree.Node nodeParent;
                        if ((nodeParent = tree_new.findNode(tree_new.vertices[i])) != null) {
                            List<Edge> edges = result.getAdjacent(node);
                            if (edges != null) {
                                for (Edge edge : edges) {
                                    if (edge.getFrom().equals(node)) {
                                        for (int j = 0; j < tree_new.vertices.length; j++) {
                                            if (nodes[j].equals(edge.getTo())) {
                                                nodeParent.addNode(tree_new.vertices[j]);
                                                toVisit.add(nodes[j]);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return tree_new;
        }catch (NullPointerException npe) {
            throw new EmptyLogException();
        }

    }

}
