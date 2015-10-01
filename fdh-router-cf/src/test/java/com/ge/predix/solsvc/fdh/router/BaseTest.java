/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.fdh.router;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mimosa.osacbmv3_3.DAString;
import org.mimosa.osacbmv3_3.DMDataSeq;
import org.mimosa.osacbmv3_3.DMReal;
import org.mimosa.osacbmv3_3.DataEvent;
import org.mimosa.osacbmv3_3.OsacbmDataType;
import org.mimosa.osacbmv3_3.OsacbmTime;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.dsp.pm.ext.entity.asset.Asset;
import com.ge.dsp.pm.ext.entity.asset.assetidentifier.AssetIdentifier;
import com.ge.dsp.pm.ext.entity.assetselector.AssetSelector;
import com.ge.dsp.pm.ext.entity.engunit.EngUnit;
import com.ge.dsp.pm.ext.entity.field.Field;
import com.ge.dsp.pm.ext.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.dsp.pm.ext.entity.fielddata.FieldData;
import com.ge.dsp.pm.ext.entity.fielddata.OsaData;
import com.ge.dsp.pm.ext.entity.fieldidentifiervalue.FieldIdentifierValue;
import com.ge.dsp.pm.ext.entity.fieldselection.FieldSelection;
import com.ge.dsp.pm.ext.entity.model.Model;
import com.ge.dsp.pm.ext.entity.osa.selectionfilter.AnchorTimeType;
import com.ge.dsp.pm.ext.entity.osa.selectionfilter.SelectionFilterDefinition;
import com.ge.dsp.pm.ext.entity.osa.selectionfilter.SelectionFilterType;
import com.ge.dsp.pm.ext.entity.osa.selectionfilter.TimeOffsetUnits;
import com.ge.dsp.pm.ext.entity.osa.selectionfilter.TimeOrRowSelectionFilter;
import com.ge.dsp.pm.ext.entity.osa.selectionfilter.TimeSelectionFilter;
import com.ge.dsp.pm.ext.entity.selectionfilter.FieldSelectionFilter;
import com.ge.dsp.pm.ext.entity.selectionfilter.OsaSelectionFilter;
import com.ge.dsp.pm.ext.entity.solution.identifier.solutionidentifier.SolutionIdentifier;
import com.ge.dsp.pm.ext.entity.util.map.Entry;
import com.ge.dsp.pm.ext.entity.util.map.Map;
import com.ge.dsp.pm.fielddatahandler.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataResult;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataCriteria;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataResult;
import com.ge.fdh.asset.helper.Utils;
import com.ge.predix.solsvc.fdh.router.service.GetFieldDataService;
import com.ge.predix.solsvc.fdh.router.service.PutFieldDataService;
import com.ge.predix.solsvc.fdh.router.util.XmlSupport;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * 
 * @author predix -
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{
        "classpath*:META-INF/spring/TEST-mock-fdh-router-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml",
        "classpath*:META-INF/spring/ext-util-scan-context.xml",
        "classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml",
        "classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml",
        "classpath*:META-INF/spring/fdh-router-scan-context.xml",
        "classpath*:META-INF/spring/fdh-router-cxf-context.xml",
        "classpath*:META-INF/spring/fdh-asset-handler-scan-context.xml",
        "classpath*:META-INF/spring/fdh-timeseries-handler-scan-context.xml"
        })
@ActiveProfiles("local")
public abstract class BaseTest
{
    
    private static final String   TEST_SERVER_IP        = "localhost"; //$NON-NLS-1$
    private String                serverIP              = TEST_SERVER_IP;
    private static final String HTTP_PAYLOAD_XML      = "application/xml"; //$NON-NLS-1$


    @Autowired
    private RestClient            restClient;
    
    @Autowired
    private GetFieldDataService          getFieldData;
    @Autowired
    private PutFieldDataService putFieldData;

    /**
     * @param solutionId -
     * @param calculatedFieldId - the attribute/column that holds the data
     * @param calculatedFieldName - a human readable name
     * @param calculatedFieldSource - the source system that has the data
     * @param assetId -
     * @param gmtTimeString -
     * @param log -
     * @param expectedResult -
     * @param expectedOSADataType -
     * @param contentType -
     * @param port -
     * @param startDef -
     * @param starTimeOffset -
     * @param anchorTimeType -
     * @param timeOffsetUnits -
     * @param endDef -
     * @throws UnsupportedEncodingException -
     * @throws IOException -
     * @throws HttpException -
     * @throws JAXBException -
     */
    @SuppressWarnings("nls")
    protected void callGetFieldData(final String solutionId, final String calculatedFieldId,
            final String calculatedFieldName, final String calculatedFieldSource, final String assetId,
            final String gmtTimeString, final Logger log, final String[] expectedResult,
            OsacbmDataType expectedOSADataType, String contentType, String port, String startDef,
            String starTimeOffset, AnchorTimeType anchorTimeType, TimeOffsetUnits timeOffsetUnits, String endDef)
            throws UnsupportedEncodingException, IOException, HttpException, JAXBException
    {
        if ( calculatedFieldId == null ) throw new IllegalArgumentException("calculatedFieldId null");

        if ( calculatedFieldName == null || calculatedFieldName.isEmpty() )
            throw new IllegalArgumentException("calculatedFieldName null or empty");

        if ( assetId == null || assetId.isEmpty() ) throw new IllegalArgumentException("assetId null or empty");

        if ( gmtTimeString == null || gmtTimeString.isEmpty() )
            throw new IllegalArgumentException("gmtTimeString null or empty");

        if ( log == null ) throw new IllegalArgumentException("log null");

        if ( expectedResult == null ) throw new IllegalArgumentException("expectedResult null");

        GetFieldDataRequest request = getFieldDataRequest(solutionId, calculatedFieldId, calculatedFieldName,
                calculatedFieldSource, assetId, gmtTimeString, expectedOSADataType, null, null, contentType, startDef,
                starTimeOffset, anchorTimeType, timeOffsetUnits, endDef);

        //make the call
        List<Header> headers = this.restClient.getOauthHttpHeaders();
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
        String body = "[{\"@type\":\"Model\",\"description\":\"desc\",\"additionalAttributes\":{\"keys\":[\"key\"],\"values\":[\"value\"]}}]";
        InputStream stream = new ByteArrayInputStream(body.getBytes());
        Mockito.when(entity.getContent()).thenReturn(stream );
        Mockito.when(entity.getContentLength()).thenReturn(new Long(body.length()));
       
        
        java.util.Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
        GetFieldDataResult result = this.getFieldData.getFieldData(request, modelLookupMap , headers);

        //check the results
        final List<FieldData> fieldData = result.getFieldData();
        Assert.assertNotNull("fieldData null", fieldData);
        Assert.assertFalse("fieldData empty", fieldData.isEmpty());

        final FieldData field0Data = fieldData.get(0);
        Assert.assertNotNull("fieldData[0] null", fieldData);

        final DataEvent field0dataEvent = ((OsaData) field0Data.getData()).getDataEvent();
        Assert.assertNotNull("fieldData[0].dataEvent null", fieldData);
        Assert.assertTrue("field0dataEvent not instanceOf " + expectedOSADataType + ", but was "
                + field0dataEvent.getClass().getName(),
                expectedOSADataType.equals(OsacbmDataType.fromValue(field0dataEvent.getClass().getSimpleName())));

        if ( expectedOSADataType.equals(OsacbmDataType.DM_DATA_SEQ) )
        {
            final List<Double> fieldValues = ((DMDataSeq) field0dataEvent).getValues();
            Assert.assertNotNull("fieldData[0].dataEvent.values null", fieldValues);
            Assert.assertNotEquals(0, fieldValues.size());
            //
            // Checking only 24 data points as field 1090 is used for both
            // TFF and CTMB. Hence 1 st output data point is not considered
            // for assertion.
            //
            for (int i = 0; i < fieldValues.size(); i++)
            {
                log.debug("FieldValues " + fieldValues.get(i));
                if ( i >= 1 && (i % 2 != 0) )
                {
                    log.debug("expectedResult " + expectedResult[i / 2]);
                    if ( NumberUtils.isNumber(expectedResult[i / 2]) )
                        Assert.assertEquals("Comparing expected and Actual", new Double(expectedResult[i / 2]),
                                fieldValues.get(i));
                    else
                        Assert.assertEquals("Comparing expected and Actual", expectedResult[i / 2], fieldValues.get(i));
                }
            }
        }
        else if ( expectedOSADataType.equals(OsacbmDataType.DA_STRING) )
        {
            Assert.assertNotNull("fieldData.value null", ((DAString) field0dataEvent).getValue());
        }

    }
    
    /**
     * @param solutionId -
     * @param fieldId -
     * @param fieldName -
     * @param fieldSource -
     * @param assetId -
     * @param gmtTimeString -
     * @param expectedDataType -
     * @param mapKey -
     * @param mapValue -
     * @param contentType -
     * @param startDef -
     * @param starTimeOffset -
     * @param anchorTimeType -
     * @param timeOffsetUnits -
     * @param endDef -
     * @return -
     */
    @SuppressWarnings("nls")
    public static GetFieldDataRequest getFieldDataRequest(final String solutionId, final String fieldId,
            final String fieldName, final String fieldSource, final String assetId, final String gmtTimeString,
            final OsacbmDataType expectedDataType, final String mapKey, final String mapValue, String contentType,
            String startDef, String starTimeOffset, AnchorTimeType anchorTimeType, TimeOffsetUnits timeOffsetUnits,
            String endDef)
    {
        if ( fieldName == null || fieldName.isEmpty() ) throw new IllegalArgumentException("fieldName null or empty");

        if ( assetId == null || assetId.isEmpty() ) throw new IllegalArgumentException("assetId null or empty");

        if ( gmtTimeString == null || gmtTimeString.isEmpty() )
            throw new IllegalArgumentException("gmtTimeString null or empty");

        if ( expectedDataType == null ) throw new IllegalArgumentException("expectedDataType null");

        if ( mapKey != null )
        {
            if ( mapKey.isEmpty() ) throw new IllegalArgumentException("mapKey empty");

            if ( mapValue == null || mapValue.isEmpty() ) throw new IllegalArgumentException("mapValue null or empty");
        }

        GetFieldDataRequest request = null;
        String step = "?";
        try
        {
            step = "setup";
            request = setupRequest(solutionId, fieldId, fieldName, fieldSource, assetId, gmtTimeString,
                    expectedDataType, mapKey, mapValue, startDef, starTimeOffset, anchorTimeType, timeOffsetUnits,
                    endDef);

        }
        catch (Throwable th)
        {
            throw new IllegalStateException("unexpected error parsing request at step " + step + ", "
                    + String.valueOf(request), th);
        }

        return request;
    }
    
    /**
     * Setup the JAXB request
     * 
     * @param fieldId -
     * @param fieldName -
     * @param assetId -
     * @param gmtTimeString -
     * @param expectedDataType -
     * @param mapKey -
     * @param mapValue -
     * @param startDef
     * @param starTimeOffset
     * @param anchorTimeType
     * @param timeOffsetUnits
     * @param endDef
     * @return the populated request
     */
    @SuppressWarnings("nls")
    private static GetFieldDataRequest setupRequest(final String solutionId, final String fieldId,
            final String fieldName, final String fieldSource, final String assetId, final String gmtTimeString,
            final OsacbmDataType expectedDataType, final String mapKey, final String mapValue, String startDef,
            String starTimeOffset, AnchorTimeType anchorTimeType, TimeOffsetUnits timeOffsetUnits, String endDef)
    {
        final GetFieldDataRequest request = new GetFieldDataRequest();
        SolutionIdentifier solutionIdentifier = new SolutionIdentifier();
        solutionIdentifier.setId(solutionId);
        request.setSolutionIdentifier(solutionIdentifier);
        Map externalAttributeMap = new Map();
        Entry entry = new Entry();
        // entry.setKey(IAssetDataInterceptor.SOLUTION_ID_KEY);
        // entry.setValue(solutionId);
        externalAttributeMap.getEntry().add(entry);
        request.setExternalAttributeMap(externalAttributeMap);

        if ( mapKey != null )
        {
            entry = new Entry();
            entry.setKey(mapKey);
            entry.setValue(mapValue);
            externalAttributeMap.getEntry().add(entry);
        }

        final FieldDataCriteria criteria = new FieldDataCriteria();
        // criteria.setDataType(expectedDataType);

        final FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId(fieldId);
        fieldIdentifier.setName(fieldName);
        fieldIdentifier.setSource(fieldSource);

        final FieldSelection fieldSelection = new FieldSelection();
        fieldSelection.setResultId("0");
        fieldSelection.setFieldIdentifier(fieldIdentifier);
        criteria.getFieldSelection().add(fieldSelection);

        FieldSelectionFilter assetIdSelectionFilter = new FieldSelectionFilter();
        FieldIdentifierValue assetIdFieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier assetIdFieldIdentifier = new FieldIdentifier();
        assetIdFieldIdentifierValue.setFieldIdentifier(assetIdFieldIdentifier);
        assetIdFieldIdentifier.setId("/asset/assetId");
        assetIdFieldIdentifier.setName("/asset/assetId");
        assetIdFieldIdentifier.setSource(fieldSource);
        assetIdFieldIdentifierValue.setValue(assetId);
        assetIdSelectionFilter.getFieldIdentifierValue().add(assetIdFieldIdentifierValue);
        criteria.setSelectionFilter(assetIdSelectionFilter);

        if ( startDef != null )
        {
            // String startDef = "Sat Sep 26 16:38:33 PDT 2013";
            // String starTimeOffset = "1";
            // AnchorTimeType anchorTimeType = AnchorTimeType.DISPATCHER;
            // TimeOffsetUnits timeOffsetUnits = TimeOffsetUnits.DAYS;
            // String endDef = "Sat Sep 27 16:38:33 PDT 2013";
            SelectionFilterDefinition timeSelectionFilter = createSelectionDefinitionTimeSelection(startDef,
                    starTimeOffset, anchorTimeType, timeOffsetUnits, endDef, starTimeOffset, anchorTimeType,
                    timeOffsetUnits);
            timeSelectionFilter.setAssetSelectionFilter(assetIdSelectionFilter.getFieldIdentifierValue());

            OsaSelectionFilter osaSelectionFilter = new OsaSelectionFilter();
            osaSelectionFilter.setSelectionFilter(timeSelectionFilter);
            criteria.setSelectionFilter(osaSelectionFilter);
        }

        request.getFieldDataCriteria().add(criteria);

        return request;
    }
    
    /**
     * selectionFilterDefinition.setStartDefinition
     * selectionFilterDefinition.setEndDefinition
     * 
     * @param startDef -
     * @param starTimeOffset -
     * @param startAnchorTimeType -
     * @param startTimeOffsetUnits -
     * @param endDef -
     * @param endTimeOffSet -
     * @param endAnchorTimeType -
     * @param endTimeOffsetUnits -
     * 
     * @return SelectionFilterDefinition
     */
    public static SelectionFilterDefinition createSelectionDefinitionTimeSelection(String startDef,
            String starTimeOffset, AnchorTimeType startAnchorTimeType, TimeOffsetUnits startTimeOffsetUnits,
            String endDef, String endTimeOffSet, AnchorTimeType endAnchorTimeType, TimeOffsetUnits endTimeOffsetUnits)
    {
        SelectionFilterDefinition selectionFilterDefinition = new SelectionFilterDefinition();
        selectionFilterDefinition.setStartDefinition(createTimeSelectionFilter(startDef, starTimeOffset,
                startAnchorTimeType, startTimeOffsetUnits));
        selectionFilterDefinition.setEndDefinition(createTimeSelectionFilter(endDef, endTimeOffSet, endAnchorTimeType,
                endTimeOffsetUnits));

        return selectionFilterDefinition;
    }
    
    /**
     * TimeOffset = timeOffSet TimeOffsetUnits = timeOffsetUnits AnchorTimeType
     * = anchorTimeType osaCbmTime = date
     * 
     * @param dateStr -
     * @param timeOffSet -
     * @param anchorTimeType -
     * @param timeOffsetUnits -
     * 
     * @return TimeSelectionFilter
     */
    public static TimeOrRowSelectionFilter createTimeSelectionFilter(String dateStr, String timeOffSet,
            AnchorTimeType anchorTimeType, TimeOffsetUnits timeOffsetUnits)
    {

        TimeSelectionFilter timeSelectionFilter = new TimeSelectionFilter();

        timeSelectionFilter.setAnchorTimeType(anchorTimeType);
        timeSelectionFilter.setTimeOffset(new BigInteger(timeOffSet));
        timeSelectionFilter.setTimeOffsetUnits(timeOffsetUnits);
        timeSelectionFilter.setAnchorTime(createOsacbmTime(dateStr));

        TimeOrRowSelectionFilter timeOrRowSelectionFilter = new TimeOrRowSelectionFilter();
        timeOrRowSelectionFilter.setSelectionFilterType(SelectionFilterType.TIME_SELECTION_FILTER);
        timeOrRowSelectionFilter.setTimeSelectionFilter(timeSelectionFilter);

        return timeOrRowSelectionFilter;

    }
    
    /**
     * 
     * @param dateStr - Sat Sep 26 16:38:33 PDT 2013
     * @return osaCbmTime Sat Sep 26 16:38:33 PDT 2013
     */
    public static OsacbmTime createOsacbmTime(String dateStr)
    {

        if ( dateStr == null || "".equals(dateStr) ) return null;
        return Utils.convertDateToOsacbmTime(new Date(dateStr));
    }
    /**
     * @param solutionId -
     * @param rawFieldId -
     * @param rawFieldName -
     * @param fieldSource -
     * @param rawValue -
     * @param assetId -
     * @param d1 -
     * @param log -
     * @param contentType -
     * @param port -
     * @throws IOException -
     * @throws HttpException -
     */
    @SuppressWarnings("nls")
    protected void putFieldData(Long solutionId, String rawFieldId, String rawFieldName, String fieldSource, Object rawValue,
            String assetId, Date d1, Logger log, String contentType, String port)
    {
        try
        {
            putFieldData(solutionId, rawFieldId, rawFieldName, fieldSource, rawValue, assetId, d1, log, OsacbmDataType.DM_REAL,
                    contentType, port);
        }
        catch (IOException ex)
        {
            final String info = " solutionId: " + solutionId + " rawFieldId: " + rawFieldId + " rawFieldName: "
                    + rawFieldName + " rawValue: " + rawValue + " assetId: " + assetId + " date: " + d1;
            throw new IllegalStateException(ex.getClass().getSimpleName() + " I/O error for ==>" + info + "; message: "
                    + ex.getMessage(), ex);
        }
        catch (Throwable th)
        {
            final String info = " solutionId: " + solutionId + " rawFieldId: " + rawFieldId + " rawFieldName: "
                    + rawFieldName + " rawValue: " + rawValue + " assetId: " + assetId + " date: " + d1;
            throw new IllegalStateException(th.getClass().getSimpleName() + " unexpected error for ==>" + info
                    + "; message: " + th.getMessage(), th);
        }
    }
    
    /**
     * @param solutionId -
     * @param rawFieldId -
     * @param rawFieldName -
     * @param fieldSource -
     * @param rawValue -
     * @param assetId -
     * @param firstDate -
     * @param log -
     * @param osacbmDataType -
     * @param contentType -
     * @param port -
     * @throws IOException -
     * @throws HttpException -
     */
    @SuppressWarnings("nls")
    protected void putFieldData(final Long solutionId, final String rawFieldId, final String rawFieldName, String fieldSource,
            final Object rawValue, final String assetId, final Date firstDate, final Logger log,
            final OsacbmDataType osacbmDataType, String contentType, String port)
            throws HttpException, IOException
    {
        if ( solutionId == null ) throw new IllegalArgumentException("solutionId null or empty");

        if ( rawFieldName == null || rawFieldName.isEmpty() )
            throw new IllegalArgumentException("rawFieldName null or empty");

        if ( rawValue == null ) throw new IllegalArgumentException("rawFieldName null or empty");

        if ( assetId == null || assetId.isEmpty() ) throw new IllegalArgumentException("assetId null or empty");

        if ( firstDate == null ) throw new IllegalArgumentException("d1 null or empty");

        if ( log == null ) throw new IllegalArgumentException("log null or empty");

        if ( osacbmDataType == null ) throw new IllegalArgumentException("osacbmDataType null or empty");

        double currentTime = firstDate.getTime();
        PutFieldDataRequest payload = getPutFieldDataRequest(solutionId, currentTime, rawFieldId, rawFieldName, fieldSource, assetId, rawValue,
                osacbmDataType, contentType);

        callPutFieldData(log, payload, contentType, port);
    }
    
    /**
     * @param solutionId -
     * @param currentTime -
     * @param fieldId -
     * @param fieldName -
     * @param fieldSource -
     * @param assetId -
     * @param value -
     * @param osacbmDataType -
     * @param contentType -
     * @return -
     */
    @SuppressWarnings("nls")
    protected PutFieldDataRequest getPutFieldDataRequest(Long solutionId, double currentTime, String fieldId, String fieldName, String fieldSource,
            String assetId, Object value, OsacbmDataType osacbmDataType, String contentType)
    {
            PutFieldDataRequest request = new PutFieldDataRequest();
            request.setSolutionIdentifier(new SolutionIdentifier());
            request.getSolutionIdentifier().setId(solutionId.toString());
            Map externalAttributeMap = new Map();
            Entry entry = new Entry();
            // entry.setKey(IAssetDataInterceptor.SOLUTION_ID_KEY);
            // entry.setValue(solutionId);
            externalAttributeMap.getEntry().add(entry);
            // request.setSolutionId() //TODO when it's ready
            request.setExternalAttributeMap(externalAttributeMap);
            // DataRef dataRef = new DataRef();
            // request.setDataRef(dataRef);

            FieldData fieldData = new FieldData();
            DataEvent dataEvent = null;
            if ( osacbmDataType == null || OsacbmDataType.DM_DATA_SEQ.equals(osacbmDataType) )
            {
                dataEvent = new DMDataSeq();
                DMDataSeq dmDataSeq = (DMDataSeq) dataEvent;
                dmDataSeq.setSequenceNum(1l);
                dmDataSeq.getValues().add(Double.parseDouble(value.toString()));
                // dMDataSeq.getValues().add(66d);
                dmDataSeq.setXAxisStart(Double.valueOf(currentTime - 1000));// 1000
                // ms
                // before
                // current
                // time
                dmDataSeq.getXAxisDeltas().add(Double.valueOf(currentTime - 1000));
            }
            else if ( OsacbmDataType.DM_REAL.equals(osacbmDataType) )
            {
                dataEvent = new DMReal();
                if ( value != null ) ((DMReal) dataEvent).setValue(Double.parseDouble(value.toString()));
            }
            else if ( OsacbmDataType.DA_STRING.equals(osacbmDataType) )
            {
                dataEvent = new DAString();
                if ( value != null ) ((DAString) dataEvent).setValue(value.toString());
            }
            else
                throw new UnsupportedOperationException("Unable to support creation of " + osacbmDataType);
            // dMDataSeq.getXAxisDeltas().add(Double.valueOf(currentTime - (1000
            // * 2)));
            // dMDataSeq.getXAxisDeltas().add(Double.valueOf(currentTime - (1000
            // * 3)));
            OsaData osaData = new OsaData();
            osaData.setDataEvent(dataEvent);
            fieldData.setData(osaData);
            EngUnit engUnit = new EngUnit();
            fieldData.setEngUnit(engUnit);
            FieldIdentifier fieldIdentifier = new FieldIdentifier();
            fieldIdentifier.setId(fieldId);
            fieldIdentifier.setName(fieldName);
            fieldIdentifier.setSource(fieldSource);
            Field field = new Field();
            fieldData.setField(field);
            field.setFieldIdentifier(fieldIdentifier);

            PutFieldDataCriteria putFieldDataCriteria = new PutFieldDataCriteria();
            putFieldDataCriteria.setFieldData(fieldData);
            FieldSelectionFilter selectionFilter = new FieldSelectionFilter();
            Asset asset = new Asset();
            AssetSelector assetSelector = new AssetSelector();
            FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
            fieldIdentifier = new FieldIdentifier();
            fieldIdentifierValue.setFieldIdentifier(fieldIdentifier);
            fieldIdentifier.setId("/asset/assetId");
            fieldIdentifier.setName("/asset/assetId");
            fieldIdentifier.setSource(fieldSource);
            fieldIdentifierValue.setValue(assetId);
            assetSelector.getFieldIdentifierValue().add(fieldIdentifierValue);
            asset.setAssetSelector(assetSelector);
            AssetIdentifier assetIdentifier = new AssetIdentifier();
            assetIdentifier.setId(assetId);
            asset.setAssetIdentifier(assetIdentifier);
            selectionFilter.getFieldIdentifierValue().add(fieldIdentifierValue);
            putFieldDataCriteria.setSelectionFilter(selectionFilter);
            request.getPutFieldDataCriteria().add(putFieldDataCriteria );

           

            return request;


    }

    /**
     * @param log -
     * @param request -
     * @param contentType -
     * @param port -
     * @throws UnsupportedEncodingException -
     * @throws IOException -
     * @throws HttpException -
     */
    @SuppressWarnings("nls")
    public void callPutFieldData(final Logger log, final PutFieldDataRequest request, String contentType, String port)
            throws UnsupportedEncodingException, IOException, HttpException
    {
        if ( log == null ) throw new IllegalArgumentException("log null");

        if ( request == null ) throw new IllegalArgumentException("request null or empty");

        if ( this.serverIP == null || this.serverIP.isEmpty() )
            throw new IllegalArgumentException("uninitialized: serverIP null or empty");

        //
        List<Header> headers = this.restClient.getOauthHttpHeaders();
        java.util.Map<Integer, Model> modelLookupMap  = new HashMap<Integer, Model>();
        PutFieldDataResult result = this.putFieldData.putFieldData(request, modelLookupMap  , headers);

        log.debug("Response status code: " + result);

        try
        {
            Assert.assertNotNull("reply null", result);

            Assert.assertFalse(result.getErrorEvent() == null);
            Assert.assertFalse(result.getErrorEvent().size()>0);
        }
        catch (AssertionError e)
        {
            if ( result != null  )
            {
                if ( contentType.equals(HTTP_PAYLOAD_XML) )
                {
                    // nice place to put a breakpoint
                    String xmlStringPretty = XmlSupport.marshal(result, true);
                    log.error(xmlStringPretty, e);
                }
                else
                    log.error(result.toString(), e);
            }
            else
                log.error("assertionError", e);
            throw e;
        }

    }
}
