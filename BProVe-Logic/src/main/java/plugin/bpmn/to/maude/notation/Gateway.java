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


public class Gateway extends ProcElement  implements java.io.Serializable{
	
	ArrayList<Edge> EdgeSet;
	public Edge Edge;	
	GatewayType Gtype;
	
	public enum GatewayType{Join, Split};
	
	public Gateway()
	{
		
	}
	
	public ArrayList<String> TokenGateway(String gateway)
	{
		ArrayList<String> SplitTokenGateway = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(gateway, ",");
		while (st.hasMoreTokens())
	    	{
				SplitTokenGateway.add(st.nextToken());
	    	}
		return SplitTokenGateway;	
	}
	
	public String extractEdges(String TokenGateway)
	{
			String edges = null;
			Pattern patternextractEdges = Pattern.compile("^(edges\\()|^\\s*+(edges\\()");
			Matcher matcherextractEdges = patternextractEdges.matcher(TokenGateway);
			if (matcherextractEdges.find())
			{
				edges = TokenGateway.substring(matcherextractEdges.group().length());				
			}
									
			return edges;					
	}			
}