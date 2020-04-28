/*-
 * #%L
 * This file is part of "Apromore Community".
 *
 * Copyright (C) 2017 Bruce Nguyen.
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apromore.plugin.portal.perfmining.view.system;

import org.zkoss.zul.*;

/**
 *
 * @author Administrator
 */
public class SummaryRowRenderer implements RowRenderer {
    
    public void render(final Row row, final java.lang.Object data) {
        String[] ary = (String[]) data;
        new Label(ary[0]).setParent(row);
        new Label(ary[1]).setParent(row);
        new Label(ary[2]).setParent(row);
        new Label(ary[3]).setParent(row);
        new Label(ary[4]).setParent(row);
        new Label(ary[5]).setParent(row);
        new Label(ary[6]).setParent(row);
    }
  
    public void render(final Row row, final java.lang.Object data, int test) {
        String[] ary = (String[]) data;
        new Label(ary[0]).setParent(row);
        new Label(ary[1]).setParent(row);
        new Label(ary[2]).setParent(row);
        new Label(ary[3]).setParent(row);
        new Label(ary[4]).setParent(row);
        new Label(ary[5]).setParent(row);
        new Label(ary[6]).setParent(row);
    }  
}
