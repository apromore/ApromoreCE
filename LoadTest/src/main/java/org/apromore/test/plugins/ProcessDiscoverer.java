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
package org.apromore.test.plugins;

import org.apromore.test.config.TestConfig;
import org.apromore.test.helper.TestLogger;
import org.apromore.test.helper.TestSetting;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ProcessDiscoverer extends Plugin
{

	public ProcessDiscoverer(TestSetting testSetting) throws Exception
	{
		super(testSetting);
		if(log != null)
		{	
			try {
				init();
				testSetting.nUsersLoggedIn++;
			}catch(Exception ex)
			{
				closeBrowser(driver);	
				throw ex;
			}	
			
		}else
		{
			closeBrowser(driver);
		}
	}
	
	private void init()
	{
		action.moveToElement(log).perform();
	}

	public String call() {
              
        try {        	
        	action.doubleClick().perform(); 
        	
            long t1;        	
        	long remainingTime = testSetting.wait_sec;
        	long duration = 0;
        	
        	long stTime = t1 = System.currentTimeMillis();     	
        	(new WebDriverWait(driver,
        			remainingTime, TestConfig.pollingInMillis)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(., 'Process Discoverer')]"))
        					);                   
        	remainingTime -= (System.currentTimeMillis() - stTime) / 1000;        	
           
           duration += (System.currentTimeMillis() - t1);
       	   // wait for certain amount of time until the loading popup appears if it is going to of course
           WebElement loading = null;
           try {
        	   loading = (new WebDriverWait(driver,
	        		   5)).until(ExpectedConditions.visibilityOfElementLocated(By.className("z-loading-indicator"))); 
	        }catch(TimeoutException ex){}
           
           if(loading != null)
           {
        	   t1 = stTime = System.currentTimeMillis();
               (new WebDriverWait(driver,
            		   remainingTime, TestConfig.pollingInMillis)).until(ExpectedConditions.invisibilityOf(loading));        
       		
               duration += (System.currentTimeMillis() - t1);
           }
           
           TestLogger.logDuration(testSetting.logger, "Openning process map" , duration / 1000);
           
        }catch(TimeoutException ex)
		{
        	logUnsuccessfulOperation("Could not open process map");
		}finally
		{
			closeBrowser(driver);
		}
		
		return null;
	}
	
	
	

}
