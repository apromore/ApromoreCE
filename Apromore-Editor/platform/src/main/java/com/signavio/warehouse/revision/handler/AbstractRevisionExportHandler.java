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
package com.signavio.warehouse.revision.handler;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import com.signavio.platform.annotations.HandlerExportConfiguration;
import com.signavio.platform.handler.ExportHandler;
import com.signavio.platform.security.business.FsSecureBusinessObject;
import com.signavio.warehouse.model.business.FsModel;
import com.signavio.warehouse.revision.business.FsModelRevision;

public abstract class AbstractRevisionExportHandler extends ExportHandler {

	public AbstractRevisionExportHandler(ServletContext servletContext) {
		super(servletContext);
	}

	@Override
	protected void setFileName(FsSecureBusinessObject sbo, HttpServletResponse res) {

		FsModel m = null;
		
		if(sbo instanceof FsModel) {
			m = (FsModel)sbo;
		} else {
			FsModelRevision mr = (FsModelRevision)sbo;
			Set<FsModel> parents = mr.getParents(false, FsModel.class);
			if(parents.size() == 0) {
				parents = mr.getParents(true, FsModel.class);
			}
			m = parents.iterator().next();
		}
		
		if (m != null) {
			HandlerExportConfiguration an = this.getClass().getAnnotation(HandlerExportConfiguration.class);
			res.setHeader("Content-Disposition", " "+(an.download()?"attachment":"inline")+"; filename=\"" + m.getName() + "." + an.name().toLowerCase()+"\"");
		}
	}
}
