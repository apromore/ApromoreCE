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

//import static org.hamcrest.MatcherAssert.assertThat;

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.util.Set;

import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.junit.Test;

import com.google.gwt.dev.util.collect.HashSet;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.comparison.PartialSynchronizedProduct;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.Unfolder_PetriNet;

public class CopyOfBPMNReaderTest {

	@Test
	public void test() throws Exception {
		long startTime = System.nanoTime();
		
		String foldertemplate =
				//"bpm2014/%s.bpmn"
				"models/RunningExample/%s.bpmn"
				;
		
		String bpmn1 =
				//"SASub3"
				"cpnoloops"
				;
		
		String bpmn2 =
				//"WASub3"
				"cp_lgb"
				;
		
		PrimeEventStructure<Integer> pes1 = getPES1(bpmn1, foldertemplate);
		PrimeEventStructure<Integer> pes2 = getPES1(bpmn2, foldertemplate);
		
//		IOUtils.toFile(bpmn1 + ".dot", pes1.toDot());
//		IOUtils.toFile(bpmn2 + ".dot", pes2.toDot());
		
		PartialSynchronizedProduct<Integer> psp = new PartialSynchronizedProduct<Integer>(
				new PESSemantics<Integer>(pes1), new PESSemantics<Integer>(pes2));
		
		psp.perform().prune();

		System.out.println("Total time: " + (System.nanoTime() - startTime) / 1000000.0);

		IOUtils.toFile("pspSAWA.dot", psp.toDot());
	}
	
	public PrimeEventStructure<Integer> getPES1(String modelName, String foldertemplate) throws Exception {

		BPMNProcess<Element> model = BPMN2Reader.parse(new File(String.format(foldertemplate, modelName)));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		
		PetriNet net = petrifier.petrify(model.getSources().iterator().next(), model.getSinks().iterator().next());
		IOUtils.toFile("net.dot", net.toDot());
		Set<String> labels = new HashSet<String>();
		for (Integer node: model.getVisibleNodes())
			labels.add(model.getName(node));
		System.out.println(model.getLabels());
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
		
		
		return new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels).getPES();
	}

}
