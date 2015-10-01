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
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mimosa.osacbmv3_3.DMReal;
import org.mimosa.osacbmv3_3.OsacbmDataType;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.dsp.pm.ext.entity.field.Field;
import com.ge.dsp.pm.ext.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.dsp.pm.ext.entity.fielddata.FieldData;
import com.ge.dsp.pm.ext.entity.fielddata.OsaData;
import com.ge.dsp.pm.ext.entity.fieldidentifiervalue.FieldIdentifierValue;
import com.ge.dsp.pm.ext.entity.fieldselection.FieldSelection;
import com.ge.dsp.pm.ext.entity.model.Model;
import com.ge.dsp.pm.ext.entity.selectionfilter.FieldSelectionFilter;
import com.ge.dsp.pm.ext.entity.solution.identifier.solutionidentifier.SolutionIdentifier;
import com.ge.dsp.pm.fielddatahandler.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataCriteria;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.solsvc.bootstrap.ams.common.AssetRestConfig;
import com.ge.predix.solsvc.bootstrap.ams.factories.ModelFactory;
import com.ge.predix.solsvc.fdh.handler.GetFieldDataHandler;
import com.ge.predix.solsvc.fdh.handler.PutFieldDataHandler;
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
    private GetFieldDataHandler getFieldDataProcessor;
    @Autowired
    private PutFieldDataHandler putFieldDataProcessor;
    @Autowired
    private RestClient                restClient;
    /**
     * 
     */
    @Rule
    public ExpectedException          thrown = ExpectedException.none();
    @Autowired
    private AssetRestConfig           assetRestConfig;
    @Autowired
    private ModelFactory              modelFactory;

    /**
     * @throws java.lang.Exception -
     */
    @Before
    public void setUp()
            throws Exception
    {
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
     * "@type": "SampleEngine",
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
    @Test
    public void testNoModelInAssetForPostOnPolymporphicClassThatExists() throws IllegalStateException, IOException
    {
        String model = "com.ge.dsp.pm.e"
                + "xt.entity.model.SampleEngine";
        String uriField = "/" + model + "/uri";
        String uri = "/" + model + "/engine22";
        String fieldId = "/" + model + "/AverageSpeed";
        Double fieldValue = 22.6;
        
        ProtocolVersion proto2 = new ProtocolVersion("HTTP", 1, 1);
        BasicStatusLine line2 = new BasicStatusLine(proto2, HttpStatus.SC_NO_CONTENT, "test reason");
        HttpResponse response2 = new BasicHttpResponse(line2);
        HttpEntity entity2 = Mockito.mock(HttpEntity.class);
        response2.setEntity(entity2 );
        Mockito.when(this.restClient.delete(Matchers.anyString(), Matchers.anyListOf(Header.class)))
        .thenReturn(response2);
        
        Mockito.when(this.restClient.hasToken(Matchers.anyListOf(Header.class)))
        .thenReturn(true);
        Mockito.when(this.restClient.hasZoneId(Matchers.anyListOf(Header.class)))
        .thenReturn(true);
        ProtocolVersion proto = new ProtocolVersion("HTTP", 1, 1);
        BasicStatusLine line = new BasicStatusLine(proto, HttpStatus.SC_OK, "test reason testNoModelForPostOnPolymporphicClassThatExists");
        HttpResponse response = new BasicHttpResponse(line);
        HttpEntity entity = Mockito.mock(HttpEntity.class);
        response.setEntity(entity );
        Mockito.when(this.restClient.get(Matchers.anyString(), Matchers.anyListOf(Header.class)))
        .thenReturn(response);
        String body = "[{\"@type\":\"Model\",\"additionalAttributes\":{\"keys\":[\"key\"],\"values\":[\"value\"]}}]";
        InputStream stream = new ByteArrayInputStream(body.getBytes());
        Mockito.when(entity.getContent()).thenReturn(stream );
        Mockito.when(entity.getContentLength()).thenReturn(new Long(body.length()));
       
        ProtocolVersion protoPut = new ProtocolVersion("HTTP", 1, 1);
        BasicStatusLine linePut = new BasicStatusLine(protoPut, HttpStatus.SC_NO_CONTENT, "test reason");
        HttpResponse responsePut = new BasicHttpResponse(linePut);
        HttpEntity entityPut = Mockito.mock(HttpEntity.class);
        responsePut.setEntity(entityPut );
        Mockito.when(this.restClient.put(Matchers.anyString(), Matchers.anyString(), Matchers.anyListOf(Header.class)))
        .thenReturn(responsePut);

        List<Header> headers = this.restClient.getSecureTokenForClientId();

        // get rid of it
        this.modelFactory.deleteModel(uri, headers);//$NON-NLS-1$

        // add it back
        String filterFieldId = uriField;
        String filterFieldValue = uri;
        PutFieldDataRequest putFieldDataRequest = createPutRequest(fieldId, fieldValue, filterFieldId, filterFieldValue);
        Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
        // this.thrown.expect(RuntimeException.class);
        this.putFieldDataProcessor.putFieldData(putFieldDataRequest, modelLookupMap , headers, HttpMethod.POST);

        
    }

    /**
     * -
     * @throws IOException -
     * @throws IllegalStateException -
     */
    @Test
    public void testNoModelInAssetForPostOnClassThatDoesNotExist() throws IllegalStateException, IOException
    {
        String model = "DoesNotExist";
        String uriField = "/" + model + "/uri";
        String uri = "/" + model + "/engine22";
        String fieldId = "/" + model + "/AverageSpeed";
        Double fieldValue = 22.6;

        ProtocolVersion protoGet = new ProtocolVersion("HTTP", 1, 1);
        BasicStatusLine lineGet = new BasicStatusLine(protoGet, HttpStatus.SC_OK, "test reason for Get");
        HttpResponse responseGet = new BasicHttpResponse(lineGet);
        HttpEntity entityGet = Mockito.mock(HttpEntity.class);
        responseGet.setEntity(entityGet );
        String bodyGet = "[]";
        InputStream streamGet = new ByteArrayInputStream(bodyGet.getBytes());

//        SampleEngine model = new SampleEngine();
//        PositionalAttributes pa = new PositionalAttributes();
//        model.setAdditionalAttributes(pa);
//        pa.getKeys().add("key");
//        pa.getValues().add("value");
//        String body = FdhJsonMapper.toJson(model);
//        model = FdhJsonMapper.fromJson(body, SampleEngine.class);
        
        String bodyGet2 = "[{\"@type\":\"Model\",\"additionalAttributes\":{\"keys\":[\"key\"],\"values\":[\"value\"]}}]";
        BasicStatusLine lineGet2 = new BasicStatusLine(protoGet, HttpStatus.SC_OK, "test reason for Get2");
        InputStream streamGet2 = new ByteArrayInputStream(bodyGet2.getBytes());
        HttpResponse responseGet2 = new BasicHttpResponse(lineGet2);
        HttpEntity entityGet2 = Mockito.mock(HttpEntity.class);
        responseGet2.setEntity(entityGet2 );
 
        Mockito.when(entityGet.getContent()).thenReturn(streamGet);
        Mockito.when(entityGet.getContentLength()).thenReturn(new Long(bodyGet.length()));
        Mockito.when(entityGet2.getContent()).thenReturn(streamGet2);
        Mockito.when(entityGet2.getContentLength()).thenReturn(new Long(bodyGet2.length()));
                
        Answer<?> answerGet = new Answer() {
            private int count = 1;

            public Object answer(InvocationOnMock invocation) {
                if (this.count++ == 1)
                    return responseGet;

                return responseGet2;
            }
        };
        Mockito.when(this.restClient.get(Matchers.anyString(), Matchers.anyListOf(Header.class)))
        .thenAnswer(answerGet);        

        ProtocolVersion protoPut = new ProtocolVersion("HTTP", 1, 1);
        BasicStatusLine linePut = new BasicStatusLine(protoPut, HttpStatus.SC_NO_CONTENT, "test reason");
        HttpResponse responsePut = new BasicHttpResponse(linePut);
        HttpEntity entityPut = Mockito.mock(HttpEntity.class);
        responsePut.setEntity(entityPut );
        Mockito.when(this.restClient.put(Matchers.anyString(), Matchers.anyString(), Matchers.anyListOf(Header.class)))
        .thenReturn(responsePut);

        ProtocolVersion protoDelete = new ProtocolVersion("HTTP", 1, 1);
        BasicStatusLine lineDelete = new BasicStatusLine(protoDelete, HttpStatus.SC_NO_CONTENT, "test reason");
        HttpResponse responseDelete = new BasicHttpResponse(lineDelete);
        HttpEntity entityDelete = Mockito.mock(HttpEntity.class);
        responseDelete.setEntity(entityDelete );
        Mockito.when(this.restClient.delete(Matchers.anyString(), Matchers.anyListOf(Header.class)))
        .thenReturn(responseDelete);
        
        List<Header> headers = this.restClient.getSecureTokenForClientId();

        // get rid of it
        this.modelFactory.deleteModel(uri, headers);//$NON-NLS-1$

        // add it back
        String filterFieldId = uriField;
        String filterFieldValue = uri;
        PutFieldDataRequest putFieldDataRequest = createPutRequest(fieldId, fieldValue, filterFieldId, filterFieldValue);
        Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
        // this.thrown.expect(RuntimeException.class);
        this.putFieldDataProcessor.putFieldData(putFieldDataRequest, modelLookupMap , headers, HttpMethod.POST);

        
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
        String filterFieldValue = "pump22";
        
        ProtocolVersion protoGet = new ProtocolVersion("HTTP", 1, 1);
        BasicStatusLine lineGet = new BasicStatusLine(protoGet, HttpStatus.SC_OK, "test reason for Get");
        HttpResponse responseGet = new BasicHttpResponse(lineGet);
        HttpEntity entityGet = Mockito.mock(HttpEntity.class);
        responseGet.setEntity(entityGet );
        String bodyGet = "[]";
        InputStream streamGet = new ByteArrayInputStream(bodyGet.getBytes());

//        SampleEngine model = new SampleEngine();
//        PositionalAttributes pa = new PositionalAttributes();
//        model.setAdditionalAttributes(pa);
//        pa.getKeys().add("key");
//        pa.getValues().add("value");
//        String body = FdhJsonMapper.toJson(model);
//        model = FdhJsonMapper.fromJson(body, SampleEngine.class);
        
        String bodyGet2 = "[{\"@type\":\"Model\",\"additionalAttributes\":{\"keys\":[\"key\"],\"values\":[\"value\"]}}]";
        BasicStatusLine lineGet2 = new BasicStatusLine(protoGet, HttpStatus.SC_OK, "test reason for Get2");
        InputStream streamGet2 = new ByteArrayInputStream(bodyGet2.getBytes());
        HttpResponse responseGet2 = new BasicHttpResponse(lineGet2);
        HttpEntity entityGet2 = Mockito.mock(HttpEntity.class);
        responseGet2.setEntity(entityGet2 );
 
        Mockito.when(entityGet.getContent()).thenReturn(streamGet);
        Mockito.when(entityGet2.getContent()).thenReturn(streamGet2);
        Mockito.when(entityGet.getContentLength()).thenReturn(new Long(bodyGet.length()));
        Mockito.when(entityGet2.getContentLength()).thenReturn(new Long(bodyGet2.length()));
                
        Answer<?> answerGet = new Answer() {
            private int count = 1;

            public Object answer(InvocationOnMock invocation) {
                if (this.count++ == 1)
                    return responseGet;

                return responseGet2;
            }
        };
        Mockito.when(this.restClient.get(Matchers.anyString(), Matchers.anyListOf(Header.class)))
        .thenAnswer(answerGet);        

        ProtocolVersion protoPut = new ProtocolVersion("HTTP", 1, 1);
        BasicStatusLine linePut = new BasicStatusLine(protoPut, HttpStatus.SC_NO_CONTENT, "test reason");
        HttpResponse responsePut = new BasicHttpResponse(linePut);
        HttpEntity entityPut = Mockito.mock(HttpEntity.class);
        responsePut.setEntity(entityPut );
        Mockito.when(this.restClient.put(Matchers.anyString(), Matchers.anyString(), Matchers.anyListOf(Header.class)))
        .thenReturn(responsePut);

        ProtocolVersion protoDelete = new ProtocolVersion("HTTP", 1, 1);
        BasicStatusLine lineDelete = new BasicStatusLine(protoDelete, HttpStatus.SC_NO_CONTENT, "test reason");
        HttpResponse responseDelete = new BasicHttpResponse(lineDelete);
        HttpEntity entityDelete = Mockito.mock(HttpEntity.class);
        responseDelete.setEntity(entityDelete );
        Mockito.when(this.restClient.delete(Matchers.anyString(), Matchers.anyListOf(Header.class)))
        .thenReturn(responseDelete);

        // get rid of it
        this.modelFactory.deleteModel(filterFieldId, headers);//$NON-NLS-1$
        
        PutFieldDataRequest putFieldDataRequest = createPutRequest(fieldId, fieldValue, filterFieldId, filterFieldValue);
        Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
        //this.thrown.expect(RuntimeException.class);
        this.putFieldDataProcessor.putFieldData(putFieldDataRequest, modelLookupMap , headers, HttpMethod.POST);

       
    }

    /**
     * -
     * 
     * @return
     */
    private GetFieldDataRequest createGetRequest()
    {
        GetFieldDataRequest getFieldDataRequest = new GetFieldDataRequest();
        SolutionIdentifier solutionIdentifier = new SolutionIdentifier();
        solutionIdentifier.setId(1001);
        getFieldDataRequest.setSolutionIdentifier(solutionIdentifier);

        FieldDataCriteria fieldDataCriteria = new FieldDataCriteria();

        // fieldSelection.setResultId("1");

        // add it to fieldData Criteria
        // fieldDataCriteria.getFieldSelection().add(fieldSelection );

        // set selection filter in data criteria
        FieldSelectionFilter condition = new FieldSelectionFilter();
        FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier assetIdFieldIdentifier = new FieldIdentifier();
        assetIdFieldIdentifier.setId("/asset/compressor-2015");
        fieldIdentifierValue.setFieldIdentifier(assetIdFieldIdentifier);
        condition.getFieldIdentifierValue().add(fieldIdentifierValue);
        fieldDataCriteria.setSelectionFilter(condition);

        FieldSelection fieldSelection = new FieldSelection();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId("/meter/cylinder-crank-frame-velocity");
        fieldSelection.setFieldIdentifier(fieldIdentifier);
        fieldDataCriteria.getFieldSelection().add(fieldSelection);

        fieldSelection.setExpectedDataType(OsacbmDataType.DM_DATA_SEQ.name());
        getFieldDataRequest.getFieldDataCriteria().add(fieldDataCriteria);
        return getFieldDataRequest;
    }

    /**
     * @param fieldId
     * @param fieldValue
     * @param filterFieldId
     * @param filterFieldValue
     * @return -
     */
    private PutFieldDataRequest createPutRequest(String fieldId, Double fieldValue, String filterFieldId,
            String filterFieldValue)
    {
        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
        putFieldDataRequest.setCorrelationId("string");
        SolutionIdentifier solutionIdentifier = new SolutionIdentifier();
        solutionIdentifier.setId("soid");
        putFieldDataRequest.setSolutionIdentifier(solutionIdentifier);

        FieldData fieldData = new FieldData();
        Field field = new Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId(fieldId);
        field.setFieldIdentifier(fieldIdentifier);
        fieldData.setField(field);
        PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();
        DMReal dataEvent = new DMReal();
        dataEvent.setValue(fieldValue);
        OsaData data = new OsaData();
        data.setDataEvent(dataEvent);
        fieldData.setData(data);
        fieldDataCriteria.setFieldData(fieldData);
        FieldSelectionFilter fieldSelectionFilter = createSelectionFilter(filterFieldId, filterFieldValue);
        fieldDataCriteria.setSelectionFilter(fieldSelectionFilter);
        putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria);
        return putFieldDataRequest;
    }

    /**
     * @param fieldId
     * @param fieldValue
     * @return -
     */
    private FieldSelectionFilter createSelectionFilter(String fieldId, String fieldValue)
    {
        FieldSelectionFilter fieldSelectionFilter = new FieldSelectionFilter();
        FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
        fieldIdentifierValue.setFieldIdentifier(new FieldIdentifier());
        fieldIdentifierValue.getFieldIdentifier().setId(fieldId);
        fieldIdentifierValue.setValue(fieldValue);
        fieldSelectionFilter.getFieldIdentifierValue().add(fieldIdentifierValue);
        return fieldSelectionFilter;
    }

}
