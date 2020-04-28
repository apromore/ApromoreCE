/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package de.epml.cache;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * Stores all the JAXB Context entries for fast retrieval later.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@SuppressWarnings("rawtypes")
public class JAXBCachedEntry {

    private final String cachedClass;
    private final ClassLoader cachedClassloader;
    private final JAXBContext context;

    public JAXBCachedEntry(String type, ClassLoader classLoader) throws JAXBException{
        context = JAXBContext.newInstance(type, classLoader);
        cachedClass = type;
        cachedClassloader = classLoader;
    }

    public String getCachedClass() {
        return cachedClass;
    }

    public ClassLoader getCachedClassLoader() {
        return cachedClassloader;
    }

    public JAXBContext getContext() {
        return context;
    }

}
