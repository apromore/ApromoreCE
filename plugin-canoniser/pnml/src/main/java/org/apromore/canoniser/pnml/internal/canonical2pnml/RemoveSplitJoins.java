/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.pnml.internal.canonical2pnml;

import java.util.LinkedList;
import java.util.List;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;

public class RemoveSplitJoins {
    DataHandler data;
    CanonicalProcessType cproc = new CanonicalProcessType();
    RemoveDuplicateListItems tl = new RemoveDuplicateListItems();
    AnnotationsType annotations = new AnnotationsType();
    List<Object> removenodes = new LinkedList<Object>();
    List<Object> removeanno = new LinkedList<Object>();
    List<String> annotationid = new LinkedList<String>();

    public void setValue(AnnotationsType annotations, DataHandler data,
                         CanonicalProcessType cproc) {
        this.data = data;
        this.cproc = cproc;
        this.annotations = annotations;
    }

    public void remove() {
        for (NetType net : cproc.getNet()) {

            for (ANDJoinType aj : data.get_andjoinmap().values()) {
                if (data.get_andsplitmap().containsKey(aj.getName())) {
                    ANDSplitType asplit = data.get_andsplitmap_value(aj
                            .getName());
                    removenodes.add(asplit);
                    annotationid.add(String.valueOf(asplit.getId()));
                    if (aj.getOriginalID().contains("_op_")) {
                        data.put_specialoperators(
                                "andsplitjoin-" + aj.getName(), aj);
                    }
                    for (EdgeType edge : net.getEdge()) {
                        if (edge instanceof EdgeType) {
                            if (edge.getSourceId().equals(asplit.getId())) {
                                edge.setSourceId(aj.getId());
                            }
                        }
                    }
                } else if (data.get_xorsplitmap().containsKey(aj.getName())) {
                    int sourcecount = 0;
                    XORSplitType xsplit = data.get_xorsplitmap_value(aj
                            .getName());
                    removenodes.add(xsplit);
                    annotationid.add(String.valueOf(xsplit.getId()));
                    if (aj.getOriginalID().contains("_op_")) {
                        data.put_specialoperators(
                                "andjoinxorsplit-" + aj.getName(), aj);
                    }
                    for (EdgeType edge : net.getEdge()) {
                        if (edge instanceof EdgeType) {
                            if (edge.getSourceId().equals(xsplit.getId())) {
                                edge.setSourceId(aj.getId());
                                sourcecount++;
                            }
                        }
                    }
                    data.put_specialoperatorscount(
                            "splitcount-" + aj.getName(), sourcecount);
                }
            }
            for (XORJoinType xj : data.get_xorjoinmap().values()) {
                if (data.get_andsplitmap().containsKey(xj.getName())) {
                    int joincount = 0;
                    ANDSplitType asplit = data.get_andsplitmap_value(xj
                            .getName());
                    removenodes.add(asplit);
                    annotationid.add(String.valueOf(asplit.getId()));
                    if (xj.getOriginalID().contains("_op_")) {
                        data.put_specialoperators(
                                "xorjoinandsplit-" + xj.getName(), xj);
                    }
                    for (EdgeType edge : net.getEdge()) {
                        if (edge instanceof EdgeType) {
                            if (edge.getSourceId().equals(asplit.getId())) {
                                edge.setSourceId(xj.getId());
                                joincount++;
                            }
                        }

                    }
                    data.put_specialoperatorscount("joincount-" + xj.getName(),
                            joincount);
                } else if (data.get_xorsplitmap().containsKey(xj.getName())) {
                    int joincount = 0;
                    int splitcount = 0;
                    XORSplitType xsplit = data.get_xorsplitmap_value(xj
                            .getName());
                    removenodes.add(xsplit);
                    annotationid.add(String.valueOf(xsplit.getId()));
                    if (xj.getOriginalID().contains("_op_")) {
                        data.put_specialoperators(
                                "xorsplitjoin-" + xj.getName(), xj);
                    }
                    for (EdgeType edge : net.getEdge()) {
                        if (edge instanceof EdgeType) {
                            if (edge.getSourceId().equals(xsplit.getId())) {
                                edge.setSourceId(xj.getId());
                                splitcount++;
                            } else if (edge.getTargetId().equals(xj.getId())) {
                                joincount++;
                            }
                        }
                    }
                    data.put_specialoperatorscount(
                            "splitcount-" + xj.getName(), splitcount);
                    data.put_specialoperatorscount("joincount-" + xj.getName(),
                            joincount);
                }
            }
            if (annotations != null) {
                for (AnnotationType annotation : annotations.getAnnotation()) {
                    if (annotationid.contains(String.valueOf(annotation.getId()))) {
                        removeanno.add(annotation);
                    }
                }
            }
        }
        for (NetType net : cproc.getNet()) {
            if (removenodes.size() > 0) {
                for (Object obj : removenodes) {
                    net.getNode().remove(obj);
                }
            }
        }
        if (removeanno.size() > 0) {
            for (Object obj : removeanno) {
                annotations.getAnnotation().remove(obj);
            }

        }

    }

    public CanonicalProcessType getCanonicalProcess() {
        return cproc;
    }

    public AnnotationsType getAnnotations() {
        return annotations;
    }

}
