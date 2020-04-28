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

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apromore.test.config.TestConfig;
import org.apromore.test.helper.TestLogger;
import org.apromore.test.helper.TestSetting;
import org.openqa.selenium.By;
import org.openqa.selenium.Point;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class Plugin implements Callable<String> {

	protected TestSetting testSetting;
	protected WebDriver driver;
	protected WebElement log;
	protected Actions action;
	
	protected Plugin(TestSetting testSetting)
	{
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setHeadless(TestConfig.headless);
		chromeOptions.addArguments("window-size=1200x800");
		
		this.testSetting = testSetting;	
        driver = new ChromeDriver(chromeOptions);
        action = new Actions(driver); 
        
        init_();
		/*
		 * if(log == null) { TestLogger.logTimeout(testSetting.logger,
		 * "Could not login", testSetting.wait_sec); closeBrowser(driver); return null;
		 * }
		 */
	}
	
	protected void init_()
	{
		driver.navigate().to(TestConfig.ApromoreURL);
        log = accessLog(driver);        
        
	}
	

	public WebElement accessLog(WebDriver driver)
	{
		
		WebElement log = null;
		try {
			
			long t1;
			long remainingTime = testSetting.wait_sec;
        	long duration = 0;
			
        	WebElement uernameElement = (new WebDriverWait(driver, 60))
					  .until(ExpectedConditions.presenceOfElementLocated(By.name("j_username")));
        	WebElement passwordElement = (new WebDriverWait(driver, 60))
					  .until(ExpectedConditions.presenceOfElementLocated(By.name("j_password")));
        	
			uernameElement.sendKeys(TestConfig.username);
			passwordElement.sendKeys(TestConfig.password);
			
			passwordElement.submit();        	
        	
			long stTime = t1 = System.currentTimeMillis();
			WebElement folder = (new WebDriverWait(driver, remainingTime))
					  .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(., '" + TestConfig.LoadTestFolder1 + "')]")));
			remainingTime -= (System.currentTimeMillis() - stTime) / 1000;
        	duration += (System.currentTimeMillis() - t1);
        	
	        action.moveToElement(folder).pause(1000).doubleClick().perform();
	        	        
	        t1 = stTime = System.currentTimeMillis();
	        folder = (new WebDriverWait(driver, remainingTime))
					  .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(., '" + TestConfig.LoadTestFolder2 + "')]")));
	        remainingTime -= (System.currentTimeMillis() - stTime) / 1000;
        	duration += (System.currentTimeMillis() - t1);
        	
	        action.moveToElement(folder).pause(1000).doubleClick().perform();
	        
	        t1 = stTime = System.currentTimeMillis();
	        log = (new WebDriverWait(driver, remainingTime))
	      		  .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(., '" + testSetting.logName +"')]")));
	        remainingTime -= (System.currentTimeMillis() - stTime) / 1000;
        	duration += (System.currentTimeMillis() - t1);
        	
	        //TestLogger.logDuration(testSetting.logger, "Accessing the log (including login)" , duration / 1000);
		}catch(TimeoutException ex)
		{
			TestLogger.logTimeout(testSetting.logger,
					 "Could not login", testSetting.wait_sec);
			
			//ex.printStackTrace();
			return null;
		}
		
		return log;       
		        
	}
	
	protected void closeBrowser(WebDriver driver)
	{
		driver.quit();
	}
	
	protected void logUnsuccessfulOperation(String messge)
	{
		TestLogger.logTimeout(testSetting.logger, messge, testSetting.wait_sec);
	}
}
