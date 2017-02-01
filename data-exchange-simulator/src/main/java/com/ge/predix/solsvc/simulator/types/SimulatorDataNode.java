package com.ge.predix.solsvc.simulator.types;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
@Component
public class SimulatorDataNode {
	@JsonProperty("assetId") private String assetId;
	@JsonProperty("nodeName") private String nodeName;
	@JsonProperty("dataType") private String dataType;
	@JsonProperty("upperThreshold") private Double upperThreshold;
	@JsonProperty("lowerThreshold") private Double lowerThreshold;
	@JsonProperty("expression") private String expression;
	@JsonProperty("increment") private int increment;
	
	private Boolean simulateAlarm = Boolean.FALSE;
	
	public String getAssetId() {
		return this.assetId;
	}
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
	public String getNodeName() {
		return this.nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getDataType() {
		return this.dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public Double getUpperThreshold() {
		return this.upperThreshold;
	}
	public void setUpperThreshold(Double upperThreshold) {
		this.upperThreshold = upperThreshold;
	}
	public Double getLowerThreshold() {
		return this.lowerThreshold;
	}
	public void setLowerThreshold(Double lowerThreshold) {
		this.lowerThreshold = lowerThreshold;
	}
	public String getExpression() {
		return this.expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public Boolean isSimulateAlarm() {
		return this.simulateAlarm;
	}
	public void setSimulateAlarm(Boolean simulateAlarm) {
		this.simulateAlarm = simulateAlarm;
	}
	public int getIncrement() {
		return this.increment;
	}
	public void setIncrement(int increment) {
		this.increment = increment;
	}
	public Boolean getSimulateAlarm() {
		return this.simulateAlarm;
	}
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "SimulatorDataNode [assetId=" + this.assetId + ", nodeName=" + this.nodeName + ", dataType=" + this.dataType
                + ", upperThreshold=" + this.upperThreshold + ", lowerThreshold=" + this.lowerThreshold + ", expression="
                + this.expression + ", increment=" + this.increment + ", simulateAlarm=" + this.simulateAlarm + "]";
    }
	
	
}
