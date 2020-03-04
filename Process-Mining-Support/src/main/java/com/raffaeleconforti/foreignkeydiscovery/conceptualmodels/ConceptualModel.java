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

import org.deckfour.xes.model.XAttribute;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.models.graphbased.directed.*;

import java.util.*;

/**
 * A conceptual model consisting of entities and relationships
 *
 * @author Viara Popova
 * @author Modified by Raffaele Conforti
 */
public class ConceptualModel extends AbstractDirectedGraph<Entity, Relationship> {
    private Set<Entity> entities;
    private Set<Relationship> relationships;
    private Set<Entity> topEntities;

    public ConceptualModel() {
        this.entities = new UnifiedSet<Entity>();
        this.relationships = new UnifiedSet<Relationship>();
        this.topEntities = new UnifiedSet<Entity>();
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public Boolean addRelationship(Relationship r) {
        return relationships.add(r);
    }

    public Relationship getRelationship(Entity id1, Entity id2) {
        Relationship r = null;
        for (Relationship r1 : relationships) {
            if (r1.containsEntity(id1) && r1.containsEntity(id2)) {
                r = r1;
                break;
            }
        }
        return r;
    }

    public void findHorizon() { //finds the logical horizon for each entity
        for (Entity e : entities) { //calculate the immediate LH (of distance 1)
            for (Entity ent : entities) {
                if (ent.equals(e))
                    continue;
                Cardinality c;
                Relationship r = this.getRelationship(e, ent);
                if (r != null) { //is there relationship
                    Entity e1 = r.getSEntityId();
                    if (e.equals(e1)) {//which one is the source and which the target
                        c = r.getSTCard();
                    } else {
                        c = r.getTSCard();
                    }
                    if (c.equals(Cardinality.ZERO_OR_ONE) || c.equals(Cardinality.ONE))
                        e.add2Horizon(ent);
                }
            }
        }
    }

    public void findTopEntities(ConcModelInstances allInstances) {
        Set<Entity> candidates = new UnifiedSet<Entity>(); //candidates for top-level entities
        candidates.addAll(entities);
        for (Entity e1 : entities) { //over all entities
            for (Entity e2 : e1.getHorizon()) { //over all entities in the horizon of e1
                for (EntityInstance inst1 : allInstances.getEntInstances(e1).getInstances()) { //over all instances of e1
                    Boolean precedes = true;
                    String date1 = inst1.findCreation(e1.getTimestamps());
                    UnifiedMap<Attribute, XAttribute> key_values1 = inst1.getValues(e1.getForeignKeys(), e2);
                    if (key_values1 == null)
                        continue;
                    for (EntityInstance inst2 : allInstances.getEntInstances(e2).getInstances()) { //over all instances of e2
                        UnifiedMap<Attribute, XAttribute> key_values2 = inst2.getValues(e2.getKeys());

                        if (key_values1.equals(key_values2)) { //inst1 corresponds to inst2
                            String date2 = inst2.findCreation(e2.getTimestamps());

                            Integer pr = date2.compareTo(date1);
                            if (pr >= 0) { //inst1 precedes inst2
                                precedes = false;
                                break;
                            }
                        }
                    }
                    if (precedes) {
                        candidates.remove(e1);
                        break;
                    }

                }
            }
        }
        for (Entity cand : candidates) {
            cand.setTopEntity();
            topEntities.add(cand);
        }
    }

    public Set<Entity> getTopEntities() {
        return this.topEntities;
    }

    public Set<Entity> getNonTopEntities() {
        Set<Entity> n = new UnifiedSet<Entity>();
        for (Entity i : entities) {
            if (!topEntities.contains(i))
                n.add(i);
        }
        return n;
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    public ArrayList<String> findCardinalitites(ConcModelInstances allInstances) {
        //checks only for "many" but not whether it's 0-to-many or 1-to-many
        //assumes 0-to-many if duplications found, otherwise takes 0-or-1 by default
        Iterator<Entity> itr1 = entities.iterator();
        Entity ent, current;
        Set<Entity> checked;
        Iterator<ForeignKey> k, k1;
        ForeignKey a, a1;
        ArrayList<Attribute> attrs;
        ArrayList<EntityInstance> insts;
        ListIterator<EntityInstance> in, in1;
        EntityInstance inst, inst1;
        UnifiedMap<Attribute, XAttribute> vals, vals1, kvals, kvals1;
        Relationship r;
        while (itr1.hasNext()) { //over all entities
            ent = itr1.next();
            checked = new UnifiedSet<Entity>();
            if (!ent.getForeignKeys().isEmpty()) {
                k = ent.getForeignKeys().iterator();
                while (k.hasNext()) { //over all foreign keys
                    //there is probably a better way to implement this
                    a = k.next();
                    current = a.getEntity();
                    if (checked.contains(current))
                        continue;
                    else
                        checked.add(current);
                    k1 = ent.getForeignKeys().iterator();
                    attrs = new ArrayList<Attribute>();//all foreign keys that correspond to the same entity
                    while (k1.hasNext()) {//assumes that composite (foreign) keys are possible
                        a1 = k1.next(); //there are some redundant checks here
                        if (a1.getEntity().equals(current))//ent.getForeignKeys().get(a1).equals(current))
                        {
                            attrs.add(a1.getFKey());
                        }
                    }
                    insts = allInstances.getEntInstances(ent).getInstances();//this.getEntity(current).getInstances();
                    in = insts.listIterator();
                    Boolean foundEqual = false;
                    while (!foundEqual && in.hasNext()) { //over all instances
                        inst = in.next();
                        if (in.hasNext()) {
                            in1 = insts.listIterator(in.nextIndex());
                            while (in1.hasNext()) {//over all instances after the current one (inst)
                                inst1 = in1.next();
                                vals = inst.getValues(attrs);
                                vals1 = inst1.getValues(attrs);
                                if (vals == null || vals1 == null)
                                    continue;
                                if (vals.equals(vals1)) { //check the values of the keys
                                    kvals = inst.getValues(ent.getKeys());
                                    kvals1 = inst1.getValues(ent.getKeys());
                                    if (!kvals.equals(kvals1)) {
                                        foundEqual = true;
                                    }
                                }
                            }
                        }
                    }
                    if (foundEqual) {
                        r = this.getRelationship(ent, current);
                        if (r != null) {
                            if (r.getSEntityId().equals(current))
                                r.setSTCard(Cardinality.ZERO_OR_MANY);
                            else
                                r.setTSCard(Cardinality.ZERO_OR_MANY);
                        } else {
                            r = new Relationship(current, ent, Cardinality.ZERO_OR_MANY, Cardinality.ZERO_OR_ONE);
                            this.addRelationship(r);
                        }
                    }
                }
            }
        }
        Iterator<Relationship> rels = this.relationships.iterator();
        ArrayList<String> results = new ArrayList<String>();
        while (rels.hasNext()) { //output all relatioships
            r = rels.next();
            results.add("Relationship between entity " + r.getSEntityId().getName() + " and entity "
                    + r.getTEntityId().getName() + " with cardinality: " + r.getSTCard() + ", " + r.getTSCard());
        }
        return results;
    }

    //returns the entity to which the event type belongs - used for reading instance data from the logs
    public Entity getEntity(String attrName) {
        Iterator<Entity> entItr = this.entities.iterator();
        Entity ent;
        Iterator<Attribute> attrItr;
        Attribute attr;
        while (entItr.hasNext()) {
            ent = entItr.next();
            attrItr = ent.getTimestamps().iterator();
            while (attrItr.hasNext()) {
                attr = attrItr.next();
                if (attrName.equalsIgnoreCase(attr.getName()))
                    return ent;
            }
        }
        return null;
    }

    public Set<Entity> getNodes() {
        return entities;
    }

    public Set<Relationship> getEdges() {
        return relationships;
    }

    @Override
    public void removeNode(DirectedGraphNode cell) {
        removeSurroundingEdges((Entity) cell);
        entities.remove(cell);
        graphElementRemoved(cell);
    }

    protected AbstractDirectedGraph<Entity, Relationship> getEmptyClone() {
        return new ConceptualModel();
    }

    @Override
    protected Map<? extends DirectedGraphElement, ? extends DirectedGraphElement> cloneFrom(
            DirectedGraph<Entity, Relationship> graph) {
        ConceptualModel model = (ConceptualModel) graph;

        UnifiedMap<DirectedGraphElement, DirectedGraphElement> mapping = new UnifiedMap<DirectedGraphElement, DirectedGraphElement>();

        for (Entity t : model.entities) {
            Entity copy = new Entity(t.getName(), this);
            addEntity(copy);
            if (t.isTopEntity()) {
                copy.setTopEntity();
                topEntities.add(copy);
            }
            mapping.put(t, copy);
        }

        for (Relationship r : model.relationships) {
            Relationship copy = new Relationship((Entity) mapping.get(r.getSource()), (Entity) mapping.get(r
                    .getTarget()), r.getSTCard(), r.getTSCard());
            addRelationship(copy);

            mapping.put(r, copy);
        }

        return mapping;
    }

    @SuppressWarnings("rawtypes")
    public void removeEdge(DirectedGraphEdge edge) {
        relationships.remove(edge);
    }
}
