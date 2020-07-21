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

public class Trace {
	
	private String id; // Unique ID of the trace for internal use (e.g., Trace#1, ..., Trace#N).
					   // --- This value is STABLE after the transition between the TracesPerspective panel and the ConstraintsPerspective panel.
	
	private Vector<String> trace_alphabet_vector; // Alphabet of the activities of the trace (e.g., a,b,c,d, ... ecc.).
	
	private Vector<String> original_trace_content_vector; // Original and ordered content of the trace (e.g., a,b,a,c,d,a, ... ecc.).
	
	private Vector<String> original_transaction_id; // original id of the task in the BPMN model
	
	public Trace(String trace_id) {		
		id = trace_id;
		trace_alphabet_vector = new Vector<String>();
		original_trace_content_vector = new Vector<String>();
		original_transaction_id = new Vector<String>();
	}
	
	public Vector<String> getOriginal_transaction_id() {
		return original_transaction_id;
	}

	public void setOriginal_transaction_id(Vector<String> original_transaction_id) {
		this.original_transaction_id = original_transaction_id;
	}

	public String getTraceID() {
		return id;
	}
	
	public void setTraceID(String trace_ID) {
		id = trace_ID;
	}
	
	public Vector<String> getTraceAlphabet_vector() {
		return trace_alphabet_vector;
	}

	public void setTraceAlphabet_vector(Vector<String> tr_alphabet_vector) {
		this.trace_alphabet_vector = tr_alphabet_vector;
	}	
	
	public Vector<String> getOriginalTraceContent_vector() {
		return original_trace_content_vector;
	}



	public void setOriginalTraceContent_vector(Vector<String> trace_content) {
		original_trace_content_vector = trace_content;
	}

	public String getTraceNumber() {
		
		String[] split = this.getTraceID().split("#");
		return split[1];
	}

}
