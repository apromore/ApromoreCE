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

package com.raffaeleconforti.statistics.sn;

import com.raffaeleconforti.statistics.StatisticsMeasureAbstract;
import com.raffaeleconforti.statistics.median.Median;

import java.util.Arrays;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 22/11/16.
 */
public class Sn extends StatisticsMeasureAbstract {

    private Median median = new Median();

    @Override
    public double evaluate(Double val, double... values) {
        try {
            values = Arrays.copyOf(values, values.length);
            Arrays.sort(values);

            double[] v = new double[values.length];
            for(int i = 0; i < values.length; i++) {
                double[] v1 = new double[values.length];
                for(int j = 0; j < values.length; j++) {
                    v1[i] = Math.abs(values[i] - values[j]);
                }
                v[i] = median.evaluate(null, v1);
            }
            return 1.1925 * median.evaluate(null, v);
        }catch (ArrayIndexOutOfBoundsException e) {

        }
        return 0;
    }

}
