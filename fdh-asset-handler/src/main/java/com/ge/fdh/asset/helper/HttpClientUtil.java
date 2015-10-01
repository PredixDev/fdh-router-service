/*
 * Copyright (C) 2012 GE Software Center of Excellence.
 * All rights reserved
 */
package com.ge.fdh.asset.helper;

import static java.text.MessageFormat.format;

import java.io.InputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

/**
 * This is an HTTP Client Utility
 * 
 * @author Gia Insight Dev Team
 */
@SuppressWarnings("nls")
public class HttpClientUtil
        implements DisposableBean
{

    private static final Logger     log                                 = LoggerFactory
                                                                                   .getLogger(HttpClientUtil.class);

    /**
	 * 
	 */
    protected static final String   DEFAULT_MAX_CONNECTIONS_PER_HOST       = "http.client.default.max.connections.per.host";
    /**
	 * 
	 */
    protected static final String   MAX_TOTAL_CONNECTIONS                  = "http.max.total.connections";
    /**
	 * 
	 */
    protected static final int      DEFAULT_HTTP_CONNECTION_TIMEOUT        = 30000;
    /**
	 * 
	 */
    protected static final int      DEFAULT_HTTP_SOCKET_CONNECTION_TIMEOUT = 60000;

    /**
     * Spring property param -
     * org.apache.commons.httpclient.MultiThreadedHttpConnectionManager
     */
    private HttpConnectionManager   connectionManager;

    /**
     * This instance of HttpClient is thread-safe since it utilizes
     * MultiThreadedHttpConnectionManager for the HttpConnection.
     */
    HttpClient                      httpclient                             = null;

    /**
     * UTF-8 encoding
     */
    private static final String     CHARSET                                = "UTF-8";

    private SslHandlerUtil          sslHandlerUtil                         = null;

    private RestClientSamlGenerator restClientSamlGenerator                = null;

    /**
     * This method to be invoked by spring - used to initialize the HttpClient
     * with the MultiThreadedHttpConnectionManager. This method Creates an
     * HttpClient with the MultiThreadedHttpConnectionManager. This connection
     * manager must be used if more than one thread will be using the
     * HttpClient.
     * @throws Exception -
     */
    public void init()
            throws Exception
    {
        log.debug("PMP>>In init method: HttpClientUtil class");

        this.httpclient = new HttpClient(this.connectionManager);

        HttpConnectionManagerParams httpConnectionManagerParams = new HttpConnectionManagerParams();

        // httpConnectionManagerParams.setDefaultMaxConnectionsPerHost(Integer.parseInt(getPmpResource()
        // .getPropertyConfig().getProperty(DEFAULT_MAX_CONNECTIONS_PER_HOST)));

        // httpConnectionManagerParams.setMaxTotalConnections(Integer.parseInt(getPmpResource().getPropertyConfig()
        // .getProperty(MAX_TOTAL_CONNECTIONS)));

        this.connectionManager.setParams(httpConnectionManagerParams);

        // get http params
        org.apache.commons.httpclient.params.HttpClientParams params = this.httpclient.getParams();

        // set protocal and charset
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, CHARSET);

        /*
         * set timeout for httpclient timeout in milliseconds for waiting on
         * activity between two consecutive data packets
         */
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT, getHttpSocketTimeout());

        /*
         * determines the timeout in milliseconds until a connection is
         * established.
         */
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, getHttpConnectionTimeout());

        // update the client again after we updated the params.
        this.httpclient.setParams(params);

        log.debug("DONE init method: HttpClientUtil class");

    }

    /**
     * This method utilizes the apache httpclient for calling the Restful
     * services. The httpclient wraps the http protocol nicer than the java Http
     * related classes.
     * 
     * @param httpParams -
     * @return the response back as string
     * @throws Exception -
     */
    public String httpClientInvoker(HttpParams httpParams)
            throws Exception
    {
        StringBuffer result = new StringBuffer();
        PostMethod post = null;

        try
        {
            checkAndFixEndpointUrl(httpParams);
            if ( getSslHandlerUtil().isHttpsEnabled() )
            {
                // attach security certificate to the current thread
                getSslHandlerUtil().attachSSLTrustToThread();
            }

            log.debug("in httpClientInvoker executing postMethod for this endpoint: "
                    + httpParams.getEndpointUrl());

            // byte of data from file or -1 if the end of the file is reached
            int ch;

            // prepare the post method for the httpclient to execute it - by
            // default PostMethod sets Content-Type to
            // application/x-www-form-urlencoded
            post = new PostMethod(httpParams.getEndpointUrl());

            if ( getSslHandlerUtil().isAuthEnabled() )
            {
                // using internal user generate SAML token
                String tokenId = this.restClientSamlGenerator.generateSamlTokenId();
                if ( post.getRequestHeader("Content-Type") != null
                        && post.getRequestHeader("Content-Type").getValue().trim().length() > 0 )
                {
                    post.removeRequestHeader("Content-Type");
                }
                if ( post.getRequestHeader("Authorization") != null
                        && post.getRequestHeader("Authorization").getValue().trim().length() > 0 )
                {
                    post.removeRequestHeader("Authorization");
                }
                if ( httpParams.getContentType() == null || httpParams.getContentType().trim().length() == 0 )
                {
                    post.addRequestHeader("Content-Type", "application/xml");
                }
                post.addRequestHeader("Authorization", "SAMLid " + tokenId);
            }

            if ( !StringUtils.isEmpty(httpParams.getPayload()) )
            {
                RequestEntity requestEntity = new StringRequestEntity(httpParams.getPayload(),
                        httpParams.getContentType(), httpParams.getCharset());
                post.setRequestEntity(requestEntity);
            }

            // set chunked content to false -
            post.setContentChunked(false);

            if ( httpParams.getFormParams() != null && httpParams.getFormParams().size() > 0 )
            {
                // hold name:value pair for the http params
                NameValuePair[] formData = new NameValuePair[httpParams.getFormParams().size()];
                int i = 0;

                // populate the form params
                for (String key : httpParams.getFormParams().keySet())
                {
                    formData[i++] = new NameValuePair(key, httpParams.getFormParams().get(key));
                }

                // update the body.
                post.setRequestBody(formData);
            }

            // invoke the post method -
            int statusCode = this.httpclient.executeMethod(post);

            log.debug("Response status code: " + statusCode);

            if ( statusCode == HttpStatus.SC_NO_CONTENT )
            {
                // there is nothing to return because there is no content to
                // return by definition
                result.append("");
            }
            else if ( statusCode == HttpStatus.SC_OK )
            {

                log.debug(format("{0} invoked Successfully with HttpParams = {1}", httpParams.getEndpointUrl(),
                        httpParams));

                log.debug("Start to parse RESPONSE");

                // execute method and handle any error responses.
                InputStream in = post.getResponseBodyAsStream();

                /*
                 * To read bytes from FileInputStream This code reads a byte
                 * from FileInputStream. It returns next byte of data from file
                 * or -1 if the end of the file is reached. Throws IOException
                 * in case of any IO errors.
                 */
                if ( in != null )
                {
                    while ((ch = in.read()) != -1)
                    {
                        result.append((char) ch);
                    }
                }
                log.debug("HTTP finished executeMethod(post)");
            }
            else
            {
                String errorMessage = format("Error invoking [{0}] with HttpStatus code = [{1}] ",
                        httpParams.getEndpointUrl(), statusCode);
                log.debug(errorMessage);
                throw new Exception(errorMessage);
            }

        }
        catch (Throwable e)
        {
            String errorMessage = format("Error invoking service at {0} with HttpParams: " + e.getMessage(),
                    httpParams.getEndpointUrl(), httpParams);
            log.error(errorMessage, e);
            throw new Exception(errorMessage);
        }
        finally
        {
            /*
             * Free up the connection so it can be resused.
             */
            if ( post != null )
            {
                post.releaseConnection();
            }
            log.debug("Releasing HTTP connection for: " + httpParams.getEndpointUrl());
        }

        log.debug("Return the result of invoking the following endpoint: " + httpParams.getEndpointUrl());

        return result.toString();
    }

    /**
     * This method utilizes the apache httpclient for calling the Restful
     * services. The httpclient wraps the http protocol nicer than the java Http
     * related classes.
     * 
     * @param httpParams -
     * @return the response back as string
     * @throws Exception -
     */
    public String httpClientPutInvoker(HttpParams httpParams)
            throws Exception
    {

        /**
         * @return: the return value from invoking the service
         * 
         */
        StringBuffer result = new StringBuffer();

        /**
         * putMethod to execute the request going to the server
         */
        PutMethod put = null;

        try
        {
            checkAndFixEndpointUrl(httpParams);
            if ( getSslHandlerUtil().isHttpsEnabled() )
            {
                // attach security certificate to the current thread
                getSslHandlerUtil().attachSSLTrustToThread();
            }

            log.debug("in httpClientInvoker executing postMethod for this endpoint: "
                    + httpParams.getEndpointUrl());

            // byte of data from file or -1 if the end of the file is reached
            int ch;

            // prepare the put method for the httpclient to execute it - by
            // default PutMethod sets Content-Type to
            // application/x-www-form-urlencoded
            put = new PutMethod(httpParams.getEndpointUrl());

            if ( getSslHandlerUtil().isAuthEnabled() )
            {
                // using internal user generate SAML token
                String tokenId = this.restClientSamlGenerator.generateSamlTokenId();
                if ( put.getRequestHeader("Content-Type") != null
                        && put.getRequestHeader("Content-Type").getValue().trim().length() > 0 )
                {
                    put.removeRequestHeader("Content-Type");
                }
                if ( put.getRequestHeader("Authorization") != null
                        && put.getRequestHeader("Authorization").getValue().trim().length() > 0 )
                {
                    put.removeRequestHeader("Authorization");
                }
                if ( httpParams.getContentType() == null || httpParams.getContentType().trim().length() == 0 )
                {
                    put.addRequestHeader("Content-Type", "application/xml");
                }
                put.addRequestHeader("Authorization", "SAMLid " + tokenId);
            }

            if ( !StringUtils.isEmpty(httpParams.getPayload()) )
            {
                RequestEntity requestEntity = new StringRequestEntity(httpParams.getPayload(),
                        httpParams.getContentType(), httpParams.getCharset());
                put.setRequestEntity(requestEntity);
            }

            // set chunked content to false -
            put.setContentChunked(false);

            put.setRequestHeader(HttpHeaders.ACCEPT, httpParams.getContentType());

            // invoke the post method -
            int statusCode = this.httpclient.executeMethod(put);

            log.debug("Response status code: " + statusCode);

            if ( statusCode == HttpStatus.SC_NO_CONTENT )
            {
                // there is nothing to return because there is no content to
                // return by definition
                result.append("");
            }
            else if ( statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED )
            {

                log.debug(format("{0} invoked Successfully with HttpParams = {1}", httpParams.getEndpointUrl(),
                        httpParams));

                log.debug("Start to parse RESPONSE");

                // execute method and handle any error responses.
                InputStream in = put.getResponseBodyAsStream();

                if ( in != null )
                {
                    while ((ch = in.read()) != -1)
                    {
                        result.append((char) ch);
                    }
                }

                log.debug("HTTP finished executeMethod(post)");
            }
            else
            {
                String errorMessage = format("Error invoking [{0}] with HttpStatus code = [{1}] with HttpParams[{2}]",
                        httpParams.getEndpointUrl(), statusCode, httpParams);
                log.debug(errorMessage);
                throw new Exception(errorMessage);
            }

        }
        catch (Throwable e)
        {
            String errorMessage = format("Error invoking service at {0} with HttpParams {1} - " + e.getMessage(),
                    httpParams.getEndpointUrl(), httpParams);
            log.error(errorMessage, e);
            throw new Exception(errorMessage);
        }
        finally
        {
            /*
             * Free up the connection so it can be resused.
             */
            if ( put != null )
            {
                put.releaseConnection();
            }
            log.debug("Releasing HTTP connection for: " + httpParams.getEndpointUrl());
        }

        log.debug("Return the result of invoking the following endpoint: " + httpParams.getEndpointUrl());

        return result.toString();
    }

    /**
     * This method utilizes the apache httpclient for calling the Restful
     * services. The httpclient wraps the http protocol nicer than the java Http
     * related classes.
     * 
     * @param httpParams -
     * @return the response back as string
     * @throws Exception -
     */
    public String httpClientGetInvoker(HttpParams httpParams)
            throws Exception
    {
        StringBuffer result = new StringBuffer();
        GetMethod get = null;

        try
        {
            checkAndFixEndpointUrl(httpParams);
            if ( getSslHandlerUtil().isHttpsEnabled() )
            {
                // attach security certificate to the current thread
                getSslHandlerUtil().attachSSLTrustToThread();
            }
            log.debug("in httpClientInvoker executing postMethod for this endpoint: "
                    + httpParams.getEndpointUrl());

            // byte of data from file or -1 if the end of the file is reached
            int ch;

            // prepare the post method for the httpclient to execute it - by
            // default PostMethod sets Content-Type to
            // application/x-www-form-urlencoded
            get = new GetMethod(httpParams.getEndpointUrl());

            if ( getSslHandlerUtil().isAuthEnabled() )
            {
                // using internal user generate SAML token
                String tokenId = this.restClientSamlGenerator.generateSamlTokenId();

                if ( get.getRequestHeader("Authorization") != null
                        && get.getRequestHeader("Authorization").getValue().trim().length() > 0 )
                {
                    get.removeRequestHeader("Authorization");
                }

                get.addRequestHeader("Authorization", "SAMLid " + tokenId);
            }
            get.setRequestHeader(HttpHeaders.ACCEPT, httpParams.getContentType());

            // invoke the post method -
            int statusCode = this.httpclient.executeMethod(get);

            log.debug("Response status code: " + statusCode);

            if ( statusCode == HttpStatus.SC_NO_CONTENT )
            {
                // there is nothing to return because there is no content to
                // return by definition
                result.append("");
            }
            else if ( statusCode == HttpStatus.SC_OK )
            {

                log.debug(format("{0} invoked Successfully with HttpParams {1}", httpParams.getEndpointUrl(),
                        httpParams));

                log.debug("Start to parse RESPONSE");

                // execute method and handle any error responses.
                InputStream in = get.getResponseBodyAsStream();

                if ( in != null )
                {
                    while ((ch = in.read()) != -1)
                    {
                        result.append((char) ch);
                    }
                }

                log.debug("HTTP finished executeMethod(post)");
            }
            else
            {
                String errorMessage = format("Error invoking [{0}] with HttpStatus code = [{1}] ",
                        httpParams.getEndpointUrl(), statusCode);
                log.debug(errorMessage);
                throw new Exception(errorMessage);
            }

        }
        catch (Throwable e)
        {
            String errorMessage = format("Error invoking service at {0} with HttpParams {1}" + e.getMessage(),
                    httpParams.getEndpointUrl(), httpParams);
            log.error(errorMessage, e);
            throw new Exception(errorMessage);
        }
        finally
        {
            /*
             * Free up the connection so it can be resused.
             */
            if ( get != null )
            {
                get.releaseConnection();
            }
            log.debug("Releasing HTTP connection for: " + httpParams.getEndpointUrl());
        }

        log.debug("Return the result of invoking the following endpoint: " + httpParams.getEndpointUrl());

        return result.toString();
    }

    /**
     * This method delete a resource by making REST call using Apache HttpClient
     * 
     * @param httpParams
     *            - Http Params
     * @return the response back as string
     * @throws Exception -
     */
    public String httpClientDeleteInvoker(HttpParams httpParams)
            throws Exception
    {
        StringBuffer result = new StringBuffer();

        DeleteMethod delete = null;

        try
        {
            checkAndFixEndpointUrl(httpParams);
            if ( getSslHandlerUtil().isHttpsEnabled() )
            {
                // attach security certificate to the current thread
                getSslHandlerUtil().attachSSLTrustToThread();
            }
            log.debug("in httpClientInvoker executing postMethod for this endpoint: "
                    + httpParams.getEndpointUrl());

            // byte of data from file or -1 if the end of the file is reached
            int ch;

            // prepare the post method for the httpclient to execute it - by
            // default PostMethod sets Content-Type to
            // application/x-www-form-urlencoded
            delete = new DeleteMethod(httpParams.getEndpointUrl());
            delete.setRequestHeader(HttpHeaders.ACCEPT, httpParams.getContentType());
            if ( getSslHandlerUtil().isAuthEnabled() )
            {
                // using internal user generate SAML token
                String tokenId = this.restClientSamlGenerator.generateSamlTokenId();

                if ( delete.getRequestHeader("Authorization") != null
                        && delete.getRequestHeader("Authorization").getValue().trim().length() > 0 )
                {
                    delete.removeRequestHeader("Authorization");
                }

                delete.addRequestHeader("Authorization", "SAMLid " + tokenId);
            }

            // invoke the post method -
            int statusCode = this.httpclient.executeMethod(delete);

            log.debug("Response status code: " + statusCode);

            if ( statusCode == HttpStatus.SC_NO_CONTENT )
            {
                // there is nothing to return because there is no content to
                // return by definition
                result.append("");
            }
            else if ( statusCode == HttpStatus.SC_OK )
            {

                log.debug(format("{0} invoked Successfully with HttpParams = {1}", httpParams.getEndpointUrl(),
                        httpParams));

                log.debug("Start to parse RESPONSE");

                // execute method and handle any error responses.
                InputStream in = delete.getResponseBodyAsStream();

                if ( in != null )
                {
                    while ((ch = in.read()) != -1)
                    {
                        result.append((char) ch);
                    }
                }

                log.debug("HTTP finished executeMethod(post)");
            }
            else
            {
                String errorMessage = format("Error invoking [{0}] with HttpStatus code = [{1}] ",
                        httpParams.getEndpointUrl(), statusCode);
                log.debug(errorMessage);
                throw new Exception(errorMessage);
            }

        }
        catch (Throwable e)
        {
            String errorMessage = format("Error invoking service at {0} with HttpParams", httpParams.getEndpointUrl(),
                    httpParams);
            log.error(errorMessage, e);
            throw new Exception(errorMessage);
        }
        finally
        {
            /*
             * Free up the connection so it can be resused.
             */
            if ( delete != null )
            {
                delete.releaseConnection();
            }
            log.debug("Releasing HTTP connection for: " + httpParams.getEndpointUrl());
        }

        log.debug("Return the result of invoking the following endpoint: " + httpParams.getEndpointUrl());

        return result.toString();
    }

    /**
     * @return -
     */
    public HttpConnectionManager getConnectionManager()
    {
        return this.connectionManager;
    }

    /**
     * @param connectionManager -
     */
    public void setConnectionManager(HttpConnectionManager connectionManager)
    {
        this.connectionManager = connectionManager;
    }

    @Override
    public void destroy()
            throws Exception
    {
        // When HttpClient instance is no longer needed,
        // shut down the connection manager to ensure
        // immediate re-cycling of system resources
        if ( this.connectionManager != null )
        {
            ((MultiThreadedHttpConnectionManager) this.connectionManager).shutdown();
            log.debug("shutdown connection manager successfully");
        }
    }

    /**
     * Timeout in milliseconds for waiting on activity between two consecutive
     * data packets This value is defined in the
     * performance-management.properties for the following key:
     * caf.http.socket.timeout
     * 
     * @return the httpSocketTimeout
     *         <p/>
     *         {link}@see org.apache.http.params#SO_TIMEOUT}
     * @category properties file
     */
    public Integer getHttpSocketTimeout()
    {

        Integer httpSocketTimeout;

        try
        {

            httpSocketTimeout = 0; //Integer.parseInt(getPmpResource().getPropertyConfig().getProperty( "caf.http.socket.timeout"));

        }
        catch (NumberFormatException n)
        {
            httpSocketTimeout = DEFAULT_HTTP_SOCKET_CONNECTION_TIMEOUT;
        }

        log.debug("returning httpSocketTimeout: " + httpSocketTimeout);

        return httpSocketTimeout;
    }

    /**
     * Determines the timeout in milliseconds until a connection is interpreted
     * as an infinite timeout This value is defined in the
     * performance-management.properties for the following key:
     * caf.http.connection.timeout
     * 
     * @return the httpConnectionTimeout
     * @category properties file
     *           <p/>
     *           {link@see org.apache.http.params#CONNECTION_TIMEOUT}
     */
    public Integer getHttpConnectionTimeout()
    {

        Integer httpConnectionTimeout;

        try
        {

            httpConnectionTimeout = 0; //Integer.parseInt(getPmpResource().getPropertyConfig().getProperty( "caf.http.connection.timeout"));

        }
        catch (NumberFormatException n)
        {
            httpConnectionTimeout = DEFAULT_HTTP_CONNECTION_TIMEOUT;
        }

        log.debug("returning httpConnectionTimeout: " + httpConnectionTimeout);

        return httpConnectionTimeout;
    }



    private void checkAndFixEndpointUrl(HttpParams httpParams)
    {
        if ( httpParams.getEndpointUrl().startsWith("http:") || httpParams.getEndpointUrl().startsWith("https:")
                || httpParams.getEndpointUrl().startsWith("HTTP:") || httpParams.getEndpointUrl().startsWith("HTTPS:") )
        {
            // do nothing
        }
        else
        {
            if ( getSslHandlerUtil().isHttpsEnabled() )
            {
                httpParams.setEndpointUrl("https://" + httpParams.getEndpointUrl());
            }
            else
            {
                httpParams.setEndpointUrl("http://" + httpParams.getEndpointUrl());
            }
        }

        // // This is to handle scheduler based invoking
        // if ( getSslHandlerUtil().isHttpsEnabled() )
        // {
        // httpParams.setEndpointUrl(httpParams.getEndpointUrl().replaceAll("0000", getSslHandlerUtil().getHttpsPort()));
        // }
        // else
        // {
        // httpParams.setEndpointUrl(httpParams.getEndpointUrl().replaceAll("0000", getSslHandlerUtil().getHttpPort()));
        // }
    }

    /**
     * @return the sslHandlerUtil
     */
    public SslHandlerUtil getSslHandlerUtil()
    {
        return this.sslHandlerUtil;
    }

    /**
     * @param sslHandlerUtil the sslHandlerUtil to set
     */
    public void setSslHandlerUtil(SslHandlerUtil sslHandlerUtil)
    {
        this.sslHandlerUtil = sslHandlerUtil;
    }

    /**
     * @return the restClientSamlGenerator
     */
    public RestClientSamlGenerator getRestClientSamlGenerator()
    {
        return this.restClientSamlGenerator;
    }

    /**
     * @param restClientSamlGenerator the restClientSamlGenerator to set
     */
    public void setRestClientSamlGenerator(RestClientSamlGenerator restClientSamlGenerator)
    {
        this.restClientSamlGenerator = restClientSamlGenerator;
    }

}
