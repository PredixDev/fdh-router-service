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
import org.apache.http.message.BasicHeader;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mimosa.osacbmv3_3.DMReal;
import org.mimosa.osacbmv3_3.OsacbmDataType;
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
import com.ge.dsp.pm.ext.entity.model.SampleEngine;
import com.ge.dsp.pm.ext.entity.selectionfilter.FieldSelectionFilter;
import com.ge.dsp.pm.ext.entity.solution.identifier.solutionidentifier.SolutionIdentifier;
import com.ge.dsp.pm.fielddatahandler.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataCriteria;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataRequest;
import com.ge.fdh.asset.common.ModelQuery;
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
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml",
        "classpath*:META-INF/spring/fdh-asset-handler-scan-context.xml",
        "classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml"

})
public class GetFieldDataHandlerIT extends AbstractRequestProcessorTest
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
        //
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
     */
    @Test
    public void testNoModelForPostOnPolymporphicClassThatExists()
    {
        String model = "com.ge.dsp.pm.ext.entity.model.SampleEngine";
        String uriField = "/" + model + "/uri";
        String uri = "/" + model + "/engine22";
        String fieldId = "/" + model + "/AverageSpeed";
        Double fieldValue = 22.6;

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

        ModelQuery modelQuery = new ModelQuery();
        modelQuery.setUri(filterFieldValue);
        List<Model> resultingModelList = this.modelFactory.getModels(modelQuery.getQuery(), model, headers);

        Assert.assertNotNull(resultingModelList);
        Assert.assertTrue(resultingModelList.size() > 0);
        Assert.assertEquals(((SampleEngine) resultingModelList.get(0)).getAverageSpeed(), "22.6");
    }

    /**
     * -
     */
    @Test
    public void testNoModelForPostOnClassThatDoesNotExist()
    {
        String model = "DoesNotExist";
        String uriField = "/" + model + "/uri";
        String uri = "/" + model + "/engine22";
        String fieldId = "/" + model + "/AverageSpeed";
        Double fieldValue = 22.6;

        List<Header> headers = this.restClient.getSecureTokenForClientId();
        this.restClient.addZoneToHeaders(headers, this.assetRestConfig.getZoneId());

        // get rid of it
        this.modelFactory.deleteModel(uri, headers);//$NON-NLS-1$

        // add it back
        String filterFieldId = uriField;
        String filterFieldValue = uri;
        PutFieldDataRequest putFieldDataRequest = createPutRequest(fieldId, fieldValue, filterFieldId, filterFieldValue);
        Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
        // this.thrown.expect(RuntimeException.class);
        this.putFieldDataProcessor.putFieldData(putFieldDataRequest, modelLookupMap , headers, HttpMethod.POST);

        ModelQuery modelQuery = new ModelQuery();
        modelQuery.setUri(filterFieldValue);
        List<Model> resultingModelList = this.modelFactory.getModels(modelQuery.getQuery(), model, headers);

        Assert.assertNotNull(resultingModelList);
        Assert.assertTrue(resultingModelList.size() > 0);
        Assert.assertEquals(((SampleEngine) resultingModelList.get(0)).getAverageSpeed(), "22.6");
    }

    /**
     * -
     */
    // @Test
    @Test
    public void testNoModelForPut()
    {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Authorization", null));

        String fieldId = "/Pump/AverageFlow";
        Double fieldValue = 22.6;
        String filterFieldId = "/Pump/uri";
        String filterFieldValue = "pump22";
        PutFieldDataRequest putFieldDataRequest = createPutRequest(fieldId, fieldValue, filterFieldId, filterFieldValue);
        this.thrown.expect(RuntimeException.class);
        Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
        this.putFieldDataProcessor.putFieldData(putFieldDataRequest, modelLookupMap , headers, HttpMethod.POST);

        GetFieldDataRequest getRequest = createGetRequest();
        // this.getFieldDataProcessor.getFieldData(getFieldDataRequest, headers);

        // Assert.assertEquals(getFieldDataValidator, this.getFieldDataProcessor.getGetFieldDataValidator());
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
