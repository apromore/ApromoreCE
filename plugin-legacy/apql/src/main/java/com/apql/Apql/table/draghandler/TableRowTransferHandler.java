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

package com.apql.Apql.table.draghandler;

import com.apql.Apql.controller.QueryController;
import com.apql.Apql.controller.ViewController;
import com.apql.Apql.table.TableProcess;
import com.apql.Apql.table.TableRow;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.HashSet;

/**
 * Created by conforti on 9/01/15.
 */
/**
 * Created by conforti on 9/01/15.
 */
public class TableRowTransferHandler extends TransferHandler {
    private QueryController queryController;
    private ViewController viewController;

    public TableRowTransferHandler(QueryController queryController, ViewController viewController) {
        this.queryController = queryController;
        this.viewController = viewController;
    }

    public void drop(JComponent c) {
        System.out.println(c.getClass().toString());
        System.out.println("TABLE ROW");
        TableProcess dragContents = (TableProcess)c.getParent().getParent().getParent().getParent();
        HashSet<String> locationsVersion = new HashSet<>();
        for(TableRow row : dragContents.getRows()) {
            if (row.isSelected()) {
                locationsVersion.add(row.toString());
                row.clearSelection();
            }
        }
        queryController.setLocations(locationsVersion);
        queryController.addQueryLocation();
        viewController.getTableProcess().clearSelection();
    }


    protected Transferable createTransferable(JComponent c) {
        drop(c);
        return new StringSelection("");
    }

    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }
}