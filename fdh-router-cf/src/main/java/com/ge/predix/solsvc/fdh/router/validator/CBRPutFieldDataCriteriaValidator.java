/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.fdh.router.validator;

import org.springframework.stereotype.Component;

import com.ge.dsp.pm.ext.entity.selectionfilter.FieldSelectionFilter;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataCriteria;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataRequest;

/**
 * 
 * @author predix
 */
@Component
@SuppressWarnings("nls")
public class CBRPutFieldDataCriteriaValidator extends BaseValidator
{

    /**
     * @param fieldDataCriteria -
     */
    public void validatePutFieldDataCriteria(PutFieldDataCriteria fieldDataCriteria)
    {
        if ( fieldDataCriteria.getFieldData().getField() == null )
            throw new UnsupportedOperationException("fieldSelection.getField()="
                    + fieldDataCriteria.getFieldData().getField() + " not supported");

        if ( fieldDataCriteria.getFieldData().getField().getFieldIdentifier() == null )
            throw new UnsupportedOperationException("fieldSelection.getField().getFieldIdentifier()="
                    + fieldDataCriteria.getFieldData().getField().getFieldIdentifier() + " not supported");

        if ( fieldDataCriteria.getFieldData().getField().getFieldIdentifier().getSource() == null )
            throw new UnsupportedOperationException("fieldSelection.getField().getFieldIdentifier().getSource()="
                    + fieldDataCriteria.getFieldData().getField().getFieldIdentifier().getSource() + " not supported");

        if ( isSelectionFilterNull(fieldDataCriteria) )
            throw new RuntimeException(("PutFieldDataRequest is invalid isSelectionFilterNull"));

        if ( isGetAssetNull(fieldDataCriteria) )
            throw new RuntimeException(("PutFieldDataRequest is invalid isGetAssetNull"));

        if ( isSerialNumberNull(fieldDataCriteria) )
            throw new RuntimeException(("PutFieldDataRequest is invalid isSerialNumberNull"));

    }
    /**
     * @param putFieldDataRequest -
     * @param errorHolder -
     * @return -
     */
    boolean isPutFieldDataCriteriaNull(PutFieldDataRequest putFieldDataRequest) {
        if ((putFieldDataRequest.getPutFieldDataCriteria() == null ||
                putFieldDataRequest.getPutFieldDataCriteria().size() == 0)) {
            return true;
        }
        return false;
    }

    /**
     * @param putFieldDataCriteria -
     * @param errorHolder -
     * @return -
     */
    boolean isSelectionFilterNull(PutFieldDataCriteria putFieldDataCriteria) {
        if (putFieldDataCriteria.getSelectionFilter() == null){
            return true;
        }
        return false;
    }

    /**
     * @param putFieldDataCriteria -
     * @param errorHolder -
     * @return -
     */
    boolean isGetAssetNull(PutFieldDataCriteria putFieldDataCriteria)
    {
        if ( putFieldDataCriteria.getSelectionFilter() != null
                && ((FieldSelectionFilter) putFieldDataCriteria.getSelectionFilter()).getFieldIdentifierValue().size() == 0
                && !((FieldSelectionFilter) putFieldDataCriteria.getSelectionFilter()).getFieldIdentifierValue().get(0)
                        .getFieldIdentifier().getId().toString().contains("assetId") )
        {
            return true;
        }
        return false;
    }

    /**
     * @param putFieldDataCriteria -
     * @param errorHolder -
     * @return -
     */
    boolean isSerialNumberNull(PutFieldDataCriteria putFieldDataCriteria)
    {
        if ( putFieldDataCriteria.getSelectionFilter() != null
                && ((FieldSelectionFilter) putFieldDataCriteria.getSelectionFilter()).getFieldIdentifierValue().size() == 0
                && ((FieldSelectionFilter) putFieldDataCriteria.getSelectionFilter()).getFieldIdentifierValue().get(0)
                        .getValue() == null )
        {
            return true;
        }
        return false;
    }
 

}
