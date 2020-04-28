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
package com.signavio.platform.exceptions;

import org.apache.log4j.Logger;

/**
 * This class is the base for all runtime platform exceptions. It logs each thrown exception as fatal using log4j.
 * @author Bjoern Wagner
 *
 */

public abstract class LoggedRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 4180454029425889094L;

	private static Logger logger = Logger.getLogger(InconsistentDataException.class);

	protected void logException() {
		logger.fatal(this.getMessage(), this);
	}
	
	public LoggedRuntimeException() {
		logException();
	}

	public LoggedRuntimeException(String message) {
		super(message);
		logException();
	}

	public LoggedRuntimeException(Throwable cause) {
		super(cause);
		logException();
	}

	public LoggedRuntimeException(String message, Throwable cause) {
		super(message, cause);
		logException();
	}

}
