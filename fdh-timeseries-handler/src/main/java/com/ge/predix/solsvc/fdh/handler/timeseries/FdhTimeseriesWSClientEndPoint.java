package com.ge.predix.solsvc.fdh.handler.timeseries;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;

import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.timeseries.bootstrap.factories.TimeseriesFactory;

/**
 * 
 * @author predix.adoption@ge.com -
 */
@ComponentScan(basePackages = { "com.ge.predix.solsvc" })
@ClientEndpoint
public class FdhTimeseriesWSClientEndPoint implements ApplicationContextAware {
	/**
	 * 
	 */
	static Logger logger = LoggerFactory.getLogger(FdhTimeseriesWSClientEndPoint.class);

	private TimeseriesFactory timeseriesFactory;

	/**
	 * @param message
	 *            -
	 * @param session
	 *            -
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		logger.info("Message in Websocket Client: " + message); //$NON-NLS-1$
		JsonMapper jsonMapper = new JsonMapper();
		DatapointsIngestion datapointsIngestion = jsonMapper.fromJson(message, DatapointsIngestion.class);
		this.timeseriesFactory.postDataToTimeseriesWebsocket(datapointsIngestion);
	}

	/**
	 * @param session
	 *            -
	 * @param t
	 *            -
	 */
	@OnError
	public void onError(Session session, Throwable t) {
		t.printStackTrace();
		logger.error("Client: Session " + session.getId() + " closed because of " + t.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.timeseriesFactory = applicationContext.getBean(TimeseriesFactory.class);
		this.timeseriesFactory.createConnectionToTimeseriesWebsocket();
	}
}
