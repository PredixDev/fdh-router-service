package com.ge.predix.solsvc.fdh.handler.timeseries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.data.Data;
import com.ge.predix.entity.field.Field;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddata.OsaData;
import com.ge.predix.entity.fielddatacriteria.AssetFieldDataCriteria;
import com.ge.predix.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.predix.entity.fieldselection.FieldSelection;
import com.ge.predix.entity.filter.Filter;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.DatapointsLatestQuery;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.entity.timeseriesfilter.AssetCriteriaAwareTimeseriesFilter;
import com.ge.predix.entity.timeseriesfilter.TimeseriesFilter;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.handler.FDHUtil;
import com.ge.predix.solsvc.fdh.handler.GetDataHandler;
import com.ge.predix.solsvc.fdh.handler.asset.helper.OsaJavaDataTypeConversion;
import com.ge.predix.solsvc.fdh.handler.asset.helper.OsaJavaDataTypeConversion.SupportedJavaTypes;
import com.ge.predix.solsvc.timeseries.bootstrap.client.TimeseriesClient;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

/**
 * Time series Handler - This handler supports 2 Filters. A pure TimeseriesFilter and a AssetCriteriaAwareTimeseriesFilter.
 * 
 * TimeseriesFilter - simply takes the Time series requests and forwards it on to Time series SDK
 * AssetCriteriaAwareTimeseriesFilter -invokes Asset service to get attributes and then replaces the values where it find {{replaceMe}} mustache templates
 * 
 * After it receives the time series data it adapts it to the expectedDataType in the expectedEngineeringUnits
 * 
 * @author predix
 */
@Component
@SuppressWarnings("nls")
@ImportResource(
{
        "classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml",
        "classpath*:META-INF/spring/fdh-adapter-scan-context",
        "classpath*:META-INF/spring/fdh-asset-handler-scan-context.xml"
})
@Profile("timeseries")
public class TimeseriesGetDataHandler
        implements GetDataHandler
{
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(TimeseriesGetDataHandler.class.getName());

    @Autowired
    private TimeseriesClient    timeseriesClient;

    @Autowired
    @Qualifier(value = "assetGetFieldDataHandler")
    private GetDataHandler      assetHandler;

    @Autowired
    private JsonMapper          jsonMapper;

    /**
     * 
     */
    public TimeseriesGetDataHandler()
    {
        super();
    }

    @Override
    public GetFieldDataResult getData(GetFieldDataRequest request, Map<Integer, Object> modelLookupMap,
            List<Header> headers)
    {
        FieldDataCriteria currentCriteria = null;
        try
        {
            // 1. Validate Request
            validateRequest(request);

            GetFieldDataResult result = new GetFieldDataResult();
            for (FieldDataCriteria criteria : request.getFieldDataCriteria())
            {
                currentCriteria = criteria;
                
                // 2. Extract Input from GetFieldDataRequest
                TimeseriesFilter tsFilter = getTimeseriesFilter(criteria, headers);
                
                // 3. Use the override header or Set the header based on environment
                boolean zoneIdFound = FDHUtil.setHeader(headers, criteria.getHeaders(), "Predix-Zone-Id");
                if ( !zoneIdFound )
        			this.timeseriesClient.addZoneIdToHeaders(headers);

                // 4. Get historical data from Time Series
                DatapointsResponse dataPoints = getTimeseriesData(tsFilter, headers);
                adaptDataToExpectedDatatypeAndEngineeringUnits(result, criteria, dataPoints);
            }

            // 5. Send Response
            return result;
        }
        catch (Throwable t)
        {
            String message = "error getting data for data event FieldDataCriteria=" + currentCriteria;
            throw new RuntimeException(message, t);
        }
    }



    private void validateRequest(GetFieldDataRequest request)
    {
        List<FieldDataCriteria> criteriaList = request.getFieldDataCriteria();

        if ( criteriaList == null || criteriaList.size() == 0 )
            throw new UnsupportedOperationException("No FieldDataCriteria");

        for (FieldDataCriteria criteria : criteriaList)
        {
            if ( criteria.getFilter() == null || (!(criteria.getFilter() instanceof TimeseriesFilter)
                    && !(criteria.getFilter() instanceof AssetCriteriaAwareTimeseriesFilter))

            )
            {
                throw new UnsupportedOperationException("filter type=" + criteria.getFilter() + " not supported");
            }
        }
    }

    /**
     * @param criteria
     * @param headers
     * @param filter
     */
    private TimeseriesFilter getTimeseriesFilter(FieldDataCriteria criteria, List<Header> headers)
    {
        Filter filter = criteria.getFilter();

        TimeseriesFilter tsFilter = null;

        if ( filter instanceof TimeseriesFilter )
        {
            tsFilter = (TimeseriesFilter) filter;
        }
        else if ( filter instanceof AssetCriteriaAwareTimeseriesFilter )
        {
            AssetCriteriaAwareTimeseriesFilter assetTsFilter = (AssetCriteriaAwareTimeseriesFilter) filter;
            tsFilter = assetTsFilter.getTimeseriesFilter();

            // looks up attributes in Predix Asset and then replaces the {{replaceMe}} mustache variables
            String json = this.jsonMapper.toJson(tsFilter);
            if ( json.contains("{{") || json.contains("}}"))
            {
            	List<Header> headersToUse = FDHUtil.copyHeaders(headers);
                Map<String, Object> responseMap = getAssetAttributesFromAssetCriteria(criteria, headersToUse);
                String replacedJson = json.replaceAll("<!-- -->", "");
                
                Template template = Mustache.compiler().compile(replacedJson);
                json = template.execute(responseMap);
            }
            tsFilter = this.jsonMapper.fromJson(json, TimeseriesFilter.class);
        }

        return tsFilter;
    }

    /**
     * @param assetTagKey
     */
    private DatapointsResponse getTimeseriesData(TimeseriesFilter tsFilter, List<Header> headers)
    {

        DatapointsQuery query = null;
        DatapointsLatestQuery queryLatest = null;
        DatapointsResponse response = null;

        query = tsFilter.getDatapointsQuery();
      
    	
        if ( query == null )
        {
            // Try for getting latest data points query
            queryLatest = tsFilter.getDatapointsLatestQuery();

            response = this.timeseriesClient.queryForLatestDatapoint(queryLatest, headers);
        }
        else
        {
            response = this.timeseriesClient.queryForDatapoints(query, headers);
        }

        return response;
    }

    /**
     * @param criteria
     * @return
     */
    private Map<String, Object> getAssetAttributesFromAssetCriteria(FieldDataCriteria originalCriteria,
            List<Header> headers)
    {
        AssetCriteriaAwareTimeseriesFilter filter = (AssetCriteriaAwareTimeseriesFilter) originalCriteria.getFilter();
        AssetFieldDataCriteria assetFieldDataCriteria = filter.getAssetFieldDataCriteria();

        // prepare a request to the AssetHandler
        GetFieldDataRequest assetRequest = new GetFieldDataRequest();
        assetRequest.getFieldDataCriteria().add(assetFieldDataCriteria);

        // call Predix Asset
        GetFieldDataResult assetResponse = this.assetHandler.getData(assetRequest, null, headers);

        // Form the results in a map and then replace the {{replaceMe}} mustache variables
        Map<String, Object> responseMap = new HashMap<String, Object>();
        for (FieldData fieldData : assetResponse.getFieldData())
        {
            String resultId = fieldData.getResultId();
            Data data = fieldData.getData();
            String dataAsString = adaptDataToString(data);
            responseMap.put(resultId, dataAsString);
        }

        return responseMap;
    }

    /**
     * @param responseMap
     * @param resultId
     * @param data -
     */
    private String adaptDataToString(Data data)
    {
        if ( data instanceof OsaData )
        {
            String dataAsString = (String) OsaJavaDataTypeConversion.convertOsaDataToJavaStructure((OsaData) data,
                    SupportedJavaTypes.String);
            return dataAsString;
        }
        throw new UnsupportedOperationException(
                "unable to adapt variable because data type conversion not supported for data=" + data);
    }

    /**
     * @param result
     * @param criteria
     * @param dataPoints
     */
    private void adaptDataToExpectedDatatypeAndEngineeringUnits(GetFieldDataResult result, FieldDataCriteria criteria,
            DatapointsResponse datapoints)
    {
        for (FieldSelection selection : criteria.getFieldSelection())
        {
            String expectedDataType = selection.getExpectedDataType();
            String expectedEngineeringUnits = selection.getExpectedEU() == null ? null
                    : selection.getExpectedEU().getCode();

            if ( expectedEngineeringUnits != null )
            {
                throw new UnsupportedOperationException(
                        "conversion to expectedEngineeringUnits=" + expectedEngineeringUnits + " not supported");
            }

            if ( DatapointsResponse.class.getSimpleName().equals(expectedDataType) )
            {
                FieldData fieldData = new FieldData();

                fieldData.setResultId(selection.getResultId());
                fieldData.setData(datapoints);
                Field field = new Field();
                field.setFieldIdentifier(criteria.getFieldSelection().get(0).getFieldIdentifier());
                fieldData.getField().add(field);
                result.getFieldData().add(fieldData);
                return;
            }
            throw new UnsupportedOperationException(
                    "conversion to expectedDataType=" + expectedDataType + " not supported");
        }
    }

}
