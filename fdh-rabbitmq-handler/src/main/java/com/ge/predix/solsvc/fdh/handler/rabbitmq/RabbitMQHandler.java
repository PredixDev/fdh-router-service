/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
package com.ge.predix.solsvc.fdh.handler.rabbitmq; 


import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddata.PredixString;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.solsvc.fdh.handler.GetDataHandler;
import com.ge.predix.solsvc.fdh.handler.PutDataHandler;

/**
 * 
 * @author predix -
 */
@Component(value = "rabbitMQPutFieldDataHandler")
@ImportResource(
{
        "classpath*:META-INF/spring/fdh-rabbitmq-handler-scan-context.xml"
        
})
@Profile("rabbitmq")
public class RabbitMQHandler implements GetDataHandler, PutDataHandler
{

	@Autowired
    private RabbitTemplate eventTemplate;

    @Autowired
    private MessageConverter messageConverter;
    
    
	@Value("${fieldChangedEvent.MainQueue}")
    private String mainQ;

    /* (non-Javadoc)
     * @see com.ge.predix.solsvc.fdhcontentbasedrouter.GetFieldDataInterface#getFieldData(java.util.List, com.ge.predix.entity.getfielddata.GetFieldDataRequest)
     */
    @SuppressWarnings("nls")
    @Override
    public GetFieldDataResult getData(GetFieldDataRequest request,Map<Integer, Model> modelLookupMap, List<Header> headers)
    {     
    	throw new UnsupportedOperationException("unimplemented");
    }

    /* (non-Javadoc)
     * @see com.ge.predix.solsvc.fdh.handler.PutFieldDataInterface#processRequest(com.ge.predix.entity.putfielddata.PutFieldDataRequest, java.util.List)
     */
    @SuppressWarnings("nls")
    @Override
    public PutFieldDataResult putData(PutFieldDataRequest request, Map<Integer, Model> modelLookupMap, List<Header> headers, String httpMethod)
    {
        validate();
        for (PutFieldDataCriteria criteria:request.getPutFieldDataCriteria()) {
        	FieldData fieldData = criteria.getFieldData();
        	PredixString data = (PredixString) fieldData.getData();
        	MessageProperties prop = new MessageProperties();
        	prop.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN);
        	
            Message msg = messageConverter.toMessage(data.toString(), prop);           
            this.eventTemplate.convertAndSend(mainQ, msg);
        }

        PutFieldDataResult result = new PutFieldDataResult();
        result.getErrorEvent().add("SampleHandler - put your code here");
        return result;
    }

    /**
     *  -
     */
    private void validate()
    {
        // TODO Auto-generated method stub
        
    }

    public MessageConverter getMessageConverter() {
		return messageConverter;
	}

	public void setMessageConverter(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	public RabbitTemplate getEventTemplate() {
		return eventTemplate;
	}

	public void setEventTemplate(RabbitTemplate eventTemplate) {
		this.eventTemplate = eventTemplate;
	}

}
