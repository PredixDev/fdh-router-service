package com.ge.fdh.asset.helper;

import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Utility used by HttpClientUtil to attach SSL context to thread
 * init triggered by bean initialization. caller code sets SSL
 * context to thread.
 */
public class SslHandlerUtil
{
    // if the value of httpsStatus is "enabled" or "active", https is enabled
    private String  httpsStatus               = null;

    private String  httpsPort                 = null;

    private String  httpPort                  = null;

    private String  keyStoreFile              = null;

    private String  keyStorePassword          = null;

    private String  encryptedKeyStorePassword = null;

    private String  keyStorePasswordType      = null;

    private boolean isHttpsEnabled            = false;

    // if the value of authStatus is "enforced" or "active", auth is enabled
    private String  authStatus                = null;
    
    private boolean isAuthEnabled             = false;
    
    /**
     * bean init
     */
    @SuppressWarnings("nls")
    public void init()
    {
        if ( getHttpsStatus() != null
                && (("enforced".equalsIgnoreCase(getHttpsStatus()) || "active".equalsIgnoreCase(getHttpsStatus()))) )  //$NON-NLS-1$//$NON-NLS-2$
        {
            this.setHttpsEnabled(true);
        }
        
        if ( getAuthStatus() != null
                && (("enforced".equalsIgnoreCase(getAuthStatus()) || "active".equalsIgnoreCase(getAuthStatus()))) )
        {
            this.setAuthEnabled(true);
        }
    }

    /**
     * attach SSL context to current thread.
     */
    public void attachSSLTrustToThread()
    {

        KeyStore trustStore = null;
        InputStream trustStream = null;
        TrustManagerFactory trustFactory = null;

        if ( this.isHttpsEnabled() == false ) return;
        try
        {
            trustStore = KeyStore.getInstance("JKS"); //$NON-NLS-1$

            trustStream = new URL(this.getKeyStoreFile()).openStream();

            trustStore.load(trustStream, this.keyStorePassword.toCharArray());
            trustStream.close();

            trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustFactory.init(trustStore);
            TrustManager[] tm = trustFactory.getTrustManagers();

            SSLContext ctx = SSLContext.getInstance("TLS"); //$NON-NLS-1$
            ctx.init(null, tm, new SecureRandom());
            SSLContext.setDefault(ctx);

        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException("Unable to attach SSL context to thread", ex); //$NON-NLS-1$
        }
        finally
        {
            try
            {
                if (trustStream != null)
                {
                    trustStream.close();
                }
            }
            catch (Exception ex)
            {
                // error
            }
        }

    }

    /**
     * @return the httpsStatus
     */
    public String getHttpsStatus()
    {
        return this.httpsStatus;
    }

    /**
     * @param httpsStatus the httpsStatus to set
     */
    public void setHttpsStatus(String httpsStatus)
    {
        this.httpsStatus = httpsStatus;
    }

    /**
     * @return the keyStoreFile
     */
    public String getKeyStoreFile()
    {
        return this.keyStoreFile;
    }

    /**
     * @param keyStoreFile the keyStoreFile to set
     */
    public void setKeyStoreFile(String keyStoreFile)
    {
        this.keyStoreFile = keyStoreFile;
    }

    /**
     * @return the keyStorePassword
     */
    public String getKeyStorePassword()
    {
        return this.keyStorePassword;
    }

    /**
     * @param keyStorePassword the keyStorePassword to set
     */
    public void setKeyStorePassword(String keyStorePassword)
    {
        this.keyStorePassword = keyStorePassword;
    }

    /**
     * @return the keyStorePasswordType
     */
    public String getKeyStorePasswordType()
    {
        return this.keyStorePasswordType;
    }

    /**
     * @param keyStorePasswordType the keyStorePasswordType to set
     */
    public void setKeyStorePasswordType(String keyStorePasswordType)
    {
        this.keyStorePasswordType = keyStorePasswordType;
    }

    /**
     * @return the encryptedKeyStorePassword
     */
    public String getEncryptedKeyStorePassword()
    {
        return this.encryptedKeyStorePassword;
    }

    /**
     * @param encryptedKeyStorePassword the encryptedKeyStorePassword to set
     */
    public void setEncryptedKeyStorePassword(String encryptedKeyStorePassword)
    {
        this.encryptedKeyStorePassword = encryptedKeyStorePassword;
    }

    /**
     * @return the httpsPort
     */
    public String getHttpsPort()
    {
        return this.httpsPort;
    }

    /**
     * @param httpsPort the httpsPort to set
     */
    public void setHttpsPort(String httpsPort)
    {
        this.httpsPort = httpsPort;
    }

    /**
     * @return the isHttpsEnabled
     */
    public boolean isHttpsEnabled()
    {
        return this.isHttpsEnabled;
    }

    /**
     * @param isHttpsEnabled the isHttpsEnabled to set
     */
    public void setHttpsEnabled(boolean isHttpsEnabled)
    {
        this.isHttpsEnabled = isHttpsEnabled;
    }

    /**
     * @return the httpPort
     */
    public String getHttpPort()
    {
        return this.httpPort;
    }

    /**
     * @param httpPort the httpPort to set
     */
    public void setHttpPort(String httpPort)
    {
        this.httpPort = httpPort;
    }

    /**
     * @return the authStatus
     */
    public String getAuthStatus()
    {
        return this.authStatus;
    }

    /**
     * @param authStatus the authStatus to set
     */
    public void setAuthStatus(String authStatus)
    {
        this.authStatus = authStatus;
    }

    /**
     * @return the isAuthEnabled
     */
    public boolean isAuthEnabled()
    {
        return this.isAuthEnabled;
    }

    /**
     * @param isAuthEnabled the isAuthEnabled to set
     */
    public void setAuthEnabled(boolean isAuthEnabled)
    {
        this.isAuthEnabled = isAuthEnabled;
    }

}
