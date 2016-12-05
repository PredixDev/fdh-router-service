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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ge.predix.entity.datafile.DataFile;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.timeseries.bootstrap.config.DefaultTimeseriesConfig;
import com.ge.predix.solsvc.timeseries.bootstrap.factories.TimeseriesFactory;

/**
 * 
 * @author 212421693
 */
@Component("timeSeriesPutDataFileExecutor")
public class TimeSeriesPutDataFileExecutor {

	private static final Logger log = LoggerFactory.getLogger(TimeSeriesPutDataFileExecutor.class);


	@Autowired
	private JsonMapper mapper;

    @Autowired
    private DefaultTimeseriesConfig timeseriesConfig;
    
    @Autowired
    private TimeseriesFactory timeseriesFactory;
    
    /**
     * @param headers -
     * @param threadName -
     * @param uuidId -
     * @param putFieldDataCriteria -
     */
    void processDataFile(List<Header> headers, String threadName, String uuidId,
            PutFieldDataCriteria putFieldDataCriteria)
    {
        DataFile datafile = (DataFile) putFieldDataCriteria.getFieldData().getData();
        InputStream file = IOUtils.toInputStream(new String((byte[])datafile.getFile()));

        log.info("UUID :" + uuidId + "   working...to upload the file = " + datafile.getName()); //$NON-NLS-1$ //$NON-NLS-2$

        headers.add(new BasicHeader("Origin", "http://predix.io")); //$NON-NLS-1$ //$NON-NLS-2$
        headers.add(new BasicHeader(this.timeseriesConfig.getZoneIdHeader(),
                this.timeseriesConfig.getZoneId()));

        this.timeseriesFactory.createConnectionToTimeseriesWebsocket();
        if (!StringUtils.isEmpty(datafile.getName())
                && datafile.getName().toLowerCase().endsWith("csv")) { //$NON-NLS-1$
            // process csv file
            processUploadCsv(headers, file, uuidId);
        }
        log.info("UUID :" + uuidId + "   " + threadName + " has completed work."); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    }


	/**
	 * @param headers
	 *            -
	 * @param file
	 *            -
	 * @param uuid
	 *            -
	 */

	@SuppressWarnings("nls")
	void processUploadCsv(List<Header> headers, InputStream file, String uuid) {

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
			this.timeseriesFactory.postDataToTimeseriesWebsocket(dpIngestion);
			log.info("UUID :" + uuid + " # records are " + csvRecords.size()); //$NON-NLS-1$ //$NON-NLS-2$

		} catch (IOException | ParseException e) {
		    log.error("UUID :" + uuid + " Error processing upload response ", e); //$NON-NLS-1$ //$NON-NLS-2$
		    throw new RuntimeException("UUID :" + uuid + " Error processing upload response ", e);
		} 
	}
}
