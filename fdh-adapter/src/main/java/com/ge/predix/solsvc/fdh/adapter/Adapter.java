/*
 * Copyright (c) 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.predix.solsvc.fdh.adapter;

/**
 * 
 * @author predix -
 * @param <SourceT> - the source to adapt from
 * @param <DestT> - the dest to adapt to
 */
public interface Adapter<SourceT, DestT> {
	
	/**
	 * @param source -
	 * @return -
	 */
	public DestT adapt(SourceT source);

}
