package com.ge.predix.solsvc.fdh.handler.asset.helper;

import org.mimosa.osacbmv3_3.DABool;
import org.mimosa.osacbmv3_3.DMBool;
import org.mimosa.osacbmv3_3.DataEvent;

/**
 * Converter from DABool into supported OSA data types
 * 
 * @author DSP-PM Development Team
 */
public class DABoolConverter extends BoolConverter
{

    /**
     * DataEvent to convert to
     */
    DABool daBool;

    /**
     * Initializes the converter
     * 
     * @param daBool
     *            DABool to convert
     */
    public DABoolConverter(DABool daBool)
    {
        this.daBool = daBool;
        this.boolValue = daBool.isValue();
    }

    /**
     * Converts DABool to destination DataEvent type
     * 
     * @param clazz
     *            dataEvent Class
     * @return converted DataEvent
     * @throws OsacbmConversionException -
     */
    @Override
    public DataEvent convertTo(Class<?> clazz)
            throws OsacbmConversionException
    {
        DataEvent converted;

        if ( clazz.equals(DMBool.class) )
        {
            converted = getDMBool();
        }
        else
        {
            converted = super.convertTo(clazz);
        }

        copyBaseDataEventProperties(this.daBool, converted, clazz);

        return converted;
    }

    /**
     * Returns DMBool DataEvent
     * 
     * @return DMBool dataEvent
     */
    @Override
    protected DMBool getDMBool()
    {
        DMBool dmBool = new DMBool();
        dmBool.setValue(this.boolValue);
        return dmBool;
    }

}
