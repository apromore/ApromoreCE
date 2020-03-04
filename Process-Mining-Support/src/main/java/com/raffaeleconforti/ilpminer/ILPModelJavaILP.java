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
import org.processmining.plugins.ilpminer.*;
import org.processmining.plugins.log.logabstraction.LogRelations;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 21/10/2016.
 */
public abstract class ILPModelJavaILP {

    protected Set<ILPMinerSolution> solutions = new HashSet<ILPMinerSolution>();
    protected Class<?>[] extensions;
    protected Map<XEventClass, Integer> m;
    protected PrefixClosedLanguage l;
    protected LogRelations r;
    protected Map<ILPMinerSettings.SolverSetting, Object> solverSettings;
    protected SolverFactory factory;

    public ILPModelJavaILP(Class<?>[] extensions, Map<ILPMinerSettings.SolverSetting, Object> solverSettings, ILPModelSettings settings) {
        this.extensions = extensions;
        this.solverSettings = solverSettings;
    }

    /**
     * Generates the model specific data from the generic data
     *
     */
    public abstract void makeData();

    /**
     * Returns the model instantiated via the Java-ILP interface
     *
     * @return problem
     */
    public abstract Problem getModel();

    /**
     * Builds the ILP problem and executes it.
     *
     */
    public void findPetriNetPlaces(Map<XEventClass, Integer> indices, PrefixClosedLanguage pfclang,
                                   LogRelations relations, PluginContext context) throws IOException {
        solutions = new HashSet<ILPMinerSolution>();
        factory = loadLibraries();
        factory.setParameter(Solver.VERBOSE, 0);
        factory.setParameter(Solver.TIMEOUT, 100);

        m = indices;
        l = pfclang;
        r = relations;
        makeData();

        context.getProgress().setCaption("Searching places...");
        // execute the model using the ILP model variant overwriting this class
        processModel(context, factory);
    }

    /**
     * Loads the required jar and dll files (from the location) provided by the
     * user via the settings if not loaded already and creates a solverfactory
     *
     * @return solverfactory
     * @throws IOException
     */
    protected SolverFactory loadLibraries() throws IOException {
        SolverFactory factory;
        try {
            System.loadLibrary("lpsolve55");
            System.loadLibrary("lpsolve55j");
            factory = new SolverFactoryLpSolve();
        } catch (Exception e) {
            throw new IOException("Unable to load required libraries.", e);
        }

        return factory;
    }

    /**
     * Finds the solutions required for this model
     *
     * @param context
     * @param factory
     */
    protected abstract void processModel(PluginContext context, SolverFactory factory);

    /**
     * solves the model in the modeldefinition with this being the data source
     *
     * @param context
     */
    protected Result solve(PluginContext context) {
        context.log("Generating Java-ILP model");

        Problem problem = getModel();
        try {
            context.log("Solving...");

            Solver solver = factory.get();
            long solveTime = System.currentTimeMillis();
            Result result = solver.solve(problem);
            context.log("Solving time: " + (System.currentTimeMillis() - solveTime));
            context.log("Memory: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
            System.gc();
            return result;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * returns the solutions found with processModel
     *
     * @return Solution list
     */
    public Set<ILPMinerSolution> getSolutions() {
        return solutions;
    }

    private static ILPMinerStrategy getAnnotation(Class<?> strategy) throws ClassNotFoundException {
        return Class.forName(strategy.getName()).getAnnotation(ILPMinerStrategy.class);
    }

    public static String getName(Class<?> strategy) {
        try {
            return getAnnotation(strategy).name();
        } catch (Exception e) {
            return "[Unnamed strategy]";
        }
    }

    /**
     * adds all the extensions constraints to the problem via reflection
     *
     */
    public void addExtensionConstraints(Problem p) {
        for (Class<?> extension : extensions) {
            Method[] methods = extension.getMethods();
            for (Method m : methods) {
                if (m.isAnnotationPresent(ILPMinerStrategyExtensionImpl.class)) {
                    ILPMinerStrategyExtensionImpl a = m.getAnnotation(ILPMinerStrategyExtensionImpl.class);
                    try {
                        m.invoke(extension.newInstance(), p, this);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

}