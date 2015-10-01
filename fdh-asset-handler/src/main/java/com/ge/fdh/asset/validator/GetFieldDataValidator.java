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

import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataResult;

/**
 * 
 * @author 212369540
 */
@Component
public class GetFieldDataValidator
{

    /**
     * @param getFieldDataRequest -
     * @param getFieldDataResult -
     */
    @SuppressWarnings("nls")
    public void validate(GetFieldDataRequest getFieldDataRequest, GetFieldDataResult getFieldDataResult)
    {
        if ( getFieldDataRequest == null )
        {
            getFieldDataResult.getErrorEvent().add("Invalid getFieldDataRequest=null");
        }

        if ( !validateSolutionId(getFieldDataRequest) )
        {
            getFieldDataResult.getErrorEvent().add("Invalid SolutionId=");
        }
    }

    /**
     * @param getFieldDataRequest -
     * @return -
     */
    public boolean validateSolutionId(GetFieldDataRequest getFieldDataRequest)
    {
        try
        {
            if ( getFieldDataRequest.getSolutionIdentifier().getId() != null )
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
