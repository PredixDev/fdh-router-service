/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.machinedata;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.dspmicro.machinegateway.api.adapter.IDataSubscription;
import com.ge.dspmicro.machinegateway.api.adapter.IDataSubscriptionListener;
import com.ge.dspmicro.machinegateway.api.adapter.ISubscriptionMachineAdapter;
import com.ge.dspmicro.machinegateway.types.ITransferable;
import com.ge.dspmicro.machinegateway.types.PDataNode;
import com.ge.predix.entity.field.Field;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddata.PredixString;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.solsvc.dataexchange.api.IDataExchangeConnector;
import com.ge.predix.solsvc.ext.util.JsonMapper;

/**
 * 
 * @author Predix Machine Sample
 */
public class DataExchangeSubscription implements Runnable, IDataSubscription {
	/**
	 * 
	 */
	protected static Logger logger = LoggerFactory.getLogger(DataExchangeSubscription.class);
	private UUID uuid;
	private String name;
	private int updateInterval;
	private ISubscriptionMachineAdapter adapter;
	private List<IDataSubscriptionListener> listeners = new ArrayList<IDataSubscriptionListener>();
	private final AtomicBoolean threadRunning = new AtomicBoolean();
	private JsonMapper mapper = new JsonMapper();
	private IDataExchangeConnector dataExchange;
	/**
	 * Constructor
	 * 
	 * @param adapter
	 *            machine adapter
	 * @param subName
	 *            Name of this subscription
	 * @param updateInterval
	 *            in milliseconds
	 * @param nodes
	 *            list of nodes for this subscription
	 */
	public DataExchangeSubscription(ISubscriptionMachineAdapter adapter, String subName, int updateInterval,
			List<PDataNode> nodes,IDataExchangeConnector dataExchange) {
		mapper.init();
		this.dataExchange = dataExchange;
		if (updateInterval > 0) {
			this.updateInterval = updateInterval;
		} else {
			throw new IllegalArgumentException("updataInterval must be greater than zero."); //$NON-NLS-1$
		}

		
		this.adapter = adapter;

		// Generate unique id.
		this.uuid = UUID.randomUUID();
		this.name = subName;

		this.threadRunning.set(false);
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public UUID getId() {
		return this.uuid;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getUpdateInterval() {
		return this.updateInterval;
	}

	@Override
	public List<PDataNode> getSubscriptionNodes() {
		return this.adapter.getNodes();
	}

	/**
	 * @param listener
	 *            callback listener
	 */
	@Override
	public synchronized void addDataSubscriptionListener(IDataSubscriptionListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	/**
	 * @param listener
	 *            callback listener
	 */
	@Override
	public synchronized void removeDataSubscriptionListener(IDataSubscriptionListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}

	/**
	 * get all listeners
	 * 
	 * @return a list of listeners.
	 */
	@Override
	public synchronized List<IDataSubscriptionListener> getDataSubscriptionListeners() {
		return this.listeners;
	}

	/**
	 * Thread to generate random data for the nodes in this subscription.
	 */
	@Override
	public void run() {
		
		if (!this.threadRunning.get() && this.adapter.getNodes().size() > 0) {
			this.threadRunning.set(true);
			logger.info("Running");
			while (this.threadRunning.get()) {
				// Generate random data for each node and push data update.
				List<DataExchangeDataValue> data = new ArrayList<DataExchangeDataValue>();

				for (PDataNode node : this.adapter.getNodes()) {
					data.add((DataExchangeDataValue)this.adapter.readData(node.getNodeId()));
				}

				logger.info("VALUES :" + data.toString()); //$NON-NLS-1$
				DatapointsIngestion datapointsIngestion = createTimeseriesDataBody(data);
				PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
				PutFieldDataCriteria criteria = new PutFieldDataCriteria();
				FieldData fieldData = new FieldData();
				Field field = new Field();
				FieldIdentifier fieldIdentifier = new FieldIdentifier();
				
				fieldIdentifier.setSource(FieldSourceEnum.PREDIX_TIMESERIES.name());
				field.setFieldIdentifier(fieldIdentifier);
				List<Field> fields = new ArrayList<Field>();
				fields.add(field);
				fieldData.setField(fields);
				PredixString predixString = new PredixString();
				predixString.setString(mapper.toJson(datapointsIngestion));
				fieldData.setData(predixString);
				criteria.setFieldData(fieldData);
				List<PutFieldDataCriteria> list = new ArrayList<PutFieldDataCriteria>();
				list.add(criteria);
				putFieldDataRequest.setPutFieldDataCriteria(list);
				String strPutDataRequest = mapper.toJson(putFieldDataRequest);
				dataExchange.sendMachineData(strPutDataRequest);

				try {
					// Wait for an updateInterval period before pushing next
					// data update.
					Thread.sleep(this.updateInterval);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Stops generating random data.
	 */
	public void stop() {
		if (this.threadRunning.get()) {
			this.threadRunning.set(false);

			// Send notification to all listeners.
			for (IDataSubscriptionListener listener : this.listeners) {
				listener.onSubscriptionDelete(this.adapter, this.uuid);
			}

			// Do other clean up if needed...
		}
	}

	
	
	@SuppressWarnings("unchecked")
	private DatapointsIngestion createTimeseriesDataBody(List<DataExchangeDataValue> values) {
		DatapointsIngestion dpIngestion = new DatapointsIngestion();
		dpIngestion.setMessageId(UUID.randomUUID().toString());
		List<Body> bodies = new ArrayList<Body>();
		for (ITransferable t : values) {
			DataExchangeDataValue sdv = (DataExchangeDataValue) t;
			Body body = new Body();
			List<Object> datapoints = new ArrayList<Object>();
			body.setName(sdv.getNodeName());

			// attributes
			com.ge.predix.entity.util.map.Map map = new com.ge.predix.entity.util.map.Map();
			map.put("assetId", sdv.getAssetId());

			map.put("sourceTagId", sdv.getNodeName());

			body.setAttributes(map);

			// datapoints
			List<Object> datapoint = new ArrayList<Object>();
			datapoint.add(converLocalTimeToUtcTime(sdv.getTimestamp().getTimeMilliseconds()));
			datapoint.add(sdv.getValue().getValue());
			datapoints.add(datapoint);

			body.setDatapoints(datapoints);
			bodies.add(body);
		}
		dpIngestion.setBody(bodies);

		return dpIngestion;
	}
	
	private long converLocalTimeToUtcTime(long timeSinceLocalEpoch) {
		return timeSinceLocalEpoch + getLocalToUtcDelta();
	}
	private long getLocalToUtcDelta() {
		Calendar local = Calendar.getInstance();
		local.clear();
		local.set(1970, Calendar.JANUARY, 1, 0, 0, 0);
		return local.getTimeInMillis();
	}
}
