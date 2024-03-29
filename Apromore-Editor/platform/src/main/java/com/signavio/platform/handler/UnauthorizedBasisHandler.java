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
package com.signavio.platform.handler;

import javax.servlet.ServletContext;

import com.signavio.platform.core.Platform;
import com.signavio.platform.exceptions.InconsistentDataException;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsRootUser;

/**
 * Implementation of Handler which abstracts all Handler for 
 * a specific context, e.g. Model or Directory.
 * @author Willi
 *
 */
public abstract class UnauthorizedBasisHandler extends BasisHandler {

	public UnauthorizedBasisHandler(ServletContext servletContext) {
		super(servletContext);
	}
	
	/**
	 * 
	 * @return The root token! This token has to be used in handlers that do not need authentication, e. g. login
	 */
	protected FsAccessToken getRootToken() {
		// ISSUE : This section has been changed as it used database-dependent code
		return FsAccessToken.getDummy();
	}
}
