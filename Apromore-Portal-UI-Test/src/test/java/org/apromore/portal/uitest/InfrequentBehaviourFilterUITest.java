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

import java.awt.Robot;
import java.awt.event.KeyEvent;
import org.junit.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;

public class InfrequentBehaviourFilterUITest extends AbstractPortalUITest {

  final String ACTIVITY_FILTER_DIALOG_XPATH = "/html/body/div[div[text()='Activity Filter']]";
  final String TEST_LOG_NAME = "repairExample_complete_lifecycle_only";

  @Test
  public void filterActivityCancel() throws Exception {
    popup("Filter Out Infrequent Activities", TEST_LOG_NAME);
    assertTrue(isElementPresent(By.xpath(ACTIVITY_FILTER_DIALOG_XPATH)));
    driver.findElement(By.xpath(ACTIVITY_FILTER_DIALOG_XPATH + "//button[text()=' Cancel']")).click();
    delay();
    assertFalse(isElementPresent(By.xpath(ACTIVITY_FILTER_DIALOG_XPATH)));
  }

  @Test
  public void filterActivityOK() throws Exception {
    final String TEST_FILTERED_LOG_NAME = TEST_LOG_NAME + "_activity_filtered";

    popup("Filter Out Infrequent Activities", TEST_LOG_NAME);
    assertTrue(isElementPresent(By.xpath(ACTIVITY_FILTER_DIALOG_XPATH)));
    driver.findElement(By.xpath(ACTIVITY_FILTER_DIALOG_XPATH + "//td/div[contains(text(),'complete')]/span")).click();
    driver.findElement(By.xpath(ACTIVITY_FILTER_DIALOG_XPATH + "//button[text()=' OK']")).click();
    Thread.currentThread().sleep(1000);
    assertTrue(isProcessModel(TEST_FILTERED_LOG_NAME));
    try {
      assertFalse(isElementPresent(By.xpath(ACTIVITY_FILTER_DIALOG_XPATH)));

    } finally {
      deleteProcessModel(TEST_FILTERED_LOG_NAME);
    }
  }

  @Test
  public void filterBehaviour() throws Exception {
    final String TEST_FILTERED_LOG_NAME = TEST_LOG_NAME + "_behavioural_filtered";

    assertFalse(isProcessModel(TEST_FILTERED_LOG_NAME));
    popup("Filter Out Infrequent Behavior", TEST_LOG_NAME);
    Thread.currentThread().sleep(1000);
    assertTrue(isProcessModel(TEST_FILTERED_LOG_NAME));
    deleteProcessModel(TEST_FILTERED_LOG_NAME);
  }

  private void popup(String menuItemName, String logName) {
    clickFolder("Home");
    delay();
    clickFolder("repair");
    delay();

    clickProcessModel(logName);
    delay();
    clickMenuBar("Discover");
    delay();
    clickMenuItem(menuItemName);
    delay();
  }
}

