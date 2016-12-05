package com.ge.predix.solsvc.fdh.handler.asset.helper;

import java.util.List;

import org.mimosa.osacbmv3_3.DABool;
import org.mimosa.osacbmv3_3.DAInt;
import org.mimosa.osacbmv3_3.DAReal;
import org.mimosa.osacbmv3_3.DAString;
import org.mimosa.osacbmv3_3.DAValueWaveform;
import org.mimosa.osacbmv3_3.DAWaveform;
import org.mimosa.osacbmv3_3.DMBool;
import org.mimosa.osacbmv3_3.DMInt;
import org.mimosa.osacbmv3_3.DMReal;
import org.mimosa.osacbmv3_3.DMVector;
import org.mimosa.osacbmv3_3.DataEvent;
import org.mimosa.osacbmv3_3.DoubleValue;

/**
 * Convert from osa DataEvent objects to the supported Java types (see the SupportedJavaTypes enum)
 * and convert from the supported java types to the supported OsaDataTypes (see the SupportedOsaDataTypes enum)
 * 
 * @author 200002567
 * 
 */
public class OsaJavaDataTypeConversion
{

    /**
     * TimeArray data structures are Object[][] structures such that
     * Object[i][0] is a Double representing the number of milliseconds from Unix Epoc
     * Object[i][1] is the value corresponding to the [i][0] time
     * 
     * @author 200002567
     * 
     */
    public static enum SupportedJavaTypes
    {
        /**
         * 
         */
        Boolean,
        /**
         * 
         */
        Double,
        /**
         * 
         */
        Integer,
        /**
         * 
         */
        String,
        /**
         * 
         */
        BooleanVector,
        /**
         * 
         */
        DoubleVector,
        /**
         * 
         */
        IntegerVector,
        /**
         * 
         */
        StringVector,
        /**
         * 
         */
        BooleanTimeArray,
        /**
         * 
         */
        DoubleTimeArray,
        /**
         * 
         */
        IntegerTimeArray,
        /**
         * 
         */
        StringTimeArray
    };

    /**
     * 
     * @author predix -
     */
    public static enum SupportedOsaDataTypes
    {
        /**
         * 
         */
        DABool,
        /**
         * 
         */
        DAInt,
        /**
         * 
         */
        DAReal,
        /**
         * 
         */
        DAString,
        /**
         * 
         */
        DMBool,
        /**
         * 
         */
        DMInt,
        /**
         * 
         */
        DMReal,
        /**
         * 
         */
        DMVector,
        /**
         * 
         */
        DADataSeq,
        /**
         * 
         */
        DAValueWaveform,
        /**
         * 
         */
        DAValueDataSeq,
        /**
         * 
         */
        DAWaveform,
        /**
         * 
         */
        DMDataSeq;

        /**
         * @param v -
         * @return -
         */
        public static SupportedOsaDataTypes fromValue(String v)
        {
            return valueOf(v);
        }
    };

    /**
     * Convert OSA DataEvent objects to the supported standard java types (see the SupportedJavaTypes enum).
     * The conversion only applies for the supported OSA data types listed in the SupportedOsaDataTypes enum.
     * 
     * @param dataEvent -
     * @param toType -
     * @return -
     * @throws OsacbmConversionException -
     */
    public static Object convertDataEventToJavaStructure(DataEvent dataEvent, SupportedJavaTypes toType)
            throws OsacbmConversionException
    {
        if ( dataEvent == null )
        {
            return null;
        }
        if ( toType.compareTo(SupportedJavaTypes.Double) == 0 )
        {
            return convertOsaDatatypeToDouble(dataEvent);
        }

        if ( toType.compareTo(SupportedJavaTypes.Integer) == 0 )
        {
            return convertOsaDatatypeToInteger(dataEvent);
        }

        if ( toType.compareTo(SupportedJavaTypes.String) == 0 )
        {
            return convertOsaDatatypeToString(dataEvent);
        }

        if ( toType.compareTo(SupportedJavaTypes.Boolean) == 0 )
        {
            return convertOsaDatatypeToBoolean(dataEvent);
        }

        if ( toType.compareTo(SupportedJavaTypes.DoubleVector) == 0 )
        {
            return convertOsaDatatypeToDoubleSeries(dataEvent);
        }

        if ( toType.compareTo(SupportedJavaTypes.IntegerVector) == 0 )
        {
            return convertOsaDatatypeToIntegerSeries(dataEvent);
        }

        if ( toType.compareTo(SupportedJavaTypes.StringVector) == 0 )
        {
            return convertOsaDatatypeToStringSeries(dataEvent);
        }

        if ( toType.compareTo(SupportedJavaTypes.BooleanVector) == 0 )
        {
            return convertOsaDatatypeToBooleanSeries(dataEvent);
        }

        if ( toType.compareTo(SupportedJavaTypes.DoubleTimeArray) == 0 )
        {
            return convertOsaDatatypeToDoubleTimeSeries(dataEvent);
        }

        if ( toType.compareTo(SupportedJavaTypes.IntegerTimeArray) == 0 )
        {
            return convertOsaDatatypeToIntegerTimeSeries(dataEvent);
        }

        if ( toType.compareTo(SupportedJavaTypes.StringTimeArray) == 0 )
        {
            return convertOsaDatatypeToStringTimeSeries(dataEvent);
        }

        if ( toType.compareTo(SupportedJavaTypes.BooleanTimeArray) == 0 )
        {
            return convertOsaDatatypeToBooleanTimeSeries(dataEvent);
        }

        return null;
    }

    /**
     * convert an osa data value to an boolean: convert it to DABool then return
     * the value
     * 
     * @param osaValue
     * @return
     * @throws OsacbmConversionException
     */
    private static Boolean convertOsaDatatypeToBoolean(DataEvent osaValue)
            throws OsacbmConversionException
    {
        try
        {
            DABool convertedOsaValue = (DABool) OsaDataTypeConverter.convertToType(osaValue, DABool.class);

            return convertedOsaValue.isValue();
        }
        catch (OsacbmConversionException e)
        {
            throw new OsacbmConversionException(
                    "Trying to convert input to Boolean, but input is an incompatible type:" //$NON-NLS-1$
                            + osaValue.getClass().getName());
        }
    }

    /**
     * convert an osa data value to an double: convert it to DAReal then return
     * the value
     * 
     * @param osaValue
     * @return
     * @throws OsacbmConversionException
     */
    private static Double convertOsaDatatypeToDouble(DataEvent osaValue)
            throws OsacbmConversionException
    {
        try
        {
            DAReal convertedOsaValue = (DAReal) OsaDataTypeConverter.convertToType(osaValue, DAReal.class);

            return convertedOsaValue.getValue();
        }
        catch (OsacbmConversionException e)
        {
            throw new OsacbmConversionException("Trying to convert input to Double, but input is an incompatible type:" //$NON-NLS-1$
                    + osaValue.getClass().getName());
        }
    }

    /**
     * convert an osa data value to an integer: convert it to DMInt then return
     * the value
     * 
     * @param osaValue
     * @return
     * @throws OsacbmConversionException
     */
    private static Integer convertOsaDatatypeToInteger(DataEvent osaValue)
            throws OsacbmConversionException
    {
        try
        {
            DMInt convertedOsaValue = (DMInt) OsaDataTypeConverter.convertToType(osaValue, DMInt.class);

            return convertedOsaValue.getValue();
        }
        catch (OsacbmConversionException e)
        {
            throw new OsacbmConversionException(
                    "Trying to convert input to Integer, but input is an incompatible type:" //$NON-NLS-1$
                            + osaValue.getClass().getName());
        }
    }

    /**
     * convert an osa data value to an String: convert it to DAString then
     * return the value
     * 
     * @param osaValue
     * @return
     * @throws OsacbmConversionException
     */
    private static String convertOsaDatatypeToString(DataEvent osaValue)
            throws OsacbmConversionException
    {
        try
        {
            DAString convertedOsaValue = (DAString) OsaDataTypeConverter.convertToType(osaValue, DAString.class);

            return convertedOsaValue.getValue();
        }
        catch (OsacbmConversionException e)
        {
            throw new OsacbmConversionException("Trying to convert input to String, but input is an incompatible type:" //$NON-NLS-1$
                    + osaValue.getClass().getName() + " Got error:" + e.getMessage()); //$NON-NLS-1$
        }
    }

    /**
     * convert an osa data value to a Boolean series: if it is a singleton
     * osa data type return a
     * series with a single value. Otherwise the input must be a Values list
     * containing Boolean, Double, String, Integer, or Long values. This method returns null if there are
     * no values to return (as opposed to an empty Boolean[] structure).
     * 
     * @param osaValue
     * @return
     * @throws OsacbmConversionException
     */
    @SuppressWarnings(
    {
            "rawtypes", "unchecked"
    })
    private static Boolean[] convertOsaDatatypeToBooleanSeries(DataEvent osaValue)
            throws OsacbmConversionException
    {

        if ( osaValue instanceof DABool || osaValue instanceof DMBool || osaValue instanceof DAInt
                || osaValue instanceof DMInt || osaValue instanceof DMReal || osaValue instanceof DAReal
                || osaValue instanceof DAString || osaValue instanceof DMVector )
        {
            // put the singleton value into list
            DMBool convertedOsaValue = (DMBool) OsaDataTypeConverter.convertToType(osaValue, DMBool.class);
            Boolean ret[] = new Boolean[1];
            ret[0] = convertedOsaValue.isValue();
            return ret;
        }
        // get the vector values
        List values = Utils.getValuesList(osaValue);
        if ( values == null || values.size() == 0 )
        {
            return null;
        }

        Boolean[] ret = new Boolean[values.size()];
        if ( values.get(0) instanceof Boolean )
        {
            ret = (Boolean[]) values.toArray(new Boolean[values.size()]);
        }
        else if ( values.get(0) instanceof Double )
        {
            for (int i = 0; i < values.size(); i++)
            {
                ret[i] = (Double) values.get(i) > 0.0;
            }
        }
        else if ( values.get(0) instanceof Integer )
        {
            for (int i = 0; i < values.size(); i++)
            {
                ret[i] = (Integer) values.get(i) > 0;
            }
        }
        else if ( values.get(0) instanceof Long )
        {
            for (int i = 0; i < values.size(); i++)
            {
                ret[i] = (Long) values.get(i) > 0L;
            }
        }
        else if ( values.get(0) instanceof String )
        {
            for (int i = 0; i < values.size(); i++)
            {
                ret[i] = (String) values.get(i) != null && !((String) values.get(i)).trim().equals(""); //$NON-NLS-1$
            }
        }
        else
        {
            throw new OsacbmConversionException(
                    "Trying to convert input to list of Boolean, but input is an incompatible type:" //$NON-NLS-1$
                            + osaValue.getClass().getName() + " with " + values.get(0).getClass().getName() //$NON-NLS-1$
                            + " values."); //$NON-NLS-1$
        }

        return ret;
    }

    /**
     * convert an osa data value to a Double series: if it is a singleton
     * osa data type return a
     * series with a single value. Otherwise the input must be a Values list
     * containing DblArrayValue or a DADataSeq, DMDataSeq, DAWaveform.
     * This method returns null if there are
     * no values to return (as opposed to an empty Double[] structure).
     * 
     * @param osaValue
     * @return
     * @throws OsacbmConversionException
     */
    @SuppressWarnings(
    {
            "rawtypes", "unchecked", "nls"
    })
    private static Double[] convertOsaDatatypeToDoubleSeries(DataEvent osaValue)
            throws OsacbmConversionException
    {

        if ( osaValue instanceof DABool || osaValue instanceof DMBool || osaValue instanceof DAInt
                || osaValue instanceof DMInt || osaValue instanceof DMReal || osaValue instanceof DAReal
                || osaValue instanceof DAString || osaValue instanceof DMVector )
        {
            // put the singleton value into list
            DMReal convertedOsaValue = (DMReal) OsaDataTypeConverter.convertToType(osaValue, DMReal.class);
            Double ret[] = new Double[1];
            ret[0] = convertedOsaValue.getValue();
            return ret;
        }
        // get the vector values
        List values = Utils.getValuesList(osaValue);
        if ( values == null || values.size() == 0 )
        {
            return null;
        }

        Double[] ret = new Double[values.size()];
        if ( values.get(0) instanceof Boolean )
        {
            for (int i = 0; i < values.size(); i++)
            {
                if ( (Boolean) values.get(i) )
                {
                    ret[i] = 1.0;
                }
                else
                {
                    ret[i] = 0.0;
                }
            }
        }
        else if ( values.get(0) instanceof Double )
        {
            ret = (Double[]) values.toArray(new Double[values.size()]);
        }
        else if ( values.get(0) instanceof Integer )
        {
            for (int i = 0; i < values.size(); i++)
            {
                ret[i] = (Integer) values.get(i) + 0.0;
            }
        }
        else if ( values.get(0) instanceof Long )
        {
            for (int i = 0; i < values.size(); i++)
            {
                ret[i] = (Long) values.get(i) + 0.0;
            }
        }
        else if ( values.get(0) instanceof String )
        {
            for (int i = 0; i < values.size(); i++)
            {
                try
                {
                    ret[i] = Double.parseDouble(((String) values.get(i)).trim()) + 0.0;
                }
                catch (NumberFormatException nfe)
                {
                    throw new OsacbmConversionException(
                            "Trying to convert list of String input to list of Double, but got parse exception:" //$NON-NLS-1$
                                    + osaValue.getClass().getName() + " with " + values.get(0).getClass().getName() //$NON-NLS-1$
                                    + " values.");
                }
            }
        }
        else
        {
            throw new OsacbmConversionException(
                    "Trying to convert input to list of Double, but input is an incompatible type:"
                            + osaValue.getClass().getName() + " with " + values.get(0).getClass().getName()
                            + " values.");
        }

        return ret;
    }

    /**
     * convert an osa data value to an Integer series: if it is a singleton
     * osa data type return a
     * series with a single value. Otherwise the input must be a Values list
     * containing IntegerArrayValue. This method returns null if there are
     * no values to return (as opposed to an empty Integer[] structure).
     * 
     * @param osaValue
     * @return
     * @throws OsacbmConversionException
     */
    @SuppressWarnings(
    {
            "rawtypes", "unchecked", "nls"
    })
    private static Integer[] convertOsaDatatypeToIntegerSeries(DataEvent osaValue)
            throws OsacbmConversionException
    {

        if ( osaValue instanceof DABool || osaValue instanceof DMBool || osaValue instanceof DAInt
                || osaValue instanceof DMInt || osaValue instanceof DMReal || osaValue instanceof DAReal
                || osaValue instanceof DAString || osaValue instanceof DMVector )
        {
            // put the singleton value into list
            DMInt convertedOsaValue = (DMInt) OsaDataTypeConverter.convertToType(osaValue, DMInt.class);
            Integer ret[] = new Integer[1];
            ret[0] = convertedOsaValue.getValue();
            return ret;
        }
        // get the vector values
        List values = Utils.getValuesList(osaValue);
        if ( values == null || values.size() == 0 )
        {
            return null;
        }

        Integer[] ret = new Integer[values.size()];
        if ( values.get(0) instanceof Boolean )
        {
            for (int i = 0; i < values.size(); i++)
            {
                if ( (Boolean) values.get(i) )
                {
                    ret[i] = 1;
                }
                else
                {
                    ret[i] = 0;
                }
            }
        }
        else if ( values.get(0) instanceof Double )
        {
            for (int i = 0; i < values.size(); i++)
            {
                ret[i] = Long.valueOf(Math.round((Double) values.get(i))).intValue();
            }
        }
        else if ( values.get(0) instanceof Integer )
        {
            ret = (Integer[]) values.toArray(new Integer[values.size()]);
        }
        else if ( values.get(0) instanceof Long )
        {
            for (int i = 0; i < values.size(); i++)
            {
                ret[i] = ((Long) values.get(i)).intValue();
            }
        }
        else if ( values.get(0) instanceof String )
        {
            for (int i = 0; i < values.size(); i++)
            {
                try
                {
                    ret[i] = Integer.valueOf(Integer.parseInt(((String) values.get(i)).trim()));
                }
                catch (NumberFormatException nfe)
                {
                    throw new OsacbmConversionException(
                            "Trying to convert list of String input to list of Integer, but got parse exception:" //$NON-NLS-1$
                                    + osaValue.getClass().getName() + " with " + values.get(0).getClass().getName() //$NON-NLS-1$
                                    + " values.");
                }
            }
        }
        else
        {
            throw new OsacbmConversionException(
                    "Trying to convert input to list of Integer, but input is an incompatible type:"
                            + osaValue.getClass().getName() + " with " + values.get(0).getClass().getName()
                            + " values.");
        }

        return ret;
    }

    /**
     * convert an osa data value to an String series: if it is a singleton
     * osa data type return a
     * series with a single value. Otherwise the input must be a Values list
     * containing StringArrayValue. This method returns null if there are
     * no values to return (as opposed to an empty String[] structure).
     * 
     * @param osaValue
     * @return
     * @throws OsacbmConversionException
     */
    @SuppressWarnings(
    {
            "rawtypes", "unchecked", "nls"
    })
    private static String[] convertOsaDatatypeToStringSeries(DataEvent osaValue)
            throws OsacbmConversionException
    {

        if ( osaValue instanceof DABool || osaValue instanceof DMBool || osaValue instanceof DAInt
                || osaValue instanceof DMInt || osaValue instanceof DMReal || osaValue instanceof DAReal
                || osaValue instanceof DAString || osaValue instanceof DMVector )
        {
            // put the singleton value into list
            DAString convertedOsaValue = (DAString) OsaDataTypeConverter.convertToType(osaValue, DAString.class);
            String ret[] = new String[1];
            ret[0] = convertedOsaValue.getValue();
            return ret;
        }
        // get the vector values
        List values = Utils.getValuesList(osaValue);
        if ( values == null || values.size() == 0 )
        {
            return null;
        }

        String[] ret = new String[values.size()];
        if ( values.get(0) instanceof Boolean )
        {
            for (int i = 0; i < values.size(); i++)
            {
                if ( (Boolean) values.get(i) )
                {
                    ret[i] = "true";
                }
                else
                {
                    ret[i] = "false";
                }
            }
        }
        else if ( values.get(0) instanceof String )
        {
            ret = (String[]) values.toArray(new String[values.size()]);
        }
        else if ( values.get(0) instanceof Integer )
        {
            for (int i = 0; i < values.size(); i++)
            {
                ret[i] = values.get(i) + "";
            }
        }
        else if ( values.get(0) instanceof Long )
        {
            for (int i = 0; i < values.size(); i++)
            {
                ret[i] = values.get(i) + "";
            }
        }
        else if ( values.get(0) instanceof Double )
        {
            for (int i = 0; i < values.size(); i++)
            {
                ret[i] = values.get(i) + "";
            }
        }
        else
        {
            throw new OsacbmConversionException(
                    "Trying to convert input to list of String, but input is an incompatible type:"
                            + osaValue.getClass().getName() + " with " + values.get(0).getClass().getName()
                            + " values.");
        }

        return ret;
    }

    /**
     * convert an osa data value to an Boolean time series: if it is a singleton
     * osa data type return a
     * series with a single value. Otherwise the input must be a Values list
     * containing BooleanArrayValue. This method returns null if there are
     * no values to return (as opposed to an empty [][] structure).
     * 
     * @param osaValue
     * @return
     * @throws OsacbmConversionException
     */
    @SuppressWarnings("rawtypes")
    private static Object[][] convertOsaDatatypeToBooleanTimeSeries(DataEvent osaValue)
            throws OsacbmConversionException
    {

        if ( osaValue instanceof DABool || osaValue instanceof DMBool || osaValue instanceof DAInt
                || osaValue instanceof DMInt || osaValue instanceof DMReal || osaValue instanceof DAReal
                || osaValue instanceof DAString )
        {
            // put the singleton value into list
            DABool convertedOsaValue = (DABool) OsaDataTypeConverter.convertToType(osaValue, DABool.class);
            Object ret[][] = new Object[1][2];
            ret[0][1] = Boolean.valueOf(convertedOsaValue.isValue());
            ret[0][0] = Utils.convertOsacbmTimeToDouble(osaValue.getTime());
            return ret;
        }
        else if ( osaValue instanceof DMVector )
        {
            DABool convertedOsaValue = (DABool) OsaDataTypeConverter.convertToType(osaValue, DABool.class);
            Object ret[][] = new Object[1][2];
            ret[0][1] = convertedOsaValue.isValue();
            ret[0][0] = Utils.convertOsacbmTimeToDouble(osaValue.getTime()) + ((DMVector) osaValue).getXValue();
            return ret;

        }
        else
        {
            // get the vector values
            List values = Utils.getValuesList(osaValue);
            if ( values == null || values.size() == 0 )
            {
                return null;
            }

            Double xAxisStart = Utils.getXAxisStart(osaValue);
            List<Double> timeDeltas = Utils.getxAxisDeltasList(osaValue);
            Double timeDelta = null;
            if ( osaValue instanceof DAValueWaveform )
            {
                timeDelta = ((DoubleValue) ((DAValueWaveform) osaValue).getXAxisDelta()).getValue();
            }
            else if ( osaValue instanceof DAWaveform )
            {
                timeDelta = ((DAWaveform) osaValue).getXAxisDelta();
            }

            Double priorTime;
            priorTime = Utils.convertOsacbmTimeToDouble(osaValue.getTime()) + xAxisStart;

            Object[][] ret = new Object[values.size()][2];
            if ( values.get(0) instanceof Boolean )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    ret[i][1] = values.get(i);
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else if ( values.get(0) instanceof Integer )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    ret[i][1] = ((Integer) values.get(i)).equals(1);
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else if ( values.get(0) instanceof Long )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    ret[i][1] = ((Long) values.get(i)).equals(1L);
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else if ( values.get(0) instanceof Double )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    ret[i][1] = ((Double) values.get(i)).equals(1.0);
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else if ( values.get(0) instanceof String )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    ret[i][1] = (String) values.get(i) != null && !((String) values.get(i)).trim().equals(""); //$NON-NLS-1$
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else
            {
                throw new OsacbmConversionException(
                        "Trying to convert input to list of Double, but input is an incompatible type:" //$NON-NLS-1$
                                + osaValue.getClass().getName() + " with " + values.get(0).getClass().getName() //$NON-NLS-1$
                                + " values."); //$NON-NLS-1$
            }

            return ret;

        }
    }

    /**
     * convert an osa data value to an Double time series: if it is a singleton
     * osa data type return a
     * series with a single value. Otherwise the input must be a Values list
     * containing DblArrayValue. This method returns null if there are
     * no values to return (as opposed to an empty [][] structure).
     * 
     * @param osaValue
     * @return
     * @throws OsacbmConversionException
     */
    @SuppressWarnings(
    {
            "rawtypes", "nls"
    })
    private static Double[][] convertOsaDatatypeToDoubleTimeSeries(DataEvent osaValue)
            throws OsacbmConversionException
    {
        if ( osaValue instanceof DABool || osaValue instanceof DMBool || osaValue instanceof DAInt
                || osaValue instanceof DMInt || osaValue instanceof DMReal || osaValue instanceof DAReal
                || osaValue instanceof DAString )
        {
            // put the singleton value into list
            DMReal convertedOsaValue = (DMReal) OsaDataTypeConverter.convertToType(osaValue, DMReal.class);
            Double ret[][] = new Double[1][2];
            ret[0][1] = convertedOsaValue.getValue();
            ret[0][0] = Utils.convertOsacbmTimeToDouble(osaValue.getTime());
            return ret;
        }
        else if ( osaValue instanceof DMVector )
        {
            DMReal convertedOsaValue = (DMReal) OsaDataTypeConverter.convertToType(osaValue, DMReal.class);
            Double ret[][] = new Double[1][2];
            ret[0][1] = convertedOsaValue.getValue();
            ret[0][0] = Utils.convertOsacbmTimeToDouble(osaValue.getTime()) + ((DMVector) osaValue).getXValue();
            return ret;

        }
        else
        {
            // get the vector values
            List values = Utils.getValuesList(osaValue);
            if ( values == null || values.size() == 0 )
            {
                return null;
            }

            Double xAxisStart = Utils.getXAxisStart(osaValue);
            List<Double> timeDeltas = Utils.getxAxisDeltasList(osaValue);
            Double timeDelta = null;
            if ( osaValue instanceof DAValueWaveform )
            {
                timeDelta = ((DoubleValue) ((DAValueWaveform) osaValue).getXAxisDelta()).getValue();
            }
            else if ( osaValue instanceof DAWaveform )
            {
                timeDelta = ((DAWaveform) osaValue).getXAxisDelta();
            }

            Double priorTime = Utils.convertOsacbmTimeToDouble(osaValue.getTime()) + xAxisStart;

            Double[][] ret = new Double[values.size()][2];
            if ( values.get(0) instanceof Boolean )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    if ( (Boolean) values.get(i) )
                    {
                        ret[i][1] = Double.valueOf(1.0);
                    }
                    else
                    {
                        ret[i][1] = Double.valueOf(0.0);
                    }
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else if ( values.get(0) instanceof Integer )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    ret[i][1] = (Integer) values.get(i) + 0.0;
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else if ( values.get(0) instanceof Long )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    ret[i][1] = ((Long) values.get(i)).intValue() + 0.0;
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else if ( values.get(0) instanceof Double )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    ret[i][1] = (Double) values.get(i);
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else if ( values.get(0) instanceof String )
            {

                for (int i = 0; i < values.size(); i++)
                {
                    try
                    {
                        ret[i][1] = Double.parseDouble(((String) values.get(i)).trim()) + 0.0;
                    }
                    catch (NumberFormatException nfe)
                    {
                        throw new OsacbmConversionException(
                                "Trying to convert list of String input to list of Double, but got parse exception:" //$NON-NLS-1$
                                        + osaValue.getClass().getName() + " with " + values.get(0).getClass().getName() //$NON-NLS-1$
                                        + " values.");
                    }
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else
            {
                throw new OsacbmConversionException(
                        "Trying to convert input to list of Double, but input is an incompatible type:" //$NON-NLS-1$
                                + osaValue.getClass().getName() + " with " //$NON-NLS-1$
                                + values.get(0).getClass().getName() + " values."); //$NON-NLS-1$
            }

            return ret;

        }

    }

    /**
     * convert an osa data value to an Integer time series: if it is a singleton
     * osa data type return a
     * series with a single value. Otherwise the input must be a Values list
     * containing IntArrayValue. This method returns null if there are
     * no values to return (as opposed to an empty [][] structure).
     * 
     * @param osaValue
     * @return
     * @throws OsacbmConversionException
     */
    @SuppressWarnings(
    {
            "rawtypes", "nls"
    })
    private static Object[][] convertOsaDatatypeToIntegerTimeSeries(DataEvent osaValue)
            throws OsacbmConversionException
    {

        if ( osaValue instanceof DABool || osaValue instanceof DMBool || osaValue instanceof DAInt
                || osaValue instanceof DMInt || osaValue instanceof DMReal || osaValue instanceof DAReal
                || osaValue instanceof DAString )
        {
            // put the singleton value into list
            DMInt convertedOsaValue = (DMInt) OsaDataTypeConverter.convertToType(osaValue, DMInt.class);
            Object ret[][] = new Object[1][2];
            ret[0][1] = convertedOsaValue.getValue();
            ret[0][0] = Utils.convertOsacbmTimeToDouble(osaValue.getTime());
            return ret;
        }
        else if ( osaValue instanceof DMVector )
        {
            DMInt convertedOsaValue = (DMInt) OsaDataTypeConverter.convertToType(osaValue, DMInt.class);
            Object ret[][] = new Object[1][2];
            ret[0][1] = convertedOsaValue.getValue();
            ret[0][0] = Utils.convertOsacbmTimeToDouble(osaValue.getTime()) + ((DMVector) osaValue).getXValue();
            return ret;

        }
        else
        {
            // get the vector values
            List values = Utils.getValuesList(osaValue);
            if ( values == null || values.size() == 0 )
            {
                return null;
            }

            Double xAxisStart = Utils.getXAxisStart(osaValue);
            List<Double> timeDeltas = Utils.getxAxisDeltasList(osaValue);
            Double timeDelta = null;
            if ( osaValue instanceof DAValueWaveform )
            {
                timeDelta = ((DoubleValue) ((DAValueWaveform) osaValue).getXAxisDelta()).getValue();
            }
            else if ( osaValue instanceof DAWaveform )
            {
                timeDelta = ((DAWaveform) osaValue).getXAxisDelta();
            }

            Double priorTime = Utils.convertOsacbmTimeToDouble(osaValue.getTime()) + xAxisStart;

            Object[][] ret = new Object[values.size()][2];
            if ( values.get(0) instanceof Boolean )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    if ( (Boolean) values.get(i) )
                    {
                        ret[i][1] = Integer.valueOf(1);
                    }
                    else
                    {
                        ret[i][1] = Integer.valueOf(0);
                    }
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else if ( values.get(0) instanceof Integer )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    ret[i][1] = values.get(i);
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else if ( values.get(0) instanceof Long )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    ret[i][1] = ((Long) values.get(i)).intValue();
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else if ( values.get(0) instanceof Double )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    ret[i][1] = Long.valueOf(Math.round((Double) values.get(i))).intValue();
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else if ( values.get(0) instanceof String )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    try
                    {
                        ret[i][1] = Integer.valueOf(Integer.parseInt(((String) values.get(i)).trim()));
                    }
                    catch (NumberFormatException nfe)
                    {
                        throw new OsacbmConversionException(
                                "Trying to convert list of String input to list of Integer, but got parse exception:" //$NON-NLS-1$
                                        + osaValue.getClass().getName() + " with " + values.get(0).getClass().getName() //$NON-NLS-1$
                                        + " values.");
                    }
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else
            {
                throw new OsacbmConversionException(
                        "Trying to convert input to list of Double, but input is an incompatible type:" //$NON-NLS-1$
                                + osaValue.getClass().getName() + " with " + values.get(0).getClass().getName() //$NON-NLS-1$
                                + " values."); //$NON-NLS-1$
            }

            return ret;
        }

    }

    /**
     * convert an osa data value to a String time series: if it is a singleton
     * osa data type return a
     * series with a single value. Otherwise the input must be a Values list
     * containing StringArrayValue. This method returns null if there are
     * no values to return (as opposed to an empty [][] structure).
     * 
     * @param osaValue
     * @return
     * @throws OsacbmConversionException
     */
    @SuppressWarnings(
    {
            "rawtypes", "nls"
    })
    private static Object[][] convertOsaDatatypeToStringTimeSeries(DataEvent osaValue)
            throws OsacbmConversionException
    {
        if ( osaValue instanceof DABool || osaValue instanceof DMBool || osaValue instanceof DAInt
                || osaValue instanceof DMInt || osaValue instanceof DMReal || osaValue instanceof DAReal
                || osaValue instanceof DAString )
        {
            // put the singleton value into list
            DAString convertedOsaValue = (DAString) OsaDataTypeConverter.convertToType(osaValue, DAString.class);
            Object ret[][] = new Object[1][2];
            ret[0][1] = convertedOsaValue.getValue();
            ret[0][0] = Utils.convertOsacbmTimeToDouble(osaValue.getTime());
            return ret;
        }
        else if ( osaValue instanceof DMVector )
        {
            DAString convertedOsaValue = (DAString) OsaDataTypeConverter.convertToType(osaValue, DAString.class);
            Object ret[][] = new Object[1][2];
            ret[0][1] = convertedOsaValue.getValue();
            ret[0][0] = Utils.convertOsacbmTimeToDouble(osaValue.getTime()) + ((DMVector) osaValue).getXValue();
            return ret;

        }
        else
        {
            // get the vector values
            List values = Utils.getValuesList(osaValue);
            if ( values == null || values.size() == 0 )
            {
                return null;
            }

            Double xAxisStart = Utils.getXAxisStart(osaValue);
            List<Double> timeDeltas = Utils.getxAxisDeltasList(osaValue);
            Double timeDelta = null;
            if ( osaValue instanceof DAValueWaveform )
            {
                timeDelta = ((DoubleValue) ((DAValueWaveform) osaValue).getXAxisDelta()).getValue();
            }
            else if ( osaValue instanceof DAWaveform )
            {
                timeDelta = ((DAWaveform) osaValue).getXAxisDelta();
            }

            Double priorTime = Utils.convertOsacbmTimeToDouble(osaValue.getTime()) + xAxisStart;

            Object[][] ret = new Object[values.size()][2];
            if ( values.get(0) instanceof Boolean )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    if ( (Boolean) values.get(i) )
                    {
                        ret[i][1] = "true"; //$NON-NLS-1$
                    }
                    else
                    {
                        ret[i][1] = "false";
                    }
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else if ( values.get(0) instanceof String )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    ret[i][1] = (String) values.get(i) + "";
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else if ( values.get(0) instanceof Integer )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    ret[i][1] = values.get(i) + "";
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else if ( values.get(0) instanceof Long )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    ret[i][1] = values.get(i) + "";
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else if ( values.get(0) instanceof Double )
            {
                for (int i = 0; i < values.size(); i++)
                {
                    ret[i][1] = values.get(i) + "";
                    priorTime = setDeltaTime(timeDeltas, timeDelta, i, priorTime, ret);
                }
            }
            else
            {
                throw new OsacbmConversionException(
                        "Trying to convert input to list of Double, but input is an incompatible type:"
                                + osaValue.getClass().getName() + " with " + values.get(0).getClass().getName()
                                + " values.");
            }

            return ret;
        }
    }

    private static Double setDeltaTime(List<Double> timeDeltas, Double timeDelta, int i, Double priorTime,
            Object ret[][])
    {
        if ( timeDeltas != null )
        {
            ret[i][0] = new Double(priorTime + timeDeltas.get(i));
            priorTime += timeDeltas.get(i);
        }
        else if ( timeDelta != null )
        {
            ret[i][0] = new Double(priorTime + timeDelta);
            priorTime += timeDelta;
        }
        else
        {
            ret[i][0] = 0.0;
        }

        return priorTime;
    }

}
