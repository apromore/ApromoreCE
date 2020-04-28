/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2018 Abel Armas.
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

package org.apromore.service.apm.compliance.impl;

import hub.top.petrinet.PetriNet;
import nl.rug.ds.bpm.pnml.verifier.PnmlVerifier;
import nl.rug.ds.bpm.variability.SpecificationToXML;
import nl.rug.ds.bpm.variability.VariabilitySpecification;
import org.apromore.service.apm.compliance.APMService;
import org.springframework.stereotype.Service;

@Service
public class ApmServiceImpl implements APMService {
    @Override
    public String[] getVerification(PetriNet net, String xmlSpecification) {
        System.out.println("enter compliance logic");
        PnmlVerifier pnmlVerifier = new PnmlVerifier();
        System.out.println("pnml verifier created");
        return pnmlVerifier.verify(net, xmlSpecification);
    }

    @Override
    public String[] getVerification(PetriNet net, String[] specifications){
        System.out.println("enter compliance logic");
        PnmlVerifier pnmlVerifier = new PnmlVerifier();
        System.out.println("pnml verifier created");
        return pnmlVerifier.verify(net, specifications);
    }
}