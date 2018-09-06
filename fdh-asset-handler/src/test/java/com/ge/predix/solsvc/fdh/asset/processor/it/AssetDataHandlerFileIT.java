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
import com.ge.predix.solsvc.bootstrap.ams.client.AssetClientImpl;
import com.ge.predix.solsvc.bootstrap.ams.client.LinkedHashMapModel;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.asset.helper.JetEngineNoModel;
import com.ge.predix.solsvc.fdh.asset.helper.JetEnginePart;
import com.ge.predix.solsvc.fdh.handler.GetDataHandler;
import com.ge.predix.solsvc.fdh.handler.PutDataHandler;
import com.ge.predix.solsvc.fdh.handler.asset.common.AssetQueryBuilder;

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
        "classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml",
        "classpath*:META-INF/spring/fdh-asset-handler-scan-context.xml",
        "classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml"
})
@ActiveProfiles({"asset", "asset-file"})
public class AssetDataHandlerFileIT
{

    private static Logger    logger = LoggerFactory.getLogger(AssetDataHandlerFileIT.class);

    /**
     * Processor for GetFieldData request
     */
    @Autowired
    @Qualifier(value = "assetGetFieldDataHandler")
    private GetDataHandler   getFieldDataProcessor;
    @Autowired
    @Qualifier(value = "assetPutFieldDataHandler")
    private PutDataHandler   putFieldDataProcessor;
    /**
     * 
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();
 
    
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


    @SuppressWarnings({
            "resource", "javadoc"
    })
    @Test
    public void testWithFileUpload() throws IOException
    {

        String putFieldDataString = "{\"putFieldDataCriteria\":[{\"namespaces\":[],\"fieldData\":{\"field\":[{\"fieldIdentifier\":{\"complexType\":\"FieldIdentifier\",\"source\":\"PREDIX_ASSET\"},\"parents\":[]}],\"data\":{\"complexType\":\"DataFile\",\"source\":\"PREDIX_ASSET\"}}}]}";
        List<Header> headers = this.assetClient.getAssetHeaders();

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
