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

package ee.ut.mining.log;

import java.io.File;

import org.deckfour.xes.in.XMxmlGZIPParser;
import org.deckfour.xes.in.XMxmlParser;
import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;

public class XLogReader {
	public static XLog openLog(String inputLogFileName) throws Exception {
		XLog log = null;

		if(inputLogFileName.toLowerCase().contains("mxml.gz")){
			XMxmlGZIPParser parser = new XMxmlGZIPParser();
			if(parser.canParse(new File(inputLogFileName))){
				try {
					log = parser.parse(new File(inputLogFileName)).get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else if(inputLogFileName.toLowerCase().contains("mxml") || 
				inputLogFileName.toLowerCase().contains("xml")){
			XMxmlParser parser = new XMxmlParser();
			if(parser.canParse(new File(inputLogFileName))){
				try {
					log = parser.parse(new File(inputLogFileName)).get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if(inputLogFileName.toLowerCase().contains("xes.gz")){
			XesXmlGZIPParser parser = new XesXmlGZIPParser();
			if(parser.canParse(new File(inputLogFileName))){
				try {
					log = parser.parse(new File(inputLogFileName)).get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else if(inputLogFileName.toLowerCase().contains("xes")){
			XesXmlParser parser = new XesXmlParser();
			if(parser.canParse(new File(inputLogFileName))){
				try {
					log = parser.parse(new File(inputLogFileName)).get(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}


		if(log == null)
			throw new Exception("Oops ...");
		
		return log;
	}
}
