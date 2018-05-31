/*
 * Copyright (c) 2018 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.fdh.handler;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.ge.predix.entity.util.map.Map;

/**
 * 
 * @author predix -
 */
public class FDHUtil {
	
	/**
	 * @param headers -
	 * @return -
	 */
	public static List<Header> copyHeaders(List<Header> headers) {
		List<Header> headersToUse = new ArrayList<Header>();
		for ( Header header : headers ) {
			headersToUse.add(header);
		}
		return headersToUse;
	}
	
	/**
	 * @param headers -
	 * @param criteriaHeaders -
	 * @param headerName -
	 * @return -
	 */
	public static boolean setHeader(List<Header> headers, Map criteriaHeaders, String headerName) {
		boolean zoneIdFound = false;
		boolean overrideZoneId = false;
		Header headerToRemove = null;
		Header headerToAdd = null;
		if ( criteriaHeaders != null && criteriaHeaders.containsKey(headerName) )
			overrideZoneId = true;
		for ( Header header : headers ) {
			if ( header.getName().equals(headerName)) {
				if ( criteriaHeaders != null && overrideZoneId ) {
					headerToRemove = header;
					headerToAdd = new BasicHeader(headerName, (String) criteriaHeaders.get(headerName));
				}
				zoneIdFound  = true;
				break;
			}
		}
		if ( headerToRemove != null ) {
			headers.remove(headerToRemove);
			headers.add(headerToAdd);
		}
		else if ( !zoneIdFound && criteriaHeaders != null && overrideZoneId ) {
			headers.add(new BasicHeader(headerName, (String) criteriaHeaders.get(headerName)));
			zoneIdFound  = true;
		}
			
			
		return zoneIdFound;
	}

}
