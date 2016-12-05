/*
 * Copyright (C) 2012 GE Software Center of Excellence.
 * All rights reserved
 */
package com.ge.predix.solsvc.fdh.handler.asset.helper;

import java.util.Map;

import javax.ws.rs.core.MediaType;

/**
 * This class is used to hold the input parameters for HttpClientUtil.
 * 
 */
public class HttpParams
{

    /**
     * 
     */
    public static final String        DEFAULT_CHARSET      = "UTF-8"; //$NON-NLS-1$
    /**
     * 
     */
    public static final String        DEFAULT_CONTENT_TYPE = MediaType.APPLICATION_XML;

    private final String              payload;
    private final String              contentType;
    private final String              charset;
    private final Map<String, String> formParams;
    private String                    endpointUrl;

    /**
     * @param endpointUrl -
     * @param payload -
     * @param contentType - 
     * @param charset -
     * @param formParams - 
     *
     */
    public HttpParams(String endpointUrl, String payload, String contentType, String charset,
            Map<String, String> formParams)
    {
        this.endpointUrl = endpointUrl;
        this.payload = payload;
        this.contentType = contentType;
        this.charset = charset;
        this.formParams = formParams;
    }

    /**
     * @param endpointUrl -
     * @param payload -
     * @param contentType -
     * @param charset -
     */
    public HttpParams(String endpointUrl, String payload, String contentType, String charset)
    {
        this(endpointUrl, payload, contentType, charset, null);
    }

    /**
     * @param endpointUrl -
     * @param formParams -
     */
    public HttpParams(String endpointUrl, Map<String, String> formParams)
    {
        this(endpointUrl, null, DEFAULT_CONTENT_TYPE, DEFAULT_CHARSET, formParams);
    }

    /**
     * @return -
     */
    public Map<String, String> getFormParams()
    {
        return this.formParams;
    }

    /**
     * @return -
     */
    public String getEndpointUrl()
    {
        return this.endpointUrl;
    }

    /**
     * @param endpointUrl -
     */
    public void setEndpointUrl(String endpointUrl)
    {
        this.endpointUrl = endpointUrl;
    }

    /**
     * @return -
     */
    public String getPayload()
    {
        return this.payload;
    }

    /**
     * @return -
     */
    public String getContentType()
    {
        return this.contentType;
    }

    /**
     * @return -
     */
    public String getCharset()
    {
        return this.charset;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "HttpParams{" + "endpointUrl='" + getEndpointUrl() + '\'' + ", payload='" + this.payload + '\''
                + ", contentType='" + this.contentType + '\'' + ", charset='" + this.charset + '\'' + ", formParams="
                + this.formParams + '}';
    }
}
