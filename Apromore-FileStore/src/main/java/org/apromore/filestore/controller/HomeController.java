/*-
 * #%L
 * This file is part of "Apromore Community".
 * 
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
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

package org.apromore.filestore.controller;

import org.apromore.filestore.client.FileStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Spring Controller to handle getting and displaying the user picked folder.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Controller
public class HomeController {

    @Autowired
    @Qualifier("fileStoreClient")
    private FileStoreService fileStoreService;


    /**
     * This is the entry for the home page.
     * Since we are only hitting this page at the start we have no data so auto pick the root to display.
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String home(final Model model) throws Exception {
        model.addAttribute("contents", fileStoreService.list("/"));
        return "index";
    }

}
