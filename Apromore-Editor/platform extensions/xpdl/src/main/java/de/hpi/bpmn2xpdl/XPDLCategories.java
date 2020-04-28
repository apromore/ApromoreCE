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

package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Categories")
public class XPDLCategories extends XMLConvertible {

    @Element("Category")
    protected ArrayList<XPDLCategory> categories;

    public ArrayList<XPDLCategory> getCategories() {
        return categories;
    }

    public void readJSONcategories(JSONObject modelElement) throws JSONException {
        passInformationToCategory(modelElement, "categories");
    }

    public void readJSONcategoriesunknowns(JSONObject modelElement) {
        readUnknowns(modelElement, "categoriesunknowns");
    }

    public void readJSONcategoryunknowns(JSONObject modelElement) throws JSONException {
        passInformationToCategory(modelElement, "categoryunknowns");
    }

    public void readJSONid(JSONObject modelElement) {
    }

    public void setCategories(ArrayList<XPDLCategory> categories) {
        this.categories = categories;
    }

    public void writeJSONcategories(JSONObject modelElement) {
        XPDLCategory firstCategory = getFirstCategory();
        if (firstCategory != null) {
            firstCategory.write(modelElement);
        }
    }

    public void writeJSONcategoriesunknowns(JSONObject modelElement) throws JSONException {
        writeUnknowns(modelElement, "categoriesunknowns");
    }

    protected XPDLCategory getFirstCategory() {
        ArrayList<XPDLCategory> categoriesList = getCategories();
        if (categoriesList != null) {
            if (categoriesList.size() >= 1) {
                return categoriesList.get(0);
            }
        }
        return null;
    }

    protected void initializeCategories() {
        if (getCategories() == null) {
            setCategories(new ArrayList<XPDLCategory>());
        }
    }

    protected void initializeCategory() {
        initializeCategories();

        ArrayList<XPDLCategory> categoriesList = getCategories();
        if (categoriesList.size() == 0) {
            categoriesList.add(new XPDLCategory());
        }
    }

    protected void passInformationToCategory(JSONObject modelElement, String key) throws JSONException {
        initializeCategory();

        JSONObject passObject = new JSONObject();
        passObject.put("id", modelElement.optString("id"));
        passObject.put(key, modelElement.optString(key));
        getFirstCategory().parse(passObject);
    }
}
