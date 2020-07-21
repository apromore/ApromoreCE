/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package com.apql.Apql;

// Java 2 Standard Edition
import java.net.URI;
import java.util.Arrays;
import java.util.List;

// Third party packages
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test suite for {@link PQLServiceClient}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class PQLServiceClientUnitTest {

    protected PQLServiceClient pqlServiceClient;

    @Before public void setup() throws Exception {
        pqlServiceClient = new PQLServiceClient(new URI("http://localhost:9000/pql/services"));
    }

    @Ignore
    @Test public void testRunAPQLQuery() {
        List<String> expected = Arrays.asList("37/MAIN/1.0");
        List<String> actual = pqlServiceClient.runAPQLQuery("SELECT * FROM *;", Arrays.asList("37/MAIN/1.0"), "ad1f7b60-1143-4399-b331-b887585a0f30");
        assertEquals(expected, actual);
    }
}
