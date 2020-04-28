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

public class BEBoPUITest extends AbstractPortalUITest {

  @Test
  public void checkModelGuidelinesRepairExample() throws Exception {
    final String TAB_NAME = "Check model guidelines with BEBoP";
    final String RESULT_XPATH = "//div[@class='z-listbox' and div//th/div/text()='Understandability Guidelines Check Result']/div[3]/table/tbody";

    assertFalse(isTab(TAB_NAME));
    clickFolder("Home");
    delay();
    clickFolder("repair");
    delay();
    clickProcessModel("repairExample");
    delay();
    clickMenuBar("Analyze");
    delay();
    clickMenuItem("Check model guidelines with BEBoP");
    Thread.currentThread().sleep(3000);

    assertTrue(isTab(TAB_NAME));
    try {
      assertEquals(
        "  Converging gateways do not required to be labeled. When the convergence logic is not obvious, a text annotation should be associated to the gateway.",
        driver.findElement(By.xpath(RESULT_XPATH + "/tr[14]")).getText()
      );

    } finally {
      closeTab(TAB_NAME);
      delay();
      assertFalse(isTab(TAB_NAME));
    }
  }
}
