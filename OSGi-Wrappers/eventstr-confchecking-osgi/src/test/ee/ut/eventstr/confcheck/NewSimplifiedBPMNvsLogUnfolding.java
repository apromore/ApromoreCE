package ee.ut.eventstr.confcheck;

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.bpmn.utils.BPMN2Reader;
import ee.ut.bpmn.utils.Petrifier;
import ee.ut.eventstr.BehaviorRelation;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.comparison.NewOpenPartialSynchronizedProduct;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.ConcurrencyRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;

public class NewSimplifiedBPMNvsLogUnfolding {

	@Test
	public void test() throws Exception {
		String logfilename = 
//				"nolooplog"
//				"innerlog"
//				"outerlog"
//				"nestedlog"
//				"overlappinglog"
				"cpreal"
				;
		
		String bpmnfilename = 
//				"noloop"
//				"inner"
//				"outer"
//				"nested"
//				"overlapping"
				"cp_lgb"
				;
		
		String logfiletemplate = 
				"models/RunningExample/%s.mxml"
				;
		
		String bpmnfolder = 
				"models/RunningExample/"
//				"models/simple/"
				;
		
		long startingTime = System.nanoTime();
		PESSemantics<Integer> logpes = getPES2(); //LogPESExample(logfilename, logfiletemplate);
		NewUnfoldingPESSemantics<Integer> bpmnpes = getUnfoldingPESExample(bpmnfilename, bpmnfolder);
		
		NewOpenPartialSynchronizedProduct<Integer> psp = new NewOpenPartialSynchronizedProduct<Integer>(logpes, bpmnpes);
		psp.perform()
			.prune()
			;
	
		System.out.println(">>> Time: " + (System.nanoTime() - startingTime) / 1000 / 1000 + " ms");
		
		IOUtils.toFile("psp.dot", psp.toDot());
//		psp.verbalize(new HashMap<>());
		
	}
	
	public NewUnfoldingPESSemantics<Integer> getUnfoldingPESExample(String filename, String folder) throws JDOMException, IOException {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File(folder + filename + ".bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(model.getSources().iterator().next(), model.getSinks().iterator().next());
		System.out.println(model.getLabels());
		
		Set<String> labels = new HashSet<String>();
		for (Integer node: model.getVisibleNodes())
			labels.add(model.getName(node));

		labels.remove("start");
//		labels.remove("AND2");
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
		PetriNet bp = unfolder.getUnfoldingAsPetriNet();
		
		IOUtils.toFile("net.dot", net.toDot());
		IOUtils.toFile("bp.dot", bp.toDot());
		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		NewUnfoldingPESSemantics<Integer> pessem = new NewUnfoldingPESSemantics<Integer>(pes.getPES(), pes);
		IOUtils.toFile("bpmnpes.dot", pessem.toDot());
		return pessem;
	}
	
	public PESSemantics<Integer> getPES2() {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		adj.put(1, 3);
		adj.put(2, 4);
		adj.put(3, 5);
		adj.put(4, 6);
		adj.put(6, 7);
		adj.put(7, 8);
		adj.put(8, 9);
		adj.put(5, 10);
		adj.put(9, 10);
		adj.put(10, 11);
		
		
		Multimap<Integer, Integer> conc = HashMultimap.create();
		conc.putAll(2, Arrays.asList(3, 5));
		conc.putAll(4, Arrays.asList(3, 5));
		conc.putAll(6, Arrays.asList(3, 5));
		conc.putAll(7, Arrays.asList(3, 5));
		conc.putAll(8, Arrays.asList(3, 5));
		conc.putAll(9, Arrays.asList(3, 5));
		conc.putAll(3, Arrays.asList(2, 4, 6, 7, 8, 9));
		conc.putAll(5, Arrays.asList(2, 4, 6, 7, 8, 9));

		List<String> labels = Arrays.asList("_0_", "A", "C", "D","B","D","C","F","G", "H", "I", "_1_");

		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(adj, conc, Arrays.asList(0), Arrays.asList(11), labels, "PES2");
		
		IOUtils.toFile("simple.dot", pes.toDot());
		return new PESSemantics<Integer>(pes);
	}

	
	public PESSemantics<Integer> getLogPESExample(String logfilename, String logfiletemplate) throws Exception {
		XLog log = XLogReader.openLog(String.format(logfiletemplate, logfilename));
		
		Multimap<String, String> concurrency = HashMultimap.create();
		concurrency.put("B", "D"); concurrency.put("D", "B");
		concurrency.put("C", "D"); concurrency.put("D", "C");
		concurrency.put("E", "D"); concurrency.put("D", "E");
		concurrency.put("F", "D"); concurrency.put("D", "F");
		concurrency.put("G", "D"); concurrency.put("D", "G");
		concurrency.put("H", "D"); concurrency.put("D", "H");
		
		ConcurrencyRelations alphaRelations = new ConcurrencyRelations() {
			public boolean areConcurrent(String label1, String label2) {
				return concurrency.containsEntry(label1, label2);
			}
		};

		
//		AlphaRelations alphaRelations = new AlphaRelations(log);
		
		File target = new File("target");
		if (!target.exists())
			target.mkdirs();
		
		System.out.println("Alpha relations created");
		
		PORuns runs = new PORuns();
		System.out.println("PO runs initialized");

//		Thread.sleep(1000);
		
//		for (XTrace trace: log) {
		int initial = 167, num = 4;
//		int initial = 363, num = 4;
//		int initial = 0, num = 100;
		for (int i = initial; i < initial + num; i++) { XTrace trace = log.get(i);			
			PORun porun = new PORun(alphaRelations, trace);
			runs.add(porun);
		}
		System.out.println("PO runs created");
		
		Map<Integer, String> labels = runs.getLabels();
		
		System.out.println("Sources: " + runs.getSources().size());
		for (int src: runs.getSources()) 
			if (labels.get(src) != "_0_") 
				System.out.println("FALSE SOURCE");
				
		System.out.println("Sinks: " + runs.getSinks().size());
		for (int snk: runs.getSinks()) 
			if (labels.get(snk) != "_1_")
				System.out.println("FALSE SINK");
		
//		Thread.sleep(2000);
		
		IOUtils.toFile(logfilename + "_prefix.dot", runs.toDot());
		runs.mergePrefix();
		IOUtils.toFile(logfilename + "_merged.dot", runs.toDot());

		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(runs, logfilename);
		List<String> peslabels = pes.getLabels();
		
		Multimap<Integer, Integer> map = runs.getConcurrency();
		Map<Integer, String> runlabels = runs.getLabels();		
		for (int key: map.keySet()) {
			if (runlabels.get(key) == "_1_") {
				System.out.println("End event spotted");
				for (int value: map.get(key))
					if (runlabels.get(value) == "_1_")
						System.out.println("PROBLEM IS IN A RUN");
			}
		}
		
		for (int snk: pes.getSinks())
			if (peslabels.get(snk) != "_1_")
				System.out.println("FALSE SINK");
		
		Boolean properend = false;
		BehaviorRelation[][] br = pes.getBRelMatrix();
		for (int i = 0; i < br.length; i++) {
			properend = false;
			for(int j = i + 1; j < br.length; j++) {
				if ((peslabels.get(j) == "_1_") && (br[i][j] != BehaviorRelation.CONFLICT)) {
					if (br[i][j] == BehaviorRelation.CONCURRENCY) System.out.println(peslabels.get(i) + " <<");
					properend = true;
				}
			}
			if (properend = false)
				System.out.println("TRUNCATED TRACE");
		}
		
		return new PESSemantics<Integer>(pes);
	}
}
