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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.fielddata.PredixString;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.entity.util.map.AttributeMap;
import com.ge.predix.entity.util.map.Entry;
import com.ge.predix.eventhub.Ack;
import com.ge.predix.eventhub.AckStatus;
import com.ge.predix.eventhub.EventHubClientException;
import com.ge.predix.eventhub.client.Client;
import com.ge.predix.eventhub.configuration.EventHubConfiguration;
import com.ge.predix.eventhub.configuration.PublishSyncConfiguration;
import com.ge.predix.solsvc.fdh.handler.AsyncPutRequestHandler;

/**
 * PutFieldDataProcessor processes PutFieldDataRequest - Puts data in the time
 * series handlers -
 * 
 * @author predix
 */
@SuppressWarnings("nls")
@Component(value = "eventHubPutFieldDataHandler")
@ImportResource({
		"classpath*:META-INF/spring/predix-websocket-client-scan-context.xml",
})
@Profile("eventhub")
public class EventHubPutDataHandler extends AsyncPutRequestHandler {
	private static final Logger log = LoggerFactory.getLogger(EventHubPutDataHandler.class);

	private Client synchClient;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ge.fdh.asset.processor.IPutFieldDataProcessor#processRequest(com.
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
			
			List<PutFieldDataCriteria> fieldDataCriteria = request.getPutFieldDataCriteria();
			for (PutFieldDataCriteria putFieldDataCriteria : fieldDataCriteria) {
				if (putFieldDataCriteria.getFieldData().getData() instanceof PredixString) {
					createClient(request.getExternalAttributeMap());
					if (synchClient != null) {
						String body = ((PredixString)putFieldDataCriteria.getFieldData().getData()).getString();
						synchClient.addMessage(uuidId, body, null);
						List<Ack> messages = synchClient.flush(); 
						for (Ack ack:messages) {
							if (ack.getStatusCode() != AckStatus.ACCEPTED) {
								log.error("Data ingestion failed");
							}else{
								log.info("Data ingested successfully");
							}
						}
					}else{
						putFieldDataResult.getErrorEvent().add("EventHub Client not created. Please check the configuration");
						break;
					}
				}
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
		return putFieldDataResult;
	}
	
	public void createClient(AttributeMap attributeMap) throws UnsupportedEncodingException {
	    // make the async and sync clients
	    try {
	    	String[] client = null;
	    	String eventHubServiceName = "";
	    	String eventHubUAAServiceName = "";
	    	String eventHubUAAURL = "";
	    	String eventHubZoneId = "";
	    	String eventHubHostName = "";
	    	for (Entry entry : attributeMap.getEntry()) {
	    		if ("CLIENT_ID".equalsIgnoreCase(entry.getKey().toString())) {
	    			client = entry.getValue().toString().split(":");
	    		}
	    		if ("EVENTHUB_SERVICE_NAME".equalsIgnoreCase(entry.getKey().toString())) {
	    			eventHubServiceName = entry.getValue().toString();
	    		}
	    		if ("EVENTHUB_UAA_SERVICE_NAME".equalsIgnoreCase(entry.getKey().toString())) {
	    			eventHubUAAServiceName = entry.getValue().toString();
	    		}
	    		if ("EVENTHUB_HOST_NAME".equalsIgnoreCase(entry.getKey().toString())) {
	    			eventHubHostName = entry.getValue().toString();
	    		}
	    		
	    		if ("EVENTHUB_ZONE_ID".equalsIgnoreCase(entry.getKey().toString())) {
	    			eventHubZoneId = entry.getValue().toString();
	    		}
	    		
	    		if ("EVENTHUB_UAA_URL".equalsIgnoreCase(entry.getKey().toString())) {
	    			eventHubUAAURL = entry.getValue().toString();
	    		}
	    	}
	    	EventHubConfiguration eventHubConfiguration = null;
	    	if ((eventHubServiceName != null && !"".equals(eventHubServiceName) || 
	    			(eventHubUAAServiceName != null && !"".equals(eventHubUAAServiceName)))) {
	    		eventHubConfiguration = new EventHubConfiguration.Builder()
	    		    .fromEnvironmentVariables(eventHubServiceName, eventHubUAAServiceName)
	    		    .clientID(client[0])
	    		    .clientSecret(client[1])
	    		    .publishConfiguration(new PublishSyncConfiguration.Builder().build())
	    		    .automaticTokenRenew(true)
	    		    .build();
	    	}else {
	    		eventHubConfiguration = new EventHubConfiguration.Builder()
	    		    .host(eventHubHostName)
	    		    .clientID(client[0])
	    		    .clientSecret(client[1])
	    		    .zoneID(eventHubZoneId)
	    		    .authURL(eventHubUAAURL)
	    		    .publishConfiguration(new PublishSyncConfiguration.Builder().build())
	    		    .automaticTokenRenew(true)
	    		    .build();
	    	}
	    	
	    	synchClient  = new Client(eventHubConfiguration);
	    	
	    } catch (EventHubClientException.InvalidConfigurationException e) {
	    	log.error("*** Could not make client ***",e);
	    	throw new RuntimeException("Could not make event hub client");
	    }
	  }

}
