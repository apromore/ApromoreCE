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

public class RelevantTransition {

	private String id; // Unique id of the transition (e.g., tr_0_0, tr_0_1)
					   // --ATTENTION-- tr_x_y is such that x is the unique id of the automaton where the transition takes place, 
					   // while y is the unique id of the transition within the automaton x.
	private String source_state; // Source state of the transition (e.g., s_0_0)	
	private String target_state; // Target state of the transition (e.g., s_0_1)	
	private String label; // Positive label of the transition (e.g., A, ..., B, C, D)	
	private String original_label; // Label of the transition (e.g., A, ..., !A)	
	private StringBuffer PDDL_preconditions; // PDDL Preconditions associated to the relevant transition [e.g., (currstate s_0_0) ]
	private StringBuffer PDDL_effects; // PDDL Effects associated to the relevant transition [e.g., (not (currstate s_0_0)) (currstate s_0_1) ]
	
	public RelevantTransition(String tr_id, String tr_source_state, String tr_target_state, String tr_label, String tr_original_label) {		
		id = tr_id;
		source_state = tr_source_state;
		target_state = tr_target_state;
		label = tr_label;
		original_label = tr_original_label;
		initializePreconditionsAndEffects();
	}

	public String getId() {
		return id;
	}

	public String getSource_state() {
		return source_state;
	}

	public String getTarget_state() {
		return target_state;
	}

	public String getLabel() {
		return label;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSource_state(String source_state) {
		this.source_state = source_state;
	}

	public void setTarget_state(String target_state) {
		this.target_state = target_state;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getOriginal_label() {
		return original_label;
	}

	public void setOriginal_label(String original_label) {
		this.original_label = original_label;
	}
	
	public StringBuffer getPDDL_preconditions() {
		return PDDL_preconditions;
	}

	public StringBuffer getPDDL_effects() {
		return PDDL_effects;
	}

	public void setPDDL_preconditions(StringBuffer pDDL_preconditions) {
		PDDL_preconditions = pDDL_preconditions;
	}

	public void setPDDL_effects(StringBuffer pDDL_effects) {
		PDDL_effects = pDDL_effects;
	}
	
	private void initializePreconditionsAndEffects() {
		PDDL_preconditions = new StringBuffer();
		PDDL_effects = new StringBuffer();
		PDDL_preconditions.append("(currstate " + source_state + ")");
		PDDL_effects.append("(not (currstate " + source_state + "))" + " (currstate " + target_state + ")");
	}

	public void printInformation() {
		System.out.println("ID : " + id + " - SOURCE STATE : " + source_state + " - TARGET STATE : " + target_state + " - PDDL PRECONDITIONS : " + PDDL_preconditions + " - PDDL EFFECTS : " + PDDL_effects + " - LABEL : " + label + " - ORIGINAL LABEL : " + original_label);
	}
	
}
