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


public class EndSndMsg extends End implements java.io.Serializable{
	
	//Status Edge Msg
	
	String Status;
	Msg msg;
	
	public EndSndMsg()
	{
		
	}
	
	public EndSndMsg (String endsndmsg)
	{
		ArrayList<String> EndSndCatch = new ArrayList<String>();
		
		EndSndCatch = TokenEnd(endsndmsg);
		this.Status = EndSndCatch.get(0);
		
		Pattern pattern = Pattern.compile("^\"|^[ ]+\"");
		Matcher matcher = pattern.matcher(EndSndCatch.get(1));	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length();
			int endindex = EndSndCatch.get(1).lastIndexOf("\"");
			String substring = EndSndCatch.get(1).substring(startindex, endindex);
			this.Edge = new Edge(substring);
		}
		
		this.msg = new Msg(EndSndCatch.get(2));
	}
	
	public boolean compareEndSndMsg(EndSndMsg endsndmsg1, EndSndMsg endsndmsg2)
	{
		if(endsndmsg1.name.equals(endsndmsg2.name)
				&& !endsndmsg1.Edge.EdgeToken.equals(endsndmsg2.Edge.EdgeToken))
		{
		return true;
		}else
		{
			return false;
		}
	}	
	
	/*public boolean compareEndSndMsgName (EndSndMsg endsndmsg1, EndSndMsg endsndmsg2)
	{
		if(endsndmsg1.name.equals(endsndmsg2.name))
		{
			return true;
		}else return false;	
	}*/
	
	public void printEndSndMsg()
	{
		//Status Edge Msg
		//System.out.println("\nEndSndMsg: ");
		//System.out.println("Status: "+this.Status);
		this.Edge.printEdge();
		this.msg.printMsg();
		////System.out.println("OutputToken: "+OutputToken);
	}
}
