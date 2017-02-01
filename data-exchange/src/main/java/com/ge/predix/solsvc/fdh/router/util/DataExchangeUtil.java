package com.ge.predix.solsvc.fdh.router.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class DataExchangeUtil {
	public static List<Header> getRequestHeadersToKeep(MessageContext context, List<String> headersToKeep) {
		List<Header> headers = new ArrayList<Header>();
		for (String key : context.getHttpHeaders().getRequestHeaders().keySet()) {
			if (headersToKeep.contains(key))
				headers.add(new BasicHeader(key, context.getHttpHeaders().getHeaderString(key)));
		}
		return headers;
	}
}
