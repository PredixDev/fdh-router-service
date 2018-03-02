/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.handler.asset.helper;

import org.mimosa.osacbmv3_3.DataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author 502200255
 */
@SuppressWarnings(
{
        "unused", "nls"
})
public class FdhAssetEngUnitsConverter
{
    private static final Logger log = LoggerFactory.getLogger(FdhAssetEngUnitsConverter.class);
    private IUnitConverter   unitConverter;
    private IUomDataExchange uomDataExchange;
    private String           uomHostAddress;
    private String           uomPortNumber;

    /**
     * @param uomDataExchange uomDataExchange
     * @param uomHostAddress uomHostAddress
     * @param uomPortNumber uomPortNumber
     */
    public FdhAssetEngUnitsConverter(IUomDataExchange uomDataExchange, String uomHostAddress, String uomPortNumber)
    {
        setUomDataExchange(uomDataExchange);
        setUomHostAddress(uomHostAddress);
        setUomPortNumber(uomPortNumber);
        prepareUnitConverter();
    }

    /**
     * invoke the unit converter to perform the actual conversion
     * 
     * @param givenDataEvent givenDataEvent
     * @param givenUoM givenUoM
     * @param expectedUoM expectedUoM
     * @return convertedDataEvent
     */
    public DataEvent convert(final DataEvent givenDataEvent, final String givenUoM, final String expectedUoM)
    {
        if ( givenUoM == null && expectedUoM == null || expectedUoM == null || expectedUoM.equals(givenUoM) )
        {
            return givenDataEvent;
        }
        if ( givenUoM == null )
        {
            throw new RuntimeException("UoM conversion failed: expected = " + expectedUoM
                    + " but given UoM is not known");
        }
        getUnitConverter().convertDataEvent(givenUoM, expectedUoM, givenDataEvent);
        return givenDataEvent;
    }

    /**
     * @return the unitConverter
     */
    public IUnitConverter getUnitConverter()
    {
        return this.unitConverter;
    }

    /**
     * @param unitConverter the unitConverter to set
     */
    public void setUnitConverter(IUnitConverter unitConverter)
    {
        this.unitConverter = unitConverter;
    }

    /**
     * uomDataExchange.getIUnitConverter call will result eventually in a rest call in order to get
     * the UnitConverter.
     * 
     * @param orchestrationContext - Orchestration Context
     */
    private void prepareUnitConverter()
    {
        this.unitConverter = getUomDataExchange().getIUnitConverter(getUomHostAddress(), getUomPortNumber());
    }

    /**
     * @return the uomDataExchange
     */
    public IUomDataExchange getUomDataExchange()
    {
        return this.uomDataExchange;
    }

    /**
     * @param uomDataExchange the uomDataExchange to set
     */
    public void setUomDataExchange(IUomDataExchange uomDataExchange)
    {
        this.uomDataExchange = uomDataExchange;
    }

    /**
     * @return the uomHostAddress
     */
    public String getUomHostAddress()
    {
        return this.uomHostAddress;
    }

    /**
     * @param uomHostAddress the uomHostAddress to set
     */
    public void setUomHostAddress(String uomHostAddress)
    {
        this.uomHostAddress = uomHostAddress;
    }

    /**
     * @return the uomPortNumber
     */
    public String getUomPortNumber()
    {
        return this.uomPortNumber;
    }

    /**
     * @param uomPortNumber the uomPortNumber to set
     */
    public void setUomPortNumber(String uomPortNumber)
    {
        this.uomPortNumber = uomPortNumber;
    }

}
