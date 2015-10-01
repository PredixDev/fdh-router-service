package com.ge.predix.solsvc.fdh.router.service.cxf;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.springframework.stereotype.Component;

/**
 * An example of how to create a Rest service using standard javax.ws.rs annotations
 * and registered with CXF as a spring bean...
 * see src/main/resources/META-INF/spring/predix-boot-cxf-context.xml
 * 
 * @author predix
 *
 */
@Component
@Path("/")
public class DefaultService
{

    /**
     * 
     */
    public DefaultService()
    {
        super();
    }

    /**
     * -
     * 
     * @return string
     */
    @SuppressWarnings("nls")
    @GET
    @Path("ping/")
    public Response doNothing()
    {
        return handleResult("Greetings from CXF Bean Rest Service " + new Date());
    }

    /**
     * @param entity
     *            to be wrapped into JSON response
     * @return JSON response with entity wrapped
     */
    protected Response handleResult(Object entity)
    {
        ResponseBuilder responseBuilder = Response.status(Status.OK);
        responseBuilder.type(MediaType.APPLICATION_JSON);
        responseBuilder.entity(entity);
        return responseBuilder.build();
    }
}
