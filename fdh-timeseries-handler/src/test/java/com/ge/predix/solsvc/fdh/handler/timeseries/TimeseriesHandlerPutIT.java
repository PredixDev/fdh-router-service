package com.ge.predix.solsvc.fdh.handler.timeseries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.junit.Assert;
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
import com.ge.predix.solsvc.restclient.impl.RestClient;
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
		"classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml",
        "classpath*:META-INF/spring/predix-websocket-client-scan-context.xml",
        "classpath*:META-INF/spring/fdh-timeseries-handler-scan-context.xml",
        "classpath*:META-INF/spring/ext-util-scan-context.xml",
        "classpath*:META-INF/spring/TEST-fdh-timeseries-handler-properties-context.xml"
})
@ActiveProfiles({"timeseries"})
public class TimeseriesHandlerPutIT {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(TimeseriesHandlerPutIT.class);

	@Autowired
	private TimeseriesPutDataHandler putHandler;

	@Autowired
	private RestClient restClient;

	@Autowired
	private DefaultTimeseriesConfig timseriesConfig;

	/**
	 * 
	 */
	@Test
	public void testInjection() {
		PutFieldDataRequest request = TestData.getPutFieldDataRequest();

		Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
		List<Header> headers = this.restClient.getSecureTokenForClientId();
		this.restClient.addZoneToHeaders(headers, this.timseriesConfig.getZoneId());
		String httpMethod = null;
		PutFieldDataResult result = this.putHandler.putData(request, modelLookupMap, headers, httpMethod);
		
		Assert.assertNotNull(result);
		Assert.assertTrue(result.getErrorEvent().size()==0);
	}

}
