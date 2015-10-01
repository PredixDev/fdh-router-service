package com.ge.fdh.asset.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 
 * @author predix -
 */
public class DateUtil
{
    /**
     * 
     */
    public static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"; //$NON-NLS-1$

    /**
     * @param iso -
     * @return -
     */
    @SuppressWarnings("nls")
    public static Date getDateFromISOString(String iso)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        sdf.setLenient(false);
        try
        {
            if ( iso == null )
                return null;
            return sdf.parse(iso);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param iso -
     * @param format -
     * @return -
     */
    public static Date getDateFromString(String iso, String format )
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try
        {
            return sdf.parse(iso);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param time -
     * @return -
     */
    @SuppressWarnings("nls")
    public static String getISOStringFromDate(long time)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date(time));
    }

    /**
     * @param date -
     * @return -
     */
    public static String getISOStringFromDate(Date date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); //$NON-NLS-1$
        return sdf.format(date);
    }

}
