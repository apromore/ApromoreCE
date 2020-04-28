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
/**
 * 
 */
package com.signavio.platform.exceptions;

/**
 * @author bjnwagner
 *
 */
public class RequestException extends RuntimeException {

	
	private String errorCode;
	private String[] params;
	private int httpStatusCode = 500;
	
	/**
	 * @param errorCode
	 */
	public RequestException(String errorCode) {
		super("RequestException Error Code: " + errorCode);
		this.errorCode = errorCode;
	}

	/**
	 * @param errorCode
	 * @param cause
	 */
	public RequestException(String errorCode, Throwable cause) {
		super("RequestException Error Code: " + errorCode, cause);
		this.errorCode = errorCode;
	}
	
	/**
	 * @param errorCode
	 */
	public RequestException(String errorCode, String ... params) {
		super("RequestException Error Code: " + errorCode);
		this.errorCode = errorCode;
		this.params = params;
	}

	/**
	 * @param errorCode
	 * @param cause
	 */
	public RequestException(String errorCode, Throwable cause, String ... params) {
		super("RequestException Error Code: " + errorCode, cause);
		this.errorCode = errorCode;
		this.params = params;
	}
	
	public RequestException(String errorCode, int httpStatusCode,
			String[] params) {
		super("RequestException Error Code: " + errorCode);
		this.errorCode = errorCode;
		this.httpStatusCode = httpStatusCode;
		this.params = params;
	}
	
	public RequestException(String errorCode, Throwable cause, int httpStatusCode,
			String[] params) {
		super("RequestException Error Code: " + errorCode, cause);
		this.errorCode = errorCode;
		this.httpStatusCode = httpStatusCode;
		this.params = params;
	}
	
	public RequestException(String errorCode, int httpStatusCode) {
		super("RequestException Error Code: " + errorCode);
		this.errorCode = errorCode;
		this.httpStatusCode = httpStatusCode;
	}
	
	public RequestException(String errorCode, Throwable cause, int httpStatusCode) {
		super("RequestException Error Code: " + errorCode, cause);
		this.errorCode = errorCode;
		this.httpStatusCode = httpStatusCode;
	}

	public String getErrorCode() {
		return errorCode;
	}
	
	public String[] getParams() {
		return params;
	}
	
	public int getHttpStatusCode() {
		return httpStatusCode;
	}
}
