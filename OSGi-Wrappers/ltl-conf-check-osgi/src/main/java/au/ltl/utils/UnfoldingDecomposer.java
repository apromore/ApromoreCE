/*-
 * #%L
 * This file is part of "Apromore Community".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package au.ltl.utils;

import hub.top.petrinet.Node;
import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import au.ltl.domain.Subnet;

/**
 * Created by armascer on 9/11/2017.
 */
public class UnfoldingDecomposer {
    ModelAbstractions model;
    PetriNet unfolding;
    private HashSet<Subnet> subNets;

    public UnfoldingDecomposer(ModelAbstractions model){
        this.model = model;
        this.unfolding = model.getUnfolding();
    }

    public HashSet<Subnet> getSubNets() {
        HashSet<Subnet> subNets = new HashSet<>();
        Place finalPlaceNet = getSinkPlace(model.getNet());

        if(finalPlaceNet == null) {
            System.out.println("Not a workflow net");
            System.exit(0);
        }

        for(Place p : unfolding.getPlaces()){
            if(p.getPostSet().isEmpty() && !model.getUnfolder().cutoffs.contains(p))
                subNets.add(extractSubNetFrom(p));
        }

        return subNets;
    }

    private Subnet extractSubNetFrom(Place p) {
        PetriNet newNet = new PetriNet();
        
        Queue<Node> toCheck = new LinkedList<>();
        toCheck.add(p);
        
        HashMap<Node, Node> mapNewOld = new HashMap<>();
        HashMap<Node, Node> mapOldNew = new HashMap<>();
        Node node=newNet.addPlace(p.getName());
        mapOldNew.put(p, node);
        mapNewOld.put(node,p);

        while(!toCheck.isEmpty()){
            Node current = toCheck.remove();

            if(current instanceof Place){
                for(Transition n : ((Place)current).getPreSet()) {
                    if(!mapOldNew.containsKey(n)) {
                    	Node node1=newNet.addTransition(n.getName());
                        mapOldNew.put(n, node1);
                        mapNewOld.put(node1,n);
                        toCheck.add(n);
                    }
                    newNet.addArc((Transition) mapOldNew.get(n), (Place) mapOldNew.get(current));

                }
            }else{
                for(Place n : ((Transition)current).getPreSet()) {
                    if(!mapOldNew.containsKey(n)) {
                    	Node node1=newNet.addPlace(n.getName());
                        mapOldNew.put(n, node1);
                        mapNewOld.put(node1,n);
                        toCheck.add(n);
                    }
                    newNet.addArc((Place)mapOldNew.get(n), (Transition) mapOldNew.get(current));
                }
            }
        }
        Subnet s= new Subnet(newNet,mapNewOld);
        return s;
    }

    private Place getSinkPlace(PetriNet net) {
        for(Place p : net.getPlaces())
            if(p.getPostSet().isEmpty())
                return p;

        return null;
    }
}

