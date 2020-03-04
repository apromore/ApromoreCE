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

package com.raffaeleconforti.outliers;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 22/12/17.
 */

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.StatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class GrubbsTest {

    public List<Double> getOutliers(ArrayList<Double> values, double significanceLevel) {
        List<Double> outliers = new ArrayList<>();
        Double outlier = getOutlier(values, significanceLevel);
        if(outlier != null) {
            outlier = Math.abs(outlier);
            outliers.add(outlier);
            ArrayList<Double> new_values = new ArrayList<>();
            for(int i = 0; i < values.size(); i++) {
                if(Math.abs(values.get(i)) != outlier) {
                    new_values.add(values.get(i));
                }
            }
            GrubbsTest t = new GrubbsTest();
            List<Double> new_outliers = t.getOutliers(new_values, significanceLevel);
            outliers.addAll(new_outliers);
        }
        return outliers;
    }

    public Double getOutlier(ArrayList<Double> values, double significanceLevel) {
        AtomicReference<Double> outlier = new AtomicReference<Double>();
        double size = values.size();
        if (size < 3) {
            return null;
        }
        if (getGrubbs(values, outlier) > getGrubbsCompareValue(values, significanceLevel, size)) {
            return outlier.get();
        } else {
            return null;
        }
    }

    public Double getGrubbsCompareValue(ArrayList<Double> values, double significanceLevel, double size) {
        TDistribution tDistribution = new TDistribution(size - 2.0);
        double criticalValue = tDistribution.inverseCumulativeProbability((1.0 - significanceLevel) / (2.0 * size));
        double criticalValueSquare = criticalValue * criticalValue;
        return ((size - 1) / Math.sqrt(size)) * Math.sqrt((criticalValueSquare) / (size - 2.0 + criticalValueSquare));
    }

    public Double getGrubbs(ArrayList<Double> values, AtomicReference<Double> outlier) {
        double[] array = toArray(values);
        double mean = StatUtils.mean(array);
        double standardDeviation = standardDeviation(values);
        double maximalDeviation = 0;
        for (Double value : values) {
            if (Math.abs(mean - value) > maximalDeviation) {
                maximalDeviation = Math.abs(mean - value);
                outlier.set(value);
            }
        }
        return maximalDeviation / standardDeviation;
    }

    public Double standardDeviation(ArrayList<Double> values) {
        return standardDeviation(toArray(values));
    }

    public Double standardDeviation(double[] values) {
        return Math.sqrt(StatUtils.variance(values));
    }

    public double[] toArray(ArrayList<Double> values) {
        return values.stream().mapToDouble(d -> d).toArray();
    }
}
