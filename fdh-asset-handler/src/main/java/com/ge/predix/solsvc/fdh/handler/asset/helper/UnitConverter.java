/**
 * Copyright (C) 2012 General Electric Company
 * All rights reserved.
 *
 */
package com.ge.predix.solsvc.fdh.handler.asset.helper;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mimosa.osacbmv3_3.DADataSeq;
import org.mimosa.osacbmv3_3.DAInt;
import org.mimosa.osacbmv3_3.DAReal;
import org.mimosa.osacbmv3_3.DAValueDataSeq;
import org.mimosa.osacbmv3_3.DAValueWaveform;
import org.mimosa.osacbmv3_3.DAVector;
import org.mimosa.osacbmv3_3.DAWaveform;
import org.mimosa.osacbmv3_3.DMDataSeq;
import org.mimosa.osacbmv3_3.DMInt;
import org.mimosa.osacbmv3_3.DMReal;
import org.mimosa.osacbmv3_3.DMVector;
import org.mimosa.osacbmv3_3.DataEvent;
import org.mimosa.osacbmv3_3.DblArrayValue;
import org.mimosa.osacbmv3_3.DoubleValue;
import org.mimosa.osacbmv3_3.FloatArrayValue;
import org.mimosa.osacbmv3_3.FloatValue;
import org.mimosa.osacbmv3_3.IntArrayValue;
import org.mimosa.osacbmv3_3.IntValue;
import org.mimosa.osacbmv3_3.LongArrayValue;
import org.mimosa.osacbmv3_3.LongValue;
import org.mimosa.osacbmv3_3.ShortArrayValue;
import org.mimosa.osacbmv3_3.ShortValue;
import org.mimosa.osacbmv3_3.Value;

import com.ge.predix.entity.vo.unitcode.UnitCodeVO;

/**
 * This class provides a method <code>convertDataEvent</code> for converting fromUnit, toUnit based on the UOM table values. Basically, an analytic inputs are
 * expressed in terms of units of measurements. As a result, as the analytic is computing its output, it may need data in certain units of measurements and thus
 * it will invoke the <code>convertDataEvent</code> method to get the data in the proper units.
 */
public class UnitConverter
        implements IUnitConverter {

    /**
     * keeps track of all unit code found in the database
     */
    private static Map<String, UnitCodeVO> unitCodeMap = null;

    /**
     * @param unitCodeVOList -
     */
    public UnitConverter(List<UnitCodeVO> unitCodeVOList) {
        init(unitCodeVOList);
    }

    /**
     * @param unitCodeMap -
     */
    public UnitConverter(Map<String, UnitCodeVO> unitCodeMap) {
        this.unitCodeMap = unitCodeMap;
    }

    @SuppressWarnings("nls")
    @Override
    public <T> List<T> convert(String actualUnitName, String expectedUnitName,
                               List<T> valueToBeConverted) {
        int i = 0;
        UnitCodeVO actualUnitCode = lookupUnitCode(actualUnitName);
        if (actualUnitCode == null)
            throw new UnsupportedOperationException("Unable to find UnitConversion info for actualUnitName=" + actualUnitName);

        UnitCodeVO expectedUnitCode = lookupUnitCode(expectedUnitName);
        if (expectedUnitCode == null)
            throw new UnsupportedOperationException("Unable to find UnitConversion info for expectedUnitName=" + expectedUnitName);

        for (T valToBeConverted : valueToBeConverted) {
            double valToBeConvertedAsDouble = 0;
            if (valToBeConverted instanceof Double) {
                valToBeConvertedAsDouble = (Double) valToBeConverted;
            } else if (valToBeConverted instanceof Integer) {
                valToBeConvertedAsDouble = ((Integer)valToBeConverted).doubleValue();
            } else if (valToBeConverted instanceof Short) {
                valToBeConvertedAsDouble = ((Short)valToBeConverted).doubleValue();
            } else if (valToBeConverted instanceof Long) {
                valToBeConvertedAsDouble = ((Long)valToBeConverted).doubleValue();
            } else if (valToBeConverted instanceof Float) {
                valToBeConvertedAsDouble = ((Float)valToBeConverted).doubleValue();
            }

            Double finalConvertedValueAsDouble;
            Double convertedValueAsDouble = valToBeConvertedAsDouble * actualUnitCode.getConvertScaleM() + actualUnitCode
                    .getConvertOffestB();
            if (expectedUnitCode.getStandardType().equals("Y")) {
                // apply this formula (y = mx + b) since it is the standard type we are converting to.
                finalConvertedValueAsDouble = convertedValueAsDouble;
            } else {
                // x = (y -b)/m
                finalConvertedValueAsDouble = (convertedValueAsDouble - expectedUnitCode.getConvertOffestB())
                        / expectedUnitCode.getConvertScaleM();
            }
            if (valToBeConverted instanceof Double) {
            	valueToBeConverted.set(i, (T) finalConvertedValueAsDouble);
            } else if (valToBeConverted instanceof Integer) {
            	valueToBeConverted.set(i, (T) (new Integer(finalConvertedValueAsDouble.intValue())));
            } else if (valToBeConverted instanceof Short) {
            	valueToBeConverted.set(i, (T) (new Short(finalConvertedValueAsDouble.shortValue())));
            } else if (valToBeConverted instanceof Long) {
            	valueToBeConverted.set(i, (T) (new Long(finalConvertedValueAsDouble.longValue())));
            } else if (valToBeConverted instanceof Float) {
            	valueToBeConverted.set(i, (T) (new Float(finalConvertedValueAsDouble.floatValue())));
            }
            
            i++;
        }
        return valueToBeConverted;

    }


    @Override
    public double convert(String actualUnitName, String expectedUnitName, double valueToBeConverted) {

        double convertedValue = 0;
        double finalConvertedValue = 0;

        UnitCodeVO actualUnitCode = lookupUnitCode(actualUnitName);
        if (actualUnitCode == null)
            throw new UnsupportedOperationException("Unable to find UnitConversion info for actualUnitName=" + actualUnitName);

        UnitCodeVO expectedUnitCode = lookupUnitCode(expectedUnitName);
        if (expectedUnitCode == null)
            throw new UnsupportedOperationException("Unable to find UnitConversion info for expectedUnitName=" + expectedUnitName);


        convertedValue = valueToBeConverted * actualUnitCode.getConvertScaleM() + actualUnitCode.getConvertOffestB();

        if (expectedUnitCode.getStandardType().equals("Y")) {
            // apply this formula (y = mx + b) since it is the standard type we are converting to.
            finalConvertedValue = convertedValue;

        } else {
            // x = (y -b)/m
            finalConvertedValue = (convertedValue - expectedUnitCode.getConvertOffestB())
                    / expectedUnitCode.getConvertScaleM();

        }

        return finalConvertedValue;
    }

    /**
     * @param unitName
     * @return unitCode object for the given lookupCode
     */
    private UnitCodeVO lookupUnitCode(String unitName) {

        return unitCodeMap.get(unitName);

    }

    /**
     * @param unitCodeVOList
     */
    private void init(List<UnitCodeVO> unitCodeVOList) {

        unitCodeMap = new HashMap<String, UnitCodeVO>();

        for (UnitCodeVO o : unitCodeVOList) {
            unitCodeMap.put(o.getUnitName(), o);
        }
    }

    @Override
    public void convertDataEvent(String actualUnitName, String expectedUnitName, DataEvent valueToBeConverted) {

        if (actualUnitName == null || expectedUnitName == null) {
            return;
        }
        if (unitCodeMap == null || unitCodeMap.size() == 0) {

            return;
        }
        if (actualUnitName.equalsIgnoreCase(expectedUnitName)) {

            return;
        }

        // call the helper method to perform conversion for valueToBeConverted
        convert(actualUnitName, expectedUnitName, valueToBeConverted);
    }

    /**
     * This method does the conversion for the dataEvent object - it is the helper method.
     *
     * @param valueToBeConverted
     * @param expectedUnitName
     * @param valueToBeConverted
     */
    private void convert(String actualUnitName, String expectedUnitName, DataEvent valueToBeConverted) {

        int i = 0;

        // get the source data
        if (valueToBeConverted instanceof DADataSeq) {
            // DADataSeq
            convert(actualUnitName, expectedUnitName, ((DADataSeq) valueToBeConverted).getValues());

            return;
        }

        if (valueToBeConverted instanceof DAInt) {
            // DAInt
            ((DAInt) valueToBeConverted).setValue((int) convert(actualUnitName, expectedUnitName, ((DAInt) valueToBeConverted).getValue()));
            return;
        }

        if (valueToBeConverted instanceof DAReal) {
            // DAReal
            ((DAReal) valueToBeConverted).setValue(convert(actualUnitName, expectedUnitName, ((DAReal) valueToBeConverted).getValue()));

            return;
        }

        Value valueValue;
        if (valueToBeConverted instanceof DAValueDataSeq) {
            // DAValueDataSeq
            valueValue = ((DAValueDataSeq) valueToBeConverted).getValues();
            if (valueValue != null) {
                if (valueValue instanceof DblArrayValue) {
                    convert(actualUnitName, expectedUnitName, ((DblArrayValue) valueValue).getValues());
                } else if (valueValue instanceof LongValue) {
                    ((LongValue) valueValue).setValue((long) convert(actualUnitName, expectedUnitName, ((LongValue) valueValue).getValue()));
                } else if (valueValue instanceof ShortValue) {
                    ((ShortValue) valueValue).setValue((short) convert(actualUnitName, expectedUnitName, ((ShortValue) valueValue).getValue()));
                } else if (valueValue instanceof FloatValue) {
                    ((FloatValue) valueValue).setValue((float) convert(actualUnitName, expectedUnitName, ((FloatValue) valueValue).getValue()));
                } else if (valueValue instanceof DoubleValue) {
                    ((DoubleValue) valueValue).setValue(convert(actualUnitName, expectedUnitName, ((DoubleValue) valueValue).getValue()));
                } else if (valueValue instanceof IntValue) {
                    ((IntValue) valueValue).setValue((int) convert(actualUnitName, expectedUnitName, ((IntValue) valueValue).getValue()));
                } else if (valueValue instanceof ShortArrayValue) {
                    convert(actualUnitName, expectedUnitName, ((ShortArrayValue) valueValue).getValues());
                } else if (valueValue instanceof LongArrayValue) {
                    convert(actualUnitName, expectedUnitName, ((LongArrayValue) valueValue).getValues());
                } else if (valueValue instanceof FloatArrayValue) {
                    convert(actualUnitName, expectedUnitName, ((FloatArrayValue) valueValue).getValues());
                } else if (valueValue instanceof IntArrayValue) {
                    convert(actualUnitName, expectedUnitName, ((IntArrayValue) valueValue).getValues());
                }
            }
            return;
        }

        if (valueToBeConverted instanceof DAValueWaveform) {
            // DAValueWaveform
            valueValue = ((DAValueWaveform) valueToBeConverted).getValues();
            if (valueValue != null) {
                if (valueValue instanceof DblArrayValue) {
                    convert(actualUnitName, expectedUnitName, ((DblArrayValue) valueValue).getValues());
                } else if (valueValue instanceof LongValue) {
                    ((LongValue) valueValue).setValue((long) convert(actualUnitName, expectedUnitName, convert(actualUnitName, expectedUnitName, ((LongValue) valueValue).getValue())));
                } else if (valueValue instanceof ShortValue) {
                    ((ShortValue) valueValue).setValue((short) convert(actualUnitName, expectedUnitName, ((ShortValue) valueValue).getValue()));
                } else if (valueValue instanceof FloatValue) {
                    ((FloatValue) valueValue).setValue((float) convert(actualUnitName, expectedUnitName, ((FloatValue) valueValue).getValue()));
                } else if (valueValue instanceof DoubleValue) {
                    ((DoubleValue) valueValue).setValue(convert(actualUnitName, expectedUnitName, ((DoubleValue) valueValue).getValue()));
                } else if (valueValue instanceof IntValue) {
                    ((IntValue) valueValue).setValue((int) convert(actualUnitName, expectedUnitName, ((IntValue) valueValue).getValue()));
                } else if (valueValue instanceof ShortArrayValue) {
                    convert(actualUnitName, expectedUnitName, ((ShortArrayValue) valueValue).getValues());
                } else if (valueValue instanceof LongArrayValue) {
                    convert(actualUnitName, expectedUnitName, ((LongArrayValue) valueValue).getValues());
                } else if (valueValue instanceof FloatArrayValue) {
                    convert(actualUnitName, expectedUnitName, ((FloatArrayValue) valueValue).getValues());
                } else if (valueValue instanceof IntArrayValue) {
                    convert(actualUnitName, expectedUnitName, ((IntArrayValue) valueValue).getValues());
                }
            }
            return;
        }

        if (valueToBeConverted instanceof DAVector) {
            // DAVector
            ((DAVector) valueToBeConverted).setValue(convert(actualUnitName, expectedUnitName, ((DAVector) valueToBeConverted).getValue()));
            ((DAVector) valueToBeConverted).setXValue(convert(actualUnitName, expectedUnitName, ((DAVector) valueToBeConverted).getXValue()));
            return;
        }

        if (valueToBeConverted instanceof DAWaveform) {
            convert(actualUnitName, expectedUnitName, ((DAWaveform) valueToBeConverted).getValues());
            return;

        }

        if (valueToBeConverted instanceof DMDataSeq) {
            convert(actualUnitName, expectedUnitName, ((DMDataSeq) valueToBeConverted).getValues());
            return;
        }

        if (valueToBeConverted instanceof DMInt) {
            ((DMInt) valueToBeConverted).setValue((int) convert(actualUnitName, expectedUnitName, ((DMInt) valueToBeConverted).getValue()));
            return;
        }

        if (valueToBeConverted instanceof DMReal) {
            ((DMReal) valueToBeConverted).setValue(convert(actualUnitName, expectedUnitName, ((DMReal) valueToBeConverted).getValue()));
            return;
        }

        if (valueToBeConverted instanceof DMVector) {
            ((DMVector) valueToBeConverted).setValue(convert(actualUnitName, expectedUnitName, ((DMVector) valueToBeConverted).getValue()));
            ((DMVector) valueToBeConverted).setXValue(convert(actualUnitName, expectedUnitName, ((DMVector) valueToBeConverted).getXValue()));

        }
    }

}
