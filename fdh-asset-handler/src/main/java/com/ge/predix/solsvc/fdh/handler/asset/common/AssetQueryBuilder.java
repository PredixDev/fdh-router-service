/*
 * Copyright (c) 2012 - 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.handler.asset.common;

/**
 * 
 * @author 212325745
 */
@SuppressWarnings(
{
    "nls"
})
public class AssetQueryBuilder extends Query<Object>
{
    private String model;


    /**
     * -
     */
    public AssetQueryBuilder()
    {
        super(Object.class);
    }

    /*
     * (non-Javadoc)
     * @see com.ge.fdh.asset.common.Query#getQuery()
     */
    @Override
    public String build()
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
    public AssetQueryBuilder setUri(String uri)
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
    public AssetQueryBuilder addAttributeFilter(String attributeName, Operator operator, String value)
    {
    	String localAttributeName = attributeName;
    	String localValue = value;
        if ( localAttributeName != null && operator != null && localValue != null )
        {
            if ( localAttributeName.equals("assetId") && (localValue.startsWith("/asset/") || localValue.startsWith("asset/"))) {
            	localAttributeName = "assetId";
            	localValue = localValue.substring(localValue.lastIndexOf('/')+1);
            }
                
            if ( localAttributeName.equals("uri")) {
                this.uriString = localValue;
            }
            else
            {
                if ( this.attributeString != null )
                {
                    this.attributeString += ":" + localAttributeName;
                }
                else
                {
                    this.attributeString = localAttributeName;
                }

                if ( operator != null && localValue != null )
                {
                    switch (operator)
                    {
                        case EQ:
                        {
                            this.attributeString += "=" + localValue;
                        }
                            break;
                        case GE:
                        {
                            this.attributeString += "=" + localValue + "...*";
                        }
                            break;
                        case LE:
                        {
                            this.attributeString += "=*..." + localValue;
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
        return this.build() + " " + super.toString();
    }


}
