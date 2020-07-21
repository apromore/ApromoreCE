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

package org.apromore.canoniser.pnml.internal.canonical2pnml;

import java.math.BigInteger;

import org.apromore.cpf.NodeType;
import org.apromore.pnml.NodeNameType;
import org.apromore.pnml.OperatorType;
import org.apromore.pnml.TransitionToolspecificType;
import org.apromore.pnml.TransitionType;

public class TranslateANDSplitJoin {
    DataHandler data;
    long ids;

    public void setValues(DataHandler data, long ids) {
        this.data = data;
        this.ids = ids;
    }

    public void translate(NodeType node) {
        TransitionType tran = new TransitionType();
        TransitionToolspecificType trantool = new TransitionToolspecificType();
        OperatorType op = new OperatorType();
        data.put_id_map(node.getId(), String.valueOf(ids));
        tran.setId(String.valueOf(ids++));
        if (node.getName() != null) {
            NodeNameType test = new NodeNameType();
            test.setText(node.getName());
            tran.setName(test);
        }
        trantool.setTool("WoPeD");
        trantool.setVersion("1.0");
        String splitid[] = node.getOriginalID().split("_");
        op.setId(splitid[0]);
        op.setType(107);
        trantool.setOrientation(3);
        trantool.setOperator(op);
        tran.getToolspecific().add(trantool);
        data.getNet().getTransition().add(tran);
        data.put_pnmlRefMap(tran.getId(), tran);
        data.getStartNodeMap().put(node, tran);
        data.getEndNodeMap().put(node, tran);
        data.put_originalid_map(BigInteger.valueOf(Long.valueOf(tran.getId())),
                node.getOriginalID());
    }

    public long getIds() {
        return ids;
    }
}
