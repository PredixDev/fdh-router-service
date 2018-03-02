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
 * @param <T> -
 */
@SuppressWarnings(
{
})
public abstract class Query<T>
{
    private final Class<T> type;
    /**
     * 
     */
    protected final String filterString = "?filter="; //$NON-NLS-1$
    /**
     * 
     */
    protected String uriString;
    /**
     * 
     */
    protected String attributeString;
    /**
     * @return -
     */
    public Class<T> getClassOfT()
    {
        return this.type;
    }

    /**
     * @param type -
     */
    public Query(Class<T> type)
    {
        this.type = type;
    }
    
    /**
     * @return -
     */
    public abstract String build();
    


}
