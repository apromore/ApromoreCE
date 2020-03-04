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

import com.raffaeleconforti.wrappers.settings.MiningSettings;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.processtree.ProcessTree;

/**
 * Created by conforti on 20/02/15.
 */
public interface MiningAlgorithm {

    boolean canMineProcessTree();
    ProcessTree mineProcessTree(UIPluginContext context, XLog log, boolean structure, MiningSettings params, XEventClassifier xEventClassifier);
    PetrinetWithMarking minePetrinet(UIPluginContext context, XLog log, boolean structure, MiningSettings params, XEventClassifier xEventClassifier);
    BPMNDiagram mineBPMNDiagram(UIPluginContext context, XLog log, boolean structure, MiningSettings params, XEventClassifier xEventClassifier);
    String getAlgorithmName();
    String getAcronym();

}
