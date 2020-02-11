package au.ltl.utils;
/* 
 * Copyright (C) 2010 - Artem Polyvyanyy, Luciano Garcia Banuelos
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.Map.Entry;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.unfold.DNodeSys_PetriNet;
import hub.top.uma.InvalidModelException;

/**
 * This class is a modification to the original implementation provided in uma package
 */
public class BPstructBPSys extends DNodeSys_PetriNet {

	public BPstructBPSys(PetriNet net) throws InvalidModelException {
		super(net);
	}
	
	protected void finalize_setProperNames() {
		uniqueNames = new String[nameToID.size()];
		for (Entry<String,Short> line : nameToID.entrySet()) {
			uniqueNames[line.getValue()] = line.getKey();
		}

		super.finalize_setProperNames();
	}
	public void packageProperNames() {
		finalize_setProperNames();
	}
}
