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
package org.apromore.test;

import java.util.logging.Logger;

import org.apromore.test.helper.TestLogger;
import org.apromore.test.helper.TestSetting;

public class Test {
	
	protected TestSetting testSetting;

	public void init(String logName, String loggerName, int nConcurrentUsers, int wait_sec)
	{
		System.setProperty("webdriver.chrome.driver", 
				"./ChromeDriver/chromedriver.exe");
		
		testSetting = new TestSetting(logName, loggerName, nConcurrentUsers, wait_sec);
	}
	
	protected void printFinalMessage()
	{
		System.out.println("All tests finished!");
		System.out.println("# of users successfully logged in (attempted the main operation) was = " + testSetting.nUsersLoggedIn);
	}
}
