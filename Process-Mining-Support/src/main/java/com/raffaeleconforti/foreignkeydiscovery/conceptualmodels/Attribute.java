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

package com.raffaeleconforti.foreignkeydiscovery.conceptualmodels;

/**
 * Defines an attribute of an entity or an artifact An attribute can only belong
 * to one entity or artifact. An attribute with the same set of values and
 * semantics in a different entity/artifact will have to be created separately.
 * It can have the same name and type.
 *
 * @author Viara Popova
 */
public class Attribute implements Comparable<Attribute> {
    private final String name;
    private AttributeType type;
    private String dataType;

    public Attribute(String name, AttributeType type, String dataType) {
        this.name = name;
        this.type = type;
        this.dataType = dataType;
    }

    public String getName() {
        return name;
    }

    public AttributeType getType() {
        return type;
    }

    public void setType(AttributeType t) {
        this.type = t;
    }

    public String getDataType() {
        return dataType;
    }

    public int compareTo(Attribute o) {
        if (o.name.equals(name)) {
            if (o.dataType.equals(dataType)) {
                if (o.type.equals(type)) {
                    return 0;
                }
                return type.compareTo(o.type);
            }
            return dataType.compareTo(o.dataType);
        }
        return name.compareTo(o.name);
    }

}
