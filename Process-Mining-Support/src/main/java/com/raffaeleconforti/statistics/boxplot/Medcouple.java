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

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by conforti on 11/02/15.
 */
public class Medcouple {

    public static double evaluate(double... values) {
        double[] x = Arrays.copyOf(values, values.length);
        Arrays.sort(x);

        double median = median(x);
        ArrayList<Double> res = new ArrayList<Double>();

        for(int i = 0; i < x.length && x[i] <= median; i++) {
            for(int j = 0; j < x.length; j++) {
                if(x[j] >= median) {
                    res.add(kernel(x[i], x[j], median));
                }
            }
        }

        Double[] result = res.toArray(new Double[res.size()]);
        Arrays.sort(result);
        return median(result);
    }

    private static double median(Double... values) {
        if(values.length % 2 == 0) {
            int half = values.length / 2;
            return (values[half - 1] + values[half]) / 2.0;
        }else {
            int half = (int) Math.floor(values.length / 2);
            return values[half];
        }
    }

    private static double median(double... values) {
        if(values.length % 2 == 0) {
            int half = values.length / 2;
            return (values[half - 1] + values[half]) / 2.0;
        }else {
            int half = (int) Math.floor(values.length / 2);
            return values[half];
        }
    }

    private static double kernel(double x_i, double x_j, double median) {
        return ((x_j - median) - (median - x_i)) / (x_j - x_i);
    }

}
