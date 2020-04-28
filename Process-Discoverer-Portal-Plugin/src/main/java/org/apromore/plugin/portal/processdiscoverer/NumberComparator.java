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

package org.apromore.plugin.portal.processdiscoverer;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import java.util.Comparator;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class NumberComparator implements Comparator<Object> {

    private boolean ascending;
    private int columnIndex;

    public NumberComparator(boolean ascending, int columnIndex) {
        this.ascending = ascending;
        this.columnIndex = columnIndex;
    }

    @Override
    public int compare(Object o1, Object o2) {
        Double contributor1 = Double.parseDouble(((Listcell) ((Listitem) o1).getChildren().get(columnIndex)).getLabel());
        Double contributor2 = Double.parseDouble(((Listcell) ((Listitem) o2).getChildren().get(columnIndex)).getLabel());
        return contributor1.compareTo(contributor2) * (ascending ? 1 : -1);
    }

}