/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Bruce Nguyen.
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apromore.plugin.portal.perfmining;

import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.joda.time.DateTime;
import org.apromore.service.perfmining.models.SPF;
import org.apromore.service.perfmining.models.Stage;

/**
 *
 * @author Administrator
 */
public class DatasetFactory {
    public static XYDataset createCFDDataset(SPF bpf) {
        TimeTableXYDataset dataset = new TimeTableXYDataset();

        for (int i = bpf.getStages().size() - 1; i >= 0; i--) {
            Stage stage = bpf.getStages().get(i);

            if (i == bpf.getStages().size() - 1) {
                for (int j = 0; j < bpf.getTimeSeries().size(); j++) {
                    TimePeriod timeP = getTimePeriod(bpf, bpf.getTimeSeries().get(j));
                    dataset.add(timeP, stage.getServicePassedCounts().get(j).doubleValue(), stage.getName() + "-Complete");
                }
            }

            //Exit band
            for (int j = 0; j < bpf.getTimeSeries().size(); j++) {
                TimePeriod timeP = getTimePeriod(bpf, bpf.getTimeSeries().get(j));
                dataset.add(timeP, stage.getServiceDepartureCounts().get(j) - stage.getServicePassedCounts().get(j),
                                stage.getName() + "-Exit");
                //System.out.println("Exit " + j + ": " + (stage.getServiceDepartureCounts().get(j) - stage.getServicePassedCounts().get(j)));
            }

            //Service band
            for (int j = 0; j <  bpf.getTimeSeries().size(); j++) {
                TimePeriod timeP = getTimePeriod(bpf, bpf.getTimeSeries().get(j));
                dataset.add(timeP, stage.getServiceArrivalCounts().get(j) - stage.getServiceDepartureCounts().get(j),
                                stage.getName() + "-Service");
            }

            //Queue band
        for (int j = 0; j < bpf.getTimeSeries().size(); j++) {
                TimePeriod timeP = getTimePeriod(bpf, bpf.getTimeSeries().get(j));
                dataset.add(timeP, stage.getQueueArrivalCounts().get(j) - stage.getServiceArrivalCounts().get(j),
                                stage.getName() + "-Queue");
            }

        }

        return dataset;
    }
    
    private static TimePeriod getTimePeriod(SPF bpf, DateTime timePoint) {
        if (bpf.getConfig().getTimeStep() <= 3600) {
            return new Hour(timePoint.toDate());
        } else {
            return new Day(timePoint.toDate());
        }
    }
}
