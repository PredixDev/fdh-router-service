package com.ge.predix.solsvc.fdh.handler.asset.helper;

import org.mimosa.osacbmv3_3.DABool;
import org.mimosa.osacbmv3_3.DMBool;
import org.mimosa.osacbmv3_3.DataEvent;

/**
 * This class converts DMBool into supported OSA data types
 */
public class DMBoolConverter extends BoolConverter
{

    private DMBool dmBool = null;

    /**
     * Initializes the converter
     * 
     * @param dmBool DMBool to convert
     */
    public DMBoolConverter(DMBool dmBool)
    {
        this.boolValue = dmBool.isValue();
        this.dmBool = dmBool;
    }

    /**
     * Converts DMBool to destination DataEvent type
     * 
     * @param clazz class of target DataEvent
     * @return converted DataEvent
     */
    @Override
    public DataEvent convertTo(Class<?> clazz)
            throws OsacbmConversionException
    {

        DataEvent converted;

        if ( clazz.equals(DABool.class) )
        {
            converted = getDABool();
        }
        else
        {
            converted = super.convertTo(clazz);
        }

        copyBaseDataEventProperties(this.dmBool, converted, clazz);
        return converted;

    }

    /**
     * Returns DABool
     * 
     * @return converted DABool
     */
    @Override
    protected DABool getDABool()
    {
        DABool daBool = new DABool();
        daBool.setValue(this.boolValue);
        return daBool;
    }

}
