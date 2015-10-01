/*
 * Copyright (C) 2012 GE Software Center of Excellence.
 * All rights reserved
 */
package com.ge.predix.solsvc.fdh.router.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.dsp.core.spi.IServiceManagerService;
import com.ge.dsp.core.spi.NamedCxfProperties;
import com.ge.dsp.pm.ext.entity.model.Model;
import com.ge.dsp.pm.fielddatahandler.entity.createfields.CreateFieldsRequest;
import com.ge.dsp.pm.fielddatahandler.entity.createfields.CreateFieldsResult;
import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataResult;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * Invokes the proper impl for the each of the interfaces
 * 
 * Note: for CXF ensure there no annotations or if need to override a copy of all Jax-RS annotations that are on the Interface
 * ResourceUtils.evaluateResourceClass()
 * see ResourceUtils.evaluateResourceClass() AnnotationUtils.getAnnotatedMethod(m)
 */
@Component
@SuppressWarnings("nls")
public class FieldDataHandlerContentBasedRouter
        implements FieldDataHandlerRest
{
    private static final Logger    log                             = LoggerFactory
                                                                           .getLogger(FieldDataHandlerContentBasedRouter.class);
    /**
     * the API name
     */
    public static final String     ASSETDATAHANDLER_GETFIELDDATA   = "FieldDataHandler.GetFieldData";
    /**
     * 
     */
    public static final String     ASSETDATAHANDLER_GETFIELDVALUES = "FieldDataHandler.GetFieldValues";
    /**
     * 
     */
    public static final String     ASSETDATAHANDLER_GETASSETS      = "FieldDataHandler.GetAssets";
    /**
     * 
     */
    public static final String     ASSETDATAHANDLER_PUTFIELDDATA   = "FieldDataHandler.PutFieldData";

    @Autowired
    private GetFieldDataService    getFieldData;

    @Autowired
    private PutFieldDataService    putFieldData;

    @Autowired
    private RestClient             restClient;

    @Autowired
    private IServiceManagerService serviceManagerService;

    /**
     * 
     */
    @PostConstruct
    public void init()
    {
        log.debug("***Initializing FDHRouter");
        Map<String, String> attributeMap = new HashMap<String, String>();
        attributeMap.put(NamedCxfProperties.DSP_CUSTOM_CXF_PROVIDERS.name(),
                "com.ge.predix.solsvc.fdhcontentbasedrouter.util.ApplicationJSONProvider");
        this.serviceManagerService.createRestWebService(this, attributeMap);
    }

    @Override
    public CreateFieldsResult createFields(MessageContext context, CreateFieldsRequest arg0)
    {
        List<String> headersToKeep = new ArrayList<String>();
        headersToKeep.add("Authorization");
        @SuppressWarnings("unused")
        List<Header> headers = this.restClient.getRequestHeadersToKeep(context, headersToKeep);

        return null;
    }

    @Override
    public GetFieldDataResult getFieldData(MessageContext context, GetFieldDataRequest getFieldDataRequest)
    {
        try
        {
            List<String> headersToKeep = new ArrayList<String>();
            headersToKeep.add("Authorization");
            headersToKeep.add("Content-Type");
            List<Header> headers = this.restClient.getRequestHeadersToKeep(context, headersToKeep);
            Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
            GetFieldDataResult getFieldDataResult = this.getFieldData.getFieldData(getFieldDataRequest, modelLookupMap, headers);
            return getFieldDataResult;
        }
        catch (Throwable e)
        {
            log.error("error at boundary", e);
            // @TODO put in ErrorDataEvent if applicable for this operation
            throw e;
        }
    }

    @Override
    public PutFieldDataResult putFieldData(MessageContext context, PutFieldDataRequest putFieldDataRequest)
    {
        try
        {
            List<String> headersToKeep = new ArrayList<String>();
            headersToKeep.add("Authorization");
            List<Header> headers = this.restClient.getRequestHeadersToKeep(context, headersToKeep);

            Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
            PutFieldDataResult putFieldDataResult = this.putFieldData.putFieldData(putFieldDataRequest, modelLookupMap , headers);
            return putFieldDataResult;
        }
        catch (Throwable e)
        {
            log.error("error at boundary", e);
            // @TODO put in ErrorDataEvent if applicable for this operation
            throw e;
        }
    }

    @Override
    public Response heartbeat(String id)
    {
        try
        {
            if ( id == null )
                return handleResult("Usage: To reflect back your string, pass a queryParam with id=  e.g. http://localhost:9090/service/fdhrouter/fielddatahandler/heartbeat?id=hello world");
            return handleResult(id);
        }
        catch (Throwable e)
        {
            log.error("error at boundary", e);
            // @TODO put in ErrorDataEvent if applicable for this operation
            throw e;
        }
    }

    /**
     * @param entity
     *            to be wrapped into JSON response
     * @return JSON response with entity wrapped
     */
    protected Response handleResult(Object entity)
    {
        ResponseBuilder responseBuilder = Response.status(Status.OK);
        responseBuilder.type(MediaType.APPLICATION_JSON);
        responseBuilder.entity(entity);
        return responseBuilder.build();
    }
}
