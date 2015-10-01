package com.ge.predix.solsvc.fdh.router.util;

import org.mimosa.osacbmv3_3.DMReal;
import org.mimosa.osacbmv3_3.OsacbmDataType;
import org.mimosa.osacbmv3_3.DMBool;

import com.ge.dsp.pm.ext.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.dsp.pm.ext.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.dsp.pm.ext.entity.fielddata.FieldData;
import com.ge.dsp.pm.ext.entity.fielddata.OsaData;
import com.ge.dsp.pm.ext.entity.fieldidentifiervalue.FieldIdentifierValue;
import com.ge.dsp.pm.ext.entity.fieldselection.FieldSelection;
import com.ge.dsp.pm.ext.entity.selectionfilter.FieldSelectionFilter;
import com.ge.dsp.pm.ext.entity.solution.identifier.solutionidentifier.SolutionIdentifier;
import com.ge.dsp.pm.fielddatahandler.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataCriteria;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataRequest;



/**
 * 
 * @author predix
 */
public class TestData {
	
 
    /**
     * @param field -
     * @param fieldSource - 
     * @param expectedDataType - 
     * @param uriField -
     * @param uriFieldValue - 
     * @param startTime -
     * @param endTime 
     * @return -
     */
    @SuppressWarnings("nls")
    public static GetFieldDataRequest getFieldDataRequest(String field, String fieldSource, String expectedDataType, Object uriField, Object uriFieldValue, Object startTime, Object endTime)
    {
        GetFieldDataRequest getFieldDataRequest = new GetFieldDataRequest();
        SolutionIdentifier solutionIdentifier = new SolutionIdentifier();
        solutionIdentifier.setId(1001);
        getFieldDataRequest.setSolutionIdentifier(solutionIdentifier );
        
        FieldDataCriteria fieldDataCriteria = new FieldDataCriteria();
        
        FieldSelectionFilter fieldSelectionFilter = new FieldSelectionFilter();
        FieldSelection fieldSelection = new FieldSelection();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId(field);
        fieldIdentifier.setSource(fieldSource);
        fieldSelection.setFieldIdentifier(fieldIdentifier);
        fieldSelection.setExpectedDataType(expectedDataType);
        fieldDataCriteria.getFieldSelection().add(fieldSelection);
        fieldDataCriteria.setSelectionFilter(fieldSelectionFilter);

        //add FieldIdValue pair for assetId
        FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier assetIdFieldIdentifier = new FieldIdentifier();
        assetIdFieldIdentifier.setId(uriField);
        //assetIdFieldIdentifier.setSource(FieldSourceEnum.PREDIX_ASSET.name());
        fieldIdentifierValue.setFieldIdentifier(assetIdFieldIdentifier );
        fieldIdentifierValue.setValue(uriFieldValue);
        fieldSelectionFilter.getFieldIdentifierValue().add(fieldIdentifierValue );
        
        //add FieldIdValue pair for time        
        FieldIdentifierValue startTimefieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier startTimeFieldIdentifier = new FieldIdentifier();
        startTimeFieldIdentifier.setId("startTime");
        startTimefieldIdentifierValue.setFieldIdentifier(startTimeFieldIdentifier);
        //fieldIdentifierValue.setValue("1438906239475");
        startTimefieldIdentifierValue.setValue(startTime);
        fieldSelectionFilter.getFieldIdentifierValue().add(startTimefieldIdentifierValue);

        FieldIdentifierValue endTimefieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier endTimeFieldIdentifier = new FieldIdentifier();
        endTimeFieldIdentifier.setId("endTime");
        endTimefieldIdentifierValue.setFieldIdentifier(endTimeFieldIdentifier);
        //fieldIdentifierValue.setValue("1438906239475");
        endTimefieldIdentifierValue.setValue(endTime);
        fieldSelectionFilter.getFieldIdentifierValue().add(endTimefieldIdentifierValue);
        
        getFieldDataRequest.getFieldDataCriteria().add(fieldDataCriteria);
        return getFieldDataRequest;
    }
    
    /**
     * @return -
     */
    @SuppressWarnings("nls")
    public static PutFieldDataRequest putFieldDataRequestSetAlertStatus()
    {
        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
        
        // Solution ID
        SolutionIdentifier solutionIdentifier = new SolutionIdentifier();
        solutionIdentifier.setId(1001);
        putFieldDataRequest.setSolutionIdentifier(solutionIdentifier );
        
        // Asset to Query 
        FieldSelectionFilter selectionFilter = new FieldSelectionFilter();
        FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier assetIdFieldIdentifier = new FieldIdentifier();
        assetIdFieldIdentifier.setId("/asset/assetId");
        fieldIdentifierValue.setFieldIdentifier(assetIdFieldIdentifier );
        fieldIdentifierValue.setValue("/asset/compressor-2015");
        selectionFilter.getFieldIdentifierValue().add(fieldIdentifierValue);
        
        // Data to change 
        FieldData fieldData = new FieldData();
        com.ge.dsp.pm.ext.entity.field.Field field = new com.ge.dsp.pm.ext.entity.field.Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId("/asset/meter/meterattribute/crank-frame-dischargepressure/alertStatus");        
        fieldIdentifier.setSource("PREDIX_ASSET");
        field.setFieldIdentifier(fieldIdentifier );
        OsaData crankFrameVelocityData = new OsaData();
        DMBool crankFrameVelocity = new DMBool();
        crankFrameVelocity.setValue(true);        
        crankFrameVelocityData.setDataEvent(crankFrameVelocity);
        fieldData.setField(field );                
        fieldData.setData(crankFrameVelocityData);
        
        PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();
        fieldDataCriteria.setFieldData(fieldData);
        fieldDataCriteria.setSelectionFilter(selectionFilter );
        putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria);
        
        return putFieldDataRequest;
    }
    /**
     * @return -
     */
    @SuppressWarnings("nls")
    public static PutFieldDataRequest putFieldDataRequest()
    {
        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
        
        // Solution ID
        SolutionIdentifier solutionIdentifier = new SolutionIdentifier();
        solutionIdentifier.setId(1001);
        putFieldDataRequest.setSolutionIdentifier(solutionIdentifier );
        
        // Asset to Query 
        FieldSelectionFilter selectionFilter = new FieldSelectionFilter();
        FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier assetIdFieldIdentifier = new FieldIdentifier();
        assetIdFieldIdentifier.setId("/asset/assetId");
        fieldIdentifierValue.setFieldIdentifier(assetIdFieldIdentifier );
        fieldIdentifierValue.setValue("/asset/compressor-2015");
        selectionFilter.getFieldIdentifierValue().add(fieldIdentifierValue);
        
        // Data to change 
        FieldData fieldData = new FieldData();
        com.ge.dsp.pm.ext.entity.field.Field field = new com.ge.dsp.pm.ext.entity.field.Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId("/asset/meter/meterattribute/crank-frame-velocity/outputMaximum");        
        fieldIdentifier.setSource("PREDIX_ASSET");
        field.setFieldIdentifier(fieldIdentifier );
        OsaData crankFrameVelocityData = new OsaData();
        DMReal crankFrameVelocity = new DMReal();
        crankFrameVelocity.setValue(19.88);        
        crankFrameVelocityData.setDataEvent(crankFrameVelocity);
        fieldData.setField(field );                
        fieldData.setData(crankFrameVelocityData);
        
        PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();
        fieldDataCriteria.setFieldData(fieldData);
        fieldDataCriteria.setSelectionFilter(selectionFilter );
        putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria);
        
        return putFieldDataRequest;
    }
}
