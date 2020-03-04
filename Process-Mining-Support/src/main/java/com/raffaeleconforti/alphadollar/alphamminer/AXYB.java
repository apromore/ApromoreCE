/*
 *  Copyright (C) 2018 Raffaele Conforti (www.raffaeleconforti.com)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.raffaeleconforti.alphadollar.alphamminer;

public class AXYB {
	//4 task names of the mendatious relationship.
	String a;
	String x;
	String y;
	String b;
	int invTasktype; 
	final static String start_a_tag= "START_TASK_A";
	final static String start_x_tag= "END_TASK_X";
	final static String end_y_tag= "END_TASK_Y";
	final static String end_b_tag= "END_TASK_B";
	final static int START = 1;
	final static int END = 2;
	final static int SKIP = 3;
	final static int REDO = 4;
	final static int SWITCH = 5;
	
	public AXYB(String _a, String _x, String _y, String _b, int _type)
	{
		a = _a;
		x = _x;
		y = _y;
		b = _b;
		invTasktype = _type;
	}	
	
	@Override
	public boolean equals(Object _axyb)
	{
		if (!(_axyb instanceof AXYB))
			return false;
		AXYB _a = (AXYB) _axyb;
		if (!this.a.equals(_a.a))
			return false;
		if (!this.b.equals(_a.b))
			return false;
		if (!this.x.equals(_a.x))
			return false;
        return this.y.equals(_a.y);
    }
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" A:"+a);
		sb.append(" B:"+b);
		sb.append(" X:"+x);
		sb.append(" Y:"+y);
		return sb.toString();		
	}
}
