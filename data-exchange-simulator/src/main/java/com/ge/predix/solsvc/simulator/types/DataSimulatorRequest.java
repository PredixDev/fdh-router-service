package com.ge.predix.solsvc.simulator.types;

public class DataSimulatorRequest {
	String simulatorRequest;
	String simulatorRequestType = Constants.SIMULATOR_REQUEST_TYPE_JSON;
	
	public String getSimulatorRequest() {
		return simulatorRequest;
	}
	public void setSimulatorRequest(String simulatorRequest) {
		this.simulatorRequest = simulatorRequest;
	}
	public String getSimulatorRequestType() {
		return simulatorRequestType;
	}
	public void setSimulatorRequestType(String simulatorRequestType) {
		this.simulatorRequestType = simulatorRequestType;
	}
	
}
