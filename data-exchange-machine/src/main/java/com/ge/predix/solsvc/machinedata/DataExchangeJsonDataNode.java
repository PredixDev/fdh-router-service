/*
 * Copyright (c) 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.machinedata;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * @author 212546387 -
 */
public class DataExchangeJsonDataNode {
	@JsonProperty("assetId") private String assetId;
	@JsonProperty("nodeName") private String nodeName;
	@JsonProperty("dataType") private String dataType;
	@JsonProperty("upperThreshold") private Double upperThreshold;
	@JsonProperty("lowerThreshold") private Double lowerThreshold;
	@JsonProperty("expression") private String expression;
		
	private Boolean simulateAlarm = Boolean.FALSE;
	/**
	 * @return the assetId
	 */
	public String getAssetId() {
		return this.assetId;
	}
	/**
	 * @param assetId the assetId to set
	 */
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
	/**
	 * @return the nodeName
	 */
	public String getNodeName() {
		return this.nodeName;
	}
	/**
	 * @param nodeName the nodeName to set
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return this.dataType;
	}
	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	/**
	 * @return the upperThreshold
	 */
	public Double getUpperThreshold() {
		return this.upperThreshold;
	}
	/**
	 * @param upperThreshold the upperThreshold to set
	 */
	public void setUpperThreshold(Double upperThreshold) {
		this.upperThreshold = upperThreshold;
	}
	/**
	 * @return the lowerThreshold
	 */
	public Double getLowerThreshold() {
		return this.lowerThreshold;
	}
	/**
	 * @param lowerThreshold the lowerThreshold to set
	 */
	public void setLowerThreshold(Double lowerThreshold) {
		this.lowerThreshold = lowerThreshold;
	}
	/**
	 * @return the expression
	 */
	public String getExpression() {
		return this.expression;
	}
	/**
	 * @param expression the expression to set
	 */
	public void setExpression(String expression) {
		this.expression = expression;
	}
	/**
	 * @return the simulateAlarm
	 */
	public Boolean getSimulateAlarm() {
		return this.simulateAlarm;
	}
	/**
	 * @param simulateAlarm the simulateAlarm to set
	 */
	public void setSimulateAlarm(Boolean simulateAlarm) {
		this.simulateAlarm = simulateAlarm;
	}

}
