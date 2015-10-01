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

import com.ge.predix.solsvc.bootstrap.ams.dto.Classification;


/**
 * 
 * @author 212325745
 */
@SuppressWarnings(
{
        "nls"
})
public class ClassificationQuery extends Query<Classification>
{
    private String filterString = "?filter=";
    private String uriString;
    private String nameString;

    /**
     *  -
     */
    public ClassificationQuery()
    {
        super(Classification.class);
    }

    /**
     * @param uri -
     */
    public ClassificationQuery(String uri)
    {
        super(Classification.class);
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
        else if ( this.nameString != null )
        {
            query = this.filterString + this.nameString;
        }

        return query;
    }

    /**
     * @param name -
     * @return -
     */
    public ClassificationQuery addNameFilter(String name)
    {
        if ( name != null )
        {
            this.nameString = "name=" + name;
        }
        return this;
    }

}
