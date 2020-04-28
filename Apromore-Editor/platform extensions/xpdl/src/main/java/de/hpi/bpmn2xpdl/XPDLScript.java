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
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("Script")
public class XPDLScript extends XMLConvertible {

    @Attribute("Type")
    protected String scriptType;

    public String getScriptType() {
        return scriptType;
    }

    public void readJSONexpressionlanguage(JSONObject modelElement) {
        setScriptType(modelElement.optString("expressionlanguage"));
    }

    public void readJSONexpressionunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "expressionunknowns");
    }

    public void setScriptType(String typeValue) {
        scriptType = typeValue;
    }

    public void writeJSONexpressionlanguage(JSONObject modelElement) throws JSONException {
        modelElement.put("expressionlanguage", getScriptType());
    }

    public void writeJSONexpressionunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "expressionunknowns");
    }
}