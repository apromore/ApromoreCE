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

import com.raffaeleconforti.alphadollar.alphamminer.AlphaMMiner;
import com.raffaeleconforti.log.util.LogReaderClassic;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.mining.logabstraction.*;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 22/04/2016.
 */
public class HeuristicsDollarMiner {

    public PetriNet mine(LogReaderClassic logReader) {
        if (logReader != null) {
            // Mine the log for a Petri net.
            AlphaMMiner miningPlugin = new AlphaMMiner();

            LogRelations logRelations = getLogRelations(logReader);
            logRelations = HeuristicsRelations.simplifyRelations(logReader, logRelations);

            return miningPlugin.mine(logReader, logRelations, true);
        } else {
            System.err.println("No log reader could be constructed.");
            return null;
        }
    }



    public LogRelations getLogRelations(LogReaderClassic log) {
        LogAbstraction logAbstraction = new LogAbstractionImpl(log, true);
        LogRelations relations = (new MinValueLogRelationBuilder(logAbstraction, 0,
                log.getLogSummary().getLogEvents())).getLogRelations();
        String[][] intervals = new String[0][0];

        // Third layer: Use Finite State Machine to insert causality
        if (true) {
            relations = (new FSMLogRelationBuilder(relations)).getLogRelations();
        }

        for (int i = 0; i < intervals.length; i++) {
            relations = (new TimeIntervalLogRelationBuilder(relations, log, intervals[i][0],
                    intervals[i][1])).getLogRelations();
        }

        return relations;
    }

}
