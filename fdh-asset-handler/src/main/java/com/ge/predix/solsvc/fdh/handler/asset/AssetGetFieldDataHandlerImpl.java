/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.solsvc.fdh.handler.asset;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ge.predix.entity.assetfilter.AssetFilter;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.predix.entity.fieldselection.FieldSelection;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.model.Model;
import com.ge.predix.solsvc.bootstrap.ams.factories.AssetClientImpl;
import com.ge.predix.solsvc.fdh.adapter.factory.AdapterFactory;
import com.ge.predix.solsvc.fdh.handler.GetDataHandler;
import com.ge.predix.solsvc.fdh.handler.asset.common.AssetQueryBuilder;
import com.ge.predix.solsvc.fdh.handler.asset.common.FieldModel;
import com.ge.predix.solsvc.fdh.handler.asset.data.AttributeHandlerFactory;
import com.ge.predix.solsvc.fdh.handler.asset.data.AttributeHandlerInterface;
import com.ge.predix.solsvc.fdh.handler.asset.helper.PaUtility;
import com.ge.predix.solsvc.fdh.handler.asset.validator.GetFieldDataValidator;

/**
 * GetFieldDataProcessor processes GetFieldDataRequest.
 * For each FieldDataCriteria,
 * - Retrieve the asset using Selector
 * - Get the requested data type, engineering unit and filters
 * - Create the data retrieval strategy and delegate the request
 * - Prepare the GetFieldDataResult and returns
 * 
 * @author 212369540
 */
@SuppressWarnings("nls")
@Component(value = "assetGetFieldDataHandler")
@ImportResource(
{
        "classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml",
        "classpath*:META-INF/spring/fdh-asset-handler-scan-context.xml",
        "classpath*:META-INF/spring/fdh-adapter-scan-context.xml"
})
@Profile("asset")
public class AssetGetFieldDataHandlerImpl
        implements GetDataHandler
{
    private static final Logger     log    = LoggerFactory.getLogger(AssetGetFieldDataHandlerImpl.class);

    @Autowired
    private GetFieldDataValidator   getFieldDataValidator;

    @Autowired
	@Qualifier("AssetClient")
	private AssetClientImpl assetClient;

    @Autowired
    private AttributeHandlerFactory attributeHandlerFactory;

    @Autowired
    private AdapterFactory          adapterFactory;

    /*
     * (non-Javadoc)
     * @see
     * com.ge.fdh.asset.processor.IGetFieldDataProcessor#processRequest(com.ge.predix.fielddatahandler.entity.getfielddata.GetFieldDataRequest)
     */
    @Override
    public GetFieldDataResult getData(GetFieldDataRequest request, Map<Integer, Object> modelLookupMap,
            List<Header> headers)
    {
        FieldDataCriteria currentCriteria = null;
        try
        {
            GetFieldDataResult result = new GetFieldDataResult();

            // 1. Validate Request
            if ( !this.getFieldDataValidator.validate(request, result) ) return result;

            for (FieldDataCriteria criteria : request.getFieldDataCriteria())
            {
                currentCriteria = criteria;

                // 2. Get data from Asset
                List<Object> models = retrieveModels(criteria, modelLookupMap, headers);
                if ( models != null )
                {
                    result.getFieldData().addAll(processModels(criteria, models, modelLookupMap, headers));
                }
            }

            // 3. Send Response
            return result;
        }
        catch (Throwable t)
        {
            String message = "error getting data for data event FieldDataCriteria=" + currentCriteria;
            throw new RuntimeException(message, t);
        }
    }

    /**
     * @param fieldDataCriteria
     * @param modelLookupMap
     * @param headers
     * @param httpMethod
     * @param modelLookupMap
     * @return -
     * @throws JsonProcessingException
     */
    @SuppressWarnings("unchecked")
    private List<Object> retrieveModels(FieldDataCriteria fieldDataCriteria, Map<Integer, Object> modelLookupMap,
            List<Header> headers)
            throws JsonProcessingException
    {
        if ( fieldDataCriteria.getFilter() instanceof AssetFilter )
        {
            return (List<Object>) retrieveModels(fieldDataCriteria, headers);
        }
        throw new UnsupportedOperationException("filter=" + fieldDataCriteria.getFilter() + " not supported");
    }

    /**
     * @param fieldDataCriteria
     *            -
     * @param headers
     *            -
     * @return -
     * 
     */
    protected List<?> retrieveModels(FieldDataCriteria fieldDataCriteria, List<Header> headers)
    {
        // keeping it simple, assume the first Selection holds the type of model desired
        this.assetClient.setZoneIdInHeaders(headers);
        List<FieldSelection> selections = fieldDataCriteria.getFieldSelection();
        FieldSelection firstSelection = selections.get(0);
        String field = firstSelection.getFieldIdentifier().getId().toString();
        FieldModel fieldModel = PaUtility.getFieldModel(field);

        if ( fieldDataCriteria.getFilter() instanceof AssetFilter )
        {

            AssetFilter assetFilter = (AssetFilter) fieldDataCriteria.getFilter();

            // GET Asset by URIsure
            AssetQueryBuilder assetQueryBuilder = new AssetQueryBuilder();
            assetQueryBuilder.setUri(assetFilter.getUri());
            if(! StringUtils.isEmpty(assetFilter.getFilterString())){
                assetQueryBuilder.setUri(assetFilter.getUri()+"?filter="+assetFilter.getFilterString());
            }

            @SuppressWarnings("rawtypes")
            List<?> models = new ArrayList();
            // if the request is for PredixString bypass Transformation
            
            log.info("fieldModel.getModelForUnMarshal()" + fieldModel.getModelForUnMarshal());
            if ( "PredixString".equalsIgnoreCase(fieldModel.getModelForUnMarshal()) )
            {
                List<String> modelString = this.assetClient.getModels(assetQueryBuilder.build(), headers);
                models = (List<?>) this.adapterFactory.getAdapter("StringList", "PredixStringList").adapt(modelString);
            }
            else
            {
                models = this.assetClient.getModels(assetQueryBuilder.build(), fieldModel.getModelForUnMarshal(),
                        headers);
            }

            return models;
        }
        throw new UnsupportedOperationException(
                "Filter" + fieldDataCriteria.getFilter().getClass().getCanonicalName() + " not supported");
    }

    /**
     * Loop across the Models found and get the attribute from the Object Graph.
     * Add it to the FieldData. Say something requested /asset/address/city.
     * We would get the city and place it in the expectedDataType.
     * 
     * @param fieldDataCriteria
     * @param models
     * @param modelLookupMap
     * @param headers
     * @param httpMethod -
     * @return
     */
    private List<FieldData> processModels(FieldDataCriteria fieldDataCriteria, List<Object> models,
            Map<Integer, Object> modelLookupMap, List<Header> headers)
    {
        List<FieldData> fieldDataList = new ArrayList<FieldData>();
        FieldData fieldData = new FieldData();
        fieldData.setResultId(fieldDataCriteria.getResultId());
        for (Object model : models)
        {
            for (FieldSelection selection : fieldDataCriteria.getFieldSelection())
            {
                String field = selection.getFieldIdentifier().getId().toString();
                if ( !StringUtils.isEmpty(field) )
                {
                    FieldModel fieldModel = PaUtility.getFieldModel(field);
                    // find the attribute and enrich the fieldData
                    fieldData = processModel(fieldData, fieldDataCriteria, model, fieldModel, modelLookupMap, headers);
                    fieldDataList.add(fieldData); // fix: to add each field data
                }
            }
        }

        return fieldDataList;
    }

    /**
     * Based on the FieldId traverse the object graph till we find the attribute. Then
     * enrich the FieldData with the data for that attribute
     * 
     * @param fieldData
     * @param fieldDataCriteria
     * @param model
     * @param fieldModel
     * @param modelLookupMap
     * @param headers
     * @return -
     */
    private FieldData processModel(FieldData fieldData, FieldDataCriteria fieldDataCriteria, Object model,
            FieldModel fieldModel, Map<Integer, Object> modelLookupMap, List<Header> headers)
    {
        try
        {
            Object objectContainingAttribute = model;
            if ( fieldModel.getChildModels() != null && fieldModel.getChildModels().size() > 0 )
            {
                int childModelNameIndex = 0;
                objectContainingAttribute = traverseToChild(fieldData, fieldDataCriteria, objectContainingAttribute,
                        fieldModel, childModelNameIndex, modelLookupMap, headers);
                if ( objectContainingAttribute != null )
                    return getAttribute(fieldDataCriteria, objectContainingAttribute, fieldModel, headers);
            }
            else
            {
                return getAttribute(fieldDataCriteria, objectContainingAttribute, fieldModel, headers);
            }

        }
        catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        return fieldData;
    }

    /**
     * @param fieldDataCriteria
     * @param objectContainingAttribute
     * @param fieldModel
     * @param headers -
     * @return
     */
    private FieldData getAttribute(FieldDataCriteria fieldDataCriteria, Object objectContainingAttribute,
            FieldModel fieldModel, List<Header> headers)
    {
        // We have the object with the attribute, get data at the attribute level
        AttributeHandlerInterface attributeHandler = this.attributeHandlerFactory.getHandler(fieldModel.getField());
        FieldData fieldData = attributeHandler.retrieveData(objectContainingAttribute, fieldDataCriteria, headers);
        return fieldData;
    }

    /**
     * This method recursively visits objects within the retrieved Model as instructed by the rest based FieldId. Following Rest principles we can
     * easily traverse the Object Graph. Here is fairly complex example showing how we can visit Objects within the found Model but we can even jump to
     * another model following a getXxxxUri() link (requiring another query to Asset of course)
     * 
     * e.g. /asset/assetTag/crank-frame-dischargepressure/tagExtensions/attributes/alertStatus/value
     * would find the AssetTag object on the Asset. In this case assetTag is a map with a key='crank-frame-dischargepressure'. The value returned is an
     * AssetTag. On this object is not getTagExtensions() but instead it has a getTagExtensionsUri() which is a reference (foreign-key) to another Model
     * in PredixAsset. We look up the URI and find that it has a map called 'attributes' with a key="alertStatus". The value is an Attribute object which has a
     * getValue() method.
     * 
     * @param fieldData -
     * @param fieldDataCriteria -
     * @param model -
     * @param fieldModel -
     * @param modelLookupMap -
     * @param headers -
     * @param childModelNameIndex -
     * @return -
     * @throws IllegalAccessException -
     * @throws InvocationTargetException -
     * @throws NoSuchMethodException -
     */
    protected Object traverseToChild(FieldData fieldData, FieldDataCriteria fieldDataCriteria, Object model,
            FieldModel fieldModel, int childModelNameIndex, Map<Integer, Object> modelLookupMap, List<Header> headers)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        Object objectContainingAttribute = model;
        // attribute is in an inner object
        String childModelName = fieldModel.getChildModels().get(childModelNameIndex);
        if ( PropertyUtils.isReadable(model, childModelName) )
        {
            // loop across the instance vars till one matches
            for (Field field : model.getClass().getDeclaredFields())
            {
                if ( field.getName().equals(childModelName) )
                {
                    // we found an instance var that matches the childModel name
                    field.setAccessible(true);
                    objectContainingAttribute = field.get(model);
                    if ( objectContainingAttribute == null )
                        throw new UnsupportedOperationException(
                                "Unable to get data from a Null object. field=" + fieldModel);
                    else if ( objectContainingAttribute instanceof Map )
                    {
                        // handle if it's a Map
                        if ( fieldModel.getChildModels().size() < childModelNameIndex + 2 )
                        {
                            // atribute is get/set in the map
                            if ( fieldModel.getAtrribute() != null && !fieldModel.getAtrribute().equals("*") )
                            {
                                objectContainingAttribute = ((Map) objectContainingAttribute)
                                        .get(fieldModel.getAtrribute());
                            }
                        }
                        else
                        {
                            // must need to traverse to the map value (in other words the attribute is in an interior object)
                            String key = fieldModel.getChildModels().get(childModelNameIndex + 1);
                            @SuppressWarnings("rawtypes")
                            Object mapValue = ((Map) objectContainingAttribute).get(key);
                            if ( mapValue != null )
                            {
                                objectContainingAttribute = mapValue;
                                if ( fieldModel.getChildModels().size() > childModelNameIndex + 2 )
                                {
                                    objectContainingAttribute = traverseToChild(fieldData, fieldDataCriteria,
                                            objectContainingAttribute, fieldModel, childModelNameIndex + 2,
                                            modelLookupMap, headers);

                                }
                                else if ( fieldModel.getAtrribute().equals("*") )
                                {
                                    // just return the map Entry
                                }
                                else if ( !PropertyUtils.isReadable(objectContainingAttribute,
                                        fieldModel.getAtrribute()) )
                                {
                                    // attribute not here, check if we need to follow a reference and to another query to Asset
                                    throw new UnsupportedOperationException(
                                            "Object Containing Attribute is a Map we successfully found the key but the attribute is not here and a reference to another ModelUri was not specified");
                                }
                            }
                            else
                                throw new RuntimeException("key=" + key + " not found in " + objectContainingAttribute);
                        }
                    }
                    else if ( objectContainingAttribute instanceof List )
                    {
                        // handle if it's a List
                        // predix asset does not support list of objects yet
                        boolean itemFoundInList = false;
                        String uri = fieldModel.getChildModels().get(1);
                        @SuppressWarnings("rawtypes")
                        List objectContainingAttributeList = (List) objectContainingAttribute;
                        for (Object item : objectContainingAttributeList)
                        {
                            if ( PropertyUtils.isReadable(objectContainingAttributeList, "uri") )
                            {
                                String uriValue = BeanUtils.getProperty(item, "uri");
                                if ( uri.equals(uriValue) )
                                {
                                    objectContainingAttribute = item;
                                    itemFoundInList = true;
                                    break;
                                }
                            }
                        }
                        if ( !itemFoundInList )
                            throw new RuntimeException("uri=" + uri + " not found in " + objectContainingAttributeList);
                    }
                    else if ( fieldModel.getChildModels().size() > childModelNameIndex + 1 )
                    {
                        objectContainingAttribute = traverseToChild(fieldData, fieldDataCriteria,
                                (Model) objectContainingAttribute, fieldModel, childModelNameIndex + 1, modelLookupMap,
                                headers);
                    }
                    // else if must be a regular object
                    break;
                }
            }
        }
        else
        {
            // attribute is in a referenced Model. We'll have to get the Uri and make another query to Asset
            followReference(fieldData, fieldDataCriteria, model, fieldModel, childModelName, modelLookupMap, headers);
            return null;
        }
        return objectContainingAttribute;
    }

    /**
     * @param fieldData -
     * @param fieldDataCriteria -
     * @param model -
     * @param fieldModel -
     * @param childModelPropertyName -
     * @param modelLookupMap -
     * @param headers -
     * @throws IllegalAccessException -
     * @throws InvocationTargetException -
     * @throws NoSuchMethodException -
     */
    protected void followReference(FieldData fieldData, FieldDataCriteria fieldDataCriteria, Object model,
            FieldModel fieldModel, String childModelPropertyName, Map<Integer, Object> modelLookupMap,
            List<Header> headers)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        String childUriProperty = PaUtility.firstCharLower(childModelPropertyName) + "Uri";
        String childUriValue = BeanUtils.getProperty(model, childUriProperty);

        String childField = fieldModel.getField();
        childField = "/" + childField.substring(childField.indexOf(childModelPropertyName));
        String childModelName = PaUtility.getModelName(childUriValue);
        childField = childField.replace(childModelPropertyName, childModelName);
        FieldModel childFieldModel = PaUtility.getFieldModel(childField);

        AssetQueryBuilder query = new AssetQueryBuilder();
        query.setModel(fieldModel.getModel());
        query.setUri(childUriValue);
        List<Object> models = this.assetClient.getModels(query.build(), childFieldModel.getModelForUnMarshal(),
                headers);
        if ( models == null || (models != null && models.size() != 1) )
        {
            int size = 0;
            if ( models != null ) size = models.size();
            // we expect exactly one model to return, if not throw error
            throw new RuntimeException("Expected to retrieve exactly 1 asset with the model selector, retrieved " + size
                    + " instead. modelselector=" + query.build());
        }
        Object childModelFound = models.get(0);
        // keep the resulting model in memory
        modelLookupMap.put(query.hashCode(), model);

        // recursive call to next model
        processModel(fieldData, fieldDataCriteria, childModelFound, childFieldModel, modelLookupMap, headers);
    }

}
