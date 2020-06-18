/*-
 * #%L
 * This file is part of "Apromore Community".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.plugin.editor.bimp2;

// Third party packages
import org.springframework.stereotype.Component;

// Local packages
import org.apromore.plugin.editor.DefaultEditorPlugin;

@Component("plugin")
public class BIMPEditorPlugin extends DefaultEditorPlugin {

    @Override
    public String getJavaScriptURI() {
        return "/bimp2/editor_plugin.js";
    }

    @Override
    public String getJavaScriptPackage() {
        return "ORYX.Plugins.BIMPSimulator";
    }
}
