package com.ge.predix.solsvc.fdh.handler.asset.utils;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.predix.solsvc.bootstrap.ams.dto.Message;

/**
 * 
 * @author predix -
 */
public abstract class ExceptionUtil
{
    /**
     * @param message -
     * @return -
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> extractedErrors(Message message)
    {
        return (message == null) ? null : (Map<String, String>) message.getErrors();
    }

    /**
     * @param errorResponseBody -
     * @return -
     */
    public static Message getErrorMessageFromJsonString(String errorResponseBody)
    {
        if ( errorResponseBody == null )
        {
            return null;
        }
        TypeReference<Message> listOfRefType = new TypeReference<Message>()
        {
            //
        };
        try
        {
            return new ObjectMapper().readValue(errorResponseBody, listOfRefType);
        }
        catch (JsonParseException ignore)
        {
            //
        }
        catch (IOException ignore)
        {
            //
        }
        return null;
    }

}
