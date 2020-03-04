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

package com.raffaeleconforti.foreignkeydiscovery.util;

import com.raffaeleconforti.datastructures.Tree;
import com.raffaeleconforti.foreignkeydiscovery.DatabaseCreator;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.Attribute;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.AttributeType;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.Entity;
import com.raffaeleconforti.foreignkeydiscovery.databasestructure.Column;
import com.raffaeleconforti.foreignkeydiscovery.databasestructure.ForeignKey;
import com.raffaeleconforti.foreignkeydiscovery.databasestructure.PrimaryKey;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by conforti on 29/10/2014.
 */
public class EntityPrimaryKeyConverter {

    private Map<String, Entity> map = new UnifiedMap<String, Entity>();

    public Entity getEntity(TreeSet<Column> primaryKeys, TreeSet<Column> foreignKeys) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for(Column c : primaryKeys) {
            sb.append(c.getColumnName());
            sb.append(", ");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        sb.append(")");
        String nameP = sb.toString();
        Entity entityP = null;
        if((entityP = map.get(nameP)) == null) {
            entityP = new Entity(nameP, null);
            for(Column c : primaryKeys) {
                entityP.addAttribute(c.getColumnName(), AttributeType.KEY, "String");
            }
            map.put(nameP, entityP);
        }

        if(foreignKeys != null) {
            sb = new StringBuilder();
            sb.append("(");
            for (Column c : foreignKeys) {
                sb.append(c.getColumnName());
                sb.append(", ");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.deleteCharAt(sb.length()-1);
            sb.append(")");
            String nameF = sb.toString();
            Entity entityF = null;
            if((entityF = map.get(nameP)) == null) {
                entityF = new Entity(nameF, null);
                for(Column c : foreignKeys) {
                    entityF.addAttribute(c.getColumnName(), AttributeType.KEY, "String");
                }
                map.put(nameF, entityF);
            }

            entityP.makeForeignKey(new Attribute(entityF.getName(), AttributeType.ID, "String"), entityF, new Attribute(entityP.getName(), AttributeType.ID, "String"));
        }

        return entityP;
    }

    public Entity getEntity(TreeSet<Column> primaryKeys, TreeSet<Column> foreignKeys, List<Tree<Entity>.Node<Entity>> entities) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for(Column c : primaryKeys) {
            sb.append(c.getColumnName());
            sb.append(", ");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);
        sb.append(")");
        String nameP = sb.toString();
        Entity entityP = null;
        if((entityP = map.get(nameP)) == null) {
            entityP = new Entity(nameP, null);
            for(Column c : primaryKeys) {
                entityP.addAttribute(c.getColumnName(), AttributeType.KEY, "String");
            }
            map.put(nameP, entityP);
        }

        if(foreignKeys != null) {
            sb = new StringBuilder();
            sb.append("(");
            for (Column c : foreignKeys) {
                sb.append(c.getColumnName());
                sb.append(", ");
            }
            sb.deleteCharAt(sb.length()-1);
            sb.deleteCharAt(sb.length()-1);
            sb.append(")");
            String nameF = sb.toString();
            Entity entityF = null;
            if((entityF = map.get(nameP)) == null) {
                entityF = new Entity(nameF, null);
                for(Column c : foreignKeys) {
                    entityF.addAttribute(c.getColumnName(), AttributeType.KEY, "String");
                }
                map.put(nameF, entityF);
            }

            entityP.makeForeignKey(new Attribute(entityF.getName(), AttributeType.ID, "String"), entityF, new Attribute(entityP.getName(), AttributeType.ID, "String"));
        }

        return entityP;
    }

    public Entity getEntity(ForeignKey foreignKey) {
        return getEntity(foreignKey.getColumnsP(), foreignKey.getColumnsF());
    }

    public Entity getEntity(PrimaryKey primaryKey) {
        Entity entityP = null;
        if((entityP = map.get(primaryKey.getName())) == null) {
            entityP = new Entity(primaryKey.getName(), null);
            map.put(primaryKey.getName(), entityP);
        }
        for(Column c : primaryKey.getColumns()) {
            entityP.addAttribute(c.getColumnName(), AttributeType.KEY, "String");
        }
        return entityP;
//        return getEntity(primaryKey.getColumns(), null);
    }

    public PrimaryKey getPrimaryKey(DatabaseCreator databaseCreator, Entity entity) {
        TreeSet<Column> columns = new TreeSet<Column>();
        for(String key : EntityNameExtractor.getEntityName(entity)) {
            Column best = null;
            for(Column column : databaseCreator.getSetOfColumns()) {
                String name = column.getColumnName().substring(column.getColumnName().lastIndexOf("|") + 1);
                if(name.equals(key)) {
                    if(best == null) {
                        best = column;
                    }else {
                        UnifiedSet<String> v1 = new UnifiedSet<String>(Arrays.asList(best.getColumnValues().getValues()));
                        UnifiedSet<String> v2 = new UnifiedSet<String>(Arrays.asList(column.getColumnValues().getValues()));
                        if(v2.size() > v1.size()) {
                            best = column;
                        }
                    }
                }
            }
            columns.add(new Column(key, best.getColumnValues(), entity.getName()));
        }
        return new PrimaryKey(columns);
    }

}
