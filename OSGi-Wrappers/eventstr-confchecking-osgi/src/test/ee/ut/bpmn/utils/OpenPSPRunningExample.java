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
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.UnfoldingPESSemantics;
import ee.ut.eventstr.comparison.OpenPartialSynchronizedProduct;
import ee.ut.eventstr.freq.FrequencyAwarePrimeEventStructure;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.fpes.PORuns2FPES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.Unfolder_PetriNet;

public class OpenPSPRunningExample {

	@Test
	public void test() throws Exception {
		String folder = 
				"models/RunningExample/"
				//"e:/documents/nicta/experimental logs/sclogs/"
				;
		String logname = 
				//"SyncLoopModel1"
				"simpleloop"
				//"Windscreen-GIOManual"
				;
		String fileformat = 
				//"%s.mxml.gz"
				//"%s.xes"
				"%s.mxml"
				;
		
		FrequencyAwarePrimeEventStructure<Integer> pes1 = getLogPESExample(folder, logname, fileformat);
		
		
		String folder2 = 
				"models/RunningExample/"
				;
		String file2 = 
				"noloop.bpmn"
				;
		
		UnfoldingPESSemantics<Integer> pes2 = getUnfoldingPESExample(folder2, file2);
		
		OpenPartialSynchronizedProduct<Integer> psp = new OpenPartialSynchronizedProduct<Integer>(new PESSemantics<Integer>(pes1), pes2);
		psp.perform()
			.prune()
		;
		
		IOUtils.toFile("psp.dot", psp.toDot());
	}
	
	public UnfoldingPESSemantics<Integer> getUnfoldingPESExample(String folder, String file) throws JDOMException, IOException {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File(folder + file));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(model.getSources().iterator().next(), model.getSinks().iterator().next());
		System.out.println(model.getLabels());
		Set<String> labels = new HashSet<>();
		for (Integer node: model.getVisibleNodes())
			labels.add(model.getName(node));

		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ONEUNFOLDING);
		unfolder.computeUnfolding();
		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		IOUtils.toFile("net.dot", net.toDot());
		IOUtils.toFile("bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		IOUtils.toFile("bpmnpes.dot", pes.getPES().toDot());
		
		return new UnfoldingPESSemantics<Integer>(pes.getPES(), pes);
	}

	public FrequencyAwarePrimeEventStructure<Integer> getLogPESExample(String folder, String logname, String fileformat) throws Exception {
		XLog log = XLogReader.openLog(String.format(folder + fileformat, logname));		
		AlphaRelations alphaRelations = new AlphaRelations(log);
		
		File target = new File("target");
		if (!target.exists())
			target.mkdirs();
		
		PORuns runs = new PORuns();
		
		for (XTrace trace: log) {
			PORun porun = new PORun(alphaRelations, trace);
			runs.add(porun);
		}
		
		IOUtils.toFile(logname + "_prefix.dot", runs.toDot());
		runs.mergePrefix();
		IOUtils.toFile(logname + "_merged.dot", runs.toDot());

		return PORuns2FPES.getPrimeEventStructure(runs, logname);
	}	
	
	public void printBR(Unfolding2PES pes) {
		PrimeEventStructure<Integer> p = pes.getPES();
		
		System.out.println(p.getLabels());
		for (int i = 0; i < p.getBRelMatrix().length; i++) {
			for (int j = 0; j < p.getBRelMatrix().length; j++) {
				System.out.print(p.getBRelMatrix()[i][j] + " ");
			}
			System.out.println();
		}
	}

}
