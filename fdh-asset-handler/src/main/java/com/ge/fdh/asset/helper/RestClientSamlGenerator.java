/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.fdh.asset.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author 212368294
 */
public class RestClientSamlGenerator
{

    private static final Logger log = LoggerFactory.getLogger(RestClientSamlGenerator.class);

    /**
     * provides the latest updated saml token refreshed by timertask
     */
    protected static String      samlTokenId           = null;

    /**
     * provides the latest updated refresh token updated by timertask
     */
    protected static String      refreshTokenId        = null;

    private static AtomicBoolean isTokenBeingRefreshed = new AtomicBoolean(false);
    private static int           CONNECTION_TIMEOUT    = 5000;
    private static int           SOCKET_TIMEOUT        = 10000;



    private Timer                samlRefreshTimer      = null;

    private String               secureUrl;
    private String               contentType;
    private String               authClientId;
    private String               authClientSecret;

    private String               username;
    private String               password;
    private String               certLocation;
    private String               certPassword;

    private String               samlTokenLifeTime;

    private String               authEnabled;

   // private CryptoUtil           cryptoUtil;


//    /**
//     * @return the cryptoUtil
//     */
//    public CryptoUtil getCryptoUtil()
//    {
//        return this.cryptoUtil;
//    }
//
//    /**
//     * @param cryptoUtil the cryptoUtil to set
//     */
//    public void setCryptoUtil(CryptoUtil cryptoUtil)
//    {
//        this.cryptoUtil = cryptoUtil;
//    }


    /**
     * @return the secureUrl
     */
    public String getSecureUrl()
    {
        return this.secureUrl;
    }

    /**
     * @param secureUrl the secureUrl to set
     */
    public void setSecureUrl(String secureUrl)
    {
        this.secureUrl = secureUrl;
    }

    /**
     * @return the contentType
     */
    public String getContentType()
    {
        return this.contentType;
    }

    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    /**
     * @return the authClientId
     */
    public String getAuthClientId()
    {
        return this.authClientId;
    }

    /**
     * @param authClientId the authClientId to set
     */
    public void setAuthClientId(String authClientId)
    {
        this.authClientId = authClientId;
    }

    /**
     * @return the authClientSecret
     */
    public String getAuthClientSecret()
    {
        return this.authClientSecret;
    }

    /**
     * @param authClientSecret the authClientSecret to set
     */
    public void setAuthClientSecret(String authClientSecret)
    {
        this.authClientSecret = authClientSecret;
    }

    /**
     * @return the username
     */
    public String getUsername()
    {
        return this.username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /**
     * @return the certLocation
     */
    public String getCertLocation()
    {
        return this.certLocation;
    }

    /**
     * @param certLocation the certLocation to set
     */
    public void setCertLocation(String certLocation)
    {
        this.certLocation = certLocation;
    }

    /**
     * @return the certPassword
     */
    public String getCertPassword()
    {
        return this.certPassword;
    }

    /**
     * @param certPassword the certPassword to set
     */
    public void setCertPassword(String certPassword)
    {
        this.certPassword = certPassword;
    }

    /**
     * @return the samlTokenLifeTime
     */
    public String getSamlTokenLifeTime()
    {
        return this.samlTokenLifeTime;
    }

    /**
     * @param samlTokenLifeTime the samlTokenLifeTime to set
     */
    public void setSamlTokenLifeTime(String samlTokenLifeTime)
    {
        this.samlTokenLifeTime = samlTokenLifeTime;
    }

    /**
     * @return the authEnabled
     */
    public String getAuthEnabled()
    {
        return this.authEnabled;
    }

    /**
     * @param authEnabled the authEnabled to set
     */
    public void setAuthEnabled(String authEnabled)
    {
        this.authEnabled = authEnabled;
    }

    /**
     * @param plainText text to be converted
     * @return base64 encoded string
     */
    protected String encode(String plainText)
    {

        byte[] encodedBytes = Base64.encodeBase64(plainText.getBytes());
        String base64Text = null;
        try
        {
            base64Text = new String(encodedBytes, "UTF-8"); //$NON-NLS-1$

        }
        catch (UnsupportedEncodingException e)
        {
            log.warn("The encoding is not supported " + e.getMessage()); //$NON-NLS-1$
        }

        return base64Text;
    }

    /**
     * execute POST request w/ URL
     * 
     * @param url endpoint
     * @param formPostData SAML request www-urlencoded string
     */
    protected void executeRequest(URL url, String formPostData)
    {
        try
        {
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            this.executeRequestConnection(connection, formPostData);
        }
        catch (IOException ee)
        {
            log.warn("ERROR: Could not generate SAML token id. Nested exception:" + ee.getMessage()); //$NON-NLS-1$
            throw new IllegalArgumentException("Could not generate SAML token id", ee); //$NON-NLS-1$
        }

    }

    /**
     * execute POST request w/ HttpsURLConnection
     * 
     * @param connection HttpsURLConnection
     * @param formPostData www-url-encoded string
     */
    protected void executeRequestConnection(HttpsURLConnection connection, String formPostData)
    {
        OutputStream out = null;
        InputStream in = null;

        try
        {

            SSLSocketFactory sockFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            connection.setSSLSocketFactory(sockFactory);

            // Discovered issue in regression env due to CN check across hosts
            // Need host specific certs and cannot use CN=localhost across domains
            // Workaround considered to disableCNCheck but faced too many issues.
            /*
             * if ( url.getQuery().contains("disableCNCheck=true") ) //$NON-NLS-1$
             * {
             * HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
             * {
             * @Override
             * public boolean verify(String string, SSLSession ssls)
             * {
             * return true;
             * }
             * });
             * }
             */

            connection.setRequestMethod("POST"); //$NON-NLS-1$
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", this.contentType); //$NON-NLS-1$
            connection.setRequestProperty("Authorization", "Basic "  //$NON-NLS-1$//$NON-NLS-2$
                    + this.encode(this.authClientId + ":" + this.authClientSecret)); //$NON-NLS-1$

            connection.setUseCaches(false);

            // set the timeouts in case STS does not respond
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(SOCKET_TIMEOUT);

            out = connection.getOutputStream();
            out.write(formPostData.getBytes());
            out.flush();
            out.close();
            out = null;

            int responseCode = connection.getResponseCode();
            String responseMsg = connection.getResponseMessage();
            if ( responseCode != 200 && !responseMsg.equalsIgnoreCase("Ok") ) //$NON-NLS-1$
            {
                throw new IllegalArgumentException(
                        "Could not successfully execute HTTPS SAML request. HTTP status=" + responseCode + " Message=" + responseMsg); //$NON-NLS-1$
            }

            in = connection.getInputStream();
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer, "UTF-8"); //$NON-NLS-1$

            this.getSamlTokenId(writer);

            writer.close();
            in.close();

        }
        catch (IOException ee)
        {
            log.warn("ERROR: Could not generate SAML token id. Nested exception:" + ee.getMessage()); //$NON-NLS-1$
            throw new IllegalArgumentException("Could not generate SAML token id", ee); //$NON-NLS-1$
        }
        finally
        {
            try
            {
                if ( in != null )
                {
                    in.close();
                }
                if ( out != null )
                {
                    out.close();
                }
            }
            catch (IOException ex)
            {
                //
            }
        }
    }

    /**
     * initialized during bean creation
     */
    public void init()
    {
        if ( !("enforced".equalsIgnoreCase(this.getAuthEnabled()) || "active".equalsIgnoreCase(this.getAuthEnabled())) )  //$NON-NLS-1$//$NON-NLS-2$
        {
            log.info("Auth is not enabled. SAML token generation disabled. Auth:" + this.getAuthEnabled()); //$NON-NLS-1$
            return;
        }
        log.info("Initializing SAML token init..."); //$NON-NLS-1$

        this.loadConfiguration();

        samlTokenId = this.generateSamlTokenId();

        // start the timer and keep refreshing saml token
        this.samlRefreshTimer = new Timer();
        int refreshDelay = Integer.parseInt(this.getSamlTokenLifeTime());

        // if Saml token TTL is configured as X secs (dsp.sts.conf), then refresh every X/2 secs
        // delay = start first refresh after X/2 secs
        // period = repeat successfive refresh after every X/2 secs
        this.samlRefreshTimer.scheduleAtFixedRate(new SamlRefresherTask(this), refreshDelay * 500, refreshDelay * 500);
    }

    /**
     * load configurations through pmpResource or inject directly in object (as done in test)
     */
    private void loadConfiguration()
    {
        // if pmpResource is not initialized, client expected to explicitly set
        // RestClientSamlGenerator configurations. If neither is set, then throw IllegalArgumentException
//        if ( this.pmpResource == null )
//        {
//            if ( this.secureUrl == null || this.authClientId == null || this.authClientSecret == null
//                    || this.username == null || this.password == null )
//            {
//                logger
//                        .warn("SAML token generation failed. Configurations not initialized. " + this.secureUrl + "/" //$NON-NLS-1$ //$NON-NLS-2$
//                                + this.authClientId
//                                + "/" + this.authClientSecret + "/" + this.username );  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
//                throw new IllegalArgumentException("SAML token generation failed. Configurations not initialized. " //$NON-NLS-1$
//                        + this.secureUrl + "/" + this.authClientId + "/" + this.authClientSecret + "/" + this.username  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
//                        ); //$NON-NLS-1$
//            }
//
//            return; // configurations are injected already
//
//        }
//
//        this.secureUrl = this.pmpResource.getPropertyConfig().getProperty("dsppm.internal.saml.issue.endpoint"); //$NON-NLS-1$
//        this.authClientId = this.pmpResource.getPropertyConfig().getProperty("dsppm.internal.saml.cid"); //$NON-NLS-1$
//        this.authClientSecret = this.pmpResource.getPropertyConfig().getProperty("dsppm.internal.saml.cidsecret"); //$NON-NLS-1$
//        String isCidSecretEncrypted = this.pmpResource.getPropertyConfig().getProperty(
//                "dsppm.internal.saml.cidsecret.type"); //$NON-NLS-1$
//        if ( (this.authClientSecret == null || this.authClientSecret.trim().length() == 0)
//                && isCidSecretEncrypted != null && "encrypted".equalsIgnoreCase(isCidSecretEncrypted.trim()) ) //$NON-NLS-1$
//        {
//            String cidSecretVal = this.pmpResource.getPropertyConfig().getProperty(
//                    "dsppm.internal.saml.cidsecret.encrypted"); //$NON-NLS-1$
//            try
//            {
//                this.authClientSecret = this.cryptoUtil.unmangle(cidSecretVal);
//            }
//            catch (Exception e)
//            {
//                log.warn("Unable to unmangle dsppm.internal.saml.cidsecret.encrypted value", e); //$NON-NLS-1$
//            }
//
//        }
//        this.username = this.pmpResource.getPropertyConfig().getProperty("dsppm.internal.saml.user"); //$NON-NLS-1$
//        this.password = this.pmpResource.getPropertyConfig().getProperty("dsppm.internal.saml.userpwd"); //$NON-NLS-1$
//        String isPasswordEncrypted = this.pmpResource.getPropertyConfig().getProperty(
//                "dsppm.internal.saml.userpwd.type"); //$NON-NLS-1$
//        if ( (this.password == null || this.password.trim().length() == 0) && isPasswordEncrypted != null
//                && "encrypted".equalsIgnoreCase(isPasswordEncrypted.trim()) ) //$NON-NLS-1$
//        {
//            String passwordVal = this.pmpResource.getPropertyConfig().getProperty(
//                    "dsppm.internal.saml.userpwd.encrypted"); //$NON-NLS-1$
//            try
//            {
//                this.password = this.cryptoUtil.unmangle(passwordVal);
//            }
//            catch (Exception e)
//            {
//                log.warn("Unable to unmangle dsppm.internal.saml.userpwd.encrypted value", e); //$NON-NLS-1$
//            }
//
//        }

    }

    /**
     * called by HttpClientUtil. generates initial token
     * 
     * @return SAML token
     */
    public String generateSamlTokenId()
    {
    	if( this.password == null)
    	{
    		throw new IllegalArgumentException("Saml token could not be generated because the password is not initialized.");
    	}

        if ( samlTokenId == null )
        {
            this.generateSamlTokenId("grant_type=password&username=" + this.username +  //$NON-NLS-1$
                    "&password=" + encode(this.password)); //$NON-NLS-1$
        }
        while (isTokenBeingRefreshed.get())
        {
            // wait until the token is refreshed
            // System.out.print("+");
        }

        return samlTokenId;
    }

    /**
     * used to issue new token or refresh existing token
     * 
     * @param url base URL for STS
     * @param formPostData url-encoded STS params
     */
    @SuppressWarnings("nls")
    private void generateSamlTokenId(String formPostData)
    {
        URL tokenUrl = null;

        log.info("Generating new token. Timestamp:" + System.currentTimeMillis() + " Arguments: " + this.toString()); //$NON-NLS-1$
        log.info("SAML generate params. URL:" + this.secureUrl + " FormPostData:" + formPostData); //$NON-NLS-1$ //$NON-NLS-2$

        isTokenBeingRefreshed.set(true);

        // override if no arguments are passed; should not happen
        if ( formPostData == null || formPostData.trim().length() == 0 )
        {
            formPostData = "grant_type=password&username=" + this.username + "&password=" + encode(this.password); //$NON-NLS-1$ //$NON-NLS-2$
        }

        boolean freshToken = false;

        final int MAX_RETRY = 5;
        int retryCount = 0;
        while (!freshToken && retryCount++ < MAX_RETRY)
        {
            try
            {
                tokenUrl = new URL(this.secureUrl);
                this.setupSecureContext();
                this.executeRequest(tokenUrl, formPostData);
                freshToken = true;
            }
            catch (MalformedURLException e)
            {
                throw new IllegalArgumentException("Bad secure URL. " + this.secureUrl, e); //$NON-NLS-1$
            }
            catch (Exception ex)
            {
                log.warn("Unable to get SAML token from STS. Error:" + ex.getMessage() + ". Retry count: " + retryCount); //$NON-NLS-1$ //$NON-NLS-2$
                // in case sts not able to talk to wso2 when TimerTask requests for refreshed token
                // com.ge.dsp.wso2.client.Wso2InteractionException: wso2.communication.error
                // delay next request by 5 secs

                if ( retryCount >= MAX_RETRY )
                {
                    log.warn(MAX_RETRY + " retries exhausted. Next service request will generate SAML token."); //$NON-NLS-1$
                    RestClientSamlGenerator.samlTokenId = null;
                    break;
                }
                try
                {
                    Thread.sleep(5000);
                }
                catch (InterruptedException e)
                {
                    // do nothing really
                }
                // if there is an exception; likely saml token has expired and refresh did not work
                // request for a new token with /token endpoint
                formPostData = "grant_type=password&username=" + this.username + "&password=" + encode(this.password);
            }
            finally
            {
                isTokenBeingRefreshed.set(false);
            }
        }

        isTokenBeingRefreshed.set(false);

    }

    /**
     * attach SSL context to request
     */
    @SuppressWarnings("nls")
    private void setupSecureContext()
    {
        try
        {

            // We need to connect using HTTPS.
            KeyStore trustStore = KeyStore.getInstance("JKS"); //$NON-NLS-1$

            // injected certLocation of format: file:certs/authTruststore.jks
            InputStream trustStream = null;
            try
            {
                trustStream = new URL(this.getCertLocation()).openStream();
            }
            catch (Exception ex)
            {
                URL res = Thread.currentThread().getContextClassLoader().getResource(this.getCertLocation());
                trustStream = res.openStream();
            }
            if ( trustStream == null )
            {
                throw new IllegalArgumentException("Unable to locate keystore " + this.getCertLocation());
            }
            trustStore.load(trustStream, this.certPassword.trim().toCharArray());
            trustStream.close();

            TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory
                    .getDefaultAlgorithm());
            trustFactory.init(trustStore);
            TrustManager[] tm = trustFactory.getTrustManagers();

            SSLContext ctx = SSLContext.getInstance("TLS"); //$NON-NLS-1$
            ctx.init(null, tm, new SecureRandom());
            SSLContext.setDefault(ctx);

        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException("Unable to create secure context.", ex);
        }
    }

    /**
     * @param writer
     * 
     *            {"tokenKey":
     *            "nVZbd6LYEn73V2TZjy4DCIq6kqy1uYioICCg+IawuQiCwkYuv35QO5kkPT2n57xZxVe1v7pYVS+5fYoHU5DnMENhmjyJ3GvXm4xoeoLDPk3YXp+auHbfPgzH/fFoCF1vCA8DCu8+iXleQDHJkZ2g1+4AJ6g+PuoTYx2npwN6SoyfR9Rk330yYZa3nlvIc2tVneIkn95ffe0WWTJN7TzMp4l9gvkUOdMNkFbTFjm13ym921T5azdA6DzFsLIsn0vyOc18bIDjBLaTVhsngCf7b2z4v8H98E7ega1VHk5RfYav3W/50Ftl9+3lob1HnL1xG+UF+6J5cfPpJvQTGxUZ/EnB/R1dHMMnWItx89D/0f2wha6YeOldZO0kTULHjsPGvpGQIApS9wnEfpqFKDj9NjQCvznuw8rpOwSV/OhiX6n9oaMvDLPc7ueBTfz0pUEPZrBN2pOhia/dH3/UK3dLPbOT3EuzU/5V/G90YHKFcXqGbj9/j+onsz93+A+JenuBzlRMnLjIwyuUb914th2YPykZ9MJqFeZtj1f5e39B5z/VAPtM75v4yAYX+jBH/099PtXm4cS04wK+WRGj1yneA5IfSCtxjHq9UnG0iFiJr3cCn8F3xUdlH+K3nvzooYdFIHCCqqSDvWE0gD4ftfNBKWfS1V7SpNdDyYqoNCknhn6N6nDNL2FiptZxORpny61DMdRyHp0ng0GJ9mlGc4STea5sYHO7kUnKkMVEvFQoCAxMWmAVxidsvkb+rtyYvjJUFb86N1cnHVs4fV4qV+Gw4nehFiNNJbdZzLORIJhDTRPkpN6w5nLg0aJXmRpkc2UfjnsRzqoWYVOGfenN9KGhGtgkHtES12yM2VA2Z4y+NWvHmlBHaefIIxxel2FM+7CKcLDmgBD6kiYc6h27A5tsW+muVNJbU8BYZBe441VMwastZVJRFHK+Eg15cNlTwfHUNOfLyeB8Dsoqtr+sixjk6X6NBJ6nAn0wBK+vH6n/lOtb+pew/ijFbohPOBvZHwJ7m1VeOzAQfJNEkTMalgXHwgelyABf5JcMeXCPQGb86BJEoTApcQao+QxwjC2pedmmgzNVVeDLhWk0/EoCkQAIg++wjMSqOF/xR6AyvmwywNFZUw4OIbOxtsT1cPwbzAYSq+FGxTW3h27YXGdipt5vtTPkeKkjgfQOBJXEGYSmSwy143QelzirkhuxkXS+kcz0piO+6crtp4eY9qGOaUoV24DF4yVfB5HMSJpY8uAeypwvY93aUr5xmqF2BH3Cpi3WnUnquOQeYa86fLlXnZMZ2Tu/4jiwfAAlHRCycc/lRryBF8t0LwZXRwYqzzAq4HyfVwDXfldTtv3NgEWz6ug9/7rk6wHrjTU9Hs74DZybRj46G2sdmSsvvBzD0cGjG1cfOb3DoVCS9XBN070FE4mRu7RP+5wlTieWXjWdxlqmtrnbjoQ9RksXKgp6wkKm4uGOnTU1WNuZdD2bu8S56kslsoimmcuDSjWG2LUgh8nC1ddD0+I3zjkvOo0jUSOpqMTmqAP9fKSdEhOMndhYI7ptnmzTpJqVY+X+KMqY5rI9PYJkYJ4nmcom2GqhZe4sGa20MA8nnd1eqUU+RUc5ts8hN7N0EcvF2kHywMuOmOyp4Wq7WyNPFzNSKZU9lEOR3q53x4MQiDxRKzsGX0HhwI3tjizOtMLzaWu1dI9WrQ+PwsiRTrVX7raSJVNiHLGgrS2wpUBixuX8VjkNXzOMxc/W+2yI6+I+iOLynJQdbWWzi951R9EDRQK4wG4uwkY8kFxbNbY0AKDaf4UKKGWnsF56wQiHTMLoStZ7uLerilvHa+IyjLLBea90ZA4tldrXC5yX63WUoeVgnRyT/DIEMDWqieQcFXyjJnPxrDc45WWAq+sSjZSWFJ378QTCS2+sNboLlh3cLJTTyKUvBmsEbR2lbEVcWaEhSDP15Dje2gXjIC3nNHJIyQJo+LjASXYlkgfHjoB0JEli42rRhY+aTo9ZyO7A45vRld5SXEhni5oMi0OvN8HnYW2VThSEA+ZsErkeBz7hIrenmuOFqBv2frNONn44C1Mda/jE6VwtBfm9HnZB7b/PsS7pymsmrBHy9lgndXIyF0zCoScWPy/rGXeJRGwyj93ekqJYnFkoQHeNJUvvra073t5H2Pex9KF8DC7s80j7MvLeb61NcThCB72Lt40sck+zdmHa6PeHI/FM3DWh2/fu0GmR5GfotDSg2326eVGL9q5qxexjuTqV92y32z6A9wWbo7z71p6GoR+gIny/9B4EvrFj08QLb8/cLufH8v73o9Y5TQ/QzmB2vwr+MdLWpxveHOZPcooY2MYBf3NeD/H2vG5B62SdAQ/dQvqKIwcfOOzdP0AoCw8FghvUFuYEE/TLl3ueXrsojWDSz+ClaA+FNHuk770C/5K77+7uW+zTiV2193/7KfG7b3aBgpbBvUfc94R8NXz7Rf2r5lMo2LcD/u0v"
     *            ,
     *            "tokenType":"bearer",
     *            "refreshToken":"1efb2893c8caaf2b6ebf956c58e9f9f",
     *            "expiresIn":300,
     *            "issuedAt":-1,
     *            "parameters":{"tokenKeyId":"f967790e-71af-49da-ab58-865edf5eb240"},"approvedScope":null}
     */
    @SuppressWarnings("nls")
    protected void getSamlTokenId(StringWriter writer)
    {

        JSONObject obj = new JSONObject(writer.getBuffer().toString());
        obj.getString("tokenKey");
        obj.getString("tokenType");

        obj.getInt("expiresIn");
        obj.getInt("issuedAt");

        refreshTokenId = obj.getString("refreshToken"); //$NON-NLS-1$
        samlTokenId = obj.getJSONObject("parameters").getString("tokenKeyId"); //$NON-NLS-1$  //$NON-NLS-2$
        log.info("SAMLTokenId:" + samlTokenId + " RefreshToken:" + refreshTokenId);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
//        String starPwd = this.password == null || this.password.trim().length() == 0 ? "*" : StringUtils.repeat('*',
//                this.password.trim().length());
//        String starClientSecret = this.authClientSecret == null || this.authClientSecret.trim().length() == 0 ? "*"
//                : StringUtils.repeat('*', this.authClientSecret.trim().length());
//        String keystorePwd = this.certPassword == null || this.certPassword.trim().length() == 0 ? "*" : StringUtils
//                .repeat('*', this.certPassword.trim().length());

        return "RestClientSamlGenerator [secureUrl=" + this.secureUrl + ", contentType=" + this.contentType
                + ", authClientId=" + this.authClientId + 
                //", authClientSecret=" + starClientSecret + 
                ", username=" + this.username + 
                //", password=" + starPwd + 
                ", certLocation=" + this.certLocation + 
                //", certPassword=" + keystorePwd + 
                "]";
    }

    /**
     * 
     * @author 212368294
     */
    class SamlRefresherTask extends TimerTask
    {

        private RestClientSamlGenerator timerSamlGenerator = null;

        /**
         * @param generator -
         */
        @SuppressWarnings("nls")
        public SamlRefresherTask(RestClientSamlGenerator generator)
        {
            this.timerSamlGenerator = generator;
            RestClientSamlGenerator.log.info("Registering SamlRefresherTask.... arguments:"
                    + generator.toString());
        }

        @Override
        public void run()
        {
            this.timerSamlGenerator.generateSamlTokenId("grant_type=refresh_token&refresh_token=" +  //$NON-NLS-1$
                    RestClientSamlGenerator.refreshTokenId);
        }
    }
}
