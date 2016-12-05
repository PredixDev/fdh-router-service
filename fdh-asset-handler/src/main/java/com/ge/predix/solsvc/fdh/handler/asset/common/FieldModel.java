/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.fdh.handler.asset.common;

import java.util.List;

import com.ge.predix.solsvc.fdh.handler.asset.helper.PaUtility;

/**
 * 
 * @author predix -
 */
public class FieldModel
{

    private String model;
    private List<String> childModels;
    private String attribute;
    
    private String field;

    /**
     * @param model -
     */
    public void setModel(String model)
    {
        this.model = model;
        
    }

    /**
     * @return the childModels
     */
    public List<String> getChildModels()
    {
        return this.childModels;
    }

    /**
     * @param childModels -
     */
    public void setChildModels(List<String> childModels)
    {
        this.childModels = childModels;
        
    }

    /**
     * @param attribute -
     */
    public void setAttribute(String attribute)
    {
        this.attribute = attribute;
        
    }

    /**
     *  -
     * @return -
     */
    public String getModel()
    {
        return this.model;
        
    }

    /**
     * @return -
     */
    public String getAtrribute()
    {
        return this.attribute;
    }

    /**
     * @return -
     */
    @SuppressWarnings("nls")
    public String getModelForUnMarshal()
    {
        String modelForUnmarshal = this.model;
        if ( !this.model.contains("."))
            modelForUnmarshal = PaUtility.firstCharUpper(modelForUnmarshal);
        return modelForUnmarshal;
    }

    /**
     *  -
     * @return -
     */
    public String getField()
    {
        return this.field;
    }

    /**
     * @param field -
     */
    public void setField(String field)
    {
        this.field = field;    
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "FieldModel [attribute=" + this.attribute + ", model=" + this.model + ", childModels=" + this.childModels + ", field="
                + this.field + "]";
    }


}
