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

import plugin.bpmn.to.maude.notation.Gateway.GatewayType;

public class AndSplit extends Gateway implements java.io.Serializable{
	
	//Edge EdgeSet
	public AndSplit()
	{
		
	}
	
	public AndSplit(String andsplit)
	{
		ArrayList<String> SplitGateway = TokenGateway(andsplit);
		Edge edge = new Edge();		
		this.Edge = new Edge(SplitGateway.get(0));
		
		this.EdgeSet = edge.SplitEdges(extractEdges(SplitGateway.get(1)));
				
		this.Gtype = GatewayType.Join;
	}
	
	public boolean compareAndSplit(AndSplit andsplit1, AndSplit andsplit2)
	{
		if(!andsplit1.EdgeSet.get(0).EdgeToken.equals(andsplit2.EdgeSet.get(0).EdgeToken))
		{
		return true;
		}else
		{
			return false;
		}
	}
	
	/*public boolean compareAndSplitName (AndSplit andsplit1, AndSplit andsplit2)
	{
		if(andsplit1.name.equals(andsplit2.name))
		{
			return true;
		}else return false;	
	}*/
	
	public void printAndSplit()
	{	
		// Edge EdgeSet
		//System.out.println("\nAndSplit: ");
		Edge edgeprint = new Edge();
		edgeprint.printEdgeList(this.EdgeSet);
		this.Edge.printEdge();				
	}
}
