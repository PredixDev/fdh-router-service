package com.ge.predix.solsvc.fdh.handler.asset.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author 212325745
 */
public class RequestContext
{
    private static ThreadLocal<Map<String, Object>> localValueHolder = new ThreadLocal<Map<String, Object>>()
                                                                     {
                                                                         @Override
                                                                         protected Map<String, Object> initialValue()
                                                                         {
                                                                             return new HashMap<>();
                                                                         }
                                                                     };

    /**
     * @param key -
     * @param <T> - generic
     * @return -
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key)
    {
        return (T) localValueHolder.get().get(key);
    }

    /**
     * @param key -
     * @param value -
     */
    public static void put(String key, Object value)
    {
        localValueHolder.get().put(key, value);
    }

    /**
     * @param key -
     * @return -
     */
    public static Object remove(String key)
    {
        return localValueHolder.get().remove(key);
    }

    /**
     *  -
     */
    public static void clear()
    {
        localValueHolder.get().clear();
    }
}
