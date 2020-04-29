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


package org.apromore.logfilter.criteria.factory.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.Section;
import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.apromore.logfilter.criteria.factory.LogFilterCriterionFactory;
import org.apromore.logfilter.criteria.impl.LogFilterCriterionAttribute;
import org.apromore.logfilter.criteria.impl.LogFilterCriterionCaseId;
import org.apromore.logfilter.criteria.impl.LogFilterCriterionCaseUtilization;
import org.apromore.logfilter.criteria.impl.LogFilterCriterionCaseVariant;
import org.apromore.logfilter.criteria.impl.LogFilterCriterionDirectFollow;
import org.apromore.logfilter.criteria.impl.LogFilterCriterionDurationAverageProcessing;
import org.apromore.logfilter.criteria.impl.LogFilterCriterionDurationAverageWaiting;
import org.apromore.logfilter.criteria.impl.LogFilterCriterionDurationMaxProcessing;
import org.apromore.logfilter.criteria.impl.LogFilterCriterionDurationMaxWaiting;
import org.apromore.logfilter.criteria.impl.LogFilterCriterionDurationRange;
import org.apromore.logfilter.criteria.impl.LogFilterCriterionDurationTotalProcessing;
import org.apromore.logfilter.criteria.impl.LogFilterCriterionDurationTotalWaiting;
import org.apromore.logfilter.criteria.impl.LogFilterCriterionEndRange;
import org.apromore.logfilter.criteria.impl.LogFilterCriterionEventuallyFollow;
import org.apromore.logfilter.criteria.impl.LogFilterCriterionRework;
import org.apromore.logfilter.criteria.impl.LogFilterCriterionStartRange;
import org.apromore.logfilter.criteria.model.Action;
import org.apromore.logfilter.criteria.model.Containment;
import org.apromore.logfilter.criteria.model.Level;
import org.apromore.logfilter.criteria.model.LogFilterTypeSelector;

/**
 * @author Bruce Hoang Nguyen (11/07/2019)
 * Modified: Chii Chang (31/12/2019)
 */
public class LogFilterCriterionFactoryImpl implements LogFilterCriterionFactory {

	@Override
    public LogFilterCriterion getLogFilterCriterion(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        switch (LogFilterTypeSelector.getType(attribute)) {
            case DIRECT_FOLLOW:
                return new LogFilterCriterionDirectFollow(action, containment, level, label, attribute, value);
            case EVENTUAL_FOLLOW:
                return new LogFilterCriterionEventuallyFollow(action, containment, level, label, attribute, value);
//            case TIME_DURATION:
//                return new LogFilterCriterionDuration(action, containment, level, label, attribute, value);
            case DURATION_RANGE:
                return new LogFilterCriterionDurationRange(action, containment, level, label, attribute, value);
            case CASE_VARIANT:
                return new LogFilterCriterionCaseVariant(action, containment, level, label, attribute, value);
            case TIME_STARTRANGE:
                return new LogFilterCriterionStartRange(action, containment, level, label, attribute, value);
            case TIME_ENDRANGE:
                return new LogFilterCriterionEndRange(action, containment, level, label, attribute, value);
            case CASE_ID: //2019-09-25
                return new LogFilterCriterionCaseId(action, containment, level, label, attribute, value);
            case DURATION_TOTAL_PROCESSING:
                return new LogFilterCriterionDurationTotalProcessing(action, containment, level, label, attribute, value);
            case DURATION_AVERAGE_PROCESSING:
                return new LogFilterCriterionDurationAverageProcessing(action, containment, level, label, attribute, value);
            case DURATION_MAX_PROCESSING:
                return new LogFilterCriterionDurationMaxProcessing(action, containment, level, label, attribute, value);
            case DURATION_TOTAL_WAITING:
                return new LogFilterCriterionDurationTotalWaiting(action, containment, level, label, attribute, value);
            case DURATION_AVERAGE_WAITING:
                return new LogFilterCriterionDurationAverageWaiting(action, containment, level, label, attribute, value);
            case DURATION_MAX_WAITING:
                return new LogFilterCriterionDurationMaxWaiting(action, containment, level, label, attribute, value);
            case CASE_UTILIZATION:
                return new LogFilterCriterionCaseUtilization(action, containment, level, label, attribute, value);
            case REWORK_REPETITION:
                return new LogFilterCriterionRework(action, containment, level, label, attribute, value);
            default:
                if (label.equals("rework:repetition")) {
                    return new LogFilterCriterionRework(action, containment, level, label, attribute, value);
                } else {
                    return new LogFilterCriterionAttribute(action, containment, level, label, attribute, value);
                }
        }
    }
    
	@Override
    public LogFilterCriterion copyFilterCriterion(LogFilterCriterion criterion) {
    	return this.getLogFilterCriterion(criterion.getAction(), 
    															criterion.getContainment(), 
    															criterion.getLevel(), 
    															criterion.getLabel(), 
    															criterion.getAttribute(), 
    															new HashSet<>(criterion.getValue()));
    }
    
	@Override
    public List<LogFilterCriterion> copyFilterCriterionList(List<LogFilterCriterion> list) {
    	List<LogFilterCriterion> newList = new ArrayList<>();
    	for (LogFilterCriterion c : list) {
    		newList.add(this.copyFilterCriterion(c));
    	}
    	return newList;
    }
	
    @Override
    public LogFilterCriterion convertFilterCriteria(LogFilterRule rule) {
        return this.getLogFilterCriterion(
                rule.getChoice() == Choice.REMOVE ? Action.REMOVE : Action.RETAIN, 
                rule.getInclusion() == Inclusion.ANY_VALUE ? Containment.CONTAIN_ANY : Containment.CONTAIN_ALL, 
                rule.getSection() == Section.CASE ? Level.TRACE : Level.EVENT, 
                "", 
                rule.getKey(), 
                rule.getPrimaryValuesInString());
    }   	
	
	@Override
    public List<LogFilterCriterion> convertFilterCriteria(List<LogFilterRule> list) {
	    List<LogFilterCriterion> criteria = new ArrayList<>();
	    for (LogFilterRule rule : list) {
	    	criteria.add(this.convertFilterCriteria(rule));
	    }
	    return criteria;
	}
	
}
