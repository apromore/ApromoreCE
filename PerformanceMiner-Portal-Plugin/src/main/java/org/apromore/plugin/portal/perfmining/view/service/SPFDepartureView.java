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
package org.apromore.plugin.portal.perfmining.view.service;

import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.perfmining.Visualization;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.apromore.service.perfmining.models.SPF;
import org.apromore.service.perfmining.models.Stage;
import org.apromore.plugin.portal.perfmining.view.SPFView;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

public class SPFDepartureView extends SPFView {
	public SPFDepartureView(SPF bpf, String stageName, String subStageName, String viewName, boolean isCategory) {
		super(bpf, stageName, subStageName, viewName, isCategory);
	}

	@Override
	protected XYDataset createDataset() {
		TimeTableXYDataset dataset = new TimeTableXYDataset();
		Stage stage = bpf.getStageByName(stageName);

		for (int i = 0; i < bpf.getTimeSeries().size(); i++) {
			TimePeriod timeP = getTimePeriod(bpf.getTimeSeries().get(i));
			dataset.add(timeP, stage.getServicePassedCounts().get(i), stage.getName() + "-passed");
		}

		for (String exitType : stage.getServiceExitSubCounts().keySet()) {
			for (int i = 0; i < bpf.getTimeSeries().size(); i++) {
				TimePeriod timeP = getTimePeriod(bpf.getTimeSeries().get(i));
				dataset.add(timeP, stage.getServiceExitSubCounts().get(exitType).get(i), stage.getName() + "-"
						+ exitType);
			}
		}

		return dataset;
	}
}
