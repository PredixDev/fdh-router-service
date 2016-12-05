/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
package com.ge.predix.solsvc.fdh.handler.custom; 


import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.mimosa.osacbmv3_3.DAString;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddata.OsaData;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.solsvc.fdh.handler.GetDataHandler;
import com.ge.predix.solsvc.fdh.handler.PutDataHandler;

/**
 * 
 * @author predix -
 */
@Component
@ImportResource(
{
        "classpath*:META-INF/spring/fdh-custom-handler-scan-context.xml"
})
public class SampleHandler implements GetDataHandler, PutDataHandler
{

   

    /* (non-Javadoc)
     * @see com.ge.predix.solsvc.fdhcontentbasedrouter.GetFieldDataInterface#getFieldData(java.util.List, com.ge.predix.entity.getfielddata.GetFieldDataRequest)
     */
    @SuppressWarnings("nls")
    @Override
    public GetFieldDataResult getData(GetFieldDataRequest request,Map<Integer, Model> modelLookupMap, List<Header> headers)
    {
        GetFieldDataResult result = new GetFieldDataResult();
        FieldData fieldData = new FieldData();
        OsaData osaData = new OsaData();
        DAString daString = new DAString();
        daString.setValue("SampleHandler - put your code here");
        osaData.setDataEvent(daString);
        fieldData.setData(osaData);
        result.getFieldData().add(fieldData);
        return result;
    }

    /* (non-Javadoc)
     * @see com.ge.predix.solsvc.fdh.handler.PutFieldDataInterface#processRequest(com.ge.predix.entity.putfielddata.PutFieldDataRequest, java.util.List)
     */
    @SuppressWarnings("nls")
    @Override
    public PutFieldDataResult putData(PutFieldDataRequest singleRequest, Map<Integer, Model> modelLookupMap, List<Header> headers, String httpMethod)
    {
        validate();
        

        PutFieldDataResult result = new PutFieldDataResult();
        result.getErrorEvent().add("SampleHandler - put your code here");
        return result;
    }

    /**
     *  -
     */
    private void validate()
    {
        // TODO Auto-generated method stub
        
    }


}
