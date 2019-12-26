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

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("RedefinableHeader")
public class XPDLRedefinableHeader extends XMLConvertible {

    @Element("Author")
    protected XPDLAuthor author;
    @Element("Version")
    protected XPDLVersion version;

    public XPDLAuthor getAuthor() {
        return author;
    }

    public XPDLVersion getVersion() {
        return version;
    }

    public void readJSONauthor(JSONObject modelElement) throws JSONException {
        passInformationToAuthor(modelElement, "author");
    }

    public void readJSONauthorunknowns(JSONObject modelElement) throws JSONException {
        passInformationToAuthor(modelElement, "authorunknowns");
    }

    public void readJSONredefinableheaderunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "redefinableheaderunknowns");
    }

    public void readJSONversion(JSONObject modelElement) throws JSONException {
        passInformationToVersion(modelElement, "version");
    }

    public void readJSONversionunknowns(JSONObject modelElement) throws JSONException {
        passInformationToVersion(modelElement, "versionunknowns");
    }

    public void setAuthor(XPDLAuthor authorValue) {
        author = authorValue;
    }

    public void setVersion(XPDLVersion versionValue) {
        version = versionValue;
    }

    public void writeJSONauthor(JSONObject modelElement) {
        XPDLAuthor authorObject = getAuthor();
        if (authorObject != null) {
            authorObject.write(modelElement);
        }
    }

    public void writeJSONredefinableheaderunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "redefinableheaderunknowns");
    }

    public void writeJSONversion(JSONObject modelElement) {
        XPDLVersion versionObject = getVersion();
        if (versionObject != null) {
            versionObject.write(modelElement);
        }
    }

    protected void initializeAuthor() {
        if (getAuthor() == null) {
            setAuthor(new XPDLAuthor());
        }
    }

    protected void initializeVersion() {
        if (getVersion() == null) {
            setVersion(new XPDLVersion());
        }
    }

    protected void passInformationToAuthor(JSONObject modelElement, String key) throws JSONException {
        initializeAuthor();

        JSONObject passObject = new JSONObject();
        passObject.put(key, modelElement.optString(key));
        getAuthor().parse(passObject);
    }

    protected void passInformationToVersion(JSONObject modelElement, String key) throws JSONException {
        initializeVersion();

        JSONObject passObject = new JSONObject();
        passObject.put(key, modelElement.optString(key));
        getVersion().parse(passObject);
    }
}