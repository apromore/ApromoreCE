/*-
 * #%L
 * This file is part of "Apromore Community".
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

package org.apromore.logfilter.criteria.impl.util;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * @author Chii Chang (created: 2019)
 * Modified: Chii Chang (23/03/2020)
 */
public class TimeUtil {
    public static long epochMilliOf(ZonedDateTime zonedDateTime){
        long s = zonedDateTime.toInstant().toEpochMilli();
        return s;
    }
    public static ZonedDateTime millisecondToZonedDateTime(long millisecond){
        Instant i = Instant.ofEpochMilli(millisecond);
        ZonedDateTime z = ZonedDateTime.ofInstant(i, ZoneId.systemDefault());
        return z;
    }
    public static ZonedDateTime zonedDateTimeOf(XEvent xEvent) {
        String timestampString = xEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString();
        Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime(timestampString);
        ZonedDateTime z = millisecondToZonedDateTime(calendar.getTimeInMillis());
        return z;
    }

    public static String convertTimestamp(long milliseconds) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault());
        return zdt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS"));
    }
    
	public static String valueToUnit(long milliseconds) {
		DecimalFormat decimalFormat = new DecimalFormat("##############0.##");
		double seconds = milliseconds / 1000.0D;
		double minutes = seconds / 60.0D;
		double hours = minutes / 60.0D;
		double days = hours / 24.0D;
		double weeks = days / 7.0D;
		double months = days / 30.0D;
		double years = days / 365.0D;
	    
		if (years > 1.0D) {
			return decimalFormat.format(years) + " Years";
		}
	  
		if (months > 1.0D) {
		  return decimalFormat.format(months) + " Months";
		}
	
		if (weeks > 1.0D) {
		  return decimalFormat.format(weeks) + " Weeks";
		}
	
		if (days > 1.0D) {
		  return decimalFormat.format(days) + " Days";
		}
	
		if (hours > 1.0D) {
		  return decimalFormat.format(hours) + " Hours";
		}
	
		if (minutes > 1.0D) {
		  return decimalFormat.format(minutes) + " Minutes";
		}
	  
		return decimalFormat.format(seconds) + " Seconds";
	}
}
