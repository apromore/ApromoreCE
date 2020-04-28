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
package demo.data.pojo;
 
public class Contact {
    private final String name;
    private final String category;
 
    public Contact(String category) {
        this.category = category;
        this.name = null;
        this.profilepic = null;
    }
 
    public Contact(String name, String profilepic) {
        this.name = name;
        this.profilepic = profilepic;
        this.category = null;
    }
 
    public String getName() {
        return name;
    }
 
    public String getCategory() {
        return category;
    }
 
    public String getProfilepic() {
        return profilepic;
    }
 
    private final String profilepic;
}