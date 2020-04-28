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

package org.apromore.plugin.editor.loganimation2;

// Third party packages
import org.springframework.stereotype.Component;

// Local packages
import org.apromore.plugin.editor.DefaultEditorPlugin;

public class LogAnimationPlugin extends DefaultEditorPlugin {
    private String javaScriptURI = ""; //initialized in Spring beans
    private String javaScriptPackage = ""; //initialized in Spring beans
	
    @Override
    public String getJavaScriptURI() {
        return javaScriptURI;
    }
   
    public void setJavaScriptURI(String newValue) {
    	javaScriptURI = newValue;
    }

    @Override
    public String getJavaScriptPackage() {
        return javaScriptPackage;
    }
    
    public void setJavaScriptPackage(String newValue) {
    	javaScriptPackage = newValue;
    }
}
