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

package org.apromore.canoniser.yawl.yawl2cpf.patterns.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternUnitTest;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.TaskType;
import org.junit.Test;

public class DirectDistributionUnitTest extends BasePatternUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/Resource/WPR1DirectDistribution.yawl");
    }

    @Test
    public void testDirectDistribution() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);
        final NodeType nodeA = getNodeByName(rootNet, "A");
        checkNode(rootNet, nodeA, TaskType.class, 1, 1);

        // Check Resources available in Process
        final CanonicalProcessType process = yawl2Canonical.getCpf();

        // Should contain 2 Roles + 3 Participants
        assertEquals(5, process.getResourceType().size());

        final TaskType taskA = (TaskType) nodeA;
        // Only linked to distribution set
        assertEquals(1, taskA.getResourceTypeRef().size());

        final ResourceTypeRefType resourceRef = taskA.getResourceTypeRef().get(0);
        assertEquals("Primary", resourceRef.getQualifier());

        boolean foundResource = false;
        for (final ResourceTypeType resource : process.getResourceType()) {
            if (resource.getId().equals(resourceRef.getResourceTypeId())) {
                foundResource = true;
                assertEquals("TestX TestX", resource.getName());
            }
        }
        assertTrue(foundResource);
    }

}
