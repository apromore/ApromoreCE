/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Queensland University of Technology.
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
package org.apromore.portal.uitest;

import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;

public class APMComplianceUITest extends AbstractPortalUITest {

  final String SETUP_DIALOG_XPATH = "/html/body/div[div/text()='Input specification or XML file']";
  final String VERIFICATION_DIALOG_XPATH = "/html/body/div[div/text()='Verification']";

  @Test
  public void cancel() throws Exception {
    popup();
    driver.findElement(By.xpath(SETUP_DIALOG_XPATH + "//button[text()=' Cancel']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(SETUP_DIALOG_XPATH)));
  }

  @Test
  public void ok() throws Exception {
    final String TEST_PROCESS_NAME = "Test structured process";

    popup();
    WebElement directInput = driver.findElement(By.xpath(SETUP_DIALOG_XPATH + "//textarea"));
    directInput.clear();
    directInput.sendKeys("Foo");
    driver.findElement(By.xpath(SETUP_DIALOG_XPATH + "//button[text()=' OK']")).click();
    Thread.currentThread().sleep(1000);
    assertTrue(isElementPresent(By.xpath(VERIFICATION_DIALOG_XPATH)));
    driver.findElement(By.xpath(VERIFICATION_DIALOG_XPATH + "//button[text()='close']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(VERIFICATION_DIALOG_XPATH)));
  }

  private void popup() {
    clickFolder("Home");
    delay();
    clickFolder("repair");
    delay();

    clickProcessModel("repairExample");
    delay();
    clickMenuBar("Analyze");
    delay();
    clickMenuItem("Verify Compliance");
    delay();
    assertTrue(isElementPresent(By.xpath(SETUP_DIALOG_XPATH)));
  }
}

