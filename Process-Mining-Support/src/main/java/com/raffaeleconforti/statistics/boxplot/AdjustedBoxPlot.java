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

/**
 * Created by conforti on 11/02/15.
 */
public class AdjustedBoxPlot extends BoxPlot {

    private Percentile percentile = new Percentile();

    @Override
    public double computeLowerBound(double[] values, double IQR) {
        double MC = Medcouple.evaluate(values);
        if(MC >= 0) {
            return (percentile.evaluate(0.25, values) - (1.5 * Math.exp(-4.0 * MC) * IQR));
        }else {
            return (percentile.evaluate(0.25, values) - (1.5 * Math.exp(-3.0 * MC) * IQR));
        }
//        return (Percentile.evaluate(0.25, values) - (1.5 * Math.exp(-3.5 * MC) * IQR));
    }

    @Override
    public double computeUpperBound(double[] values, double IQR) {
        double MC = Medcouple.evaluate(values);
        if(MC >= 0) {
            return (percentile.evaluate(0.75, values) + (1.5 * Math.exp(3.0 * MC) * IQR));
        }else {
            return (percentile.evaluate(0.75, values) + (1.5 * Math.exp(4.0 * MC) * IQR));
        }
//        return (Percentile.evaluate(0.75, values) + (1.5 * Math.exp(3.5 * MC) * IQR));
    }

}
