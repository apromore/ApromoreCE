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

package org.apromore.processdiscoverer.dfg;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class Arc implements Comparable<Arc>{
    private final int source;
    private final int target;
    private Integer hashCode = null;

    public Arc(int source, int target) {
        this.source = source;
        this.target = target;
    }

    public int getSource() {
        return source;
    }

    public int getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Arc) {
            Arc arc = (Arc) obj;
            return source == arc.source && target == arc.target;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if(hashCode == null) {
            HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
            hashCode = hashCodeBuilder.append(source).append(target).hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return "(" + source + ", " + target + ")";
    }

    @Override
    public int compareTo(Arc o) {
        int compare = Integer.compare(source, o.source);
        if(compare == 0) return Integer.compare(target, o.target);
        return compare;
    }
}
