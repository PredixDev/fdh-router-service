/**
 * Copyright (C) 2012 General Electric Company
 * All rights reserved.
 *
 */

package com.ge.fdh.asset.helper;

import static java.text.MessageFormat.format;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ge.dsp.pm.ext.entity.vo.unitcode.UnitCodeVO;
import com.ge.dsp.pm.ext.entity.vo.unitcode.UnitCodesVO;

/**
 * 1- Provide REST API to get all of the unitCodes into cache - this handler acts will reach out to the provider
 * (UomDataExchangeProvider) which is host by the orchestration bundle
 * 2- Once the GetUomDataExchangeResult is obtained - the unitConverter will be updated with the UnitCodes object and
 * provided as a conversion utilty for unit of measurements.
 * 3- No database access needed for the unit of code since we are getting it via a REST call from the orchestration
 * bundle (hosted by the UomDataExchangeProvider on the Orchestration bundle side.)
 * 4- This code belong to the dspm util which is generic to be used by any bundle that doesn't have access to the
 * platform-manager bundle.
 * <p/>
 * In summary, we are eliminating the need to be in the same VM as the platform-manager bundle in order to obtain a utility that provides a unitConversion APIs.
 */
@Component
public class UomDataExchangeHandler
        implements IUomDataExchange
{

    private static final Logger                                                  log                      = LoggerFactory
                                                                                                                     .getLogger(UomDataExchangeHandler.class);

    /**
     * provides access to the unitConversion logic
     */
    private IUnitConverter                                                       iUnitConverter              = null;

    /**
     * this is the invoker for the UomDataExchangeProvider
     */
    HttpClientUtilDecorator<GetUomDataExchangeRequest, GetUomDataExchangeResult> httpClientUtilDecorator;

    /**
     * The result of the uom data exchange provider
     */
    GetUomDataExchangeResult                                                     getUomDataExchangeResult    = null;

    /**
     * RESTFUL service address = /service/@RestfulServiceAddress("uomdataexchangeservice")/@Path("/iuomdataexchange")
     * <p/>
     * This value never changes as you see it is part of the annotation - the only additional information that is required is the IP address of the provider and
     * port number: which will be pass by the caller or set in a property file
     */
    private static final String                                                  URI_UOMDATAEXCHANGE_RESTFUL = "/service/uomdataexchangeservice/iuomdataexchange/getUomData";

    /**
     * holds the unitCodeMap passed from the orchestration engine by calling the database
     */
    private final Map<String, UnitCodeVO>                                        unitCodeMap                 = new HashMap<String, UnitCodeVO>();

    private volatile boolean                                                     isUnitCodeMapInitialized    = false;

    /**
     * Jaxb2Marshaller
     */
    //private Jaxb2Marshaller                                                      tmarshaller                 = null;                                                          ;
    /**
     * HttpClientUtil
     */
    private HttpClientUtil                                                       httpClientUtil              = null;                                                          ;

    /**
     * indicates if an error has occurred during the REST call
     */
    private boolean                                                              providerError               = false;

    /**
     * serviceHost for the UomDataExchangeService (rest provider IP)
     */
    private String                                                               providerIPAddress           = null;                                                          ;

    /**
     * service port for the UomDataExchangeService (rest provider port)
     */
    private String                                                               providerPortNumber          = null;                                                          ;

    /**
     *
     */
    public UomDataExchangeHandler()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the unitConversion module for performing Engineering unit conversion
     */
    @SuppressWarnings("nls")
    @Override
    public IUnitConverter getIUnitConverter(String providerIPAddress, String providerPortNumber)
    {

        log.debug(format("REF={0} Entering UomDataExchange: providerIPAddress={1} providerPortNumber= {2}",
                providerIPAddress, providerPortNumber));

        if ( this.iUnitConverter == null || !this.isUnitCodeMapInitialized )
        {

            log.debug(format(
                    "REF={0} Fetching UomDataExchange:unitCodeMap providerIPAddress={1} providerPortNumber= {2}",
                    providerIPAddress, providerPortNumber));

            setProviderIPAddress(providerIPAddress);
            setProviderPortNumber(providerPortNumber);
            initUnitCodeMap(providerIPAddress, providerPortNumber);
            this.iUnitConverter = new UnitConverter(this.unitCodeMap);
        }

        return this.iUnitConverter;
    }

    /**
     * helper method to invoke the provider and build the internal map for the unit codes and also
     * build the proper errors in the errorBuilder in case of an error
     * 
     * @param providerIPAddress
     * @param providerPortNumber
     */
    @SuppressWarnings("nls")
    private void initUnitCodeMap(String providerIPAddress, String providerPortNumber)
    {

        // we need to invoke the provider in order to the unitCodeVo objects since
        // the current map size is = 0 which means it has not been updated yet
        if ( this.unitCodeMap.size() == 0 )
        {
            String providerURL = providerIPAddress + ":" + providerPortNumber;

            log.info(format("REF={0} Preparing UomDataExchange:initUnitCodeMap providerURL={1}", providerURL));

            GetUomDataExchangeRequest getUomDataExchangeRequest = null;
            getUomDataExchangeRequest = new GetUomDataExchangeRequest();

            log.debug(format("REF={0} INVOKING UomDataExchange:httpClientUtilDecorator providerURL={1}",
                    providerURL));

//            this.httpClientUtilDecorator = new HttpClientUtilDecorator<GetUomDataExchangeRequest, GetUomDataExchangeResult>(
//                    providerURL, URI_UOMDATAEXCHANGE_RESTFUL, getTmarshaller(), getHttpClientUtil());

            try
            {
                this.getUomDataExchangeResult = this.httpClientUtilDecorator.execute(getUomDataExchangeRequest);

                log.debug(format("REF={0} RETURNING UomDataExchange:getUomDataExchangeResult providerURL={1}",
                        providerURL));

                initUnitCodeMap();
            }
            catch (Exception e)
            {

                log.debug(format("REF={0} EXCEPTION UomDataExchange:httpClientUtilDecorator providerURL={1}",
                        providerURL));

                this.providerError = true;
            }
        }
    }

    /**
     * helper method that parsers the result and builds the appropriate errors or return successful return code
     * 
     * @param errorBuilder
     */
    @SuppressWarnings("nls")
    private void initUnitCodeMap()
    {

        log.debug(format("REF={0} BUILDING UNIT CONVERTER UomDataExchange:"
                + "getUomDataExchangeResult.getDateTime()={1} ", this.getUomDataExchangeResult.getDateTime()));

        UnitCodesVO unitCodesVO = this.getUomDataExchangeResult.getUnitCodeVOMap();

        if ( this.getUomDataExchangeResult.getCapacity() == -1 || unitCodesVO == null )
        {
            log.debug(format("UomDataExchangeHandler - initUnitCodeMap: ErrorCodes.CONFIG112, "
                    + "Expected capacity={0} but local cache size= {1}", -1, null));

            this.providerError = true;

            return;

        }

        XMLCalendarAdapter.getCalendar(this.getUomDataExchangeResult.getDateTime());

        List<UnitCodeVO> unitCodeVOList = unitCodesVO.getUnitCodeVO();

        for (UnitCodeVO o : unitCodeVOList)
        {
            this.unitCodeMap.put(o.getUnitName(), o);
        }

        int capacity = this.getUomDataExchangeResult.getCapacity();

        if ( capacity != this.unitCodeMap.size() )
        {

            log.debug(format("UomDataExchangeHandler - initUnitCodeMap: ErrorCodes.CONFIG112, "
                    + "Expected capacity={0} but local cache size= {1}", capacity, this.unitCodeMap.size()));

            this.providerError = true;

        }
        else
        {

            log.info("SUCCESSFULLY CREATED UnitCodeMapVO: UomDataExchangeHandler:");

            this.providerError = false;
        }

        this.isUnitCodeMapInitialized = true;
    }

    /**
     * @return true if there is an error in the REST call by the invoker
     */
    public boolean isErrorOccurred()
    {
        return this.providerError;
    }

//    /**
//     * @return -
//     */
//    public Jaxb2Marshaller getTmarshaller()
//    {
//        return this.tmarshaller;
//    }
//
//    /**
//     * @param tmarshaller -
//     */
//    public void setTmarshaller(Jaxb2Marshaller tmarshaller)
//    {
//        this.tmarshaller = tmarshaller;
//    }

    /**
     * @return -
     */
    public HttpClientUtil getHttpClientUtil()
    {
        return this.httpClientUtil;
    }

    /**
     * @param httpClientUtil -
     */
    public void setHttpClientUtil(HttpClientUtil httpClientUtil)
    {
        this.httpClientUtil = httpClientUtil;
    }

    /**
     * @return -
     */
    public String getProviderIPAddress()
    {
        return this.providerIPAddress;
    }

    /**
     * @param providerIPAddress -
     */
    public void setProviderIPAddress(String providerIPAddress)
    {

        this.providerIPAddress = providerIPAddress;
    }

    /**
     * @return -
     */
    public String getProviderPortNumber()
    {
        return this.providerPortNumber;
    }

    /**
     * @param providerPortNumber -
     */
    public void setProviderPortNumber(String providerPortNumber)
    {
        this.providerPortNumber = providerPortNumber;
    }

}
