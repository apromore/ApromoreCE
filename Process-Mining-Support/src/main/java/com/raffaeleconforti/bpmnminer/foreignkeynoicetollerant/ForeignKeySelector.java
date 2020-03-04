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

package com.raffaeleconforti.bpmnminer.foreignkeynoicetollerant;


import com.raffaeleconforti.foreignkeydiscovery.Couple;
import com.raffaeleconforti.foreignkeydiscovery.DatabaseCreator;
import com.raffaeleconforti.foreignkeydiscovery.ForeignKeyDiscoverer;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.Attribute;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.AttributeType;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.Entity;
import com.raffaeleconforti.foreignkeydiscovery.databasestructure.Column;
import com.raffaeleconforti.foreignkeydiscovery.databasestructure.ForeignKey;
import com.raffaeleconforti.foreignkeydiscovery.databasestructure.PrimaryKey;
import com.raffaeleconforti.foreignkeydiscovery.grouping.Group;
import com.raffaeleconforti.foreignkeydiscovery.util.EntityPrimaryKeyConverter;
import com.raffaeleconforti.log.util.LogOptimizer;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.*;

/**
 * Created by conforti on 4/11/2014.
 */
public class ForeignKeySelector {

    public Set<Group> selectForeignKeys(DatabaseCreator databaseCreator, EntityPrimaryKeyConverter entityPrimaryKeyConverter, LogOptimizer logOptimizer, List<Entity> groupEntities, List<Entity> selectedEntities, List<Entity> candidatesEntities, Set<Entity> entities, Map<Entity, Map<String, Double>> entityCertancy) {

        Scanner console = new Scanner(System.in);
        Set<Group> artifacts = new UnifiedSet<Group>();

        Set<PrimaryKey> setOfSingleColumnPrimaryKeys = new UnifiedSet<PrimaryKey>();
        Set<PrimaryKey> setOfMultiColumnPrimaryKeys = new UnifiedSet<PrimaryKey>();

        for(Entity entity : groupEntities) {
            System.out.println("Entity "+entity.getName());

            PrimaryKey primaryKey = null;
            TreeSet<Column> set = new TreeSet<Column>();
            if(entity.getKeys().size() == 1) {
                for (Attribute a : entity.getKeys()) {
                    for (Column c : databaseCreator.getSetOfColumns()) {
                        if (c.getColumnName().equals(a.getName()) && entity.getName().equals(c.getTable())) {
                            set.add(c);
                        }
                    }
                }
                primaryKey = new PrimaryKey(set);
                setOfSingleColumnPrimaryKeys.add(primaryKey);
            }else {
                for (Attribute a : entity.getKeys()) {
                    for (Column c : databaseCreator.getSetOfColumns()) {
                        if (c.getColumnName().equals(a.getName()) && entity.getName().equals(c.getTable())) {
                            set.add(c);
                        }
                    }
                }
                primaryKey = new PrimaryKey(set);
                setOfMultiColumnPrimaryKeys.add(primaryKey);
            }
            entities.add(entity);
        }
        ForeignKeyDiscoverer foreignKeyDiscoverer = new ForeignKeyDiscoverer();
        ArrayList<Couple<ForeignKey, Double>> foreignKeys = foreignKeyDiscoverer.discoverForeignKey(databaseCreator.getSetOfTables(), databaseCreator.getSetOfColumns(), setOfSingleColumnPrimaryKeys, setOfMultiColumnPrimaryKeys, 0.9, 256, 4);

        for(Couple<ForeignKey, Double> foreignKey : foreignKeys) {
                TreeSet<Column> setP = new TreeSet<Column>();
                UnifiedSet<String> tablesP = new UnifiedSet<String>();
                for(Column c : foreignKey.getFirstElement().getColumnsP()) {
                    String name = (String) logOptimizer.getReductionMap().get(c.getColumnName().substring(c.getColumnName().lastIndexOf("|")+1));
                    Column a = new Column(name, c.getColumnValues(), c.getTable());
                    setP.add(a);
                    tablesP.add(c.getTable());
                }

                UnifiedSet<String> tablesF = new UnifiedSet<String>();
                for(Column c : foreignKey.getFirstElement().getColumnsF()) {
                    tablesF.add(c.getTable());
                }
                boolean same = true;
                for(String s : tablesP) {
                    if(!tablesF.contains(s)) {
                        same = false;
                        break;
                    }
                }
                if(same) {
                    for(String s : tablesF) {
                        if(!tablesP.contains(s)) {
                            same = false;
                            break;
                        }
                    }
                }
                boolean contain = true;
                for(Column c : foreignKey.getFirstElement().getColumnsF()) {
                    if(!tablesF.contains(c.getColumnName().substring(1, c.getColumnName().length()-1))) {
                        contain = false;
                        break;
                    }
                }
                if(contain || tablesF.equals(tablesP) ) {
                    same = true;
                }
                if(!same) {
                    System.out.println("Select " + tablesF + " -> " + tablesP + " Foreign Key-Primary Key Relations? (y/n) " + foreignKey.getSecondElement());
                    System.out.println(foreignKey.getFirstElement().getColumnsF().first().getColumnName() + " is a foreign key to " + foreignKey.getFirstElement().getColumnsP().first().getColumnName());

                    for (Entity e : entities) {
                        for (Column c : foreignKey.getFirstElement().getColumnsF()) {
                            if (e.getName().equals(c.getTable())) {
                                Entity foreign = null;
                                Entity tmp = null;
                                for (Entity e1 : entities) {
                                    tmp = entityPrimaryKeyConverter.getEntity(setP, null);
                                    if (e1.getName().equals(tmp.getName())) {
                                        foreign = e1;
                                        break;
                                    }
                                }
                                boolean skip = false;
                                for (Attribute a : e.getKeys()) {
                                    if (a.getName().equals(c.getColumnName())) {
                                        skip = true;
                                    }
                                }
                                if (foreign != null) {
                                    if (!skip) {
                                        e.makeForeignKey(new Attribute(c.getColumnName(), AttributeType.FOREIGN_KEY, "String"), foreign, new Attribute(foreign.getName(), AttributeType.ID, "String"));
                                    }
                                } else {
                                    if (!skip) {
                                        e.makeForeignKey(new Attribute(c.getColumnName(), AttributeType.FOREIGN_KEY, "String"), tmp, new Attribute(tmp.getName(), AttributeType.ID, "String"));
                                    }
                                }

                                Map<String, Double> certancy = null;
                                if((certancy = entityCertancy.get(e)) == null) {
                                    certancy = new UnifiedMap<String, Double>();
                                    entityCertancy.put(e, certancy);
                                }
                                certancy.put(c.getColumnName(), foreignKey.getSecondElement());
                            }
                        }
                    }
                }
        }

        Iterator<Entity> entityIt2 = entities.iterator();
        while (entityIt2.hasNext()) {
            Entity entity = entityIt2.next();
            //String entityLabel = entity.getLabel();
            Group newgroup = new Group(entity);

            //add timestamps of secondary entities
            for (int i = 0; i < candidatesEntities.size(); i++) {
                if (selectedEntities.get(i).equals(entity)) {
                    Entity e = candidatesEntities.get(i);
                    newgroup.addEntity(e);
                }
            }
            artifacts.add(newgroup);
        }

        return artifacts;
    }
}
