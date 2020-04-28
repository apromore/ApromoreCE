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
package com.signavio.platform.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;



/**
 * 
 * Encoding filter sets the character encoding of the request and the response
 * to the encoding specified as init-param "encoding" in the web.xml file.
 * 
 * @author Nicolas Peters
 * 
 */
public class EncodingFilter implements Filter {

	private static final Logger logger = Logger.getLogger(EncodingFilter.class);

	private String encoding = null;

	/** PUBLIC METHODS */

	public void destroy() {
		logger.info("EncodingFilter destroyed...");
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (logger.isDebugEnabled()) {
			logger.debug("Filtering: "
					+ ((HttpServletRequest) request).getRequestURL());
		}
		String encoding = getEncoding();
		
		if (encoding != null) {
			//request.setCharacterEncoding(encoding);
			response.setCharacterEncoding(encoding);
		}

		chain.doFilter(request, response);

		if (logger.isDebugEnabled()) {
			logger.debug("Leaving");
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("EncodingFilter initializing...");
		this.setEncoding(filterConfig.getInitParameter("encoding"));
		logger.info("Using encoding: " + this.encoding);
	}

	/** PROTECTED METHODS */
	protected String getEncoding() {
		return (this.encoding);
	}

	protected void setEncoding(String enc) {
		if (enc != null) {
			this.encoding = enc;
		}
	}
}
