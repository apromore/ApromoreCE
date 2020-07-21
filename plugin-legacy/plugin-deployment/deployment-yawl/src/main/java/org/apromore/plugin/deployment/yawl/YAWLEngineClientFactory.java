/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
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
package org.apromore.plugin.deployment.yawl;

/**
 * Factory providing YAWL Engine Clients
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public class YAWLEngineClientFactory {

    /**
     * Returns a YAWL Engine client that is connected to specified YAWL Engine.
     *
     * @param engineUrl full URL to YAWL Engine
     * @param username of YAWL user
     * @param password of YAWL user
     * @return a {@link YAWLEngineClient}
     */
    public YAWLEngineClient newInstance(final String engineUrl, final String username, final String password) {
        return new YAWLEngineClient(engineUrl, username, password);
    }

}
