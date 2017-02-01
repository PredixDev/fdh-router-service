/*
 * Copyright (c) 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.dataexchange.impl;

import java.io.IOException;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.predix.solsvc.dataexchange.api.IDataExchangeConfig;

import aQute.bnd.annotation.metatype.Configurable;
import aQute.bnd.annotation.metatype.Meta;

/**
 * 
 * @author predix.adoption@ge.com -
 */
@Component(name=DataExchangeConfig.SERVICE_PID, configurationPolicy = ConfigurationPolicy.REQUIRE,service={
        IDataExchangeConfig.class
})
public class DataExchangeConfig implements com.ge.predix.solsvc.dataexchange.api.IDataExchangeConfig{
	/**
	 * Create logger to report errors, warning massages, and info messages
	 * (runtime Statistics)
	 */
	protected static Logger _logger = LoggerFactory.getLogger(DataExchangeConfig.class);

	/** Service PID for Sample Machine Adapter */
	public static final String SERVICE_PID = "com.ge.predix.solsvc.dataexchange.config"; //$NON-NLS-1$
	
	private Integer subscriptionUpdateInterval;
	private String[] dataSubscriptions;
	private String subscriptionDataAdapterName;
	private String subscriptionDataAdapterDescription;
	private String nodeConfigFile;
	
	private String assetDataFile;
	private String dxRestEndpoint;
	private String dxWebsocketEndpoint;
	private String dxAssetZoneHeaderName;
	private String dxAssetZoneHeaderValue;
	
	
	/** Key for Node Configuration File*/
    public static final String 				  NODE_CONFIG_FILE 	  = SERVICE_PID+".NodeConfigFile";                                    //$NON-NLS-1$
    /** Key for Update Interval */
    public static final String                UPDATE_INTERVAL     = SERVICE_PID + ".SubscriptionUpdateInterval";                             //$NON-NLS-1$
    /** key for machine adapter name */
    public static final String                ADAPTER_NAME        = SERVICE_PID + ".SubscriptionDataAdapterName";                                       //$NON-NLS-1$
    /** Key for machine adapter description */
    public static final String                ADAPTER_DESCRIPTION = SERVICE_PID + ".SubscriptionDataAdapterDescription";                                //$NON-NLS-1$
    /** data subscriptions */
    public static final String                DATA_SUBSCRIPTIONS  = SERVICE_PID + ".DataSubscriptions";                          //$NON-NLS-1$
    
    /** Key for Asset Data File */
    public static final String 				  ASSET_DATA_FILE = SERVICE_PID+".AssetDataFile"; //$NON-NLS-1$
    
    /** Key for Data Exchange Rest Endpoint */
    public static final String 				  DATA_EXCHANGE_REST_ENDPOINT = SERVICE_PID+".RestEndpoint"; //$NON-NLS-1$
    
    /** Key for Data Exchange Websocket Endpoint */
    public static final String 				  DATA_EXCHANGE_WEBSOCKET_ENDPOINT = SERVICE_PID+".WebsocketEndpoint"; //$NON-NLS-1$
    
    /** Key for Data Exchange Asset Zone Header Name */
    public static final String 				  ASSET_ZONEID_HEADER_NAME = SERVICE_PID+".asset.zone-header-name"; //$NON-NLS-1$
    
    /** Key for Data Exchange Asset Zone Header value */
    public static final String 				  ASSET_ZONEID_HEADER_VALUE = SERVICE_PID+".asset.zone-header-value"; //$NON-NLS-1$
    
	/**
	 * @return the assetDataFile
	 */
	public String getAssetDataFile() {
		return this.assetDataFile;
	}
	/**
	 * @param assetDataFile the assetDataFile to set
	 */
	public void setAssetDataFile(String assetDataFile) {
		this.assetDataFile = assetDataFile;
	}
	/**
	 * @return the dxRestEndpoint
	 */
	public String getDxRestEndpoint() {
		return this.dxRestEndpoint;
	}
	/**
	 * @param dxRestEndpoint the dxRestEndpoint to set
	 */
	public void setDxRestEndpoint(String dxRestEndpoint) {
		this.dxRestEndpoint = dxRestEndpoint;
	}
	/**
	 * @return the dxWebsocketEndpoint
	 */
	public String getDxWebsocketEndpoint() {
		return this.dxWebsocketEndpoint;
	}
	/**
	 * @param dxWebsocketEndpoint the dxWebsocketEndpoint to set
	 */
	public void setDxWebsocketEndpoint(String dxWebsocketEndpoint) {
		this.dxWebsocketEndpoint = dxWebsocketEndpoint;
	}

	private IDataExchangeReadConfig dataExchangeConfig;
	// Meta mapping for configuration properties
    /**
     * 
     * @author predix.adoption@ge.com -
     */
    @Meta.OCD(name = "%component.name", localization = "OSGI-INF/l10n/bundle")
    interface IDataExchangeReadConfig
    {
        /**
         * @return -
         */
        @Meta.AD(name = "%updateInterval.name", description = "%updateInterval.description", id = UPDATE_INTERVAL, required = false, deflt = "")
        Integer updateInterval();

        /**
         * @return -
         */
        @Meta.AD(name = "%nodeConfigFile.name", description = "%nodeConfigFile.description", id = NODE_CONFIG_FILE, required = false, deflt = "")
        String nodeConfigFile();

        /**
         * @return -
         */
        @Meta.AD(name = "%adapterName.name", description = "%adapterName.description", id = ADAPTER_NAME, required = false, deflt = "")
        String adapterName();

        /**
         * @return -
         */
        @Meta.AD(name = "%adapterDescription.name", description = "%adapterDescription.description", id = ADAPTER_DESCRIPTION, required = false, deflt = "")
        String adapterDescription();

        /**
         * @return -
         */
        @Meta.AD(name = "%dataSubscriptions.name", description = "%dataSubscriptions.description", id = DATA_SUBSCRIPTIONS, required = true, deflt = "")
        String[] dataSubscriptions();
        
        /**
         * @return -
         */
        @Meta.AD(name = "%AssetDataFile.name", description = "%AssetDataFile.description", id = ASSET_DATA_FILE, required = true, deflt = "")
        String assetDataFile();
        
        /**
         * @return -
         */
        @Meta.AD(name = "%DxRestEndpoint.name", description = "%DxRestEndpoint.description", id = DATA_EXCHANGE_REST_ENDPOINT, required = true, deflt = "")
        String dataExchangeRestEndpoint();
        
        /**
         * @return -
         */
        @Meta.AD(name = "%DxWebsocketEndpoint.name", description = "%WebsocketEndpoint.description", id = DATA_EXCHANGE_WEBSOCKET_ENDPOINT, required = true, deflt = "")
        String dataExchangeWebsocketEndpoint();
        
        /**
         * @return -
         */
        @Meta.AD(name = "%AssetZoneHeaderName.name", description = "%AssetZoneHeaderName.description", id = ASSET_ZONEID_HEADER_NAME, required = true, deflt = "")
        String dataExchangeAssetZoneHeaderName();
        
        /**
         * @return -
         */
        @Meta.AD(name = "%AssetZoneHeaderValue.name", description = "%AssetZoneHeaderValue.description", id = ASSET_ZONEID_HEADER_VALUE, required = true, deflt = "")
        String dataExchangeAssetZoneHeaderValue();
    }
    
	/**
	 * @return the subscriptionUpdateInterval
	 */
    @Override
	public Integer getSubscriptionUpdateInterval() {
		return this.subscriptionUpdateInterval;
	}
	/**
	 * @param subscriptionUpdateInterval the subscriptionUpdateInterval to set
	 */
	public void setSubscriptionUpdateInterval(Integer subscriptionUpdateInterval) {
		this.subscriptionUpdateInterval = subscriptionUpdateInterval;
	}
	/**
	 * @return the dataSubscriptions
	 */
	@Override
	public String[] getDataSubscriptions() {
		return this.dataSubscriptions;
	}
	/**
	 * @param dataSubscriptions the dataSubscriptions to set
	 */
	public void setDataSubscriptions(String[] dataSubscriptions) {
		this.dataSubscriptions = dataSubscriptions;
	}
	/**
	 * @return the subscriptionDataAdapterName
	 */
	@Override
	public String getSubscriptionDataAdapterName() {
		return this.subscriptionDataAdapterName;
	}
	/**
	 * @param subscriptionDataAdapterName the subscriptionDataAdapterName to set
	 */
	public void setSubscriptionDataAdapterName(String subscriptionDataAdapterName) {
		this.subscriptionDataAdapterName = subscriptionDataAdapterName;
	}
	/**
	 * @return the subscriptionDataAdapterDescription
	 */
	@Override
	public String getSubscriptionDataAdapterDescription() {
		return this.subscriptionDataAdapterDescription;
	}
	/**
	 * @param subscriptionDataAdapterDescription the subscriptionDataAdapterDescription to set
	 */
	public void setSubscriptionDataAdapterDescription(String subscriptionDataAdapterDescription) {
		this.subscriptionDataAdapterDescription = subscriptionDataAdapterDescription;
	}
	/**
	 * @return the nodeConfigFile
	 */
	@Override
	public String getNodeConfigFile() {
		return this.nodeConfigFile;
	}
	/**
	 * @param nodeConfigFile the nodeConfigFile to set
	 */
	public void setNodeConfigFile(String nodeConfigFile) {
		this.nodeConfigFile = nodeConfigFile;
	}
	
	
	
	/**
	 * @param ctx -
	 * @throws IOException -
	 */
	@Activate
	public void activate(ComponentContext ctx) throws IOException {
		_logger.info("Config Component Initialized......"); //$NON-NLS-1$

        this.dataExchangeConfig = Configurable.createConfigurable(IDataExchangeReadConfig.class, ctx.getProperties());
        updateInstanceVariables(this.dataExchangeConfig);
	}
	
	private void updateInstanceVariables(IDataExchangeReadConfig config){
		setDataSubscriptions(config.dataSubscriptions());
		setNodeConfigFile(config.nodeConfigFile());
		setSubscriptionDataAdapterDescription(config.adapterDescription());
		setSubscriptionDataAdapterName(config.adapterName());
		setSubscriptionUpdateInterval(config.updateInterval());
	}
	/**
	 * @return the dxAssetZoneHeaderName
	 */
	public String getDxAssetZoneHeaderName() {
		return this.dxAssetZoneHeaderName;
	}
	/**
	 * @param dxAssetZoneHeaderName the dxAssetZoneHeaderName to set
	 */
	public void setDxAssetZoneHeaderName(String dxAssetZoneHeaderName) {
		this.dxAssetZoneHeaderName = dxAssetZoneHeaderName;
	}
	/**
	 * @return the dxAssetZoneHeaderValue
	 */
	public String getDxAssetZoneHeaderValue() {
		return this.dxAssetZoneHeaderValue;
	}
	/**
	 * @param dxAssetZoneHeaderValue the dxAssetZoneHeaderValue to set
	 */
	public void setDxAssetZoneHeaderValue(String dxAssetZoneHeaderValue) {
		this.dxAssetZoneHeaderValue = dxAssetZoneHeaderValue;
	}
}
