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

package com.raffaeleconforti.heuristicsminer;

import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import org.processmining.framework.log.*;
import org.processmining.framework.models.heuristics.HNSet;
import org.processmining.framework.models.heuristics.HNSubSet;
import org.processmining.framework.models.heuristics.HeuristicsNet;
import org.processmining.mining.MiningResult;
import org.processmining.mining.geneticmining.fitness.duplicates.DTContinuousSemanticsFitness;
import org.processmining.mining.geneticmining.fitness.duplicates.DTImprovedContinuousSemanticsFitness;
import org.processmining.mining.heuristicsmining.HeuristicsMinerGUI;
import org.processmining.mining.heuristicsmining.HeuristicsMinerParameters;
import org.processmining.mining.heuristicsmining.HeuristicsNetResult;
import org.processmining.mining.heuristicsmining.models.DependencyHeuristicsNet;
import org.processmining.mining.logabstraction.LogAbstraction;
import org.processmining.mining.logabstraction.LogAbstractionImpl;

import java.io.IOException;
import java.util.BitSet;

/**
 * Created by Raffaele Conforti on 20/02/14.
 */
public class HeuristicsMiner {

    private final static double CAUSALITY_FALL = 0.8;
    // public final static boolean LT_DEBUG = false;

    private LogEvents events;
    // the different counters and measurments
    private DoubleMatrix1D startCount;
    private DoubleMatrix1D endCount;
    private DoubleMatrix2D directSuccessionCount; // = logAbstraction.getFollowerInfo(1).copy();
    private DoubleMatrix2D succession2Count; // = logAbstraction.getCloseInInfo(2).copy();

    private DoubleMatrix2D longRangeSuccessionCount; // calculated in makeBasicRelations
    private DoubleMatrix2D longRangeDependencyMeasures; // information about the longrange dependecy relation
    // private DoubleMatrix2D HNlongRangeFollowingChance; //information about the following chance in the Heuristics Net

    private DoubleMatrix2D causalSuccession; // calculated in makeBasicRelations

    private DoubleMatrix1D L1LdependencyMeasuresAll;
    private DoubleMatrix2D L2LdependencyMeasuresAll;
    private DoubleMatrix2D ABdependencyMeasuresAll;

    private DoubleMatrix2D dependencyMeasuresAccepted;

    private DoubleMatrix2D andInMeasuresAll;
    private DoubleMatrix2D andOutMeasuresAll;

    private boolean[] L1Lrelation;

    // noiseCounter counts the total wrong dependency observations in the log
    private DoubleMatrix2D noiseCounters;

    private HeuristicsMinerParameters parameters = new HeuristicsMinerParameters();
    private HeuristicsMinerGUI ui = null;

    // this is the Heuristics Miner methode call
    public MiningResult mine(LogReader log, boolean keepUnusedRelations) {
        LogAbstraction logAbstraction;
        HeuristicsNet net; //because we have a single individual

        // show a small context window

        logAbstraction = new LogAbstractionImpl(log);
        events = log.getLogSummary().getLogEvents();

        try {
            startCount = logAbstraction.getStartInfo().copy();
            endCount = logAbstraction.getEndInfo().copy();

            directSuccessionCount = logAbstraction.getFollowerInfo(1).copy();

            succession2Count = logAbstraction.getCloseInInfo(2).copy();

        } catch (IOException ex) {
            return null;
        }


        longRangeSuccessionCount = DoubleFactory2D.dense.make(events.size(), events.size(), 0);
        causalSuccession = DoubleFactory2D.dense.make(events.size(), events.size(), 0);
        longRangeDependencyMeasures = DoubleFactory2D.dense.make(events.size(), events.size(), 0);
        L1LdependencyMeasuresAll = DoubleFactory1D.sparse.make(events.size(), 0);
        andInMeasuresAll = DoubleFactory2D.sparse.make(events.size(), events.size(), 0);
        andOutMeasuresAll = DoubleFactory2D.sparse.make(events.size(), events.size(), 0);

        L2LdependencyMeasuresAll = DoubleFactory2D.sparse.make(events.size(), events.size(), 0);
        ABdependencyMeasuresAll = DoubleFactory2D.sparse.make(events.size(), events.size(), 0);
        dependencyMeasuresAccepted = DoubleFactory2D.sparse.make(events.size(), events.size(), 0);
        noiseCounters = DoubleFactory2D.sparse.make(events.size(), events.size(), 0);

        // Building basic relations
        makeBasicRelations(log, CAUSALITY_FALL);

        net = makeHeuristicsRelations(log);
        if (!keepUnusedRelations) {
            removeUnusedElements(net);
        }

        //return new DTGeneticMinerResult(net, log);
        return new HeuristicsNetResult(net, log);
    }

    private void showExtraInfo() {

        int numberOfConnections = 0;

        for (int i = 0; i < dependencyMeasuresAccepted.rows(); i++) {
            for (int j = 0; j < dependencyMeasuresAccepted.columns(); j++) {
                if (dependencyMeasuresAccepted.get(i, j) > 0.01) {
                    numberOfConnections = numberOfConnections + 1;
                }
            }
        }

        int noiseTotal = 0;

        for (int i = 0; i < noiseCounters.rows(); i++) {
            for (int j = 0; j < noiseCounters.columns(); j++) {
                noiseTotal = noiseTotal + (int) noiseCounters.get(i, j);
            }
        }
    }

    private double calculateDependencyMeasure(int i, int j) {
        return (directSuccessionCount.get(i, j) - directSuccessionCount.get(j, i)) /
                (directSuccessionCount.get(i, j) +
                        directSuccessionCount.get(j, i) +
                        parameters.getDependencyDivisor()
                );
    }

    private double calculateL1LDependencyMeasure(int i) {
        return directSuccessionCount.get(i, i) /
                (directSuccessionCount.get(i, i) + parameters.getDependencyDivisor());
    }

    private double calculateL2LDependencyMeasure(int i, int j) {
        // problem if for instance we have a A -> A loop
        // in parallel with B the |A>B>A|-value can be high without a L2L-loop
        if ((L1Lrelation[i] && succession2Count.get(i,
                j) >= parameters.getPositiveObservationsThreshold()) ||
                (L1Lrelation[j] && succession2Count.get(j,
                        i) >= parameters.getPositiveObservationsThreshold())) {
            return 0.0;
        } else {
            return (succession2Count.get(i, j) + succession2Count.get(j, i)) /
                    (succession2Count.get(i, j) +
                            succession2Count.get(j, i) +
                            parameters.getDependencyDivisor()
                    );
        }
    }

    private double calculateLongDistanceDependencyMeasure(int i, int j) {
        return (longRangeSuccessionCount.get(i, j) /
                (events.getEvent(i).getOccurrenceCount() + parameters.getDependencyDivisor())) -
                (5.0 * (Math.abs(events.getEvent(i).getOccurrenceCount() -
                        events.getEvent(j).getOccurrenceCount())) /
                        events.getEvent(i).getOccurrenceCount());

    }

    private void makeBasicRelations(LogReader log, double causalityFall) {
        log.reset();
        while (log.hasNext()) {
            ProcessInstance pi = log.next();
            AuditTrailEntries ate = pi.getAuditTrailEntries();

            int i = 0;
            boolean terminate = false;

            while (!terminate) {
                ate.reset();
                // Skip the first i entries of the trace
                for (int j = 0; j < i; j++) {
                    ate.next();
                }
                // Work with the other entries.
                AuditTrailEntry begin = ate.next();
                // Find the correct row of the matices
                int row = events.findLogEventNumber(begin.getElement(),
                        begin.getType());
                int distance = 0;
                boolean foundSelf = false;
                HNSubSet done = new HNSubSet();
                terminate = (!ate.hasNext());
                while (ate.hasNext() && (!foundSelf)) {
                    AuditTrailEntry end = ate.next();
                    int column = events.findLogEventNumber(end.
                                    getElement(),
                            end.getType()
                    );

                    foundSelf = (row == column);
                    distance++;

                    if (done.contains(column)) {
                        continue;
                    }
                    done.add(column);

                    // update long range matrix
                    longRangeSuccessionCount.set(row, column, longRangeSuccessionCount.get(row,
                            column) + 1);

                    // update causal matrix
                    causalSuccession.set(row, column, causalSuccession.get(row,
                            column) + Math.pow(causalityFall, distance - 1));

                }
                i++;
            }
        }

        // calculate causalSuccesion (==> not yet used during heuristics process mining!!!
        for (int i = 0; i < causalSuccession.rows(); i++) {
            for (int j = 0; j < causalSuccession.columns(); j++) {
                if (causalSuccession.get(i, j) == 0) {
                    continue;
                }
                causalSuccession.set(i, j, causalSuccession.get(i, j) /
                        longRangeSuccessionCount.get(i, j));
            }
        }
        // calculate longRangeDependencyMeasures
        for (int i = 0; i < longRangeDependencyMeasures.rows(); i++) {
            for (int j = 0; j < longRangeDependencyMeasures.columns(); j++) {
                if (events.getEvent(i).getOccurrenceCount() == 0) {
                    continue;
                }
                longRangeDependencyMeasures.set(i, j, calculateLongDistanceDependencyMeasure(i, j));
            }

        }

    }

//    private void makeBasicRelations(XLog log, double causalityFall) {
//        XConceptExtension xce = XConceptExtension.instance();
//        for(XTrace trace : log) {
//
//            int i = 0;
//            int pos = 0;
//            boolean terminate = false;
//
//            while (!terminate) {
//                pos = 0;
//                // Skip the first i entries of the trace
//                for (int j = 0; j < i; j++) {
//                    pos++;
//                }
//                // Work with the other entries.
//                XEvent begin = trace.get(pos);
//                // Find the correct row of the matices
//                int row = events.findLogEventNumber(xce.extractName(begin),
//                        begin.getType());
//                int distance = 0;
//                boolean foundSelf = false;
//                HNSubSet done = new HNSubSet();
//                terminate = (pos < trace.size());
//                while (pos < trace.size() && (!foundSelf)) {
//                    pos++;
//                    XEvent end = trace.get(pos);
//                    int column = events.findLogEventNumber(end.
//                                    getElement(),
//                            end.getType()
//                    );
//
//                    foundSelf = (row == column);
//                    distance++;
//
//                    if (done.contains(column)) {
//                        continue;
//                    }
//                    done.add(column);
//
//                    // update long range matrix
//                    longRangeSuccessionCount.set(row, column, longRangeSuccessionCount.get(row,
//                            column) + 1);
//
//                    // update causal matrix
//                    causalSuccession.set(row, column, causalSuccession.get(row,
//                            column) + Math.pow(causalityFall, distance - 1));
//
//                }
//                i++;
//            }
//        }
//
//        // calculate causalSuccesion (==> not yet used during heuristics process mining!!!
//        for (int i = 0; i < causalSuccession.rows(); i++) {
//            for (int j = 0; j < causalSuccession.columns(); j++) {
//                if (causalSuccession.get(i, j) == 0) {
//                    continue;
//                }
//                causalSuccession.set(i, j, causalSuccession.get(i, j) /
//                        longRangeSuccessionCount.get(i, j));
//            }
//        }
//        // calculate longRangeDependencyMeasures
//        for (int i = 0; i < longRangeDependencyMeasures.rows(); i++) {
//            for (int j = 0; j < longRangeDependencyMeasures.columns(); j++) {
//                if (events.getEvent(i).getOccurrenceCount() == 0) {
//                    continue;
//                }
//                longRangeDependencyMeasures.set(i, j, calculateLongDistanceDependencyMeasure(i, j));
//            }
//
//        }
//
//    }

    public boolean escapeToEndPossibleF(int x, int y, BitSet alreadyVisit,
                                        DependencyHeuristicsNet result) {
        HNSet outputSetX, outputSetY;
        //double max, min, minh;
        boolean escapeToEndPossible;
        int minNum;

        //          [A B]
        // X        [C]     ---> Y
        //          [D B F]

        // build subset h = [A B C D E F] of all elements of outputSetX
        // search for minNum of elements of min subset with X=B as element: [A B] , minNum = 2

        outputSetX = result.getOutputSet(x);
        outputSetY = result.getOutputSet(y);

        HNSubSet h = new HNSubSet();
        minNum = 1000;
        for (int i = 0; i < outputSetX.size(); i++) {
            HNSubSet outputSubSetX = outputSetX.get(i);
            if ((outputSubSetX.contains(y)) && (outputSubSetX.size() < minNum)) {
                minNum = outputSubSetX.size();
            }
            for (int j = 0; j < outputSubSetX.size(); j++) {
                h.add(outputSubSetX.get(j));
            }
        }

        if (alreadyVisit.get(x)) {
            return false;
        } else if (x == y) {
            return false;
        } else if (outputSetY.size() < 0) {
            // y is an eEe element
            return false;
        } else if (h.size() == 0) {
            // x is an eEe element
            return true;
        } else if (h.contains(y) && (minNum == 1)) {
            // x is unique connected with y
            return false;
        } else {
            // iteration over OR-subsets in outputSetX
            for (int i = 0; i < outputSetX.size(); i++) {
                HNSubSet outputSubSetX = outputSetX.get(i);
                escapeToEndPossible = false;
                for (int j = 0; j < outputSubSetX.size(); j++) {
                    int element = outputSubSetX.get(j);
                    BitSet hulpAV = (BitSet) alreadyVisit.clone();
                    hulpAV.set(x);
                    if (escapeToEndPossibleF(element, y, hulpAV, result)) {
                        escapeToEndPossible = true;
                    }

                }
                if (!escapeToEndPossible) {
                    return false;
                }
            }
            return true;
        }
    }

    public HNSet buildOrInputSets(int ownerE, HNSubSet inputSet) {
        HNSet h = new HNSet();
        int currentE;
        // using the welcome method,
        // distribute elements of TreeSet inputSet over the elements of UnifiedSet h
        boolean minimalOneOrWelcome;
        //setE = null;
        //Iterator hI = h.iterator();
        HNSubSet helpTreeSet;
        for (int isetE = 0; isetE < inputSet.size(); isetE++) {
            currentE = inputSet.get(isetE);
            minimalOneOrWelcome = false;
            for (int ihI = 0; ihI < h.size(); ihI++) {
                helpTreeSet = h.get(ihI);
                if (xorInWelcome(ownerE, currentE, helpTreeSet)) {
                    minimalOneOrWelcome = true;
                    helpTreeSet.add(currentE);
                }
            }
            if (!minimalOneOrWelcome) {
                helpTreeSet = new HNSubSet();
                helpTreeSet.add(currentE);
                h.add(helpTreeSet);
            }
        }

        // look to the (A v B) & (B v C) example with B A C in the inputSet;
        // result is [AB] [C]
        // repeat to get [AB] [BC]

        for (int isetE = 0; isetE < inputSet.size(); isetE++) {
            currentE = inputSet.get(isetE);
            for (int ihI = 0; ihI < h.size(); ihI++) {
                helpTreeSet = h.get(ihI);
                if (xorInWelcome(ownerE, currentE, helpTreeSet)) {
                    helpTreeSet.add(currentE);
                }
            }
        }
        return h;
    }

    public HNSet buildOrOutputSets(int ownerE, HNSubSet outputSet) {
        HNSet h = new HNSet();
        int currentE;

        // using the welcome method,
        // distribute elements of TreeSet inputSet over the elements of UnifiedSet h
        boolean minimalOneOrWelcome;
        //setE = null;
        HNSubSet helpTreeSet;
        for (int isetE = 0; isetE < outputSet.size(); isetE++) {
            currentE = outputSet.get(isetE);
            minimalOneOrWelcome = false;
            for (int ihI = 0; ihI < h.size(); ihI++) {
                helpTreeSet = h.get(ihI);
                if (xorOutWelcome(ownerE, currentE, helpTreeSet)) {
                    minimalOneOrWelcome = true;
                    helpTreeSet.add(currentE);
                }
            }
            if (!minimalOneOrWelcome) {
                helpTreeSet = new HNSubSet();
                helpTreeSet.add(currentE);
                h.add(helpTreeSet);
            }
        }

        // look to the (A v B) & (B v C) example with B A C in the inputSet;
        // result is [AB] [C]
        // repeat to get [AB] [BC]
        for (int isetE = 0; isetE < outputSet.size(); isetE++) {
            currentE = outputSet.get(isetE);
            for (int ihI = 0; ihI < h.size(); ihI++) {
                helpTreeSet = h.get(ihI);
                if (xorOutWelcome(ownerE, currentE, helpTreeSet)) {
                    helpTreeSet.add(currentE);
                }
            }
        }

        return h;
    }

    private boolean xorInWelcome(int ownerE, int newE, HNSubSet h) {
        boolean welcome = true;
        int oldE;
        double andValue;

        for (int ihI = 0; ihI < h.size(); ihI++) {
            oldE = h.get(ihI);
            andValue = andInMeasureF(ownerE, oldE, newE);
            if (newE != oldE) {
                andInMeasuresAll.set(newE, oldE, andValue);
            }
            if (andValue > parameters.getAndThreshold()) {
                welcome = false;
            }
        }
        return welcome;
    }

    private boolean xorOutWelcome(int ownerE, int newE, HNSubSet h) {
        boolean welcome = true;
        int oldE;
        double andValue;

        for (int ihI = 0; ihI < h.size(); ihI++) {
            oldE = h.get(ihI);
            andValue = andOutMeasureF(ownerE, oldE, newE);
            if (newE != oldE) {
                andOutMeasuresAll.set(newE, oldE, andValue);
            }
            if (andValue > parameters.getAndThreshold()) {
                welcome = false;
            }
        }
        return welcome;
    }

    private double andInMeasureF(int ownerE, int oldE, int newE) {
        if (ownerE == newE) {
            return 0.0;
        } else if ((directSuccessionCount.get(oldE,
                newE) < parameters.getPositiveObservationsThreshold()) ||
                (directSuccessionCount.get(newE,
                        oldE) < parameters.getPositiveObservationsThreshold())) {
            return 0.0;
        } else {
            return (directSuccessionCount.get(oldE, newE) + directSuccessionCount.get(newE,
                    oldE)) /
                    // relevantInObservations;
                    (directSuccessionCount.get(newE, ownerE) + directSuccessionCount.get(oldE,
                            ownerE) + 1);
        }
    }

    private double andOutMeasureF(int ownerE, int oldE, int newE) {
        if (ownerE == newE) {
            return 0.0;
        } else if ((directSuccessionCount.get(oldE,
                newE) < parameters.getPositiveObservationsThreshold()) ||
                (directSuccessionCount.get(newE,
                        oldE) < parameters.getPositiveObservationsThreshold())) {
            return 0.0;
        } else {
            return (directSuccessionCount.get(oldE, newE) + directSuccessionCount.get(newE,
                    oldE)) /
                    // relevantOutObservations;
                    (directSuccessionCount.get(ownerE, newE) + directSuccessionCount.get(ownerE,
                            oldE) + 1);
        }
    }

    private HeuristicsNet makeHeuristicsRelations(LogReader log) {
        // use causalSuccession      =>
        //     directSuccession      >
        //     succession2Count     ABA
        //     longRangeSuccession   >>>
        //     Starter(s)
        //     Ender(s)

        // make: causalRelations     -->
        //       parallelRelations   ||

        int bestStart = 0;
        int bestEnd = 0;
        int size = events.size();
        double score;

        DependencyHeuristicsNet result = new DependencyHeuristicsNet(events,
                dependencyMeasuresAccepted,
                directSuccessionCount);

        double measure;
        double[] bestInputMeasure = new double[size];
        double[] bestOutputMeasure = new double[size];
        int[] bestInputEvent = new int[size];
        int[] bestOutputEvent = new int[size];
        L1Lrelation = new boolean[size];
        int[] L2Lrelation = new int[size];
        boolean[] alwaysVisited = new boolean[size];

        HNSubSet[] inputSet = new HNSubSet[size];
        HNSubSet[] outputSet = new HNSubSet[size];

        for (int i = 0; i < size; i++) {
            inputSet[i] = new HNSubSet();
            outputSet[i] = new HNSubSet();
            L1Lrelation[i] = false;
            L2Lrelation[i] = -10;
        }

        // stap 1: Look for the best start and end task:
        // ============================================
        for (int i = 0; i < size; i++) {
            if (startCount.get(i) > startCount.get(bestStart)) {
                bestStart = i;
            }
            if (endCount.get(i) > endCount.get(bestEnd)) {
                bestEnd = i;
            }
        }

        //setting the start task
        HNSubSet startTask = new HNSubSet();
        startTask.add(bestStart);
        result.setStartTasks(startTask);
        //setting the end task

        HNSubSet endTask = new HNSubSet();
        endTask.add(bestEnd);
        result.setEndTasks(endTask);

        // update noiseCounters
        noiseCounters.set(bestStart, 0,
                log.getLogSummary().getNumberOfProcessInstances() - startCount.get(bestStart));
        noiseCounters.set(0, bestEnd,
                log.getLogSummary().getNumberOfProcessInstances() - endCount.get(bestEnd));

        if (Math.abs(startCount.get(bestStart) -
                log.getLogSummary().getNumberOfProcessInstances()) >
                parameters.getPositiveObservationsThreshold()) {
        }

        if (Math.abs(endCount.get(bestEnd) - log.getLogSummary().getNumberOfProcessInstances()) >
                parameters.getPositiveObservationsThreshold()) {
        }

        // Stap 2: build dependencyMeasuresAccepted
        // ============================================

        // stap 2.1 L1L loops (remark: L1L loops overrules L2L loops to prevent the EAE pitfall!
        //                     with E in a direct loop)
        for (int i = 0; i < size; i++) {
            measure = calculateL1LDependencyMeasure(i);
            L1LdependencyMeasuresAll.set(i, measure);
            if (measure >= parameters.getL1lThreshold() &&
                    directSuccessionCount.get(i,
                            i) >= parameters.getPositiveObservationsThreshold()) {
                dependencyMeasuresAccepted.set(i, i, measure);
                L1Lrelation[i] = true;
                inputSet[i].add(i);
                outputSet[i].add(i);
            }
        }

        // stap 2.2: L2L loops
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                measure = calculateL2LDependencyMeasure(i, j);
                L2LdependencyMeasuresAll.set(i, j, measure);
                L2LdependencyMeasuresAll.set(j, i, measure);

                if ((i != j) && (measure >= parameters.getL2lThreshold()) &&
                        ((succession2Count.get(i, j) + succession2Count.get(j, i))
                                >= parameters.getPositiveObservationsThreshold())) {
                    dependencyMeasuresAccepted.set(i, j, measure);
                    dependencyMeasuresAccepted.set(j, i, measure);
                    L2Lrelation[i] = j;
                    L2Lrelation[j] = i;
                    inputSet[i].add(j);
                    outputSet[j].add(i);
                    inputSet[j].add(i);
                    outputSet[i].add(j);
                }
            }
        }

        // Stap 2.3: normal dependecy measure
        // Stap 2.3.1: independed of any threshold
        //             search the best input and output connection.
        // stap 2.3.1.1: initialization
        for (int i = 0; i < size; i++) {
            bestInputMeasure[i] = -10.0;
            bestOutputMeasure[i] = -10.0;
            bestInputEvent[i] = -1;
            bestOutputEvent[i] = -1;
        }

        // stap 2.3.1.2: search the beste ones:
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    measure = calculateDependencyMeasure(i, j);
                    ABdependencyMeasuresAll.set(i, j, measure);

                    if (measure > bestOutputMeasure[i]) {
                        bestOutputMeasure[i] = measure;
                        bestOutputEvent[i] = j;
                    }
                    if (measure > bestInputMeasure[j]) {
                        bestInputMeasure[j] = measure;
                        bestInputEvent[j] = i;
                    }
                }
            }
        }
        // Extra check for best compared with L2L-loops
        for (int i = 0; i < size; i++) {
            if ((i != bestStart) && (i != bestEnd)) {
                for (int j = 0; j < size; j++) {
                    measure = calculateL2LDependencyMeasure(i, j);
                    if (measure > bestInputMeasure[i]) {
                        dependencyMeasuresAccepted.set(i, j, measure);
                        dependencyMeasuresAccepted.set(j, i, measure);
                        L2Lrelation[i] = j;
                        L2Lrelation[j] = i;
                        inputSet[i].add(j);
                        outputSet[j].add(i);
                        inputSet[j].add(i);
                        outputSet[i].add(j);
                    }
                }
            }

        }


        // stap 2.3.1.3: update the dependencyMeasuresAccepted matrix,
        //               the inputSet, outputSet arrays and
        //               the noiseCounters matrix
        //
        // extra: if L1Lrelation[i] then process normal
        //        if L2Lrelation[i]=j is a ABA connection then only attach the strongest
        //        input and output connection

        if (parameters.useAllConnectedHeuristics) {
            for (int i = 0; i < size; i++) {
                int j = L2Lrelation[i];
                if (i != bestStart) {
                    if (!((j > -1) && (bestInputMeasure[j] > bestInputMeasure[i]))) {
                        dependencyMeasuresAccepted.set(bestInputEvent[i], i, bestInputMeasure[i]);
                        inputSet[i].add(bestInputEvent[i]);
                        outputSet[bestInputEvent[i]].add(i);
                        noiseCounters.set(bestInputEvent[i], i, directSuccessionCount.get(i,
                                bestInputEvent[i]));
                    }
                }
                if (i != bestEnd) {
                    if (!((j > -1) && (bestOutputMeasure[j] > bestOutputMeasure[i]))) {
                        dependencyMeasuresAccepted.set(i, bestOutputEvent[i],
                                bestOutputMeasure[i]);
                        inputSet[bestOutputEvent[i]].add(i);
                        outputSet[i].add(bestOutputEvent[i]);
                        noiseCounters.set(i, bestOutputEvent[i],
                                directSuccessionCount.get(bestOutputEvent[i], i));
                    }
                }


            }
        }

        // Stap 2.3.2: search for other connections that fulfill all the thresholds:

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (dependencyMeasuresAccepted.get(i, j) <= 0.0001) {
                    measure = calculateDependencyMeasure(i, j);
                    if (((bestOutputMeasure[i] - measure) <=
                            parameters.getRelativeToBestThreshold()) &&
                            (directSuccessionCount.get(i,
                                    j) >= parameters.getPositiveObservationsThreshold()) &&
                            (measure >= parameters.getDependencyThreshold())) {
                        dependencyMeasuresAccepted.set(i, j, measure);
                        inputSet[j].add(i);
                        outputSet[i].add(j);
                        noiseCounters.set(i, j, directSuccessionCount.get(j, i));
                    }
                }
            }
        }

        //Stap 3: Given the InputSets and OutputSets build
        //        OR-subsets;

        // AndOrAnalysis andOrAnalysis = new AndOrAnalysis();
        // double AverageRelevantInObservations = 0.0;
        // double AverageRelevantOutObservations = 0.0;
        // double sumIn, sumOut;

        // depending on the current event i, calculate the numeber of
        // relevant In and Out observations
        // NOT IN USE !!!


        for (int i = 0; i < size; i++) {
            result.setInputSet(i, buildOrInputSets(i, inputSet[i]));
            result.setOutputSet(i, buildOrOutputSets(i, outputSet[i]));
        }

        // Update the HeuristicsNet with non binairy dependecy relations:

        // Search for always visited activities:

        if (parameters.useLongDistanceDependency) {
            alwaysVisited[bestStart] = false;
            for (int i = 1; i < size; i++) {
                BitSet h = new BitSet();
                alwaysVisited[i] = !escapeToEndPossibleF(bestStart, i, h, result);
            }
        }

        if (parameters.useLongDistanceDependency) {
            for (int i = (size - 1); i >= 0; i--) {
                for (int j = (size - 1); j >= 0; j--) {
                    if ((i == j) || (alwaysVisited[j] && (j != bestEnd))) {
                        continue;
                    }
                    score = calculateLongDistanceDependencyMeasure(i, j);
                    if (score > parameters.getLDThreshold()) {
                        BitSet h = new BitSet();
                        if (escapeToEndPossibleF(i, j, h, result)) {
                            // HNlongRangeFollowingChance.set(i, j, hnc);
                            dependencyMeasuresAccepted.set(i, j, score);

                            // update heuristicsNet
                            HNSubSet helpSubSet = new HNSubSet();
                            HNSet helpSet;

                            helpSubSet.add(j);
                            helpSet = result.getOutputSet(i);
                            helpSet.add(helpSubSet);
                            result.setOutputSet(i, helpSet);

                            helpSubSet = new HNSubSet();

                            helpSubSet.add(i);
                            helpSet = result.getInputSet(j);
                            helpSet.add(helpSubSet);
                            result.setInputSet(j, helpSet);
                        }
                    }
                }
            }
        }

        int numberOfConnections = 0;

        for (int i = 0; i < dependencyMeasuresAccepted.rows(); i++) {
            for (int j = 0; j < dependencyMeasuresAccepted.columns(); j++) {
                if (dependencyMeasuresAccepted.get(i, j) > 0.01) {
                    numberOfConnections = numberOfConnections + 1;
                }
            }
        }

        int noiseTotal = 0;

        for (int i = 0; i < noiseCounters.rows(); i++) {
            for (int j = 0; j < noiseCounters.columns(); j++) {
                noiseTotal = noiseTotal + (int) noiseCounters.get(i, j);
            }
        }

        if (parameters.extraInfo) {
            showExtraInfo();
        }

        // parse the log to get extra parse information:
        // (i)  fitness
        // (ii) the number of times a connection is used

        HeuristicsNet[] population = new HeuristicsNet[1];
        population[0] = result;

        DTContinuousSemanticsFitness fitness1 = new DTContinuousSemanticsFitness(
                log);
        fitness1.calculate(population);

        DTImprovedContinuousSemanticsFitness fitness2 = new DTImprovedContinuousSemanticsFitness(
                log);
        fitness2.calculate(population);

        return population[0];

    }

    private HeuristicsNet removeUnusedElements(HeuristicsNet population) {
        population.disconnectUnusedElements();
        return population;
    }

}