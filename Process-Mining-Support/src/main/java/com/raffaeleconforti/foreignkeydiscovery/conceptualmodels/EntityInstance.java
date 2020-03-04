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

import java.util.ArrayList;
import java.util.List;

/**
 * Defines an instance of an entity with specific values for the attributes Can
 * store more than one value per attribute in the same instance but these are
 * only used for timestamp attributes. For other attributes only the first value
 * is used. Currently no information is kept on which value of non-timestamp
 * attribute corresponds to which value of a timestamp attribute.
 *
 * @author Viara Popova
 */
public class EntityInstance {
    private UnifiedMap<String, List<XAttribute>> attribute_values; //the attribute name and the value(s)

    public EntityInstance() {
        this.attribute_values = new UnifiedMap<String, List<XAttribute>>();
    }

    public void addAttribute(Attribute attr, XAttribute value) {
        List<XAttribute> list;
        if ((list = attribute_values.get(attr.getName())) == null) { //element exists
            list = new ArrayList<XAttribute>();
            attribute_values.put(attr.getName(), list);
        }
        list.add(value);
    }

    public void addAttribute(String attrName, XAttribute value) {
        List<XAttribute> list;
        if ((list = attribute_values.get(attrName)) == null) { //element exists
            list = new ArrayList<XAttribute>();
            attribute_values.put(attrName, list);
        }
        list.add(value);
    }

    public String findCreation(List<Attribute> attrs) { //attrs should be the set of timestamp attributes
        String date = null;
        for (Attribute attr : attrs) {
            List<XAttribute> newdateArr;
            if ((newdateArr = attribute_values.get(attr.getName())) != null) {
                for (XAttribute aNewdateArr : newdateArr) {
                    String newdate = aNewdateArr.toString();
                    if (date == null || date.compareTo(newdate) > 0)
                        date = newdate;
                }
            }
        }
        return date;
    }

    //find the values for a set of attributes
    //assumes that no two attributes of an entity have the same names
    public UnifiedMap<Attribute, XAttribute> getValues(List<Attribute> attrs) {
        UnifiedMap<Attribute, XAttribute> values = new UnifiedMap<Attribute, XAttribute>();
        for (Attribute attr : attrs) {
            List<XAttribute> vals = attribute_values.get(attr.getName());
            if (vals == null)
                return null;
            values.put(attr, vals.get(0)); //only the first value if more than one
        }
        return values;
    }

    public UnifiedMap<String, XAttribute> getValuesStr(List<Attribute> attrs) {
        UnifiedMap<String, XAttribute> values = new UnifiedMap<String, XAttribute>();
        for (Attribute attr : attrs) {
            List<XAttribute> val = attribute_values.get(attr.getName()); //only the first value if more than one
            if (val == null)
                return null;
            values.put(attr.getName(), val.get(0));
        }
        return values;
    }

    //find the values for foreign keys attributes of a given entity
    public UnifiedMap<Attribute, XAttribute> getValues(List<ForeignKey> attrs, Entity e) {
        UnifiedMap<Attribute, XAttribute> values = new UnifiedMap<Attribute, XAttribute>();
        for (ForeignKey attr : attrs) { //over all attributes
            if (attr.getEntity().equals(e)) {
                List<XAttribute> vals = attribute_values.get(attr.getFKey().getName());
                if (vals == null)
                    return null;
                values.put(attr.getKey(), vals.get(0)); //only the first value if more than one
            }
        }
        return values;
    }

    public UnifiedMap<String, XAttribute> getValuesKeysStr(List<ForeignKey> attrs) {
        UnifiedMap<String, XAttribute> values = new UnifiedMap<String, XAttribute>();
        for (ForeignKey attr : attrs) { //over all attributes
            List<XAttribute> vals = attribute_values.get(attr.getFKey().getName());
            if (vals == null)
                return null;
            values.put(attr.getKey().getName(), vals.get(0)); //only the first value if more than one
        }
        return values;
    }

    public XAttribute getValue(String attrName) {//Attribute attr){
        if (attribute_values.get(attrName) == null)
            return null;
        return attribute_values.get(attrName).get(0); //only the first value if more than one
    }

    public ArrayList<XAttribute> getValueArray(String attrName) {//Attribute attr){
        if (attribute_values.get(attrName) == null)
            return null;
        ArrayList<XAttribute> valArr = new ArrayList<XAttribute>();
        for (XAttribute val : attribute_values.get(attrName)) {
            valArr.add(val);
        }
        return valArr; //only the first value if more than one
    }

    public int getSize(Attribute attr) {
        List<XAttribute> list;
        if ((list = attribute_values.get(attr.getName())) == null) return 0;
        return list.size();
    }
}
