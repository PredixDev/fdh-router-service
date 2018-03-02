package com.ge.predix.solsvc.fdh.handler.asset.helper;

import org.mimosa.osacbmv3_3.DAInt;
import org.mimosa.osacbmv3_3.DMInt;
import org.mimosa.osacbmv3_3.DataEvent;


/**
 * Converter from DAInt into supported OSA data types
 * 
 * @author DSP-PM Development Team
 */
public class DAIntConverter extends IntConverter
{

    /**
     * DAInt DataEvent to convert to other dataEvent type
     */
    DAInt daInt;

    /**
     * Initializes the converter
     * 
     * @param daInt DAInt to convert
     */
    public DAIntConverter(DAInt daInt)
    {
        this.daInt = daInt;
        this.intValue = daInt.getValue();
    }

    /**
     * Converts DAInt to destination DataEvent type
     * 
     * @param clazz class of target DataEvent
     * @return converted DataEvent
     * @throws OsacbmConversionException conversion error
     */
    @Override
    public DataEvent convertTo(Class<?> clazz)
            throws OsacbmConversionException
    {
        DataEvent converted;
        if ( clazz.equals(DMInt.class) )
        {
            converted = getDMInt();
        }
        else
        {
            converted = super.convertTo(clazz);
        }
        copyBaseDataEventProperties(this.daInt, converted, clazz);
        return converted;
    }

    /**
     * Returns DMInt
     * 
     * @return DMInt converted from DAInt
     */
    @Override
    protected DMInt getDMInt()
    {
        DMInt dmInt = new DMInt();
        dmInt.setValue(this.intValue);
        return dmInt;
    }

}
