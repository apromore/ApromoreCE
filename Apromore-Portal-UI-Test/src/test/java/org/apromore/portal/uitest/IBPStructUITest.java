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

public class IBPStructUITest extends AbstractPortalUITest {

  final String IBPSTRUCT_SETUP_DIALOG_XPATH = "/html/body/div[div[text()='iBPStruct setup']]";

  @Test
  public void cancel() throws Exception {
    final String TEST_PROCESS_NAME = "Test structured process";

    popup();
    driver.findElement(By.xpath(IBPSTRUCT_SETUP_DIALOG_XPATH + "//button[text()=' Cancel']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(IBPSTRUCT_SETUP_DIALOG_XPATH)));
  }

  @Test
  public void structure() throws Exception {
    final String TEST_PROCESS_NAME = "Test structured process";

    popup();
    assertFalse(isProcessModel(TEST_PROCESS_NAME));
    WebElement structuredProcessName = driver.findElement(By.name("Structured Process Name"));
    //assertEquals("structured_repairExample", structuredProcessName.getText());
    structuredProcessName.clear();
    structuredProcessName.sendKeys(TEST_PROCESS_NAME);
    driver.findElement(By.xpath(IBPSTRUCT_SETUP_DIALOG_XPATH + "//button[text()=' Structure']")).click();
    Thread.currentThread().sleep(2000);
    assertTrue(isProcessModel(TEST_PROCESS_NAME));

    deleteProcessModel(TEST_PROCESS_NAME);
  }

  private void popup() {
    clickFolder("Home");
    delay();
    clickFolder("repair");
    delay();

    clickProcessModel("repairExample");
    delay();
    clickMenuBar("Discover");
    delay();
    clickMenuItem("Structure Process Model");
    delay();
    assertTrue(isElementPresent(By.xpath(IBPSTRUCT_SETUP_DIALOG_XPATH)));
  }
}

