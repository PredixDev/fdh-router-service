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
import org.springframework.stereotype.Component;

import com.ge.dsp.pm.ext.entity.fielddata.FieldData;
import com.ge.dsp.pm.ext.entity.fielddata.OsaData;
import com.ge.dsp.pm.ext.entity.model.Model;
import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataResult;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.solsvc.fdh.handler.GetFieldDataHandler;
import com.ge.predix.solsvc.fdh.handler.PutFieldDataHandler;

/**
 * 
 * @author predix -
 */
@Component
public class SampleHandler implements GetFieldDataHandler, PutFieldDataHandler
{

   

    /* (non-Javadoc)
     * @see com.ge.predix.solsvc.fdhcontentbasedrouter.GetFieldDataInterface#getFieldData(java.util.List, com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataRequest)
     */
    @SuppressWarnings("nls")
    @Override
    public GetFieldDataResult getFieldData(GetFieldDataRequest request,Map<Integer, Model> modelLookupMap, List<Header> headers, String httpMethod)
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
     * @see com.ge.predix.solsvc.fdh.handler.PutFieldDataInterface#processRequest(com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataRequest, java.util.List)
     */
    @SuppressWarnings("nls")
    @Override
    public PutFieldDataResult putFieldData(PutFieldDataRequest singleRequest, Map<Integer, Model> modelLookupMap, List<Header> headers, String httpMethod)
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
