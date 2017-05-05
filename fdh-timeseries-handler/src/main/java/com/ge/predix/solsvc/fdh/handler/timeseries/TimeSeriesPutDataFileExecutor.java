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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.predix.entity.datafile.DataFile;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.entity.util.map.AttributeMap;
import com.ge.predix.entity.util.map.Entry;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.timeseries.bootstrap.client.TimeseriesClient;
import com.ge.predix.solsvc.timeseries.bootstrap.config.DefaultTimeseriesConfig;

/**
 * 
 * @author 212421693
 */
@Profile("timeseries")
@Component("timeSeriesPutDataFileExecutor")
public class TimeSeriesPutDataFileExecutor {

	private static final Logger log = LoggerFactory.getLogger(TimeSeriesPutDataFileExecutor.class);


	@Autowired
	private JsonMapper mapper;

    @Autowired
    @Qualifier("defaultTimeseriesConfig")
    private DefaultTimeseriesConfig timeseriesConfig;
    
    @Autowired
    private TimeseriesClient timeseriesClient;
    
    /**
     * @param headers -
     * @param threadName -
     * @param uuidId -
     * @param putFieldDataCriteria -
     */
    void processDataFile(List<Header> headers, String threadName, String uuidId,
            PutFieldDataCriteria putFieldDataCriteria,PutFieldDataResult putFieldDataResult)
    {
        InputStream file = null;
        try
        {
            DataFile datafile = (DataFile) putFieldDataCriteria.getFieldData().getData();
            file = IOUtils.toInputStream(new String((byte[])datafile.getFile()));

            log.info("UUID :" + uuidId + "   working...to upload the file = " + datafile.getName()); //$NON-NLS-1$ //$NON-NLS-2$

            headers.add(new BasicHeader("Origin", "http://predix.io")); //$NON-NLS-1$ //$NON-NLS-2$
            headers.add(new BasicHeader(this.timeseriesConfig.getZoneIdHeader(),
                    this.timeseriesConfig.getZoneId()));

            this.timeseriesClient.createTimeseriesWebsocketConnectionPool();
            if (!StringUtils.isEmpty(datafile.getName())
                    && (datafile.getName().toLowerCase().endsWith("csv") || datafile.getName().toLowerCase().endsWith("xls"))) { //$NON-NLS-1$
                // process csv file
                processUploadCsv(headers, file, uuidId,putFieldDataResult);
            }else {
                
                throw new NotImplementedException(" Upload not implemented for this extension"); //$NON-NLS-1$
            }
            log.info("UUID :" + uuidId + "   " + threadName + " has completed work."); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        }
        finally {
            if ( file != null ) try
            {
                file.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        
    }

    /**
	 * @param headers
	 *            -
	 * @param file
	 *            -
	 * @param uuid
	 *            -
	 * @param putFieldDataResult -
	 */

	@SuppressWarnings("nls")
	void processUploadCsv(List<Header> headers, InputStream file, String uuid,PutFieldDataResult putFieldDataResult) {
		DatapointsIngestion dpIngestion = new DatapointsIngestion();
		dpIngestion.setMessageId(String.valueOf(System.currentTimeMillis()));
		//List<Body> bodies = new ArrayList<Body>();
		SimpleDateFormat df = null;
		Set<String>tags = new HashSet<String>();
        Map<String , Body> mapOfBodies = new HashMap<String,Body>();  
		try (CSVParser csvFileParser = CSVFormat.EXCEL.withHeader().parse(new InputStreamReader(file))){
			
			List<CSVRecord> csvRecords = csvFileParser.getRecords();
			Map<String, Integer> headerMap = csvFileParser.getHeaderMap();
			for (CSVRecord csvRecord : csvRecords) {
				long epoch = 0;
				int quality = 3;
				Map attributes = null ;
				for (String name : headerMap.keySet()) {
					String key = name.toString();
					String value = csvRecord.get(key);
					if(key.toLowerCase().contains("Date".toLowerCase())){
					//if (StringUtils.startsWithIgnoreCase(key, "Date")) {
						String dataFormattedString = StringUtils.replace(key, "Date", "").replace("(", "").replace(")", "").trim(); //$NON-NLS-1$//$NON-NLS-2$
						df = new SimpleDateFormat(dataFormattedString);
						
						if(!StringUtils.isEmpty(value.replace("(", "").replace(")", ""))){
						    String timevalue= value.replace("(", "").replace(")", "").trim();
						  try { 
    						Date date = df.parse(timevalue);
    						epoch = date.getTime();
						  } catch (ParseException ex) {
						      log.info("error parsing data"+timevalue);
						      continue;
						  }
						}
					}else if (key.toLowerCase().contains("Quality".toLowerCase())) {
					   try{
					        quality= Integer.parseInt(value);
					        
					    } catch ( NumberFormatException ex) {
					        
					        switch (value) {
		                        case "BAD":
		                            quality = 1;break;
		                        case "UNCERTAIN":
		                            quality = 1;break;
		                        case "NA":
		                            quality = 2;break;
		                        default :
		                            quality = 3;break;                      
		                        }  
					    }
						
					}else if (key.toLowerCase().contains("attributes".toLowerCase())) {
					    attributes = new ObjectMapper().readValue(value, HashMap.class);
					}
					else {
					
						String tagName = StringUtils.replace(StringUtils.replace(StringUtils.replace(StringUtils.replace(StringUtils.replace(StringUtils.replace(key," ", ""),"(",""),")",""),"/",""),":",""),"-","").trim();
						tags.add(tagName);
						
						List<Object> dp = new ArrayList<Object>();
						dp.add(epoch);
						dp.add(new Double(value));
						dp.add(quality);
						
						if(! mapOfBodies.containsKey(tagName))  {
						    Body body = new Body();
						    body.setName(tagName);
	                        body.setAttributes((com.ge.predix.entity.util.map.Map) attributes);
						    body.getDatapoints().add(dp);
						    mapOfBodies.put(tagName, body); 
						}
						mapOfBodies.get(tagName).getDatapoints().add(dp);
					}
					
				}
			} // record close
			
			for (java.util.Map.Entry<String, Body> entry : mapOfBodies.entrySet())
			{
			    List<Body> bodies = new ArrayList<>();
			    bodies.add(entry.getValue());
			    dpIngestion.setBody(bodies);
			    log.trace("Ingestion for Tags :" + entry.getKey() + " injection" + dpIngestion.toString()); //$NON-NLS-1$ //$NON-NLS-2$
	            log.info(this.mapper.toPrettyJson(dpIngestion));
	            this.timeseriesClient.postDataToTimeseriesWebsocket(dpIngestion);
			          
			}
			
			// once done send the entries back to metaData
			List<Entry> entryList =new ArrayList<Entry>();
			 Entry entry = new Entry();
	         entry.setKey("uploadIds");
	         String tagString= StringUtils.collectionToDelimitedString(tags, ",");
	         entry.setValue(tagString);
	         entryList.add(entry);
	          
	         entry = new Entry();
	         entry.setKey("uploadCount");
	         entry.setValue(csvRecords.size());
	         entryList.add(entry);
	         
	         if(putFieldDataResult.getExternalAttributeMap() == null ) {
	             AttributeMap attributeMap = new AttributeMap();
	             putFieldDataResult.setExternalAttributeMap(attributeMap);
	         }
	         
	         // uploadedIds: updated here on the putFieldDataResultrequest
	         Set<String> set = new HashSet<String>();
	         putFieldDataResult.getExternalAttributeMap().setEntry(entryList);
	            
			log.info("UUID :" + uuid + " # records are " + csvRecords.size()); //$NON-NLS-1$ //$NON-NLS-2$
			

		} catch (IOException e) {
		    log.error("UUID :" + uuid + " Error processing upload response ", e); //$NON-NLS-1$ //$NON-NLS-2$
		    throw new RuntimeException("UUID :" + uuid + " Error processing upload response ", e);
		} 
	}
	
}
