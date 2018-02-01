/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.asset.processor.it;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mimosa.osacbmv3_3.DAString;
import org.mimosa.osacbmv3_3.DMReal;
import org.mimosa.osacbmv3_3.OsacbmDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.asset.Asset;
import com.ge.predix.entity.asset.AssetList;
import com.ge.predix.entity.asset.AssetTag;
import com.ge.predix.entity.assetfilter.AssetFilter;
import com.ge.predix.entity.datafile.DataFile;
import com.ge.predix.entity.field.Field;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddata.OsaData;
import com.ge.predix.entity.fielddata.PredixString;
import com.ge.predix.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.predix.entity.fieldselection.FieldSelection;
import com.ge.predix.entity.filter.Filter;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.model.SampleEngine;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.entity.util.map.DataMapList;
import com.ge.predix.entity.util.map.Map;
import com.ge.predix.solsvc.bootstrap.ams.common.AssetConfig;
import com.ge.predix.solsvc.bootstrap.ams.factories.AssetClientImpl;
import com.ge.predix.solsvc.bootstrap.ams.factories.LinkedHashMapModel;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.asset.helper.JetEngineNoModel;
import com.ge.predix.solsvc.fdh.asset.helper.JetEnginePart;
import com.ge.predix.solsvc.fdh.handler.GetDataHandler;
import com.ge.predix.solsvc.fdh.handler.PutDataHandler;
import com.ge.predix.solsvc.fdh.handler.asset.common.AssetQueryBuilder;
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
        "classpath*:META-INF/spring/ext-util-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml",
        "classpath*:META-INF/spring/fdh-asset-handler-scan-context.xml",
        "classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml"
})
@ActiveProfiles({"asset"})
public class AssetDataHandlerIT
{

    private static Logger    logger = LoggerFactory.getLogger(AssetDataHandlerIT.class);

    /**
     * Processor for GetFieldData request
     */
    @Autowired
    @Qualifier(value = "assetGetFieldDataHandler")
    private GetDataHandler   getFieldDataProcessor;
    @Autowired
    @Qualifier(value = "assetPutFieldDataHandler")
    private PutDataHandler   putFieldDataProcessor;
    @Autowired
    private RestClient       restClient;
    /**
     * 
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Autowired
    private AssetConfig      assetConfig;
    
    @Autowired
    @Qualifier("AssetClient")
    private AssetClientImpl              assetClient;

    @Autowired
    private JsonMapper       jsonMapper;

    /**
     * @throws java.lang.Exception
     *             -
     */
    @Before
    public void setUp()
    {
        //
    }

    /**
     * @throws java.lang.Exception
     *             -
     */
    @After
    public void tearDown()
    {
        //
    }

    /**
     * - Note SampleEngine extends Model extends Data which has a JsonTypeInfo
     * annotation so it gets polymorphically (Animal/Cat/Dog) generated {
     * "complexType": "SampleEngine", "additionalAttributes": [ { "name":
     * "averageSpeed", "attribute": { "value": [ "22.5" ] } } ], "averageSpeed":
     * "22.2" }
     * 
     * @throws InterruptedException
     *             -
     */
   @Test
    public void testWithFilterPuttingAClassThatExtendsModelForPostOnPolymporphicClassThatExists()
            throws InterruptedException
    {
        String model = "com.ge.predix.entity.model.SampleEngine";
        String uri = "/" + model + "/engine23";
        String fieldId = "/" + model + "/averageSpeed";
        Double fieldValue = 22.6;

        List<Header> headers = this.restClient.getSecureTokenForClientId();
        this.restClient.addZoneToHeaders(headers, this.assetConfig.getZoneId());

        // get rid of it
        this.assetClient.deleteModel(uri, headers);// $NON-NLS-1$

        // add it back
        PutFieldDataRequest putFieldDataRequest = createPutRequestUsingDMReal(fieldId, fieldValue, uri);
        java.util.Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();
        // this.thrown.expect(RuntimeException.class);
        this.putFieldDataProcessor.putData(putFieldDataRequest, modelLookupMap, headers, HttpPost.METHOD_NAME);

        AssetQueryBuilder assetQueryBuilder = new AssetQueryBuilder();
        assetQueryBuilder.setUri(uri);
        List<Object> resultingModelList = this.assetClient.getModels(assetQueryBuilder.build(), model, headers);

        Assert.assertNotNull(resultingModelList);
        Assert.assertTrue(resultingModelList.size() > 0);
        String averageSpeed = ((SampleEngine) resultingModelList.get(0)).getAverageSpeed();
        Assert.assertEquals("22.6", averageSpeed);
    }

    /**
     * -
     */
   @Test
    public void testWithFilterNoModelForPostOnClassThatDoesNotExist()
    {
        String model = "DoesNotExist";
        String uri = "/" + model + "/engine22";
        String fieldId = "/" + model + "/averageSpeed";
        Double fieldValue = 22.6;

        List<Header> headers = this.restClient.getSecureTokenForClientId();
        this.restClient.addZoneToHeaders(headers, this.assetConfig.getZoneId());

        // get rid of it
        this.assetClient.deleteModel(uri, headers);// $NON-NLS-1$

        // add it back
        PutFieldDataRequest putFieldDataRequest = createPutRequestUsingDMReal(fieldId, fieldValue, uri);
        java.util.Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();
        // this.thrown.expect(RuntimeException.class);
        this.putFieldDataProcessor.putData(putFieldDataRequest, modelLookupMap, headers, HttpPost.METHOD_NAME);

        AssetQueryBuilder assetQueryBuilder = new AssetQueryBuilder();
        assetQueryBuilder.setUri(uri);
        List<Object> resultingModelList = this.assetClient.getModels(assetQueryBuilder.build(), model, headers);

        Assert.assertNotNull(resultingModelList);
        Assert.assertTrue(resultingModelList.size() > 0);
        String averageSpeed = (String) ((LinkedHashMapModel) resultingModelList.get(0)).getMap().get("averageSpeed");
        Assert.assertEquals("22.6", averageSpeed);
    }

   
   /**
 *  -
 * @throws JSONException -
 */
@Test
   public void testWithFilterNoModelForPostOnClassThatDoesNotExistUsingPredixString() throws JSONException
   {
       String model = "DoesNotExist";
       String uri = "/" + model + "/engine22PredixString";
       String fieldId = "/" + model + "/averageSpeed";
       /**
        * This exists in the Asset UAA URI for the query:https://predix-asset.run.aws-usw02-pr.ice.predix.io/DoesNotExist
        * [
			  {
			    "complexType": "Model",
			    "uri": "/DoesNotExist/engine22",
			    "averageSpeed": "22.6"
			  },
			  {
			    "uri": "/DoesNotExist/engine22PredixString",
			    "description": "test"
			  }
		]
        */
       JSONObject jsonObj = new JSONObject();
       jsonObj.put("uri", uri);
       jsonObj.put("description", "test");
       String fieldValue = jsonObj.toString();

       List<Header> headers = this.restClient.getSecureTokenForClientId();
       this.restClient.addZoneToHeaders(headers, this.assetConfig.getZoneId());

       // get rid of it
       this.assetClient.deleteModel(uri, headers);// $NON-NLS-1$

       // add it back
       PutFieldDataRequest putFieldDataRequest = createPutRequestUsingPredixString(fieldId, fieldValue, uri);
       java.util.Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();
       // this.thrown.expect(RuntimeException.class);
       this.putFieldDataProcessor.putData(putFieldDataRequest, modelLookupMap, headers, HttpPost.METHOD_NAME);

       AssetQueryBuilder assetQueryBuilder = new AssetQueryBuilder();
       assetQueryBuilder.setUri(uri);
       List<Object> resultingModelList = this.assetClient.getModels(assetQueryBuilder.build(), model, headers);

       Assert.assertNotNull(resultingModelList);
       Assert.assertTrue(resultingModelList.size() > 0);
       String description = (String) ((LinkedHashMapModel) resultingModelList.get(0)).getMap().get("description");
       Assert.assertEquals("test", description);
       
       GetFieldDataRequest request = createGetRequest(uri, "/asset", PredixString.class.getSimpleName());
       GetFieldDataResult getResult = this.getFieldDataProcessor.getData(request, modelLookupMap, headers);
       Assert.assertNotNull(getResult);
       
   }

   
    /**
     * -
     */
    @SuppressWarnings("unchecked")
   @Test
    public void testNoFilterClassThatExtendsModelGettingAChildEntityForPostOnPolymporphicClassThatExists()
    {
        List<Header> headers = this.restClient.getSecureTokenForClientId();
        this.restClient.addZoneToHeaders(headers, this.assetConfig.getZoneId());

        // get rid of it
        this.assetClient.deleteModel("/asset/1", headers);//$NON-NLS-1$

        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
        putFieldDataRequest.setCorrelationId("string");

        FieldData fieldData = new FieldData();
        Field field = new Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId("/asset/1");
        fieldIdentifier.setSource(FieldSourceEnum.PREDIX_ASSET.name());
        field.setFieldIdentifier(fieldIdentifier);
        fieldData.getField().add(field);

        PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();
        fieldDataCriteria.setFieldData(fieldData);
        putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria);

        // create a sample asset with no Filter in the put and the data is a
        // json String, it should simply save it in Predix Asset
        AssetList assets = new AssetList();
        Asset asset = new Asset();
        assets.getAsset().add(asset);
        asset.setUri("/asset/1");
        asset.setName("my sample asset");
        asset.setAssetTag(new Map());
        AssetTag assetTag = new AssetTag();
        assetTag.setTagUri("/pressure");
        asset.getAssetTag().put("pressure", assetTag);
        @SuppressWarnings("unused")
        String assetJson = this.jsonMapper.toJson(assets);
        fieldData.setData(assets);

        @SuppressWarnings("unused")
        String json = this.jsonMapper.toJson(putFieldDataRequest);

        // add it back
        java.util.Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();
        this.putFieldDataProcessor.putData(putFieldDataRequest, modelLookupMap, headers, HttpPost.METHOD_NAME);

        AssetQueryBuilder assetQueryBuilder = new AssetQueryBuilder();
        assetQueryBuilder.setUri("/asset/1");
        List<Object> resultingModelList = this.assetClient.getModels(assetQueryBuilder.build(), "Asset", headers);

        Assert.assertNotNull(resultingModelList);
        Assert.assertTrue(resultingModelList.size() > 0);
        Assert.assertEquals("/pressure",
                ((AssetTag) ((Asset) resultingModelList.get(0)).getAssetTag().get("pressure")).getTagUri());

        String filterFieldValue = "/asset/1";
        String selection = "/asset/assetTag/pressure";
        GetFieldDataRequest request = createGetRequest(filterFieldValue, selection, OsacbmDataType.DA_STRING.value());
        GetFieldDataResult getResult = this.getFieldDataProcessor.getData(request, modelLookupMap, headers);

        logger.debug("GET FIELD DATA RESPONSE====================");
        logger.debug(this.jsonMapper.toJson(getResult));

        Assert.assertTrue(((DAString) ((OsaData) getResult.getFieldData().get(0).getData()).getDataEvent()).getValue()
                .contains("pressure"));
        logger.info("value = "
                + ((DAString) ((OsaData) getResult.getFieldData().get(0).getData()).getDataEvent()).getValue());

    }

    /**
     * -
     */
    @SuppressWarnings("unchecked")
   @Test
    public void testClassThatDoesNotExtendModelOnNonPolymporphicClassThatExistsButUnregisteredWithJsonMapper()
    {
        List<Header> headers = this.restClient.getSecureTokenForClientId();
        this.restClient.addZoneToHeaders(headers, this.assetConfig.getZoneId());

        // get rid of it
        this.assetClient.deleteModel("/jetEngineNoModel/1", headers);//$NON-NLS-1$

        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
        putFieldDataRequest.setCorrelationId("string");

        FieldData fieldData = new FieldData();
        Field field = new Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId("/jetEngineNoModel/1");
        fieldIdentifier.setSource(FieldSourceEnum.PREDIX_ASSET.name());
        field.setFieldIdentifier(fieldIdentifier);
        fieldData.getField().add(field);

        PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();
        fieldDataCriteria.setFieldData(fieldData);
        putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria);

        // create a sample asset with no Filter in the put and the data is a
        // json String, it should simply save it in Predix Asset
        ArrayList<JetEngineNoModel> assets = new ArrayList<JetEngineNoModel>();
        JetEngineNoModel asset = new JetEngineNoModel();
        assets.add(asset);
        asset.setUri("/jetEngineNoModel/1");
        asset.setSerialNo(12345);
        JetEnginePart jetEnginePart = new JetEnginePart();
        jetEnginePart.setsNo(22222);
        asset.setJetEnginePart(jetEnginePart);

        String assetJson = this.jsonMapper.toJson(assets);
        List<Object> listNoModel = this.jsonMapper.fromJsonArray(assetJson, Object.class);
        DataMapList data = new DataMapList();
        for (Object item : listNoModel)
        {
            LinkedHashMap<?, ?> linkedMap = (LinkedHashMap<?, ?>) item;
            com.ge.predix.entity.util.map.Map map = new com.ge.predix.entity.util.map.Map();
            data.getMap().add(map);
            map.putAll(linkedMap);
        }
        fieldData.setData(data);
        @SuppressWarnings("unused")
        String json = this.jsonMapper.toJson(putFieldDataRequest);

        // add it back
        java.util.Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();
        PutFieldDataResult result = this.putFieldDataProcessor.putData(putFieldDataRequest, modelLookupMap, headers,
                HttpPost.METHOD_NAME);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getErrorEvent().size() == 0);

        AssetQueryBuilder assetQueryBuilder = new AssetQueryBuilder();
        assetQueryBuilder.setUri("/jetEngineNoModel/1");
        List<Object> resultingModelList = this.assetClient.getModels(assetQueryBuilder.build(),
                "com.ge.predix.solsvc.fdh.asset.helper.JetEngineNoModel", headers);

        Assert.assertNotNull(resultingModelList);
        Assert.assertTrue(resultingModelList.size() > 0);
        Assert.assertEquals(new Integer(12345),
                ((LinkedHashMapModel) resultingModelList.get(0)).getMap().get("serialNo"));

        this.jsonMapper.resetSubtypes();

        String filterFieldValue = "/jetEngineNoModel/1";
        String selection = "/jetEngineNoModel/map/jetEnginePart/*";
        GetFieldDataRequest request = createGetRequest(filterFieldValue, selection, OsacbmDataType.DA_STRING.value());
        GetFieldDataResult getResult = this.getFieldDataProcessor.getData(request, modelLookupMap, headers);
        logger.debug(this.jsonMapper.toJson(getResult));
        boolean value = ((DAString) ((OsaData) getResult.getFieldData().get(0).getData()).getDataEvent()).getValue()
                .contains("22222");

        Assert.assertTrue(value);
        logger.info("value = "
                + ((DAString) ((OsaData) getResult.getFieldData().get(0).getData()).getDataEvent()).getValue());
    }

    /**
     */
   @Test
    public void testAssetCriteriaForRootofAsset()
    {

        logger.info("================================");
        String assetUri = "/asset/compressor-2017";
        String assetAttribute = "/asset/*";
        String assetExpectedDataType = OsacbmDataType.DA_STRING.value();

        GetFieldDataRequest request = createGetRequest(assetUri, assetAttribute, assetExpectedDataType);

        logger.debug("request=" + this.jsonMapper.toJson(request));

        java.util.Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Content-Type", "application/json"));
        this.restClient.addSecureTokenForHeaders(headers);

        GetFieldDataResult response = this.getFieldDataProcessor.getData(request, modelLookupMap, headers);

        logger.info("Response = " + response.getFieldData().get(0).getData());

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getFieldData().get(0).getData());
        Assert.assertTrue(response.getFieldData().get(0).getData() instanceof OsaData);
        OsaData osaData = (OsaData) response.getFieldData().get(0).getData();
        Assert.assertTrue(((DAString) osaData.getDataEvent()).getValue().length() > 0);

        logger.info("value = " + ((DAString) osaData.getDataEvent()).getValue());

    }

    /**
     */
   @Test
    public void testAssetCriteriaForRootAssetAttribute()
    {

        logger.info("================================");
        String assetUri = "/asset/compressor-2017";
        String assetAttribute = "/description";
        String assetExpectedDataType = OsacbmDataType.DA_STRING.value();

        GetFieldDataRequest request = createGetRequest(assetUri, assetAttribute, assetExpectedDataType);

        logger.debug("request=" + this.jsonMapper.toJson(request));

        java.util.Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Content-Type", "application/json"));
        this.restClient.addSecureTokenForHeaders(headers);

        GetFieldDataResult response = this.getFieldDataProcessor.getData(request, modelLookupMap, headers);

        logger.info("Response = " + response.getFieldData().get(0).getData());

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getFieldData().get(0).getData());
        Assert.assertTrue(response.getFieldData().get(0).getData() instanceof OsaData);
        OsaData osaData = (OsaData) response.getFieldData().get(0).getData();
        Assert.assertTrue(((DAString) osaData.getDataEvent()).getValue().length() > 0);

        logger.info("value = " + ((DAString) osaData.getDataEvent()).getValue());

    }

    /**
     */
   @Test
    public void testAssetCriteriaForRootofMap()
    {

        logger.info("================================");
        String assetUri = "/asset/compressor-2017";
        String assetAttribute = "/asset/assetTag/*";
        String assetExpectedDataType = OsacbmDataType.DA_STRING.value();

        GetFieldDataRequest request = createGetRequest(assetUri, assetAttribute, assetExpectedDataType);

        logger.debug("request=" + this.jsonMapper.toJson(request));

        java.util.Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Content-Type", "application/json"));
        this.restClient.addSecureTokenForHeaders(headers);

        GetFieldDataResult response = this.getFieldDataProcessor.getData(request, modelLookupMap, headers);

        logger.info("Response = " + response.getFieldData().get(0).getData());

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getFieldData().get(0).getData());
        Assert.assertTrue(response.getFieldData().get(0).getData() instanceof OsaData);
        OsaData osaData = (OsaData) response.getFieldData().get(0).getData();
        Assert.assertTrue(((DAString) osaData.getDataEvent()).getValue().length() > 0);

        logger.info("value = " + ((DAString) osaData.getDataEvent()).getValue());

    }

    /**
     */
   @Test
    public void testAssetCriteriaForRootofMapItem()
    {

        logger.info("================================");
        String assetUri = "/asset/compressor-2017";
        String assetAttribute = "/asset/assetTag/crank-frame-dischargepressure/*";
        String assetExpectedDataType = OsacbmDataType.DA_STRING.value();

        GetFieldDataRequest request = createGetRequest(assetUri, assetAttribute, assetExpectedDataType);

        logger.debug("request=" + this.jsonMapper.toJson(request));

        java.util.Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Content-Type", "application/json"));
        this.restClient.addSecureTokenForHeaders(headers);

        GetFieldDataResult response = this.getFieldDataProcessor.getData(request, modelLookupMap, headers);

        logger.info("Response = " + response.getFieldData().get(0).getData());

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getFieldData().get(0).getData());
        Assert.assertTrue(response.getFieldData().get(0).getData() instanceof OsaData);
        OsaData osaData = (OsaData) response.getFieldData().get(0).getData();
        Assert.assertTrue(((DAString) osaData.getDataEvent()).getValue().length() > 0);

        logger.info("value = " + ((DAString) osaData.getDataEvent()).getValue());

    }

    /**
     */
   @Test
    public void testAssetCriteriaForMapItemAttribute()
    {

        logger.info("================================");
        String assetUri = "/asset/compressor-2017";
        // String assetAttribute = "/asset/assetTag/crank-frame-dischargepressure/timeseriesDatasource/tag";
        String assetAttribute = "/asset/assetTag/crank-frame-dischargepressure/timeseriesDatasource/tag*";
        String assetExpectedDataType = OsacbmDataType.DA_STRING.value();

        GetFieldDataRequest request = createGetRequest(assetUri, assetAttribute, assetExpectedDataType);

        logger.debug("request=" + this.jsonMapper.toJson(request));

        java.util.Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Content-Type", "application/json"));
        this.restClient.addSecureTokenForHeaders(headers);

        GetFieldDataResult response = this.getFieldDataProcessor.getData(request, modelLookupMap, headers);

        logger.info("Response = " + response.getFieldData().get(0).getData());

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getFieldData().get(0).getData());
        Assert.assertTrue(response.getFieldData().get(0).getData() instanceof OsaData);
        OsaData osaData = (OsaData) response.getFieldData().get(0).getData();
        Assert.assertTrue(((DAString) osaData.getDataEvent()).getValue().length() > 0);

        logger.info("value = " + ((DAString) osaData.getDataEvent()).getValue());

    }

    /**
     * -
     * 
     * @param filterFieldId
     *            TODO
     * @param expectedDataType
     *            TODO
     * @param fieldSelection
     *            TODO
     * @return
     */
    private GetFieldDataRequest createGetRequest(String filterFieldValue, String selection, String expectedDataType)
    {
        GetFieldDataRequest getFieldDataRequest = new GetFieldDataRequest();

        FieldDataCriteria fieldDataCriteria = new FieldDataCriteria();

        // SELECT
        FieldSelection fieldSelection = new FieldSelection();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId(selection);
        fieldSelection.setFieldIdentifier(fieldIdentifier);
        fieldSelection.setExpectedDataType(expectedDataType);

        // FILTER
        AssetFilter assetFilter = new AssetFilter();
        assetFilter.setUri(filterFieldValue);

        // SELECT
        fieldDataCriteria.getFieldSelection().add(fieldSelection);
        // WHERE
        fieldDataCriteria.setFilter(assetFilter);

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
    private PutFieldDataRequest createPutRequestUsingDMReal(String fieldId, Double fieldValue, String uri)
    {

        FieldData fieldData = new FieldData();
        Field field = new Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId(fieldId);
        field.setFieldIdentifier(fieldIdentifier);
        fieldData.getField().add(field);

        DMReal dataEvent = new DMReal();
        dataEvent.setValue(fieldValue);
        OsaData data = new OsaData();
        data.setDataEvent(dataEvent);
        fieldData.setData(data);

        // Criteria
        PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();
        fieldDataCriteria.setFieldData(fieldData);

        // Asset filter
        AssetFilter assetFilter = createAssetFilter(uri);
        fieldDataCriteria.setFilter(assetFilter);

        // Request
        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
        putFieldDataRequest.setCorrelationId("string");
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
    private PutFieldDataRequest createPutRequestUsingPredixString(String fieldId, String fieldValue, String uri)
    {

        FieldData fieldData = new FieldData();
        Field field = new Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId(fieldId);
        field.setFieldIdentifier(fieldIdentifier);
        fieldData.getField().add(field);

        PredixString predixString = new PredixString();
        predixString.setString(fieldValue);
        fieldData.setData(predixString);

        // Criteria
        PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();
        fieldDataCriteria.setFieldData(fieldData);

        // Asset filter
        AssetFilter assetFilter = createAssetFilter(uri);
        fieldDataCriteria.setFilter(assetFilter);

        // Request
        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
        putFieldDataRequest.setCorrelationId("string");
        putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria);

        return putFieldDataRequest;
    }

    
    /**
     * @param fieldId
     * @param fieldValue
     * @return -
     */
    private AssetFilter createAssetFilter(String uri)
    {
        AssetFilter assetFilter = new AssetFilter();
        assetFilter.setUri(uri);
        return assetFilter;
    }

    @SuppressWarnings({
            "resource", "javadoc"
    })
    @Test
    public void testWithFileUpload() throws IOException
    {

        String putFieldDataString = "{\"putFieldDataCriteria\":[{\"namespaces\":[],\"fieldData\":{\"field\":[{\"fieldIdentifier\":{\"complexType\":\"FieldIdentifier\",\"source\":\"PREDIX_ASSET\"},\"parents\":[]}],\"data\":{\"complexType\":\"DataFile\",\"source\":\"PREDIX_ASSET\"}}}]}";
        List<Header> headers = this.restClient.getSecureTokenForClientId();
        this.restClient.addZoneToHeaders(headers, this.assetConfig.getZoneId());
        headers.add(new BasicHeader("Content-type", "application/json"));
        // get rid of it
        this.assetClient.deleteModel("/classification/Locomotive-Test", headers);//$NON-NLS-1$

        PutFieldDataRequest putFieldDataRequest = this.jsonMapper.fromJson(putFieldDataString,
                PutFieldDataRequest.class);
        File initialFile = new File("src/test/resources/testFiles/CarAndLocomotives.json");
        InputStream targetStream;

        targetStream = FileUtils.openInputStream(initialFile);
        updatePutRequest(initialFile.getName(), targetStream, putFieldDataRequest);
        // add it back
        java.util.Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();
        PutFieldDataResult result = this.putFieldDataProcessor.putData(putFieldDataRequest, modelLookupMap, headers,
                HttpPost.METHOD_NAME);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getErrorEvent() != null);
    

    }

    private void updatePutRequest(String filename, InputStream file, PutFieldDataRequest putFieldDataRequest)
    {

        if ( putFieldDataRequest == null ) throw new RuntimeException("PutFieldDataRequest is missing "); //$NON-NLS-1$

        PutFieldDataCriteria fieldDataCriteria = null;
        if ( putFieldDataRequest != null && (putFieldDataRequest.getPutFieldDataCriteria() == null
                || putFieldDataRequest.getPutFieldDataCriteria().size() == 0) )
        {
            fieldDataCriteria = new PutFieldDataCriteria();
            Filter selectionFilter = new Filter();
            fieldDataCriteria.setFilter(selectionFilter);
            putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria);
        }
        fieldDataCriteria = putFieldDataRequest.getPutFieldDataCriteria().get(0);
        FieldData fieldData = fieldDataCriteria.getFieldData();

        DataFile datafile = new DataFile();
        datafile.setName(filename);
        try
        {
            datafile.setFile(IOUtils.toByteArray(file));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        fieldData.setData(datafile);
        fieldDataCriteria.setFieldData(fieldData);
    }

}
