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

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.log.logfilters.LogFilter;
import org.processmining.plugins.log.logfilters.LogFilterException;
import org.processmining.plugins.log.logfilters.XTraceEditor;
import org.processmining.processtree.Node;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.impl.AbstractBlock;
import org.processmining.processtree.impl.AbstractTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by conforti on 28/01/2016.
 */
public class LogPreprocessing {

    private XEvent start;
    private XEvent end;
    private final XFactory factory = new XFactoryNaiveImpl();
    private final XConceptExtension xce = XConceptExtension.instance();
    private final XTimeExtension xte = XTimeExtension.instance();
    private final XLifecycleExtension xle = XLifecycleExtension.instance();
    private final String startLabel = "###$$$%%%$$$###START###$$$%%%$$$###";
    private final String endLabel = "###$$$%%%$$$###END###$$$%%%$$$###";

    public XLog preprocessLog(UIPluginContext context, XLog log) {
        ArrayList<String> classifiers = new ArrayList<String>();
        for(XEventClassifier c : log.getClassifiers()) {
            for(String s : c.getDefiningAttributeKeys()) {
                classifiers.add(s);
            }
        }

        start = createStartEvent(classifiers);
        end = createEndEvent(classifiers);
        return addArtificialStartAndEndEvents(context, log);
    }

    public void removedLifecycleFromName(Petrinet petrinet) {
        for (Transition t : petrinet.getTransitions()) {
            if (t.getLabel().toLowerCase().endsWith("+complete")) {
                t.getAttributeMap().put(AttributeMap.LABEL, t.getLabel().substring(0, t.getLabel().toLowerCase().indexOf("+complete")));
            } else if (t.getLabel().toLowerCase().endsWith("+start")) {
                t.getAttributeMap().put(AttributeMap.LABEL, t.getLabel().substring(0, t.getLabel().toLowerCase().indexOf("+start")));
            }
        }
    }

    public void removedAddedElements(Petrinet petrinet) {
        for (Transition t : petrinet.getTransitions()) {
            if (t.getLabel().contains(startLabel)) {
                t.setInvisible(true);
                t.getAttributeMap().put(AttributeMap.LABEL, "source");
            } else if (t.getLabel().contains(endLabel)) {
                t.setInvisible(true);
                t.getAttributeMap().put(AttributeMap.LABEL, "sink");
//            } else if (t.getLabel().toLowerCase().endsWith("+complete")) {
//                t.getAttributeMap().put(AttributeMap.LABEL, t.getLabel().substring(0, t.getLabel().toLowerCase().indexOf("+complete")));
//            } else if (t.getLabel().toLowerCase().endsWith("+start")) {
//                t.getAttributeMap().put(AttributeMap.LABEL, t.getLabel().substring(0, t.getLabel().toLowerCase().indexOf("+start")));
            }
        }
    }
    public void removedAddedElements(ProcessTree processTree) {
        List<Node> remove = new ArrayList<>();
        for (Node n : processTree.getNodes()) {
            if(n instanceof AbstractTask) {
                AbstractTask t = (AbstractTask) n;
                if (t.getName().contains(startLabel)) {
                    remove.add(t);
                } else if (t.getName().contains(endLabel)) {
                    remove.add(t);
//                } else if (t.getName().toLowerCase().endsWith("+complete")) {
//                    t.setName(t.getName().substring(0, t.getName().toLowerCase().indexOf("+complete")));
//                } else if (t.getName().toLowerCase().endsWith("+start")) {
//                    t.setName(t.getName().substring(0, t.getName().toLowerCase().indexOf("+start")));
                }
            }
        }

        for(Node n : remove) {
            AbstractBlock parent = (AbstractBlock) n.getIncomingEdges().get(0).getSource();
            parent.removeOutgoingEdge(n.getIncomingEdges().get(0));
            processTree.removeEdge(n.getIncomingEdges().get(0));
            processTree.removeNode(n);
            boolean test = processTree.getNodes().contains(n);
            System.out.println(test);
        }
    }

    private XEvent createStartEvent(ArrayList<String> classifiers) {
        XEvent start = factory.createEvent();
        for(String s : classifiers) {
            XAttributeLiteralImpl a = new XAttributeLiteralImpl(s, startLabel);
            start.getAttributes().put(s, a);
        }
        xce.assignName(start, startLabel);
        xte.assignTimestamp(start, 1L);
        xle.assignStandardTransition(start, XLifecycleExtension.StandardModel.COMPLETE);
        return start;
    }

    private XEvent createEndEvent(ArrayList<String> classifiers) {
        XEvent end = factory.createEvent();
        for (String s : classifiers) {
            XAttributeLiteralImpl a = new XAttributeLiteralImpl(s, endLabel);
            end.getAttributes().put(s, a);
        }
        xce.assignName(end, endLabel);
        xte.assignTimestamp(end, Long.MAX_VALUE);
        xle.assignStandardTransition(end, XLifecycleExtension.StandardModel.COMPLETE);
        return end;
    }

    private XLog addArtificialStartAndEndEvents(UIPluginContext context, XLog log) {
        try {
            log = LogFilter.filter(context.getProgress(), 100, log, XLogInfoFactory.createLogInfo(log),
                    new XTraceEditor() {

                        public XTrace editTrace(XTrace trace) {
                            // Add the new final event
                            trace.add(0, start);
                            return trace;
                        }
                    });

            log = LogFilter.filter(context.getProgress(), 100, log, XLogInfoFactory.createLogInfo(log),
                    new XTraceEditor() {

                        public XTrace editTrace(XTrace trace) {
                            // Add the new final event
                            trace.add(end);
                            return trace;
                        }
                    });
        } catch (LogFilterException e) {
            e.printStackTrace();
        }

        return log;
    }

    private XLog addArtificialStartAndEndEvents(UIPluginContext context, XLog log, XEventClassifier xEventClassifier) {
        try {
            log = LogFilter.filter(context.getProgress(), 100, log, XLogInfoFactory.createLogInfo(log, xEventClassifier),
                    new XTraceEditor() {

                        public XTrace editTrace(XTrace trace) {
                            // Add the new final event
                            trace.add(0, start);
                            return trace;
                        }
                    });

            log = LogFilter.filter(context.getProgress(), 100, log, XLogInfoFactory.createLogInfo(log, xEventClassifier),
                    new XTraceEditor() {

                        public XTrace editTrace(XTrace trace) {
                            // Add the new final event
                            trace.add(end);
                            return trace;
                        }
                    });
        } catch (LogFilterException e) {
            e.printStackTrace();
        }

        return log;
    }

    private XLog removeArtificialStartAndEndEvents(UIPluginContext context, XLog log) {
        try {
            log = LogFilter.filter(context.getProgress(), 100, log, XLogInfoFactory.createLogInfo(log),
                    new XTraceEditor() {

                        public XTrace editTrace(XTrace trace) {
                            // Add the new final event
                            trace.remove(start);
                            return trace;
                        }
                    });

            log = LogFilter.filter(context.getProgress(), 100, log, XLogInfoFactory.createLogInfo(log),
                    new XTraceEditor() {

                        public XTrace editTrace(XTrace trace) {
                            // Add the new final event
                            trace.remove(end);
                            return trace;
                        }
                    });
        } catch (LogFilterException e) {
            e.printStackTrace();
        }

        return log;
    }
}
