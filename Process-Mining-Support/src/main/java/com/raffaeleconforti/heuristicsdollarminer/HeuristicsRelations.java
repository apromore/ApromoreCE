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

package com.raffaeleconforti.heuristicsdollarminer;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import org.processmining.framework.log.*;
import org.processmining.framework.models.heuristics.HNSubSet;
import org.processmining.mining.logabstraction.LogAbstraction;
import org.processmining.mining.logabstraction.LogAbstractionImpl;
import org.processmining.mining.logabstraction.LogRelations;

import java.io.IOException;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 22/04/2016.
 */
public class HeuristicsRelations {

    private static LogEvents events;
    private static DoubleMatrix2D longRangeSuccessionCount; // calculated in makeBasicRelations
//    private DoubleMatrix2D longRangeDependencyMeasures;

    public static LogRelations simplifyRelations(LogReader logReader, LogRelations logRelations) {
        LogAbstraction logAbstraction = new LogAbstractionImpl(logReader, true);

        try {
            DoubleMatrix2D parallel = logRelations.getParallelMatrix();
            DoubleMatrix2D causalFollower = logRelations.getCausalFollowerMatrix();
            DoubleMatrix2D directFollow = logAbstraction.getFollowerInfo(1);

            makeBasicRelations(logReader, directFollow, 0.8);

            for(int i = 0; i < logRelations.getNumberElements(); i++) {
                for(int j = i + 1; j < logRelations.getNumberElements(); j++) {
                    if(parallel.get(i, j) > 0) {
                        double ij = directFollow.get(i, j);
                        double ji = directFollow.get(j, i);

                        double num = ij - ji;
                        double den = ij + ji + 1;

                        double val = Math.abs(num) / den;

                        if(val > 0.7) {
                            System.out.println("Changing");
                            parallel.set(i, j, 0);
                            parallel.set(j, i, 0);
                            if(num > 0) {
                                causalFollower.set(i, j, 1);
                            }else {
                                causalFollower.set(j, i, 1);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logRelations;
    }

    private static void makeBasicRelations(LogReader log, DoubleMatrix2D causalSuccession, double causalityFall) {
        events = log.getLogSummary().getLogEvents();
        longRangeSuccessionCount = DoubleFactory2D.dense.make(events.size(), events.size(), 0);

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
//                    System.out.println("Change from " + causalSuccession.get(row,
//                            column) + " to " + causalSuccession.get(row,
//                            column) + Math.pow(causalityFall, distance - 1));
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
    }

}
