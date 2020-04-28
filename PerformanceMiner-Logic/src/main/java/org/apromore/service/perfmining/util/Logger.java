/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Bruce Nguyen.
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
package org.apromore.service.perfmining.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

/**
 * @author R.P. Jagadeesh Chandra 'JC' Bose
 * @date 14 July 2010
 * @since 01 July 2010
 * @version 1.0
 * @email j.c.b.rantham.prabhakara@tue.nl
 * @copyright R.P. Jagadeesh Chandra 'JC' Bose Architecture of Information
 *            Systems Group (AIS) Department of Mathematics and Computer Science
 *            University of Technology, Eindhoven, The Netherlands
 */
public class Logger {

	static PrintStream outFile = null;
	static String logDir, fileName;
	static int callDepth = 0;
	static String prefix = "";
	public static boolean debug = true; // decide whether in debug mode or not.

	// Start a Log file that logs all input and output
	// The file is saved to the c:\temp directory

	public static void startLog(String logDir, String fileName) {

		Logger.logDir = logDir;
		Logger.fileName = fileName;
		try {
			outFile = new PrintStream(new FileOutputStream(logDir + "\\" + fileName));
			outFile.println("----- Log file " + fileName + " started " + new Date());
			System.out.println("----- Writing output to the file: " + fileName + ". ");
		} catch (IOException e) {
			System.out.println("IOException in startLog opening " + fileName + "\n" + e);
		}
	}

	public static void stopLog() {
		outFile.println("----- Log file " + fileName + " closed " + new Date());
		outFile.close();
		outFile = null;
	}

	public static void println(String s) {
		if (outFile != null) {
			outFile.println(prefix + s);
		} else {
			System.out.println(prefix + s);
		}
	}

	public static void println(Object o) {
		if (outFile != null) {
			outFile.println(prefix + o.toString());
		} else {
			System.out.println(prefix + o.toString());
		}
	}

	public static void print(String s) {
		if (outFile != null) {
			outFile.print(s);
		} else {
			System.out.print(s);
			System.out.flush();
		}
	}

	public static void print(Object o) {
		if (outFile != null) {
			outFile.print(o.toString());
		} else {
			System.out.print(o.toString());
			System.out.flush();
		}
	}

	public static void printCall(String s) {
		println(s);
		callDepth++;
		prefix = getPrefix(callDepth);
	}

	static String getPrefix(int i) {
		if (i > 0) {
			return ("| " + getPrefix(i - 1));
		} else {
			return ("");
		}
	}

	public static void printReturn(String s) {
		callDepth--;
		prefix = getPrefix(callDepth);
		println(s);
	}

	public static PrintStream getPrintStream() {
		return outFile;
	}
}
