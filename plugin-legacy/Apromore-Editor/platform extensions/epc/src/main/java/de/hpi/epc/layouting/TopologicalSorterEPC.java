/*-
 * #%L
 * This file is part of "Apromore Community".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package de.hpi.epc.layouting;

import de.hpi.epc.layouting.model.EPCElement;
import de.hpi.epc.layouting.model.EPCType;
import de.hpi.layouting.model.LayoutingDiagram;
import de.hpi.layouting.model.LayoutingElement;
import de.hpi.layouting.topologicalsort.SortableLayoutingElement;
import de.hpi.layouting.topologicalsort.TopologicalSorter;

public class TopologicalSorterEPC extends TopologicalSorter {

    public TopologicalSorterEPC(LayoutingDiagram diagram,
                                LayoutingElement parent) {
        super(diagram, parent);
    }

    @Override
    /**
     * @param parent
     */
    protected void addAllChilds(LayoutingElement parent) {
        for (LayoutingElement el : diagram.getElements().values()) {
            EPCElement element = (EPCElement) el;
            // LayoutingElement element = diagram.getElement(id);
            if (!EPCType.isAConnectingElement(element.getType())) {
                elementsToSort.put(element.getId(), new SortableLayoutingElement(
                        element));
            }
        }
    }

}
