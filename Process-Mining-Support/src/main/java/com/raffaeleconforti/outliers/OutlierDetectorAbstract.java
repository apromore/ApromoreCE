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

import com.raffaeleconforti.log.util.NameExtractor;
import com.raffaeleconforti.outliers.statistics.OutlierMap;
import com.raffaeleconforti.outliers.statistics.mapbuilder.OutlierMapBuilder;
import com.raffaeleconforti.outliers.statistics.outlieridentifiers.OutlierIdentifierGenerator;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.Map;

/**
 * Created by conforti on 10/02/15.
 */
public abstract class OutlierDetectorAbstract implements OutlierDetector {

    private final OutlierMapBuilder outlierMapBuilder = null;
    private OutlierIdentifierGenerator<String> outlierIdentifierGenerator = null;
    private final OutlierMap<String> map = new OutlierMap<String>();
    private final Map<OutlierIdentifier, Double> mapAverage = new UnifiedMap<OutlierIdentifier, Double>();
    private final OutlierMap<String> mapOutliers = new OutlierMap<String>();
    private final Map<String, Double> mapNumberOfEvents = new UnifiedMap<String, Double>();
    private final NameExtractor nameExtractor;

    public static final int STANDARDDEV= 0;
    public static final int MAD = 1;

    public OutlierDetectorAbstract(XEventClassifier xEventClassifier) {
        this.nameExtractor = new NameExtractor(xEventClassifier);
    }

    @Override
    public void setOutlierIdentifierGenerator(OutlierIdentifierGenerator<String> outlierIdentifierGenerator) {
        this.outlierIdentifierGenerator = outlierIdentifierGenerator;
    }

    @Override
    public void cleanMap() {
        map.clear();
        mapAverage.clear();
        mapOutliers.clear();
        mapNumberOfEvents.clear();
        outlierMapBuilder.clearMap();
    }

    @Override
    public void countEvents(XLog log) {
        for(XTrace trace : log) {
            for(XEvent event : trace) {
                String name = nameExtractor.getEventName(event);
                Double count;
                if((count = mapNumberOfEvents.get(name)) == null) {
                    count = 0.0;
                }
                count++;
                mapNumberOfEvents.put(name, count);
            }
        }
    }
}
