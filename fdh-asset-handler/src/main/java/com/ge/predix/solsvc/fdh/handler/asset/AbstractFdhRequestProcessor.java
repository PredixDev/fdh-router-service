/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.handler.asset;

import java.util.List;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ge.predix.entity.fieldidentifiervalue.FieldIdentifierValue;
import com.ge.predix.solsvc.bootstrap.ams.common.AssetConfig;
import com.ge.predix.solsvc.bootstrap.ams.factories.ModelFactory;
import com.ge.predix.solsvc.fdh.handler.asset.common.AssetQueryBuilder;
import com.ge.predix.solsvc.fdh.handler.asset.common.Operator;
import com.ge.predix.solsvc.fdh.handler.asset.data.AttributeHandlerFactory;
import com.ge.predix.solsvc.fdh.handler.asset.helper.PaUtility;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * Base class of the ADH request processor classes
 * Contains helper methods shared by derived classes
 * 
 * @author 212369540
 */
@SuppressWarnings(
{
    "nls"
})
public abstract class AbstractFdhRequestProcessor
{
    @SuppressWarnings("unused")
    private static final Logger     log = LoggerFactory.getLogger(AbstractFdhRequestProcessor.class);
    /**
     * 
     */
    @Autowired
    protected AttributeHandlerFactory        attributeHandlerFactory;

    /**
     * 
     */
    @Autowired
    protected RestClient              restClient;

    /**
     * 
     */
    @Autowired
    protected AssetConfig assetConfig;


    /**
     * 
     */
    @Autowired
    protected ModelFactory modelFactory;
    // @Autowired
    // private IUomDataExchange uomDataExchangeHandler;


     /**
     * @param model -
     * @param modelFilter -
     * @param headers -
     * @return -
     */
    protected AssetQueryBuilder getModelQuery(String model, List<FieldIdentifierValue> modelFilter, List<Header> headers)
    {
        AssetQueryBuilder assetQueryBuilder = new AssetQueryBuilder();
        assetQueryBuilder.setModel(model);
        for (FieldIdentifierValue filter : modelFilter)
        {
            String filterFieldId = filter.getFieldIdentifier().getId().toString();
            Object filterFieldValue = filter.getValue();
            if ( PaUtility.isModelAttributeField(filterFieldId) )
            {
                if ( filterFieldValue != null && filterFieldValue.toString().length() > 0 )
                {
                    String attributeName = PaUtility.getAttributeName(filterFieldId);
                    assetQueryBuilder.addAttributeFilter(attributeName, Operator.EQ, filterFieldValue.toString());
                }
            }
            else
            	throw new UnsupportedOperationException("filterFieldId=" + filterFieldId
            			+ " is not supported.  Expecting resource and field.  e.g. address/addressLine1 or asset/assetId");
        }
        String query = assetQueryBuilder.build();
        if ( query == null || query.equals("") )
            throw new UnsupportedOperationException("filter=" + query
                    + " is not supported.  Would result in all assets being returned.");
        return assetQueryBuilder;
    }


}
