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

import hub.top.petrinet.Place;
import hub.top.petrinet.Transition;

import java.util.LinkedList;

/**
 * Created by armascer on 9/11/2017.
 */
public class Execution {
    LinkedList<Place> marking;
    LinkedList<Transition> firingSeq;
    LinkedList<String> trace;

    public void setMarking(LinkedList<Place> marking) {
        this.marking = marking;
    }

    public Execution clone() {
        Execution clone = new Execution();

        if(this.marking != null)
            clone.setMarking(new LinkedList<Place>(this.marking));

        if(this.firingSeq != null)
            clone.setFiringSeq(new LinkedList<Transition>(firingSeq));

        if(this.trace != null)
            clone.setTrace(new LinkedList<String>(trace));

        return clone;
    }

    public void addFired(Transition t){
        if(firingSeq == null)
            this.firingSeq = new LinkedList<>();

        if(trace == null)
            this.trace = new LinkedList<>();

        if(marking == null)
            this.marking = new LinkedList<>();

        this.firingSeq.add(t);
        this.trace.add(t.getName());
        this.marking.removeAll(t.getPreSet());
        this.marking.addAll(t.getPostSet());
    }

    public void setFiringSeq(LinkedList<Transition> firingSeq) {
        this.firingSeq = firingSeq;
    }

    public void setTrace(LinkedList<String> trace) {
        this.trace = trace;
    }

    public LinkedList<String> getTrace() {
        return trace;
    }
}
