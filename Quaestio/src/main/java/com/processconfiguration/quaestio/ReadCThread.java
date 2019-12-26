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


package com.processconfiguration.quaestio;

import com.processconfiguration.qml.QMLType;

public class ReadCThread extends Thread {

	public String constraints;
	private QMLType qml = null;

	public ReadCThread(QMLType qml){
		this.qml  = qml;
	}

	
	@Override
	public void run(){
		constraints = qml.getConstraints();
	}
}
