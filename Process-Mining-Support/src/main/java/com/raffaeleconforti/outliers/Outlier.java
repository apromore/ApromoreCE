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

/**
 * Created by conforti on 12/02/15.
 */
public class Outlier<T> {

    private final T elementToRemove;
    private final OutlierIdentifier identifier;
    private final boolean real;

    public Outlier(T elementToRemove, OutlierIdentifier identifier, boolean real) {
        this.elementToRemove = elementToRemove;
        this.identifier = identifier;
        this.real = real;
    }

    public boolean isReal() {
        return real;
    }

    public T getElementToRemove() {
        return elementToRemove;
    }

    public OutlierIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Outlier) {
            Outlier outlier = (Outlier) o;
            return outlier.getIdentifier().equals(identifier) && outlier.getElementToRemove().equals(elementToRemove);
        }
        return false;
    }

    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(identifier.toString()).append("+").append(elementToRemove.toString());
        return sb.toString().hashCode();
    }
}
