/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
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

package org.apromore.canoniser.yawl.cpf2yawl.external;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WholeDirectoryUnitTest extends BaseCPF2YAWLUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(WholeDirectoryUnitTest.class);

    public WholeDirectoryUnitTest(final File testCPFFile, final File testANFFile) {
        super();
        this.testANFFile = testANFFile;
        this.testCPFFile = testCPFFile;
        LOGGER.debug("Testing file CPF: {] ", testCPFFile.getName());
    }

    private final File testANFFile;
    private final File testCPFFile;

    @Override
    protected File getCPFFile() {
        return testCPFFile;
    }

    @Override
    protected File getANFFile() {
        return testANFFile;
    }

    protected final boolean isName(final File file, final String name) {
        return file.getName().equals(name);
    }


}
