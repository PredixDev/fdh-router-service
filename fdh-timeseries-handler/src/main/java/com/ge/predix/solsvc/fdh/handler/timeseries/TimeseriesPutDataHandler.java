/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.handler.timeseries;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.datafile.DataFile;
import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.entity.util.map.AttributeMap;
import com.ge.predix.entity.util.map.Entry;
import com.ge.predix.solsvc.fdh.handler.PutDataHandler;
import com.ge.predix.solsvc.timeseries.bootstrap.factories.TimeseriesFactory;

/**
 * PutFieldDataProcessor processes PutFieldDataRequest - Puts data in the time
 * series handlers -
 * 
 * @author predix
 */
@SuppressWarnings("nls")
@Component(value = "timeseriesPutFieldDataHandler")
@ImportResource(
{
        "classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml",
        "classpath*:META-INF/spring/predix-websocket-client-scan-context.xml",
        "classpath*:META-INF/spring/fdh-timeseries-handler-scan-context.xml"
})
@Profile("timeseries")
public class TimeseriesPutDataHandler implements PutDataHandler {
	private static final Logger log = LoggerFactory.getLogger(TimeseriesPutDataHandler.class);

	@Autowired
	@Qualifier("timeSeriesPutDataFileExecutor")
	private TimeSeriesPutDataFileExecutor timeSeriesPutDataFileExecutor;
	
    @Autowired
    private TimeseriesFactory timeseriesFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ge.fdh.asset.processor.IPutFieldDataProcessor#processRequest(com.
	 * ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataRequest)
	 */
	@Override
	public PutFieldDataResult putData(PutFieldDataRequest request, Map<Integer, Model> modelLookupMap,
			List<Header> headers, String httpMethod) {
		try {
			UUID idOne = UUID.randomUUID();
			PutFieldDataResult putFieldDataResult = getPutFieldDataResult(idOne);
		     String threadName = Thread.currentThread().getName();
		        String uuidId = ""; //$NON-NLS-1$
		        List<Entry> entries = putFieldDataResult.getExternalAttributeMap().getEntry();
		        for (Entry entry : entries) {
		            if (org.apache.commons.lang.StringUtils.endsWithIgnoreCase(entry.getKey().toString(), "UUID")) { //$NON-NLS-1$
		                uuidId = entry.getValue().toString();
		            }

		        }

		        log.info("UUID :" + uuidId + "   " + threadName + " has began working."); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

		        List<PutFieldDataCriteria> fieldDataCriteria = request.getPutFieldDataCriteria();
		        for (PutFieldDataCriteria putFieldDataCriteria : fieldDataCriteria) {
		            if (putFieldDataCriteria.getFieldData().getData() instanceof DataFile) {
		                this.timeSeriesPutDataFileExecutor.processDataFile(headers, threadName, uuidId, putFieldDataCriteria);
		            } else if (putFieldDataCriteria.getFieldData().getData() instanceof DatapointsIngestion) {
                        processDatapointsIngestion(putFieldDataCriteria);
                    }
		            else
		                throw new UnsupportedOperationException("unable to process data=" + putFieldDataCriteria.getFieldData().getData());
		        }

			return putFieldDataResult;
		} catch (Throwable e) {
			String msg = "unable to process request errorMsg=" + e.getMessage() + " request.correlationId="
					+ request.getCorrelationId() + " request = " + request;
			log.error(msg, e);
			RuntimeException dspPmException = new RuntimeException(msg, e);
			throw dspPmException;
		}
	}


    /**
     * @param putFieldDataCriteria -
     */
    private void processDatapointsIngestion(PutFieldDataCriteria putFieldDataCriteria)
    {
        DatapointsIngestion datapointsIngestion = (DatapointsIngestion) putFieldDataCriteria.getFieldData().getData();
        this.timeseriesFactory.createConnectionToTimeseriesWebsocket();
        this.timeseriesFactory.postDataToTimeseriesWebsocket(datapointsIngestion);
    }

	/**
	 * @param idOne
	 * @return
	 */
	private PutFieldDataResult getPutFieldDataResult(UUID idOne) {
		PutFieldDataResult putFieldDataResult = new PutFieldDataResult();
		AttributeMap attributeMap = new AttributeMap();
		Entry uuidEntru = new Entry();
		uuidEntru.setKey("UUID");
		uuidEntru.setValue(idOne.toString());
		attributeMap.getEntry().add(uuidEntru);
		putFieldDataResult.setExternalAttributeMap(attributeMap);
		return putFieldDataResult;
	}

}
