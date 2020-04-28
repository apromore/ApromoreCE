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

public class PredictiveMonitoringTraining extends Plugin
{

	public PredictiveMonitoringTraining(TestSetting testSetting) throws Exception
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
		action.moveToElement(log).click().perform();  
        
        WebElement analyzeMenu = driver.findElement(By.xpath("//span[contains(text(), 'Monitor')]"));
        action.moveToElement(analyzeMenu).click().perform();  
        
        WebElement trainingItem = (new WebDriverWait(driver,
        		testSetting.wait_sec)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(), 'Train Predictor with Log')]")));    
        
        action.moveToElement(trainingItem).click().perform(); 
    	
        WebElement trainModelButton = (new WebDriverWait(driver,
        		testSetting.wait_sec)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(text(), 'Train model')]")));       
        
 		action.moveToElement(trainModelButton).perform();
	}

	public String call() {
		
		if(log != null)
		{
			try {
	            
	            long t1;        	
	        	long remainingTime = testSetting.wait_sec;
	        	long duration = 0;
	        	
	        	action.click().perform(); 
	        	   	
	        	long stTime = t1 = System.currentTimeMillis();
	    		(new WebDriverWait(driver,
	        			remainingTime, TestConfig.pollingInMillis)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(., 'RUNNING')]")));    
	    		remainingTime -= (System.currentTimeMillis() - stTime) / 1000;
	    		
	    		System.out.println("RUNNING observed");
	    		
	    		duration += (System.currentTimeMillis() - t1); 
	    		
	        	t1 = stTime = System.currentTimeMillis();     
	        	(new WebDriverWait(driver,
	        			remainingTime, TestConfig.pollingInMillis)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(., 'COMPLETED')]")));    
	        	remainingTime -= (System.currentTimeMillis() - stTime) / 1000;
	        	
	        	duration += (System.currentTimeMillis() - t1);       	              
	            TestLogger.logDuration(testSetting.logger, "Training prediction model" , duration / 1000);
	           
	        }catch(TimeoutException ex)
			{
	        	logUnsuccessfulOperation("Could not finish training prediction model");
			}finally
			{
				closeBrowser(driver);
			}
		}
        
		
		return null;
	}
	

}
