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


public class StartRcvMsg  extends Start implements java.io.Serializable{
	
	//Status Edge Msg
	
	//String MsgName;
	//String MsgToken;
	
	Msg msg;
	
	public StartRcvMsg()
	{
		
	}
	
	public StartRcvMsg(String StartRcvMsg) {
		
		ArrayList<String> StartRcvToken = new ArrayList<String>();	
		//ArrayList<String> StartRcvParam = new ArrayList<String>();
		StartRcvToken = super.TokenStartEdge(StartRcvMsg);
		
		this.Status = StartRcvToken.get(0);
		
		Pattern pattern = Pattern.compile("^\"|^\\s*+\"");
		Matcher matcher = pattern.matcher(StartRcvToken.get(1));	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length()-1;
			//int endindex = StartRcvToken.get(1).lastIndexOf(")");
			String substring = StartRcvToken.get(1).substring(startindex);// endindex);
			this.Edge = new Edge(substring);
		}
		this.msg = new Msg(StartRcvToken.get(2));
	}
	
	public boolean compareStartRcvMsg(StartRcvMsg startrcvmsg1, StartRcvMsg startrcvmsg2)
	{
		if(startrcvmsg1.Edge.EdgeName.equals(startrcvmsg2.Edge.EdgeName)
				&& !startrcvmsg1.Status.equals(startrcvmsg2.Status))
		{
		return true;
		}else
		{
			return false;
		}
	}
	
	/*public boolean compareStartRcvMsgName (StartRcvMsg startrcvmsg1, StartRcvMsg startrcvmsg2)
	{
		if(startrcvmsg1.name.equals(startrcvmsg2.name))
		{
			return true;
		}else
		{
			return false;
		}
	}*/
	
	public void printStartRcvMsg()
	{
		//Status Edge Msg
		//System.out.println("\nStartRcv: ");
		//System.out.println("Status: "+this.Status);
		this.Edge.printEdge();
		this.msg.printMsg();
		/*//System.out.println("InputToken: "+InputToken);
		//System.out.println("MsgEventName : "+name);
		//System.out.println("MsgName: "+MsgName);
		//System.out.println("MsgToken: "+MsgToken);
		this.OutputEdge.printEdge();*/		
	}	

}
