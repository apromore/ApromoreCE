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

package com.raffaeleconforti.noisefiltering.event;

import com.raffaeleconforti.automaton.Automaton;
import com.raffaeleconforti.automaton.AutomatonFactory;
import com.raffaeleconforti.automaton.Node;
import com.raffaeleconforti.bpmn.util.BPMNDiagramMerger;
import com.raffaeleconforti.bpmnminer.subprocessminer.BPMNMiner;
import com.raffaeleconforti.bpmnminer.subprocessminer.selection.SelectMinerResult;
import com.raffaeleconforti.conversion.bpmn.BPMNToPetriNetConverter;
import com.raffaeleconforti.log.util.LogModifier;
import com.raffaeleconforti.log.util.LogOptimizer;
import com.raffaeleconforti.noisefiltering.event.frequencyanalysis.FrequencyAnalyser;
import com.raffaeleconforti.noisefiltering.event.frequentbehaviour.automaton.AutomatonFrequentBehaviourDetector;
import com.raffaeleconforti.noisefiltering.event.frequentbehaviour.automaton.AutomatonFrequentBehaviourRemover;
import com.raffaeleconforti.noisefiltering.event.infrequentbehaviour.automaton.AutomatonInfrequentBehaviourDetector;
import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

import java.awt.*;
import java.util.Set;

/**
 * Created by conforti on 7/02/15.
 */

public class FrequentBehaviourFilter {

    private final XEventClassifier xEventClassifier = new XEventAndClassifier(new XEventNameClassifier(), new XEventLifeTransClassifier());
    private final AutomatonFactory automatonFactory = new AutomatonFactory(xEventClassifier);
    private final AutomatonFrequentBehaviourDetector automatonFrequentBehaviourDetector = new AutomatonFrequentBehaviourDetector(AutomatonInfrequentBehaviourDetector.MAX);
    private final boolean useGurobi;
    private final boolean useArcsFrequency;

    public FrequentBehaviourFilter() {
        this(false, false);
    }

    public FrequentBehaviourFilter(boolean useGurobi, boolean useArcsFrequency) {
        this.useGurobi = useGurobi;
        this.useArcsFrequency = useArcsFrequency;
    }

    public BPMNDiagram generateDiagram(final UIPluginContext context, XLog rawlog) {
        XLog res = filterLog(context, rawlog, rawlog, "#N#");

        BPMNMiner bpmnMiner = new BPMNMiner();
        BPMNDiagram diagram = null;
        diagram = bpmnMiner.mineBPMNDiagram(context, res, null, SelectMinerResult.IMPOS, null, false, false, xEventClassifier);
        for(Activity activity : diagram.getActivities()) {
            if(activity.getLabel().startsWith("#N#")) {
                activity.getAttributeMap().put(AttributeMap.LABEL, activity.getLabel().substring(3));
                activity.getAttributeMap().put(AttributeMap.FILLCOLOR, Color.RED);
            }
        }
        return diagram;
    }

    public BPMNDiagram generateDiagramTwoLogs(final UIPluginContext context, XLog rawlog1, XLog rawlog2) {
        XLog res1 = filterLog(context, rawlog1, rawlog2, "#1#");
        XLog res2 = filterLog(context, rawlog2, rawlog1, "#2#");
        XLog res = new XLogImpl(res1.getAttributes());
        res.getAttributes().putAll(res2.getAttributes());

        for(XTrace trace : res1) {
            res.add(trace);
        }
        for(XTrace trace : res2) {
            res.add(trace);
        }

        BPMNMiner bpmnMiner = new BPMNMiner();
        BPMNDiagram diagram = null;
        diagram = bpmnMiner.mineBPMNDiagram(context, res, null, SelectMinerResult.IMPOS, null, false, false, xEventClassifier);

        FrequencyAnalyser frequencyAnalyser = new FrequencyAnalyser((Petrinet) BPMNToPetriNetConverter.convert(diagram)[0], res, xEventClassifier, context);
        frequencyAnalyser.analyse();
        int max = frequencyAnalyser.getMax();

        for(Activity activity : diagram.getActivities()) {
            String label = activity.getLabel();
            int freq = frequencyAnalyser.getFrequency(label);

            if(label.startsWith("#1#")) {
                activity.getAttributeMap().put(AttributeMap.LABEL, label.substring(3));
                Color c = Color.RED;
                int diff = computeDiff(freq, frequencyAnalyser.getFrequency(label.replace("#1#", "#2#")));
                c = brighten(c, diff / 10.0);
                activity.getAttributeMap().put(AttributeMap.FILLCOLOR, c);
            }else if(label.startsWith("#2#")) {
                activity.getAttributeMap().put(AttributeMap.LABEL, label.substring(3));
                Color c = Color.BLUE;
                int diff = computeDiff(freq, frequencyAnalyser.getFrequency(label.replace("#2#", "#1#")));
                c = brighten(c, diff / 10.0);
                activity.getAttributeMap().put(AttributeMap.FILLCOLOR, c);
            }
        }

        diagram = BPMNDiagramMerger.mergeDiagram(diagram);
        return diagram;

    }

    private int computeDiff(int freq1, int freq2) {
        if(freq2 > freq1) return (freq1 > 0)?(freq1 * 10 / freq2):0;
        else if(freq1 > freq2) return computeDiff(freq2, freq1);
        return 10;
    }

    private static Color brighten(Color color, double fraction) {

        int red = (int) Math.round(Math.min(255, color.getRed() + 255 * fraction));
        int green = (int) Math.round(Math.min(255, color.getGreen() + 255 * fraction));
        int blue = (int) Math.round(Math.min(255, color.getBlue() + 255 * fraction));

        int alpha = color.getAlpha();

        return new Color(red, green, blue, alpha);

    }

    private XLog filterLog(final UIPluginContext context, XLog logToFilter, XLog logToUseAsReference, String label) {
        XFactory factory = new XFactoryNaiveImpl();
        LogOptimizer logOptimizer = new LogOptimizer(factory);
        LogModifier logModifier = new LogModifier(factory, XConceptExtension.instance(), XTimeExtension.instance(), logOptimizer);

        XLog frequentLog = logToUseAsReference;

        XLog log = logOptimizer.optimizeLog(logToFilter);

        frequentLog = logOptimizer.optimizeLog(frequentLog);

        log = logModifier.insertArtificialStartAndEndEvent(log);
        frequentLog = logModifier.insertArtificialStartAndEndEvent(frequentLog);

        Automaton<String> automatonFrequent = automatonFactory.generate(frequentLog);

        XLog res = AutomatonFrequentBehaviourRemover.filter1(context, log, automatonFrequent, label);
        res = logModifier.removeArtificialStartAndEndEvent(res);

        return res;
    }

    private Automaton<String> getFilteredAutomaton(Automaton<String> automatonOriginal, Set<Node<String>> requiredStates, double threshold) {
        Automaton<String> automaton = (Automaton<String>) automatonOriginal.clone();
        return automatonFrequentBehaviourDetector.removeFrequentBehaviour(automaton, requiredStates, threshold, useGurobi, useArcsFrequency);
    }

}
