/*-
 * #%L
 * This file is part of "Apromore Community".
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
package org.apromore.processdiscoverer.logprocessors;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.model.XEvent;

public class EventClassifier extends XEventAttributeClassifier {
	protected String[] attributes;
	protected XEventAttributeClassifier[] classifiers;
	
	public EventClassifier(String... attributes) {
		super("eventClassifier", attributes);
		this.attributes = attributes;
		
		classifiers = new XEventAttributeClassifier[attributes.length];
		for (int i=0;i<attributes.length;i++) {
			classifiers[i] = new XEventAttributeClassifier(attributes[i], attributes[i]); 
		}
	}
	
	@Override
	public String getClassIdentity(XEvent event) {
		StringBuilder identity = new StringBuilder();
		for (int i=0;i<classifiers.length;i++) {
			String identity1 = classifiers[i].getClassIdentity(event);
			if (identity1 != null) identity.append(identity1);
			if (i < classifiers.length-1) identity.append("+");
		}
		XEventClassifier lifecycleClassifier = new XEventLifeTransClassifier();
		String lifecycle = lifecycleClassifier.getClassIdentity(event);
		if (lifecycle != null) identity.append("+" + lifecycle); 
		
		return identity.toString();
	}
	
	public String[] getAttributes() {
		return attributes;
	}
	
	@Override
	public int hashCode() {
		String[] copy = Arrays.copyOf(attributes, attributes.length);
		Arrays.sort(copy);
		return Arrays.hashCode(copy);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof EventClassifier)) return false;
		EventClassifier otherClassifier = (EventClassifier) other;
		Set<String> attributes1 = new HashSet<>(Arrays.asList(this.getAttributes()));
		Set<String> attributes2 = new HashSet<>(Arrays.asList(otherClassifier.getAttributes()));
		return attributes1.equals(attributes2);
	}
}
