/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.canoniser.pnml.internal.pnml2canonical;

import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.StateType;
import org.apromore.pnml.PlaceType;

public abstract class TranslatePlace {

    static public void translateState(PlaceType place, DataHandler data) {
        StateType node = new StateType();
        data.put_id_map(place.getId(), String.valueOf(data.getIds()));
        node.setId(String.valueOf(data.nextId()));
        if (place.getName() != null) {
            node.setName(place.getName().getText());
        }
        node.setOriginalID(place.getId());
        data.getNet().getNode().add(node);
    }

    static public void translateEvent(PlaceType place, DataHandler data) {
        data.put_id_map(place.getId(), String.valueOf(data.getIds()));

        EventType node = new EventType();
        node.setId(String.valueOf(data.nextId()));
        if (place.getName() != null) {
            node.setName(place.getName().getText());
        }
        node.setOriginalID(place.getId());
        data.getNet().getNode().add(node);
    }

    static public void translateInput(PlaceType place, DataHandler data) {

        data.put_id_map(place.getId(), String.valueOf(data.getIds()));

        // (event)
        EventType node = new EventType();
        node.setId(String.valueOf(data.nextId()));
        if (place.getName() != null) {
            node.setName(place.getName().getText());
        }
        node.setOriginalID(place.getId());
        data.getNet().getNode().add(node);

        // (state)
        StateType state = new StateType();
        state.setId(String.valueOf(data.nextId()));
        if (place.getName() != null) {
            state.setName(place.getName().getText());
        }
        data.setInputEvent(state.getId());
        data.getNet().getNode().add(state);

        // (event) -(edge)-> (state)
        data.setInputEdge(new EdgeType());
        data.getInputEdge().setId(String.valueOf(data.nextId()));
        data.getInputEdge().setSourceId(node.getId());
        data.getInputEdge().setTargetId(state.getId());
    }

    static public void translateOutput(PlaceType place, DataHandler data) {

        data.put_id_map(place.getId(), String.valueOf(data.getIds()));

        // (event)
        EventType node = new EventType();
        node.setId(String.valueOf(data.nextId()));
        if (place.getName() != null) {
            node.setName(place.getName().getText());
        }
        node.setOriginalID(place.getId());
        data.getNet().getNode().add(node);

        // (state)
        StateType state = new StateType();
        state.setId(String.valueOf(data.nextId()));
        if (place.getName() != null) {
            state.setName(place.getName().getText());
        }
        data.setOutputState(state.getId());
        data.getNet().getNode().add(state);

        // (state) -(edge)-> (node)
        data.setOutputEdge(new EdgeType());
        data.getOutputEdge().setId(String.valueOf(data.nextId()));
        data.getOutputEdge().setSourceId(state.getId());
        data.getOutputEdge().setTargetId(node.getId());
    }

}
