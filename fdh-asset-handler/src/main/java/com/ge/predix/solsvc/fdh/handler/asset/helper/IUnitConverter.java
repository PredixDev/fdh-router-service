/**
 * Copyright (C) 2012 General Electric Company
 * All rights reserved.
 *
 */
package com.ge.predix.solsvc.fdh.handler.asset.helper;

import java.util.List;

import org.mimosa.osacbmv3_3.DataEvent;

/**
 * This interface provides a method for converting fromUnit, toUnit based on the UOM table values.
 */
public interface IUnitConverter {

    /**
     * @param actualUnitName -
     * @param expectedUnitName -
     * @param valueToBeConverted -
     */
    void convertDataEvent(String actualUnitName, String expectedUnitName, DataEvent valueToBeConverted);

    /**
     * @param actualUnitName -
     * @param expectedUnitName -
     * @param valueToBeConverted -
     * @param <T> the item to convert
     * @return -
     */
    public <T> List<T> convert(String actualUnitName, String expectedUnitName, List<T> valueToBeConverted);

    /**
     * @param actualUnitName -
     * @param expectedUnitName -
     * @param valueToBeConverted -
     * @return -
     */
    public double convert(String actualUnitName, String expectedUnitName, double valueToBeConverted);

}
