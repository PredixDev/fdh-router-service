package com.ge.predix.solsvc.fdh.handler.timeseries;

import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.http.Header;

import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataResult;

/**
 * 
 * @author predix -
 */
public interface FieldDataHandlerGetFieldData
{

    /**
     * @param uri -
     * @param headers -
     * @param request -
     * @return -
     */
    GetFieldDataResult getFieldData(String uri, List<Header> headers, GetFieldDataRequest request);

    /**
     * @param id -
     * @return -
     */
    Response heartbeat(String id);

}
