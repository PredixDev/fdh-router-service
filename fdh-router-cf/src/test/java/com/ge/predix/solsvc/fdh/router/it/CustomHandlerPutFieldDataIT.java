package com.ge.predix.solsvc.fdh.router.it;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
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
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.ge.dsp.pm.ext.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.router.boot.FdhRouterApplication;
import com.ge.predix.solsvc.fdh.router.util.TestData;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * 
 * @author predix
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FdhRouterApplication.class)
@WebAppConfiguration
@IntegrationTest({"server.port=9092"})
public class CustomHandlerPutFieldDataIT
{

    private static final Logger     log = LoggerFactory.getLogger(CustomHandlerPutFieldDataIT.class);

    @Autowired
    private RestClient                    restClient;

    @Autowired
    private JsonMapper jsonMapper;

    /**
     * @throws Exception -
     */
    @BeforeClass
    public static void setUpBeforeClass()
            throws Exception
    {
        //
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
     * @throws MalformedURLException -
     */
    @SuppressWarnings("nls")
    @Test
    public void testPutFieldData()
            throws MalformedURLException
    {
        log.debug("================================");

        PutFieldDataRequest request = TestData.putFieldDataRequest();
        FieldIdentifier field = request.getPutFieldDataCriteria().get(0).getFieldData().getField().getFieldIdentifier();
        field.setSource("/handler/sampleHandler");

        String url = "http://localhost:" + "9092" + "/services/fdhrouter/fielddatahandler/putfielddata";
        log.debug("URL = " + url);

        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Content-Type", "application/json"));
        log.debug("REQUEST: Input json to get field data = " + this.jsonMapper.toJson(request));
        
        HttpResponse response = this.restClient.post(url, this.jsonMapper.toJson(request), headers);
        log.debug("RESPONSE: Response from Put Field Data  = " + response);
        String responseAsString = this.restClient.getResponse(response);
        log.debug("RESPONSE: Response from Put Field Data  = " + responseAsString);

        Assert.assertNotNull(response);
        Assert.assertTrue(response.toString().contains("HTTP/1.1 200 OK"));
        Assert.assertTrue(responseAsString.contains("SampleHandler"));

    }
}
