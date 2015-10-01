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

import com.ge.predix.solsvc.bootstrap.ams.dto.Asset;

/**
 * 
 * @author 212325745
 */
@SuppressWarnings(
{
        "nls"
})
public class AssetQuery extends Query<Asset>
{
    
    private String assetIdString;
    protected String classificationString;

    /**
     *  -
     */
    public AssetQuery()
    {
        super(Asset.class);
    }

    /**
     * @param uri -
     */
    public AssetQuery(String uri)
    {
        super(Asset.class);
        this.uriString = uri;
    }

    /* (non-Javadoc)
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
        }
        else if ( this.assetIdString != null )
        {
            query = this.filterString + this.assetIdString;
        }
        else
        {
            // check for query by classification
            if ( this.classificationString != null )
            {
                query = this.filterString + this.classificationString;
            }
            // check for query by one or more attributes
            // it can be combined with classification
            if ( this.attributeString != null )
            {
                if ( query == null )
                {
                    query = this.filterString + this.attributeString;
                }
                else
                {
                    query += ":" + this.attributeString;
                }
            }
        }
        return query;
    }

    /**
     * @param uri -
     * @return -
     */
    public AssetQuery addUriFilter(String uri)
    {
        if ( uri != null )
        {
            this.uriString = uri;
        }
        return this;
    }


    /**
     * @param assetId -
     * @return -
     */
    public AssetQuery addAssetIdFilter(String assetId)
    {
        if ( assetId != null )
        {
            this.assetIdString = "assetId=" + assetId;
        }
        return this;
    }

    /**
     * @param attributeName -
     * @param operator -
     * @param value -
     * @return -
     */
    public AssetQuery addAttributeFilter(String attributeName, Operator operator, String value)
    {
        if ( attributeName != null && operator != null && value != null )
        {
            if ( attributeName.equals("assetId") && (value.startsWith("/asset/") || value.startsWith("asset/"))) {
                attributeName = "assetId";
                value = value.substring(value.lastIndexOf('/')+1);
            }
                
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
        return this;
    }
    
    /**
     * @param classificationUri -
     * @return -
     */
    public Query addClassificationFilter(String classificationUri)
    {
        if ( classificationUri != null )
        {
            this.classificationString = "classification=" + classificationUri;
        }
        return this;
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
