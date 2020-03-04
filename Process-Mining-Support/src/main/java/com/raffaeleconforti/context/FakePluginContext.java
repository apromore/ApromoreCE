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

package com.raffaeleconforti.context;

import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.ConnectionID;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginExecutionResult;
import org.processmining.framework.plugin.ProMFuture;
import org.processmining.framework.plugin.Progress;

import java.util.Iterator;

/**
 * Created by Seppe vanden Broucke on 29/10/2014.
 */

public class FakePluginContext extends UIPluginContext {

    public FakePluginContext() {
        this(MAIN_PLUGINCONTEXT, "Fake Plugin Context");
    }

    public FakePluginContext(UIPluginContext context, String label) {
        super(context, label);
    }

    public FakePluginContext(PluginContext context) {
        this(MAIN_PLUGINCONTEXT, "Fake Plugin Context");
        for (Iterator<ConnectionID> iterator = context.getConnectionManager().getConnectionIDs().iterator(); iterator.hasNext(); ) {
            ConnectionID cid = iterator.next();
            try {
                org.processmining.framework.connections.Connection connection = context.getConnectionManager().getConnection(cid);
                addConnection(connection);
            } catch (ConnectionCannotBeObtained connectioncannotbeobtained) {
            }
        }

    }

    public Progress getProgress() {
        return new FakeProgress();
    }

    public ProMFuture getFutureResult(int i) {
        return new ProMFuture(String.class, "Fake Future") {

            @Override
            protected Object doInBackground() {
                return null;
            }
        };
    }

    public void setFuture(PluginExecutionResult pluginexecutionresult) {
    }

    private static UIPluginContext MAIN_PLUGINCONTEXT;

    static {
        UIContext MAIN_CONTEXT = new UIContext();
        MAIN_PLUGINCONTEXT = MAIN_CONTEXT.getMainPluginContext().createChildContext("");
    }
}
