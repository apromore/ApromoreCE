/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Queensland University of Technology.
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
package org.apromore.service.bebop.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)

public  class abstractGuideline  {



	@XmlTransient
	protected boolean status;

	@XmlTransient
	protected String IDProcess;



	@XmlAttribute(name = "id", required = true)
	protected String id;
	@XmlAttribute(name = "Name", required = true)
	protected  String Name;

	@XmlElement(name = "Description", required = true)
	protected String Description;

	@XmlElement(name = "Suggestion", required = true)
	protected String Suggestion;
	@XmlElement(name = "ElementID", required = false)
	@XmlElementWrapper(name = "Elements",  nillable=false)
	protected Collection<ElementID> Elements = null;

	abstractGuideline(){

	}


	public boolean getStatus() {

		return status;
	}

	@SuppressWarnings("null")
	public ArrayList<String> getIDsNameAndType(){
		ArrayList<String> ret = new ArrayList<String>();
		if(!getStatus()){
			if(Elements!=null){
				for(ElementID elem: Elements)ret.add(elem.toStringIDName());
			}
		}
		return ret;
	}

	@SuppressWarnings("null")
	public ArrayList<String> getID(){
		ArrayList<String> ret = new ArrayList<String>();
		if(!getStatus()){
			if(Elements!=null){
				for(ElementID elem: Elements)ret.add(elem.toString());
			}
		}
		return ret;
	}


	public String getid() {
		return id;
	}



	public Collection<ElementID> getElements() {
		return Elements;
	}



	public void setElements(String element, String refprocessid, String name) {
		if(Elements==null){
			Elements = new ArrayList<ElementID>();
		}
		Elements.add(new ElementID(element, refprocessid, name));
	}


	public String getDescription() {

		return Description;

	}

	public String getName() {

		return Name;
	}


	public String getProcessID() {
		return IDProcess;
	}

	public String getSuggestion() {
		return Suggestion;
	}




	public String getState(){
		switch (Thread.currentThread().getState()) {
			case TERMINATED:
				return "OK";

			default:
				return "IN PROGRESS";
		}

	}

	public String getColor(){
		String color = "#47d147";// "green"; //"#FF0000";
		if(!(this.getSuggestion().equals("Well done!") | this.getSuggestion().equals("Ben Fatto!"))){
			color = " #cc2900";//"red";//"#00FF7F";
		}
		return color;
	}

}
