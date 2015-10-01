package com.ge.predix.solsvc.fdh.router;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.message.BasicHeader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mimosa.osacbmv3_3.OsacbmDataType;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.ge.dsp.pm.ext.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.dsp.pm.ext.entity.osa.selectionfilter.AnchorTimeType;
import com.ge.dsp.pm.ext.entity.osa.selectionfilter.TimeOffsetUnits;
import com.ge.predix.solsvc.bootstrap.ams.dto.Asset;
import com.ge.predix.solsvc.bootstrap.ams.dto.AssetMeter;
import com.ge.predix.solsvc.bootstrap.ams.dto.Attribute;
import com.ge.predix.solsvc.bootstrap.ams.factories.AssetFactory;
import com.ge.predix.solsvc.bootstrap.tbs.entity.TimeseriesQueryBuilder;
import com.ge.predix.solsvc.bootstrap.tbs.response.entity.TimeseriesQuery;
import com.ge.predix.solsvc.bootstrap.tbs.response.entity.TimeseriesQueryResponse;
import com.ge.predix.solsvc.bootstrap.tbs.response.entity.TimeseriesResult;
import com.ge.predix.solsvc.bootstrap.tsb.client.TimeseriesRestConfig;
import com.ge.predix.solsvc.bootstrap.tsb.factories.TimeseriesFactory;
import com.ge.predix.solsvc.fdh.router.util.StringUtil;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * @author tturner
 */
@SuppressWarnings(
{
        "nls"
})
@ActiveProfiles("local")
public class GetFieldDataTest extends BaseTest {
    private static final Logger          log = LoggerFactory.getLogger(GetFieldDataTest.class.getName());
    
    private static final String HTTP_PAYLOAD_JSON     = "application/json";
    private static final String CONTAINER_SERVER_PORT = "9092";


    @Autowired
    private AssetFactory assetFactory;
    @Autowired
    private TimeseriesFactory timeseriesFactory;
    @Autowired
    private RestClient restClient;
    @Autowired
    private TimeseriesRestConfig timeseriesRestConfig;

    /**
     * @throws Exception -
     */
    @Before
    public void onSetUp()
            throws Exception
    {
        //
    }

    /**
     * 
     */
    @After
    public void onTearDown()
    {
        //
    }

    /**
     * @throws JMSException -
     * @throws HttpException -
     * @throws IOException -
     * @throws JAXBException -
     */
    @Test
    public void testGet()
            throws IOException, JAXBException, HttpException
    {
        String solutionId = "1000";
        String namespace = "asset";
        String attributeName = "description";
        String fieldId = namespace + "/" + attributeName;
        String fieldName = namespace + "/" + attributeName;
        String fieldSource = FieldSourceEnum.PREDIX_ASSET.name();
        String assetId = "12345";
        String[] expectedValues = new String[]
        {
            "Tanks"
        };
		String startDef = null;
		String starTimeOffset = null;
		AnchorTimeType anchorTimeType = null;
		TimeOffsetUnits timeOffsetUnits = null;
		String endDef = null;

        Date now = new Date();
        String gmtTimeString = StringUtil.convertToDateTimeString(now);

        List<Asset> assets = new ArrayList<Asset>();
        Asset asset = new Asset();
        asset.setAssetId("12345");
        asset.setAttributes(new LinkedHashMap<String, Attribute>());
        Attribute attribute = new Attribute(); 
        attribute.getValue().add("value");
        asset.getAttributes().put(attributeName,attribute );
        assets.add(asset );
        Mockito.when(this.assetFactory.getAssetsByFilter(Matchers.any(), Matchers.anyListOf(Header.class))).thenReturn(assets);
      
        callGetFieldData(solutionId, fieldId, fieldName, fieldSource, assetId, gmtTimeString, log, expectedValues, OsacbmDataType.DA_STRING, HTTP_PAYLOAD_JSON, CONTAINER_SERVER_PORT, startDef, starTimeOffset, anchorTimeType, timeOffsetUnits, endDef);
    }

    /**
     * @throws JMSException -
     * @throws HttpException -
     * @throws IOException -
     * @throws JAXBException -
     */
    @Test
    public void testGetWithTimeSelectionFilter()
            throws IOException, JAXBException, HttpException
    {
        String solutionId = "1000";
        String namespace = "classification/meter";
        String attributeName = "MyMeter1";
        String fieldId = namespace + "/" + attributeName;
        String fieldName = "/classification/MyMeter1";
        String fieldSource = FieldSourceEnum.PREDIX_TIMESERIES.name();
        String assetId = "12345";
        String[] expectedValues = new String[]
        {
            "123.0",
            "124.0"
        };
		String startDef = "Sat Sep 26 16:38:33 PDT 2013";
		String starTimeOffset = "1";
		AnchorTimeType anchorTimeType = AnchorTimeType.DISPATCHER;
		TimeOffsetUnits timeOffsetUnits = TimeOffsetUnits.DAYS;
		String endDef = "Sat Sep 27 16:38:33 PDT 2013";
		
        Date now = new Date();
        String gmtTimeString = StringUtil.convertToDateTimeString(now);
        
        Asset asset = new Asset();
        asset.setAssetId("12345");
        asset.setAttributes(new LinkedHashMap<String, Attribute>());
        Attribute attribute = new Attribute(); 
        attribute.getValue().add("value");
        asset.getAttributes().put(attributeName,attribute );
        asset.setAssetMeter(new LinkedHashMap<String, AssetMeter>());
        AssetMeter meter = new AssetMeter();
        meter.setSourceTagId("myTimeseriesTag1");
        asset.getAssetMeter().put("MyMeter1", meter);
        Mockito.when(this.assetFactory.getAsset(Matchers.anyString(), Matchers.anyListOf(Header.class))).thenReturn(asset);
        
        
        TimeseriesQueryResponse timeseriesQueryResponse = new TimeseriesQueryResponse();
        TimeseriesQuery timeseriesQuery = new TimeseriesQuery();
        List<List<Double>> tagsResultList = new ArrayList<List<Double>>();
        List<Double> tagResult = new ArrayList<Double>();
        Double time = new Double(new Date().getTime());
        Double value = new Double(expectedValues[0]);
        tagResult.add(time );
        tagResult.add(value );
        tagsResultList.add(tagResult);
        tagResult = new ArrayList<Double>();
        time = new Double(new Date().getTime());
        value = new Double(expectedValues[1]);
        tagResult.add(time );
        tagResult.add(value );
        tagsResultList.add(tagResult);
        TimeseriesResult timeseriesResult = new TimeseriesResult("myTimeseriesTag1", null, tagsResultList, null);
        timeseriesQuery.getResults().add(timeseriesResult );
        timeseriesQueryResponse.getQueries().add(timeseriesQuery );
        Mockito.when(this.timeseriesFactory.query(Matchers.anyString(), Matchers.any(TimeseriesQueryBuilder.class), Matchers.anyListOf(Header.class))).thenReturn(timeseriesQueryResponse );
        
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Authorization", null));
        Mockito.when(this.restClient.getSecureToken(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyString(),
                Matchers.anyString(), Matchers.anyString(), Matchers.anyBoolean())).thenReturn(headers );
        callGetFieldData(solutionId, fieldId, fieldName, fieldSource, assetId, gmtTimeString, log, expectedValues, OsacbmDataType.DM_DATA_SEQ, HTTP_PAYLOAD_JSON, CONTAINER_SERVER_PORT, startDef, starTimeOffset, anchorTimeType, timeOffsetUnits, endDef);

    }



}
