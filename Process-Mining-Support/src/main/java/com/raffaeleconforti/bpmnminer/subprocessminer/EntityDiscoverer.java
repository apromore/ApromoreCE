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

package com.raffaeleconforti.bpmnminer.subprocessminer;

import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.Attribute;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.ConceptualModel;
import com.raffaeleconforti.foreignkeydiscovery.conceptualmodels.Entity;
import com.raffaeleconforti.foreignkeydiscovery.grouping.Group;
import com.raffaeleconforti.foreignkeydiscovery.util.EntityNameExtractor;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Raffaele Conforti on 20/02/14.
 */
public class EntityDiscoverer {

    private List<Entity> nonTopEnt;
    List<Entity> groupEntities;

    public List<Entity> discoverTopEntities(ConceptualModel concModel) {
        List<Entity> topEnt = new ArrayList<Entity>();
        for (Entity ent : concModel.getTopEntities()) {
            topEnt.add(ent);
        }
        return topEnt;
    }

    public List<Entity> discoverNonTopEntities(ConceptualModel concModel) {
        nonTopEnt = new ArrayList<Entity>();
        for (Entity ent : concModel.getNonTopEntities()) {
            nonTopEnt.add(ent);
        }
        return nonTopEnt;
    }

    public List<Entity> setGroupEntities(ConceptualModel concModel, List<Entity> groupEntities, boolean all) {
        if(!all) {
            this.groupEntities = groupEntities;
            this.groupEntities.addAll(concModel.getTopEntities());
        } else {
            int[] chosenArtifactsIndex = new int[nonTopEnt.size()];
            this.groupEntities = new ArrayList<Entity>();
            for (int dataIndex = 0; dataIndex < nonTopEnt.size(); dataIndex++) {
                if (chosenArtifactsIndex[dataIndex] == 0)
                    this.groupEntities.add(nonTopEnt.get(dataIndex));
            }
            return this.groupEntities;
        }
        // add top-level entities

        return this.groupEntities;
    }

    public List<Entity> discoverCandidatesEntities(ConceptualModel concModel, List<Entity> groupEntities) {
        List<Entity> candidatesEntities = new ArrayList<Entity>();

        for (Entity entity : concModel.getEntities()) {
            if (!groupEntities.contains(entity)) {
                candidatesEntities.add(entity);
            }
        }

        return candidatesEntities;
    }

    public Set<Group> generateArtifactGroup(List<Entity> groupEntities, List<Entity> candidatesEntities, List<Entity> selectedEntities) {
        //preparation for generating artifact logs
        Set<Group> artifacts = new UnifiedSet<Group>();
        Iterator<Entity> entityIt2 = groupEntities.iterator();

        //for each main entity (artifact)
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

    public Entity automaticDiscoverRootProcessKey(Set<Entity> entities, XLog rawLog) {
        Entity result = null;
        int size = Integer.MAX_VALUE;

        for (Entity ent : entities) {
            if (ent.getForeignKeys().size() == 0) {
                if(ent.getKeys().size() < size) {
                    if (result != null) System.out.println("Possible Problems");
                    result = ent;
                    size = EntityNameExtractor.getEntityName(ent).size();
                }
            }
        }

        if (result == null) {
            result = manuallyDiscoverRootProcessKey(entities, rawLog);
        }

        if (result == null) {
            result = semiManualDiscoverRootProcessKey(entities, rawLog);
        }

        return result;

    }

    private Entity semiManualDiscoverRootProcessKey(Set<Entity> entities, XLog rawLog) {
        Set<Entity> possibleRootKeys = new UnifiedSet<Entity>();

        for(XTrace trace : rawLog) {
            XEvent event = trace.get(0);
            int ent = 0;
            for(Entity entity : entities) {
                int hasEntity = 0;
                for(Attribute attribute1 : entity.getKeys()) {
                    for(String attribute : event.getAttributes().keySet()) {
                        if(attribute.equals(attribute1.getName())) {
                            hasEntity++;
                        }
                    }
                }
                if(hasEntity == entity.getNumKeys()) {
                    ent++;
                }
            }
            if(ent == 1) {
                for(Entity entity : entities) {
                    int hasEntity = 0;
                    for(Attribute attribute1 : entity.getKeys()) {
                        for(String attribute : event.getAttributes().keySet()) {
                            if(attribute.equals(attribute1.getName())) {
                                hasEntity++;
                            }
                        }
                    }
                    if(hasEntity == entity.getNumKeys()) {
                        possibleRootKeys.add(entity);
                        break;
                    }
                }
            }
        }

        if(possibleRootKeys.size() > 0) {
            return possibleRootKeys.iterator().next();
        }

        return null;
    }

    private Entity manuallyDiscoverRootProcessKey(Set<Entity> entities, XLog rawLog) {
        Entity result = null;

        for (Entity ent : entities) {

            boolean end = false;

            Set<String> setEntitiesName = EntityNameExtractor.getEntityName(ent);

            for (XTrace trace : rawLog) {
                for (XEvent event : trace) {
                    for (String key : setEntitiesName) {
                        XAttribute attribute = event.getAttributes().get(key);
                        if (attribute == null) {
                            end = true;
                            break;
                        }
                    }
                    if (end) {
                        break;
                    }
                }
                if (end) {
                    break;
                }
            }

            if (!end) {
                result = ent;
                break;
            }
        }

        return result;
    }

}
