/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
package com.ge.predix.solsvc.fdh.handler.websocket; 


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ge.predix.entity.datafile.DataFile;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.handler.GetDataHandler;
import com.ge.predix.solsvc.fdh.handler.PutDataHandler;
import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.websocket.client.WebSocketClient;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;

/**
 * 
 * @author predix -
 */
@Component(value = "webSocketHandler")
@ImportResource(
{
        "classpath*:META-INF/spring/fdh-websocket-handler-scan-context.xml"
        
})

public class WebsocketHandler implements GetDataHandler, PutDataHandler
{
	private static final Logger log = LoggerFactory.getLogger(WebsocketHandler.class);
			
	@Autowired
	private WebSocketClientConfig webSocketClientConfig;
	
	@Autowired
	private WebSocketClient webSocketClient;
	
	@Autowired
	private JsonMapper mapper;
	
	@Autowired
	private RestClient restClient;
	
	/**
	 * 
	 */
	private WebSocketAdapter messageListener = new WebSocketAdapter() {
		@SuppressWarnings("nls")
		@Override
		public void onTextMessage(WebSocket wsocket, String message) {
			log.info("Websocket Client Handler : Recieved success message from " + wsocket.getURI() + " : " + message);
		}
	};
	
	@PostConstruct
	public void init() {
		//List<Header> headers = restClient.getSecureTokenForClientId();
		this.webSocketClient.overrideWebSocketConfig(webSocketClientConfig);
		this.webSocketClient.init(restClient, new ArrayList<Header>(), messageListener);
	}
    /* (non-Javadoc)
     * @see com.ge.predix.solsvc.fdhcontentbasedrouter.GetFieldDataInterface#getFieldData(java.util.List, com.ge.predix.entity.getfielddata.GetFieldDataRequest)
     */
    @Override
    public GetFieldDataResult getData(GetFieldDataRequest request,Map<Integer, Model> modelLookupMap, List<Header> headers)
    {        
    	throw new UnsupportedOperationException("unimplemented");
    }

    /* (non-Javadoc)
     * @see com.ge.predix.solsvc.fdh.handler.PutFieldDataInterface#processRequest(com.ge.predix.entity.putfielddata.PutFieldDataRequest, java.util.List)
     */
    @Override
    public PutFieldDataResult putData(PutFieldDataRequest request, Map<Integer, Model> modelLookupMap, List<Header> headers, String httpMethod)
    {
        validate();
        for (PutFieldDataCriteria criteria:request.getPutFieldDataCriteria()) {
        	FieldData fieldData = criteria.getFieldData();
        	DatapointsIngestion dpIngestion = null;
        	if (fieldData.getData() instanceof DataFile) {
        		DataFile datafile = (DataFile) criteria.getFieldData().getData();
        		InputStream file = IOUtils.toInputStream(new String((byte[])datafile.getFile()));
        		processUploadCsv(headers, file, UUID.randomUUID().toString());
        	}else if (fieldData.getData() instanceof DatapointsIngestion) {
        		dpIngestion = (DatapointsIngestion)fieldData.getData();
        		String payload = mapper.toJson(dpIngestion);
            	log.info("Payload : "+payload);
            	try {
    				this.webSocketClient.postTextWSData(payload);
    			} catch (IOException|WebSocketException e) {
    				throw new RuntimeException("Exception when posting data to websocket",e);
    			}
        	}
        }
        PutFieldDataResult result = new PutFieldDataResult();
        result.getErrorEvent().add("Data Sent to Websocket Server");
        return result;
    }

    /**
     *  -
     */
    private void validate()
    {
        // TODO Auto-generated method stub
        
    }
    
    private void processUploadCsv(List<Header> headers, InputStream file, String uuid) {

    	DatapointsIngestion dpIngestion = new DatapointsIngestion();
		dpIngestion.setMessageId(UUID.randomUUID().toString());
		List<Body> bodies = new ArrayList<Body>();
		SimpleDateFormat df = null;

		try (CSVParser csvFileParser = CSVFormat.EXCEL.withHeader().parse(new InputStreamReader(file))){
			
			List<CSVRecord> csvRecords = csvFileParser.getRecords();
			Map<String, Integer> headerMap = csvFileParser.getHeaderMap();
			for (CSVRecord csvRecord : csvRecords) {
				long epoch = 0;
				int quality = 3;
				for (String name : headerMap.keySet()) {
					List<Object> datapoint = new ArrayList<Object>();
					String key = name.toString();
					String value = csvRecord.get(key);
					if (StringUtils.startsWithIgnoreCase(key, "Date")) {
						String dataFormattedString = StringUtils.replace(key, "Date(", ""); //$NON-NLS-1$//$NON-NLS-2$
						String dataformat = StringUtils.replace(dataFormattedString, ")", ""); //$NON-NLS-1$//$NON-NLS-2$
						df = new SimpleDateFormat(dataformat);
						Date date = df.parse(value);
						epoch = date.getTime();
					}else if (StringUtils.endsWithIgnoreCase(key, "Quality")) {
						switch (value) {
						case "BAD":
							quality = 0;break;
						case "UNCERTAIN":
							quality = 1;break;
						case "NA":
							quality = 2;break;
						default :
							quality = 3;break;						
						}
					}else {
						Body body = new Body();
						String tagName = StringUtils.replace(StringUtils.replace(StringUtils.replace(StringUtils.replace(StringUtils.replace(StringUtils.replace(key," ", ""),"(",""),")",""),"/",""),":",""),"-","").trim();
						body.setName(tagName);
						datapoint.add(epoch);
						datapoint.add(new Double(value));
						datapoint.add(quality);
						body.setDatapoints(datapoint);
						bodies.add(body);
					}
					
				}
			} // record close
			dpIngestion.setBody(bodies);
			log.trace("UUID :" + uuid + " injection" + dpIngestion.toString()); //$NON-NLS-1$ //$NON-NLS-2$
			log.info(this.mapper.toPrettyJson(dpIngestion));
			log.info("WS URL : "+this.webSocketClientConfig.getWsUri());
			this.webSocketClient.postTextWSData(this.mapper.toJson(dpIngestion));
			log.info("UUID :" + uuid + " # records are " + csvRecords.size()); //$NON-NLS-1$ //$NON-NLS-2$

		} catch (IOException | ParseException | WebSocketException e) {
		    log.error("UUID :" + uuid + " Error processing upload response ", e); //$NON-NLS-1$ //$NON-NLS-2$
		    throw new RuntimeException("UUID :" + uuid + " Error processing upload response ", e);
		} 
    }
}
