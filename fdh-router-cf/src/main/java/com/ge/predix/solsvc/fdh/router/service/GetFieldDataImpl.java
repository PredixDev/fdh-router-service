package com.ge.predix.solsvc.fdh.router.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;

import org.apache.http.Header;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.ge.dsp.pm.ext.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.dsp.pm.ext.entity.fieldselection.FieldSelection;
import com.ge.dsp.pm.ext.entity.model.Model;
import com.ge.dsp.pm.fielddatahandler.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.solsvc.bootstrap.tsb.client.TimeseriesRestConfig;
import com.ge.predix.solsvc.fdh.handler.GetFieldDataHandler;
import com.ge.predix.solsvc.fdh.handler.timeseries.TimeseriesDataRetrievalStrategy;
import com.ge.predix.solsvc.fdh.router.validator.CBRGetFieldDataValidator;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * 
 * @author predix -
 */
@Component(value = "getFieldDataService")
public class GetFieldDataImpl
        implements GetFieldDataService, ApplicationContextAware
{

    @Autowired
    private CBRGetFieldDataValidator        validator;

    @Autowired
    private TimeseriesDataRetrievalStrategy timeseriesHandler;
    @Autowired
    @Qualifier("assetGetFieldDataHandler")
    private GetFieldDataHandler           getFieldDataHandler;
    @Autowired
    private RestClient                      restClient;

    @Autowired
    private TimeseriesRestConfig timeseriesRestConfig;
    
    private ApplicationContext context;

    /**
     * This methods loops through the Requests for a given asset - and continues
     * through the same processing logic for a different asset. We need to start
     * transaction at this point here to ensure no lazy initialization exception
     * is thrown due to restoring the FieldMetaData objects.
     * 
     * @param context
     *            -
     * @param getFieldDataRequest
     *            -
     * @param errorHolder
     *            -
     * @return -
     */
    @Override
    public GetFieldDataResult getFieldData(GetFieldDataRequest getFieldDataRequest, Map<Integer, Model> modelLookupMap, List<Header> headers)
    {
        this.validator.validate(getFieldDataRequest);
        GetFieldDataResult fullResult = processRequest(getFieldDataRequest, modelLookupMap, headers);
        return fullResult;
    }

    /**
     */
    @SuppressWarnings("nls")
    private GetFieldDataResult processRequest(GetFieldDataRequest request,Map<Integer, Model> modelLookupMap, List<Header> headers)
    {
        try
        {
            GetFieldDataResult fullResult = new GetFieldDataResult();
            for (FieldDataCriteria fieldDataCriteria : request.getFieldDataCriteria())
            {
                for (FieldSelection fieldSelection : fieldDataCriteria.getFieldSelection())
                {
                    String source = fieldSelection.getFieldIdentifier().getSource();
                    if ( source == null )
                        throw new UnsupportedOperationException("fieldSelection.getField().getFieldIdentifier().getSource()=" + source + " not supported" );

                    if ( source.equals(FieldSourceEnum.PREDIX_ASSET.name()) )
                    {
                        processSinglePredixAsset(request, fullResult, fieldDataCriteria, fieldSelection, modelLookupMap, headers);
                    }
                    else if ( source.equals(FieldSourceEnum.PREDIX_TIMESERIES.name()) )
                    {
                        processSinglePredixTimeseries(request, headers, fullResult, fieldDataCriteria, fieldSelection);
                    }
                    else if ( source.contains("/handler")){
                        processSingleCustomHandler(request, headers, fullResult, fieldDataCriteria, fieldSelection);
                    }
                    else
                        throw new UnsupportedOperationException("fieldSelection.getField().getFieldIdentifier().getSource()=" + source + " not supported" );

                }
            }
            return fullResult;
        }
        catch (IllegalStateException e)
        {
            throw new RuntimeException("error in getFieldData", e);
        }
    }

    /**
     * @param request
     * @param headers
     * @param fullResult
     * @param fieldDataCriteria
     * @param fieldSelection -
     */
    @SuppressWarnings("nls")
    private void processSingleCustomHandler(GetFieldDataRequest request, List<Header> headers, GetFieldDataResult fullResult,
            FieldDataCriteria fieldDataCriteria, FieldSelection fieldSelection)
    {
        GetFieldDataRequest singleRequest = makeSingleRequest(request, fieldDataCriteria, fieldSelection);
        
        String source = fieldSelection.getFieldIdentifier().getSource();
        if ( !source.contains("handler/"))
            throw new UnsupportedOperationException("please add the name of Spring Bean after handler  e.g. handler/sampleHandler");
        String beanName = source.substring(source.indexOf("handler/")+8);
        beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
        
        GetFieldDataHandler bean = (GetFieldDataHandler) this.context.getBean(beanName);
        Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();

        GetFieldDataResult singleResult = bean.getFieldData(singleRequest,modelLookupMap, headers,HttpMethod.GET);
        fullResult.getFieldData().addAll(singleResult.getFieldData());
    }

    /**
     * @param context
     * @param request
     * @param result
     * @param fieldDataCriteria
     * @param fieldSelection
     */
    private void processSinglePredixTimeseries(GetFieldDataRequest request, List<Header> headers,
            GetFieldDataResult fullResult, FieldDataCriteria fieldDataCriteria, FieldSelection fieldSelection)
    {
        GetFieldDataRequest singleRequest = makeSingleRequest(request, fieldDataCriteria, fieldSelection);
        GetFieldDataResult singleResult = this.timeseriesHandler.getFieldData(this.timeseriesRestConfig.getQueryUri(), headers, singleRequest);
        fullResult.getFieldData().addAll(singleResult.getFieldData());
    }

    /**
     * @param request
     * @param fieldDataCriteria
     * @param fieldSelection
     * @param modelLookupMap 
     * @param context
     * @param result
     */
    private void processSinglePredixAsset(GetFieldDataRequest request, GetFieldDataResult fullResult, FieldDataCriteria fieldDataCriteria, FieldSelection fieldSelection,
            Map<Integer, Model> modelLookupMap, List<Header> headers)
    {
        GetFieldDataRequest singleRequest = makeSingleRequest(request, fieldDataCriteria, fieldSelection);
        GetFieldDataResult singleResult = this.getFieldDataHandler.getFieldData(singleRequest, modelLookupMap, headers, HttpMethod.GET);
        fullResult.getFieldData().addAll(singleResult.getFieldData());
    }

    private GetFieldDataRequest makeSingleRequest(GetFieldDataRequest request, FieldDataCriteria fieldDataCriteria,
            FieldSelection fieldSelection)
    {
        GetFieldDataRequest singleRequest = new GetFieldDataRequest();
        singleRequest.setCorrelationId(request.getCorrelationId());
        singleRequest.setExternalAttributeMap(request.getExternalAttributeMap());
        singleRequest.setSolutionIdentifier(request.getSolutionIdentifier());
        FieldDataCriteria singleFieldDataCriteria = new FieldDataCriteria();
        singleFieldDataCriteria.setResultId(fieldDataCriteria.getResultId());
        singleFieldDataCriteria.getFieldSelection().add(fieldSelection);
        singleFieldDataCriteria.setSelectionFilter(fieldDataCriteria.getSelectionFilter());
        singleRequest.getFieldDataCriteria().add(singleFieldDataCriteria);
        return singleRequest;
    }

    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException
    {
        this.context = applicationContext;
    }

}
