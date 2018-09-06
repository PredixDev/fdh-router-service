package com.ge.predix.solsvc.fdh.handler.timeseries;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.helpers.IOUtils;
import org.apache.http.Header;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.timeseries.bootstrap.client.TimeseriesClient;
import com.ge.predix.solsvc.timeseries.bootstrap.config.DefaultTimeseriesConfig;

/**
 * 
 * 
 * @author 212421693
 */

@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = { TimeseriesPutDataHandler.class})
@ContextConfiguration(locations =
{
//		"classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml",
        "classpath*:META-INF/spring/fdh-timeseries-handler-scan-context.xml",
        "classpath*:META-INF/spring/ext-util-scan-context.xml",
        "classpath*:META-INF/spring/TEST-fdh-timeseries-handler-properties-context.xml"
})
@ActiveProfiles({"asset", "timeseries"})
public class TimeseriesHandlerPutIT {

	private static Logger log = LoggerFactory.getLogger(TimeseriesHandlerPutIT.class);

	@Autowired
	private TimeseriesPutDataHandler putHandler;

	@Autowired
	private TimeseriesClient timeseriesClient;

	@Autowired
	@Qualifier("defaultTimeseriesConfig")
	private DefaultTimeseriesConfig timseriesConfig;
	
    @Autowired
    private JsonMapper               mapper;

	/**
	 * 
	 */
	@Test
	public void testInjection() {
		PutFieldDataRequest request = this.mapper.fromJson(createTestData(), PutFieldDataRequest.class);

		
		log.debug("request=" + this.mapper.toJson(request)); //$NON-NLS-1$

		Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();
		List<Header> headers = this.timeseriesClient.getTimeseriesHeaders();
		this.timeseriesClient.addZoneIdToHeaders(headers);
		String httpMethod = null;
		PutFieldDataResult result = this.putHandler.putData(request, modelLookupMap, headers, httpMethod);
		
		Assert.assertNotNull(result);
		Assert.assertTrue(result.getErrorEvent().size()==0);
	}
	
	@SuppressWarnings("nls")
	private String createTestData() {
		String testdataStr = null;

		try {
			testdataStr = IOUtils.toString(getClass()
					.getClassLoader().getResourceAsStream(
							"PutFieldDataTS.json"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return testdataStr;
	}

}
