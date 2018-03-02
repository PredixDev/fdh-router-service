package com.ge.predix.solsvc.fdh.handler.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.restclient.config.DefaultOauthRestConfig;
import com.ge.predix.solsvc.websocket.config.IWebSocketConfig;

@Component("webSocketClientConfig")
@Profile("websocket")
public class WebSocketClientConfig extends DefaultOauthRestConfig implements IWebSocketConfig
{
	@Value("${predix.websocket.uri}")
    private String             wsUri;

    @Value("${predix.websocket.pool.maxIdle:5}")
    private int                wsMaxIdle;

    @Value("${predix.websocket.pool.maxActive:5}")
    private int                wsMaxActive;

    @Value("${predix.websocket.pool.maxWait:8000}")
    private int                wsMaxWait;

	@Override
	public String getWsUri() {
		return this.wsUri;
	}	

	@Override
	public int getWsMaxIdle() {
		return this.wsMaxIdle;
	}

	@Override
	public int getWsMaxActive() {
		return this.wsMaxActive;
	}

	@Override
	public int getWsMaxWait() {
		return this.wsMaxWait;
	}

	@Override
	public void setWsUri(String wsUri) {
		this.wsUri = wsUri;
	}

	@Override
	public String getZoneId() {
		return null;
	}

	@Override
	public String getZoneIdHeader() {
		// TODO Auto-generated method stub
		return null;
	}
}
