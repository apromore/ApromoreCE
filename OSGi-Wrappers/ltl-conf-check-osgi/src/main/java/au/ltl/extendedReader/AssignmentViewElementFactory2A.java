/*-
 * #%L
 * This file is part of "Apromore Community".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package au.ltl.extendedReader;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import org.processmining.plugins.declare.visualizing.ActivityDefinition;
import org.processmining.plugins.declare.visualizing.AssignmentModel;
import org.processmining.plugins.declare.visualizing.AssignmentModelView;
import org.processmining.plugins.declare.visualizing.ConstraintDefinition;
import org.processmining.plugins.declare.visualizing.DVertex;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AssignmentViewElementFactory2A extends AssignmentElementFactory2 {
    private static final String TAG_GRAPHICAL = "graphical";
    private static final String TAG_GRAPHICAL_CELLS = "activities";
    private static final String TAG_GRAPHICAL_CONNECTORS = "constraints";
    private static final String TAG_BOUND = "cell";
    private static final String TAG_BOUNDS_ID = "id";
    private static final String TAG_BOUNDS_X = "x";
    private static final String TAG_BOUNDS_Y = "y";
    private static final String TAG_BOUNDS_WIDTH = "width";
    private static final String TAG_BOUNDS_HEIGHT = "height";

    public AssignmentViewElementFactory2A(XMLBroker2 broker) {
        super(broker);
    }

    public Element createAssignmentElement(AssignmentModel model, AssignmentModelView view) {
        Element element = super.createAssignmentElement(model);
        element.appendChild(this.assignmentGraphicalToElement(view));
        return element;
    }

    private Element assignmentGraphicalToElement(AssignmentModelView view) {
        Element element = this.createElement("graphical");
        Element cellsTag = this.createElement("activities");
        this.getAllBounds(cellsTag, new ArrayList(view.activityDefinitionCells()));
        element.appendChild(cellsTag);
        Element connectorsTag = this.createElement("constraints");
        this.getAllBounds(connectorsTag, new ArrayList(view.connectorCells()));
        element.appendChild(connectorsTag);
        return element;
    }

    private void getAllBounds(Element element, List<DVertex> cells) {
        for(int i = 0; i < cells.size(); ++i) {
            element.appendChild(this.boundsToElement((DVertex)cells.get(i)));
        }

    }

    private Element boundsToElement(DVertex cell) {
        Element element = this.createElement("cell");
        Rectangle2D bounds = cell.getBounds();
        String x = String.valueOf(bounds.getX());
        String y = String.valueOf(bounds.getY());
        String width = String.valueOf(bounds.getWidth());
        String height = String.valueOf(bounds.getHeight());
        String id = cell.getBase().getIdString();
        this.setAttribute(element, "id", id);
        this.setAttribute(element, "x", x);
        this.setAttribute(element, "y", y);
        this.setAttribute(element, "width", width);
        this.setAttribute(element, "height", height);
        return element;
    }

    public void elementToAssignmentGraphical(AssignmentModelView view, AssignmentModel model, Element element) {
        Element assignment = super.getAssignmentElement(element);
        Element graphTag = this.getFirstElement(assignment, "graphical");
        Element cellsTag = this.getFirstElement(graphTag, "activities");
        Element connectorsTag = this.getFirstElement(graphTag, "constraints");
        NodeList cells = cellsTag.getElementsByTagName("cell");

        int i;
        Element cell;
        int id;
        Rectangle2D bounds;
        for(i = 0; i < cells.getLength(); ++i) {
            cell = (Element)cells.item(i);
            id = Integer.valueOf(cell.getAttribute("id")).intValue();
            bounds = this.elementToBouds(cell);
            ActivityDefinition constraint = model.activityDefinitionWithId(id);
            view.getActivityDefinitionCell(constraint).setBounds(bounds);
        }

        cells = connectorsTag.getElementsByTagName("cell");

        for(i = 0; i < cells.getLength(); ++i) {
            cell = (Element)cells.item(i);
            id = Integer.valueOf(cell.getAttribute("id")).intValue();
            bounds = this.elementToBouds(cell);
            ConstraintDefinition var14 = model.constraintWithId(id);
            view.getConnector(var14).setBounds(bounds);
        }

        view.updateUI();
    }

    private Rectangle2D elementToBouds(Element element) {
        String x = element.getAttribute("x");
        String y = element.getAttribute("y");
        String width = element.getAttribute("width");
        String height = element.getAttribute("height");
        double dx = Double.valueOf(x).doubleValue();
        double dy = Double.valueOf(y).doubleValue();
        double dwidth = Double.valueOf(width).doubleValue();
        double dheight = Double.valueOf(height).doubleValue();
        return new java.awt.geom.Rectangle2D.Double(dx, dy, dwidth, dheight);
    }
}
