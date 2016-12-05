/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.asset.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.asset.Asset;
import com.ge.predix.entity.field.Field;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.model.ModelList;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.entity.util.map.DataMap;
import com.ge.predix.solsvc.fdh.asset.helper.JetEngineNoModel;
import com.ge.predix.solsvc.fdh.handler.asset.AssetPutDataHandlerImpl;
import com.ge.predix.solsvc.fdh.handler.asset.helper.PmpModuleConfigRootParser;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * 
 * @author 212397779
 */
@SuppressWarnings(
{
        "nls"
})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{
        "classpath*:META-INF/spring/MOCK-fdh-asset-handler-context.xml",
        "classpath*:META-INF/spring/ext-util-scan-context.xml",
        "classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml",
        "classpath*:META-INF/spring/fdh-asset-handler-scan-context.xml"

})
public class PutFieldDataHandlerTest extends AbstractRequestProcessorTest
{

    /**
     * 
     */
    protected PmpModuleConfigRootParser    parser = null;


    /**
     * Processor for GetFieldData request
     */
    @Autowired
    @Qualifier(value="assetPutFieldDataHandler")
    private AssetPutDataHandlerImpl putFieldDataProcessor;

    @Autowired
    private RestClient                        restClient;


    /**
     * @throws java.lang.Exception -
     */
    @Before
    public void setUp()
            throws Exception
    {
        @SuppressWarnings("rawtypes")
        Class[] classes = new Class[2];
        classes[0] = com.ge.predix.entity.putfielddata.PutFieldDataRequest.class;
        classes[1] = com.ge.predix.entity.putfielddata.PutFieldDataResult.class;

        // initialize parser
        this.parser = new PmpModuleConfigRootParser(classes);
    }

    /**
     * @throws java.lang.Exception -
     */
    @After
    public void tearDown()
            throws Exception
    {
        //
    }

    /**
     *  -
     */
    @SuppressWarnings("unused")
	@Test
    public void testPutFieldDataProcessorOfSomethingThatExtendsModel()
    {
        
        @SuppressWarnings("resource")
		CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        HttpEntity entity2 = Mockito.mock(HttpEntity.class);
        response.setEntity(entity2 );
        Mockito.when(response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_NO_CONTENT, "test reason!"));
        
        Mockito.when(this.restClient.post(Matchers.anyString(), Matchers.anyString(), Matchers.anyListOf(Header.class), Matchers.anyInt(), Matchers.anyInt()))
                .thenReturn(response);

        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
        putFieldDataRequest.setCorrelationId("string");
        
        
        FieldData fieldData = new FieldData();
        Field field = new Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId("/asset/1");
		field.setFieldIdentifier(fieldIdentifier);
        fieldData.getField().add(field);
        
        PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();
        fieldDataCriteria.setFieldData(fieldData);
        putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria  );
        
        ModelList data = new ModelList();
        Asset asset = new Asset();
        data.getModel().add(asset);
        asset.setUri("/asset/1");
        asset.setName("Asset_1");
		String assetJson = this.jsonMapper.toJson(data);
		
        //data.setString("[ { \"uri\":\"/asset/1\", \"name\":\"Asset_1\", \"parts\":{ \"motor\": {\"id\":\"M-01\" }, \"PressureGuage\": {\"id\":\"P-01\" } } } ]");
        fieldData.setData(data);
        putFieldDataRequest.getPutFieldDataCriteria().get(0).getFieldData().setData(data);
		String json = this.jsonMapper.toJson(putFieldDataRequest);
        Object obj = this.jsonMapper.fromJson(json, PutFieldDataRequest.class);

        List<Header> headers = new ArrayList<Header>();
        Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
        // Invoke the request processor
        PutFieldDataResult result = this.putFieldDataProcessor.putData(putFieldDataRequest, modelLookupMap , headers, HttpMethod.POST);

        this.jsonMapper.toJson(result);

        
        String expectedResult = null;
        String resultString = null;
        
        Assert.assertEquals(expectedResult, resultString);

    }

   

    /**
     *  -
     */
    @SuppressWarnings("unchecked")
	@Test
    public void testPutFieldDataProcessorOfSomethingThatDoesNotExtendModelAndHasAUri()
    {
        
        @SuppressWarnings("resource")
		CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        HttpEntity entity2 = Mockito.mock(HttpEntity.class);
        response.setEntity(entity2 );
        Mockito.when(response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_NO_CONTENT, "test reason!"));
        
        Mockito.when(this.restClient.post(Matchers.anyString(), Matchers.anyString(), Matchers.anyListOf(Header.class), Matchers.anyInt(), Matchers.anyInt()))
                .thenReturn(response);

        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
        putFieldDataRequest.setCorrelationId("string");
       
        
        FieldData fieldData = new FieldData();
        Field field = new Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId("/asset/1");
		field.setFieldIdentifier(fieldIdentifier);
        fieldData.getField().add(field);
        
        PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();
        fieldDataCriteria.setFieldData(fieldData);
        putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria  );
        
        //something that doesn't extend model
        ArrayList<JetEngineNoModel> assets = new ArrayList<JetEngineNoModel>();
        JetEngineNoModel asset = new JetEngineNoModel();
        assets.add(asset);
        asset.setUri("/asset/1");
        asset.setSerialNo(12345);
		String assetJson = this.jsonMapper.toJson(assets);
		List<Object> listNoModel = (List<Object>) this.jsonMapper.fromJsonArray(assetJson, Object.class);
		
		//put it in a DataMap and add to FieldData
        DataMap data = new DataMap();
		for ( Object item : listNoModel) {
			com.ge.predix.entity.util.map.Map map = new com.ge.predix.entity.util.map.Map();
			data.getMap().add(map);
			map.putAll((Map) item);
		}
        fieldData.setData(data );
        putFieldDataRequest.getPutFieldDataCriteria().get(0).getFieldData().setData(data);
        
        //can it marshal and unmarshal?
        String json = this.jsonMapper.toJson(putFieldDataRequest);
        @SuppressWarnings("unused")
		PutFieldDataRequest obj = this.jsonMapper.fromJson(json,PutFieldDataRequest.class);

        //let's call FDH
        List<Header> headers = new ArrayList<Header>();
        Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
        // Invoke the request processor
        PutFieldDataResult result = this.putFieldDataProcessor.putData(putFieldDataRequest, modelLookupMap , headers, HttpMethod.POST);

        this.jsonMapper.toJson(result);

        
        String expectedResult = null;
        String resultString = null;
        
        Assert.assertEquals(expectedResult, resultString);

    }

}
