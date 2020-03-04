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

package com.raffaeleconforti.statistics.outlierremover;

import com.raffaeleconforti.log.util.NameExtractor;
import com.raffaeleconforti.outliers.Outlier;
import com.raffaeleconforti.statistics.OutlierMap;
import com.raffaeleconforti.statistics.outlieridentifiers.OutlierIdentifierGenerator;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;

import java.util.Map;

/**
 * Created by conforti on 12/02/15.
 */
public abstract class OutlierRemoverAbstract implements OutlierRemover {

    protected final OutlierIdentifierGenerator<String> outlierIdentifierGenerator;
    protected OutlierMap<String> mapOutliers;
    protected Outlier<String> outlier;
    private final NameExtractor nameExtractor;

    public OutlierRemoverAbstract(OutlierIdentifierGenerator outlierIdentifierGenerator, XEventClassifier xEventClassifier) {
        this.outlierIdentifierGenerator = outlierIdentifierGenerator;
        this.nameExtractor = new NameExtractor(xEventClassifier);
    }

    public void setMapOutliers(OutlierMap<String> mapOutliers) {
        this.mapOutliers = mapOutliers;
    }

    protected void select(Map<Outlier<String>, Integer> removed, boolean smallestOrLargest) {
        int min = (smallestOrLargest)?Integer.MAX_VALUE:0;
        for (Map.Entry<Outlier<String>, Integer> entry : removed.entrySet()) {
            boolean removeSafe = true;
            for(Outlier<String> outlier1 : mapOutliers.getOutliers(entry.getKey().getIdentifier())) {
                if(!outlier1.isReal()) {
                    removeSafe = false;
                    System.out.println("not safe");
                    break;
                }
            }
            if(removeSafe) {
                if (smallestOrLargest && entry.getValue() > 0 && entry.getValue() < min) {
                    min = entry.getValue();
                    outlier = entry.getKey();
                } else {
                    if (!smallestOrLargest && entry.getValue() > 0 && entry.getValue() > min) {
                        min = entry.getValue();
                        outlier = entry.getKey();
                    }
                }
            }
        }
    }

    protected String getEventName(XEvent event) {
        return nameExtractor.getEventName(event);
    }

}
