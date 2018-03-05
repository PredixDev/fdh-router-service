package com.ge.predix.solsvc.fdh.handler.edgemanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.edgemanager.Device;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.solsvc.ext.util.JsonMapper;

/**
 * 
 * @author predix
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/dx-edgemanager-handler-scan-context.xml",
		"classpath*:META-INF/spring/ext-util-scan-context.xml",
		"classpath*:META-INF/spring/TEST-dx-edgemanager-handler-properties-context.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EdgeManagerIT {

	private static final Logger log = LoggerFactory.getLogger(EdgeManagerIT.class);

	@Autowired
	private EdgeManagerHandler edgeManagerHandler;

	@Autowired
	private JsonMapper mapper;

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
	@Test
	public void testACreatetFieldData() {
		log.debug("===========Create Test================="); //$NON-NLS-1$
		try {
			PutFieldDataRequest request = this.mapper.fromJson(createTestData("EdgeManagerPutDeviceRequest.json"), //$NON-NLS-1$
					PutFieldDataRequest.class);
			List<Header> headers = new ArrayList<Header>();
			Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();
			PutFieldDataResult result = this.edgeManagerHandler.putData(request, modelLookupMap, headers,
					HttpMethod.POST);
			log.info("Result : " + this.mapper.toJson(result)); //$NON-NLS-1$
			Assert.assertNotNull(result);
			Assert.assertTrue(result.getErrorEvent().size() == 0);
			// Assert.assertTrue(result.getErrorEvent().get(0).contains("SampleHandler"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String createTestData(String testFileName) {
		String testdataStr = ""; //$NON-NLS-1$
		try {
			testdataStr = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(testFileName));
		} catch (IOException e) {
			throw new RuntimeException("Exception when reading file "+testFileName,e); //$NON-NLS-1$
		}
		//log.info(testdataStr);
		return testdataStr;
	}

	/**
	 * @throws IllegalStateException
	 *             -
	 * @throws IOException
	 *             -
	 */
	@Test
	public void testBGetFieldData() throws IllegalStateException, IOException {
		log.debug("===========Get Device Test================="); //$NON-NLS-1$

		GetFieldDataRequest request = this.mapper.fromJson(createTestData("EdgeManagerGetDeviceRequest.json"), //$NON-NLS-1$
				GetFieldDataRequest.class);
		String deviceId = request.getFieldDataCriteria().get(0).getFieldSelection().get(0).getFieldIdentifier().getId().toString();
		// String url = "http://localhost:" + "9092" +
		// "/services/fdhrouter/fielddatahandler/getfielddata"; //$NON-NLS-1$
		// //$NON-NLS-2$ //$NON-NLS-3$
		// log.debug("URL = " + url); //$NON-NLS-1$

		List<Header> headers = new ArrayList<Header>();
		Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();
		GetFieldDataResult result = this.edgeManagerHandler.getData(request, modelLookupMap, headers);
		log.info("Result : " + this.mapper.toJson(result)); //$NON-NLS-1$
		Assert.assertNotNull(result);
		Assert.assertTrue(result.getErrorEvent().size() == 0);
		Device d = (Device)result.getFieldData().get(0).getData();
		Assert.assertEquals(deviceId, d.getDeviceId());
		
		// Assert.assertTrue(((DAString) ((OsaData)
		// result.getFieldData().get(0).getData()).getDataEvent()).getValue().contains("SampleHandler"));
		// //$NON-NLS-1$

	}

	/**
	 * @throws IllegalStateException
	 *             -
	 * @throws IOException
	 *             -
	 */
	@Test
	public void testCDeleteFieldData() throws IllegalStateException, IOException {
		log.debug("===========Delete Test================="); //$NON-NLS-1$
		try {
			PutFieldDataRequest request = this.mapper.fromJson(createTestData("EdgeManagerDeleteDeviceRequest.json"), //$NON-NLS-1$
					PutFieldDataRequest.class);

			List<Header> headers = new ArrayList<Header>();
			Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();
			PutFieldDataResult result = this.edgeManagerHandler.putData(request, modelLookupMap, headers,
					HttpMethod.DELETE);
			log.info("Result : " + this.mapper.toJson(result)); //$NON-NLS-1$
			Assert.assertNotNull(result);
			Assert.assertTrue(result.getErrorEvent().size() == 0);
			// Assert.assertTrue(result.getErrorEvent().get(0).contains("SampleHandler"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
