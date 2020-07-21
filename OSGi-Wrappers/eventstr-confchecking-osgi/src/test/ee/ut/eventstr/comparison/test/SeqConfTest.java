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
package ee.ut.eventstr.comparison.test;

import java.util.Arrays;

import org.jbpt.utils.IOUtils;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ee.ut.eventstr.PESSemantics;
import ee.ut.eventstr.PrimeEventStructure;
import ee.ut.eventstr.comparison.PartialSynchronizedProduct;
import ee.ut.mining.log.poruns.pes.PORuns2PES;

public class SeqConfTest {
	@Test
	public void testCode() {
		PrimeEventStructure<Integer> pes1 = getPES1(), pes2 = getPES2();

		PartialSynchronizedProduct<Integer> psp = 
				new PartialSynchronizedProduct<>(new PESSemantics<>(pes1), new PESSemantics<>(pes2));
		
		for (String diff: psp.perform().prune().getDiff()) {
			System.out.println("DIFF: " + diff);
		}

		IOUtils.toFile("target/seq_conf.dot", psp.toDot());
	}

	public PrimeEventStructure<Integer> getPES1() {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(0, 2);
		Multimap<Integer, Integer> conc = HashMultimap.create();

		return PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(1,2), Arrays.asList("a", "b", "c"), "PES1");
	}
	public PrimeEventStructure<Integer> getPES2() {
		Multimap<Integer, Integer> adj = HashMultimap.create();
		adj.put(0, 1);
		adj.put(1, 2);
		Multimap<Integer, Integer> conc = HashMultimap.create();

		return PORuns2PES.getPrimeEventStructure(
				adj, conc, Arrays.asList(0), Arrays.asList(2), Arrays.asList("a", "b", "c"), "PES2");
	}
}
