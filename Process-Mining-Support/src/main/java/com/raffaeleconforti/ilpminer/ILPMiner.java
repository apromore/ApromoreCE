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

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginExecutionResult;
import org.processmining.framework.plugin.PluginParameterBinding;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.framework.util.Pair;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.ilpminer.*;
import org.processmining.plugins.ilpminer.ILPMinerSettings.SolverSetting;
import org.processmining.plugins.ilpminer.ILPMinerSettings.SolverType;
import org.processmining.plugins.log.logabstraction.LogRelations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

/**
 * This plugin uses the Java-ILP library in order to execute ILP problems that
 * will generate petrinet places.
 *
 * @author T. van der Wiel
 *
 */
public class ILPMiner {
    /**
     * This method shows the GUI, in which the user can indicate which variant
     * of the ILP must be used and then tries to construct a petrinet.
     *
     * @param context
     * @param log
     * @return Petrinet & Marking
     * @throws Exception
     */
    public Object[] doILPMining(UIPluginContext context, XLog log) throws Exception {
        ILPMinerSettings settings = generateSetting(context, log);
        saveSettings(settings);
        return doILPMiningWithSettings(context, log, XLogInfoFactory.createLogInfo(log), settings);
    }

    public ILPMinerSettings generateSetting(UIPluginContext context, XLog log) {
        ILPMinerUI ui;
        try {
            ILPMinerLogPetrinetConnection conn = context.getConnectionManager()
                    .getFirstConnection(ILPMinerLogPetrinetConnection.class,
                            context, log);
            ui = new ILPMinerUI(conn.getObjectWithRole(ILPMinerLogPetrinetConnection.SETTINGS));
        } catch (Exception e) {
            ui = new ILPMinerUI();
        }
        return ui.getSettings();

    }

    /**
     * Stores the settings of the chosen ILP solver in the
     * (HKEY_Current_User/Software/JavaSoft/Prefs) registry
     *
     *            containing the user settings about the solver to use (and its
     *            components locations)
     */
    private void saveSettings(ILPMinerSettings settings) {
        Preferences prefs = Preferences.userNodeForPackage(ILPMiner.class);
        prefs.putInt("SolverEnum", ((SolverType) settings
                .getSolverSetting(SolverSetting.TYPE)).ordinal());
        prefs.put("LicenseDir", (String) settings
                .getSolverSetting(SolverSetting.LICENSE_DIR));
    }

    /**
     * This method tries to construct log relations on the log as they are not
     * provided. Exceptions are thrown if no plugin is available to do so.
     *
     * @param context
     * @param log
     * @param log
     *            summary
     * @return Petrinet & Marking built using the ILP model variant provided in
     *         the settings
     * @throws Exception
     */
    public Object[] doILPMiningWithSettings(PluginContext context, XLog log,
                                            XLogInfo summary, ILPMinerSettings settings) throws Exception {

        // No log relations are specified, so find a plugin that can construct
        // them.
        // This is done, by asking the plugin manager for a plugin, that:
        // 1) It's a plugin (i.e. the annotation is Plugin.class),
        // 2) It returns LogRelations (i.e. one of the return types is
        // LogRelations.class or any subclass thereof),
        // 3) It can be executed in a child of this context, which is of type
        // context.getPluginContextType(),
        // 4) It can be executed on the given input (i.e. no extra input is
        // needed, and all input is used),
        // 5) It accepts the input in any given order (i.e. not in the specified
        // order),
        // 6) It does not have to be user visible
        // 7) It can use objects given by log and summary (i.e. types
        // log.getClass() and summary.getClass()).
        Collection<Pair<Integer, PluginParameterBinding>> plugins = context
                .getPluginManager().find(Plugin.class, LogRelations.class,
                        context.getPluginContextType(), true, false, false,
                        log.getClass(), summary.getClass());

        if (plugins.isEmpty()) {
            context
                    .log(
                            "No plugin found to create log relations, please specify relations manually",
                            MessageLevel.ERROR);
            return null;
        }
        // Let's just take the first available plugin for the job of
        // constructing log abstractions
        Pair<Integer, PluginParameterBinding> plugin = plugins.iterator()
                .next();

        // Now, the binding can be executed on the log and the summary
        // FIrst, we instantiate a new context for this plugin, which is a child
        // context of the current context.
        PluginContext c2 = context
                .createChildContext("Log Relation Constructor");

        // Let's notify our lifecyclelisteners about the fact that we created a
        // new context. this is
        // optional, but if this is not done, then the user interface doesn't
        // show it (if there is a UI).
        context.getPluginLifeCycleEventListeners().firePluginCreated(c2);

        // At this point, we execute the binding to get the LogRelations. For
        // this, we call the invoke method
        // on the PluginParameterBinding stored in the plugin variable. The
        // return type is LogRelations.class and
        // as input we give the new context c2, the log and the summary. Note
        // that the plugin might return mulitple
        // objects, hence we extract the object with number x, where x is stored
        // as the first element of the plugin
        // variable.

        PluginExecutionResult pluginResult = plugin.getSecond().invoke(c2, log,
                summary);
        pluginResult.synchronize();
        LogRelations relations = pluginResult.getResult(plugin
                .getFirst());

        // Now we have the relations and we can continue with the mining.
        return doILPMiningPrivateWithRelations(context, relations.getSummary(),
                relations, settings);
    }

    public Object[] doILPMiningWithLanguage(PluginContext context, XLog log,
                                            XLogInfo summary, ILPMinerSettings settings, PrefixClosedLanguage l)
            throws Exception {
        // just do this as there has to be a plugin that recognizes mining with
        // a pfcl. the pfcl will be detected later on anyway.
        return doILPMiningWithSettings(context, log, summary, settings);
    }

    /**
     * Initializes the ILP problem and makes a petrinet from the found solution.
     *
     * @param context
     * @param summary
     * @param relations
     * @param settings
     * @return Petrinet & Marking
     * @throws Exception
     */
    public Object[] doILPMiningPrivateWithRelations(PluginContext context,
                                                     XLogInfo summary, LogRelations relations, ILPMinerSettings settings)
            throws Exception {
        XLog log = relations.getLog();
        XEventClasses classes = summary.getEventClasses();

        // give the petrinet a name that describes which ILP model variant and
        // log it represents
        String extensions = " (";
        for (Class<?> c : settings.getExtensions()) {
            extensions += c.getMethod("getName",
                    new Class[] { Class.class }).invoke(null,
                    new Object[] { c })
                    + ", ";
        }
        if (extensions.length() == 2) {
            extensions = "";
        } else {
            extensions = extensions.substring(0, extensions.length() - 2) + ")";
        }
        Petrinet net = PetrinetFactory.newPetrinet(settings.getVariant()
                .getMethod("getName", new Class[] { Class.class }).invoke(null,
                        settings.getVariant())
                + extensions
                + " from "
                + XConceptExtension.instance().extractName(log)
                + ", mined with ILP Miner");
        context.getFutureResult(0).setLabel(net.getLabel());
        context.getFutureResult(1).setLabel(
                "Initial Marking of " + net.getLabel());

        Map<XEventClass, Integer> indices = new HashMap<XEventClass, Integer>();
        Map<Integer, Transition> transitions = new HashMap<Integer, Transition>();
        int i = 0;
        // create the mapping between the eventclasses and an integer for a
        // smaller ILP model
        for (XEventClass evClass : classes.getClasses()) {
            indices.put(evClass, i);
            Transition t = net.addTransition(evClass.toString());
            transitions.put(i, t);
            i++;
        }
        // generate the prefix closed language of the log (i.e. L = {w | exi w2
        // in Transitions* : (w w2) in Traces})
        PrefixClosedLanguage l;
        try {
            LogPrefixclosedlanguageConnection conn = context
                    .getConnectionManager().getFirstConnection(
                            LogPrefixclosedlanguageConnection.class, context,
                            log);
            l = conn
                    .getObjectWithRole(LogPrefixclosedlanguageConnection.LANGUAGE);
        } catch (Exception e) {
            l = new PrefixClosedLanguage(log, indices, classes);
            context.getProvidedObjectManager().createProvidedObject(
                    "Prefix-closed language", l, PrefixClosedLanguage.class,
                    context);
            context
                    .addConnection(new LogPrefixclosedlanguageConnection(log, l));
        }
        return doILPMiningPrivateWithRelationsAndLanguage(context, relations,
                settings, l, indices, net, classes, transitions);
    }

    private Object[] doILPMiningPrivateWithRelationsAndLanguage(
            PluginContext context, LogRelations relations,
            ILPMinerSettings settings, PrefixClosedLanguage l,
            Map<XEventClass, Integer> indices, Petrinet net,
            XEventClasses classes, Map<Integer, Transition> transitions)
            throws Exception {
        Set<ILPMinerSolution> solutions;
        // create an instance of the ILP model variant indicated by the user
        ILPModelJavaILP modelJavaILP;
        try {
            modelJavaILP = new PetriNetILPModel(settings.getExtensions(), settings.getSolverSettings(), settings.getModelSettings());
        } catch (Exception e) {
            throw e;
        }

        // let the ILP model find all solutions (all: meaning the amount of
        // places it is supposed to find)
        modelJavaILP.findPetriNetPlaces(indices, l, relations, context);
        solutions = modelJavaILP.getSolutions();

        postProcessSolutions(solutions);

        // add all found solutions to the petrinet/marking
        int placeId = 1;
        Marking m = new Marking();
        for (ILPMinerSolution s : solutions) {
            Place p = net.addPlace("P " + placeId++);
            // Add arcs corresponding to the x vector
            for (int i = 0; i < s.getInputSet().length; i++) {
                if (s.getInputSet()[i] > 0) {
                    net.addArc(transitions.get(i), p);
                }
            }
            // Add arcs corresponding to the y vector
            for (int i = 0; i < s.getOutputSet().length; i++) {
                if (s.getOutputSet()[i] > 0) {
                    net.addArc(p, transitions.get(i));
                }
            }
            // Update marking
            for (int i = 0; i < s.getTokens(); i++) {
                m.add(p);
            }
        }

        // Let the framework know that the marking and the petrinet belong
        // together
        context.addConnection(new InitialMarkingConnection(net, m));

        // let the framework know that there is a connection between the event
        // classes in the log and the transitions in the Petri net.
        Map<Transition, XEventClass> mapping = new HashMap<Transition, XEventClass>(
                classes.size());
        for (XEventClass clazz : indices.keySet()) {
            if (clazz != null) {
                mapping.put(transitions.get(indices.get(clazz)), clazz);
            }
        }
        context.getProvidedObjectManager()
                .createProvidedObject("ILP Miner settings", settings,
                        ILPMinerSettings.class, context);
        context.addConnection(new ILPMinerLogPetrinetConnection(relations
                .getLog(), classes, net, mapping, settings));

        // return the net and the marking
        return new Object[] { net, m };
    }

    private void postProcessSolutions(Set<ILPMinerSolution> solutions) {
        ILPMinerSolution[] array = solutions.toArray(new ILPMinerSolution[0]);
        for (int i = 0; i < array.length; i++) {
            for (ILPMinerSolution s : solutions) {
                if (array[i].compareTo(s) < 0) {
                    solutions.remove(array[i]);
                    break;
                }
            }
        }
    }
}