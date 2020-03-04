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

package com.raffaeleconforti.noisefiltering.event.optimization.wrapper;

import com.raffaeleconforti.automaton.Automaton;
import com.raffaeleconforti.automaton.Edge;
import com.raffaeleconforti.automaton.Node;
import com.raffaeleconforti.ilpsolverwrapper.ILPSolver;
import com.raffaeleconforti.ilpsolverwrapper.ILPSolverExpression;
import com.raffaeleconforti.ilpsolverwrapper.ILPSolverVariable;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by conforti on 2/04/15.
 */
public class WrapperInfrequentBehaviourSolver<T> {

    private boolean debug_mode = false;

    private final Automaton<T> automaton;
    private final Set<Edge<T>> infrequentEdges;
    private final Set<Node<T>> requiredStatus;
    private final boolean useArcsFrequency;

    public WrapperInfrequentBehaviourSolver(Automaton<T> automaton, Set<Edge<T>> infrequentEdges, Set<Node<T>> requiredStates, boolean useArcsFrequency) {
        this.automaton = automaton;
        this.infrequentEdges = infrequentEdges;
        this.requiredStatus = requiredStates;
        this.useArcsFrequency = useArcsFrequency;
    }

    public Set<Edge<T>> identifyRemovableEdges(ILPSolver solver) {
        Set<Edge<T>> removable = new UnifiedSet<Edge<T>>();
        List<Edge<T>> edgeList = new ArrayList<Edge<T>>(automaton.getEdges());
        List<Node<T>> nodeList = new ArrayList<Node<T>>(automaton.getNodes());

        solver.createModel();
        solver.setAlwaysFeasible(true);

        // Create variables
        ILPSolverVariable[] edges = new ILPSolverVariable[edgeList.size()];
        for(int i = 0; i < edges.length; i++) {
            edges[i] = solver.addVariable(0.0, 1.0, 1.0, ILPSolver.VariableType.BINARY, edgeList.get(i).toString().replaceAll("-","_").replaceAll(" ",""));
        }

        ILPSolverVariable[] connectedSourceList = new ILPSolverVariable[nodeList.size()];
        ILPSolverVariable[] connectedTargetList = new ILPSolverVariable[nodeList.size()];
        for(int i = 0; i < connectedSourceList.length; i++) {
            connectedSourceList[i] = solver.addVariable(0.0, 1.0, 1.0, ILPSolver.VariableType.BINARY, "S_"+nodeList.get(i).toString().replaceAll("-","_").replaceAll(" ",""));
            connectedTargetList[i] = solver.addVariable(0.0, 1.0, 1.0, ILPSolver.VariableType.BINARY, "T_"+nodeList.get(i).toString().replaceAll("-","_").replaceAll(" ",""));
        }

        ILPSolverVariable[][] subconnectedSourceList = new ILPSolverVariable[nodeList.size()][nodeList.size()];
        ILPSolverVariable[][] subconnectedTargetList = new ILPSolverVariable[nodeList.size()][nodeList.size()];
        for (Edge<T> edge : edgeList) {
            for(int i = 0; i < nodeList.size(); i++) {
                if(edge.getSource().equals(nodeList.get(i))) {
                    for (int j = 0; j < nodeList.size(); j++) {
                        if (i != j && edge.getTarget().equals(nodeList.get(j))) {
                            subconnectedSourceList[i][j] = solver.addVariable(0.0, solver.getInfinity(), 1.0, ILPSolver.VariableType.INTEGER, "SL_" + nodeList.get(j).toString() + "_" + nodeList.get(i).toString().replaceAll("-", "_").replaceAll(" ", ""));
                            subconnectedTargetList[i][j] = solver.addVariable(0.0, solver.getInfinity(), 1.0, ILPSolver.VariableType.INTEGER, "TL_" + nodeList.get(i).toString() + "_" + nodeList.get(j).toString().replaceAll("-", "_").replaceAll(" ", ""));
                            break;
                        }
                    }
                    break;
                }
            }
        }

        // Integrate new variables
        solver.integrateVariables();

        // Set objective: summation of all edges (Equation 1 Paper)
        ILPSolverExpression obj = solver.createExpression();
        for(int i = 0; i < edges.length; i++) {
//            System.out.println("DEBUG - " + edgeList.get(i).getFrequency());
            if(!useArcsFrequency) {
                obj.addTerm(edges[i], 1.0);
            }else {
                obj.addTerm(edges[i], 1 - edgeList.get(i).getFrequency());
            }
        }
        solver.setObjectiveFunction(obj);

        // Add constraint: set mandatory edges (Equation 2 Paper)
        for(int i = 0; i < edgeList.size(); i++) {
            if(!infrequentEdges.contains(edgeList.get(i))) {
                ILPSolverExpression expr = solver.createExpression();
                expr.addTerm(edges[i], 1.0);
                solver.addConstraint(expr, ILPSolver.Operator.EQUAL, 1.0, "edge" + i);
            }
        }

        Set<Integer> sources = new UnifiedSet<Integer>();
        // Add constraint: source is connected to source (Equation 3 Paper)
        for(int i = 0; i < nodeList.size(); i++) {
            if(automaton.getAutomatonStart().contains(nodeList.get(i))) {
                ILPSolverExpression expr = solver.createExpression();
                expr.addTerm(connectedSourceList[i], 1.0);
                solver.addConstraint(expr, ILPSolver.Operator.EQUAL, 1.0, "Start" + i);
                sources.add(i);
            }
        }

        Set<Integer> sinks = new UnifiedSet<Integer>();
        // Add constraint: target is connected to target (Equation 4 Paper)
        for(int i = 0; i < nodeList.size(); i++) {
            if(automaton.getAutomatonEnd().contains(nodeList.get(i))) {
                ILPSolverExpression expr = solver.createExpression();
                expr.addTerm(connectedTargetList[i], 1.0);
                solver.addConstraint(expr, ILPSolver.Operator.EQUAL, 1.0, "End" + i);
                sinks.add(i);
            }
        }

        // Add constraint: node is connected from source 3 (Equation 5 Paper)
        for(int i = 0; i < nodeList.size(); i++) {
            if(requiredStatus.contains(nodeList.get(i))) {
                ILPSolverExpression expr = solver.createExpression();
                expr.addTerm(connectedSourceList[i], 1.0);
                solver.addConstraint(expr, ILPSolver.Operator.EQUAL, 1.0, "");
            }
        }

        // Add constraint: node is connected to target 3 (Equation 6 Paper)
        for(int i = 0; i < nodeList.size(); i++) {
            if(requiredStatus.contains(nodeList.get(i))) {
                ILPSolverExpression expr = solver.createExpression();
                expr.addTerm(connectedTargetList[i], 1.0);
                solver.addConstraint(expr, ILPSolver.Operator.EQUAL, 1.0, "");
            }
        }

        // Add constraint: node is connected from source 1 (Equation 7 using equation 11 Paper)
        for(int i = 0; i < nodeList.size(); i++) {
            if(!sources.contains(i)) {
                for (int j = 0; j < nodeList.size(); j++) {
                    if (i != j && subconnectedSourceList[i][j] != null) {
                        for (int k = 0; k < edgeList.size(); k++) {
                            if (edgeList.get(k).getSource().equals(nodeList.get(i)) && edgeList.get(k).getTarget().equals(nodeList.get(j))) {
                                ILPSolverExpression expr1 = solver.createExpression();
                                expr1.addTerm(connectedSourceList[i], -1.0);
                                expr1.addTerm(edges[k], -1.0);
                                expr1.addTerm(subconnectedSourceList[i][j], 2.0);
                                solver.addConstraint(expr1, ILPSolver.Operator.LESS_EQUAL, 0, "");

                                ILPSolverExpression expr2 = solver.createExpression();
                                expr2.addTerm(connectedSourceList[i], 1.0);
                                expr2.addTerm(edges[k], 1.0);
                                expr2.addTerm(subconnectedSourceList[i][j], -2);
                                solver.addConstraint(expr2, ILPSolver.Operator.LESS_EQUAL, 1.0, "");
                            }
                        }
                    }
                }
            }
        }

        // Add constraint: node is connected to target 1 (Equation 8 using equation 11 Paper)
        for(int i = 0; i < nodeList.size(); i++) {
            if(!sinks.contains(i)) {
                for (int j = 0; j < nodeList.size(); j++) {
                    if (j != i && subconnectedTargetList[i][j] != null) {
                        for (int k = 0; k < edgeList.size(); k++) {
                            if (edgeList.get(k).getSource().equals(nodeList.get(i)) && edgeList.get(k).getTarget().equals(nodeList.get(j))) {
                                ILPSolverExpression expr1 = solver.createExpression();
                                expr1.addTerm(connectedTargetList[j], -1.0);
                                expr1.addTerm(edges[k], -1.0);
                                expr1.addTerm(subconnectedTargetList[i][j], 2.0);
                                solver.addConstraint(expr1, ILPSolver.Operator.LESS_EQUAL, 0, "");

                                ILPSolverExpression expr2 = solver.createExpression();
                                expr2.addTerm(connectedTargetList[j], 1.0);
                                expr2.addTerm(edges[k], 1.0);
                                expr2.addTerm(subconnectedTargetList[i][j], -2);
                                solver.addConstraint(expr2, ILPSolver.Operator.LESS_EQUAL, 1.0, "");
                            }
                        }
                    }
                }
            }
        }

        // Add constraint: node is connected from source 2 (Equation 9 using equation 12 Paper)
        for(int i = 0; i < nodeList.size(); i++) {
            if(!sources.contains(i)) {
                ILPSolverExpression expr1 = solver.createExpression();
                for (int j = 0; j < nodeList.size(); j++) {
                    if (subconnectedSourceList[j][i] != null) {
                        expr1.addTerm(subconnectedSourceList[j][i], -1.0);
                    }
                }
                expr1.addTerm(connectedSourceList[i], 1.0);
                solver.addConstraint(expr1, ILPSolver.Operator.LESS_EQUAL, 0, "");

                ILPSolverExpression expr2 = solver.createExpression();
                for (int j = 0; j < nodeList.size(); j++) {
                    if (subconnectedSourceList[j][i] != null) {
                        expr2.addTerm(subconnectedSourceList[j][i], 1.0);
                    }
                }
                expr2.addTerm(connectedSourceList[i], -solver.getInfinity());
                solver.addConstraint(expr2, ILPSolver.Operator.LESS_EQUAL, 0.0, "");
            }
        }

        // Add constraint: node is connected to target 2 (Equation 10 using equation 12 Paper)
        for(int i = 0; i < nodeList.size(); i++) {
            if(!sinks.contains(i)) {
                ILPSolverExpression expr1 = solver.createExpression();
                for (int j = 0; j < nodeList.size(); j++) {
                    if (subconnectedTargetList[i][j] != null) {
                        expr1.addTerm(subconnectedTargetList[i][j], -1.0);
                    }
                }
                expr1.addTerm(connectedTargetList[i], 1.0);
                solver.addConstraint(expr1, ILPSolver.Operator.LESS_EQUAL, 0, "");

                ILPSolverExpression expr2 = solver.createExpression();
                for (int j = 0; j < nodeList.size(); j++) {
                    if (subconnectedTargetList[i][j] != null) {
                        expr2.addTerm(subconnectedTargetList[i][j], 1.0);
                    }
                }
                expr2.addTerm(connectedTargetList[i], -solver.getInfinity());
                solver.addConstraint(expr2, ILPSolver.Operator.LESS_EQUAL, 0.0, "");
            }
        }

        // Optimize model
        solver.solve();
        if(debug_mode) {
            System.out.println(solver.printProblem());
        }
        ILPSolver.Status status = solver.getStatus();

        if (status == ILPSolver.Status.OPTIMAL) {
            if(debug_mode) {
                System.out.println("The optimal objective is " +
                        solver.getSolutionValue());
            }

            // Identify Removable Arcs
            double[] sol = solver.getSolutionVariables(edges);
            for (int i = 0; i < edges.length; i++) {
                if (sol[i] == 0) {
                    removable.add(edgeList.get(i));
                }
            }
        }else {
            if (status == ILPSolver.Status.UNBOUNDED) {
                if(debug_mode) {
                    System.out.println("The model cannot be solved "
                            + "because it is unbounded");
                }
            }
            if (status == ILPSolver.Status.INFEASIBLE) {
                if(debug_mode) {
                    System.out.println("The model is infeasible");
                }
            }
        }

        // Dispose of model and environment
        solver.dispose();

        return removable;
    }
}
