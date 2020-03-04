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

package com.raffaeleconforti.foreignkeydiscovery;

import java.util.Arrays;

/**
 * Created by Raffaele Conforti on 17/10/14.
 */
public class Cell implements Comparable<Cell>{

    private int[] cellPosition;
    private Integer hashCode;

    public Cell(int[] cellPosition) {
        this.cellPosition = Arrays.copyOf(cellPosition, cellPosition.length);
    }

    public int[] getCellPosition() {
        return cellPosition;
    }

    @Override
    public String toString() {
        return Arrays.toString(cellPosition);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Cell) {
            Cell c = (Cell) o;
            for(int i = 0; i < cellPosition.length; i++) {
                if(c.cellPosition[i] != this.cellPosition[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if(hashCode == null) {
            hashCode = Arrays.hashCode(cellPosition);
        }
        return hashCode;
    }

    @Override
    public int compareTo(Cell o) {
        for(int i = 0; i < cellPosition.length; i++) {
            int result = Integer.valueOf(cellPosition[i]).compareTo(o.cellPosition[i]);
            if(result != 0) {
                return result;
            }
        }
        return 0;
    }
}
