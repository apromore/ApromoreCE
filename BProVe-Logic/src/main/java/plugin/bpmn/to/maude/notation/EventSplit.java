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

public class EventSplit extends Gateway  implements java.io.Serializable{
	
	ArrayList<EventInterRcv> eventinterrcv;
	
	public EventSplit()
	{
				
	}
	
	public EventSplit(String eventsplit) {
		this.eventinterrcv = new ArrayList<EventInterRcv>();
		
		////System.out.println("EVENTSPLIT: "+eventsplit);
		
		Pattern pattern = Pattern.compile("^\\s*+\"");
		Matcher matcher = pattern.matcher(eventsplit);	
		
		if (matcher.find()) 
		{
			int startindex = matcher.group().length()-1;
			int endindex = eventsplit.indexOf(",");
			String substring = eventsplit.substring(startindex, endindex);
			////System.out.println("SUBSTRING: "+substring);
			this.Edge = new Edge(substring);
		}
		
		eventsplit = eventCatch(eventsplit);
		
		ArrayList<String> Split = TokenEventSplit(eventsplit);
	
		for(int i=0; i<Split.size(); i++)
		{
		this.eventinterrcv.add(new EventInterRcv(Split.get(i)));
		}
		
					
	}
	
	public String eventCatch(String eventsplit)
	{
		Pattern pattern2 = Pattern.compile("\\(+(eventInterRcv)+\\(");
		Matcher matcher2 = pattern2.matcher(eventsplit);	
		
		if (matcher2.find()) 
		{
			int startindex = eventsplit.indexOf(matcher2.group())+matcher2.group().length();
			eventsplit = eventsplit.substring(startindex);
		}
		
		return eventsplit;
	}
	
	public ArrayList<String> TokenEventSplit(String gateway)
	{
		ArrayList<String> SplitTokenGateway = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(gateway, "^");
		while (st.hasMoreTokens())
	    	{
				SplitTokenGateway.add(st.nextToken());
	    	}
		return SplitTokenGateway;	
	}
		
	public boolean compareEventBasedgat(EventSplit eventsplit1, EventSplit eventsplit2)
	{
		if(eventsplit1.Edge.EdgeName.equals(eventsplit2.Edge.EdgeName) 
				&& !eventsplit1.Edge.EdgeToken.equals(eventsplit2.Edge.EdgeToken))
		{
		return true;
		}else
		{
			return false;
		}
	}
	
	/*public boolean compareEventBasedgatName (EventSplit eventnbasedgat1, EventSplit eventnbasedgat2)
	{
		if(eventnbasedgat1.name.equals(eventnbasedgat2.name))				
		{
		return true;
		}else
		{
			return false;
		}
	}*/
	
	
	
	public void printEventSplit()
	{
		//System.out.println("\nEventSplit: ");
		this.Edge.printEdge();
		
		for(int k=0; k<this.eventinterrcv.size(); k++)
		{
			this.eventinterrcv.get(k).printInter();
		}
		
	}

}
