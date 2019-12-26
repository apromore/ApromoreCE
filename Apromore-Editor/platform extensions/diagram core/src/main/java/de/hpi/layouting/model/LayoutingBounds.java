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
package de.hpi.layouting.model;

/**
 * Represents the geometry of an element. Needed for the decorators
 *
 * @author Team Royal Fawn
 */
public interface LayoutingBounds {
    /**
     * @return the x
     */
    public abstract double getX();

    /**
     * @return the y
     */
    public abstract double getY();

    /**
     * @return the width
     */
    public abstract double getWidth();

    /**
     * @return the height
     */
    public abstract double getHeight();

    /**
     * @return the x2
     */
    public abstract double getX2();

    /**
     * @return the y2
     */
    public abstract double getY2();
}
