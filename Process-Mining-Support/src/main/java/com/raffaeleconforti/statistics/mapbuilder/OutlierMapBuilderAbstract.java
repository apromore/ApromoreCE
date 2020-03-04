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

import com.raffaeleconforti.log.util.NameExtractor;
import com.raffaeleconforti.statistics.outlieridentifiers.OutlierIdentifierGenerator;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XEvent;

/**
 * Created by conforti on 12/02/15.
 */
public abstract class OutlierMapBuilderAbstract implements OutlierMapBuilder {

    protected OutlierIdentifierGenerator<String> outlierIdentifierGenerator = null;
    private final NameExtractor nameExtractor;

    public OutlierMapBuilderAbstract(XEventClassifier xEventClassifier) {
        nameExtractor = new NameExtractor(xEventClassifier);
    }

    public void setOutlierIdentifierGenerator(OutlierIdentifierGenerator<String> outlierIdentifierGenerator) {
        this.outlierIdentifierGenerator = outlierIdentifierGenerator;
    }

    protected String getEventName(XEvent event) {
        return nameExtractor.getEventName(event);
    }

}
