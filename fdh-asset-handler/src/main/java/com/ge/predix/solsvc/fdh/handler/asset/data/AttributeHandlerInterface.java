/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.handler.asset.data;

import java.util.List;

import org.apache.http.Header;

import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddatacriteria.FieldDataCriteria;

/**
 * 
 * @author 212369540
 * 
 *         Interface for retrieving and storing the asset data
 */
public interface AttributeHandlerInterface
{

    /**
     * Retrieves the asset data as DataEvent
     * @param model -
     * 
     * @param fieldDataCriteria specifies the filters for the data in the returning DataEvent
     * @param headers -
     * @return DataEvent
     */
    public FieldData retrieveData(Object model, FieldDataCriteria fieldDataCriteria, List<Header> headers);




}
