package com.ge.predix.solsvc.dataexchange.impl;

import java.io.IOException;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.ge.predix.solsvc.dataexchange.api.IDataExchangeConfig;


/**
 * 
 * @author predix.adoption@ge.com -
 */
@Component(name=DataExchangeRefreshAssetModel.SERVICE_PID)
public class DataExchangeRefreshAssetModel {
	
	/** Service PID for Sample Machine Adapter */
	public static final String SERVICE_PID = "com.ge.predix.solsvc.dataexchange.asset"; //$NON-NLS-1$

	private IDataExchangeConfig dataExchangeConfig;
	
	/**
	 * @param ctx -
	 * @throws IOException -
	 */
	@Activate
	public void activate(ComponentContext ctx) throws IOException {
		
	}

	/**
	 * @return the dataExchangeConfig
	 */
	public IDataExchangeConfig getDataExchangeConfig() {
		return this.dataExchangeConfig;
	}

	/**
	 * @param dataExchangeConfig the dataExchangeConfig to set
	 */
	@Reference
	public void setDataExchangeConfig(IDataExchangeConfig dataExchangeConfig) {
		this.dataExchangeConfig = dataExchangeConfig;
	}
}
