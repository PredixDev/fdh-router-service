package com.ge.predix.solsvc.fdh.handler.eventhub;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.helpers.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.solsvc.ext.util.JsonMapper;

/**
 * 
 * 
 * @author 212421693
 */

@RunWith(SpringJUnit4ClassRunner.class)
@Import(value={ EventHubPutFieldDataHandler.class})
@ContextConfiguration(locations =
{
        "classpath*:META-INF/spring/fdh-eventhub-handler-scan-context.xml",
        "classpath*:META-INF/spring/ext-util-scan-context.xml",
        "classpath*:META-INF/spring/TEST-fdh-eventhub-handler-properties-context.xml"
})
@ActiveProfiles({ "eventhub-websocket"})
@Ignore
public class EventHubHandlerPutWebsocketIT {

	private static Logger log = LoggerFactory.getLogger(EventHubHandlerPutWebsocketIT.class);

	@Autowired
	private EventHubPutFieldDataHandler putHandler;
	
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
		PutFieldDataResult result = this.putHandler.putData(request, modelLookupMap, null, null);
		log.info(this.mapper.toJson(result));
		Assert.assertNotNull(result);
		Assert.assertTrue(result.getErrorEvent().size()==0);
	}
	
	@SuppressWarnings("nls")
	private String createTestData() {
		String testdataStr = null;

		try {
			testdataStr = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("PutFieldDataTS.json"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return testdataStr;
	}

}
