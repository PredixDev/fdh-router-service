/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.fdh.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 
 * @author predix
 */
@Component
@Profile("cloud")
public class FdhRestCloudConfig implements EnvironmentAware, IFdhRestConfig
{
    @SuppressWarnings("unused")
    private static Logger log = LoggerFactory.getLogger(FdhRestCloudConfig.class);
    
    @Value("${predix_fdh_restProtocol:https}")
    private String  restProtocol;
    @Value("${predix_fdh_restHost}")
    private String  restHost;
    @Value("${predix_fdh_restPort:80}")
    private String  restPort;
    @Value("${predix_fdh_restBaseResource:services/fdhrouter/fielddatahandler}")
    private String  restBaseResource;
    
    /* (non-Javadoc)
     * @see com.ge.predix.solsvc.fdh.client.IFdhRestConfig#setEnvironment(org.springframework.core.env.Environment)
     */
    @Override
    public void setEnvironment(Environment environment)
    {
//        try
//        {
//            this.vcapRestUri =  environment.getProperty("vcap.services.predixAsset.credentials.uri");
//            if ( this.vcapRestUri != null ) {
//                URI uri = new URI(this.vcapRestUri);
//                this.restHost = uri.getHost();
//                this.restPort = Integer.toString(uri.getPort());
//                this.restProtocol = uri.getScheme();
//            }
//        }
//        catch (URISyntaxException e)
//        {
//           throw new RuntimeException(e);
//        }
    }

    /* (non-Javadoc)
     * @see com.ge.predix.solsvc.fdh.client.IFdhRestConfig#getGetFieldDataEndPoint()
     */
    @Override
    @SuppressWarnings("nls")
    public String getGetFieldDataEndPoint()
    {
        return this.restProtocol + "://" + this.restHost + ":" + this.restPort + "/" + this.restBaseResource + "/getfielddata" ;
    }

    /* (non-Javadoc)
     * @see com.ge.predix.solsvc.fdh.client.IFdhRestConfig#getPutFieldDataEndPoint()
     */
    @Override
    @SuppressWarnings("nls")
    public String getPutFieldDataEndPoint()
    {
        return this.restProtocol + "://" + this.restHost + ":" + this.restPort + "/" + this.restBaseResource + "/putfielddata" ;
    }

}
