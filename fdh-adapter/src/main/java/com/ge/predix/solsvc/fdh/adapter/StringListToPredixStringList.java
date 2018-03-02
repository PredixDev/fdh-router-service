/*
 * Copyright (c) 2017 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.fdh.adapter;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.predix.entity.fielddata.PredixString;

/**
 * 
 * @author 212421693 -
 */
@Component
public class StringListToPredixStringList implements Adapter<List<String>, List<PredixString>>
{
    private static final Logger     log = LoggerFactory.getLogger(StringListToPredixStringList.class);
    private ObjectMapper    mapper          = new ObjectMapper();

    /* (non-Javadoc)
     * @see com.ge.predix.solsvc.fdh.adapter.Adapter#adapt(java.lang.Object)
     */
    @Override
    public List<PredixString> adapt(List<String> source)
    {
        List<PredixString> models=new ArrayList<PredixString>();
        for(String pString:source) {
            PredixString reponseString = new PredixString();
            try
            {
                reponseString.setString(this.getMapper().writeValueAsString(pString));
                models.add(reponseString);
            }
            catch (JsonProcessingException e)
            {
               log.error("Error Parsing Json response"+e); //$NON-NLS-1$
              
            }
            
        }
        return models;
    }

    /**
     * @return the mapper
     */
    public ObjectMapper getMapper()
    {
        return this.mapper;
    }

    /**
     * @param mapper the mapper to set
     */
    public void setMapper(ObjectMapper mapper)
    {
        this.mapper = mapper;
    }

}
