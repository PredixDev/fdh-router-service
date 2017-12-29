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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.mimosa.osacbmv3_3.DataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.ge.predix.entity.asset.AssetList;
import com.ge.predix.entity.assetfilter.AssetFilter;
import com.ge.predix.entity.datafile.DataFile;
import com.ge.predix.entity.engunit.EngUnit;
import com.ge.predix.entity.field.Field;
import com.ge.predix.entity.fielddata.Data;
import com.ge.predix.entity.fielddata.OsaData;
import com.ge.predix.entity.fielddata.PredixString;
import com.ge.predix.entity.metadata.MetaData;
import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.model.ModelList;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.entity.util.map.AttributeMap;
import com.ge.predix.entity.util.map.DataMap;
import com.ge.predix.entity.util.map.Entry;
import com.ge.predix.solsvc.bootstrap.ams.factories.LinkedHashMapModel;
import com.ge.predix.solsvc.bootstrap.ams.factories.AssetClient;
import com.ge.predix.solsvc.bootstrap.ams.factories.AssetClientImpl;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.handler.PutDataHandler;
import com.ge.predix.solsvc.fdh.handler.asset.common.AssetQueryBuilder;
import com.ge.predix.solsvc.fdh.handler.asset.common.FieldModel;
import com.ge.predix.solsvc.fdh.handler.asset.data.AttributeHandlerFactory;
import com.ge.predix.solsvc.fdh.handler.asset.data.updater.AttributeUpdaterInterface;
import com.ge.predix.solsvc.fdh.handler.asset.helper.PaUtility;
import com.ge.predix.solsvc.fdh.handler.asset.validator.PutFieldDataCriteriaValidator;
import com.ge.predix.solsvc.fdh.handler.asset.validator.PutFieldDataRequestValidator;

/**
 * PutFieldDataProcessor processes PutFieldDataRequest - Retrieves the asset per
 * the asset selector in the request - For each field data in the list, convert
 * UoM as needed and update the asset with given data
 * 
 * @author 212397779
 */
@SuppressWarnings("nls")
@Component(value = "assetPutFieldDataHandler")
@ImportResource(
{
        "classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml",
        "classpath*:META-INF/spring/fdh-asset-handler-scan-context.xml"
})
public class AssetPutDataHandlerImpl
        implements PutDataHandler
{
    private static final Logger           log    = LoggerFactory.getLogger(AssetPutDataHandlerImpl.class);

    @Autowired
    private PutFieldDataRequestValidator  putFieldDataRequestValidator;

    @Autowired
    private PutFieldDataCriteriaValidator putFieldDataCriteriaValidator;

    @Autowired
	@Qualifier("AssetClient")
	private AssetClientImpl assetClient;

    @Autowired
    private JsonMapper                    jsonMapper;

    @Autowired
    private AttributeHandlerFactory       attributeHandlerFactory;

    @Autowired
    private AssetPutDataFileExecutor      assetPutDataFileExecutor;

    /*
     * (non-Javadoc)
     * @see
     * com.ge.fdh.asset.processor.IPutFieldDataProcessor#processRequest(com.ge.
     * predix.entity.putfielddata.PutFieldDataRequest)
     */
    @Override
    public PutFieldDataResult putData(PutFieldDataRequest request, Map<Integer, Object> modelLookupMap,
            List<Header> headers, String httpMethod)
    {
        try
        {
            PutFieldDataResult putFieldDataResult = new PutFieldDataResult();

            if ( !this.putFieldDataRequestValidator.validate(request, putFieldDataResult) ) return putFieldDataResult;

            this.assetClient.setZoneIdInHeaders(headers);

            putFieldDataProcessor(request, putFieldDataResult, modelLookupMap, headers, httpMethod);
            return putFieldDataResult;
        }
        catch (Throwable e)
        {
            String msg = "unable to process request errorMsg=" + e.getMessage() + " request.correlationId="
                    + request.getCorrelationId() + " request = " + request;
            log.error(msg, e);
            RuntimeException exception = new RuntimeException(msg, e);
            throw exception;
        }
    }

    /**
     * Create the model in Asset if no filter. If there is a filter, find the
     * model in Asset and then update the contents and save it back.
     * 
     * @param putFieldDataRequest
     *            -
     * @param putFieldDataResult
     *            -
     * @param modelLookupMap
     *            -
     * @param headers
     *            -
     * @param httpMethod
     *            -
     * @throws IOException -
     * @throws JsonMappingException -
     * @throws JsonParseException -
     */
    protected void putFieldDataProcessor(PutFieldDataRequest putFieldDataRequest, PutFieldDataResult putFieldDataResult,
            Map<Integer, Object> modelLookupMap, List<Header> headers, String httpMethod)
            throws JsonParseException, JsonMappingException, IOException
    {
        List<PutFieldDataCriteria> fieldCriteriaList = putFieldDataRequest.getPutFieldDataCriteria();
        for (PutFieldDataCriteria fieldDataCriteria : fieldCriteriaList)
        {
            if ( !this.putFieldDataCriteriaValidator.validate(fieldDataCriteria, putFieldDataResult) ) return;

            for (Field field : fieldDataCriteria.getFieldData().getField())
            {
                if ( fieldDataCriteria.getFilter() == null )
                {
                    // no filter, let's just post the whole asset
                    Data data = fieldDataCriteria.getFieldData().getData();
                    // handle meta-data request first

                    if ( data instanceof DataMap )
                    {
                        // recommended structure - most flexible - a List of
                        // HashMaps, just like Jackson creates, wrapped in
                        // somethng that extends Data.
                        String jsonString = this.jsonMapper.toJson(((DataMap) data).getMap());
                        this.assetClient.createModelFromJson(jsonString, headers);

                    }
                    else if ( data instanceof Model )
                    {
                        // a single Model - uri and attribute map
                        ArrayList<Model> models = new ArrayList<>();
                        models.add((Model) data);
                        this.assetClient.createModel(models, headers);
                    }
                    else if ( data instanceof ModelList )
                    {
                        // uris and attribute maps
                        this.assetClient.createModel(((ModelList) data).getModel(), headers);
                    }
                    else if ( data instanceof AssetList )
                    {
                        // reference app asset model
                        this.assetClient.createModel(((AssetList) data).getAsset(), headers);
                    }
                    else if ( data instanceof PredixString )
                    {
                        // last resort - any old json string, but quotes are
                        // escaped, must be an
                        // array, each object needs a uri
                        String jsonString = ((PredixString) data).getString();
                        this.assetClient.createModelFromJson(jsonString, headers);
                    }
                    else if ( data instanceof DataFile )
                    {
                        // process file upload
                        this.assetPutDataFileExecutor.processDataFile(headers, Thread.currentThread().getName(),
                                UUID.randomUUID(), fieldDataCriteria, putFieldDataResult, data);
                    }
                    else if ( data instanceof MetaData )
                    {
                        processMetaDataRequest(data, putFieldDataResult, modelLookupMap, headers);
                    }
                    else
                    {
                        throw new UnsupportedOperationException("data of type=" + data + " not supported");
                    }
                }
                else
                {
                    // retrieve the entity and update something inside it
                    String fieldId = (String) field.getFieldIdentifier().getId();
                    FieldModel fieldModel = PaUtility.getFieldModel(fieldId);
                    List<Object> models = retrieveModels(fieldDataCriteria, fieldModel, headers, httpMethod);
                    if ( models != null )
                    {
                        for (Object model : models)
                        {
                            processModel(fieldDataCriteria, model, fieldModel, modelLookupMap, headers, httpMethod);
                        }
                    }
                }
            }

        }
    }

    /**
     * -
     * 
     * @param headers
     * @param modelLookupMap
     * @param putFieldDataResult
     * @param data
     * @param modelLookupMap
     * @return
     */
    private MetaData processMetaDataRequest(Data metaData, PutFieldDataResult putFieldDataResult,
            Map<Integer, Object> modelLookupMap, List<Header> headers)
    {

        MetaData metaDataModel = null;

        if ( metaData instanceof MetaData )
        {
            ArrayList<MetaData> metadatas = new ArrayList<>();
            metaDataModel = (MetaData) metaData;
            if ( StringUtils.isEmpty(metaDataModel.getTimestamp()) )
            {
                metaDataModel.setTimestamp(Long.toString(System.currentTimeMillis()));
            }
            metadatas.add(metaDataModel);
            // String jsonString = this.jsonMapper.toJson(metadata);
            this.assetClient.createModel(metadatas, headers);
            Entry entry = new Entry();
            entry.setKey("metadata_uri");
            entry.setValue(((MetaData) metaData).getUri());
            List<Entry> entryList = new ArrayList<Entry>();
            entryList.add(entry);
            if ( putFieldDataResult.getExternalAttributeMap() == null )
            {
                AttributeMap attributeMap = new AttributeMap();
                putFieldDataResult.setExternalAttributeMap(attributeMap);
            }
            // meta-data uri: updated here on the putFieldDataResultrequest

            putFieldDataResult.getExternalAttributeMap().setEntry(entryList);

        }

        return metaDataModel;

    }

    private List<Object> retrieveModels(PutFieldDataCriteria fieldDataCriteria, FieldModel fieldModel,
            List<Header> headers, String httpMethod)
    {
        if ( !(fieldDataCriteria.getFilter() instanceof AssetFilter) )
            throw new UnsupportedOperationException("filter=" + fieldDataCriteria.getFilter() + " not supported");

        AssetFilter assetFilter = (AssetFilter) fieldDataCriteria.getFilter();
        AssetQueryBuilder assetQueryBuilder = new AssetQueryBuilder();
        assetQueryBuilder.setUri(assetFilter.getUri());

        List<Object> resultingModelList = this.assetClient.getModels(assetQueryBuilder.build(),
                fieldModel.getModelForUnMarshal(), headers);

        if ( HttpPost.METHOD_NAME.equals(httpMethod) )
        {
            if ( resultingModelList == null || resultingModelList.size() == 0 )
            {
                createModel(fieldDataCriteria, fieldModel, headers);
                resultingModelList = this.assetClient.getModels(assetQueryBuilder.build(), fieldModel.getModel(),
                        headers);
                if ( resultingModelList == null ) throw new RuntimeException(
                        "Returned from a successful createModel and then when we queried it back we didn't get it! criteria="
                                + fieldDataCriteria);
            }
        }
        else if ( HttpPut.METHOD_NAME.equals(httpMethod) )
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
            this.assetClient.updateModel(data, modelName, headers);
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
                AssetFilter assetFilter = (AssetFilter) fieldDataCriteria.getFilter();
                model.setUri(assetFilter.getUri());
                this.assetClient.updateModel(model, modelName, headers);
                models.add(model);
            }
            else if ( data instanceof PredixString )
            {
                AssetFilter assetFilter = (AssetFilter) fieldDataCriteria.getFilter();
                model.setUri(assetFilter.getUri());
                String dataAsString = ((PredixString) data).getString();
                this.assetClient.updateModel(dataAsString, modelName, headers);
                models.add(dataAsString);
            }
            else
                throw new UnsupportedOperationException("attribute=" + fieldModel.getAtrribute() + "model=" + modelName
                        + " not supported in creation of Model");

        }
    }

    private void processModel(PutFieldDataCriteria fieldDataCriteria, Object model, FieldModel fieldModel,
            Map<Integer, Object> modelLookupMap, List<Header> headers, String httpMethod)
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
     * @param objectContainingAttribute
     *            -
     */
    private void updateAttribute(PutFieldDataCriteria fieldDataCriteria, Object model, FieldModel fieldModel,
            List<Header> headers, Object objectContainingAttribute)
    {
        AttributeUpdaterInterface attributeUpdater = this.attributeHandlerFactory.create(fieldModel.getField());
        Data data = fieldDataCriteria.getFieldData().getData();
        if ( data instanceof OsaData )
        {
            DataEvent dataEvent = ((OsaData) data).getDataEvent();
            EngUnit engUnit = fieldDataCriteria.getFieldData().getEngUnit();
            Object objectContainingAttribute2 = objectContainingAttribute;
            if ( objectContainingAttribute2 instanceof LinkedHashMapModel )
                objectContainingAttribute2 = ((LinkedHashMapModel) model).getMap();
            attributeUpdater.storeOsaData(objectContainingAttribute2, fieldModel, dataEvent, engUnit, headers);
            if ( model instanceof LinkedHashMapModel )
            {
                LinkedHashMap<String, Object> map = ((LinkedHashMapModel) model).getMap();
                this.assetClient.updateModel(map, fieldModel.getModelForUnMarshal(), headers);
            }
            else
                this.assetClient.updateModel(model, fieldModel.getModelForUnMarshal(), headers);
        }
        else if ( data instanceof PredixString )
        {
            // do nothing for now
        }
        else
            throw new UnsupportedOperationException("Only OsaData updates are supported");
    }

    /**
     * This method recursively visits objects within the retrieved Model as
     * instructed by the rest based FieldId. Following Rest principles we can
     * easily traverse the Object Graph. Here is fairly complex example showing
     * how we can visit Objects within the found Model but we can even jump to
     * another model following a getXxxxUri() link (requiring another query to
     * Asset of course. Hence this should satisfy most use-cases.
     * 
     * e.g.
     * /asset/assetTag/crank-frame-dischargepressure/tagExtensions/attributes/
     * alertStatus/value would find the AssetTag object on the Asset. In this
     * case assetTag is a map with a key='crank-frame-dischargepressure'. The
     * value returned is an AssetTag. On this object is not getTagExtensions()
     * but instead it has a getTagExtensionsUri() which is a reference
     * (foreign-key) to another Model in PredixAsset. We look up the URI and
     * find that it has a map called 'attributes' with a key="alertStatus". The
     * value is an Attribute object which has a setValue() method upon which we
     * store the value. The setValue can take either a single value or an
     * array[].
     * 
     * 
     * @param fieldDataCriteria
     *            -
     * @param model
     *            -
     * @param fieldModel
     *            -
     * @param modelLookupMap
     *            -
     * @param headers
     *            -
     * @param httpMethod
     *            -
     * @param childModelNameIndex
     *            -
     * @return -
     * @throws IllegalAccessException
     *             -
     * @throws InvocationTargetException
     *             -
     * @throws NoSuchMethodException
     *             -
     */
    protected Object traverseToChild(PutFieldDataCriteria fieldDataCriteria, Object model, FieldModel fieldModel,
            int childModelNameIndex, Map<Integer, Object> modelLookupMap, List<Header> headers, String httpMethod)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        Object objectContainingAttribute = model;
        // attribute is in an inner object
        String childModelName = fieldModel.getChildModels().get(childModelNameIndex);
        if ( childModelName.toUpperCase().endsWith("URI") )
        {
            // attribute is in a referenced Model. We'll have to get the Uri and make another query to Asset
            followReference(fieldDataCriteria, model, fieldModel, childModelName, modelLookupMap, headers, httpMethod);
            return null;
        }
        else if ( PropertyUtils.isReadable(model, childModelName) )
        {
            // loop across the instance vars till one matches
            for (java.lang.reflect.Field field : model.getClass().getDeclaredFields())
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
                            // attribute is get/set in the map
                        }
                        else
                        {
                            // must need to traverse to the map value (in other
                            // words the attribute is in an interior object)
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
                                    // attribute not here, check if we need to
                                    // follow a reference and to another query
                                    // to Asset
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
                        objectContainingAttribute = traverseToChild(fieldDataCriteria, objectContainingAttribute,
                                fieldModel, childModelNameIndex + 1, modelLookupMap, headers, httpMethod);
                    }
                    // else if must be a regular object
                    break;
                }
            }
        }
        else
        {
            // attribute is in a referenced Model. We'll have to get the Uri and
            // make another query to Asset
            followReference(fieldDataCriteria, model, fieldModel, childModelName, modelLookupMap, headers, httpMethod);
            return null;
        }
        return objectContainingAttribute;
    }

    /**
     * @param fieldDataCriteria
     *            -
     * @param model
     *            -
     * @param fieldModel
     *            -
     * @param childModelPropertyName
     *            -
     * @param modelLookupMap
     *            -
     * @param headers
     *            -
     * @param httpMethod
     *            -
     * @throws IllegalAccessException
     *             -
     * @throws InvocationTargetException
     *             -
     * @throws NoSuchMethodException
     *             -
     */
    protected void followReference(PutFieldDataCriteria fieldDataCriteria, Object model, FieldModel fieldModel,
            String childModelPropertyName, Map<Integer, Object> modelLookupMap, List<Header> headers, String httpMethod)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        String childUriProperty = PaUtility.firstCharLower(childModelPropertyName);
        if ( !childUriProperty.toUpperCase().endsWith("URI") ) childUriProperty += "Uri";
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
        // modelLookupMap.put(query.hashCode(), model);

        // recursive call to next model
        processModel(fieldDataCriteria, childModelFound, childFieldModel, modelLookupMap, headers, httpMethod);
    }

}
