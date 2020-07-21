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
package ee.ut.nets.unfolding;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;

import org.jbpt.utils.IOUtils;
import org.junit.Test;

import ee.ut.nets.unfolding.BPstructBP.MODE;

public class CandidatePaperTest {
	@Test
	public void fistTrial() {
		PetriNet net = new PetriNet();
		Place p0 = net.addPlace("p0");
		Place p1 = net.addPlace("p1");
		Place p2 = net.addPlace("p2");
		Place p3 = net.addPlace("p3");
		Place p4 = net.addPlace("p4");
		Place p5 = net.addPlace("p5");
		
		Transition t0 = net.addTransition("A");
		Transition t1 = net.addTransition("B");
		Transition t2 = net.addTransition("D");
		Transition t3 = net.addTransition("C");
		Transition tau = net.addTransition("tau");
		Transition t4 = net.addTransition("E");

		net.addArc(p0, t0);
		net.addArc(t0, p1);
		net.addArc(t0, p2);
		net.addArc(p1, t1);
		net.addArc(p1, t2);
		net.addArc(p2, t2);
		net.addArc(p2, t3);
		net.addArc(p2, tau);
		
		net.addArc(t1, p3);
		net.addArc(t2, p3);
		net.addArc(t2, p4);
		net.addArc(t3, p4);
		net.addArc(tau, p4);
		
		net.addArc(p3, t4);
		net.addArc(p4, t4);
		net.addArc(t4, p5);
		
		p0.setTokens(1);
		
		Unfolder_PetriNet unfolder = new Unfolder_PetriNet(net, MODE.ESPARZA);
		unfolder.computeUnfolding();
		IOUtils.toFile("net1.dot", net.toDot());
		IOUtils.toFile("bp1.dot", unfolder.getUnfoldingAsDot());
	}
}
