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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.annotations;

import org.apromore.anf.GraphicsType;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.CanonicalElementHandler;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.yawlfoundation.yawlschema.LayoutFactsType.Specification;
import org.yawlfoundation.yawlschema.LayoutNetFactsType;

public abstract class ElementGraphicsTypeHandler extends CanonicalElementHandler<GraphicsType, Specification> {

    protected LayoutNetFactsType findNetLayout(final String cpfId, final Specification specLayout) {
        // First find Net that contains this element
        final NetType net = findNetContainsElement(cpfId);
        // Then search for NetLlayout
        return getContext().getConvertedNetLayout(generateUUID(net.getId()));
    }

    // TODO may be optimised!
    private NetType findNetContainsElement(final String cpfId) {
        for (final NetType net : getContext().getCanonicalProcess().getNet()) {
            for (final NodeType node : net.getNode()) {
                if (node.getId().equals(cpfId)) {
                    return net;
                }
            }
            for (final EdgeType edge : net.getEdge()) {
                if (edge.getId().equals(cpfId)) {
                    return net;
                }
            }
        }
        return null;
    }
}
