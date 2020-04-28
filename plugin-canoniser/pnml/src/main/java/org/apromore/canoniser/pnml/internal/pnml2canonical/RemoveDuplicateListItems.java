/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
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

package org.apromore.canoniser.pnml.internal.pnml2canonical;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class RemoveDuplicateListItems {

    /**
     * Sort a list and remove one of each distinct string which occurs.
     *
     * E.g. [a,b,r,a,c,a,d,a,b,r,a] becomes [a,a,a,a,b,r]
     *
     * @param list  will be mutated, may not be <code>null</code>, must support the options {@link List.remove} operation
     * @return <var>list</var>, redundantly since the parameter is mutated into the result as well
     */
    public static List<String> transform(List<String> list) {
        Collections.sort(list);
        String lastValue = null;
        for (Iterator<String> i = list.iterator(); i.hasNext(); ) {
            String currentValue = i.next();
            if (lastValue != null && currentValue.equals(lastValue)) {

            } else {
                i.remove();
            }
            lastValue = currentValue;
        }
        return list;
    }

}
