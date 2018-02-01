/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
package com.ge.predix.solsvc.fdh.handler.edgemanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.edgemanager.Device;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.entity.util.map.AttributeMap;
import com.ge.predix.entity.util.map.Entry;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.handler.GetDataHandler;
import com.ge.predix.solsvc.fdh.handler.PutDataHandler;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * 
 * @author predix -
 */
@Component
@ImportResource({ "classpath*:META-INF/spring/dx-edgemanager-handler-scan-context.xml" })
public class EdgeManagerHandler implements GetDataHandler, PutDataHandler {

	private static final Logger log = LoggerFactory.getLogger(EdgeManagerHandler.class.getName());

	@Value("${predix.edgemanager.oauth.clientId}")
	private String edgeManagerClient;

	@Value("${predix.edgemanager.api.baseurl}")
	private String edgeManagerAPIBaseURL;

	@Value("${predix.edgemanager.tenantId}")
	private String tenantId;
	@Autowired
	private RestClient restClient;

	@Autowired
	private JsonMapper jsonMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ge.predix.solsvc.fdhcontentbasedrouter.GetFieldDataInterface#
	 * getFieldData(java.util.List,
	 * com.ge.predix.entity.getfielddata.GetFieldDataRequest)
	 */
	@Override
	public GetFieldDataResult getData(GetFieldDataRequest request, Map<Integer, Object> modelLookupMap,
			List<Header> headers) {
		addEdgeManagerHeaders(headers);
		GetFieldDataResult result = new GetFieldDataResult();
		for (FieldDataCriteria criteria : request.getFieldDataCriteria()) {
			String deviceId = criteria.getFieldSelection().get(0).getFieldIdentifier().getId().toString();
			String url = this.edgeManagerAPIBaseURL + "/device-management/devices/"+deviceId; //$NON-NLS-1$
			try (CloseableHttpResponse response = this.restClient.get(url, headers)) {
				HttpEntity responseEntity = response.getEntity();
				String device = EntityUtils.toString(responseEntity);

				Device d = this.jsonMapper.fromJson(device, Device.class);
				log.info("device : " + this.jsonMapper.toJson(d)); //$NON-NLS-1$
				
				FieldData fieldData = new FieldData();
				fieldData.setData(d);
				result.getFieldData().add(fieldData);
			} catch (IOException e) {
				result.getErrorEvent().add("Exception when querying device"+e.getMessage()); //$NON-NLS-1$
				log.error("Exception when querying device",e); //$NON-NLS-1$
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ge.predix.solsvc.fdh.handler.PutFieldDataInterface#processRequest(com
	 * .ge.predix.entity.putfielddata.PutFieldDataRequest, java.util.List)
	 */
	@Override
	public PutFieldDataResult putData(PutFieldDataRequest singleRequest, Map<Integer, Object> modelLookupMap,
			List<Header> headers, String httpMethod) {
		log.info("httpMethod : " + httpMethod); //$NON-NLS-1$
		validate();
		UUID idOne = UUID.randomUUID();
		PutFieldDataResult putFieldDataResult = getPutFieldDataResult(idOne);

		String threadName = Thread.currentThread().getName();
		String uuidId = ""; //$NON-NLS-1$
		List<Entry> entries = putFieldDataResult.getExternalAttributeMap().getEntry();
		for (Entry entry : entries) {
			if (entry.getKey().toString().equalsIgnoreCase("UUID")) { //$NON-NLS-1$
				uuidId = entry.getValue().toString();
			}
		}

		log.info("UUID :" + uuidId + "   " + threadName + " has began working."); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

		String url = this.edgeManagerAPIBaseURL + "/device-management/devices"; //$NON-NLS-1$
		addEdgeManagerHeaders(headers);
		for (PutFieldDataCriteria criteria : singleRequest.getPutFieldDataCriteria()) {
			FieldData fieldData = criteria.getFieldData();
			Device device = (Device) fieldData.getData();
			
			switch (httpMethod) {
			case HttpPost.METHOD_NAME:
				
				String deviceStr = this.jsonMapper.toJson(device).replaceAll("\"complexType\":\"Device\",", ""); //$NON-NLS-1$ //$NON-NLS-2$
				log.info("Creating device : "+deviceStr); //$NON-NLS-1$
				try (CloseableHttpResponse response = this.restClient.post(url, deviceStr, headers,
						this.restClient.getRestConfig().getDefaultConnectionTimeout(),
						this.restClient.getRestConfig().getDefaultSocketTimeout());) {
					int statusCode = response.getStatusLine().getStatusCode();
					log.info("Status Code : " + statusCode + " : " + response.getStatusLine().getReasonPhrase()); //$NON-NLS-1$ //$NON-NLS-2$
					switch (statusCode) {
					case HttpStatus.SC_CREATED:
						log.info(device.getDeviceId() + " created successfully"); //$NON-NLS-1$
						break;
					default:
						break;
					}
				} catch (IOException e) {
					log.error("Exception when creating device " + device.getDeviceId(), e); //$NON-NLS-1$
				}
				break;
			case HttpDelete.METHOD_NAME:
				String deviceId = device.getDeviceId();
				String deleteURL = url + "/" + deviceId; //$NON-NLS-1$
				try (CloseableHttpResponse response = this.restClient.delete(deleteURL, headers,
						this.restClient.getRestConfig().getDefaultConnectionTimeout(),
						this.restClient.getRestConfig().getDefaultSocketTimeout());) {
					int statusCode = response.getStatusLine().getStatusCode();
					switch (statusCode) {
					case HttpStatus.SC_NO_CONTENT:
						log.info(deviceId + " deleted successfully"); //$NON-NLS-1$
						break;
					default:
						break;
					}
				} catch (IOException e) {
					putFieldDataResult.getErrorEvent().add("Exception when deleting device " + deviceId+" : "+e.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
					log.error("Exception when deleting device " + deviceId, e); //$NON-NLS-1$
				}
				break;
			default:
				break;
			}
		}
		return putFieldDataResult;
	}

	/**
	 * -
	 */
	private void validate() {
		// TODO Auto-generated method stub

	}

	/**
	 * @param headers
	 *            -
	 */
	private void addEdgeManagerHeaders(List<Header> headers) {
		String token = this.restClient.requestToken(this.edgeManagerClient);
		Header bearer = new BasicHeader("Authorization", token); //$NON-NLS-1$
		headers.add(bearer);
		Header zoneId = new BasicHeader("Predix-Zone-Id", this.tenantId); //$NON-NLS-1$
		headers.add(zoneId);
		Header contentType = new BasicHeader("Content-Type", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
		headers.add(contentType);
		Header accepts = new BasicHeader("Accept", "application/json"); //$NON-NLS-1$ //$NON-NLS-2$
		headers.add(accepts);

	}

	/**
	 * @param idOne
	 * @return
	 */
	private PutFieldDataResult getPutFieldDataResult(UUID idOne) {
		PutFieldDataResult putFieldDataResult = new PutFieldDataResult();
		AttributeMap attributeMap = new AttributeMap();
		Entry uuidEntru = new Entry();
		uuidEntru.setKey("UUID"); //$NON-NLS-1$
		uuidEntru.setValue(idOne.toString());
		attributeMap.getEntry().add(uuidEntru);
		putFieldDataResult.setExternalAttributeMap(attributeMap);
		putFieldDataResult.setErrorEvent(new ArrayList<String>());
		return putFieldDataResult;
	}
}
