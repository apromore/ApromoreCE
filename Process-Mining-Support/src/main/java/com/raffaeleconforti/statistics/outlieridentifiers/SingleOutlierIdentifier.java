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

package com.raffaeleconforti.statistics.outlieridentifiers;

import com.raffaeleconforti.outliers.OutlierIdentifier;

/**
 * Created by conforti on 12/02/15.
 */
public class SingleOutlierIdentifier<T> implements OutlierIdentifier {

    private final T identifier1;

    public SingleOutlierIdentifier(T identifier) {
        this.identifier1 = identifier;
    }

    public T getIdentifier() {
        return identifier1;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof SingleOutlierIdentifier) {
            SingleOutlierIdentifier outlier = (SingleOutlierIdentifier) o;
            return outlier.getIdentifier().equals(identifier1);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return identifier1.hashCode();
    }

    @Override
    public String toString() {
        return identifier1.toString();
    }

}
