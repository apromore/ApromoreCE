/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
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


package com.processconfiguration.utils;


import java.io.File;

import javax.swing.filechooser.FileFilter;



public class TXTFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;//enable directories
		}

		String extension = Utils.getExtension(f);
		if (extension != null) {
			if (extension.equals(Utils.txt))
				return true;
			else {
				return false;
			}
		}

		return false;
	}


	@Override
	public String getDescription() {
		return "Text document (*.txt)";
	}

}
