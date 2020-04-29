/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 Adriano Augusto.
 * Copyright (C) 2017 Queensland University of Technology.
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

package au.edu.qut.bpmn.exporter.impl;

import au.edu.qut.bpmn.exporter.BPMNDiagramExporter;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.plugins.bpmn.BpmnDefinitions;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Created by Adriano on 29/10/2015.
 */
public class BPMNDiagramExporterImpl implements BPMNDiagramExporter {

    public BPMNDiagramExporterImpl() {}

    @Override
    public String exportBPMNDiagram(BPMNDiagram diagram) throws Exception {
        String result;

        //UIContext context = new UIContext();
        //UIPluginContext uiPluginContext = context.getMainPluginContext();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(new MetalLookAndFeel());
                } catch (UnsupportedLookAndFeelException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        BpmnDefinitions.BpmnDefinitionsBuilder definitionsBuilder = new BpmnDefinitions.BpmnDefinitionsBuilder(diagram);
        BpmnDefinitions definitions = new BpmnDefinitions("definitions", definitionsBuilder);

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n " +
                "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"\n " +
                "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"\n " +
                "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"\n " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
                "targetNamespace=\"http://www.omg.org/bpmn20\"\n " +
                "xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">");
        sb.append(definitions.exportElements());
        sb.append("</definitions>");

        result = sb.toString();

        result = result.replaceAll("\n", "&#10;");
        result = result.replaceAll(">&#10;", ">\n");
        result = result.replaceAll("\"&#10;", "\"\n");
        result = result.replaceFirst("<bpmndi:BPMNDiagram>.*</bpmndi:BPMNDiagram>", "");

        return result;
    }

}
