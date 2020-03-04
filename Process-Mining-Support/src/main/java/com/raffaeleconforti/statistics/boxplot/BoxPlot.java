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

package com.raffaeleconforti.statistics.boxplot;

import com.raffaeleconforti.statistics.percentile.Percentile;

import java.util.ArrayList;

/**
 * Created by conforti on 11/02/15.
 */
public class BoxPlot {

    private Percentile percentile = new Percentile();

    public int[] discoverOutlier(double[] values, double significance) {
        ArrayList<Integer> result = new ArrayList<Integer>();

        double IQR = computeIQR(values);
        double lower = computeLowerBound(values, IQR);
        double upper = computeUpperBound(values, IQR);

        for(int j = 0; j < values.length; j++) {
            if (values[j] < lower || values[j] > upper) {
                result.add(j);
            }
        }

        int[] pos = new int[result.size()];
        int i = 0;
        for(Integer r : result) {
            pos[i] = r;
            i++;
        }

        return pos;
    }

    private double computeIQR(double[] values) {
        return (percentile.evaluate(0.75, values) - percentile.evaluate(0.25, values));
    }

    public double computeLowerBound(double[] values, double IQR) {
        return (percentile.evaluate(0.25, values) - (1.5 * IQR));
    }

    public double computeUpperBound(double[] values, double IQR) {
        return (percentile.evaluate(0.75, values) + (1.5 * IQR));
    }
}
