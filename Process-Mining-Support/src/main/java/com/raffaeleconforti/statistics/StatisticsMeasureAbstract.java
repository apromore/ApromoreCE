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

package com.raffaeleconforti.statistics;

public abstract class StatisticsMeasureAbstract implements StatisticsMeasure {

    public double evaluate(Float val, float... values) {
        Double val2 = (val != null) ? Double.valueOf(val) : null;
        double[] values2 = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            values2[i] = values[i];
        }
        return evaluate(val2, values2);
    }

    public double evaluate(Long val, long... values) {
        Double val2 = (val != null) ? Double.valueOf(val) : null;
        double[] values2 = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            values2[i] = values[i];
        }
        return evaluate(val2, values2);
    }

    public double evaluate(Integer val, int... values) {
        Double val2 = (val != null) ? Double.valueOf(val) : null;
        double[] values2 = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            values2[i] = values[i];
        }
        return evaluate(val2, values2);
    }


}
