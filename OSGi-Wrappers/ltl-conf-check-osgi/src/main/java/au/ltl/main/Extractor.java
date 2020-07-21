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

package au.ltl.main;

import org.processmining.models.connections.GraphLayoutConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.pnml.base.Pnml;
import org.processmining.plugins.pnml.importing.PnmlImportUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Extractor {

//    public static PetrinetGraph extractPetriNet(String pnmlFilename) throws Exception {
//        PnmlImportUtils pnmlImportUtils = new PnmlImportUtils();
//        File pnFile = new File (pnmlFilename);
//
//        InputStream input = new FileInputStream(pnFile);
//        Pnml pnml = pnmlImportUtils.importPnmlFromStream(null,input, pnFile.getName(), pnFile.length());
//        String nameWithoutExtension = pnFile.getName().split("\\.")[0];
//        PetrinetGraph net = PetrinetFactory.newPetrinet(nameWithoutExtension);
//        Marking marking = new Marking();
//        pnml.convertToNet(net, marking, new GraphLayoutConnection(net));
//        return net;
//    }
}
