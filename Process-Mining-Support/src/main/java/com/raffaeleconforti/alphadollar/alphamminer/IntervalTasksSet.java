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
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.framework.log.AuditTrailEntryList;
import org.processmining.framework.log.LogEvents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

class IntervalTasks {
	Set<Set<Integer>> content;		//each integer means the interval tasks.
	
	Set<Integer> mayIn;			//may be in the content
	Set<Integer> mustNotIn;		//must not be in the content.
	
	public IntervalTasks(int n)
	{
		content = new UnifiedSet<Set<Integer>>();
	}

	public Set<Integer> getIntervalTasks() {
		Set<Integer> result = new UnifiedSet<Integer>();
		for (Set<Integer> set	:	content)
		{
			result = AlphaMMinerDataUtil.union(result, set);
		}
		return result;
	}
	
	public void beginAdd() {
		mayIn = new UnifiedSet<Integer>();
		mustNotIn = new UnifiedSet<Integer>();
	}
	
	public void addIn(int task)
	{
		mayIn.add(task);
	}
	
	public void addNotIn(int task)
	{
		mustNotIn.add(task);
	}
	
	public void endAdd() {
		//content = AlphaMMinerDataUtil.except(mayIn, mustNotIn);
		if (mayIn.size() != 0)
			content.add(mayIn);				
	}
	
}

public class IntervalTasksSet {
	Map<Integer, IntervalTasks> content;
	Map<String, Integer> transferTabel;
	int base;

	public IntervalTasksSet(int n) {
		base = n+2;
		content = new UnifiedMap<Integer, IntervalTasks>();
		for (int i=0; i<(n-1)*base + (n-1)+1; i++)
		{
			IntervalTasks intervalTasks = new IntervalTasks(n);
			content.put(i,intervalTasks);			
		}
		transferTabel = new UnifiedMap<String, Integer>();
	}
	
	public Set<Integer> getBetween(Integer taskY, Integer taskX) {
		Set<Integer> result = new UnifiedSet<Integer>();
		IntervalTasks intervalTasks = content.get(taskY*base + taskX);
		if (intervalTasks != null)
			result = intervalTasks.getIntervalTasks();	
		return result;
	}

	public void beginAdd()
	{
		for (int i=0; i<content.size(); i++)
			content.get(i).beginAdd();
	}
	
	public void endAdd()
	{
		for (int i=0; i<content.size(); i++)
			content.get(i).endAdd();
	}

	public void addTrace(AuditTrailEntryList ates, LogEvents logEvents) 
	{
		ArrayList<Integer> intTrace = new ArrayList<Integer>();
		beginAdd();
		//1.get the int-version of trace(intTrace)
		for (int i=0; i<ates.size(); i++)
		{
			try 
			{
				Integer taskInt;
				String taskName = ates.get(i).getName();
				if ((taskInt = transferTabel.get(taskName)) == null)
				{
					for (int j=0; j<logEvents.size(); j++)					
						if (logEvents.get(j).getModelElementName().equals(taskName))
						{
							taskInt = j;
							break;
						}
					transferTabel.put(taskName, taskInt);
				}				
				if (taskInt != null)
					intTrace.add(taskInt);								
			} 
			catch (IndexOutOfBoundsException e)
			{			
				e.printStackTrace();
			} 
			catch (IOException e)
			{			
				e.printStackTrace();
			}			
		}
		
		//2.search to add
		for (int i=0; i<intTrace.size(); i++)
			for (int j=i; j<intTrace.size(); j++)
			{
				int pre = intTrace.get(i);
				int post = intTrace.get(j);
				IntervalTasks intervalTasks = content.get(pre*base+post);
				for (int k=0; k<i; k++)
					intervalTasks.addNotIn(intTrace.get(k));
				for (int k=i+1; k<j; k++)
					intervalTasks.addIn(intTrace.get(k));
				for (int k=j+1; k<intTrace.size(); k++)
					intervalTasks.addNotIn(intTrace.get(k));
			}
		endAdd();
	}

	public Set<Set<Integer>> getContent(int taskY, int taskX) {
		IntervalTasks intervalTasks = content.get(taskY*base + taskX);
		if (intervalTasks != null)
			return intervalTasks.content;
		else
			return new UnifiedSet<Set<Integer>>();
	}

}
