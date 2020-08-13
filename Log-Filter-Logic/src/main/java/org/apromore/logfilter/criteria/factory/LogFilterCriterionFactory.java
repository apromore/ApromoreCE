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


package org.apromore.logfilter.criteria.factory;

import java.util.List;
import java.util.Set;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.apromore.logfilter.criteria.model.Action;
import org.apromore.logfilter.criteria.model.Containment;
import org.apromore.logfilter.criteria.model.Level;

/**
 * @author Bruce Hoang Nguyen (11/07/2019)
 */
public interface LogFilterCriterionFactory {

    public LogFilterCriterion getLogFilterCriterion(Action action, Containment containment, Level level, String label, String attribute, Set<String> value);
    public LogFilterCriterion copyFilterCriterion(LogFilterCriterion criterion);
    public List<LogFilterCriterion> copyFilterCriterionList(List<LogFilterCriterion> list);
    
    public LogFilterCriterion convertFilterRuleToFilterCriterion(LogFilterRule rule);
    public List<LogFilterCriterion> convertFilterRulesToFilterCriteria(List<LogFilterRule> list);
    
    public LogFilterRule convertFilterCriterionToFilterRule(LogFilterCriterion criterion);
    public List<LogFilterRule> convertFilterCriteriaToFilterRules(List<LogFilterCriterion> criteria);
}
