/*-
 * #%L
 * This file is part of "Apromore Community".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.canoniser.yawl.internal.utils;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.HashSet;

import org.junit.Test;

public class ConversionUtilsUnitTest {

    @Test
    public void testConvertColorToString() {
        assertEquals("R:204G:204B:204", ConversionUtils.convertColorToString(-3355444));
    }

    @Test
    public void testConvertColorToBigInteger() {
        // Different result as we are missing the Hue value in our String
        assertEquals(new BigInteger("13421772"), ConversionUtils.convertColorToBigInteger("R:204G:204B:204"));
    }

    @Test
    public void testGenerateUniqueName() {
        HashSet<String> nameSet = new HashSet<String>();
        nameSet.add("test");
        nameSet.add("test1");
        nameSet.add("test2");
        assertEquals("test3", ConversionUtils.generateUniqueName("test", nameSet));
    }

}
