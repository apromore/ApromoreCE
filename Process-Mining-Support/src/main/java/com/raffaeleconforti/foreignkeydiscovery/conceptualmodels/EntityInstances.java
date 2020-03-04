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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntityInstances {//the instances of one entity

    private UnifiedMap<UnifiedMap<String, XAttribute>, Set<EntityInstance>> cache = new UnifiedMap<UnifiedMap<String, XAttribute>, Set<EntityInstance>>();

    private ArrayList<EntityInstance> instances;

    public EntityInstances() {
        this.instances = new ArrayList<EntityInstance>();
    }

    public ArrayList<EntityInstance> getInstances() {
        return this.instances;
    }

    public void addInstance(EntityInstance inst) {
        this.instances.add(inst);
    }

    public ValueList getValues(Attribute attr) {
        ValueList values = new ValueList();
        for (EntityInstance inst : instances) {
            XAttribute val = inst.getValue(attr.getName());
            if (val != null) {
                String s = val.toString();
                values.addValue(s);
            }
        }
        return values;
    }

    public ValueList getValues(List<Attribute> attrs) {
        ValueList values = new ValueList();
        if (attrs.size() == 0)
            return values;
        for (EntityInstance inst : instances) { //for each instance
            StringBuilder val = new StringBuilder();
            Integer attr = 0;
            while (attr < attrs.size()) {
                Attribute a = attrs.get(attr);
                if(inst.getValue(a.getName()) != null) {
                    String s = inst.getValue(a.getName()).toString();
                    val.append("|"); //delimiter between values, assumes that this symbol does not appear in the values
                    val.append(s);
                    attr++;
                }else {
                    attr++;
                }
            }
            if (val.length() > 0)
                values.addValue(val.toString());
        }
        return values;
    }

    //returns the instances that contain these values for the specified attributes
    //if attrValues contain the (composite) key attributes then the resulting set will contain only one instance
    public Set<EntityInstance> getInstances(UnifiedMap<String, XAttribute> attrValues) {
        Set<EntityInstance> foundInstances;
//        if((foundInstances = cache.get(attrValues)) == null) {
        foundInstances = new UnifiedSet<EntityInstance>();
        for (EntityInstance inst : instances) {
            Boolean match = true;
            for (Map.Entry<String, XAttribute> entry : attrValues.entrySet()) {
                if (inst.getValue(entry.getKey()) == null || !inst.getValue(entry.getKey()).equals(entry.getValue())) {
                    match = false;
                    break;
                }
            }
            if (match) {
                foundInstances.add(inst);
            }
        }
//            cache.put(attrValues, foundInstances);
//        }
        return foundInstances;
    }

}
