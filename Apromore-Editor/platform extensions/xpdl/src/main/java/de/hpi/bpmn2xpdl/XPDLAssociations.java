/*-
 * #%L
 * This file is part of "Apromore Community".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Associations")
public class XPDLAssociations extends XMLConvertible {

    @Element("Association")
    protected ArrayList<XPDLAssociation> associations;

    public void add(XPDLAssociation newAssociation) {
        initializeAssociations();

        getAssociations().add(newAssociation);
    }

    public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
        if (getAssociations() != null) {
            for (XPDLThing thing : getAssociations()) {
                thing.setResourceIdToObject(mapping);
                mapping.put(thing.getId(), thing);
            }
        }
    }

    public ArrayList<XPDLAssociation> getAssociations() {
        return associations;
    }

    public void readJSONassociationsunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "associationsunknowns");
    }

    public void setAssociations(ArrayList<XPDLAssociation> association) {
        this.associations = association;
    }

    public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
        ArrayList<XPDLAssociation> connections = getAssociations();
        if (connections != null) {
            initializeChildShapes(modelElement);

            JSONArray childShapes = modelElement.getJSONArray("childShapes");
            for (int i = 0; i < connections.size(); i++) {
                JSONObject newConnection = new JSONObject();
                connections.get(i).write(newConnection);

                childShapes.put(newConnection);
            }
        }
    }

    public void writeJSONassociationsunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "associationsunknowns");
    }

    protected void initializeAssociations() {
        if (getAssociations() == null) {
            setAssociations(new ArrayList<XPDLAssociation>());
        }
    }

    protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
        if (modelElement.optJSONArray("childShapes") == null) {
            modelElement.put("childShapes", new JSONArray());
        }
    }
}
