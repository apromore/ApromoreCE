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

package com.raffaeleconforti.noisefiltering.event.selection;

import com.raffaeleconforti.automaton.Node;

import java.util.Set;

/**
 * Created by conforti on 26/02/15.
 */
public class NoiseFilterResult {

    private int approach;
    private boolean fixLevel;
    private double noiseLevel;
    private double percentile;
    private boolean repeated;
    private boolean removeTraces;
    private boolean removeNodes;
    private Set<Node<String>> requiredStates;

    public int getApproach() {
        return approach;
    }

    public void setApproach(int approach) {
        this.approach = approach;
    }

    public boolean isFixLevel() {
        return fixLevel;
    }

    public void setFixLevel(boolean fixLevel) {
        this.fixLevel = fixLevel;
    }

    public double getNoiseLevel() {
        return noiseLevel;
    }

    public void setNoiseLevel(double noiseLevel) {
        this.noiseLevel = noiseLevel;
    }

    public double getPercentile() {
        return percentile;
    }

    public void setPercentile(double percentile) {
        this.percentile = percentile;
    }

    public boolean isRepeated() {
        return repeated;
    }

    public void setRepeated(boolean repeated) {
        this.repeated = repeated;
    }

    public boolean isRemoveTraces() {
        return removeTraces;
    }

    public void setRemoveTraces(boolean removeTraces) {
        this.removeTraces = removeTraces;
    }

    public boolean isRemoveNodes() {
        return removeNodes;
    }

    public void setRemoveNodes(boolean removeNodes) {
        this.removeNodes = removeNodes;
    }

    public Set<Node<String>> getRequiredStates() {
        return requiredStates;
    }

    public void setRequiredStates(Set<Node<String>> requiredStates) {
        this.requiredStates = requiredStates;
    }

}
