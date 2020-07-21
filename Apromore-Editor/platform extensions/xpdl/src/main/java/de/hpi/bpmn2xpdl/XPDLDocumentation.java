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

import org.json.JSONException;
import org.json.JSONObject;

import org.xmappr.RootElement;
import org.xmappr.Text;

@RootElement("Documentation")
public class XPDLDocumentation extends XMLConvertible {

    @Text
    protected String content;

    public String getContent() {
        return content;
    }

    public void readJSONdocumentation(JSONObject modelElement) {
        setContent(modelElement.optString("documentation"));
    }

    public void readJSONdocumentationunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "documentationunknowns");
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void writeJSONdocumentation(JSONObject modelElement) throws JSONException {
        modelElement.put("documentation", getContent());
    }

    public void writeJSONdocumentationunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "documentationunknowns");
    }
}
