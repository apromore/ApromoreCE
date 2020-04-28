/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.service.compare.impl;

import ee.ut.eventstr.comparison.*;
import ee.ut.eventstr.comparison.differences.Differences;
import ee.ut.eventstr.comparison.differences.DifferencesML;
import ee.ut.eventstr.comparison.differences.ModelAbstractions;
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.apromore.service.compare.CompareService;

import java.util.HashSet;
import java.util.Set;

@Service
public class CompareServiceImpl implements CompareService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompareServiceImpl.class);

    // Non-graphical verbalizer
    // @Override
    // public Set<String> discoverBPMNModel(PetriNet net, XLog log, HashSet<String> obs) throws Exception {
    //      ApromoreCompareML comparator = new ApromoreCompareML();
    //      return comparator.getDifferences(net, log, obs);
    // }

    @Override
    public DifferencesML discoverBPMNModel(ModelAbstractions model, XLog log, HashSet<String> obs) throws Exception {
        ApromoreCompareML comparator = new ApromoreCompareML();
        DiffMLGraphicalVerbalizerNew verbalizer = comparator.analyzeDifferences(model, log, obs);
        verbalizer.verbalize();
        return verbalizer.getDifferences();
    }

//    @Override
//    public Set<String> discoverModelModel(PetriNet net1, PetriNet net2, HashSet<String> silent1, HashSet<String> silent2) throws Exception{
//        ApromoreCompareMM comparator = new ApromoreCompareMM();
//        return comparator.getDifferences(net1, net2, silent1, silent2);
//    }

    @Override
    public Differences discoverModelModelAbs(ModelAbstractions model1, ModelAbstractions model2, HashSet<String> silent1, HashSet<String> silent2) throws Exception{
        ApromoreCompareMM comparator = new ApromoreCompareMM();
        DiffMMGraphicalVerbalizer verbalizer = comparator.analyzeDifferences(model1, model2, new HashSet<String>(model1.getReader().getTaskLabels()), new HashSet<String>(model2.getReader().getTaskLabels()));
        // model1.getReader().getTaskLabels(), model2.getReader().getTaskLabels());
        verbalizer.verbalize();
        return verbalizer.getDifferences();
//        return comparator.getDifferences(model1, net2, silent1, silent2);
    }

    @Override
    public Set<String> discoverLogLog(XLog log1, XLog log2) throws Exception{
        ApromoreCompareLL compare = new ApromoreCompareLL();
        return compare.getDifferences(log1, log2);
    }
}