/*-
 * #%L
 * This file is part of "Apromore Community".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package au.ltl.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class for reading and printing on stdout an {@link InputStream} in a separated thread.
 * 
 *
 */
public class StreamAsyncReader extends Thread {
	
	/**
	 * The {@link InputStream} to be handled.
	 */
	InputStream inputStream;
	
	/**
	 * The tag to be associated to each prints.
	 */
	String type;

	public StreamAsyncReader(InputStream inputStream, String type) {
		this.inputStream = inputStream;
		this.type = type;
	}

	@Override
	public void run() {
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line = null;
			while ((line = bufferedReader.readLine()) != null)
				System.out.println(type + ">" + line);
			
		} catch (IOException e) {
			e.printStackTrace();  
		}
	}
}
