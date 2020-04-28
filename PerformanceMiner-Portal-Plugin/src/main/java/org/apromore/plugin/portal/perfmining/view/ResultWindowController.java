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
package org.apromore.plugin.portal.perfmining.view;

import java.io.IOException;
import java.util.HashMap;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.perfmining.DatasetFactory;
import org.apromore.plugin.portal.perfmining.Visualization;
import org.apromore.service.perfmining.models.SPF;
 
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;
 
public class ResultWindowController {
    private static final long serialVersionUID = 3814570327995355261L;
    private final PortalContext portalContext;
    private Window resultW;
    private Tree tree;
    private AdvancedTreeModel spfViewTreeModel;
    
    public ResultWindowController(PortalContext portalContext, SPF spf) throws IOException {
        this.portalContext = portalContext;
        this.resultW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/result.zul", null, null);
        this.resultW.setTitle("Performance Mining Result");
        
        // Set up UI elements
        tree = (Tree) this.resultW.getFellow("tree");
        spfViewTreeModel = new AdvancedTreeModel(new SPFViewList(spf).getRoot());
        tree.setItemRenderer(new SPFViewTreeRenderer());
        tree.setModel(spfViewTreeModel);        
        
        // Show the window
        this.resultW.doModal();        
    }
 
//    public void doAfterCompose(Component comp) throws Exception {
//        super.doAfterCompose(comp);     
//        contactTreeModel = new AdvancedTreeModel(new SPFViewList().getRoot());
//        tree.setItemRenderer(new SPFViewTreeRenderer());
//        tree.setModel(contactTreeModel);
//    }
 
    /**
     * The structure of tree
     * 
     * <pre>
     * &lt;treeitem>
     *   &lt;treerow>
     *     &lt;treecell>...&lt;/treecell>
     *   &lt;/treerow>
     *   &lt;treechildren>
     *     &lt;treeitem>...&lt;/treeitem>
     *   &lt;/treechildren>
     * &lt;/treeitem>
     * </pre>
     */
    private final class SPFViewTreeRenderer implements TreeitemRenderer<SPFViewTreeNode> {
        @Override
        public void render(final Treeitem treeItem, SPFViewTreeNode treeNode, int index) throws Exception {
            SPFViewTreeNode ctn = treeNode;
            SPFView spfView = (SPFView) ctn.getData();
            Treerow dataRow = new Treerow();
            dataRow.setParent(treeItem);
            treeItem.setValue(ctn);
            treeItem.setOpen(ctn.isOpen());
 
            if (!spfView.isCategory) { // SPFView Row
                Hlayout hl = new Hlayout();
                //hl.appendChild(new Image("/widgets/tree/dynamic_tree/img/"));
                hl.appendChild(new Label(spfView.getFullName()));
                hl.setSclass("h-inline-block");
                Treecell treeCell = new Treecell();
                treeCell.appendChild(hl);
                dataRow.setDraggable("false");
                dataRow.appendChild(treeCell);
                dataRow.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        SPFViewTreeNode clickedNodeValue = (SPFViewTreeNode) ((Treeitem) event.getTarget().getParent())
                                .getValue();
                        ((SPFView)clickedNodeValue.getData()).showChart(portalContext);
                    }
                });
            } else { // Category Row
                dataRow.appendChild(new Treecell(spfView.getFullName()));
            }
 
        }
    }
}
