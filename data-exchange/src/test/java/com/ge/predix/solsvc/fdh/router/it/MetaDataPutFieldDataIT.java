package com.ge.predix.solsvc.fdh.router.it;

import java.io.IOException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.handler.asset.AssetGetFieldDataHandlerImpl;
import com.ge.predix.solsvc.fdh.router.boot.FdhRouterApplication;
import com.ge.predix.solsvc.fdh.router.util.TestData;
import com.ge.predix.solsvc.restclient.config.IOauthRestConfig;
import com.ge.predix.solsvc.restclient.impl.RestClient;


/**
 * 
 * @author predix
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { FdhRouterApplication.class, AssetGetFieldDataHandlerImpl.class })
@WebAppConfiguration
@IntegrationTest({"server.port=9092"})
public class MetaDataPutFieldDataIT
{

    @SuppressWarnings("unused")
    private static final Logger     log = LoggerFactory.getLogger(MetaDataPutFieldDataIT.class);

    @Autowired
    private RestClient                    restClient;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    @Qualifier("defaultOauthRestConfig")
	private IOauthRestConfig restConfig;


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
     *  -
     * @throws IOException -
     */
    @SuppressWarnings("nls")
    @Test
    public void testPutMetaDataFieldDataRequest() throws IOException
    {
        PutFieldDataRequest request = TestData.putMetaDataFieldDataRequest();

        String url = "http://localhost:" + "9092" + "/services/fdhrouter/fielddatahandler/putfielddata";

        List<Header> headers = this.restClient.getSecureTokenForClientId();
        headers.add(new BasicHeader("Content-Type", "application/json"));
        CloseableHttpResponse response = null;
        try {
	        response = this.restClient.post(url, this.jsonMapper.toJson(request), headers, this.restConfig.getDefaultConnectionTimeout(), this.restConfig.getDefaultSocketTimeout());
	        
	        Assert.assertNotNull(response);
	        Assert.assertTrue(response.toString().contains("HTTP/1.1 200 OK"));
	        String body = EntityUtils.toString(response.getEntity());
			Assert.assertTrue(body.contains("errorEvent\":[]"));
        } finally {
        	if(response!=null)
				response.close();
	    }

    }
}
