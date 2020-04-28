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

public class BIMPUITest extends AbstractPortalUITest {

  final String SETUP_DIALOG_XPATH = "/html/body/div[div/text()='Input specification or XML file']";
  final String VERIFICATION_DIALOG_XPATH = "/html/body/div[div/text()='Verification']";

  @Test
  public void test() throws Exception {
    clickFolder("Home");
    delay();
    clickFolder("repair");
    delay();
    clickProcessModel("repairExample");
    delay();
    clickMenuBar("Analyze");
    delay();
    //String portalWindowHandle = driver.getWindowHandle();
    clickMenuItem("Simulate with BIMP");
    Thread.currentThread().sleep(3000);
    
    // Find the BIMP window handle
    String bimpWindowHandle = findNewWindowHandle();

    // Close the BIMP tab and switch back to the portal
    driver.switchTo().window(bimpWindowHandle);
    driver.close();
    driver.switchTo().window(portalWindowHandle);
  }
}

