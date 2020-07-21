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

package ee.ut.nets.unfolding;

public enum Multiplicity {
	ZERO,
	ONE,
	ZERO_ONE,
	ZERO_MORE,
	ONE_MORE,
	LOOP_ZERO_MORE,
	LOOP_ONE_MORE; // an event can be preceded by itself
	
	public static String getSymbol(Multiplicity multiplicity) {
		switch (multiplicity) {
		case LOOP_ZERO_MORE:
			return "*\\circlearrowright";
		case LOOP_ONE_MORE:
			return "+\\circlearrowright";
		case ONE:
			return "1";
		case ONE_MORE:
			return "+";
		case ZERO:
			return "0";
		case ZERO_MORE:
			return "*";
		case ZERO_ONE:
			return "0..1";
		default:
			break;
		}

		return "";
	}
}
