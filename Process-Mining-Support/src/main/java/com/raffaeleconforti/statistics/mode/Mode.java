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

package com.raffaeleconforti.statistics.mode;

import com.raffaeleconforti.statistics.StatisticsMeasureAbstract;

import java.util.Arrays;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 14/11/16.
 */
public class Mode extends StatisticsMeasureAbstract {

    @Override
    public double evaluate(Double val, double... values) {
        try {
            values = Arrays.copyOf(values, values.length);
            Arrays.sort(values);

            Double mode = null;
            int modeCount = 0;

            Double value = null;
            int count = 0;

            for(double v : values) {
                if(value == null || value != v) {
                    value = v;
                    count = 0;
                }
                count++;

                if(count > modeCount) {
                    modeCount = count;
                    mode = value;
                }
            }

            return mode;
        }catch (ArrayIndexOutOfBoundsException e) {

        }
        return 0;
    }

}
