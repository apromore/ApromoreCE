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

package org.apromore.canoniser.yawl.yawl2cpf.patterns.controlflow;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.apromore.canoniser.yawl.utils.TestUtils;
import org.apromore.canoniser.yawl.yawl2cpf.patterns.BasePatternUnitTest;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TaskType;
import org.junit.Test;

public class ParallelSplitUnitTest extends BasePatternUnitTest {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.patterns.controlflow.PatternTest#getYAWLFile()
     */
    @Override
    protected File getYAWLFile() {
        return new File(TestUtils.TEST_RESOURCES_DIRECTORY + "YAWL/Patterns/ControlFlow/WPC2ParallelSplit.yawl");
    }

    @Test
    public void testIsParallelSplit() {
        final NetType rootNet = yawl2Canonical.getCpf().getNet().get(0);
        assertEquals(10, rootNet.getEdge().size());
        assertEquals(9, rootNet.getNode().size());

        final NodeType nodeA = checkNode(rootNet, "A", TaskType.class, 1, 1);

        final List<EdgeType> edges = getOutgoingEdges(rootNet, nodeA.getId());
        assertEquals(1, edges.size());
        final NodeType routingNode = getNodeByID(rootNet, edges.get(0).getTargetId());
        checkNode(rootNet, routingNode, ANDSplitType.class, 1, 3);

        checkNode(rootNet, "B", TaskType.class, 1, 1);
        final NodeType nodeC = checkNode(rootNet, "C", TaskType.class, 1, 1);

        final List<EdgeType> cEdges = getOutgoingEdges(rootNet, nodeC.getId());
        assertEquals(1, cEdges.size());
        final NodeType joiningNode = getNodeByID(rootNet, cEdges.get(0).getTargetId());
        checkNode(rootNet, joiningNode, ANDJoinType.class, 3, 1);
    }

}
