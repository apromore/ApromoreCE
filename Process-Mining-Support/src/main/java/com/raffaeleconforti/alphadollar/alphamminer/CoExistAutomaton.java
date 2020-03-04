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

import org.processmining.framework.models.petrinet.Transition;

import java.util.ArrayList;

public class CoExistAutomaton {
	private int status;
	
	ArrayList<String> ASet;
	ArrayList<String> BSet;
	ArrayList<String> joinSet;
	Transition t1;
	Transition t2;
	
	public static final int ANYISOK = 0;
	public static final int NeedA = 1;
	public static final int NeedB = 2;
	public static final int AFTERMEETJOIN_ANYISOK = 3;
	
	public boolean isCoexist;
	
	public CoExistAutomaton(ArrayList<String> _ASet, ArrayList<String> _BSet, Transition _t1, Transition _t2)
	{
		isCoexist = true;
		status = ANYISOK;
		ASet = (ArrayList<String>) _ASet.clone();
		BSet = (ArrayList<String>) _BSet.clone();
		
		joinSet = AlphaMMinerDataUtil.join(ASet, BSet);
		ASet =AlphaMMinerDataUtil.except(ASet,joinSet);
		BSet =AlphaMMinerDataUtil.except(BSet,joinSet);
		t1 = _t1;
		t2 = _t2;
	}

	public void add(String task)
	{
		if (task.equals("newtrace"))	//新的trace,在上一个trace的时候完成了共现才可以
		{
			switch (status)
			{
				case ANYISOK:					
					break;
				case NeedA:
					isCoexist = false;
					break;
				case NeedB:
					isCoexist = false;
					break;
				case AFTERMEETJOIN_ANYISOK:
					isCoexist = false;
					break;
			}
		}
		else if (ASet.contains(task))	//A的集合
		{
			switch (status)
			{
				case ANYISOK:
					status = NeedB;
					break;
				case NeedA:
					status = ANYISOK;
					break;
				case NeedB:
					isCoexist = false;
					break;
				case AFTERMEETJOIN_ANYISOK:
					status = ANYISOK;
					break;
			}
		}
		else if (BSet.contains(task))	//B的集合
		{
			switch (status)
			{
				case ANYISOK:
					status = NeedA;
					break;
				case NeedA:
					isCoexist = false;
					break;
				case NeedB:
					status = ANYISOK;
					break;
				case AFTERMEETJOIN_ANYISOK:
					status = ANYISOK;
					break;
			}
		}
		else if (joinSet.contains(task))	//两者的交际
		{
			switch (status)
			{
				case ANYISOK:
					status = AFTERMEETJOIN_ANYISOK;
					break;
				case NeedA:
					status = ANYISOK;
					break;
				case NeedB:
					status = ANYISOK;
					break;
				case AFTERMEETJOIN_ANYISOK:
					status = ANYISOK;
					break;
			}
		}									
	}
		
}
