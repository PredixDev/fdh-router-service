/*
 * Copyright (C) 2012 General Electric
 * All rights reserved
 */
package com.ge.predix.solsvc.fdh.handler.asset.helper;

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
import org.mimosa.osacbmv3_3.OsacbmDataType;

/**
 * This class provides utility methods for converting a type of DataEvent to
 * another.
 */
public class OsaDataTypeConverter {

    /**
     * Prevent instantiation of this class
     */
    private OsaDataTypeConverter() {
    }

    /**
     * Sets DataEvent properties
     *
     * @param fromDE source DataEvent
     * @param toDE   target DataEvent
     */
    public static void setDataEventProperties(DataEvent fromDE, DataEvent toDE) {
        toDE.setAlertStatus(fromDE.isAlertStatus());
        toDE.setConfid(fromDE.getConfid());
        toDE.setId(fromDE.getId());
        toDE.setSequenceNum(fromDE.getSequenceNum());
        toDE.setSite(fromDE.getSite());
        toDE.setTime(fromDE.getTime());
    }

    private static OsaConverter osaConverter = new OsaConverter();

    /**
     * Converts an instance of DataEvent into another DataEvent-type instance
     *
     * @param from   DataEvent to convert
     * @param toType type of DataEvent to convert to
     * @return converted DataEvent of type param toType
     * @throws OsacbmConversionException when conversion fails
     */
    public static DataEvent convertToType(DataEvent from, Class<?> toType)
            throws OsacbmConversionException {
        return osaConverter.convert(from, toType);
    }

    /**
     * Converts an instance of DataEvent into another DataEvent-type instance
     *
     * @param from   DataEvent to convert
     * @param toType the osacbm type to convert the DataEvent to
     * @return converted DataEvent of type param toType
     * @throws OsacbmConversionException when conversion fails
     */
    @SuppressWarnings("nls")
    public static DataEvent convertToType(DataEvent from, OsacbmDataType toType)
            throws OsacbmConversionException {
        if (toType.compareTo(OsacbmDataType.DA_UNKNOWN) == 0
                || toType.compareTo(OsacbmDataType.DM_UNKNOWN) == 0
                || toType.compareTo(OsacbmDataType.HA_UNKNOWN) == 0
                || toType.compareTo(OsacbmDataType.PA_UNKNOWN) == 0
                || toType.compareTo(OsacbmDataType.AG_UNKNOWN) == 0

                ) {
            return from;
        }
        if (toType.compareTo(OsacbmDataType.DA_WAVEFORM) == 0) {
            return osaConverter.convert(from, DAWaveform.class);
        }
        if (toType.compareTo(OsacbmDataType.DA_VECTOR) == 0) {
            return osaConverter.convert(from, DAVector.class);
        }
        if (toType.compareTo(OsacbmDataType.DM_REAL) == 0) {
            return osaConverter.convert(from, DMReal.class);
        }
        if (toType.compareTo(OsacbmDataType.DA_DATA_SEQ) == 0) {
            return osaConverter.convert(from, DADataSeq.class);
        }
        if (toType.compareTo(OsacbmDataType.DA_REAL) == 0) {
            return osaConverter.convert(from, DAReal.class);
        }
        if (toType.compareTo(OsacbmDataType.DA_INT) == 0) {
            return osaConverter.convert(from, DAInt.class);
        }
        if (toType.compareTo(OsacbmDataType.DA_BOOL) == 0) {
            return osaConverter.convert(from, DABool.class);
        }
        if (toType.compareTo(OsacbmDataType.DM_VECTOR) == 0) {
            return osaConverter.convert(from, DMVector.class);
        }
        if (toType.compareTo(OsacbmDataType.DM_REAL) == 0) {
            return osaConverter.convert(from, DMReal.class);
        }
        if (toType.compareTo(OsacbmDataType.DM_INT) == 0) {
            return osaConverter.convert(from, DMInt.class);
        }
        if (toType.compareTo(OsacbmDataType.DM_BOOL) == 0) {
            return osaConverter.convert(from, DMBool.class);
        }
        if (toType.compareTo(OsacbmDataType.DM_DATA_SEQ) == 0) {
            return osaConverter.convert(from, DMDataSeq.class);
        }
        if (toType.compareTo(OsacbmDataType.DA_STRING) == 0) {
            return osaConverter.convert(from, DAString.class);
        }
        if (toType.compareTo(OsacbmDataType.DA_VALUE_WAVEFORM) == 0) {
            return osaConverter.convert(from, DAValueWaveform.class);
        }
        if (toType.compareTo(OsacbmDataType.DA_VALUE_DATA_SEQ) == 0) {
            return osaConverter.convert(from, DAValueDataSeq.class);
        }

        throw new OsacbmConversionException("Conversion from "
                + from.getClass().getSimpleName() + " to " + toType.name()
                + " is not supported");

    }

}
