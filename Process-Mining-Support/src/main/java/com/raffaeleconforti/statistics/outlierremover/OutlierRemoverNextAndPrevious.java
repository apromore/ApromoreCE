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

import com.raffaeleconforti.outliers.Outlier;
import com.raffaeleconforti.outliers.OutlierIdentifier;
import com.raffaeleconforti.statistics.outlieridentifiers.OutlierIdentifierGenerator;
import com.raffaeleconforti.statistics.outlieridentifiers.SingleOutlierIdentifier;
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
public class OutlierRemoverNextAndPrevious extends OutlierRemoverAbstract {

    public OutlierRemoverNextAndPrevious(OutlierIdentifierGenerator outlierIdentifierGenerator, XEventClassifier xEventClassifier) {
        super(outlierIdentifierGenerator, xEventClassifier);
    }

    @Override
    public void selectOulierToRemove(XLog log, int lookAHead, boolean selectOnlyOneOutlier, boolean smallestOrLargest) {
        if(selectOnlyOneOutlier) {
            boolean outlearsFound = false;
            if(mapOutliers.size() > 0) {
                outlearsFound = true;
            }

            if (outlearsFound) {
                Map<Outlier<String>, Integer> removed = new UnifiedMap<Outlier<String>, Integer>();
                for (XTrace t : log) {
                    if(t.size() > 0) {
                        for (int i = lookAHead; i < t.size(); i++) {
                            checkIfOutlier(removed, t, i, lookAHead, true);
                        }
                    }
                }
                select(removed, smallestOrLargest);
            }
        }
    }

    @Override
    public void selectOulierToRemoveReverse(XLog log, int lookAHead, boolean selectOnlyOneOutlier, boolean smallestOrLargest) {
        if(selectOnlyOneOutlier) {
            boolean outlearsFound = false;
            if(mapOutliers.size() > 0) {
                outlearsFound = true;
            }

            if (outlearsFound) {
                Map<Outlier<String>, Integer> removed = new UnifiedMap<Outlier<String>, Integer>();
                for (XTrace t : log) {
                    if(t.size() > 0) {
                        for (int i = t.size() - lookAHead - 1; i >= 0; i--) {
                            checkIfOutlier(removed, t, i, lookAHead, false);
                        }
                    }
                }
                select(removed, smallestOrLargest);
            }
        }
    }

    private void checkIfOutlier(Map<Outlier<String>, Integer> removed, XTrace t, int i, int lookAHead, boolean directOrReverse) {
        XEvent current = directOrReverse?t.get((i - lookAHead)):t.get((i + lookAHead));
        XEvent next = t.get(i);
        String nameCurrent = getEventName(current);
        String nameNext = getEventName(next);

        OutlierIdentifier outlierIdentifier = outlierIdentifierGenerator.generate(nameCurrent);
        Set<Outlier<String>> set = mapOutliers.getOutliers(outlierIdentifier);
        Outlier<String> outlier = new Outlier<String>(nameNext, outlierIdentifier, false);

        if (set != null && set.contains(outlier)) {
            Integer val;
            if ((val = removed.get(outlier)) == null) {
                val = 0;
            }
            val++;
            removed.put(outlier, val);
        }
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

                for (int i = 0; i < lookAHead && i < t.size(); i++) {
                    XEvent current = (XEvent) t.get(i).clone();
                    newT.add(current);
                }

                for (int i = lookAHead; i < t.size(); i++) {
                    if(selectOnlyOneOutlier) {
                        removeOutlierSelectOnlyOne(t, newT, i, lookAHead, true);
                    }else {
                        removeOutlierSelect(t, newT, i, lookAHead, true);
                    }
                }
                if(newT.size() > 0) newLog.add(newT);
            }

            return newLog;
        }else {
            return log;
        }
    }

    @Override
    public XLog generateNewLogReverse(XLog log, OutlierIdentifierGenerator<String> outlierIdentifierGenerator, int lookAHead, boolean selectOnlyOneOutlier) {
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

                for (int i = t.size() - 1; i >= t.size() - lookAHead && i >= 0; i--) {
                    XEvent current = (XEvent) t.get(i).clone();
                    newT.add(current);
                }

                for (int i = t.size() - lookAHead - 1; i >= 0; i--) {
                    if(selectOnlyOneOutlier) {
                        removeOutlierSelectOnlyOne(t, newT, i, lookAHead, true);
                    }else {
                        removeOutlierSelect(t, newT, i, lookAHead, true);
                    }
                }
                if(newT.size() > 0) newLog.add(newT);
            }

            return newLog;
        }else {
            return log;
        }
    }

    private void removeOutlierSelectOnlyOne(XTrace t, XTrace newT, int i, int lookAHead, boolean directOrReverse) {
        XEvent current = directOrReverse?t.get((i - lookAHead)):t.get((i + lookAHead));
        XEvent next = t.get(i);
        String nameCurrent = getEventName(current);
        String nameNext = getEventName(next);

        if (!nameCurrent.equals(((SingleOutlierIdentifier) outlier.getIdentifier()).getIdentifier()) || !nameNext.equals(outlier.getElementToRemove())) {
            current = (XEvent) next.clone();
            newT.add(current);
        }
    }

    private void removeOutlierSelect(XTrace t, XTrace newT, int i, int lookAHead, boolean directOrReverse) {
        XEvent current = directOrReverse?t.get((i - lookAHead)):t.get((i + lookAHead));
        XEvent next = t.get(i);
        String nameCurrent = getEventName(current);
        String nameNext = getEventName(next);

        OutlierIdentifier outlierIdentifier = outlierIdentifierGenerator.generate(nameCurrent);
        Set<Outlier<String>> set = mapOutliers.getOutliers(outlierIdentifier);
        Outlier<String> outlier = new Outlier<String>(nameNext, outlierIdentifier, false);

        if (set == null || !set.contains(outlier)) {
            current = (XEvent) next.clone();
            newT.add(current);
        }
    }
}
