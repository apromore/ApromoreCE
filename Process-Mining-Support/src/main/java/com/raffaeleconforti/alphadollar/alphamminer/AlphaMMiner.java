/*
 *  Copyright (C) 2018 Raffaele Conforti (www.raffaeleconforti.com)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.raffaeleconforti.alphadollar.alphamminer;

import com.raffaeleconforti.log.util.LogReaderClassic;
import org.processmining.framework.models.petrinet.PetriNet;
import org.processmining.mining.logabstraction.LogRelations;

import java.util.Date;


public class AlphaMMiner {
	AlphaPPData alphaPPData = new AlphaPPData();

	public PetriNet mine(LogReaderClassic log, LogRelations relations, boolean heuristics)	{
		ModifiedAlphaPPProcessMiner alphapp = new ModifiedAlphaPPProcessMiner();
		ModifiedAlphaSharpProcessMiner alphasharp = new ModifiedAlphaSharpProcessMiner();
		Date d1 = new Date();
		alphaPPData = alphasharp.mineAlphaSharpInfo(log,relations, heuristics);
		Date d2 = new Date();
//		System.out.println("Alpha Sharp"+(d2.getTime() - d1.getTime()));
		PetriNet petriNet = alphapp.mineAlphPPInfo(log, alphaPPData);
		Date d3 = new Date();
//		System.out.println("Alpha PP"+(d3.getTime() - d2.getTime()));
		return petriNet;
	}
}


