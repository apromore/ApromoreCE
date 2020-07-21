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
package ee.ut.pnml;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Transition;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.utils.IOUtils;
import org.junit.Test;

import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;

public class PNMLReaderTest {
	@Test
	public void readFile() throws Exception {
		PetriNet net = PNMLReader.parse(new File("models/pnml/b3.s00000185__s00002121.pnml"));
		IOUtils.toFile("net.dot", net.toDot());
		
		Set<String> labels = new HashSet<>();
		for (Transition t: net.getTransitions()) {
			if (!(t.getName().startsWith("decision") || t.getName().startsWith("merge") || t.getName().startsWith("join") || t.getName().startsWith("fork")))
				labels.add(t.getName());
		}
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		IOUtils.toFile("bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		NewUnfoldingPESSemantics<Integer> pessem = new NewUnfoldingPESSemantics<Integer>(pes.getPES(), pes);
		IOUtils.toFile("pes.dot", pessem.toDot());

	}
}
