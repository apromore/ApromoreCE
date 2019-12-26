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

package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("MessageFlows")
public class XPDLMessageFlows extends XMLConvertible {

    @Element("MessageFlow")
    protected ArrayList<XPDLMessageFlow> messageFlows;

    public void add(XPDLMessageFlow newFlow) {
        initializeMessageFlows();

        getMessageFlows().add(newFlow);
    }

    public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
        if (getMessageFlows() != null) {
            for (XPDLThing thing : getMessageFlows()) {
                thing.setResourceIdToObject(mapping);
                mapping.put(thing.getId(), thing);
            }
        }
    }

    public ArrayList<XPDLMessageFlow> getMessageFlows() {
        return messageFlows;
    }

    public void readJSONmessageflowsunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "messageflowsunknowns");
    }

    public void setMessageFlows(ArrayList<XPDLMessageFlow> flow) {
        this.messageFlows = flow;
    }

    public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
        ArrayList<XPDLMessageFlow> flows = getMessageFlows();
        if (flows != null) {
            initializeChildShapes(modelElement);

            JSONArray childShapes = modelElement.getJSONArray("childShapes");
            for (int i = 0; i < flows.size(); i++) {
                JSONObject newFlow = new JSONObject();
                flows.get(i).write(newFlow);

                childShapes.put(newFlow);
            }
        }
    }

    public void writeJSONmessageflowsunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "messageflowsunknowns");
    }

    protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
        if (modelElement.optJSONArray("childShapes") == null) {
            modelElement.put("childShapes", new JSONArray());
        }
    }

    protected void initializeMessageFlows() {
        if (getMessageFlows() == null) {
            setMessageFlows(new ArrayList<XPDLMessageFlow>());
        }
    }
}
