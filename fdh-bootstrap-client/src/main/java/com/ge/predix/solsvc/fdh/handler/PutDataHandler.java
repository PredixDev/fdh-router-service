/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.fdh.handler;

import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;

/**
 * 
 * @author predix -
 */
public interface PutDataHandler
{

    /**
     * @param singleRequest -
     * @param modelLookupMap -
     * @param headers -
     * @param httpMethod - GET, PUT, POST, DELETE
     * @return -
     */
    PutFieldDataResult putData(PutFieldDataRequest singleRequest, Map<Integer, Model> modelLookupMap, List<Header> headers, String httpMethod);

}
