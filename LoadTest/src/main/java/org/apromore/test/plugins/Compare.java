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
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Compare extends Plugin
{

	private String item1, item2;
	
	public Compare(TestSetting testSetting, String item1, String item2) throws Exception
	{
		super(testSetting);
		this.item1 = item1;
		this.item2 = item2;
		
		try {
			init();
			testSetting.nUsersLoggedIn++;
		}catch(Exception ex)
		{
			closeBrowser(driver);
			throw ex;
		}	
	}
	
	private void init()
	{
		action.moveToElement(log).doubleClick().perform();  
        
        WebElement item1El = (new WebDriverWait(driver,
    			60)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(), '" + item1 + "')]"))); 
        
        WebElement item2El = (new WebDriverWait(driver,
    			60)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(), '" + item2 + "')]")));
        
      
        action.moveToElement(item1El).click().perform();  
        action.keyDown(Keys.CONTROL).perform();
        action.moveToElement(item2El).click().perform();  
        action.keyUp(Keys.CONTROL).perform();
        
        WebElement analyzeMenu = driver.findElement(By.xpath("//span[contains(text(), 'Analyze')]"));
        action.moveToElement(analyzeMenu).click().perform();  
        
        WebElement compareItem = (new WebDriverWait(driver,
    			60)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[./text()='Compare']"))).get(1);    
        
        action.moveToElement(compareItem).perform();
	}

	public String call() {

        try {
        	
        	long duration = 0;
        	
        	action.click().perform(); 
        	
       	
        	// wait for certain amount of time until the loading popup appears if it is going to of course
            WebElement loading = null;
            try {
         	   loading = (new WebDriverWait(driver,
 	        		   5)).until(ExpectedConditions.visibilityOfElementLocated(By.className("z-loading-indicator"))); 
 	        }catch(TimeoutException ex){}
            
            
            
            if(loading != null)
            {
         	   long t1 = System.currentTimeMillis();
                (new WebDriverWait(driver,
             		   testSetting.wait_sec, TestConfig.pollingInMillis)).until(ExpectedConditions.invisibilityOf(loading));        
        		
               duration += (System.currentTimeMillis() - t1);
            }
        	       	              
            TestLogger.logDuration(testSetting.logger, "Comparing " + item1 + " and " + item2 , duration / 1000);
           
        }catch(TimeoutException ex)
		{
        	logUnsuccessfulOperation("Could not finish comparing " + item1 + " and " + item2);
		}finally
		{
			closeBrowser(driver);
		}
		
		return null;
	}
		

}
