/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.handler.asset.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.fdh.handler.asset.data.updater.AttributeUpdaterInterface;
import com.ge.predix.solsvc.fdh.handler.asset.data.updater.ModelAttributeUpdater;
import com.ge.predix.solsvc.fdh.handler.asset.helper.PaUtility;

/**
 * Factory to create the object that can retrieve and store the asset data
 * The type of object created is based on the type of field
 * 
 * @author 212369540
 */
@Component
public class AttributeHandlerFactory
{
    @SuppressWarnings("unused")
    private static final Logger   log = LoggerFactory.getLogger(AttributeHandlerFactory.class);

    @Autowired
    private ModelAttributeUpdater modelAttributeUpdater;

    /**
     * @param fieldIdString indicates the type of field so that appropriate type of object is created
     * @return FieldData object that can retrieve and store the asset data
     */
    public AttributeHandlerInterface getHandler(String fieldIdString)
    {
        return this.modelAttributeUpdater;
    }

    /**
     * @param fieldIdString -
     * @return -
     */
    @SuppressWarnings("nls")
    public AttributeUpdaterInterface create(String fieldIdString)
    {
        if ( PaUtility.isModelAttributeField(fieldIdString) )
        {
            return new ModelAttributeUpdater();
        }
        throw new UnsupportedOperationException("AttributeUpdater for fieldIdString=" + fieldIdString + " not found");
    }

}
