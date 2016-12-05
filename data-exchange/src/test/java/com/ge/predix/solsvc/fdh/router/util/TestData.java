package com.ge.predix.solsvc.fdh.router.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mimosa.osacbmv3_3.DMBool;
import org.mimosa.osacbmv3_3.DMReal;

import com.ge.predix.entity.assetfilter.AssetFilter;
import com.ge.predix.entity.field.Field;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddata.OsaData;
import com.ge.predix.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.predix.entity.fieldidentifiervalue.FieldIdentifierValue;
import com.ge.predix.entity.fieldselection.FieldSelection;
import com.ge.predix.entity.filter.FieldFilter;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag;
import com.ge.predix.entity.timeseriesfilter.AssetAwareTimeseriesFilter;
import com.ge.predix.entity.timeseriesfilter.TimeseriesFilter;

/**
 * 
 * @author predix
 */
public class TestData
{

    /**
     * @param field
     *            -
     * @param fieldSource
     *            -
     * @param expectedDataType
     *            -
     * @param uriField
     *            -
     * @param uriFieldValue
     *            -
     * @param startTime
     *            -
     * @param endTime
     *            -
     * @return -
     */
    @SuppressWarnings("nls")
    public static GetFieldDataRequest getFieldDataRequest(String field, String fieldSource, String expectedDataType,
            Object uriField, Object uriFieldValue, Object startTime, Object endTime)
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

        // add FieldIdValue pair for assetId
        FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier assetIdFieldIdentifier = new FieldIdentifier();
        assetIdFieldIdentifier.setId(uriField);
        // assetIdFieldIdentifier.setSource(FieldSourceEnum.PREDIX_ASSET.name());
        fieldIdentifierValue.setFieldIdentifier(assetIdFieldIdentifier);
        fieldIdentifierValue.setValue(uriFieldValue);

        DatapointsQuery dpQuery = new DatapointsQuery();
        Tag tag1 = new Tag();
        List<Tag> tagList = new ArrayList<Tag>();

        dpQuery.setStart(startTime);
        dpQuery.setEnd(endTime);

        tag1.setName("MYTAG");
        tagList.add(tag1);
        dpQuery.setTags(tagList);
        tsFilter.setDatapointsQuery(dpQuery);

        if ( startTime != null && endTime != null )
        {
            // add FieldIdValue pair for time
            FieldIdentifierValue startTimefieldIdentifierValue = new FieldIdentifierValue();
            FieldIdentifier startTimeFieldIdentifier = new FieldIdentifier();
            startTimeFieldIdentifier.setId("startTime");
            startTimefieldIdentifierValue.setFieldIdentifier(startTimeFieldIdentifier);
            // fieldIdentifierValue.setValue("1438906239475");
            startTimefieldIdentifierValue.setValue(startTime);
            // fieldFilter.getFieldIdentifierValue().add(
            // startTimefieldIdentifierValue);

            FieldIdentifierValue endTimefieldIdentifierValue = new FieldIdentifierValue();
            FieldIdentifier endTimeFieldIdentifier = new FieldIdentifier();
            endTimeFieldIdentifier.setId("endTime");
            endTimefieldIdentifierValue.setFieldIdentifier(endTimeFieldIdentifier);
            // fieldIdentifierValue.setValue("1438906239475");
            endTimefieldIdentifierValue.setValue(endTime);
            // fieldFilter.getFieldIdentifierValue().add(
            // endTimefieldIdentifierValue);
        }

        getFieldDataRequest.getFieldDataCriteria().add(fieldDataCriteria);
        return getFieldDataRequest;
    }

    /**
     * @param field -
     * @param fieldSource -
     * @param expectedDataType -
     * @param uriField -
     * @param uriFieldValue -
     * @param startTime -
     * @param endTime -
     * @return -
     */
    @SuppressWarnings("nls")
    public static GetFieldDataRequest getFieldDataRequestwithAssetAndTs(String field, String fieldSource,
            String expectedDataType, Object uriField, Object uriFieldValue, Object startTime, Object endTime)
    {
        GetFieldDataRequest getFieldDataRequest = new GetFieldDataRequest();
        FieldDataCriteria fieldDataCriteria = new FieldDataCriteria();

        AssetAwareTimeseriesFilter assetTSFilter = new AssetAwareTimeseriesFilter();
        TimeseriesFilter tsFilter = new TimeseriesFilter();
        AssetFilter assetFilter = new AssetFilter();

        FieldSelection fieldSelection = new FieldSelection();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId(field);
        fieldIdentifier.setSource(fieldSource);
        fieldSelection.setFieldIdentifier(fieldIdentifier);
        fieldSelection.setExpectedDataType(expectedDataType);
        fieldDataCriteria.getFieldSelection().add(fieldSelection);

        assetTSFilter.setAssetFilter(assetFilter);
        assetTSFilter.setTimeseriesFilter(tsFilter);
        fieldDataCriteria.setFilter(assetTSFilter);

        // SET Asset Filter
        assetFilter.setFilterString((String) uriFieldValue);

        // SET Timeseries Filter
        DatapointsQuery query = new DatapointsQuery();
        query.setStart(startTime);
        query.setEnd(endTime);
        com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag();
        tag.setName("Compressor-2015:DischargePressure");
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
     * @return -
     */
    public static GetFieldDataRequest getFieldDataRequestwithAsset(String field, String fieldSource,
            String expectedDataType, Object uriField, Object uriFieldValue)
    {

        GetFieldDataRequest getFieldDataRequest = new GetFieldDataRequest();
        FieldDataCriteria fieldDataCriteria = new FieldDataCriteria();

        AssetFilter assetFilter = new AssetFilter();

        FieldSelection fieldSelection = new FieldSelection();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId(field);
        fieldIdentifier.setSource(fieldSource);
        fieldSelection.setFieldIdentifier(fieldIdentifier);
        fieldSelection.setExpectedDataType(expectedDataType);
        fieldDataCriteria.getFieldSelection().add(fieldSelection);

        fieldDataCriteria.setFilter(assetFilter);

        // SET Asset Filter
        // assetFilter.setFilterString((String) uriFieldValue);
        assetFilter.setUri((String) uriFieldValue);

        getFieldDataRequest.getFieldDataCriteria().add(fieldDataCriteria);
        return getFieldDataRequest;
    }

    /**
     * @param field -
     * @param fieldSource -
     * @param expectedDataType -
     * @param uriField -
     * @param uriFieldValue -
     * @param startTime -
     * @param endTime -
     * @return -
     */
    @SuppressWarnings("nls")
    public static GetFieldDataRequest getFieldDataRequestwithTs(String field, String fieldSource,
            String expectedDataType, Object uriField, Object uriFieldValue, Object startTime, Object endTime)
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
        tag.setName("Compressor-2015:DischargePressure");
        List<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag> tags = new ArrayList<com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag>();
        tags.add(tag);
        query.setTags(tags);

        tsFilter.setDatapointsQuery(query);

        getFieldDataRequest.getFieldDataCriteria().add(fieldDataCriteria);
        return getFieldDataRequest;
    }

    /**
     * @return -
     */
    @SuppressWarnings("nls")
    public static PutFieldDataRequest putFieldDataRequestSetAlertStatus()
    {
        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();

        // Asset to Query
        AssetFilter filter = new AssetFilter();
        filter.setUri("/asset/compressor-2015");

        // Data to change
        FieldData fieldData = new FieldData();
        com.ge.predix.entity.field.Field field = new com.ge.predix.entity.field.Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId(
                "/asset/assetTag/crank-frame-dischargepressure/tagDatasource/tagExtensions/attributes/alertStatus/value");
        fieldIdentifier.setSource("PREDIX_ASSET");
        field.setFieldIdentifier(fieldIdentifier);
        OsaData crankFrameVelocityData = new OsaData();
        DMBool crankFrameVelocity = new DMBool();
        crankFrameVelocity.setValue(true);
        crankFrameVelocityData.setDataEvent(crankFrameVelocity);
        fieldData.getField().add(field);
        fieldData.setData(crankFrameVelocityData);

        PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();
        fieldDataCriteria.setFieldData(fieldData);
        fieldDataCriteria.setFilter(filter);
        putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria);

        return putFieldDataRequest;
    }

    /**
     * @return -
     */
    @SuppressWarnings("nls")
    public static PutFieldDataRequest customPutFieldDataRequest()
    {
        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();

        // Asset to Query
        FieldFilter filter = new FieldFilter();
        FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier assetIdFieldIdentifier = new FieldIdentifier();
        assetIdFieldIdentifier.setId("/asset/assetId");
        fieldIdentifierValue.setFieldIdentifier(assetIdFieldIdentifier);
        fieldIdentifierValue.setValue("/asset/compressor-2015");
        filter.getFieldIdentifierValue().add(fieldIdentifierValue);

        // Data to change
        FieldData fieldData = new FieldData();
        com.ge.predix.entity.field.Field field = new com.ge.predix.entity.field.Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId("/asset/assetTag/crank-frame-velocity/outputMaximum");
        fieldIdentifier.setSource("PREDIX_ASSET");
        field.setFieldIdentifier(fieldIdentifier);
        OsaData crankFrameVelocityData = new OsaData();
        DMReal crankFrameVelocity = new DMReal();
        crankFrameVelocity.setValue(19.88);
        crankFrameVelocityData.setDataEvent(crankFrameVelocity);
        fieldData.getField().add(field);
        fieldData.setData(crankFrameVelocityData);

        PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();
        fieldDataCriteria.setFieldData(fieldData);
        fieldDataCriteria.setFilter(filter);
        putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria);

        return putFieldDataRequest;
    }

    /**
     * @return -
     */
    @SuppressWarnings("nls")
    public static PutFieldDataRequest assetPutFieldDataRequest()
    {
        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();

        // Asset to Query
        AssetFilter assetfilter = new AssetFilter();
        assetfilter.setUri("/asset/compressor-2015");

        // Data to change
        FieldData fieldData = new FieldData();
        com.ge.predix.entity.field.Field field = new com.ge.predix.entity.field.Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId("/asset/assetTag/crank-frame-velocity/outputMaximum");
        fieldIdentifier.setSource("PREDIX_ASSET");
        field.setFieldIdentifier(fieldIdentifier);
        OsaData crankFrameVelocityData = new OsaData();
        DMReal crankFrameVelocity = new DMReal();
        crankFrameVelocity.setValue(19.88);
        crankFrameVelocityData.setDataEvent(crankFrameVelocity);
        fieldData.getField().add(field);
        fieldData.setData(crankFrameVelocityData);

        PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();
        fieldDataCriteria.setFieldData(fieldData);
        fieldDataCriteria.setFilter(assetfilter);
        putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria);

        return putFieldDataRequest;
    }

    /**
     * @param assetId -
     * @param nodeName -
     * @param lowerThreshold - 
     * @param upperThreshold -
     * @return -
     */
    public static PutFieldDataRequest putFieldDataRequest(String assetId, String nodeName, double lowerThreshold, double upperThreshold)
    {
        DatapointsIngestion datapointsIngestion = createTimeseriesDataBody(assetId, nodeName, lowerThreshold, upperThreshold);
        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
        PutFieldDataCriteria criteria = new PutFieldDataCriteria();
        FieldData fieldData = new FieldData();
        Field field = new Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();

        fieldIdentifier.setSource(FieldSourceEnum.PREDIX_TIMESERIES.name());
        field.setFieldIdentifier(fieldIdentifier);
        List<Field> fields = new ArrayList<Field>();
        fields.add(field);
        fieldData.setField(fields);

        fieldData.setData(datapointsIngestion);
        criteria.setFieldData(fieldData);
        List<PutFieldDataCriteria> list = new ArrayList<PutFieldDataCriteria>();
        list.add(criteria);
        putFieldDataRequest.setPutFieldDataCriteria(list);

        return putFieldDataRequest;
    }

    @SuppressWarnings("nls")
    private static DatapointsIngestion createTimeseriesDataBody(String assetId, String nodeName, double lowerThreshold, double upperThreshold)
    {
        DatapointsIngestion dpIngestion = new DatapointsIngestion();
        dpIngestion.setMessageId(UUID.randomUUID().toString());
        List<Body> bodies = new ArrayList<Body>();
        // log.info("NodeList : " + this.mapper.toJson(aNodeList));

        Body body = new Body();
        List<Object> datapoints = new ArrayList<Object>();
        body.setName(assetId + ":" + nodeName);

        List<Object> datapoint = new ArrayList<Object>();
        datapoint.add(getCurrentTimestamp());
        datapoint.add(generateRandomUsageValue(lowerThreshold, upperThreshold));
        datapoints.add(datapoint);

        body.setDatapoints(datapoints);
        bodies.add(body);

        dpIngestion.setBody(bodies);

        return dpIngestion;
    }

    private static Timestamp getCurrentTimestamp()
    {
        java.util.Date date = new java.util.Date();
        Timestamp ts = new Timestamp(date.getTime());
        return ts;
    }
    
    private static double generateRandomUsageValue(double low, double high)
    {
        return low + Math.random() * (high - low);
    }
}
