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
import java.util.List;

import com.ge.predix.entity.assetfilter.AssetFilter;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddatacriteria.AssetFieldDataCriteria;
import com.ge.predix.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.predix.entity.fieldselection.FieldSelection;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.DatapointsLatestQuery;
import com.ge.predix.entity.timeseriesfilter.AssetCriteriaAwareTimeseriesFilter;
import com.ge.predix.entity.timeseriesfilter.TimeseriesFilter;

/**
 * 
 * @author predix -
 */
public class TestData
{

    /**
     * @return -
     */
    @SuppressWarnings("nls")
    public static PutFieldDataRequest getPutFieldDataRequest()
    {
        PutFieldDataRequest putRequest = new PutFieldDataRequest();
        putRequest.setCorrelationId("correlationId");
        List<PutFieldDataCriteria> putCriterias = new ArrayList<PutFieldDataCriteria>();
        PutFieldDataCriteria putCriteria = new PutFieldDataCriteria();
        FieldData fieldData = new FieldData();
        com.ge.predix.entity.field.Field field = new com.ge.predix.entity.field.Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        // fieldIdentifier.setId("");
        fieldIdentifier.setSource("PREDIX_TIMESERIES");
        field.setFieldIdentifier(fieldIdentifier);
        DatapointsIngestion dpIngestion = createMetrics();
        fieldData.setData(dpIngestion);

        putCriteria.setFieldData(fieldData);
        putCriterias.add(putCriteria);
        putRequest.setPutFieldDataCriteria(putCriterias);
        return putRequest;
    }

    @SuppressWarnings(
    {
            "nls", "unchecked"
    })
    private static DatapointsIngestion createMetrics()
    {
        DatapointsIngestion dpIngestion = new DatapointsIngestion();
        dpIngestion.setMessageId(String.valueOf(System.currentTimeMillis()));

        Body body = new Body();
        body.setName("RMD_metric2");
        List<Object> datapoint1 = new ArrayList<Object>();
        datapoint1.add(System.currentTimeMillis());
        datapoint1.add(10);
        datapoint1.add(3); // quality

        List<Object> datapoint2 = new ArrayList<Object>();
        datapoint2.add(System.currentTimeMillis());
        datapoint2.add(10);
        datapoint2.add(1); // quality

        List<Object> datapoints = new ArrayList<Object>();
        datapoints.add(datapoint1);
        datapoints.add(datapoint2);

        body.setDatapoints(datapoints);

        com.ge.predix.entity.util.map.Map map = new com.ge.predix.entity.util.map.Map();
        map.put("host", "server1");
        map.put("customer", "Acme");

        body.setAttributes(map);

        List<Body> bodies = new ArrayList<Body>();
        bodies.add(body);

        dpIngestion.setBody(bodies);
        return dpIngestion;
    }

    /**
     * @param field -
     * @param fieldSource -
     * @param expectedDataType -
     * @param uriField -
     * @param uriFieldValue -
     * @param tagname -
     * @param startTime -
     * @param endTime -
     * @return -
     */
    public static GetFieldDataRequest getFieldDataRequestwithTs(String field, String fieldSource,
            String expectedDataType, String tagname, Object startTime, Object endTime)
    {
        GetFieldDataRequest getFieldDataRequest = new GetFieldDataRequest();
        FieldDataCriteria fieldDataCriteria = new FieldDataCriteria();

        TimeseriesFilter tsFilter = new TimeseriesFilter();

        FieldSelection fieldSelection = new FieldSelection();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId(field);
        fieldIdentifier.setSource(fieldSource);
        fieldSelection.setFieldIdentifier(fieldIdentifier);
        fieldSelection.setExpectedDataType(expectedDataType);
        fieldDataCriteria.getFieldSelection().add(fieldSelection);

        fieldDataCriteria.setFilter(tsFilter);

        // SET Timeseries Filter
        DatapointsQuery query = new DatapointsQuery();
        query.setStart(startTime);
        query.setEnd(endTime);
        com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag();
        tag.setName(tagname);
        tag.setLimit(3);
        List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag> tags = new ArrayList<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag>();
        tags.add(tag);
        query.setTags(tags);

        tsFilter.setDatapointsQuery(query);

        getFieldDataRequest.getFieldDataCriteria().add(fieldDataCriteria);
        return getFieldDataRequest;
    }

    /**
     * @param field -
     * @param fieldSource -
     * @param expectedDataType -
     * @param uriField -
     * @param uriFieldValue -
     * @param tagName -
     * @param startTime -
     * @param endTime -
     * @return -
     */
    public static GetFieldDataRequest getFieldDataRequestLatestDatapoint(String field, String fieldSource,
            String expectedDataType, String tagName)
    {
        GetFieldDataRequest getFieldDataRequest = new GetFieldDataRequest();
        FieldDataCriteria fieldDataCriteria = new FieldDataCriteria();

        TimeseriesFilter tsFilter = new TimeseriesFilter();

        FieldSelection fieldSelection = new FieldSelection();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId(field);
        fieldIdentifier.setSource(fieldSource);
        fieldSelection.setFieldIdentifier(fieldIdentifier);
        fieldSelection.setExpectedDataType(expectedDataType);
        fieldDataCriteria.getFieldSelection().add(fieldSelection);

        fieldDataCriteria.setFilter(tsFilter);

        // SET Timeseries Filter with latest data point query object
        DatapointsLatestQuery query = new DatapointsLatestQuery();
        com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag();
        tag.setName(tagName);
        List<com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag> tags = new ArrayList<com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag>();
        tags.add(tag);
        query.setTags(tags);

        tsFilter.setDatapointsLatestQuery(query);

        getFieldDataRequest.getFieldDataCriteria().add(fieldDataCriteria);
        return getFieldDataRequest;
    }

    
    /**
     * @param timeseriesField -
     * @param timeseriesFieldSource -
     * @param timeseriesExpectedDataType -
     * @param tagName -
     * @param startTime -
     * @param endTime -
     * @param assetUri -
     * @param assetFilterQuery -
     * @param assetAttribute -
     * @param assetSource -
     * @param assetExpectedDataType -
     * @param assetResultId -
     * @return -
     */
    public static GetFieldDataRequest getFieldDataRequestwithAssetCriteriaAndTs(String timeseriesField,
            String timeseriesFieldSource,  String timeseriesExpectedDataType, String tagName, String startTime, String endTime,
            String assetUri, String assetFilterQuery, String assetAttribute, String assetSource, String assetExpectedDataType, String assetResultId)
    {
        GetFieldDataRequest getFieldDataRequest = new GetFieldDataRequest();
        FieldDataCriteria timeseriesFieldDataCriteria = new FieldDataCriteria();
    
        AssetCriteriaAwareTimeseriesFilter assetCriteriaTSFilter = new AssetCriteriaAwareTimeseriesFilter();
      
        //add a timeseries filter
        TimeseriesFilter tsFilter = new TimeseriesFilter();
        assetCriteriaTSFilter.setTimeseriesFilter(tsFilter);
        //define the timeseries selection
        FieldSelection timeseriesFieldSelection = new FieldSelection();
        FieldIdentifier timeseriesFieldIdentifier = new FieldIdentifier();
        timeseriesFieldIdentifier.setId(timeseriesField);
        timeseriesFieldIdentifier.setSource(timeseriesFieldSource);
        timeseriesFieldSelection.setFieldIdentifier(timeseriesFieldIdentifier);
        timeseriesFieldSelection.setExpectedDataType(timeseriesExpectedDataType);
        timeseriesFieldDataCriteria.getFieldSelection().add(timeseriesFieldSelection);
        timeseriesFieldDataCriteria.setFilter(assetCriteriaTSFilter);
        // SET Timeseries Filter
        DatapointsQuery query = new DatapointsQuery();
        query.setStart(startTime);
        query.setEnd(endTime);
        com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag();
        tag.setName(tagName);
        List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag> tags = new ArrayList<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag>();
        tags.add(tag);
        tag.setLimit(10);
        query.setTags(tags);
        tsFilter.setDatapointsQuery(query);
        
        //add a assetCriteria
        AssetFieldDataCriteria assetFieldDataCriteria = new AssetFieldDataCriteria();
        assetFieldDataCriteria.setResultId(assetResultId);
        assetCriteriaTSFilter.setAssetFieldDataCriteria(assetFieldDataCriteria);
        // SET Asset Criteria 
        FieldSelection assetFieldSelection = new FieldSelection();
        FieldIdentifier assetFieldIdentifier = new FieldIdentifier();
        assetFieldIdentifier.setId(assetAttribute);
        assetFieldIdentifier.setSource(assetSource);
        assetFieldSelection.setFieldIdentifier(assetFieldIdentifier);
        assetFieldSelection.setExpectedDataType(assetExpectedDataType);
        //define Asset Selection Subresource
        assetFieldDataCriteria.getFieldSelection().add(assetFieldSelection); //select this attribute
        AssetFilter assetFilter = new AssetFilter();
        assetFilter.setUri(assetUri); //from this asset (or these assets)
        assetFilter.setFilterString(assetFilterQuery); //optionally, using this filter
        assetFieldDataCriteria.setFilter(assetFilter);
    
    
        getFieldDataRequest.getFieldDataCriteria().add(timeseriesFieldDataCriteria);
        return getFieldDataRequest;
    }
}
