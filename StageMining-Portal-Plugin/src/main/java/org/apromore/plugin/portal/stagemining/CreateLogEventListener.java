/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Bruce Nguyen, Queensland University of Technology.
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
package org.apromore.plugin.portal.stagemining;

import org.deckfour.xes.model.XLog;
import org.processmining.stagemining.models.DecompositionTree;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Window;

;

/**
 *
 * @author Administrator
 * Based on http://zkfiddle.org/sample/2vah9aj/29-add-new-row#source-2
 */
public class CreateLogEventListener implements EventListener<Event> {
    Window parentW = null;
    XLog log = null;
    DecompositionTree tree = null;

    
    
    public CreateLogEventListener(Window window, XLog log, DecompositionTree tree) {
        this.parentW = window;
        this.log = log;
        this.tree = tree;
    }
    public void onEvent(Event event) throws Exception {
        
    }
    
}
