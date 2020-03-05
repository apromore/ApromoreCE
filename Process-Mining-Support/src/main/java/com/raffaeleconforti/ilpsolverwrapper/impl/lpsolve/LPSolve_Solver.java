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

import com.raffaeleconforti.ilpsolverwrapper.ILPSolver;
import com.raffaeleconforti.ilpsolverwrapper.ILPSolverConstraint;
import com.raffaeleconforti.ilpsolverwrapper.ILPSolverExpression;
import com.raffaeleconforti.ilpsolverwrapper.ILPSolverVariable;
import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 4/4/17.
 */
public class LPSolve_Solver implements ILPSolver {

    public static final double INFINITY = Math.pow(2, 31) - 1;//1.0E30;//1.0E100D;

    private boolean debug_mode = false;

    private final int MAX_EXP = 20;
    private int max_exp;
    private boolean alwaysFeasible;

    private LpSolve lp;
    private List<LPSolve_Variable> variables;
    private List<LPSolve_Constraint> constraints;
    private LPSolve_Constraint objectiveFunction;
    private boolean minimize;
    private int status;
    private String problem;

    @Override
    public double getInfinity() {
        return INFINITY;
    }

    @Override
    public void setAlwaysFeasible(boolean isAlwaysFeasible) {
        alwaysFeasible = isAlwaysFeasible;
    }

    private double infinity() {
        //return Double.parseDouble("1.0E" + max_exp);
        return Math.pow(2, max_exp) - 1;
    }

    @Override
    public void createModel() {
        variables = new ArrayList<>();
        constraints = new ArrayList<>();
        minimize = true;
        alwaysFeasible = false;
        max_exp = MAX_EXP;
    }

    @Override
    public ILPSolverVariable addVariable(double lowerBound, double upperBound, double objectiveCoefficient, VariableType variableType, String variableName) {
        LPSolve_Variable variable = new LPSolve_Variable(variables.size(), lowerBound, upperBound, variableType, variableName);
        variables.add(variable);
        return variable;
    }

    @Override
    public ILPSolverExpression createExpression() {
        return new LPSolve_Expression();
    }

    @Override
    public ILPSolverConstraint addConstraint(ILPSolverExpression expression, Operator operator, double coefficient, String constraintName) {
        int lpSolveOperator = LpSolve.EQ;
        switch (operator) {
            case LESS_EQUAL     : lpSolveOperator = LpSolve.LE;
                break;
            case EQUAL          : lpSolveOperator = LpSolve.EQ;
                break;
            case GREATER_EQUAL  : lpSolveOperator = LpSolve.GE;
        }

        int[] colno = new int[variables.size()];
        double[] row = new double[variables.size()];

        List<ILPSolverVariable> expression_variables = ((LPSolve_Expression) expression).getVariables();
        List<Double> expression_coefficients = ((LPSolve_Expression) expression).getCoefficients();

        for(int i = 0; i < expression_variables.size(); i++) {
            LPSolve_Variable variable = (LPSolve_Variable) expression_variables.get(i);
            double variable_coefficient = expression_coefficients.get(i);

            colno[variable.getVariablePosition()] = variable.getVariablePosition() + 1;
            row[variable.getVariablePosition()] = variable_coefficient;
        }

        LPSolve_Constraint lpSolveConstraint = new LPSolve_Constraint(variables.size(), row, colno, lpSolveOperator, coefficient);
        constraints.add(lpSolveConstraint);
        return lpSolveConstraint;
    }

    @Override
    public void setObjectiveFunction(ILPSolverExpression objectiveFunction) {
        int[] colno = new int[variables.size()];
        double[] row = new double[variables.size()];

        List<ILPSolverVariable> function_variables = ((LPSolve_Expression) objectiveFunction).getVariables();
        List<Double> function_coefficients = ((LPSolve_Expression) objectiveFunction).getCoefficients();

        for(int i = 0; i < function_variables.size(); i++) {
            LPSolve_Variable variable = (LPSolve_Variable) function_variables.get(i);
            double variable_coefficient = function_coefficients.get(i);

            colno[variable.getVariablePosition()] = variable.getVariablePosition() + 1;
            row[variable.getVariablePosition()] = variable_coefficient;
        }

        this.objectiveFunction = new LPSolve_Constraint(variables.size(), row, colno, LpSolve.EQ, 0.0);
    }

    @Override
    public void setMaximize() {
        minimize = false;
    }

    @Override
    public void setMinimize() {
        minimize = true;
    }

    @Override
    public void integrateVariables() {
        try {
            lp = LpSolve.makeLp(0, variables.size());

            for(LPSolve_Variable variable : variables) {
                lp.setColName(variable.getVariablePosition() + 1, variable.getVariableName());
                if(variable.getVariableType() == VariableType.INTEGER || variable.getVariableType() == VariableType.BINARY) {
                    lp.setInt(variable.getVariablePosition() + 1, true);
                }else if(variable.getVariableType() == VariableType.BINARY) {
                    lp.setBinary(variable.getVariablePosition() + 1, true);
                }
                lp.setBounds(variable.getVariablePosition() + 1, fixInfinity(variable.getLowerBound()), fixInfinity(variable.getUpperBound()));
            }

        } catch (LpSolveException e) {
            e.printStackTrace();
        }
    }

    private double[] reduceInfinity(double[] row) {
        double[] new_row = new double[row.length];
        for(int i = 0; i < row.length; i++) {
            new_row[i] = fixInfinity(row[i]);
        }
        return new_row;
    }

    private double fixInfinity(double value) {
        if(value == getInfinity()) {
            return infinity();
        }else if(-value == getInfinity()) {
            return -infinity();
        }
        return value;
    }

    @Override
    public void solve() {
        try {
            double[] row;
            lp.setAddRowmode(true);

            for(LPSolve_Constraint constraint : constraints) {
                double coefficient = constraint.getCoefficient();
                coefficient = fixInfinity(coefficient);
                row = reduceInfinity(constraint.getRow());
                lp.addConstraintex(constraint.getSize(), row, constraint.getColno(), constraint.getLpSolveOperator(), coefficient);
            }

            lp.setAddRowmode(false);

            row = reduceInfinity(objectiveFunction.getRow());
            lp.setObjFnex(objectiveFunction.getSize(), row, objectiveFunction.getColno());
            if(minimize) {
                lp.setMinim();
            }else {
                lp.setMaxim();
            }

            if(debug_mode) problem = saveProblem();

            lp.setVerbose(LpSolve.IMPORTANT);
            status = lp.solve();
            if(status == LpSolve.NUMFAILURE || (status == LpSolve.INFEASIBLE && (max_exp != MAX_EXP || alwaysFeasible))) {
                System.out.print("NUM FAILURE with infinity = " + infinity());
                if(max_exp > 0) max_exp--;
                System.out.println(" using " + infinity());
                dispose();
                integrateVariables();
                solve();
            }
        } catch (LpSolveException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double[] getSolutionVariables(ILPSolverVariable[] variables) {
        try {
            double[] row = new double[this.variables.size()];
            lp.getVariables(row);

            double[] res = new double[variables.length];
            for(int i = 0; i < variables.length; i++) {
                LPSolve_Variable variable = (LPSolve_Variable) variables[i];
                res[i] = row[variable.getVariablePosition()];
            }
            return res;
        } catch (LpSolveException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public double getSolutionValue() {
        try {
            return lp.getObjective();
        } catch (LpSolveException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Status getStatus() {
        if(status == LpSolve.OPTIMAL) return Status.OPTIMAL;
        else if(status == LpSolve.INFEASIBLE) return Status.INFEASIBLE;
        else if(status == LpSolve.UNBOUNDED) return Status.UNBOUNDED;
        else if(status == LpSolve.NUMFAILURE) {
            System.out.println("NUM FAILURE");
            return Status.OPTIMAL;
        }
        else return Status.ERROR;
    }

    @Override
    public String printProblem() {
        return problem;
    }

    private String saveProblem() {
        try {
            String file = "problem.lp";
            lp.writeLp(file);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            int count = 0;
            boolean constrain = false;
            boolean print = true;
            while(line != null) {
                if(line.equalsIgnoreCase("/* Objective function */")) {
                    line = br.readLine();
                    if (line.startsWith("min:")) {
                        sb.append("Minimize\n").append(standardize(line.replaceAll("min:", " "), count, constrain)).append("\n");
                    } else {
                        sb.append("Maximize\n").append(standardize(line.replaceAll("max:", " "), count, constrain)).append("\n");
                    }
                }else if(line.equalsIgnoreCase("/* Constraints */")) {
                    sb.append("\nSubject To");
                    constrain = true;
                    print = true;
                }else if(line.equalsIgnoreCase("/* Variable bounds */")) {
                    print = false;
                }else if(line.equalsIgnoreCase("/* Integer definitions */")) {
                    sb.append("\nGenerals\n");
                    constrain = false;
                    line = br.readLine();
                    line = line.substring(3);
                    line = standardize(line, count, constrain);
                    sb.append(line.replaceAll(",", " ")).append("\n");
                }else if(line.isEmpty()) {

                }else {
                    if(print) sb.append(standardize(line, count, constrain));
                    if(constrain && !line.startsWith(" ")) count++;
                }
                line = br.readLine();
            }
            sb.append("End\n");
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LpSolveException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String standardize(String line, int count, boolean constrain) {
        line = line.replaceAll("-", "- ");
        line = line.replaceAll("[+]", "+ ");
        line = line.replaceAll("1e[+] ", "1e+");
        line = line.replaceAll(";", "");
        if(line.startsWith("R")) line = line.substring(line.indexOf(":") + 2);
        if(constrain) {
//            System.out.println(line);
            if(line.startsWith("+")) line = line.substring(2);
            if(!line.startsWith(" ")) line = "\n R" + count + ": " + line;
        }
        return line;
    }

    @Override
    public void dispose() {
        lp.deleteLp();
    }

}
