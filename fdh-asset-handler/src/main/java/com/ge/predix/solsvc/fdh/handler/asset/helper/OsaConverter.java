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

/**
 * Main OSA Converter
 */
public class OsaConverter
{

    /**
     * Initializes the converter
     * @param src -
     * @param destClassType DataEvent to convert
     * @return -
     * @throws OsacbmConversionException  when conversion error
     */
    @SuppressWarnings("nls")
    public DataEvent convert(DataEvent src, Class<?> destClassType)
            throws OsacbmConversionException
    {

        if ( src.getClass().equals(destClassType) )
        {
            return src;
        }

        if ( src instanceof DABool )
        {
            return new DABoolConverter((DABool) src).convertTo(destClassType);
        }

        if ( src instanceof DMBool )
        {
            return new DMBoolConverter((DMBool) src).convertTo(destClassType);
        }

        if ( src instanceof DMReal )
        {
            return new DMRealConverter((DMReal) src).convertTo(destClassType);
        }

        if ( src instanceof DAReal )
        {
            return new DARealConverter((DAReal) src).convertTo(destClassType);
        }

        if ( src instanceof DMInt )
        {
            return new DMIntConverter((DMInt) src).convertTo(destClassType);
        }

        if ( src instanceof DAInt )
        {
            return new DAIntConverter((DAInt) src).convertTo(destClassType);
        }

        if ( src instanceof DADataSeq )
        {
            return new DADataSeqConverter((DADataSeq) src).convertTo(destClassType);
        }

        if ( src instanceof DMDataSeq )
        {
            return new DMDataSeqConverter((DMDataSeq) src).convertTo(destClassType);
        }

        if ( src instanceof DAValueDataSeq )
        {
            return new DAValueDataSeqConverter((DAValueDataSeq) src).convertTo(destClassType);
        }

        if ( src instanceof DAValueWaveform )
        {
            return new DAValueWaveformConverter((DAValueWaveform) src).convertTo(destClassType);
        }

        if ( src instanceof DAWaveform )
        {
            return new DAValueWaveformConverter((DAWaveform) src).convertTo(destClassType);
        }

        if ( src instanceof DAVector )
        {
            return new DAVectorConverter((DAVector) src).convertTo(destClassType);
        }

        if ( src instanceof DMVector )
        {
            return new DMVectorConverter((DMVector) src).convertTo(destClassType);
        }

        if ( src instanceof DAString )
        {
            return new DAStringConverter((DAString) src).convertTo(destClassType);
        }

        throw new OsacbmConversionException("Conversion from " + src.getClass().getSimpleName() + " to "
                + destClassType.getSimpleName() + " is not supported");
    }

}
