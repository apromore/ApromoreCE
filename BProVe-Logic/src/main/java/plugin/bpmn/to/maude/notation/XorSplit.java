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





public class XorSplit extends Gateway implements java.io.Serializable{
	
	//Edge EdgeSet
	public XorSplit()
	{
		
	}
	
	public XorSplit(String xorsplit)
	{
		//System.out.println("\nxorsplit: "+xorsplit);
		ArrayList<String> SplitGateway = TokenGateway(xorsplit);
		Edge edge = new Edge();	
		
		//for(int i;i<SplitGateway.size();i++)//System.out.println("\nSplitGateway.get(i): "+SplitGateway.get(i));
		
		//System.out.println("\nSplitGateway.get(0): "+SplitGateway.get(0));
		this.Edge = new Edge(SplitGateway.get(0));
		
		//System.out.println("\nSplitGateway.get(1): "+SplitGateway.get(1));
		this.EdgeSet = edge.SplitEdges(extractEdges(SplitGateway.get(1)));
				
		this.Gtype = GatewayType.Join;
	}
	
	public boolean compareXorSplit(XorSplit xorsplit1, XorSplit xorsplit2)
	{
		
		if(xorsplit1.Edge.EdgeName.equals(xorsplit2.Edge.EdgeName) && !xorsplit1.Edge.EdgeToken.equals(xorsplit2.Edge.EdgeToken))
		{
		return true;
		}
		return false;
	
	}
	
	/*public boolean compareXorSplit(XorSplit xorsplit1, XorSplit xorsplit2)
	{
		for(int i=0; i<xorsplit1.EdgeSet.size(); i++)
		{
			if(!xorsplit1.EdgeSet.get(i).EdgeToken.equals(xorsplit2.EdgeSet.get(i).EdgeToken))return true;
									
		}
		return false;
	}*/
	
	/*public boolean compareXorSplitName (XorSplit xorsplit1, XorSplit xorsplit2)
	{
		if(xorsplit1.name.equals(xorsplit2.name)) 
		{
			return true;			
		}else return false;		
	}*/
	
	public void printXorSplit()
	{
		//System.out.println("\nXorSplit: ");
		Edge edgeprint = new Edge();
		this.Edge.printEdge();	
		edgeprint.printEdgeList(this.EdgeSet);
						
	}
	

 
}
