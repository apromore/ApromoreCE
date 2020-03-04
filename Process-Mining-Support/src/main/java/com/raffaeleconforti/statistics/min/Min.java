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

package com.raffaeleconforti.statistics.min;

import com.raffaeleconforti.statistics.StatisticsMeasureAbstract;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 14/11/16.
 */
public class Min extends StatisticsMeasureAbstract {

    @Override
    public double evaluate(Double val, double... values) {
        try {
            double min = Double.MAX_VALUE;
            for(double v : values) {
                min = Math.min(min, v);
            }
            return min;
        }catch (ArrayIndexOutOfBoundsException e) {

        }
        return 0;
    }

}
