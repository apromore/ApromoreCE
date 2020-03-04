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

package com.raffaeleconforti.statistics.medianabsolutedeviation;

import com.raffaeleconforti.statistics.StatisticsMeasureAbstract;
import com.raffaeleconforti.statistics.median.Median;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;

import java.util.Arrays;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 14/11/16.
 */
public class LeftMedianAbsoluteDeviation extends StatisticsMeasureAbstract {

    private Median median = new Median();

    @Override
    public double evaluate(Double val, double... values) {
        try {
            values = Arrays.copyOf(values, values.length);
            Arrays.sort(values);
            double med = median.evaluate(null, values);
            DoubleArrayList vals = new DoubleArrayList();
            for(int i = 0; i < values.length && values[i] <= val; i++) {
                vals.add(Math.abs(values[i] - med));
            }
            return 1.4826 * median.evaluate(null, vals.toArray());
        }catch (ArrayIndexOutOfBoundsException e) {

        }
        return 0;
    }

}
