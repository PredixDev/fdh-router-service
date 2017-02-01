package com.ge.predix.solsvc.fdh.handler.asset.utils;

import java.nio.charset.Charset;

import org.springframework.http.HttpStatus;

/**
 * 
 * @author predix -
 */
public class AmsHttpServerErrorException extends AmsJsonErrorMessageException
{
    private static final long serialVersionUID = 1L;

    /**
     * @param statusCode -
     * @param statusText -
     * @param body -
     * @param charsetl -
     */
    public AmsHttpServerErrorException(HttpStatus statusCode, String statusText, String body, Charset charsetl)
    {
        super(statusCode, statusText, body, charsetl);
    }

}
