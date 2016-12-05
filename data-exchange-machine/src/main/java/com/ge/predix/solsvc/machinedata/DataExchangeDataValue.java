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

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.ge.dspmicro.machinegateway.types.PDataValue;
import com.ge.dspmicro.machinegateway.types.PEnvelope;

/**
 * 
 * @author 212546387 -
 */
public class DataExchangeDataValue extends PDataValue{
	private String assetId;
	 private static ObjectMapper _mapper = new ObjectMapper();
	
	/**
	 * @param nodeId -
	 * @param envelope -
	 */
	public DataExchangeDataValue(UUID nodeId, PEnvelope envelope) {
		super(nodeId,envelope);
	}
	/* (non-Javadoc)
	 * @see com.ge.dspmicro.machinegateway.types.PDataValue#toString()
	 */
	@Override
	public String toString() {
		String data = super.toString();
		try {
			Map<String, Object> jsonMap = _mapper.readValue(data, new TypeReference<Map<String, Object>>(){/* */});
			jsonMap.put("assetId", this.getAssetId()); //$NON-NLS-1$
			data = _mapper.writeValueAsString(jsonMap);
			//log.debug("Data : "+data); //$NON-NLS-1$
		} catch (JsonParseException e) {
			throw new RuntimeException(e.getMessage(),e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e.getMessage(),e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		return data;
	}
	/**
	 * @return -
	 */
	public String getAssetId() {
		return this.assetId;
	}
	/**
	 * @param assetId -
	 */
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
}
