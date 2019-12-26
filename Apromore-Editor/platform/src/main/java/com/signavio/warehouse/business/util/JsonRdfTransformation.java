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

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.servlet.ServletContext;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.json.JSONObject;

public class JsonRdfTransformation {

	private String erdf;
	private ServletContext context;

	public JsonRdfTransformation(JSONObject json, ServletContext context){
		this.erdf = jsonToErdf(json);
		this.context = context;
	}

	public JsonRdfTransformation(String json, ServletContext context){
		this.erdf = jsonToErdf(json);
		this.context = context;
	}
	
    static protected boolean isJson(String content){
    	return !content.startsWith("<");
    }
    
	public String toString(){
		try {
			return erdfToRdf(this.erdf);
		} catch (TransformerException e) {}
		return "";
	}
    
	private String erdfToRdf(String erdf) throws TransformerException{
		String serializedDOM = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
		"<html xmlns=\"http://www.w3.org/1999/xhtml\" " +
		"xmlns:b3mn=\"http://b3mn.org/2007/b3mn\" " +
		"xmlns:ext=\"http://b3mn.org/2007/ext\" " +
		"xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" "  +
		"xmlns:atom=\"http://b3mn.org/2007/atom+xhtml\">" +
		"<head profile=\"http://purl.org/NET/erdf/profile\">" +
		"<link rel=\"schema.dc\" href=\"http://purl.org/dc/elements/1.1/\" />" +
		"<link rel=\"schema.dcTerms\" href=\"http://purl.org/dc/terms/ \" />" +
		"<link rel=\"schema.b3mn\" href=\"http://b3mn.org\" />" +
		"<link rel=\"schema.oryx\" href=\"http://oryx-editor.org/\" />" +
		"<link rel=\"schema.raziel\" href=\"http://raziel.org/\" />" +
		"</head><body>" + erdf + "</body></html>";
        
		InputStream xsltStream = this.context.getResourceAsStream("/WEB-INF/xslt/extract-rdf.xsl");
        Source xsltSource = new StreamSource(xsltStream);
        Source erdfSource = new StreamSource(new StringReader(serializedDOM));

        TransformerFactory transFact =
                TransformerFactory.newInstance();
        Transformer trans = transFact.newTransformer(xsltSource);
        StringWriter output = new StringWriter();
        trans.transform(erdfSource, new StreamResult(output));
		return output.toString();
	}
	
	protected static String erdfToJson(String erdf, String serverUrl){
		return "";
		// TODO: Implement
		/*DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document rdfDoc = builder.parse(new ByteArrayInputStream(erdfToRdf(erdf).getBytes()));
			return RdfJsonTransformation.toJson(rdfDoc, serverUrl).toString();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;*/
	}
	
	protected static String jsonToErdf(String json){
		return new JsonErdfTransformation(json).toString();
	}

	protected static String jsonToErdf(JSONObject json){
		return new JsonErdfTransformation(json).toString();
	}
}
