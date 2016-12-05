/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.router;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mimosa.osacbmv3_3.DAString;
import org.mimosa.osacbmv3_3.DMDataSeq;
import org.mimosa.osacbmv3_3.DMReal;
import org.mimosa.osacbmv3_3.DataEvent;
import org.mimosa.osacbmv3_3.OsacbmDataType;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import com.ge.predix.entity.assetfilter.AssetFilter;
import com.ge.predix.entity.engunit.EngUnit;
import com.ge.predix.entity.eventasset.eventassetidentifier.AssetIdentifier;
import com.ge.predix.entity.field.Field;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddata.OsaData;
import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.entity.util.map.AttributeMap;
import com.ge.predix.entity.util.map.Entry;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.handler.asset.AssetGetFieldDataHandlerImpl;
import com.ge.predix.solsvc.fdh.handler.timeseries.TimeseriesGetDataHandler;
import com.ge.predix.solsvc.fdh.router.service.FdhWebSocketServerEndPoint;
import com.ge.predix.solsvc.fdh.router.service.router.PutRouter;
import com.ge.predix.solsvc.fdh.router.util.XmlSupport;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * 
 * @author predix -
 */
@SpringBootApplication
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/TEST-mock-fdh-router-context.xml",
		"classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
		"classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml",
		"classpath*:META-INF/spring/ext-util-scan-context.xml",
		"classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml",
		"classpath*:META-INF/spring/predix-websocket-client-scan-context.xml",
		"classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml",
		"classpath*:META-INF/spring/fdh-router-scan-context.xml",
		"classpath*:META-INF/spring/fdh-router-cxf-context.xml",
		"classpath*:META-INF/spring/fdh-asset-handler-scan-context.xml",
		"classpath*:META-INF/spring/fdh-timeseries-handler-scan-context.xml",
		"classpath*:META-INF/spring/fdh-rabbitmq-handler-scan-context.xml" })
@ActiveProfiles("local")
@SpringApplicationConfiguration(classes = { AssetGetFieldDataHandlerImpl.class, TimeseriesGetDataHandler.class })
@EnableWebSocket
public abstract class BaseTest {

	private static final String TEST_SERVER_IP = "localhost"; //$NON-NLS-1$
	private String serverIP = TEST_SERVER_IP;
	private static final String HTTP_PAYLOAD_XML = "application/xml"; //$NON-NLS-1$

	/**
	 * 
	 */
	@Autowired
	protected RestClient restClient;

	@Autowired
	private PutRouter putFieldData;
	/**
	 * 
	 */
	@Autowired
	protected JsonMapper jsonMapper;

	/**
	 * @return -
	 */
	@Bean
	public FdhWebSocketServerEndPoint fdhWebSocketServerEndPoint() {
		return new FdhWebSocketServerEndPoint();
	}

	/**
	 * @param solutionId
	 *            -
	 * @param rawFieldId
	 *            -
	 * @param rawFieldName
	 *            -
	 * @param fieldSource
	 *            -
	 * @param rawValue
	 *            -
	 * @param assetId
	 *            -
	 * @param d1
	 *            -
	 * @param log
	 *            -
	 * @param contentType
	 *            -
	 * @param port
	 *            -
	 * @throws IOException
	 *             -
	 * @throws HttpException
	 *             -
	 */
	@SuppressWarnings("nls")
	protected void putFieldData(Long solutionId, String rawFieldId, String rawFieldName, String fieldSource,
			Object rawValue, String assetId, Date d1, Logger log, String contentType, String port) {
		try {
			putFieldData(solutionId, rawFieldId, rawFieldName, fieldSource, rawValue, assetId, d1, log,
					OsacbmDataType.DM_REAL, contentType, port);
		} catch (IOException ex) {
			final String info = " solutionId: " + solutionId + " rawFieldId: " + rawFieldId + " rawFieldName: "
					+ rawFieldName + " rawValue: " + rawValue + " assetId: " + assetId + " date: " + d1;
			throw new IllegalStateException(
					ex.getClass().getSimpleName() + " I/O error for ==>" + info + "; message: " + ex.getMessage(), ex);
		} catch (Throwable th) {
			final String info = " solutionId: " + solutionId + " rawFieldId: " + rawFieldId + " rawFieldName: "
					+ rawFieldName + " rawValue: " + rawValue + " assetId: " + assetId + " date: " + d1;
			throw new IllegalStateException(th.getClass().getSimpleName() + " unexpected error for ==>" + info
					+ "; message: " + th.getMessage(), th);
		}
	}

	/**
	 * @param solutionId
	 *            -
	 * @param rawFieldId
	 *            -
	 * @param rawFieldName
	 *            -
	 * @param fieldSource
	 *            -
	 * @param rawValue
	 *            -
	 * @param assetId
	 *            -
	 * @param firstDate
	 *            -
	 * @param log
	 *            -
	 * @param osacbmDataType
	 *            -
	 * @param contentType
	 *            -
	 * @param port
	 *            -
	 * @throws IOException
	 *             -
	 * @throws HttpException
	 *             -
	 */
	@SuppressWarnings("nls")
	protected void putFieldData(final Long solutionId, final String rawFieldId, final String rawFieldName,
			String fieldSource, final Object rawValue, final String assetId, final Date firstDate, final Logger log,
			final OsacbmDataType osacbmDataType, String contentType, String port) throws HttpException, IOException {
		if (solutionId == null)
			throw new IllegalArgumentException("solutionId null or empty");

		if (rawFieldName == null || rawFieldName.isEmpty())
			throw new IllegalArgumentException("rawFieldName null or empty");

		if (rawValue == null)
			throw new IllegalArgumentException("rawFieldName null or empty");

		if (assetId == null || assetId.isEmpty())
			throw new IllegalArgumentException("assetId null or empty");

		if (firstDate == null)
			throw new IllegalArgumentException("d1 null or empty");

		if (log == null)
			throw new IllegalArgumentException("log null or empty");

		if (osacbmDataType == null)
			throw new IllegalArgumentException("osacbmDataType null or empty");

		double currentTime = firstDate.getTime();
		PutFieldDataRequest payload = getPutFieldDataRequest(solutionId, currentTime, rawFieldId, rawFieldName,
				fieldSource, assetId, rawValue, osacbmDataType, contentType);

		callPutFieldData(log, payload, contentType, port);
	}

	/**
	 * @param solutionId
	 *            -
	 * @param currentTime
	 *            -
	 * @param fieldId
	 *            -
	 * @param fieldName
	 *            -
	 * @param fieldSource
	 *            -
	 * @param assetId
	 *            -
	 * @param value
	 *            -
	 * @param osacbmDataType
	 *            -
	 * @param contentType
	 *            -
	 * @return -
	 */
	@SuppressWarnings("nls")
	protected PutFieldDataRequest getPutFieldDataRequest(Long solutionId, double currentTime, String fieldId,
			String fieldName, String fieldSource, String assetId, Object value, OsacbmDataType osacbmDataType,
			String contentType) {
		PutFieldDataRequest request = new PutFieldDataRequest();
		AttributeMap externalAttributeMap = new AttributeMap();
		Entry entry = new Entry();
		// entry.setKey(IAssetDataInterceptor.SOLUTION_ID_KEY);
		// entry.setValue(solutionId);
		externalAttributeMap.getEntry().add(entry);
		// request.setSolutionId() //TODO when it's ready
		request.setExternalAttributeMap(externalAttributeMap);
		// DataRef dataRef = new DataRef();
		// request.setDataRef(dataRef);

		FieldData fieldData = new FieldData();
		DataEvent dataEvent = null;
		if (osacbmDataType == null || OsacbmDataType.DM_DATA_SEQ.equals(osacbmDataType)) {
			dataEvent = new DMDataSeq();
			DMDataSeq dmDataSeq = (DMDataSeq) dataEvent;
			dmDataSeq.setSequenceNum(1l);
			dmDataSeq.getValues().add(Double.parseDouble(value.toString()));
			// dMDataSeq.getValues().add(66d);
			dmDataSeq.setXAxisStart(Double.valueOf(currentTime - 1000));// 1000
			// ms
			// before
			// current
			// time
			dmDataSeq.getXAxisDeltas().add(Double.valueOf(currentTime - 1000));
		} else if (OsacbmDataType.DM_REAL.equals(osacbmDataType)) {
			dataEvent = new DMReal();
			if (value != null)
				((DMReal) dataEvent).setValue(Double.parseDouble(value.toString()));
		} else if (OsacbmDataType.DA_STRING.equals(osacbmDataType)) {
			dataEvent = new DAString();
			if (value != null)
				((DAString) dataEvent).setValue(value.toString());
		} else
			throw new UnsupportedOperationException("Unable to support creation of " + osacbmDataType);
		// dMDataSeq.getXAxisDeltas().add(Double.valueOf(currentTime - (1000
		// * 2)));
		// dMDataSeq.getXAxisDeltas().add(Double.valueOf(currentTime - (1000
		// * 3)));
		OsaData osaData = new OsaData();
		osaData.setDataEvent(dataEvent);
		fieldData.setData(osaData);
		EngUnit engUnit = new EngUnit();
		fieldData.setEngUnit(engUnit);
		FieldIdentifier fieldIdentifier = new FieldIdentifier();
		fieldIdentifier.setId(fieldId);
		fieldIdentifier.setName(fieldName);
		fieldIdentifier.setSource(fieldSource);
		Field field = new Field();
		fieldData.getField().add(field);
		field.setFieldIdentifier(fieldIdentifier);

		AssetIdentifier assetIdentifier = new AssetIdentifier();
		assetIdentifier.setId(assetId);

		// Asset filter
		AssetFilter assetFilter = new AssetFilter();
		assetFilter.setUri("/asset/assetId");

		// Criteria
		PutFieldDataCriteria putFieldDataCriteria = new PutFieldDataCriteria();
		putFieldDataCriteria.setFieldData(fieldData);
		putFieldDataCriteria.setFilter(assetFilter);

		// Request
		request.getPutFieldDataCriteria().add(putFieldDataCriteria);
		return request;

	}

	/**
	 * @param log
	 *            -
	 * @param request
	 *            -
	 * @param contentType
	 *            -
	 * @param port
	 *            -
	 * @throws UnsupportedEncodingException
	 *             -
	 * @throws IOException
	 *             -
	 * @throws HttpException
	 *             -
	 */
	@SuppressWarnings("nls")
	public void callPutFieldData(final Logger log, final PutFieldDataRequest request, String contentType, String port)
			throws UnsupportedEncodingException, IOException, HttpException {
		if (log == null)
			throw new IllegalArgumentException("log null");

		if (request == null)
			throw new IllegalArgumentException("request null or empty");

		if (this.serverIP == null || this.serverIP.isEmpty())
			throw new IllegalArgumentException("uninitialized: serverIP null or empty");

		//
		List<Header> headers = this.restClient.getOauthHttpHeaders();
		java.util.Map<Integer, Model> modelLookupMap = new HashMap<Integer, Model>();
		PutFieldDataResult result = this.putFieldData.putData(request, modelLookupMap, headers);

		log.debug("Response status code: " + result);

		try {
			Assert.assertNotNull("reply null", result);

			Assert.assertFalse(result.getErrorEvent() == null);
			Assert.assertTrue(result.getErrorEvent().size() == 0);
		} catch (AssertionError e) {
			if (result != null) {
				if (contentType.equals(HTTP_PAYLOAD_XML)) {
					// nice place to put a breakpoint
					String xmlStringPretty = XmlSupport.marshal(result, true);
					log.error(xmlStringPretty, e);
				} else
					log.error(result.toString(), e);
			} else
				log.error("assertionError", e);
			throw e;
		}

	}
}
