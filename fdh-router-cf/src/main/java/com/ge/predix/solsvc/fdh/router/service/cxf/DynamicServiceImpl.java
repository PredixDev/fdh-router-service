package com.ge.predix.solsvc.fdh.router.service.cxf;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.dsp.core.spi.IServiceManagerService;

/**
 * This Rest service registers itself the same way it was done in Predix Core 14.3
 * 
 * @author 212307911
 */
@Component
public class DynamicServiceImpl implements DynamicService {

    @Autowired
	private IServiceManagerService serviceManagerService;
    
	/**
	 * 
	 */
	public DynamicServiceImpl() {
		super();
	}

    /**
     * 
     */
    @PostConstruct
    public void init()
    {
        this.serviceManagerService.createRestWebService(this, null);
    }
	/**
	 * 
	 * @return -
	 */
	@SuppressWarnings("nls")
    @Override
    public Response doNothing() {
		return handleResult("Greetings from Self Registering Cloud Service " + new Date());
	}

	/**
	 * @param entity
	 *            to be wrapped into JSON response
	 * @return JSON response with entity wrapped
	 */
	protected Response handleResult(Object entity) {
		ResponseBuilder responseBuilder = Response.status(Status.OK);
		responseBuilder.type(MediaType.APPLICATION_JSON);
		responseBuilder.entity(entity);
		return responseBuilder.build();
	}
}

