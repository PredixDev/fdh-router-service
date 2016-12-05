package com.ge.predix.solsvc.fdh.router.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for String manipulation
 * 
 * @author tturner
 * 
 */
public class StringUtil
{

    private static final Logger log = LoggerFactory.getLogger(StringUtil.class);

    /**
     * @param enums -
     * @return -
     */
    public static <T extends Enum<T>> List<String> enumToString(List<T> enums)
    {
        List<String> stringList = new ArrayList<String>();
        for (T theEnum : enums)
        {
            stringList.add(theEnum.name());
        }
        return stringList;
    }

    /**
     * @return -
     */
    @SuppressWarnings("nls")
    public static String getClasspath()
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        URL[] urls = null;
        if ( cl instanceof URLClassLoader )
        {
            urls = ((URLClassLoader) cl).getURLs();
            StringBuffer buffer = new StringBuffer();
            for (URL url : urls)
            {
                buffer.append(url.getFile());
            }

            return buffer.toString();
        }
        // else if ( cl instanceof BundleDelegatingClassLoader )
        // {
        // // urls = ((BundleDelegatingClassLoader)cl).get;
        // StringBuffer buffer = new StringBuffer();
        // // for(URL url: urls){
        // // buffer.append(url.getFile());
        // // }
        //
        // return buffer.toString();
        // }
        return "unknown classpath for cl=" + cl;

    }

    /**
     * e.g. "2012-09-11T10:10:10"
     * 
     * @param dateTime -
     * @return -
     */
    @SuppressWarnings("nls")
    public static XMLGregorianCalendar getXMLDate(Date dateTime)
    {
        DatatypeFactory f = null;
        try
        {
            f = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e)
        {
            throw new RuntimeException("convert to runtime exception", e);
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.setTime(dateTime);
        XMLGregorianCalendar xmLGregorianCalendar = f.newXMLGregorianCalendar(cal);
        return xmLGregorianCalendar;
    }

    /**
     * @param d1 -
     * @return -
     */
    @SuppressWarnings("nls")
    public static String convertToDateTimeString(Date d1)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String dString = null;
        dString = df.format(d1);
        return dString;
    }

    /**
     * @param gmtTimeString -
     * @return -
     */
    @SuppressWarnings("nls")
    public static Date parseDate(String gmtTimeString)
    {
        try
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            df.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
            Date date = df.parse(gmtTimeString);
            return date;
        }
        catch (ParseException e)
        {
            throw new RuntimeException("convrt to runtime exception", e);
        }
    }

}
