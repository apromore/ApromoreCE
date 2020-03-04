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

import org.processmining.framework.plugin.Progress;

/**
 * Created by Seppe vanden Broucke on 29/10/2014.
 */

public class FakeProgress implements Progress {

    public FakeProgress() {
        cancelled = false;
    }

    public void cancel() {
        cancelled = true;
    }

    public String getCaption() {
        return "";
    }

    public int getMaximum() {
        return 0;
    }

    public int getMinimum() {
        return 0;
    }

    public int getValue() {
        return 0;
    }

    public void inc() {
    }

    public boolean isCanceled() {
        return cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isIndeterminate() {
        return true;
    }

    public void setCaption(String s) {
    }

    public void setIndeterminate(boolean flag) {
    }

    public void setMaximum(int i) {
    }

    public void setMinimum(int i) {
    }

    public void setValue(int i) {
    }

    private boolean cancelled;
}