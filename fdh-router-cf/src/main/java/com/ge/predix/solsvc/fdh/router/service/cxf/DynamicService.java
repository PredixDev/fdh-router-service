package com.ge.predix.solsvc.fdh.router.service.cxf;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * An example of how to create a Rest service using standard javax.ws.rs annotations but registering with CXF
 * the way it was done in Predix 14.3
 * 
 * @author predix
 */
@Consumes({ "application/json", "application/xml" })
@Produces({ "application/json", "application/xml" })
@Path("/dynamicservice")
public interface DynamicService {

	/**
	 * @return -
	 */
	@GET
	@Path("/dynamic")
    public Response doNothing();
	
}
