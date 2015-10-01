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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mimosa.osacbmv3_3.DMInt;
import org.mimosa.osacbmv3_3.DataEvent;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.dsp.pm.ext.entity.field.Field;
import com.ge.dsp.pm.ext.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.dsp.pm.ext.entity.fielddata.FieldData;
import com.ge.dsp.pm.ext.entity.fielddata.OsaData;
import com.ge.dsp.pm.ext.entity.fieldidentifiervalue.FieldIdentifierValue;
import com.ge.dsp.pm.ext.entity.model.Model;
import com.ge.dsp.pm.ext.entity.selectionfilter.FieldSelectionFilter;
import com.ge.dsp.pm.ext.entity.solution.identifier.solutionidentifier.SolutionIdentifier;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataCriteria;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataResult;
import com.ge.fdh.asset.helper.FileUtils;
import com.ge.fdh.asset.helper.PmpModuleConfigRootParser;
import com.ge.fdh.asset.processor.PutFieldDataHandlerImpl;
import com.ge.fdh.asset.validator.PutFieldDataRequestValidator;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.restclient.config.OauthRestConfig;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * 
 * @author 212397779
 */
@SuppressWarnings(
{
        "nls"
})
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{
        "classpath*:META-INF/spring/MOCK-fdh-asset-handler-context.xml",
        "classpath*:META-INF/spring/ext-util-scan-context.xml",
        "classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml",
        "classpath*:META-INF/spring/fdh-asset-handler-scan-context.xml"

})
public class PutFieldDataHandlerUsingFilesTest extends AbstractRequestProcessorTest
{

    /**
     * 
     */
    protected PmpModuleConfigRootParser    parser = null;


    /**
     * Processor for GetFieldData request
     */
    @Autowired
    private PutFieldDataHandlerImpl putFieldDataProcessor;

    @Autowired
    private OauthRestConfig                        restConfig;

    @Autowired
    private RestClient                        restClient;


    @Autowired
    private PutFieldDataRequestValidator putFieldDataRequestValidator;
    
    @Autowired
    private JsonMapper jsonMapper;


    /**
     * @throws java.lang.Exception -
     */
    @Before
    public void setUp()
            throws Exception
    {
        @SuppressWarnings("rawtypes")
        Class[] classes = new Class[2];
        classes[0] = com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataRequest.class;
        classes[1] = com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataResult.class;

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
     *  -
     */
    @Test
    public void testGettersAndSetters()
    {
        Assert.assertNotNull("putFieldDataRequestValidator is null", this.putFieldDataRequestValidator);
    }

    /**
     *  -
     */
    @Test
    public void testPutFieldData()
    {
        String requestFile = "testData/PutFieldDataRequest.json";
        String resultFile = "testData/PutFieldDataResult.json";
        testPutFieldDataProcessor(requestFile, resultFile);
    }

    /**
     *  -
     */
    @Test
    public void testPutFieldDataNoSolutionId()
    {
        String requestFile = "testData/negative/PutFieldDataRequest-NoSolutionId.json";
        String resultFile = "testData/negative/PutFieldDataResult-NoSolutionId.json";
        testPutFieldDataProcessor(requestFile, resultFile);
    }

    /**
     *  -
     */
    @Test
    public void testPutFieldDataNoFieldData()
    {
        String requestFile = "testData/negative/PutFieldDataRequest-NoFieldData.json";
        String resultFile = "testData/negative/PutFieldDataResult-NoFieldData.json";
        testPutFieldDataProcessor(requestFile, resultFile);
    }

    private void testPutFieldDataProcessor(String requestFile, String resultFile)
    {
        ProtocolVersion proto = new ProtocolVersion("HTTP", 1, 1);
        BasicStatusLine line = new BasicStatusLine(proto, 200, "test reason");
        HttpResponse response = new BasicHttpResponse(line);
        Mockito.when(this.restClient.put(Matchers.anyString(), Matchers.anyString(), Matchers.anyListOf(Header.class)))
                .thenReturn(response);

        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
        putFieldDataRequest.setCorrelationId("string");
        SolutionIdentifier solutionIdentifier = new SolutionIdentifier();
        solutionIdentifier.setId("soid");
        putFieldDataRequest.setSolutionIdentifier(solutionIdentifier);
        FieldSelectionFilter fieldSelectionFilter = new FieldSelectionFilter();
        FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
        fieldIdentifierValue.setFieldIdentifier(new FieldIdentifier());
        fieldIdentifierValue.getFieldIdentifier().setId("test");
        fieldIdentifierValue.setValue("test");
        fieldSelectionFilter.getFieldIdentifierValue().add(fieldIdentifierValue);
        FieldData fieldData = new FieldData();
        Field field = new Field();
        fieldData.setField(field);
        PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();
        fieldDataCriteria.setFieldData(fieldData);
        fieldDataCriteria.setSelectionFilter(fieldSelectionFilter);
        putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria  );
        DataEvent dataEvent = new DMInt();
        OsaData data = new OsaData();
        data.setDataEvent(dataEvent);
        fieldData.setData(data);
        putFieldDataRequest.getPutFieldDataCriteria().get(0).getFieldData().setData(data);
        StreamResult streamResult2 = new StreamResult(new StringWriter());
        this.parser.marshal(putFieldDataRequest, streamResult2);
        @SuppressWarnings("unused")
        String resultXml2 = streamResult2.getWriter().toString();

        // Load the request from the test data file
        if ( requestFile.endsWith("xml") )
            putFieldDataRequest = unmarshalXmlRequest(requestFile);
        else
            putFieldDataRequest = unmarshalJsonRequest(requestFile);
        // FieldIdentifier fieldIdentifier = new FieldIdentifier();
        // fieldIdentifier.setId("id");
        // putFieldDataRequest.getFieldData().get(0).getField().setFieldIdentifier(fieldIdentifier );
        String jsonRequest = this.jsonMapper.toJson(putFieldDataRequest);

        List<Header> headers = new ArrayList<Header>();
        Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
        // Invoke the request processor
        PutFieldDataResult result = this.putFieldDataProcessor.putFieldData(putFieldDataRequest, modelLookupMap , headers, HttpMethod.POST);

        // Stream the actual result
        String jsonResult = this.jsonMapper.toJson(result);

        // StreamResult streamResult = new StreamResult(new StringWriter());
        // this.parser.marshal(result, streamResult);
        // String resultXml = streamResult.getWriter().toString();
        // Object object = this.parser.unmarshal(resultXml);
        // if ( object instanceof PutFieldDataResult )
        // {
        // PutFieldDataResult putFieldDataResult = (PutFieldDataResult) object;
        // if ( putFieldDataResult.getErrorDataEvent() != null )
        // Assert.fail("PutFieldDataResult contains an ErrorDataEvent" +
        // putFieldDataResult);
        // }
        String expectedResult = null;
        String resultString = null;
        if ( resultFile.endsWith("xml") )
        {
            StreamResult streamResult3 = new StreamResult(new StringWriter());
            this.parser.marshal(result, streamResult3);
            resultString = streamResult3.getWriter().toString();
            PutFieldDataResult expectedPutFieldDataResult = unmarshalXmlResult(resultFile);
            streamResult3 = new StreamResult(new StringWriter());
            this.parser.marshal(expectedPutFieldDataResult, streamResult3);
            expectedResult = streamResult3.getWriter().toString();
        }
        else
        {
            resultString = this.jsonMapper.toJson(result);
            PutFieldDataResult expectedPutFieldDataResult = unmarshalJsonResult(resultFile);
            expectedResult = this.jsonMapper.toJson(result);

        }

        // Load the expected result from the test data file

        // Stream the expected result
        // StreamResult expectedStreamResult = new StreamResult(new StringWriter());
        // this.parser.marshal(expectedPutFieldDataResult, expectedStreamResult);
        // String expectedResultXml = expectedStreamResult.getWriter().toString();
        Assert.assertEquals(expectedResult, resultString);

    }

    private PutFieldDataRequest unmarshalJsonRequest(String filePath)
    {
        try
        {
            String fileStr = FileUtils.readFile(filePath);
            PutFieldDataRequest request = this.jsonMapper.fromJson(fileStr, PutFieldDataRequest.class);
            return request;
            // Object object = this.parser.unmarshal(xmlStr);
            // if ( object instanceof PutFieldDataRequest )
            // {
            // return (PutFieldDataRequest) object;
            // }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private PutFieldDataRequest unmarshalXmlRequest(String filePath)
    {
        try
        {
            String fileStr = FileUtils.readFile(filePath);
            Object object = this.parser.unmarshal(fileStr);
            if ( object instanceof PutFieldDataRequest )
            {
                return (PutFieldDataRequest) object;
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

    private PutFieldDataResult unmarshalJsonResult(String filePath)
    {
        try
        {
            String fileStr = FileUtils.readFile(filePath);
            PutFieldDataResult result = this.jsonMapper.fromJson(fileStr, PutFieldDataResult.class);
            return result;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private PutFieldDataResult unmarshalXmlResult(String filePath)
    {
        try
        {
            String fileStr = FileUtils.readFile(filePath);
            Object object = this.parser.unmarshal(fileStr);
            if ( object instanceof PutFieldDataResult )
            {
                return (PutFieldDataResult) object;
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return null;
    }

}
