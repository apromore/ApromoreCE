package ee.ut.bpmn.utils;

//import static org.hamcrest.MatcherAssert.assertThat;

//--
//OpenPSP test No loop - Loop: BPMN without loop vs log with loop
//--

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.UnfoldingPESSemantics;
import ee.ut.eventstr.comparison.OpenPartialSynchronizedProduct;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolding2PES;
import ee.ut.nets.unfolding.Unfolder_PetriNet;

public class OpenPSPExampleNLL {

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
	
	public UnfoldingPESSemantics<Integer> getUnfoldingPESExample() throws JDOMException, IOException {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File("E:/JavaProjects/workspace/eventstr-confcheck/eventstr-confchecking/models/acyclic10.bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(model.getSources().iterator().next(), model.getSinks().iterator().next());
		System.out.println(model.getLabels());
		Set<String> labels = new HashSet<>();
		for (Integer node: model.getVisibleNodes())
			labels.add(model.getName(node));

		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		IOUtils.toFile("net.dot", net.toDot());
		IOUtils.toFile("bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		IOUtils.toFile("unfpes.dot", pes.getPES().toDot());
		return new UnfoldingPESSemantics<Integer>(pes.getPES(), pes);
	}

	public PESSemantics<Integer> getLogPESExample() throws Exception {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(2, 3);
		adj.put(2, 4);
		adj.put(3, 5);
		adj.put(4, 6);
		adj.put(5, 6);
		adj.put(6, 7);
		adj.put(6, 8);
		adj.put(6, 9);
		adj.put(7, 10);
		adj.put(8, 10);
		adj.put(9, 11);
		adj.put(10, 12);
		adj.put(12, 13);
		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.put(3, 4); conc.put(4, 3);
		conc.put(5, 4); conc.put(4, 5);
		conc.put(7, 8); conc.put(8, 7);
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(11, 13), Arrays.asList("_0_", "start", "init", "A", "C", "B", "D", "B", "C", "end", "D", "_1_", "end", "_1_"), "PES1");

		IOUtils.toFile("pes1.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}
}
