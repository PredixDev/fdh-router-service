package com.ge.predix.solsvc.fdh.handler.asset.helper;

import org.mimosa.osacbmv3_3.DAInt;
import org.mimosa.osacbmv3_3.DMInt;
import org.mimosa.osacbmv3_3.DataEvent;


/**
 * This class converts DMInt into supported OSA data types
 */
public class DMIntConverter extends IntConverter
{

    private DMInt dmInt;

    /**
     * Initializes the converter
     * 
     * @param dmInt DMInt to convert
     */
    public DMIntConverter(DMInt dmInt)
    {
        this.intValue = dmInt.getValue();
        this.dmInt = dmInt;
    }

    /**
     * Converts DMInt to destination DataEvent type
     * 
     * @param clazz class of target DataEvent
     * @return converted DataEvent
     */
    @Override
    public DataEvent convertTo(Class<?> clazz)
            throws OsacbmConversionException
    {
        DataEvent converted;
        if ( clazz.equals(DAInt.class) )
        {
            converted = getDAInt();
        }
        else
        {
            converted = super.convertTo(clazz);
        }
        copyBaseDataEventProperties(this.dmInt, converted, clazz);
        return converted;
    }

    /**
     * Returns DAInt
     * 
     * @return converted DAInt
     */
    @Override
    protected DAInt getDAInt()
    {
        DAInt daInt = new DAInt();
        daInt.setValue(this.intValue);
        return daInt;
    }

}
