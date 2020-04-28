/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Bruce Nguyen, Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package demo.tree.dynamic_tree;
 
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
 
import demo.data.pojo.Contact;
 
public class AdvancedTreeModel extends DefaultTreeModel<Contact> {
    private static final long serialVersionUID = -5513180500300189445L;
     
    DefaultTreeNode<Contact> _root;
 
    public AdvancedTreeModel(ContactTreeNode contactTreeNode) {
        super(contactTreeNode);
        _root = contactTreeNode;
    }
 
    /**
     * remove the nodes which parent is <code>parent</code> with indexes
     * <code>indexes</code>
     * 
     * @param parent
     *            The parent of nodes are removed
     * @param indexFrom
     *            the lower index of the change range
     * @param indexTo
     *            the upper index of the change range
     * @throws IndexOutOfBoundsException
     *             - indexFrom < 0 or indexTo > number of parent's children
     */
    public void remove(DefaultTreeNode<Contact> parent, int indexFrom, int indexTo) throws IndexOutOfBoundsException {
        DefaultTreeNode<Contact> stn = parent;
        for (int i = indexTo; i >= indexFrom; i--)
            try {
                stn.getChildren().remove(i);
            } catch (Exception exp) {
                exp.printStackTrace();
            }
    }
 
    public void remove(DefaultTreeNode<Contact> target) throws IndexOutOfBoundsException {
        int index = 0;
        DefaultTreeNode<Contact> parent = null;
        // find the parent and index of target
        parent = dfSearchParent(_root, target);
        for (index = 0; index < parent.getChildCount(); index++) {
            if (parent.getChildAt(index).equals(target)) {
                break;
            }
        }
        remove(parent, index, index);
    }
 
    /**
     * insert new nodes which parent is <code>parent</code> with indexes
     * <code>indexes</code> by new nodes <code>newNodes</code>
     * 
     * @param parent
     *            The parent of nodes are inserted
     * @param indexFrom
     *            the lower index of the change range
     * @param indexTo
     *            the upper index of the change range
     * @param newNodes
     *            New nodes which are inserted
     * @throws IndexOutOfBoundsException
     *             - indexFrom < 0 or indexTo > number of parent's children
     */
    public void insert(DefaultTreeNode<Contact> parent, int indexFrom, int indexTo, DefaultTreeNode<Contact>[] newNodes)
            throws IndexOutOfBoundsException {
        DefaultTreeNode<Contact> stn = parent;
        for (int i = indexFrom; i <= indexTo; i++) {
            try {
                stn.getChildren().add(i, newNodes[i - indexFrom]);
            } catch (Exception exp) {
                throw new IndexOutOfBoundsException("Out of bound: " + i + " while size=" + stn.getChildren().size());
            }
        }
    }
 
    /**
     * append new nodes which parent is <code>parent</code> by new nodes
     * <code>newNodes</code>
     * 
     * @param parent
     *            The parent of nodes are appended
     * @param newNodes
     *            New nodes which are appended
     */
    public void add(DefaultTreeNode<Contact> parent, DefaultTreeNode<Contact>[] newNodes) {
        DefaultTreeNode<Contact> stn = (DefaultTreeNode<Contact>) parent;
 
        for (int i = 0; i < newNodes.length; i++)
            stn.getChildren().add(newNodes[i]);
 
    }
 
    private DefaultTreeNode<Contact> dfSearchParent(DefaultTreeNode<Contact> node, DefaultTreeNode<Contact> target) {
        if (node.getChildren() != null && node.getChildren().contains(target)) {
            return node;
        } else {
            int size = getChildCount(node);
            for (int i = 0; i < size; i++) {
                DefaultTreeNode<Contact> parent = dfSearchParent((DefaultTreeNode<Contact>) getChild(node, i), target);
                if (parent != null) {
                    return parent;
                }
            }
        }
        return null;
    }
 
}