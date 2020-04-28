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
package org.apromore.plugin.portal.perfmining.view;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoundedRangeModel;
import javax.swing.JPanel;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.perfmining.Visualization;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.xy.XYDataset;
import org.joda.time.DateTime;
import org.apromore.service.perfmining.models.SPF;
import org.apromore.service.perfmining.models.Stage;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Window;

public class SPFView {
	/**
	 * 
	 */
	private static final long serialVersionUID = -202881433419871306L;
	private static final double ZOOM_IN_FACTOR = 0.9; //the new range of axis is 90% of current range
	private static final double ZOOM_OUT_FACTOR = (1.0 / 0.909); //the new range of axis is 110% of current range
        protected String stageName;
        protected String subStageName;
	protected String viewName;
	protected SPF bpf;
	protected ChartPanel chartPanel;
	protected Map<TimePeriod, DateTime> timeMap = new HashMap<TimePeriod, DateTime>(); //to map the time on chart to time point
	BoundedRangeModel orihoriScrollbarModel = null;
        protected boolean isCategory = false;

	public SPFView(SPF bpf, String stageName, String subStageName, String viewName, boolean isCategory) {
            this.stageName = stageName;
            this.subStageName = subStageName;
            this.viewName = viewName;
            this.bpf = bpf;
            this.isCategory = isCategory;
	}

	public SPF getBPF() {
            return bpf;
	}

	public String getStageName() {
            return stageName;
	}
        
        public String getSubStageName() {
            return subStageName;
	}
        
        public String getViewName() {
            return viewName;
	}

        public String getFullName() {
            return stageName + "-" + subStageName + "-" + viewName;
	}
        
	protected TimePeriod getTimePeriod(DateTime timePoint) {
            if (bpf.getConfig().getTimeStep() <= 3600) {
                    return new Hour(timePoint.toDate());
            } else {
                    return new Day(timePoint.toDate());
            }
	}
        
        public boolean isCategory() {
            return this.isCategory;
        }

	protected XYDataset createDataset() {
            return null;
	}
        
        public void showChart(PortalContext portalContext) throws Exception {
            Window chartW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/metrics.zul", null, null);
            String jsonString = Visualization.createChartJson(this.createDataset()).toString();
            String javascript = "loadData('" + jsonString + "');";
            Clients.evalJavaScript(javascript);
            Stage stage = bpf.getStageByName(stageName);
            chartW.setTitle(this.getFullName());
            chartW.doModal();
        }
}
