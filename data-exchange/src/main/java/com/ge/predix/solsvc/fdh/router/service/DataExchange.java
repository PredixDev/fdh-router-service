package com.ge.predix.solsvc.fdh.router.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.fielddatahandler.entity.createfields.CreateFieldsRequest;
import com.ge.predix.fielddatahandler.entity.createfields.CreateFieldsResult;

/**
 * 
 * @author predix
 */
@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,MediaType.MULTIPART_FORM_DATA})
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@Path("/fdhrouter/fielddatahandler")
public interface DataExchange {

    /**
     * Rest implementation for the service to act as a GET heart beat: /heartbeat
     * @param id - the id to reflect back
     * 
     *
     * @return the Response
     */
	@GET
    @Path("/")
    public Response hello();
	
    /**
     * @param id -
     * @return -
     */
    @GET
    @Path("/heartbeat")
    public Response heartbeat(@QueryParam("id") String id);
    
	/**
	 * @param context -
	 * @param request -
	 * @return -
	 */
    @POST
    @Path("/getfielddata")
    public GetFieldDataResult getFieldData(@Context MessageContext context,
			GetFieldDataRequest request);

	/**
	 * @param context -
	 * @param request -
	 * @return -
	 */
    @POST
    @Path("/putfielddata")
    public PutFieldDataResult putFieldData(@Context MessageContext context, PutFieldDataRequest request);

	/**
	 * For advanced use cases where you want to save meta data about the Field
	 * 
	 * @param context -
	 * @param request -
	 * @return -
	 */
    @POST
    @Path("/createfields")
	public CreateFieldsResult createFields(@Context MessageContext context,
			CreateFieldsRequest request);

    /**
	 * @param context -
     * @param body -
	 * @param request -
	 * @return -
	 */
    @POST
    @Path("/putfielddatafile")
    PutFieldDataResult putFieldData(@Context MessageContext context,
			MultipartBody body);

}
