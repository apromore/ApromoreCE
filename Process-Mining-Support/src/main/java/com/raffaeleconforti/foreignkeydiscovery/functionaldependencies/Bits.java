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

package com.raffaeleconforti.foreignkeydiscovery.functionaldependencies;

/**
 * This class provides basic methods for clearing, setting and testing bits on
 * an integer variable. The underlying code has been adapted from the book
 * "Java ist eine Insel, C. Ulleboom".
 *
 * @author Tobias
 */
public class Bits {
    /**
     * Sets the bit at the specified position to true.
     * <p/>
     * The position range from 0 to 31
     *
     * @param n   The bit coded integer value
     * @param pos The bit position to be set true
     * @return The new coded integer bit
     */
    public static int setBit(int n, int pos) {
        return n | (1 << pos);
    }

    /**
     * Sets the bit at the specified position to false.
     * <p/>
     * The position range from 0 to 31
     *
     * @param n   The bit coded integer value
     * @param pos The bit position to be set false
     * @return The new coded integer bit
     */

    public static int clearBit(int n, int pos) {
        return n & ~(1 << pos);
    }

    /**
     * Tests if the bit at the specified position is true.
     * <p/>
     * The position range from 0 to 31
     *
     * @param n   The bit coded integer value
     * @param pos The bit position to be set false
     * @return True, if bit at specified position is true, false otherwise
     */
    public static boolean testBit(int n, int pos) {
        return (n & 1 << pos) != 0;
    }
}
