/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.processconfiguration.quaestio;

import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;

/**
 * Abstracts away where exactly we're reading our process models from.
 */
public interface ProcessModel {

	/**
         * @return the C-BPMN process model
	 */
	BpmnDefinitions getBpmn() throws Exception;

	/**
         * @return the filename of the process model, <code>null</code> if the model has no file
         */
	String getText();

	/**
	 * Durably record any changes to the configurable process model.
         *
         * @param bpmn  the new value of the process model
	 */
	void update(final BpmnDefinitions bpmn) throws Exception;
}

