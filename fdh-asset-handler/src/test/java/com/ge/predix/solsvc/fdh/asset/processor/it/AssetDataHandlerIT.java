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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.HttpMethod;

import org.apache.http.Header;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.asset.Asset;
import com.ge.predix.entity.asset.AssetList;
import com.ge.predix.entity.asset.AssetTag;
import com.ge.predix.entity.assetfilter.AssetFilter;
import com.ge.predix.entity.field.Field;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddata.OsaData;
import com.ge.predix.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.predix.entity.fieldselection.FieldSelection;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.model.SampleEngine;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.entity.util.map.DataMap;
import com.ge.predix.entity.util.map.Map;
import com.ge.predix.solsvc.bootstrap.ams.common.AssetConfig;
import com.ge.predix.solsvc.bootstrap.ams.factories.LinkedHashMapModel;
import com.ge.predix.solsvc.bootstrap.ams.factories.ModelFactory;
import com.ge.predix.solsvc.fdh.asset.helper.JetEngineNoModel;
import com.ge.predix.solsvc.fdh.asset.processor.AbstractRequestProcessorTest;
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
public class AssetDataHandlerIT extends AbstractRequestProcessorTest
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
    private ModelFactory     modelFactory;

    /**
     * @throws java.lang.Exception
     *             -
     */
    @Before
    public void setUp()
            throws Exception
    {
        //
    }

    /**
     * @throws java.lang.Exception
     *             -
     */
    @After
    public void tearDown()
            throws Exception
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
        String uri = "/" + model + "/engine22";
        String fieldId = "/" + model + "/averageSpeed";
        Double fieldValue = 22.6;

        List<Header> headers = this.restClient.getSecureTokenForClientId();
        this.restClient.addZoneToHeaders(headers, this.assetConfig.getZoneId());

        // get rid of it
        this.modelFactory.deleteModel(uri, headers);// $NON-NLS-1$

        // add it back
        PutFieldDataRequest putFieldDataRequest = createPutRequest(fieldId, fieldValue, uri);
        java.util.Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
        // this.thrown.expect(RuntimeException.class);
        this.putFieldDataProcessor.putData(putFieldDataRequest, modelLookupMap, headers, HttpMethod.POST);

        AssetQueryBuilder assetQueryBuilder = new AssetQueryBuilder();
        assetQueryBuilder.setUri(uri);
        List<Model> resultingModelList = this.modelFactory.getModels(assetQueryBuilder.build(), model, headers);

        Assert.assertNotNull(resultingModelList);
        Assert.assertTrue(resultingModelList.size() > 0);
        Assert.assertEquals("22.6", ((SampleEngine) resultingModelList.get(0)).getAverageSpeed());
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
        this.modelFactory.deleteModel(uri, headers);// $NON-NLS-1$

        // add it back
        PutFieldDataRequest putFieldDataRequest = createPutRequest(fieldId, fieldValue, uri);
        java.util.Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
        // this.thrown.expect(RuntimeException.class);
        this.putFieldDataProcessor.putData(putFieldDataRequest, modelLookupMap, headers, HttpMethod.POST);

        AssetQueryBuilder assetQueryBuilder = new AssetQueryBuilder();
        assetQueryBuilder.setUri(uri);
        List<Model> resultingModelList = this.modelFactory.getModels(assetQueryBuilder.build(), model, headers);

        Assert.assertNotNull(resultingModelList);
        Assert.assertTrue(resultingModelList.size() > 0);
        Assert.assertEquals("22.6", ((LinkedHashMapModel) resultingModelList.get(0)).getMap().get("averageSpeed"));
    }

    /**
	 * -
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testNoFilterClassThatDoesNotExtendModelOnFullAssetForPostOnPolymporphicClassThatExists() {
		List<Header> headers = this.restClient.getSecureTokenForClientId();
		this.restClient.addZoneToHeaders(headers,
				this.assetConfig.getZoneId());

		// get rid of it
		this.modelFactory.deleteModel("/jetEngineNoModel/1", headers);//$NON-NLS-1$

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
		String assetJson = this.jsonMapper.toJson(assets);
		List<Object> listNoModel = this.jsonMapper.fromJsonArray(assetJson, Object.class);
		DataMap data = new DataMap();
		for (Object item : listNoModel) {
		    LinkedHashMap<?, ?> linkedMap = (LinkedHashMap<?, ?>) item;
			com.ge.predix.entity.util.map.Map map = new com.ge.predix.entity.util.map.Map();
			data.getMap().add(map);
			map.putAll(linkedMap);
		}
		fieldData.setData(data);
		@SuppressWarnings("unused")
		String json = this.jsonMapper.toJson(putFieldDataRequest);

		// add it back
		java.util.Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
		PutFieldDataResult result = this.putFieldDataProcessor.putData(
				putFieldDataRequest, modelLookupMap, headers, HttpMethod.POST);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.getErrorEvent().size() == 0);

		AssetQueryBuilder assetQueryBuilder = new AssetQueryBuilder();
		assetQueryBuilder.setUri("/jetEngineNoModel/1");
		List<Model> resultingModelList = this.modelFactory.getModels(
				assetQueryBuilder.build(),
				"com.ge.predix.solsvc.fdh.asset.helper.JetEngineNoModel",
				headers);

		Assert.assertNotNull(resultingModelList);
		Assert.assertTrue(resultingModelList.size() > 0);
		Assert.assertEquals(
				12345,
				((LinkedHashMapModel) resultingModelList.get(0)).getMap().get(
						"serialNo"));

		String filterFieldValue = "/jetEngineNoModel/1";
		String selection = "/jetEngineNoModel";
		GetFieldDataRequest request = createGetRequest(
				filterFieldValue, selection, OsacbmDataType.DA_STRING.value());
		GetFieldDataResult getResult = this.getFieldDataProcessor.getData(
				request, modelLookupMap, headers);
		logger.debug(this.jsonMapper.toJson(getResult));

		Assert.assertTrue(((DAString) ((OsaData) getResult.getFieldData()
				.get(0).getData()).getDataEvent()).getValue().contains("12345"));

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
        this.modelFactory.deleteModel("/asset/1", headers);//$NON-NLS-1$

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
        java.util.Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
        this.putFieldDataProcessor.putData(putFieldDataRequest, modelLookupMap, headers, HttpMethod.POST);

        AssetQueryBuilder assetQueryBuilder = new AssetQueryBuilder();
        assetQueryBuilder.setUri("/asset/1");
        List<Model> resultingModelList = this.modelFactory.getModels(assetQueryBuilder.build(), "Asset", headers);

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
    private PutFieldDataRequest createPutRequest(String fieldId, Double fieldValue, String uri)
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
     * @return -
     */
    private AssetFilter createAssetFilter(String uri)
    {
        AssetFilter assetFilter = new AssetFilter();
        assetFilter.setUri(uri);
        return assetFilter;
    }

}
