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

package com.raffaeleconforti.datastructures;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Created by Raffaele Conforti on 20/02/14.
 */
public class Tree<T> {
    private Node<T> root;
    int[][] adjacency_matrix;
    public T[] vertices;

    public Tree(T rootData, int[][] adjacency_matrix) {
        root = new Node<T>();
        root.level = 1;
        root.data = rootData;
        root.children = new ArrayList<Node<T>>();
        this.adjacency_matrix = adjacency_matrix;
    }

    public Tree(T rootData) {
        root = new Node<T>();
        root.level = 1;
        root.data = rootData;
        root.children = new ArrayList<Node<T>>();
    }

    public int[][] getAdjacencyMatrix() {
        return  adjacency_matrix;
    }

    public Node<T> getRoot() {
        return root;
    }

    public Node<T> findNode(T data) {
        Deque<Node<T>> toBeVisited = new ArrayDeque<Node<T>>();
        toBeVisited.add(root);

        while (toBeVisited.size() > 0) {
            Node<T> node = toBeVisited.removeFirst();

            if (node.data.equals(data)) {
                return node;
            } else {
                if(node.children.size() > 0) toBeVisited.addAll(node.children);
            }
        }

        return null;

    }

    public List<Node<T>> findLeaves() {
        List<Node<T>> leaves = new ArrayList<Node<T>>();
        Deque<Node<T>> toBeVisited = new ArrayDeque<Node<T>>();
        if (root != null) {
            toBeVisited.add(root);

            while (toBeVisited.size() > 0) {
                Node<T> node = toBeVisited.removeFirst();

                if (node != null) {
                    if (node.children.size() == 0) {
                        leaves.add(node);
                    } else {
                        toBeVisited.addAll(node.children);
                    }
                }
            }
        }

        return leaves;
    }

    public void removeLeave(Node node) {
        if (node == root) {
            root = null;
        } else if (node.parent != null) {
            node.parent.children.remove(node);
        }
    }

    public List<T> toList() {
        List<T> list = new ArrayList<T>();
        Deque<Node<T>> navigationList = new ArrayDeque<Node<T>>();
        navigationList.add(root);

        while (navigationList.size() > 0) {
            Node<T> node = navigationList.removeFirst();
            list.add(node.getData());
            for (Node<T> child : node.getChildren()) {
                navigationList.add(child);
            }
        }

        return list;
    }

    @Override
    public String toString() {
        return root.toString();
    }

    @Override
    public Object clone() {
        Tree<T> tree = new Tree<T>(this.root.data);

        tree.root = (Node<T>) this.root.clone();

        return tree;
    }

    public class Node<T> {
        private int level = 0;
        private T data;
        private Node<T> parent;
        private List<Node<T>> children = new ArrayList<Node<T>>();

        public int getLevel() {
            if (level != 1) return parent.getLevel() + 1;
            else return 1;
        }

        public Node<T> addNode(T data) {
            Node<T> child = new Node<T>();
            child.data = data;
            this.children.add(child);
            child.parent = this;

            return child;
        }

        @Override
        public String toString() {
            String space = "";
            for(int i = 0; i < getLevel()-1; i++) {
                space += "|";
            }
            StringBuilder res = new StringBuilder();
            res.append(space).append(data);

            for (Node node : children) {
                res.append("\n").append(node.toString());
            }
            return res.toString();
        }

        @Override
        public Object clone() {
            Node<T> node = new Node<T>();
            node.data = this.data;

            for (Node<T> child : this.children) {

                Node<T> newChild = (Node<T>) child.clone();
                node.children.add(newChild);
                newChild.parent = node;

            }

            return node;
        }

        public List<Node<T>> getChildren() {
            return children;
        }

        public Node<T> getParent() {
            return parent;
        }

        public void setParent(Node<T> node) {
            parent = node;
        }

        public T getData() {
            return data;
        }

    }
}
