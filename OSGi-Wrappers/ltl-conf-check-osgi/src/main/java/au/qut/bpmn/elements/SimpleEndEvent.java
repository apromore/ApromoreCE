/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package au.qut.bpmn.elements;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jdom2.Element;

public class SimpleEndEvent extends Node {
	protected Transition positiveCase;
		
	public SimpleEndEvent(Element element, PetriNet net) {
		super(element, net);
		
		this.positiveCase = new Transition(element.getAttributeValue("name"));
				
		net.addFlow(inputPlace, positiveCase);
	}
	
	public void connectTo(Place place) {
		net.addFlow(positiveCase, place);
	}
		
	public void connectToOk(Place ok) {
		net.addFlow(ok, positiveCase);
	}
	
	public void connectToNOk(Place nok) {
		net.addFlow(positiveCase, nok);
	}

	@Override
	public org.jbpt.petri.Node getTransition() {
		return positiveCase;
	}
}
