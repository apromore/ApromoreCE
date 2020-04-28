/*-
 * #%L
 * This file is part of "Apromore Community".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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
package org.apromore.processdiscoverer.dfg;


import java.util.Date;

import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.dfg.abstraction.TraceAbstraction;
import org.apromore.processdiscoverer.dfg.collectors.ArcInfoCollector;
import org.apromore.processdiscoverer.dfg.collectors.NodeInfoCollector;
import org.apromore.processdiscoverer.dfg.vis.BPMNDiagramBuilder;
import org.apromore.processdiscoverer.logprocessors.EventClassifier;
import org.apromore.processdiscoverer.logprocessors.SimplifiedLog;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;

/**
 * 
 * @author Bruce Nguyen
 *
 */
public class TraceDFG {
	private ArcInfoCollector arcInfoCollector; 
	private NodeInfoCollector nodeInfoCollector;
	XTrace trace;
	LogDFG logDfg;
    
    public TraceDFG(XTrace trace, LogDFG logDfg) {
    	this.trace = trace;
    	this.logDfg = logDfg;
    	this.arcInfoCollector = new ArcInfoCollector(logDfg);
    	nodeInfoCollector = new NodeInfoCollector(logDfg);
    }
    
    public XTrace getTrace() {
    	return this.trace;
    }
    
    public LogDFG getLogDFG() {
    	return this.logDfg;
    }
    
    public ArcInfoCollector getArcInfoCollector() {
    	return arcInfoCollector;
    }
    
    public NodeInfoCollector getNodeInfoCollector() {
    	return nodeInfoCollector;
    }
    
    private int getStart(XTrace trace, int pos, EventClassifier classifier) {
        XEvent event = trace.get(pos);
        XConceptExtension xce = XConceptExtension.instance();
        for(int i = pos - 1; i >= 0; i--) {
            XEvent event1 = trace.get(i);
            if(classifier.getClassIdentity(event1).toLowerCase().endsWith("start") && xce.extractName(event).equals(xce.extractName(event1))) {
                return i;
            }
        }
        return -1;
    }

    private int getPreviousComplete(XTrace trace, int pos, EventClassifier classifier) {
        for(int i = pos - 1; i >= 0; i--) {
            XEvent event1 = trace.get(i);
            if(classifier.getClassIdentity(event1).toLowerCase().endsWith("complete")) {
                return i;
            }
        }
        return -1;
    }
    
    // TODO: this abstraction stores all weights for nodes and arcs in the labels
    // The NodeInfoCollector and ArcInfoCollector is not used 
    // Node: each node is an activity in the event
    // Node weight: the duration of the start and complete events of the same activity
    // Arc weight: the duration from the previous complete event to an event of different activity
    public BPMNDiagram getDFG(AbstractionParams params) {
    	XConceptExtension xce = XConceptExtension.instance();
        XTimeExtension xte = XTimeExtension.instance();
        BPMNDiagramBuilder bpmnDiagramBuilder = new BPMNDiagramBuilder(arcInfoCollector);
        BPMNNode lastNode = bpmnDiagramBuilder.addNode(SimplifiedLog.START_NAME);
        for(int i = 0; i < trace.size(); i++) {
            XEvent event = trace.get(i);
            //if(params.getClassifier().getClassIdentity(event).toLowerCase().endsWith("complete")) {
            //String name = xce.extractName(event);
//            int previous_start = getStart(trace, i, params.getClassifier());
//            if(previous_start > -1) {
//                Date date1 = xte.extractTimestamp(trace.get(previous_start));
//                Date date2 = xte.extractTimestamp(event);
//                Long diff = date2.getTime() - date1.getTime();
//                name += "\\n\\n[" + diff.toString() + "]";
//            }
            String name = params.getClassifier().getClassIdentity(event);
            BPMNNode node = bpmnDiagramBuilder.addNode(name);
//            String label = "";
//
//            int previous_complete = getPreviousComplete(trace, i, params.getClassifier());
//            if (previous_complete > -1) {
//                Date date1 = xte.extractTimestamp(trace.get(previous_complete));
//                Date date2 = xte.extractTimestamp(event);
//                Long diff = date2.getTime() - date1.getTime();
//                label = "[" + diff.toString() + "]";
//            }
            Date date1 = (i==0) ? xte.extractTimestamp(event) : xte.extractTimestamp(trace.get(i-1));
            Date date2 = xte.extractTimestamp(event);
            Long diff = date2.getTime() - date1.getTime();
            String label = "[" + diff.toString() + "]";
            bpmnDiagramBuilder.addFlow(lastNode, node, label);
            lastNode = node;
            //}
        }
        BPMNNode node = bpmnDiagramBuilder.addNode(SimplifiedLog.END_NAME);
        bpmnDiagramBuilder.addFlow(lastNode, node, "");
        return bpmnDiagramBuilder.getBpmnDiagram();
    }
    
	public TraceAbstraction getTraceAbstraction(AbstractionParams params) {
		TraceAbstraction traceAbs = new TraceAbstraction(this, params);
		return traceAbs;
	}
}
