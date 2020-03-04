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

package com.raffaeleconforti.ilpminer;

import net.sf.javailp.*;
import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.Pair;
import org.processmining.plugins.ilpminer.ILPMinerSettings;
import org.processmining.plugins.ilpminer.ILPMinerSolution;
import org.processmining.plugins.ilpminer.ILPModelSettings;
import org.processmining.plugins.ilpminer.templates.PetriNetILPModelSettings;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 21/10/2016.
 */
public class PetriNetILPModel extends ILPModelJavaILP {
    protected static PetriNetILPModelSettings strategySettings;

    public int trans = 0, lang = 0;
    public int[][] a = {}, aPrime = {};
    public ArrayList<Integer> cd = null;
    protected ArrayList<XEventClass> initialPlaces, notInitialPlaces;

    protected boolean initialPlace;

    public PetriNetILPModel(Class<?>[] extensions, Map<ILPMinerSettings.SolverSetting, Object> solverSettings, ILPModelSettings settings) {
        super(extensions, solverSettings, settings);
        strategySettings = (PetriNetILPModelSettings) settings;
    }

    public Problem getModel() {
        Problem p = new Problem();
        addObjective(p);
        addConstraints(p);
        addExtensionConstraints(p);
        addVariables(p);
        return p;
    }

    public void makeData() {
        // store the log information provided in the parameters for future
        // access
        Object[] values = m.values().toArray();
        trans = values.length;
        lang = l.last();
        a = l.getTransitionCountMatrix();
        aPrime = l.getTransitionCountMatrix(1);

        // find all transitions that are not in the second part of a causal
        // dependency
        // start with everything and remove those that are in the second part of
        // a causal dependency
        initialPlaces = new ArrayList<XEventClass>(r.getEventClasses().getClasses());
        notInitialPlaces = new ArrayList<XEventClass>();
        for (Map.Entry<Pair<XEventClass, XEventClass>, Double> entry : r.getCausalDependencies().entrySet()) {
            if (entry.getValue() > 0) {
                int size = initialPlaces.size();
                initialPlaces.remove(entry.getKey().getSecond());
                if (initialPlaces.size() != size) {
                    notInitialPlaces.add(entry.getKey().getSecond());
                }
            }
        }
    }

    protected void processModel(PluginContext context, SolverFactory factory) {
        if (strategySettings.separateInitialPlaces()) {
            initialPlace = true;
            processInitialPlaces(context);
        }
        initialPlace = false;
        switch (strategySettings.getSearchType()) {
            case BASIC :
                processBasic(context);
                break;
            case PER_TRANSITION :
                processTransitions(context, true);
                processTransitions(context, false);
                break;
            case PRE_PER_TRANSITION :
                processTransitions(context, true);
                break;
            case POST_PER_TRANSITION :
                processTransitions(context, false);
                break;
            default :// PER_CD
                processCausalDependencies(context);
                break;
        }
    }

    protected void processInitialPlaces(PluginContext context) {
        for (XEventClass c : initialPlaces) {
            if (context.getProgress().isCancelled()) {
                return;
            }
            cd = new ArrayList<Integer>();
            cd.add(m.get(c));
            cd.add(-1);

            addSolution(solve(context));
        }
    }

    protected void processBasic(PluginContext context) {
        boolean isOK = true;

        while (isOK && !context.getProgress().isCancelled()) {
            Result res = solve(context);
            addSolution(res);
            isOK = res != null;
            isOK = isOK && (solutions.size() < Math.pow(r.getEventClasses().size(), 2));
        }
    }

    protected void processTransitions(PluginContext context, boolean beforeTrans) {
        ArrayList<XEventClass> places = new ArrayList<XEventClass>(r.getEventClasses().getClasses());
        if (strategySettings.separateInitialPlaces() && beforeTrans) {
            places = notInitialPlaces;
        }

        int i = 1;
        context.getProgress().setIndeterminate(false);
        context.getProgress().setMaximum(places.size());
        for (XEventClass clazz : places) {
            if (context.getProgress().isCancelled()) {
                return;
            }
            context.getProgress().setValue(i);
            i++;

            cd = new ArrayList<Integer>();
            cd.add(m.get(clazz));
            // abuse cd to indicate wether we search
            // in front of (0) or after (1) a transition
            cd.add(beforeTrans ? 0 : 1);

            addSolution(solve(context));
        }
        context.getProgress().setIndeterminate(true);
    }

    protected void processCausalDependencies(PluginContext context) {
        int i = 1;
        context.getProgress().setIndeterminate(false);
        context.getProgress().setMaximum(r.getCausalDependencies().size());
        for (Map.Entry<Pair<XEventClass, XEventClass>, Double> entry : r.getCausalDependencies().entrySet()) {
            if (context.getProgress().isCancelled()) {
                return;
            }
            if (entry.getValue() > 0) {
                context.getProgress().setValue(i);
                i++;

                cd = new ArrayList<Integer>();
                cd.add(m.get(entry.getKey().getFirst()));
                cd.add(m.get(entry.getKey().getSecond()));

                addSolution(solve(context));
            }
        }
        context.getProgress().setIndeterminate(true);
    }

    /**
     * defines the variable types and bounds in the problem
     *
     * @param p
     *            - problem
     */
    protected void addVariables(Problem p) {
        for (int t = 0; t < trans; t++) {
            p.setVarType("x" + t, VarType.INT);
            p.setVarLowerBound("x" + t, 0);
            p.setVarUpperBound("x" + t, 1);
            p.setVarType("y" + t, VarType.INT);
            p.setVarLowerBound("y" + t, 0);
            p.setVarUpperBound("y" + t, 1);
        }
        p.setVarType("c", VarType.INT);
        p.setVarLowerBound("c", 0);
        p.setVarUpperBound("c", 1);
    }

    /**
     * adds an objective to the problem
     *
     * @param p
     *            - problem
     */
    protected void addObjective(Problem p) {
        // "c + sum(w in Lang) ( c + ( sum(t in trans) A[w][t] ) * (x[t] - y[t]) );";
        Linear l = new Linear();
        l.add(1 + lang, "c");
        for (int t = 0; t < trans; t++) {
            int sum = 0;
            for (int w = 0; w < lang; w++) {
                sum += a[w][t];
            }
            l.add(sum, "x" + t);
            l.add(-sum, "y" + t);
        }
        p.setObjective(l, OptType.MIN);
    }

    /**
     * adds the constraints to the problem
     *
     * @param p
     *            - problem
     */
    protected void addConstraints(Problem p) {
        // forall(w in Lang) ctMinimalRegion: c + ( sum(t in Trans) APrime[w][t]
        // * x[t] ) - ( sum(t in Trans) A[w][t] * y[t] ) >= 0;
        for (int w = 0; w < lang; w++) {
            Linear l = new Linear();
            l.add(1, "c");
            for (int t = 0; t < trans; t++) {
                if (aPrime[w][t] > 0) {
                    l.add(aPrime[w][t], "x" + t);
                }
                if (a[w][t] > 0) {
                    l.add(-a[w][t], "y" + t);
                }
            }
            p.add(l, Operator.GE, 0);
        }
        addPlaceConstraints(p);
    }

    protected void addPlaceConstraints(Problem p) {
        if (initialPlace) {
            // ctAtLeastOneArc: sum(t in Trans) (x[t] + y[t]) >= 1;
            // => volgt uit volgende constraint(s)
            // ctToken: c == 1;
            Linear l = new Linear();
            l.add(1, "c");
            p.add(l, Operator.EQ, 1);
            // ctInitialPlace: sum(t in Trans) x[t] == 0;" + "y[CD.To] == 1;
            l = new Linear();
            for (int t = 0; t < trans; t++) {
                l.add(1, "x" + t);
            }
            p.add(l, Operator.EQ, 0);
            l = new Linear();
            l.add(1, "y" + cd.get(0).toString());
            p.add(l, Operator.EQ, 1);
        } else {
            if (strategySettings.separateInitialPlaces()) {
                // ctNoToken: c == 0;
                Linear l = new Linear();
                l.add(1, "c");
                p.add(l, Operator.EQ, 0);
            }
            switch (strategySettings.getSearchType()) {
                case BASIC :
                    // ctAtLeastOneArc: sum(t in Trans) (x[t] + y[t]) >= 1;
                    Linear l = new Linear();
                    for (int t = 0; t < trans; t++) {
                        l.add(1, "x" + t);
                        l.add(1, "y" + t);
                    }
                    p.add(l, Operator.GE, 1);
                    // ctNotOldSolution: forall(s in Solutions) 2 * c * s.C + sum(t
                    // in Trans) (2 * x[t] * s.X[t] + 2 * y[t] * s.Y[t]) < c + s.c +
                    // sum(t in Trans) (x[t] + s.X[t] + y[t] + s.Y[t]);
                    for (ILPMinerSolution s : solutions) {
                        int sumS = 0;
                        l = new Linear();
                        l.add(2 * s.getTokens() - 1, "c");
                        sumS += s.getTokens();
                        for (int t = 0; t < trans; t++) {
                            l.add(2 * s.getInputSet()[t] - 1, "x" + t);
                            sumS += s.getInputSet()[t];
                            l.add(2 * s.getOutputSet()[t] - 1, "y" + t);
                            sumS += s.getOutputSet()[t];
                        }
                        p.add(l, Operator.LE, sumS - 1);
                    }
                    break;
                case PER_TRANSITION :
                case PRE_PER_TRANSITION :
                case POST_PER_TRANSITION :
                    // ctAtLeastOneArc: sum(t in Trans) (x[t] + y[t]) >= 1;
                    // => volgt uit volgende constraint(s)
                    l = new Linear();
                    if (cd.get(1) == 0) { // search the place in front of the
                        // transition
                        l.add(1, "y" + cd.get(0).toString());
                    } else { // cd.get(1) == 1; search the place after the
                        // transition
                        l.add(1, "x" + cd.get(0).toString());
                    }
                    p.add(l, Operator.EQ, 1);
                    break;
                default : // PER_CD
                    // ctAtLeastOneArc: sum(t in Trans) (x[t] + y[t]) >= 1;
                    // => volgt uit volgende constraint(s)
                    // ctCausalDependency: x[CD.From] == 1 && y[CD.To] == 1;
                    l = new Linear();
                    l.add(1, "x" + cd.get(0).toString());
                    p.add(l, Operator.EQ, 1);
                    l = new Linear();
                    l.add(1, "y" + cd.get(1).toString());
                    p.add(l, Operator.EQ, 1);
                    break;
            }
        }
    }

    /**
     * converts the Java-ILP result in a solution (place representation) and
     * adds it to the set of found solutions
     *
     * @param result
     */
    protected void addSolution(Result result) {
        if (result != null) {
            solutions.add(makeSolution(result));
        }
    }

    /**
     * converts the Java-ILP result in a solution (place representation)
     *
     * @param result
     * @return the converted solution
     */
    protected ILPMinerSolution makeSolution(Result result) {
        double[] x = new double[trans];
        double[] y = new double[trans];
        for (int t = 0; t < trans; t++) {
            x[t] = result.get("x" + t).doubleValue();
            y[t] = result.get("y" + t).doubleValue();
        }
        double c = result.get("c").doubleValue();
        return new ILPMinerSolution(x, y, c);
    }
}
