/*-
 * #%L
 * This file is part of "Apromore Community".
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
/**
 * Copyright (c) 2011-2012 Felix Mannhardt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * See: http://www.opensource.org/licenses/mit-license.php
 *
 */
package org.apromore.common.converters.pnml.handler.impl;

import org.apromore.common.converters.pnml.context.PNMLConversionContext;
import org.apromore.pnml.DimensionType;
import org.apromore.pnml.GraphicsNodeType;
import org.apromore.pnml.PlaceType;
import org.oryxeditor.server.diagram.basic.BasicNode;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TypePlaceHandler extends NodeHandler {

    final PlaceType place;


    public TypePlaceHandler(PNMLConversionContext context, PlaceType place) {
        super(context);
        this.place = place;
    }


    @Override
    protected Map<String, String> convertProperties() {
        HashMap<String, String> hashMap = new HashMap<>();
        if (place.getName() != null) {
            hashMap.put("title", place.getName().getText());
        }
        if (place.getInitialMarking() != null) {
            hashMap.put("numberoftokens", place.getInitialMarking().getText());
            hashMap.put("numberoftokens_drawing", place.getInitialMarking().getText());
            try {
                if (place.getInitialMarking().getText() != null && Integer.parseInt(place.getInitialMarking().getText()) >= 5) {
                     hashMap.put("numberoftokens_text", place.getInitialMarking().getText());
                }
            } catch (NumberFormatException nfe) {
                // Do nothing, we don't need to show anything in the editor.
            }
        }
        return hashMap;
    }

    @Override
    protected GraphicsNodeType getGraphics() {
        return place.getGraphics();
    }

    @Override
    protected BasicNode createShape() {
        return new BasicNode(getShapeId(), "Place");
    }

    @Override
    protected String getShapeId() {
        return place.getId();
    }

    @Override
    protected DimensionType getDefaultDimension() {
        DimensionType dimension = new DimensionType();
        dimension.setX(BigDecimal.valueOf(30));
        dimension.setY(BigDecimal.valueOf(30));

        return dimension;
    }
}
