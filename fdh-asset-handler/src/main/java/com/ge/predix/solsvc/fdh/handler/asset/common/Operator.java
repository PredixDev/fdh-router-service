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
import java.util.regex.Pattern;

/**
 * Operator - equals, greater-than-or-equal, less-than-or-equal, like
 * 
 * @author predix -
 */
public enum Operator
{
    /**
     * 
     */
    EQ, 
    /**
     * 
     */
    GE
    {
        @Override
        public boolean isValidValue(String value)
        {
            return gePattern.matcher(value).matches();
        }

        @Override
        public Object getValue(String value)
        {
            if ( !isValidValue(value) )
            {
                throw new IllegalArgumentException();
            }
            int idx = value.indexOf("..."); //$NON-NLS-1$
            return Double.valueOf(value.substring(0, idx));
        }
    },
    /**
     * 
     */
    LE
    {
        @Override
        public boolean isValidValue(String value)
        {
            return lePattern.matcher(value).matches();
        }

        @Override
        public Object getValue(String value)
        {
            if ( !isValidValue(value) )
            {
                throw new IllegalArgumentException();
            }
            int idx = value.indexOf("..."); //$NON-NLS-1$
            return Double.valueOf(value.substring(idx + 3));
        }
    };

    /**
     * 
     */
    static Pattern lePattern = Pattern.compile("^\\*[.]{3}[+-]?\\d+(\\.\\d+)?([eE][-+]?[0-9]+)?$"); //$NON-NLS-1$
    /**
     * 
     */
    static Pattern gePattern = Pattern.compile("^[+-]?\\d+(\\.\\d+)?([eE][-+]?[0-9]+)?[.]{3}\\*$"); //$NON-NLS-1$

    /**
     * @param value -
     * @return -
     */
    public boolean isValidValue(String value)
    {
        return true;
    }

    /**
     * @param value -
     * @return -
     */
    public Object getValue(String value)
    {
        return value;
    }
}
