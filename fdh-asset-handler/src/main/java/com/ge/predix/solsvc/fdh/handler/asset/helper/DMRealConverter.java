package com.ge.predix.solsvc.fdh.handler.asset.helper;

import java.util.ArrayList;

import org.mimosa.osacbmv3_3.DADataSeq;
import org.mimosa.osacbmv3_3.DAInt;
import org.mimosa.osacbmv3_3.DAReal;
import org.mimosa.osacbmv3_3.DAValueDataSeq;
import org.mimosa.osacbmv3_3.DAValueWaveform;
import org.mimosa.osacbmv3_3.DAVector;
import org.mimosa.osacbmv3_3.DAWaveform;
import org.mimosa.osacbmv3_3.DMBool;
import org.mimosa.osacbmv3_3.DMDataSeq;
import org.mimosa.osacbmv3_3.DMReal;
import org.mimosa.osacbmv3_3.DMVector;
import org.mimosa.osacbmv3_3.DataEvent;
import org.mimosa.osacbmv3_3.DblArrayValue;
import org.mimosa.osacbmv3_3.DoubleValue;
import org.mimosa.osacbmv3_3.OsacbmTime;
import org.mimosa.osacbmv3_3.Value;

/**
 * This class converts DMReal into supported OSA data types
 */
public class DMRealConverter extends RealConverter
{

    private DMReal dmReal;

    /**
     * Initializes the converter
     * 
     * @param dmReal DMReal to convert
     */
    public DMRealConverter(DMReal dmReal)
    {
        this.dmReal = dmReal;
        this.doubleValue = dmReal.getValue();
    }

    /**
     * Converts DMReal to destination DataEvent type
     * 
     * @param clazz class of target DataEvent
     * @return converted DataEvent
     */
    @Override
    public DataEvent convertTo(Class<?> clazz)
            throws OsacbmConversionException
    {
        DataEvent converted;
        if ( clazz.equals(DAReal.class) )
        {
            converted = getDAReal();
        }
        else if ( clazz.equals(DMDataSeq.class) )
        {
            converted = getDMDataSeq();
        }
        else if ( clazz.equals(DAValueDataSeq.class) )
        {
            converted = getDAValueDataSeq();
        }
        else if ( clazz.equals(DAValueWaveform.class) )
        {
            converted = getDAValueWaveform();
        }
        else if ( clazz.equals(DAVector.class) )
        {
            converted = getDAVector();
        }
        else if ( clazz.equals(DAWaveform.class) )
        {
            converted = getDAWaveform();
        }
        else if ( clazz.equals(DMBool.class) )
        {
            converted = getDMBool();
        }
        else if ( clazz.equals(DAInt.class) )
        {
            converted = getDAInt();
        }
        else if ( clazz.equals(DMReal.class) )
        {
            converted = getDMReal();
        }
        else if ( clazz.equals(DMVector.class) )
        {
            converted = getDMVector();

        }
        else
        {
            converted = super.convertTo(clazz);
        }
        setOsaTime( converted);
        copyBaseDataEventProperties(this.dmReal, converted, clazz);
        return converted;

    }

    /**
     * Returns DAReal
     * 
     * @return converted DAReal
     */
    protected DAReal getDAReal()
    {
        DAReal dmreal = new DAReal();
        dmreal.setValue(this.doubleValue);
        return dmreal;
    }

    /**
     * Returns DAReaDMDataSeq
     * 
     * @return converted DMDataSeq
     */
    protected DMDataSeq getDMDataSeq()
    {
        DMDataSeq dmDataSeq = new DMDataSeq();
        if ( this.dmReal.getTime() != null )
        {
            dmDataSeq.setXAxisStart(this.dmReal.getTime().getTimeBinary().doubleValue());
        }
        dmDataSeq.setValues(new ArrayList<Double>());
        dmDataSeq.getValues().add(this.dmReal.getValue());
        dmDataSeq.setXAxisDeltas(new ArrayList<Double>());
        dmDataSeq.getXAxisDeltas().add(0.0);
        return dmDataSeq;
    }

    /**
     * converts DAreal to DAValueDataSeq
     * 
     * @return converted DAValueDataSeq
     * @throws OsacbmConversionException if OsaCbmTime specified in date has incorrect date format.
     */
    protected DAValueDataSeq getDAValueDataSeq()
            throws OsacbmConversionException
    {
        DAValueDataSeq daValueDataSeq = new DAValueDataSeq();
        Value values = new DblArrayValue();
        ((DblArrayValue) values).getValues().add(this.dmReal.getValue());
        daValueDataSeq.setValues(values);
        if ( this.dmReal.getTime() != null )
        {
            Value toxAxisDeltas = new DblArrayValue();
            Double fromTimestamp = getTimeAsDouble(this.dmReal.getTime());
            ((DblArrayValue) toxAxisDeltas).getValues().add(fromTimestamp);
            daValueDataSeq.setXAxisDeltas(toxAxisDeltas);
            daValueDataSeq.setXAxisStart(new DoubleValue());
            ((DoubleValue) daValueDataSeq.getXAxisStart()).setValue(0.0);
        }
        return daValueDataSeq;
    }

    /**
     * converts DAreal to DAValueWaveform
     * 
     * @return converted DAValueWaveform
     * @throws OsacbmConversionException if OsaCbmTime specified in date has incorrect date format.
     */
    protected DAValueWaveform getDAValueWaveform()
            throws OsacbmConversionException
    {
        DAValueWaveform daValueWaveform = new DAValueWaveform();
        Value values = new DblArrayValue();
        ((DblArrayValue) values).getValues().add(this.dmReal.getValue());
        daValueWaveform.setValues(values);
        if ( this.dmReal.getTime() != null )
        {
            Value toxAxisDeltas = new DblArrayValue();
            Double fromTimestamp = getTimeAsDouble(this.dmReal.getTime());
            ((DblArrayValue) toxAxisDeltas).getValues().add(fromTimestamp);
            daValueWaveform.setXAxisDelta(toxAxisDeltas);
            daValueWaveform.setXAxisStart(new DoubleValue());
            ((DoubleValue) daValueWaveform.getXAxisStart()).setValue(0.0);
        }
        return daValueWaveform;
    }

    /**
     * converts DAreal to DAVector
     * 
     * @return converted DAVector
     * @throws OsacbmConversionException if OsaCbmTime specified in date has incorrect date format.
     */
    protected DAVector getDAVector()
            throws OsacbmConversionException
    {
        DAVector daVector = new DAVector();
        daVector.setValue(this.dmReal.getValue());
        daVector.setXValue(getTimeAsDouble(this.dmReal.getTime()));
        return daVector;
    }

    /**
     * converts DAreal to DAWaveform
     * 
     * @return converted DAWaveform
     * @throws OsacbmConversionException if OsaCbmTime specified in date has incorrect date format.
     */
    protected DAWaveform getDAWaveform()
            throws OsacbmConversionException
    {
        DAWaveform daWaveform = new DAWaveform();
        daWaveform.getValues().add(this.dmReal.getValue());
        daWaveform.setXAxisDelta(getTimeAsDouble(this.dmReal.getTime()));
        daWaveform.setXAxisStart(0.0);
        return daWaveform;
    }

    /**
     * returns osaTime to double value. this method uses Utils.convertOsacbmTimeToDouble
     * to convert osaCbmTime to double
     * 
     * @param osaTime
     * @return osaCbmTime as a double
     * @throws OsacbmConversionException if OsaCbmTime specified in date has incorrect date format
     */
    @SuppressWarnings("nls")
    private double getTimeAsDouble(OsacbmTime osaTime)
            throws OsacbmConversionException
    {
        Double fromTimestamp = null;
        fromTimestamp = Utils.convertOsacbmTimeToDouble(osaTime);
        return fromTimestamp;
    }

    /**
     * Returns DMReal
     * 
     * @return converted DAReal
     */
    protected DMReal getDMReal()
    {
        DMReal dmreal = new DMReal();
        dmreal.setValue(this.doubleValue);
        dmreal.setTime(this.dmReal.getTime());
        return dmreal;
    }

    /**
     * converts DAreal to DMVector
     * 
     * @return converted DMVector
     * @throws OsacbmConversionException if OsaCbmTime specified in date has incorrect date format.
     */
    protected DMVector getDMVector()
            throws OsacbmConversionException
    {
        DMVector dmVector = new DMVector();
        dmVector.setValue(this.dmReal.getValue());
        dmVector.setXValue(getTimeAsDouble(this.dmReal.getTime()));
        return dmVector;
    }
    
    private void setOsaTime( DataEvent dataEvent) throws OsacbmConversionException{
        if( dataEvent instanceof DADataSeq){
             ((DADataSeq) dataEvent).setXAxisStart( getTimeAsDouble( this.dmReal.getTime()));
        }
    }
}
