/*
 *  Copyright (C) 2018 Raffaele Conforti (www.raffaeleconforti.com)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.raffaeleconforti.wrappers;

import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.petrinet.PetriNetVisualization;

import javax.swing.*;

/**
 * Created by conforti on 23/02/15.
 */
@Plugin(name = "Visualize PetrinetWithMarking", returnLabels = {"Visualized PetrinetWithMarking"}, returnTypes = { JComponent.class }, parameterLabels = {"PetrinetWithMarking" }, userAccessible = true)
@Visualizer
public class PetrinetWithMarkingVisualizer {

    @PluginVariant(requiredParameterLabels = { 0 })
    public JComponent visualize(PluginContext context, PetrinetWithMarking petrinetWithMarking) {
        PetriNetVisualization petriNetVisualization = new PetriNetVisualization();
        return petriNetVisualization.visualize(context, petrinetWithMarking.getPetrinet(), petrinetWithMarking.getInitialMarking());
    }

}
