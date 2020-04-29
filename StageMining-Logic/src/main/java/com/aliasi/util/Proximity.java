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
/*
 * LingPipe v. 4.1.0
 * Copyright (C) 2003-2011 Alias-i
 *
 * This program is licensed under the Alias-i Royalty Free License
 * Version 1 WITHOUT ANY WARRANTY, without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Alias-i
 * Royalty Free License Version 1 for more details.
 *
 * You should have received a copy of the Alias-i Royalty Free License
 * Version 1 along with this program; if not, visit
 * http://alias-i.com/lingpipe/licenses/lingpipe-license-1.txt or contact
 * Alias-i, Inc. at 181 North 11th Street, Suite 401, Brooklyn, NY 11211,
 * +1 (718) 290-9170.
 */

package com.aliasi.util;

/**
 * The <code>Proximity</code> interface provides a general method for
 * defining closeness between two objects.  Proximity is a similarity
 * measure, with two objects having higher proximity being more
 * similar to one another.  It provides a single method {@link
 * #proximity(Object,Object)} returning the proximity between two
 * objects.  The closer two objects are, the higher their proximity
 * value.
 *
 * <p>Proximity runs in the other direction from distance.  With
 * distance, the closer two objects are, the lower their distance
 * value.  Many classes implement both <code>Proximity</code> and
 * {@link Distance}, with one method defined in terms of the other.
 * For instance, negation converts a distance into a proximity.
 *
 * @author  Bob Carpenter
 * @version 3.0
 * @since   LingPipe3.0
 * @param <E> the type of objects between which proximity is defined
 */
public interface Proximity<E> {

    /**
     * Returns the distance between the specified pair of objects.
     *
     * @param e1 First object.
     * @param e2 Second object.
     * @return Proximity between the two objects.
     */
    public double proximity(E e1, E e2);

}
