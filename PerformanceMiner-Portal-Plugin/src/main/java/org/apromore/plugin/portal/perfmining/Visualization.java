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
package org.apromore.plugin.portal.perfmining;

import org.jfree.data.time.TimeTableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Administrator
 */
public class Visualization {
    public static JSONObject createCFDJson(XYDataset ds) throws JSONException {
        JSONObject json = new JSONObject();
        
        //-----------------------------------------
        // For the series
        //-----------------------------------------
        JSONArray jsonSeriesArray = new JSONArray();
        
//        for (int i=0;i<ds.getSeriesCount();i++) {
        for (int i=(ds.getSeriesCount()-1);i>=0;i--) {
            System.out.println("Start series " + ds.getSeriesKey(i).toString());
            //----------------
            //For one series
            //----------------
            JSONObject jsonOneSeries = new JSONObject();
            jsonOneSeries.put("name", ds.getSeriesKey(i).toString());
            jsonOneSeries.put("type", "area");
            
            if (ds.getSeriesKey(i).toString().contains("Exit")) {
                jsonOneSeries.put("color", "grey");
            }
            
            //For data array in one series
            JSONArray jsonOneSeriesData = new JSONArray();
            for (int j=0;j<ds.getItemCount(i);j++) {
                JSONArray jsonDataItem = new JSONArray();
                jsonDataItem.put(ds.getXValue(i, j));
                jsonDataItem.put(ds.getYValue(i, j));
                jsonOneSeriesData.put(jsonDataItem); 
            }
            jsonOneSeries.put("data",jsonOneSeriesData);
            
            jsonSeriesArray.put(jsonOneSeries);
            System.out.println("Finish series " + ds.getSeriesKey(i).toString());
        }
        //jsonSeries.put("series", jsonSeriesArray);
        json.put("series", jsonSeriesArray);
        
        return json;
    }
    
    public static JSONObject createChartJson(XYDataset ds) throws JSONException {
        JSONObject json = new JSONObject();
        
        //-----------------------------------------
        // For the series
        //-----------------------------------------
        JSONArray jsonSeriesArray = new JSONArray();
        
        for (int i=(ds.getSeriesCount()-1);i>=0;i--) {
            System.out.println("Start series " + ds.getSeriesKey(i).toString());
            //----------------
            //For one series
            //----------------
            JSONObject jsonOneSeries = new JSONObject();
            jsonOneSeries.put("name", ds.getSeriesKey(i).toString());
            jsonOneSeries.put("tooltip", "{valueDecimals: 0}");
            
            //For data array in one series
            JSONArray jsonOneSeriesData = new JSONArray();
            for (int j=0;j<ds.getItemCount(i);j++) {
                JSONArray jsonDataItem = new JSONArray();
                jsonDataItem.put(ds.getXValue(i, j));
                jsonDataItem.put(ds.getYValue(i, j));
                jsonOneSeriesData.put(jsonDataItem); 
            }
            jsonOneSeries.put("data",jsonOneSeriesData);
            
            jsonSeriesArray.put(jsonOneSeries);
            System.out.println("Finish series " + ds.getSeriesKey(i).toString());
        }
        //jsonSeries.put("series", jsonSeriesArray);
        json.put("series", jsonSeriesArray);
        
        return json;
        
    }
}
