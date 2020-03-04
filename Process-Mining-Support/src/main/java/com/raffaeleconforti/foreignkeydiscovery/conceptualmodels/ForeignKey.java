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
 * A foreign key consisting of the attribute which is a foreign key and the
 * entity and attribute to which it points (key or part of it)
 *
 * @author Viara Popova
 */
public class ForeignKey implements Comparable<ForeignKey> {
    private Attribute fk;
    private Entity ent; //entity
    private Attribute key;//key attribute

    public ForeignKey(Attribute f, Entity e, Attribute k) {
        this.fk = f;
        this.ent = e;
        this.key = k;
    }

    public Attribute getFKey() {
        return fk;
    }

    public Entity getEntity() {
        return ent;
    }

    public Attribute getKey() {
        return key;
    }

    public int compareTo(ForeignKey o) {
        if (ent.equals(o.ent)) {
            if (fk.equals(o.fk)) {
                if (key.equals(o.fk)) {
                    return 0;
                }
                return key.compareTo(o.key);
            }
            return fk.compareTo(o.fk);
        }
        return ent.compareTo(o.ent);
    }
}
