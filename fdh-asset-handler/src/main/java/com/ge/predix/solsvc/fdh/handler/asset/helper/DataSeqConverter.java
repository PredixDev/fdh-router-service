package com.ge.predix.solsvc.fdh.handler.asset.helper;

import java.util.ArrayList;
import java.util.List;

import org.mimosa.osacbmv3_3.DABool;
import org.mimosa.osacbmv3_3.DAInt;
import org.mimosa.osacbmv3_3.DAReal;
import org.mimosa.osacbmv3_3.DAString;
import org.mimosa.osacbmv3_3.DAValueDataSeq;
import org.mimosa.osacbmv3_3.DAValueWaveform;
import org.mimosa.osacbmv3_3.DAVector;
import org.mimosa.osacbmv3_3.DAWaveform;
import org.mimosa.osacbmv3_3.DMBool;
import org.mimosa.osacbmv3_3.DMInt;
import org.mimosa.osacbmv3_3.DMReal;
import org.mimosa.osacbmv3_3.DMVector;
import org.mimosa.osacbmv3_3.DataEvent;
import org.mimosa.osacbmv3_3.DblArrayValue;
import org.mimosa.osacbmv3_3.DoubleValue;

/**
 * Converter from DataSeq into supported OSA data types
 * 
 * @author DSP-PM Development Team
 */
public class DataSeqConverter extends BaseConverter
{

    /**
     * 
     */
    protected List<Double> doubleValues;


    /**
     * Converts DataSeq to destination DataEvent type
     * 
     * @param destClassType class of target DataEvent
     * @return converted DataEvent
     */
    @SuppressWarnings("nls")
    @Override
    public DataEvent convertTo(Class<?> destClassType)
            throws OsacbmConversionException
    {
        if ( destClassType.equals(DMReal.class) )
        {
            return getDMReal();
        }
        if ( destClassType.equals(DAReal.class) )
        {
            return getDAReal();
        }
        if ( destClassType.equals(DABool.class) )
        {
            return getDABool();
        }
        if ( destClassType.equals(DAInt.class) )
        {
            return getDAInt();
        }
        if ( destClassType.equals(DMInt.class) )
        {
            return getDMInt();
        }
        if ( destClassType.equals(DAValueDataSeq.class) )
        {
            return getDAValueDataSeq();
        }
        if ( destClassType.equals(DAVector.class) )
        {
            return getDAVector();
        }
        if ( destClassType.equals(DMVector.class) )
        {
            return getDMVector();
        }
        if ( destClassType.equals(DMBool.class) )
        {
            return getDMBool();
        }
        if ( destClassType.equals(DAWaveform.class) )
        {
            return getDAWaveform();
        }
        if ( destClassType.equals(DAString.class) )
        {
            return getDAString();
        }
        if ( destClassType.equals(DAValueWaveform.class) )
        {
            return getDAValueWaveform();
        }
        throw new OsacbmConversionException("Conversion to " + destClassType.getSimpleName() + " is not supported");
    }

    /**
     * return DAWaveform. Throws and error if the xAxisDeltas are null or non-uniform
     * 
     * @return
     * @throws OsacbmConversionException
     */
    @SuppressWarnings("nls")
    private DataEvent getDAWaveform()
            throws OsacbmConversionException
    {
        if ( this.xAxisDeltas == null )
        {
            throw new OsacbmConversionException(
                    "Converting Data seq to DAWaveform the xAxisDeltas on the DataSeq are null.");
        }
        for (int i = 1; i < this.xAxisDeltas.size(); i++)
        {
            if ( !(this.xAxisDeltas.get(i).equals(this.xAxisDeltas.get(i - 1))) )
            {
                throw new OsacbmConversionException(
                        "Converting Data seq to DAWaveform the xAxisDeltas on the DataSeq are not equal.");
            }
        }
        DAWaveform daWaveform = new DAWaveform();
        daWaveform.setValues(this.doubleValues);
        daWaveform.setXAxisStart(this.xAxisStart);
        daWaveform.setXAxisDelta(this.xAxisDeltas.get(0));

        return daWaveform;
    }

    /**
     * Returns DABool
     * 
     * @return converted DABool
     * @throws OsacbmConversionException conversion error
     */
    @SuppressWarnings("nls")
    protected DABool getDABool()
            throws OsacbmConversionException
    {
        DABool daBool = new DABool();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                daBool.setValue(this.doubleValues.get(this.doubleValues.size() - 1) != 0.0);
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DABool");
            }
        }
        return daBool;
    }

    /**
     * Returns DAInt
     * 
     * @return converted DAInt
     * @throws OsacbmConversionException conversion error
     */
    @SuppressWarnings("nls")
    protected DAInt getDAInt()
            throws OsacbmConversionException
    {
        DAInt daInt = new DAInt();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                daInt.setValue((new Long(Math.round(this.doubleValues.get(this.doubleValues.size() - 1)))).intValue());
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DAInt");
            }
        }
        return daInt;
    }

    /**
     * Returns DAReal
     * 
     * @return converted DAReal
     * @throws OsacbmConversionException conversion error
     */
    @SuppressWarnings("nls")
    protected DAReal getDAReal()
            throws OsacbmConversionException
    {
        DAReal daReal = new DAReal();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                daReal.setValue(this.doubleValues.get(this.doubleValues.size() - 1));
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DAReal");
            }
        }
        return daReal;
    }

    /**
     * Returns DAValueDataSeq
     * 
     * @return converted DAValueDataSeq
     * @throws OsacbmConversionException conversion error
     */
    @SuppressWarnings("nls")
    protected DAValueDataSeq getDAValueDataSeq()
            throws OsacbmConversionException
    {
        DAValueDataSeq daValueDataSeq = new DAValueDataSeq();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                DblArrayValue toValue = new DblArrayValue();
                daValueDataSeq.setValues(toValue);
                toValue.getValues().addAll((this.doubleValues));
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DAValueDataSeq");
            }
            if ( this.xAxisStart != null )
            {
                DoubleValue value = new DoubleValue();
                value.setValue(this.xAxisStart);
                daValueDataSeq.setXAxisStart(value);
            }
            daValueDataSeq.setXAxisDeltas(new DblArrayValue());
            if ( this.xAxisDeltas != null )
            {
                ((DblArrayValue) daValueDataSeq.getXAxisDeltas()).getValues().addAll(this.xAxisDeltas);
            }
        }
        return daValueDataSeq;
    }

    /**
     * Returns DAVector
     * 
     * @return converted DAVector
     * @throws OsacbmConversionException conversion error
     */
    @SuppressWarnings("nls")
    protected DAVector getDAVector()
            throws OsacbmConversionException
    {
        DAVector daVector = new DAVector();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                daVector.setValue(this.doubleValues.get(this.doubleValues.size() - 1));
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DAVector");
            }
            if ( this.xAxisDeltas != null )
            {
                daVector.setXValue(Utils.getTimeAtIndex(this.xAxisDeltas, this.xAxisStart, this.xAxisDeltas.size() - 1));
            }
        }
        return daVector;
    }

    /**
     * Returns DMBool
     * 
     * @return converted DMBool
     * @throws OsacbmConversionException conversion error
     */
    @SuppressWarnings("nls")
    protected DMBool getDMBool()
            throws OsacbmConversionException
    {
        DMBool dmBool = new DMBool();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                dmBool.setValue(this.doubleValues.get(this.doubleValues.size() - 1) != 0.0);
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DMBool");
            }
        }
        return dmBool;
    }

    /**
     * Returns DMInt
     * 
     * @return converted DMInt
     * @throws OsacbmConversionException -
     */
    @SuppressWarnings("nls")
    protected DMInt getDMInt()
            throws OsacbmConversionException
    {
        DMInt dmInt = new DMInt();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                dmInt.setValue((new Long(Math.round(this.doubleValues.get(this.doubleValues.size() - 1)))).intValue());
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DMInt");
            }
        }
        return dmInt;
    }

    /**
     * Returns DMReal
     * 
     * @return converted DMReal
     * @throws OsacbmConversionException conversion error
     */
    @SuppressWarnings("nls")
    protected DMReal getDMReal()
            throws OsacbmConversionException
    {
        DMReal dmReal = new DMReal();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                dmReal.setValue(this.doubleValues.get(this.doubleValues.size() - 1));
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DMReal");
            }
        }
        return dmReal;
    }

    /**
     * Returns DMVector
     * 
     * @return converted DAVector
     * @throws OsacbmConversionException conversion error
     */
    @SuppressWarnings("nls")
    protected DMVector getDMVector()
            throws OsacbmConversionException
    {
        DMVector dmVector = new DMVector();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                dmVector.setValue(this.doubleValues.get(this.doubleValues.size() - 1));
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DAVector");
            }
            if ( this.xAxisDeltas != null )
            {
                dmVector.setXValue(Utils.getTimeAtIndex(this.xAxisDeltas, this.xAxisStart, this.xAxisDeltas.size() - 1));
            }
        }
        return dmVector;
    }

    /**
     * @return -
     * @throws OsacbmConversionException -
     */
    @SuppressWarnings("nls")
    protected DAString getDAString()
            throws OsacbmConversionException
    {
        DAString daString = new DAString();
        if ( this.doubleValues != null )
        {
            if ( this.doubleValues.size() > 0 )
            {
                Double value = this.doubleValues.get(this.doubleValues.size() - 1);
                if ( value != null )
                {
                    daString.setValue(Double.toString(value));
                }
            }
            else
            {
                throw new OsacbmConversionException("Converting empty array of double values to a DAString");
            }
        }

        return daString;
    }

    /**
     * @return -
     * @throws OsacbmConversionException -
     */
    protected DAValueWaveform getDAValueWaveform()
            throws OsacbmConversionException
    {
        DAValueWaveform daValueWaveform = new DAValueWaveform();
        if ( this.doubleValues != null )
        {
            DblArrayValue value = new DblArrayValue();
            List<Double> values = new ArrayList<Double>();
            value.setValues(values);
            for (Double val : this.doubleValues)
            {
                values.add(val);
            }
            daValueWaveform.setValues(value);
        }

        if ( this.xAxisDeltas != null )
        {
            DblArrayValue delta = new DblArrayValue();
            List<Double> deltas = new ArrayList<Double>();
            delta.setValues(deltas);
            for (Double val : this.xAxisDeltas)
            {
                deltas.add(val);
            }
            daValueWaveform.setXAxisDelta(delta);
        }
        if ( this.xAxisStart != null )
        {
            DoubleValue xAxisStartValue = new DoubleValue();
            xAxisStartValue.setValue(this.xAxisStart);
            daValueWaveform.setXAxisStart(xAxisStartValue);
        }

        return daValueWaveform;
    }
}
