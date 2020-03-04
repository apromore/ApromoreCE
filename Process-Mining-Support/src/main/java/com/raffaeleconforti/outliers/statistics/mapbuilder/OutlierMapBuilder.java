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

package com.raffaeleconforti.outliers.statistics.mapbuilder;

import com.raffaeleconforti.outliers.statistics.OutlierMap;
import com.raffaeleconforti.outliers.statistics.outlieridentifiers.OutlierIdentifierGenerator;
import org.deckfour.xes.model.XLog;

/**
 * Created by conforti on 12/02/15.
 */
public interface OutlierMapBuilder {

    void clearMap();

    OutlierMap<String> buildOutliers(XLog log, int lookAHead, boolean smart);

    OutlierMap<String> buildOutliersReverse(XLog log, int lookAHead, boolean smart);

    void setOutlierIdentifierGenerator(OutlierIdentifierGenerator<String> outlierIdentifierGenerator);

}
