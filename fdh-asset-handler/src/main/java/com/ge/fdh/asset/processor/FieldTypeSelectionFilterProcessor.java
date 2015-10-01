/*
 * Copyright (c) 2015 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */
 
package com.ge.fdh.asset.processor;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.http.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ge.dsp.pm.ext.entity.attributedef.AttributeDef;
import com.ge.dsp.pm.ext.entity.attributedef.AttributeType;
import com.ge.dsp.pm.ext.entity.attributedef.AttributeTypeEnum;
import com.ge.dsp.pm.ext.entity.field.Field;
import com.ge.dsp.pm.ext.entity.field.FieldTypeEnum;
import com.ge.dsp.pm.ext.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.dsp.pm.ext.entity.fielddata.FieldData;
import com.ge.dsp.pm.ext.entity.fielddata.Fields;
import com.ge.dsp.pm.ext.entity.model.Model;
import com.ge.dsp.pm.ext.entity.selectionfilter.FieldTypeSelectionFilter;
import com.ge.dsp.pm.fielddatahandler.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.fdh.asset.helper.PaConverter;
import com.ge.fdh.asset.validator.GetFieldDataValidator;
import com.ge.predix.solsvc.bootstrap.ams.dto.AssetMeter;
import com.ge.predix.solsvc.bootstrap.ams.dto.Attribute;
import com.ge.predix.solsvc.bootstrap.ams.dto.Classification;
import com.ge.predix.solsvc.bootstrap.ams.dto.Meter;

/**
 * This Processor for GetFieldData is used when FieldTypeSelectionFilter is passed in.  The FieldData it returns
 * is simply a list of Fields.  Each Field id/name represents a Column or Attribute in the source system.
 * 
 * In the case of Predix Asset each Field is an Attribute on all the Classifications in the system.
 * 
 * Predix Asset has Classifications which are entity definitions.  And Assets which are of type Classification.
 * 
 * @author predix -
 */
@Component
public class FieldTypeSelectionFilterProcessor extends AbstractFdhRequestProcessor
{

    //TODO base props returned off the SelectionFilter
    private static PropertyDescriptor[] meterProps      = PropertyUtils.getPropertyDescriptors(Meter.class);
    private static PropertyDescriptor[] assetMeterProps = PropertyUtils.getPropertyDescriptors(AssetMeter.class);

    @Autowired
    private GetFieldDataValidator             getFieldDataValidator;
    /**
     * @param selectionFilter -
     * @param getFieldDataResult -
     * @return -
     */
    @SuppressWarnings("nls")
    public boolean validateFieldTypeSelectionFilter(FieldTypeSelectionFilter selectionFilter)
    {
        if ( !validateFieldType(selectionFilter) )
        {
            String fieldType = null;
            if ( selectionFilter.getFieldType().size() > 0
                    && selectionFilter.getFieldType().get(0).getFieldTypeEnum() != null )
                fieldType = selectionFilter.getFieldType().get(0).getFieldTypeEnum().name();

            throw new UnsupportedOperationException("Invalid FieldType=" + fieldType);
        }
        if ( !validateSearchType(selectionFilter) )
        {
            String searchType = null;
            if ( selectionFilter.getSearchType() != null && selectionFilter.getSearchType().getSearchTypeEnum() != null )
                searchType = selectionFilter.getSearchType().getSearchTypeEnum().name();
            throw new UnsupportedOperationException("Invalid SearchType=" + searchType);
        }
        return true;

    }
    
    /**
     * @param selectionFilter -
     * @return -
     */
    public boolean validateFieldType(FieldTypeSelectionFilter selectionFilter)
    {
        try
        {
            if ( selectionFilter.getFieldType().get(0).getFieldTypeEnum() != null )
            {
                return true;
            }
        }
        catch (Throwable t)
        {
            return false;
        }
        return false;
    }

    /**
     * @param selectionFilter -
     * @return -
     */
    public boolean validateSearchType(FieldTypeSelectionFilter selectionFilter)
    {
        try
        {
            if ( selectionFilter.getSearchType().getSearchTypeEnum() != null )
            {
                return true;
            }
        }
        catch (Throwable t)
        {
            return false;
        }
        return false;
    }
    
    /**
     * @param fieldDataCriteria -
     * @param assetLookupMap -
     * @param headers -
     * @return -
     */
    public FieldData process(FieldDataCriteria fieldDataCriteria,
            List<Header> headers)
    {
        FieldData fieldData = new FieldData();
        FieldTypeSelectionFilter selectionFilter = (FieldTypeSelectionFilter) fieldDataCriteria.getSelectionFilter();
        FieldTypeEnum fieldTypeEnum = selectionFilter.getFieldType().get(0).getFieldTypeEnum();
        List<Classification> classifications = null; //this.classificationFactory.getAllClassifications(headers);

        if ( fieldTypeEnum == FieldTypeEnum.TEMPORARYFIELD )
        {
            // Fdh Asset doesn't support temporary fields
            return fieldData;
        }



        // For each classification
        // For each attribute of each classification
        // If the attribute is enumerable then it's an asset grouping field
        for (Classification classification : classifications)
        {
            Field field = null;
            field = convertClassificationToField(classification);
            ((Fields) fieldData.getData()).getField().add(field);
            Map<String, Attribute> attributeMap = classification.getAttributes();
            for (Entry<String, Attribute> attributeMapEntry : attributeMap.entrySet())
            {
                String attributeName = attributeMapEntry.getKey();
                Attribute attribute = attributeMapEntry.getValue();
                boolean isAttributeValueEnumerable = isAttributeValueEnumerable(attribute);
                if ( fieldTypeEnum == FieldTypeEnum.ASSETGROUPINGFIELD && isAttributeValueEnumerable
                        || fieldTypeEnum == FieldTypeEnum.GENERALFIELD && !isAttributeValueEnumerable )
                {
                    if ( attribute.getValue() != null )
                    {
                        field = convertClassificationAttributeToField(classification, attributeName, attribute);
                        ((Fields) fieldData.getData()).getField().add(field);
                    }
                    else
                    {
                        for (Object attributeValue : attribute.getValue())
                        {
                            if ( attributeValue instanceof String )
                            {
                                field = convertClassificationAttributeToField(classification,
                                        (String) attributeValue, attribute);
                                ((Fields) fieldData.getData()).getField().add(field);
                            }
                        }
                    }
                }
            }
        }

        if ( fieldTypeEnum == FieldTypeEnum.GENERALFIELD )
        {
            // Add the time series fields
            //addMeterFields(fieldData, headers);
        }
        return fieldData;
    }
        

    /**
     * @param attribute -
     * @return -
     */
    protected boolean isAttributeValueEnumerable(Attribute attribute)
    {
        List<Object> enumerationList = attribute.getEnumeration();
        return enumerationList != null && enumerationList.size() > 1;
    }
    
    /**
     * @param fieldId
     * @param fieldName
     * @param type
     * @param isAttributeValueEnumerable
     * @return
     */
    private Field createField(String fieldId, String fieldName, String type, boolean isAttributeValueEnumerable)
    {
        Field field = new Field();
        field.setFieldIdentifier(new FieldIdentifier());
        field.getFieldIdentifier().setId(fieldId);
        field.getFieldIdentifier().setName(fieldName);

        AttributeDef attr = new com.ge.dsp.pm.ext.entity.attributedef.AttributeDef();
        attr.setAttributeType(new AttributeType());
        attr.getAttributeType().setAttributeTypeEnum(PaConverter.convertToAttributeTypeEnum(type));
        attr.setIsEnumerable(isAttributeValueEnumerable);
        field.setAttributeDef(attr);

        // field.setOsacbmDataType(PapiConverter.convertToOsacbmDataType(type));
        return field;
    }

    /**
     * @param fieldId
     * @param fieldName
     * @param dataEventType
     * @param attributeType
     * @return
     */
    private Field getField(String fieldId, String fieldName, AttributeTypeEnum attributeType)
    {
        Field field = new Field();
        field.setFieldIdentifier(new FieldIdentifier());
        field.getFieldIdentifier().setId(fieldId);
        field.getFieldIdentifier().setName(fieldName);

        AttributeDef attr = new AttributeDef();
        attr.setAttributeType(new AttributeType());
        attr.getAttributeType().setAttributeTypeEnum(attributeType);
        attr.setIsEnumerable(false);
        field.setAttributeDef(attr);
        // field.setOsacbmDataType(dataEventType);
        return field;
    }

    /**
     * @param classification
     * @param attributeName
     * @param attribute
     * @return
     */
    private Field convertClassificationToField(Classification classification)
    {
        String fieldId = classification.getUri();
        String fieldName = "C/" + classification.getName(); //$NON-NLS-1$
        String type = "Classification"; //$NON-NLS-1$

        Field field = createField(fieldId, fieldName, type, false);

        return field;
    }

    /**
     * @param classification
     * @param attributeName
     * @param attribute
     * @return
     */
    private Field convertClassificationAttributeToField(Classification classification, String attributeName,
            Attribute attribute)
    {
        String fieldId = classification.getUri() + "/" + attributeName; //$NON-NLS-1$
        String fieldName = "CA/" + classification.getName() + "/" + attributeName; //$NON-NLS-1$ //$NON-NLS-2$
        String type = attribute.getType();
        boolean isAttributeValueEnumerable = isAttributeValueEnumerable(attribute);

        Field field = createField(fieldId, fieldName, type, isAttributeValueEnumerable);

        return field;
    }

    /**
     * @param fieldDataCriteria -
     * @param headers -
     * @return -
     */
    public List<Model> retrieveModels(FieldDataCriteria fieldDataCriteria, List<Header> headers)
    {
        // TODO Auto-generated method stub
        //since we have rearranged things this needs to be re-written
        return null;
    }


}
