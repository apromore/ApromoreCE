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

import org.apromore.anf.AnnotationType;
import org.apromore.anf.SimulationType;
import org.apromore.pnml.TransitionToolspecificType;
import org.apromore.pnml.TransitionType;

public class TranslateTransitionToolspecific {
    DataHandler data;

    public void setValue(DataHandler data) {
        this.data = data;
    }

    public void translate(AnnotationType annotation, String cid) {
        Object obj = data.get_pnmlRefMap_value(data.get_id_map_value(cid));
        if (obj instanceof TransitionType) {
            SimulationType simu = (SimulationType) annotation;
            TransitionToolspecificType ttt = new TransitionToolspecificType();
            if (simu.getTime() != null) {
                ttt.setTime(Integer.valueOf(String.valueOf(simu.getTime())));
            }
            if (simu.getTimeUnit() != null) {
                ttt.setTimeUnit(Integer.valueOf(String.valueOf(simu
                        .getTimeUnit())));
            }

            ttt.setOrientation(1);
            if (((TransitionType) obj).getToolspecific().size() > 0) {
                TransitionToolspecificType alreadythere = ((TransitionType) obj)
                        .getToolspecific().get(0);
                alreadythere.setTime(ttt.getTime());
                alreadythere.setTimeUnit(ttt.getTimeUnit());
                if (alreadythere.getOrientation() == null) {
                    alreadythere.setOrientation(1);
                }

            } else {
                ttt.setTool("WoPeD");
                ttt.setVersion("1.0");
                ((TransitionType) obj).getToolspecific().add(ttt);
            }
        }

    }

}
