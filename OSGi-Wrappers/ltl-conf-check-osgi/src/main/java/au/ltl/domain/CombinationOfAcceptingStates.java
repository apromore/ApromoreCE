/*-
 * #%L
 * This file is part of "Apromore Community".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package au.ltl.domain;

import java.util.Vector;

public class CombinationOfAcceptingStates {

	private String id;
			
	private Vector<String> combination_of_accepting_states_vector; // Vector containing a unique combinations of accepting states related to the automata representing LTL/Declare constraints. 
		
	public CombinationOfAcceptingStates(String cosID, Vector<String> vector_with_combination_of_states) {		
		id = cosID;
		combination_of_accepting_states_vector = vector_with_combination_of_states;
	}

	public Vector<String> getCombinationOfAcceptingStates_vector() {
		return combination_of_accepting_states_vector;
	}

	public void setCombinationOfAcceptingStates_vector(Vector<String> coas_vector) {
		this.combination_of_accepting_states_vector = coas_vector;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
