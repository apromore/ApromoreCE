/*-
 * #%L
 * This file is part of "Apromore Community".
 *
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
package plugin.bpmn.to.maude.notation;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Task extends ProcElement  implements java.io.Serializable{
	// Status Edge Edge TaskName
	public Edge InputEdge;
	String TaskToken;
	String TaskStatus;
	Edge OutputEdge;
	
	public Task()
	{
		
	}
	
	public Task(String task)
	{
		// Status Edge Edge TaskName
		
		ArrayList<String> TaskString = new ArrayList<String>();
		TaskString = EdgeTaskEdge(task);
		
		this.TaskStatus = TaskString.get(0);
		this.InputEdge = new Edge(TaskString.get(1)); 
		this.OutputEdge = new Edge(TaskString.get(2));
		
		Pattern pattern = Pattern.compile("^\"|^[ ]+\"");
		Matcher matcher = pattern.matcher(TaskString.get(3));	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length();
			int endindex = TaskString.get(3).lastIndexOf("\"");
			String substring = TaskString.get(3).substring(startindex, endindex);
			this.name = substring;
		}			
		
		/*//System.out.println("STRING TASK: "+task);
		ArrayList<String> TaskString = new ArrayList<String>();
		ArrayList<String> TaskParam = new ArrayList<String>();		
		TaskString = EdgeTaskEdge(task);
		for(String a: TaskString)
			//System.out.println("DIVISIONE TASK: "+a);
		this.InputEdge = new Edge(TaskString.get(0)); 
		this.OutputEdge = new Edge(TaskString.get(2));
		TaskParam = PointDivision(TaskString.get(1));
		
		Pattern pattern = Pattern.compile("^\"|^[ ]+\"");
		Matcher matcher = pattern.matcher(TaskParam.get(0));	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length();
			int endindex = TaskParam.get(0).lastIndexOf("\"");
			String substring = TaskParam.get(0).substring(startindex, endindex);
			this.name = substring;
		}
		
		this.TaskStatus = TaskParam.get(0);*/

	}
	
	public ArrayList<String> EdgeTaskEdge(String task)
	{
		ArrayList<String> EdgeTaskEdge = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(task, ",");
		while (st.hasMoreTokens())
		    	{
		    		EdgeTaskEdge.add(st.nextToken());
		    	}
		return EdgeTaskEdge;	
	}
	
	public ArrayList<String> PointDivision(String EdgeTaskEdge)
	{
		ArrayList<String> PointDivision = new ArrayList<String>();
		
		StringTokenizer st = new StringTokenizer(EdgeTaskEdge, ".");
		while (st.hasMoreTokens())
		    	{
		    		PointDivision.add(st.nextToken());
		    	}
			
		return PointDivision;	
	}
	
	public boolean compareTask(Task task1, Task task2)
	{
		if(task1.name.equals(task2.name) && !task1.TaskStatus.equals(task2.TaskStatus))
		{
			return true;
		}else{
			return false;
		}
	}
	
	/*public boolean compareTask(Task task1, Task task2)
	{
		if(task1.name.equals(task2.name) && !task1.InputEdge.EdgeToken.equals(task2.InputEdge.EdgeToken))
		{
			return true;
		}else{
			return false;
		}
	}*/
	
	public boolean compareTaskName (Task task1, Task task2)
	{
		if(task1.name.equals(task2.name))
		{
			return true;
		}else{
			return false;
		}
	}
	
	public void printTask()
	{
		
		//System.out.println("\nTask: ");
		//System.out.println("TaskStatus: "+TaskStatus);
		this.InputEdge.printEdge();
		this.OutputEdge.printEdge();
		//System.out.println("TaskName: "+name);
				
	}
			
}
