/**
 * Copyright (c) 2009, Ole Eckermann, Stefan Krumnow & Signavio GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.signavio.warehouse.business.util.jpdl4;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Process {

    private String name;
    private String key;
    private String version;
    private String description;
    private String namespace = "http://jbpm.org/4.4/jpdl";

    private List<com.signavio.warehouse.business.util.jpdl4.Node> childNodes;
    private HashMap<String, com.signavio.warehouse.business.util.jpdl4.Node> children;
    private Node root;

    public Process(Node rootNode) {
        this.root = rootNode;
        childNodes = new ArrayList<com.signavio.warehouse.business.util.jpdl4.Node>();
        children = new HashMap<String, com.signavio.warehouse.business.util.jpdl4.Node>();

        NamedNodeMap attributes = root.getAttributes();
        this.name = JpdlToJson.getAttribute(attributes, "name");
        this.key = JpdlToJson.getAttribute(attributes, "key");
        this.version = JpdlToJson.getAttribute(attributes, "version");
        this.description = JpdlToJson.getAttribute(attributes, "description");

        if (root.hasChildNodes()) {
            int x = 0;
            try {
                for (Node node = root.getFirstChild(); node != null; node = node
                        .getNextSibling()) {

                    String stencil = node.getNodeName();
                    com.signavio.warehouse.business.util.jpdl4.Node item = null;
                    if (stencil.equals("start"))
                        item = new StartEvent(node);
                    else if (stencil.equals("end"))
                        item = new EndEvent(node);
                    else if (stencil.equals("end-error"))
                        item = new EndErrorEvent(node);
                    else if (stencil.equals("end-cancel"))
                        item = new EndCancelEvent(node);
                    else if (stencil.equals("task"))
                        item = new Task(node);
                    else if (stencil.equals("state"))
                        item = new State(node);
                    else if (stencil.equals("java"))
                        item = new Java(node);
                    else if (stencil.equals("esb"))
                        item = new Esb(node);
                    else if (stencil.equals("sql"))
                        item = new Sql(node);
                    else if (stencil.equals("hql"))
                        item = new Hql(node);
                    else if (stencil.equals("script"))
                        item = new Script(node);
                    else if (stencil.equals("join") || stencil.equals("fork"))
                        item = new And(node);
                    else if (stencil.equals("decision"))
                        item = new Xor(node);
                    else if (stencil.equals("custom"))
                        item = new Custom(node);
                    if (item != null) {
                        childNodes.add(item);
                        try {
                            String nodeName = node.getAttributes()
                                    .getNamedItem("name").getNodeValue();
                            children.put(nodeName, item);
                        } catch (Exception e) {
                            children.put("start" + x, item);
                            x++;
                        }
                    }
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    public Process(JSONObject process) {
        try {
            this.name = process.getJSONObject("properties").getString("name");
        } catch (JSONException e) {
        }

        try {
            this.key = process.getJSONObject("properties").getString("key");
        } catch (JSONException e) {
        }

        try {
            this.version = process.getJSONObject("properties").getString(
                    "version");
        } catch (JSONException e) {
        }

        try {
            this.namespace = process.getJSONObject("properties").getString("namespace");
        } catch (JSONException e) {
            this.namespace = "http://jbpm.org/4.4/jpdl";
        }

        try {
            this.description = process.getJSONObject("properties").getString(
                    "documentation");
        } catch (JSONException e) {
        }

        childNodes = new ArrayList<com.signavio.warehouse.business.util.jpdl4.Node>();

        try {
            JSONArray processElements = process.getJSONArray("childShapes");

            // Create all process nodes
            for (int i = 0; i < processElements.length(); i++) {
                JSONObject currentElement = processElements.getJSONObject(i);
                String currentElementID = currentElement.getJSONObject(
                        "stencil").getString("id");
                com.signavio.warehouse.business.util.jpdl4.Node item = null;
                if (currentElementID.equals("StartEvent"))
                    item = new StartEvent(currentElement);
                else if (currentElementID.equals("EndEvent"))
                    item = new EndEvent(currentElement);
                else if (currentElementID.equals("EndErrorEvent"))
                    item = new EndErrorEvent(currentElement);
                else if (currentElementID.equals("EndCancelEvent"))
                    item = new EndCancelEvent(currentElement);
                else if (currentElementID.equals("Task"))
                    item = new Task(currentElement);
                else if (currentElementID.equals("wait"))
                    item = new State(currentElement);
                else if (currentElementID.equals("java"))
                    item = new Java(currentElement);
                else if (currentElementID.equals("esb"))
                    item = new Esb(currentElement);
                else if (currentElementID.equals("sql"))
                    item = new Sql(currentElement);
                else if (currentElementID.equals("hql"))
                    item = new Hql(currentElement);
                else if (currentElementID.equals("script"))
                    item = new Script(currentElement);
                else if (currentElementID.equals("AND_Gateway"))
                    item = new And(currentElement);
                else if (currentElementID.equals("Exclusive_Databased_Gateway"))
                    item = new Xor(currentElement);
                else if (currentElementID.equals("custom"))
                    item = new Custom(currentElement);

                if (item != null)
                    childNodes.add(item);
            }
        } catch (JSONException e) {
        }
    }

    public String toJpdl() throws InvalidModelException {
        StringWriter jpdl = new StringWriter();
        jpdl.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        jpdl.write("<process");
        jpdl.write(JsonToJpdl.transformAttribute("name", name));
        jpdl.write(JsonToJpdl.transformAttribute("key", key));
        jpdl.write(JsonToJpdl.transformAttribute("version", version));
        jpdl.write(JsonToJpdl.transformAttribute("description", description));
        jpdl.write(JsonToJpdl.transformAttribute("xmlns",
                this.namespace));
        jpdl.write(" >\n\n");

        for (int i = 0; i < childNodes.size(); i++) {
            jpdl.write(childNodes.get(i).toJpdl());
        }

        jpdl.write("</process>");
        return jpdl.toString();
    }

    public void createTransitions() {
        int x = 0;
        for (Node node = root.getFirstChild(); node != null; node = node
                .getNextSibling()) {
            if (!node.getNodeName().equals("#text")) {
                com.signavio.warehouse.business.util.jpdl4.Node currentStencil;
                try {
                    String currentStencilName = node.getAttributes()
                            .getNamedItem("name").getNodeValue();
                    currentStencil = children.get(currentStencilName);
                } catch (Exception e) {
                    currentStencil = children.get("start" + x);
                    x++;
                }
                List<Transition> outgoings = new ArrayList<Transition>();
                if (node.hasChildNodes()) {
                    for (Node item = node.getFirstChild(); item != null; item = item
                            .getNextSibling()) {
                        if (item.getNodeName().equals("transition")) {
                            Transition t = new Transition(item);
                            t.setStart(new Docker(currentStencil.getBounds()
                                    .getWidth() / 2, currentStencil.getBounds()
                                    .getHeight() / 2));
                            outgoings.add(t);
                        }
                    }
                }
                currentStencil.setOutgoings(outgoings);
            }
        }
    }

    public String toJson() throws JSONException {
        JSONObject process = new JSONObject();

        JSONObject stencilset = new JSONObject();
        stencilset.put("url", "/editor/stencilsets/jbpm4/jbpm4.json");
        stencilset.put("namespace", "http://b3mn.org/stencilset/jbpm4#");

        JSONArray extensions = new JSONArray();

        JSONObject stencil = new JSONObject();
        stencil.put("id", "BPMNDiagram");

        JSONObject properties = new JSONObject();

        if (name != null)
            properties.put("name", name);
        if (key != null)
            properties.put("key", key);
        if (version != null)
            properties.put("version", version);
        if (description != null)
            properties.put("documentation", description);
        if (namespace != null) {
            properties.put("namespace", namespace);
        }

        process.put("resourceId", "oryx-canvas123");
        process.put("stencilset", stencilset);
        process.put("ssextensions", extensions);
        process.put("stencil", stencil);
        process.put("properties", properties);
        JSONArray childShapes = new JSONArray();

        // add all childShapes
        for (com.signavio.warehouse.business.util.jpdl4.Node n : childNodes) {
            childShapes.put(n.toJson());
            for (Transition t : n.getOutgoings())
                childShapes.put(t.toJson());
        }

        process.put("childShapes", childShapes);
        return process.toString();
    }

    public com.signavio.warehouse.business.util.jpdl4.Node getTarget(String targetName) {
        return children.get(targetName);
    }
}
