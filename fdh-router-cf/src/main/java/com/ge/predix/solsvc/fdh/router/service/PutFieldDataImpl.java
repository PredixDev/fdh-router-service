/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.router.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.ge.dsp.pm.ext.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.dsp.pm.ext.entity.model.Model;
import com.ge.dsp.pm.ext.entity.selectionfilter.SelectionFilter;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataCriteria;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.solsvc.fdh.handler.PutFieldDataHandler;
import com.ge.predix.solsvc.fdh.router.validator.CBRPutFieldDataCriteriaValidator;
import com.ge.predix.solsvc.fdh.router.validator.CBRPutFieldDataValidator;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * 
 * @author predix
 */
@Component(value = "putFieldDataService")
public class PutFieldDataImpl
        implements PutFieldDataService, ApplicationContextAware
{
    private static final Logger      log = LoggerFactory.getLogger(PutFieldDataImpl.class);

    @Autowired
    private CBRPutFieldDataValidator validator;

    @Autowired
    private CBRPutFieldDataCriteriaValidator criteriaValidator;
    
    @Autowired
    private RestClient               restClient;

    @Autowired
    @Qualifier("assetPutFieldDataHandler")
    private PutFieldDataHandler      putFieldDataHandler;

    private ApplicationContext       context;

    /**
     * @param context
     *            -
     * @param request -
     * @param headers -
     * @param putFieldDataRequest
     *            -
     * @param errorHolder
     *            -
     * @return -
     */
    @Override
    @SuppressWarnings(
    {
        "nls"
    })
    public PutFieldDataResult putFieldData(PutFieldDataRequest request,Map<Integer, Model> modelLookupMap, List<Header> headers)
    {
        this.validator.validate(request);
        
        PutFieldDataResult fullResult = new PutFieldDataResult();
        for (PutFieldDataCriteria fieldDataCriteria : request.getPutFieldDataCriteria())
        {
            try
            {
                this.criteriaValidator.validatePutFieldDataCriteria(fieldDataCriteria);

                if ( fieldDataCriteria.getFieldData().getField().getFieldIdentifier().getSource() != null
                        && fieldDataCriteria.getFieldData().getField().getFieldIdentifier().getSource()
                                .equals(FieldSourceEnum.PREDIX_ASSET.name()) )
                {
                    processSinglePredixAsset(request, modelLookupMap, headers, fullResult, fieldDataCriteria);
                }
                else if ( fieldDataCriteria.getFieldData().getField().getFieldIdentifier().getSource()
                        .contains("handler/") )
                {
                    processSingleCustomHandler(request,modelLookupMap, headers, fullResult, fieldDataCriteria);
                }
                else
                    throw new UnsupportedOperationException("Source="
                            + fieldDataCriteria.getFieldData().getField().getFieldIdentifier().getSource()
                            + " not supported");
            }
            catch (Throwable e)
            {
                // if one of the FieldData can't be routed/stored we'll continue with the others. The caller should submit compensating transactions.
                String fieldString = null;
                if ( fieldDataCriteria != null && fieldDataCriteria.getFieldData().getField() != null )
                    fieldString = fieldDataCriteria.getFieldData().getField().toString();
                SelectionFilter selectionFilter = null;
                if ( fieldDataCriteria != null && fieldDataCriteria.getSelectionFilter() != null )
                    selectionFilter = fieldDataCriteria.getSelectionFilter();
                String msg = "unable to process request errorMsg=" + e.getMessage() + " request.correlationId="
                        + request.getCorrelationId() + " solutionIdentifier" + request.getSolutionIdentifier()
                        + " selectionFilter=" + selectionFilter + " for field=" + fieldString;
                log.error(msg);
                fullResult.getErrorEvent().add(msg);
            }

        }
        return fullResult;
    }



    /**
     * @param request
     * @param headers
     * @param fullResult
     * @param fieldDataCriteria -
     */
    @SuppressWarnings("nls")
    private void processSingleCustomHandler(PutFieldDataRequest request, Map<Integer, Model> modelLookupMap, List<Header> headers,
            PutFieldDataResult fullResult, PutFieldDataCriteria fieldDataCriteria)
    {
        PutFieldDataRequest singleRequest = makeSingleRequest(request, fieldDataCriteria);

        String source = fieldDataCriteria.getFieldData().getField().getFieldIdentifier().getSource();
        String beanName = source.substring(source.indexOf("handler/") + 8);
        beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);

        PutFieldDataHandler bean = (PutFieldDataHandler) this.context.getBean(beanName);
        PutFieldDataResult singleResult = bean.putFieldData(singleRequest, modelLookupMap , headers, HttpMethod.POST);
        if ( singleResult.getErrorEvent() != null && singleResult.getErrorEvent().size() > 0 )
            fullResult.getErrorEvent().addAll(singleResult.getErrorEvent());
    }

    /**
     * @param request
     * @param headers
     * @param fullResult
     * @param fieldDataCriteria -
     */
    private void processSinglePredixAsset(PutFieldDataRequest request, Map<Integer, Model> modelLookupMap, List<Header> headers,
            PutFieldDataResult fullResult, PutFieldDataCriteria fieldDataCriteria)
    {
        PutFieldDataRequest singleRequest = makeSingleRequest(request, fieldDataCriteria);
        PutFieldDataResult singleResult = this.putFieldDataHandler.putFieldData(singleRequest, modelLookupMap, headers, HttpMethod.POST);
        if ( singleResult.getErrorEvent() != null && singleResult.getErrorEvent().size() > 0 )
            fullResult.getErrorEvent().addAll(singleResult.getErrorEvent());
    }

    /**
     * @param request
     * @param fieldDataCriteria
     * @return
     */
    private PutFieldDataRequest makeSingleRequest(PutFieldDataRequest request, PutFieldDataCriteria putFieldDataCriteria)
    {
        PutFieldDataRequest singleRequest = new PutFieldDataRequest();
        singleRequest.setCorrelationId(request.getCorrelationId());
        singleRequest.setExternalAttributeMap(request.getExternalAttributeMap());
        singleRequest.setSolutionIdentifier(request.getSolutionIdentifier());
        singleRequest.getPutFieldDataCriteria().add(putFieldDataCriteria);

        return singleRequest;
    }

    /**
     * Each FieldData might have a different handler. This creates a Request
     * with just the one FieldData for the current Field
     * 
     * @param fieldData
     * @return
     */
    @SuppressWarnings("unused")
    private PutFieldDataRequest adaptNewRequest(PutFieldDataRequest request, PutFieldDataCriteria fieldDataCriteria)
    {

        PutFieldDataRequest newRequest = new PutFieldDataRequest();
        newRequest.setExternalAttributeMap(request.getExternalAttributeMap());
        newRequest.setCorrelationId(request.getCorrelationId());
        newRequest.setSolutionIdentifier(request.getSolutionIdentifier());
        newRequest.getPutFieldDataCriteria().add(fieldDataCriteria);
        return newRequest;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException
    {
        this.context = applicationContext;
    }

}
