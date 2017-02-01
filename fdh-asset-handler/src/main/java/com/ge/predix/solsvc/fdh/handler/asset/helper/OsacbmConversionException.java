/**
 * Copyright (C) 2012 General Electric Company
 * All rights reserved.
 * 
 * Last Updated: Mar 21, 2012
 * By: 200002567
 */
package com.ge.predix.solsvc.fdh.handler.asset.helper;

/**
 * @author 200002567
 * 
 */
public class OsacbmConversionException extends RuntimeException
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -294400015455670896L;

    /**
	 * 
	 */
    public OsacbmConversionException()
    {
        super();
    }

    /**
     * @param message -
     */
    public OsacbmConversionException(String message)
    {
        super(message);
    }

    /**
     * @param cause -
     */
    public OsacbmConversionException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message -
     * @param cause -
     */
    public OsacbmConversionException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
