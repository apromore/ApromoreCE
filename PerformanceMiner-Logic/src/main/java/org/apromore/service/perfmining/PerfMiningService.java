/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Bruce Nguyen.
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

package org.apromore.service.perfmining;

import org.deckfour.xes.model.XLog;
import org.apromore.service.perfmining.filter.TraceAttributeFilterParameters;
import org.apromore.service.perfmining.models.SPF;
import org.apromore.service.perfmining.parameters.SPFConfig;

/**
 * Interface for the Process Performance Mining Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author barca
 */
public interface PerfMiningService {

    public SPF mine(XLog log, SPFConfig config, TraceAttributeFilterParameters filter) throws Exception;

}
