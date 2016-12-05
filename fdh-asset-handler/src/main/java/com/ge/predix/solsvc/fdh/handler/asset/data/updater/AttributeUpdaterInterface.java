/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.handler.asset.data.updater;

import java.util.List;

import org.apache.http.Header;
import org.mimosa.osacbmv3_3.DataEvent;

import com.ge.predix.entity.engunit.EngUnit;
import com.ge.predix.solsvc.fdh.handler.asset.common.FieldModel;
import com.ge.predix.solsvc.fdh.handler.asset.data.AttributeHandlerInterface;

/**
 * 
 * @author 212369540
 * 
 *         Interface for retrieving and storing the asset data
 */
public interface AttributeUpdaterInterface extends AttributeHandlerInterface
{

    /**
     * @param model -
     * @param fieldModel -
     * @param dataEvent Data to be stored
     * @param engUnit Engineering unit of the incoming data
     * @param headers -
     */
    public void storeOsaData(Object model, FieldModel fieldModel, DataEvent dataEvent, EngUnit engUnit, List<Header> headers);



}
