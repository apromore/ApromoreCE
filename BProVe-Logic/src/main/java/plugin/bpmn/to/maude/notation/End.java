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


public class End extends Event implements java.io.Serializable{
	
	//Edge
	String Status;
	//String OutputToken;
	public Edge Edge;
	
	public End()
	{
		
	}
	public End(String end)
	{
		ArrayList<String> EndString = new ArrayList<String>();
		EndString = TokenEnd(end);
		this.Status = EndString.get(0);
		this.Edge = new Edge(EndString.get(1));
		
		/*Pattern pattern = Pattern.compile("^\"|^[ ]+\"");
		Matcher matcher = pattern.matcher(EndString.get(1));	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length();
			int endindex = EndString.get(1).lastIndexOf("\"");
			String substring = EndString.get(1).substring(startindex, endindex);
			////System.out.println("SUBSTRING STARTRCVMSG: "+substring);
			////System.out.println("STARTINDEX: "+startindex);
			this.name = substring;
		}
		
		//this.name = EndString.get(1).substring(1, EndString.get(1).length()-1);
		this.OutputToken = EndString.get(2).replaceAll("\\D+","");*/
		
	}
	public ArrayList<String> TokenEnd(String end)
	{

		ArrayList<String> TokenEnd = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(end, ",");
		
		while (st.hasMoreTokens())
	    	{
			TokenEnd.add(st.nextToken());
	    	}
		return TokenEnd;	
	}
	
	public ArrayList<String> EndSndMsgPointDivision(String endmsg)
	{
		ArrayList<String> MsgDivision = new ArrayList<String>();
		
	
			StringTokenizer st = new StringTokenizer(endmsg, ".");
			while (st.hasMoreTokens())
				{
					MsgDivision.add(st.nextToken());
				}
		
		return MsgDivision;	
	}
	
	public boolean compareEnd(End end1, End end2)
	{
		if(end1.Edge.EdgeName.equals(end2.Edge.EdgeName) 
				&& !end1.Edge.EdgeToken.equals(end2.Edge.EdgeToken)
				&& !end1.Status.equals(end2.Status) )
		{
			return true;
		}else
		{
			return false;
		}
	}
	
	/*public boolean compareEndName (End end1, End end2)
	{
		if(end1.name.equals(end2.name))
		{
			return true;
		}else return false;	
	}*/
	
	public void printEnd()
	{
		//System.out.println("\nEnd: ");
		this.Edge.printEdge();
		////System.out.println("EndName: "+this.name);
		////System.out.println("OutputToken: "+this.OutputToken);		
	}
	
	
}

