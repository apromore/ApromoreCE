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

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * This interface defines changes on settings
 * 
 * @author Hoang Nguyen
 * 
 */
public interface SPFSettingsListener {
	//	public void setActivityList(List<String> activityList);
	//	public void setGateList(List<String> gateList);
	public void setStageList(List<String> stageList);

	public void setExitStatusList(List<String> statusList);

	public void setEventStageMap(Map<String, String> eventStageMap);

	public void setTimeZone(TimeZone timezone);

	public void setCheckStartCompleteEvents(boolean check);
}
