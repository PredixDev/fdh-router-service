package com.ge.predix.solsvc.fdh.handler.rabbitmq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.helpers.IOUtils;
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

import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.solsvc.ext.util.JsonMapper;

/**
 * 
 * @author predix
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/TEST-fdh-rabbitmq-handler-scan-context.xml",
		"classpath:/META-INF/spring/ext-util-scan-context.xml" })
@ActiveProfiles({ "rabbitmq" })
public class RabbitMQHandlerPutFieldDataTestHarness {

	private static final Logger log = LoggerFactory.getLogger(RabbitMQHandlerPutFieldDataTestHarness.class);

	@Autowired
	private JsonMapper jsonMapper;

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
	 * This is the preferred way of using this. Use a DataMap, it looks nicer.
	 * 
	 * @throws IOException
	 *             -
	 * @throws IllegalStateException
	 *             -
	 */
	@SuppressWarnings("nls")
	@Test
	public void testPutFieldDataAsDataMapList() throws IllegalStateException, IOException {
		log.debug("================================");

		PutFieldDataRequest request = this.jsonMapper.fromJson(createFieldChangedEventAsDataMapList(),
				PutFieldDataRequest.class);

		log.debug("================================" + this.jsonMapper.toJson(request));

		List<Header> headers = new ArrayList<Header>();
		Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();
		PutFieldDataResult result = this.rabbitMqHandler.putData(request, modelLookupMap, headers, null);
		Assert.assertNotNull(result);
		//Assert.assertTrue(result.getErrorEvent().get(0).contains("SampleHandler"));

	}

	/**
	 * This is NOT the preferred way of using this. Use a DataMap instead, it
	 * looks nicer.
	 * 
	 * @throws IOException
	 *             -
	 * @throws IllegalStateException
	 *             -
	 */
	@SuppressWarnings("nls")
	@Test
	public void testPutFieldDataAsPredixString() throws IllegalStateException, IOException {
		log.debug("================================");

		PutFieldDataRequest request = this.jsonMapper.fromJson(createFieldChangedEventAsPredixString(),
				PutFieldDataRequest.class);

		log.debug("================================" + this.jsonMapper.toJson(request));

		List<Header> headers = new ArrayList<Header>();
		Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();
		PutFieldDataResult result = this.rabbitMqHandler.putData(request, modelLookupMap, headers, null);
		Assert.assertNotNull(result);
		//Assert.assertTrue(result.getErrorEvent().get(0).contains("SampleHandler"));

	}

	@SuppressWarnings("nls")
	private String createFieldChangedEventAsPredixString() {
		String fieldChangedEventJsonString = null;

		try {
			fieldChangedEventJsonString = IOUtils.toString(getClass().getClassLoader()
					.getResourceAsStream("PutFieldDataWithFieldChangedEventAsPredixString.json"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fieldChangedEventJsonString;
	}

	@SuppressWarnings("nls")
	private String createFieldChangedEventAsDataMapList() {
		String fieldChangedEventJsonString = null;

		try {
			fieldChangedEventJsonString = IOUtils.toString(getClass().getClassLoader()
					.getResourceAsStream("PutFieldDataWithFieldChangedEventAsDataMapList.json"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fieldChangedEventJsonString;
	}

}
