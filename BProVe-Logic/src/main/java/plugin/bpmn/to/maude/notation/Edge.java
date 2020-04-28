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
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class Edge implements java.io.Serializable{
	
	public String EdgeName;
	String EdgeToken;
	
	public Edge()
	{
		this.EdgeName = "";
		this.EdgeToken = "";		
	}
	
	public Edge(String stringEdge)
	{
		ArrayList<String> ArrayEdge = PointDivision(stringEdge);
		
		Pattern patterEdgeName = Pattern.compile("^\"+[A-Za-z0-9-_:]+\" |^\\s*+\"+[A-Za-z0-9-_:]+\"");
		Matcher matcherEdgeName = patterEdgeName.matcher(ArrayEdge.get(0));
		
		if(matcherEdgeName.find())
		{
			String replaceEdgeName = matcherEdgeName.group().replaceAll("\\s+","");
		
			this.EdgeName = replaceEdgeName.substring(1, replaceEdgeName.length()-1);
			this.EdgeToken = ArrayEdge.get(1).replaceAll("\\D+","");
		}
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
	
	public ArrayList<Edge> SplitEdges(String edge)
	{	
		
		//System.out.println("\nEdge: "+edge);
		String[] strEdge = edge.split("and");
		ArrayList<String> Edges = new ArrayList<String>(Arrays.asList(strEdge));
		ArrayList<Edge> ArrayEdge = new ArrayList<Edge>();
		for(int i=0; i<Edges.size(); i++)
			{
				StringTokenizer st = new StringTokenizer(Edges.get(i), ".");
				Edge edge1 = new Edge(Edges.get(i));
				ArrayEdge.add(edge1);
				while (st.hasMoreTokens())
					{	
					st.nextToken();
					}
			}
			return ArrayEdge;	
		}
	
	
	public void printEdge()
	{
		//System.out.println("EdgeName: "+this.EdgeName);
		//System.out.println("EdgeToken: "+this.EdgeToken);
	}
	
	public void printEdgeList(ArrayList <Edge> EdgeList)
	{
		for(int i=0; i<EdgeList.size(); i++)
		{
			EdgeList.get(i).printEdge();
		}
	}
}
