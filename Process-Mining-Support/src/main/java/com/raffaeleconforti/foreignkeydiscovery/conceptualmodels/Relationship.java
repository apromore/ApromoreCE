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

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;

/**
 * Defines a relationship between entities and the corresponding cardinalities
 * The distinction between which node is source and which target is arbitrary.
 * This is so that the generality is preserved. In practice many situations can
 * arise when discovering inclusion dependencies, for example inclusion
 * dependencies both fk.A -> k.B and fk.B -> k.A for tables A and B. The
 * implementation can be changed to assume foreign key -> primary key order
 * which is how a new relationship is added in getDependencies().
 *
 * @author Viara Popova
 */
public class Relationship extends AbstractDirectedGraphEdge<Entity, Entity> {

    private final Entity id1; //source
    private final Entity id2; //target
    private Cardinality st_cardinality; //source to target cardinality
    private Cardinality ts_cardinality; //target to source cardinality
    private String[] extralabels;

    public Relationship(Entity e1, Entity e2, Cardinality c1, Cardinality c2) {
        super(e1, e2);
        this.id1 = e1;
        this.id2 = e2;
        st_cardinality = c1;
        ts_cardinality = c2;

        extralabels = new String[]{ts_cardinality.toString(), st_cardinality.toString()};
    }

    public Entity getSEntityId() {
        return id1;
    }

    public Entity getTEntityId() {
        return id2;
    }

    public Cardinality getSTCard() {
        return st_cardinality;
    }

    public void setSTCard(Cardinality c) {
        this.st_cardinality = c;
        getAttributeMap().put(AttributeMap.EXTRALABELS,
                new String[]{ts_cardinality.toString(), st_cardinality.toString()});
    }

    public Cardinality getTSCard() {
        return ts_cardinality;
    }

    public void setTSCard(Cardinality c) {
        this.ts_cardinality = c;
        getAttributeMap().put(AttributeMap.EXTRALABELS,
                new String[]{ts_cardinality.toString(), st_cardinality.toString()});
    }

    public Boolean containsEntity(Entity e) {
        Boolean b = false;
        if (this.id1.equals(e) || this.id2.equals(e))
            b = true;
        return b;
    }

}
