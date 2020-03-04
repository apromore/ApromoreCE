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

import java.util.ArrayList;

public class InvTask {
	public final static int START = 1;
	public final static int END = 2;
	public final static int SKIP_REDO_SWITCH = 3;
	public final static String TRACE_BEGIN_TAG = "START_TRACE";
	public final static String TRACE_END_TAG = "END_TRACE";
	
	String taskName;
	int type;
	ArrayList<String> pre;
	ArrayList<String> suc;
	
	public InvTask(String _taskName, int _type)	{
		taskName = _taskName;
		pre = new ArrayList<String>();
		suc = new ArrayList<String>();
		type = _type;
	}
	
	public void addPreTask(String taskName)
	{
		pre.add(taskName);
	}
	
	public void addSucTask(String taskName)
	{
		suc.add(taskName);
	}
		
}
