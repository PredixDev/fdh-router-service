package com.ge.predix.solsvc.fdh.handler.asset.helper;

import org.mimosa.osacbmv3_3.DADataSeq;
import org.mimosa.osacbmv3_3.DMDataSeq;
import org.mimosa.osacbmv3_3.DataEvent;

/**
 * Converter from DADataSeq into supported OSA data types
 * 
 * @author DSP-PM Development Team
 */
public class DADataSeqConverter extends DataSeqConverter
{

    /**
     * DADataSeq DataEvent to convert to other dataEvent type
     */
    DADataSeq daDataSeq;

    /**
     * Initializes the converter
     * 
     * @param daDataSeq DADataSeq to convert
     */
    public DADataSeqConverter(DADataSeq daDataSeq)
    {
        this.daDataSeq = daDataSeq;
        this.xAxisStart = daDataSeq.getXAxisStart();
        this.doubleValues = daDataSeq.getValues();
        this.xAxisDeltas = daDataSeq.getXAxisDeltas();
    }

    /**
     * Converts DABool to destination DataEvent type
     * 
     * @param destClassType class of target DataEvent
     * @return converted DataEvent
     * @throws OsacbmConversionException conversion error
     */
    @Override
    public DataEvent convertTo(Class<?> destClassType)
            throws OsacbmConversionException
    {
        DataEvent converted;
        if ( destClassType.equals(DMDataSeq.class) )
        {
            converted = getDMDataSeq();
        }
        else
        {
            converted = super.convertTo(destClassType);
        }
        copyBaseDataEventProperties(this.daDataSeq, converted, destClassType);
        return converted;
    }

    /**
     * Returns DMDataSeq
     * 
     * @return DMDataSeq converted from DADataSeq
     * @throws OsacbmConversionException conversion error
     */
    protected DMDataSeq getDMDataSeq()
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

}
