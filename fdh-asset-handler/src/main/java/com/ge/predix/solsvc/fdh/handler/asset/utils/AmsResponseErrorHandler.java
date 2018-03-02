package com.ge.predix.solsvc.fdh.handler.asset.utils;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;

/**
 * 
 * @author predix -
 */
public class AmsResponseErrorHandler extends DefaultResponseErrorHandler
{
    private static Logger        log       = LoggerFactory.getLogger(AmsResponseErrorHandler.class);

    @SuppressWarnings("nls")
    @Override
    public void handleError(ClientHttpResponse response)
            throws IOException
    {
        HttpStatus statusCode = response.getStatusCode();
        MediaType contentType = response.getHeaders().getContentType();
        Charset charset = contentType != null ? contentType.getCharSet() : null;
        String body = null;
        try
        {
            body = IOUtils.toString(response.getBody(), "UTF-8");
        }
        catch (IOException e)
        {
            // ignore
        }
        log.debug(String.format("status code: %s, status text: %s, body : %s ", statusCode.value(),
                response.getStatusText(), body));
//        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) { // 401 error
//            
//        }
//            
        switch (statusCode.series())
        {
            case CLIENT_ERROR:
                throw new AmsHttpClientErrorException(statusCode, response.getStatusText(), body, charset);
            case SERVER_ERROR:
                throw new AmsHttpServerErrorException(statusCode, response.getStatusText(), body, charset);
            default:
                throw new RestClientException("Unknown status code [" + statusCode + "]");
        }
    }

    @Override
    public boolean hasError(ClientHttpResponse response)
            throws IOException
    {
        return hasError(response.getStatusCode());
    }

    @Override
    protected boolean hasError(HttpStatus statusCode)
    {
        return (statusCode.series() == HttpStatus.Series.CLIENT_ERROR || statusCode.series() == HttpStatus.Series.SERVER_ERROR);
    }
}
