/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Bruce Nguyen.
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
package org.apromore.service.perfmining.database;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * XML File Example: <?xml version="1.0" encoding="UTF-8" standalone="no"?>
 * <!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
 * <properties> <entry key="jdbc.driver">sun.jdbc.odbc.JdbcOdbcDriver</entry>
 * <entry key="jdbc.url">jdbc:odbc:SAW</entry> <entry
 * key="jdbc.username">root</entry> <entry key="jdbc.password">mysql</entry>
 * 
 * </properties>
 */
public class DBConnectionParamReader {
	private final String driverName;
	private final String url;
	private final String username;
	private final String password;

	public DBConnectionParamReader(String filename) throws FileNotFoundException, IOException, NumberFormatException {

		//Reading properties file in Java example
//		Properties props = new Properties();
//		FileInputStream fis = new FileInputStream(filename);

		//loading properties from properties file
//		props.loadFromXML(fis);

		//reading property
//		driverName = props.getProperty("jdbc.driver");
//		url = props.getProperty("jdbc.url");
//		username = props.getProperty("jdbc.username");
//		password = props.getProperty("jdbc.password");
                driverName = "org.apache.derby.jdbc.EmbeddedDriver";
                url = "jdbc:derby:perfminer/spf;create=true";
                username = "me";
                password = "mine";
	}

	public String getDriverName() {
		return driverName;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
