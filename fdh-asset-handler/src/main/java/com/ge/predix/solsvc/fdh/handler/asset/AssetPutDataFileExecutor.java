/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.handler.asset;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.http.Header;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.ge.predix.entity.data.Data;
import com.ge.predix.entity.datafile.DataFile;
import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.entity.util.map.AttributeMap;
import com.ge.predix.entity.util.map.Entry;
import com.ge.predix.solsvc.bootstrap.ams.client.AssetClientImpl;

/**
 * PutFieldDataProcessor processes PutFieldDataRequest - Stores Asset Model from File
 * @author 212421693
 */
@SuppressWarnings("nls")
@Component(value = "assetPutDataFileExecutor")
@Profile("asset-file")
public class AssetPutDataFileExecutor  {
	private static final Logger log = LoggerFactory.getLogger(AssetPutDataFileExecutor.class);

	
	@Autowired
	@Qualifier("AssetClient")
	private AssetClientImpl assetClient;
	
    
    /**
     * 
     * @param headers - headers
     * @param threadName - 
     * @param uuidId - uUid tracer
     * @param putFieldDataCriteria -
     * @param putFieldDataResult  - results 
     * @param data - Data in this case File
     * @throws IOException  - 
     * @throws JsonMappingException -  
     * @throws JsonParseException - 
     */
    void processDataFile(List<Header> headers, String threadName, UUID uuidId,
            PutFieldDataCriteria putFieldDataCriteria,PutFieldDataResult putFieldDataResult,Data data) throws JsonParseException, JsonMappingException, IOException
    {
         InputStream file = null;
         try
         {
             DataFile datafile = (DataFile) data;
             file = IOUtils.toInputStream(new String((byte[])datafile.getFile()));
             log.info("UUID :" + uuidId + "   working...to upload the file = " + datafile.getName()); //$NON-NLS-1$ //$NON-NLS-2$
             if (!StringUtils.isEmpty(datafile.getName())
                     && datafile.getName().toLowerCase().endsWith("json")) { //$NON-NLS-1$
                 processUploadJson(putFieldDataResult,datafile.getName(),file, uuidId,headers);
                 log.info("UUID :" + uuidId + "   " + threadName + " has completed work."); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                 
             } else {
                 throw new NotImplementedException(" Upload not implemented for this extension");//$NON-NLS-1$
             }
            
         }  finally {
             if ( file != null ) try
             {
                 file.close();
             }
             catch (IOException e)
             {
                 throw new RuntimeException(e);
             }
         }
     
        
    }
	

	
	


    /**
     * @param putFieldDataResult 
     * @param file
     * @param uuidId -
	 * @param headers 
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
     */
    @SuppressWarnings({
    })
    @Autowired(required = false)
    private void processUploadJson(PutFieldDataResult putFieldDataResult, String name ,InputStream file, UUID uuidId, List<Header> headers) throws JsonParseException, JsonMappingException, IOException
    {
        log.debug("Processing Json data file " + name); //$NON-NLS-1$\
       
        //this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        log.debug("Processing Json data file " + name); //$NON-NLS-1$
        
        String assetString = IOUtils.toString(file, "UTF-8"); 
     
       Map<String,JSONArray> listObjects = new HashMap<String,JSONArray>();
        List<Entry> entryList =new ArrayList<Entry>();
        JSONArray array;
        try
        {
            array = new JSONArray(assetString);
            for (int i = 0; i < array.length(); i++)
            {
                String uriKey="";
                JSONObject jsonObject = array.getJSONObject(i);
               
                    if(!jsonObject.has("uri") || "".equalsIgnoreCase(jsonObject.getString("uri"))) {
                        jsonObject.put("uri", UUID.randomUUID().toString());
                    }
                    uriKey = jsonObject.getString("uri");
                    
                    Entry entry = new Entry();
                    entry.setKey("uploadIds");
                    entry.setValue(uriKey);
                    entryList.add(entry);
                    String resource = null;
                    
                    if(uriKey.startsWith("/")) {
                        resource = uriKey.split("/")[1];
                    } else{    
                       resource =uriKey.split("/")[0];
                    }
                
                    
                    String complexType="PredixString"; // default complexType is not set
                    
                    if(jsonObject.has("complexType") && "".equalsIgnoreCase(jsonObject.getString("complexType"))) {
                       jsonObject.put("complexType", complexType);
                    }
                    //else if( !jsonObject.has("complexType")) {
                      //  jsonObject.put("complexType", complexType);
                    //}
                    if(! listObjects.containsKey(resource)){
                        listObjects.put(resource, new JSONArray());
                    }
                    JSONArray newary = new JSONArray ();
                    newary.put(jsonObject);
                    log.info("jsonObject adding json "+resource +" With payload"+newary.toString());

                    
                    this.assetClient.createFromJson(resource,newary.toString(), headers);
              
            }
          
                   
        }
        catch (JSONException e1)
        {
            throw new RuntimeException("unable to unmarshal file", e1);
        }
       
        if(putFieldDataResult.getExternalAttributeMap() == null ) {
            AttributeMap attributeMap = new AttributeMap();
            putFieldDataResult.setExternalAttributeMap(attributeMap);
        }
        
        // uploadedIds: updated here on the putFieldDataResultrequest
      
        putFieldDataResult.getExternalAttributeMap().setEntry(entryList);
   
       
    }



    /**
     * @param listOfModels
     * @param model -
     */
    private void addModelToMap(Map<String, List<Model>> listOfModels, Model model)
    {
        String uriKey = model.getUri();
        
        if(model.getUri().startsWith("/")) {
            uriKey = model.getUri().split("/")[1];
        } else{    
         uriKey = model.getUri().split("/")[0];
        }
        
        if(!listOfModels.containsKey(uriKey)){
            listOfModels.put(uriKey, new ArrayList<Model>());
        }
        
        (listOfModels.get(uriKey)).add(model);
    }

}
