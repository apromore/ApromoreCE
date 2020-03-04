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

package com.raffaeleconforti.statistics.percentile;

import com.raffaeleconforti.statistics.StatisticsMeasureAbstract;

import java.util.Arrays;

/**
 * Created by conforti on 11/02/15.
 */
public class Percentile extends StatisticsMeasureAbstract {

    @Override
    public double evaluate(Double percentile, double... values) {
        try {
            values = Arrays.copyOf(values, values.length);
            Arrays.sort(values);
            int pos = (int) Math.round(values.length * percentile) - 1;
            if(pos < 0) pos = 0;
            return values[pos];
        }catch (ArrayIndexOutOfBoundsException e) {

        }
        return 0;
    }
}
