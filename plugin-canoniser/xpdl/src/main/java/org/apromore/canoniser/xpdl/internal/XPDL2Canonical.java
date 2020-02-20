/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.xpdl.internal;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apromore.anf.AnnotationsType;
import org.apromore.anf.FillType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.PositionType;
import org.apromore.anf.SizeType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.ConditionExpressionType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.InputOutputType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.StateType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.WorkType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wfmc._2009.xpdl2.Activities;
import org.wfmc._2009.xpdl2.Activity;
import org.wfmc._2009.xpdl2.Artifact;
import org.wfmc._2009.xpdl2.Association;
import org.wfmc._2009.xpdl2.Condition;
import org.wfmc._2009.xpdl2.ConnectorGraphicsInfo;
import org.wfmc._2009.xpdl2.ConnectorGraphicsInfos;
import org.wfmc._2009.xpdl2.Coordinates;
import org.wfmc._2009.xpdl2.EndEvent;
import org.wfmc._2009.xpdl2.Event;
import org.wfmc._2009.xpdl2.Input;
import org.wfmc._2009.xpdl2.InputSet;
import org.wfmc._2009.xpdl2.InputSets;
import org.wfmc._2009.xpdl2.IntermediateEvent;
import org.wfmc._2009.xpdl2.Lane;
import org.wfmc._2009.xpdl2.NodeGraphicsInfo;
import org.wfmc._2009.xpdl2.NodeGraphicsInfos;
import org.wfmc._2009.xpdl2.PackageType;
import org.wfmc._2009.xpdl2.Pool;
import org.wfmc._2009.xpdl2.ProcessType;
import org.wfmc._2009.xpdl2.Route;
import org.wfmc._2009.xpdl2.StartEvent;
import org.wfmc._2009.xpdl2.Transition;
import org.wfmc._2009.xpdl2.TransitionRestriction;
import org.wfmc._2009.xpdl2.TransitionRestrictions;
import org.wfmc._2009.xpdl2.Transitions;

public class XPDL2Canonical {

    private static final Logger LOGGER = LoggerFactory.getLogger(XPDL2Canonical.class);

    List<Activity> activities = new LinkedList<Activity>();
    List<Transition> transitions = new LinkedList<Transition>();
    List<Pool> pools = new LinkedList<Pool>();
    List<Lane> lanes = new LinkedList<Lane>();
    Map<Lane, ResourceTypeType> lane2resourceType = new HashMap<Lane, ResourceTypeType>();
    Map<Pool, ResourceTypeType> pool2resourceType = new HashMap<Pool, ResourceTypeType>();
    Map<String, Activity> xpdlRefMap = new HashMap<String, Activity>();
    Map<Activity, NodeType> xpdl2canon = new HashMap<Activity, NodeType>();
    Map<Transition, EdgeType> edgeMap = new HashMap<Transition, EdgeType>();
    Map<NodeType, NodeType> implicitANDSplit = new HashMap<NodeType, NodeType>();
    Map<NodeType, NodeType> implicitORSplit = new HashMap<NodeType, NodeType>();
    Map<NodeType, NodeType> implicitJoin = new HashMap<NodeType, NodeType>();
    Map<NodeType, List<EdgeType>> outgoings = new HashMap<NodeType, List<EdgeType>>();
    Set<NodeType> linked = new HashSet<NodeType>();
//    List<BigInteger> unrequired_event_list = new LinkedList<BigInteger>();
//    List<NodeType> node_remove_list = new LinkedList<NodeType>();
    Map<String, ResourceTypeType> pool_resource_map = new HashMap<String, ResourceTypeType>();
    Map<String, ObjectType> object_map = new HashMap<String, ObjectType>();

    long cpfId = System.currentTimeMillis();
    long anfId = 1;

    CanonicalProcessType cpf;
    AnnotationsType anf;

    public CanonicalProcessType getCpf() {
        return cpf;
    }

    public AnnotationsType getAnf() {
        return anf;
    }

    /**
     * The constructor receives the file header then does the canonization process
     * in order to allow the user to retrieve the produced process again into
     * the canonical format. The user also will be able to retrieve the annotation
     * element which stores the annotation data for the canonized modelass isolated
     * from the process flow.
     * @param pkg the header for an XPDL (XML Process Definition Language) which
     * is file format for BPMN diagrams.
     * @since 1.0
     */
    public XPDL2Canonical(final PackageType pkg) throws CanoniserException {
        main(pkg);
    }

    public XPDL2Canonical(final PackageType pkg, final long id) throws CanoniserException {
        this.cpfId = id;
        main(pkg);
    }

    void main(final PackageType pkg) throws CanoniserException {
        this.cpf = new CanonicalProcessType();
        this.anf = new AnnotationsType();

        this.cpf.setName(pkg.getName());
        //TODO which URI to use here?
        this.cpf.setUri("id"+UUID.randomUUID().toString());
        //TODO which version to use here?
        this.cpf.setVersion("1.0");

        if (pkg.getPools() != null) {
            for (Pool pool : pkg.getPools().getPool()) {
                if (pool.isBoundaryVisible()) {
                    ResourceTypeType res = new ResourceTypeType();
                    res.setId(String.valueOf(cpfId++));
                    res.setName(pool.getName());
                    pool_resource_map.put(pool.getProcess(), res);
                    if (pool.getLanes() != null) {
                        for (Lane lane : pool.getLanes().getLane()) {
                            ResourceTypeType r = new ResourceTypeType();
                            r.setId(String.valueOf(cpfId++));
                            r.setName(lane.getName());
                            res.getSpecializationIds().add(String.valueOf(cpfId - 1));
                            this.cpf.getResourceType().add(r);
                            lanes.add(lane);
                            lane2resourceType.put(lane, r);
                        }
                    }
                    this.cpf.getResourceType().add(res);
                    pool2resourceType.put(pool, res);
                    pools.add(pool);
                }
            }
        }

        if (pkg.getArtifacts() != null) {
            for (Object obj : pkg.getArtifacts().getArtifactAndAny()) {
                if (obj instanceof Artifact) {
                    Artifact arti = (Artifact) obj;
                    if (arti.getArtifactType() != null && arti.getArtifactType().equals("DataObject") && arti.getDataObject() != null) {
                        ObjectType ot = new ObjectType();
                        ot.setName(arti.getDataObject().getName());
                        ot.setId(String.valueOf(cpfId++));
                        object_map.put(arti.getId(), ot);
                        //object_map.put(arti.getId(), ot.getId());
                        //this.cpf.getObject().add(ot);
                    }
                }
            }
        }

        if (pkg.getWorkflowProcesses() != null) {
            int size, count = 1;
            size = pkg.getWorkflowProcesses().getWorkflowProcess().size();
            for (ProcessType bpmnproc : pkg.getWorkflowProcesses().getWorkflowProcess()) {
                NetType net = new NetType();
                net.setId(String.valueOf(cpfId++));
                ResourceTypeType res;
                res = pool_resource_map.get(bpmnproc.getId());
                ResourceTypeRefType ref = new ResourceTypeRefType();
                if (res != null && res.getSpecializationIds().size() == 0) {
                    ref.setResourceTypeId(res.getId());
                    //ref.setOptional(false);
                } else {
                    ref = null;
                }
                translateProcess(net, bpmnproc, ref);
                //process_unrequired_events(net);
                recordAnnotations(bpmnproc, this.anf);

                /* Temp solution to turn around multiple lanes problem*/
                if (size == 2 && count == 1) {
                    count++;
                } else {
                    activities.clear();
                    transitions.clear();
                    this.cpf.getNet().add(net);
                }
            }
        }

        if (pkg.getAssociations() != null) {
            for (Object obj : pkg.getAssociations().getAssociationAndAny()) {
                if (obj instanceof Association) {
                    Association as = (Association) obj;
                    String source, target;
                    source = as.getSource();
                    target = as.getTarget();
                    try {
                        if (xpdlRefMap.get(source) != null) {
                            Activity act = xpdlRefMap.get(source);
                            if (xpdl2canon.get(act) instanceof WorkType) {
                                WorkType node = (WorkType) xpdl2canon.get(act);
                                ObjectRefType ref = new ObjectRefType();
                                ObjectType o = object_map.get(target);
                                if (o != null) {
                                    ref.setObjectId(o.getId());
                                }
                                ref.setType(InputOutputType.OUTPUT);
                                node.getObjectRef().add(ref);
                            }
                        } else if (xpdlRefMap.get(target) != null) {
                            Activity act = xpdlRefMap.get(target);
                            if (xpdl2canon.get(act) instanceof WorkType) {
                                WorkType node = (WorkType) xpdl2canon.get(act);
                                ObjectRefType ref = new ObjectRefType();
                                ObjectType o = object_map.get(source);
                                if (o != null) {
                                    ref.setObjectId(o.getId());
                                }
                                ref.setType(InputOutputType.INPUT);
                                node.getObjectRef().add(ref);
                            }
                        }
                    } catch (ClassCastException e) {
                        String msg = "Not Supported: Gateways in Canonical Format don't have a connection with an Object Type.";
                        throw new CanoniserException(msg, e);
                    }
                }
            }
        }
    }

//    private void process_unrequired_events(final NetType net) throws CanoniserException {
//        List<EdgeType> edge_remove_list = new LinkedList<EdgeType>();
//        String source_id;
//        try {
//            for (BigInteger id : unrequired_event_list) {
//                source_id = null;
//                for (EdgeType edge : net.getEdge()) {
//                    if (edge.getTargetId().equals(id.toString())) {
//                        source_id = edge.getSourceId();
//                        edge_remove_list.add(edge);
//                        break;
//                    }
//                }
//                for (EdgeType edge : net.getEdge()) {
//                    if (edge.getSourceId().equals(id.toString())) {
//                        if (source_id == null) {
//                            edge_remove_list.add(edge);
//                        } else {
//                            edge.setSourceId(source_id);
//                        }
//                    }
//                }
//            }
//
//            for (EdgeType edge : edge_remove_list) {
//                net.getEdge().remove(edge);
//            }
//            edge_remove_list.clear();
//            for (NodeType node : node_remove_list) {
//                net.getNode().remove(node);
//            }
//            node_remove_list.clear();
//
//        } catch (Exception e) {
//            String msg = "Failed to get some attributes when removing the unrequired events.";
//            throw new CanoniserException(msg, e);
//        }
//    }

    private void recordAnnotations(final ProcessType bpmnproc, final AnnotationsType annotations) {
        for (Activity act : activities) {
            GraphicsType cGraphInfo = new GraphicsType();
            cGraphInfo.setId(String.valueOf(anfId++));
            cGraphInfo.setCpfId(xpdl2canon.get(act).getId());
            for (Object obj : act.getContent()) {
                if (obj instanceof NodeGraphicsInfos) {
                    for (NodeGraphicsInfo xGraphInfo : ((NodeGraphicsInfos) obj).getNodeGraphicsInfo()) {
                        if (xGraphInfo.getFillColor() != null) {
                            FillType fill = new FillType();
                            fill.setColor(xGraphInfo.getFillColor());
                            cGraphInfo.setFill(fill);
                        }

                        if (xGraphInfo.getCoordinates() != null) {
                            PositionType pos = new PositionType();
                            pos.setX(BigDecimal.valueOf(xGraphInfo.getCoordinates().getXCoordinate()));
                            pos.setY(BigDecimal.valueOf(xGraphInfo.getCoordinates().getYCoordinate()));
                            cGraphInfo.getPosition().add(pos);
                            if (xpdl2canon.get(act) instanceof WorkType) {
                                addRefs((WorkType) xpdl2canon.get(act), xGraphInfo.getCoordinates().getXCoordinate(), xGraphInfo.getCoordinates().getYCoordinate());
                            }
                        }

                        SizeType size = new SizeType();
                        if (xGraphInfo.getHeight() != null && xGraphInfo.getWidth() != null) {
                            size.setHeight(BigDecimal.valueOf(xGraphInfo.getHeight()));
                            size.setWidth(BigDecimal.valueOf(xGraphInfo.getWidth()));
                        }

                        cGraphInfo.setSize(size);
                    }
                }
            }
            annotations.getAnnotation().add(cGraphInfo);
        }

        for (Transition trans : transitions) {
            GraphicsType cGraphInfo = new GraphicsType();
            cGraphInfo.setId(String.valueOf(anfId++));
            if (edgeMap.get(trans) != null) {
                cGraphInfo.setCpfId(edgeMap.get(trans).getId());
                ConnectorGraphicsInfos infos = trans.getConnectorGraphicsInfos();
                if (infos != null) {
                    for (ConnectorGraphicsInfo xGraphInfo : infos.getConnectorGraphicsInfo()) {
                        for (Coordinates coord : xGraphInfo.getCoordinates()) {
                            PositionType pos = new PositionType();
                            if (cGraphInfo.getPosition() != null && coord != null) {
                                pos.setX(BigDecimal.valueOf(coord.getXCoordinate()));
                                pos.setY(BigDecimal.valueOf(coord.getYCoordinate()));
                                cGraphInfo.getPosition().add(pos);
                            }
                        }
                    }
                }
                annotations.getAnnotation().add(cGraphInfo);
            }

        }

        for (Pool pool : pools) {
            GraphicsType cGraphInfo = new GraphicsType();
            cGraphInfo.setId(String.valueOf(anfId++));
            cGraphInfo.setCpfId(pool2resourceType.get(pool).getId());
            NodeGraphicsInfos infos = pool.getNodeGraphicsInfos();

            if (infos != null) {
                for (NodeGraphicsInfo xGraphInfo : infos.getNodeGraphicsInfo()) {
                    if (xGraphInfo.getFillColor() != null) {
                        FillType fill = new FillType();
                        fill.setColor(xGraphInfo.getFillColor());
                        cGraphInfo.setFill(fill);
                    }

                    if (xGraphInfo.getCoordinates() != null) {
                        PositionType pos = new PositionType();
                        pos.setX(BigDecimal.valueOf(xGraphInfo.getCoordinates().getXCoordinate()));
                        pos.setY(BigDecimal.valueOf(xGraphInfo.getCoordinates().getYCoordinate()));
                        cGraphInfo.getPosition().add(pos);
                    }

                    SizeType size = new SizeType();
                    if (xGraphInfo.getHeight() != null) {
                        size.setHeight(BigDecimal.valueOf(xGraphInfo.getHeight()));
                    }
                    if (xGraphInfo.getWidth() != null) {
                        size.setWidth(BigDecimal.valueOf(xGraphInfo.getWidth()));
                    }

                    cGraphInfo.setSize(size);
                }
            }

            annotations.getAnnotation().add(cGraphInfo);
        }

        for (Lane lane : lanes) {
            GraphicsType cGraphInfo = new GraphicsType();
            cGraphInfo.setId(String.valueOf(anfId++));
            cGraphInfo.setCpfId(lane2resourceType.get(lane).getId());
            NodeGraphicsInfos infos = lane.getNodeGraphicsInfos();

            if (infos != null) {
                for (NodeGraphicsInfo xGraphInfo : infos.getNodeGraphicsInfo()) {
                    if (xGraphInfo.getFillColor() != null) {
                        FillType fill = new FillType();
                        fill.setColor(xGraphInfo.getFillColor());
                        cGraphInfo.setFill(fill);
                    }

                    if (xGraphInfo.getCoordinates() != null) {
                        PositionType pos = new PositionType();
                        pos.setX(BigDecimal.valueOf(xGraphInfo.getCoordinates().getXCoordinate()));
                        pos.setY(BigDecimal.valueOf(xGraphInfo.getCoordinates().getYCoordinate()));
                        cGraphInfo.getPosition().add(pos);
                    }

                    SizeType size = new SizeType();
                    if (xGraphInfo.getHeight() != null) {
                        size.setHeight(BigDecimal.valueOf(xGraphInfo.getHeight()));
                    }
                    if (xGraphInfo.getWidth() != null) {
                        size.setWidth(BigDecimal.valueOf(xGraphInfo.getWidth()));
                    }

                    cGraphInfo.setSize(size);
                }
            }

            annotations.getAnnotation().add(cGraphInfo);
        }
    }

    private void addRefs(final WorkType node, final Double xCoordinate, final Double yCoordinate) {
        double x, w, y, h;
        for (Lane lane : lanes) {
            if (lane.getNodeGraphicsInfos() != null
                    && lane.getNodeGraphicsInfos().getNodeGraphicsInfo().size() > 0
                    && lane.getNodeGraphicsInfos().getNodeGraphicsInfo().get(0) != null) {
                NodeGraphicsInfo gi = lane.getNodeGraphicsInfos().getNodeGraphicsInfo().get(0);
                if (gi.getCoordinates() != null) {
                    x = gi.getCoordinates().getXCoordinate();
                    y = gi.getCoordinates().getYCoordinate();
                    w = gi.getWidth();
                    h = gi.getHeight();
                    if (xCoordinate >= x && xCoordinate <= x + w && yCoordinate >= y && yCoordinate <= y + h) {
                        ResourceTypeRefType ref = new ResourceTypeRefType();
                        ref.setResourceTypeId(lane2resourceType.get(lane).getId());
                        //ref.setOptional(false);
                        node.getResourceTypeRef().add(ref);
                    }
                }
            }
        }

    }

    private void translateProcess(final NetType net, final ProcessType bpmnproc, final ResourceTypeRefType ref) throws CanoniserException {
        for (Object obj : bpmnproc.getContent()) {
            if (obj instanceof Activities) {
                activities = ((Activities) obj).getActivity();
            } else if (obj instanceof Transitions) {
                transitions = ((Transitions) obj).getTransition();
            }
        }

        for (Activity act : activities) {
            translateActivity(net, act, ref);
        }

        for (Transition flow : transitions) {
            translateSequenceFlow(net, flow);
        }

        linkImplicitOrSplits(net);
    }

    private void linkImplicitOrSplits(final NetType net) throws CanoniserException {
        for (NodeType act : implicitORSplit.keySet()) {
            NodeType andSplit = implicitANDSplit.get(act);
            NodeType orSplit = implicitORSplit.get(act);

            if (outgoings.get(andSplit) != null && outgoings.get(andSplit).size() > 0) {
                addEdge(net, andSplit, orSplit);
            } else if (outgoings.get(act) != null && orSplit != null) {
                EdgeType edge = outgoings.get(act).get(0);
                edge.setTargetId(orSplit.getId());
                net.getNode().remove(andSplit);
            }
        }
    }

    private void translateSequenceFlow(final NetType net, final Transition flow) {
        Activity xsrc = xpdlRefMap.get(flow.getFrom());
        Activity xtgt = xpdlRefMap.get(flow.getTo());

        NodeType csrc = xpdl2canon.get(xsrc);
        NodeType ctgt = xpdl2canon.get(xtgt);

        if (csrc != null && ctgt != null) {
            Condition cond = flow.getCondition();
            //Expression condE = cond.getContent();
            if (csrc instanceof TaskType && cond != null && !cond.getContent().isEmpty()) {
                NodeType split = implicitORSplit.get(csrc);
                if (split == null) {
                    split = new ORSplitType();
                    split.setId(String.valueOf(cpfId++));
                    implicitORSplit.put(csrc, split);
                    net.getNode().add(split);
                }
                csrc = split;
            } else {
                if (implicitANDSplit.containsKey(csrc)) {
                    NodeType split = implicitANDSplit.get(csrc);
                    if (!linked.contains(split)) {
                        addEdge(net, csrc, split);
                        linked.add(split);
                    }
                    csrc = split;
                }

                if (implicitJoin.containsKey(ctgt)) {
                    NodeType join = implicitJoin.get(ctgt);
                    if (!linked.contains(join)) {
                        addEdge(net, join, ctgt);
                        linked.add(join);
                    }
                    ctgt = join;
                }
            }

            EdgeType edge = addEdge(net, csrc, ctgt);
            if (cond != null && cond.getExpression() != null) {
                edge.setConditionExpr(convertConditionExpr(cond.getExpression()));
            }

            edgeMap.put(flow, edge);

        }
    }

    private ConditionExpressionType convertConditionExpr(final String expression) {
        ConditionExpressionType expr = new ConditionExpressionType();
        expr.setDescription(expression);
        return expr;
    }

    private EdgeType addEdge(final NetType net, final NodeType src, final NodeType tgt) {
        EdgeType edge = new EdgeType();
        edge.setId(String.valueOf(cpfId++));
        edge.setSourceId(src.getId());
        edge.setTargetId(tgt.getId());
        List<EdgeType> trans = outgoings.get(src);
        if (trans == null) {
            trans = new LinkedList<EdgeType>();
            outgoings.put(src, trans);
        }
        trans.add(edge);
        net.getEdge().add(edge);
        return edge;
    }

    private void translateActivity(final NetType net, final Activity act, final ResourceTypeRefType ref) throws CanoniserException {
        NodeType node;
        Route route = null;
        Event event = null;
        InputSets inputs = null;
        TransitionRestrictions trests = null;
        for (Object obj : act.getContent()) {
            if (obj instanceof Route) {
                route = (Route) obj;
            } else if (obj instanceof Event) {
                event = (Event) obj;
            } else if (obj instanceof InputSets) {
                inputs = (InputSets) obj;
            } else if (obj instanceof TransitionRestrictions) {
                trests = (TransitionRestrictions) obj;
            }
        }
        if (route != null) {
            node = translateGateway(net, act, route, trests);
        } else if (event != null) {
            node = translateEvent(net, act, event);
            if (ref != null) {
                ((EventType) node).getResourceTypeRef().add(ref);
            }
        } else {
            // TODO: Subprocesses ...
            node = translateTask(net, act);
            if (inputs != null) {
                translateObjects(net, inputs.getInputSet());
            }
            if (ref != null) {
                ((TaskType) node).getResourceTypeRef().add(ref);
            }
        }

        node.setId(String.valueOf(cpfId++));
        net.getNode().add(node);
        xpdl2canon.put(act, node);
        xpdlRefMap.put(act.getId(), act);
    }

    /* Used to Translate the XPDL Inputs into object references. */
    private void translateObjects(final NetType net, final List<InputSet> inputSets) {
        for (InputSet inSet : inputSets) {
            for (Input in : inSet.getInput()) {
                net.getObject().add(object_map.get(in.getArtifactId()));
            }
        }
    }

    private NodeType translateTask(final NetType net, final Activity act) {
        NodeType node = new TaskType();
        boolean isSplit = false, isJoin = false;

        for (Object obj : act.getContent()) {
            if (obj instanceof TransitionRestrictions) {
                for (TransitionRestriction tres : ((TransitionRestrictions) obj).getTransitionRestriction()) {
                    if (tres.getSplit() != null) {
                        isSplit = true;
                    } else if (tres.getJoin() != null) {
                        isJoin = true;
                    }
                }
            }
        }

        if (isSplit) {
            NodeType split = new ANDSplitType();
            split.setId(String.valueOf(cpfId++));
            net.getNode().add(split);
        }
        if (isJoin) {
            NodeType join = new XORJoinType();
            join.setId(String.valueOf(cpfId++));
            net.getNode().add(join);
        }
        node.setName(act.getName());
        return node;
    }

    private NodeType translateEvent(final NetType net, final Activity act, final Event event) throws CanoniserException {
        NodeType node = null;

        if (event != null) {
            if (event.getStartEvent() != null) {
                StartEvent startEvent = event.getStartEvent();
                if (startEvent.getTrigger() != null) {
                    if (startEvent.getTrigger().equals("None") || startEvent.getTrigger().equals("Conditional")) {
                        node = new EventType();
                    } else if (startEvent.getTrigger().equals("Message")) {
                        node = new MessageType();
                    } else if (startEvent.getTrigger().equals("Timer")) {
                        node = new TimerType();
                    } else {
                        throw new CanoniserException("XPDL2Canonical: event type not supported (Start event): " + startEvent.getTrigger());
                    }
                }
            } else if (event.getEndEvent() != null) {
                EndEvent endEvent = event.getEndEvent();
                if (endEvent.getResult() != null) {
                    if (endEvent.getResult().equals("None")) {
                        node = new EventType();
                    } else if (endEvent.getResult().equals("Message")) {
                        node = new MessageType();
                    } else if (endEvent.getResult().equals("Cancel")) {
                        node = new EventType();
                        node.setName("Cancel");
                    } else {
                        throw new CanoniserException("XPDL2Canonical: event type not supported (End Event): " + endEvent.getResult());
                    }
                }
            } else  if (event.getIntermediateEvent() != null) {
                IntermediateEvent interEvent = event.getIntermediateEvent();
                if (interEvent.getTrigger() != null) {
                    if (interEvent.getTrigger().equals("None")) {
                        node = new EventType();
                    } else if (interEvent.getTrigger().equals("Message")) {
                        node = new MessageType();
                    } else if (interEvent.getTrigger().equals("Timer")) {
                        node = new TimerType();
                    } else if (interEvent.getTrigger().equals("Link") || interEvent.getTrigger().equals("Rule")) {
                        node = new EventType();
                    } else {
                        throw new CanoniserException("XPDL2Canonical: event type not supported: (Intermediate event)" + interEvent.getTrigger());
                    }
                }
            } else {
                LOGGER.warn("Found an Event where all details within were empty!");
            }
        } else {
            LOGGER.warn("Found an Event that was null!");
        }

        // Still could be null at this point if we just had an empty <event/> tag (they do exist)
        if (node == null) {
            node = new EventType();
        }
        node.setName(act.getName());
        return node;
    }

    private NodeType translateGateway(final NetType net, final Activity act, final Route route, final TransitionRestrictions trests) throws CanoniserException {
        boolean isSplit = false;
        boolean isJoin = false;

        if (trests != null) {
            for (TransitionRestriction trest : trests.getTransitionRestriction()) {
                if (trest.getSplit() != null) {
                    isSplit = true;
                }
                if (trest.getJoin() != null) {
                    isJoin = true;
                }
            }
        }

        NodeType node = null;
        if (route.getGatewayType().equals("Parallel") || route.getGatewayType().equals("AND")) {
            if (isSplit) {
                node = new ANDSplitType();
            } else {
                node = new ANDJoinType();
            }
        } else if (route.getGatewayType().equals("Exclusive") || route.getGatewayType().equals("XOR")) {
            if (route.getExclusiveType().equals("Data")) {
                if (isSplit) {
                    node = new XORSplitType();
                } else {
                    node = new XORJoinType();
                }
            } else {
                node = new StateType();
            }
        } else if (route.getGatewayType().equals("Inclusive") || route.getGatewayType().equals("OR")) {
            if (isSplit) {
                node = new ORSplitType();
            } else {
                node = new ORJoinType();
            }
        } else if (route.getGatewayType().equals("EventBasedXOR")) {
            node = new StateType();
        } else {
            throw new CanoniserException("XPDL2Canonical: gateway type not supported:[DEPRECATED] " + route.getGatewayType());
        }
        node.setName(act.getName());

        return node;
    }
}
