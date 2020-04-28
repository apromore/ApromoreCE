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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.apromore.test.config.TestConfig;
import org.apromore.test.helper.TestLogger;
import org.apromore.test.plugins.ProcessDiscoverer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;


public class ProcessDiscovererTest extends org.apromore.test.Test {

	public ProcessDiscovererTest()
	{
		super.init(TestConfig.logName, 
				getClass().getName(),				
				TestConfig.nConcurrentUsers,
				TestConfig.wait_sec);
	}
	
	@Test
	public void test()
	{					
		ExecutorService executorService = Executors.newFixedThreadPool(testSetting.nConcurrentUsers);
		
		List<ProcessDiscoverer> pluginList = new ArrayList<ProcessDiscoverer>(testSetting.nConcurrentUsers);
		for(int i = 0; i < testSetting.nConcurrentUsers; i++)
		{
			try {
				pluginList.add(new ProcessDiscoverer(testSetting));
			}catch(Exception ex)
			{
				
			}
		}		
				
		try {
			executorService.invokeAll(pluginList);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		printFinalMessage();
		
		TestLogger.close(testSetting.logger);
	}
	
	
	
}


