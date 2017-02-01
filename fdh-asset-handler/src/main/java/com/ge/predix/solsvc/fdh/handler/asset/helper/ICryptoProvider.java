/*
* Copyright (C) 2012 GE Software Center of Excellence.
* All rights reserved
*/
package com.ge.predix.solsvc.fdh.handler.asset.helper;

/**
 * Utility class to obfuscate passwords. This will make calls to different
 * routines for encoding and encryption tools found in dsp-k MangleUtil
 *
 */
public interface ICryptoProvider {

    /**
     * 
     */
    static final String KEY_CONTAINS_MANGLED = "mangled"; //$NON-NLS-1$
    /**
     * 
     */
    static final String KEY_CONTAINS_ENCRYPT = "encrypt"; //$NON-NLS-1$

    /**
     * @param plainText
     *            Plain text input to be encrypted
     * @return encrypted string
     * @throws Exception -if the text could not be encrypted
     */
    String mangle(String plainText) throws Exception;

    /**
     *
     *
     * @param mangleText
     *            Encrypted text input to be decrypted
     * @return Plain text string
     * @throws Exception if the text could not be decrypted
     */
    String unmangle(String mangleText) throws Exception;
}
