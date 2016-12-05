package com.ge.predix.solsvc.fdh.handler.custom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;

import org.apache.http.Header;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;

/**
 * 
 * @author predix
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{
        "classpath*:META-INF/spring/fdh-custom-handler-scan-context.xml"
        })
public class CustomHandlerPutFieldDataTest
{

    private static final Logger     log = LoggerFactory.getLogger(CustomHandlerPutFieldDataTest.class);


    /**
     * @throws Exception -
     */
    @BeforeClass
    public static void setUpBeforeClass()
            throws Exception
    {
        //new RestTemplate();
    }

    /**
     * @throws Exception -
     */
    @AfterClass
    public static void tearDownAfterClass()
            throws Exception
    {
        //
    }

    @Autowired
    private SampleHandler sampleHandler;

    /**
     * @throws Exception -
     */
    @SuppressWarnings(
    {
    })
    @Before
    public void setUp()
            throws Exception
    {
       //
    }

    /**
     * @throws Exception -
     */
    @After
    public void tearDown()
            throws Exception
    {
        //
    }

    
    /**
     * @throws IOException -
     * @throws IllegalStateException - 
     */
    @SuppressWarnings("nls")
    @Test
    public void testGetFieldData()
            throws IllegalStateException, IOException
    {
        log.debug("================================");

        PutFieldDataRequest request = TestData.putFieldDataRequestWithFieldFilter();
        FieldIdentifier field = request.getPutFieldDataCriteria().get(0).getFieldData().getField().get(0).getFieldIdentifier();
        field.setSource("/handler/sampleHandler");
        
        String url = "http://localhost:" + "9092" + "/services/fdhrouter/fielddatahandler/getfielddata";
        log.debug("URL = " + url);

        List<Header> headers = new ArrayList<Header>();
        Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
        PutFieldDataResult result = this.sampleHandler.putData(request, modelLookupMap , headers, HttpMethod.POST);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.getErrorEvent().get(0).contains("SampleHandler"));
        
    }
    
}
