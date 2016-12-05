package com.ge.predix.solsvc.fdh.handler.asset.helper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mimosa.osacbmv3_3.BooleanArrayValue;
import org.mimosa.osacbmv3_3.DABool;
import org.mimosa.osacbmv3_3.DADataSeq;
import org.mimosa.osacbmv3_3.DAInt;
import org.mimosa.osacbmv3_3.DAReal;
import org.mimosa.osacbmv3_3.DAString;
import org.mimosa.osacbmv3_3.DAValueDataSeq;
import org.mimosa.osacbmv3_3.DAValueWaveform;
import org.mimosa.osacbmv3_3.DAWaveform;
import org.mimosa.osacbmv3_3.DMBool;
import org.mimosa.osacbmv3_3.DMDataSeq;
import org.mimosa.osacbmv3_3.DMInt;
import org.mimosa.osacbmv3_3.DMReal;
import org.mimosa.osacbmv3_3.DataEvent;
import org.mimosa.osacbmv3_3.DblArrayValue;
import org.mimosa.osacbmv3_3.DoubleValue;
import org.mimosa.osacbmv3_3.FloatArrayValue;
import org.mimosa.osacbmv3_3.IntArrayValue;
import org.mimosa.osacbmv3_3.OsacbmTime;
import org.mimosa.osacbmv3_3.OsacbmTimeType;
import org.mimosa.osacbmv3_3.SITECATEGORY;
import org.mimosa.osacbmv3_3.ShortArrayValue;
import org.mimosa.osacbmv3_3.StringArrayValue;
import org.mimosa.osacbmv3_3.Value;

import com.ge.predix.solsvc.fdh.handler.asset.helper.OsaJavaDataTypeConversion.SupportedOsaDataTypes;

/**
 * 
 * @author predix -
 */
public class JavaOsaDataTypeConversion
{

    /**
     * convert one of the supported java data structures to an Osa datatype. The
     * method creates the most directly compatible DataEvent from the input data
     * values, then uses the OSA Conversion utility to convert this to the
     * output data type.
     * 
     * The input can be a singleton: Boolean, Integer, Double, String or a
     * vector: Boolean[], Integer[], Double[], String[] or a timeseries:
     * Boolean[][], Integer[][], Double[][], String[][]
     * 
     * Note if the input is an Object[][] array where the time is in string
     * format, the Osa data structure generated will have time in millisecond
     * format. (This is because we need a non-string format for the deltas, and
     * the code needs to parse the string date to a Date structure and in Java
     * we can only get milliseconds from the date structure.)
     * 
     * @param fromValue
     *            the input value - note we are assuming the time values are the
     *            number of milliseconds from Unix Epoch
     * @param toType
     *            the target datatype
     * @param sourceTimeType
     *            the source type for the input time values
     * @return DataEvent
     * @throws OsacbmConversionException
     *             when error happens in OsaCbmconversion
     */
    @SuppressWarnings("unchecked")
    public static DataEvent convertJavaStructureToDataEvent(Object fromValue, SupportedOsaDataTypes toType,
            OsacbmTimeType sourceTimeType)
            throws OsacbmConversionException
    {

        DataEvent fromDataEvent = null;

        // get singleton DataEvents as the singleton osa structures
        if ( fromValue == null )
        {
            fromDataEvent = createDAString((String) fromValue);
        }
        else if ( fromValue instanceof Boolean )
        {
            fromDataEvent = createDABool((Boolean) fromValue);
        }
        else if ( fromValue instanceof Integer )
        {
            fromDataEvent = createDAInt((Integer) fromValue);
        }
        else if ( fromValue instanceof Double )
        {
            fromDataEvent = createDAReal((Double) fromValue);
        }
        else if ( fromValue instanceof String )
        {
            fromDataEvent = createDAString((String) fromValue);

            // get series data events as a DAValueDataSeq
        }
        else if ( fromValue instanceof Boolean[] )
        {
            BooleanArrayValue value = new BooleanArrayValue();
            value.setValues(Arrays.asList((Boolean[]) fromValue));
            fromDataEvent = createDAValueDataSeq(value);
            createTickTimes((DAValueDataSeq) fromDataEvent, ((Boolean[]) fromValue).length);
        }
        else if ( fromValue instanceof Integer[] )
        {
            IntArrayValue value = new IntArrayValue();
            value.setValues(Arrays.asList((Integer[]) fromValue));
            fromDataEvent = createDAValueDataSeq(value);
            createTickTimes((DAValueDataSeq) fromDataEvent, ((Integer[]) fromValue).length);
        }
        else if ( fromValue instanceof Double[] )
        {
            DblArrayValue value = new DblArrayValue();
            value.setValues(Arrays.asList((Double[]) fromValue));
            fromDataEvent = createDAValueDataSeq(value);
            createTickTimes((DAValueDataSeq) fromDataEvent, ((Double[]) fromValue).length);
        }
        else if ( fromValue instanceof String[] )
        {
            StringArrayValue value = new StringArrayValue();
            value.setValues(Arrays.asList((String[]) fromValue));
            fromDataEvent = createDAValueDataSeq(value);
            createTickTimes((DAValueDataSeq) fromDataEvent, ((String[]) fromValue).length);

            // get the time series data events as a DAValueDataSeq
        }
        else if ( fromValue instanceof Object[][] )
        {

            Object[][] fromVal = (Object[][]) fromValue;

            if ( fromVal[0][1] instanceof Boolean )
            {
                BooleanArrayValue value = new BooleanArrayValue();
                value.setValues(getValues(fromVal));
                fromDataEvent = createDAValueDataSeq(value);
            }
            else if ( fromVal[0][1] instanceof Integer )
            {
                IntArrayValue value = new IntArrayValue();
                value.setValues(getValues(fromVal));
                fromDataEvent = createDAValueDataSeq(value);
            }
            else if ( fromVal[0][1] instanceof Double )
            {
                DblArrayValue value = new DblArrayValue();
                value.setValues(getValues(fromVal));
                fromDataEvent = createDAValueDataSeq(value);
            }
            else if ( fromVal[0][1] instanceof String )
            {
                StringArrayValue value = new StringArrayValue();
                value.setValues(getValues(fromVal));
                fromDataEvent = createDAValueDataSeq(value);
            }
            else if ( fromVal[0][1] instanceof Float )
            {
                FloatArrayValue value = new FloatArrayValue();
                value.setValues(getValues(fromVal));
                fromDataEvent = createDAValueDataSeq(value);
            }
            else if ( fromVal[0][1] instanceof Short )
            {
                // unknown type
                ShortArrayValue value = new ShortArrayValue();
                value.setValues(getValues(fromVal));
                fromDataEvent = createDAValueDataSeq(value);
            }
            else if ( fromVal[0][1] instanceof BigDecimal )
            {
                // We default to Double ...
                DblArrayValue value = new DblArrayValue();
                value.setValues(getValuesAsDouble(fromVal));
                fromDataEvent = createDAValueDataSeq(value);
            }
            else
            {
                // We default to Double ...
                DblArrayValue value = new DblArrayValue();
                value.setValues(getValues(fromVal));
                fromDataEvent = createDAValueDataSeq(value);
            }

            setTimes((DAValueDataSeq) fromDataEvent, fromVal, sourceTimeType);
            fromDataEvent.setSite(Utils.createSiteWithRequiredFields(SITECATEGORY.SITE_SPECIFIC));
        }
        else
        {
            throw new OsacbmConversionException("from type must be java Boolean, Double, String, Integer, " //$NON-NLS-1$
                    + "Boolean[], Double[], String[], Integer[], " //$NON-NLS-1$
                    + "Boolean[][], Double[][], String[][] or Integer[][]" //$NON-NLS-1$
                    + " instead it was:" //$NON-NLS-1$
                    + (fromValue==null ? null : fromValue.getClass().getName()));
        }

        if ( fromDataEvent.getTime() == null )
        {
            OsacbmTime timeObj = new OsacbmTime();
            timeObj.setTimeType(OsacbmTimeType.OSACBM_TIME_SYSTEM_TICK);
            timeObj.setTimeBinary(new BigInteger("0")); //$NON-NLS-1$
            fromDataEvent.setTime(timeObj);
        }

        // now convert the generated DataEvent to the target DataEvent type
        if ( toType == null)
        {
            return fromDataEvent;
        }
        else if ( toType.compareTo(SupportedOsaDataTypes.DABool) == 0 )
        {
            return OsaDataTypeConverter.convertToType(fromDataEvent, DABool.class);
        }
        else if ( toType.compareTo(SupportedOsaDataTypes.DMBool) == 0 )
        {
            return OsaDataTypeConverter.convertToType(fromDataEvent, DMBool.class);
        }
        else if ( toType.compareTo(SupportedOsaDataTypes.DAInt) == 0 )
        {
            return OsaDataTypeConverter.convertToType(fromDataEvent, DAInt.class);
        }
        else if ( toType.compareTo(SupportedOsaDataTypes.DMInt) == 0 )
        {
            return OsaDataTypeConverter.convertToType(fromDataEvent, DMInt.class);
        }
        else if ( toType.compareTo(SupportedOsaDataTypes.DAReal) == 0 )
        {
            return OsaDataTypeConverter.convertToType(fromDataEvent, DAReal.class);
        }
        else if ( toType.compareTo(SupportedOsaDataTypes.DAString) == 0 )
        {
            return OsaDataTypeConverter.convertToType(fromDataEvent, DAString.class);
        }
        else if ( toType.compareTo(SupportedOsaDataTypes.DMReal) == 0 )
        {
            return OsaDataTypeConverter.convertToType(fromDataEvent, DMReal.class);
        }
        else if ( toType.compareTo(SupportedOsaDataTypes.DMVector) == 0 )
        {
            return OsaDataTypeConverter.convertToType(fromDataEvent, DAReal.class);
        }
        else if ( toType.compareTo(SupportedOsaDataTypes.DADataSeq) == 0 )
        {
            return OsaDataTypeConverter.convertToType(fromDataEvent, DADataSeq.class);
        }
        else if ( toType.compareTo(SupportedOsaDataTypes.DAValueDataSeq) == 0 )
        {
            return OsaDataTypeConverter.convertToType(fromDataEvent, DAValueDataSeq.class);
        }
        else if ( toType.compareTo(SupportedOsaDataTypes.DAValueWaveform) == 0 )
        {
            adjustFirstDeltaForWaveform(fromDataEvent);
            return OsaDataTypeConverter.convertToType(fromDataEvent, DAValueWaveform.class);
        }
        else if ( toType.compareTo(SupportedOsaDataTypes.DAWaveform) == 0 )
        {
            adjustFirstDeltaForWaveform(fromDataEvent);
            return OsaDataTypeConverter.convertToType(fromDataEvent, DAWaveform.class);
        }
        else if ( toType.compareTo(SupportedOsaDataTypes.DMDataSeq) == 0 )
        {
            return OsaDataTypeConverter.convertToType(fromDataEvent, DMDataSeq.class);
        }

        return null;

    }

    /**
     * the dataEvent is going to be converted to a waveform value. Change the
     * first delta so it matches the second one.
     * 
     * @param dataEvent
     */
    private static void adjustFirstDeltaForWaveform(DataEvent dataEvent)
    {
        if ( !(dataEvent instanceof DAValueDataSeq) || ((DAValueDataSeq) dataEvent).getXAxisDeltas() == null )
        {
            return;
        }
        DblArrayValue deltaValues = (DblArrayValue) ((DAValueDataSeq) dataEvent).getXAxisDeltas();

        List<Double> deltas = deltaValues.getValues();

        if ( deltas.size() > 1 )
        {
            Double delta = deltas.get(1);
            DoubleValue start = (DoubleValue) ((DAValueDataSeq) dataEvent).getXAxisStart();
            start.setValue(start.getValue() + (deltas.get(0) - delta));
            deltas.set(0, delta);
        }

    }

    /**
     * get the 2nd entry from each row as the data point value (as opposed to
     * the data point's time) build a list of the data point values
     * 
     * @param inValues
     * @return
     */
    @SuppressWarnings(
    {
            "rawtypes", "unchecked"
    })
    private static List getValues(Object[][] inValues)
    {
        List ret = new ArrayList();
        for (int i = 0; i < inValues.length; i++)
        {
            ret.add(inValues[i][1]);
        }

        return ret;
    }

    private static void createTickTimes(DAValueDataSeq dataEvent, int size)
    {
        DblArrayValue times = new DblArrayValue();
        dataEvent.setXAxisDeltas(times);
        times.setValues(new ArrayList<Double>());

        DoubleValue startValue = new DoubleValue();
        startValue.setValue(Double.valueOf(0.));
        dataEvent.setXAxisStart(startValue);
        
        if (size > 0) {
            times.getValues().add(Double.valueOf(0.0));
        }

        for (int i = 1; i < size; i++)
        {
            Double timeValue = Double.valueOf(1.0);
            times.getValues().add(timeValue);
        }

    }

    /**
     * convert the 1st entry in each row to a delta time from the prior row's
     * time. We assume that the time values in each row are the number of
     * milliseconds from Unix epoch (Note: not delta from prior value)
     * 
     * @param dataEvent
     * @param fromVals
     * @throws OsacbmConversionException
     */
    private static void setTimes(DAValueDataSeq dataEvent, Object[][] fromVals, OsacbmTimeType sourceTimeType)
            throws OsacbmConversionException
    {
        OsacbmTime startTime = new OsacbmTime();
        startTime.setTimeType(sourceTimeType);
        dataEvent.setTime(startTime);

        DoubleValue xAxisStart = new DoubleValue();
        xAxisStart.setValue(0.0);
        dataEvent.setXAxisStart(xAxisStart);
        DblArrayValue xAxisDeltas = new DblArrayValue();
        dataEvent.setXAxisDeltas(xAxisDeltas);
        xAxisDeltas.setValues(new ArrayList<Double>());
        List<Double> deltas = xAxisDeltas.getValues();

        if ( sourceTimeType.compareTo(OsacbmTimeType.OSACBM_TIME_MIMOSA) == 0 )
        {
            // note since we have a vector we cannot have the time type be
            // OSACBM_TIME_MIMOSA (we need units for
            // the delta x values). Java only gives us milliseconds for time
            // strings so we
            // will use milliseconds
            startTime.setTime(null);
            startTime.setTimeBinary(new BigInteger("0")); //$NON-NLS-1$
            startTime.setTimeType(OsacbmTimeType.OSACBM_TIME_POSIX_MSEC_6);
            long priorTime = 0L;
            if ( fromVals[0][0] instanceof String )
            {
                for (int i = 0; i < fromVals.length; i++)
                {
                    try
                    {
                        long timeInMills = Utils.getIsoFormat().parse((String) fromVals[i][0]).getTime();
                        if ( i == 0 )
                        {
                            deltas.add(timeInMills + 0.0);
                        }
                        else
                        {
                            deltas.add(timeInMills - priorTime + 0.0);
                        }
                        priorTime = timeInMills;
                    }
                    catch (ParseException e)
                    {
                        throw new OsacbmConversionException(
                                "Trying to generate time deltas from the input but the time value on entry " + i //$NON-NLS-1$
                                        + " is not a valid iso time.  It is " + fromVals[i][0]); //$NON-NLS-1$
                    }
                }

                return;

            }

        }
        else
        {
            startTime.setTimeBinary(new BigInteger("0")); //$NON-NLS-1$
            for (int i = 0; i < fromVals.length; i++)
            {
                if ( i == 0 )
                {
                    deltas.add((Double) fromVals[i][0] + 0.0);
                }
                else
                {
                    deltas.add((Double) fromVals[i][0] - (Double) fromVals[i - 1][0] + 0.0);
                }
            }
            return;
        }

        throw new OsacbmConversionException(
                "Trying to generate time deltas from the input but the time value on entry " + 0 //$NON-NLS-1$
                        + " is not a String time.  It is " + fromVals[0][0].getClass().getName()); //$NON-NLS-1$
    }

    private static DataEvent createDAValueDataSeq(Value value)
    {
        DAValueDataSeq ret = new DAValueDataSeq();
        ret.setValues(value);
        return ret;
    }

    private static DABool createDABool(Boolean fromValue)
    {
        DABool ret = new DABool();
        ret.setValue(fromValue);
        return ret;
    }

    private static DAInt createDAInt(Integer fromValue)
    {
        DAInt ret = new DAInt();
        ret.setValue(fromValue);
        return ret;
    }

    private static DAReal createDAReal(Double fromValue)
    {
        DAReal ret = new DAReal();
        ret.setValue(fromValue);
        return ret;
    }

    private static DAString createDAString(String fromValue)
    {
        DAString ret = new DAString();
        ret.setValue(fromValue);
        return ret;
    }

    /**
     * get the 2nd entry from each row as the data point value (as opposed to
     * the data point's time) build a list of the data point values. The second element
     * is BigDecimal type, it converts it into Double type
     * 
     * @param inValues of 2 dim objects
     * @return List of Double  
     */

    private static List<Double> getValuesAsDouble(Object[][] inValues)
    {
        List<Double> ret = new ArrayList<Double>();
        for (int i = 0; i < inValues.length; i++)
        {
            Object obj = inValues[i][1];
            if ( obj != null )
            {
                ret.add(((BigDecimal) obj).doubleValue());
            }
            else
            {
                ret.add(null);
            }

        }

        return ret;
    }
}
