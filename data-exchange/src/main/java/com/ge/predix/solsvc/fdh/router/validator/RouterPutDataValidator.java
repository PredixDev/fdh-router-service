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

import com.ge.predix.entity.putfielddata.PutFieldDataRequest;

/**
 * 
 * @author predix
 */
@Component
@SuppressWarnings("nls")
public class RouterPutDataValidator extends BaseValidator
{

    /**
     * @param putFieldDataRequest -
     */
    public void validate(PutFieldDataRequest putFieldDataRequest)
    {

        if (isPutFieldDataCriteriaListNull(putFieldDataRequest) )
                    throw new RuntimeException(("PutFieldDataRequest is invalid isFieldDataListNull"));


    }

    /**
     * @param putFieldDataRequest -
     * @return -
     */
    boolean isPutFieldDataCriteriaListNull(PutFieldDataRequest putFieldDataRequest) {
        if (putFieldDataRequest.getPutFieldDataCriteria() == null || putFieldDataRequest.getPutFieldDataCriteria().size() == 0) {
            return true;
        }
        return false;
    }

 

}
