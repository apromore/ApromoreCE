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

import com.raffaeleconforti.ilpsolverwrapper.ILPSolver;
import com.raffaeleconforti.ilpsolverwrapper.ILPSolverConstraint;
import com.raffaeleconforti.ilpsolverwrapper.ILPSolverExpression;
import com.raffaeleconforti.ilpsolverwrapper.ILPSolverVariable;
import gurobi.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 4/4/17.
 */
public class Gurobi_Solver implements ILPSolver {

    public static final double INFINITY = Math.pow(2, 31) - 1;//1.0E30;//1.0E100D;

    private GRBEnv env;
    private GRBModel model;
    private ILPSolverExpression objectiveFunction;
    private List<Gurobi_Variable> variables;
    private List<Gurobi_Constraint> constraints;
    private boolean minimize;

    @Override
    public double getInfinity() {
        return INFINITY;
    }

    @Override
    public void setAlwaysFeasible(boolean isAlwaysFeasible) {

    }

    @Override
    public void createModel() {
        try {
            objectiveFunction = null;
            variables = new ArrayList<>();
            constraints = new ArrayList<>();
            minimize = true;

            System.setOut(new PrintStream(new OutputStream() {
                @Override
                public void write(int b) {}
            }));

            env = new GRBEnv("qp.noisefiltering");
            model = new GRBModel(env);
            model.getEnv().set(GRB.IntParam.LogToConsole, 0);

            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ILPSolverVariable addVariable(double lowerBound, double upperBound, double objectiveCoefficient, VariableType variableType, String variableName) {
        try {
            char gurobiVariableType = GRB.CONTINUOUS;
            switch (variableType) {
                case BINARY     : gurobiVariableType = GRB.BINARY;
                                  break;
                case INTEGER    : gurobiVariableType = GRB.INTEGER;
                                  break;
                case CONTINUOUS : gurobiVariableType = GRB.CONTINUOUS;
            }

//            Gurobi_Variable var = new Gurobi_Variable(model.addVar(-getInfinity(), getInfinity(), 1, gurobiVariableType, variableName), lowerBound, upperBound, variableType, variableName);
//            variables.add(var);
//            return var;
            Gurobi_Variable var = new Gurobi_Variable(model.addVar(lowerBound, upperBound, objectiveCoefficient, gurobiVariableType, variableName), lowerBound, upperBound, variableType, variableName);
            variables.add(var);
            return var;
        } catch (GRBException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ILPSolverExpression createExpression() {
        return new Gurobi_Expression(new GRBLinExpr());
    }

    @Override
    public ILPSolverConstraint addConstraint(ILPSolverExpression expression, Operator operator, double coefficient, String constraintName) {
        try {
            char gurobiOperator = GRB.EQUAL;
            switch (operator) {
                case LESS_EQUAL     : gurobiOperator = GRB.LESS_EQUAL;
                    break;
                case EQUAL          : gurobiOperator = GRB.EQUAL;
                    break;
                case GREATER_EQUAL  : gurobiOperator = GRB.GREATER_EQUAL;
            }

            Gurobi_Constraint gurobi_constraint = new Gurobi_Constraint(model.addConstr(((Gurobi_Expression) expression).getLinearExpression(), gurobiOperator, coefficient, ""));
            constraints.add(gurobi_constraint);
            return gurobi_constraint;
        } catch (GRBException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setObjectiveFunction(ILPSolverExpression objectiveFunction) {
        try {
            this.objectiveFunction = objectiveFunction;
            model.setObjective(((Gurobi_Expression) objectiveFunction).getLinearExpression(), minimize ? GRB.MINIMIZE : GRB.MAXIMIZE);
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMaximize() {
        minimize = false;
        if(objectiveFunction != null) setObjectiveFunction(objectiveFunction);
    }

    @Override
    public void setMinimize() {
        minimize = true;
        if(objectiveFunction != null) setObjectiveFunction(objectiveFunction);
    }

    @Override
    public void integrateVariables() {
        try {
            model.update();

            for(Gurobi_Variable variable : variables) {
                double diff = (variable.getVariableType() == VariableType.CONTINUOUS) ? Double.MIN_VALUE : 1;
                GRBLinExpr expression = new GRBLinExpr();
                expression.addTerm(1, variable.getVariable());
                if(variable.getLowerBound() != -getInfinity()) {
//                    model.addConstr(expression, GRB.GREATER_EQUAL, variable.getLowerBound() + diff, "");
                    model.addConstr(expression, GRB.GREATER_EQUAL, variable.getLowerBound(), "");
                }
                if(variable.getUpperBound() != getInfinity()) {
//                    model.addConstr(expression, GRB.LESS_EQUAL, variable.getUpperBound() - diff, "");
                    model.addConstr(expression, GRB.LESS_EQUAL, variable.getUpperBound(), "");
                }
            }

        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void solve() {
        try {
            model.optimize();
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double[] getSolutionVariables(ILPSolverVariable[] variables) {
        try {
            if(getStatus() != Status.OPTIMAL) return null;
            double[] solutions = new double[variables.length];
            for(int i = 0; i < variables.length; i++) {
                solutions[i] = ((Gurobi_Variable) variables[i]).getVariable().get(GRB.DoubleAttr.X);
            }
            return solutions;
        } catch (GRBException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public double getSolutionValue() {
        try {
            if(getStatus() != Status.OPTIMAL) return 0;
            return model.get(GRB.DoubleAttr.ObjVal);
        } catch (GRBException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Status getStatus() {
        try {
            int status = model.get(GRB.IntAttr.Status);
            if(status == GRB.OPTIMAL) return Status.OPTIMAL;
            else if(status == GRB.INFEASIBLE) return Status.INFEASIBLE;
            else if(status == GRB.UNBOUNDED || status == GRB.INF_OR_UNBD) return Status.UNBOUNDED;
            else return Status.ERROR;
        } catch (GRBException e) {
            e.printStackTrace();
        }
        return Status.ERROR;
    }

    @Override
    public String printProblem() {
        try {
            String file = "problem.lp";
            model.write(file);
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            boolean skip = false;
            boolean subjectTo = true;
            while(line != null) {
                if(line.equalsIgnoreCase("Bounds")) {
                    skip = true;
                }else if(line.equalsIgnoreCase("Generals")) {
                    skip = false;
                    subjectTo = true;
                }else if(line.equalsIgnoreCase("Subject To")) {
                    subjectTo = false;
                }
                if(!skip) {
                    if(subjectTo || line.startsWith(" R") || line.equalsIgnoreCase("Subject To")) sb.append("\n");
                    else line = line.substring(2);
                    sb.append(line);
                }
                line = br.readLine();
            }
            return sb.toString();
        } catch (GRBException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void dispose() {
        try {
            model.dispose();
            env.dispose();
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

}
