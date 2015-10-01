/**
 * Copyright (C) 2012 General Electric Company
 * All rights reserved.
 *
 */

package com.ge.fdh.asset.helper;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.ws.rs.core.MediaType;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * This class provides a function to handle REST web services call. It does the following:
 * <p/>
 * <ul>
 * <li>Marshal a Request-type object into XML Request String</li>
 * <li>HttpClient connects to REST Service and sends the XML String</li>
 * <li>HttpClient gets the XML Response String</li>
 * <li>UnMarshal an XML Response string to a Response-type object</li>
 * </ul>
 * <pre>

 * </pre>
 *
 *  Request-type object marshall-able by the marshaller provided
 *  Response-type object marshall-able by the marshaller provided
 */
public class HttpClientUtilDecorator<S, R>  {

    private static final String CHARSET = "UTF-8"; //$NON-NLS-1$

    private Jaxb2Marshaller jaxb2Marshaller;
    private HttpClientUtil httpClientUtil;
    private String endpointUrl;
    private String restUri;


    /**
     * @param endpointUrl -
     * @param restUri -
     * @param marshaller -
     * @param httpClientUtil -
     */
    public HttpClientUtilDecorator(String endpointUrl, String restUri,
                                   Jaxb2Marshaller marshaller,
                                   HttpClientUtil httpClientUtil) {

        this.jaxb2Marshaller = marshaller;
        this.httpClientUtil = httpClientUtil;
        this.endpointUrl = endpointUrl;
        this.restUri = restUri;
    }

    /**
     * Marshals request object, Calls REST Service, and Unmarshalls XML string back
     *
     * @param src request-type object
     * @return response-type object
     * @throws Exception -
     */
    public R execute(S src) throws Exception {

        StreamResult streamResult = createStreamResult();
        this.jaxb2Marshaller.marshal(src, streamResult);
        String payload = streamResult.getWriter().toString();

        HttpClientUtil clientUtil = this.httpClientUtil;
        String xmlResult = null;


        xmlResult = clientUtil.httpClientInvoker(new HttpParams(this.endpointUrl +
                this.restUri, payload, MediaType.APPLICATION_XML, CHARSET));

        return (R) this.jaxb2Marshaller.unmarshal(convertToStreamSource(xmlResult));
    }

    /**
     * Marshals request object, Calls REST Service
     *
     * @param src request-type object
     * @throws Exception -
     */
    public void invoke(S src) throws Exception {

        StreamResult streamResult = createStreamResult();
        this.jaxb2Marshaller.marshal(src, streamResult);
        String payload = streamResult.getWriter().toString();

        HttpClientUtil clientUtil = this.httpClientUtil;


        clientUtil.httpClientInvoker(new HttpParams(this.endpointUrl +
                this.restUri, payload, MediaType.APPLICATION_XML, CHARSET));

    }

    /**
     *
     * @return
     */
    private StreamResult createStreamResult() {
        Writer writer = new StringWriter();
        return new StreamResult(writer);
    }

    /**
     *
     * @param xmlResult
     * @return
     */
    private StreamSource convertToStreamSource(String xmlResult) {
        return new StreamSource(new ByteArrayInputStream(xmlResult.getBytes()));
    }


}
