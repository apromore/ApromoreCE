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

public class LogVisualizerUITest extends AbstractPortalUITest {

  @Test
  public void visualizeRepairExample() throws Exception {
    clickFolder("Home");
    delay();
    clickFolder("repair");
    delay();
    clickProcessModel("repairExample_complete_lifecycle_only");
    delay();
    clickMenuBar("Discover");
    delay();
    clickMenuItem("Visualize Log");
    Thread.currentThread().sleep(2000);

    WebElement activities = driver.findElement(By.xpath("//tr[td/div/span/text()='Activities']/td/div/input"));
    activities.clear();
    activities.sendKeys("60");
    WebElement arcs = driver.findElement(By.xpath("//tr[td/div/span/text()='Arcs']/td/div/input"));
    arcs.clear();
    arcs.sendKeys("60");
    activities.click();  // Weirdly, this is required to get the arcs to update
    delay();

    driver.findElement(By.xpath("//div[@title='Close']")).click();
    delay();
  }
}
