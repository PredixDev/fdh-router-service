package com.ge.predix.solsvc.fdh.handler.asset.helper;


/**
 * Created with IntelliJ IDEA.
 * User: 502147947
 * Date: 2/18/13
 * Time: 12:23 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IUomDataExchange {
    /**
     * @param providerIPAddress -
     * @param providerPortNumber -
     * @return -
     */
    IUnitConverter getIUnitConverter(String providerIPAddress, String providerPortNumber);
}
