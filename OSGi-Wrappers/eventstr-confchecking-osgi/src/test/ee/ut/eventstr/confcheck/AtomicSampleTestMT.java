package ee.ut.eventstr.confcheck;

import hub.top.petrinet.PetriNet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jbpt.utils.IOUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;

import ee.ut.bpmn.BPMNProcess;
import ee.ut.bpmn.utils.BPMN2Reader;
import ee.ut.bpmn.utils.Petrifier;
import ee.ut.eventstr.NewUnfoldingPESSemantics;
import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.SinglePORunPESSemantics;
import ee.ut.eventstr.comparison.DiffVerbalizer;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct;
import ee.ut.eventstr.comparison.PrunedOpenPartialSynchronizedProduct.Operation;
import ee.ut.mining.log.AlphaRelations;
import ee.ut.mining.log.XLogReader;
import ee.ut.mining.log.poruns.PORun;
import ee.ut.mining.log.poruns.PORuns;
import ee.ut.mining.log.poruns.pes.PORuns2PES;
import ee.ut.nets.unfolding.BPstructBP.MODE;
import ee.ut.nets.unfolding.Unfolder_PetriNet;
import ee.ut.nets.unfolding.Unfolding2PES;

public class AtomicSampleTestMT {
	Lock lock = new ReentrantLock();
	Condition condition = lock.newCondition();
	ConcurrentHashMap<Integer, List<List<Operation>>> opMap = new ConcurrentHashMap<Integer, List<List<Operation>>>();

	AtomicInteger currentSink = new AtomicInteger();
	
	AtomicInteger finishedThreadCount = new AtomicInteger(0);
	
	PESSemantics<Integer> fullLogPesSem;
	DiffVerbalizer<Integer> verbalizer;
	
	final int totalThreadCount = 1;
	
	@Test
	public void test() throws Exception {
		String logfilename = 
//				"SampleLogBase"
//				"SampleLogI1"
//				"SampleLogI2"
//				"SampleLogI3"
				"SampleLogR1"
//				"SampleLogR2"
//				"SampleLogO1"
//				"SampleLogO2"
//				"SampleLogO3"
//				"SampleLogO3a"
//				"SampleLogIRO"
				;
		
		String bpmnfilename = 
				"SampleAtomicBase"
//				"SampleAtomicI1"
//				"SampleAtomicI2"
//				"SampleAtomicI3"
//				"SampleAtomicR1"
//				"SampleAtomicR2"
//				"SampleAtomicO1"
//				"SampleAtomicO2"
//				"SampleAtomicO3"
//				"SampleAtomicO3a"
				;
		
		String logfiletemplate = 
				"logs/sample/%s.mxml"
				;
		
		String bpmnfolder = 
				"models/sample/"
				;
		
		long totalStartTime = System.nanoTime();
		
		new Thread(new PSPgenerator(0, logfilename, logfiletemplate, bpmnfilename, bpmnfolder, true)).start();
		for (int i = 1; i < totalThreadCount; i++) {
			new Thread(new PSPgenerator(i, logfilename, logfiletemplate, bpmnfilename, bpmnfolder, false)).start();
		}
		
		// wait for the threads to finish
	    lock.lock();
	    while (finishedThreadCount.get() < totalThreadCount) {
	    	condition.await();
	    }
	    lock.unlock();

	    for (int i = 0; i < totalThreadCount; i++) {
	    	for (List<Operation> op: opMap.get(i)) {
	    		verbalizer.addPSP(op);
	    	}
	    }
	    verbalizer.verbalize();
	    
	    System.out.println("Total time: " + (System.nanoTime() - totalStartTime) / 1000 / 1000 + "ms");
	}
	
	public NewUnfoldingPESSemantics<Integer> getUnfoldingPESExample(String filename, String folder) throws JDOMException, IOException {
		BPMNProcess<Element> model = BPMN2Reader.parse(new File(folder + filename + ".bpmn"));
		Petrifier<Element> petrifier = new Petrifier<Element>(model);
		PetriNet net = petrifier.petrify(model.getSources().iterator().next(), model.getSinks().iterator().next());
		System.out.println(model.getLabels());
		
		Set<String> labels = new HashSet<String>();
		for (Integer node: model.getVisibleNodes())
			labels.add(model.getName(node));

		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();

		Unfolding2PES pes = new Unfolding2PES(unfolder.getSys(), unfolder.getBP(), labels);
		NewUnfoldingPESSemantics<Integer> pessem = new NewUnfoldingPESSemantics<Integer>(pes.getPES(), pes);
//		IOUtils.toFile("modelpes.dot", pessem.toDot());

		return pessem;
	}

	public PrimeEventStructure<Integer> getLogPESExample(String logfilename, String logfiletemplate) throws Exception {
		XLog log = XLogReader.openLog(String.format(logfiletemplate, logfilename));

		AlphaRelations alphaRelations = new AlphaRelations(log);

//		Multimap<String, String> concurrency = HashMultimap.create();
//		concurrency.put("B", "C"); concurrency.put("C", "B");
//		
//		ConcurrencyRelations alphaRelations = new ConcurrencyRelations() {
//			public boolean areConcurrent(String label1, String label2) {
//				return concurrency.containsEntry(label1, label2);
//			}
//		};
		
		File target = new File("target");
		if (!target.exists())
			target.mkdirs();
		
		System.out.println("Alpha relations created");
		
		PORuns runs = new PORuns();
		System.out.println("PO runs initialized");
		
		Set<Integer> eventlength = new HashSet<Integer>();
		Set<Integer> succ;
		
		for (XTrace trace: log) {
//			PORun porun = new AbstractingShort12LoopsPORun(alphaRelations, trace);
			PORun porun = new PORun(alphaRelations, trace);
			
			succ = new HashSet<Integer>(porun.asSuccessorsList().values());
			eventlength.add(succ.size());
				
			runs.add(porun);
		}
		System.out.println("PO runs created");
				
		runs.mergePrefix();
		
		PrimeEventStructure<Integer> pes = PORuns2PES.getPrimeEventStructure(runs, logfilename);
		
//		IOUtils.toFile("logpes.dot", pes.toDot());
		return pes;
	}
	
	class PSPgenerator implements Runnable {
		private SinglePORunPESSemantics<Integer> logpessem;
		private PrunedOpenPartialSynchronizedProduct<Integer> psp;
		private PrimeEventStructure<Integer> logpes;
		private NewUnfoldingPESSemantics<Integer> bpmnpes;
		private List<List<Operation>> operations = new ArrayList<List<Operation>>();
		private List<Integer> allSinks;
		
		private int index;
		
		public PSPgenerator(int index, String logfilename, String logfiletemplate, String bpmnfilename, String bpmnfolder, Boolean initialize) throws Exception {
			this.logpes = getLogPESExample(logfilename, logfiletemplate);
			this.bpmnpes = getUnfoldingPESExample(bpmnfilename, bpmnfolder);
			this.index = index;
					
			allSinks = new ArrayList<Integer>(logpes.getSinks());
			if (initialize) {
				currentSink.set(allSinks.size() - 1);
				fullLogPesSem = new PESSemantics<Integer>(logpes);
				verbalizer = new DiffVerbalizer<Integer>(fullLogPesSem, bpmnpes);
			}
		}
		
		@Override
		public void run() {
			int sink;
			int sinkindex;
			double startingTime;
			Boolean finishedSinks = false;
			
			while (!finishedSinks) {
				sinkindex = currentSink.getAndDecrement();
				
				if (sinkindex >= 0) {
					sink = allSinks.get(sinkindex);
	
					startingTime = System.nanoTime();
		        	logpessem = new SinglePORunPESSemantics<Integer>(logpes, sink);			
					psp = new PrunedOpenPartialSynchronizedProduct<Integer>(logpessem, bpmnpes);
					
					psp.perform()
						.prune()
						;
					operations.add(psp.getOperationSequence());

					IOUtils.toFile("/test/test" + sink + ".dot", psp.toDot());
					System.out.println(">>> Trace " + sinkindex + ", Sink " + sink + ", Time: " + (System.nanoTime() - startingTime) / 1000000 + ", " + psp.matchings.cost);
				}
				else {
					finishedSinks = true;
				}
			}		
			
			opMap.put(index, operations);

			lock.lock();
			condition.signalAll();
			lock.unlock();
			
			finishedThreadCount.incrementAndGet();
		}
	}
}
