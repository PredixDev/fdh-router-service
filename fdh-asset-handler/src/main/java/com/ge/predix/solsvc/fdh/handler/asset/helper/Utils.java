/**
 *
 * The information contained in this document is General Electric Company (GE) 
 * proprietary information and is disclosed in confidence. It is the property 
 * of GE and shall not be used, disclosed to others or reproduced without the 
 * express written consent of GE. If consent is given for reproduction in whole 
 * or in part, this notice and the notice set forth on each page of this document 
 * shall appear in any such reproduction in whole or in part. The information 
 * contained in this document may also be controlled by the U.S. export control 
 * laws.  Unauthorized export or re-export is prohibited.
 *
 * This module is intended for:  (Choose one of the following options)
 *      Military use only
 *  X   Commercial use only
 *      Both military and commercial use
 */
package com.ge.predix.solsvc.fdh.handler.asset.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.mimosa.osacbmv3_3.BooleanArrayValue;
import org.mimosa.osacbmv3_3.ByteArrayValue;
import org.mimosa.osacbmv3_3.CharArrayValue;
import org.mimosa.osacbmv3_3.DABLOBData;
import org.mimosa.osacbmv3_3.DABool;
import org.mimosa.osacbmv3_3.DADataEvent;
import org.mimosa.osacbmv3_3.DADataSeq;
import org.mimosa.osacbmv3_3.DAInt;
import org.mimosa.osacbmv3_3.DAReal;
import org.mimosa.osacbmv3_3.DAString;
import org.mimosa.osacbmv3_3.DAValueDataSeq;
import org.mimosa.osacbmv3_3.DAValueWaveform;
import org.mimosa.osacbmv3_3.DAVector;
import org.mimosa.osacbmv3_3.DAWaveform;
import org.mimosa.osacbmv3_3.DMBLOBData;
import org.mimosa.osacbmv3_3.DMBool;
import org.mimosa.osacbmv3_3.DMDataEvent;
import org.mimosa.osacbmv3_3.DMDataSeq;
import org.mimosa.osacbmv3_3.DMInt;
import org.mimosa.osacbmv3_3.DMReal;
import org.mimosa.osacbmv3_3.DMVector;
import org.mimosa.osacbmv3_3.DataEvent;
import org.mimosa.osacbmv3_3.DataEventSet;
import org.mimosa.osacbmv3_3.DblArrayValue;
import org.mimosa.osacbmv3_3.DoubleValue;
import org.mimosa.osacbmv3_3.ErrorInfo;
import org.mimosa.osacbmv3_3.FloatArrayValue;
import org.mimosa.osacbmv3_3.IntArrayValue;
import org.mimosa.osacbmv3_3.LongArrayValue;
import org.mimosa.osacbmv3_3.MIMKey3;
import org.mimosa.osacbmv3_3.NumAlert;
import org.mimosa.osacbmv3_3.OsacbmDataType;
import org.mimosa.osacbmv3_3.OsacbmTime;
import org.mimosa.osacbmv3_3.OsacbmTimeType;
import org.mimosa.osacbmv3_3.Parameter;
import org.mimosa.osacbmv3_3.SITECATEGORY;
import org.mimosa.osacbmv3_3.SelectionFilter;
import org.mimosa.osacbmv3_3.ShortArrayValue;
import org.mimosa.osacbmv3_3.Site;
import org.mimosa.osacbmv3_3.StringArrayValue;
import org.mimosa.osacbmv3_3.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for OSA-CBM data conversion.
 * 
 * @author Bowden Wise
 */
public class Utils
{
    private static final Logger log                      = LoggerFactory.getLogger(Utils.class);
    // public static final String NL = System.getProperty("line.separator");
    // public static final String SystemFustionDataEventTag =
    // "SystemFusionDataEvent";
    // public static final String PrognosicsStrengthDataEventTag =
    // "PrognosicsStrengthDataEvent";

    /**
     * 
     */
    public static final Double  signedEightByteMaxNumber = java.lang.Math.pow(2, 64 - 1);
    /**
     * 
     */
    public static final Double  signedEightByteMinNumber = -(signedEightByteMaxNumber - 1);

    /**
     * 
     */
    public static final Double  signedSixByteMaxNumber   = java.lang.Math.pow(2, 48 - 1);
    /**
     * 
     */
    public static final Double  signedSixByteMinNumber   = -(signedSixByteMaxNumber - 1);

    /**
     * 
     */
    public static final Double  signedFourByteMaxNumber  = java.lang.Math.pow(2, 32 - 1);
    /**
     * 
     */
    public static final Double  signedFourByteMinNumber  = -(signedFourByteMaxNumber - 1);

    /**
     * Utility classes should not have a public or default constructor.
     */
    private Utils()
    {

    }

    /**
     * Convert an OsacbmTime vector to a vector of doubles
     * 
     * @param time
     *            vector of OsacbmTime values
     * @return a vector of doubles
     */
    public static double[] convertOsacbmTimeVectorToDoubleVector(OsacbmTime[] time)
    {

        double[] ret = new double[time.length];
        for (int i = 0; i < time.length; i++)
        {
            double val = time[i].getTimeBinary().doubleValue();
            ret[i] = val;
        }

        return ret;
    }

    /**
     * Convert OsacbmTime to Long
     * 
     * @param osaTime
     *            OsacbmTime
     * @return Date
     */
    public static Long convertOsacbmTimeToLong(OsacbmTime osaTime)
    {
        if ( osaTime == null )
        {
            return null;
        }

        OsacbmTimeType timeType = osaTime.getTimeType();
        if ( timeType.compareTo(OsacbmTimeType.OSACBM_TIME_SYSTEM_TICK) == 0
                || timeType.compareTo(OsacbmTimeType.OSACBM_TIME_POSIX_NSEC_8) == 0
                || timeType.compareTo(OsacbmTimeType.OSACBM_TIME_POSIX_USEC_8) == 0 )
        {
            return osaTime.getTimeBinary().longValue();
        }
        else if ( timeType.compareTo(OsacbmTimeType.OSACBM_TIME_MIMOSA) == 0 )
        {
            Date date = convertStringToDate(osaTime.getTime());
            return date.getTime();
        }
        else
        {
            // FIXME - should actually get the lower 6 bytes only (not the whole
            // 8 bytes - hopefully salim is implementing this code
            // the mask is: sum(FFx * 2**(8 * i)) for i in 0 .. 5 ?
            return osaTime.getTimeBinary().longValue();
        }
    }

    /**
     * Converts OsacbmTime array to a list of String
     * 
     * @param vals
     *            array of OsacbmTime
     * @return list of String containing converted OsacbmTime
     */
    public static List<String> convertOsacbmTimeToStrings(OsacbmTime[] vals)
    {
        List<String> ret = new ArrayList<String>();

        if ( vals != null )
        {
            for (OsacbmTime val : vals)
            {
                ret.add(getDisplayDateFormat().format(Utils.convertOsacbmTimeToDate(val)));
            }
        }
        return ret;
    }

    /**
     * add a value to an osacbm time object. We assume that the value is in the
     * same units as the time value
     * 
     * @param timeObj
     *            -
     * @param val
     *            -
     * @throws OsacbmConversionException
     *             -
     */
    public static void addToTime(OsacbmTime timeObj, double val)
            throws OsacbmConversionException
    {
        // if a time object is not provided
        if ( timeObj == null )
        {
            throw new OsacbmConversionException("null time object passed to addToTime"); //$NON-NLS-1$
        }
        Double newTime;
        newTime = convertOsacbmTimeToDouble(timeObj) + val;

        if ( timeObj.getTimeType().compareTo(OsacbmTimeType.OSACBM_TIME_MIMOSA) == 0 )
        {
            Date date = new Date(Math.round(newTime));
            timeObj.setTime(getIsoFormat().format(date));
        }
        else
        {
            BigDecimal bd = new BigDecimal(newTime);
            BigInteger bi = bd.toBigInteger();
            timeObj.setTimeBinary(bi);
        }
    }

    /**
     * Converts date to OsacbmTime
     * 
     * @param date
     *            java.util.Date
     * @return OsacbmTime
     */
    public static OsacbmTime convertDateToOsacbmTime(Date date)
    {
        OsacbmTime osaTime = new OsacbmTime();

        String timeString = getIsoFormat().format(date);
        osaTime.setTime(timeString);
        long sec = date.getTime();
        osaTime.setTimeBinary(new BigInteger(sec + "")); //$NON-NLS-1$
        osaTime.setTimeType(OsacbmTimeType.OSACBM_TIME_MIMOSA);

        return osaTime;
    }

    /**
     * Converts date to OsacbmTime
     * 
     * @param date
     *            java.util.Date
     * @param milliSeconds
     *            -
     * @return OsacbmTime
     */
    public static OsacbmTime convertDateToOsacbmTime(Date date, long milliSeconds)
    {
        OsacbmTime osaTime = new OsacbmTime();

        String timeString = getIsoFormat().format(date);
        osaTime.setTime(timeString);
        long sec = date.getTime() + milliSeconds;
        osaTime.setTimeBinary(new BigInteger(sec + "")); //$NON-NLS-1$
        osaTime.setTimeType(OsacbmTimeType.OSACBM_TIME_MIMOSA);

        return osaTime;
    }

    /**
     * Converts date to OsacbmTime
     * 
     * @param date
     *            java.util.Date
     * @param binaryValue
     *            -
     * @param timeType
     *            -
     * @return OsacbmTime
     */
    public static OsacbmTime convertDateToOsacbmTime(Date date, BigInteger binaryValue, OsacbmTimeType timeType)
    {
        OsacbmTime osaTime = new OsacbmTime();

        String timeString = getIsoFormat().format(date);
        osaTime.setTime(timeString);
        long mSec = date.getTime();

        // FIXME merge With Salim's changes - if typeType is not tick, then
        // timeBinary
        // is the timeType representation for timeString in
        // milliseconds/nanseconds from Unix Epoch
        // if timeType is tick timeString is null
        osaTime.setTimeBinary(new BigInteger(mSec + "")); //$NON-NLS-1$
        osaTime.setTimeType(timeType);

        return osaTime;
    }

    /**
     * Convert OsacbmTime array to java.util.Date array
     * 
     * @param osaTimes
     *            osaCbmTime array
     * @return java.util.Date array
     */
    public static Date[] convertOsacbmTimeArrayToDateArray(OsacbmTime[] osaTimes)
    {
        Date[] ds = new Date[osaTimes.length];

        for (int x = 0; x < ds.length; x++)
        {
            ds[x] = convertOsacbmTimeToDate(osaTimes[x]);
        }

        return ds;
    }

    /**
     * Convert OsacbmTime to Date
     * 
     * @param osaTime
     *            OsacbmTime
     * @return Date
     */
    public static Date convertOsacbmTimeToDate(OsacbmTime osaTime)
    {
        if ( osaTime == null )
        {
            return null;
        }

        Date d = null;
        OsacbmTimeType timeType = osaTime.getTimeType();
        if ( OsacbmTimeType.OSACBM_TIME_MIMOSA.equals(timeType) )
        {
            // construct date using 'time' property of the OsacbmTime
            if ( osaTime.getTime() != null )
            {
                d = getIsoFormat().parse(osaTime.getTime(), new ParsePosition(0));
                if ( d == null )
                {
                    throw new RuntimeException("Unable to parse time : " + osaTime.getTime() //$NON-NLS-1$
                            + " expected date format : " + " for  time type " + OsacbmTimeType.OSACBM_TIME_MIMOSA); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
        else if ( osaTime.getTimeBinary() != null )
        {

            if ( OsacbmTimeType.OSACBM_TIME_POSIX_NSEC_8.equals(timeType) )
            {
                // construct date using 'timeBinary' property of the OsacbmTime
                // which is nanoseconds
                // from UNIX EPOCH
                numberRangeCheck(osaTime.getTimeBinary(), timeType);
                d = new java.util.Date(osaTime.getTimeBinary().longValue() / (1000 * 1000));

            }
            else if ( OsacbmTimeType.OSACBM_TIME_POSIX_USEC_8.equals(timeType) )
            {
                // construct date using 'timeBinary' property of the OsacbmTime
                // which is microseconds
                // from UNIX EPOCH
                numberRangeCheck(osaTime.getTimeBinary(), timeType);
                d = new java.util.Date(osaTime.getTimeBinary().longValue() / 1000);

            }
            else if ( OsacbmTimeType.OSACBM_TIME_POSIX_USEC_6.equals(timeType) )
            {
                // construct date using 'timeBinary' property of the OsacbmTime
                // which is microseconds
                // from UNIX EPOCH
                numberRangeCheck(osaTime.getTimeBinary(), timeType);
                d = new java.util.Date(osaTime.getTimeBinary().longValue() / 1000);

            }
            else if ( OsacbmTimeType.OSACBM_TIME_POSIX_MSEC_6.equals(timeType) )
            {
                // construct date using 'timeBinary' property of the OsacbmTime
                // which is miliseconds
                // from UNIX EPOCH
                numberRangeCheck(osaTime.getTimeBinary(), timeType);
                d = new java.util.Date(osaTime.getTimeBinary().longValue());

            }
            else if ( OsacbmTimeType.OSACBM_TIME_POSIX_SEC_4.equals(timeType) )
            {
                // construct date using 'timeBinary' property of the OsacbmTime
                // which is seconds
                // from UNIX EPOCH
                numberRangeCheck(osaTime.getTimeBinary(), timeType);
                d = new java.util.Date(osaTime.getTimeBinary().longValue() * 1000);

            }
            else if ( OsacbmTimeType.OSACBM_TIME_TICK_NSEC.equals(timeType) )
            {

                d = new java.util.Date(osaTime.getTimeBinary().longValue() / (1000 * 1000));

            }
            else if ( OsacbmTimeType.OSACBM_TIME_TICK_USEC.equals(timeType) )
            {

                d = new java.util.Date(osaTime.getTimeBinary().longValue() / (1000));

            }
            else if ( OsacbmTimeType.OSACBM_TIME_TICK_MSEC.equals(timeType) )
            {

                d = new java.util.Date(osaTime.getTimeBinary().longValue());

            }
            else if ( OsacbmTimeType.OSACBM_TIME_SYSTEM_TICK.equals(timeType) )
            {

                d = new java.util.Date(osaTime.getTimeBinary().longValue());
            }
            else
            {
                d = new java.util.Date(osaTime.getTimeBinary().longValue());
            }
        }
        else
        {
            throw new UnsupportedOperationException("Invalid osaTime : time is empty "); //$NON-NLS-1$
        }
        return d;
    }

    /**
     * Converts OsacbmTime to Double type value
     * 
     * @param osaTime OsacbmTime
     * @return converted OsacbmTime double value
     */
    @Deprecated
    public static Double convertOsacbmTimeToDouble(OsacbmTime osaTime)
    {
        if ( osaTime == null )
        {
            return null;
        }

        if ( osaTime.getTimeType().compareTo(OsacbmTimeType.OSACBM_TIME_MIMOSA) == 0 )
        {
            Date d;
            try
            {
                d = getIsoFormat().parse(osaTime.getTime());
            }
            catch (ParseException e)
            {
                throw new RuntimeException("unable to prse osaTime.getTime()");
            }
            return d.getTime() + 0.0;
        }

        return osaTime.getTimeBinary().doubleValue();
    }

    /**
     * get the MSec value for an osa time structure
     * 
     * @param time
     *            -
     * @return -
     * @throws ParseException
     *             -
     */
    public static Double convertOsacbmTimeToDoubleMSec(OsacbmTime time)
            throws ParseException
    {
        Double rawValue = convertOsacbmTimeToDouble(time);

        if ( time.getTimeType().compareTo(OsacbmTimeType.OSACBM_TIME_POSIX_NSEC_8) == 0
                || time.getTimeType().compareTo(OsacbmTimeType.OSACBM_TIME_TICK_NSEC) == 0 )
        {
            // convert nanoseconds (10 ** -9) to milliseconds (10 ** -3)
            return rawValue / 1000000;
        }
        else if ( time.getTimeType().compareTo(OsacbmTimeType.OSACBM_TIME_POSIX_USEC_6) == 0
                || time.getTimeType().compareTo(OsacbmTimeType.OSACBM_TIME_TICK_USEC) == 0 )
        {
            // convert microseconds (10 ** -6) to milliseconds (10 ** -3)
            return rawValue / 1000;
        }
        else if ( time.getTimeType().compareTo(OsacbmTimeType.OSACBM_TIME_POSIX_SEC_4) == 0
                || time.getTimeType().compareTo(OsacbmTimeType.OSACBM_TIME_TICK_USEC) == 0 )
        {
            // convert seconds to milliseconds (10 ** -3)
            return rawValue * 1000;
        }
        else
        {
            return rawValue;
        }
    }

    /**
     * Expects either a {@link org.mimosa.osacbmv3_3.DMDataSeq DMDataSeq} or a {@link org.mimosa.osacbmv3_3.DADataSeq DADataSeq} and returns true if the
     * number of points is greater than the desired number of points and false
     * if it is equal to or less than the desired number of points.
     * 
     * @param data
     *            dataEvent instance
     * @param minimumPoints
     *            minimum number of points
     * @return true if meets minimum, otherwise false.
     */
    public static boolean meetsTimeSeriesMinimum(Object data, Integer minimumPoints)
    {

        if ( data instanceof DataEventSet )
        {
            DataEventSet dataEventSet = (DataEventSet) data;
            if ( dataEventSet.getDataEvents().size() > 1 )
            {
                return meetsDataEventSetTimeSeriesMinimum(dataEventSet, minimumPoints);
            }
            else if ( dataEventSet.getDataEvents().size() == 1 )
            {
                DataEvent dataEvent = (dataEventSet.getDataEvents().get(0));
                return meetsTimeSeriesMinimum(dataEvent, minimumPoints);
            }
            else
            {
                return false;
            }
        }
        else if ( data instanceof DMDataSeq )
        {
            return meetsDMDataSeqTimeSeriesMinimum((DMDataSeq) data, minimumPoints);
        }
        else if ( data instanceof DADataSeq )
        {
            return meetsDADataSeqTimeSeriesMinimum((DADataSeq) data, minimumPoints);
        }
        else
        {
            return false;
        }
    }

    /**
     * Checks the number of values contained within the passed in {@link org.mimosa.osacbmv3_3.DMDataSeq DMDataSeq} and returns true if the
     * number of points is greater than the desired number of points and false
     * if it is equal to or less than the desired number of points.
     * 
     * @param dataEvent
     *            -
     * @param minimumPoints
     *            -
     * @return -
     */
    public static boolean meetsDMDataSeqTimeSeriesMinimum(DMDataSeq dataEvent, Integer minimumPoints)
    {
        boolean meetsTimeSeriesMinimum = false;

        if ( dataEvent.getValues() != null && dataEvent.getValues().size() > minimumPoints )
        {
            meetsTimeSeriesMinimum = true;
        }

        return meetsTimeSeriesMinimum;
    }

    /**
     * Checks the number of values contained within the passed in {@link org.mimosa.osacbmv3_3.DataEventSet dataEventSet} and returns true
     * if the number of points is greater than the desired number of points and
     * false if it is equal to or less than the desired number of points.
     * 
     * @param dataEventSet
     *            -
     * @param minimumPoints
     *            -
     * @return -
     */
    public static boolean meetsDataEventSetTimeSeriesMinimum(DataEventSet dataEventSet, Integer minimumPoints)
    {
        boolean meetsTimeSeriesMinimum = false;

        if ( dataEventSet.getDataEvents() != null && dataEventSet.getDataEvents().size() > minimumPoints )
        {
            meetsTimeSeriesMinimum = true;
        }

        return meetsTimeSeriesMinimum;
    }

    /**
     * Checks the number of values contained within the passed in {@link org.mimosa.osacbmv3_3.DADataSeq DADataSeq} and returns true if the
     * number of points is greater than the desired number of points and false
     * if it is equal to or less than the desired number of points.
     * 
     * @param dataEvent
     *            -
     * @param minimumPoints
     *            -
     * @return -
     */
    public static boolean meetsDADataSeqTimeSeriesMinimum(DADataSeq dataEvent, Integer minimumPoints)
    {
        boolean meetsTimeSeriesMinimum = false;

        if ( dataEvent.getValues() != null && dataEvent.getValues().size() > minimumPoints )
        {
            meetsTimeSeriesMinimum = true;
        }

        return meetsTimeSeriesMinimum;
    }

    /**
     * Retrieves EndTime from DataEvent
     * 
     * @param dataEvent
     *            dataEventSet to extract time from
     * @return time value
     */
    public static double getEndTimeFromDataEvent(DataEvent dataEvent)
    {

        try
        {
            if ( dataEvent instanceof DMReal )
            {
                return ((DMReal) dataEvent).getTime().getTimeBinary().doubleValue();
            }
            else if ( dataEvent instanceof DMInt )
            {
                return ((DMInt) dataEvent).getTime().getTimeBinary().doubleValue();
            }
            else if ( dataEvent instanceof DAReal )
            {
                return ((DAReal) dataEvent).getTime().getTimeBinary().doubleValue();
            }
            else if ( dataEvent instanceof DAInt )
            {
                return ((DAInt) dataEvent).getTime().getTimeBinary().doubleValue();
            }
            else if ( dataEvent instanceof DMDataSeq )
            {
                DMDataSeq tDataSeq = (DMDataSeq) dataEvent;
                return getTimeAtIndex(tDataSeq.getXAxisDeltas(), tDataSeq.getXAxisStart(),
                        tDataSeq.getXAxisDeltas().size() - 1);
            }
            else if ( dataEvent instanceof DADataSeq )
            {
                DADataSeq tDataSeq = (DADataSeq) dataEvent;
                return getTimeAtIndex(tDataSeq.getXAxisDeltas(), tDataSeq.getXAxisStart(),
                        tDataSeq.getXAxisDeltas().size() - 1);
            }
            else if ( dataEvent instanceof DAWaveform )
            {
                DAWaveform tDataSeq = (DAWaveform) dataEvent;
                return tDataSeq.getXAxisStart() + (tDataSeq.getXAxisDelta() * tDataSeq.getValues().size());
            }
            else if ( dataEvent instanceof DAValueWaveform )
            {
                DAValueWaveform tDataSeq = (DAValueWaveform) dataEvent;
                return ((DoubleValue) tDataSeq.getXAxisStart()).getValue()
                        + (((DoubleValue) tDataSeq.getXAxisDelta()).getValue()
                                * ((DblArrayValue) tDataSeq.getValues()).getValues().size());
            }
            else if ( dataEvent instanceof DAValueDataSeq )
            {
                DAValueDataSeq tDataSeq = (DAValueDataSeq) dataEvent;
                return getTimeAtIndex(((DblArrayValue) tDataSeq.getXAxisDeltas()).getValues(),
                        ((DoubleValue) tDataSeq.getXAxisStart()).getValue(),
                        ((DblArrayValue) tDataSeq.getXAxisDeltas()).getValues().size() - 1);
            }
        }
        catch (Throwable throwable)
        {
            return 0.0;
        }

        return 0.0;
    }

    /**
     * Retrieves the number of data points in a DataEvent
     * 
     * @param dataEvent
     *            dataEventSet to extract time from
     * @return time value
     */
    public static int getNumberOfDataPointsInDataEvent(DataEvent dataEvent)
    {

        if ( dataEvent instanceof DMReal )
        {
            return 1;
        }
        else if ( dataEvent instanceof DMInt )
        {
            return 1;
        }
        else if ( dataEvent instanceof DAReal )
        {
            return 1;
        }
        else if ( dataEvent instanceof DAInt )
        {
            return 1;
        }
        else if ( dataEvent instanceof DMDataSeq )
        {
            DMDataSeq tDataSeq = (DMDataSeq) dataEvent;
            return tDataSeq.getValues().size();
        }
        else if ( dataEvent instanceof DADataSeq )
        {
            DADataSeq tDataSeq = (DADataSeq) dataEvent;
            return tDataSeq.getValues().size();
        }
        else if ( dataEvent instanceof DAWaveform )
        {
            DAWaveform tDataSeq = (DAWaveform) dataEvent;
            return tDataSeq.getValues().size();
        }
        else if ( dataEvent instanceof DAValueWaveform )
        {
            DAValueWaveform tDataSeq = (DAValueWaveform) dataEvent;
            return ((DblArrayValue) tDataSeq.getValues()).getValues().size();
        }
        else if ( dataEvent instanceof DAValueDataSeq )
        {
            DAValueDataSeq tDataSeq = (DAValueDataSeq) dataEvent;
            return ((DblArrayValue) tDataSeq.getValues()).getValues().size();
        }

        return 0;
    }

    /**
     * Retrieves StartTime from DataEventSet
     * 
     * @param t
     *            dataEventSet to extract time from
     * @return time value
     */
    public static double getStartTimeFromDataEventSet(DataEventSet t)
    {
        DataEvent firstEvent = t.getDataEvents().get(0);
        return getStartTimeFromDataEvent(firstEvent);
    }

    /**
     * Retrieves StartTime from DataEventSet
     * 
     * @param dataEvent
     *            dataEventSet to extract time from
     * @return time value
     */
    public static double getStartTimeFromDataEvent(DataEvent dataEvent)
    {

        try
        {
            if ( dataEvent instanceof DMReal )
            {
                return ((DMReal) dataEvent).getTime().getTimeBinary().doubleValue();
            }
            else if ( dataEvent instanceof DMInt )
            {
                return ((DMInt) dataEvent).getTime().getTimeBinary().doubleValue();
            }
            else if ( dataEvent instanceof DAReal )
            {
                return ((DAReal) dataEvent).getTime().getTimeBinary().doubleValue();
            }
            else if ( dataEvent instanceof DAInt )
            {
                return ((DAInt) dataEvent).getTime().getTimeBinary().doubleValue();
            }
            else
            {
                return getXAxisStart(dataEvent);
            }
            // FIXME - we shouldn't be swallowing exceptions!
        }
        catch (Throwable throwable)
        {
            return 0.0;
        }

    }

    /**
     * Retrieves Delta Time from DataEventSet - if the first entry in the
     * DataEventSet is aDMDataSeq or DADataSeq simply return the xAxisDeltas
     * from this DataEvent. Otherwise construct a vector correspoding to the
     * xAxisDeltas concept as follows: The first entry in the return vector is
     * the first DataEvent's time from the TimeBinary.longValue field.
     * Subsequent values in the vector are the difference between the current
     * DataEvent's TimeBinary.longValue and the prior DataEvent's
     * TimeBinary.longValue
     * 
     * @param t
     *            DataEventSet to extract Delta time from
     * @return list of delta time values
     */
    @Deprecated
    public static List<Double> getDeltaTimeFromDataEventSet(DataEventSet t)
    {

        List<Double> ret = new ArrayList<Double>();

        Iterator<DataEvent> dataEventsI = t.getDataEvents().iterator();

        boolean atFirstEntry = true;
        Double priorTime = null;

        while (dataEventsI.hasNext())
        {
            DataEvent dataEvent = dataEventsI.next();
            if ( dataEvent instanceof DMDataSeq )
            {
                return ((DMDataSeq) dataEvent).getXAxisDeltas();
            }
            else if ( dataEvent instanceof DADataSeq )
            {
                return ((DADataSeq) dataEvent).getXAxisDeltas();
            }
            else
            {
                if ( atFirstEntry )
                {
                    atFirstEntry = false;
                    ret.add(dataEvent.getTime().getTimeBinary().longValue() + 0.0);

                }
                else
                {
                    ret.add(dataEvent.getTime().getTimeBinary().longValue() - priorTime);
                }
            }
            priorTime = dataEvent.getTime().getTimeBinary().longValue() + 0.0;
        }

        return ret;

    }

    /**
     * Converts double array to List of Double
     * 
     * @param data1
     *            data value
     * @return list of Double
     */
    public static List<Double> convertDoubleArrayToDoubleList(double[] data1)
    {
        List<Double> ret = new ArrayList<Double>();

        for (double d : data1)
        {
            ret.add(d);
        }

        return ret;
    }

    /**
     * Convert a 2x2 array of Doubles to a List of ArrayList Doubles.
     * 
     * @param data1
     *            array of double values
     * @return a two dimension List of Lists
     */
    public static List<List<Double>> convertDoubleDoubleArrayToListDoubleArrayList(Double[][] data1)
    {
        List<List<Double>> ret = new ArrayList<List<Double>>();
        for (Double[] dOuter : data1)
        {
            boolean first = true;
            ArrayList<Double> vals = null;
            for (Double dInner : dOuter)
            {
                if ( first )
                {
                    first = false;
                    vals = new ArrayList<Double>();
                }
                vals.add(dInner);
            }
            ret.add(vals);
        }

        return ret;
    }

    /**
     * Retrieves DateVector from DataEventSet
     * 
     * @param t
     *            DataEventSet
     * @return an array of Date
     */
    @Deprecated
    public static Date[] getDateVectorFromDataEventSet(DataEventSet t)
    {
        int len = t.getDataEvents().size();
        Date[] ret = new Date[len];

        for (int i = 0; i < t.getDataEvents().size(); i++)
        {
            DataEvent dataEvent = t.getDataEvents().get(i);
            if ( dataEvent instanceof DMReal )
            {
                ret[i] = Utils.convertOsacbmTimeToDate(dataEvent.getTime());
            }
        }

        return ret;
    }

    /**
     * Returns ISO DataFormat 'yyyy-MM-dd'T'HH:mm:ss.SSS'
     * 
     * @return ISO DateFormat
     */
    public static DateFormat getIsoFormat()
    {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); //$NON-NLS-1$
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
        return isoFormat;
    }

    /**
     * Returns Display DateFormat 'yyyy-MM-dd HH:mm:ss'
     * 
     * @return dateFormat
     */
    public static DateFormat getDisplayDateFormat()
    {
        SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
        displayDateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
        return displayDateFormat;
    }

    /**
     * Retrieves UserTag from DataEvent
     * 
     * @param osaObj
     *            dataEvent or DataEventSet
     * @return user tag
     */
    public static String getUserTag(Object osaObj)
    {
        if ( osaObj instanceof DataEventSet )
        {
            if ( ((DataEventSet) osaObj).getSite() == null )
            {
                return null;
            }
            return ((DataEventSet) osaObj).getSite().getUserTag();
        }
        else if ( osaObj instanceof DataEvent )
        {
            if ( ((DataEvent) osaObj).getSite() == null )
            {
                return null;
            }
            return ((DataEvent) osaObj).getSite().getUserTag();
        }

        return null;
    }

    /**
     * Converts string to Date using Default date format
     * 
     * @param arg
     *            string to convert
     * @return Date
     */
    private static Date convertStringToDate(String arg)
    {

        DateFormat df = getIsoFormat();

        Date d = null;
        try
        {
            d = df.parse(arg);
        }
        catch (ParseException e)
        {
            //
        }

        return d;
    }

    /**
     * This method creates the ErrorInfo object which is purely an ordered List.
     * 
     * @param contextId context identifier
     * @param errorCode error code
     * @param severity severity code
     * @param analyticName analytic name
     * @param shortDesc short description of error
     * @param detailDesc detailed description of error
     * @return the ErrorInfo
     */
    public static ErrorInfo createErrorDataEvent(String contextId, String errorCode, String severity,
            String analyticName, String shortDesc, String detailDesc)
    {

        // set the context id = 1st param context
        Parameter contextParam = new Parameter();
        contextParam.setDescription(contextId);

        // set the errorCode = 2nd param errorCode
        Parameter errorCodeParam = new Parameter();
        errorCodeParam.setDescription(errorCode);

        // set the severity = 3rd param severity
        Parameter severityParam = new Parameter();
        severityParam.setDescription(severity);

        // set the analyticName = 4th param analyticName
        Parameter analyticNameParam = new Parameter();
        analyticNameParam.setDescription(analyticName);

        // set the shortDesc = 5th param shortDesc
        Parameter shortDescParam = new Parameter();
        shortDescParam.setDescription(shortDesc);

        // set the detailDesc = 6th param detailDesc
        Parameter detailDescriptionParam = new Parameter();
        detailDescriptionParam.setDescription(detailDesc);

        // List<Parameter> = ErrorInfo
        ErrorInfo errorInfo = new ErrorInfo();

        // populate the List<Parameter>
        errorInfo.getParameters().add(contextParam);
        errorInfo.getParameters().add(errorCodeParam);
        errorInfo.getParameters().add(severityParam);
        errorInfo.getParameters().add(analyticNameParam);
        errorInfo.getParameters().add(shortDescParam);
        errorInfo.getParameters().add(detailDescriptionParam);

        return errorInfo;
    }

    /**
     * Creates dataEventSet with required fields
     * 
     * @return DataEventSet
     */
    public static DataEventSet createDataEventSetWithRequiredFields()
    {
        DataEventSet eventSet = new DataEventSet();
        Site requiredSite = createSiteWithRequiredFields(null);
        eventSet.setSite(requiredSite);
        OsacbmTime requiredOsaTime = new OsacbmTime();
        requiredOsaTime.setTimeType(OsacbmTimeType.OSACBM_TIME_MIMOSA);
        requiredOsaTime.setTime(getIsoFormat().format(new Date(0)));
        eventSet.setTime(requiredOsaTime);

        return eventSet;

    }

    /**
     * Creates Site with Required fields
     * 
     * @param category
     *            SITECATEGORY values
     * @return Site
     */
    public static Site createSiteWithRequiredFields(SITECATEGORY category)
    {
        Site ret = new Site();

        if ( category != null )
        {
            ret.setCategory(category);
        }
        else
        {
            ret.setCategory(SITECATEGORY.SITE_SPECIFIC);
        }

        return ret;
    }

    /**
     * Converts ValueArray-types to String
     * 
     * @param val
     *            Value type
     * @param i
     *            index in the array
     * @return converted value
     */
    public static String ValueArrayEntryToString(Value val, int i)
    {
        if ( val instanceof LongArrayValue )
        {
            return ((LongArrayValue) val).getValues().get(i) + ""; //$NON-NLS-1$

        }
        else if ( val instanceof ByteArrayValue )
        {
            return ((ByteArrayValue) val).getValues().get(i) + ""; //$NON-NLS-1$

        }
        else if ( val instanceof IntArrayValue )
        {
            return ((IntArrayValue) val).getValues().get(i) + ""; //$NON-NLS-1$

        }
        else if ( val instanceof ShortArrayValue )
        {
            return ((ShortArrayValue) val).getValues().get(i) + ""; //$NON-NLS-1$

        }
        else if ( val instanceof StringArrayValue )
        {
            return ((StringArrayValue) val).getValues().get(i);

        }
        else if ( val instanceof DblArrayValue )
        {
            return ((DblArrayValue) val).getValues().get(i) + ""; //$NON-NLS-1$

        }
        else if ( val instanceof FloatArrayValue )
        {
            return ((FloatArrayValue) val).getValues().get(i) + ""; //$NON-NLS-1$

        }
        else if ( val instanceof CharArrayValue )
        {
            return ((CharArrayValue) val).getValues().get(i);

        }
        else if ( val instanceof BooleanArrayValue )
        {
            return ((BooleanArrayValue) val).getValues().get(i).toString();
        }

        return null;

    }

    /**
     * @param xAxisStart
     *            -
     * @param xAxisDeltas
     *            -
     * @return -
     */
    public static Double getLastDateFromTimeSeriesDeltas(Double xAxisStart, List<Double> xAxisDeltas)
    {
        Double ret = xAxisStart;

        for (Double delta : xAxisDeltas)
        {
            ret += delta;
        }

        return ret;
    }

    /**
     * determine if the filter is compatible with an osa data type. At
     * this point the logid only verifies that we have a SequenceFilter
     * or TimeFilter and that the datatype is one of da_waveform,
     * da_data_seq, dm_data_seq or da_value_data_seq
     * 
     * @param dataType
     *            the osa data type
     * @param filter
     *            the filter
     * @return -
     */
    public static boolean isCompatibleSelectionFilters(OsacbmDataType dataType, List<SelectionFilter> filter)
    {
        if ( filter != null && filter.size() > 0 )
        {
            if ( (dataType.equals(OsacbmDataType.DA_VALUE_DATA_SEQ) || dataType.equals(OsacbmDataType.DA_VALUE_WAVEFORM)
                    || dataType.equals(OsacbmDataType.DA_WAVEFORM) || dataType.equals(OsacbmDataType.DA_DATA_SEQ)
                    || dataType.equals(OsacbmDataType.DM_DATA_SEQ)) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * returns the list of values from the dataEvent or null if the dataEvent is
     * not an osa structure that has a list of values.
     * 
     * @param de
     *            -
     * @return -
     */
    public static List<?> getValuesList(DataEvent de)
    {
        if ( de instanceof DAWaveform )
        {
            return ((DAWaveform) de).getValues();
        }

        if ( de instanceof DAValueDataSeq )
        {
            Value values = ((DAValueDataSeq) de).getValues();
            if ( values instanceof LongArrayValue )
            {
                return ((LongArrayValue) values).getValues();
            }
            else if ( values instanceof ByteArrayValue )
            {
                return ((ByteArrayValue) values).getValues();
            }
            else if ( values instanceof IntArrayValue )
            {
                return ((IntArrayValue) values).getValues();
            }
            else if ( values instanceof ShortArrayValue )
            {
                return ((ShortArrayValue) values).getValues();
            }
            else if ( values instanceof StringArrayValue )
            {
                return ((StringArrayValue) values).getValues();
            }
            else if ( values instanceof DblArrayValue )
            {
                return ((DblArrayValue) values).getValues();
            }
            else if ( values instanceof FloatArrayValue )
            {
                return ((FloatArrayValue) values).getValues();
            }
            else if ( values instanceof CharArrayValue )
            {
                return ((CharArrayValue) values).getValues();
            }
            else if ( values instanceof BooleanArrayValue )
            {
                return ((BooleanArrayValue) values).getValues();
            }
            else
            {
                return null;
            }
        }

        if ( de instanceof DADataSeq )
        {
            return ((DADataSeq) de).getValues();
        }

        if ( de instanceof DMDataSeq )
        {
            return ((DMDataSeq) de).getValues();
        }

        if ( de instanceof DAValueWaveform )
        {
            Value values = ((DAValueWaveform) de).getValues();
            if ( values instanceof LongArrayValue )
            {
                return ((LongArrayValue) values).getValues();
            }
            else if ( values instanceof ByteArrayValue )
            {
                return ((ByteArrayValue) values).getValues();
            }
            else if ( values instanceof IntArrayValue )
            {
                return ((IntArrayValue) values).getValues();
            }
            else if ( values instanceof ShortArrayValue )
            {
                return ((ShortArrayValue) values).getValues();
            }
            else if ( values instanceof StringArrayValue )
            {
                return ((StringArrayValue) values).getValues();
            }
            else if ( values instanceof DblArrayValue )
            {
                return ((DblArrayValue) values).getValues();
            }
            else if ( values instanceof FloatArrayValue )
            {
                return ((FloatArrayValue) values).getValues();
            }
            else if ( values instanceof CharArrayValue )
            {
                return ((CharArrayValue) values).getValues();
            }
            else if ( values instanceof BooleanArrayValue )
            {
                return ((BooleanArrayValue) values).getValues();
            }
            else
            {
                return null;
            }
        }

        if ( de instanceof DAReal )
        {
            List<Double> doubles = new ArrayList<>();
            doubles.add(((DAReal) de).getValue());
            return doubles;
        }

        if ( de instanceof DMReal )
        {
            List<Double> doubles = new ArrayList<>();
            doubles.add(((DMReal) de).getValue());
            return doubles;
        }

        if ( de instanceof DAInt )
        {
            List<Integer> values = new ArrayList<>();
            values.add(((DAInt) de).getValue());
            return values;
        }

        if ( de instanceof DMInt )
        {
            List<Integer> values = new ArrayList<>();
            values.add(((DMInt) de).getValue());
            return values;
        }

        if ( de instanceof DABool )
        {
            List<Boolean> values = new ArrayList<>();
            values.add(((DABool) de).isValue());
            return values;
        }

        if ( de instanceof DMBool )
        {
            List<Boolean> values = new ArrayList<>();
            values.add(((DMBool) de).isValue());
            return values;
        }

        if ( de instanceof DMVector )
        {
            List<Double> doubles = new ArrayList<>();
            doubles.add(((DMVector) de).getValue());
            return doubles;
        }

        if ( de instanceof DAString )
        {
            List<String> stringValues = new ArrayList<>();
            stringValues.add(((DAString) de).getValue());
            return stringValues;
        }

        return null;
    }

    /*
     * TODO FIX ME: This fucnation was copied from trunk version of this class
     * to make this class work with csvReaderWriter class used by analytic
     * wrapper. We have implementation of this funcationality in platform-utils
     * we just need to consolidate these two classes.
     */

    /**
     * get the delta x time value from the data event for the data point at the
     * specified index. Note the value will be in the DataEvent's time units.
     * 
     * @param t
     *            -
     * @param index
     *            -
     * @return -
     */
    public static Double getDataPointTimeFromDataEvent(DataEvent t, int index)
    {

        if ( t instanceof DMReal || t instanceof DAReal || t instanceof DAInt || t instanceof DMInt
                || t instanceof DABool || t instanceof DMBool || t instanceof DAVector || t instanceof DMVector
                || t instanceof DAString || t instanceof DABLOBData || t instanceof DMBLOBData )
        {
            return t.getTime().getTimeBinary().doubleValue();
        }
        else if ( t instanceof DMDataSeq )
        {
            return getTimeAtIndex(((DMDataSeq) t).getXAxisDeltas(), ((DMDataSeq) t).getXAxisStart(), index);
        }
        else if ( t instanceof DADataSeq )
        {
            return getTimeAtIndex(((DADataSeq) t).getXAxisDeltas(), ((DADataSeq) t).getXAxisStart(), index);
        }
        else if ( t instanceof DAValueDataSeq )
        {
            DAValueDataSeq t1 = (DAValueDataSeq) t;
            if ( t1.getXAxisStart() instanceof DoubleValue && t1.getXAxisDeltas() instanceof DblArrayValue )
            {
                return getTimeAtIndex(((DblArrayValue) t1.getXAxisDeltas()).getValues(),
                        ((DoubleValue) t1.getXAxisStart()).getValue(), index);
            }
            return null;
        }
        else if ( t instanceof DAValueWaveform )
        {
            DAValueWaveform t1 = (DAValueWaveform) t;
            return ((DoubleValue) (t1.getXAxisStart())).getValue()
                    + ((index + 1) * ((DoubleValue) (t1.getXAxisDelta())).getValue());
        }
        else if ( t instanceof DAWaveform )
        {
            DAWaveform t1 = (DAWaveform) t;
            return t1.getXAxisStart() + ((index + 1) * t1.getXAxisDelta());
        }

        // FIXME - expand for other data types
        return null;
    }

    /**
     * find the time value for a specific point in a time series. The time
     * series times values are characterized with a start time and a vector of
     * delta values, such that each delta value represents the delta time from
     * the prior point. Compute the time for a fixed point in the time series by
     * accumulating the time deltas up to and including that point and adding
     * the sum to the start time.
     * 
     * @param timeDeltas
     *            the timeDeltas for each value in the time series. Each delta
     *            is delta time from the prior data point
     * @param timeStart
     *            the start time for the time series
     * @param targetIndex
     *            the index for the value whose time value is being requested
     * @return the starttime + the sum of time deltas up to and including the
     *         targetIndex's delta time
     */
    public static double getTimeAtIndex(List<Double> timeDeltas, double timeStart, int targetIndex)
    {
        double ret = timeStart;
        for (int i = 0; i <= targetIndex; i++)
        {
            ret += timeDeltas.get(i);
        }

        return ret;
    }

    /**
     * find the index for the first data point at or after a given time
     * 
     * @param sourceData
     *            the source data set
     * @param timeBinary
     *            the target time value
     * @return an index into the data set
     */
    public static int findDataPointIndexAtTime(DataEvent sourceData, BigInteger timeBinary)
    {
        List<Double> xDeltas = null;
        Double time = null;
        int index = 0;

        if ( sourceData instanceof DADataSeq )
        {
            xDeltas = ((DADataSeq) sourceData).getXAxisDeltas();
            time = ((DADataSeq) sourceData).getXAxisStart();

        }
        else if ( sourceData instanceof DMDataSeq )
        {
            xDeltas = ((DMDataSeq) sourceData).getXAxisDeltas();
            time = ((DMDataSeq) sourceData).getXAxisStart();

        }
        else if ( sourceData instanceof DAWaveform )
        {
            double deltaFromStart = (timeBinary.doubleValue()
                    - (((DAWaveform) sourceData).getXAxisStart() + ((DAWaveform) sourceData).getXAxisDelta()));
            if ( deltaFromStart <= 0 )
            {
                return 0;
            }
            return new Long(Math.round(deltaFromStart / ((DAWaveform) sourceData).getXAxisDelta())).intValue();

        }
        else if ( sourceData instanceof DAValueDataSeq )
        {
            xDeltas = ((DblArrayValue) ((DAValueDataSeq) sourceData).getXAxisDeltas()).getValues();
            time = ((DoubleValue) ((DAValueDataSeq) sourceData).getXAxisStart()).getValue();

        }
        else
        {
            double deltaFromStart = timeBinary.doubleValue()
                    - (((DoubleValue) ((DAValueWaveform) sourceData).getXAxisStart()).getValue()
                            + ((DoubleValue) ((DAValueWaveform) sourceData).getXAxisDelta()).getValue());
            if ( deltaFromStart <= 0 )
            {
                return 0;
            }
            return new Long(Math
                    .round(deltaFromStart / ((DoubleValue) ((DAValueWaveform) sourceData).getXAxisDelta()).getValue()))
                            .intValue();

        }

        for (Double delta : xDeltas)
        {
            if ( time + delta >= timeBinary.doubleValue() )
            {
                return index;
            }
            index++;
            time += delta;
        }

        return xDeltas.size() - 1;
    }

    /**
     * @param de
     *            -
     * @return -
     */
    public static List<Double> getxAxisDeltasList(DataEvent de)
    {
        /**
         * returns the list of values from the dataEvent or null if the
         * dataEvent is not an osa structure that has a list of values.
         * 
         * @param de
         * @return
         */
        if ( de instanceof DAWaveform )
        {
            List<Double> ret = new ArrayList<Double>();
            for (int i = 0; i < getValuesList(de).size(); i++)
            {
                ret.add(((DAWaveform) de).getXAxisDelta());
            }
            return ret;
        }
        else if ( de instanceof DAValueDataSeq )
        {
            return ((DblArrayValue) ((DAValueDataSeq) de).getXAxisDeltas()).getValues();
        }
        else if ( de instanceof DADataSeq )
        {
            return ((DADataSeq) de).getXAxisDeltas();
        }
        else if ( de instanceof DMDataSeq )
        {
            return ((DMDataSeq) de).getXAxisDeltas();
        }
        else if ( de instanceof DAValueWaveform )
        {
            List<Double> ret = new ArrayList<Double>();
            for (int i = 0; i < getValuesList(de).size(); i++)
            {
                ret.add(((DoubleValue) ((DAValueWaveform) de).getXAxisDelta()).getValue());
            }
            return ret;
        }

        return null;
    }

    /**
     * return true if the dataEvent object a DSPPM recognized time series. DSPPM
     * recognizes DADataSeq, DMDataSeq, DAValueDataSeq, DAWaveform, and
     * DAValueWaveform as time series
     * 
     * @param dataEvent
     *            -
     * @return -
     */
    public static boolean isTimeSeries(DataEvent dataEvent)
    {
        if ( dataEvent instanceof DAWaveform )
        {
            return true;
        }
        else if ( dataEvent instanceof DAValueDataSeq )
        {
            return true;
        }
        else if ( dataEvent instanceof DADataSeq )
        {
            return true;
        }
        else if ( dataEvent instanceof DMDataSeq )
        {
            return true;
        }
        else if ( dataEvent instanceof DAValueWaveform )
        {
            return true;
        }

        return false;
    }

    /**
     * @param targetTimeSeries
     *            -
     * @return the xAxisStart
     */
    public static Double getXAxisStart(DataEvent targetTimeSeries)
    {
        if ( targetTimeSeries instanceof DADataSeq )
        {
            return ((DADataSeq) targetTimeSeries).getXAxisStart();
        }
        else if ( targetTimeSeries instanceof DAWaveform )
        {
            return ((DAWaveform) targetTimeSeries).getXAxisStart();
        }
        else if ( targetTimeSeries instanceof DAValueWaveform )
        {
            return ((DoubleValue) ((DAValueWaveform) targetTimeSeries).getXAxisStart()).getValue();
        }
        else if ( targetTimeSeries instanceof DAValueDataSeq )
        {
            return ((DoubleValue) ((DAValueDataSeq) targetTimeSeries).getXAxisStart()).getValue();
        }
        else if ( targetTimeSeries instanceof DMDataSeq )
        {
            return ((DMDataSeq) targetTimeSeries).getXAxisStart();
        }
        return new Double(0);
    }

    /**
     * @param dataEvent
     *            -
     * @return -
     */
    public static List<Double> getDeltaTimeFromDataEvent(DataEvent dataEvent)
    {

        List<Double> ret = new ArrayList<Double>();

        if ( dataEvent instanceof DMDataSeq )
        {
            return ((DMDataSeq) dataEvent).getXAxisDeltas();
        }
        else if ( dataEvent instanceof DADataSeq )
        {
            return ((DADataSeq) dataEvent).getXAxisDeltas();
        }
        else if ( dataEvent instanceof DAValueDataSeq )
        {
            return ((DblArrayValue) (((DAValueDataSeq) dataEvent).getXAxisDeltas())).getValues();
        }
        else if ( dataEvent instanceof DAWaveform )
        {
            ret.add(0.0);
            for (int i = 1; i < ((DAWaveform) dataEvent).getValues().size(); i++)
            {
                ret.add(((DAWaveform) dataEvent).getXAxisDelta());
            }
            return ret;
        }
        else if ( dataEvent instanceof DAValueWaveform )
        {
            ret.add(0.0);
            for (int i = 1; i < ((DblArrayValue) ((DAValueWaveform) dataEvent).getValues()).getValues().size(); i++)
            {
                ret.add(((DoubleValue) ((((DAValueWaveform) dataEvent).getXAxisDelta()))).getValue());
            }
            return ret;
        }

        return null;

    }

    /**
     * build an array of Date structures from the date value(s) in a data event
     * 
     * @param osaValue
     *            -
     * @return -
     */
    public static Date[] extractDateArrayFromDataEvent(DataEvent osaValue)
    {

        OsacbmTime dataEventTime = osaValue.getTime();

        Date dateFromOsa = convertOsacbmTimeToDate(dataEventTime);
        Double currentTime = Double.valueOf(dateFromOsa.getTime());

        Double xAxisStart = getXAxisStart(osaValue);
        List<Double> xAxisDeltas = getxAxisDeltasList(osaValue);

        if ( xAxisDeltas == null || xAxisDeltas.size() == 0 )
        {
            Date[] ret = new Date[1];
            ret[0] = dateFromOsa;
            return ret;
        }

        Date[] ret = new Date[xAxisDeltas.size()];
        currentTime += xAxisStart;

        for (int i = 0; i < xAxisDeltas.size(); i++)
        {
            Double delta = xAxisDeltas.get(i);
            currentTime += delta;
            Date date = new Date(Long.valueOf(Math.round(currentTime)));
            ret[i] = date;
        }

        return ret;
    }

    /**
     * check if given number is within a range based on osacbmTime. If it is
     * not, it throws exception with the detailed message
     * 
     * @param num
     *            BigInteger
     * @param timeType
     *            OsacbmTimeType
     */
    private static void numberRangeCheck(BigInteger num, OsacbmTimeType timeType)
    {
        BigInteger min = null;
        BigInteger max = null;
        DecimalFormat decimalFomratter = new DecimalFormat("#"); //$NON-NLS-1$
        if ( OsacbmTimeType.OSACBM_TIME_POSIX_NSEC_8.equals(timeType)
                || OsacbmTimeType.OSACBM_TIME_POSIX_USEC_8.equals(timeType) )
        {
            min = new BigInteger(decimalFomratter.format(signedEightByteMinNumber));
            max = new BigInteger(decimalFomratter.format(signedEightByteMaxNumber));
        }
        if ( OsacbmTimeType.OSACBM_TIME_POSIX_USEC_6.equals(timeType)
                || OsacbmTimeType.OSACBM_TIME_POSIX_MSEC_6.equals(timeType) )
        {
            min = new BigInteger(decimalFomratter.format(signedSixByteMinNumber));
            max = new BigInteger(decimalFomratter.format(signedSixByteMaxNumber));
        }
        if ( OsacbmTimeType.OSACBM_TIME_POSIX_USEC_6.equals(timeType)
                || OsacbmTimeType.OSACBM_TIME_POSIX_MSEC_6.equals(timeType) )
        {
            min = new BigInteger(decimalFomratter.format(signedSixByteMinNumber));
            max = new BigInteger(decimalFomratter.format(signedSixByteMaxNumber));
        }
        if ( OsacbmTimeType.OSACBM_TIME_POSIX_SEC_4.equals(timeType) )
        {
            min = new BigInteger(decimalFomratter.format(signedFourByteMinNumber));
            max = new BigInteger(decimalFomratter.format(signedFourByteMaxNumber));
        }

        if ( min != null && max != null && (num.compareTo(min) == -1 || num.compareTo(max) == 1) )
        {
            throw new UnsupportedOperationException("Invalid time specified for type " + timeType //$NON-NLS-1$
                    + " expected range of  time [ " + min.toString() + " , " + max.toString() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    /**
     * return true if the class is one of DABool, DMBool, DAInt, DMInt, DAReal,
     * DMReal, DAString or DMVector
     * 
     * @param osaDataEventClass
     *            -
     * @return -
     */
    public static boolean isOsaSingleton(Class<?> osaDataEventClass)
    {
        if ( osaDataEventClass.equals(DABool.class) || osaDataEventClass.equals(DMBool.class)
                || osaDataEventClass.equals(DAInt.class) || osaDataEventClass.equals(DMInt.class)
                || osaDataEventClass.equals(DAReal.class) || osaDataEventClass.equals(DMReal.class)
                || osaDataEventClass.equals(DAString.class) || osaDataEventClass.equals(DMVector.class) )
        {
            return true;
        }

        return false;
    }

    /**
     * return true if the class is one of DABool, DMBool, DAInt, DMInt, DAReal,
     * DMReal, DAString or DMVector
     * 
     * @param dataType
     *            -
     * @return -
     */
    public static boolean isOsaSingleton(OsacbmDataType dataType)
    {
        if ( dataType.compareTo(OsacbmDataType.DA_BOOL) == 0 || dataType.compareTo(OsacbmDataType.DM_BOOL) == 0
                || dataType.compareTo(OsacbmDataType.DA_INT) == 0 || dataType.compareTo(OsacbmDataType.DM_INT) == 0
                || dataType.compareTo(OsacbmDataType.DA_REAL) == 0 || dataType.compareTo(OsacbmDataType.DM_REAL) == 0
                || dataType.compareTo(OsacbmDataType.DA_STRING) == 0
                || dataType.compareTo(OsacbmDataType.DM_VECTOR) == 0 )
        {
            return true;
        }

        return false;
    }

    /**
     * build a list of Date structures from the date value(s) in a data event
     * 
     * @param osaValue
     *            -
     * @return -
     */
    public static List<Date> extractDateSeriesFromDataEvent(DataEvent osaValue)
    {
        List<Date> ret = new ArrayList<Date>();

        OsacbmTime dataEventTime = osaValue.getTime();

        Date dateFromOsa = convertOsacbmTimeToDate(dataEventTime);
        Double currentTime = Double.valueOf(dateFromOsa.getTime());

        Double xAxisStart = getXAxisStart(osaValue);
        List<Double> xAxisDeltas = getxAxisDeltasList(osaValue);

        currentTime += xAxisStart;

        for (Double delta : xAxisDeltas)
        {
            currentTime += delta;
            Date date = new Date(Long.valueOf(Math.round(currentTime)));
            ret.add(date);
        }

        return ret;
    }

    /**
     * build a list of double values from the data event's time values
     * 
     * @param dataEvent
     *            -
     * @return -
     * @throws OsacbmConversionException
     *             -
     */
    public static List<Double> extractDateSeriesAsDoubleFromDataEvent(DataEvent dataEvent)
            throws OsacbmConversionException
    {
        List<Double> ret = new ArrayList<Double>();

        OsacbmTime dataEventTime = dataEvent.getTime();

        Double initTime;
        initTime = convertOsacbmTimeToDouble(dataEventTime);

        if ( initTime == null )
        {
            initTime = 0.0;
        }

        Double xAxisStart = getXAxisStart(dataEvent);
        List<Double> xAxisDeltas = getxAxisDeltasList(dataEvent);

        if ( xAxisDeltas == null || xAxisDeltas.size() == 0 )
        {
            ret.add(initTime + xAxisStart);
            return ret;
        }

        Double currentTime = initTime + xAxisStart;

        for (Double delta : xAxisDeltas)
        {
            currentTime += delta;
            ret.add(currentTime);
        }

        return ret;
    }

    /**
     * build an array of double values from the data event's time values
     * 
     * @param dataEvent -
     * @return - 
     */
    public static Double[] extractDateArrayAsDoubleFromDataEvent(DataEvent dataEvent)
    {
        OsacbmTime dataEventTime = dataEvent.getTime();

        Double initTime = null;
        initTime = convertOsacbmTimeToDouble(dataEventTime);

        if ( initTime == null )
        {
            initTime = 0.0;
        }

        Double xAxisStart = getXAxisStart(dataEvent);
        List<Double> xAxisDeltas = getxAxisDeltasList(dataEvent);

        Double[] ret = null;
        if ( xAxisDeltas == null || xAxisDeltas.size() == 0 )
        {
            ret = new Double[1];
            ret[0] = initTime + xAxisStart;
            return ret;
        }
        ret = new Double[xAxisDeltas.size()];
        Double currentTime = initTime + xAxisStart;

        for (int i = 0; i < xAxisDeltas.size(); i++)
        {
            Double delta = xAxisDeltas.get(i);
            currentTime += delta;
            ret[i] = currentTime;
        }

        return ret;
    }

    /**
     * get the quality value for a singleton data value (real, int, boolean,
     * etc., not a series data value) The quality is expected to be in the
     * alertSeverity.code field in the first NumAlert entry on the dataEvent.
     * Note the dataEvent must be a DMREal, DAREal, DMInt, DAInt, DMBool, DABool
     * or DAString
     * 
     * @param dataEvent
     *            the DataEvent object containing the value
     * @return the quality value or null if there is not quality value
     */
    public static Double getQualityAsDouble(DataEvent dataEvent)
    {
        if ( !((dataEvent instanceof DMReal) || (dataEvent instanceof DAReal) || (dataEvent instanceof DMInt)
                || (dataEvent instanceof DAInt) || (dataEvent instanceof DMBool) || (dataEvent instanceof DABool)
                || (dataEvent instanceof DAString)) )
        {
            throw new RuntimeException(dataEvent.toString() + ErrorCodes.FRAME111 + "Get quality for data value"); //$NON-NLS-1$
        }

        List<NumAlert> numAlerts = null;
        if ( dataEvent instanceof DMDataEvent )
        {
            numAlerts = ((DMDataEvent) dataEvent).getNumAlerts();
        }
        else
        {
            numAlerts = ((DADataEvent) dataEvent).getNumAlerts();
        }

        if ( numAlerts == null || numAlerts.size() == 0 )
        {
            return null;
        }

        NumAlert qualityNumAlert = numAlerts.get(0);
        if ( qualityNumAlert.getAlertSeverity() == null )
        {
            return null;
        }
        return Double.valueOf(qualityNumAlert.getAlertSeverity().getCode());
    }

    /**
     * get the quality values for a data value. The quality values are expected
     * to be in the alertSeverity.code fields in a list of NumAlert entries such
     * that each NumAlert entry corresponds to each value in the dataEvent value
     * series. Note the dataEvent must be a singleton: DMREal, DAREal, DMInt,
     * DAInt, DMBool, DABool or DAString or a series: DMDataSeq, DADataSeq,
     * DAWaveform, DAValueDataSeq, DAValueWaveform or DAString
     * 
     * @param dataEvent
     *            the DataEvent object containing the value
     * @return the quality value or null if there is no quality values
     */
    public static List<Double> getQualityAsList(DataEvent dataEvent)
    {

        List<Double> ret = new ArrayList<Double>();
        if ( isOsaSingleton(dataEvent.getClass()) )
        {

            List<NumAlert> numAlerts = null;
            if ( dataEvent instanceof DMDataEvent )
            {
                numAlerts = ((DMDataEvent) dataEvent).getNumAlerts();
            }
            else
            {
                numAlerts = ((DADataEvent) dataEvent).getNumAlerts();
            }

            if ( numAlerts == null || numAlerts.size() == 0 )
            {
                return null;
            }

            NumAlert qualityNumAlert = numAlerts.get(0);
            if ( qualityNumAlert == null || qualityNumAlert.getAlertSeverity() == null )
            {
                return null;
            }
            ret.add(Double.valueOf(qualityNumAlert.getAlertSeverity().getCode()));
            return ret;

        }

        if ( !((dataEvent instanceof DMDataSeq) || (dataEvent instanceof DADataSeq) || (dataEvent instanceof DAWaveform)
                || (dataEvent instanceof DAValueDataSeq) || (dataEvent instanceof DAValueWaveform)) )
        {
            throw new RuntimeException(
                    dataEvent.toString() + ErrorCodes.FRAME111 + "Get list of quality values for data value"); //$NON-NLS-1$
        }

        List<NumAlert> numAlerts = null;
        if ( dataEvent instanceof DMDataEvent )
        {
            numAlerts = ((DMDataEvent) dataEvent).getNumAlerts();
        }
        else
        {
            numAlerts = ((DADataEvent) dataEvent).getNumAlerts();
        }

        if ( numAlerts == null || numAlerts.size() == 0 )
        {
            return null;
        }

        for (NumAlert numAlert : numAlerts)
        {
            if ( numAlert == null || numAlert.getAlertSeverity() == null )
            {
                ret.add(null);
            }
            else
            {
                ret.add(Double.valueOf(numAlert.getAlertSeverity().getCode()));
            }
        }

        return ret;
    }

    /**
     * Add the quality to a singleton dataEvent. The data event must be one of
     * DMReal, DARea, DMInt, DAInt, DMBool, DABool or DAString or an exception
     * is thrown.
     * 
     * @param dataEvent -
     * @param qualityValue -
     */
    public static void setQuality(DataEvent dataEvent, Double qualityValue)
    {

        if ( !isOsaSingleton(dataEvent.getClass()) )
        {
            throw new RuntimeException(dataEvent.toString() + ErrorCodes.FRAME111
                    + "Set quality for data value, dataEvent is not a recognized type " //$NON-NLS-1$
                    + "(DMReal, DAReal, DMInt, DAInt, DMBool, DABool, DAString. " + "Instead it is " //$NON-NLS-1$ //$NON-NLS-2$
                    + dataEvent.getClass().getName());
        }

        NumAlert numAlert = new NumAlert();
        MIMKey3 severity = new MIMKey3();
        numAlert.setAlertSeverity(severity);
        severity.setCode(Long.valueOf(Math.round(qualityValue)));

        if ( dataEvent instanceof DADataEvent )
        {
            ((DADataEvent) dataEvent).getNumAlerts().add(numAlert);
        }
        else
        {
            ((DMDataEvent) dataEvent).getNumAlerts().add(numAlert);
        }
    }

    /**
     * Add the quality values to a dataEvent. The data event must be a singleton
     * data event or one of DMDataSeq, DADataSeq, DAWaveform, DAValueDataSeq or
     * DAValueWaveform or an exception is thrown. Note: null is allowed as a
     * quality value.
     * 
     * @param dataEvent -
     * @param qualityValues -
     */
    public static void setQualityList(DataEvent dataEvent, List<Double> qualityValues)
    {

        if ( qualityValues == null )
        {
            return;
        }

        if ( isOsaSingleton(dataEvent.getClass()) )
        {
            if ( qualityValues.size() != 1 )
            {
                throw new RuntimeException(dataEvent.toString() + ErrorCodes.FRAME111
                        + "Set the quality value was called for a singleton data element but was passed " //$NON-NLS-1$
                        + qualityValues.size() + "  values when only 1 quality value was expected."); //$NON-NLS-1$
            }
            setQuality(dataEvent, qualityValues.get(0));
            return;
        }

        if ( !((dataEvent instanceof DMDataSeq) || (dataEvent instanceof DADataSeq) || (dataEvent instanceof DAWaveform)
                || (dataEvent instanceof DAValueDataSeq) || (dataEvent instanceof DAValueWaveform)) )
        {
            throw new RuntimeException(dataEvent.toString() + ErrorCodes.FRAME111
                    + "Setting list of quality values.  The dataEvent is not a recognized type: " //$NON-NLS-1$
                    + "DADataSeq, DMDataSeq, DAWaveform, DAValueDataSeq, DAValueWaveform.  " + "It is " //$NON-NLS-1$ //$NON-NLS-2$
                    + dataEvent.getClass().getName());
        }

        List<?> values = Utils.getValuesList(dataEvent);
        if ( values == null )
        {
            throw new RuntimeException(dataEvent.toString() + ErrorCodes.FRAME111
                    + "Setting list of quality values.  There are no data values in the data element."); //$NON-NLS-1$
        }

        if ( values.size() != qualityValues.size() )
        {
            throw new RuntimeException(dataEvent.toString() + ErrorCodes.FRAME111
                    + "Setting list of quality values.  The number of data values in the data element (" + values.size() //$NON-NLS-1$
                    + ") does not equal the number of quality values (" + qualityValues.size() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if ( dataEvent instanceof DADataEvent )
        {
            for (Double qualityValue : qualityValues)
            {
                if ( qualityValue == null )
                {
                    ((DADataEvent) dataEvent).getNumAlerts().add(null);
                }
                else
                {
                    NumAlert numAlert = new NumAlert();
                    MIMKey3 severity = new MIMKey3();
                    numAlert.setAlertSeverity(severity);
                    severity.setCode(Long.valueOf(Math.round(qualityValue)));
                    ((DADataEvent) dataEvent).getNumAlerts().add(numAlert);
                }
            }
        }
        else
        {
            for (Double qualityValue : qualityValues)
            {
                if ( qualityValue == null )
                {
                    ((DMDataEvent) dataEvent).getNumAlerts().add(null);
                }
                else
                {
                    NumAlert numAlert = new NumAlert();
                    MIMKey3 severity = new MIMKey3();
                    numAlert.setAlertSeverity(severity);
                    severity.setCode(Long.valueOf(Math.round(qualityValue)));
                    ((DMDataEvent) dataEvent).getNumAlerts().add(numAlert);
                }
            }
        }
    }

    /**
     * Add the quality values to a dataEvent. The data event must be a singleton
     * data event or one of DMDataSeq, DADataSeq, DAWaveform, DAValueDataSeq or
     * DAValueWaveform or an exception is thrown. Note: null is allowed as a
     * quality value.
     * 
     * @param dataEvent -
     * @param qualityValues -
     */
    public static void setQualityList(DataEvent dataEvent, Double[] qualityValues)
    {
        setQualityList(dataEvent, Arrays.asList(qualityValues));
    }

    /**
     * returns the elements added in List a with respect to b
     * 
     * @param a - list of element of type T
     * @param b - list of element of type T
     * @param <T> - generic
     * @return list list of element of type T
     */
    public static <T> Collection<T> added(Collection<T> a, Collection<T> b)
    {

        Collection<T> aCopy = new ArrayList<T>(a);
        Collection<T> bCopy = new ArrayList<T>(b);

        aCopy.removeAll(bCopy);
        return aCopy;
    }

    /**
     * returns the elements added in List a with respect to b
     * 
     * @param a - list of element of type T
     * @param b - list of element of type T
     * @param <T> - generic
     * @return T
     */
    public static <T> Collection<T> removed(Collection<T> a, Collection<T> b)
    {

        Collection<T> aCopy = new ArrayList<T>(a);
        Collection<T> bCopy = new ArrayList<T>(b);
        Collection<T> added = added(a, b);
        aCopy.removeAll(added);
        bCopy.removeAll(aCopy);
        return bCopy;
    }

    @SuppressWarnings("unchecked")
	public static <T> T deepClone(T fromBean)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(fromBean);
            oos.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            T toBean = (T) ois.readObject();
            ois.close();
            return toBean;
        }
        catch (IOException e)
        {
            log.error("Unable to clone object", e); //$NON-NLS-1$
            return null;
        }
        catch (ClassNotFoundException e)
        {
            log.error("Unable to clone object", e); //$NON-NLS-1$
            return null;
        }
    }

    // /*****************************************************************************************************/
    // /*****************************************************************************************************/
    // /*****************************************************************************************************/
    // /*****************************************************************************************************/
    // /* deprecated stuff */
    // /*****************************************************************************************************/
    // /*****************************************************************************************************/
    // /*****************************************************************************************************/

    // /**
    // * Converts TimeSeries to DADataSeq
    // *
    // * @param id dataEvent id
    // * @param dates array of Date
    // * @param data data values
    // * @return DADataSeq
    // */
    // public static DADataSeq convertTimeSeriesToDADataSeq(long id,
    // Date[] dates, double data[]) {
    //
    // if ((dates == null) || (data == null) || (dates.length != data.length)) {
    // return null;
    // }
    //
    // DADataSeq daseq = new DADataSeq();
    // Site site = createSiteWithRequiredFields(null);
    // daseq.setSite(site);
    //
    // if (data.length > 0) {
    // daseq.setXAxisStart(new Double(dates[0].getTime()));
    // setDataAndTime(data, daseq.getValues(), dates, daseq.getXAxisDeltas(),
    // daseq.getXAxisStart());
    //
    // } else {
    // daseq.setXAxisStart(0.0);
    // }
    // return daseq;
    // }
    //
    // private static void setDataAndTime(double[] srcValues, List<Double>
    // toData, Date[] srcDates, List<Double> toDeltas, Double startTime) {
    // toData.add(srcValues[0]);
    // toDeltas.add(0.0);
    //
    // for (int i = 1; i < srcValues.length; i++) {
    // toData.add(srcValues[i]);
    // double delta = srcDates[i].getTime() - srcDates[i-1].getTime();
    // toDeltas.add(delta);
    // }
    //
    // }

    // // don't think the following is used and also don't think it is
    // consistent
    // // with other
    // // conversion routines in the ordering of the x,y values
    //
    // /**
    // * Convert the time series and values contained in a DADataSeq into a
    // * double[][] Convert time series to number series.
    // *
    // * @param in the DADataSeq to be converted
    // * @return the 2-dimensional array
    // */
    // public static OsacbmTime[] convertDateArrayToOsacbmTimeArray(Date[]
    // dates) {
    // OsacbmTime[] osacbmTimes = new OsacbmTime[dates.length];
    //
    // for (int x = 0; x < dates.length; x++) {
    // osacbmTimes[x] = convertDateToOsacbmTime(dates[x]);
    // }
    //
    // return osacbmTimes;
    // }
    //
    // /**
    // * Initializes DataEvent properties
    // *
    // * @param fromDE source DataEvent
    // * @param toDE target DataEvent
    // */
    // private static void setDataEventProperties(DataEvent fromDE, DataEvent
    // toDE) {
    // toDE.setAlertStatus(fromDE.isAlertStatus());
    // toDE.setConfid(fromDE.getConfid());
    // toDE.setId(fromDE.getId());
    // toDE.setSequenceNum(fromDE.getSequenceNum());
    // toDE.setSite(fromDE.getSite());
    // toDE.setTime(fromDE.getTime());
    // }
    //
    // private static void setDataAndTime(double[] srcValues, List<Double>
    // toData, Long[] srcDates, List<Double> toDeltas, Double startTime) {
    // toData.add(srcValues[0]);
    // toDeltas.add(0.0);
    //
    // for (int i = 1; i < srcValues.length; i++) {
    // toData.add(srcValues[i]);
    // double delta = srcDates[i] - srcDates[i-1];
    // toDeltas.add(delta);
    // }
    //
    // }
    //
    // /**
    // * Contains the actual logic for the public methods:
    // * {@link Utils#extractTimeDataAsDoubleArray(DADataSeq)} and
    // * {@link Utils#extractTimeDataAsDoubleArray(DMDataSeq)}
    // *
    // * @param xAxisStart x-axis start value
    // * @param xAxisDeltas x-axis delta list
    // * @return array of values
    // */
    // private static double[] createDoubleArrayFromXAxisDeltasAndStart(
    // Double xAxisStart, List<Double> xAxisDeltas) {
    //
    // double[] resultingDoubles = new double[xAxisDeltas.size()];
    // resultingDoubles[0] = xAxisStart + xAxisDeltas.get(0);
    //
    // for (int x = 1; x < resultingDoubles.length; x++) {
    // resultingDoubles[x] = resultingDoubles[x-1] + xAxisDeltas.get(x);
    // }
    //
    // return resultingDoubles;
    // }
    // /**
    // * Convert an OSA-CBM time series structure to a row,col ordered set of
    // Double[][].
    // * [0][0], [0][1] is the first (time, value) pair, [1][0], [1][1] is the
    // second (time, value) pair,
    // * etc.
    // * @param dataEvent
    // * @return
    // */
    // public static double[][] getRowColXYValues(DataEvent dataEvent) {
    // List<Double> values = getValuesList(dataEvent);
    //
    // double[][] ret = new double[values.size()][2];
    //
    //
    // Double startTime = getStartTimeFromDataEvent(dataEvent);
    // List<Double> timeDeltas = getDeltaTimeFromDataEvent(dataEvent);
    // double[] times = createDoubleArrayFromXAxisDeltasAndStart(startTime,
    // timeDeltas);
    //
    // for (int i = 0; i < values.size() && i < times.length; i++) {
    // ret[i][0] = times[i];
    // ret[i][1] = values.get(i);
    // }
    //
    // return ret;
    // }

    // private static SimpleDateFormat defaultDateFormat = null;
    //
    // static {
    // // ISO 8601 Standard
    // // YYYY-MM-DDThh:mm:ss.sTZD
    // // where:
    // // YYYY = four-digit year
    // // MM = two-digit month (01=January, etc.)
    // // DD = two-digit day of month (01 through 31)
    // // hh = two digits of hour (00 through 23) (am/pm NOT allowed)
    // // mm = two digits of minute (00 through 59)
    // // ss = two digits of second (00 through 59)
    // // s = one or more digits representing a decimal fraction of a second
    //
    // // Timezone not needed for OSA CBM
    // // TZD = time zone designator (Z or +hh:mm or -hh:mm)
    //
    // defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // defaultDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    // }
    //
    // /**
    // * Returns default date format
    // *
    // * @return DateFormat
    // */
    // private static DateFormat getDefaultDateFormat() {
    // return defaultDateFormat;
    // }
    //
    // /**
    // * Creates DataEventSet with errors
    // *
    // * @param string message
    // * @param e
    // * @return
    // */
    // public static DataEventSet createErrorReturn(String string,
    // OsacbmConversionException e) {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // /**
    // * Contains the actual logic for the public methods:
    // * {@link Utils#extractTimeDataAsDateArray(DADataSeq)} and
    // * {@link Utils#extractTimeDataAsDateArray(DMDataSeq)}
    // *
    // * @param xAxisStart
    // * @param xAxisDeltas
    // * @return
    // */
    // private static Date[] createDateArrayFromXAxisDeltasAndStart(
    // Double xAxisStart, List<Double> xAxisDeltas) {
    // Date[] resultingDates = new Date[xAxisDeltas.size()];
    // resultingDates[0] = new Date(Math.round(xAxisStart +
    // xAxisDeltas.get(0)));
    //
    // for (int x = 1; x < resultingDates.length; x++) {
    // resultingDates[x] = new Date(Math.round(resultingDates[x-1].getTime() +
    // xAxisDeltas.get(x)));
    // }
    //
    // return resultingDates;
    // }
    // /**
    // * Converts TimeSeries to DMDataSeq
    // *
    // * @param id dataEvent id
    // * @param dates Long representation of a Date
    // * @param data data values
    // * @return DMDataSeq
    // */
    // public static DMDataSeq convertTimeSeriesToDMDataSeq(long id,
    // Long[] dates, double data[]) {
    //
    // if ((dates == null) || (data == null) || (dates.length != data.length)) {
    // return null;
    // }
    //
    // DMDataSeq dmseq = new DMDataSeq();
    // Site site = createSiteWithRequiredFields(null);
    // dmseq.setSite(site);
    // dmseq.setId(id);
    //
    // if (data.length > 0) {
    // dmseq.setXAxisStart(new Double(dates[0]));
    // setDataAndTime(data, dmseq.getValues(), dates, dmseq.getXAxisDeltas(),
    // dmseq.getXAxisStart());
    //
    // } else {
    // dmseq.setXAxisStart(0.0);
    // }
    //
    // return dmseq;
    // }
    //
    //
    // /**
    // * convenience routine for converting the various osa Value data types to
    // an Integer
    // *
    // * @param value
    // * @return
    // */
    // private static Integer convertValueTypeToInt(Value value) {
    // if (value instanceof LongArrayValue) {
    // List<Long> vals = ((LongArrayValue) value).getValues();
    // if (vals.size() == 0) {
    // return null;
    // }
    // return (vals.get(vals.size() - 1)).intValue();
    // }
    //
    // if (value instanceof ShortArrayValue) {
    // List<Short> vals = ((ShortArrayValue) value).getValues();
    // if (vals.size() == 0) {
    // return null;
    // }
    // return (int) vals.get(vals.size() - 1);
    // }
    //
    // if (value instanceof IntArrayValue) {
    // List<Integer> vals = ((IntArrayValue) value).getValues();
    // if (vals.size() == 0) {
    // return null;
    // }
    // return (vals.get(vals.size() - 1));
    // }
    //
    // if (value instanceof DblArrayValue) {
    // List<Double> vals = ((DblArrayValue) value).getValues();
    // if (vals.size() == 0) {
    // return null;
    // }
    // return new Long(java.lang.Math.round(vals.get(vals.size() -
    // 1))).intValue();
    // }
    //
    // if (value instanceof FloatArrayValue) {
    // List<Float> vals = ((FloatArrayValue) value).getValues();
    // if (vals.size() == 0) {
    // return null;
    // }
    // return new Long(java.lang.Math.round(vals.get(vals.size() -
    // 1))).intValue();
    // }
    //
    // if (value instanceof BooleanArrayValue) {
    // List<Boolean> vals = ((BooleanArrayValue) value).getValues();
    // if (vals.size() == 0) {
    // return null;
    // }
    // if (vals.get(vals.size() - 1)) {
    // return 1;
    // } else {
    // return 0;
    // }
    // }
    //
    // if (value instanceof BooleanValue) {
    // Boolean val = ((BooleanValue) value).isValue();
    // if (val) {
    // return 1;
    // } else {
    // return 0;
    // }
    // }
    //
    // if (value instanceof IntValue) {
    // return ((IntValue) value).getValue();
    // }
    //
    // if (value instanceof DoubleValue) {
    // return new Long(java.lang.Math.round(((DoubleValue)
    // value).getValue())).intValue();
    // }
    //
    // if (value instanceof FloatValue) {
    // return new Long(java.lang.Math.round(((FloatValue)
    // value).getValue())).intValue();
    // }
    //
    // if (value instanceof LongValue) {
    // return new Long(((LongValue) value).getValue()).intValue();
    // }
    //
    // if (value instanceof ShortValue) {
    // return (int) ((ShortValue) value).getValue();
    // }
    //
    // return null;
    //
    // }
    //
    // /**
    // * convenience routine for converting the various osa Value data types to
    // an Integer
    // *
    // * @param value
    // * @return
    // */
    // private static Double convertValueTypeToDouble(Value value) {
    // if (value instanceof LongArrayValue) {
    // List<Long> vals = ((LongArrayValue) value).getValues();
    // if (vals.size() == 0) {
    // return null;
    // }
    // return (vals.get(vals.size() - 1)).doubleValue();
    // }
    //
    // if (value instanceof ShortArrayValue) {
    // List<Short> vals = ((ShortArrayValue) value).getValues();
    // if (vals.size() == 0) {
    // return null;
    // }
    // return (double) vals.get(vals.size() - 1);
    // }
    //
    // if (value instanceof IntArrayValue) {
    // List<Integer> vals = ((IntArrayValue) value).getValues();
    // if (vals.size() == 0) {
    // return null;
    // }
    // return (double) vals.get(vals.size() - 1);
    // }
    //
    // if (value instanceof DblArrayValue) {
    // List<Double> vals = ((DblArrayValue) value).getValues();
    // if (vals.size() == 0) {
    // return null;
    // }
    // return vals.get(vals.size() - 1);
    // }
    //
    // if (value instanceof FloatArrayValue) {
    // List<Float> vals = ((FloatArrayValue) value).getValues();
    // if (vals.size() == 0) {
    // return null;
    // }
    // return (double) vals.get(vals.size() - 1);
    // }
    //
    // if (value instanceof BooleanArrayValue) {
    // List<Boolean> vals = ((BooleanArrayValue) value).getValues();
    // if (vals.size() == 0) {
    // return null;
    // }
    // if (vals.get(vals.size() - 1)) {
    // return 1.0;
    // } else {
    // return 0.0;
    // }
    // }
    //
    // if (value instanceof BooleanValue) {
    // Boolean val = ((BooleanValue) value).isValue();
    // if (val) {
    // return 1.0;
    // } else {
    // return 0.0;
    // }
    // }
    //
    // if (value instanceof IntValue) {
    // return (double) ((IntValue) value).getValue();
    // }
    //
    // if (value instanceof DoubleValue) {
    // return ((DoubleValue) value).getValue();
    // }
    //
    // if (value instanceof FloatValue) {
    // return (double) ((FloatValue) value).getValue();
    // }
    //
    // if (value instanceof LongValue) {
    // return (double) ((LongValue) value).getValue();
    // }
    //
    // if (value instanceof ShortValue) {
    // return (double) ((ShortValue) value).getValue();
    // }
    //
    // return null;
    //
    // }
    // /**
    // * Converts string to StringArrayValue type
    // *
    // * @param strings array of values
    // * @return StringArrayValue
    // */
    // public static StringArrayValue convertToStringArray(String[] strings) {
    // StringArrayValue sa = null;
    // if (strings != null) {
    // sa = new StringArrayValue();
    // for (int i = 0; i < strings.length; i++) {
    // sa.getValues().add(strings[i]);
    // }
    // }
    // return sa;
    // }
    //
    //

    /**
     * Create a DMReal from a double
     * 
     * @param id
     *            dataEventId
     * @param value
     *            the double value
     * @return a DMReal
     */
    public static DMReal convertDoubleToDMReal(long id, double value)
    {
        DMReal dmr = new DMReal();
        dmr.setId(id);
        dmr.setValue(value);
        Site site = createSiteWithRequiredFields(null);
        dmr.setSite(site);

        return dmr;
    }
    //
    // /**
    // * Create a DMInt from a integer
    // *
    // * @param id dataEvent id
    // * @param value the integer value
    // * @return a DMInt
    // */
    // public static DMInt convertIntToDMInt(long id, int value) {
    // DMInt dmi = new DMInt();
    // dmi.setId(id);
    // dmi.setValue(value);
    //
    // Site site = createSiteWithRequiredFields(null);
    // dmi.setSite(site);
    //
    // return dmi;
    // }
    //

    // /**
    // * Converts DataEventSet to StringBuffer
    // *
    // * @param dataSet DataEventSet
    // * @return a StringBuffer
    // */
    // public static StringBuffer toString(DataEventSet dataSet) {
    // StringBuffer ret = null;
    //
    // if (dataSet.getDataEvents() != null) {
    // ret = new StringBuffer();
    // for (DataEvent dataEvent : dataSet.getDataEvents()) {
    // List<String> valuesList = null;
    // if (dataEvent instanceof DADataSeq) {
    // valuesList = getStrings((DADataSeq) dataEvent);
    // } else if (dataEvent instanceof DMDataSeq) {
    // valuesList = getStrings((DMDataSeq) dataEvent);
    // } else if (dataEvent instanceof DAInt) {
    // valuesList = getStrings((DAInt) dataEvent);
    // } else if (dataEvent instanceof DMInt) {
    // valuesList = getStrings((DMInt) dataEvent);
    // } else if (dataEvent instanceof DAReal) {
    // valuesList = getStrings((DAReal) dataEvent);
    // } else if (dataEvent instanceof DMReal) {
    // valuesList = getStrings((DMReal) dataEvent);
    // } else if (dataEvent instanceof DAVector) {
    // valuesList = getStrings((DAVector) dataEvent);
    // } else if (dataEvent instanceof DMVector) {
    // valuesList = getStrings((DMVector) dataEvent);
    // } else if (dataEvent instanceof SDEnumSet) {
    // valuesList = getStrings((SDEnumSet) dataEvent);
    // } else if (dataEvent instanceof DMBool) {
    // valuesList = getStrings((DMBool) dataEvent);
    // }
    //
    // ret.append(dataEvent.getSite().getUserTag());
    // ret.append(",");
    // ret.append(stringsToStringBuffer(valuesList, ","));
    // ret.append(System.getProperty("line.separator"));
    // }
    // }
    //
    // return ret;
    // }
    // /**
    // * Converts an Osa Data Type to XY Vectors
    // *
    // * @param dataIn dataEvent/DataEventSet
    // * @return two dimensional values
    // * @throws OsacbmConversionException conversion erro
    // */
    // public static double[][] convertOsaDatatypeToXYVectors(Object dataIn)
    // throws OsacbmConversionException {
    //
    // double[] x;
    // double[] y;
    //
    // if (dataIn instanceof DataEventSet) {
    // List<DataEvent> dataEvents = ((DataEventSet) dataIn)
    // .getDataEvents();
    // if (dataEvents != null && dataEvents.size() == 1) {
    // DataEvent dataEvent = dataEvents.get(0);
    // if (dataEvent instanceof DADataSeq) {
    // y = Utils.convertDADataSeqToDouble((DADataSeq) dataEvent);
    // x = Utils
    // .extractTimeDataAsDoubleArray((DADataSeq) dataEvent);
    // } else if (dataEvent instanceof DMDataSeq) {
    // y = Utils.convertDMDataSeqToDouble((DMDataSeq) dataEvent);
    // x = Utils
    // .extractTimeDataAsDoubleArray((DMDataSeq) dataEvent);
    // } else {
    // throw new OsacbmConversionException(
    // "Invalid type for input: ["
    // + dataEvent.getSite().getUserTag()
    // +
    // "], expected the single data event in the DataEventSet to be a DMDataSeq, DADataSeq.");
    // }
    // } else {
    // throw new OsacbmConversionException(
    // "Invalid type for input: ["
    // + ((DataEventSet) dataIn).getSite()
    // .getUserTag()
    // + "], expected DataEventSet with a single DMDataSeq, DADataSeq.");
    // }
    // } else if (dataIn instanceof DMDataSeq) {
    // y = Utils.convertDMDataSeqToDouble((DMDataSeq) dataIn);
    // x = Utils.extractTimeDataAsDoubleArray((DMDataSeq) dataIn);
    // } else if (dataIn instanceof DADataSeq) {
    // y = Utils.convertDADataSeqToDouble((DADataSeq) dataIn);
    // x = Utils.extractTimeDataAsDoubleArray((DADataSeq) dataIn);
    // } else if (dataIn == null) {
    // throw new OsacbmConversionException(
    // "Trying to convert input to double[][] but input=null.");
    // } else {
    // throw new OsacbmConversionException(
    // "Trying to convert input to double[][], but input is an incompatible type:"
    // + dataIn.getClass().getName());
    // }
    //
    // double ret[][] = new double[2][];
    // ret[0] = x;
    // ret[1] = y;
    // return ret;
    // }
    //
    // /**
    // * Converts Osa data type to X-Vector
    // *
    // * @param dataIn dataEvent/dataEventSet
    // * @return array of values
    // * @throws OsacbmConversionException conversion error
    // */
    // public static double[] convertOsaDatatypeToXVector(Object dataIn)
    // throws OsacbmConversionException {
    //
    // double[] y;
    //
    // if (dataIn instanceof DataEventSet) {
    // List<DataEvent> dataEvents = ((DataEventSet) dataIn)
    // .getDataEvents();
    // if (dataEvents != null && dataEvents.size() == 1) {
    // DataEvent dataEvent = dataEvents.get(0);
    // if (dataEvent instanceof DADataSeq) {
    // y = Utils.convertDADataSeqToDouble((DADataSeq) dataEvent);
    // } else if (dataEvent instanceof DMDataSeq) {
    // y = Utils.convertDMDataSeqToDouble((DMDataSeq) dataEvent);
    // } else if (dataEvent instanceof DAWaveform) {
    // y = Utils.convertDAWaveformToDouble((DAWaveform) dataEvent);
    // } else if (dataEvent instanceof DAValueDataSeq) {
    // y = Utils
    // .convertDAValueDataSeqToDouble((DAValueDataSeq) dataEvent);
    // } else {
    // throw new OsacbmConversionException(
    // "Invalid type for input: ["
    // + dataEvent.getSite().getUserTag()
    // +
    // "], expected the single data event in the DataEventSet to be a DMDataSeq, DADataSeq.");
    // }
    // } else {
    // throw new OsacbmConversionException(
    // "Invalid type for input: ["
    // + ((DataEventSet) dataIn).getSite()
    // .getUserTag()
    // + "], expected DataEventSet with a single DMDataSeq, DADataSeq.");
    // }
    // } else if (dataIn instanceof DMDataSeq) {
    // y = Utils.convertDMDataSeqToDouble((DMDataSeq) dataIn);
    // } else if (dataIn instanceof DADataSeq) {
    // y = Utils.convertDADataSeqToDouble((DADataSeq) dataIn);
    // } else if (dataIn instanceof DAWaveform) {
    // y = Utils.convertDAWaveformToDouble((DAWaveform) dataIn);
    // } else if (dataIn instanceof DAValueDataSeq) {
    // y = Utils.convertDAValueDataSeqToDouble((DAValueDataSeq) dataIn);
    // } else if (dataIn == null) {
    // throw new OsacbmConversionException(
    // "Trying to convert input to double[][ but input=null.");
    // } else {
    // throw new OsacbmConversionException(
    // "Trying to convert input to double[], but input is an incompatible type:"
    // + dataIn.getClass().getName());
    // }
    //
    // return y;
    // }
    //
    //
    // /**
    // * Convert DMDataSeq to StringBuffer
    // *
    // * @param in DMDataSeq
    // * @param delim delimiter
    // * @return
    // */
    // @Deprecated
    // public static StringBuffer convertDMDataSeqToStringBuffer(DMDataSeq in,
    // String delim) {
    // StringBuffer ret = new StringBuffer("");
    //
    // if (in != null) {
    // List<Double> vals = in.getValues();
    // if (vals != null) {
    // for (int i = 0; i < vals.size(); i++) {
    // if (i == 0) {
    // ret.append(vals.get(i).doubleValue());
    // } else {
    // ret.append(delim).append(vals.get(i).doubleValue());
    // }
    // }
    // }
    // }
    //
    // ret.append(NL);
    // return ret;
    // }
    //
    // /**
    // * Converts DMDataSeq to String list
    // *
    // * @param in DMDataSeq
    // * @return list of String
    // */
    // @Deprecated
    // public static List<String> convertDMDataSeqToStrings(DMDataSeq in) {
    // List<String> ret = new ArrayList<String>();
    //
    // if (in != null) {
    // List<Double> vals = in.getValues();
    // if (vals != null) {
    // for (int i = 0; i < vals.size(); i++) {
    // ret.add(vals.get(i).doubleValue() + "");
    // }
    // }
    // }
    // return ret;
    // }
    //
    // /**
    // * Converts DADataSeq to StringBuffer
    // *
    // * @param in DADataSeq
    // * @param delim delimiter
    // * @return a StringBuffer instance
    // */
    // @Deprecated
    // public static StringBuffer convertDADataSeqToStringBuffer(DADataSeq in,
    // String delim) {
    // StringBuffer ret = new StringBuffer("");
    //
    // if (in != null) {
    // List<Double> vals = in.getValues();
    // if (vals != null) {
    // for (int i = 0; i < vals.size(); i++) {
    // if (i == 0) {
    // ret.append(vals.get(i).doubleValue());
    // } else {
    // ret.append(delim).append(vals.get(i).doubleValue());
    // }
    // }
    // }
    // }
    //
    // ret.append(NL);
    // return ret;
    // }
    //
    // /**
    // * Converts DADataSeq to a list of String
    // *
    // * @param in DADataSeq
    // * @return list of String
    // */
    // @Deprecated
    // public static List<String> convertDADataSeqToStrings(DADataSeq in) {
    // List<String> ret = new ArrayList<String>();
    //
    // if (in != null) {
    // List<Double> vals = in.getValues();
    // if (vals != null) {
    // for (Double val : vals) {
    // ret.add(val + "");
    // }
    // }
    // }
    // return ret;
    // }
    //
    // /**
    // * Build a string buffer containing a formatted disply of the values from
    // a
    // * OsacbmTime item. The display will contain: local time, time,
    // timeBinary,
    // * timeType
    // *
    // * @param osaTime osaTime
    // * @return a StingBuffer
    // */
    // @Deprecated
    // public static StringBuffer osacbmTimeToStringBuffer(OsacbmTime osaTime) {
    // StringBuffer ret = new StringBuffer("");
    //
    // if (osaTime.getTime() != null) {
    // ret.append(osaTime.getTime());
    // } else if (osaTime.getTimeBinary() != null) {
    // ret.append(osaTime.getTimeBinary().longValue());
    // } else if (osaTime.getTimeType() != null) {
    // ret.append(osaTime.getTimeType().toString());
    // } else if (osaTime.getLocalTime() != null) {
    // ret.append(osaTime.getLocalTime().toString());
    // }
    //
    // ret.append(NL);
    // return ret;
    // }
    //
    // /**
    // * Build a string buffer containing a formatted disply of the values from
    // a
    // * OsacbmTime vector. The display will contain: local time, time,
    // * timeBinary, timeType on each line
    // *
    // * @param osaTime OsacbmTime
    // * @param delim delimiter
    // * @return a StringBuffer
    // */
    // @Deprecated
    // public static StringBuffer osacbmTimeVectorToStringBuffer(
    // OsacbmTime[] osaTime, String delim) {
    // StringBuffer ret = new StringBuffer("");
    //
    // for (int i = 0; i < osaTime.length; i++) {
    //
    // if (osaTime[i].getTime() != null) {
    // ret.append(osaTime[i].getTime());
    // } else if (osaTime[i].getTimeBinary() != null) {
    // ret.append( osaTime[i].getTimeBinary().longValue());
    // } else if (osaTime[i].getTimeType() != null) {
    // ret.append(osaTime[i].getTimeType().toString());
    // } else if (osaTime[i].getLocalTime() != null) {
    // ret.append(osaTime[i].getLocalTime().toString());
    // }
    // if (i < osaTime.length) {
    // ret.append(delim);
    // }
    // }
    // ret.append(NL);
    //
    // return ret;
    // }
    //
    // /**
    // * Format a double as a List<String>
    // *
    // * @param d the double
    // * @return the list of strings
    // */
    // @Deprecated
    // public static List<String> getStrings(double d) {
    // List<String> l = new ArrayList<String>();
    // l.add(Double.toString(d));
    // return l;
    // }
    //
    // /**
    // * Format a DMReal as a List<String>
    // *
    // * @param dmr the double
    // * @return the list of strings
    // */
    // @Deprecated
    // public static List<String> getStrings(DMReal dmr) {
    // List<String> l = new ArrayList<String>();
    // l.add(Double.toString(dmr.getValue()));
    // return l;
    // }
    //
    // /**
    // * Format a DMReal as a List<String>
    // *
    // * @param d the double
    // * @return the list of strings
    // */
    // @Deprecated
    // public static List<String> getStrings(DMBool dmr) {
    // List<String> l = new ArrayList<String>();
    // l.add(Boolean.toString(dmr.isValue()));
    // return l;
    // }
    //
    // /**
    // * Format a DAReal as a List<String>
    // *
    // * @param d the double
    // * @return the list of strings
    // */
    // @Deprecated
    // public static List<String> getStrings(DAReal dar) {
    // List<String> l = new ArrayList<String>();
    // l.add(Double.toString(dar.getValue()));
    // return l;
    // }
    //
    // /**
    // * Format a DAReal as a List<String>
    // *
    // * @param d the double
    // * @return the list of strings
    // */
    // @Deprecated
    // public static List<String> getStrings(DAInt dar) {
    // List<String> l = new ArrayList<String>();
    // l.add(Double.toString(dar.getValue()));
    // return l;
    // }
    //
    // /**
    // * Format a DAReal as a List<String>
    // *
    // * @param d the double
    // * @return the list of strings
    // */
    // @Deprecated
    // public static List<String> getStrings(DMInt dar) {
    // List<String> l = new ArrayList<String>();
    // l.add(Double.toString(dar.getValue()));
    // return l;
    // }
    //
    // /**
    // * Format a DAReal as a List<String>
    // *
    // * @param dar the double
    // * @return the list of strings
    // */
    // @Deprecated
    // public static List<String> getStrings(DAVector dar) {
    // List<String> l = new ArrayList<String>();
    // l.add(Double.toString(dar.getValue()));
    // l.add(Double.toString(dar.getXValue()));
    // return l;
    // }
    //
    // /**
    // * Format a DAReal as a List<String>
    // *
    // * @param dar the double
    // * @return the list of strings
    // */
    // @Deprecated
    // public static List<String> getStrings(DMVector dar) {
    // List<String> l = new ArrayList<String>();
    // l.add(Double.toString(dar.getValue()));
    // l.add(Double.toString(dar.getXValue()));
    // return l;
    // }
    //
    // /**
    // * Format a double array as a List<String>
    // *
    // * @param d the double array
    // * @return the list of strings
    // */
    // @Deprecated
    // public static List<String> getStrings(double d[]) {
    // List<String> l = null;
    // if (d != null) {
    // l = new ArrayList<String>();
    // for (double aD : d) {
    // l.add(Double.toString(aD));
    // }
    // }
    //
    // return l;
    // }
    //
    // /**
    // * Format a int array as a List<String>
    // *
    // * @param d the int array
    // * @return the list of strings
    // */
    // @Deprecated
    // public static List<String> getStrings(int d[]) {
    // List<String> l = null;
    // if (d != null) {
    // l = new ArrayList<String>();
    // for (int aD : d) {
    // l.add(Integer.toString(aD));
    // }
    // }
    //
    // return l;
    // }
    //
    // /**
    // * Format a Date array as a List<String>
    // *
    // * @param d the Date array
    // * @return the list of strings
    // */
    // @Deprecated
    // public static List<String> getStrings(Date d[]) {
    // List<String> l = null;
    // if (d != null) {
    // l = new ArrayList<String>();
    // for (Date aD : d) {
    // l.add(Utils.getDisplayDateFormat().format(aD));
    // }
    // }
    // return l;
    // }
    //
    // /**
    // * Format a DADataSeq as a List<String>
    // *
    // * @param d the DADataSeq
    // * @return the list of strings
    // */
    // @Deprecated
    // public static List<String> getStrings(DADataSeq d) {
    // List<String> l = new ArrayList<String>();
    // for (int i = 0; i < d.getValues().size(); i++) {
    // l.add(d.getValues().get(i).toString());
    // }
    // return l;
    // }
    //
    // /**
    // * Format a DMDataSeq as a List<String>
    // *
    // * @param d the DMDataSeq
    // * @return the list of strings
    // */
    // @Deprecated
    // public static List<String> getStrings(DMDataSeq d) {
    // List<String> l = new ArrayList<String>();
    // for (int i = 0; i < d.getValues().size(); i++) {
    // l.add(d.getValues().get(i).toString());
    // }
    // return l;
    // }
    //
    // /**
    // * Format a OsacbmTime[] array as a List<String>
    // *
    // * @param d the OsacbmTime[] array
    // * @return the list of strings
    // */
    // @Deprecated
    // public static List<String> getStrings(OsacbmTime[] d) {
    // List<String> l = null;
    // if (d != null) {
    // l = new ArrayList<String>();
    // for (OsacbmTime aD : d) {
    // l.add(aD.getTime());
    // }
    // }
    //
    // return l;
    // }
    //
    // /**
    // * Returns a list of SDNumSet values as string
    // *
    // * @param enumSet SDEnumSet
    // * @return a list of string
    // */
    // @Deprecated
    // public static List<String> getStrings(SDEnumSet enumSet) {
    //
    // List<String> ret = new ArrayList<String>();
    //
    // List<SDEnumSetDataItem> enums = enumSet.getValues();
    // for (SDEnumSetDataItem enumItem : enums) {
    // EnumValue ev = enumItem.getValue();
    // ret.add(ev.getValue() + "");
    // ret.add(ev.getName());
    // ret.add(NL);
    // }
    //
    // return ret;
    // }
    //
    // /**
    // * convert a list of strings to a comma delimeted string buffer
    // *
    // * @param in - the list of strings
    // * @return a comma delimeted string buffer of in's contents
    // */
    // @Deprecated
    // public static StringBuffer stringsToStringBuffer(List<String> in,
    // String delim) {
    // StringBuffer ret = new StringBuffer();
    //
    // Iterator<String> iter = in.iterator();
    //
    // boolean first = true;
    // while (iter.hasNext()) {
    // if (first) {
    // first = false;
    // } else {
    // ret.append(delim);
    // }
    // ret.append(iter.next());
    // }
    //
    // ret.append(NL);
    // return ret;
    // }
    //
    // /**
    // * create a comma delimited string of the time steps in the sequence
    // *
    // * @param in - the sequence structure
    // * @return a comma delimited string of the time steps in the sequence
    // */
    // @Deprecated
    // public static StringBuffer getHeader(DADataSeq in) {
    //
    // StringBuffer ret = new StringBuffer();
    //
    // if (in.getXAxisDeltas() == null || in.getXAxisDeltas().size() == 0) {
    // return ret;
    // }
    //
    // double time = in.getXAxisStart();
    //
    // for (int i = 0; i < in.getXAxisDeltas().size(); i++) {
    // time += in.getXAxisDeltas().get(i);
    // Date d = new Date((long) time);
    // ret.append(getDisplayDateFormat().format(d));
    // if (i < in.getXAxisDeltas().size()) {
    // ret.append(",");
    // }
    // }
    //
    // return ret;
    //
    // }
    //
    // /**
    // * create a comma delimited string of the time steps in the sequence
    // *
    // * @param in - the sequence structure
    // * @return a comma delimited string of the time steps in the sequence
    // */
    // @Deprecated
    // public static StringBuffer getHeader(DMDataSeq in) {
    //
    // StringBuffer ret = new StringBuffer();
    //
    // if (in.getXAxisDeltas() == null || in.getXAxisDeltas().size() == 0) {
    // return ret;
    // }
    //
    // Double time = in.getXAxisStart();
    //
    // if (time == null) {
    // return ret;
    // }
    //
    // for (int i = 0; i < in.getXAxisDeltas().size(); i++) {
    // time += in.getXAxisDeltas().get(i);
    //
    // Date d = new Date(time.longValue());
    // ret.append(getDisplayDateFormat().format(d));
    // if (i < in.getXAxisDeltas().size()) {
    // ret.append(",");
    // }
    // }
    //
    // return ret;
    //
    // }
    //
    // /**
    // * create a comma delimited string of the values in the sequence
    // *
    // * @param in - the sequence structure
    // * @return a comma delimited string of the values in the sequence
    // */
    // @Deprecated
    // public static StringBuffer getValues(DMDataSeq in) {
    //
    // StringBuffer ret = new StringBuffer();
    //
    // for (int i = 0; i < in.getValues().size(); i++) {
    // ret.append(in.getValues().get(i));
    // if (i < in.getValues().size()) {
    // ret.append(",");
    // }
    // }
    //
    // return ret;
    //
    // }
    //
    // /**
    // * Convert the values contained in a DMDataSeq into a DMVector
    // *
    // * @param from the DMDataSeq to be converted
    // * @return DMVector
    // */
    // @Deprecated
    // public static DMVector convertDMDataSeqToDMVector(DMDataSeq from) throws
    // OsacbmConversionException {
    // if (from == null) {
    // return null;
    // }
    //
    // DMVector to = new DMVector();
    // setDataEventProperties(from, to);
    //
    // // setting the values
    // List<Double> vals = from.getValues();
    // if (vals == null) {
    // return to;
    // }
    // if (vals.size() > 0 && from.getXAxisDeltas() != null &&
    // from.getXAxisDeltas().size() == vals.size()) {
    // to.setValue(vals.get(vals.size() - 1).doubleValue());
    // to.setXValue(getLastDateFromTimeSeriesDeltas(from.getXAxisStart(),
    // from.getXAxisDeltas()));
    // return to;
    // }
    //
    //
    // throw new
    // OsacbmConversionException("Error converting DMDataSeq to DMVector: " +
    // from.toString());
    //
    // }
    //
    // /**
    // * convert DADataSeq to DMInt
    // *
    // * @param from the DADataSeq value
    // * @return the DMInt value extracted from the last value in the DADataSeq
    // */
    // @Deprecated
    // public static DMInt convertDADataSeqToDMInt(DADataSeq from) throws
    // OsacbmConversionException {
    //
    // if (from == null) {
    // return null;
    // }
    //
    // DMInt to = new DMInt();
    // setDataEventProperties(from, to);
    //
    // // setting the values
    // List<Double> vals = from.getValues();
    // if (vals == null) {
    // return to;
    // }
    // if (vals.size() > 0 && from.getXAxisDeltas() != null &&
    // from.getXAxisDeltas().size() == vals.size()) {
    // to.setValue(new Long(java.lang.Math.round(vals.get(vals.size() -
    // 1).doubleValue())).intValue());
    // return to;
    // }
    //
    //
    // throw new
    // OsacbmConversionException("Error converting DADataSeq to DMInt: " +
    // from.toString());
    //
    // }
    //
    // /**
    // * convert DADataSeq to a DMReal value
    // *
    // * @param from
    // * @return a DMReal structure with the last value from the DADataSeq
    // values
    // * @throws OsacbmConversionException
    // */
    // @Deprecated
    // public static DMReal convertDADataSeqToDMReal(DADataSeq from) throws
    // OsacbmConversionException {
    //
    // if (from == null) {
    // return null;
    // }
    //
    // DMReal to = new DMReal();
    // setDataEventProperties(from, to);
    //
    // // setting the values
    // List<Double> vals = from.getValues();
    // if (vals == null) {
    // return to;
    // }
    // if (vals.size() > 0 && from.getXAxisDeltas() != null &&
    // from.getXAxisDeltas().size() == vals.size()) {
    // to.setValue(vals.get(vals.size() - 1).doubleValue());
    // return to;
    // }
    //
    //
    // throw new
    // OsacbmConversionException("Error converting DADataSeq to DMReal: " +
    // from.toString());
    //
    // }
    //
    // /**
    // * convert a DADataSeq entry to a DMVector
    // *
    // * @param from
    // * @return the DMVector containing the last value and its corresponding
    // time value in the xValue
    // * @throws OsacbmConversionException
    // */
    // @Deprecated
    // public static DMVector convertDADataSeqToDMVector(DADataSeq from) throws
    // OsacbmConversionException {
    //
    // if (from == null) {
    // return null;
    // }
    //
    // DMVector to = new DMVector();
    // setDataEventProperties(from, to);
    //
    // // setting the values
    // List<Double> vals = from.getValues();
    // if (vals == null) {
    // return to;
    // }
    // if (vals.size() > 0 && from.getXAxisDeltas() != null &&
    // from.getXAxisDeltas().size() == vals.size()) {
    // to.setValue(vals.get(vals.size() - 1).doubleValue());
    // to.setXValue(getLastDateFromTimeSeriesDeltas(from.getXAxisStart(),
    // from.getXAxisDeltas()));
    // return to;
    // }
    //
    //
    // throw new
    // OsacbmConversionException("Error converting DADataSeq to DMReal: " +
    // from.toString());
    //
    // }
    //
    // /**
    // * convert a DAValueDataSeq to a DMInt - for array Value entries, use the
    // last data value in the array.
    // *
    // * @param from
    // * @return a DMInt
    // * @throws OsacbmConversionException
    // */
    // @Deprecated
    // public static DMInt convertDAValueDataSeqToDMnt(DAValueDataSeq from)
    // throws OsacbmConversionException {
    //
    // if (from == null) {
    // return null;
    // }
    //
    // DMInt to = new DMInt();
    // setDataEventProperties(from, to);
    //
    // // setting the values
    // Value val = from.getValues();
    // if (val == null) {
    // return to;
    // }
    // to.setValue(convertValueTypeToInt(val).intValue());
    //
    // return to;
    //
    // }
    //
    // /**
    // * convert a DAValueDataSeq to a DMReal - for array Value entries, use the
    // last data value in the array.
    // *
    // * @param from
    // * @return a DMReal
    // * @throws OsacbmConversionException
    // */
    // @Deprecated
    // public static DMReal convertDAValueDataSeqToDMReal(DAValueDataSeq from)
    // throws OsacbmConversionException {
    //
    // if (from == null) {
    // return null;
    // }
    //
    // DMReal to = new DMReal();
    // setDataEventProperties(from, to);
    //
    // // setting the values
    // Value val = from.getValues();
    // if (val == null) {
    // return to;
    // }
    // to.setValue(convertValueTypeToDouble(val));
    //
    // return to;
    //
    // }
    //
    // /**
    // * convert a DAValueWaveform to a DMInt - for array Value entries, use the
    // last data value in the array.
    // *
    // * @param from
    // * @return a DMInt
    // * @throws OsacbmConversionException
    // */
    // @Deprecated
    // public static DMInt convertDAValueWaveformToDMnt(DAValueWaveform from)
    // throws OsacbmConversionException {
    //
    // if (from == null) {
    // return null;
    // }
    //
    // DMInt to = new DMInt();
    // setDataEventProperties(from, to);
    //
    // // setting the values
    // Value val = from.getValues();
    // if (val == null) {
    // return to;
    // }
    // to.setValue(convertValueTypeToInt(val).intValue());
    //
    // return to;
    //
    // }
    //
    // /**
    // * convert a DAValueWaveform to a DMReal - for array Value entries, use
    // the last data value in the array.
    // *
    // * @param from
    // * @return a DMReal
    // * @throws OsacbmConversionException
    // */
    // @Deprecated
    // public static DMReal convertDAValueWaveformToDMReal(DAValueWaveform from)
    // throws OsacbmConversionException {
    //
    // if (from == null) {
    // return null;
    // }
    //
    // DMReal to = new DMReal();
    // setDataEventProperties(from, to);
    //
    // // setting the values
    // Value val = from.getValues();
    // if (val == null) {
    // return to;
    // }
    // to.setValue(convertValueTypeToDouble(val));
    //
    // return to;
    //
    // }
    //
    //
    // /**
    // * Convert a DMVector to an OSA-CBM DMDataSeq
    // *
    // * @param from a DMVector
    // * @return a DMDataSeq
    // */
    // @Deprecated
    // public static DMDataSeq convertDMVectorToDMDataSeq(DMVector from) {
    //
    // if (from == null) {
    // return null;
    // }
    //
    // DMDataSeq to = new DMDataSeq();
    // setDataEventProperties(from, to);
    //
    // // setting X and Y values
    // to.getValues().add(from.getValue());
    //
    // // seting XAxisStart and XAxisDeltas
    // to.setXAxisStart(from.getXValue());
    // to.getXAxisDeltas().add(0.0);
    //
    // return to;
    // }
    //
    // /**
    // * Convert a DMVector to a DMInt
    // *
    // * @param from the DMVector
    // * @return DMInt
    // */
    // @Deprecated
    // public static DMInt convertDMVectorToDMInt(DMVector from) {
    // if (from == null) {
    // return null;
    // }
    // DMInt to = new DMInt();
    // setDataEventProperties(from, to);
    // // setting the value
    // to.setValue(new Long(java.lang.Math.round((int)
    // from.getValue())).intValue());
    //
    // return to;
    // }
    //
    // /**
    // * Convert a DMVector to a DMReal
    // *
    // * @param from the DMVector
    // * @return DMReal
    // */
    // @Deprecated
    // public static DMReal convertDMVectorToDMReal(DMVector from) {
    // if (from == null) {
    // return null;
    // }
    // DMReal to = new DMReal();
    // setDataEventProperties(from, to);
    // // setting the value
    // to.setValue(from.getValue());
    //
    // return to;
    // }
    //
    // /**
    // * Convert a DMInt to a DMDataSeq
    // *
    // * @param from the DMInt
    // * @return a DMDataSeq
    // */
    // @Deprecated
    // public static DMDataSeq convertDMIntToDMDataSeq(DMInt from) {
    // if (from == null) {
    // return null;
    // }
    //
    // DMDataSeq to = new DMDataSeq();
    // setDataEventProperties(from, to);
    //
    // if (from != null) {
    // // setting the value
    // to.getValues().add((double) from.getValue());
    //
    // to.setXAxisStart((double) from.getValue());
    // to.getXAxisDeltas().add(0.0);
    // }
    //
    // return to;
    // }
    //
    //
    // /*
    // * Convert a DMInt to a DMReal
    // *
    // * @param from
    // * the DMInt
    // * @return a DMReal
    // */
    // @Deprecated
    // public static DMReal convertDMIntToDMReal(DMInt from) {
    // if (from == null) {
    // return null;
    // }
    //
    // DMReal to = new DMReal();
    // setDataEventProperties(from, to);
    // to.setValue(from.getValue());
    //
    // return to;
    // }
    //
    // /**
    // * Convert a DMInt to a DMVector
    // *
    // * @param from the DMInt
    // * @return a DMVector
    // */
    // @Deprecated
    // public static DMVector convertDMIntToDMVector(DMInt from) {
    // if (from == null) {
    // return null;
    // }
    // DMVector to = new DMVector();
    // setDataEventProperties(from, to);
    // // setting the value
    // to.setValue(from.getValue());
    // to.setXValue(0.0);
    //
    // return to;
    // }
    //
    // /**
    // * Convert a DMBool to a DMInt
    // *
    // * @param from the DMBool
    // * @return a DmInt
    // */
    // @Deprecated
    // public static DMInt convertDMBoolToDMInt(DMBool from) {
    // if (from == null) {
    // return null;
    // }
    //
    // DMInt to = new DMInt();
    // setDataEventProperties(from, to);
    // // setting the value
    // if (from.isValue()) {
    // to.setValue(1);
    // } else {
    // to.setValue(0);
    // }
    //
    // return to;
    // }
    //
    // /**
    // * Convert a DMBool to a DMInt
    // *
    // * @param from the DMBool
    // * @return a DmInt
    // */
    // @Deprecated
    // public static DMReal convertDMBoolToDMReal(DMBool from) {
    // if (from == null) {
    // return null;
    // }
    //
    // DMReal to = new DMReal();
    // setDataEventProperties(from, to);
    // // setting the value
    // if (from.isValue()) {
    // to.setValue(1.0);
    // } else {
    // to.setValue(0.0);
    // }
    //
    // return to;
    // }
    //
    // /**
    // * Convert a DMBool to a DMDataSeq
    // *
    // * @param from the DMBool
    // * @return a DMDataSeq
    // */
    // @Deprecated
    // public static DMDataSeq convertDMBoolToDMDataSeq(DMBool from) {
    // if (from == null) {
    // return null;
    // }
    //
    // DMDataSeq to = new DMDataSeq();
    // setDataEventProperties(from, to);
    //
    // // setting the value
    // if (from.isValue()) {
    // to.getValues().add(1.0);
    // } else {
    // to.getValues().add(0.0);
    // }
    //
    // // seting XAxisStart and XAxisDeltas
    // to.setXAxisStart(from.getTime().getTimeBinary().doubleValue());
    // to.getXAxisDeltas().add(0.0);
    //
    // return to;
    // }
    //
    // /**
    // * /**
    // * Convert a DMReal to a DMInt
    // *
    // * @param from the DMReal
    // * @return a DMInt
    // */
    // @Deprecated
    // public static DMInt convertDMRealToDMInt(DMReal from) {
    // if (from == null) {
    // return null;
    // }
    //
    // DMInt to = new DMInt();
    // setDataEventProperties(from, to);
    //
    // // setting the value
    // to.setValue(new Long(java.lang.Math.round(from.getValue())).intValue());
    //
    // return to;
    // }
    //
    // /**
    // * Convert a DMReal to a DMVector
    // *
    // * @param from the DMReal
    // * @return a DMVector
    // */
    // @Deprecated
    // public static DMVector convertDMRealToDMVector(DMReal from) {
    // if (from == null) {
    // return null;
    // }
    //
    // DMVector to = new DMVector();
    // setDataEventProperties(from, to);
    //
    // // setting the value
    // to.setValue(from.getValue());
    // to.setXValue(0.0);
    //
    // return to;
    // }
    //
    // /**
    // * Convert a DMReal to a DMDataSeq
    // *
    // * @param from the DMReal
    // * @return a DMDataSeq
    // */
    // @Deprecated
    // public static DMDataSeq convertDMRealToDMDataSeq(DMReal from) {
    // if (from == null) {
    // return null;
    // }
    //
    // DMDataSeq to = new DMDataSeq();
    // setDataEventProperties(from, to);
    //
    // to.getValues().add(from.getValue());
    //
    // // seting XAxisStart and XAxisDeltas
    // to.setXAxisStart(from.getValue());
    // to.getXAxisDeltas().add(0.0);
    //
    // return to;
    // }
    //
    // /**
    // * Convert the values contained in a DMDataSeq into a DMInt
    // *
    // * @param from the DMDataSeq to be converted
    // * @return DMInt
    // */
    // @Deprecated
    // public static DMInt convertDMDataSeqToDMInt(DMDataSeq from) {
    // if (from == null) {
    // return null;
    // }
    //
    // DMInt to = new DMInt();
    // setDataEventProperties(from, to);
    //
    // // setting the values
    // List<Double> vals = from.getValues();
    // if (vals != null && vals.size() > 0) {
    // to.setValue(new Long(java.lang.Math.round((vals.get(vals.size() -
    // 1).doubleValue()))).intValue());
    // }
    //
    // return to;
    //
    // }
    //
    // /**
    // * Convert the values contained in a DMDataSeq into a DMReal
    // *
    // * @param from the DMDataSeq to be converted
    // * @return DMReal
    // */
    // @Deprecated
    // public static DMReal convertDMDataSeqToDMReal(DMDataSeq from) {
    // if (from == null) {
    // return null;
    // }
    //
    // DMReal to = new DMReal();
    // setDataEventProperties(from, to);
    //
    // // setting the values
    // List<Double> vals = from.getValues();
    // if (vals != null && vals.size() > 0) {
    // to.setValue(vals.get(vals.size() - 1).doubleValue());
    // }
    // return to;
    //
    // }
    //
    // /**
    // * Extracts the time stamps of data contained within a
    // * {@link org.mimosa.osacbmv3_3.DMDataSeq DMDataSeq} and returns an array
    // of
    // * doubles representing the time in milliseconds from the epoch.
    // *
    // * @param dms DMDataSeq
    // * @return array of values
    // */
    // @Deprecated
    // public static double[] extractTimeDataAsDoubleArray(DMDataSeq dms) {
    //
    // if (dms.getXAxisStart() != null && dms.getXAxisDeltas() != null) {
    // return createDoubleArrayFromXAxisDeltasAndStart(
    // dms.getXAxisStart(), dms.getXAxisDeltas());
    // }
    // return null;
    // }
    //
    // /**
    // * Extracts the time stamps of data contained within a
    // * {@link org.mimosa.osacbmv3_3.DADataSeq DADataSeq} and returns an array
    // of
    // * doubles representing the time in milliseconds from the epoch.
    // *
    // * @param das DADataSeq
    // * @return array of values
    // */
    // @Deprecated
    // public static double[] extractTimeDataAsDoubleArray(DADataSeq das) {
    //
    // return createDoubleArrayFromXAxisDeltasAndStart(das.getXAxisStart(),
    // das.getXAxisDeltas());
    // }
    //
    // /**
    // * Extracts the time stamps of data contained within a
    // * {@link org.mimosa.osacbmv3_3.DMDataSeq DMDataSeq} and returns an array
    // of
    // * {@link java.util.Date Date} objects.
    // *
    // * @param dms
    // * @return
    // */
    // @Deprecated
    // public static Date[] extractTimeDataAsDateArray(DMDataSeq dms) {
    //
    // return createDateArrayFromXAxisDeltasAndStart(dms.getXAxisStart(),
    // dms.getXAxisDeltas());
    // }
    //
    // /**
    // * Extracts the time stamps of data contained within a
    // * {@link org.mimosa.osacbmv3_3.DADataSeq DADataSeq} and returns an array
    // of
    // * {@link java.util.Date Date} objects.
    // *
    // * @param das DADataSeq
    // * @return array of Date
    // */
    // @Deprecated
    // public static Date[] extractTimeDataAsDateArray(DADataSeq das) {
    //
    // return createDateArrayFromXAxisDeltasAndStart(das.getXAxisStart(),
    // das.getXAxisDeltas());
    // }
    //
    //
    // /**
    // * convert a UserDef that is an OSA String data type to a java long
    // *
    // * @param in
    // * @return
    // */
    // @Deprecated
    // public static long convertDataToLong(UserDef in) {
    // return ((org.mimosa.osacbmv3_3.LongValue) ((org.mimosa.osacbmv3_3.Data)
    // in
    // .getValue()).getValue()).getValue();
    // }
    //
    //
    // /**
    // * convert a UserDef that is an OSA String data type to a java String
    // *
    // * @param in
    // * @return
    // */
    // @Deprecated
    // public static String convertDataToString(UserDef in) {
    // return ((org.mimosa.osacbmv3_3.StringValue) ((org.mimosa.osacbmv3_3.Data)
    // in
    // .getValue()).getValue()).getValue();
    // }
    //
    // /**
    // * Retrieves the SDEnumSetHeader from SDEnumSet
    // *
    // * @param es SDEnumSet
    // * @return a list of String
    // */
    // @Deprecated
    // public static List<String> getSDEnumSetHeader(SDEnumSet es) {
    // List<String> ret = new ArrayList<String>();
    // ret.add("ID " + "Fault Name");
    // return ret;
    // }
    //
    // @Deprecated
    // public static List<String> getFutureHlthTrendHeader(FutureHlthTrend
    // trend) {
    //
    // final String faultnamehdr = "Fault";
    // final String faultidhdr = "FaultId";
    // final String curvehdr = "Prognostic Curve";
    //
    // List<String> ret = new ArrayList<String>();
    //
    // ret.add(faultnamehdr);
    // ret.add(faultidhdr);
    // ret.add(curvehdr);
    //
    // return ret;
    // }
    //
    // /**
    // * Retrieves FutureHlthTrend TimeAxis from FutureHlthTrend
    // * as string
    // *
    // * @param trend FutureHlthTrend
    // * @return time axis in a list of String
    // */
    // @Deprecated
    // public static List<String> getFutureHlthTrendTimeAxisStrings(
    // FutureHlthTrend trend) {
    // List<String> ret = new ArrayList<String>();
    //
    // ret.add(trend.getItemId().getName());
    // ret.add(trend.getItemId().getCode() + "");
    // for (int i = 0; i < trend.getAtRefs().size(); i++) {
    // ret.add(trend.getAtRefs().get(i) + "");
    // }
    //
    // return ret;
    // }
    //
    // /**
    // * Retrieves FutureHlthTrend Trend Strength X-Axis from FutureHlthTrend
    // * as string
    // *
    // * @param trend FutureHlthTrend
    // * @return string list of Trend Strength X-Axis
    // */
    // @Deprecated
    // public static List<String> getFutureHlthTrendStrengthAxisStrings(
    // FutureHlthTrend trend) {
    // List<String> ret = new ArrayList<String>();
    //
    // ret.add(trend.getItemId().getName());
    // ret.add(trend.getItemId().getCode() + "");
    // for (int i = 0; i < trend.getHlthGrades().size(); i++) {
    // ret.add(trend.getHlthGrades().get(i) + "");
    // }
    //
    // return ret;
    // }
    //
    //
    // /**
    // * This method supports use as per the diagnostics module.
    // * i.e it expects only a single itemHealth and uses the info from that.
    // *
    // * @param in HADataEvent
    // * @return list of objects
    // */
    // @Deprecated
    // public static List<Object> convertHADataEventToTaggedArrayList(
    // HADataEvent in) {
    // final String confid = "Confidence";
    // final String diagnosisname = "Diagnosis";
    // final String diagnosiscode = "Diagnosis Id";
    // final String duration = "Duration";
    // final String faultstartdate = "Start Date";
    // final String strength = "Strength";
    //
    // List<Object> ret = new ArrayList<Object>();
    //
    // // Name
    // ArrayList<Object> entry = new ArrayList<Object>();
    // entry.add(diagnosisname);
    // entry.add(in.getItemHealth().get(0).getItemId().getName());
    // ret.add(entry);
    //
    // // Diagnosis ID
    // entry = new ArrayList<Object>();
    // entry.add(diagnosiscode);
    // entry.add(in.getItemHealth().get(0).getItemId().getCode());
    // ret.add(entry);
    //
    // // Confidence
    // entry = new ArrayList<Object>();
    // entry.add(confid);
    // entry.add(in.getConfid());
    // ret.add(entry);
    //
    // // Duration
    // entry = new ArrayList<Object>();
    // entry.add(duration);
    // entry.add(in.getItemHealth().get(0).getHGradeReal());
    // ret.add(entry);
    //
    // // Fault Start Date
    // entry = new ArrayList<Object>();
    // entry.add(faultstartdate);
    // entry.add(Utils.convertOsacbmTimeToDate(in.getItemHealth().get(0)
    // .getUtcHealth()));
    // ret.add(entry);
    //
    // // Strength
    // entry = new ArrayList<Object>();
    // entry.add(strength);
    // entry.add(in.getItemHealth().get(0).getLikelihood());
    // ret.add(entry);
    //
    // return ret;
    // }
    //
    // /**
    // * Retrieves HADataEvent information an returns it as list of String
    // *
    // * @param in HADataEvent
    // * @return list of string
    // */
    // @Deprecated
    // public static List<String> getHADataEventStrings(HADataEvent in) {
    // List<String> l = null;
    //
    // l = new ArrayList<String>();
    // l.add(in.getItemHealth().get(0).getItemId().getName());
    // l.add(in.getItemHealth().get(0).getItemId().getCode() + "");
    // l.add(in.getConfid() + "");
    // l.add(in.getItemHealth().get(0).getHGradeReal() + "");
    // l.add(Utils.getDisplayDateFormat().format(
    // Utils.convertOsacbmTimeToDate(in.getItemHealth().get(0)
    // .getUtcHealth())));
    // l.add(in.getItemHealth().get(0).getLikelihood() + "");
    //
    // return l;
    // }
    //
    // /**
    // * Retrieves HDDataEventHeader
    // *
    // * @param in HADataEvent
    // * @return list of string
    // */
    // @Deprecated
    // public static List<String> getHADataEventHeader(HADataEvent in) {
    // final String confid = "Confidence";
    // final String diagnosisname = "Diagnosis";
    // final String diagnosiscode = "Diagnosis Id";
    // final String duration = "Duration";
    // final String faultstartdate = "Start Date";
    // final String strength = "Strength";
    //
    // List<String> l = null;
    //
    // l = new ArrayList<String>();
    // l.add(diagnosisname);
    // l.add(diagnosiscode);
    // l.add(confid);
    // l.add(duration);
    // l.add(faultstartdate);
    // l.add(strength);
    // return l;
    // }
    //
    // /**
    // * Creates new HADataEvent
    // *
    // * @param name itemName
    // * @param id item Id
    // * @param confidence confidence value
    // * @param duration duration value
    // * @param start start date
    // * @param strength strength value
    // * @return HADataEvent
    // */
    // @Deprecated
    // public static HADataEvent getNewHADataEvent(String name, int id,
    // double confidence, double duration, Date start, double strength) {
    // HADataEvent ret = new HADataEvent();
    // ItemHealth ih = new ItemHealth();
    // ItemId ii = new ItemId();
    //
    // // Put name into itemHealth.item_id.name
    // // id into itemHealth.item_id.code
    // // confidence into confid
    // // duration into itemHealth.hGradeReal
    // // start into itemHealth.utc_health
    // // strength into itemHealth.likelihood
    //
    // ii.setName(name);
    // ii.setCode(new Long(id + ""));
    // ret.setConfid((float) confidence);
    // ih.setHGradeReal(duration);
    // ih.setUtcHealth(Utils.convertDateToOsacbmTime(start));
    // ih.setLikelihood(strength);
    //
    // ih.setItemId(ii);
    // ret.getItemHealth().add(ih);
    //
    // Site site = createSiteWithRequiredFields(null);
    // site.setUserTag(name);
    // ret.setSite(site);
    //
    // return ret;
    // }
    //
    // /**
    // * Converts Diagnostic DataEventSet to a List
    // *
    // * @param in DataEventSet
    // * @return list of HADataEvent
    // */
    // @Deprecated
    // public static List<Object>
    // convertDiagnosticDataEventSetToTaggedArrayList(
    // DataEventSet in) {
    // ArrayList<Object> ret = new ArrayList<Object>();
    //
    // for (DataEvent d : in.getDataEvents()) {
    // if (d instanceof HADataEvent) {
    // ret.add(Utils
    // .convertHADataEventToTaggedArrayList((HADataEvent) d));
    // }
    // }
    //
    // return ret;
    // }
    //
    // /**
    // * Converts MPS DataEventSet to a list
    // *
    // * @param in DataEventSet
    // * @return list of SDEvent
    // */
    // @Deprecated
    // static public List<Object> convertMPSDataEventSetToTaggedArrayList(
    // DataEventSet in) {
    //
    // ArrayList<Object> ret = new ArrayList<Object>();
    //
    // for (int i = 0; i < in.getDataEvents().size(); i++) {
    // if (in.getDataEvents().get(i) instanceof SDEvent) {
    // ret.add(Utils
    // .convertShiftSDEventToTaggedArrayList(((SDEvent) in
    // .getDataEvents().get(i))));
    // }
    // }
    //
    // return ret;
    // }
    //
    // /**
    // * Converts ShiftS SDEvent to a list
    // *
    // * @param in SDEvent
    // * @return list of objects
    // */
    // @Deprecated
    // static public List<Object> convertShiftSDEventToTaggedArrayList(
    // SDEvent in) {
    // final String startdate = "StartDate";
    // final String enddate = "EndDate";
    // final String duration = "Duration";
    // final String startvalue = "StartValue";
    // final String endvalue = "EndValue";
    // final String shift = "Shift";
    // final String noise = "Noise";
    // final int REAL_INDEX_SHIFT = 0;
    // final int REAL_INDEX_START_VALUE = 1;
    // final int REAL_INDEX_END_VALUE = 2;
    // final int REAL_INDEX_DURATION = 3;
    // final int REAL_INDEX_NOISE = 4;
    //
    // ArrayList<Object> ret = new ArrayList<Object>();
    //
    // for (int i = 0; i < in.getItemEvents().size(); i++) {
    // ItemEvent e = in.getItemEvents().get(i);
    //
    // ArrayList<Object> entry = new ArrayList<Object>();
    // entry.add(startdate);
    // entry.add(Utils.convertOsacbmTimeToDate(e.getEventStart()));
    // ret.add(entry);
    //
    // entry = new ArrayList<Object>();
    // entry.add(enddate);
    // entry.add(Utils.convertOsacbmTimeToDate(e.getEventStop()));
    // ret.add(entry);
    //
    // entry = new ArrayList<Object>();
    // entry.add(duration);
    // if (e.getItemEventNumReal().size() > REAL_INDEX_DURATION) {
    // entry.add(e.getItemEventNumReal().get(REAL_INDEX_DURATION)
    // .getDataValue());
    // } else {
    // entry.add(0.0);
    // }
    // ret.add(entry);
    //
    // entry = new ArrayList<Object>();
    // entry.add(startvalue);
    // if (e.getItemEventNumReal().size() > REAL_INDEX_START_VALUE) {
    // entry.add(e.getItemEventNumReal().get(REAL_INDEX_START_VALUE)
    // .getDataValue());
    // } else {
    // entry.add(0.0);
    // }
    // ret.add(entry);
    //
    // entry = new ArrayList<Object>();
    // entry.add(endvalue);
    // if (e.getItemEventNumReal().size() > REAL_INDEX_END_VALUE) {
    // entry.add(e.getItemEventNumReal().get(REAL_INDEX_END_VALUE)
    // .getDataValue());
    // } else {
    // entry.add(0.0);
    // }
    // ret.add(entry);
    //
    // entry = new ArrayList<Object>();
    // entry.add(shift);
    // if (e.getItemEventNumReal().size() > REAL_INDEX_SHIFT) {
    // entry.add(e.getItemEventNumReal().get(REAL_INDEX_SHIFT)
    // .getDataValue());
    // } else {
    // entry.add(0.0);
    // }
    // ret.add(entry);
    //
    // entry = new ArrayList<Object>();
    // entry.add(noise);
    // if (e.getItemEventNumReal().size() > REAL_INDEX_NOISE) {
    // entry.add(e.getItemEventNumReal().get(REAL_INDEX_NOISE)
    // .getDataValue());
    // } else {
    // entry.add(0.0);
    // }
    // ret.add(entry);
    // }
    //
    // return ret;
    // }
    //
    //
    // /**
    // * Given a {@link DataEventSet} this method will search through it and
    // * attempt to find an {@link SDEnumSet} and return it, otherwise it will
    // * return null.
    // *
    // * @param input
    // * @return The found {@link SDEnumSet} or null if none exists.
    // */
    // @Deprecated
    // public static SDEnumSet findSdEnumSet(DataEventSet input) {
    // SDEnumSet enumSet = null;
    //
    // for (DataEvent event : input.getDataEvents()) {
    // if (event instanceof SDEnumSet) {
    // enumSet = (SDEnumSet) event;
    // }
    // }
    //
    // return enumSet;
    // }
    //
    //
    // /**
    // * Converts list of double values to StringBuffer
    // *
    // * @param values list of values to convert
    // * @return a StringBuffer
    // */
    // @Deprecated
    // public static StringBuffer doublevToStringList(List<Double> values) {
    // StringBuffer ret = new StringBuffer();
    // Iterator<Double> valueI = values.iterator();
    // while (valueI.hasNext()) {
    // ret.append(valueI.next().toString());
    // if (valueI.hasNext()) {
    // ret.append(",");
    // }
    // }
    //
    // return ret;
    // }
    //
    // /**
    // * Converts list of integer values
    // *
    // * @param values list of integer values to convert
    // * @return a StringBuffer
    // */
    // @Deprecated
    // public static StringBuffer integervToStringList(List<Integer> values) {
    // StringBuffer ret = new StringBuffer();
    // Iterator<Integer> valueI = values.iterator();
    // while (valueI.hasNext()) {
    // ret.append(valueI.next().toString());
    // if (valueI.hasNext()) {
    // ret.append(",");
    // }
    // }
    //
    // return ret;
    // }
    //
    // /**
    // * Convert a double array to an OSA-CBM DMDataSeq
    // *
    // * @param result a double array
    // * @return the DMDataSeq
    // */
    // @Deprecated
    // public static DMDataSeq convertDoubleArrayToDMDataSeq(long id,
    // double[] result) {
    //
    // DMDataSeq osaDMDataSeq = new DMDataSeq();
    // osaDMDataSeq.setId(id);
    // if (result != null) {
    // for (int i = 0; i < result.length; i++) {
    // double val = result[i];
    // osaDMDataSeq.getValues().add(new Double(val));
    // }
    // }
    //
    // Site site = createSiteWithRequiredFields(null);
    // osaDMDataSeq.setSite(site);
    //
    // return osaDMDataSeq;
    // }
    //
    // /**
    // * Convert a double array to an OSA-CBM DataEvent
    // *
    // * @param result a double array
    // * @return the DADataSeq
    // */
    // @Deprecated
    // public static DADataSeq convertDoubleArrayToDADataSeq(long id,
    // double[] result) {
    //
    // DADataSeq osaDADataSeq = new DADataSeq();
    // osaDADataSeq.setId(id);
    // for (int i = 0; i < result.length; i++) {
    // double val = result[i];
    // osaDADataSeq.getValues().add(new Double(val));
    // }
    //
    // Site site = createSiteWithRequiredFields(null);
    // osaDADataSeq.setSite(site);
    //
    // return osaDADataSeq;
    // }
    //
    // /**
    // * Convert an int array to an OSA-CBM DMDataSeq
    // *
    // * @param result a int array
    // * @return the DMDataSeq
    // */
    // @Deprecated
    // public static DMDataSeq convertIntArrayToDMDataSeq(long id, int[] result)
    // {
    //
    // DMDataSeq osaDMDataSeq = new DMDataSeq();
    // osaDMDataSeq.setId(id);
    // for (int i = 0; i < result.length; i++) {
    // double val = result[i];
    // osaDMDataSeq.getValues().add(new Double(val));
    // }
    //
    // Site site = createSiteWithRequiredFields(null);
    // osaDMDataSeq.setSite(site);
    //
    // return osaDMDataSeq;
    // }
    //
    //
    // /**
    // * Convert the values contained in a DADataSeq into a double[]
    // *
    // * @param in the DADataSeq to be converted
    // * @return the values
    // */
    // @Deprecated
    // public static double[] convertDADataSeqToDouble(DADataSeq in) {
    // double ret[] = null;
    //
    // if (in != null) {
    // List<Double> vals = in.getValues();
    // if (vals != null) {
    // ret = new double[vals.size()];
    // for (int i = 0; i < vals.size(); i++) {
    // ret[i] = vals.get(i).doubleValue();
    // }
    // }
    // }
    // return ret;
    // }
    // /**
    // * Convert the values contained in a DMDataSeq into a double[]
    // *
    // * @param in the DMDataSeq to be converted
    // * @return the values
    // */
    // @Deprecated
    // public static double[] convertDMDataSeqToDouble(DMDataSeq in) {
    // double ret[] = null;
    //
    // if (in != null) {
    // List<Double> vals = in.getValues();
    // if (vals != null) {
    // ret = new double[vals.size()];
    // for (int i = 0; i < vals.size(); i++) {
    // ret[i] = vals.get(i).doubleValue();
    // }
    // }
    // }
    // return ret;
    // }
    // /**
    // * Convert the values contained in a DMDataSeq into a double[]
    // *
    // * @param in the DMDataSeq to be converted
    // * @return the values
    // */
    // @Deprecated
    // public static double[] convertDAWaveformToDouble(DAWaveform in) {
    // double ret[] = null;
    //
    // if (in != null) {
    // List<Double> vals = in.getValues();
    // if (vals != null) {
    // ret = new double[vals.size()];
    // for (int i = 0; i < vals.size(); i++) {
    // ret[i] = vals.get(i).doubleValue();
    // }
    // }
    // }
    // return ret;
    // }
    // /**
    // * Convert the values contained in a DMDataSeq into a double[]
    // *
    // * @param in the DMDataSeq to be converted
    // * @return the values
    // */
    // @Deprecated
    // public static double[] convertDAValueDataSeqToDouble(DAValueDataSeq in) {
    // double ret[] = null;
    //
    // if (in != null) {
    // Value vals = in.getValues();
    // if (vals instanceof DblArrayValue) {
    // List<Double> values = ((DblArrayValue) vals).getValues();
    // ret = new double[values.size()];
    // for (int i = 0; i < values.size(); i++) {
    // ret[i] = values.get(i).doubleValue();
    // }
    // }
    // }
    // return ret;
    // }
    // /**
    // * Convert the time series and values contained in a DMDataSeq into a
    // * double[][]
    // *
    // * @param in the DMDataSeq to be converted
    // * @return the 2-dimensional array
    // */
    // @Deprecated
    // public static double[][] convertDMDataSeqToTwoDimDouble(DMDataSeq in) {
    // double ret[][] = null;
    //
    // if (in.getXAxisDeltas() == null || in.getXAxisDeltas().size() == 0) {
    // return ret;
    // }
    //
    // if (in.getValues() == null
    // || (in.getValues().size() != in.getXAxisDeltas().size())) {
    // return ret;
    // }
    //
    // double time = in.getXAxisStart();
    //
    // ret = new double[in.getXAxisDeltas().size()][2];
    //
    // for (int i = 0; i < in.getXAxisDeltas().size(); i++) {
    // time += in.getXAxisDeltas().get(i);
    //
    // ret[i][0] = time;
    // ret[i][1] = in.getValues().get(i);
    //
    // }
    //
    // return ret;
    // }
    // // don't think this is used and don't think the x,y ordering is
    // consistent
    // // with
    // // other similar methods
    //
    // /**
    // * Convert the time series and values contained in a DADataSeq into a
    // * double[][]
    // *
    // * @param in the DADataSeq to be converted
    // * @return the 2-dimensional array
    // */
    // @Deprecated
    // public static double[][] convertDADataSeqToTwoDimDouble(DADataSeq in) {
    // double ret[][] = null;
    //
    // if (in.getXAxisDeltas() == null || in.getXAxisDeltas().size() == 0) {
    // return ret;
    // }
    //
    // if (in.getValues() == null
    // || (in.getValues().size() != in.getXAxisDeltas().size())) {
    // return ret;
    // }
    //
    // double time = in.getXAxisStart();
    //
    // ret = new double[in.getXAxisDeltas().size()][2];
    //
    // for (int i = 0; i < in.getXAxisDeltas().size(); i++) {
    // time += in.getXAxisDeltas().get(i);
    //
    // ret[i][0] = time;
    // ret[i][1] = in.getValues().get(i);
    //
    // }
    //
    // return ret;
    // }
    //
    // /**
    // * Create a DMReal from a double
    // *
    // * @param id dataEventId
    // * @param val the double value
    // * @return a DMReal with the specified value
    // */
    // @Deprecated
    // public static DMReal getDMReal(long id, double val) {
    // DMReal ret = getDMReal(val, null);
    // if (id != -1) {
    // ret.setId(id);
    // }
    // Site site = createSiteWithRequiredFields(null);
    // ret.setSite(site);
    // return ret;
    // }
    //
    // /**
    // * Create a DMReal from a double and specified alert name
    // *
    // * @param val the double value
    // * @param alertName the name of the NumAlert
    // * @return a DMReal with the specified value and alert name
    // */
    // @Deprecated
    // public static DMReal getDMReal(double val, String alertName) {
    // DMReal dmr = new DMReal();
    // dmr.setValue(val);
    //
    // if (alertName != null) {
    // NumAlert alert = new NumAlert();
    // alert.setAlertName(alertName);
    // dmr.getNumAlerts().add(alert);
    // }
    //
    // return dmr;
    // }
    //
    //
    // /**
    // * Converts DataEventSet to array of double values
    // *
    // * @param dataEventSet DataEventSet to convert
    // * @return array of values
    // */
    // @Deprecated
    // public static double[] convertDataEventSetToDouble(DataEventSet
    // dataEventSet) {
    //
    // double[] ret = new double[dataEventSet.getDataEvents().size()];
    //
    // Iterator<DataEvent> dataEventsI =
    // dataEventSet.getDataEvents().iterator();
    // int i = 0;
    // while (dataEventsI.hasNext()) {
    // DataEvent dataEvent = dataEventsI.next();
    //
    // if (dataEvent instanceof DMReal) {
    // ret[i++] = ((DMReal) dataEvent).getValue();
    // } else if (dataEvent instanceof DMInt) {
    // ret[i++] = ((DMInt) dataEvent).getValue() + 0.0;
    // } else if (dataEvent instanceof DMDataSeq) {
    // return convertDMDataSeqToDouble((DMDataSeq) dataEvent);
    // } else if (dataEvent instanceof DAReal) {
    // ret[i++] = ((DAReal) dataEvent).getValue();
    // } else if (dataEvent instanceof DAInt) {
    // ret[i++] = ((DAInt) dataEvent).getValue() + 0.0;
    // } else if (dataEvent instanceof DADataSeq) {
    // return convertDADataSeqToDouble((DADataSeq) dataEvent);
    // }
    // }
    //
    // return ret;
    // }
    // /**
    // * At this point the support types are UserDef - containing the string
    // * value, DMBool, DMInt, DMReal.
    // *
    // * @param name name of dataEvent
    // * @param valueIn the string value
    // * @param type type of DataEvent
    // * @param timeVal string representation of time value
    // * @return DataEvent
    // */
    // @Deprecated
    // public static DataEvent convertStringToDataEvent(String name,
    // String valueIn, String type, String timeVal) {
    //
    // OsacbmTime osaTime = null;
    // if (timeVal != null) {
    // Date timestamp = convertStringToDate(timeVal);
    // osaTime = convertDateToOsacbmTime(timestamp);
    // }
    //
    // if (type.equals("org.mimosa.osacbmv3_3.UserDef")
    // || type.equals("org.mimosa.osacbmv3_3.String")) {
    // org.mimosa.osacbmv3_3.StringValue value = new
    // org.mimosa.osacbmv3_3.StringValue();
    // value.setValue(valueIn);
    // org.mimosa.osacbmv3_3.Data data = new org.mimosa.osacbmv3_3.Data();
    // data.setValue(value);
    // UserDef ud = new UserDef();
    // ud.setValue(data);
    // ud.setTime(osaTime);
    //
    // Site site = createSiteWithRequiredFields(null);
    // site.setUserTag(name);
    // ud.setSite(site);
    //
    // return ud;
    // }
    //
    // if (type.equals("org.mimosa.osacbmv3_3.DMBool")) {
    // DMBool dmBool = new DMBool();
    // dmBool.setValue(Boolean.valueOf(valueIn));
    // dmBool.setTime(osaTime);
    // Site site = createSiteWithRequiredFields(null);
    // site.setUserTag(name);
    // dmBool.setSite(site);
    //
    // return dmBool;
    //
    // }
    //
    // if (type.equals("org.mimosa.osacbmv3_3.DMInt")) {
    // DMInt dmInt = new DMInt();
    // dmInt.setValue(Integer.valueOf(valueIn));
    // dmInt.setTime(osaTime);
    // Site site = createSiteWithRequiredFields(null);
    // site.setUserTag(name);
    // dmInt.setSite(site);
    //
    // return dmInt;
    // }
    //
    // if (type.equals("org.mimosa.osacbmv3_3.DMReal")) {
    // DMReal dmReal = new DMReal();
    // dmReal.setValue(Double.valueOf(valueIn));
    // dmReal.setTime(osaTime);
    // Site site = createSiteWithRequiredFields(null);
    // site.setUserTag(name);
    // dmReal.setSite(site);
    //
    // return dmReal;
    // }
    //
    // // if we are creating a DataEvent just for a time value use the valueIn
    // // parameter's
    // // value rather than the timeVal parameter
    // if (type.equals("org.mimosa.osacbmv3_3.OsacbmTime")) {
    // Date timestamp = convertStringToDate(valueIn);
    // OsacbmTime osaTime1 = convertDateToOsacbmTime(timestamp);
    //
    // DataEvent de = new DataEvent();
    // de.setTime(osaTime1);
    // Site site = createSiteWithRequiredFields(null);
    // site.setUserTag(name);
    // de.setSite(site);
    //
    // return de;
    // }
    //
    // return null;
    //
    // }
    //
    //
}
