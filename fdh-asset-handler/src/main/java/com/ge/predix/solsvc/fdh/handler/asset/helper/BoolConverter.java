package com.ge.predix.solsvc.fdh.handler.asset.helper;

import java.util.ArrayList;

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

/**
 * Base converter for Boolean
 * 
 * @author DSP-PM Development Team
 */
abstract public class BoolConverter extends BaseConverter
{

    /**
     * 
     */
    protected boolean boolValue;

    /**
     * Returns the converted DAInt
     * 
     * @return DAInt
     */
    protected DAInt getDAInt()
    {
        DAInt daInt = new DAInt();
        if ( this.boolValue )
        {
            daInt.setValue(1);
        }
        else
        {
            daInt.setValue(0);
        }
        return daInt;
    }

    /**
     * Returns the converted DAInt
     * 
     * @return DAInt
     */
    protected DAReal getDAReal()
    {
        DAReal daReal = new DAReal();
        if ( this.boolValue )
        {
            daReal.setValue(1.0);
        }
        else
        {
            daReal.setValue(0.0);
        }
        return daReal;
    }

    /**
     * Returns the converted DMInt
     * 
     * @return DMInt
     */
    protected DMInt getDMInt()
    {
        DMInt dmInt = new DMInt();
        if ( this.boolValue )
        {
            dmInt.setValue(1);
        }
        else
        {
            dmInt.setValue(0);
        }
        return dmInt;
    }

    /**
     * Returns the converted DMReal
     * 
     * @return DMReal
     */
    protected DMReal getDMReal()
    {
        DMReal dmReal = new DMReal();
        if ( this.boolValue )
        {
            dmReal.setValue(1.0);
        }
        else
        {
            dmReal.setValue(0.0);
        }
        return dmReal;
    }

    /**
     * Returns the converted DMReal
     * 
     * @return DMReal
     */
    protected DMBool getDMBool()
    {
        DMBool dmBool = new DMBool();
        dmBool.setValue(this.boolValue);
        return dmBool;
    }

    /**
     * Returns the converted DMReal
     * 
     * @return DMReal
     */
    protected DABool getDABool()
    {
        DABool daBool = new DABool();
        daBool.setValue(this.boolValue);
        return daBool;
    }

    /**
     * Returns the converted DMReal
     * 
     * @return DMReal
     */
    @SuppressWarnings("nls")
    protected DAString getDAString()
    {
        DAString daString = new DAString();
        if ( this.boolValue )
        {
            daString.setValue("true");
        }
        else
        {
            daString.setValue("false");
        }
        return daString;
    }

    @SuppressWarnings("nls")
    @Override
    /**
     * Returns the converted DataEvent
     */
    public DataEvent convertTo(Class<?> clazz)
            throws OsacbmConversionException
    {
        if ( clazz.equals(DAInt.class) )
        {
            return getDAInt();
        }
        if ( clazz.equals(DAReal.class) )
        {
            return getDAReal();
        }
        if ( clazz.equals(DMInt.class) )
        {
            return getDMInt();
        }
        if ( clazz.equals(DMReal.class) )
        {
            return getDMReal();
        }

        if ( clazz.equals(DABool.class) )
        {
            return getDABool();
        }
        if ( clazz.equals(DMBool.class) )
        {
            return getDMBool();
        }
        if ( clazz.equals(DAString.class) )
        {
            return getDAString();
        }
        if ( clazz.equals(DADataSeq.class) )
        {
            return getDADataSeq();
        }
        if ( clazz.equals(DMDataSeq.class) )
        {
            return getDMDataSeq();
        }
        if ( clazz.equals(DAWaveform.class) )
        {
            return getDAWaveform();
        }
        if ( clazz.equals(DAValueDataSeq.class) )
        {
            return getDAValueDataSeq();
        }
        if ( clazz.equals(DAValueWaveform.class) )
        {
            return getDAValueWaveform();
        }

        if ( clazz.equals(DAVector.class) )
        {
            return getDAVector();
        }
        if ( clazz.equals(DMVector.class) )
        {
            return getDMVector();
        }

        throw new OsacbmConversionException("Conversion to " + clazz.getSimpleName() + " is not supported");
    }

    private DataEvent getDAValueWaveform()
    {
        DAValueWaveform daValueWaveform = new DAValueWaveform();
        daValueWaveform.setValues(new DblArrayValue());
        if ( this.boolValue )
        {
            ((DblArrayValue) daValueWaveform.getValues()).getValues().add(1.0);
        }
        else
        {
            ((DblArrayValue) daValueWaveform.getValues()).getValues().add(0.0);

        }
        daValueWaveform.setXAxisStart(new DoubleValue());
        ((DoubleValue) daValueWaveform.getXAxisStart()).setValue(0.0);
        daValueWaveform.setXAxisDelta(new DoubleValue());
        ((DoubleValue) daValueWaveform.getXAxisDelta()).setValue(0.0);
        return daValueWaveform;
    }

    private DataEvent getDAWaveform()
    {
        DAWaveform daWaveform = new DAWaveform();
        daWaveform.setValues(new ArrayList<Double>());
        if ( this.boolValue )
        {
            daWaveform.getValues().add(1.0);
        }
        else
        {
            daWaveform.getValues().add(0.0);
        }
        daWaveform.setXAxisStart(0.0);
        daWaveform.setXAxisDelta(0.0);
        return daWaveform;
    }

    private DataEvent getDADataSeq()
    {
        DADataSeq daDataSeq = new DADataSeq();
        daDataSeq.setValues(new ArrayList<Double>());
        if ( this.boolValue )
        {
            (daDataSeq.getValues()).add(1.0);
        }
        else
        {
            (daDataSeq.getValues()).add(0.0);
        }
        daDataSeq.setXAxisDeltas(new ArrayList<Double>());
        daDataSeq.getXAxisDeltas().add(0.0);
        daDataSeq.setXAxisStart(0.0);
        return daDataSeq;
    }

    private DataEvent getDMDataSeq()
    {
        DMDataSeq dmDataSeq = new DMDataSeq();
        dmDataSeq.setValues(new ArrayList<Double>());
        if ( this.boolValue )
        {
            (dmDataSeq.getValues()).add(1.0);
        }
        else
        {
            (dmDataSeq.getValues()).add(0.0);
        }
        dmDataSeq.setXAxisDeltas(new ArrayList<Double>());
        dmDataSeq.getXAxisDeltas().add(0.0);
        dmDataSeq.setXAxisStart(0.0);
        return dmDataSeq;
    }

    private DataEvent getDAValueDataSeq()
    {
        DAValueDataSeq daValueDataSeq = new DAValueDataSeq();
        daValueDataSeq.setValues(new DblArrayValue());
        if ( this.boolValue )
        {
            ((DblArrayValue) daValueDataSeq.getValues()).getValues().add(1.0);
        }
        else
        {
            ((DblArrayValue) daValueDataSeq.getValues()).getValues().add(0.0);
        }
        daValueDataSeq.setXAxisStart(new DoubleValue());
        ((DoubleValue) daValueDataSeq.getXAxisStart()).setValue(0.0);
        daValueDataSeq.setXAxisDeltas(new DblArrayValue());
        ((DblArrayValue) daValueDataSeq.getXAxisDeltas()).getValues().add(0.0);
        return daValueDataSeq;
    }

    private DataEvent getDAVector()
    {
        DAVector dataEvent = new DAVector();
        if ( this.boolValue )
        {
            dataEvent.setValue(1.0);
        }
        else
        {
            dataEvent.setValue(0.0);
        }
        return dataEvent;
    }

    private DataEvent getDMVector()
    {
        DMVector dataEvent = new DMVector();
        if ( this.boolValue )
        {
            dataEvent.setValue(1.0);
        }
        else
        {
            dataEvent.setValue(0.0);
        }
        return dataEvent;
    }
}
