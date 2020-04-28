/*-
 * #%L
 * This file is part of "Apromore Community".
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
package org.apromore.plugin.portal.logfilter.util;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import java.util.Comparator;

/**
 * @author Bruce Hoang Nguyen (29/08/2019)
 * Modified by Chii Chang (28/01/2020)
 */
public class StringComparator implements Comparator<Object> {

    private boolean ascending;
    private int columnIndex;

    public StringComparator(boolean ascending, int columnIndex) {
        this.ascending = ascending;
        this.columnIndex = columnIndex;
    }

    @Override
    public int compare(Object listitem_1, Object listitem_2) {
        String cellLabel_1 = ((Listcell) ((Listitem) listitem_1).getChildren().get(columnIndex)).getLabel();
        String cellLabel_2 = ((Listcell) ((Listitem) listitem_2).getChildren().get(columnIndex)).getLabel();
        return cellLabel_1.compareTo(cellLabel_2) * (ascending ? 1 : -1);
    }
}
