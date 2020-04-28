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
package com.signavio.platform.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.signavio.platform.core.Platform;
import com.signavio.platform.core.PlatformInstance;
import com.signavio.platform.core.impl.FsPlatformInstanceImpl;

/**
 * This is the entry point for the application. 
 * System configuration is loaded here.
 * 
 * It is implemented as a ServletContextListener.
 * So, this is the first and the last code that is called when starting
 * the server.
 * 
 * @author nico
 *
 */
public class EntryPoint implements ServletContextListener {
	
	private final Logger logger = Logger.getLogger(EntryPoint.class);
		
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("Destroying platform...");
		Platform.shutdownInstance();
		logger.info("Done destroying platform!");
	}

	/**
	 * Boot the platform using the default {@link PlatformInstance} implementation
	 * for the servlet container.
	 */
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("Initializing platform...");
		Platform.bootInstance(FsPlatformInstanceImpl.class, sce.getServletContext());
		logger.info("Done initializing platform!");
	}

}
