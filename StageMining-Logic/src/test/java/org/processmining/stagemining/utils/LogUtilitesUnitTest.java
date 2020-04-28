/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Bruce Nguyen, Queensland University of Technology.
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
package org.processmining.stagemining.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xeslite.external.XFactoryExternalStore;
import org.xeslite.parser.XesLiteXmlParser;

public class LogUtilitesUnitTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Ignore("Test fails because Guava's Ints.fromBytes method is missing.")
    @Test
    public void addStartEndEvents() throws Exception {

        XFactory factory = new XFactoryExternalStore.InMemoryStoreImpl();
        XesLiteXmlParser parser = new XesLiteXmlParser(factory, false);
        List<XLog> parsedLog = parser.parse(new GZIPInputStream(LogUtilitesUnitTest.class.getClassLoader().getResourceAsStream("BPI13.xes.gz")));

        XLog log = parsedLog.iterator().next();

        LogUtilites.addStartEndEvents(log);

    }
}
