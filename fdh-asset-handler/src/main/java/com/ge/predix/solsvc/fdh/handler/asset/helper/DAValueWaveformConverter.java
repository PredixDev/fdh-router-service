package com.ge.predix.solsvc.fdh.handler.asset.helper;

import java.util.ArrayList;
import java.util.List;

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
 * This class converts DAValueWaveform or DAWaveform into supported OSA data types
 */
@SuppressWarnings("nls")
public class DAValueWaveformConverter extends BaseConverter
{

    private static final String CONVERTING = "Converting "; //$NON-NLS-1$
    private Value               valueValue;
    private double              xAxisDelta;
    private DataEvent           srcDataEvent;

    /**
     * Initializes the converter for a DAValueWaveform object
     * 
     * @param daValueWaveform dataEvent to convert
     */
    public DAValueWaveformConverter(DAValueWaveform daValueWaveform)
    {
        this.srcDataEvent = daValueWaveform;
        this.valueValue = daValueWaveform.getValues();
        if ( daValueWaveform.getXAxisDelta() instanceof DoubleValue )
        {
            this.xAxisDelta = ((DoubleValue) daValueWaveform.getXAxisDelta()).getValue();
        }
        this.xAxisStart = ((DoubleValue) daValueWaveform.getXAxisStart()).getValue();
    }

    /**
     * Initializes the converter for a DAWaveform object
     * @param daWaveform -
     * 
     */
    public DAValueWaveformConverter(DAWaveform daWaveform)
    {
        this.srcDataEvent = daWaveform;
        this.valueValue = new DblArrayValue();
        ((DblArrayValue) this.valueValue).setValues(daWaveform.getValues());
        this.xAxisDelta = daWaveform.getXAxisDelta();
        this.xAxisStart = daWaveform.getXAxisStart();
    }

    /**
     * Converts DAValueWaveform to destination DataEvent type
     * 
     * @param clazz class of target DataEvent
     * @return converted DataEvent
     */
    @SuppressWarnings("rawtypes")
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
        else if ( clazz.equals(DAInt.class) )
        {
            converted = getDAInt();
        }
        else if ( clazz.equals(DAReal.class) )
        {
            converted = getDAReal();
        }
        else if ( clazz.equals(DAWaveform.class) )
        {
            converted = getDAWaveform();
        }
        else if ( clazz.equals(DMBool.class) )
        {
            converted = getDMBool();
        }
        else if ( clazz.equals(DAValueDataSeq.class) )
        {
            converted = getDAValueDataSeq();
        }
        else if ( clazz.equals(DMDataSeq.class) )
        {
            converted = getDMDatSeq();
        }
        else if ( clazz.equals(DMReal.class) )
        {
            converted = getDMReal();
        }
        else if ( clazz.equals(DAVector.class) )
        {
            converted = getDAVector();
        }
        else if ( clazz.equals(DMVector.class) )
        {
            converted = getDMVector();
        }
        else if ( clazz.equals(DMInt.class) )
        {
            converted = getDMInt();
        }
        else if ( clazz.equals(DAValueWaveform.class) )
        {
            converted = getDAValueWaveform();
        }
        else if ( clazz.equals(DAString.class) )
        {
            converted = getDAString();
        }

        copyBaseDataEventProperties(this.srcDataEvent, converted, clazz);

        return converted;
    }

    private DataEvent getDAString()
            throws OsacbmConversionException
    {
        DAString daString = new DAString();

        if ( this.valueValue != null )
        {
            if ( this.valueValue instanceof DblArrayValue )
            {
                if ( ((DblArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daString.setValue(((DblArrayValue) this.valueValue).getValues().get(
                            (((DblArrayValue) this.valueValue).getValues().size() - 1))
                            + "");
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of DblArrayValue to a DAString");
                }
            }
            else if ( this.valueValue instanceof LongArrayValue )
            {
                if ( ((LongArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daString.setValue(((LongArrayValue) this.valueValue).getValues().get(
                            ((LongArrayValue) this.valueValue).getValues().size() - 1)
                            + "");
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of LongArrayValue to a DAString");
                }
            }
            else if ( this.valueValue instanceof FloatArrayValue )
            {
                if ( ((FloatArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daString.setValue(((FloatArrayValue) this.valueValue).getValues().get(
                            ((FloatArrayValue) this.valueValue).getValues().size() - 1)
                            + "");
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of FloatArrayValue to a DAString");
                }
            }
            else if ( this.valueValue instanceof IntArrayValue )
            {
                if ( ((IntArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    daString.setValue(((IntArrayValue) this.valueValue).getValues().get(
                            ((IntArrayValue) this.valueValue).getValues().size() - 1)
                            + "");
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of IntArrayValue to a DAString");
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
                            ((StringArrayValue) this.valueValue).getValues().size() - 1)
                            + "");
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of StringArrayValue to a DAString");
                }
            }
            else
            {
                throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName() + " to DAString value");
            }
        }
        return daString;
    }

    /**
     * Converts DAValueWaveform to DADataSeq
     * 
     * @return DADataSeq
     * @throws OsacbmConversionException -
     */
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
            throw new OsacbmConversionException("Converting null Value to DADataSeq values");
        }
        daDataSeq.setXAxisStart(this.xAxisStart);
        daDataSeq.setXAxisDeltas(new ArrayList<Double>());
        //
        if(  this.srcDataEvent instanceof DAValueWaveform ){
            Value xAxisDeltasValue = null; 
            if( ((DAValueWaveform)this.srcDataEvent).getXAxisDelta() instanceof DoubleValue){
                for(int  i = 0 ; i < daDataSeq.getValues().size(); i++){
                    daDataSeq.getXAxisDeltas().add( this.xAxisDelta);
                }
            }
            if( ((DAValueWaveform)this.srcDataEvent).getXAxisDelta() instanceof DblArrayValue){
                xAxisDeltasValue = ((DAValueWaveform)this.srcDataEvent).getXAxisDelta();
                daDataSeq.getXAxisDeltas().addAll( ((DblArrayValue)xAxisDeltasValue).getValues());
            }
        }
        if(  this.srcDataEvent instanceof DAWaveform ){
            
                for(int  i = 0 ; i < daDataSeq.getValues().size(); i++){
                    daDataSeq.getXAxisDeltas().add( this.xAxisDelta);
                }
            
        }
        return daDataSeq;

    }

    /**
     * @return -
     * @throws OsacbmConversionException -
     */
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
                            (((DblArrayValue) this.valueValue).getValues().size() - 1)) != 0.0);
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
                throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName() + " to DABool value");
            }
        }
        return daBool;
    }

    /**
     * @return -
     * @throws OsacbmConversionException -
     */
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
                    daInt.setValue((new Long(java.lang.Math.round(((DblArrayValue) this.valueValue).getValues().get(
                            (((DblArrayValue) this.valueValue).getValues().size() - 1))))).intValue());
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of DblArrayValue to a DAInt");
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
                    daInt.setValue((new Long(java.lang.Math.round(((FloatArrayValue) this.valueValue).getValues().get(
                            ((FloatArrayValue) this.valueValue).getValues().size() - 1)))).intValue());
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
                    throw new OsacbmConversionException("Converting empty array of BooleanArrayValue to a DAInt");
                }
            }
            else
            {
                throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName() + " to DAInt value");
            }
        }
        return daInt;
    }

    /**
     * @return -
     * @throws OsacbmConversionException -
     */
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
                            (((DblArrayValue) this.valueValue).getValues().size() - 1)));
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of DblArrayValue to a DAReal");
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
                    throw new OsacbmConversionException("Converting empty array of BooleanArrayValue to a DAReal");
                }
            }
            else
            {
                throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName() + " to DAReal value");
            }
        }
        return daReal;
    }

    /**
     * @return -
     * @throws OsacbmConversionException -
     */
    protected DAValueDataSeq getDAValueDataSeq()
            throws OsacbmConversionException
    {
        DAValueDataSeq daValueDataSeq = new DAValueDataSeq();

        daValueDataSeq.setValues(this.valueValue);
        if ( this.valueValue != null )
        {
            daValueDataSeq.setXAxisStart(new DoubleValue());
            ((DoubleValue) daValueDataSeq.getXAxisStart()).setValue(this.xAxisStart);
            daValueDataSeq.setXAxisDeltas(new DblArrayValue());
            Double xAxisDeltaValue = null;
            if ( this.srcDataEvent instanceof DAValueWaveform
                    && ((DAValueWaveform) this.srcDataEvent).getXAxisDelta() != null )
            {
            	if ((((DAValueWaveform) this.srcDataEvent).getXAxisDelta() instanceof DoubleValue)) 
            	{
                	xAxisDeltaValue = ((DoubleValue)(((DAValueWaveform) this.srcDataEvent).getXAxisDelta())).getValue();
            	} else
                {
            		throw new OsacbmConversionException("Converting from DAValueWaveform and the xAxisDelta is not a DoubleValue.  The plaform only supports DAValueWaveforms with DoubleVaue for XAxisDelta.");
                }
            }
            if ( this.srcDataEvent instanceof DAWaveform )
            {
            	xAxisDeltaValue = ((DAWaveform)this.srcDataEvent).getXAxisDelta();
            }

            if ( xAxisDeltaValue != null) 
            {
        		List values = Utils.getValuesList(this.srcDataEvent);
        		if (values != null) 
        		{
        			daValueDataSeq.setXAxisDeltas(new DblArrayValue());
                	for (int i = 0; i < values.size(); i++) 
                	{
                		((DblArrayValue)daValueDataSeq.getXAxisDeltas()).getValues().add(xAxisDeltaValue);
                	}
        		}            		
            }
        }
        return daValueDataSeq;
    }

    /**
     * @return -
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
                        daWaveform.getValues().add(1.0);
                    }
                    else
                    {
                        daWaveform.getValues().add(0.0);
                    }
                }
            }
        }
        daWaveform.setXAxisStart(this.xAxisStart);
        daWaveform.setXAxisDelta(this.xAxisDelta);
        return daWaveform;

    }

    /**
     * @return -
     */
    protected DAValueWaveform getDAValueWaveform()
    {
        DAValueWaveform daValueWaveform = new DAValueWaveform();
        // we must be converting from DAWaveform to DAValueWaveform, thus valueValue must be a DblArrayValue
        daValueWaveform.setValues(this.valueValue);
        daValueWaveform.setXAxisStart(new DblArrayValue());
        daValueWaveform.setXAxisDelta(new DblArrayValue());
        ((DblArrayValue) daValueWaveform.getXAxisStart()).getValues().add(this.xAxisStart);
        ((DblArrayValue) daValueWaveform.getXAxisDelta()).getValues().add(this.xAxisDelta);
        return daValueWaveform;

    }

    /**
     * @return -
     * @throws OsacbmConversionException -
     */
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
                            (((DblArrayValue) this.valueValue).getValues().size() - 1)) != 0.0);
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
                throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName() + " to DMBool value");
            }
        }
        return dmBool;
    }

    /**
     * @return -
     * @throws OsacbmConversionException -
     */
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
                    dmInt.setValue((new Long(java.lang.Math.round(((DblArrayValue) this.valueValue).getValues().get(
                            (((DblArrayValue) this.valueValue).getValues().size() - 1))))).intValue());
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of DblArrayValue to a DMInt");
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
                    dmInt.setValue((new Long(java.lang.Math.round(((FloatArrayValue) this.valueValue).getValues().get(
                            ((FloatArrayValue) this.valueValue).getValues().size() - 1)))).intValue());
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
                    throw new OsacbmConversionException("Converting empty array of BooleanArrayValue to a DMInt");
                }
            }
            else
            {
                throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName() + " to DMInt value");
            }
        }
        return dmInt;
    }

    /**
     * @return -
     * @throws OsacbmConversionException -
     */
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
                            (((DblArrayValue) this.valueValue).getValues().size() - 1)));
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of DblArrayValue to a DMReal");
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
                    throw new OsacbmConversionException("Converting empty array of BooleanArrayValue to a DMReal");
                }
            }
            else
            {
                throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName() + " to DMReal value");
            }
        }
        return dmReal;
    }

    /**
     * @return -
     * @throws OsacbmConversionException -
     */
    protected DMDataSeq getDMDatSeq()
            throws OsacbmConversionException
    {
        DMDataSeq typedTo = new DMDataSeq();

        if ( this.valueValue != null )
        {
            if ( this.valueValue instanceof DblArrayValue )
            {
                typedTo.getValues().addAll(((DblArrayValue) this.valueValue).getValues());
            }
        }
        else if ( this.valueValue instanceof LongArrayValue )
        {
            for (Long value : ((LongArrayValue) this.valueValue).getValues())
            {
                typedTo.getValues().add((double) value);
            }
        }
        else if ( this.valueValue instanceof FloatArrayValue )
        {
            for (Float value : ((FloatArrayValue) this.valueValue).getValues())
            {
                typedTo.getValues().add((double) value);
            }
        }
        else if ( this.valueValue instanceof IntArrayValue )
        {
            for (Integer value : ((IntArrayValue) this.valueValue).getValues())
            {
                typedTo.getValues().add((double) value);
            }
        }
        else if ( this.valueValue instanceof BooleanArrayValue )
        {
            for (Boolean value : ((BooleanArrayValue) this.valueValue).getValues())
            {
                if ( value )
                {
                    typedTo.getValues().add(1.0);
                }
                else
                {
                    typedTo.getValues().add(0.0);
                }
            }
        }
        else
        {
            throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName() + " to DMDataSeq values");
        }
        typedTo.setXAxisStart(this.xAxisStart);
        if ( this.srcDataEvent instanceof DAWaveform )
        {
            for (Double fromValue : ((DblArrayValue) this.valueValue).getValues())
            {
                typedTo.getXAxisDeltas().add(this.xAxisDelta);
            }
        }
        else
        {
            typedTo.getXAxisDeltas().add(this.xAxisDelta);
        }
        return typedTo;
    }

    /**
     * @return -
     * @throws OsacbmConversionException -
     */
    protected DMVector getDMVector()
            throws OsacbmConversionException
    {
        DMVector typedTo = new DMVector();

        if ( this.valueValue != null )
        {
            if ( this.valueValue instanceof DblArrayValue )
            {
                if ( ((DblArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    typedTo.setValue(((DblArrayValue) this.valueValue).getValues().get(
                            (((DblArrayValue) this.valueValue).getValues().size() - 1)));
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
                    typedTo.setValue(((LongArrayValue) this.valueValue).getValues()
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
                    typedTo.setValue(((FloatArrayValue) this.valueValue).getValues().get(
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
                    typedTo.setValue(((IntArrayValue) this.valueValue).getValues().get(
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
                        typedTo.setValue(1.0);
                    }
                    else
                    {
                        typedTo.setValue(0.0);
                    }
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of BooleanArrayValue to a DMVector");
                }
            }
            else
            {
                throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName() + " to DMVector value");
            }
        }

        typedTo.setXValue(this.xAxisStart + this.xAxisDelta);

        return typedTo;
    }

    /**
     * @return -
     * @throws OsacbmConversionException -
     */
    protected DAVector getDAVector()
            throws OsacbmConversionException
    {
        DAVector typedTo = new DAVector();

        if ( this.valueValue != null )
        {
            if ( this.valueValue instanceof DblArrayValue )
            {
                if ( ((DblArrayValue) this.valueValue).getValues().size() > 0 )
                {
                    typedTo.setValue(((DblArrayValue) this.valueValue).getValues().get(
                            (((DblArrayValue) this.valueValue).getValues().size() - 1)));
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
                    typedTo.setValue(((LongArrayValue) this.valueValue).getValues()
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
                    typedTo.setValue(((FloatArrayValue) this.valueValue).getValues().get(
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
                    typedTo.setValue(((IntArrayValue) this.valueValue).getValues().get(
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
                        typedTo.setValue(1.0);
                    }
                    else
                    {
                        typedTo.setValue(0.0);
                    }
                }
                else
                {
                    throw new OsacbmConversionException("Converting empty array of BooleanArrayValue to a DMVector");
                }
            }
            else
            {
                throw new OsacbmConversionException(CONVERTING + this.valueValue.getClass().getName() + " to DMVector value");
            }
        }

        typedTo.setXValue(this.xAxisStart + this.xAxisDelta);

        return typedTo;
    }
}
