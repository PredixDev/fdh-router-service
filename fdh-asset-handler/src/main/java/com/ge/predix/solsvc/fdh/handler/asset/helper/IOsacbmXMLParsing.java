/**
 * Copyright (C) 2012 General Electric Company
 * All rights reserved.
 *
 */
package com.ge.predix.solsvc.fdh.handler.asset.helper;

import com.ge.predix.entity.moduleconfigroot.ModuleConfigRoot;


/**
 * This is the interface for marshaling and un-marshaling ModuleConfigRoot
 *
 * @author DSP-PM Development Team
 */
public interface IOsacbmXMLParsing {


    /**
     * Marshals the ModuleConfigRoot
     *
     * @param object an instance of ModuleConfigRoot
     * @return a String representation of ModuleConfigRoot
     */
    public String marshal(ModuleConfigRoot object);

    /**
     * Un-Marshals the ModuleConfigRoot
     *
     * @param xmlString an XML string representation of ModuleConfigRoot
     * @return an instance of ModuleConfigRoot
     */
    public ModuleConfigRoot unmarshal(String xmlString);

}
