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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.entity.util.map.AttributeMap;
import com.ge.predix.entity.util.map.Entry;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.handler.GetDataHandler;
import com.ge.predix.solsvc.fdh.handler.PutDataHandler;

/**
 * 
 * @author predix -
 */
@Component(value = "webSocketHandler")
@ImportResource(
{
        "classpath*:META-INF/spring/fdh-websocket-handler-scan-context.xml"

})
public class WebsocketHandler
        implements GetDataHandler, PutDataHandler
{
    private static final Logger log = LoggerFactory.getLogger(WebsocketHandler.class);

    @Autowired
    private JsonMapper          mapper;

    /*
     * (non-Javadoc)
     * @see com.ge.predix.solsvc.fdhcontentbasedrouter.GetFieldDataInterface#
     * getFieldData(java.util.List,
     * com.ge.predix.entity.getfielddata.GetFieldDataRequest)
     */
    @SuppressWarnings("nls")
    @Override
    public GetFieldDataResult getData(GetFieldDataRequest request, Map<Integer, Object> modelLookupMap,
            List<Header> headers)
    {
        throw new UnsupportedOperationException("unimplemented");
    }

    /*
     * (non-Javadoc)
     * @see
     * com.ge.predix.solsvc.fdh.handler.PutFieldDataInterface#processRequest(com
     * .ge.predix.entity.putfielddata.PutFieldDataRequest, java.util.List)
     */
    @SuppressWarnings(
    {
            "nls", "unchecked", "resource"
    })
    @Override
    public PutFieldDataResult putData(PutFieldDataRequest request, Map<Integer, Object> modelLookupMap,
            List<Header> headers, String httpMethod)
    {
        PutFieldDataResult result = new PutFieldDataResult();
        validate();
        AttributeMap attrMap = request.getExternalAttributeMap();
        if ( attrMap != null )
        {
            List<Entry> entries = attrMap.getEntry();
            List<Session> sessions = new ArrayList<Session>();
            Session currentSession = null;
            for (Entry entry : entries)
            {
                if ( "SESSIONS".equals(entry.getKey().toString()) )
                {
                    sessions = (List<Session>) entry.getValue();
                }
                if ( "SESSION".equals(entry.getKey().toString()) )
                {
                    currentSession = (Session) entry.getValue();
                }
            }
            if ( currentSession == null || sessions == null || sessions.size() == 0 )
            {
                result.getErrorEvent().add("ERROR: No sessions to process");
                return result;
            }
            for (PutFieldDataCriteria criteria : request.getPutFieldDataCriteria())
            {
                FieldData fieldData = criteria.getFieldData();
                DatapointsIngestion dpIngestion = null;
                if ( fieldData.getData() instanceof DatapointsIngestion )
                {
                    dpIngestion = (DatapointsIngestion) fieldData.getData();
                    try
                    {
                        Iterator<Body> bodyList = dpIngestion.getBody().iterator();
                        int i=0;
                        while (bodyList.hasNext())
                        {
                            Body body = bodyList.next();
                            String msg = this.mapper.toJson(body);
                            for (Session s : sessions)
                            {
                                log.debug("Session is s" + s.getId() + " URL=" + s.getRequestURI().toString()
                                        + " CurrentSession is c" + currentSession.getId() + " body Name =" + body.getName());
                                if ( s.isOpen() && s.getRequestURI().toString().endsWith(body.getName()) )
                                {
                                    log.debug("sending data for Body[ " + i + "] " + s.getRequestURI().toString() + " message=" + msg);
                                    s.getBasicRemote().sendText(msg);
                                }
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException("Exception when posting data to websocket for criteria=" + criteria.toString(), e);
                    }
                }
            }
        }
        return result;
    }

    /**
     * -
     */
    private void validate()
    {
        // TODO Auto-generated method stub

    }
}
