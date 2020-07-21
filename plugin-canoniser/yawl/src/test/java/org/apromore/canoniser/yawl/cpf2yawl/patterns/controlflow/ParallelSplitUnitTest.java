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

package org.apromore.canoniser.yawl.cpf2yawl.patterns.controlflow;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.junit.Test;
import org.yawlfoundation.yawlschema.ControlTypeCodeType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;

public class ParallelSplitUnitTest extends BaseCPF2YAWLUnitTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getCPFFile()
     */
    @Override
    protected File getCPFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPC2ParallelSplit.yawl.cpf");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.BaseCPF2YAWLUnitTest#getANFFile()
     */
    @Override
    protected File getANFFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "CPF/Internal/FromYAWL/WPC2ParallelSplit.yawl.anf");
    }

    @Test
    public void testStructure() {
        final NetFactsType rootNet = findRootNet();
        final ExternalTaskFactsType a = findTaskByName("A", rootNet);
        assertNotNull("Could not find Task with Name A", a);
        assertTrue(a.getSplit().getCode().equals(ControlTypeCodeType.AND));
        final ExternalTaskFactsType b = findTaskByName("B", rootNet);
        assertNotNull("Could not find Task with Name B", b);
        final ExternalTaskFactsType c = findTaskByName("C", rootNet);
        assertNotNull("Could not find Task with Name C", c);
        final ExternalTaskFactsType d = findTaskByName("D", rootNet);
        assertNotNull("Could not find Task with Name D", d);
        final ExternalTaskFactsType e = findTaskByName("E", rootNet);
        assertNotNull("Could not find Task with Name E", e);
        assertTrue(e.getJoin().getCode().equals(ControlTypeCodeType.AND));
    }

}
