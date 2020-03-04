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

package com.raffaeleconforti.statistics.qn;

import com.raffaeleconforti.statistics.StatisticsMeasureAbstract;
import org.eclipse.collections.impl.map.mutable.primitive.DoubleIntHashMap;

import java.util.Arrays;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 22/11/16.
 */
public class RightQn extends StatisticsMeasureAbstract {

    @Override
    public double evaluate(Double val, double... values) {
        try {
            values = Arrays.copyOf(values, values.length);
            Arrays.sort(values);

            DoubleIntHashMap map = new DoubleIntHashMap();
            double total = 0;

            for(int i = 0; i < values.length; i++) {
                if(values[i] >= val) {
                    int count = 1;
                    Double last = null;
                    for (int j = i + 1; j < values.length; j++) {
                        if(values[j] >= val) {
                            if (last == null) last = values[j];
                            else if (last == values[j]) count++;
                            else {
                                double key = Math.abs(values[i] - last);
                                int value = map.get(key);
                                map.put(key, value + count);
                                total += count;
                                last = values[j];
                                count = 1;
                            }
                        }
                    }
                    if(last != null) {
                        double key = Math.abs(values[i] - last);
                        int value = map.get(key);
                        map.put(key, value + count);
                        total += count;
                    }
                }
            }

            double[] keys = map.keySet().toArray();
            Arrays.sort(keys);

            int visited = 0;
            for(double key : keys) {
                visited += map.get(key);
                if(visited >= total * 0.25) {
                    return 2.219 * key;
                }
            }
        }catch (ArrayIndexOutOfBoundsException e) {

        }
        return 0;
    }

}
