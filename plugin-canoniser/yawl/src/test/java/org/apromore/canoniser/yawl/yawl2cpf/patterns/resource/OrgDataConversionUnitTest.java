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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NonhumanType;
import org.apromore.cpf.NonhumanTypeEnum;
import org.apromore.cpf.ResourceTypeType;
import org.junit.Test;

public class OrgDataConversionUnitTest extends BaseYAWL2CPFUnitTest {

    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Others/RoleWithFilter.yawl");
    }

    @Test
    public void testBasicResourceConversion() {
        CanonicalProcessType cpf = yawl2Canonical.getCpf();

        ResourceTypeType roleX = getResourceByName(cpf, "RoleX");
        assertNotNull(roleX);

        ResourceTypeType roleY = getResourceByName(cpf, "RoleY");
        assertNotNull(roleY);

        ResourceTypeType roleZ = getResourceByName(cpf, "RoleZ");
        assertNotNull(roleZ);

        ResourceTypeType participantY = getResourceByName(cpf, "TestY TestY");
        assertNotNull(participantY);
        hasAttribute(participantY, "Capability", "CapabilityX");
        hasAttribute(participantY, "Capability", "CapabilityY");

        ResourceTypeType participantX = getResourceByName(cpf, "TestX TestX");
        assertNotNull(participantX);
        hasAttribute(participantX, "Capability", "CapabilityX");
        hasAttribute(participantX, "Position", "PositionX");

        ResourceTypeType resourceA = getResourceByName(cpf, "ResourceA");
        assertNotNull(resourceA);
        assertTrue(resourceA instanceof NonhumanType);
        assertTrue(NonhumanTypeEnum.EQUIPMENT.equals(((NonhumanType)resourceA).getType()));
    }


}
