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
import org.mimosa.osacbmv3_3.DAString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.fielddata.OsaData;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.model.Model;

/**
 * 
 * @author predix
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/TEST-fdh-rabbitmq-handler-scan-context.xml" })
@ActiveProfiles({"rabbitmq"})
public class RabbitMQHandlerGetFieldDataTest {

	private static final Logger log = LoggerFactory.getLogger(RabbitMQHandlerGetFieldDataTest.class);

	/**
	 * @throws Exception
	 *             -
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// new RestTemplate();
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
	@Test(expected=UnsupportedOperationException.class)
	public void testGetFieldData() throws IllegalStateException, IOException {
		log.debug("================================");

		GetFieldDataRequest request = TestData.getFieldDataRequest();
		FieldIdentifier field = request.getFieldDataCriteria().get(0).getFieldSelection().get(0).getFieldIdentifier();
		field.setSource("/handler/sampleHandler");

		String url = "http://localhost:" + "9092" + "/services/fdhrouter/fielddatahandler/getfielddata";
		log.debug("URL = " + url);

		List<Header> headers = new ArrayList<Header>();
		Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
		GetFieldDataResult result = this.rabbitMqHandler.getData(request, modelLookupMap, headers);
		Assert.assertNotNull(result);
		Assert.assertTrue(((DAString) ((OsaData) result.getFieldData().get(0).getData()).getDataEvent()).getValue()
				.contains("SampleHandler"));

	}

}
