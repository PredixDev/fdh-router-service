package com.ge.predix.solsvc.fdh.handler.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.ge.predix.solsvc.restclient.config.DefaultOauthRestConfig;
import com.ge.predix.solsvc.websocket.config.IWebSocketConfig;

@Configuration

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
	public String printName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOauthIssuerId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOauthGrantType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOauthCertLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOauthCertPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOauthClientId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getOauthClientIdEncode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getOauthProxyHost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOauthProxyPort() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOauthTokenType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOauthUserName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOauthUserPassword() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isOauthEncodeUserPassword() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getOauthConnectionTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDefaultConnectionTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOauthSocketTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDefaultSocketTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPoolMaxSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPoolValidateAfterInactivityTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getPoolConnectionRequestTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getWsUri() {
		return this.wsUri;
	}

	@Override
	public String getZoneId() {
		// TODO Auto-generated method stub
		return null;
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
	public String getZoneIdHeader() {
		// TODO Auto-generated method stub
		return null;
	}

}
