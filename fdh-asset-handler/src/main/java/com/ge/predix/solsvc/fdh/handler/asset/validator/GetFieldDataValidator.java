/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.handler.asset.validator;

import org.springframework.stereotype.Component;

import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;

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
     * @return -
     */
    @SuppressWarnings("nls")
    public boolean validate(GetFieldDataRequest getFieldDataRequest, GetFieldDataResult getFieldDataResult)
    {
        if ( getFieldDataRequest == null )
        {
            getFieldDataResult.getErrorEvent().add("Invalid getFieldDataRequest=null");
            return false;
        }

       
        return true;
    }


}
