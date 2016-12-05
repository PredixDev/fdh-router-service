/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.handler.timeseries;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author predix -
 */
public class Heartbeat {
	private static final Logger log = LoggerFactory.getLogger(Heartbeat.class);

	/**
	 * @param id -
	 * @return -
	 */
	@SuppressWarnings("nls")
	public static Response heartbeat(String id) {
		try {
			if (id == null)
				return handleResult(
						"Usage: To reflect back your string, pass a queryParam with id=  e.g. http://localhost:9090/service/fdhtimeseries/fielddatahandler/heartbeat?id=hello world");
			return handleResult(id);
		} catch (Throwable e) {
			log.error("error at boundary", e);
			// @TODO put in ErrorDataEvent if applicable for this operation
			throw e;
		}
	}

	/**
	 * @param entity to be wrapped into JSON response
	 * @return JSON response with entity wrapped
	 */
	protected static Response handleResult(Object entity) {
		ResponseBuilder responseBuilder = Response.status(Status.OK);
		responseBuilder.type(MediaType.APPLICATION_JSON);
		responseBuilder.entity(entity);
		return responseBuilder.build();
	}
}
