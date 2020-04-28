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

package org.apromore.canoniser.yawl.yawl2cpf.reallife;

import java.io.File;
import java.util.List;

import org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest;
import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.XORSplitType;
import org.junit.Test;

public class CreditApplicationProcessUnitTest extends BaseYAWL2CPFUnitTest {

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.BaseYAWL2CPFUnitTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/CreditApplicationProcess.yawl");
    }


    @Test
    public void testCreditApplication() {
        NetType net = yawl2Canonical.getCpf().getNet().get(0);
        checkNode(net, "check loan amount", TaskType.class, 1, 1);
        NodeType makeDecision = checkNode(net, "make decision", TaskType.class, 1, 1);
        NodeType makeDecisionSplit = getFirstSuccessor(net, makeDecision);
        checkNode(net, makeDecisionSplit, XORSplitType.class, 1, 2);
        List<EdgeType> edges = getOutgoingEdges(net, makeDecisionSplit.getId());
        checkOnlyOneDefaultEdge(edges);
        TaskType checkLoanAmount = checkNode(net, "check loan amount", TaskType.class, 1, 1);
        NodeType checkLoanAmountSplit = getFirstSuccessor(net, checkLoanAmount);
        checkNode(net, checkLoanAmountSplit, XORSplitType.class, 1, 2);
        checkOnlyOneDefaultEdge(getOutgoingEdges(net, checkLoanAmountSplit.getId()));
    }


}
