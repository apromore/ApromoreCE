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
package org.apromore.processdiscoverer.logprocessors;

import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;

/**
 * This log contains the same number of traces and events as the original log
 * However, each trace only contains the event timestamps
 * The first timestamp is for the artificial start event and equal to the timestamp of the first event
 * The last timestamp is for the artificial end event and is equal to the timestamp of the last event
 * @author Bruce Nguyen
 *
 */
public class TimeLog extends ArrayList<LongList> {
	private final XTimeExtension xte = XTimeExtension.instance();
	private XLog log;
	
	public TimeLog(XLog log) {
		this.log = log;
        for(XTrace trace : log) {
            LongArrayList simplified_times_trace = new LongArrayList(trace.size());

            for(int i = 0; i < trace.size(); i++) {
                XEvent event = trace.get(i);
                Long time = xte.extractTimestamp(event).getTime();
                if(i == 0) {
                    simplified_times_trace.add(time);
                }
                if(i == trace.size() - 1) {
                    simplified_times_trace.add(time);
                }
                simplified_times_trace.add(time);
            }

            this.add(simplified_times_trace);
        }

    }
	
	public XLog getXLog() {
		return this.log;
	}
}
