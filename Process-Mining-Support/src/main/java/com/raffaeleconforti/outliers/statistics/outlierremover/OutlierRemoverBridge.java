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

package com.raffaeleconforti.outliers.statistics.outlierremover;

import com.raffaeleconforti.outliers.Outlier;
import com.raffaeleconforti.outliers.OutlierIdentifier;
import com.raffaeleconforti.outliers.statistics.outlieridentifiers.DoubleOutlierIdentifier;
import com.raffaeleconforti.outliers.statistics.outlieridentifiers.OutlierIdentifierGenerator;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.Map;
import java.util.Set;

/**
 * Created by conforti on 12/02/15.
 */
public class OutlierRemoverBridge extends OutlierRemoverAbstract {

    public OutlierRemoverBridge(OutlierIdentifierGenerator outlierIdentifierGenerator, XEventClassifier xEventClassifier) {
        super(outlierIdentifierGenerator, xEventClassifier);
    }

    @Override
    public void selectOulierToRemove(XLog log, int lookAHead, boolean selectOnlyOneOutlier, boolean smallestOrLargest) {
        if(selectOnlyOneOutlier) {
            boolean outlearsFound = false;
            if(mapOutliers.size() > 0) {
                outlearsFound = true;
            }

            Map<Outlier<String>, Integer> removed = new UnifiedMap<Outlier<String>, Integer>();
            if (outlearsFound) {
                for (XTrace t : log) {
                    if(t.size() > 0) {
                        for (int i = 1; i < t.size() - 1; i++) {
                            XEvent before = t.get(i - 1);
                            XEvent guilty = t.get(i);
                            XEvent after = t.get(i + 1);

                            String nameBefore = getEventName(before);
                            String nameGuilty = getEventName(guilty);
                            String nameAfter = getEventName(after);

                            OutlierIdentifier outlierIdentifier = outlierIdentifierGenerator.generate(nameBefore, nameAfter);
                            Set<Outlier<String>> set = mapOutliers.getOutliers(outlierIdentifier);
                            Outlier<String> outlier1 = new Outlier<String>(nameGuilty, outlierIdentifier, false);

                            if (set != null && set.contains(outlier1)) {
                                Integer val;
                                if ((val = removed.get(outlier1)) == null) {
                                    val = 0;
                                }
                                val++;
                                removed.put(outlier1, val);
                            }
                        }
                    }
                }
                select(removed, smallestOrLargest);
            }
        }
    }

    @Override
    public void selectOulierToRemoveReverse(XLog log, int lookAHead, boolean selectOnlyOneOutlier, boolean smallestOrLargest) {
        selectOulierToRemove(log, lookAHead, selectOnlyOneOutlier, smallestOrLargest);
    }

    @Override
    public XLog generateNewLog(XLog log, OutlierIdentifierGenerator<String> outlierIdentifierGenerator, int lookAHead, boolean selectOnlyOneOutlier) {
        boolean outlearsFound = false;
        if(mapOutliers.size() > 0) {
            outlearsFound = true;
        }

        if(outlearsFound) {
            XLog newLog = (XLog) log.clone();
            newLog.clear();

            for (XTrace t : log) {
                XTrace newT = (XTrace) t.clone();
                newT.clear();

                newT.add((XEvent) t.get(0).clone());
                for (int i = 1; i < t.size() - 1; i++) {
                    if(selectOnlyOneOutlier) {
                        removeOutlierSelectOnlyOne(t, newT, i);
                    }else {
                        removeOutlierSelect(t, newT, i);
                    }
                }
                newT.add((XEvent) t.get(t.size()-1).clone());
                if(newT.size() > 0) newLog.add(newT);
            }

            return newLog;
        }else {
            return log;
        }
    }

    @Override
    public XLog generateNewLogReverse(XLog log, OutlierIdentifierGenerator<String> outlierIdentifierGenerator, int lookAHead, boolean selectOnlyOneOutlier) {
        return generateNewLog(log, outlierIdentifierGenerator, lookAHead, selectOnlyOneOutlier);
    }

    private void removeOutlierSelectOnlyOne(XTrace t, XTrace newT, int i) {
        XEvent before = t.get(i - 1);
        XEvent guilty = t.get(i);
        XEvent after = t.get(i + 1);

        String nameBefore = getEventName(before);
        String nameGuilty = getEventName(guilty);
        String nameAfter = getEventName(after);

        if (!nameBefore.equals(((DoubleOutlierIdentifier) outlier.getIdentifier()).getIdentifier1())
                || !nameAfter.equals(((DoubleOutlierIdentifier) outlier.getIdentifier()).getIdentifier2())
                || !nameGuilty.equals(outlier.getElementToRemove())) {
            newT.add((XEvent) t.get(i).clone());
        }
    }

    private void removeOutlierSelect(XTrace t, XTrace newT, int i) {
        XEvent before = t.get(i - 1);
        XEvent guilty = t.get(i);
        XEvent after = t.get(i + 1);

        String nameBefore = getEventName(before);
        String nameGuilty = getEventName(guilty);
        String nameAfter = getEventName(after);

        OutlierIdentifier outlierIdentifier = outlierIdentifierGenerator.generate(nameBefore, nameAfter);
        Set<Outlier<String>> set = mapOutliers.getOutliers(outlierIdentifier);
        Outlier<String> outlier = new Outlier<String>(nameGuilty, outlierIdentifier, false);

        if (set == null || !set.contains(outlier)) {
            newT.add((XEvent) t.get(i).clone());
        }
    }
}
