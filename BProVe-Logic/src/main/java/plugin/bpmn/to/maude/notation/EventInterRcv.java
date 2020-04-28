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

public class EventInterRcv extends EventSplit implements java.io.Serializable{
	
	String Status;
	Edge InterEdge;
	Msg InterMsg;
	
	public EventInterRcv()
	{
		
	}
	
	public EventInterRcv(String inter)
	{
		
		ArrayList<String> interrcv = new ArrayList<String>();
		interrcv = TokenGateway(inter);
		
		Pattern pattern2 = Pattern.compile("(eventInterRcv)+\\(");
		Matcher matcher2 = pattern2.matcher(interrcv.get(0));	
		
		if (matcher2.find()) 
		{
			int startindex = interrcv.get(0).indexOf(matcher2.group())+matcher2.group().length();
			this.Status = interrcv.get(0).substring(startindex).replaceAll("\\s+","");
		}else{
			this.Status = interrcv.get(0).replaceAll("\\s+","");
		}
		
		this.InterEdge = new Edge(interrcv.get(1));
		this.InterMsg = new Msg(interrcv.get(2));
		
	}
	
	public boolean compareEventInterRcv(EventInterRcv eveninter1, EventInterRcv eveninter2)
	{
		if(eveninter1.InterEdge.EdgeName.equals(eveninter2.InterEdge.EdgeName) 
				&& !eveninter1.InterEdge.EdgeToken.equals(eveninter2.InterEdge.EdgeToken))
		{
		return true;
		}else
		{
			return false;
		}
	}
	
	public void SetToken (EventInterRcv eventint)
	{
		//System.out.println("SETTOKEN");
		String token = String.valueOf(1);
		eventint.InterEdge.EdgeToken = token;	
		//System.out.println("TOKEN "+eventint.InterEdge.EdgeToken);
	}
	
	public void printInter()
	{
		//System.out.println("Status: "+this.Status);
		this.InterEdge.printEdge();
		this.InterMsg.printMsg();
	}
}
