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

package com.raffaeleconforti.statistics.mapbuilder;

import com.raffaeleconforti.statistics.outlieridentifiers.OutlierIdentifierGenerator;
import org.deckfour.xes.classification.XEventClassifier;

/**
 * Created by conforti on 12/02/15.
 */
public class MapBuilderFactory {

    public final static int NEXT_AND_PREVIOUS = 0;
    public final static int BRIDGE = 1;

    public static OutlierMapBuilder getOutlierMapBuilder(OutlierIdentifierGenerator<String> outlierIdentifierGenerator, int type, XEventClassifier xEventClassifier) {
        OutlierMapBuilder outlierMapBuilder;
        if(type == NEXT_AND_PREVIOUS) {
            outlierMapBuilder = new OutlierMapBuilderNextAndPrevious(xEventClassifier);
        }else {
            outlierMapBuilder = new OutlierMapBuilderBridge(xEventClassifier);
        }
        outlierMapBuilder.setOutlierIdentifierGenerator(outlierIdentifierGenerator);
        return outlierMapBuilder;
    }

}
