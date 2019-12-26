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
package de.hbrs.oryx.yawl.converter.handler.oryx;

import java.awt.Rectangle;

import org.oryxeditor.server.diagram.basic.BasicShape;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;

/**
 * Handler for all kinds of shapes: Net, Tasks, Conditions, Flows ...
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public abstract class OryxShapeHandler extends OryxHandlerImpl {

    private final BasicShape shape;

    public OryxShapeHandler(final OryxConversionContext context, final BasicShape shape) {
        super(context);
        this.shape = shape;
    }

    /**
     * @return the BasicShape to convert
     */
    public BasicShape getShape() {
        return shape;
    }

    protected Rectangle convertShapeBounds(final BasicShape shape) {
        return new Rectangle(shape.getUpperLeft().getX().intValue(), shape.getUpperLeft().getY().intValue(), (int) shape.getHeight(),
                (int) shape.getWidth());
    }

}
