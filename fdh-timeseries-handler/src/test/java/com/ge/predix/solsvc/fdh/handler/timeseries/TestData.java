/*
 * Copyright (c) 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.handler.timeseries;

import java.util.ArrayList;
import java.util.List;

import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;

/**
 * 
 * @author predix -
 */
public class TestData {

	/**
	 * @return -
	 */
	@SuppressWarnings("nls")
	public static PutFieldDataRequest getPutFieldDataRequest() {
		PutFieldDataRequest putRequest = new PutFieldDataRequest();
		putRequest.setCorrelationId("correlationId");
		List<PutFieldDataCriteria> putCriterias = new ArrayList<PutFieldDataCriteria>();
		PutFieldDataCriteria putCriteria = new PutFieldDataCriteria();
		FieldData fieldData = new FieldData();
		com.ge.predix.entity.field.Field field = new com.ge.predix.entity.field.Field();
		FieldIdentifier fieldIdentifier = new FieldIdentifier();
		//fieldIdentifier.setId("");
		fieldIdentifier.setSource("PREDIX_TIMESERIES");
		field.setFieldIdentifier(fieldIdentifier);	
		DatapointsIngestion dpIngestion = createMetrics();
		fieldData.setData(dpIngestion);

		putCriteria.setFieldData(fieldData );
		putCriterias.add(putCriteria );
		putRequest.setPutFieldDataCriteria(putCriterias);
		return putRequest;
	}

	@SuppressWarnings({ "nls", "unchecked" })
	private static DatapointsIngestion createMetrics() {
		DatapointsIngestion dpIngestion = new DatapointsIngestion();
		dpIngestion.setMessageId(String.valueOf(System.currentTimeMillis()));

		Body body = new Body();
		body.setName("RMD_metric2");
		List<Object> datapoint1 = new ArrayList<Object>();
		datapoint1.add(System.currentTimeMillis());
		datapoint1.add(10);
		datapoint1.add(3); // quality

		List<Object> datapoint2 = new ArrayList<Object>();
		datapoint2.add(System.currentTimeMillis());
		datapoint2.add(10);
		datapoint2.add(1); // quality

		List<Object> datapoints = new ArrayList<Object>();
		datapoints.add(datapoint1);
		datapoints.add(datapoint2);

		body.setDatapoints(datapoints);

		com.ge.predix.entity.util.map.Map map = new com.ge.predix.entity.util.map.Map();
		map.put("host", "server1");
		map.put("customer", "Acme");

		body.setAttributes(map);

		List<Body> bodies = new ArrayList<Body>();
		bodies.add(body);

		dpIngestion.setBody(bodies);
		return dpIngestion;
	}
}
