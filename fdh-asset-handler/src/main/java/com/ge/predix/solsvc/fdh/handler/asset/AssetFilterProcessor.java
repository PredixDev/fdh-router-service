/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.handler.asset;

import java.util.List;

import org.apache.http.Header;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.assetfilter.AssetFilter;
import com.ge.predix.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.predix.entity.fieldselection.FieldSelection;
import com.ge.predix.entity.model.Model;
import com.ge.predix.solsvc.fdh.handler.asset.common.AssetQueryBuilder;
import com.ge.predix.solsvc.fdh.handler.asset.common.FieldModel;
import com.ge.predix.solsvc.fdh.handler.asset.helper.PaUtility;

/**
 * 
 * @author predix -
 */
@Component
public class AssetFilterProcessor extends AbstractFdhRequestProcessor {

	/**
	 * @param fieldDataCriteria
	 *            -
	 * @return -
	 */
	public boolean validateAssetFilter(FieldDataCriteria fieldDataCriteria) {
		// no validations yet
		return true;

	}

	/**
	 * @param fieldDataCriteria
	 *            -
	 * @param headers
	 *            -
	 * @return -
	 */
	@SuppressWarnings("nls")
	protected List<Model> retrieveModels(FieldDataCriteria fieldDataCriteria,
			List<Header> headers) {
		// keeping it simple, assume the first Selection holds the type of model
		// desired
		List<FieldSelection> selections = fieldDataCriteria.getFieldSelection();
		FieldSelection firstSelection = selections.get(0);
		String field = firstSelection.getFieldIdentifier().getId().toString();
		FieldModel fieldModel = PaUtility.getFieldModel(field);

		if (fieldDataCriteria.getFilter() instanceof AssetFilter) {
			
			AssetFilter assetFilter = (AssetFilter) fieldDataCriteria
					.getFilter();
			
			
			//GET Asset by URI
			AssetQueryBuilder assetQueryBuilder = new AssetQueryBuilder();
			assetQueryBuilder.setUri(assetFilter.getUri());
			List<Model> models = this.modelFactory.getModels(
					assetQueryBuilder.build(), fieldModel.getModelForUnMarshal(),
					headers);
			
			return models;
		}
		throw new UnsupportedOperationException("Filter" + fieldDataCriteria.getFilter().getClass().getCanonicalName() + " not supported");
	}

}
