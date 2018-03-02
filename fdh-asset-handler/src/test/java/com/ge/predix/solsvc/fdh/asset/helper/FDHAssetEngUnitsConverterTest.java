/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.asset.helper;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mimosa.osacbmv3_3.DataEvent;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.solsvc.fdh.asset.handler.BaseTest;
import com.ge.predix.solsvc.fdh.handler.asset.helper.FdhAssetEngUnitsConverter;
import com.ge.predix.solsvc.fdh.handler.asset.helper.IUnitConverter;
import com.ge.predix.solsvc.fdh.handler.asset.helper.IUomDataExchange;

/**
 * 
 * @author 212369540
 */
@SuppressWarnings(
{
        "nls"
})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml",
        "classpath*:META-INF/spring/ext-util-scan-context.xml",
        "classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml",
        "classpath*:META-INF/spring/fdh-asset-handler-scan-context.xml"

})
public class FDHAssetEngUnitsConverterTest extends BaseTest
{
    /**
     *  -
     */
    @Test
    public void testPapiEngUnitsConverter()
    {
        IUomDataExchange uomDataExchange = Mockito.mock(IUomDataExchange.class);
        IUnitConverter unitConverter = Mockito.mock(IUnitConverter.class);
        Mockito.when(uomDataExchange.getIUnitConverter(Matchers.anyString(), Matchers.anyString())).thenReturn(
                unitConverter);
        String uomHostAddress = "localhost";
        String uomPortNumber = "9090";

        FdhAssetEngUnitsConverter fdhAssetEngUnitsConverter = new FdhAssetEngUnitsConverter(uomDataExchange, uomHostAddress,
                uomPortNumber);

        // test getUnitConverter
        IUnitConverter unitConverter2 = fdhAssetEngUnitsConverter.getUnitConverter();
        Assert.assertEquals(unitConverter, unitConverter2);
        // test setUnitConverter
        fdhAssetEngUnitsConverter.setUnitConverter(unitConverter2);
        Assert.assertEquals(unitConverter2, fdhAssetEngUnitsConverter.getUnitConverter());

        // uomDataExchange
        IUomDataExchange uomDataExchange2 = fdhAssetEngUnitsConverter.getUomDataExchange();
        Assert.assertEquals(uomDataExchange, uomDataExchange2);
        fdhAssetEngUnitsConverter.setUomDataExchange(uomDataExchange2);
        Assert.assertEquals(uomDataExchange2, fdhAssetEngUnitsConverter.getUomDataExchange());

        // uomHostAddress
        String uomHostAddress2 = fdhAssetEngUnitsConverter.getUomHostAddress();
        Assert.assertEquals(uomHostAddress, uomHostAddress2);
        fdhAssetEngUnitsConverter.setUomHostAddress(uomHostAddress2);
        Assert.assertEquals(uomHostAddress2, fdhAssetEngUnitsConverter.getUomHostAddress());

        // uomPortNumber
        String uomPortNumber2 = fdhAssetEngUnitsConverter.getUomPortNumber();
        Assert.assertEquals(uomPortNumber, uomPortNumber2);
        fdhAssetEngUnitsConverter.setUomPortNumber(uomPortNumber2);
        Assert.assertEquals(uomPortNumber2, fdhAssetEngUnitsConverter.getUomPortNumber());

        // test conversion
        DataEvent dataEvent = Mockito.mock(DataEvent.class);
        String givenUoM;
        String expectedUoM;
        // test no conversion needed case because both given and expected UoM are null
        givenUoM = null;
        expectedUoM = null;
        DataEvent convertedDataEvent = fdhAssetEngUnitsConverter.convert(dataEvent, givenUoM, expectedUoM);
        Assert.assertEquals(dataEvent, convertedDataEvent);
        // test no conversion needed case because expected UoM is not specified
        givenUoM = "gal";
        expectedUoM = null;
        convertedDataEvent = fdhAssetEngUnitsConverter.convert(dataEvent, givenUoM, expectedUoM);
        Assert.assertEquals(dataEvent, convertedDataEvent);
        // test no conversion needed case because given and expected UoM are the same
        givenUoM = "L";
        expectedUoM = "L";
        convertedDataEvent = fdhAssetEngUnitsConverter.convert(dataEvent, givenUoM, expectedUoM);
        Assert.assertEquals(dataEvent, convertedDataEvent);
        // expect exception if expected UoM is not null and given UoM is null
        try
        {
            givenUoM = null;
            expectedUoM = "m";
            convertedDataEvent = fdhAssetEngUnitsConverter.convert(dataEvent, givenUoM, expectedUoM);
            Assert.fail("Exception expected for conversion");
        }
        catch (RuntimeException e)
        {
            // expected exception
        }
    }
}
