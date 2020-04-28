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

public class Terminate extends End implements java.io.Serializable{
	
	 //Status Edge
	String Status;
	
	
	public Terminate()
	{
		
	}
	
	public Terminate(String stringterminate) {
		ArrayList<String> TerminateToken = new ArrayList<String>();
		
		TerminateToken = TokenTerminateEdge(stringterminate);
		this.Status = TerminateToken.get(0);
		
		Pattern pattern = Pattern.compile("^\"|^\\s*+\"");
		Matcher matcher = pattern.matcher(TerminateToken.get(1));	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length()-1;
			int endindex = TerminateToken.get(1).lastIndexOf(")");
			String substring = TerminateToken.get(1).substring(startindex, endindex);
			this.Edge = new Edge(substring);
		}
	}
	
	public ArrayList<String> TokenTerminateEdge(String terminate)
	{
		ArrayList<String> TokenTerminateEdge = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(terminate, ",");
		while (st.hasMoreTokens())
	    	{
				TokenTerminateEdge.add(st.nextToken());
	    	}
		return TokenTerminateEdge;	
	}
	
	public boolean compareTerminate(Terminate terminate1, Terminate terminate2)
	{
		if(terminate1.Status.equals(terminate2.Status))
		{
			return true;
		}else
		{
			return false;
		}
	}
	
	public void printTerminate()
	{
		//Status Edge
		//System.out.println("\nStart: ");
		//System.out.println("Status: "+this.Status);
		this.Edge.printEdge();		
	}

}
