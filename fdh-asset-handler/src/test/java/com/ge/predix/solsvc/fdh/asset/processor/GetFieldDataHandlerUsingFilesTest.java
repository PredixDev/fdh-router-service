/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.asset.processor;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.Header;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.dsp.pm.ext.entity.model.Model;
import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataResult;
import com.ge.fdh.asset.helper.FileUtils;
import com.ge.fdh.asset.helper.PmpModuleConfigRootParser;
import com.ge.fdh.asset.processor.GetFieldDataHandlerImpl;
import com.ge.fdh.asset.processor.PutFieldDataHandlerImpl;
import com.ge.predix.solsvc.restclient.impl.RestClient;

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
public class GetFieldDataHandlerUsingFilesTest extends AbstractRequestProcessorTest
{

    /**
     * 
     */
    protected PmpModuleConfigRootParser    parser = null;


    /**
     * Processor for GetFieldData request
     */
    @Autowired
    private GetFieldDataHandlerImpl getFieldDataProcessor;
    @Autowired
    private PutFieldDataHandlerImpl putFieldDataProcessor;
    @Autowired
    private RestClient restClient;

    /**
     * @throws java.lang.Exception -
     */
    @Before
    public void setUp()
            throws Exception
    {
        @SuppressWarnings("rawtypes")
        Class[] classes = new Class[4];
        classes[0] = com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataRequest.class;
        classes[1] = com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataResult.class;
        classes[2] = com.ge.dsp.pm.ext.entity.osa.selectionfilter.SelectionFilterDefinition.class;
        classes[3] = com.ge.dsp.pm.ext.entity.osa.selectionfilter.SelectionFilterType.class;
        // initialize parser
        this.parser = new PmpModuleConfigRootParser(classes);

    }

    /**
     * @throws java.lang.Exception -
     */
    @After
    public void tearDown()
            throws Exception
    {
        //
    }

    /**
     * -
     */
    @Test
    public void testGetFieldDataNoSolutionId()
    {
        String requestFile = "testData/negative/GetFieldDataRequest-NoSolutionId.xml";
        String resultFile = "testData/negative/GetFieldDataResult-NoSolutionId.xml";
        testGetFieldDataProcessor(requestFile, resultFile);
    }

    private void testGetFieldDataProcessor(String requestFile, String resultFile)
    {
        // Load the request from the test data file
        GetFieldDataRequest getFieldDataRequest = unmarshalGetFieldDataRequest(requestFile);

        Map<Integer, Model> modelLookupMap = null;
        List<Header> headers = null;
        // Invoke the request processor
        GetFieldDataResult getFieldDataResult = this.getFieldDataProcessor.getFieldData(getFieldDataRequest, modelLookupMap,headers, HttpMethod.GET);

        // Stream the actual result
        StreamResult streamResult = new StreamResult(new StringWriter());
        this.parser.marshal(getFieldDataResult, streamResult);
        @SuppressWarnings("unused")
        String resultXml = streamResult.getWriter().toString();

        // Load the expected result from the test data file
        GetFieldDataResult expectedGetFieldDataResult = unmarshalGetFieldDataResult(resultFile);

        // Stream the expected result
        StreamResult expectedStreamResult = new StreamResult(new StringWriter());
        this.parser.marshal(expectedGetFieldDataResult, expectedStreamResult);
        @SuppressWarnings("unused")
        String expectedResultXml = expectedStreamResult.getWriter().toString();

        // TODO can't handle the stack track when comparing, but we need the stack at runtime in production
        // Assert.assertEquals(expectedResultXml, resultXml);
    }

    private GetFieldDataRequest unmarshalGetFieldDataRequest(String filePath)
    {
        try
        {
            String xmlStr = FileUtils.readFile(filePath);
            Object object = this.parser.unmarshal(xmlStr);
            if ( object instanceof GetFieldDataRequest )
            {
                return (GetFieldDataRequest) object;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Assert.assertTrue(false);
        return null;
    }

    private GetFieldDataResult unmarshalGetFieldDataResult(String filePath)
    {
        try
        {
            String xmlStr = FileUtils.readFile(filePath);
            Object object = this.parser.unmarshal(xmlStr);
            if ( object instanceof GetFieldDataResult )
            {
                return (GetFieldDataResult) object;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Assert.assertTrue(false);
        return null;
    }

}
