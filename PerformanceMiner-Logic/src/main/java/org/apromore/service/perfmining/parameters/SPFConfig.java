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
package org.apromore.service.perfmining.parameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.deckfour.xes.model.XLog;

/*
 * This class manages all user-defined settings
 */
public class SPFConfig {

	private List<String> stageList = new ArrayList<String>();
	private Map<String, String> eventStageMap = new HashMap<String, String>();
	private final List<String> caseStatusList = new ArrayList<String>();
	private List<String> exitTypeList = new ArrayList<String>();
	private XLog log;
	private int timeStep = 3600; //seconds
	private TimeZone timezone = null;
	private boolean checkStartCompleteEvents = false;

	public SPFConfig() {
	}

	public XLog getXLog() {
		return log;
	}

	public void setXLog(XLog newLog) {
		log = newLog;
	}

	public void setStageList(List<String> stageList) {
		this.stageList = stageList;
	}

	public List<String> getStageList() {
		return stageList;
	}	

	public List<String> getCaseStatusList() {
		return caseStatusList;
	}

	public List<String> getExitTypeList() {
		return exitTypeList;
	}

	public void setExitTypeList(List<String> newList) {
		exitTypeList = newList;
	}

	public void setEventStageMap(Map<String, String> newEventStageMap) {
		eventStageMap = newEventStageMap;
	}

	public Map<String, String> getEventStageMap() {
		return eventStageMap;
	}

	public int getTimeStep() {
		return timeStep;
	}

	public void setTimeStep(int timeStep) throws Exception {
		if (timeStep < 0) {
			throw new Exception("Invalid time step");
		} else {
			this.timeStep = timeStep;
		}
	}

	public TimeZone getTimeZone() {
		return timezone;
	}

	public void setTimeZone(TimeZone timezone) {
		this.timezone = timezone;
	}

	public boolean getCheckStartCompleteEvents() {
		return checkStartCompleteEvents;
	}

	public void setCheckStartCompleteEvents(boolean check) {
		checkStartCompleteEvents = check;
	}

}
