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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class ElementID {

	@XmlAttribute(name = "refProcessID")
	private String refprocessid;

	@XmlAttribute(name = "refName")
	private String name;

	@XmlValue
	private String value;

	ElementID(){

	}


	public ElementID( String value, String refprocessid, String name) {

		this.name= name;
		this.value = value;
		this.refprocessid  = refprocessid;
	}


	public String getRefprocessid() {
		return refprocessid;
	}


	public String getValue() {
		return value;
	}

	public String getName(){
		return name;
	}


	@Override
	public String toString() {
		return  value;
	}

	public String toStringIDName() {
		return  name+" "+value;
	}


}
