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

package org.apromore.canoniser.pnml.internal.pnml2canonical;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.pnml.ArcType;
import org.apromore.pnml.NodeType;
import org.apromore.pnml.PlaceType;

public class DataHandler {
    private Map<String, String> id_map = new HashMap<String, String>();
    private Map<String, String> andsplitmap = new HashMap<String, String>();
    private Map<String, String> andjoinmap = new HashMap<String, String>();
    private Map<String, String> andsplitjoinmap = new HashMap<String, String>();
    private Map<String, String> triggermap = new HashMap<String, String>();
    private Map<String, ResourceTypeType> resourcemap = new HashMap<String, ResourceTypeType>();
    private Map<String, String> xorcounter = new HashMap<String, String>();
    private Map<String, Object> objectmap = new HashMap<String, Object>();
    private List<ArcType> centerarcs = new LinkedList<ArcType>();
    private List<ArcType> updatedarcs = new LinkedList<ArcType>();
    private List<NodeType> noderef = new LinkedList<NodeType>();
    private List<Object> annotationobjects = new LinkedList<Object>();
    private List<PlaceType> center = new LinkedList<PlaceType>();
    private List<String> targetvalues = new LinkedList<String>();
    private List<String> sourcevalues = new LinkedList<String>();
    private List<String> output = new LinkedList<String>();
    private List<String> input = new LinkedList<String>();
    private AnnotationsType annotations = new AnnotationsType();
    private String inputevent;
    private String outputstate;
    private CanonicalProcessType cproc = new CanonicalProcessType();
    private EdgeType inputedge;
    private EdgeType outputedge;
    private String subnettask;
    private long ids = 0;  // 6121979;
    private long resourceid = 10000;
    private long rootid;
    private File folder;
    private NetType net;
    private String filename;
    private String inputnode;
    private String outputnode;
    final Map<Object, String> units = new HashMap<Object, String>();
    //final Map<Object, String> roles = new HashMap<Object, String>();

    public void put_id_map(String key, String value) {
        id_map.put(key, value);
    }

    public String get_id_map_value(String key) {

        return (id_map.get(key));
    }

    public Map<String, String> get_id_map() {

        return id_map;
    }

    public void put_objectmap(String key, Object obj) {
        objectmap.put(key, obj);
    }

    public Object get_objectmap_value(String key) {

        return (objectmap.get(key));
    }

    public Map<String, Object> get_objectmap() {

        return objectmap;
    }

    public void put_triggermap(String key, String obj) {
        triggermap.put(key, obj);
    }

    public String get_triggermap_value(String key) {

        return (triggermap.get(key));
    }

    public Map<String, String> get_triggermap() {

        return triggermap;
    }

    public void put_resourcemap(String key, ResourceTypeType obj) {
        resourcemap.put(key, obj);
    }

    public ResourceTypeType get_resourcemap_value(String key) {

        return (resourcemap.get(key));
    }

    public Map<String, ResourceTypeType> get_resourcemap() {

        return resourcemap;
    }

    public void addsourcevalues(String value) {
        sourcevalues.add(value);
    }

    public List<String> getsourcevalues() {

        return sourcevalues;
    }

    public void addtargetvalues(String value) {
        targetvalues.add(value);
    }

    public List<String> gettargetvalues() {

        return targetvalues;
    }

    public void addoutput(String value) {
        output.add(value);
    }

    public List<String> getoutput() {

        return output;
    }

    public void addinput(String value) {
        input.add(value);
    }

    public List<String> getinput() {

        return input;
    }

    public void put_andsplitmap(String key, String value) {
        andsplitmap.put(key, value);
    }

    public String get_andsplitmap_value(String key) {

        return (andsplitmap.get(key));
    }

    public Map<String, String> get_andsplitmap() {

        return andsplitmap;
    }

    public void put_andjoinmap(String key, String value) {
        andjoinmap.put(key, value);
    }

    public String get_andjoinmap_value(String key) {
        return (andjoinmap.get(key));
    }

    public Map<String, String> get_andjoinmap() {
        return andjoinmap;
    }

    public void put_andsplitjoinmap(String key, String value) {
        andsplitjoinmap.put(key, value);
    }

    public String get_andsplitjoinmap_value(String key) {

        return (andsplitjoinmap.get(key));
    }

    public Map<String, String> get_andsplitjoinmap() {

        return andsplitjoinmap;
    }

    public void setInputnode(String node) {
        inputnode = node;
    }

    public String getInputnode() {
        if (inputnode == null) {
            inputnode = "start";
            return inputnode;
        } else {
            return inputnode;
        }
    }

    public void setOutputnode(String node) {
        outputnode = node;
    }

    public String getOutputnode() {
        if (outputnode == null) {
            outputnode = "end";
            return outputnode;
        } else {
            return outputnode;
        }
    }

    public void setOutputEdge(EdgeType edge) {
        outputedge = edge;
    }

    public EdgeType getOutputEdge() {

        return outputedge;

    }

    public void setInputEdge(EdgeType edge) {
        inputedge = edge;
    }

    public EdgeType getInputEdge() {

        return inputedge;

    }

    public void setOutputState(String id) {
        outputstate = id;
    }

    public String getOutputState() {
        return outputstate;

    }

    public void setInputEvent(String id) {
        inputevent = id;
    }

    public String getInputEvent() {
        return inputevent;

    }

    public void setCanonicalProcess(CanonicalProcessType cpt) {
        cproc = cpt;
    }

    public CanonicalProcessType getCanonicalProcess() {

        return cproc;

    }

    public void setAnnotations(AnnotationsType an) {
        annotations = an;
    }

    public AnnotationsType getAnnotations() {

        return annotations;

    }

    public void addNoderef(NodeType an) {
        noderef.add(an);
    }

    public List<NodeType> getNoderef() {

        return noderef;

    }

    public long getIds() {
        return ids;
    }

    public long nextId() {
        return ids++;
    }

    public void addAnnotationObject(Object object) {
        annotationobjects.add(object);
    }

    public List<Object> getAnnotationObjects() {

        return annotationobjects;

    }

    public void setRootId(long lid) {
        rootid = lid;
    }

    public long getRootid() {
        return rootid;
    }

    public void setSubnetTask(String tsub) {
        subnettask = tsub;
    }

    public String getSubnetTask() {
        return subnettask;
    }

    public void setFolder(File file) {
        folder = file;
    }

    public File getFolder() {
        return folder;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void addupdatedarc(ArcType value) {
        updatedarcs.add(value);
    }

    public List<ArcType> getupdatedarcs() {
        return updatedarcs;
    }

    public void put_xorcounter(String key, String value) {
        xorcounter.put(key, value);
    }

    public String get_xorcounter_value(String key) {

        return (xorcounter.get(key));
    }

    public Map<String, String> get_xorcounter() {

        return xorcounter;
    }

    public void addCenter(PlaceType place) {
        center.add(place);
    }

    public List<PlaceType> getCenter() {
        return center;
    }

    public void addCenterArc(ArcType arc) {
        centerarcs.add(arc);
    }

    public List<ArcType> getCenterArcs() {
        return centerarcs;
    }

    public void setNet(NetType net) {
        this.net = net;
    }

    public NetType getNet() {
        return net;
    }

    public long getResourceID() {
        return resourceid;
    }

    public void setResourceID(long id) {
        resourceid = id;
    }
}
