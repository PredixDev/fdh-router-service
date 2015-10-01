package com.ge.predix.solsvc.fdh.router.validator;

import org.springframework.stereotype.Component;

import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataRequest;

/**
 * 
 * @author predix
 */
@Component
public class CBRGetFieldDataValidator extends BaseValidator {

	/**
	 * @param getFieldDataRequest
	 *            -
	 * @param errorHolder
	 *            -
	 */
	@SuppressWarnings("nls")
	public void validate(GetFieldDataRequest getFieldDataRequest) {
		if (isAssetFieldCriteriaListNull(getFieldDataRequest))
			throw new RuntimeException(
					"GetFieldData Request is invalid for isAssetFieldCriteriaListNull");

		if (isAssetListCriteriaNull(getFieldDataRequest))
			throw new RuntimeException(
					"GetFieldData Request is invalid for isAssetListCriteriaNull");
	}

	/**
	 * @param getFieldDataRequest
	 *            -
	 * @param errorHolder
	 *            -
	 * @return -
	 */
	boolean isAssetListCriteriaNull(GetFieldDataRequest getFieldDataRequest)
    {

        if ( (getFieldDataRequest.getFieldDataCriteria() == null
                || getFieldDataRequest.getFieldDataCriteria().size() == 0) )
        {
             return true;
        }
        return false;
    }

	/**
	 * @param getFieldDataRequest
	 *            -
	 * @param errorHolder
	 *            -
	 * @return -
	 */
	boolean isAssetFieldCriteriaListNull(GetFieldDataRequest getFieldDataRequest)
    {
        if ( getFieldDataRequest.getFieldDataCriteria() == null )
        {
             return true;
        }
        return false;
    }
}
