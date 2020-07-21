/*-
 * #%L
 * This file is part of "Apromore Community".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package au.qut.bpmn.elements;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jdom2.Element;

public class TerminateEvent extends Node {
	private Transition positiveCase;
	private Transition negativeCase;
	
	private Transition subprocessCompletionSynchronisation;
	private Place internalPlace;

	public TerminateEvent(Element element, PetriNet net) {
		super(element, net);
		
		this.positiveCase = new Transition(element.getAttributeValue("name"));
		this.negativeCase = new Transition();
		
		net.addFlow(inputPlace, positiveCase);
		net.addFlow(inputPlace, negativeCase);
		
		this.subprocessCompletionSynchronisation = new Transition();
		this.internalPlace = new Place();
		net.addFlow(positiveCase, internalPlace);
		net.addFlow(internalPlace, subprocessCompletionSynchronisation);
	}
	
	public void connectTo(Place place) {
		throw new RuntimeException("Malformed BPMN model: There is a terminate event with a successor");
	}
	
	public void connectToOk(Place ok) {
		net.addFlow(ok, positiveCase);
	}
	
	public void setParent(Subprocess parent) {
		net.addFlow(subprocessCompletionSynchronisation, parent.getSinkPlace());
	}
	
	public void connectToNOk(Place nok) {
		net.addFlow(negativeCase, nok);
		net.addFlow(nok, negativeCase);
		net.addFlow(positiveCase, nok);
		net.addFlow(nok, subprocessCompletionSynchronisation);
	}
	
	public void synchroniseWith(Place place) {
//		net.addFlow(place, subprocessCompletionSynchronisation);
	}

	@Override
	public org.jbpt.petri.Node getTransition() {
	return positiveCase;
	}
}
