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

package com.raffaeleconforti.ilpsolverwrapper;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 4/4/17.
 */
public interface ILPSolver {

    enum VariableType {CONTINUOUS, BINARY, INTEGER}
    enum Operator {LESS_EQUAL, EQUAL, GREATER_EQUAL}
    enum Status {OPTIMAL, INFEASIBLE, UNBOUNDED, ERROR}

    double getInfinity();
    void setAlwaysFeasible(boolean isAlwaysFeasible);
    void createModel();
    ILPSolverVariable addVariable(double lowerBound, double upperBound, double objectiveCoefficient, VariableType variableType, String variableName);
    ILPSolverExpression createExpression();
    ILPSolverConstraint addConstraint(ILPSolverExpression expression, Operator operator, double coefficient, String constraintName);
    void setObjectiveFunction(ILPSolverExpression objectiveFunction);
    void setMaximize();
    void setMinimize();
    void integrateVariables();
    void solve();
    double[] getSolutionVariables(ILPSolverVariable[] variables);
    double getSolutionValue();
    Status getStatus();
    String printProblem();
    void dispose();
}
