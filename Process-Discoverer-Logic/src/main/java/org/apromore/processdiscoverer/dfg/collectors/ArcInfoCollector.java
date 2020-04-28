/*-
 * #%L
 * This file is part of "Apromore Community".
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

package org.apromore.processdiscoverer.dfg.collectors;

import org.apromore.processdiscoverer.VisualizationAggregation;
import org.apromore.processdiscoverer.VisualizationType;
import org.apromore.processdiscoverer.dfg.Arc;
import org.apromore.processdiscoverer.dfg.LogDFG;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectDoubleHashMap;

import static org.apromore.processdiscoverer.VisualizationType.DURATION;
import static org.apromore.processdiscoverer.VisualizationType.FREQUENCY;

import java.util.Map;
import java.util.Set;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 15/07/2018.
 */
public class ArcInfoCollector {
    private final Map<Arc, LongArrayList> arcs_frequency_set; //map from an Arc to a list of frequencies, each frequency for one trace
    private final Map<Arc, LongArrayList> arcs_duration_set; //map from an Arc to a list of duration, each duration is an occurrence of arc (could be multiple times in a trace).
    //private final Map<Arc, DoubleArrayList> arcs_impact_set;
    //private final Map<Arc, LongArrayList> tmp_arcs_impact_set;

    private final Calculator calculator;
    private final int number_of_traces;
    private int trace = 0; //current trace
    
    public ArcInfoCollector(LogDFG logDfg) {
        this.number_of_traces = logDfg.getSimplifiedLog().size();

        this.arcs_frequency_set = new UnifiedMap<>();
        this.arcs_duration_set = new UnifiedMap<>();
        //this.arcs_impact_set = new UnifiedMap<>();
        //this.tmp_arcs_impact_set = new UnifiedMap<>();

        this.calculator = new Calculator();
        calculator.setCurrentDate(Long.toString(System.currentTimeMillis()));
    }

    public ObjectDoubleHashMap<Arc> getArcsFrequencyMap(VisualizationType type, VisualizationAggregation aggregation) {
        ObjectDoubleHashMap<Arc> map = new ObjectDoubleHashMap<Arc>();
        for(Arc arc : arcs_frequency_set.keySet()) {
            map.put(arc, getArcInfo(arc, type, aggregation));
        }
        return map;
    }

    // Add up frequency to the current frequency value of Arc
    // This method must be used in coordination with the nextTrace() method.
    public void updateArcFrequency(Arc arc, int frequency) {
        LongArrayList list = FrequencySetPopulator.retreiveEntryLong(arcs_frequency_set, arc, number_of_traces);
        calculator.increment(calculator.getCurrentDate(), list.get(trace), frequency);
        list.set(trace, calculator.getCurrent());  // var4 = list.get(trace) + frequency
    }
    
    public LongArrayList getArcMeasurePopulation(Arc arc, VisualizationType type) {
    	if (type == VisualizationType.FREQUENCY) {
    		return arcs_frequency_set.get(arc);
    	}
    	else if (type == VisualizationType.DURATION) {
    		return arcs_duration_set.get(arc);
    	}
    	else {
    		return null;
    	}
    }

    // add duration to an arc. 
    // Unlike arc frequency, a duration is for one occurrence of an arc, not for a trace
    public void updateArcDuration(Arc arc, long duration) {
        LongArrayList durations = arcs_duration_set.get(arc);
        if(durations == null) {
            durations = new LongArrayList();
            arcs_duration_set.put(arc, durations);
        }
        durations.add(duration);
    }

//    public void updateArcImpact(Arc arc, long duration) {
//        LongArrayList impacts = tmp_arcs_impact_set.get(arc);
//        if(impacts == null) {
//            impacts = new LongArrayList();
//            tmp_arcs_impact_set.put(arc, impacts);
//        }
//        impacts.add(duration);
//    }

//    public void consolidateArcImpact(Arc arc, long total_duration) {
//        DoubleArrayList list = FrequencySetPopulator.retreiveEntryDouble(arcs_impact_set, arc, number_of_traces);
//        LongArrayList impacts = tmp_arcs_impact_set.get(arc);
//        double impact = (double) impacts.sum() / ((double) total_duration);
//        list.set(trace, list.get(trace) + impact);
//    }

    public boolean exists(Arc arc) {
        return arcs_frequency_set.get(arc) != null;
    }
    
    public Set<Arc> getArcs() {
    	return arcs_frequency_set.keySet();
    }

    public double getArcInfo(Arc arc, VisualizationType type, VisualizationAggregation aggregation) {
        if(type == FREQUENCY) {
            return FrequencySetPopulator.getAggregateInformation(arcs_frequency_set.get(arc), aggregation);
        }else if(type == DURATION){
            return FrequencySetPopulator.getAggregateInformation(arcs_duration_set.get(arc), aggregation);
        }
        return 0;
    }

    public void nextTrace() {
        //tmp_arcs_impact_set.clear();
        calculator.increment(calculator.getCurrentDate(), trace, 1); 
        trace = (int) calculator.getCurrent(); //method6 returns var4 = trace + 1
    }
    
    public void clear() {
    	arcs_frequency_set.clear();
    	arcs_duration_set.clear();
    }
}
