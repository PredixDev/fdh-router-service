package com.ge.predix.solsvc.fdh.handler.rabbitmq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;

/**
 * 
 * @author predix
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/TEST-fdh-rabbitmq-handler-scan-context.xml" })
@ActiveProfiles({ "rabbitmq" })
public class RabbitMQHandlerPutFieldDataTestHarness {

	private static final Logger log = LoggerFactory.getLogger(RabbitMQHandlerPutFieldDataTestHarness.class);

	/**
	 * @throws Exception
	 *             -
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//
	}

	/**
	 * @throws Exception
	 *             -
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//
	}

	@Autowired
	private RabbitMQHandler rabbitMqHandler;

	/**
	 * @throws Exception
	 *             -
	 */
	@SuppressWarnings({})
	@Before
	public void setUp() throws Exception {
		//
	}

	/**
	 * @throws Exception
	 *             -
	 */
	@After
	public void tearDown() throws Exception {
		//
	}

	/**
	 * @throws IOException
	 *             -
	 * @throws IllegalStateException
	 *             -
	 */
	@SuppressWarnings("nls")
	@Test
	public void testPutFieldData() throws IllegalStateException, IOException {
		log.debug("================================");
		
		PutFieldDataRequest request = TestData.putFieldDataRequest();
		List<Header> headers = new ArrayList<Header>();
		Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
		PutFieldDataResult result = this.rabbitMqHandler.putData(request, modelLookupMap, headers, null);
		Assert.assertNotNull(result);
		Assert.assertTrue(result.getErrorEvent().get(0).contains("SampleHandler"));

	}

}
