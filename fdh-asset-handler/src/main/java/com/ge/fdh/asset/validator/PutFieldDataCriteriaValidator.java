/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
package com.ge.fdh.asset.validator;

import org.springframework.stereotype.Component;

import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataCriteria;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataResult;

/**
 * validates PutFieldDataRequest
 */
@SuppressWarnings("nls")
@Component
public class PutFieldDataCriteriaValidator
{
    /**
     * @param putFieldDataCriteria -
     * @param putFieldDataResult -
     * @param putFieldDataRequestAdapter -
     * @return -
     */
    public boolean validate(PutFieldDataCriteria putFieldDataCriteria, PutFieldDataResult putFieldDataResult)
    {

        if ( !validateSelectionFilter(putFieldDataCriteria) )
        {
            putFieldDataResult.getErrorEvent().add("invalid SelectionFilter");
            return false;
        }

        return true;
    }

    /**
     * @param putFieldDataCriteria -
     * @return true if validation passes and false otherwise
     */
    public boolean isValidRequest(PutFieldDataCriteria putFieldDataCriteria)
    {


        if ( !validateSelectionFilter(putFieldDataCriteria) )
        {
            return false;
        }

        return true;
    }



 
    private boolean validateSelectionFilter(PutFieldDataCriteria putFieldDataCriteria)
    {
        try
        {
            if ( putFieldDataCriteria.getSelectionFilter() != null )
            {
                return true;
            }
        }
        catch (Throwable t)
        {
            return false;
        }
        return false;
    }

}
