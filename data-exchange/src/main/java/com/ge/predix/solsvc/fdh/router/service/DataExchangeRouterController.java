/*
 * Copyright (C) 2012 GE Software Center of Excellence.
 * All rights reserved
 */
package com.ge.predix.solsvc.fdh.router.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.datafile.DataFile;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.filter.Filter;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.fielddatahandler.entity.createfields.CreateFieldsRequest;
import com.ge.predix.fielddatahandler.entity.createfields.CreateFieldsResult;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.router.service.router.GetRouter;
import com.ge.predix.solsvc.fdh.router.service.router.PutRouter;
import com.ge.predix.solsvc.fdh.router.spi.IServiceManagerService;
import com.ge.predix.solsvc.fdh.router.spi.NamedCxfProperties;
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
public class DataExchangeRouterController
        implements DataExchange
{
    private static final Logger    log                             = LoggerFactory
                                                                           .getLogger(DataExchangeRouterController.class);
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
    private GetRouter    getRouter;

    @Autowired
    private PutRouter    putRouter;

    @Autowired
    private RestClient             restClient;

    @Autowired
    private IServiceManagerService serviceManagerService;

    @Autowired
    private JsonMapper jsonMapper;
    /**
     * 
     */
    @PostConstruct
    public void init()
    {
        log.debug("***Initializing FDHRouter");
        Map<String, String> attributeMap = new HashMap<String, String>();
        attributeMap.put(NamedCxfProperties.DSP_CUSTOM_CXF_PROVIDERS.name(),
                "com.ge.predix.solsvc.fdh.router.service.ApplicationJSONProvider");
        this.serviceManagerService.createRestWebService(this, attributeMap);
    }

    @Override
	public Response hello() {
    	ResponseBuilder responseBuilder = Response.status(Status.OK);
        responseBuilder.type(MediaType.TEXT_PLAIN);
        responseBuilder.entity("Welcome to Data Exchange");
        return responseBuilder.build();
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
            GetFieldDataResult getFieldDataResult = this.getRouter.getData(getFieldDataRequest, modelLookupMap, headers);
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
            PutFieldDataResult putFieldDataResult = this.putRouter.putData(putFieldDataRequest, modelLookupMap , headers);
            return putFieldDataResult;
        }
        catch (Throwable e)
        {
            log.error("error at boundary", e);
            // @TODO put in ErrorDataEvent if applicable for this operation
            throw e;
        }
    }
    
    /**
	 * Upload a csv file to timeseries
	 * 
	 * @param file -
	 * @param authorization -
	 * @param putRouter -
	 * @return -
	 */
    @Override
	public PutFieldDataResult putFieldData(MessageContext context,MultipartBody body) {
    	String putfielddata = body.getAttachmentObject("putfielddata", String.class);
   	 	PutFieldDataRequest putFieldDataRequest = this.jsonMapper.fromJson(putfielddata, PutFieldDataRequest.class);
    	
    	Attachment att = body.getAttachment("file");    	 
    	if (att == null) {
			throw new RuntimeException("You failed uploaded because the file was empty"); //$NON-NLS-1$
		}
    	String name = att.getDataHandler().getName();		
		
		try {
			updatePutRequest(att,putFieldDataRequest)   ;  
			//PutFieldDataRequest putFieldDataRequest =  createNewPutRequest(file);
			
			Map<Integer, Model> modelLookupMap= new HashMap<Integer, Model>();
			List<Header> headers = new ArrayList<Header>();
			//headers.add(new BasicHeader("authorization", authorization)); //$NON-NLS-1$
			
			PutFieldDataResult putFieldDataResult = this.putRouter.putData(putFieldDataRequest, modelLookupMap, headers);
			
			return putFieldDataResult;
			

		} catch (Exception e) {
			log.error("Error Uploading Timeseries data", e); //$NON-NLS-1$
			throw new RuntimeException("You failed uploaded " + name //$NON-NLS-1$
					+ " because the following error" + e.getMessage()); //$NON-NLS-1$
		}
	}
    @Override
    public CreateFieldsResult createFields(MessageContext context, CreateFieldsRequest arg0)
    {
        List<String> headersToKeep = new ArrayList<String>();
        headersToKeep.add("Authorization");
        @SuppressWarnings("unused")
        List<Header> headers = this.restClient.getRequestHeadersToKeep(context, headersToKeep);

        //not implemented at this time
        return null;
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
    
    private void updatePutRequest(Attachment file, PutFieldDataRequest putFieldDataRequest) {
		
		 if ( putFieldDataRequest == null)
	            throw new RuntimeException("PutFieldDataRequest is missing "); //$NON-NLS-1$
		 
		PutFieldDataCriteria fieldDataCriteria = null;
		if(putFieldDataRequest !=null && (putFieldDataRequest.getPutFieldDataCriteria() == null ||  putFieldDataRequest.getPutFieldDataCriteria().size()== 0))
		{
			fieldDataCriteria = new PutFieldDataCriteria() ;
			Filter selectionFilter = new Filter();
			fieldDataCriteria.setFilter(selectionFilter);
			putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria);
		}
		fieldDataCriteria = putFieldDataRequest.getPutFieldDataCriteria().get(0);
		FieldData fieldData = fieldDataCriteria.getFieldData();

		DataFile datafile = new DataFile();
		datafile.setName(file.getDataHandler().getName());
		try {
			datafile.setFile(IOUtils.toByteArray(file.getDataHandler().getInputStream()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		fieldData.setData(datafile);
		fieldDataCriteria.setFieldData(fieldData);
	}
}
