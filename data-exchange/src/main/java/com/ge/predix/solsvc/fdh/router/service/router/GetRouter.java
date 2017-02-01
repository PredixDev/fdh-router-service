package com.ge.predix.solsvc.fdh.router.service.router;

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
public interface GetRouter
{

    /**
     * @param headers -
     * @param request -
     * @param modelLookupMap - saves time by remembering models already retrieved
     * @return -
     */
    GetFieldDataResult getData(GetFieldDataRequest request, Map<Integer, Model> modelLookupMap, List<Header> headers );


}
