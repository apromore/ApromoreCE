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

package com.raffaeleconforti.bpmn.util;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.*;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 20/03/2016.
 */
public class BPMNCloner {

    public static BPMNDiagram cloneDiagram(BPMNDiagram model) {
        BPMNDiagram diagram = new BPMNDiagramImpl(model.getLabel());

        Map<BPMNNode, BPMNNode> map = new UnifiedMap<>();
        BPMNNode node;

        Set<SubProcess> redo = new UnifiedSet<>();
        for(SubProcess s : model.getSubProcesses()) {
            if(s.getParentSubProcess() != null && map.get(s.getParentSubProcess()) == null) {
                redo.add(s);
            }else {
                node = diagram.addSubProcess(s.getLabel(), s.isBLooped(), s.isBAdhoc(), s.isBCompensation(), s.isBMultiinstance(), s.isBCollapsed(), s.getTriggeredByEvent(), (SubProcess) map.get(s.getParentSubProcess()));
                map.put(s, node);
            }
        }

        Iterator<SubProcess> iterator = redo.iterator();
        while(iterator.hasNext()) {
            SubProcess s = iterator.next();
            if(s.getParentSubProcess() == null || map.get(s.getParentSubProcess()) != null) {
                node = diagram.addSubProcess(s.getLabel(), s.isBLooped(), s.isBAdhoc(), s.isBCompensation(), s.isBMultiinstance(), s.isBCollapsed(), s.getTriggeredByEvent(), (SubProcess) map.get(s.getParentSubProcess()));
                map.put(s, node);
                iterator.remove();
                iterator = redo.iterator();
            }
        }

        for(Activity a : model.getActivities()) {
            node = diagram.addActivity(a.getLabel(), a.isBLooped(), a.isBAdhoc(), a.isBCompensation(), a.isBMultiinstance(), a.isBCollapsed(), (SubProcess) map.get(a.getParentSubProcess()));
            map.put(a, node);
        }

        for(Event e : model.getEvents()) {
            node = diagram.addEvent(e.getLabel(), e.getEventType(), e.getEventTrigger(), e.getEventUse(), (SubProcess) map.get(e.getParentSubProcess()), Boolean.parseBoolean(e.isInterrupting()), (Activity) map.get(e.getBoundingNode()));
            map.put(e, node);
        }

        for(Gateway g : model.getGateways()) {
            node = diagram.addGateway(g.getLabel(), g.getGatewayType(), (SubProcess) map.get(g.getParentSubProcess()));
            map.put(g, node);
        }

        for(Flow f : model.getFlows()) {
            diagram.addFlow(map.get(f.getSource()), map.get(f.getTarget()), f.getLabel());
        }

        return diagram;
    }

}
