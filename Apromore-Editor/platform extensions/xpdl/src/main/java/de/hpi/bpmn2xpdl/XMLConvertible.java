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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.DomElement;
import org.xmappr.Element;

//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;

public abstract class XMLConvertible {

    @Attribute("*")
    protected HashMap<String, String> unknownAttributes = new HashMap<String, String>();

    @Element("*")
    protected ArrayList<DomElement> unknownChildren;

    protected HashMap<String, JSONObject> resourceIdToShape;
    protected Map<String, XPDLThing> resourceIdToObject;

    public HashMap<String, String> getUnknownAttributes() {
        return unknownAttributes;
    }

    public ArrayList<DomElement> getUnknownChildren() {
        return unknownChildren;
    }

    public Map<String, XPDLThing> getResourceIdToObject() {
        return resourceIdToObject;
    }

    public HashMap<String, JSONObject> getResourceIdToShape() {
        return resourceIdToShape;
    }

    @SuppressWarnings("unchecked")
    public void parse(JSONObject modelElement) {
        Iterator jsonKeys = modelElement.keys();
        while (jsonKeys.hasNext()) {
            String key = (String) jsonKeys.next();
            String readMethodName = "readJSON" + key;
            if (hasJSONMethod(readMethodName)) {
                try {
                    if (keyNotEmpty(modelElement, key)) {
                        getClass().getMethod(readMethodName, JSONObject.class).invoke(this, modelElement);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                readJSONunknownkey(modelElement, key);
            }
        }
    }

    public void readUnknowns(JSONObject modelElement, String key) {
        String storedData = modelElement.optString(key);
        if (storedData != null) {
            XMLUnknownsContainer unknownContainer = (XMLUnknownsContainer) fromStorable(storedData);

            setUnknownAttributes(unknownContainer.getUnknownAttributes());
            setUnknownChildren(unknownContainer.getUnknownElements());
        }
    }

    public void readJSONunknownkey(JSONObject modelElement, String key) {
        System.err.println("Unknown JSON-key: " + key + "\n" +
                "in JSON-Object: " + modelElement + "\n" +
                "while parsing in: " + getClass() + "\n");
    }

    public void setResourceIdToObject(Map<String, XPDLThing> mapping) {
        resourceIdToObject = mapping;
    }

    public void setResourceIdToShape(HashMap<String, JSONObject> mapping) {
        resourceIdToShape = mapping;
    }

    public void setUnknownAttributes(HashMap<String, String> unknowns) {
        unknownAttributes = unknowns;
    }

    public void setUnknownChildren(ArrayList<DomElement> unknownElements) {
        unknownChildren = unknownElements;
    }

    public void write(JSONObject modelElement) {
        Method[] methods = getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String methodName = method.getName();
            if (methodName.startsWith("writeJSON")) {
                try {
                    getClass().getMethod(methodName, JSONObject.class).invoke(this, modelElement);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void writeUnknowns(JSONObject modelElement, String key) throws JSONException {
        HashMap<String, String> unknownAttributes = getUnknownAttributes();
        ArrayList<DomElement> unknownElements = getUnknownChildren();
        if (!unknownAttributes.isEmpty() || unknownElements != null) {
            XMLUnknownsContainer unknownsContainer = new XMLUnknownsContainer();
            unknownsContainer.setUnknownAttributes(getUnknownAttributes());
            unknownsContainer.setUnknownElements(getUnknownChildren());

            modelElement.put(key, makeStorable(unknownsContainer));
        }
    }


    protected Object fromStorable(String stored) {
        try {
            //Read Base64 String and decode them
            byte[] decodedBytes = Base64.decodeBase64(stored.getBytes("utf-8"));
            ByteArrayInputStream byteStreamIn = new ByteArrayInputStream(decodedBytes);
            //Restore the object
            ObjectInputStream objectStreamIn = new ObjectInputStream(byteStreamIn);
            return objectStreamIn.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String makeStorable(Object objectToStore) {
//        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//        try {
//            //Serialize the Java object
//            ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
//            objectStream.writeObject(objectToStore);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //Encode the byte stream with Base64 -> Readable characters for the JSONObject
//        return new String(Base64.encodeBase64(byteStream.toByteArray()));
        return "";
    }

    protected boolean hasJSONMethod(String methodName) {
        Method[] methods = getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName) &
                    hasMethodJSONParameter(methods[i])) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected boolean hasMethodJSONParameter(Method method) {
        Class[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 1) {
            return parameterTypes[0].equals(JSONObject.class);
        }
        return false;
    }

    protected boolean keyNotEmpty(JSONObject modelElement, String key) {
        try {
            JSONObject objectAtKey = modelElement.getJSONObject(key);
            //Value is a valid JSONObject and has members
            return objectAtKey.length() > 0;
        } catch (JSONException objectException) {
            try {
                JSONArray arrayAtKey = modelElement.getJSONArray(key);
                //Value is a valid JSONArray and has at least one element
                return arrayAtKey.length() > 0;
            } catch (JSONException arrayException) {
                return !modelElement.optString(key).equals("");
            }
        }
    }
}
