package com.ge.predix.solsvc.fdh.handler.asset.helper;

import org.mimosa.osacbmv3_3.DADataSeq;
import org.mimosa.osacbmv3_3.DMDataSeq;
import org.mimosa.osacbmv3_3.DataEvent;

/**
 * This class converts DMDataSeq into supported OSA data types
 */
public class DMDataSeqConverter extends DataSeqConverter
{

    private DMDataSeq dmDataSeq;

    /**
     * Initializes the converter
     * 
     * @param dmDataSeq DMDataSeq to convert
     */
    public DMDataSeqConverter(DMDataSeq dmDataSeq)
    {
        this.dmDataSeq = dmDataSeq;
        this.doubleValues = dmDataSeq.getValues();
        this.xAxisDeltas = dmDataSeq.getXAxisDeltas();
        this.xAxisStart = dmDataSeq.getXAxisStart();
    }

    /**
     * Converts DMDataSeq to destination DataEvent type
     * 
     * @param destClassType class of target DataEvent
     * @return converted DataEvent
     */
    @Override
    public DataEvent convertTo(Class<?> destClassType)
            throws OsacbmConversionException
    {
        DataEvent converted;
        if ( destClassType.equals(DADataSeq.class) )
        {
            converted = getDADataSeq();
        }
        else
        {
            converted = super.convertTo(destClassType);
        }
        copyBaseDataEventProperties(this.dmDataSeq, converted, destClassType);
        return converted;
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
        }

        daDataSeq.setXAxisStart(this.xAxisStart);
        if ( this.xAxisDeltas != null )
        {
            daDataSeq.getXAxisDeltas().addAll(this.xAxisDeltas);
        }
        return daDataSeq;
    }

}
