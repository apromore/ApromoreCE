/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package com.apql.Apql.tree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by corno on 1/08/2014.
 */
public abstract class DraggableNodeTree extends DefaultMutableTreeNode {
    private String id;
    private String name;
    private String pathNode;

    public DraggableNodeTree(String id, String name, String pathNode){
        this.id=id;
        this.name=name;
        this.pathNode=pathNode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathNode() {
        return pathNode;
    }

    public void setPathNode(String path) {
        this.pathNode = path;
    }

}
