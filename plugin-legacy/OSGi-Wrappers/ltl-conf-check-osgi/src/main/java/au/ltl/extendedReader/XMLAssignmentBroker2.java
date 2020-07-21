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
package au.ltl.extendedReader;

import org.processmining.plugins.declare.visualizing.AssignmentBroker;
import org.processmining.plugins.declare.visualizing.AssignmentModel;
import org.w3c.dom.Element;

import java.io.InputStream;

/**
 * Created by armascer on 17/11/2017.
 */
public class XMLAssignmentBroker2 extends XMLBroker2 implements AssignmentBroker {
        private final AssignmentElementFactory2 factory = new AssignmentElementFactory2(this);

        public XMLAssignmentBroker2(InputStream file, String name) {
            super(file, name);
        }

        public void addAssignment(AssignmentModel model) {
            Element newAssignment = this.factory.createAssignmentElement(model);
            Element root = this.getAssignmentElement();
            root.appendChild(newAssignment);
            this.writeDocument();
        }

        public AssignmentModel readAssignment() {
            this.readDocument();
            Element root = this.getDocumentRoot();
            AssignmentModel model = this.factory.elementToAssignmentModel(root);
            return model;
        }

        public AssignmentModel readAssignmentfromString(String documentString) {
            this.readDocumentString(documentString);
            Element root = this.getDocumentRoot();
            AssignmentModel model = this.factory.elementToAssignmentModel(root);
            return model;
        }

        public Element getAssignmentElement() {
            return this.getDocumentRoot();
        }

}
