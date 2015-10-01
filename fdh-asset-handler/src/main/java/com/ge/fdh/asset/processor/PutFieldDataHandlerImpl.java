/*
 * Copyright (c) 2014 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.fdh.asset.processor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.http.Header;
import org.mimosa.osacbmv3_3.DataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.dsp.pm.ext.entity.engunit.EngUnit;
import com.ge.dsp.pm.ext.entity.fielddata.Data;
import com.ge.dsp.pm.ext.entity.fielddata.OsaData;
import com.ge.dsp.pm.ext.entity.fieldidentifiervalue.FieldIdentifierValue;
import com.ge.dsp.pm.ext.entity.model.Model;
import com.ge.dsp.pm.ext.entity.selectionfilter.FieldSelectionFilter;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataCriteria;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataResult;
import com.ge.fdh.asset.common.FieldModel;
import com.ge.fdh.asset.common.ModelQuery;
import com.ge.fdh.asset.data.updater.AttributeUpdaterInterface;
import com.ge.fdh.asset.helper.PaUtility;
import com.ge.fdh.asset.validator.PutFieldDataCriteriaValidator;
import com.ge.fdh.asset.validator.PutFieldDataRequestValidator;
import com.ge.predix.solsvc.fdh.handler.PutFieldDataHandler;

/**
 * PutFieldDataProcessor processes PutFieldDataRequest
 * - Retrieves the asset per the asset selector in the request
 * - For each field data in the list, convert UoM as needed and update the asset with given data
 * 
 * @author 212397779
 */
@SuppressWarnings("nls")
@Component(value = "assetPutFieldDataHandler")
public class PutFieldDataHandlerImpl extends AbstractFdhRequestProcessor
        implements PutFieldDataHandler
{
    private static final Logger           log = LoggerFactory.getLogger(PutFieldDataHandlerImpl.class);

    @Autowired
    private PutFieldDataRequestValidator  putFieldDataRequestValidator;

    @Autowired
    private PutFieldDataCriteriaValidator putFieldDataCriteriaValidator;


    /*
     * (non-Javadoc)
     * @see
     * com.ge.fdh.asset.processor.IPutFieldDataProcessor#processRequest(com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataRequest)
     */
    @Override
    public PutFieldDataResult putFieldData(PutFieldDataRequest request, Map<Integer, Model> modelLookupMap,
            List<Header> headers, String httpMethod)
    {
        try
        {
            PutFieldDataResult putFieldDataResult = new PutFieldDataResult();

            if ( !this.putFieldDataRequestValidator.validate(request, putFieldDataResult) ) return putFieldDataResult;

            this.restClient.addZoneToHeaders(headers, this.assetRestConfig.getZoneId());

            putFieldDataProcessor(request, putFieldDataResult, modelLookupMap, headers, httpMethod);
            return putFieldDataResult;
        }
        catch (Throwable e)
        {
            String msg = "unable to process request errorMsg=" + e.getMessage() + " request.correlationId="
                    + request.getCorrelationId() + " solutionIdentifier" + request.getSolutionIdentifier()
                    + " request = " + request;
            log.error(msg, e);
            RuntimeException dspPmException = new RuntimeException(msg, e);
            throw dspPmException;
        }
    }

    private void putFieldDataProcessor(PutFieldDataRequest putFieldDataRequest, PutFieldDataResult putFieldDataResult,
            Map<Integer, Model> modelLookupMap, List<Header> headers, String httpMethod)
    {
        List<PutFieldDataCriteria> fieldCriteriaList = putFieldDataRequest.getPutFieldDataCriteria();
        for (PutFieldDataCriteria fieldDataCriteria : fieldCriteriaList)
        {
            this.putFieldDataCriteriaValidator.validate(fieldDataCriteria, putFieldDataResult);

            // retrieve the entity
            String field = (String) fieldDataCriteria.getFieldData().getField().getFieldIdentifier().getId();
            FieldModel fieldModel = PaUtility.getFieldModel(field);
            List<Model> models = retrieveModels(fieldDataCriteria, fieldModel, headers, httpMethod);
            if ( models != null )
            {
                for (Model model : models)
                {
                    processModel(fieldDataCriteria, model, fieldModel, modelLookupMap, headers, httpMethod);
                }
            }
        }
    }

    private List<Model> retrieveModels(PutFieldDataCriteria fieldDataCriteria, FieldModel fieldModel,
            List<Header> headers, String httpMethod)
    {
        if ( !(fieldDataCriteria.getSelectionFilter() instanceof FieldSelectionFilter) )
            throw new UnsupportedOperationException("selectionFilter=" + fieldDataCriteria.getSelectionFilter()
                    + " not supported");

        List<FieldIdentifierValue> modelSelector = ((FieldSelectionFilter) fieldDataCriteria.getSelectionFilter())
                .getFieldIdentifierValue();

        ModelQuery modelQuery = getModelQuery(fieldModel.getModel(), modelSelector, headers);
        List<Model> resultingModelList = this.modelFactory.getModels(modelQuery.getQuery(),
                fieldModel.getModelForUnMarshal(), headers);
        // List<JetEngine> engines = this.modelFactory.getModels("/engine/ENG1.23", new TypeReference<List<JetEngine>>()
        if ( HttpMethod.POST.equals(httpMethod) )
        {
            if ( resultingModelList == null || resultingModelList.size() == 0 )
            {
                createModel(fieldDataCriteria, fieldModel, headers);
                resultingModelList = this.modelFactory.getModels(modelQuery.getQuery(), fieldModel.getModel(), headers);
                if ( resultingModelList == null )
                    throw new RuntimeException(
                            "Returned from a successful createModel and then when we queried it back we didn't get it! criteria="
                                    + fieldDataCriteria);
            }
        }
        else if ( HttpMethod.PUT.equals(httpMethod) )
        {
            if ( resultingModelList == null || resultingModelList.size() == 0 )
                throw new RuntimeException("No asset was retrieved based on the current selector.");
        }

        return resultingModelList;
    }

    private void createModel(PutFieldDataCriteria fieldDataCriteria, FieldModel fieldModel, List<Header> headers)
    {
        List<Object> models = new ArrayList<Object>();

        String modelName = fieldModel.getModel();
        Data data = fieldDataCriteria.getFieldData().getData();
        if ( data.getClass().getSimpleName().equals(modelName) )
        {
            models.add(data);
            // this.modelFactory.createModel(models, headers); //POST not working
            this.modelFactory.updateModel(data, modelName, headers);
        }
        else
        {
            Model model = null;
            try
            {
                model = PaUtility.getModel(modelName);
            }
            catch (ClassNotFoundException e)
            {
                model = new Model();
            }
            if ( data instanceof OsaData )
            {
                for (FieldIdentifierValue fiv : ((FieldSelectionFilter) fieldDataCriteria.getSelectionFilter())
                        .getFieldIdentifierValue())
                {
                    FieldModel filterModel = PaUtility.getFieldModel((String) fiv.getFieldIdentifier().getId());
                    if ( filterModel.getAtrribute().equals("uri") )
                    {
                        model.setUri((String) fiv.getValue());
                        break;
                    }
                }
                // this.modelFactory.createModel(models, headers); //POST not working
                this.modelFactory.updateModel(model, modelName, headers);
                models.add(model);
            }
            else
                throw new UnsupportedOperationException("attribute=" + fieldModel.getAtrribute() + "model=" + modelName
                        + " not supported in creation of Model");

        }
    }

    private void processModel(PutFieldDataCriteria fieldDataCriteria, Model model, FieldModel fieldModel,
            Map<Integer, Model> modelLookupMap, List<Header> headers, String httpMethod)
    {
        try
        {
            Object objectContainingAttribute = model;
            if ( fieldModel.getChildModels() != null && fieldModel.getChildModels().size() > 0 )
            {
                int childModelNameIndex = 0;
                objectContainingAttribute = traverseToChild(fieldDataCriteria, model, fieldModel, childModelNameIndex,
                        modelLookupMap, headers, httpMethod);
                if ( objectContainingAttribute != null )
                    updateAttribute(fieldDataCriteria, model, fieldModel, headers, objectContainingAttribute);
            }
            else
            {
                updateAttribute(fieldDataCriteria, model, fieldModel, headers, objectContainingAttribute);
            }

        }
        catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * @param fieldDataCriteria
     * @param model
     * @param fieldModel
     * @param headers
     * @param objectContainingAttribute -
     */
    private void updateAttribute(PutFieldDataCriteria fieldDataCriteria, Model model, FieldModel fieldModel,
            List<Header> headers, Object objectContainingAttribute)
    {
        AttributeUpdaterInterface attributeUpdater = this.attributeHandlerFactory.create(fieldModel.getField());
        Data data = fieldDataCriteria.getFieldData().getData();
        if ( data instanceof OsaData )
        {
            DataEvent dataEvent = ((OsaData) data).getDataEvent();
            EngUnit engUnit = fieldDataCriteria.getFieldData().getEngUnit();
            attributeUpdater.storeOsaData(objectContainingAttribute, fieldModel, dataEvent, engUnit, headers);
            this.modelFactory.updateModel(model, fieldModel.getModelForUnMarshal(), headers);
        }
        else
            throw new UnsupportedOperationException("Only OsaData updates are supported");
    }


    /**
     * This method recursively visits objects within the retrieved Model as instructed by the rest based FieldId. Following Rest principles we can
     * easily traverse the Object Graph. Here is fairly complex example showing how we can visit Objects within the found Model but we can even jump to
     * another model following a getXxxxUri() link (requiring another query to Asset of course.  Hence this should satisfy most use-cases.
     * 
     * e.g. /asset/assetMeter/crank-frame-dischargepressure/meterExtensions/attributes/alertStatus/value
     * would find the AssetMeter object on the Asset. In this case assetMeter is a map with a key='crank-frame-dischargepressure'. The value returned is an
     * AssetMeter. On this object is not getMeterExtensions() but instead it has a getMeterExtensionsUri() which is a reference (foreign-key) to another Model
     * in PredixAsset. We look up the URI and find that it has a map called 'attributes' with a key="alertStatus". The value is an Attribute object which has a
     * setValue() method upon which we store the value.  The setValue can take either a single value or an array[].
     * 
     * 
     * @param fieldDataCriteria -
     * @param model -
     * @param fieldModel -
     * @param modelLookupMap -
     * @param headers -
     * @param httpMethod -
     * @param childModelNameIndex -
     * @return -
     * @throws IllegalAccessException -
     * @throws InvocationTargetException -
     * @throws NoSuchMethodException -
     */
    protected Object traverseToChild(PutFieldDataCriteria fieldDataCriteria, Object model, FieldModel fieldModel,
            int childModelNameIndex, Map<Integer, Model> modelLookupMap, List<Header> headers, String httpMethod)
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
                        throw new UnsupportedOperationException("Unable to get data from a Null object. field="
                                + fieldModel);
                    else if ( objectContainingAttribute instanceof Map )
                    {
                        // handle if it's a Map
                        if ( fieldModel.getChildModels().size() < childModelNameIndex + 2 )
                        {
                            // attribute is get/set in the map
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
                                    objectContainingAttribute = traverseToChild(fieldDataCriteria,
                                            objectContainingAttribute, fieldModel, childModelNameIndex + 2,
                                            modelLookupMap, headers, httpMethod);

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
                            if ( PropertyUtils.isReadable(objectContainingAttribute, "uri") )
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
                            throw new RuntimeException("uri=" + uri + " not found in " + objectContainingAttribute);
                    }
                    else if ( fieldModel.getChildModels().size() > childModelNameIndex + 1 )
                    {
                        objectContainingAttribute = traverseToChild(fieldDataCriteria,
                                objectContainingAttribute, fieldModel, childModelNameIndex + 1, modelLookupMap,
                                headers, httpMethod);
                    }
                    // else if must be a regular object
                    break;
                }
            }
        }
        else
        {
            // attribute is in a referenced Model. We'll have to get the Uri and make another query to Asset
            followReference(fieldDataCriteria, model, fieldModel, childModelName, modelLookupMap, headers, httpMethod);
            return null;
        }
        return objectContainingAttribute;
    }

    /**
     * @param fieldDataCriteria -
     * @param model -
     * @param fieldModel -
     * @param childModelPropertyName -
     * @param modelLookupMap -
     * @param headers -
     * @param httpMethod -
     * @param childModel -
     * @throws IllegalAccessException -
     * @throws InvocationTargetException -
     * @throws NoSuchMethodException -
     */
    protected void followReference(PutFieldDataCriteria fieldDataCriteria, Object model, FieldModel fieldModel,
            String childModelPropertyName, Map<Integer, Model> modelLookupMap, List<Header> headers, String httpMethod)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        String childUriProperty = PaUtility.firstCharLower(childModelPropertyName) + "Uri";
        String childUriValue = BeanUtils.getProperty(model, childUriProperty);

        String childField = fieldModel.getField();
        childField = "/" + childField.substring(childField.indexOf(childModelPropertyName));
        String childModelName = PaUtility.getModelName(childUriValue);
        childField = childField.replace(childModelPropertyName, childModelName);
        FieldModel childFieldModel = PaUtility.getFieldModel(childField);

        ModelQuery query = new ModelQuery();
        query.setModel(fieldModel.getModel());
        query.setUri(childUriValue);
        List<Model> models = this.modelFactory.getModels(query.getQuery(), childFieldModel.getModelForUnMarshal(),
                headers);
        if ( models == null || (models != null && models.size() != 1) )
        {
            int size = 0;
            if ( models != null ) size = models.size();
            // we expect exactly one model to return, if not throw error
            throw new RuntimeException("Expected to retrieve exactly 1 asset with the model selector, retrieved "
                    + size + " instead. modelselector=" + query.getQuery());
        }
        Model childModelFound = models.get(0);
        // keep the resulting model in memory
        //modelLookupMap.put(query.hashCode(), model);

        // recursive call to next model
        processModel(fieldDataCriteria, childModelFound, childFieldModel, modelLookupMap, headers, httpMethod);
    }

}
