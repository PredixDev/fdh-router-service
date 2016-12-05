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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mimosa.osacbmv3_3.DMReal;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.assetfilter.AssetFilter;
import com.ge.predix.entity.field.Field;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddata.OsaData;
import com.ge.predix.entity.fielddata.PredixString;
import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.model.ModelList;
import com.ge.predix.entity.model.SampleEngine;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.solsvc.bootstrap.ams.factories.ModelFactory;
import com.ge.predix.solsvc.fdh.handler.GetDataHandler;
import com.ge.predix.solsvc.fdh.handler.PutDataHandler;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * 
 * @author 212369540
 */
@SuppressWarnings(
{
    "nls"
})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{
        "classpath*:META-INF/spring/MOCK-fdh-asset-handler-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml",
        "classpath*:META-INF/spring/ext-util-scan-context.xml",
        "classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml",
        "classpath*:META-INF/spring/fdh-asset-handler-scan-context.xml"

})
public class GetAndPutFieldDataHandlerTest extends AbstractRequestProcessorTest
{

    /**
     * Processor for GetFieldData request
     */
    @Autowired
    @Qualifier(value="assetGetFieldDataHandler")
    private GetDataHandler getFieldDataProcessor;
    @Autowired
    @Qualifier(value="assetPutFieldDataHandler")
    private PutDataHandler putFieldDataProcessor;
    @Autowired
    private RestClient                restClient;
    /**
     * 
     */
    @Rule
    public ExpectedException          thrown = ExpectedException.none();
    @Autowired
    private ModelFactory              modelFactory;
    
    private CloseableHttpResponse response;

    /**
     * @throws java.lang.Exception -
     */
    @Before
    public void setUp()
            throws Exception
    {
    	this.response = Mockito.mock(CloseableHttpResponse.class);
        HttpEntity entity = Mockito.mock(HttpEntity.class);
        this.response.setEntity(entity );
        Mockito.when(this.response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_NO_CONTENT, "test reason!"));
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
     * -
     */
    @Test
    public void aTest()
    {
        //
    }

    /**
     * - Note SampleEngine extends Model extends Data which has a JsonTypeInfo annotation so it gets polymorphically (Animal/Cat/Dog) generated
     * {
     * "complexType": "SampleEngine",
     * "additionalAttributes": [
     * {
     * "name": "averageSpeed",
     * "attribute": {
     * "value": [
     * "22.5"
     * ]
     * }
     * }
     * ],
     * "averageSpeed": "22.2"
     * }
     * @throws IOException -
     * @throws IllegalStateException - 
     */
    @SuppressWarnings({ "unchecked", "unused" })
	@Test
    public void testModelInAssetForPostOnPolymporphicClassThatExists() throws IllegalStateException, IOException
    {
        String model = "com.ge.predix.entity.model.SampleEngine";
        String uriField = "/" + model + "/uri";
        String uri = "/" + model + "/engine22";
        String fieldId = "/" + model + "/AverageSpeed";
        Double fieldValue = 22.6;
        
        ModelList list = new ModelList();
        String json = this.jsonMapper.toJson(list);
        SampleEngine model2 =new SampleEngine();
        model2.setAdditionalAttributes(new com.ge.predix.entity.util.map.Map());
        model2.getAdditionalAttributes().put("key", "value");
        json = this.jsonMapper.toJson(model2);
        list.getModel().add(model2);
        json = this.jsonMapper.toJson(list);
        Object obj = this.jsonMapper.fromJson(json, ModelList.class);
        
        
        /*ProtocolVersion proto2 = new ProtocolVersion("HTTP", 1, 1);
        BasicStatusLine line2 = new BasicStatusLine(proto2, HttpStatus.SC_NO_CONTENT, "test reason");
        HttpResponse response2 = new BasicHttpResponse(line2);*/
        CloseableHttpResponse response = Mockito.mock(CloseableHttpResponse.class);
        HttpEntity entity2 = Mockito.mock(HttpEntity.class);
        response.setEntity(entity2 );
        Mockito.when(response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_NO_CONTENT, "test reason!"));
        
        Mockito.when(this.restClient.delete(Matchers.anyString(), Matchers.anyListOf(Header.class), Matchers.anyInt(), Matchers.anyInt()))
        .thenReturn(response);
        
        Mockito.when(this.restClient.hasToken(Matchers.anyListOf(Header.class)))
        .thenReturn(true);
        Mockito.when(this.restClient.hasZoneId(Matchers.anyListOf(Header.class)))
        .thenReturn(true);
       /* ProtocolVersion proto = new ProtocolVersion("HTTP", 1, 1);
        BasicStatusLine line = new BasicStatusLine(proto, HttpStatus.SC_OK, "test reason testNoModelForPostOnPolymporphicClassThatExists");
        HttpResponse response = new BasicHttpResponse(line);
        HttpEntity entity = Mockito.mock(HttpEntity.class);*/
        response = Mockito.mock(CloseableHttpResponse.class);
        HttpEntity entity = Mockito.mock(HttpEntity.class);
        response.setEntity(entity );
        Mockito.when(response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "test reason testNoModelForPostOnPolymporphicClassThatExists"));
        
        Mockito.when(this.restClient.get(Matchers.anyString(), Matchers.anyListOf(Header.class), Matchers.anyInt(), Matchers.anyInt()))
        .thenReturn(response);
        String body = "[{\"complexType\":\"SampleEngine\",\"additionalAttributes\":{\"key\":\"value\"}}]";
        InputStream stream = new ByteArrayInputStream(body.getBytes());
        Mockito.when(entity.getContent()).thenReturn(stream );
        Mockito.when(entity.getContentLength()).thenReturn(new Long(body.length()));
       
       /* ProtocolVersion protoPut = new ProtocolVersion("HTTP", 1, 1);
        BasicStatusLine linePut = new BasicStatusLine(protoPut, HttpStatus.SC_NO_CONTENT, "test reason");
        HttpResponse responsePut = new BasicHttpResponse(linePut);*/
        CloseableHttpResponse responsePut = Mockito.mock(CloseableHttpResponse.class);
        HttpEntity entityPut = Mockito.mock(HttpEntity.class);
        Mockito.when(responsePut.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),  HttpStatus.SC_NO_CONTENT, "test reason"));
        
        responsePut.setEntity(entityPut );
        Mockito.when(this.restClient.put(Matchers.anyString(), Matchers.anyString(), Matchers.anyListOf(Header.class), Matchers.anyInt(), Matchers.anyInt()))
        .thenReturn(responsePut);

        List<Header> headers = this.restClient.getSecureTokenForClientId();

        // get rid of it
        this.modelFactory.deleteModel(uri, headers);//$NON-NLS-1$

        // add it back
        String filterFieldId = uriField;
        PutFieldDataRequest putFieldDataRequest = createPutRequest(fieldId, fieldValue, uri);
        Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
        // this.thrown.expect(RuntimeException.class);
      
        Mockito.when(entity.getContent()).thenReturn(stream );
        Mockito.when(entity.getContentLength()).thenReturn(new Long(body.length()));
        Mockito.when(response.getEntity()).thenReturn(entity);
        headers = this.restClient.getSecureTokenForClientId();
       
        this.putFieldDataProcessor.putData(putFieldDataRequest, modelLookupMap , headers, HttpMethod.POST);
        
    }

    /**
     * -
     * @throws IOException -
     * @throws IllegalStateException -
     */
    @SuppressWarnings("unused")
	@Test
    public void testNoModelInAssetForPostOnClassThatDoesNotExist() throws IllegalStateException, IOException
    {
        String model = "DoesNotExist";
        String uriField = "/" + model + "/uri";
        String uri = "/" + model + "/engine22";
        String fieldId = "/" + model + "/AverageSpeed";
        Double fieldValue = 22.6;

        
        CloseableHttpResponse responseGet = Mockito.mock(CloseableHttpResponse.class);
        HttpEntity entityGet = Mockito.mock(HttpEntity.class);
        responseGet.setEntity(entityGet );
        Mockito.when(this.response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "test reason!"));
        
       
       
        String bodyGet = "[]";
        InputStream streamGet = new ByteArrayInputStream(bodyGet.getBytes());


        
        String bodyGet2 = "[{\"complexType\":\"Model\",\"randomAttribute\":\"randomValue\",\"uri\":\"uniqueValue\",\"additionalAttributes\":{\"key\":\"value\"}}]";

       /* BasicStatusLine lineGet2 = new BasicStatusLine(protoGet, HttpStatus.SC_OK, "test reason for Get2");
        HttpResponse responseGet2 = new BasicHttpResponse(lineGet2);*/
        
        CloseableHttpResponse responseGet2 = Mockito.mock(CloseableHttpResponse.class);
        HttpEntity entityGet2 = Mockito.mock(HttpEntity.class);
        responseGet.setEntity(entityGet );
        Mockito.when(this.response.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "test reason for Get2"));
        
        InputStream streamGet2 = new ByteArrayInputStream(bodyGet2.getBytes());
    
        responseGet2.setEntity(entityGet2 );
 
        Mockito.when(entityGet.getContent()).thenReturn(streamGet);
        Mockito.when(entityGet.getContentLength()).thenReturn(new Long(bodyGet.length()));
        Mockito.when(entityGet2.getContent()).thenReturn(streamGet2);
        Mockito.when(entityGet2.getContentLength()).thenReturn(new Long(bodyGet2.length()));
                
        Answer<?> answerGet = new Answer<Object>() {
            private int count = 1;

            @Override
			public Object answer(InvocationOnMock invocation) {
                if (this.count++ == 1)
                    return responseGet;

                return responseGet2;
            }
        };
        Mockito.when(this.restClient.get(Matchers.anyString(), Matchers.anyListOf(Header.class), Matchers.anyInt(), Matchers.anyInt()))
        .thenAnswer(answerGet);        

   
        CloseableHttpResponse responsePut = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(responsePut.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),  HttpStatus.SC_NO_CONTENT, "test reason"));
        
        HttpEntity entityPut = Mockito.mock(HttpEntity.class);
        responsePut.setEntity(entityPut );
        Mockito.when(this.restClient.put(Matchers.anyString(), Matchers.anyString(), Matchers.anyListOf(Header.class), Matchers.anyInt(), Matchers.anyInt()))
        .thenReturn(responsePut);

   
        CloseableHttpResponse responseDelete = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(responseDelete.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),  HttpStatus.SC_NO_CONTENT, "test reason"));
        HttpEntity entityDelete = Mockito.mock(HttpEntity.class);
        responseDelete.setEntity(entityDelete );
        Mockito.when(this.restClient.delete(Matchers.anyString(), Matchers.anyListOf(Header.class), Matchers.anyInt(), Matchers.anyInt()))
        .thenReturn(responseDelete);
        
        List<Header> headers = this.restClient.getSecureTokenForClientId();

        // get rid of it
        this.modelFactory.deleteModel(uri, headers);//$NON-NLS-1$

        // add it back
        String filterFieldId = uriField;
        PutFieldDataRequest putFieldDataRequest = createPutRequest(fieldId, fieldValue, uri);
        Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
        
        CloseableHttpResponse responseGet3 = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(responseGet3.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),  HttpStatus.SC_OK, "test reason for Get2"));
        HttpEntity responseEntity3 = Mockito.mock(HttpEntity.class);
        responseGet3.setEntity(responseEntity3 );
        Mockito.when(responseEntity3.getContent()).thenReturn(streamGet2);
        Mockito.when(responseEntity3.getContentLength()).thenReturn(new Long(bodyGet2.length()));
        
        Mockito.when(this.restClient.get(Matchers.anyString(), Matchers.anyListOf(Header.class), Matchers.anyInt(), Matchers.anyInt()))
        .thenReturn(responseGet3);
        Mockito.when(this.restClient.get(Matchers.anyString(), Matchers.anyListOf(Header.class), Matchers.anyInt(), Matchers.anyInt())).thenReturn(responseGet3);
         
        Mockito.when(this.restClient.hasToken(Matchers.anyListOf(Header.class)))
        .thenReturn(true);
        Mockito.when(this.restClient.hasZoneId(Matchers.anyListOf(Header.class)))
        .thenReturn(true);
     
        Mockito.when(responseGet3.getEntity()).thenReturn(responseEntity3);
       
        headers = this.restClient.getSecureTokenForClientId();
        
        
        PutFieldDataResult response2 = this.putFieldDataProcessor.putData(putFieldDataRequest, modelLookupMap , headers, HttpMethod.POST);
        
        //TODO retrieve from asset and assert that averageSpeed=22.6 was added
        
    }

    /**
     * -
     * @throws IOException -
     * @throws IllegalStateException - 
     */
    // @Test
    @Test
    public void testNoModelInAssetForPut() throws IllegalStateException, IOException
    {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Authorization", null));

        String fieldId = "/DoesNotExist/AverageFlow";
        Double fieldValue = 22.6;
        String filterFieldId = "/DoesNotExist/uri";
        String uri = "pump22";
        
        CloseableHttpResponse responseGet = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(responseGet.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),  HttpStatus.SC_OK, "test reason for Get"));
        
        HttpEntity entityGet = Mockito.mock(HttpEntity.class);
        responseGet.setEntity(entityGet);
        String bodyGet = "[]";
        InputStream streamGet = new ByteArrayInputStream(bodyGet.getBytes());
        
        String bodyGet2 = "[{\"complexType\":\"Model\",\"additionalAttributes\":{\"key\":\"value\"}}]";

        InputStream streamGet2 = new ByteArrayInputStream(bodyGet2.getBytes());
        
        CloseableHttpResponse responseGet2 = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(responseGet2.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),  HttpStatus.SC_OK, "test reason for Get2"));
        HttpEntity entityGet2 = Mockito.mock(HttpEntity.class);
        responseGet2.setEntity(entityGet2 );
 
        Mockito.when(entityGet.getContent()).thenReturn(streamGet);
        Mockito.when(entityGet2.getContent()).thenReturn(streamGet2);
        Mockito.when(entityGet.getContentLength()).thenReturn(new Long(bodyGet.length()));
        Mockito.when(entityGet2.getContentLength()).thenReturn(new Long(bodyGet2.length()));
                
        Answer<?> answerGet = new Answer<Object>() {
            private int count = 1;

            @Override
			public Object answer(InvocationOnMock invocation) {
                if (this.count++ == 1)
                    return responseGet;

                return responseGet2;
            }
        };
        Mockito.when(this.restClient.get(Matchers.anyString(), Matchers.anyListOf(Header.class), Matchers.anyInt(), Matchers.anyInt()))
        .thenAnswer(answerGet);        

        
        CloseableHttpResponse responsePut = Mockito.mock(CloseableHttpResponse.class);
        HttpEntity entityPut = Mockito.mock(HttpEntity.class);
        Mockito.when(responsePut.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),  HttpStatus.SC_NO_CONTENT, "test reason"));
     
        responsePut.setEntity(entityPut );
        Mockito.when(this.restClient.put(Matchers.anyString(), Matchers.anyString(), Matchers.anyListOf(Header.class), Matchers.anyInt(), Matchers.anyInt()))
        .thenReturn(responsePut);

        CloseableHttpResponse responseDelete = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(responsePut.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),  HttpStatus.SC_NO_CONTENT, "test reason"));
        HttpEntity entityDelete = Mockito.mock(HttpEntity.class);
        responseDelete.setEntity(entityDelete );
        Mockito.when(this.restClient.delete(Matchers.anyString(), Matchers.anyListOf(Header.class), Matchers.anyInt(), Matchers.anyInt()))
        .thenReturn(responseDelete);
        Mockito.when(responseDelete.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_NO_CONTENT, "test reason!"));
      
        // get rid of it
        this.modelFactory.deleteModel(filterFieldId, headers);//$NON-NLS-1$
        
        PutFieldDataRequest putFieldDataRequest = createPutRequest(fieldId, fieldValue, uri);
        Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
        
        CloseableHttpResponse responseGet3 = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(responseGet3.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1),  HttpStatus.SC_OK, "test reason for Get2"));
        HttpEntity responseEntity3 = Mockito.mock(HttpEntity.class);
        responseGet3.setEntity(responseEntity3 );
       
        Mockito.when(responseEntity3.getContent()).thenReturn(streamGet2);
        Mockito.when(responseEntity3.getContentLength()).thenReturn(new Long(bodyGet2.length()));
        
        Mockito.when(this.restClient.get(Matchers.anyString(), Matchers.anyListOf(Header.class), Matchers.anyInt(), Matchers.anyInt()))
        .thenReturn(responseGet3);
        Mockito.when(this.restClient.get(Matchers.anyString(), Matchers.anyListOf(Header.class), Matchers.anyInt(), Matchers.anyInt())).thenReturn(responseGet3);
        
      
        //this.thrown.expect(RuntimeException.class);
         
         
        Mockito.when(this.restClient.hasToken(Matchers.anyListOf(Header.class)))
        .thenReturn(true);
        Mockito.when(this.restClient.hasZoneId(Matchers.anyListOf(Header.class)))
        .thenReturn(true);
     
        Mockito.when(responseGet3.getEntity()).thenReturn(responseEntity3);
       
        headers = this.restClient.getSecureTokenForClientId();
        
        this.putFieldDataProcessor.putData(putFieldDataRequest, modelLookupMap , headers, HttpMethod.POST);

       
    }
    
    /**
     *  -
     */
    @Test
	@SuppressWarnings("unused")
    public void getPutRequestWithModelList () {
		PutFieldDataRequest request = createPutRequestNoFilterForModelList();
    	String json = this.jsonMapper.toJson(request);
    }

    /**
     *  -
     */
    @Test
	@SuppressWarnings("unused")
    public void getPutRequestWithPredixString() {
		PutFieldDataRequest request = createPutRequestNoFilterForPredixString();
    	String json = this.jsonMapper.toJson(request);
    }
    
    /**
     * @param fieldId
     * @param fieldValue
     * @param filterFieldId
     * @param filterFieldValue
     * @return -
     */
    private PutFieldDataRequest createPutRequest(String fieldId, Double fieldValue,
            String uri)
    {
        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
        putFieldDataRequest.setCorrelationId("string");

        FieldData fieldData = new FieldData();
        Field field = new Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId(fieldId);
        fieldIdentifier.setSource(FieldSourceEnum.PREDIX_ASSET.name());
        field.setFieldIdentifier(fieldIdentifier);
        fieldData.getField().add(field);
        PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();
        DMReal dataEvent = new DMReal();
        dataEvent.setValue(fieldValue);
        OsaData data = new OsaData();
        data.setDataEvent(dataEvent);
        fieldData.setData(data);
        fieldDataCriteria.setFieldData(fieldData);
        AssetFilter fieldFilter = createAssetFilter(uri);
        fieldDataCriteria.setFilter(fieldFilter);
        putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria);
        return putFieldDataRequest;
    }
    
	private AssetFilter createAssetFilter(String uri) {
		AssetFilter assetFilter = new AssetFilter();
		assetFilter.setUri(uri);
		return assetFilter;
	}

    /**
     * @param fieldId
     * @param fieldValue
     * @param filterFieldId
     * @param filterFieldValue
     * @return -
     */
    @SuppressWarnings({ "unchecked", "unused" })
	private PutFieldDataRequest createPutRequestNoFilterForModelList()
    {
        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
        putFieldDataRequest.setCorrelationId("string");

        FieldData fieldData = new FieldData();
        Field field = new Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId("fieldId");
        fieldIdentifier.setSource(FieldSourceEnum.PREDIX_ASSET.name());
        field.setFieldIdentifier(fieldIdentifier);
        fieldData.getField().add(field);
        PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();

        
        ModelList list = new ModelList();
        String json = this.jsonMapper.toJson(list);
        SampleEngine model2 =new SampleEngine();
        model2.setAdditionalAttributes(new com.ge.predix.entity.util.map.Map());
        model2.getAdditionalAttributes().put("key", "value");
        json = this.jsonMapper.toJson(model2);
        list.getModel().add(model2);
        json = this.jsonMapper.toJson(list);
		Object obj = this.jsonMapper.fromJson(json, ModelList.class);
        
        fieldData.setData(list);
        fieldDataCriteria.setFieldData(fieldData);
        putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria);
        return putFieldDataRequest;
    }

    /**
     * @param fieldId
     * @param fieldValue
     * @param filterFieldId
     * @param filterFieldValue
     * @return -
     */
	private PutFieldDataRequest createPutRequestNoFilterForPredixString()
    {
        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
        putFieldDataRequest.setCorrelationId("string");

        FieldData fieldData = new FieldData();
        Field field = new Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId("fieldId");
        fieldIdentifier.setSource(FieldSourceEnum.PREDIX_ASSET.name());
        field.setFieldIdentifier(fieldIdentifier);
        fieldData.getField().add(field);
        PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();

        PredixString data = new PredixString();
        
        data.setString("{\"myObject\": {\"value\": \"on\" }}" );
        
        
        fieldData.setData(data);
        fieldDataCriteria.setFieldData(fieldData);
        putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria);
        return putFieldDataRequest;
    }
}
