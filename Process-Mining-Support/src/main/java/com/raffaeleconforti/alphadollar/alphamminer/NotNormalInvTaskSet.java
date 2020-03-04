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

import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.ArrayList;

class NotNormalInvTask
{
	int a,x,y,b;
	ArrayList<Integer> beforeCausal;		//干扰别人的元素
	ArrayList<Integer> afterCausal;			//被影响的元素
	ArrayList<Integer> sharpRelation;		//被影响的元素
	
	public NotNormalInvTask(int _a, int _b, int _x, int _y, ArrayList<Integer> _beforeCausal, ArrayList<Integer> _afterCausal, ArrayList<Integer> _sharpRelation)
	{
		a = _a;
		b = _b;
		x = _x;
		y = _y;	
		beforeCausal = _beforeCausal;
		afterCausal = _afterCausal;
		sharpRelation = _sharpRelation;
	}
	
	public int getA()
	{
		return a;
	}
	
	public int getB()
	{
		return b;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
}

/**
 * 在非正常的情况下的YcasualX与AsequenceB
 */
public class NotNormalInvTaskSet {
	UnifiedMap<PredSucc,NotNormalInvTask> content;
	
	public NotNormalInvTaskSet()
	{
		content = new UnifiedMap<PredSucc, NotNormalInvTask>();
	}
	
	public void addNotNormalInvTask(PredSucc ps, int a, int b, int x, int y, ArrayList<Integer> beforeCausal, ArrayList<Integer> afterCausal, ArrayList<Integer> sharpRelation)
	{
		NotNormalInvTask notNormalInvTask = new NotNormalInvTask(a,b,x,y,beforeCausal,afterCausal,sharpRelation);
		content.put(ps,notNormalInvTask);
	}

	public boolean contains(PredSucc ps) {
		return content.containsKey(ps);					
	}

	public NotNormalInvTask get(PredSucc ps) {
		return content.get(ps);		
	}	
	
	public void deleteNotNormalInvTask(PredSucc ps)
	{
		if (content.get(ps) != null)
		{
			content.remove(ps);
		}
	}
	
}
