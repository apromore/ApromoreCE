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

package com.raffaeleconforti.wrappers;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by conforti on 20/02/15.
 */
public class PetrinetWithMarking {

    private final Petrinet petrinet;
    private final Marking initialMarking;
    private Marking finalMarking;
    private Set<Marking> finalMarkings;

    public PetrinetWithMarking(Petrinet petrinet, Marking initialMarking) {
        this.initialMarking = initialMarking;
        this.petrinet = petrinet;
    }

    public PetrinetWithMarking(Petrinet petrinet, Marking initialMarking, Marking finalMarking) {
        this.finalMarking = finalMarking;
        this.finalMarkings = new HashSet<>();
        this.finalMarkings.add(finalMarking);
        this.initialMarking = initialMarking;
        this.petrinet = petrinet;
    }

    public PetrinetWithMarking(Petrinet petrinet, Marking initialMarking, Set<Marking> finalMarkings) {
        if(finalMarkings.size() == 1) {
            this.finalMarking = finalMarkings.iterator().next();
        }else {
            this.finalMarking = null;
        }
        this.finalMarkings = finalMarkings;
        this.initialMarking = initialMarking;
        this.petrinet = petrinet;
    }

    public Petrinet getPetrinet() {
        return petrinet;
    }

    public Marking getInitialMarking() {
        return initialMarking;
    }

    public Marking getFinalMarking() {
        return finalMarking;
    }

    public Set<Marking> getFinalMarkings() {
        return finalMarkings;
    }

    public void setFinalMarking(Marking finalMarking) {
        this.finalMarking = finalMarking;
        this.finalMarkings = new HashSet<>();
        this.finalMarkings.add(finalMarking);
    }

    public void setFinalMarkings(Set<Marking> finalMarkings) {
        if(finalMarkings.size() == 1) {
            this.finalMarking = finalMarkings.iterator().next();
        }else {
            this.finalMarking = null;
        }
        this.finalMarkings = finalMarkings;
    }
}
