package com.ge.predix.solsvc.fdh.handler.eventhub;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.helpers.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
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
import com.ge.predix.eventhub.EventHubClientException;
import com.ge.predix.eventhub.Message;
import com.ge.predix.eventhub.client.Client;
import com.ge.predix.eventhub.configuration.EventHubConfiguration;
import com.ge.predix.eventhub.configuration.SubscribeConfiguration;
import com.ge.predix.solsvc.ext.util.JsonMapper;

/**
 * 
 * 
 * @author 212421693
 */

@RunWith(SpringJUnit4ClassRunner.class)
// @SpringApplicationConfiguration(classes = { TimeseriesPutDataHandler.class})
@ContextConfiguration(locations = { "classpath*:META-INF/spring/fdh-eventhub-handler-scan-context.xml",
		"classpath*:META-INF/spring/ext-util-scan-context.xml",
		"classpath*:META-INF/spring/TEST-fdh-eventhub-handler-properties-context.xml" })
@ActiveProfiles({ "eventhub" })
public class EventHubHandlerPutGRPCIT {

	/**
	 * 
	 */
	static Logger log = LoggerFactory.getLogger(EventHubHandlerPutGRPCIT.class);

	@Autowired
	private EventHubPutFieldDataHandler putHandler;

	@Autowired
	private JsonMapper mapper;

	@Autowired
	private EventHubPublishConfig eventHubConfig;

	private Client eventHubClient;

	/**
	 * 
	 */
    @Before
    public void setup() {
    	SubscribeConfiguration.Builder subscribeConfigBuilder = new SubscribeConfiguration.Builder();
		if (this.eventHubConfig.getPublishTopic() != null
				&& !"".equals(this.eventHubConfig.getPublishTopic())) { //$NON-NLS-1$
			//subscribeConfigBuilder.topic(this.eventHubConfig.getPublishTopic());
		}
    	String[] client = this.eventHubConfig.getOauthClientId().split(":"); //$NON-NLS-1$
    	try {
			EventHubConfiguration eventHubConfiguration = new EventHubConfiguration.Builder()
					.host(this.eventHubConfig.getEventHubHostName()).port(this.eventHubConfig.getEventHubPort())
					.clientID(client[0]).clientSecret(client[1]).zoneID(this.eventHubConfig.getZoneId())
					.authURL(this.eventHubConfig.getOauthIssuerId())
					.subscribeConfiguration(subscribeConfigBuilder.build()).automaticTokenRenew(true).build();
			this.eventHubClient = new Client(eventHubConfiguration);
			SubscribeCallback callback = new SubscribeCallback();
			this.eventHubClient.subscribe(callback);
		} catch (EventHubClientException e) {
			throw new RuntimeException("exception with creating event hub client",e); //$NON-NLS-1$
		}
    }

	/**
	 *  -
	 */
	@Test
	@Ignore("because it says  VCAP_SERVICES environment variable is not set")
	public void testInjection() {
		PutFieldDataRequest request = this.mapper.fromJson(createTestData(), PutFieldDataRequest.class);
		log.debug("request=" + this.mapper.toJson(request)); //$NON-NLS-1$

		Map<Integer, Object> modelLookupMap = new HashMap<Integer, Object>();
		// AttributeMap map = new AttributeMap();
		// map.setEntry(new ArrayList<Entry>());
		// request.setExternalAttributeMap(map);

		PutFieldDataResult result = this.putHandler.putData(request, modelLookupMap, null, null);
		log.info("Result..... : "+this.mapper.toJson(result)); //$NON-NLS-1$
		Assert.assertNotNull(result);
		Assert.assertTrue(result.getErrorEvent().size() == 0);
	}

	private String createTestData() {
		String testdataStr = null;

		try {
			testdataStr = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("PutFieldDataTS.json")); //$NON-NLS-1$
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return testdataStr;
	}

	/**
	 * 
	 * @author 212546387 -
	 */
	public static class SubscribeCallback implements Client.SubscribeCallback {
		
		@Override
		public void onMessage(Message message) {
			String msg = message.getBody().toStringUtf8();
			log.info("Message recieved : "+msg); //$NON-NLS-1$
			Assert.assertTrue(msg != null && msg.length() > 0);
		}

		@Override
		public void onFailure(Throwable throwable) {
			Assert.fail("Test failed : "+throwable.getMessage()); //$NON-NLS-1$
		}

	}
}
