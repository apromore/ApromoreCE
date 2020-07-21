/*-
 * #%L
 * This file is part of "Apromore Community".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
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

package org.apromore.canoniser.pnml.internal.pnml2canonical;

import java.util.List;

import org.apromore.cpf.EdgeType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.pnml.TransitionToolspecificType;
import org.apromore.pnml.TransitionType;

public abstract class TranslateTrigger {

    public static boolean translateTrigger(TransitionType tran, DataHandler data) {
        if (tran.getToolspecific() != null) {
            List<TransitionToolspecificType> pnmlTransitionToolspecific = tran.getToolspecific();
            for (Object obj : pnmlTransitionToolspecific) {
                if (obj instanceof TransitionToolspecificType) {

                    TransitionToolspecificType transitionToolspecific = (TransitionToolspecificType) obj;

                    if (transitionToolspecific.getTrigger() != null) {
                        if (transitionToolspecific.getTrigger().getType() == 201) {
                            MessageType msg = new MessageType();

                            data.put_objectmap(String.valueOf(data.getIds()), msg);
                            msg.setId(String.valueOf(data.nextId()));
                            if (tran.getName() != null) {
                                msg.setName(tran.getName().getText());
                            }
                            TaskType msgtask = new TaskType();
                            msgtask.setId(String.valueOf(data.nextId()));
                            if (tran.getName() != null) {
                                msgtask.setName(tran.getName().getText());
                            }
                            msgtask.setOriginalID(tran.getId());
                            EdgeType msgedge = new EdgeType();
                            msgedge.setId(String.valueOf(data.nextId()));
                            msgedge.setSourceId(msg.getId());
                            msgedge.setTargetId(msgtask.getId());

                            data.getNet().getNode().add(msg);
                            data.getNet().getEdge().add(msgedge);
                            data.getNet().getNode().add(msgtask);
                            data.put_andjoinmap(String.valueOf(tran.getId()),
                                    String.valueOf(msgtask.getId()));
                            return true;
                        } else if (transitionToolspecific.getTrigger()
                                .getType() == 202) {
                            TimerType time = new TimerType();

                            data.put_objectmap(String.valueOf(data.getIds()), time);
                            time.setId(String.valueOf(data.nextId()));
                            if (tran.getName() != null) {
                                time.setName(tran.getName().getText());
                            }
                            TaskType timetask = new TaskType();
                            timetask.setId(String.valueOf(data.nextId()));
                            if (tran.getName() != null) {
                                timetask.setName(tran.getName().getText());
                            }
                            timetask.setOriginalID(tran.getId());
                            EdgeType timeedge = new EdgeType();
                            timeedge.setId(String.valueOf(data.nextId()));
                            timeedge.setSourceId(time.getId());
                            timeedge.setTargetId(timetask.getId());

                            data.getNet().getNode().add(time);
                            data.getNet().getEdge().add(timeedge);
                            data.getNet().getNode().add(timetask);
                            data.put_andjoinmap(String.valueOf(tran.getId()),
                                    String.valueOf(timetask.getId()));
                            return true;
                        } else {
                            return false;
                        }

                    }
                }
            }
        }
        return false;
    }

    public static String translateOperationTrigger(TransitionType tran) {
        if (tran.getToolspecific() != null) {
            List<TransitionToolspecificType> pnmlTransitionToolspecific = tran
                    .getToolspecific();
            for (Object obj : pnmlTransitionToolspecific) {
                if (obj instanceof TransitionToolspecificType) {

                    TransitionToolspecificType transitionToolspecific = (TransitionToolspecificType) obj;

                    if (transitionToolspecific.getTrigger() != null) {
                        if (transitionToolspecific.getTrigger().getType() == 201) {

                            return "message";
                        } else if (transitionToolspecific.getTrigger()
                                .getType() == 202) {
                            return "time";
                        } else if (transitionToolspecific.getTrigger()
                                .getType() == 200) {
                            return "res";
                        } else {
                            return "none";
                        }

                    }
                }
            }
        }
        return "none";
    }

    /*
    public long getIds() {
        return ids;
    }
    */
}
