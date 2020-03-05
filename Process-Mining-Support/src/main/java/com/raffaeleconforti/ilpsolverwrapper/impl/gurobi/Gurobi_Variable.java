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

package com.raffaeleconforti.ilpsolverwrapper.impl.gurobi;

import com.raffaeleconforti.ilpsolverwrapper.ILPSolver.VariableType;
import com.raffaeleconforti.ilpsolverwrapper.ILPSolverVariable;
import gurobi.GRBVar;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 4/4/17.
 */
public class Gurobi_Variable implements ILPSolverVariable {

    private GRBVar variable;
    private double lowerBound;
    private double upperBound;
    private VariableType variableType;
    private String variableName;

    public Gurobi_Variable(GRBVar variable,
                           double lowerBound,
                           double upperBound,
                           VariableType variableType,
                           String variableName) {
        this.variable = variable;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.variableType = variableType;
        this.variableName = variableName;
    }

    public GRBVar getVariable() {
        return variable;
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
