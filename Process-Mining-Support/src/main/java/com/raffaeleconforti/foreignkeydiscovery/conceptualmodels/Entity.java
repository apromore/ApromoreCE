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

import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.models.shapes.Decorated;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Defines an entity containing a set of attributes
 *
 * @author Viara Popova
 * @author Modified by Raffaele Conforti
 */
public class Entity extends AbstractDirectedGraphNode implements Decorated {
    private final static int TEXTHEIGHT = 10;
    private final String name; //entity name
    private final ConceptualModel erModel;
    private SortedSet<Attribute> keys;
    private SortedSet<Attribute> attributes;
    private SortedSet<ForeignKey> foreignkeys;
    private SortedSet<Attribute> timestamps;
    private List<Entity> horizon;
    private boolean top;
    private Set<Attribute> cachedTimeStampsSet = null;
    private boolean modifiedSet = false;

    public Entity(String name, ConceptualModel erModel) {
        this.name = name;
        this.erModel = erModel;
        this.horizon = new ArrayList<Entity>();

        this.timestamps = new TreeSet<Attribute>();
        this.foreignkeys = new TreeSet<ForeignKey>();
        this.attributes = new TreeSet<Attribute>();
        this.keys = new TreeSet<Attribute>();

        getAttributeMap().put(AttributeMap.LABEL, name);
        getAttributeMap().put(AttributeMap.SHOWLABEL, false);
        getAttributeMap().put(AttributeMap.AUTOSIZE, true);

    }

    public Attribute addAttribute(String label, AttributeType type, String dataType) {
        Attribute a = new Attribute(label, type, dataType);
        switch (type) {
            case KEY:
                keys.add(a);
                break;
            case TIMESTAMP:
                timestamps.add(a);
                modifiedSet = true;
                break;
            case OTHER:
                attributes.add(a);
                break;
            default:
                return null;
        }
        return a;
    }

    //makes an existing attribute a foreign key for entity e and key k
    public void makeForeignKey(Attribute a, Entity e, Attribute k) {
        a.setType(AttributeType.FOREIGN_KEY);
        foreignkeys.add(new ForeignKey(a, e, k));
    }

    public Integer getNumKeys() {
        return this.keys.size();
    }

    public Boolean add2Horizon(Entity e) {
        return horizon.add(e);
    }

    public List<Entity> getHorizon() {
        return this.horizon;
    }

    public List<Attribute> getKeys() {
        return new ArrayList<Attribute>(this.keys);
    }

    public List<Attribute> getAttributes() {
        return new ArrayList<Attribute>(this.attributes);
    }

    public List<ForeignKey> getForeignKeys() {
        return new ArrayList<ForeignKey>(this.foreignkeys);
    }

    //the set of foreign keys (composite) for entity e
    public List<ForeignKey> getForeignKeys(Entity e) {
        List<ForeignKey> fkeys = new ArrayList<ForeignKey>();
        for (ForeignKey fk : foreignkeys) {
            if (fk.getEntity().equals(e))
                fkeys.add(fk);
        }
        return fkeys;
    }

    public Set<Attribute> getTimestampsSet() {
        if (cachedTimeStampsSet == null || modifiedSet) {
            cachedTimeStampsSet = new UnifiedSet<Attribute>(this.timestamps);
            modifiedSet = false;
        }
        return cachedTimeStampsSet;
    }

    public List<Attribute> getTimestamps() {
        return new ArrayList<Attribute>(this.timestamps);
    }

    public String getName() {
        return this.name;
    }

    public ConceptualModel getGraph() {
        return erModel;
    }

    public void setTopEntity() {
        top = true;
    }

    public boolean isTopEntity() {
        return top;
    }

    public String toString() {
        return name;
    }

    public void decorate(Graphics2D g2d, double x, double y, double width, double height) {
        // fill in the semantic box
        // general stroke
        BasicStroke stroke = new BasicStroke((float) 1.0);
        g2d.setStroke(stroke);
        g2d.drawString(name, 1, TEXTHEIGHT);
        g2d.drawLine(0, TEXTHEIGHT + 2, (int) Math.floor(width), TEXTHEIGHT + 2);
        stroke = new BasicStroke((float) 0.5);
        g2d.setStroke(stroke);

        int i = 1;
        Font f = g2d.getFont();
        Font fi = g2d.getFont().deriveFont(Font.ITALIC);
        for (Attribute a : keys) {
            String label = a.getName() + ":" + a.getDataType();
            for (ForeignKey k : foreignkeys) {
                if (k.getFKey().equals(a)) {
                    g2d.setFont(fi);
                    break;
                }
            }
            g2d.drawString(label, 1, (++i) * TEXTHEIGHT);
            g2d.setFont(f);
            int w = g2d.getFontMetrics().stringWidth(label);
            g2d.drawLine(2, i * TEXTHEIGHT + 2, w, i * TEXTHEIGHT + 2);
        }
        g2d.setFont(fi);
        for (ForeignKey k : foreignkeys) {
            if (keys.contains(k.getFKey())) {
                continue;
            }
            String label = k.getFKey().getName() + ":" + k.getFKey().getDataType();
            g2d.drawString(label, 1, (++i) * TEXTHEIGHT);
            int w = g2d.getFontMetrics().stringWidth(label);
            g2d.drawLine(2, i * TEXTHEIGHT + 2, w, i * TEXTHEIGHT + 2);
        }
        g2d.setFont(f);
        attr:
        for (Attribute a : attributes) {
            for (ForeignKey k : foreignkeys) {
                if (k.getFKey().equals(a)) {
                    continue attr;
                }
            }
            if (keys.contains(a)) {
                continue;
            }
            String label = a.getName() + ":" + a.getDataType();
            g2d.drawString(label, 1, (++i) * TEXTHEIGHT);
        }

    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Entity) {
            Entity e = (Entity) o;
            if(this.getKeys().size() == e.getKeys().size()) {
                Set<String> setThis = new UnifiedSet<String>();
                for(int i = 0; i < this.getKeys().size(); i++) {
                    setThis.add(this.getKeys().get(i).getName());
                }
                Set<String> setE = new UnifiedSet<String>();
                for(int i = 0; i < this.getKeys().size(); i++) {
                    setE.add(((Entity) o).getKeys().get(i).getName());
                }
                return setThis.equals(setE);
            }
        }
        return false;
    }

}
