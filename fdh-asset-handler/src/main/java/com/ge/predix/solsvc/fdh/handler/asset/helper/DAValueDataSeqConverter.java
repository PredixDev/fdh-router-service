package com.ge.predix.solsvc.fdh.handler.asset.helper;

import org.mimosa.osacbmv3_3.BooleanArrayValue;
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
import org.mimosa.osacbmv3_3.FloatArrayValue;
import org.mimosa.osacbmv3_3.IntArrayValue;
import org.mimosa.osacbmv3_3.LongArrayValue;
import org.mimosa.osacbmv3_3.StringArrayValue;
import org.mimosa.osacbmv3_3.Value;

/**
 * Converter from DAValueDataSeq into supported OSA data types
 * 
 * @author DSP-PM Development Team
 */
public class DAValueDataSeqConverter extends BaseConverter
{

    private static final String CONVERTING                                            = "Converting "; //$NON-NLS-1$
    private static final String CONVERTING_EMPTY_ARRAY_OF_DBL_ARRAY_VALUE_TO_A_DAINT  = "Converting empty array of DblArrayValue to a DAInt"; //$NON-NLS-1$
    private static final String CONVERTING_EMPTY_ARRAY_OF_DBL_ARRAY_VALUE_TO_A_DMINT  = "Converting empty array of DblArrayValue to a DMInt"; //$NON-NLS-1$
    private static final String CONVERTING_EMPTY_ARRAY_OF_DBL_ARRAY_VALUE_TO_A_DAREAL = "Converting empty array of DblArrayValue to a DAReal"; //$NON-NLS-1$
    private static final String CONVERTING_EMPTY_ARRAY_OF_DBL_ARRAY_VALUE_TO_A_DMREAL = "Converting empty array of DblArrayValue to a DMReal"; //$NON-NLS-1$

    final private Value         valueValue;
    private DAValueDataSeq      daValueDataSeq;

    /**
     * Initializes the converter
     * 
     * @param daValueDataSeq DAValueDataSeq to convert
     */
    public DAValueDataSeqConverter(DAValueDataSeq daValueDataSeq)
    {
        this.daValueDataSeq = daValueDataSeq;
        this.valueValue = daValueDataSeq.getValues();
        if ( daValueDataSeq.getXAxisDeltas() != null )
        {
            this.xAxisDeltas = ((DblArrayValue) daValueDataSeq.getXAxisDeltas()).getValues();
        }
        else
        {
            this.xAxisDeltas = null;
        }
        if ( daValueDataSeq.getXAxisStart() != null )
        {
            this.xAxisStart = ((DoubleValue) daValueDataSeq.getXAxisStart()).getValue();
        }
        else
        {
            this.xAxisStart = null;
        }
    }

    /**
     * Converts DAValueDataSeq to destination DataEvent type
     * 
     * @param clazz class of target DataEvent
     * @return converted DataEvent
     */
    @SuppressWarnings({
            "rawtypes", "nls"
    })
    @Override
    public DataEvent convertTo(Class clazz)
            throws OsacbmConversionException
    {

        DataEvent converted = null;

        if ( clazz.equals(DADataSeq.class) )
        {
            converted = getDADataSeq();
        }
        else if ( clazz.equals(DABool.class) )
        {
            converted = getDABool();
        }
        else if ( clazz.equals(DMBool.class) )
        {
            converted = getDMBool();
        }
        else if ( clazz.equals(DAInt.class) )
        {
            converted = getDAInt();
        }
        else if ( clazz.equals(DMInt.class) )
        {
            converted = getDMInt();
        }
        else if ( clazz.equals(DAReal.class) )
        {
            converted = getDAReal();
        }
        else if ( clazz.equals(DMReal.class) )
        {
            converted = getDMReal();
        }
        else if ( clazz.equals(DAValueWaveform.class) )
        {
            converted = getDAValueWaveform();
        }
        else if ( clazz.equals(DAWaveform.class) )
        {
            converted = getDAWaveform();
        }
        else if ( clazz.equals(DAVector.class) )
        {
            converted = getDAVector();
        }
        else if ( clazz.equals(DMVector.class) )
        {
            converted = getDMVector();
        }
        else if ( clazz.equals(DMDataSeq.class) )
        {
            converted = getDMDataSeq();
        }
        else if ( clazz.equals(DAString.class) )
        {
            converted = getDAString();
        }
        else
        {
            throw new OsacbmConversionException("Conversion from DAValueDataSeq to " + clazz.getName()
                    + " has not been implemented.");
        }

        copyBaseDataEventProperties(this.daValueDataSeq, converted, clazz);

        return converted;
    }

    /**
     * Returns DADataSeq
     * 
     * @return converted DADataSeq
     * @throws OsacbmConversionException conversion error
     */
    @SuppressWarnings("nls")
    protected DADataSeq getDADataSeq()
            throws OsacbmConversionException
    {
        DADataSeq daDataSeq = new DADataSeq();
        if ( this.valueValue != null )
        {
            if ( this.valueValue instanceof DblArrayValue )
            {

                daDataSeq.getValues().addAll(((DblArrayValue) this.valueValue).getValues());
            }
            else if ( this.valueValue instanceof LongArrayValue )
            {
                for (Long value : ((LongArrayValue) this.valueValue).getValues())
                {
                    daDataSeq.getValues().add((double) value);
                }
            }
            else if ( this.valueValue instanceof FloatArrayValue )
            {
                for (Float value : ((FloatArrayValue) this.valueValue).getValues())
                {
                    daDataSeq.getValues().add((double) value);
                }
            }
            else if ( this.valueValue instanceof IntArrayValue )
            {
                for (Integer value : ((IntArrayValue) this.valueValue).getValues())
                {
                    daDataSeq.getValues().add((double) value);
                }
            }
            else if ( this.valueValue instanceof BooleanArrayValue )
            {
                for (Boolean value : ((BooleanArrayValue) this.valueValue).getValues())
                {
                    if ( value )
                    {
                        daDataSeq.getValues().add(1.0);
                    }
                    else
                    {
                        daDataSeq.getValues().add(0.0);
                    }
                }
            }
        }
        else
        {
            throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName()
                    + " to DADataSeq values");
        }
        daDataSeq.setXAxisStart(this.xAxisStart);
        if ( this.xAxisDeltas != null )
        {
            daDataSeq.getXAxisDeltas().addAll(this.xAxisDeltas);
        }
        return daDataSeq;
    }

    /**
     * Returns DABool
     * 
     * @return converted DABook
     * @throws OsacbmConversionException conversion error
     */
    @SuppressWarnings("nls")
    protected DABool getDABool()
            throws OsacbmConversionException
    {
        DABool daBool = new DABool();

        if ( this.valueValue != null )
        {
            if ( this.valueValue instanceof DblArrayValue )
            {
                if ( ((DblArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daBool.setValue(((DblArrayValue) this.valueValue).getValues().get(
                            ((DblArrayValue) this.valueValue).getValues().size() - 1) != 0.0);
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of DblArrayValue to a DABool");
                }
            }
            else if ( this.valueValue instanceof LongArrayValue )
            {
                if ( ((LongArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daBool.setValue(((LongArrayValue) this.valueValue).getValues().get(
                            ((LongArrayValue) this.valueValue).getValues().size() - 1) != 0L);
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of LongArrayValue to a DABool");
                }
            }
            else if ( this.valueValue instanceof FloatArrayValue )
            {
                if ( ((FloatArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daBool.setValue(((FloatArrayValue) this.valueValue).getValues().get(
                            ((FloatArrayValue) this.valueValue).getValues().size() - 1) != 0.0);
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of FloatArrayValue to a DABool");
                }
            }
            else if ( this.valueValue instanceof IntArrayValue )
            {
                if ( ((IntArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daBool.setValue(((IntArrayValue) this.valueValue).getValues().get(
                            ((IntArrayValue) this.valueValue).getValues().size() - 1) != 0);
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of IntArrayValue to a DABool");
                }
            }
            else if ( this.valueValue instanceof BooleanArrayValue )
            {
                if ( ((BooleanArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daBool.setValue(((BooleanArrayValue) this.valueValue).getValues().get(
                            ((BooleanArrayValue) this.valueValue).getValues().size() - 1));
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of BooleanArrayValue to a DABool");
                }
            }
            else
            {
                throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName()
                        + " to DABool value");
            }
        }
        return daBool;
    }

    /**
     * Returns DABool
     * 
     * @return converted DABook
     * @throws OsacbmConversionException conversion error
     */
    @SuppressWarnings("nls")
    protected DMBool getDMBool()
            throws OsacbmConversionException
    {
        DMBool dmBool = new DMBool();

        if ( this.valueValue != null )
        {
            if ( this.valueValue instanceof DblArrayValue )
            {
                if ( ((DblArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    dmBool.setValue(((DblArrayValue) this.valueValue).getValues().get(
                            ((DblArrayValue) this.valueValue).getValues().size() - 1) != 0.0);
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of DblArrayValue to a DMBool");
                }
            }
            else if ( this.valueValue instanceof LongArrayValue )
            {
                if ( ((LongArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    dmBool.setValue(((LongArrayValue) this.valueValue).getValues().get(
                            ((LongArrayValue) this.valueValue).getValues().size() - 1) != 0L);
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of LongArrayValue to a DMBool");
                }
            }
            else if ( this.valueValue instanceof FloatArrayValue )
            {
                if ( ((FloatArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    dmBool.setValue(((FloatArrayValue) this.valueValue).getValues().get(
                            ((FloatArrayValue) this.valueValue).getValues().size() - 1) != 0.0);
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of FloatArrayValue to a DMBool");
                }
            }
            else if ( this.valueValue instanceof IntArrayValue )
            {
                if ( ((IntArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    dmBool.setValue(((IntArrayValue) this.valueValue).getValues().get(
                            ((IntArrayValue) this.valueValue).getValues().size() - 1) != 0);
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of IntArrayValue to a DMBool");
                }
            }
            else if ( this.valueValue instanceof BooleanArrayValue )
            {
                if ( ((BooleanArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    dmBool.setValue(((BooleanArrayValue) this.valueValue).getValues().get(
                            ((BooleanArrayValue) this.valueValue).getValues().size() - 1));
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of BooleanArrayValue to a DMBool");
                }
            }
            else
            {
                throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName()
                        + " to DMBool value");
            }
        }
        return dmBool;
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
        if ( this.valueValue != null )
        {
            if ( this.valueValue instanceof DblArrayValue )
            {
                if ( ((DblArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daInt.setValue(new Long(java.lang.Math.round(((DblArrayValue) this.valueValue).getValues().get(
                            ((DblArrayValue) this.valueValue).getValues().size() - 1))).intValue());
                }
                else
                {
                    throw new OsacbmConversionException(CONVERTING_EMPTY_ARRAY_OF_DBL_ARRAY_VALUE_TO_A_DAINT);
                }
            }
            else if ( this.valueValue instanceof LongArrayValue )
            {
                if ( ((LongArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daInt.setValue(((LongArrayValue) this.valueValue).getValues()
                            .get(((LongArrayValue) this.valueValue).getValues().size() - 1).intValue());
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of LongArrayValue to a DAInt");
                }
            }
            else if ( this.valueValue instanceof FloatArrayValue )
            {
                if ( ((FloatArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daInt.setValue(new Long(java.lang.Math.round(((FloatArrayValue) this.valueValue).getValues().get(
                            ((FloatArrayValue) this.valueValue).getValues().size() - 1))).intValue());
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of FloatArrayValue to a DAInt");
                }
            }
            else if ( this.valueValue instanceof IntArrayValue )
            {
                if ( ((IntArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daInt.setValue(((IntArrayValue) this.valueValue).getValues().get(
                            ((IntArrayValue) this.valueValue).getValues().size() - 1));
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of IntArrayValue to a DAInt");
                }
            }
            else if ( this.valueValue instanceof BooleanArrayValue )
            {
                if ( ((BooleanArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    if ( ((BooleanArrayValue) this.valueValue).getValues().get(
                            ((BooleanArrayValue) this.valueValue).getValues().size() - 1) )
                    {
                        daInt.setValue(1);
                    }
                    else
                    {
                        daInt.setValue(0);
                    }
                }
                else
                {
                    throw new OsacbmConversionException(CONVERTING_EMPTY_ARRAY_OF_DBL_ARRAY_VALUE_TO_A_DAINT);
                }
            }
            else
            {
                throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName()
                        + " to DAInt value");
            }
        }
        return daInt;
    }

    /**
     * Returns DAInt
     * 
     * @return converted DAInt
     * @throws OsacbmConversionException conversion error
     */
    @SuppressWarnings("nls")
    protected DMInt getDMInt()
            throws OsacbmConversionException
    {
        DMInt dmInt = new DMInt();
        if ( this.valueValue != null )
        {
            if ( this.valueValue instanceof DblArrayValue )
            {
                if ( ((DblArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    dmInt.setValue(new Long(java.lang.Math.round(((DblArrayValue) this.valueValue).getValues().get(
                            ((DblArrayValue) this.valueValue).getValues().size() - 1))).intValue());
                }
                else
                {
                    throw new OsacbmConversionException(CONVERTING_EMPTY_ARRAY_OF_DBL_ARRAY_VALUE_TO_A_DMINT);
                }
            }
            else if ( this.valueValue instanceof LongArrayValue )
            {
                if ( ((LongArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    dmInt.setValue(((LongArrayValue) this.valueValue).getValues()
                            .get(((LongArrayValue) this.valueValue).getValues().size() - 1).intValue());
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of LongArrayValue to a DMInt");
                }
            }
            else if ( this.valueValue instanceof FloatArrayValue )
            {
                if ( ((FloatArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    dmInt.setValue(new Long(java.lang.Math.round(((FloatArrayValue) this.valueValue).getValues().get(
                            ((FloatArrayValue) this.valueValue).getValues().size() - 1))).intValue());
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of FloatArrayValue to a DMInt");
                }
            }
            else if ( this.valueValue instanceof IntArrayValue )
            {
                if ( ((IntArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    dmInt.setValue(((IntArrayValue) this.valueValue).getValues().get(
                            ((IntArrayValue) this.valueValue).getValues().size() - 1));
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of IntArrayValue to a DMInt");
                }
            }
            else if ( this.valueValue instanceof BooleanArrayValue )
            {
                if ( ((BooleanArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    if ( ((BooleanArrayValue) this.valueValue).getValues().get(
                            ((BooleanArrayValue) this.valueValue).getValues().size() - 1) )
                    {
                        dmInt.setValue(1);
                    }
                    else
                    {
                        dmInt.setValue(0);
                    }
                }
                else
                {
                    throw new OsacbmConversionException(CONVERTING_EMPTY_ARRAY_OF_DBL_ARRAY_VALUE_TO_A_DMINT);
                }
            }
            else
            {
                throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName()
                        + " to DMInt value");
            }
        }
        return dmInt;
    }

    /**
     * Returns DAReal
     * 
     * @return converted DAReal
     * @throws OsacbmConversionException -
     */
    @SuppressWarnings("nls")
    protected DAReal getDAReal()
            throws OsacbmConversionException
    {
        DAReal daReal = new DAReal();

        if ( this.valueValue != null )
        {

            if ( this.valueValue instanceof DblArrayValue )
            {
                if ( ((DblArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daReal.setValue(((DblArrayValue) this.valueValue).getValues().get(
                            ((DblArrayValue) this.valueValue).getValues().size() - 1));
                }
                else
                {
                    throw new OsacbmConversionException(CONVERTING_EMPTY_ARRAY_OF_DBL_ARRAY_VALUE_TO_A_DAREAL);
                }
            }
            else if ( this.valueValue instanceof LongArrayValue )
            {
                if ( ((LongArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daReal.setValue(((LongArrayValue) this.valueValue).getValues()
                            .get(((LongArrayValue) this.valueValue).getValues().size() - 1).intValue());
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of LongArrayValue to a DAReal");
                }
            }
            else if ( this.valueValue instanceof FloatArrayValue )
            {
                if ( ((FloatArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daReal.setValue(((FloatArrayValue) this.valueValue).getValues().get(
                            ((FloatArrayValue) this.valueValue).getValues().size() - 1));
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of FloatArrayValue to a DAReal");
                }
            }
            else if ( this.valueValue instanceof IntArrayValue )
            {
                if ( ((IntArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daReal.setValue(((IntArrayValue) this.valueValue).getValues().get(
                            ((IntArrayValue) this.valueValue).getValues().size() - 1));
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of IntArrayValue to a DAReal");
                }
            }
            else if ( this.valueValue instanceof BooleanArrayValue )
            {
                if ( ((BooleanArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    if ( ((BooleanArrayValue) this.valueValue).getValues().get(
                            ((BooleanArrayValue) this.valueValue).getValues().size() - 1) )
                    {
                        daReal.setValue(1.0);
                    }
                    else
                    {
                        daReal.setValue(0.0);
                    }
                }
                else
                {
                    throw new OsacbmConversionException(CONVERTING_EMPTY_ARRAY_OF_DBL_ARRAY_VALUE_TO_A_DAREAL);
                }
            }
        }
        return daReal;
    }

    /**
     * Returns DAReal
     * 
     * @return converted DAReal
     * @throws OsacbmConversionException -
     */
    @SuppressWarnings("nls")
    protected DMReal getDMReal()
            throws OsacbmConversionException
    {
        DMReal dmReal = new DMReal();

        if ( this.valueValue != null )
        {

            if ( this.valueValue instanceof DblArrayValue )
            {
                if ( ((DblArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    dmReal.setValue(((DblArrayValue) this.valueValue).getValues().get(
                            ((DblArrayValue) this.valueValue).getValues().size() - 1));
                }
                else
                {
                    throw new OsacbmConversionException(CONVERTING_EMPTY_ARRAY_OF_DBL_ARRAY_VALUE_TO_A_DMREAL);
                }
            }
            else if ( this.valueValue instanceof LongArrayValue )
            {
                if ( ((LongArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    dmReal.setValue(((LongArrayValue) this.valueValue).getValues()
                            .get(((LongArrayValue) this.valueValue).getValues().size() - 1).intValue());
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of LongArrayValue to a DMReal");
                }
            }
            else if ( this.valueValue instanceof FloatArrayValue )
            {
                if ( ((FloatArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    dmReal.setValue(((FloatArrayValue) this.valueValue).getValues().get(
                            ((FloatArrayValue) this.valueValue).getValues().size() - 1));
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of FloatArrayValue to a DMReal");
                }
            }
            else if ( this.valueValue instanceof IntArrayValue )
            {
                if ( ((IntArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    dmReal.setValue(((IntArrayValue) this.valueValue).getValues().get(
                            ((IntArrayValue) this.valueValue).getValues().size() - 1));
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of IntArrayValue to a DMReal");
                }
            }
            else if ( this.valueValue instanceof BooleanArrayValue )
            {
                if ( ((BooleanArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    if ( ((BooleanArrayValue) this.valueValue).getValues().get(
                            ((BooleanArrayValue) this.valueValue).getValues().size() - 1) )
                    {
                        dmReal.setValue(1.0);
                    }
                    else
                    {
                        dmReal.setValue(0.0);
                    }
                }
                else
                {
                    throw new OsacbmConversionException(CONVERTING_EMPTY_ARRAY_OF_DBL_ARRAY_VALUE_TO_A_DMREAL);
                }
            }
        }
        return dmReal;
    }

    /**
     * Returns DAValueWaveform
     * 
     * @return converted DAValueWaveform
     * @throws OsacbmConversionException conversion error
     */
    @SuppressWarnings("nls")
    protected DAValueWaveform getDAValueWaveform()
            throws OsacbmConversionException
    {
        DAValueWaveform daValueWaveform = new DAValueWaveform();

        if ( this.xAxisDeltas != null )
        {
            for (int i = 1; i < this.xAxisDeltas.size(); i++)
            {
                if ( !this.xAxisDeltas.get(i).equals(this.xAxisDeltas.get(i - 1)) )
                {
                    throw new OsacbmConversionException(
                            "Converting data sequence to DAValueWaveform, the xAxisDeltas are not all equal.");
                }
            }
            DoubleValue delta = new DoubleValue();
            delta.setValue(this.xAxisDeltas.get(0));
            daValueWaveform.setXAxisDelta(delta);
        }
        else
        {
            daValueWaveform.setXAxisDelta(null);
        }

        daValueWaveform.setValues(this.valueValue);
        if ( super.xAxisStart != null )
        {
            daValueWaveform.setXAxisStart(new DoubleValue());
            ((DoubleValue) daValueWaveform.getXAxisStart()).setValue(super.xAxisStart);
        }
        else
        {
            daValueWaveform.setXAxisStart(null);
        }

        return daValueWaveform;
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

        if ( this.valueValue != null )
        {
            if ( this.valueValue instanceof DblArrayValue )
            {
                if ( ((DblArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daVector.setValue(((DblArrayValue) this.valueValue).getValues().get(
                            ((DblArrayValue) this.valueValue).getValues().size() - 1));
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of DblArrayValue to a DAVector");
                }
            }
            else if ( this.valueValue instanceof LongArrayValue )
            {
                if ( ((LongArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daVector.setValue(((LongArrayValue) this.valueValue).getValues()
                            .get(((LongArrayValue) this.valueValue).getValues().size() - 1).intValue());
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of LongArrayValue to a DAVector");
                }
            }
            else if ( this.valueValue instanceof FloatArrayValue )
            {
                if ( ((FloatArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daVector.setValue(((FloatArrayValue) this.valueValue).getValues().get(
                            ((FloatArrayValue) this.valueValue).getValues().size() - 1));
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of FloatArrayValue to a DAVector");
                }
            }
            else if ( this.valueValue instanceof IntArrayValue )
            {
                if ( ((IntArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daVector.setValue(((IntArrayValue) this.valueValue).getValues().get(
                            ((IntArrayValue) this.valueValue).getValues().size() - 1));
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of IntArrayValue to a DAVector");
                }
            }
            else if ( this.valueValue instanceof BooleanArrayValue )
            {
                if ( ((BooleanArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    if ( ((BooleanArrayValue) this.valueValue).getValues().get(
                            ((BooleanArrayValue) this.valueValue).getValues().size() - 1) )
                    {
                        daVector.setValue(1.0);
                    }
                    else
                    {
                        daVector.setValue(0.0);
                    }
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of BooleanArrayValue to a DAVector");
                }
            }
            else
            {
                throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName()
                        + " to DAVector value");
            }
            if ( this.xAxisDeltas != null )
            {
                daVector.setXValue(this.xAxisStart + this.xAxisDeltas.size() - 1);
            }
        }
        return daVector;
    }

    /**
     * Returns DAWaveform
     * 
     * @return converted DAWaveform
     */
    protected DAWaveform getDAWaveform()
    {
        DAWaveform daWaveform = new DAWaveform();

        if ( this.valueValue != null )
        {
            if ( this.valueValue instanceof DblArrayValue )
            {
                daWaveform.getValues().addAll(((DblArrayValue) this.valueValue).getValues());
            }
            else if ( this.valueValue instanceof LongArrayValue )
            {
                for (Long value : ((LongArrayValue) this.valueValue).getValues())
                {
                    daWaveform.getValues().add(new Double(value));
                }
            }
            else if ( this.valueValue instanceof FloatArrayValue )
            {
                for (Float value : ((FloatArrayValue) this.valueValue).getValues())
                {
                    daWaveform.getValues().add(new Double(value));
                }
            }
            else if ( this.valueValue instanceof IntArrayValue )
            {
                for (Integer value : ((IntArrayValue) this.valueValue).getValues())
                {
                    daWaveform.getValues().add(new Double(value));
                }
            }
            else if ( this.valueValue instanceof BooleanArrayValue )
            {
                for (Boolean value : ((BooleanArrayValue) this.valueValue).getValues())
                {
                    if ( value )
                    {
                        daWaveform.getValues().add(new Double(1.0));
                    }
                    else
                    {
                        daWaveform.getValues().add(new Double(0.0));
                    }
                }
            }
        }
        daWaveform.setXAxisStart(this.xAxisStart);
        if ( this.xAxisDeltas != null && this.xAxisDeltas.size() > 0 )
        {
            daWaveform.setXAxisDelta(this.xAxisDeltas.get(0));
        }
        return daWaveform;
    }

    /**
     * Returns DMDataSeq
     * 
     * @return converted DMDataSeq
     */
    protected DMDataSeq getDMDataSeq()
    {
        DMDataSeq dmDataSeq = new DMDataSeq();

        if ( this.valueValue != null )
        {
            if ( this.valueValue instanceof DblArrayValue )
            {
                dmDataSeq.getValues().addAll(((DblArrayValue) this.valueValue).getValues());
            }
            else if ( this.valueValue instanceof LongArrayValue )
            {
                for (Long value : ((LongArrayValue) this.valueValue).getValues())
                {
                    dmDataSeq.getValues().add((double) value);
                }
            }
            else if ( this.valueValue instanceof FloatArrayValue )
            {
                for (Float value : ((FloatArrayValue) this.valueValue).getValues())
                {
                    dmDataSeq.getValues().add((double) value);
                }
            }
            else if ( this.valueValue instanceof IntArrayValue )
            {
                for (Integer value : ((IntArrayValue) this.valueValue).getValues())
                {
                    dmDataSeq.getValues().add((double) value);
                }
            }
            else if ( this.valueValue instanceof BooleanArrayValue )
            {
                for (Boolean value : ((BooleanArrayValue) this.valueValue).getValues())
                {
                    if ( value )
                    {
                        dmDataSeq.getValues().add(1.0);
                    }
                    else
                    {
                        dmDataSeq.getValues().add(0.0);
                    }
                }
            }
        }
        dmDataSeq.setXAxisStart(this.xAxisStart);
        if ( this.xAxisDeltas != null )
        {
            dmDataSeq.getXAxisDeltas().addAll(this.xAxisDeltas);
        }

        return dmDataSeq;

    }

    /**
     * Returns DMVector
     * 
     * @return converted DMVector
     * @throws OsacbmConversionException conversion error
     */
    @SuppressWarnings("nls")
    protected DMVector getDMVector()
            throws OsacbmConversionException
    {
        DMVector dmVector = new DMVector();

        if ( this.valueValue != null )
        {
            if ( this.valueValue instanceof DblArrayValue )
            {
                if ( ((DblArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    dmVector.setValue(((DblArrayValue) this.valueValue).getValues().get(
                            ((DblArrayValue) this.valueValue).getValues().size() - 1));
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of DblArrayValue to a DMVector");
                }
            }
            else if ( this.valueValue instanceof LongArrayValue )
            {
                if ( ((LongArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    dmVector.setValue(((LongArrayValue) this.valueValue).getValues()
                            .get(((LongArrayValue) this.valueValue).getValues().size() - 1).intValue());
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of LongArrayValue to a DMVector");
                }
            }
            else if ( this.valueValue instanceof FloatArrayValue )
            {
                if ( ((FloatArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    dmVector.setValue(((FloatArrayValue) this.valueValue).getValues().get(
                            ((FloatArrayValue) this.valueValue).getValues().size() - 1));
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of FloatArrayValue to a DMVector");
                }
            }
            else if ( this.valueValue instanceof IntArrayValue )
            {
                if ( ((IntArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    dmVector.setValue(((IntArrayValue) this.valueValue).getValues().get(
                            ((IntArrayValue) this.valueValue).getValues().size() - 1));
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of IntArrayValue to a DMVector");
                }
            }
            else if ( this.valueValue instanceof BooleanArrayValue )
            {
                if ( ((BooleanArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    if ( ((BooleanArrayValue) this.valueValue).getValues().get(
                            ((BooleanArrayValue) this.valueValue).getValues().size() - 1) )
                    {
                        dmVector.setValue(1.0);
                    }
                    else
                    {
                        dmVector.setValue(0.0);
                    }
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of BooleanArrayValue to a DMVector");
                }
            }
            else
            {
                throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName()
                        + " to DMVector value");
            }
        }
        if ( this.xAxisDeltas != null )
        {
            dmVector.setXValue(this.xAxisStart + this.xAxisDeltas.get(this.xAxisDeltas.size() - 1));
        }
        return dmVector;
    }

    @SuppressWarnings("nls")
    private DAString getDAString()
            throws OsacbmConversionException
    {

        DAString daString = new DAString();

        if ( this.valueValue != null )
        {
            if ( this.valueValue instanceof DblArrayValue )
            {
                if ( ((DblArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daString.setValue(((DblArrayValue) this.valueValue).getValues()
                            .get(((DblArrayValue) this.valueValue).getValues().size() - 1).toString());
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of DblArrayValue to a DMVector");
                }
            }
            else if ( this.valueValue instanceof LongArrayValue )
            {
                if ( ((LongArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daString.setValue(((LongArrayValue) this.valueValue).getValues()
                            .get(((LongArrayValue) this.valueValue).getValues().size() - 1).intValue()
                            + "");
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of LongArrayValue to a DMVector");
                }
            }
            else if ( this.valueValue instanceof FloatArrayValue )
            {
                if ( ((FloatArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daString.setValue(((FloatArrayValue) this.valueValue).getValues()
                            .get(((FloatArrayValue) this.valueValue).getValues().size() - 1).toString());
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of FloatArrayValue to a DMVector");
                }
            }
            else if ( this.valueValue instanceof IntArrayValue )
            {
                if ( ((IntArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daString.setValue(((IntArrayValue) this.valueValue).getValues()
                            .get(((IntArrayValue) this.valueValue).getValues().size() - 1).toString());
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of IntArrayValue to a DMVector");
                }
            }
            else if ( this.valueValue instanceof BooleanArrayValue )
            {
                if ( ((BooleanArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    if ( ((BooleanArrayValue) this.valueValue).getValues().get(
                            ((BooleanArrayValue) this.valueValue).getValues().size() - 1) )
                    {
                        daString.setValue("true");
                    }
                    else
                    {
                        daString.setValue("false");
                    }
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of BooleanArrayValue to a DAString");
                }
            }
            else if ( this.valueValue instanceof StringArrayValue )
            {
                if ( ((StringArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daString.setValue(((StringArrayValue) this.valueValue).getValues().get(
                            ((StringArrayValue) this.valueValue).getValues().size() - 1));
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of StringArrayValue to a DMVector");
                }
            }
            else
            {
                throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName()
                        + " to DAString value");
            }
        }

        return daString;

    }
}
