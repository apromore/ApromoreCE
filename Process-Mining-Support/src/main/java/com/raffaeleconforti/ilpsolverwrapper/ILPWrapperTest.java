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

import com.raffaeleconforti.ilpsolverwrapper.impl.gurobi.Gurobi_Solver;
import com.raffaeleconforti.ilpsolverwrapper.impl.lpsolve.LPSolve_Solver;

import java.util.Arrays;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 4/4/17.
 */
public class ILPWrapperTest {

    public static void main(String[] args) {
        test(new Gurobi_Solver());
        test(new LPSolve_Solver());
    }

    private static void test(ILPSolver solver) {
        solver.createModel();
        ILPSolverVariable X_0_1 = solver.addVariable(0, 1.0, 1, ILPSolver.VariableType.BINARY, "X_0_1");
        ILPSolverVariable X_0_2 = solver.addVariable(0, 1.0, 1, ILPSolver.VariableType.BINARY, "X_0_2");
        ILPSolverVariable X_0_3 = solver.addVariable(0, 1.0, 1, ILPSolver.VariableType.BINARY, "X_0_3");
        ILPSolverVariable X_1_0 = solver.addVariable(0, 1.0, 1, ILPSolver.VariableType.BINARY, "X_1_0");
        ILPSolverVariable X_1_2 = solver.addVariable(0, 1.0, 1, ILPSolver.VariableType.BINARY, "X_1_2");
        ILPSolverVariable X_1_3 = solver.addVariable(0, 1.0, 1, ILPSolver.VariableType.BINARY, "X_1_3");
        ILPSolverVariable X_2_0 = solver.addVariable(0, 1.0, 1, ILPSolver.VariableType.BINARY, "X_2_0");
        ILPSolverVariable X_2_1 = solver.addVariable(0, 1.0, 1, ILPSolver.VariableType.BINARY, "X_2_1");
        ILPSolverVariable X_2_3 = solver.addVariable(0, 1.0, 1, ILPSolver.VariableType.BINARY, "X_2_3");
        ILPSolverVariable X_3_0 = solver.addVariable(0, 1.0, 1, ILPSolver.VariableType.BINARY, "X_3_0");
        ILPSolverVariable X_3_1 = solver.addVariable(0, 1.0, 1, ILPSolver.VariableType.BINARY, "X_3_1");
        ILPSolverVariable X_3_2 = solver.addVariable(0, 1.0, 1, ILPSolver.VariableType.BINARY, "X_3_2");

        ILPSolverVariable U_0 = solver.addVariable(0, 3.0, 1, ILPSolver.VariableType.INTEGER, "U_0");
        ILPSolverVariable U_1 = solver.addVariable(0, 3.0, 1, ILPSolver.VariableType.INTEGER, "U_1");
        ILPSolverVariable U_3 = solver.addVariable(0, 3.0, 1, ILPSolver.VariableType.INTEGER, "U_3");

        solver.integrateVariables();

        ILPSolverExpression objectiveFunction = solver.createExpression();
//        objectiveFunction.addTerm(X_0_1, 0.9929947676208064);
//        objectiveFunction.addTerm(X_0_2, -3.0);
//        objectiveFunction.addTerm(X_0_3, 1.2311480455524777E-5);
//        objectiveFunction.addTerm(X_1_0, 1.3290802764486976E-5);
//        objectiveFunction.addTerm(X_1_2, -3.0);
//        objectiveFunction.addTerm(X_1_3, 0.9929947676208064);
//        objectiveFunction.addTerm(X_2_0, -3.0);
//        objectiveFunction.addTerm(X_2_1, -3.0);
//        objectiveFunction.addTerm(X_2_3, -3.0);
//        objectiveFunction.addTerm(X_3_0, -3.0);
//        objectiveFunction.addTerm(X_3_1, -3.0);
//        objectiveFunction.addTerm(X_3_2, 1.0);

//        objectiveFunction.addTerm(X_0_1, 0.993);
//        objectiveFunction.addTerm(X_0_2, -3.0);
//        objectiveFunction.addTerm(X_0_3, 1.231E-5);
//        objectiveFunction.addTerm(X_1_0, 1.329E-5);
//        objectiveFunction.addTerm(X_1_2, -3.0);
//        objectiveFunction.addTerm(X_1_3, 0.993);
//        objectiveFunction.addTerm(X_2_0, -3.0);
//        objectiveFunction.addTerm(X_2_1, -3.0);
//        objectiveFunction.addTerm(X_2_3, -3.0);
//        objectiveFunction.addTerm(X_3_0, -3.0);
//        objectiveFunction.addTerm(X_3_1, -3.0);
//        objectiveFunction.addTerm(X_3_2, 1.0);

        objectiveFunction.addTerm(X_0_1, 1);
        objectiveFunction.addTerm(X_0_2, -3.0);
        objectiveFunction.addTerm(X_0_3, 0);
        objectiveFunction.addTerm(X_1_0, 0);
        objectiveFunction.addTerm(X_1_2, -3.0);
        objectiveFunction.addTerm(X_1_3, 1);
        objectiveFunction.addTerm(X_2_0, -3.0);
        objectiveFunction.addTerm(X_2_1, -3.0);
        objectiveFunction.addTerm(X_2_3, -3.0);
        objectiveFunction.addTerm(X_3_0, -3.0);
        objectiveFunction.addTerm(X_3_1, -3.0);
        objectiveFunction.addTerm(X_3_2, 1.0);

        solver.setMaximize();
        solver.setObjectiveFunction(objectiveFunction);

        ILPSolverExpression expression1 = solver.createExpression();
        expression1.addTerm(X_0_1, 1);
        expression1.addTerm(X_0_2, 1);
        expression1.addTerm(X_0_3, 1);
        ILPSolverConstraint constraint1 = solver.addConstraint(expression1, ILPSolver.Operator.EQUAL, 1, "");

        ILPSolverExpression expression2 = solver.createExpression();
        expression2.addTerm(X_1_0, 1);
        expression2.addTerm(X_1_2, 1);
        expression2.addTerm(X_1_3, 1);
        ILPSolverConstraint constraint2 = solver.addConstraint(expression2, ILPSolver.Operator.EQUAL, 1, "");

        ILPSolverExpression expression3 = solver.createExpression();
        expression3.addTerm(X_2_0, 1);
        expression3.addTerm(X_2_1, 1);
        expression3.addTerm(X_2_3, 1);
        ILPSolverConstraint constraint3 = solver.addConstraint(expression3, ILPSolver.Operator.EQUAL, 1, "");

        ILPSolverExpression expression4 = solver.createExpression();
        expression4.addTerm(X_3_0, 1);
        expression4.addTerm(X_3_1, 1);
        expression4.addTerm(X_3_2, 1);
        ILPSolverConstraint constraint4 = solver.addConstraint(expression4, ILPSolver.Operator.EQUAL, 1, "");



        ILPSolverExpression expression5 = solver.createExpression();
        expression5.addTerm(X_1_0, 1);
        expression5.addTerm(X_2_0, 1);
        expression5.addTerm(X_3_0, 1);
        ILPSolverConstraint constraint5 = solver.addConstraint(expression5, ILPSolver.Operator.EQUAL, 1, "");

        ILPSolverExpression expression6 = solver.createExpression();
        expression6.addTerm(X_0_1, 1);
        expression6.addTerm(X_2_1, 1);
        expression6.addTerm(X_3_1, 1);
        ILPSolverConstraint constraint6 = solver.addConstraint(expression6, ILPSolver.Operator.EQUAL, 1, "");

        ILPSolverExpression expression7 = solver.createExpression();
        expression7.addTerm(X_0_2, 1);
        expression7.addTerm(X_1_2, 1);
        expression7.addTerm(X_3_2, 1);
        ILPSolverConstraint constraint7 = solver.addConstraint(expression7, ILPSolver.Operator.EQUAL, 1, "");

        ILPSolverExpression expression8 = solver.createExpression();
        expression8.addTerm(X_0_3, 1);
        expression8.addTerm(X_1_3, 1);
        expression8.addTerm(X_2_3, 1);
        ILPSolverConstraint constraint8 = solver.addConstraint(expression8, ILPSolver.Operator.EQUAL, 1, "");



        ILPSolverExpression expression9 = solver.createExpression();
        expression9.addTerm(U_0, 1);
        expression9.addTerm(U_1, -1.0);
        expression9.addTerm(X_0_1, 3);
        ILPSolverConstraint constraint9 = solver.addConstraint(expression9, ILPSolver.Operator.LESS_EQUAL, 2, "");

        ILPSolverExpression expression10 = solver.createExpression();
        expression10.addTerm(U_0, 1);
        expression10.addTerm(U_3, -1.0);
        expression10.addTerm(X_0_3, 3);
        ILPSolverConstraint constraint10 = solver.addConstraint(expression10, ILPSolver.Operator.LESS_EQUAL, 2, "");

        ILPSolverExpression expression11 = solver.createExpression();
        expression11.addTerm(U_1, 1);
        expression11.addTerm(U_0, -1.0);
        expression11.addTerm(X_1_0, 3);
        ILPSolverConstraint constraint11 = solver.addConstraint(expression11, ILPSolver.Operator.LESS_EQUAL, 2, "");

        ILPSolverExpression expression12 = solver.createExpression();
        expression12.addTerm(U_1, 1);
        expression12.addTerm(U_3, -1.0);
        expression12.addTerm(X_1_3, 3);
        ILPSolverConstraint constraint12 = solver.addConstraint(expression12, ILPSolver.Operator.LESS_EQUAL, 2, "");

        ILPSolverExpression expression13 = solver.createExpression();
        expression13.addTerm(U_3, 1);
        expression13.addTerm(U_0, -1.0);
        expression13.addTerm(X_3_0, 3);
        ILPSolverConstraint constraint13 = solver.addConstraint(expression13, ILPSolver.Operator.LESS_EQUAL, 2, "");

        ILPSolverExpression expression14 = solver.createExpression();
        expression14.addTerm(U_3, 1);
        expression14.addTerm(U_1, -1.0);
        expression14.addTerm(X_3_1, 3);
        ILPSolverConstraint constraint14 = solver.addConstraint(expression14, ILPSolver.Operator.LESS_EQUAL, 2, "");



        ILPSolverExpression expression15 = solver.createExpression();
        expression15.addTerm(X_3_2, 1);
        ILPSolverConstraint constraint15 = solver.addConstraint(expression15, ILPSolver.Operator.EQUAL, 1, "");

        solver.solve();
        System.out.println(solver.printProblem());
        System.out.println(solver.getStatus());
        System.out.println(solver.getSolutionValue());
        System.out.println(Arrays.toString(solver.getSolutionVariables(
                new ILPSolverVariable[] {X_0_1, X_0_2, X_0_3, X_1_0, X_1_2, X_1_3, X_2_0, X_2_1, X_2_3, X_3_0, X_3_1, X_3_2, U_0, U_1, U_3})));
    }

}
