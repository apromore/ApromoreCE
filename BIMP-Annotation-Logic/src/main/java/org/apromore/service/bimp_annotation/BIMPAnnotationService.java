/*-
 * #%L
 * This file is part of "Apromore Community".
 *
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

package org.apromore.service.bimp_annotation;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.deckfour.xes.model.XLog;

/**
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au"/>Simon Raboczi</a>
 */
public interface BIMPAnnotationService {

    String annotateBPMNModelForBIMP(String model, XLog log, Context context) throws InterruptedException, IOException, TimeoutException;

    /**
     * Callback hooks offered by the BIMP annotation logic.
     */
    public interface Context {

        /**
         * @param description  possibly <code>null</code>, otherwise a short (one-line) text describing the Python script's current activity
         */
        void setDescription(String description);

        /**
         * @param fractionComplete  A value in the 0...1 range representing the Python script's estimated completion; <code>null</code> indicates indefinite progress
         */
        void setFractionComplete(Double fractionComplete);
    }
}
