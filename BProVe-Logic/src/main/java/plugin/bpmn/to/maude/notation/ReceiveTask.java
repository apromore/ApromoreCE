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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ReceiveTask extends Task implements java.io.Serializable{
	 
	//Status Edge Edge Msg TaskName
	//String MsgName;
	//String MsgToken;
	Msg msg;
	
	public ReceiveTask()
	{
		
	}
	
	public ReceiveTask(String ReceiveTask) {
		
		ArrayList<String> ReceiveTaskString = new ArrayList<String>();
		ReceiveTaskString = EdgeTaskEdge(ReceiveTask);
		
		this.TaskStatus = ReceiveTaskString.get(0);
		this.InputEdge = new Edge(ReceiveTaskString.get(1)); 
		this.OutputEdge = new Edge(ReceiveTaskString.get(2));
		this.msg = new Msg(ReceiveTaskString.get(3));
		
		Pattern pattern = Pattern.compile("^\"|^[ ]+\"");
		Matcher matcher = pattern.matcher(ReceiveTaskString.get(4));	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length();
			int endindex = ReceiveTaskString.get(4).lastIndexOf("\"");
			String substring = ReceiveTaskString.get(4).substring(startindex, endindex);
			////System.out.println("SUBSTRING STARTRCVMSG: "+substring);
			this.name = substring;
		}
		
		
		/*ArrayList<String> ReceiveTaskString = new ArrayList<String>();
		ArrayList<String> ReceiveTaskParam = new ArrayList<String>();
		ArrayList<String> ReceiveMsgParam = new ArrayList<String>();
		ReceiveTaskString = EdgeTaskEdge(ReceiveTask);		
		this.InputEdge = new Edge(ReceiveTaskString.get(0)); 
		this.OutputEdge = new Edge(ReceiveTaskString.get(ReceiveTaskString.size()-1));
		ReceiveTaskParam = PointDivision(ReceiveTaskString.get(1));
		ReceiveMsgParam = PointDivision(ReceiveTaskString.get(2));
		
		Pattern pattern = Pattern.compile("^\"|^[ ]+\"");
		Matcher matcher = pattern.matcher(ReceiveTaskParam.get(0));	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length();
			int endindex = ReceiveTaskParam.get(0).lastIndexOf("\"");
			String substring = ReceiveTaskParam.get(0).substring(startindex, endindex);
			////System.out.println("SUBSTRING STARTRCVMSG: "+substring);
			////System.out.println("STARTINDEX: "+startindex);
			this.name = substring;
		}
		
		this.TaskToken = ReceiveTaskParam.get(1);
		this.TaskStatus = ReceiveTaskParam.get(2);
		this.MsgName = ReceiveMsgParam.get(0);
		this.MsgToken = ReceiveMsgParam.get(1).replaceAll("\\D+","");*/		
	}
	
	public boolean compareReceiveTask(ReceiveTask ReceiveTask1, ReceiveTask ReceiveTask2)
	{
		if(ReceiveTask1.name.equals(ReceiveTask2.name) 
				&& !ReceiveTask1.InputEdge.EdgeToken.equals(ReceiveTask2.InputEdge.EdgeToken))
		{
		return true;
		}else
		{
			return false;
		}
	}
	
	public boolean compareReceiveTaskName (ReceiveTask ReceiveTask1, ReceiveTask ReceiveTask2)
	{
		if(ReceiveTask1.name.equals(ReceiveTask2.name))
		{
			return true;
		}else
		{
			return false;
		}
	}
	
	public void printReceiveTask()
	{
		//System.out.println("\nReceiveTask: ");
		//System.out.println("TaskStatus: "+this.TaskStatus);
		this.InputEdge.printEdge();
		this.OutputEdge.printEdge();
		this.msg.printMsg();
		//System.out.println("TaskName: "+name);
		
		/*//System.out.println("\nReceiveTask: ");
		this.InputEdge.printEdge();
		//System.out.println("TaskName: "+name);
		//System.out.println("TaskToken: "+TaskToken);
		//System.out.println("TaskStatus: "+TaskStatus);
		//System.out.println("MsgName: "+MsgName);
		//System.out.println("MsgToken: "+MsgToken);
		this.OutputEdge.printEdge();*/	
	}
		
}
