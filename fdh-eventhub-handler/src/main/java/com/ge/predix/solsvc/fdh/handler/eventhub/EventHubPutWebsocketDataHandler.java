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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.entity.util.map.AttributeMap;
import com.ge.predix.entity.util.map.Entry;
import com.ge.predix.eventhub.Message;
import com.ge.predix.eventhub.Messages;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.websocket.client.WebSocketClient;
import com.ge.predix.solsvc.websocket.client.WebSocketClientImpl;
import com.ge.predix.solsvc.websocket.config.IWebSocketConfig;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

/**
 * PutFieldDataProcessor processes PutFieldDataRequest - Puts data in the time
 * series handlers -
 * 
 * @author predix
 */
@SuppressWarnings("nls")
@Component(value = "eventhubWebsocketPutFieldDataHandler")
@ImportResource(
{
        "classpath*:META-INF/spring/fdh-event-handler-scan-context.xml"
})
@Profile("eventhub-websocket")
public class EventHubPutWebsocketDataHandler extends EventHubPutFieldDataHandler {
	/**
	 * 
	 */
	static final Logger log = LoggerFactory.getLogger(EventHubPutWebsocketDataHandler.class);

	@Autowired
	private EventHubPublishConfig eventHubPublishConfig;
	
	@Autowired
	private JsonMapper mapper;
	
	@Autowired
	@Qualifier("eventHubPublishConfig")
	private IWebSocketConfig websocketConfig;
	
	@Autowired
	private RestClient restClient;
	
	//@Autowired
	private WebSocketClient eventHubClient = new WebSocketClientImpl();
		
	private WebSocketAdapter messageListener = new WebSocketAdapter() {
		@Override
		public void onTextMessage(WebSocket wsocket, String message) {
			log.info("RECEIVED....from " + wsocket.getURI().toString() + message); // $$
		}

		@Override
		public void onBinaryMessage(WebSocket wsocket, byte[] binary) {
			String str = new String(binary, StandardCharsets.UTF_8);
			log.info("RECEIVED....from " + wsocket.getURI().toString() + str); // $$
		}
	};
	
	/**
	 * -
	 */
	@PostConstruct
	public void init() {
		this.mapper.addSubtype(Messages.class);
		this.mapper.addSubtype(Message.class);
		this.restClient.overrideRestConfig(this.eventHubPublishConfig);
		this.eventHubClient.overrideWebSocketConfig(this.eventHubPublishConfig);
		this.eventHubClient.init(this.restClient, this.restClient.getSecureTokenForClientId(), this.messageListener);
	}
	
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
			
			
			//String data = "Random : "+UUID.randomUUID().toString();
			String data = makeMessage(UUID.randomUUID().toString(), this.mapper.toJson(request));
			//messageBuilder.getTags().put("publish_type", "web_socket");
			try {
				log.info("Websocket Client : " + this.eventHubPublishConfig.getWsUri());
				log.info("Messsge Payload : "+data);
				
                
				this.eventHubClient.postBinaryWSData(data.getBytes());
				
				Thread.sleep(5000);
			} catch (IOException | WebSocketException e) {
				throw new RuntimeException("Exception when posting data to websocket", e);
			}catch (InterruptedException e) {
				log.error("Failed due to thread interruption." + e.getMessage()); // $$ //$NON-NLS-1$
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
	
	private String makeMessage(String id, String body){
        return String.format("[{\"id\":\"%s\", \"body\":{\"message\":%s}, \"tags\":{\"publish_type\":\"web_socket\"}}]", id, body);
    }
}
