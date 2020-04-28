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

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
 
public class SummaryViewModel {
     
    SummaryData summaryData = new SummaryData();
     
    public SummaryData getData() {
        return summaryData;
    }
 
//    @Command
//    @NotifyChange("mailData")
//    public void revertItem() {
//        mailData.revertDeletedItems();
//    }
//     
//    @Command
//    @NotifyChange("mailData")
//    public void deleteAllItems() {
//        mailData.deleteAllItems();
//    }
//     
//    @Command
//    @NotifyChange("mailData")
//    public void removeItem(@BindingParam("mail") SummaryLineItem myItem) {
//        mailData.deleteItem(myItem);
//    }
}
