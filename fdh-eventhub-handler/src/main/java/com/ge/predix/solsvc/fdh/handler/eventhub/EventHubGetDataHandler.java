package com.ge.predix.solsvc.fdh.handler.eventhub;

import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.timeseriesfilter.AssetCriteriaAwareTimeseriesFilter;
import com.ge.predix.entity.timeseriesfilter.TimeseriesFilter;
import com.ge.predix.solsvc.fdh.handler.GetDataHandler;

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
        "classpath*:META-INF/spring/predix-websocket-client-scan-context.xml",
        "classpath*:META-INF/spring/fdh-adapter-scan-context"
})
@Profile("eventhub")
public class EventHubGetDataHandler
        implements GetDataHandler
{
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(EventHubGetDataHandler.class.getName());

    
    /**
     * 
     */
    public EventHubGetDataHandler()
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
                
            }

            // 4. Send Response
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
}
