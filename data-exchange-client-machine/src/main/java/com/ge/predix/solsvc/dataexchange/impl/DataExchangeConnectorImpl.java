package com.ge.predix.solsvc.dataexchange.impl;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.dspmicro.websocketriver.send.api.IWebSocketRiverConfig;
import com.ge.predix.solsvc.dataexchange.api.IDataExchangeConnector;

/**
 * 
 * @author predix.adoption@ge.com -
 */
@Component(name = DataExchangeConnectorImpl.SERVICE_PID)
public class DataExchangeConnectorImpl implements IDataExchangeConnector{
	/**
	 * Create logger to report errors, warning massages, and info messages
	 * (runtime Statistics)
	 */
	protected static Logger _logger = LoggerFactory.getLogger(DataExchangeConnectorImpl.class);

	/** Service PID for Sample Machine Adapter */
	public static final String SERVICE_PID = "com.ge.predix.solsvc.dataexchange"; //$NON-NLS-1$


	private WebSocketContainer clientContainer;

	/**
	 * 
	 */
	private Map<String, Configuration> configMap = new HashMap<String, Configuration>();

	/**
	 * 
	 */
	private Map<String, Session> sessionMap = new HashMap<String, Session>();

	private static final int RECEIVE_TIMEOUT = 5000;

	/** Lock object to sync the async call and callback. */
	private static Object _syncLock = new Object();

	private ConfigurationAdmin configAdmin;

	/**
	 * @param ctx -
	 * @throws IOException -
	 */
	@Activate
	public void activate(ComponentContext ctx) throws IOException {
		_logger.info("Component Initialized......"); //$NON-NLS-1$
		try {
			Configuration[] configs = this.configAdmin.listConfigurations(null);
			for (Configuration config : configs) {
				if (config.getFactoryPid() != null
						&& config.getFactoryPid().equals("com.ge.dspmicro.websocketriver.send")) { //$NON-NLS-1$
					_logger.info("FactoryPid . " + config.getFactoryPid()); //$NON-NLS-1$
					_logger.info("Pid . " + config.getPid()); //$NON-NLS-1$
					_logger.info("BundleLocation : " + config.getBundleLocation()); //$NON-NLS-1$
					_logger.info("Properties : " + config.getProperties()); //$NON-NLS-1$
					this.configMap.put(
							config.getProperties().get(IWebSocketRiverConfig.PROPKEY_DESTINATION_URL).toString(),
							config);
					this.sessionMap.put(
							config.getProperties().get(IWebSocketRiverConfig.PROPKEY_DESTINATION_URL).toString(),
							getWebSocketContainer().connectToServer(WebSocketClientHandlerSample.class,
									URI.create(config.getProperties()
											.get(IWebSocketRiverConfig.PROPKEY_DESTINATION_URL).toString())));
				}
			}

		} catch (DeploymentException | InvalidSyntaxException e) {
			_logger.error("Exception when connecting", e); //$NON-NLS-1$
		}
	}

	@Override
	public void sendMachineData(String data) {
		_logger.info("Sending test data to cloud."); //$NON-NLS-1$
		// Send the data
		try {
			synchronized (_syncLock) {
				for (Session session : this.sessionMap.values()) {
					String sessionURI = session.getRequestURI().toString();
					if (!session.isOpen()) {
						this.sessionMap.remove(sessionURI);
						Configuration config = this.configMap.get(sessionURI);
						session = getWebSocketContainer().connectToServer(WebSocketClientHandlerSample.class,
								URI.create(config.getProperties().get(IWebSocketRiverConfig.PROPKEY_DESTINATION_URL)
										.toString()));
						this.sessionMap.put(sessionURI, session);
					}
					try {
						_logger.info("Sending Data : "+data); //$NON-NLS-1$
						session.getBasicRemote().sendText(data);
					} catch (IOException e) {
						_logger.error("Exception : ", e); //$NON-NLS-1$
					}
					try {
						_syncLock.wait(RECEIVE_TIMEOUT);
					} catch (InterruptedException e) {
						_logger.error("Exception : ", e); //$NON-NLS-1$
					} // Callback will notify when transfer completes
				}
			}
		} catch (Exception e) {
			_logger.error("Exception : ", e); //$NON-NLS-1$
		}
	}

	/**
	 * Dependency injection of Websocket container
	 * 
	 * @param container
	 *            Websocket Container used to connect sessions with the server
	 */
	@Reference
	protected void setWebSocketContainer(WebSocketContainer container) {
		this.clientContainer = container;
	}

	/**
	 *  -
	 */
	@Deactivate
	public void deActivate() {
		unsetWebSocketContainter(this.clientContainer);
	}
	/**
	 * Unset of Websocket container
	 * 
	 * @param container
	 *            Websocket Container used to connect sessions with the server
	 */
	protected void unsetWebSocketContainter(WebSocketContainer container) {
		for (Session session : this.sessionMap.values()) {
			if (session.isOpen()) {
				try {
					session.close();
				} catch (IOException e) {
					_logger.error("Exception when closing Websocket Connection: ", e); //$NON-NLS-1$
				}
			}
		}
		this.clientContainer = null;
	}

	/**
	 * @return the WebSocketContainer
	 */
	public WebSocketContainer getWebSocketContainer() {
		return this.clientContainer;
	}

	/**
	 * @return -
	 */
	public ConfigurationAdmin getConfigAdmin() {
		return this.configAdmin;
	}

	/**
	 * @param configAdmin -
	 */
	@Reference
	public void setConfigAdmin(ConfigurationAdmin configAdmin) {
		this.configAdmin = configAdmin;
	}
}
