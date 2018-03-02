package com.ge.predix.solsvc.fdh.handler;

import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;

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
     * @return -
     */
    GetFieldDataResult getData(GetFieldDataRequest request, Map<Integer, Object> modelLookupMap, List<Header> headers );


}
