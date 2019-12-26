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
package com.signavio.warehouse.business.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;

//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
//import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class JsonErdfTransformation {
	private JSONObject canvas;
	private Document doc;
	private Node root;
	private List<JSONObject> allShapes;
	
	public JsonErdfTransformation(String canvas){
		try {
			this.canvas = new JSONObject(canvas);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public JsonErdfTransformation(JSONObject canvas){
		this.canvas = canvas;
	}
	
	public String toString(){
		Document document = toDoc();
		
		OutputFormat format = new OutputFormat(document);

		StringWriter stringOut = new StringWriter();
		XMLSerializer serial2 = new XMLSerializer(stringOut, format);
		try {
			serial2.asDOMSerializer();
			serial2.serialize(document);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String serialized = stringOut.toString();
		//TODO this isn't nice at all!
		serialized = serialized.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
		serialized = serialized.replace("<root>", "");
		serialized = serialized.replace("</root>", "");

		return serialized;
	}
	
	public Document toDoc(){
		doc = createNewDocument();
		allShapes = new LinkedList<JSONObject>();
		
		createRoot();
		try {
			createCanvas();
		} catch (DOMException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return doc;
	}
	
	protected void createRoot(){
		Element r = doc.createElement("root");
		/*r.setAttribute("xmlns:rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		r.setAttribute("xmlns:admin", "http://webns.net/mvcb/");
		r.setAttribute("xmlns:doap", "http://usefulinc.com/ns/doap#");
		r.setAttribute("xmlns:dc", "http://purl.org/dc/elements/1.1/");
		r.setAttribute("xmlns:foaf", "http://xmlns.com/foaf/0.1/");
		r.setAttribute("xmlns:rdfs", "http://www.w3.org/2000/01/rdf-schema#");*/
		root = doc.appendChild(r);
	}
	
	protected void createCanvas() throws DOMException, JSONException{
		Node canvasNode = createShape(canvas);
		
		// Render element
		for(JSONObject shape : allShapes){
			canvasNode.appendChild(createOryxNsResourceElement("render", "#"+shape.getString("resourceId")));
		}
		
		// Adding class="-oryx-canvas"
		Attr canvasClass = doc.createAttribute("class");
		canvasClass.setValue("-oryx-canvas");
		canvasNode.getAttributes().setNamedItem(canvasClass);
		
		// Stencil set
		canvasNode.appendChild(createOryxNsResourceElement("stencilset", canvas.getJSONObject("stencilset").getString("url")));
	}
	
	protected Node createShape(JSONObject shape) throws DOMException, JSONException{
		Element shapeEl = doc.createElement("div");
		if (shape.has("resourceId")) {
			shapeEl.setAttribute("id", shape.getString("resourceId"));
		}
		
		// Childshapes
		JSONArray childShapes = shape.getJSONArray("childShapes");
		for(int i = 0; i < childShapes.length(); i++){
			JSONObject shapeJs = childShapes.getJSONObject(i);
			allShapes.add(shapeJs);
			// Set temporarily the parent id
			if (shape.has("resourceId")){
				shapeJs.put("parent", shape.getString("resourceId"));
			}
			createShape(shapeJs);
		}

		shapeEl.appendChild(createOryxNsElement("type", shape.getJSONObject("stencil").getString("id")));
		
		// Bounds
		if(shape.has("bounds")){
			JSONObject bounds = shape.getJSONObject("bounds");
			JSONObject lowerRight = bounds.getJSONObject("lowerRight");
			JSONObject upperLeft = bounds.getJSONObject("upperLeft");
			shapeEl.appendChild(createOryxNsElement("bounds", lowerRight.getString("x")+","+lowerRight.getString("y")+","+upperLeft.getString("x")+","+upperLeft.getString("y")));
		}
		
		// Properties
		if(shape.has("properties")){
			JSONObject props = shape.getJSONObject("properties");
			Iterator<String> it = props.keys();
			while(it.hasNext()){
				String key = it.next();
				String val = props.getString(key);
				shapeEl.appendChild(createOryxNsElement(key, val));
			}
		}
		
		// Outgoings
		if(shape.has("outgoing")){
			JSONArray outgoings = shape.getJSONArray("outgoing");
			for(int i = 0; i < outgoings.length(); i++){
				shapeEl.appendChild(createRazielNsResourceElement("outgoing", "#"+outgoings.getJSONObject(i).getString("resourceId")));
				shapeEl.appendChild(createRazielNsResourceElement("target", "#"+outgoings.getJSONObject(i).getString("resourceId")));
			}
		}
		
		// Parent
		if(shape.has("parent")){
			shapeEl.appendChild(createRazielNsResourceElement("parent", "#"+shape.getString("parent")));
		}
		
		// Dockers
		if(shape.has("dockers")){
			String dockers = " # ";
			JSONArray dockerArray = shape.getJSONArray("dockers");
			for(int i = 0; i < dockerArray.length(); i++){
				JSONObject docker = dockerArray.getJSONObject(i);
				dockers = docker.getString("x") + " " + docker.getString("y") + " " + dockers;
			}
			shapeEl.appendChild(createOryxNsElement("docker", dockers));
		}
		
		return root.appendChild(shapeEl);
	}
	
	private Document createNewDocument(){
		DocumentBuilder builder;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();
			return doc;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Element createOryxNsElement(String name, String textContent){
		Element el = doc.createElement("span");
		el.setAttribute("class", "oryx-"+name);
		el.setTextContent(textContent);
		return el;
	}
	
	private Element createRazielNsResourceElement(String name, String resource){
		Element el = doc.createElement("a");
		el.setAttribute("rel", "raziel-"+name);
		el.setAttribute("href", resource);
		return el;
	}
	private Element createOryxNsResourceElement(String name, String resource){
		Element el = doc.createElement("a");
		el.setAttribute("rel", "oryx-"+name);
		el.setAttribute("href", resource);
		return el;
	}
}
