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


public class MsgEvent extends Event  implements java.io.Serializable{
	
	public MsgEvent()
	{
		
	}
	
	public MsgEvent(String msgevent)
	{
		
	}
	
	public ArrayList<String> TokenMsgEvent(String msgevent)
	{
		ArrayList<String> TokenMsgEvent = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(msgevent, ",");
		while (st.hasMoreTokens())
	    	{
				TokenMsgEvent.add(st.nextToken());
	    	}
		return TokenMsgEvent;	
	}

	public ArrayList<String> MsgEventPointDivision(String MsgEvent)
		{
			ArrayList<String> MsgDivision = new ArrayList<String>();
			
		
				StringTokenizer st = new StringTokenizer(MsgEvent, ".");
				while (st.hasMoreTokens())
					{
						MsgDivision.add(st.nextToken());
					}
			
			return MsgDivision;	
		}		
}
