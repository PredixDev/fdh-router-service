/**
 * Copyright (C) 2012 General Electric Company
 * All rights reserved.
 *
 */

package com.ge.predix.solsvc.fdh.handler.asset.helper;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The purpose of this class is to convert XMLCalendar, XMLDate, XMLTime to java types.
 * Also, to provide utility methods for constructing new XMLGregorianCalendar objects.
 */
public class XMLCalendarAdapter {
    private static Logger logger = LoggerFactory.getLogger(XMLCalendarAdapter.class);

    /**
     *
     * @param xmlCalendar -
     * @return -
     */
    public static Calendar getCalendar(XMLGregorianCalendar xmlCalendar){

        TimeZone timeZone = xmlCalendar.getTimeZone(xmlCalendar.getTimezone());
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(Calendar.YEAR,xmlCalendar.getYear());
        calendar.set(Calendar.MONTH,xmlCalendar.getMonth()-1);
        calendar.set(Calendar.DATE,xmlCalendar.getDay());
        calendar.set(Calendar.HOUR_OF_DAY,xmlCalendar.getHour());
        calendar.set(Calendar.MINUTE,xmlCalendar.getMinute());
        calendar.set(Calendar.SECOND,xmlCalendar.getSecond());

        return calendar;
    }

    /**
     *
     * @param xmlCalendar -
     * @return the following date string and in this format: 2013-11-19T10:11:40.095
     */
    @SuppressWarnings("nls")
    public static String getIsoDateString(XMLGregorianCalendar xmlCalendar){
        String isoDateStr = null;

        if (xmlCalendar != null && xmlCalendar.toString().indexOf(".") != -1){
            isoDateStr = xmlCalendar.toString().substring(0,xmlCalendar.toString().indexOf(".")+4);
        }

        return isoDateStr;
    }

    /**
     *
     * @return XMLGregorianCalendar in the current TimeZone with the currentTime and date
     */
    public static XMLGregorianCalendar newInstance(){

        // let's get the time now in the current TimeZone
        Calendar now = Calendar.getInstance(TimeZone.getDefault());


        // let create new object to return for XMLGregorianCalendar
        XMLGregorianCalendar dateTime = null;
        try {
            GregorianCalendar c = new GregorianCalendar(TimeZone.getDefault());

            dateTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

            // let init the new calendar with the current time
            dateTime.setMonth(now.get(Calendar.MONTH)+1);
            dateTime.setDay(now.get(Calendar.DATE));
            dateTime.setYear(now.get(Calendar.YEAR));
            dateTime.setHour(now.get(Calendar.HOUR));
            dateTime.setMinute(now.get(Calendar.MINUTE));
            dateTime.setSecond(now.get(Calendar.SECOND));
        } catch (DatatypeConfigurationException ex) {
            // it should not happen
            logger.error("Error while creating Xml Calendar", ex);
        }

        // let's return the calendar
        return dateTime;
    }
}
