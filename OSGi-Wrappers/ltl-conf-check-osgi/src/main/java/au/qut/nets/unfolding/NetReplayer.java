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
package au.qut.nets.unfolding;

import hub.top.petrinet.PetriNet;
import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by armascer on 9/11/2017.
 */
public class NetReplayer {
    private HashSet<Execution> executions;
    PetriNet net;

    public NetReplayer(PetriNet net){
        this.net = net;
    }

    public HashSet<LinkedList<String>> getTraces() {
        Execution exec = new Execution();
        exec.setMarking(getInitialMarking());

        Queue<Execution> toAnalyze = new LinkedList<>();
        toAnalyze.add(exec);

        HashSet<LinkedList<String>> traces = new HashSet<>();
        int i=0;

        while(!toAnalyze.isEmpty()){
            Execution current = toAnalyze.remove();
            HashSet<Transition> enabled = getEnabledTs(current.marking);

            if(enabled.isEmpty()) {
                traces.add(current.getTrace());
                System.out.println("aaaa "+current.getTrace());
                //Qui potrei mettere il mapping perchè ci arriva solo a traccia finita
            }


            for(Transition t : enabled){
                Execution c1 = current.clone();
                c1.addFired(t);

                toAnalyze.add(c1);
            }
        }

        return traces;
    }

    private HashSet<Transition> getEnabledTs(LinkedList<Place> marking) {
        HashSet<Transition> enabled = new HashSet<>();
        for(Transition t : net.getTransitions())
            if(marking.containsAll(t.getPreSet()))
                enabled.add(t);

        return enabled;
    }

    private LinkedList<Place> getInitialMarking() {
        LinkedList<Place> marking = new LinkedList<>();

        for(Place p : net.getPlaces())
            if(p.getPreSet().isEmpty())
                marking.add(p);

        return marking;
    }
}
