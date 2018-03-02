/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
package com.ge.predix.solsvc.fdh.handler.asset.validator;

import org.springframework.stereotype.Component;

import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;

/**
 * validates PutFieldDataRequest
 */
@Component
public class PutFieldDataCriteriaValidator {
	/**
	 * @param putFieldDataCriteria
	 *            -
	 * @param putFieldDataResult
	 *            -
	 * @return -
	 */
	public boolean validate(PutFieldDataCriteria putFieldDataCriteria, PutFieldDataResult putFieldDataResult) {

//		if (!validateFilter(putFieldDataCriteria)) {
//			putFieldDataResult.getErrorEvent().add("invalid Filter");
//			return false;
//		}
		
		return true;
	}

	/**
	 * @param putFieldDataCriteria
	 *            -
	 * @return true if validation passes and false otherwise
	 */
	public boolean isValidRequest(PutFieldDataCriteria putFieldDataCriteria) {

		if (!validateFilter(putFieldDataCriteria)) {
			return false;
		}

		return true;
	}

	private boolean validateFilter(PutFieldDataCriteria putFieldDataCriteria) {
		try {
			if (putFieldDataCriteria.getFilter() != null) {
				return true;
			}
		} catch (Throwable t) {
			return false;
		}
		return false;
	}

}
