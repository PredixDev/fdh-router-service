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

import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mimosa.osacbmv3_3.DAString;
import org.mimosa.osacbmv3_3.OsacbmDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.util.map.DataMapList;
import com.ge.predix.entity.util.map.Map;
import com.ge.predix.solsvc.bootstrap.ams.common.AssetConfig;
import com.ge.predix.solsvc.bootstrap.ams.factories.AssetClientImpl;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.asset.helper.JetEngineNoModel;
import com.ge.predix.solsvc.fdh.asset.helper.JetEnginePart;
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
        "classpath*:META-INF/spring/ext-util-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml",
        "classpath*:META-INF/spring/fdh-asset-handler-scan-context.xml",
        "classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml"

})
@ActiveProfiles({"asset"})
public class AssetDataHandlerWithExtraJsonMapperRegistrationIT
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
    private JsonMapper jsonMapper;

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
     * -
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testNoFilterClassThatExtendsModelGettingAChildEntityForPostOnNonPolymporphicClassThatExists()
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
        ArrayList<Object> assets = new ArrayList<Object>();
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
        this.putFieldDataProcessor.putData(putFieldDataRequest, modelLookupMap, headers, HttpPost.METHOD_NAME);

        this.jsonMapper.addSubtype(JetEngineNoModel.class);
        
        
        String filterFieldValue = "/jetEngineNoModel/1";
        String selection = "/jetEngineNoModel/jetEnginePart/*";
        GetFieldDataRequest request = createGetRequest(filterFieldValue, selection, OsacbmDataType.DA_STRING.value());
        GetFieldDataResult getResult = this.getFieldDataProcessor.getData(request, modelLookupMap, headers);

        logger.debug("GET FIELD DATA RESPONSE====================");
        logger.debug(this.jsonMapper.toJson(getResult));

        Assert.assertTrue(((DAString) ((OsaData) getResult.getFieldData().get(0).getData()).getDataEvent()).getValue()
                .contains("22222"));
        
        logger.info("value = " + ((DAString) ((OsaData) getResult.getFieldData().get(0).getData()).getDataEvent()).getValue());

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
    @SuppressWarnings("unchecked")
	private GetFieldDataRequest createGetRequest(String filterFieldValue, String selection, String expectedDataType)
    {
        GetFieldDataRequest getFieldDataRequest = new GetFieldDataRequest();

        FieldDataCriteria fieldDataCriteria = new FieldDataCriteria();
        fieldDataCriteria.setHeaders(new Map());
        fieldDataCriteria.getHeaders().put("Predix-Zone-Id", this.assetClient.getAssetConfig().getZoneId());


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

}
