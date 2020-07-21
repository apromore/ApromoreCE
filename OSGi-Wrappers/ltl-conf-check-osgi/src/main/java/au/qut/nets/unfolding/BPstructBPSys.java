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
package au.qut.nets.unfolding;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.unfold.DNodeSys_PetriNet;
import hub.top.uma.InvalidModelException;

import java.util.Map.Entry;

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
	}
	public void packageProperNames() {
		finalize_setProperNames();
	}
}
