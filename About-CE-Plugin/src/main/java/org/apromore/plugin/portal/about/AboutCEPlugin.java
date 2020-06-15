/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2019 - 2020 The University of Melbourne. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains
 * the property of the University of Melbourne and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to the University of Melbourne
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from the University of Melbourne.
 * #L%
 */

package org.apromore.plugin.portal.aboutce;

import java.util.*;
import org.apromore.plugin.portal.about.AboutPlugin;

public class AboutCEPlugin extends AboutPlugin {

    private String label = "About";

    @Override
    public String getLabel(Locale locale) {
        return label;
    }
}
