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


public class InterRcv extends MsgEvent  implements java.io.Serializable{
	
	//Status Edge Edge Msg
	String Status;
	Edge InputEdge;
	public Edge OutputEdge;
	Msg msg;
		
	public InterRcv()
	{
		
	}
	
	public InterRcv(String status, Edge input, Edge output, Msg msg)
	{
		this.Status = status;
		this.InputEdge = input;
		this.OutputEdge = output;
		this.msg = msg;
	}
	
	
	public InterRcv(String interrcv) {
		
		ArrayList<String> isnd = new ArrayList<String>();
		isnd = TokenMsgEvent(interrcv);
		this.Status = isnd.get(0);
		this.InputEdge = new Edge(isnd.get(1));
		this.OutputEdge = new Edge(isnd.get(2));
		this.msg = new Msg(isnd.get(3));
	}
	
	public boolean compareInterRcv(InterRcv interrcv1, InterRcv interrcv2)
	{
		if(!interrcv1.Status.equals(interrcv2.Status) ||
				!interrcv1.OutputEdge.EdgeToken.equals(interrcv2.OutputEdge.EdgeToken)  )
		{
			return true;
		}else
		{
			return false;
		}
	}
	
	/*public boolean compareMsgCatchEventName (InterRcv msgcatchevent1, InterRcv msgcatchevent2)
	{
		if(msgcatchevent1.name.equals(msgcatchevent2.name))
		{
			return true;
		}else
		{
			return false;
		}
	}*/
	
	public void printInterRcv()
	{
		//Status Edge Edge Msg
		//System.out.println("\nInterRcv: ");
		//System.out.println("Status: "+this.Status);
		if(this.InputEdge != null)
		{
			this.InputEdge.printEdge();
		}
		this.OutputEdge.printEdge();
		this.msg.printMsg();		
	}
	

}
