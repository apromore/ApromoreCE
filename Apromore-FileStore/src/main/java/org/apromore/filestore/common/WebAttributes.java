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

package org.apromore.filestore.common;

/**
 * Well-known keys which are used to store Apromore information in request or session scope.
 *
 * @author Raffaele Conforti
 * @since 1
 */
public final class WebAttributes {

    /**
     * Used to cache an registration-failure exception in the session.
     */
    public static final String REGISTRATION_EXCEPTION = "APROMORE_USER_REGISTRATION_EXCEPTION";

    /**
     * Used to cache an registration-message, this could be for password reset or creation successful.
     */
    public static final String REGISTRATION_MESSAGE = "APROMORE_USER_REGISTRATION_MESSAGE";


}
