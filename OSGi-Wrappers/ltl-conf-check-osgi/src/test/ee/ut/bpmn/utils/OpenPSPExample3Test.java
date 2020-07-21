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
package ee.ut.bpmn.utils;

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.junit.Test;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.UnfoldingPESSemantics;
import ee.ut.eventstr.comparison.OpenPartialSynchronizedProduct;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.Unfolder_PetriNet;

public class OpenPSPExample3Test {

	@Test
	public void test() throws Exception {
		PESSemantics<Integer> pes1 = getLogPESExample();
		UnfoldingPESSemantics<Integer> pes2 = getUnfoldingPESExample();
		
		OpenPartialSynchronizedProduct<Integer> psp = new OpenPartialSynchronizedProduct<Integer>(pes1, pes2);
		psp.perform()
			.prune()
		;
		
		IOUtils.toFile("psp.dot", psp.toDot());
	}
	
	public UnfoldingPESSemantics<Integer> getUnfoldingPESExample() throws Exception {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File("models/acyclic15p.bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(model.getSources().iterator().next(), model.getSinks().iterator().next());
		System.out.println(model.getLabels());
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		Set<String> labels = new HashSet<>();
		for (Integer node: model.getVisibleNodes())
			labels.add(model.getName(node));

		IOUtils.toFile("net.dot", net.toDot());
		IOUtils.toFile("bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		IOUtils.toFile("unfpes.dot", pes.getPES().toDot());
		return new UnfoldingPESSemantics<Integer>(pes.getPES(), pes);
	}

	public PESSemantics<Integer> getLogPESExample() throws Exception {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File("models/acyclic15p.bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(model.getSources().iterator().next(), model.getSinks().iterator().next());
		System.out.println(model.getLabels());
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ONEUNFOLDING);
		unfolder.computeUnfolding();
		
		Set<String> labels = new HashSet<String>();
		for (Integer node: model.getVisibleNodes())
			labels.add(model.getName(node));

		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		IOUtils.toFile("unfpes1.dot", pes.getPES().toDot());
		return new PESSemantics<Integer>(pes.getPES());
	}
}
