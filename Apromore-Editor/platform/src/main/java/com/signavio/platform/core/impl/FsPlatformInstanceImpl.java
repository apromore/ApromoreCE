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
package com.signavio.platform.core.impl;

import java.io.File;

import javax.servlet.ServletContext;

import com.signavio.platform.account.business.FsAccountManager;
import com.signavio.platform.core.HandlerDirectory;
import com.signavio.platform.core.PlatformInstance;
import com.signavio.platform.core.PlatformProperties;
import com.signavio.platform.exceptions.InitializationException;
import com.signavio.platform.security.business.FsAccessToken;
import com.signavio.platform.security.business.FsRootObject;
import com.signavio.platform.security.business.FsSecurityManager;
import com.signavio.platform.tenant.business.FsTenant;
import com.signavio.platform.tenant.business.FsTenantManager;
import com.signavio.usermanagement.business.FsRoleManager;
import com.signavio.warehouse.business.FsEntityManager;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.directory.business.FsRootDirectory;
import com.signavio.warehouse.model.business.ModelTypeManager;

public class FsPlatformInstanceImpl implements PlatformInstance {

	private HandlerDirectory handlerManger;
	private ServletContext servletContext;
	private FsPlatformPropertiesImpl platformProperties;
	
	public void bootInstance(Object... parameters) {
		if (parameters.length < 1 || (parameters.length >= 1 && !(parameters[0] instanceof ServletContext))) {
			throw new InitializationException("Boot of servlet container PlatformInstance failed, because ServletContext parameter is missing.");
		}
		// load configuration
		this.servletContext = (ServletContext) parameters[0];

		this.platformProperties = new FsPlatformPropertiesImpl(servletContext);
		
		FsRootDirectory.createInstance(this.platformProperties.getRootDirectoryPath());
		ModelTypeManager.createInstance();
		
		this.handlerManger = new HandlerDirectory(servletContext);
		this.handlerManger.start();
	
		FsAccessToken token = null;
		try {
			token = FsSecurityManager.createToken("root", "root", null);
		} catch (Exception e) {
			// cannot happen
		}
		FsRootObject root = FsRootObject.getRootObject(token);
		@SuppressWarnings("unused")
		FsAccountManager accountManager = root.getAccountManager();
		FsTenantManager tenantManager = root.getTenantManager();
		
		FsTenant onlyTenant = tenantManager.getChildren(FsTenant.class).iterator().next();
		@SuppressWarnings("unused")
		FsRoleManager roleManagerForTenant = FsRoleManager.getTenantManagerInstance(FsRoleManager.class, onlyTenant, token);
		FsEntityManager entityManagerForTenant = FsEntityManager.getTenantManagerInstance(FsEntityManager.class, onlyTenant, token);
		@SuppressWarnings("unused")
		FsDirectory rootDir = entityManagerForTenant.getTenantRootDirectory();
		


	}
	
	public void shutdownInstance() {
		
	}

	public File getFile(String path) {
		return new File(servletContext.getRealPath(path));
	}

	public HandlerDirectory getHandlerDirectory() {
		return handlerManger;
	}

	public PlatformProperties getPlatformProperties() {
		return platformProperties;
	}


}
