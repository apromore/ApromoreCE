/*
 *  Copyright (C) 2018 Raffaele Conforti (www.raffaeleconforti.com)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.raffaeleconforti.ilpsolverwrapper.impl.lpsolve;

import com.raffaeleconforti.ilpsolverwrapper.ILPSolver.VariableType;
import com.raffaeleconforti.ilpsolverwrapper.ILPSolverVariable;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 4/4/17.
 */
public class LPSolve_Variable implements ILPSolverVariable {

    private double lowerBound;
    private double upperBound;
    private VariableType variableType;
    private String variableName;
    private int variablePosition;

    public LPSolve_Variable(int variablePosition,
                            double lowerBound,
                            double upperBound,
                            VariableType variableType,
                            String variableName) {
        this.variablePosition = variablePosition;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.variableType = variableType;
        this.variableName = variableName;
    }

    public int getVariablePosition() {
        return variablePosition;
    }

    @Override
    public double getLowerBound() {
        return lowerBound;
    }

    @Override
    public double getUpperBound() {
        return upperBound;
    }

    @Override
    public VariableType getVariableType() {
        return variableType;
    }

    @Override
    public String getVariableName() {
        return variableName;
    }

}
