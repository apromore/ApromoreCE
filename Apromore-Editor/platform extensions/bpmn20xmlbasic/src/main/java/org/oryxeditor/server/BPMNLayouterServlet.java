/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.oryxeditor.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import de.hpi.layouting.grid.Grid;
import de.hpi.layouting.model.LayoutingBounds;
import de.hpi.layouting.model.LayoutingBoundsImpl;
import de.hpi.layouting.model.LayoutingDockers.Point;
import de.hpi.layouting.model.LayoutingElement;
import de.unihannover.se.infocup2008.bpmn.dao.JSONDiagramDao;
import de.unihannover.se.infocup2008.bpmn.layouter.CatchingIntermediateEventLayouter;
import de.unihannover.se.infocup2008.bpmn.layouter.EdgeLayouter;
import de.unihannover.se.infocup2008.bpmn.layouter.LeftToRightGridLayouter;
import de.unihannover.se.infocup2008.bpmn.layouter.topologicalsort.TopologicalSorterBPMN;
import de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram;
import de.unihannover.se.infocup2008.bpmn.model.BPMNElement;
import de.unihannover.se.infocup2008.bpmn.model.BPMNType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BPMNLayouterServlet extends HttpServlet {

    private static final long serialVersionUID = -5881072861254329384L;

    protected BPMNDiagram diagram;
    private Map<BPMNElement, Grid<BPMNElement>> grids;
    private List<BPMNElement> subprocessOrder;


    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        grids = new HashMap<>();

        request.setCharacterEncoding("UTF-8");
        String jsonmodel = request.getParameter("data");

        JSONObject jsonModel;
        try {
            jsonModel = new JSONObject(jsonmodel);
            this.diagram = new JSONDiagramDao().getDiagramFromJSON(jsonModel);
        } catch (JSONException e1) {
            response.setStatus(500);
            response.getWriter().print("import of json failed:");
            e1.printStackTrace(response.getWriter());
            return;
        }

        if (this.diagram == null) {
            response.setStatus(500);
            response.getWriter().print("import failed");
            return;
        }

        try {
            doLayoutAlgorithm();
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().print("layout failed:");
            e.printStackTrace(response.getWriter());
            return;
        }

        response.setStatus(200);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/xhtml");

        if (request.getParameter("output") != null && request.getParameter("output").equals("coordinatesonly")) {
            JSONArray json = new JSONArray();

            try {
                for (String id : this.diagram.getElements().keySet()) {
                    BPMNElement element = (BPMNElement) this.diagram.getElement(id);
                    JSONObject obj = new JSONObject();
                    obj.put("id", id);

                    LayoutingBounds bounds = element.getGeometry();
                    String boundsString = bounds.getX() + " " + bounds.getY() + " " + bounds.getX2() + " " + bounds.getY2();
                    obj.put("bounds", boundsString);

                    if (BPMNType.isAConnectingElement(element.getType()) || BPMNType.isACatchingIntermediateEvent(element.getType())) {
                        if (element.getDockers() != null) {
                            obj.put("dockers", buildDockersArray(element));
                        } else {
                            obj.put("dockers", JSONObject.NULL);
                        }
                    }

                    json.put(obj);
                }
                json.write(response.getWriter());
            } catch (JSONException e) {
                response.getWriter().print("exception: " + e.toString());
            }
        } else {
            try {
                jsonModel.write(response.getWriter());
            } catch (JSONException e) {
                response.setStatus(500);
                response.getWriter().print("json export failed:");
                e.printStackTrace(response.getWriter());
            }
        }
    }

    private JSONArray buildDockersArray(BPMNElement element) {
        JSONArray dockers = new JSONArray();
        for (Point p : element.getDockers().getPoints()) {
            JSONObject point = new JSONObject();
            try {
                point.put("x", p.x);
                point.put("y", p.y);
                dockers.put(point);
            } catch (JSONException e) {
                // Do nothing
            }
        }
        return dockers;
    }

    protected void doLayoutAlgorithm() {
        preprocessHeuristics();

        // Layouting subprocesses
        calcLayoutOrder();
        for (BPMNElement subProcess : subprocessOrder) {
            LeftToRightGridLayouter lToRGridLayouter = layoutProcess(subProcess);

            // set bounds
            double subprocessWidth = lToRGridLayouter.getWidthOfDiagramm();
            double subprocessHeight = lToRGridLayouter.getHeightOfDiagramm();
            subProcess.setGeometry(new LayoutingBoundsImpl(0, 0, subprocessWidth,subprocessHeight));
            grids.putAll(lToRGridLayouter.getGridParentMap());
        }

        // Layouting main process
        LeftToRightGridLayouter lToRGridLayouter = layoutProcess(null);
        grids.putAll(lToRGridLayouter.getGridParentMap());
        calcLayoutOrder();

        CatchingIntermediateEventLayouter.setCatchingIntermediateEvents(diagram);

        // Setting edges
        List<LayoutingElement> flows = diagram.getConnectingElements();
        for (LayoutingElement flow : flows) {
            new EdgeLayouter(this.grids, (BPMNElement) flow);
        }
    }

    private LeftToRightGridLayouter layoutProcess(BPMNElement parent) {
        // Sorting elements topologicaly
        Queue<LayoutingElement> sortedElements = new TopologicalSorterBPMN(diagram, parent).getSortedElements();

        // Sorted
        List<String> sortedIds = new LinkedList<>();
        for (LayoutingElement element : sortedElements) {
            sortedIds.add(element.getId());
        }

        // Layouting from left to right using grid
        LeftToRightGridLayouter lToRGridLayouter = new LeftToRightGridLayouter(sortedIds, parent);
        lToRGridLayouter.setDiagram(diagram);
        lToRGridLayouter.doLayout();

        return lToRGridLayouter;
    }

    /**
     * calculates the nesting order of lanes and subprocesses
     */
    private void calcLayoutOrder() {
        subprocessOrder = new LinkedList<>();
        processChilds(null);
        Collections.reverse(subprocessOrder);
    }

    /**
     * @param parent the element to process the childs from
     */
    private void processChilds(BPMNElement parent) {
        for (LayoutingElement c : this.diagram.getChildElementsOf(parent)) {
            BPMNElement child = (BPMNElement) c;
            String childType = child.getType();
            if (childType.equals(BPMNType.Subprocess)) {
                subprocessOrder.add(child);
                processChilds(child);
            }
        }
    }

    private void preprocessHeuristics() {
        // turn direction of associations to text annotations towards them
        // so that they are right of the elements
        for (LayoutingElement textAnnotation : this.diagram.getElementsOfType(BPMNType.TextAnnotation)) {
            List<LayoutingElement> outgoingLinks = textAnnotation.getOutgoingLinks();
            for (LayoutingElement edge : outgoingLinks.toArray(new LayoutingElement[outgoingLinks.size()])) {
                LayoutingElement target = edge.getOutgoingLinks().get(0);
                // remove old connection
                textAnnotation.removeOutgoingLink(edge);
                target.removeIncomingLink(edge);

                // reconnect properly
                target.addOutgoingLink(textAnnotation);
                textAnnotation.addIncomingLink(target);
            }
        }
    }


//    protected static String jsonToErdf(String json) {
//        JsonErdfTransformation trans = new JsonErdfTransformation(json);
//        return trans.toString();
//    }
}
