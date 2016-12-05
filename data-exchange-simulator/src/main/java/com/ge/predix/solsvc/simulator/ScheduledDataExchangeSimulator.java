/*
 * Copyright (c) 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.simulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.field.Field;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.client.config.IFdhRestConfig;
import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.simulator.types.SimulatorDataNode;

/**
 * 
 * @author predix.adoption@ge.com -
 */
@Component
public class ScheduledDataExchangeSimulator
{
    private static Logger                  log      = LogManager.getLogger(ScheduledDataExchangeSimulator.class);
    /**
     * 
     */
    private static List<SimulatorDataNode> nodeList = new ArrayList<SimulatorDataNode>();

    @Autowired
    private JsonMapper                     mapper;

    @Autowired
    private IFdhRestConfig                 fdhRestCloudConfig;

    @Autowired
    private RestClient                     restClient;

    private String                         serviceURL;

    /**
     * -
     */
    @PostConstruct
    public void init()
    {
        nodeList = new ArrayList<SimulatorDataNode>(10);
        this.serviceURL = this.fdhRestCloudConfig.getPutFieldDataEndPoint();
    }

    /**
     * -
     */
    @SuppressWarnings("nls")
    @Scheduled(fixedDelay = 3000)
    public void simulateData()
    {
        try
        {
            if ( nodeList.isEmpty() )
            {
                InputStream fis = null;
                try
                {
                    log.debug("NodeList empty reading from file");
                    StringWriter writer = new StringWriter();
                    fis = this.getClass().getClassLoader().getResourceAsStream("simulatorconfig.json");
                    IOUtils.copy(fis, writer, "UTF-8");
                    String simulatorConfig = writer.toString();
                    log.debug(simulatorConfig);
                    nodeList = this.mapper.fromJsonArray(simulatorConfig, SimulatorDataNode.class);
                    log.debug("NodeList From File: " + nodeList.size());
                }
                finally
                {
                    try
                    {
                        if ( fis != null ) fis.close();
                    }
                    catch (IOException e)
                    {
                        log.error("unable to close InputStream", e);
                        // swallow exception
                    }
                }
            }
            else
            {
                log.debug("NodeList size : " + nodeList.size());
            }
            PutFieldDataRequest putFieldDataRequest = createDatapointsIngestion(nodeList);
            String json = this.mapper.toJson(putFieldDataRequest);
            log.debug(json);
            postData(json);
        }
        catch (Throwable e)
        {
            log.error("unable to simulate data for nodelist=" + nodeList);
            throw new RuntimeException("unable to simulate data for nodelist=" + nodeList, e);
        }

    }

    private PutFieldDataRequest createDatapointsIngestion(List<SimulatorDataNode> aNodeList)
    {
        DatapointsIngestion datapointsIngestion = createTimeseriesDataBody(aNodeList);
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

        fieldData.setData(datapointsIngestion);
        criteria.setFieldData(fieldData);
        List<PutFieldDataCriteria> list = new ArrayList<PutFieldDataCriteria>();
        list.add(criteria);
        putFieldDataRequest.setPutFieldDataCriteria(list);

        return putFieldDataRequest;
    }

    @SuppressWarnings("nls")
    private DatapointsIngestion createTimeseriesDataBody(List<SimulatorDataNode> aNodeList)
    {
        DatapointsIngestion dpIngestion = new DatapointsIngestion();
        dpIngestion.setMessageId(UUID.randomUUID().toString());
        List<Body> bodies = new ArrayList<Body>();
        // log.info("NodeList : " + this.mapper.toJson(aNodeList));
        for (SimulatorDataNode node : aNodeList)
        {
            Body body = new Body();
            List<Object> datapoints = new ArrayList<Object>();
            body.setName(node.getAssetId() + ":" + node.getNodeName());

            List<Object> datapoint = new ArrayList<Object>();
            datapoint.add(getCurrentTimestamp());
            if ( node.getLowerThreshold() == null ) throw new UnsupportedOperationException(
                    "lower threshold may not be null for nodeName=" + node.getNodeName());
            if ( node.getUpperThreshold() == null ) throw new UnsupportedOperationException(
                    "upper threshold may not be null for nodeName=" + node.getNodeName());
            datapoint.add(generateRandomUsageValue(node.getLowerThreshold(), node.getUpperThreshold()));
            datapoints.add(datapoint);

            body.setDatapoints(datapoints);
            bodies.add(body);
        }
        dpIngestion.setBody(bodies);

        return dpIngestion;
    }

    @SuppressWarnings("nls")
    private void postData(String content)
    {
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("Accept", "application/json"));  //$NON-NLS-2$
        headers.add(new BasicHeader("Content-Type", "application/json"));  //$NON-NLS-2$
        if ( this.serviceURL != null )
        {
            log.debug("Service URL : " + this.serviceURL + " Data : " + content);
            try (CloseableHttpResponse response = this.restClient.post(this.serviceURL, content, headers, 500, 1000);)
            {
                log.debug(
                        "Send Data to Ingestion Service : Response Code : " + response.getStatusLine().getStatusCode());
                String res = this.restClient.getResponse(response);
                if ( response.getStatusLine().getStatusCode() == 200 )
                {
                    log.debug(
                            "Simulator Successfully sent data to serviceURL=" + this.serviceURL + " Response : " + res);
                }
                else
                {
                    log.error("Simulator FAILED to send data to serviceURL=" + this.serviceURL + " Response : " + res);
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
        else
            log.error("Simulator FAILED to send data, serviceURL is empty =" + this.serviceURL);
    }

    private Timestamp getCurrentTimestamp()
    {
        java.util.Date date = new java.util.Date();
        Timestamp ts = new Timestamp(date.getTime());
        return ts;
    }

    private static double generateRandomUsageValue(double low, double high)
    {
        return low + Math.random() * (high - low);
    }

    /**
     * @return the nodeList
     */
    public static List<SimulatorDataNode> getNodeList()
    {
        return ScheduledDataExchangeSimulator.nodeList;
    }

    /**
     * @param nodeList the nodeList to set
     */
    public static void setNodeList(List<SimulatorDataNode> nodeList)
    {
        log.debug("updating nodelist=" + nodeList);
        //if ( 1 == 1 ) throw new RuntimeException("unable to update");
        //ScheduledDataExchangeSimulator.nodeList = nodeList;
    }
}
