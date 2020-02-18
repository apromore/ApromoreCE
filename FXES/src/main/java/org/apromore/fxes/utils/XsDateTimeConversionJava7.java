 /*
 * Copyright (c) 2013 F. Mannhardt (f.mannhardt@tue.nl)
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 * 
 */
package org.apromore.fxes.utils;
 
import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
 
/**
 * Provides a faster conversion of DateTime for XES serialization using the new
 * parse patterns of the SimpleDateFormat class in Java 7
 * 
 * @author F. Mannhardt
 * </br>
 * Modified by Alireza Ostovar 
 */
public class XsDateTimeConversionJava7 extends XsDateTimeConversion {
 
 
    private static final ThreadLocal<SoftReference<DateFormat>> THREAD_LOCAL_DF_WITH_MILLIS = new ThreadLocal<SoftReference<DateFormat>>();
    private static final ThreadLocal<SoftReference<DateFormat>> THREAD_LOCAL_DF_WITHOUT_MILLIS = new ThreadLocal<SoftReference<DateFormat>>();
    private final ParsePosition position = new ParsePosition(0);
     
    /**
     * Returns a DateFormat for each calling thread, using {@link ThreadLocal}.
     * 
     * @return a DateFormat that is safe to use in multi-threaded environments
     */
    private static DateFormat getDateFormatWithMillis() {
        return getThreadLocaleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                THREAD_LOCAL_DF_WITH_MILLIS);
    }
 
    /**
     * Returns a DateFormat for each calling thread, using {@link ThreadLocal}.
     * 
     * @return a DateFormat that is safe to use in multi-threaded environments
     */
    private static DateFormat getDateFormatWithoutMillis() {
        return getThreadLocaleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX",
                THREAD_LOCAL_DF_WITHOUT_MILLIS);
    }
 
    private static DateFormat getThreadLocaleDateFormat(String formatString,
        ThreadLocal<SoftReference<DateFormat>> threadLocal) {
        SoftReference<DateFormat> softReference = threadLocal.get();
        if (softReference != null) {
            DateFormat dateFormat = softReference.get();
            if (dateFormat != null) {
                return dateFormat;
            }
        }
        DateFormat result = new SimpleDateFormat(formatString, Locale.US);
         
        softReference = new SoftReference<DateFormat>(result);
        threadLocal.set(softReference);
        return result;
    }
     
      
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.deckfour.xes.util.XsDateTimeConversion#parseXsDateTime(java.lang.
     * String)
     */
    public Date parseXsDateTime(String xsDateTime) {
    // Try Java 7 parsing method
        // Use with ParsePosition to avoid throwing and catching a lot of
        // exceptions, if our parsing method does not work
        position.setIndex(0);
        Date parsedDate = getDateFormatWithMillis().parse(xsDateTime,
                position);
        if (parsedDate == null) {
            // Try format without milliseconds
            position.setIndex(0);
            position.setErrorIndex(0);
            parsedDate = getDateFormatWithoutMillis().parse(xsDateTime,
                    position);
            if (parsedDate == null) {
                // Fallback to old Java 6 method
                return super.parseXsDateTime(xsDateTime);
            } else {
                return parsedDate;
            }
        } else {
            return parsedDate;
        }
    }
 
    /*
     * (non-Javadoc)
     * 
     * @see org.deckfour.xes.util.XsDateTimeConversion#format(java.util.Date)
     */
    @Override
    public String format(Date date) {
    	return getDateFormatWithMillis().format(date);
    }
 
}