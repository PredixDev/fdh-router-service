/*
 * Copyright (c) 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.handler.timeseries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mimosa.osacbmv3_3.OsacbmDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * 
 * 
 * @author Sankar
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{
        "classpath*:META-INF/spring/fdh-adapter-scan-context.xml",
        "classpath*:META-INF/spring/fdh-timeseries-handler-scan-context.xml",
        "classpath*:META-INF/spring/fdh-asset-handler-scan-context.xml",
        "classpath*:META-INF/spring/TEST-fdh-timeseries-handler-properties-context.xml",
        "classpath*:META-INF/spring/ext-util-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-websocket-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml",
        "classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml"

})
@ActiveProfiles(
{
        "timeseries"
})
public class TimeseriesHandlerGetIT
{

    private static Logger            log = LoggerFactory.getLogger(TimeseriesHandlerGetIT.class);

    @Autowired
    private TimeseriesGetDataHandler getHandler;

    @Autowired
    private RestClient               restClient;

    @Autowired
    private JsonMapper               mapper;

    /**
     * -
     */
    @SuppressWarnings("nls")
    @Test
    public void testTSFilterWithTimeBoundedRequest()
    {

        log.info("================================");
        String timeseriesField = "/tags/datapoints";
        String timeseriesFieldSource = FieldSourceEnum.PREDIX_TIMESERIES.name();
        String timeseriesExpectedDataType = DatapointsResponse.class.getSimpleName();
        String timeseriesTagname = "Compressor-2015:DischargePressure";
        
        GetFieldDataRequest request = TestData.getFieldDataRequestwithTs(timeseriesField, timeseriesFieldSource,
                timeseriesExpectedDataType, timeseriesTagname, "1d-ago", null);


        log.debug("request=" + this.mapper.toJson(request));
        Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Content-Type", "application/json"));
        this.restClient.addSecureTokenForHeaders(headers);

        GetFieldDataResult response = this.getHandler.getData(request, modelLookupMap, headers);

        log.info("Response =" + this.mapper.toJson(response));
        log.info("Response = " + response.getFieldData().get(0).getData());

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getFieldData().get(0).getData());
        Assert.assertTrue(response.getFieldData().get(0).getData() instanceof DatapointsResponse);
        DatapointsResponse dpResponse = (DatapointsResponse) response.getFieldData().get(0).getData();
        
        log.info("DP Response tags size =" + dpResponse.getTags().size());
        Assert.assertTrue(dpResponse.getTags().size() > 0);
        
        log.info("DP Response stats  =" + dpResponse.getTags().get(0).getStats());
        Assert.assertTrue(dpResponse.getTags().get(0).getStats().getRawCount() > 0);
    }

    /**
     * -
     */
    @SuppressWarnings("nls")
    @Test
    public void testTSFilterWithGetLatestDataPoints()
    {

        log.info("================================");
        String timeseriesField = "/tags/datapoints/latest";
        String timeseriesFieldSource = FieldSourceEnum.PREDIX_TIMESERIES.name();
        String timeseriesExpectedDataType = DatapointsResponse.class.getSimpleName();
        String timeseriesTagName = "Compressor-2015:DischargePressure";
        
        GetFieldDataRequest request = TestData.getFieldDataRequestLatestDatapoint(timeseriesField,
                timeseriesFieldSource, timeseriesExpectedDataType, timeseriesTagName);
        log.debug("request=" + this.mapper.toJson(request));

        Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Content-Type", "application/json"));
        this.restClient.addSecureTokenForHeaders(headers);

        GetFieldDataResult response = this.getHandler.getData(request, modelLookupMap, headers);

        log.info("Response =" + this.mapper.toJson(response));
        log.info("Response = " + response.getFieldData().get(0).getData());

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getFieldData().get(0).getData());
        Assert.assertTrue(response.getFieldData().get(0).getData() instanceof DatapointsResponse);
        Assert.assertTrue(response.getFieldData().get(0).getData() instanceof DatapointsResponse);
        DatapointsResponse dpResponse = (DatapointsResponse) response.getFieldData().get(0).getData();
        Assert.assertTrue(dpResponse.getTags().size() > 0);
        Assert.assertTrue(dpResponse.getTags().get(0).getStats().getRawCount() > 0);
    }


    /**
     */
    @SuppressWarnings("nls")
    @Test
    public void testAssetCriteriaAwareTSFilter()
    {

        log.info("================================");
        String timeseriesField = "/timeseries/tag";
        String timeseriesFieldSource = FieldSourceEnum.PREDIX_TIMESERIES.name();
        String timeseriesExpectedDataType = DatapointsResponse.class.getSimpleName();
        // String timeseriesTagname = "Compressor-2015:DischargePressure";
        String assetResultId = "sourceTagId";
        String timeseriesTagname = "{{" + assetResultId + "}}";
        String assetUri = "/asset/compressor-2015";
        String assetFilter = null;
        String assetAttribute = "/asset/assetTag/crank-frame-dischargepressure/sourceTagId";
        String assetSource = FieldSourceEnum.PREDIX_ASSET.name();
        String assetExpectedDataType = OsacbmDataType.DA_STRING.value();

        GetFieldDataRequest request = TestData.getFieldDataRequestwithAssetCriteriaAndTs(timeseriesField,
                timeseriesFieldSource, timeseriesExpectedDataType, timeseriesTagname, "1d-ago", null, assetUri,
                assetFilter, assetAttribute, assetSource, assetExpectedDataType, assetResultId);
        log.debug("request=" + this.mapper.toJson(request));

        Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Content-Type", "application/json"));
        this.restClient.addSecureTokenForHeaders(headers);

        GetFieldDataResult response = this.getHandler.getData(request, modelLookupMap, headers);

        log.debug("Response =" + this.mapper.toJson(response));
        log.info("Response = " + response.getFieldData().get(0).getData());

        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getFieldData().get(0).getData());
        Assert.assertTrue(response.getFieldData().get(0).getData() instanceof DatapointsResponse);
        DatapointsResponse dpResponse = (DatapointsResponse) response.getFieldData().get(0).getData();
        Assert.assertTrue(dpResponse.getTags().size() > 0);
        Assert.assertTrue(dpResponse.getTags().get(0).getStats().getRawCount() > 0);
    }

}
