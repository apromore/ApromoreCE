/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package org.apromore.plugin.portal.prodrift.model;

import org.zkoss.zul.ListModel;


/**
 * Created by n9348531 on 17/01/2017.
 */
public class Drift implements Comparable<Drift>{

    private long driftPoint;
    private String driftStatement;
    private ListModel<String> characterizationStatements;

    public Drift(){

    }

    public long getDriftPoint() {
        return driftPoint;
    }

    public void setDriftPoint(long driftPoint) {
        this.driftPoint = driftPoint;
    }

    public String getDriftStatement() {
        return driftStatement;
    }

    public void setDriftStatement(String driftStatement) {
        this.driftStatement = driftStatement;
    }

    public ListModel<String> getCharacterizationStatements() {
        return characterizationStatements;
    }

    public void setCharacterizationStatements(ListModel<String> characterizationStatements) {
        this.characterizationStatements = characterizationStatements;
    }

    @Override
    public int compareTo(Drift o) {
        return Long.compare(this.driftPoint, o.getDriftPoint());
    }
}
