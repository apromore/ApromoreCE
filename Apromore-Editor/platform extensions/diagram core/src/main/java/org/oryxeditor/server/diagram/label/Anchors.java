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

package org.oryxeditor.server.diagram.label;

import java.util.HashSet;
import java.util.Set;

/**
 * Wrapper class for a set of {@link Anchor} that can be used to define positioning policies of labels
 *
 * @author philipp.maschke
 */
public class Anchors {

    /**
     * Enumeration of anchor positions
     *
     * @author philipp.maschke
     */
    public enum Anchor {
        BOTTOM("bottom"), TOP("top"), LEFT("left"), RIGHT("right");

        /**
         * Returns the matching object for the given string
         *
         * @param enumString
         * @return
         * @throws IllegalArgumentException if no matching enumeration object was found
         */
        public static Anchor fromString(String enumString) {
            if (enumString == null) return null;

            for (Anchor attrEnum : values()) {
                if (attrEnum.label.equals(enumString) || attrEnum.name().equals(enumString))
                    return attrEnum;
            }

            throw new IllegalArgumentException("No matching enum constant found in '"
                    + Anchor.class.getSimpleName() + "' for: " + enumString);
        }

        private String label;


        Anchor(String label) {
            this.label = label;
        }


        @Override
        public String toString() {
            return label;
        }
    }


    public static Anchors fromString(String anchorsString) {
        if (anchorsString == null) return null;
        anchorsString = anchorsString.trim();
        if (anchorsString.equals("")) return null;
        Anchors result = new Anchors();
        for (String anchorString : anchorsString.split(" ")) {
            result.addAnchor(Anchor.fromString(anchorString));
        }
        return result;
    }


    private Set<Anchor> anchors = new HashSet<Anchor>();

    public Anchors(Anchor... anchors) {
        for (Anchor anchor : anchors) {
            this.anchors.add(anchor);
        }
    }


    public void addAnchor(Anchor newAnchor) {
        anchors.add(newAnchor);
    }


    public String toString() {
        StringBuffer buff = new StringBuffer();
        for (Anchor anchor : anchors) {
            if (buff.length() > 0) buff.append(" ");
            buff.append(anchor.toString());
        }
        return buff.toString();
    }


    public int size() {
        return anchors.size();
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((anchors == null) ? 0 : anchors.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Anchors other = (Anchors) obj;
        if (anchors == null) {
            if (other.anchors != null)
                return false;
        } else if (!anchors.equals(other.anchors))
            return false;
        return true;
    }


    public boolean contains(Anchor anchor) {
        return anchors.contains(anchor);
    }
}
