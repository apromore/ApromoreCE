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

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Assignments")
public class XPDLAssignments extends XMLConvertible {

    @Element("Assignment")
    protected ArrayList<XPDLAssignment> assignments;

    public void add(XPDLAssignment newAssignment) {
        initializeAssignments();

        getAssignments().add(newAssignment);
    }

    public ArrayList<XPDLAssignment> getAssignments() {
        return assignments;
    }

    public void readJSONassignmentsunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "assignmentsunknowns");
    }

    public void setAssignments(ArrayList<XPDLAssignment> assignments) {
        this.assignments = assignments;
    }

    public void writeJSONassignmentsunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "assignmentsunknowns");
    }

    public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
//		ArrayList<XPDLAssignment> assignmentsList = getAssignments();
//		if (assignmentsList != null) {
//			JSONObject assignmentsObject = new JSONObject();
//			
//			JSONArray items = new JSONArray();
//			for (int i = 0; i < assignmentsList.size(); i++) {
//				XPDLAssignment singleAssignment = assignmentsList.get(i);
//				JSONObject item = new JSONObject();
//				
//				singleAssignment.write(item);
//				items.put(item);
//				assignmentsObject.put("totalCount", i);
//			}
//			modelElement.put("items", items);
//		}
    }

    protected void initializeAssignments() {
        if (getAssignments() == null) {
            setAssignments(new ArrayList<XPDLAssignment>());
        }
    }
}
