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

package com.raffaeleconforti.bpmnminer.preprocessing.inclusiondependencies;

import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.*;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Finds the inclusion dependencies between all key attributes and all non-key
 * and non-timestamp attributes of other entities.
 *
 * @author Viara Popova
 */
public class InclusionDependencies {
    private UnifiedMap<Attribute, ValueList> candidatePool;
    private UnifiedMap<Entity, ArrayList<Attribute>> candidates;
    private UnifiedMap<Attribute, Entity> candidates2entities;

    public InclusionDependencies(ConceptualModel cm, ConcModelInstances allInstances) {
        this.candidatePool = new UnifiedMap<Attribute, ValueList>();
        this.candidates = new UnifiedMap<Entity, ArrayList<Attribute>>();
        this.candidates2entities = new UnifiedMap<Attribute, Entity>();
        for (Entity ent : cm.getEntities()) { //fill candidatePool and candidates
            Iterator<Attribute> aitr = ent.getAttributes().iterator();
            ArrayList<Attribute> attrs = new ArrayList<Attribute>();
            while (aitr.hasNext()) { //add all attributes
                Attribute attr = aitr.next();
                attrs.add(attr);
                ValueList vals = allInstances.getEntInstances(ent).getValues(attr);
                candidatePool.put(attr, vals);
                candidates2entities.put(attr, ent);
            }
            for (Attribute attr : ent.getKeys()) { //add all keys
                attrs.add(attr);
                ValueList vals = allInstances.getEntInstances(ent).getValues(attr);
                candidatePool.put(attr, vals);
                candidates2entities.put(attr, ent);
            }
            candidates.put(ent, attrs);
        }
    }

    //returns a set of discovered candidate composite foreign keys
    //Each composite f.k. is represented by an <ArrayList<ArrayList<Object>>
    //Each element in the composite f.k. (a single f.k.) is represented by an
    //ArrayList<Object> with four elements of types Entity,Attribute,Entity,Attribute
    //in this order which is also the order of makeForeignKey
    public ArrayList<ArrayList<ArrayList<Object>>> getDependencies(ConceptualModel cm, ConcModelInstances allInstances) {
        ArrayList<ArrayList<ArrayList<Object>>> candidateFKeys = new ArrayList<ArrayList<ArrayList<Object>>>();
        for (Entity ent : cm.getEntities()) { //for each entity
            List<Attribute> keys = ent.getKeys();
            Boolean found = true;
            if (keys.size() == 0)
                continue;
            //key attribute and approved candidates for it
            UnifiedMap<Attribute, ArrayList<Attribute>> currentApproved = new UnifiedMap<Attribute, ArrayList<Attribute>>();

            for (Attribute key : keys) { //over all keys of the entity
                if (!found)
                    break; //no candidates were found/approved for other attributes of the same composite key
                ValueList values = allInstances.getEntInstances(ent).getValues(key);
                String dataType = key.getDataType();
                ArrayList<Attribute> currentCandidates = new ArrayList<Attribute>();
                ArrayList<Attribute> approved = new ArrayList<Attribute>();
                ArrayList<Entity> entities = new ArrayList<Entity>();
                ArrayList<Entity> approvedEnt = new ArrayList<Entity>();
                for (Entity en : candidates.keySet()) { //add all attributes and key attributes of other entities as candidates
                    //only if number of attributes is at least equal to the number of attributes in the composite key
                    if (!en.equals(ent) && en.getAttributes() != null
                            && keys.size() <= (en.getAttributes().size() + en.getKeys().size())) {

                        for (Attribute at : en.getAttributes()) {
                            if (at.getDataType().equals(dataType)) {
                                currentCandidates.add(at);
                                entities.add(en);
                            }
                        }
                        for (Attribute at : en.getKeys()) {
                            if (at.getDataType().equals(dataType)) {
                                currentCandidates.add(at);
                                entities.add(en);
                            }
                        }
                    }
                }
                if (currentCandidates.isEmpty()) {
                    found = false;
                    continue;
                }
                while (!values.lastPlace()) { //iterate over all values of the key attribute
                    String keyval = values.getValue();

                    Iterator<Attribute> canditr = currentCandidates.iterator();
                    while (canditr.hasNext()) {
                        Attribute cand = canditr.next();
                        String val = candidatePool.get(cand).getValue();
                        int res = val.compareTo(keyval);
                        if (res == 0) {
                            Boolean b = candidatePool.get(cand).movePointer();
                            if (!b) {
                                approved.add(cand);
                                Integer ind = currentCandidates.indexOf(cand);
                                approvedEnt.add(entities.get(ind));
                                entities.remove(currentCandidates.indexOf(cand));
                                canditr.remove();
                                candidatePool.get(cand).resetPointer();
                            }
                        } else if (res < 0) {
                            entities.remove(currentCandidates.indexOf(cand));
                            canditr.remove();
                            candidatePool.get(cand).resetPointer();
                        }
                    }
                    if (!values.movePointer())
                        break;
                }
                values.resetPointer(); //not necessary
                for (Attribute cand : currentCandidates) {
                    candidatePool.get(cand).resetPointer();
                }
                if (approved.isEmpty())
                    found = false;
                else
                    currentApproved.put(key, approved);
                for (Attribute attr : approved) {
                    Entity e = approvedEnt.get(approved.indexOf(attr));
                    if (keys.size() == 1) { //not a composite key
                        ArrayList<ArrayList<Object>> discovered = new ArrayList<ArrayList<Object>>(); //to return discovered f.keys
                        ArrayList<Object> fk = new ArrayList<Object>();
                        fk.add(e);
                        fk.add(attr);
                        fk.add(ent);
                        fk.add(key);
                        //the corresponding foreign key would be: e.makeForeignKey(attr, ent, key);
                        discovered.add(fk);
                        candidateFKeys.add(discovered);
                    }
                }
            }

            if ((found) && (keys.size() > 1)) { //check higher levels

                UnifiedMap<ArrayList<Attribute>, ValueList> candidatePool2 = new UnifiedMap<ArrayList<Attribute>, ValueList>();
                ArrayList<ArrayList<Attribute>> candidateSets = new ArrayList<ArrayList<Attribute>>();
                ArrayList<ArrayList<Attribute>> approved2 = new ArrayList<ArrayList<Attribute>>();
                Boolean abort = false;

                for (Attribute key : keys) {//for each key of the entity
                    if (!currentApproved.containsKey(key)) { //is it necessary?
                        abort = true;
                        break;
                    }
                    Iterator<Attribute> canditr = currentApproved.get(key).iterator();
                    Boolean first = candidateSets.isEmpty();
                    Boolean firstAttr = true; //first value to add; don't copy, just add to all
                    while (canditr.hasNext()) {
                        Attribute cand = canditr.next();
                        if (first) {
                            ArrayList<Attribute> curr = new ArrayList<Attribute>();
                            curr.add(cand);
                            candidateSets.add(curr);
                        } else {

                            if (firstAttr) {
                                for (ArrayList<Attribute> set : candidateSets) {
                                    //same entity? if not, don't add
                                    if (!candidates2entities.get(cand).equals(candidates2entities.get(set.get(0))))
                                        continue;
                                    set.add(cand); //????
                                }
                                firstAttr = false;
                            } else { //copy and add ????
                                ArrayList<ArrayList<Attribute>> tempSets = new ArrayList<ArrayList<Attribute>>();
                                for (ArrayList<Attribute> set : candidateSets) {
                                    //same entity? if not, don't add
                                    if (!candidates2entities.get(cand).equals(candidates2entities.get(set.get(0))))
                                        continue;
                                    ArrayList<Attribute> newset = new ArrayList<Attribute>();
                                    newset.addAll(set);
                                    newset.remove(newset.size() - 1);
                                    newset.add(cand);
                                    tempSets.add(newset);
                                }
                                candidateSets.addAll(tempSets);
                            }
                        }
                    }
                }
                Iterator<ArrayList<Attribute>> setsitr = candidateSets.iterator();
                while (setsitr.hasNext()) { //remove sets that don't cover all keys
                    ArrayList<Attribute> set = setsitr.next();
                    if (set.size() < keys.size())
                        setsitr.remove();//candidateSets.remove(set);
                }
                if (abort)
                    break; //is it necessary?
                ValueList keyvals = allInstances.getEntInstances(ent).getValues(keys);
                for (ArrayList<Attribute> cand : candidateSets) {
                    //get values for keys, fill candidatePool2
                    ValueList values = allInstances.getEntInstances(candidates2entities.get(cand.get(0))).getValues(
                            cand);//ent.getValues(cand);//.toArray());
                    candidatePool2.put(cand, values);
                }
                //check candidates
                while (!keyvals.lastPlace()) { //iterate over all values of the key attribute
                    String keyval = keyvals.getValue();
                    Iterator<ArrayList<Attribute>> canditr2 = candidateSets.iterator();
                    while (canditr2.hasNext()) {
                        ArrayList<Attribute> cand = canditr2.next();
                        String val = candidatePool2.get(cand).getValue();
                        int res = val.compareTo(keyval);
                        if (res == 0) {
                            Boolean b = candidatePool2.get(cand).movePointer();
                            if (!b) {
                                approved2.add(cand);
                                canditr2.remove();
                                candidatePool2.get(cand).resetPointer();
                            }
                        } else if (res < 0) {
                            canditr2.remove();
                            candidatePool2.get(cand).resetPointer();
                        }
                    }
                    if (!keyvals.movePointer())
                        break;
                }
                for (ArrayList<Attribute> al : approved2) {
                    //discovered takes values Entity,Attribute,Entity,Attribute in the order of makeForeignKey
                    ArrayList<ArrayList<Object>> discovered = new ArrayList<ArrayList<Object>>();
                    for (Attribute b : al) {
                        //make b foreign key in entity candidates2entities.get(b) to key keys.get(al.indexOf(b) in entity ent
                        Entity e = candidates2entities.get(b);
                        ArrayList<Object> fk = new ArrayList<Object>();
                        fk.add(e);
                        fk.add(b);
                        fk.add(ent);
                        fk.add(keys.get(al.indexOf(b)));
                        //the corresponding foreign key would be: e.makeForeignKey(b, ent, keys.get(al.indexOf(b)));
                        discovered.add(fk);
                    }
                    candidateFKeys.add(discovered);
                }
            }
        }
        return candidateFKeys;
    }
}
