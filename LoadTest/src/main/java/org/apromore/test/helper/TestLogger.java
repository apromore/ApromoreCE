/*-
 * #%L
 * This file is part of "Apromore Community".
 *
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
package org.apromore.test.helper;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apromore.test.config.TestConfig;

public class TestLogger {
	
	public static Logger createLogger(String name)
	{
		Logger logger = Logger.getLogger(name);
		
		try {
			Formatter recordFormatter = new Formatter() {
				
				@Override
				public String format(LogRecord record) {
					
					
					return record.getSequenceNumber() + "," +
							record.getThreadID() + "," + 
							record.getLevel() + "," + 
							record.getMessage() + "," + 
							record.getParameters()[0] + "\n";
				}
			};
			Handler fileHandler  = new FileHandler(TestConfig.logPath + name + ".csv", false);
			fileHandler.setFormatter(recordFormatter);
			logger.addHandler(fileHandler);
			fileHandler.setLevel(Level.ALL);
			logger.setLevel(Level.ALL);	
			
			
			
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return logger;
	}
	
	public static void logTimeout(Logger logger, String message, long wait_sec)
	{
		logger.log(Level.SEVERE, message + " within(sec)", wait_sec);
	}
	
	public static void logDuration(Logger logger, String message, long duration)
	{
		logger.log(Level.INFO, message + " took(sec)", duration);
	}
	
	public static void close(Logger logger)
	{
		Handler[] handlers = logger.getHandlers();
		if(handlers != null && handlers.length > 0)
		{
			handlers[0].close();
		}
	}
}
