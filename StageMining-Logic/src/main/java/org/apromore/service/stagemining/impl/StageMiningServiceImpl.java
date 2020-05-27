/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Bruce Nguyen, Queensland University of Technology.
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

package org.apromore.service.stagemining.impl;

import org.apromore.service.stagemining.StageMiningService;
import org.deckfour.xes.model.XLog;
import org.processmining.stagemining.algorithms.AbstractStageMining;
import org.processmining.stagemining.algorithms.StageMiningHighestModularity;
import org.processmining.stagemining.models.DecompositionTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 */
@Service
public class StageMiningServiceImpl implements StageMiningService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StageMiningServiceImpl.class);

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     *
     */
    public StageMiningServiceImpl() {}

    @Override
    public DecompositionTree mine(XLog log, int minStageSize) throws Exception  {
        AbstractStageMining miner = new StageMiningHighestModularity();
        miner.setDebug(true);
        long startTime = System.currentTimeMillis();
        DecompositionTree tree = miner.mine(log, minStageSize);
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total Time: " + totalTime + " milliseconds");
        System.out.println("Finish phase mining");
        return tree;
    }

}
