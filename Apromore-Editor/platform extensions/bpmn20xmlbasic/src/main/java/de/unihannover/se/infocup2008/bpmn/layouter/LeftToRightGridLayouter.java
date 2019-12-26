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
package de.unihannover.se.infocup2008.bpmn.layouter;

import de.hpi.layouting.grid.Grid;
import de.hpi.layouting.grid.Grid.Cell;
import de.hpi.layouting.grid.Grid.Row;
import de.hpi.layouting.grid.SuperGrid;
import de.hpi.layouting.model.LayoutingBounds;
import de.hpi.layouting.model.LayoutingBoundsImpl;
import de.hpi.layouting.model.LayoutingElement;
import de.unihannover.se.infocup2008.bpmn.model.BPMNDiagram;
import de.unihannover.se.infocup2008.bpmn.model.BPMNElement;
import de.unihannover.se.infocup2008.bpmn.model.BPMNType;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

/**
 * The main layout algorithem. Places the elements from left to right into a
 * <tt>Grid</tt> if they a sequencial. Parallel Elements a placed from top to
 * bottom. Expects a list of id's sorted in a way that an element, that must be
 * layouted before another element, is also before that element in the list.
 *
 * @author Team Royal Fawn
 */
public class LeftToRightGridLayouter implements Layouter {

    private static final int COLLAPSED_POOL_HEIGHT = 70;
    private static final int COLLAPSED_POOL_MIN_WIDTH = 300;
    private static final int CELL_MARGIN = 30; // calc biggest elements in row /
    // column
    private static final int CELL_HEIGHT = 0;
    private static final int CELL_WIDTH = 0;
    public static final int LANE_HEAD_WIDTH = 30; // Oryx-Konstante

    /**
     * Compares th distance of two elements relativ to an third <tt>center</tt>
     * element
     *
     * @author Team Royal Fawn
     */
    private static class BackwardDistanceComperator implements
            Comparator<BPMNElement> {

        private BPMNElement ce;

        /**
         * @param center
         */
        private BackwardDistanceComperator(BPMNElement center) {
            super();
            this.ce = center;
        }

        public int compare(BPMNElement o1, BPMNElement o2) {
            return ce.backwardDistanceTo(o1) - ce.backwardDistanceTo(o2);

        }

    }

    private static class GridContext {
        private Grid<BPMNElement> grid;
        private Cell<BPMNElement> startCell;
    }

    private List<String> orderedIds;
    private BPMNDiagram diagram;
    private SuperGrid<BPMNElement> superGrid;
    private BPMNElement parent;
    private double[] heightOfRow;
    private double heightOfSuperGrid = 0;
    private double[] widthOfColumn;
    private double widthOfSuperGrid = 0;
    private HashMap<BPMNElement, GridContext> parent2Context;
    private HashMap<BPMNElement, List<BPMNElement>> lane2LaneChilds;
    private int maxLaneDepth;
    private int poolWidth;

    public LeftToRightGridLayouter(List<String> orderedIds, BPMNElement parent) {
        this.orderedIds = orderedIds;
        this.parent = parent;
        this.parent2Context = new HashMap<BPMNElement, GridContext>();
        this.lane2LaneChilds = new HashMap<BPMNElement, List<BPMNElement>>();
    }

    private GridContext getContextByElement(BPMNElement el) {
        BPMNElement elParent = null;
        if (el != null) {
            elParent = (BPMNElement) el.getParent();
        }
        GridContext result = parent2Context.get(elParent);
        if (result == null) {
            result = new GridContext();
            result.grid = new Grid<BPMNElement>();
            result.startCell = result.grid.getFirstRow().getFirstCell();
            superGrid.add(result.grid);
            parent2Context.put(elParent, result);
        }
        return result;
    }

    public SuperGrid<BPMNElement> getSuperGrid() {
        return superGrid;
    }

    public void doLayout() {
        superGrid = new SuperGrid<BPMNElement>();
        parent2Context.clear();
        lane2LaneChilds.clear();
        maxLaneDepth = 0;

        if (parent == null) {
            for (LayoutingElement pool : diagram.getElementsOfType(BPMNType.Pool)) {
                prepareLanes((BPMNElement) pool, 1);
            }
        }

        layoutElements();

        if (parent == null) {
            // set collapsed pools
            List<LayoutingElement> collapsedPools = this.diagram
                    .getElementsOfType(BPMNType.CollapsedPool);
            Grid<BPMNElement> cpGrid = new Grid<BPMNElement>();
            superGrid.add(0, cpGrid);
            for (LayoutingElement collapsedPool : collapsedPools) {
                // make them small to not disturb finding the biggest ones in
                // each row / column

                collapsedPool.setGeometry(new LayoutingBoundsImpl(0, 0, 0,
                        COLLAPSED_POOL_HEIGHT));
                for (Cell<BPMNElement> insertCell : cpGrid.addLastRow()) {
                    insertCell.setValue((BPMNElement) collapsedPool);
                }

            }

            calcGeometry(superGrid);

            poolWidth = Math.max(poolWidth, COLLAPSED_POOL_MIN_WIDTH);

            // place Lanes
            for (LayoutingElement pool : diagram.getElementsOfType(BPMNType.Pool)) {
                Grid<BPMNElement> firstGrid = findFirstGridOfPool((BPMNElement) pool);
                int firstGridFirstRowIndex = superGrid.findRow(firstGrid
                        .getFirstRow());

                double poolY = 0;
                for (int i = 0; i < firstGridFirstRowIndex; i++) {
                    poolY += heightOfRow[i];
                }
                placeLane((BPMNElement) pool, poolY, 0);
            }

            writeGeometry(superGrid);

            // set pools to start at x = CELL_MARGIN & correct size

            for (LayoutingElement collapsedPool : collapsedPools) {
                collapsedPool
                        .setGeometry(new LayoutingBoundsImpl(CELL_MARGIN, collapsedPool
                                .getGeometry().getY(), poolWidth,
                                COLLAPSED_POOL_HEIGHT));
                ((BPMNElement) collapsedPool).updateDataModel();
            }

            // convert Coordinates of Elements in Lanes from absolut to
            // realitive
            for (LayoutingElement pool : diagram.getElementsOfType(BPMNType.Pool)) {
                correctLaneElements((BPMNElement) pool, pool.getGeometry().getY(), 0);
            }

        } else {
            calcGeometry(superGrid);
            writeGeometry(superGrid);
        }
    }

    /**
     * @param pool
     * @return
     */
    private Grid<BPMNElement> findFirstGridOfPool(BPMNElement pool) {
        List<BPMNElement> childs = lane2LaneChilds.get(pool);
        if (childs.isEmpty()) {
            return parent2Context.get(pool).grid;
        } else {
            return findFirstGridOfPool(childs.get(0));
        }
    }

    private void prepareLanes(BPMNElement lane, int level) {
        maxLaneDepth = Math.max(maxLaneDepth, level);
        List<BPMNElement> childs = new ArrayList<BPMNElement>();
        BPMNElement aChild = null;
        for (LayoutingElement c : diagram.getChildElementsOf(lane)) {
            BPMNElement child = (BPMNElement) c;
            if (BPMNType.isASwimlane(child.getType())) {
                prepareLanes(child, level + 1);
                childs.add(child);
            }
            aChild = child;
        }
        // Create Grid for lane (=
        // aChild.getParent())
        if (aChild != null) {
            getContextByElement(aChild);
        } else {
            //create empty grid for empty lanes
            // to prevent nullpointer-exception
            GridContext result = new GridContext();
            result.grid = new Grid<BPMNElement>();
            result.startCell = result.grid.getFirstRow().getFirstCell();
            superGrid.add(result.grid);
            parent2Context.put(lane, result);
        }
        lane2LaneChilds.put(lane, childs);
    }

    /**
     * @param lane
     * @param relY
     * @param level
     * @return height of lane
     */
    private double placeLane(BPMNElement lane, double relY, int level) {

        List<BPMNElement> childs = lane2LaneChilds.get(lane);
        double height = 0;
        for (BPMNElement child : childs) {
            height += placeLane(child, height, level + 1);
        }

        int width = poolWidth - level * LANE_HEAD_WIDTH;
        Grid<BPMNElement> myGrid = parent2Context.get(lane).grid;
        int firstRow = superGrid.findRow(myGrid.getFirstRow());
        int lastRow = superGrid.findRow(myGrid.getLastRow());
        for (int i = firstRow; i <= lastRow; i++) {
            height += heightOfRow[i];
        }

        double minHeight = lane.getGeometry().getHeight();
        if (level == 0) {
            minHeight += CELL_MARGIN / 2;
        }
        double diff = minHeight - height;
        if (diff > 1.0) {
            firstRow = superGrid.findRow(findFirstGridOfPool(lane)
                    .getFirstRow());
            double toAdd = diff / (lastRow - firstRow + 1.0);
            for (int i = firstRow; i <= lastRow; i++) {
                heightOfRow[i] += toAdd;
            }
            // Redo placement
            return placeLane(lane, relY, level);
        }
        if (level == 0) {
            //pool with magin
            lane.setGeometry(new LayoutingBoundsImpl(CELL_MARGIN, relY + (CELL_MARGIN / 4), width, height - (CELL_MARGIN / 2)));
        } else {
            //lane without margin
            lane.setGeometry(new LayoutingBoundsImpl(CELL_MARGIN, relY, width, height));
        }
        lane.updateDataModel();
        return height;
    }

    /**
     * @param lane
     * @param absY
     * @param level
     */
    private void correctLaneElements(BPMNElement lane, double absY, int level) {
        List<BPMNElement> childs = lane2LaneChilds.get(lane);
        double height = 0;
        for (BPMNElement child : childs) {
            correctLaneElements(child, absY + height, level + 1);
            height += child.getGeometry().getHeight();
        }

        int xTrans = level * -LANE_HEAD_WIDTH;
        for (LayoutingElement content : diagram.getChildElementsOf(lane)) {
            if (!BPMNType.isASwimlane(content.getType())) {
                LayoutingBounds geom = content.getGeometry();
                content.setGeometry(new LayoutingBoundsImpl(geom.getX() + xTrans,
                        geom.getY() - absY, geom.getWidth(), geom.getHeight()));
                ((BPMNElement) content).updateDataModel();
            }
        }
    }

    private void layoutElements() {
        //System.out.println();

        for (String id : this.orderedIds) {
            //System.out.println(id);
            BPMNElement currentElement = (BPMNElement) this.diagram.getElement(id);
            List<LayoutingElement> precedingElements = currentElement
                    .getPrecedingElements();
            GridContext context = getContextByElement(currentElement);
            Cell<BPMNElement> cellOfElement = null;
            cellOfElement = placeElement(currentElement, precedingElements, context);

            boolean comesFromOtherGrid = precedingElements.size() == 1
                    && precedingElements.get(0).getParent() != currentElement
                    .getParent();
            if (!currentElement.isJoin() && !comesFromOtherGrid
                    && cellOfElement.getPrevCell() != null) {
                // there is an edge hitting us left, so lets forbid
                // interleaving to use the left cell, if it's empty
                cellOfElement.getPrevCell().setPackable(false);
            }

            if (currentElement.isSplit()) {
                prelayoutSuccessors(currentElement, context, cellOfElement);
            }

            if (BPMNType.isAActivity(currentElement.getType())) {
                // search for attached events
                for (LayoutingElement e : currentElement.getOutgoingLinks()) {
                    if (BPMNType.isACatchingIntermediateEvent(e.getType())) {
                        context.grid.setCellOfItem((BPMNElement) e, cellOfElement);
                    }
                }
            }
        }
    }

    /**
     * @param currentElement
     * @param context
     * @param cellOfElement
     */
    private void prelayoutSuccessors(BPMNElement currentElement,
                                     GridContext context, Cell<BPMNElement> cellOfElement) {
        // preLayout following Elements
        Cell<BPMNElement> baseCell = cellOfElement.after();
        Cell<BPMNElement> topCell = baseCell;
        List<LayoutingElement> followingElements = currentElement
                .getFollowingElements();

        if (BPMNType.isAActivity(currentElement.getType())) {
            // special case for docked events
            List<BPMNElement> dockedEventFollowers = new LinkedList<BPMNElement>();
            for (LayoutingElement el : currentElement.getOutgoingLinks()) {
                BPMNElement element = (BPMNElement) el;
                if (element.isADockedIntermediateEvent()) {
                    for (LayoutingElement follower : element.getFollowingElements()) {
                        dockedEventFollowers.add((BPMNElement) follower);
                    }
                }
            }
            // to avoid crossing edges if there is more than one
            // docked event
            Collections.reverse(dockedEventFollowers);

            // put them under the task
            Cell<BPMNElement> insertCell = baseCell;
            for (BPMNElement dockedEventFollower : dockedEventFollowers) {
                Cell<BPMNElement> oldCell = context.grid
                        .getCellOfItem(dockedEventFollower);
                if (oldCell != null) {
                    if (oldCell.getValue() == dockedEventFollower) {
                        continue; // Bug-Workaround: Don't prelayout
                        // layouted elements;
                    }
                }
                insertCell.getParent().insertRowBeneath();
                insertCell = insertCell.beneath();
                context.grid.setCellOfItem(dockedEventFollower, insertCell); // prelayout
            }

            // remove them from the following processing
            followingElements.removeAll(dockedEventFollowers);

        }

        // heuristic for text- & data-objects: put them to the top
        List<BPMNElement> textAnnotations = new LinkedList<BPMNElement>();
        List<BPMNElement> dataObjects = new LinkedList<BPMNElement>();
        for (LayoutingElement el : followingElements) {
            BPMNElement e = (BPMNElement) el;
            if (e.getType().equals(BPMNType.TextAnnotation)) {
                textAnnotations.add(e);
            } else if (e.getType().equals(BPMNType.DataObject)) {
                dataObjects.add(e);
            }
        }
        followingElements.removeAll(textAnnotations);
        followingElements.removeAll(dataObjects);
        // add them at the front
        followingElements.addAll(0, dataObjects);
        followingElements.addAll(0, textAnnotations);

        // heuristic for direct connection to join
        BPMNElement directJoin = null;
        for (LayoutingElement possibleJoin : followingElements) {
            if (possibleJoin.isJoin()) {
                directJoin = (BPMNElement) possibleJoin;
            }
        }
        if (directJoin != null) {
            // put in the middle
            followingElements.remove(directJoin);
            int position = (followingElements.size() / 2);
            followingElements.add(position, directJoin);
        }

        // normal preLayout following Elements
        int follow = 0;
        for (LayoutingElement newElem : followingElements) {
            if (newElem.getParent() == currentElement.getParent()) {
                follow++;
            }
        }
        for (int i = 0; i < follow / 2; i++) {
            topCell.getParent().insertRowAbove();
            baseCell.getParent().insertRowBeneath();
            topCell = topCell.above();
        }

        for (LayoutingElement newElem : followingElements) {
            if (newElem.getParent() != currentElement.getParent()) {
                continue;
            }
            context.grid.setCellOfItem((BPMNElement) newElem, topCell); // prelayout
            topCell = topCell.beneath();
            if (topCell == baseCell && follow % 2 == 0) {
                // skip baseCell if an even amount of elements is
                // following
                topCell = topCell.beneath();
            }
        }
    }

    /**
     * @param currentElement
     * @param precedingElements
     * @param context
     * @return cellOfElement
     */
    private Cell<BPMNElement> placeElement(BPMNElement currentElement,
                                           List<LayoutingElement> precedingElements, GridContext context) {
        Cell<BPMNElement> newCell;
        if (precedingElements.isEmpty()) {
            // StartEvents
            context.startCell.setValue(currentElement);
            newCell = context.startCell;
            context.startCell = context.startCell.beneath();
        } else {
            Cell<BPMNElement> leftCell;
            newCell = context.grid.getCellOfItem(currentElement); // not
            // null
            // if
            // join
            if (currentElement.isJoin()) {

                Point tmp;
                boolean splitFound = false;
                BPMNElement split = (BPMNElement) currentElement.prevSplit();
                if (split != null) {
                    // get all close splits
                    Queue<BPMNElement> splits = new PriorityQueue<BPMNElement>(
                            precedingElements.size() / 2, // should be a
                            // good rule of
                            // thumb
                            new BackwardDistanceComperator(currentElement));
                    splits.add(split);
                    for (LayoutingElement elem : precedingElements) {
                        split = (BPMNElement) elem.prevSplit();
                        if (split != null && !splits.contains(split)) {
                            splits.add(split);
                        }
                    }
                    split = null;
                    // get split with most connections
                    int maxCon = 0;
                    for (BPMNElement target : splits) {
                        if (target == currentElement) {
                            // beeing my own splits only makes trouble
                            continue;
                        } else if (target.getParent() != currentElement
                                .getParent()) {

                            continue;
                        }
                        int curCon = 0;
                        for (LayoutingElement elem : precedingElements) {
                            if (elem.backwardDistanceTo(target) < Integer.MAX_VALUE) {
                                curCon++;
                            }
                        }
                        if (curCon > maxCon) {
                            maxCon = curCon;
                            split = target;
                        }
                    }
                    splitFound = split != null;
                }

                int x = 0;
                int yAcc = 0;
                int yCnt = 0;
                for (LayoutingElement el : precedingElements) {
                    BPMNElement elem = (BPMNElement) el;
                    tmp = context.grid.find(context.grid.getCellOfItem(elem));
                    if (tmp == null) {
                        Grid<BPMNElement> preGrid = getContextByElement(elem).grid;
                        tmp = preGrid.find(preGrid.getCellOfItem(elem));
                        if (tmp == null) {
                            tmp = new Point(0, 0);
                        }
                    } else {
                        yAcc += tmp.y;
                        yCnt++;
                    }
                    x = Math.max(x, tmp.x);
                }
                if (splitFound) {

                    leftCell = context.grid.getCellOfItem(split).getParent()
                            .get(x);
                    // set path to split unpackable
                    for (Cell<BPMNElement> cCell = leftCell; cCell.getValue() != split; cCell = cCell
                            .getPrevCell()) {
                        cCell.setPackable(false);
                    }

                } else {
                    if (yCnt == 0) {
                        leftCell = context.grid.getFirstRow().above().get(x);
                    } else {
                        leftCell = context.grid.get(yAcc / yCnt).get(x);
                    }
                }
                if (newCell != null && newCell.getValue() == currentElement) {
                    newCell.setValue(null);
                }
                newCell = leftCell.after();

                // set all incoming pathes unpackable
                for (LayoutingElement e : precedingElements) {
                    BPMNElement el = (BPMNElement) e;
                    Cell<BPMNElement> target = context.grid.getCellOfItem(el);
                    if (target == null) {
                        // don't set unpackable in other grids (other edge
                        // layout)
                        continue;
                    }
                    Cell<BPMNElement> start = target.getParent().get(x + 1);
                    for (Cell<BPMNElement> cCell = start; cCell != target; cCell = cCell
                            .getPrevCell()) {
                        cCell.setPackable(false);
                    }
                }

                // if not prelayouted
            } else if (newCell == null) {
                BPMNElement preElem = (BPMNElement) precedingElements.get(0);
                leftCell = context.grid.getCellOfItem(preElem);
                if (leftCell == null) {
                    Grid<BPMNElement> preGrid = getContextByElement(preElem).grid;
                    Cell<BPMNElement> preCell = preGrid.getCellOfItem(preElem);
                    if (preCell == null) {
                        System.err.println("Cannot find Cell for " + preElem);
                    }

                    List<Grid<BPMNElement>> grids = superGrid.getGrids();
                    Row<BPMNElement> newRow = null;
                    if (grids.indexOf(preGrid) < grids.indexOf(context.grid)) {
                        newRow = context.grid.addFirstRow();
                    } else {
                        newRow = context.grid.addLastRow();
                    }
                    leftCell = newRow.get(Math.max(0, preCell.getParent().find(
                            preCell)));
                }
                newCell = leftCell.after();
            }
            if (newCell.isFilled()
                    && !newCell.getValue().equals(currentElement)) {
                newCell.getParent().insertRowBeneath();
                newCell = newCell.beneath();
            }
            newCell.setValue(currentElement);
        }
        return newCell;
    }

    /**
     * @param grids
     */
    private void calcGeometry(SuperGrid<BPMNElement> grids) {
        grids.pack();
        heightOfRow = new double[grids.getHeight()];
        widthOfColumn = new double[grids.getWidth()];
        // initialize with standard values
        Arrays.fill(heightOfRow, CELL_HEIGHT);
        Arrays.fill(widthOfColumn, CELL_WIDTH);
        // find biggest
        int row = 0;
        int column = 0;
        for (Row<BPMNElement> r : grids) {
            column = 0;
            for (Cell<BPMNElement> c : r) {
                if (c.isFilled()) {
                    BPMNElement elem = c.getValue();
                    LayoutingBounds geom = elem.getGeometry();
                    widthOfColumn[column] = Math.max(widthOfColumn[column],
                            geom.getWidth() + CELL_MARGIN);
                    heightOfRow[row] = Math.max(heightOfRow[row], geom
                            .getHeight()
                            + CELL_MARGIN);
                }
                column++;
            }
            row++;
        }

        // calc width / height
        widthOfSuperGrid = 0;
        for (double columnWidth : widthOfColumn) {
            widthOfSuperGrid += columnWidth;
        }
        heightOfSuperGrid = 0;
        for (double rowHeight : heightOfRow) {
            heightOfSuperGrid += rowHeight;
        }

        poolWidth = maxLaneDepth * LANE_HEAD_WIDTH;
        poolWidth += widthOfSuperGrid;
    }

    /**
     * @param grids
     */
    private void writeGeometry(SuperGrid<BPMNElement> grids) {
        // write cells
        double x = 0;
        double y = 0;
        int row = 0;
        int column = 0;
        for (Row<BPMNElement> r : grids) {
            column = 0;
            double cellHeight = heightOfRow[row];
            for (Cell<BPMNElement> c : r) {
                double cellWidth = widthOfColumn[column];
                if (c.isFilled()) {
                    BPMNElement elem = c.getValue();
                    LayoutingBounds geom = elem.getGeometry();
                    double newX = x + (cellWidth / 2.0)
                            - (geom.getWidth() / 2.0) + maxLaneDepth
                            * LANE_HEAD_WIDTH;
                    double newY = y + (cellHeight / 2.0)
                            - (geom.getHeight() / 2.0);

                    elem.setGeometry(new LayoutingBoundsImpl(newX, newY, geom
                            .getWidth(), geom.getHeight()));
                    elem.updateDataModel();
                }
                x += cellWidth;
                column++;
            }
            x = 0;
            y += cellHeight;
            row++;
        }
    }

    public void setDiagram(BPMNDiagram diagram) {
        this.diagram = diagram;
    }

    public Map<BPMNElement, Grid<BPMNElement>> getGridParentMap() {
        // Converts parent2Context into a Map<BPMNElement, Grid<BPMNElement>>
        return new AbstractMap<BPMNElement, Grid<BPMNElement>>() {
            @Override
            public Set<java.util.Map.Entry<BPMNElement, Grid<BPMNElement>>> entrySet() {
                return new AbstractSet<java.util.Map.Entry<BPMNElement, Grid<BPMNElement>>>() {
                    @Override
                    public Iterator<java.util.Map.Entry<BPMNElement, Grid<BPMNElement>>> iterator() {
                        final Iterator<java.util.Map.Entry<BPMNElement, GridContext>> iterator = parent2Context
                                .entrySet().iterator();
                        return new Iterator<Entry<BPMNElement, Grid<BPMNElement>>>() {
                            public boolean hasNext() {
                                return iterator.hasNext();
                            }

                            public java.util.Map.Entry<BPMNElement, Grid<BPMNElement>> next() {
                                final Map.Entry<BPMNElement, GridContext> entry = iterator
                                        .next();
                                return new Map.Entry<BPMNElement, Grid<BPMNElement>>() {
                                    public BPMNElement getKey() {
                                        return entry.getKey();
                                    }

                                    public Grid<BPMNElement> getValue() {
                                        return entry.getValue().grid;
                                    }

                                    public Grid<BPMNElement> setValue(
                                            Grid<BPMNElement> value) {
                                        throw new UnsupportedOperationException();
                                    }

                                    @Override
                                    public int hashCode() {
                                        return (this.getKey() == null ? 0
                                                : this.getKey().hashCode())
                                                ^ (this.getValue() == null ? 0
                                                : this.getValue()
                                                .hashCode());
                                    }

                                    @SuppressWarnings("unchecked")
                                    @Override
                                    public boolean equals(Object o) {
                                        if (o == null || !(o instanceof Entry)) {
                                            return false;
                                        }
                                        Entry e2 = (Entry) o;
                                        return (this.getKey() == null ? e2
                                                .getKey() == null : this
                                                .getKey().equals(e2.getKey()))
                                                && (this.getValue() == null ? e2
                                                .getValue() == null
                                                : this
                                                .getValue()
                                                .equals(
                                                        e2
                                                                .getValue()));
                                    }
                                };
                            }

                            public void remove() {
                                throw new UnsupportedOperationException();
                            }
                        };
                    }

                    @Override
                    public int size() {
                        return parent2Context.size();
                    }
                };
            }
        };
    }

    /**
     * @return the heightOfDiagramm
     */
    public double getHeightOfDiagramm() {
        return heightOfSuperGrid;
    }

    /**
     * @return the widthOfDiagramm
     */
    public double getWidthOfDiagramm() {
        return widthOfSuperGrid;
    }
}
