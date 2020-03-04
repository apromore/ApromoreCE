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

package com.raffaeleconforti.foreignkeydiscovery;

import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.Set;

/**
 * Created by Raffaele Conforti on 15/10/14.
 */
public class JaccardCoefficientEstimator {

    public static int computeEstimator(BottomKSketch bottomKSketch1, BottomKSketch bottomKSketch2, int k) {
        int rk_1 = Math.min(bottomKSketch1.getRankOfPlus1Sketch(k), bottomKSketch2.getRankOfPlus1Sketch(k));
        Set<Couple<String, Integer>> SCS = new UnifiedSet<Couple<String, Integer>>();

        Set<Couple<String, Integer>> S = new UnifiedSet<Couple<String, Integer>>(bottomKSketch1.getRankSketches(k));
        S.addAll(bottomKSketch2.getRankSketches(k));

        for(Couple<String, Integer> c : S) {
            if(c.getSecondElement() < rk_1) {
                SCS.add(c);
            }
        }

        return SCS.size();
    }

}
