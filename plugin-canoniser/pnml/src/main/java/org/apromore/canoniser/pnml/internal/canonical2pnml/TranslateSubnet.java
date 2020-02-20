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

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.pnml.NetType.Page;
import org.apromore.pnml.TransitionType;

public class TranslateSubnet {
    DataHandler data = new DataHandler();
    DataHandler dataold;
    RemoveConnectorTasks removeConnectorTasks = new RemoveConnectorTasks();
    RemoveEvents removeEvents = new RemoveEvents();
    RemoveState removeState = new RemoveState();
    RemoveSplitJoins removeSplitJoins = new RemoveSplitJoins();
    TranslateAnnotations ta = new TranslateAnnotations();
    TranslateNet tn = new TranslateNet();
    private long ids = System.currentTimeMillis();
    String id;

    public void setValue(DataHandler data, String id) {
        dataold = data;
        this.id = id;
    }

    public void addSubnet() {
        String filename = dataold.getFilename() + "_subnet";
        data.setFilename(filename);
        File cpf_file = new File("PNML_models/woped_cases_mapped_cpf_anf/"
                + filename + ".cpf");
        File anf_file = new File("PNML_models/woped_cases_mapped_cpf_anf/"
                + filename + ".anf");
        try {
            JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf", org.apromore.cpf.ObjectFactory.class.getClassLoader());
            Unmarshaller u = jc.createUnmarshaller();
            JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u
                    .unmarshal(cpf_file);
            CanonicalProcessType cpf = rootElement.getValue();

            jc = JAXBContext.newInstance("org.apromore.anf", org.apromore.anf.ObjectFactory.class.getClassLoader());
            u = jc.createUnmarshaller();
            JAXBElement<AnnotationsType> anfRootElement = (JAXBElement<AnnotationsType>) u
                    .unmarshal(anf_file);
            AnnotationsType anf = anfRootElement.getValue();
            //TODO FM extension
//            for (Object obj : cpf.getAttribute()) {
//                if (obj instanceof TypeAttribute) {
//                    data.setInitialType(((TypeAttribute) obj).getValue());
//                }
//            }
            data.setAnno(anf);
            removeEvents.setValue(anf, data, cpf);
            removeEvents.remove();
            cpf = removeEvents.getCanonicalProcess();
            anf = removeEvents.getAnnotations();
            removeConnectorTasks.setValue(data, cpf);
            removeConnectorTasks.remove();
            cpf = removeConnectorTasks.getCanonicalProcess();
            removeState.setValue(data, cpf);
            removeState.remove();
            cpf = removeState.getCanonicalProcess();
            removeSplitJoins.setValue(anf, data, cpf);
            removeSplitJoins.remove();
            cpf = removeSplitJoins.getCanonicalProcess();
            anf = removeSplitJoins.getAnnotations();
            translate(cpf, anf);
            ta.setValue(data);
            ta.mapNodeAnnotations(anf);
            AddXorOperators ax = new AddXorOperators();
            ax.setValues(data, ids);
            ax.add(cpf);
            ids = ax.getIds();
            cpf = ax.getCanonicalProcess();
            UpdateSpecialOperators uso = new UpdateSpecialOperators();
            uso.setValues(data, ids);
            uso.add(cpf);
            ids = uso.getIds();
            cpf = uso.getCanonicalProcess();
            if (data.getSubnet() != null) {
                if (data.getSubnet().size() == 1) {
                    TransitionType obj = data.getSubnet().get(0);
                    TranslateSubnet ts = new TranslateSubnet();

                    ts.setValue(data, ((TransitionType) obj).getId());
                    data.getSubnet().remove(0);
                    ts.addSubnet();
                    data = ts.getdata();
                }

            }

        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void translate(CanonicalProcessType cproc,
                          AnnotationsType annotations) {
        for (NetType net : cproc.getNet()) {

            tn.setValues(data, ids, annotations);
            tn.translateNet(net);
            ids = tn.getIds();
        }

        dataold.addsubnet(data.getNet());
        Page page = new Page();

        page.getNet().add(dataold.getsubnet());
        page.setId(id);
        dataold.getNet().getPage().add(page);

    }

    public DataHandler getdata() {
        return dataold;
    }

}
