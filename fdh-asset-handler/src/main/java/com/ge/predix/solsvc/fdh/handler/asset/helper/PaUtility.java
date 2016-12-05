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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.ge.predix.entity.model.Model;
import com.ge.predix.solsvc.fdh.handler.asset.common.FieldModel;

/**
 * 
 * @author 212369540
 */
@SuppressWarnings(
{
    "nls"
})
public class PaUtility
{
    /**
     * @param fieldIdString -
     * @return -
     */
    public static boolean isClassificationAttributeField(String fieldIdString)
    {
        if ( fieldIdString.toLowerCase().startsWith("/classification/")
                || fieldIdString.toLowerCase().startsWith("classification/") )
        {
            return true;
        }
        return false;
    }

    /**
     * @param fieldIdString -
     * @return -
     */
    public static boolean isAssetAttributeField(String fieldIdString)
    {
        if ( fieldIdString.toLowerCase().startsWith("/asset/") || fieldIdString.toLowerCase().startsWith("asset/") )
        {
            return true;
        }
        return false;
    }

    /**
     * @param fieldIdString -
     * @return -
     */
    public static boolean isAssetOrTagOrTagExtAttributeField(String fieldIdString)
    {
        if ( fieldIdString.toLowerCase().startsWith("/asset/") || fieldIdString.toLowerCase().startsWith("asset/")
                || fieldIdString.toLowerCase().startsWith("tag/")
                || fieldIdString.toLowerCase().startsWith("/asset/tag/tagattribute/")
                || fieldIdString.toLowerCase().startsWith("tagext/") )
        {
            return true;
        }
        return false;
    }

    /**
     * @param fieldIdString -
     * @return -
     */
    public static boolean isModelAttributeField(String fieldIdString)
    {
        String localString = fieldIdString;
        if ( !fieldIdString.startsWith("/"))
            localString = "/" + fieldIdString.substring(0);

        StringTokenizer t = new StringTokenizer(localString, "/");
        if ( t.countTokens() >= 2 )
        {
            return true;
        }
        return false;
    }

    /**
     * @param fieldIdString -
     * @return -
     */
    @Deprecated
    public static boolean isClassification(String fieldIdString)
    {
        if ( fieldIdString.toLowerCase().equals("classification") )
        {
            return true;
        }
        return false;
    }

    /**
     * @param fieldIdString -
     * @return -
     */
    public static boolean isTimeseriesAttributeField(String fieldIdString)
    {
        if ( fieldIdString.startsWith("/asset/tag/tagattribute") || fieldIdString.startsWith("tagext") )
        {
            return true;
        }
        return false;
    }

    /**
     * @param fieldIdString -
     * @return -
     */
    public static boolean isTimeseriesDataField(String fieldIdString)
    {
        if ( fieldIdString.startsWith("/tag") || fieldIdString.startsWith("tag") )
        {
            return true;
        }
        return false;
    }

    /**
     * @param fieldIdString -
     * @return -
     */
    public static String getClassificationUri(String fieldIdString)
    {
        int separatorIndex = fieldIdString.lastIndexOf('/');
        if ( separatorIndex > 0 )
        {
            String classificationUri = fieldIdString.substring(0, separatorIndex);
            return classificationUri;
        }
        return fieldIdString;
    }

    /**
     * @param fieldIdString -
     * @return -
     */
    public static String getAttributeName(String fieldIdString)
    {
        int separatorIndex = fieldIdString.lastIndexOf('/');
        String attributeName = fieldIdString.substring(separatorIndex + 1, fieldIdString.length());
        attributeName = attributeName.trim();
        attributeName = firstCharLower(attributeName);
        if ( attributeName.toLowerCase().equals("assetid") ) attributeName = "assetId";
        if ( attributeName.toLowerCase().equals("uri") ) attributeName = "uri";
        return attributeName;
    }

    /**
     * @param attributeName -
     * @return -
     */
    public static String firstCharLower(String attributeName)
    {
        String attribute =attributeName;
        String firstChar = attributeName.substring(0, 1);
        if ( firstChar.toUpperCase().equals(firstChar) )
        {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(firstChar.toLowerCase());
            stringBuilder.append(attributeName.substring(1));
            attribute  = stringBuilder.toString();
        }
        return attribute;
    }

    /**
     * @param attributeName -
     * @return -
     */
    public static String firstCharUpper(String attributeName)
    {
        String attribute =attributeName;
        String firstChar = attributeName.substring(0, 1);
        if ( firstChar.toLowerCase().equals(firstChar) )
        {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(firstChar.toUpperCase());
            stringBuilder.append(attributeName.substring(1));
            attribute  = stringBuilder.toString();
        }
        return attribute;
    }

    /**
     * @param fieldIdString -
     * @return -
     */
    public static String getTagKey(String fieldIdString)
    {
        String localString = fieldIdString;
        if ( !fieldIdString.startsWith("/"))
            localString = "/" + fieldIdString.substring(1);
        if ( localString.split("/").length == 2 )
        {
            int separatorIndex = localString.lastIndexOf('/');
            String tagKey = localString.substring(separatorIndex + 1, localString.length());
            return tagKey.trim();
        }
        else if ( localString.split("/").length == 3 )
        {
            int separatorIndex1 = localString.indexOf('/');
            int separatorIndex2 = localString.lastIndexOf('/');
            String tagKey = localString.substring(separatorIndex1 + 1, separatorIndex2).trim();
            return tagKey.trim();
        }
        else
            throw new UnsupportedOperationException("unexpected number of / slash delimiters in fieldIdString="
                    + localString);
    }

    /**
     * @param fieldIdString -
     * @return -
     */
    public static String getTagKeyAttribute(String fieldIdString)
    {
        if ( fieldIdString.split("/").length == 3 )
        {
            int separatorIndex1 = fieldIdString.lastIndexOf('/');
            String tagKey = fieldIdString.substring(separatorIndex1 + 1).trim();
            return tagKey.trim();
        }
        return null;
    }

    /**
     * @param fieldId -
     * @return -
     */
    public static boolean isModel(String fieldId)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @param field -
     * @return -
     */
    public static FieldModel getFieldModel(String field)
    {
        String localString = field;
        if ( !field.startsWith("/"))
            localString  = "/" + field;
        
        StringTokenizer t = new StringTokenizer(localString, "/");
        String firstToken = t.nextToken();
        List<String> childModels = new ArrayList<String>();
        while (t.hasMoreTokens())
            childModels.add(t.nextToken());
        String lastToken = null;
        if ( childModels.size() > 0 )
        {
            lastToken = childModels.get(childModels.size() - 1);
            childModels.remove(childModels.size() - 1);
        }
        FieldModel fm = new FieldModel();
        fm.setField(field);
        fm.setModel(firstToken);
        fm.setChildModels(childModels);
        fm.setAttribute(lastToken);
        return fm;
    }

    /**
     * @param modelName -
     * @return -
     * @throws ClassNotFoundException -
     */
    public static Model getModel(String modelName)
            throws ClassNotFoundException
    {
        try
        {
            // modelName = modelName.substring(0,1).toUpperCase() + modelName.substring(1);
            Class<?> c = Class.forName(modelName);
            Object model = c.newInstance();
            return (Model) model;
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param field -
     * @return -
     */
    public static String getModelName(String field)
    {
        String localString = field;
        if ( !field.startsWith("/"))
            localString  = "/" + field;
        
        StringTokenizer t = new StringTokenizer(localString, "/");
        String modelName = t.nextToken();
        return modelName;
    }

}
