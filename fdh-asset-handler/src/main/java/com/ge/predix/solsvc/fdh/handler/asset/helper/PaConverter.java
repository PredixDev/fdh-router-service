/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.handler.asset.helper;

import org.mimosa.osacbmv3_3.OsacbmDataType;

import com.ge.predix.entity.attributedef.AttributeTypeEnum;
import com.ge.predix.solsvc.fdh.handler.asset.common.RestConstants;

/**
 * 
 * @author 212369540
 */

public class PaConverter
{

    /**
     * @param attributeType -
     * @return -
     */
    static public AttributeTypeEnum convertToAttributeTypeEnum(String attributeType)
    {
        if ( attributeType != null )
        {
            switch (attributeType)
            {
                case RestConstants.URI_PRIMITIVE_BOOLEAN:
                    return AttributeTypeEnum.BOOLEAN;
                case RestConstants.URI_PRIMITIVE_DATE:
                    return AttributeTypeEnum.DATE;
                case RestConstants.URI_PRIMITIVE_STRING:
                    return AttributeTypeEnum.STRING;
                case RestConstants.URI_PRIMITIVE_NUMBER:
                default:
                    return AttributeTypeEnum.DOUBLE;
            }
        }
        return AttributeTypeEnum.OTHER;
    }

    /**
     * @param attributeType -
     * @return -
     */
    static public OsacbmDataType convertToOsacbmDataType(String attributeType)
    {
        if ( attributeType != null )
        {
            switch (attributeType)
            {
                case RestConstants.URI_PRIMITIVE_BOOLEAN:
                    return OsacbmDataType.DA_BOOL;
                case RestConstants.URI_PRIMITIVE_DATE:
                    return OsacbmDataType.DA_REAL;
                case RestConstants.URI_PRIMITIVE_STRING:
                    return OsacbmDataType.DA_STRING;
                case RestConstants.URI_PRIMITIVE_NUMBER:
                default:
                    return OsacbmDataType.DA_REAL;
            }
        }
        return OsacbmDataType.DM_UNKNOWN;
    }

}
