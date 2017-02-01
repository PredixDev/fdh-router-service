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
import org.mimosa.osacbmv3_3.IntArrayValue;

/**
 * Base converter for Int
 * 
 * @author DSP-PM Development Team
 */
abstract public class IntConverter extends BaseConverter
{

    /**
     * 
     */
    protected int intValue;

    /**
     * Converts DAInt/DMInt to destination DataEvent type
     * 
     * @param clazz class of target DataEvent
     * @return converted DataEvent
     */
    @SuppressWarnings("nls")
    @Override
    public DataEvent convertTo(Class<?> clazz)
            throws OsacbmConversionException
    {
        if ( clazz.equals(DABool.class) )
        {
            return getDABool();
        }
        if ( clazz.equals(DMBool.class) )
        {
            return getDMBool();
        }
        if ( clazz.equals(DAInt.class) )
        {
            return getDAInt();
        }
        if ( clazz.equals(DMInt.class) )
        {
            return getDMInt();
        }
        if ( clazz.equals(DAReal.class) )
        {
            return getDAReal();
        }
        if ( clazz.equals(DMReal.class) )
        {
            return getDMReal();
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

    /**
     * Returns DABool
     * 
     * @return converted DABool
     */
    protected DABool getDABool()
    {
        DABool daBool = new DABool();
        daBool.setValue(this.intValue != 0);
        return daBool;
    }

    /**
     * Returns DAReal
     * 
     * @return converted DAReal
     */
    protected DAReal getDAReal()
    {
        DAReal daReal = new DAReal();
        daReal.setValue(this.intValue);
        return daReal;
    }

    /**
     * Returns DMBool
     * 
     * @return converted DMBool
     */
    protected DMBool getDMBool()
    {
        DMBool dmBool = new DMBool();
        dmBool.setValue(this.intValue != 0);
        return dmBool;
    }

    /**
     * Returns DMReal
     * 
     * @return converted DMReal
     */
    protected DMReal getDMReal()
    {
        DMReal dmReal = new DMReal();
        dmReal.setValue(this.intValue);
        return dmReal;
    }

    /**
     * Returns DAInt
     * 
     * @return converted DAInt
     */
    protected DAInt getDAInt()
    {
        DAInt daInt = new DAInt();
        daInt.setValue(this.intValue);
        return daInt;
    }

    /**
     * Returns DMInt
     * 
     * @return converted DMInt
     */
    protected DMInt getDMInt()
    {
        DMInt dmInt = new DMInt();
        dmInt.setValue(this.intValue);
        return dmInt;
    }

    /**
     * Returns DAValueWaveform
     * 
     * @return converted DAValueWaveform
     */
    private DataEvent getDAValueWaveform()
    {
        DAValueWaveform daValueWaveform = new DAValueWaveform();
        daValueWaveform.setValues(new DblArrayValue());
        ((DblArrayValue) daValueWaveform.getValues()).getValues().add(this.intValue + 0.0);
        daValueWaveform.setXAxisStart(new DoubleValue());
        ((DoubleValue) daValueWaveform.getXAxisStart()).setValue(0.0);
        daValueWaveform.setXAxisDelta(new DoubleValue());
        ((DoubleValue) daValueWaveform.getXAxisDelta()).setValue(0.0);
        return daValueWaveform;
    }

    /**
     * Returns DAWaveform
     * 
     * @return converted DAWaveform
     */
    private DataEvent getDAWaveform()
    {
        DAWaveform daWaveform = new DAWaveform();
        daWaveform.setValues(new ArrayList<Double>());
        daWaveform.getValues().add(this.intValue + 0.0);
        daWaveform.setXAxisStart(0.0);
        daWaveform.setXAxisDelta(0.0);
        return daWaveform;
    }

    /**
     * Returns DADataSeq
     * 
     * @return converted DADataSeq
     */
    private DataEvent getDADataSeq()
    {
        DADataSeq daDataSeq = new DADataSeq();
        daDataSeq.setValues(new ArrayList<Double>());
        daDataSeq.getValues().add(this.intValue + 0.0);
        daDataSeq.setXAxisDeltas(new ArrayList<Double>());
        daDataSeq.getXAxisDeltas().add(0.0);
        daDataSeq.setXAxisStart(0.0);
        return daDataSeq;
    }

    /**
     * Returns DMDataSeq
     * 
     * @return converted DMDataSeq
     */
    private DataEvent getDMDataSeq()
    {
        DMDataSeq dmDataSeq = new DMDataSeq();
        dmDataSeq.setValues(new ArrayList<Double>());
        dmDataSeq.getValues().add(this.intValue + 0.0);
        dmDataSeq.setXAxisDeltas(new ArrayList<Double>());
        dmDataSeq.getXAxisDeltas().add(0.0);
        dmDataSeq.setXAxisStart(0.0);
        return dmDataSeq;
    }

    /**
     * Returns DAValueDataSeq
     * 
     * @return converted DAValueDataSeq
     */
    private DataEvent getDAValueDataSeq()
    {
        DAValueDataSeq daValueDataSeq = new DAValueDataSeq();
        daValueDataSeq.setValues(new IntArrayValue());
        ((IntArrayValue) daValueDataSeq.getValues()).getValues().add(this.intValue);
        daValueDataSeq.setXAxisStart(new DoubleValue());
        ((DoubleValue) daValueDataSeq.getXAxisStart()).setValue(0.0);
        daValueDataSeq.setXAxisDeltas(new DblArrayValue());
        ((DblArrayValue) daValueDataSeq.getXAxisDeltas()).getValues().add(0.0);
        return daValueDataSeq;
    }

    /**
     * Returns DAString
     * 
     * @return converted DAString
     */
    @SuppressWarnings("nls")
    protected DAString getDAString()
    {
        DAString daString = new DAString();
        daString.setValue(this.intValue + "");
        return daString;
    }

    /**
     * @return -
     */
    protected DAVector getDAVector()
    {
        DAVector daVector = new DAVector();
        daVector.setXValue(this.intValue);
        return daVector;
    }

    /**
     * @return -
     */
    protected DMVector getDMVector()
    {
        DMVector dmVector = new DMVector();
        dmVector.setXValue(this.intValue);
        return dmVector;
    }
}
