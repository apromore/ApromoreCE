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

import com.raffaeleconforti.statistics.OutlierMap;
import com.raffaeleconforti.statistics.outlieridentifiers.OutlierIdentifierGenerator;
import org.deckfour.xes.model.XLog;

/**
 * Created by conforti on 12/02/15.
 */
public interface OutlierRemover {

    void setMapOutliers(OutlierMap<String> mapOutliers);

    void selectOulierToRemove(XLog log, int lookAHead, boolean selectOnlyOneOutlier, boolean smallestOrLargest);

    XLog generateNewLog(XLog log, OutlierIdentifierGenerator<String> outlierIdentifierGenerator, int lookAHead, boolean selectOnlyOneOutlier);

    void selectOulierToRemoveReverse(XLog log, int lookAHead, boolean selectOnlyOneOutlier, boolean smallestOrLargest);

    XLog generateNewLogReverse(XLog log, OutlierIdentifierGenerator<String> outlierIdentifierGenerator, int lookAHead, boolean selectOnlyOneOutlier);

}
