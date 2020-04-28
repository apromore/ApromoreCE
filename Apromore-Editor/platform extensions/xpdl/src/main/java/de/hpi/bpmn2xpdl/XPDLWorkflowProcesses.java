/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("WorkflowProcesses")
public class XPDLWorkflowProcesses extends XMLConvertible {

    @Element("WorkflowProcess")
    protected ArrayList<XPDLWorkflowProcess> workflowProcesses;

    public void add(XPDLWorkflowProcess newProcess) {
        initializeWorkflowProcesses();

        getWorkflowProcesses().add(newProcess);
    }

    public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
        if (getWorkflowProcesses() != null) {
            for (XPDLWorkflowProcess process : getWorkflowProcesses()) {
                process.createAndDistributeMapping(mapping);
            }
        }
    }

    public ArrayList<XPDLWorkflowProcess> getWorkflowProcesses() {
        return workflowProcesses;
    }

    public void readJSONworkflowprocessesunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "workflowprocessesunknowns");
    }

    public void setWorkflowProcesses(ArrayList<XPDLWorkflowProcess> newProcess) {
        this.workflowProcesses = newProcess;
    }

    public void writeJSONworkflowprocessunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "workflowprocessesunknowns");
    }

    protected void initializeWorkflowProcesses() {
        if (getWorkflowProcesses() == null) {
            setWorkflowProcesses(new ArrayList<XPDLWorkflowProcess>());
        }
    }
}
