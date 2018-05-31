/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.handler.eventhub;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.entity.util.map.AttributeMap;
import com.ge.predix.entity.util.map.Entry;
import com.ge.predix.eventhub.Ack;
import com.ge.predix.eventhub.AckStatus;
import com.ge.predix.eventhub.EventHubClientException;
import com.ge.predix.eventhub.client.Client;
import com.ge.predix.eventhub.configuration.EventHubConfiguration;
import com.ge.predix.eventhub.configuration.PublishConfiguration;
import com.ge.predix.eventhub.configuration.PublishConfiguration.PublisherType;
import com.ge.predix.solsvc.ext.util.JsonMapper;

/**
 * PutFieldDataProcessor processes PutFieldDataRequest - Puts data in the time
 * 
 * @author predix -
 */
@SuppressWarnings("nls")
@Component(value = "eventhubGRPCPutFieldDataHandler")
@ImportResource({ "classpath*:META-INF/spring/fdh-event-handler-scan-context.xml" })
@Profile("eventhub")
public class EventHubPutGRPCDataHandler extends EventHubPutFieldDataHandler {
	private static final Logger log = LoggerFactory.getLogger(EventHubPutGRPCDataHandler.class);

	private Client synchClient;

	@Autowired
	private EventHubPublishConfig eventHubPublishConfig;

	@Autowired
	private JsonMapper mapper;

	/**
	 * -
	 */
	@PostConstruct
	public void init() {
		try {
			createEventHubClient();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Exception when creating eventHub Client", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ge.fdh.asset.processor.IPutFieldDataProcessor#processRequest(com.
	 * ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataRequest)
	 */
	@Override
	public PutFieldDataResult putData(PutFieldDataRequest request, Map<Integer, Object> modelLookupMap,
			List<Header> headers, String httpMethod) {
		try {
			UUID idOne = UUID.randomUUID();
			PutFieldDataResult putFieldDataResult = getPutFieldDataResult(idOne);

			String threadName = Thread.currentThread().getName();
			String uuidId = ""; //$NON-NLS-1$
			List<Entry> entries = putFieldDataResult.getExternalAttributeMap().getEntry();
			for (Entry entry : entries) {
				if (entry.getKey().toString().equalsIgnoreCase("UUID")) { //$NON-NLS-1$
					uuidId = entry.getValue().toString();
				}
			}

			log.info("UUID :" + uuidId + "   " + threadName + " has began working."); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

			if (this.synchClient != null) {
				// PubCallback myPubCallback = new PubCallback();
				// synchClient.registerPublishCallback(myPubCallback);

				String body = this.mapper.toJson(request);
				this.synchClient.addMessage(uuidId, body, null);
				List<Ack> messages = this.synchClient.flush();
				log.info("acks : " + messages.size());
				for (Ack ack : messages) {
					if (ack.getStatusCode() != AckStatus.ACCEPTED) {
						log.error("Data ingestion failed");
					} else {
						log.info("Data ingested successfully");
					}
				}
			} else {
				putFieldDataResult.getErrorEvent().add("EventHub Client not created. Please check the configuration");
			}

			return putFieldDataResult;
		} catch (Throwable e) {
			String msg = "unable to process request errorMsg=" + e.getMessage() + " request.correlationId="
					+ request.getCorrelationId() + " request = " + request;
			log.error(msg, e);
			RuntimeException dspPmException = new RuntimeException(msg, e);
			throw dspPmException;
		}
	}

	/**
	 * @param idOne
	 * @return
	 */
	private PutFieldDataResult getPutFieldDataResult(UUID idOne) {
		PutFieldDataResult putFieldDataResult = new PutFieldDataResult();
		AttributeMap attributeMap = new AttributeMap();
		Entry uuidEntru = new Entry();
		uuidEntru.setKey("UUID");
		uuidEntru.setValue(idOne.toString());
		attributeMap.getEntry().add(uuidEntru);
		putFieldDataResult.setExternalAttributeMap(attributeMap);
		putFieldDataResult.setErrorEvent(new ArrayList<String>());
		return putFieldDataResult;
	}

	/**
	 * @throws UnsupportedEncodingException
	 *             -
	 */
	public void createEventHubClient() throws UnsupportedEncodingException {
		// make the async and sync clients
		try {
			log.info("eventHubServiceName : " + this.eventHubPublishConfig.getEventHubServiceName());
			String[] client = this.eventHubPublishConfig.getOauthClientId().split(":");
			log.info("ClientId:Secret : " + this.eventHubPublishConfig.getOauthClientId());
			EventHubConfiguration eventHubConfiguration = new EventHubConfiguration.Builder()
						.host(this.eventHubPublishConfig.getEventHubHostName()).clientID(client[0])
						.clientSecret(client[1]).zoneID(this.eventHubPublishConfig.getZoneId())
						.authURL(this.eventHubPublishConfig.getOauthIssuerId())
						.publishConfiguration(
								new PublishConfiguration.Builder().publisherType(PublisherType.SYNC).build())
						.automaticTokenRenew(true).build();
			
			/*
			 * if ((eventHubPublishConfig.getEventHubServiceName() != null &&
			 * !"".equals(eventHubPublishConfig.getEventHubServiceName()) ||
			 * (eventHubPublishConfig.getEventHubUAAServiceName() != null &&
			 * !"".equals(eventHubPublishConfig.getEventHubUAAServiceName())))) {
			 */
			/*
			 * EventHubConfiguration eventHubConfiguration = new
			 * EventHubConfiguration.Builder()
			 * .fromEnvironmentVariables(this.eventHubPublishConfig.getEventHubServiceName()
			 * , this.eventHubPublishConfig.getEventHubUAAServiceName())
			 * .clientID(client[0]).clientSecret(client[1]) .publishConfiguration(new
			 * PublishConfiguration.Builder()
			 * .publisherType(PublishConfiguration.PublisherType.SYNC)
			 * //.topic(this.eventHubPublishConfig.getPublishTopic())
			 * .timeout(2000).build()) .automaticTokenRenew(true).build();
			 */
			/*
			 * } else { EventHubConfiguration eventHubConfiguration = new
			 * EventHubConfiguration.Builder()
			 * .host(this.eventHubPublishConfig.getEventHubHostName()).port(this.
			 * eventHubPublishConfig.getEventHubPort())
			 * .clientID(client[0]).clientSecret(client[1]).zoneID(this.
			 * eventHubPublishConfig.getZoneId())
			 * .authURL(this.eventHubPublishConfig.getOauthIssuerId())
			 * .publishConfiguration(new PublishAsyncConfiguration.Builder()
			 * //.publisherType(PublishConfiguration.PublisherType.SYNC)
			 * //.topic(this.eventHubPublishConfig.getPublishTopic())
			 * //.timeout(5000).build()) .build()) .automaticTokenRenew(true).build(); }
			 */
			this.synchClient = new Client(eventHubConfiguration);
		} catch (EventHubClientException.InvalidConfigurationException e) {
			log.error("*** Could not make client ***", e);
			throw new RuntimeException("Could not make event hub client", e);
		}
	}

	/**
	 * 
	 * @author predix -
	 */
	static class PubCallback implements Client.PublishCallback {
		private ConcurrentLinkedQueue<Ack> processAckQueue;

		/**
		 * -
		 */
		public PubCallback() {
			this.processAckQueue = new ConcurrentLinkedQueue<Ack>();
		}

		@Override
		public void onAck(List<Ack> list) {
			this.processAckQueue.addAll(list);
		}

		@Override
		public void onFailure(Throwable throwable) {
			System.out.println(throwable.toString());
		}
	}

}
