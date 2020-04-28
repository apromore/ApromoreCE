/*-
 * #%L
 * This file is part of "Apromore Community".
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
package org.apromore.fxes.log;

/**
 * 
 * @author Alireza Ostovar (Alirezaostovar@gmail.com)
 *
 */
public enum FXAttributeType {
	
	LITERAL, 
	DISCRETE, 
	CONTINUOUS, 
	BOOLEAN, 
	TIMESTAMP;
	
	/**
	 * Returns the type of the attribute
	 * @param a
	 * @return
	 */
	public static FXAttributeType getAttributeType(String a)
	{
		if (a.equalsIgnoreCase("string"))
			return LITERAL;
		if(a.equalsIgnoreCase("date"))
			return TIMESTAMP;
		if(a.equalsIgnoreCase("int"))
			return DISCRETE;
		if(a.equalsIgnoreCase("float"))
			return CONTINUOUS;
		if(a.equalsIgnoreCase("boolean"))
			return BOOLEAN;
		
		return LITERAL;
	}
}
