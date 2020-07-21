/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package com.apql.Apql.highlight;

/**
 * Created by corno on 28/07/2014.
 */

import com.apql.Apql.popup.ProcessLabel;

import java.awt.Component;

import javax.swing.*;

public class ProcessListRenderer extends  DefaultListCellRenderer {
    protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
    private final ImageIcon folderImg = new ImageIcon(getClass().getResource("/icons/"));
    private final ImageIcon modelImg = new ImageIcon(getClass().getResource("/icons/bpmn_22x22.png"));

    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean selected,
            boolean expanded) {

        ProcessLabel label=(ProcessLabel)value;
        return label;
    }
}
