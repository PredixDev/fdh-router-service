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

/**
 * This class converts DAVector into supported OSA data types
 */
@SuppressWarnings("nls")
public class DAVectorConverter extends BaseConverter
{

    private double   doubleValue;
    private DAVector daVector;

    /**
     * Initializes the converter
     * 
     * @param daVector DAVector to convert
     */
    public DAVectorConverter(DAVector daVector)
    {
        this.daVector = daVector;
        this.doubleValue = daVector.getValue();
    }

    /**
     * Converts DAVector to destination DataEvent type
     * 
     * @param clazz class of target DataEvent
     * @return converted DataEvent
     */
    @Override
    public DataEvent convertTo(Class<?> clazz)
            throws OsacbmConversionException
    {
        DataEvent dataEvent = null;
        if ( clazz.equals(DABool.class) )
        {
            dataEvent = getDABool();
        }
        else if ( clazz.equals(DAInt.class) )
        {
            dataEvent = getDAInt();
        }
        else if ( clazz.equals(DMBool.class) )
        {
            dataEvent = getDMBool();
        }
        else if ( clazz.equals(DAReal.class) )
        {
            dataEvent = getDAReal();
        }
        else if ( clazz.equals(DMReal.class) )
        {
            dataEvent = getDMReal();
        }
        else if ( clazz.equals(DMInt.class) )
        {
            dataEvent = getDMInt();
        }
        else if ( clazz.equals(DAString.class) )
        {
            dataEvent = getDAString();
        }
        else if ( clazz.equals(DAValueDataSeq.class) )
        {
            dataEvent = getDAValueDataSeq();
        }
        else if ( clazz.equals(DAValueWaveform.class) )
        {
            dataEvent = getDAValueWaveform();
        }
        else if ( clazz.equals(DAWaveform.class) )
        {
            dataEvent = getDAWaveform();
        }
        else if ( clazz.equals(DMDataSeq.class) )
        {
            dataEvent = getDMDataSeq();
        }
        else if ( clazz.equals(DMVector.class) )
        {
            dataEvent = getDMVector();
        }
        else if ( clazz.equals(DAVector.class) )
        {
            dataEvent = getDAVector();
        }
        else if ( clazz.equals(DADataSeq.class) )
        {
            dataEvent = getDADataSeq();
        }
        if ( dataEvent == null )
        {
            throw new OsacbmConversionException("Conversion to " + clazz.getSimpleName() + " is not supported");
        }
        copyBaseDataEventProperties(this.daVector, dataEvent, clazz);
        setTimestamp(dataEvent);
        return dataEvent;
    }

    private DataEvent getDADataSeq(){
        DADataSeq daDataSeq = new DADataSeq();
               
        daDataSeq.getValues().add(this.doubleValue);
        
        return daDataSeq;
    }
    /**
     * @return
     */
    private DataEvent getDMVector()
    {
        DMVector dmVector = new DMVector();
        dmVector.setValue(this.daVector.getValue());
        dmVector.setXValue(this.daVector.getXValue());
        return dmVector;
    }

    /**
     * @return
     */
    private DataEvent getDAVector()
    {
        DAVector daDataEvent = new DAVector();
        daDataEvent.setValue(this.daVector.getValue());
        daDataEvent.setXValue(this.daVector.getXValue());
        daDataEvent.setTime(this.daVector.getTime());
        daDataEvent.setSite(this.daVector.getSite());
        return daDataEvent;
    }

    /**
     * @return
     */
    private DataEvent getDMDataSeq()
    {
        DMDataSeq dmDataSeq = new DMDataSeq();
        dmDataSeq.getValues().add(this.doubleValue);
        return dmDataSeq;
    }

    /**
     * @return
     */
    private DataEvent getDAWaveform()
    {
        DAWaveform daWaveform = new DAWaveform();
        daWaveform.getValues().add(this.doubleValue);
        daWaveform.setXAxisStart(this.daVector.getXValue());
        return daWaveform;
    }

    /**
     * @return
     */
    private DataEvent getDAValueWaveform()
    {
        DAValueWaveform daValueWaveform = new DAValueWaveform();
        DblArrayValue value = new DblArrayValue();
        List<Double> values = new ArrayList<Double>();
        values.add(this.doubleValue);
        value.setValues(values);
        daValueWaveform.setValues(value);

        DoubleValue localXAxisStart = new DoubleValue();
        localXAxisStart.setValue(this.daVector.getXValue());
        daValueWaveform.setXAxisStart(localXAxisStart);
        return daValueWaveform;
    }

    /**
     * @return
     */
    private DataEvent getDAValueDataSeq()
    {
        DAValueDataSeq daValueDataSeq = new DAValueDataSeq();
        DblArrayValue value = new DblArrayValue();
        List<Double> values = new ArrayList<Double>();
        values.add(this.doubleValue);
        value.setValues(values);
        daValueDataSeq.setValues(value);

        DblArrayValue xAxisvalue = new DblArrayValue();
        List<Double> xAxisvalues = new ArrayList<Double>();
        xAxisvalues.add(this.daVector.getXValue());
        xAxisvalue.setValues(xAxisvalues);
        daValueDataSeq.setXAxisDeltas(xAxisvalue);

        return daValueDataSeq;
    }

    /**
     * @return
     */
    private DataEvent getDAString()
    {
        DAString dataEvent = new DAString();
        dataEvent.setValue(this.doubleValue + "");
        return dataEvent;
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
        daInt.setValue((new Long(java.lang.Math.round(this.doubleValue))).intValue());
        return daInt;
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
        daBool.setValue(this.doubleValue != 0.0);
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
        dmBool.setValue(this.doubleValue != 0.0);
        return dmBool;
    }

    /**
     * Returns DAReal
     * 
     * @return converted DAReal
     * @throws OsacbmConversionException conversion error
     */
    protected DAReal getDAReal()
            throws OsacbmConversionException
    {
        DAReal daReal = new DAReal();
        daReal.setValue(this.doubleValue);
        return daReal;
    }

    /**
     * Returns DMReal
     * 
     * @return converted DAReal
     * @throws OsacbmConversionException conversion error
     */
    protected DMReal getDMReal()
            throws OsacbmConversionException
    {
        DMReal dmReal = new DMReal();
        dmReal.setValue(this.doubleValue);
        return dmReal;
    }

    /**
     * Returns DMInt
     * 
     * @return convert DMInt
     * @throws OsacbmConversionException conversion error
     */
    protected DMInt getDMInt()
            throws OsacbmConversionException
    {
        DMInt dmInt = new DMInt();
        dmInt.setValue((new Long(java.lang.Math.round(this.doubleValue))).intValue());
        return dmInt;
    }

    private void setTimestamp(DataEvent dataEvent)
    {
        if ( dataEvent != null
                && (dataEvent instanceof DAReal || dataEvent instanceof DAString || dataEvent instanceof DMDataSeq
                        || dataEvent instanceof DMInt || dataEvent instanceof DAInt || dataEvent instanceof DMReal) )
        {
            OsacbmTime osaCbmTime = new OsacbmTime();
            osaCbmTime.setTimeBinary(new BigInteger(Double.valueOf(this.daVector.getXValue()).longValue() + ""));
            osaCbmTime.setTimeType(OsacbmTimeType.OSACBM_TIME_POSIX_MSEC_6);
            dataEvent.setTime(osaCbmTime);
        }
    }

}
