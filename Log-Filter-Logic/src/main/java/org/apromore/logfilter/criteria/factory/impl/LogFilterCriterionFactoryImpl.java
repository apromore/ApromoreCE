/*-
 * #%L
 * This file is part of "Apromore Community".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.LogFilterRuleImpl;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
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
import org.apromore.logfilter.criteria.impl.util.TimeUtil;
import org.apromore.logfilter.criteria.model.Action;
import org.apromore.logfilter.criteria.model.Containment;
import org.apromore.logfilter.criteria.model.Level;
import org.apromore.logfilter.criteria.model.LogFilterTypeSelector;
import org.apromore.logfilter.criteria.model.Type;

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
    public LogFilterCriterion convertFilterRuleToFilterCriterion(LogFilterRule rule) {
        return this.getLogFilterCriterion(
                rule.getChoice() == Choice.REMOVE ? Action.REMOVE : Action.RETAIN, 
                rule.getInclusion() == Inclusion.ANY_VALUE ? Containment.CONTAIN_ANY : Containment.CONTAIN_ALL, 
                rule.getSection() == Section.CASE ? Level.TRACE : Level.EVENT,
                rule.getKey(),
                getCriterionAttribute(rule), 
                getCriterionValueSet(rule));
    }
    
    private String getCriterionAttribute(LogFilterRule rule) {
    	if (rule.getFilterType()==FilterType.DIRECT_FOLLOW) {
    		return "direct:follow"; 
    	}
    	else if (rule.getFilterType()==FilterType.EVENTUAL_FOLLOW) {
    		return "eventually:follow";
    	}
    	else if (rule.getFilterType()==FilterType.CASE_VARIANT) {
    		return "case:variant";
    	}
    	else if (rule.getFilterType()==FilterType.CASE_TIME) {
    		return "time:timestamp";
    	}
    	else if (rule.getFilterType()==FilterType.EVENT_TIME) {
    		return "time:timestamp";
    	}
    	else {
    		return rule.getKey();
    	}
    }

    
    private Set<String> getCriterionValueSet(LogFilterRule rule) {
    	Set<String> valueSet = new HashSet<>();
    	Set<String> fromSet = new HashSet<>();
    	Set<String> toSet = new HashSet<>();
    	if (rule.getFilterType()==FilterType.DIRECT_FOLLOW || rule.getFilterType()==FilterType.EVENTUAL_FOLLOW) {
    		for (RuleValue ruleValue : rule.getPrimaryValues()) {
    			if (ruleValue.getOperationType() == OperationType.FROM) {
    				fromSet.add(ruleValue.getStringValue());
    			}
    			else if (ruleValue.getOperationType() == OperationType.TO) {
    				toSet.add(ruleValue.getStringValue());
    			}
    		}
    		for (String from : fromSet) {
    			for (String to : toSet) {
    				valueSet.add(from + " => " + to);
    			}
    		}
    	}
    	else if (rule.getFilterType()==FilterType.CASE_TIME || rule.getFilterType()==FilterType.EVENT_TIME) {
    		for (RuleValue ruleValue : rule.getPrimaryValues()) {
    			if (ruleValue.getOperationType() == OperationType.GREATER_EQUAL) {
    				valueSet.add(">" + ruleValue.getLongValue());
    			}
    			else if (ruleValue.getOperationType() == OperationType.LESS_EQUAL) {
    				valueSet.add("<" + ruleValue.getLongValue());
    			}
    		}
    	}
    	else if (rule.getFilterType()==FilterType.DURATION) {
    		for (RuleValue ruleValue : rule.getPrimaryValues()) {
    			if (ruleValue.getOperationType() == OperationType.GREATER_EQUAL) {
    				valueSet.add(">" + TimeUtil.valueToUnit(ruleValue.getLongValue()));
    			}
    			else if (ruleValue.getOperationType() == OperationType.LESS_EQUAL) {
    				valueSet.add("<" + TimeUtil.valueToUnit(ruleValue.getLongValue()));
    			}
    		}
    	}
    	else {
    		for (RuleValue ruleValue : rule.getPrimaryValues()) {
    			valueSet.add(ruleValue.getStringValue());
    		}
    	}
    	
    	return valueSet;
    }
	
	@Override
    public List<LogFilterCriterion> convertFilterRulesToFilterCriteria(List<LogFilterRule> list) {
	    List<LogFilterCriterion> criteria = new ArrayList<>();
	    for (LogFilterRule rule : list) {
	    	criteria.add(this.convertFilterRuleToFilterCriterion(rule));
	    }
	    return criteria;
	}
	
	@Override
    public LogFilterRule convertFilterCriterionToFilterRule(LogFilterCriterion criterion) {
		return new LogFilterRuleImpl(
				criterion.getAction()==Action.RETAIN ? Choice.RETAIN : Choice.REMOVE, 
				criterion.getContainment()==Containment.CONTAIN_ANY ? Inclusion.ANY_VALUE : Inclusion.ALL_VALUES, 
				criterion.getLevel()==Level.TRACE ? Section.CASE : Section.EVENT, 
				getFilterType(criterion), 
				getKey(criterion), 
				getRuleValueSet(criterion.getValue(), criterion.getAttribute(), getFilterType(criterion), criterion.getLabel()), 
				new HashSet<>());
    	
    }
	
	private String getKey(LogFilterCriterion criterion) {
		switch (LogFilterTypeSelector.getType(criterion.getAttribute())) {
	        case DIRECT_FOLLOW:
	            return criterion.getLabel();
	        case EVENTUAL_FOLLOW:
	            return criterion.getLabel();
	        case DURATION_RANGE:
	        	return "duration:range";
	        case TIME_TIMESTAMP:
	        	if (criterion.getLevel() == Level.TRACE) {
	        		return "case:time";
	        	}
	        	else {
	        		return "event:time";
	        	}
	        case CASE_VARIANT:
	        	return "case:variant";
	        default:
	        	return criterion.getAttribute();
		}
	}
	
	private Set<RuleValue> getRuleValueSet(Set<String> values, String attributeKey, FilterType filterType, String mainAttribute) {
		Set<RuleValue> ruleSet = new HashSet<>();
		for (String value : values) {
			ruleSet.addAll(getRuleValues(value, attributeKey, filterType, mainAttribute));
		}
		return ruleSet;
	}
	
	private FilterType getFilterType(LogFilterCriterion criterion) {
		switch (LogFilterTypeSelector.getType(criterion.getAttribute())) {
	        case DIRECT_FOLLOW:
	            return FilterType.DIRECT_FOLLOW;
	        case EVENTUAL_FOLLOW:
	            return FilterType.EVENTUAL_FOLLOW;
	        case DURATION_RANGE:
	        	return FilterType.DURATION;
	        case TIME_TIMESTAMP:
	        	if (criterion.getLevel() == Level.TRACE) {
	        		return FilterType.CASE_TIME;
	        	}
	        	else {
	        		return FilterType.EVENT_TIME;
	        	}
	        case CASE_VARIANT:
	        	return FilterType.CASE_VARIANT;
	        default:
	        	if (criterion.getLevel() == Level.TRACE) {
	        		return FilterType.CASE_EVENT_ATTRIBUTE;
	        	}
	        	else {
	        		return FilterType.EVENT_EVENT_ATTRIBUTE;
	        	}
		}
	}
	
	private Set<RuleValue> getRuleValues(String value, String attributeKey, FilterType filterType, String mainAttribute) {
		switch (LogFilterTypeSelector.getType(attributeKey)) {
        case DIRECT_FOLLOW:
            return getDirectFollowRuleValue(value, mainAttribute);
        case EVENTUAL_FOLLOW:
            return getEventualFollowRuleValue(value, mainAttribute);
        case DURATION_RANGE:
        	return getDurationRangeRuleValue(value, "duration:range", filterType);
        case TIME_TIMESTAMP:
        	if (filterType == FilterType.CASE_TIME) {
        		return getTimestampAttributeRuleValue(value, "case:timeframe", filterType);
        	}
        	else if (filterType == FilterType.EVENT_TIME) {
        		return getTimestampAttributeRuleValue(value, "event:timeframe", filterType);
        	}
        case CASE_VARIANT:
        	return getEventAttributeRuleValue(value, "case:variant", filterType);
        default:
        	return getEventAttributeRuleValue(value, attributeKey, filterType);
		}
	}
	
    private Set<RuleValue> getEventAttributeRuleValue(String value, String attributeKey, FilterType filterType) {
        Set<RuleValue> ruleValues = new HashSet<>();
        ruleValues.add(new RuleValue(filterType, OperationType.EQUAL, attributeKey, value));
        return ruleValues;
    }
    
    private Set<RuleValue> getTimestampAttributeRuleValue(String value, String attributeKey, FilterType filterType) {
    	Set<RuleValue> ruleValues = new HashSet<>();
    	if (value.startsWith(">")) {
    		ruleValues.add(new RuleValue(filterType, OperationType.GREATER_EQUAL, attributeKey, 
    				Long.parseLong(value.substring(1))));
    	}
    	else if (value.startsWith("<")) {
    		ruleValues.add(new RuleValue(filterType, OperationType.LESS_EQUAL, attributeKey, 
    				Long.parseLong(value.substring(1))));
    	}
        return ruleValues;
    }
    
    private Set<RuleValue> getDurationRangeRuleValue(String value, String attributeKey, FilterType filterType) {
    	Set<RuleValue> ruleValues = new HashSet<>();
    	long duration = LogFilterCriterionDurationRange.getDurationValue(value);
    	if (value.startsWith(">")) {
    		ruleValues.add(new RuleValue(filterType, OperationType.GREATER_EQUAL, attributeKey, duration));
    	}
    	else if (value.startsWith("<")) {
    		ruleValues.add(new RuleValue(filterType, OperationType.LESS_EQUAL, attributeKey, duration));
    	}
        return ruleValues;
    }
    
    private Set<RuleValue> getDirectFollowRuleValue(String value, String attributeKey) {
        String[] edgeParts = value.split(" => ");
        RuleValue from = new RuleValue(FilterType.DIRECT_FOLLOW, OperationType.FROM, attributeKey, edgeParts[0]);
        RuleValue to = new RuleValue(FilterType.DIRECT_FOLLOW, OperationType.TO, attributeKey, edgeParts[1]);
        return new HashSet<RuleValue>(Arrays.asList(new RuleValue[] {from,to}));
    }
    
    private Set<RuleValue> getEventualFollowRuleValue(String value, String attributeKey) {
        String[] edgeParts = value.split(" => ");
        RuleValue from = new RuleValue(FilterType.EVENTUAL_FOLLOW, OperationType.FROM, attributeKey, edgeParts[0]);
        RuleValue to = new RuleValue(FilterType.EVENTUAL_FOLLOW, OperationType.TO, attributeKey, edgeParts[1]);
        return new HashSet<RuleValue>(Arrays.asList(new RuleValue[] {from,to}));
    }
    
	@Override
    public List<LogFilterRule> convertFilterCriteriaToFilterRules(List<LogFilterCriterion> criteria) {
		List<LogFilterRule> rules = new ArrayList<>();
	    for (LogFilterCriterion c : criteria) {
	    	rules.add(this.convertFilterCriterionToFilterRule(c));
	    }
	    return rules;
    }
	
    private BigDecimal unitStringToBigDecimal(String s) {
        if(s.equals("Years")) return new BigDecimal("31536000000");
        if(s.equals("Months")) return new BigDecimal("2678400000");
        if(s.equals("Weeks")) return new BigDecimal("604800000");
        if(s.equals("Days")) return new BigDecimal("86400000");
        if(s.equals("Hours")) return new BigDecimal("3600000");
        if(s.equals("Minutes")) return new BigDecimal("60000");
        if(s.equals("Seconds")) return new BigDecimal("1000");
        return new BigDecimal(0);
    }
	
}
