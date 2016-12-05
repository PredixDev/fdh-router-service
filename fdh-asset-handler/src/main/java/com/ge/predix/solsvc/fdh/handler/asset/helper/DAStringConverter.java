package com.ge.predix.solsvc.fdh.handler.asset.helper;

import org.mimosa.osacbmv3_3.DABool;
import org.mimosa.osacbmv3_3.DADataSeq;
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
import org.mimosa.osacbmv3_3.StringArrayValue;

/**
 * Converter from DAReal into supported OSA data types
 * 
 * @author DSP-PM Development Team
 */
public class DAStringConverter extends BaseConverter
{

    private DAString daString;
    private String   stringValue;

    /**
     * Initializes the converter
     * 
     * @param daString -
     */
    public DAStringConverter(DAString daString)
    {
        this.daString = daString;
        this.stringValue = daString.getValue();
    }

    /**
     * Converts DMReal/DAReal to destination DataEvent type
     * 
     * @param clazz class of target DataEvent
     * @return converted DataEvent
     */
    @SuppressWarnings(
    {
            "rawtypes", "nls"
    })
    @Override
    public DataEvent convertTo(Class clazz)
            throws OsacbmConversionException
    {
        DataEvent converted = null;
        if ( clazz.equals(DAValueDataSeq.class) )
        {
            converted = getDAValueDataSeq();
            // throw new OsacbmConversionException("Conversion from DAString to DAValueDataSeq is not allowed");
        }
        if ( clazz.equals(DAValueWaveform.class) )
        {
            // throw new OsacbmConversionException("Conversion from DAString to DAValueWaveform is not allowed");
            converted = getDAValueWaveform();
        }
        if ( clazz.equals(DAString.class) )
        {
            converted = getDAString();
        }
        if ( clazz.equals(DAReal.class) )
        {
            throw new OsacbmConversionException("Conversion from DAString to DAReal is not allowed");

        }
        if ( clazz.equals(DAVector.class) )
        {
            // converted = getDAVector();
            throw new OsacbmConversionException("Conversion from DAString to DAVector is not allowed");
        }
        if ( clazz.equals(DAWaveform.class) )
        {
            throw new OsacbmConversionException("Conversion from DAString to DAWaveform is not allowed");
            // converted = getDAWaveform();
        }
        if ( clazz.equals(DMBool.class) )
        {
            converted = getDMBool();
        }
        if ( clazz.equals(DMDataSeq.class) )
        {
            throw new OsacbmConversionException("Conversion from DAString to DMDataSeq is not allowed");
            // converted = getDMDataSeq();
        }
        if ( clazz.equals(DMInt.class) )
        {
            // converted = getDMInt();
            throw new OsacbmConversionException("Conversion from DAString to DMInt is not allowed");
        }
        if ( clazz.equals(DMReal.class) )
        {
            converted = getDMReal();
            // converted = getDMReal();
        }
        if ( clazz.equals(DMVector.class) )
        {
            throw new OsacbmConversionException("Conversion from DAString to DMVector is not allowed");
            // converted = getDMVector();
        }
        if ( clazz.equals(DABool.class) )
        {
            converted = getDABool();
        }
        if ( clazz.equals(DADataSeq.class) )
        {
            throw new OsacbmConversionException("Conversion from DAString to DADataSeq is not allowed");
        }
        copyBaseDataEventProperties(this.daString, converted, clazz);

        return converted;
    }

    @SuppressWarnings("nls")
    private DataEvent getDMBool()
            throws OsacbmConversionException
    {
        DMBool dataEvent = new DMBool();
        if ( this.stringValue == null )
        {
            throw new OsacbmConversionException(
                    "Conversion from DAString to DMBool failed due to value is not true or false");
        }
        if ( "true".equalsIgnoreCase(this.stringValue) )
        {
            dataEvent.setValue(true);

        }
        else if ( "false".equalsIgnoreCase(this.stringValue) )
        {
            dataEvent.setValue(false);
        }
        else
        {
            throw new OsacbmConversionException(
                    "Conversion from DAString to DMBool failed due to value is not true or false");
        }
        return dataEvent;
    }

    private DataEvent getDMReal()
            throws OsacbmConversionException
    {
        DMReal dataEvent = new DMReal();
        dataEvent.setValue(Double.parseDouble(this.stringValue));

        return dataEvent;
    }

    private DataEvent getDAString()
    {
        DAString dataEvent = new DAString();
        dataEvent.setValue(this.stringValue);
        return dataEvent;
    }

    /**
     * Returns DAValueWaveform
     * 
     * @return converted DAValueWaveform
     */
    private DataEvent getDAValueWaveform()
    {
        DAValueWaveform daValueWaveform = new DAValueWaveform();
        daValueWaveform.setValues(new StringArrayValue());
        ((StringArrayValue) daValueWaveform.getValues()).getValues().add(this.stringValue);
        daValueWaveform.setXAxisStart(new DoubleValue());
        ((DoubleValue) daValueWaveform.getXAxisStart()).setValue(0.0);
        daValueWaveform.setXAxisDelta(new DoubleValue());
        ((DoubleValue) daValueWaveform.getXAxisDelta()).setValue(0.0);
        return daValueWaveform;
    }

    /**
     * Returns DAValueDataSeq
     * 
     * @return converted DAValueDataSeq
     */
    private DataEvent getDAValueDataSeq()
    {
        DAValueDataSeq daValueDataSeq = new DAValueDataSeq();
        daValueDataSeq.setValues(new StringArrayValue());
        ((StringArrayValue) daValueDataSeq.getValues()).getValues().add(this.stringValue);
        daValueDataSeq.setXAxisStart(new DoubleValue());
        ((DoubleValue) daValueDataSeq.getXAxisStart()).setValue(0.0);
        daValueDataSeq.setXAxisDeltas(new DblArrayValue());
        ((DblArrayValue) daValueDataSeq.getXAxisDeltas()).getValues().add(0.0);
        return daValueDataSeq;
    }

    @SuppressWarnings("nls")
    private DataEvent getDABool()
            throws OsacbmConversionException
    {
        DABool dataEvent = new DABool();
        if ( this.stringValue == null )
        {
            throw new OsacbmConversionException(
                    "Conversion from DAString to DMBool failed due to value is not true or false");
        }
        if ( "true".equalsIgnoreCase(this.stringValue) )
        {
            dataEvent.setValue(true);

        }
        else if ( "false".equalsIgnoreCase(this.stringValue) )
        {
            dataEvent.setValue(false);
        }
        else
        {
            throw new OsacbmConversionException(
                    "Conversion from DAString to DMBool failed due to value is not true or false");
        }
        return dataEvent;
    }
}
