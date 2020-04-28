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

package org.apromore.canoniser.pnml.internal.canonical2pnml;

import java.math.BigInteger;

import org.apromore.pnml.ArcType;
import org.apromore.pnml.PlaceType;
import org.apromore.pnml.TransitionType;

public class TranslateOriginalIDS {
    DataHandler data;
    long ids;
    String originalid;

    public void setValues(DataHandler data, long ids) {
        this.data = data;
        this.ids = ids;
    }

    public void mapIDS() {
        for (PlaceType place : data.getNet().getPlace()) {
            if (!place.getId().contains("CENTER_PLACE_")) {
                try {
                    originalid = data.get_originalid_map_value(BigInteger.valueOf(Long.valueOf(place.getId())));
                    if (originalid != null) {
                        place.setId(originalid);
                    }
                } catch (NumberFormatException e) { /* do nothing */ }
            }
        }
        for (TransitionType tran : data.getNet().getTransition()) {
            if (!tran.getId().contains("op")) {
                try {
                    originalid = data.get_originalid_map_value(BigInteger.valueOf(Long.valueOf(tran.getId())));
                    if (originalid != null) {
                        tran.setId(originalid);
                    }
                } catch (NumberFormatException e) { /* do nothing */ }
            }
        }
        for (ArcType arc : data.getNet().getArc()) {
            if (!arc.getId().contains("a")) {
                try {
                    originalid = data.get_originalid_map_value(BigInteger.valueOf(Long.valueOf(arc.getId())));
                    if (originalid != null) {
                        arc.setId(originalid);
                    }
                } catch (NumberFormatException e) { /* do nothing */ }
            }
        }
    }
}
