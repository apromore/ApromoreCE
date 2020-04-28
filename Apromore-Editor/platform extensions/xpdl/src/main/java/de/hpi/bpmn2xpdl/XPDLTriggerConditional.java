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

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("TriggerConditional")
public class XPDLTriggerConditional extends XMLConvertible {

    @Text
    protected String condition;

    public String getCondition() {
        return condition;
    }

    public void readJSONcondition(JSONObject modelElement) {
        setCondition(modelElement.optString("condition"));
    }

    public void readJSONtriggerresultunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "triggerresultunknowns");
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void writeJSONconditionref(JSONObject modelElement) throws JSONException {
        putProperty(modelElement, "conditionref", getCondition());
    }

    public void writeJSONtriggerresultunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "triggerresultunknowns");
    }

    protected JSONObject getProperties(JSONObject modelElement) {
        return modelElement.optJSONObject("properties");
    }

    protected void initializeProperties(JSONObject modelElement) throws JSONException {
        JSONObject properties = modelElement.optJSONObject("properties");
        if (properties == null) {
            JSONObject newProperties = new JSONObject();
            modelElement.put("properties", newProperties);
            properties = newProperties;
        }
    }

    protected void putProperty(JSONObject modelElement, String key, String value) throws JSONException {
        initializeProperties(modelElement);

        getProperties(modelElement).put(key, value);
    }
}
