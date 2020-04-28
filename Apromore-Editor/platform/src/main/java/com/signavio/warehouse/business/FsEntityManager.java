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
package com.signavio.warehouse.business;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.signavio.platform.security.business.FsBusinessObjectManager;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.platform.tenant.business.FsTenant;
import com.signavio.usermanagement.user.business.FsUser;
import com.signavio.warehouse.directory.business.FsDirectory;
import com.signavio.warehouse.directory.business.FsRootDirectory;

/**
 * Implementation of an Entity Manager for file system accessing Oryx.
 * 
 * @author Stefan Krumnow
 *
 */
public class FsEntityManager extends FsBusinessObjectManager {

	private static FsEntityManager SINGLETON;
	public static final String ID_OF_SINGLETON = "entity-mgr-object";
	private static final Set<FsEntityManager> SINGLETON_IN_SET;
	
	private FsTenant tenant;
	
	static {
		SINGLETON = new FsEntityManager();
		SINGLETON_IN_SET = new HashSet<FsEntityManager>(1);
		SINGLETON_IN_SET.add(SINGLETON);
	}
	
	public static FsEntityManager getSingleton() {
		return SINGLETON;
	}
	
	public static Set<FsEntityManager> getSingletonSet() {
		return SINGLETON_IN_SET;
	}
	
	public FsEntityManager() {
		tenant = FsTenant.getSingleton();
	}
	

	public FsDirectory getTenantRootDirectory() {
		return tenant.getRootDirectory();
	}

	public void createPrivateRootDirectory(FsUser user) {
		throw new UnsupportedOperationException("Filesystem accessing backend cannot create private directories.");
	}

	public void createTrashFolder(FsUser user) {
		throw new UnsupportedOperationException("Filesystem accessing backend cannot create trash directories.");
		
	}

	@SuppressWarnings("unchecked")
	public List<FsSecureBusinessObject> searchWarehouse(String searchTerm) {
		List<FsSecureBusinessObject> result = new ArrayList<FsSecureBusinessObject>();
		FsRootDirectory.getSingleton().search(searchTerm, result);
		return result;
	}

	@Override
	public String getId() {
		return ID_OF_SINGLETON;
	}
	
}
