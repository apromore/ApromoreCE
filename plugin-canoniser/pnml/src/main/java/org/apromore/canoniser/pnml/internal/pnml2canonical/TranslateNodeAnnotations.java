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

package org.apromore.canoniser.pnml.internal.pnml2canonical;

import java.math.BigInteger;
import java.util.List;

import org.apromore.anf.CpfTypeEnum;
import org.apromore.anf.FillType;
import org.apromore.anf.FontType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.LineType;
import org.apromore.anf.PositionType;
import org.apromore.anf.SimulationType;
import org.apromore.anf.SizeType;
import org.apromore.pnml.Fill;
import org.apromore.pnml.PlaceType;

public abstract class TranslateNodeAnnotations {

    static public void addNodeAnnotations(Object obj, DataHandler data) {
        GraphicsType graphT = new GraphicsType();
        LineType line = new LineType();
        FillType fill = new FillType();
        PositionType pos = new PositionType();
        SizeType size = new SizeType();
        FontType font = new FontType();
        String cpfId;

        org.apromore.pnml.NodeType element = (org.apromore.pnml.NodeType) obj;

        cpfId = data.get_id_map_value(element.getId());

        if (element.getGraphics() != null) {
            if (element.getName() != null) {
                if (element.getName().getGraphics() != null) {
                    List<Object> agt = element.getName().getGraphics().getOffsetAndFillAndLine();
                    for (Object oaf : agt) {
                        if (oaf instanceof Fill) {
                            if (((Fill) oaf).getImages() != null) {
                                fill.setImage(((Fill) oaf).getImages());
                            }
                            if (((Fill) oaf).getColor() != null) {
                                fill.setColor(((Fill) oaf).getColor());
                            }
                            if (((Fill) oaf).getGradientColor() != null) {
                                fill.setGradientColor(((Fill) oaf).getGradientColor());
                            }
                            if (((Fill) oaf).getGradientRotation() != null) {
                                fill.setGradientRotation(((Fill) oaf).getGradientRotation());
                            }
                            graphT.setFill(fill);
                        } else if (oaf instanceof org.apromore.pnml.Font) {
                            if (((org.apromore.pnml.Font) oaf).getFamily() != null) {
                                font.setFamily(((org.apromore.pnml.Font) oaf).getFamily());
                            }
                            if (((org.apromore.pnml.Font) oaf).getStyle() != null) {
                                font.setStyle(((org.apromore.pnml.Font) oaf).getStyle());
                            }
                            if (((org.apromore.pnml.Font) oaf).getWeight() != null) {
                                font.setWeight(((org.apromore.pnml.Font) oaf).getWeight());
                            }
                            if (((org.apromore.pnml.Font) oaf).getSize() != null) {
                                font.setSize(BigInteger.valueOf(Long.valueOf(((org.apromore.pnml.Font) oaf).getSize())));
                            }
                            if (((org.apromore.pnml.Font) oaf).getDecoration() != null) {
                                font.setDecoration(((org.apromore.pnml.Font) oaf).getDecoration());
                            }
                            if (((org.apromore.pnml.Font) oaf).getRotation() != null) {
                                font.setRotation(((org.apromore.pnml.Font) oaf).getRotation());
                            }
                            graphT.setFont(font);
                        } else if (oaf instanceof org.apromore.pnml.PositionType) {
                            if (graphT.getFont() != null) {
                                font = graphT.getFont();
                            }
                            if (((org.apromore.pnml.PositionType) oaf).getX() != null) {
                                font.setXPosition(((org.apromore.pnml.PositionType) oaf).getX());
                            }
                            if (((org.apromore.pnml.PositionType) oaf).getY() != null) {
                                font.setYPosition(((org.apromore.pnml.PositionType) oaf).getY());
                            }
                            graphT.setFont(font);
                        }
                    }

                }
            }
            if (element.getGraphics() != null && element.getGraphics().getDimension() != null) {
                size.setHeight(element.getGraphics().getDimension().getX());
                size.setWidth(element.getGraphics().getDimension().getY());
                graphT.setSize(size);

                pos.setX(element.getGraphics().getPosition().getX());
                pos.setY(element.getGraphics().getPosition().getY());
                graphT.getPosition().add(pos);
            }

            if (element.getGraphics().getLine() != null) {
                line.setColor(element.getGraphics().getLine().getColor());
                line.setShape(element.getGraphics().getLine().getShape());
                line.setStyle(element.getGraphics().getLine().getStyle());
                line.setWidth(element.getGraphics().getLine().getWidth());
                graphT.setLine(line);
            }

            graphT.setCpfId(cpfId);
            data.getAnnotations().getAnnotation().add(graphT);

        }
        if (element instanceof PlaceType) {
            SimulationType simu = new SimulationType();
            if (((PlaceType) element).getInitialMarking() != null) {
                if (((PlaceType) element).getInitialMarking().getText() != null) {
                    simu.setInitialMarking(((PlaceType) element).getInitialMarking().getText());
                    simu.setCpfId(cpfId);
                    simu.setCpfType(CpfTypeEnum.fromValue("WorkType"));
                    data.getAnnotations().getAnnotation().add(simu);
                }
            }

        }

    }

}
