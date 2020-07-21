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
package au.ltl.main;

import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;

import java.util.LinkedList;

/**
 * Created by armascer on 25/11/2017.
 */
public class RuleVisualization {
    private String elementId;
    private String legend;
    private String color;

    private LinkedList<String> toRemove;
    private String start;
    private String end;

    private String type;
    private LinkedList<String> newTasks;
    private LinkedList<String> sourceTargetNew;

    public RuleVisualization(String elementId, String legend, String color){
        this.elementId = elementId;
        this.legend = legend;
        this.color = color;
        this.toRemove = new LinkedList<>();
        this.newTasks = new LinkedList<>();
        this.sourceTargetNew = new LinkedList<>();
        this.type = "";
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getLegend() {
        return legend;
    }

    public void setLegend(String legend) {
        this.legend = legend;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void addToRemove(String element) {
        this.toRemove.add(element);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addNewTask(String newTask) {
        this.newTasks.add(newTask);
    }

    public void addPrePost(String source, String target) {
        this.sourceTargetNew.add(source);
        this.sourceTargetNew.add(target);
    }
}
