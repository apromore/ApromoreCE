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

package com.raffaeleconforti.statistics.standarddeviation;

import com.raffaeleconforti.statistics.StatisticsMeasureAbstract;
import com.raffaeleconforti.statistics.mean.Mean;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 14/11/16.
 */
public class LeftStandardDeviation extends StatisticsMeasureAbstract {

    private Mean mean = new Mean();

    @Override
    public double evaluate(Double val, double... values) {
        try {
            double avg = mean.evaluate(null, values);
            double sd = 0;
            int count = 0;
            for(int i = 0; i < values.length; i++) {
                if(values[i] <= val) {
                    sd += Math.pow((values[i] - avg), 2);
                    count++;
                }
            }
            return Math.sqrt(sd / (count - 1));
        }catch (ArrayIndexOutOfBoundsException e) {

        }
        return 0;
    }

}
