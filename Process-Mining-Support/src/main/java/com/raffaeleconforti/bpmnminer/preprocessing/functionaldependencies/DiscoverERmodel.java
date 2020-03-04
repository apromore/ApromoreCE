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

package com.raffaeleconforti.bpmnminer.preprocessing.functionaldependencies;

import com.raffaeleconforti.bpmnminer.preprocessing.inclusiondependencies.InclusionDependencies;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.*;
import com.raffaeleconforti.foreignkeydiscovery.functionaldependencies.Data;
import com.raffaeleconforti.foreignkeydiscovery.functionaldependencies.NoEntityException;
import com.raffaeleconforti.foreignkeydiscovery.functionaldependencies.TANEjava;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.*;
import java.util.Map.Entry;

public class DiscoverERmodel {

    private Map<Set<String>, String> primaryKeys_entityName;
    private ConcModelInstances allInstances = new ConcModelInstances();
    private UnifiedMap<String, String> dataTypes = new UnifiedMap<String, String>();

    public static String keyToString(Set<String> set) throws NoEntityException {
        StringBuilder keyString = new StringBuilder("(");
        for (Iterator<String> attrIter = set.iterator(); attrIter.hasNext(); ) {
            keyString.append(attrIter.next()).append(attrIter.hasNext() ? ", " : ")");
        }
        if(keyString.toString().equals("(")) throw new NoEntityException();
        return keyString.toString();
    }

    public List<String> generateAllAttributes(XLog log) {
        List<String> allAttributes = new ArrayList<String>(getLogAttributes(log));
        allAttributes.remove("concept:name");
        allAttributes.remove("time:timestamp");
        allAttributes.remove("lifecycle:transition");
        allAttributes.remove("org:resource");

        // show ui to user to confirm/select primary keys
        return allAttributes;
    }

    public UnifiedMap<String, Data> generateData(XLog log, List<String> ignoreAttributes) {
        UnifiedMap<String, Data> data = transferData(log, ignoreAttributes);

        //find functional dependencies and keys
        Data currentData;
        TANEjava tane;
        for (Data data1 : data.values()) {
            currentData = data1;

            try {
                tane = new TANEjava(currentData);
                tane.setConsoleOutput(false);
                tane.getFD();
                tane.getKeys();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

		/*
         * Step 1: discover primary keys
		 */
        return data;
    }

    public void discoverInclusionRelationships(ConceptualModel concModel, List<ForeignKeyData> fkeyData, boolean[] selectedFKeys) {
        // and extend conceptual model:
        ForeignKeyData fKey;
        Relationship r;
        for (int i = 0; i < selectedFKeys.length; i++) {
            if (!selectedFKeys[i])
                continue;

            // define mappings from foreign-key attributes to primary-key attributes
            fKey = fkeyData.get(i);
            for (int j = 0; j < fKey.e1_foreignKey.size(); j++) {
                fKey.e1.makeForeignKey(fKey.e1_foreignKey.get(j), fKey.e2, fKey.e2_primaryKey.get(j));
            }

            // define relationship between entities with basic cardinality
            r = concModel.getRelationship(fKey.e1, fKey.e2);
            if (r == null) { //adding a new relationship
                concModel.addRelationship(new Relationship(fKey.e1, fKey.e2, Cardinality.ZERO_OR_ONE,
                        Cardinality.ZERO_OR_ONE));
            }
        }
    }

    public List<ForeignKeyData> discoverForeignKeys(ConceptualModel concModel) {
        InclusionDependencies d = new InclusionDependencies(concModel, allInstances);
        List<? extends List<? extends List<Object>>> candidateFKeys = d.getDependencies(concModel, allInstances);
        // show user a dialog to select which relation ships to use
        return ForeignKeyData.getForeignKeyData(candidateFKeys);
    }

    public void setPrimaryKeysEntityName(Map<Set<String>, String> primaryKeys_entityName) {
        this.primaryKeys_entityName = primaryKeys_entityName;
    }

    public ConceptualModel showGUI(ConceptualModel concModel, Map<Set<String>, String> primaryKeys_entityName, int algorithm) throws NoEntityException {
		/*
         * Step 2: discover relationships (foreign key-primary key)
		 */
        List<ForeignKeyData> fkeyData = null;
        boolean[] selectedFKeys = null;

        if(algorithm == 1) {
            fkeyData = discoverForeignKeys(concModel);
            selectedFKeys = new boolean[fkeyData.size()];
            for(int i = 0; i < selectedFKeys.length; i++) {
                selectedFKeys[i] = true;
            }
        }

        return updateConceptualModel(primaryKeys_entityName, fkeyData, concModel, selectedFKeys, algorithm);
    }

    public ConceptualModel updateConceptualModel(Map<Set<String>, String> primaryKeys_entityName,
                                   List<ForeignKeyData> fkeyData, ConceptualModel concModel, boolean[] selectedFKeys, int algorithm) throws NoEntityException {
		/*
         * Step 1b: show entities and allow user to give each entity a name
		 */

        // store names of entities
        this.primaryKeys_entityName = primaryKeys_entityName;

		/*
         * Step 2: discover relationships (foreign key-primary key)
		 */
        if(algorithm == 1) {
            discoverInclusionRelationships(concModel, fkeyData, selectedFKeys);
        }

		/*
         * Step 3: discover cardinalities
		 */
        concModel.findCardinalitites(allInstances);

        concModel.findHorizon();
        concModel.findTopEntities(allInstances);

        return concModel;
    }

    public UnifiedMap<String, Data> transferData(XLog log, Collection<String> ignoreAttributes) {
        UnifiedMap<String, Data> data = new UnifiedMap<String, Data>();
        UnifiedMap<String, XAttribute> current;
        XAttribute timestamp;
        String currentTitle;
        String[] extendedTitles;
        Data table;

        for (XTrace xTrace : log) {
            for (XEvent xEvent : xTrace) {

                current = new UnifiedMap<String, XAttribute>();
                timestamp = null;
                currentTitle = "";

                for (Entry<String, XAttribute> attr : xEvent.getAttributes().entrySet()) {
                    if (attr.getKey().equals("concept:name")) {
                        currentTitle = attr.getValue().toString();
                    } else if (attr.getKey().equals("time:timestamp")) {
                        timestamp = attr.getValue();//.toString();
                    } else if (!attr.getKey().equals("lifecycle:transition") && !attr.getKey().equals("org:resource") && !ignoreAttributes.contains(attr.getKey())) {
                        current.put(attr.getKey(), attr.getValue());
                        XAttribute value = attr.getValue();
                        dataTypes.put(attr.getKey(), value.getClass().getName());
                    }
                }

                if (timestamp != null) {
                    dataTypes.put(currentTitle, timestamp.getClass().getName());

                    if ((table = data.get(currentTitle)) != null) {
                        if (table.columnTitles.length < current.size()) {
                            extendedTitles = new String[current.size()];
                            int i = 0;
                            for (String title : current.keySet()) {
                                extendedTitles[i] = title;
                                i++;
                            }
                            table.columnTitles = extendedTitles;
                        }
                    } else {
                        table = new Data();
                        table.columnTitles = new String[current.size()];
                        int i = 0;
                        for (String title : current.keySet()) {
                            table.columnTitles[i] = title;
                            i++;
                        }
                        table.title = currentTitle;
                        table.dataType = dataTypes.get(currentTitle);
                        data.put(currentTitle, table);
                    }

                    table.table.add(current);
                    table.timestamps.add(timestamp);
                }
            }
        }
        return data;
    }

    public Set<String> getLogAttributes(XLog log) {
        Set<String> attributes = new UnifiedSet<String>();
        for (XTrace xTrace : log) {
            for (XEvent xEvent : xTrace) {
                for (String attr : xEvent.getAttributes().keySet()) {
                    attributes.add(attr);
                }
            }
        }
        return attributes;
    }

    public ConceptualModel createConceptualModel(Map<Set<String>, Set<String>> group, UnifiedMap<String, Data> data) {
        ConceptualModel concModel = new ConceptualModel();

        Entity entity;
        ArrayList<String> addedAttr;
        String keyAttr, dataType, eventType, attName;
        UnifiedMap<String, XAttribute> attrValues;
        Set<EntityInstance> instances;
        Iterator<EntityInstance> instItr;
        EntityInstance inst;
        for (Entry<Set<String>, Set<String>> entry : group.entrySet()) {
            entity = new Entity(primaryKeys_entityName.get(entry.getKey()), concModel); //name?
            allInstances.addEntity(entity);
            addedAttr = new ArrayList<String>();
            //add key attributes
            for (String anEntityGroup : entry.getKey()) {
                keyAttr = anEntityGroup;
                dataType = getType(keyAttr);
                entity.addAttribute(keyAttr, AttributeType.KEY, dataType);
                addedAttr.add(keyAttr);
            }
            //add timestamp attributes
            for (String s : entry.getValue()) {
                eventType = s;
                if (!addedAttr.contains(eventType)) {
                    dataType = getType(eventType);
                    entity.addAttribute(eventType, AttributeType.TIMESTAMP, dataType);
                    addedAttr.add(eventType);

                    for (int i = 0; i < data.get(eventType).timestamps.size(); i++) {
                        attrValues = new UnifiedMap<String, XAttribute>();
                        //extract the values for the key attributes for this row of the event type table
                        for (Entry<String, XAttribute> entry1 : data.get(eventType).table.get(i).entrySet()) {
                            if (entry.getKey().contains(entry1.getKey())) {
                                attrValues.put(entry1.getKey(), entry1.getValue());
                            }
                        }

                        instances = allInstances.getEntInstances(entity).getInstances(attrValues);
                        instItr = instances.iterator();

                        if (instItr.hasNext()) {//instance exists with these values of the key attributes
                            inst = instItr.next();
                            //add timestamp and data to this instance
                            inst.addAttribute(eventType, data.get(eventType).timestamps.get(i));

                            //if an attribute already has a value(s) the new one will be added after the existing one(s)
                            //currently for non-timestamp attributes multiple values are not used - only the first one is taken
                            for (Entry<String, XAttribute> entry1 : data.get(eventType).table.get(i).entrySet()) {
                                if (!attrValues.containsKey(entry1.getKey())) {
                                    //it is not a key attribute (key attributes are written only when instance
                                    //is created - only one value is allowed)
                                    inst.addAttribute(entry1.getKey(), entry1.getValue());
                                }
                            }
                        } else {//no such instances exist for this entity
                            inst = new EntityInstance();
                            inst.addAttribute(eventType, data.get(eventType).timestamps.get(i));
                            for (Entry<String, XAttribute> entry1 : data.get(eventType).table.get(i).entrySet()) {
                                inst.addAttribute(entry1.getKey(), entry1.getValue());
                            }
                            allInstances.getEntInstances(entity).addInstance(inst);
                        }
                        //---------------------------------------------
                    }
                }
                //add other attributes - their values have already been added to the instances
                for (int i = 0; i < data.get(eventType).columnTitles.length; i++) {
                    attName = data.get(eventType).columnTitles[i];
                    if (!addedAttr.contains(attName)) {
                        dataType = getType(attName);
                        entity.addAttribute(attName, AttributeType.OTHER, dataType);
                        addedAttr.add(attName);
                    }
                }
            }
            concModel.addEntity(entity);
        }
        return concModel;
    }

    private String getType(String attrName) {
        if (dataTypes.get(attrName).contains("XAttributeLiteralImpl"))
            return "String";
        if (dataTypes.get(attrName).contains("XAttributeDiscreteImpl"))
            return "Int";
        if (dataTypes.get(attrName).contains("XAttributeTimestampImpl"))
            return "Date";
        if (dataTypes.get(attrName).contains("XAttributeBooleanImpl"))
            return "Boolean";
        if (dataTypes.get(attrName).contains("XAttributeContinuousImpl"))
            return "Continuous";
        return "String";
    }

    public Map<Set<String>,String> getPrimaryKeys_entityName() {
        return primaryKeys_entityName;
    }

    public ConcModelInstances getAllInstances() {
        return allInstances;
    }

    public static class PrimaryKeyData {

        public String name;
        public String attributes[];
        public UnifiedSet<String>[] primaryKeys;

        @SuppressWarnings("unchecked")
        public static List<PrimaryKeyData> getData(Map<String, Data> data) {

            List<PrimaryKeyData> result = new ArrayList<PrimaryKeyData>();

            PrimaryKeyData kData;

            for (Data currentData : data.values()) {

                kData = new PrimaryKeyData();
                kData.name = currentData.title;
                Set<String> set = currentData.table.get(0).keySet();
                kData.attributes = set.toArray(new String[set.size()]);

                // Create list for all available methods.
                kData.primaryKeys = new UnifiedSet[currentData.keys.size()]; //change if user can select any attributes for identifiers
                for (int i = 0; i < currentData.keys.size(); i++) {
                    kData.primaryKeys[i] = currentData.keys.get(i);
                }

                result.add(kData);
            }
            return result;
        }
    }

    public static class ForeignKeyData {

        public Entity e1, e2;
        public List<Attribute> e1_foreignKey, e2_primaryKey;

        public ForeignKeyData(List<? extends List<Object>> fKeys) {

            e1 = (Entity) fKeys.get(0).get(0);
            e2 = (Entity) fKeys.get(0).get(2);

            e1_foreignKey = new ArrayList<Attribute>();
            e2_primaryKey = new ArrayList<Attribute>();

            // primary keys and foreign keys have the same length
            for (List<Object> fKey : fKeys) {
                e1_foreignKey.add((Attribute) fKey.get(1));
                e2_primaryKey.add((Attribute) fKey.get(3));
            }
        }

        public static List<ForeignKeyData> getForeignKeyData(List<? extends List<? extends List<Object>>> candidateFKeys) {
            List<ForeignKeyData> result = new ArrayList<ForeignKeyData>();
            for (List<? extends List<Object>> fKeys : candidateFKeys)
                result.add(new ForeignKeyData(fKeys));
            return result;
        }
    }
}
