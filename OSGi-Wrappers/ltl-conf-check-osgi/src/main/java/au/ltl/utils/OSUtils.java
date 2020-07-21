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
package au.ltl.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * Utility functions to deal with OS-related stuff.
 *
 */
public class OSUtils {

	/**
	 * Create a file with the given name and write the given (textual) contents on it. If the file name consists in a
	 * path, the parent directories are automatically created.
	 * 
	 * @param fileName The name of the file to be created (possibly a path).
	 * @param contents The contents to be written.
	 * @return The newly created file.
	 */
	public static File writeTextualFile(String fileName, String contents) {
		File file = new File(fileName);
		file.getParentFile().mkdirs();

		try {
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(contents);
			fileWriter.close();
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}

		// it has to be done AFTER the file has been written.
		file.setExecutable(true, false);
		return file;
	}

	/**
	 * Delete all contents from the given directory or create it if not existing.
	 * 
	 * @param directory The File object representing the directory.
	 */
	public static void cleanDirectory(File directory) {
		if (directory.exists()) {			
			try {
				FileUtils.cleanDirectory(directory);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			directory.mkdir();
		}

		// it has to be done AFTER the directory has been created.
		directory.setWritable(true, false);
	}

	/**
	 * Check whether the OS is 64 bits.
	 * 
	 * @return true if OS is 64 bits.
	 */
	public static boolean is64bitsOS() {
		String osArch = System.getProperty("os.arch");
		String winArch = System.getenv("PROCESSOR_ARCHITECTURE");
		String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

		return osArch != null && osArch.endsWith("64")
				|| winArch != null && winArch.endsWith("64")
				|| wow64Arch != null && wow64Arch.endsWith("64");
	}
	
	public static String getOs(){
		
		String OS = System.getProperty("os.name").toLowerCase();
		if (isWindows(OS)) {
			return "windows";
		} else if (isMac(OS)) {
			return "mac";
		} else {
			return "Linux";
		}
		
	}
	private static boolean isWindows(String OS) {

		return (OS.indexOf("win") >= 0);

	}

	private static boolean isMac(String OS) {

		return (OS.indexOf("mac") >= 0);

	}

}
