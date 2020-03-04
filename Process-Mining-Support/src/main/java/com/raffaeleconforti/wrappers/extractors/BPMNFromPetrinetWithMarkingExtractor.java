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

package com.raffaeleconforti.wrappers.extractors;

import com.raffaeleconforti.conversion.petrinet.PetriNetToBPMNConverter;
import com.raffaeleconforti.wrappers.PetrinetWithMarking;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

/**
 * Created by conforti on 28/01/2016.
 */
@Plugin(name = "Extract BPMN Diagram", parameterLabels = {"PetrinetWithMarking"},
        returnLabels = {"BPMN Diagram"},
        returnTypes = {BPMNDiagram.class})
public class BPMNFromPetrinetWithMarkingExtractor {

    @UITopiaVariant(affiliation = UITopiaVariant.EHV,
        author = "Raffaele Conforti",
        email = "raffaele.conforti@unimelb.edu.au",
        pack = "Noise Filtering")
    @PluginVariant(variantLabel = "Extract BPMN Diagram", requiredParameterLabels = {0})
    public BPMNDiagram extractPetrinet(UIPluginContext context, PetrinetWithMarking petrinetWithMarking) {
        return PetriNetToBPMNConverter.convert(petrinetWithMarking.getPetrinet(), petrinetWithMarking.getInitialMarking(), petrinetWithMarking.getFinalMarking(), true);
    }
}
