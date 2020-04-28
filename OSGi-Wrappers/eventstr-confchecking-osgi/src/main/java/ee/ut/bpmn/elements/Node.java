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

package ee.ut.bpmn.elements;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jdom2.Element;

public abstract class Node {
	protected Subprocess parent;
	protected PetriNet net;
	
	protected Place inputPlace;
	
	public Node(Element element, PetriNet net) {
		this.parent = null;
		this.net = net;
		this.inputPlace = new Place("p"+net.getPlaces().size());
	}

	public Subprocess getParent() {
		return parent;
	}

	public void setParent(Subprocess parent) {
		this.parent = parent;
	}
	
	public Place getInputPlace() {
		return inputPlace;
	}
	
	public abstract void connectTo(Place place);

	public abstract org.jbpt.petri.Node getTransition();
}
