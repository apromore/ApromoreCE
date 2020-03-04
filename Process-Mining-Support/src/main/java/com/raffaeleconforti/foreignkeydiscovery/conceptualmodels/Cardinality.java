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

package com.raffaeleconforti.foreignkeydiscovery.conceptualmodels;

/**
 * Defines the possible cardinalities of relationships between entities
 *
 * @author Viara Popova
 */
public enum Cardinality {
    ZERO_OR_ONE("0..1"), ONE("1"), ZERO_OR_MANY("*"), ONE_OR_MANY("+");

    String label;

    Cardinality(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }

}
