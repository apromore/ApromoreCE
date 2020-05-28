/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Alireza Ostovar.
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

package org.apromore.prodrift.util;

public class confusionMat {
	
	private int tP=0,fP=0,fN=0;
	private double mDelay=0;
	
	public confusionMat(int tPos, int fPos, int fNeg, double meanDelay) {
		tP = tPos;
		fP = fPos;
		fN = fNeg;
		mDelay = meanDelay;
	}
	
	
	public double getFScore() {
		return (double)(2*tP)/(2*tP+fP+fN);
	}
	
	public double getPrecision() {
		return (double)(tP)/(tP+fP);
	}
	
	public double getRecall() {
		return (double)(tP)/(tP+fN);
	}
	
	public double getMeanDelay() {
		return mDelay;
	}


	public int gettP() {
		return tP;
	}


	public void settP(int tP) {
		this.tP = tP;
	}


	public int getfP() {
		return fP;
	}


	public void setfP(int fP) {
		this.fP = fP;
	}


	public int getfN() {
		return fN;
	}


	public void setfN(int fN) {
		this.fN = fN;
	}


	public double getmDelay() {
		return mDelay;
	}


	public void setmDelay(double mDelay) {
		this.mDelay = mDelay;
	}
	
	
}
