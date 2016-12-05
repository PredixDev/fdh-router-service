/*
 * Copyright (c) 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.fdh.adapter.factory;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.ge.predix.solsvc.fdh.adapter.Adapter;

/**
 * 
 * @author predix -
 */
@Component
public class AdapterFactory implements ApplicationContextAware {
	
	private ApplicationContext applicationContext;

	/**
	 * @param <SourceT> - the source to adapt from
	 * @param <DestT> - the dest to adapt to
	 * @param source - the source to adapt from
	 * @param dest - the dest to adapt to
	 * @return -
	 */
	@SuppressWarnings({ "nls", "unchecked" })
	public <SourceT, DestT> Adapter<SourceT, DestT> getAdapter(String source, String dest) {
		
		if ( source == null || dest == null )
			throw new UnsupportedOperationException("unable to find adapter for null source or null dest");
		
		String localSource = source.substring(0,1).toLowerCase();
		String localDest = dest.substring(1);
		String bean = localSource + "To" + localDest;
		
		return (Adapter<SourceT, DestT>) this.applicationContext.getBean(bean );
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
