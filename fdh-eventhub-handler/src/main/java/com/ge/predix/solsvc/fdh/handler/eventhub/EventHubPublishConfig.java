package com.ge.predix.solsvc.fdh.handler.eventhub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.restclient.config.DefaultOauthRestConfig;
import com.ge.predix.solsvc.websocket.config.IWebSocketConfig;

/**
 * 
 * @author 212546387 -
 */
@Component("eventHubPublishConfig")
@Profile({"eventhub-websocket","eventhub"})
public class EventHubPublishConfig extends DefaultOauthRestConfig implements IWebSocketConfig {
    @Value("${predix.eventhub.zoneid.header:Predix-Zone-Id}")
    private String             zoneIdHeader;
    
	@Value("${predix.eventhub.publish.zoneid:#{null}}")
	private String zoneId;

	@Value("${predix.eventhub.publish.host:#{null}}")
	private String eventHubHostName;
	
	@Value("${predix.eventhub.publish.port:#{443}}")
	private int eventHubPort;
	
	@Value("${predix.eventhub.publish.service.name}")
	private String eventHubServiceName;
	
	@Value("${predix.eventhub.publish.uaa.service.name}")
	private String eventHubUAAServiceName;
	
	@Value("${predix.eventhub.publish.proxyHost:#{null}}")
	private String proxyHost;
	
	@Value("${predix.eventhub.publish.proxyPort:8080}")
	private String proxyPort;
	
	@Value("${predix.eventhub.publish.websocket.url:null}")
	private String wsUri;
	
	@Value("${predix.eventhub.websocket.pool.maxIdle:5}")
    private int                wsMaxIdle;

    @Value("${predix.eventhub.websocket.pool.maxActive:5}")
    private int                wsMaxActive;

    @Value("${predix.eventhub.websocket.pool.maxWait:8000}")
    private int                wsMaxWait;
    
    @Value("${predix.eventhub.publish.topic}")
    private String publishTopic;

	/**
	 * @return -
	 */
	public String getEventHubServiceName() {
		return this.eventHubServiceName;
	}

	/**
	 * @param eventHubServiceName -
	 */
	public void setEventHubServiceName(String eventHubServiceName) {
		this.eventHubServiceName = eventHubServiceName;
	}

	/**
	 * @return -
	 */
	public String getEventHubUAAServiceName() {
		return this.eventHubUAAServiceName;
	}

	/**
	 * @param eventHubUAAServiceName -
	 */
	public void setEventHubUAAServiceName(String eventHubUAAServiceName) {
		this.eventHubUAAServiceName = eventHubUAAServiceName;
	}

	/**
	 * @return -
	 */
	public String getEventHubHostName() {
		return this.eventHubHostName;
	}

	/**
	 * @param eventHubHostName -
	 */
	public void setEventHubHostName(String eventHubHostName) {
		this.eventHubHostName = eventHubHostName;
	}
	
	/**
	 * @return -
	 */
	public String getProxyHost() {
		return this.proxyHost;
	}

	/**
	 * @param proxyHost -
	 */
	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	/**
	 * @return -
	 */
	public String getProxyPort() {
		return this.proxyPort;
	}

	/**
	 * @param proxyPort -
	 */
	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * @return -
	 */
	public int getEventHubPort() {
		return this.eventHubPort;
	}

	/**
	 * @param eventHubPort -
	 */
	public void setEventHubPort(int eventHubPort) {
		this.eventHubPort = eventHubPort;
	}

	@Override
	public String getWsUri() {
		return this.wsUri;
	}

	@Override
	public void setWsUri(String wsUri) {
		this.wsUri = wsUri;
	}

	@Override
	public String getZoneId() {
		return this.zoneId;
	}

	/**
	 * @param zoneId -
	 */
	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	@Override
	public int getWsMaxIdle() {
		return this.wsMaxIdle;
	}

	/**
	 * @param wsMaxIdle -
	 */
	public void setWsMaxIdle(int wsMaxIdle) {
		this.wsMaxIdle = wsMaxIdle;
	}

	@Override
	public int getWsMaxActive() {
		return this.wsMaxActive;
	}

	/**
	 * @param wsMaxActive -
	 */
	public void setWsMaxActive(int wsMaxActive) {
		this.wsMaxActive = wsMaxActive;
	}

	@Override
	public int getWsMaxWait() {
		return this.wsMaxWait;
	}

	/**
	 * @param wsMaxWait -
	 */
	public void setWsMaxWait(int wsMaxWait) {
		this.wsMaxWait = wsMaxWait;
	}

	@Override
	public String getZoneIdHeader() {
		return this.zoneIdHeader;
	}

	/**
	 * @param zoneIdHeader -
	 */
	public void setZoneIdHeader(String zoneIdHeader) {
		this.zoneIdHeader = zoneIdHeader;
	}

	/**
	 * @return -
	 */
	public String getPublishTopic() {
		return this.publishTopic;
	}

	/**
	 * @param publishTopic -
	 */
	public void setPublishTopic(String publishTopic) {
		this.publishTopic = publishTopic;
	}
}
