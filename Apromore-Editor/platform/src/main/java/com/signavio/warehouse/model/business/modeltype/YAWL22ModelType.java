/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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
package com.signavio.warehouse.model.business.modeltype;

import com.signavio.warehouse.model.business.ModelTypeRequiredNamespaces;

/**
 * Stores YAWL Models in Signavio Warehouse
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 *
 */

@ModelTypeRequiredNamespaces(namespaces={"http://b3mn.org/stencilset/yawl2.2#"})
public class YAWL22ModelType extends SignavioModelType {
	
	
	@Override
	public boolean acceptUsageForTypeName(String namespace) {
		for(String ns : this.getClass().getAnnotation(ModelTypeRequiredNamespaces.class).namespaces()) {
			if(ns.equals(namespace)) {
				return true;
			}
		}
		return false;
	}

}
