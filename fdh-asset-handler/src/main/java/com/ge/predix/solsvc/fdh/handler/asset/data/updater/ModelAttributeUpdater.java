/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.handler.asset.data.updater;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.http.Header;
import org.mimosa.osacbmv3_3.DAInt;
import org.mimosa.osacbmv3_3.DAString;
import org.mimosa.osacbmv3_3.DMBool;
import org.mimosa.osacbmv3_3.DMInt;
import org.mimosa.osacbmv3_3.DMReal;
import org.mimosa.osacbmv3_3.DataEvent;
import org.mimosa.osacbmv3_3.OsacbmTimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.engunit.EngUnit;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.predix.entity.model.Model;
import com.ge.predix.solsvc.ext.util.FieldUtil;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.handler.asset.common.FieldModel;
import com.ge.predix.solsvc.fdh.handler.asset.common.RestConstants;
import com.ge.predix.solsvc.fdh.handler.asset.helper.JavaOsaDataTypeConversion;
import com.ge.predix.solsvc.fdh.handler.asset.helper.OsaJavaDataTypeConversion;
import com.ge.predix.solsvc.fdh.handler.asset.helper.OsaJavaDataTypeConversion.SupportedJavaTypes;
import com.ge.predix.solsvc.fdh.handler.asset.helper.OsaJavaDataTypeConversion.SupportedOsaDataTypes;
import com.ge.predix.solsvc.fdh.handler.asset.helper.PaUtility;

/**
 * Retrieves and stores the asset's attribute data
 * 
 * @author 212369540
 */
@SuppressWarnings("nls")
@Component
public class ModelAttributeUpdater implements AttributeUpdaterInterface {
	private static final Logger log = LoggerFactory.getLogger(ModelAttributeUpdater.class);

	@Autowired
	private JsonMapper jsonMapper;

	/**
	 * Retrieves the asset's attribute value as DataEvent The engineering unit
	 * of the asset's attribute is specified in its classification The data is
	 * converted to the engineering unit specified as input parameter Selection
	 * filter is ignored
	 * 
	 * @param headers
	 *            -
	 */
	@Override
	public FieldData retrieveData(Object model, FieldDataCriteria fieldDataCriteria, List<Header> headers) {
		String fieldId = fieldDataCriteria.getFieldSelection().get(0).getFieldIdentifier().getId().toString();

		Object origValue = getAttributeFieldValue(fieldId, model);

		// Convert the retrieved value to DataEvent
		SupportedOsaDataTypes supportedDataType = null;
		EngUnit engUnit = fieldDataCriteria.getFieldSelection().get(0).getExpectedEU();
		String expectedDataType = fieldDataCriteria.getFieldSelection().get(0).getExpectedDataType();
		if (expectedDataType != null)
			supportedDataType = SupportedOsaDataTypes.fromValue(expectedDataType);
		Object useThisValue = origValue;
		if ( !(useThisValue instanceof String) && !useThisValue.getClass().isPrimitive() && !(ClassUtils.wrapperToPrimitive(useThisValue.getClass()) != null))
			useThisValue = this.jsonMapper.toJson(useThisValue);

		DataEvent dataEvent = JavaOsaDataTypeConversion.convertJavaStructureToDataEvent(useThisValue, supportedDataType,
				OsacbmTimeType.OSACBM_TIME_MIMOSA);

		// UoM conversion if needed
		if (engUnit != null && engUnit.getName() != null && !engUnit.getName().isEmpty()) {
			// String expectedUoM = engUnit.getName();
			// String givenUoM = getUoM(headers);
			// if ( givenUoM != null )
			// {
			// FdhAssetEngUnitsConverter uomConverter = new
			// FdhAssetEngUnitsConverter(getUomDataExchange(),
			// getUomProviderAddress(), getUomProviderPort());
			// dataEvent = uomConverter.convert(dataEvent, givenUoM,
			// expectedUoM);
			// }
		}

		// Set the data to fieldData
		FieldIdentifier fieldIdentifier = fieldDataCriteria.getFieldSelection().get(0).getFieldIdentifier();
		String resultId = fieldDataCriteria.getResultId();
		FieldData fieldData = FieldUtil.createOsaData(fieldIdentifier, resultId, engUnit, dataEvent);
		return fieldData;
	}

	/**
	 * Stores the asset's attribute value Data is converted from the source
	 * engineering unit to the target engineering unit if needed
	 * 
	 * @param headers
	 *            -
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void storeOsaData(Object model, FieldModel fieldModel, DataEvent dataEvent, EngUnit engUnit,
			List<Header> headers) {
		try {
			String attrName = fieldModel.getAtrribute();
			if (PropertyUtils.isWriteable(model, attrName)) {
				Object value = PropertyUtils.getProperty(model, attrName);
				if (value instanceof List) {
					if (dataEvent instanceof DAString)
						BeanUtils.setProperty(model, attrName, ((DAString) dataEvent).getValue() == null ? null
								: Arrays.asList(((DAString) dataEvent).getValue()));
					else if (dataEvent instanceof DMReal)
						BeanUtils.setProperty(model, attrName, Arrays.asList(((DMReal) dataEvent).getValue()));
					else if (dataEvent instanceof DMInt)
						BeanUtils.setProperty(model, attrName, Arrays.asList(((DMInt) dataEvent).getValue()));
					else if (dataEvent instanceof DMBool)
						BeanUtils.setProperty(model, attrName, Arrays.asList(((DMBool) dataEvent).isValue()));
					else
						throw new UnsupportedOperationException("dataEventType=" + dataEvent + " not supported");
				} else {
					if (dataEvent instanceof DAString)
						BeanUtils.setProperty(model, attrName, ((DAString) dataEvent).getValue());
					else if (dataEvent instanceof DMReal)
						BeanUtils.setProperty(model, attrName, ((DMReal) dataEvent).getValue());
					else if (dataEvent instanceof DMInt)
						BeanUtils.setProperty(model, attrName, ((DMInt) dataEvent).getValue());
					else if (dataEvent instanceof DMBool)
						BeanUtils.setProperty(model, attrName, ((DMBool) dataEvent).isValue());
					else
						throw new UnsupportedOperationException("dataEventType=" + dataEvent + " not supported");
				}
			} else if (model instanceof Map) {
				@SuppressWarnings("rawtypes")
				Map theMap = (Map) model;
				if (dataEvent instanceof DAString)
					theMap.put(attrName, ((DAString) dataEvent).getValue());
				else if (dataEvent instanceof DMReal)
					theMap.put(attrName, Double.toString(((DMReal) dataEvent).getValue()));
				else if (dataEvent instanceof DMInt)
					theMap.put(attrName, Integer.toString(((DAInt) dataEvent).getValue()));
				else if (dataEvent instanceof DMBool)
					theMap.put(attrName, Boolean.toString(((DMBool) dataEvent).isValue()));
				else
					throw new UnsupportedOperationException("dataEventType=" + dataEvent + " not supported");
			} else if (model instanceof Model) {
				// last resort place it in model.addtionalAttributes
				com.ge.predix.entity.util.map.Map attributes = ((Model) model).getAdditionalAttributes();
				if (attributes == null || attributes.keySet().size() == 0) {
					attributes = new com.ge.predix.entity.util.map.Map();
					attributes.put(attrName, null);
					((Model) model).setAdditionalAttributes(attributes);
					updateAssetAttributeValue(attributes, 0, dataEvent, engUnit);
				} else {
					int i = 0;
					boolean found = false;
					for (Object key : attributes.keySet()) {
						if (key.equals(attrName)) {
							// UoM conversion if needed
							// if ( engUnit != null && engUnit.getName() != null
							// && !engUnit.getName().isEmpty() )
							// {
							// String givenUoM = engUnit.getName();
							// String expectedUoM = getUoM(headers);
							// FdhAssetEngUnitsConverter uomConverter = new
							// FdhAssetEngUnitsConverter(getUomDataExchange(),
							// getUomProviderAddress(), getUomProviderPort());
							// uomConverter.convert(dataEvent, givenUoM,
							// expectedUoM);
							// }

							updateAssetAttributeValue(attributes, i, dataEvent, engUnit);
							found = true;
						}
						i++;
					}
					if (!found) {
						log.warn("Unable to find a place to put the Attribute=" + fieldModel
								+ " placing in AdditionalAttributes in model=" + model);
						// not found, add it here
						attributes.put(attrName,null);
						updateAssetAttributeValue(attributes, i, dataEvent, engUnit);
					}
				}
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);

		}
	}

	private Object getAttributeFieldValue(String fieldId, Object sourceModel) {
		try {
			FieldModel fieldModel = PaUtility.getFieldModel(fieldId);
			Object value = null;

			if (fieldModel.getAtrribute() !=null && PropertyUtils.isReadable(sourceModel, fieldModel.getAtrribute())) {
				value = BeanUtils.getProperty(sourceModel, fieldModel.getAtrribute());
			} else if (fieldModel.getAtrribute() !=null && sourceModel instanceof Model && PropertyUtils
					.isReadable(((Model) sourceModel).getAdditionalAttributes(), fieldModel.getAtrribute())) {
				value = BeanUtils.getProperty(sourceModel, fieldModel.getAtrribute());
			}
			else if ( fieldModel.getField().endsWith(fieldModel.getModel()))
				value = sourceModel;
			else if ( fieldModel.getField().endsWith(fieldModel.getAtrribute()))
				value = sourceModel;
			else
				throw new UnsupportedOperationException("fieldId=" + fieldId + " not found in model=" + sourceModel);
			return value;
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new UnsupportedOperationException("fieldId=" + fieldId + " not found in model=" + sourceModel, e);

		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void updateAssetAttributeValue(Map values, int i, DataEvent dataEvent, EngUnit engUnit) {
		String dataType = null;
		dataType = RestConstants.URI_PRIMITIVE_STRING; // only support this
														// right now
		switch (dataType) {
		case RestConstants.URI_PRIMITIVE_BOOLEAN:
			Object booleanArray = OsaJavaDataTypeConversion.convertDataEventToJavaStructure(dataEvent,
					SupportedJavaTypes.BooleanVector);
			if (booleanArray instanceof Boolean[]) {
				List<Object> booleanList = new ArrayList<Object>(Arrays.asList((Boolean[]) booleanArray));
				values.put(i, booleanList);
			}
			break;
		case RestConstants.URI_PRIMITIVE_NUMBER:
			Object doubleArray = OsaJavaDataTypeConversion.convertDataEventToJavaStructure(dataEvent,
					SupportedJavaTypes.DoubleVector);
			if (doubleArray instanceof Double[]) {
				List<Object> doubleList = new ArrayList<Object>(Arrays.asList((Double[]) doubleArray));
				values.put(i, doubleList);
			}
			break;
		case RestConstants.URI_PRIMITIVE_DATE:
		case RestConstants.URI_PRIMITIVE_STRING:
		default:
			Object string = OsaJavaDataTypeConversion.convertDataEventToJavaStructure(dataEvent,
					SupportedJavaTypes.String);
			if (string instanceof String) {
				// List<Object> stringList = new
				// ArrayList<Object>(Arrays.asList((String[]) stringArray));
				values.put(i, string);
			}
			break;
		}
	}

}
