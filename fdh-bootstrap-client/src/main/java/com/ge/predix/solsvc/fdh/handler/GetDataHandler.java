package com.ge.predix.solsvc.fdh.handler;

import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.model.Model;

/**
 * 
 * @author predix -
 */
public interface GetDataHandler
{

    /**
     * @param headers -
     * @param request -
     * @param modelLookupMap -
     * @param httpMethod - GET, PUT, POST, DELETE
     * @return -
     */
    GetFieldDataResult getData(GetFieldDataRequest request, Map<Integer, Model> modelLookupMap, List<Header> headers );


}
