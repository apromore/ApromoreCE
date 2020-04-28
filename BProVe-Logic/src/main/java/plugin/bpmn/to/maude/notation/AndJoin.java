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

public class AndJoin extends Gateway implements java.io.Serializable{
	
	//EdgeSet Edge
	
	public AndJoin()
	{
		
	}
	
	
	public AndJoin(String andjoin)
	{	
		ArrayList<String> SplitGateway = TokenGateway(andjoin);
		Edge edge = new Edge();
		this.EdgeSet = edge.SplitEdges(extractEdges(SplitGateway.get(0)));
		
		this.Edge = new Edge(SplitGateway.get(1)); 
		
		this.Gtype = GatewayType.Join;		
	}
	
		
	public boolean compareAndJoin (AndJoin andjoin1, AndJoin andjoin2)
	{
		for(int i = 0; i<andjoin1.EdgeSet.size(); i++)
		{
			if(andjoin1.EdgeSet.get(i).EdgeName.equals(andjoin2.EdgeSet.get(i).EdgeName)
					&& !andjoin1.EdgeSet.get(i).EdgeToken.equals(andjoin2.EdgeSet.get(i).EdgeToken)) return true;
		}
		return false;	
	}
	
	/*public boolean compareAndJoinName (AndJoin andjoin1, AndJoin andjoin2)
	{
		if(andjoin1.name.equals(andjoin2.name))
		{
			return true;
		}else return false;	
	}*/
	
	public void printAndJoin()
	{
		//System.out.println("\nAndJoin: ");
		Edge edgeprint = new Edge();
		edgeprint.printEdgeList(this.EdgeSet);
		this.Edge.printEdge();
		
	}
	

}
