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

package com.raffaeleconforti.wrappers.settings;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Adriano on 6/7/2017.
 */
public class MiningSettings {
    private Map<String, Object> params;

    public MiningSettings() { params = new HashMap<>(); }

    public void setParam(String param, Object value) { params.put(param, value); }
    public Object getParam(String param) { return params.get(param); }
    public boolean containsParam(String param) { return params.containsKey(param); }
}
