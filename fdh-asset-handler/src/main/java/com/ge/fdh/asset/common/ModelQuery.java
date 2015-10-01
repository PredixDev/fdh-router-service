/*
 * Copyright (c) 2012 - 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.fdh.asset.common;

/**
 * 
 * @author 212325745
 */
@SuppressWarnings(
{
    "nls"
})
public class ModelQuery extends Query<Object>
{
    private String model;


    /**
     * -
     */
    public ModelQuery()
    {
        super(Object.class);
    }

    /*
     * (non-Javadoc)
     * @see com.ge.fdh.asset.common.Query#getQuery()
     */
    @Override
    public String getQuery()
    {
        String query = null;
        // check for query by asset id
        if ( this.uriString != null )
        {
            query = this.uriString;
            if ( this.attributeString != null )
                query += this.filterString + this.attributeString;
        }
        else
        {
            // check for query by one or more attributes
            if ( this.attributeString != null )
            {
                query = this.model + this.filterString + this.attributeString;
            }
        }
        return query;
    }

    /**
     * @param uri -
     * @return -
     */
    public ModelQuery setUri(String uri)
    {
        if ( uri != null )
        {
            this.uriString = uri;
        }
        return this;
    }


    /**
     * @param attributeName -
     * @param operator -
     * @param value -
     * @return -
     */
    public ModelQuery addAttributeFilter(String attributeName, Operator operator, String value)
    {
        if ( attributeName != null && operator != null && value != null )
        {
            if ( attributeName.equals("assetId") && (value.startsWith("/asset/") || value.startsWith("asset/"))) {
                attributeName = "assetId";
                value = value.substring(value.lastIndexOf('/')+1);
            }
                
            if ( attributeName.equals("uri")) {
                this.uriString = value;
            }
            else
            {
                if ( this.attributeString != null )
                {
                    this.attributeString += ":" + attributeName;
                }
                else
                {
                    this.attributeString = attributeName;
                }

                if ( operator != null && value != null )
                {
                    switch (operator)
                    {
                        case EQ:
                        {
                            this.attributeString += "=" + value;
                        }
                            break;
                        case GE:
                        {
                            this.attributeString += "=" + value + "...*";
                        }
                            break;
                        case LE:
                        {
                            this.attributeString += "=*..." + value;
                        }
                            break;
                        default:
                        {
                            throw new IllegalArgumentException("Operator not supported.");
                        }
                    }
                }
            }
        }
        return this;
    }
    
    /**
     * @param model -
     */
    public void setModel(String model)
    {
        this.model = model;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.getQuery() + " " + super.toString();
    }


}
