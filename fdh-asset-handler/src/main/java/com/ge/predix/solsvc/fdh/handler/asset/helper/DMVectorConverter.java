package com.ge.predix.solsvc.fdh.handler.asset.helper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.mimosa.osacbmv3_3.DABool;
import org.mimosa.osacbmv3_3.DADataSeq;
import org.mimosa.osacbmv3_3.DAInt;
import org.mimosa.osacbmv3_3.DAReal;
import org.mimosa.osacbmv3_3.DAString;
import org.mimosa.osacbmv3_3.DAValueDataSeq;
import org.mimosa.osacbmv3_3.DAValueWaveform;
import org.mimosa.osacbmv3_3.DAVector;
import org.mimosa.osacbmv3_3.DAWaveform;
import org.mimosa.osacbmv3_3.DMBool;
import org.mimosa.osacbmv3_3.DMDataSeq;
import org.mimosa.osacbmv3_3.DMInt;
import org.mimosa.osacbmv3_3.DMReal;
import org.mimosa.osacbmv3_3.DMVector;
import org.mimosa.osacbmv3_3.DataEvent;
import org.mimosa.osacbmv3_3.DblArrayValue;
import org.mimosa.osacbmv3_3.DoubleValue;
import org.mimosa.osacbmv3_3.OsacbmTime;
import org.mimosa.osacbmv3_3.OsacbmTimeType;
import org.mimosa.osacbmv3_3.Value;

/**
 * This class converts DMVector into supported OSA data types
 */
public class DMVectorConverter extends BaseConverter
{

    private DMVector             dmVector;
    private List<Double> doubleValues;

    /**
     * Initializes the converter
     * 
     * @param dmVector DMVector to convert
     */
    public DMVectorConverter(DMVector dmVector)
    {
        this.dmVector = dmVector;
        this.doubleValues = new ArrayList<Double>();
        this.doubleValues.add(dmVector.getValue());
        this.xAxisStart = dmVector.getXValue();
        this.xAxisDeltas = new ArrayList<Double>();
        this.xAxisDeltas.add(0.0);
    }

    /**
     * Converts DMVector to destination DataEvent type
     * 
     * @param clazz class of target DataEvent
     * @return converted DataEvent
     */
    @Override
    public DataEvent convertTo(Class<?> clazz)
            throws OsacbmConversionException
    {
        DataEvent dataEvent = null;
        if ( clazz.equals(DADataSeq.class) )
        {
            dataEvent = getDADataSeq();
        }
        else if ( clazz.equals(DABool.class) )
        {
            dataEvent = getDABool();
        }
        else if ( clazz.equals(DAInt.class) )
        {
            dataEvent = getDAInt();
        }
        else if ( clazz.equals(DAValueDataSeq.class) )
        {
            dataEvent = getDAValueDataSeq();
        }
        else if ( clazz.equals(DAValueWaveform.class) )
        {
            dataEvent = getDAValueWaveform();
        }
        else if ( clazz.equals(DMDataSeq.class) )
        {
            dataEvent = getDMDataSeq();
        }
        else if ( clazz.equals(DMInt.class) )
        {
            dataEvent = getDMInt();
        }
        else if ( clazz.equals(DMBool.class) )
        {
            dataEvent = getDMBool();
        }
        else if ( clazz.equals(DAVector.class) )
        {
            dataEvent = getDAVector();
        }
        else if ( clazz.equals(DAReal.class) )
        {
            dataEvent = getDAReal();
        }
        else if ( clazz.equals(DAWaveform.class) )
        {
            dataEvent = getDAWaveform();
        }
        else if ( clazz.equals(DMReal.class) )
        {
            dataEvent = getDMReal();
        }
        else if ( clazz.equals(DAString.class) )
        {
            dataEvent = getDAString();
        }
        if ( dataEvent == null )
        {
            throw new OsacbmConversionException("Conversion to " + clazz.getSimpleName() + " is not supported"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        copyBaseDataEventProperties(this.dmVector, dataEvent, clazz);
        setTimestamp(dataEvent);
        return dataEvent;
    }

    /**
     * @return -
     */
    protected DAString getDAString()
    {
        DAString daString = new DAString();
        daString.setValue(Double.toString(this.dmVector.getValue()));
        return daString;
    }

    /**
     * Returns DADataSeq
     * 
     * @return converted DADataSeq
     * @throws OsacbmConversionException conversion error
     */
    protected DADataSeq getDADataSeq()
            throws OsacbmConversionException
    {
        DADataSeq daDataSeq = new DADataSeq();
        if ( this.doubleValues != null )
        {
            daDataSeq.getValues().addAll(this.doubleValues);
            daDataSeq.setXAxisStart(this.xAxisStart);
            if ( this.xAxisDeltas != null )
            {
                daDataSeq.getXAxisDeltas().addAll(this.xAxisDeltas);
            }
        }
        else
        {
            throw new OsacbmConversionException("Converting null dmVector.getValues() to DADataSeq values"); //$NON-NLS-1$
        }
        daDataSeq.setXAxisStart(this.xAxisStart);
        if ( this.xAxisDeltas != null )
        {
            daDataSeq.getXAxisDeltas().addAll(this.xAxisDeltas);
        }
        return daDataSeq;
    }

    /**
     * Returns DABool
     * 
     * @return converted DABool
     * @throws OsacbmConversionException conversion error
     */
    protected DABool getDABool()
            throws OsacbmConversionException
    {
        DABool daBool = new DABool();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                daBool.setValue(this.doubleValues.get(this.doubleValues.size() - 1) != 0.0);
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DABool"); //$NON-NLS-1$
            }
        }
        return daBool;
    }

    /**
     * Returns DMBool
     * 
     * @return converted DMBool
     * @throws OsacbmConversionException conversion error
     */
    protected DMBool getDMBool()
            throws OsacbmConversionException
    {
        DMBool dmBool = new DMBool();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                dmBool.setValue(this.doubleValues.get(this.doubleValues.size() - 1) != 0.0);
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DABool"); //$NON-NLS-1$
            }
        }
        return dmBool;
    }

    /**
     * Returns DAInt
     * 
     * @return converted DAInt
     * @throws OsacbmConversionException conversion error
     */
    protected DAInt getDAInt()
            throws OsacbmConversionException
    {
        DAInt daInt = new DAInt();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                daInt.setValue((new Long(java.lang.Math.round(this.doubleValues.get(this.doubleValues.size() - 1)))).intValue());
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DAInt"); //$NON-NLS-1$
            }
        }
        return daInt;
    }

    /**
     * Returns DAReal
     * 
     * @return convertedDAReal
     * @throws OsacbmConversionException -
     */
    protected DAReal getDAReal()
            throws OsacbmConversionException
    {
        DAReal daReal = new DAReal();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                daReal.setValue(this.doubleValues.get(this.doubleValues.size() - 1));
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DAReal"); //$NON-NLS-1$
            }
        }
        return daReal;
    }

    /**
     * Returns DAValueDataSeq
     * 
     * @return converted DAValueDataSeq
     * @throws OsacbmConversionException conversion error
     */
    protected DAValueDataSeq getDAValueDataSeq()
            throws OsacbmConversionException
    {
        DAValueDataSeq daValueDataSeq = new DAValueDataSeq();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                DblArrayValue toValue = new DblArrayValue();
                daValueDataSeq.setValues(toValue);
                toValue.getValues().addAll((this.doubleValues));
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DAValueDataSeq"); //$NON-NLS-1$
            }
            daValueDataSeq.setXAxisStart(new DoubleValue());
            ((DoubleValue) daValueDataSeq.getXAxisStart()).setValue(this.xAxisStart);
            daValueDataSeq.setXAxisDeltas(new DblArrayValue());
            if ( this.xAxisDeltas != null )
            {
                ((DblArrayValue) daValueDataSeq.getXAxisDeltas()).getValues().addAll(this.xAxisDeltas);
            }
        }
        return daValueDataSeq;
    }

    /**
     * Returns DAVAlueWaveform
     * 
     * @return converted DAValueWaveform
     * @throws OsacbmConversionException conversion error
     */
    protected DAValueWaveform getDAValueWaveform()
            throws OsacbmConversionException
    {
        DAValueWaveform daValueWaveform = new DAValueWaveform();

        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                Value values = new DblArrayValue();
                ((DblArrayValue) values).getValues().addAll(this.doubleValues);
                daValueWaveform.setValues(values);
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DAValueWaveform"); //$NON-NLS-1$
            }
            daValueWaveform.setXAxisStart(new DoubleValue());
            ((DoubleValue) daValueWaveform.getXAxisStart()).setValue(this.xAxisStart);
            daValueWaveform.setXAxisDelta(new DoubleValue());
            ((DoubleValue) daValueWaveform.getXAxisDelta()).setValue(this.xAxisDeltas.get(0));

        }

        return daValueWaveform;

    }

    /**
     * Returns DAWaveform
     * 
     * @return converted DAWaveform
     * @throws OsacbmConversionException conversion error
     */
    protected DAWaveform getDAWaveform()
            throws OsacbmConversionException
    {
        DAWaveform daWaveform = new DAWaveform();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                daWaveform.getValues().addAll(this.doubleValues);

            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DAValueWaveform"); //$NON-NLS-1$
            }
            daWaveform.setXAxisStart(0.0);
            daWaveform.setXAxisDelta(getTimeAsDouble(this.dmVector.getTime()));

        }
        return daWaveform;
        // throw new OsacbmConversionException("Converting data seq to DAValueWaveform, have multiple xAxisDeltas.");
    }

    /**
     * Returns DAVector
     * 
     * @return converted DAVector
     * @throws OsacbmConversionException conversion error
     */
    protected DAVector getDAVector()
            throws OsacbmConversionException
    {
        DAVector daVector = new DAVector();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                daVector.setValue(this.doubleValues.get(this.doubleValues.size() - 1));
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DAVector"); //$NON-NLS-1$
            }
            if ( this.xAxisDeltas != null )
            {
                daVector.setXValue(this.xAxisStart + this.xAxisDeltas.size() - 1);
            }
        }
        return daVector;
    }

    /**
     * Returns DMInt
     * 
     * @return converted DMInt
     * @throws OsacbmConversionException conversion error
     */
    public DMInt getDMInt()
            throws OsacbmConversionException
    {
        DMInt dmInt = new DMInt();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                dmInt.setValue((new Long(java.lang.Math.round(this.doubleValues.get(this.doubleValues.size() - 1)))).intValue());
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DMInt"); //$NON-NLS-1$
            }
        }
        return dmInt;
    }

    /**
     * Returns DMReal
     * 
     * @return converted DMReal
     * @throws OsacbmConversionException conversion error
     */
    public DMReal getDMReal()
            throws OsacbmConversionException
    {
        DMReal dmReal = new DMReal();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                dmReal.setValue(this.doubleValues.get(this.doubleValues.size() - 1));
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DMReal"); //$NON-NLS-1$
            }
        }
        return dmReal;
    }

    /**
     * Returns DMDataSeq
     * 
     * @return converted DMDataSeq
     * @throws OsacbmConversionException conversion error
     */
    public DMDataSeq getDMDataSeq()
            throws OsacbmConversionException
    {
        DMDataSeq dmDataSeq = new DMDataSeq();
        if ( this.doubleValues != null )
        {
            dmDataSeq.getValues().addAll(this.doubleValues);
        }
        dmDataSeq.setXAxisStart(this.xAxisStart);
        if ( this.xAxisDeltas != null )
        {
            dmDataSeq.getXAxisDeltas().addAll(this.xAxisDeltas);
        }
        return dmDataSeq;
    }

    /**
     * returns osaTime to double value. this method uses Utils.convertOsacbmTimeToDouble
     * to convert osaCbmTime to double
     * 
     * @param osaTime
     * @return osaCbmTime as a double
     * @throws OsacbmConversionException if OsaCbmTime specified in date has incorrect date format
     */
    @SuppressWarnings("deprecation")
	private double getTimeAsDouble(OsacbmTime osaTime)
            throws OsacbmConversionException
    {
        Double fromTimestamp = null;
        fromTimestamp = Utils.convertOsacbmTimeToDouble(osaTime);
        return fromTimestamp;
    }

    private void setTimestamp(DataEvent dataEvent)
    {
        if ( dataEvent != null
                && (dataEvent instanceof DAReal || dataEvent instanceof DAString || dataEvent instanceof DMDataSeq
                        || dataEvent instanceof DMInt || dataEvent instanceof DAInt || dataEvent instanceof DMReal) )
        {
            OsacbmTime osaCbmTime = new OsacbmTime();
            osaCbmTime.setTimeBinary(new BigInteger(Double.valueOf(this.dmVector.getXValue()).longValue() + "")); //$NON-NLS-1$
            osaCbmTime.setTimeType(OsacbmTimeType.OSACBM_TIME_POSIX_MSEC_6);
            dataEvent.setTime(osaCbmTime);
        }
    }
}
