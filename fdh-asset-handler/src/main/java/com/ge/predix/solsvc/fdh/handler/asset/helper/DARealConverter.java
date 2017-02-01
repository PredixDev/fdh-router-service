package com.ge.predix.solsvc.fdh.handler.asset.helper;

import java.util.ArrayList;

import org.mimosa.osacbmv3_3.DADataSeq;
import org.mimosa.osacbmv3_3.DAReal;
import org.mimosa.osacbmv3_3.DAValueDataSeq;
import org.mimosa.osacbmv3_3.DAValueWaveform;
import org.mimosa.osacbmv3_3.DAVector;
import org.mimosa.osacbmv3_3.DAWaveform;
import org.mimosa.osacbmv3_3.DMDataSeq;
import org.mimosa.osacbmv3_3.DMReal;
import org.mimosa.osacbmv3_3.DMVector;
import org.mimosa.osacbmv3_3.DataEvent;
import org.mimosa.osacbmv3_3.DblArrayValue;
import org.mimosa.osacbmv3_3.OsacbmTime;

/**
 * Converter from DAReal into supported OSA data types
 * 
 * @author DSP-PM Development Team
 */
public class DARealConverter extends RealConverter
{

    private DAReal daReal;

    /**
     * Initializes the converter
     * 
     * @param daReal DAReal to convert
     */
    public DARealConverter(DAReal daReal)
    {
        this.daReal = daReal;
        this.doubleValue = daReal.getValue();
    }

    /**
     * Converts DAReal to destination DataEvent type
     * 
     * @param clazz class of target DataEvent
     * @return converted DataEvent
     */
    @Override
    public DataEvent convertTo(Class<?> clazz)
            throws OsacbmConversionException
    {
        DataEvent converted;
        if ( clazz.equals(DMReal.class) )
        {
            converted = getDMReal();
        }
        else
        {
            converted = super.convertTo(clazz);
        }
        setOsaTime(converted);
        copyBaseDataEventProperties(this.daReal, converted, clazz);
        return converted;

    }

    /**
     * Returns DMReal equivalent
     * 
     * @return converted DMReal
     */
    protected DMReal getDMReal()
    {
        DMReal dmReal = new DMReal();
        dmReal.setValue(this.doubleValue);
        return dmReal;
    }

    private void setOsaTime(DataEvent dataEvent)
            throws OsacbmConversionException
    {
        if ( dataEvent instanceof DADataSeq )
        {
            // || dataEvent instanceof DAValueDataSeq
            ((DADataSeq) dataEvent).setXAxisStart(getTimeAsDouble(this.daReal.getTime()));
        }
        if ( dataEvent instanceof DAValueDataSeq )
        {
            // ((DAValueDataSeq) dataEvent).setXAxisStart(getTimeAsDouble(this.daReal.getTime()));
            // (((DblArrayValue) ((DAValueDataSeq) dataEvent).getXAxisDeltas()).getValues()).set(0,
            // getTimeAsDouble(this.daReal.getTime())); FIX ME for delta setting
        }
        if ( dataEvent instanceof DMDataSeq )
        {
            ((DMDataSeq) dataEvent).setXAxisStart(getTimeAsDouble(this.daReal.getTime()));
        }
        if ( dataEvent instanceof DAValueWaveform )

        {
            DblArrayValue deltaValue = new DblArrayValue();
            deltaValue.setValues(new ArrayList<Double>());
            deltaValue.getValues().add(getTimeAsDouble(this.daReal.getTime()));
            ((DAValueWaveform) dataEvent).setXAxisDelta(deltaValue);

        }
        if ( dataEvent instanceof DMDataSeq )
        {
            ((DMDataSeq) dataEvent).setXAxisStart(getTimeAsDouble(this.daReal.getTime()));
        }
        if ( dataEvent instanceof DAVector )
        {
            ((DAVector) dataEvent).setXValue(getTimeAsDouble(this.daReal.getTime()));
        }
        if ( dataEvent instanceof DMVector )
        {
            ((DMVector) dataEvent).setXValue(getTimeAsDouble(this.daReal.getTime()));
        }
        if ( dataEvent instanceof DAWaveform )
        {
            ((DAWaveform) dataEvent).setXAxisDelta(getTimeAsDouble(this.daReal.getTime()));
        }
    }

    /**
     * returns osaTime to double value. this method uses Utils.convertOsacbmTimeToDouble
     * to convert osaCbmTime to double
     * 
     * @param osaTime
     * @return osaCbmTime as a double
     * @throws OsacbmConversionException if OsaCbmTime specified in date has incorrect date format
     */
    @SuppressWarnings({ "nls", "deprecation" })
    private double getTimeAsDouble(OsacbmTime osaTime)
            throws OsacbmConversionException
    {
        Double fromTimestamp = null;
        fromTimestamp = Utils.convertOsacbmTimeToDouble(osaTime);
        return fromTimestamp;
    }

}
