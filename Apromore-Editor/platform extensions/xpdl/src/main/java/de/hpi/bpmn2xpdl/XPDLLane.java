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

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("Lane")
public class XPDLLane extends XPDLThingNodeGraphics {

    @Attribute("ParentLane")
    protected String parentLane;
    @Attribute("ParentPool")
    protected String parentPool;
    @Attribute("Orientation")
    protected String orientation;

    public static boolean handlesStencil(String stencil) {
        String[] types = {"Lane"};
        return Arrays.asList(types).contains(stencil);
    }

    public String getParentLane() {
        return parentLane;
    }

    public String getParentPool() {
        return parentPool;
    }

    public String getOrientation() {return orientation;
    }

    public void readJSONparentlane(JSONObject modelElement) {
        setParentLane(modelElement.optString("parentlane"));
    }

    public void readJSONparentpool(JSONObject modelElement) {
        setParentPool(modelElement.optString("parentpool"));
    }

    public void readJSONorientation(JSONObject modelElement) {
        setParentPool(modelElement.optString("orientation"));
    }

    public void readJSONshowcaption(JSONObject modelElement) {
        createExtendedAttribute("showcaption", modelElement.optString("showcaption"));
    }

    public void setParentLane(String laneId) {
        parentLane = laneId;
    }

    public void setParentPool(String poolId) {
        parentPool = poolId;
    }

    public void setOrientation(String orientation) {  this.orientation = orientation; }

    public void writeJSONstencil(JSONObject modelElement) throws JSONException {
        writeStencil(modelElement, "Lane");
    }
}
