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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;

/**
 * Class controlling all Macros that are used in the conversion.
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public class MacroRewriter {

    private final List<RewriteMacro> availableMacros;

    public MacroRewriter() {
        super();
        availableMacros = new ArrayList<RewriteMacro>();
    }

    /**
     * Add Macro to our list of Macros
     * 
     * @param m
     *            Macro
     */
    public void addMacro(final RewriteMacro m) {
        availableMacros.add(m);
    }

    /**
     * Executes all added Macros in the order they were added
     * 
     * @param cpf
     *            Canonical Process all Macros will be applied to
     * @return a Collection of all Macros that rewrote someting
     * @throws CanoniserException
     */
    public Collection<RewriteMacro> executeAllMacros(final CanonicalProcessType cpf) throws CanoniserException {
        final Collection<RewriteMacro> appliedMacros = new ArrayList<RewriteMacro>();
        for (final RewriteMacro m : availableMacros) {
            final boolean hasRewritten = m.rewrite(cpf);
            if (hasRewritten) {
                appliedMacros.add(m);
            }
        }
        return appliedMacros;
    }

}
