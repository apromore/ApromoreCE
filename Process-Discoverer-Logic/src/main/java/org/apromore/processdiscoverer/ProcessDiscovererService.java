/*-
 * #%L
 * This file is part of "Apromore Community".
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

package org.apromore.processdiscoverer;

import org.apromore.processdiscoverer.dfg.abstraction.BPMNAbstraction;
import org.apromore.processdiscoverer.dfg.abstraction.DFGAbstraction;
import org.apromore.processdiscoverer.dfg.abstraction.TraceAbstraction;
import org.deckfour.xes.model.XLog;
import org.json.JSONArray;

public interface ProcessDiscovererService {
    public DFGAbstraction generateDFGAbstraction(XLog log, AbstractionParams params) throws Exception;
//    public Object[] generateDFGJSON(XLog log, AbstractionParams params) throws Exception;
    public BPMNAbstraction generateBPMNAbstraction(XLog log, AbstractionParams params, DFGAbstraction dfgAbstraction) throws Exception;
//    public Object[] generateBPMNJSON(XLog log, AbstractionParams params, DFGAbstraction dfgAbstraction) throws Exception;
    public TraceAbstraction generateTraceAbstraction(String traceID, AbstractionParams params) throws Exception;
//    public JSONArray generateTraceDFGJSON(String traceID, AbstractionParams params) throws Exception;
}
