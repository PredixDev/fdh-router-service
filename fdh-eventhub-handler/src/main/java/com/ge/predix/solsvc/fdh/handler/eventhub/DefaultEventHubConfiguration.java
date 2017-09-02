package com.ge.predix.solsvc.fdh.handler.eventhub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.restclient.config.DefaultOauthRestConfig;

@Component
@Profile("eventhub")
public class DefaultEventHubConfiguration extends DefaultOauthRestConfig{
	
	@Value("${predix.eventhub.zoneid:#{null}}")
	private String eventHubZoneId;

	@Value("${predix.eventhub.host:#{null}}")
	private String eventHubHostName;
	
	@Value("${predix.eventhub.service.name:#{null}}")
	private String eventHubServiceName;
	
	@Value("${predix.uaa.service.name:#{null}}")
	private String eventHubUAAServiceName;
	
	
	public String getEventHubZoneId() {
		return eventHubZoneId;
	}

	public void setEventHubZoneId(String eventHubZoneId) {
		this.eventHubZoneId = eventHubZoneId;
	}

	public String getEventHubServiceName() {
		return eventHubServiceName;
	}

	public void setEventHubServiceName(String eventHubServiceName) {
		this.eventHubServiceName = eventHubServiceName;
	}

	public String getEventHubUAAServiceName() {
		return eventHubUAAServiceName;
	}

	public void setEventHubUAAServiceName(String eventHubUAAServiceName) {
		this.eventHubUAAServiceName = eventHubUAAServiceName;
	}

	public String getEventHubHostName() {
		return eventHubHostName;
	}

	public void setEventHubHostName(String eventHubHostName) {
		this.eventHubHostName = eventHubHostName;
	}
}
