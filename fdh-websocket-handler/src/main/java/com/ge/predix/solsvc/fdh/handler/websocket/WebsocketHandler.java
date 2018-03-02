/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
package com.ge.predix.solsvc.fdh.handler.websocket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.handler.GetDataHandler;
import com.ge.predix.solsvc.fdh.handler.PutDataHandler;
import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.websocket.client.WebSocketClient;
import com.ge.predix.solsvc.websocket.client.WebSocketClientImpl;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

/**
 * 
 * @author predix -
 */
@Component(value = "webSocketHandler")
@ImportResource({ "classpath*:META-INF/spring/fdh-websocket-handler-scan-context.xml"

})
@Profile("websocket")
public class WebsocketHandler implements GetDataHandler, PutDataHandler {
	private static final Logger log = LoggerFactory.getLogger(WebsocketHandler.class);

	@Autowired
	@Qualifier("webSocketClientConfig")
	private WebSocketClientConfig webSocketClientConfig;

	@Autowired
	private JsonMapper mapper;

	@Autowired
	private RestClient restClient2;

	//@Autowired
	private WebSocketClient client2 = new WebSocketClientImpl();

	// private WebSocketClient client2 = new WebSocketClientImpl();
	/**
	 * 
	 */
	private WebSocketAdapter messageListener = new WebSocketAdapter() {
		@SuppressWarnings("nls")
		@Override
		public void onTextMessage(WebSocket wsocket, String message) {
			log.info("RECEIVED....from " + wsocket.getURI().toString() + message); // $$
		}

		@SuppressWarnings("nls")
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
		this.restClient2.overrideRestConfig(webSocketClientConfig);
		this.client2.overrideWebSocketConfig(this.webSocketClientConfig);
		this.client2.init(this.restClient2, this.restClient2.getSecureTokenForClientId(), messageListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ge.predix.solsvc.fdhcontentbasedrouter.GetFieldDataInterface#
	 * getFieldData(java.util.List,
	 * com.ge.predix.entity.getfielddata.GetFieldDataRequest)
	 */
	@SuppressWarnings("nls")
	@Override
	public GetFieldDataResult getData(GetFieldDataRequest request, Map<Integer, Object> modelLookupMap,
			List<Header> headers) {
		throw new UnsupportedOperationException("unimplemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ge.predix.solsvc.fdh.handler.PutFieldDataInterface#processRequest(com
	 * .ge.predix.entity.putfielddata.PutFieldDataRequest, java.util.List)
	 */
	@SuppressWarnings({ "nls" })
	@Override
	public PutFieldDataResult putData(PutFieldDataRequest request, Map<Integer, Object> modelLookupMap,
			List<Header> headers, String httpMethod) {
		validate();

		for (PutFieldDataCriteria criteria : request.getPutFieldDataCriteria()) {
			FieldData fieldData = criteria.getFieldData();
			DatapointsIngestion dpIngestion = null;
			if (fieldData.getData() instanceof DatapointsIngestion) {
				dpIngestion = (DatapointsIngestion) fieldData.getData();
				String payload = this.mapper.toJson(dpIngestion);
				log.info("Payload : " + payload);
				try {
					log.info("Websocket Client : " + this.webSocketClientConfig.getWsUri());
					this.client2.postTextWSData(payload);
				} catch (IOException | WebSocketException e) {
					throw new RuntimeException("Exception when posting data to websocket", e);
				}
			}
		}
		PutFieldDataResult result = new PutFieldDataResult();
		result.getErrorEvent().add("Data Sent to Websocket Server");
		return result;
	}

	/**
	 * -
	 */
	private void validate() {
		// TODO Auto-generated method stub

	}
}
