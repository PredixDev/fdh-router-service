package com.ge.predix.solsvc.fdh.handler.custom;

import org.mimosa.osacbmv3_3.DMReal;
import org.mimosa.osacbmv3_3.OsacbmDataType;

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
     * @return -
     */
    @SuppressWarnings("nls")
    public static GetFieldDataRequest getFieldDataRequest()
    {
        GetFieldDataRequest getFieldDataRequest = new GetFieldDataRequest();
        SolutionIdentifier solutionIdentifier = new SolutionIdentifier();
        solutionIdentifier.setId(1001);
        getFieldDataRequest.setSolutionIdentifier(solutionIdentifier );
        
        FieldDataCriteria fieldDataCriteria = new FieldDataCriteria();
        
        FieldSelectionFilter fieldSelectionFilter = new FieldSelectionFilter();
        FieldSelection fieldSelection = new FieldSelection();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId("/meter/crank-frame-velocity");
        fieldIdentifier.setSource(FieldSourceEnum.PREDIX_TIMESERIES.name());
        fieldSelection.setFieldIdentifier(fieldIdentifier);
        fieldSelection.setExpectedDataType(OsacbmDataType.DM_DATA_SEQ.name());
        fieldDataCriteria.getFieldSelection().add(fieldSelection);
        fieldDataCriteria.setSelectionFilter(fieldSelectionFilter);

        //add FieldIdValue pair for assetId
        FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier assetIdFieldIdentifier = new FieldIdentifier();
        assetIdFieldIdentifier.setId("/asset/assetId");
        //assetIdFieldIdentifier.setSource(FieldSourceEnum.PREDIX_ASSET.name());
        fieldIdentifierValue.setFieldIdentifier(assetIdFieldIdentifier );
        fieldIdentifierValue.setValue("/asset/compressor-2015");
        fieldSelectionFilter.getFieldIdentifierValue().add(fieldIdentifierValue );
        
        //add FieldIdValue pair for time        
        FieldIdentifierValue startTimefieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier startTimeFieldIdentifier = new FieldIdentifier();
        startTimeFieldIdentifier.setId("startTime");
        startTimefieldIdentifierValue.setFieldIdentifier(startTimeFieldIdentifier);
        //fieldIdentifierValue.setValue("1438906239475");
        startTimefieldIdentifierValue.setValue("2015-08-01 11:00:00");
        fieldSelectionFilter.getFieldIdentifierValue().add(startTimefieldIdentifierValue);

        FieldIdentifierValue endTimefieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier endTimeFieldIdentifier = new FieldIdentifier();
        endTimeFieldIdentifier.setId("endTime");
        endTimefieldIdentifierValue.setFieldIdentifier(endTimeFieldIdentifier);
        //fieldIdentifierValue.setValue("1438906239475");
        endTimefieldIdentifierValue.setValue("2015-08-08 11:00:00");
        fieldSelectionFilter.getFieldIdentifierValue().add(endTimefieldIdentifierValue);
        
        getFieldDataRequest.getFieldDataCriteria().add(fieldDataCriteria);
        return getFieldDataRequest;
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

        /* 
        DataEvent dataEvent = new DMDataSeq();
		OsaData osaData = new OsaData();
		osaData.setDataEvent(dataEvent);
        fieldData.setData(osaData );
        

        
        
        
        FieldSelectionFilter selectionFilter = new FieldSelectionFilter();
        Asset asset = new Asset();
        org.mimosa.osacbmv3_3.Asset osaAsset = new org.mimosa.osacbmv3_3.Asset();
        osaAsset.setSerialNo("123");
        FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier assetIdFieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId("/asset/assetId");
        fieldIdentifierValue.setValue("123");
        selectionFilter.getFieldIdentifierValue().add(fieldIdentifierValue);*/
        
        FieldSelectionFilter selectionFilter = new FieldSelectionFilter();
        
        // Asset to Query 
        FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier assetIdFieldIdentifier = new FieldIdentifier();
        assetIdFieldIdentifier.setId("assetId");
        fieldIdentifierValue.setFieldIdentifier(assetIdFieldIdentifier );
        fieldIdentifierValue.setValue("/asset/compressor-2015");
        
        selectionFilter.getFieldIdentifierValue().add(fieldIdentifierValue);

        // FIELD
        //==========================
        
        // Meter to change 
        FieldData fieldData = new FieldData();
        com.ge.dsp.pm.ext.entity.field.Field field = new com.ge.dsp.pm.ext.entity.field.Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
       // fieldIdentifier.setId("/asset/meter/meterattribute/crank-frame-dischargepressure/outputMinimum");
        fieldIdentifier.setId("/asset/meter/meterattribute/crank-frame-velocity/outputMinimum");        
        fieldIdentifier.setSource("PredixAsset");
        field.setFieldIdentifier(fieldIdentifier );
        
        OsaData crankFrameVelocityData = new OsaData();
        DMReal actualCrankFrameVelocity = new DMReal();
        actualCrankFrameVelocity.setValue(19.88);
        
        crankFrameVelocityData.setDataEvent(actualCrankFrameVelocity);
        fieldData.setData(crankFrameVelocityData);
        fieldData.setField(field );                
        
        PutFieldDataCriteria putFieldDataCriteria = new PutFieldDataCriteria();
        putFieldDataCriteria.setFieldData(fieldData);
        putFieldDataRequest.getPutFieldDataCriteria().add(putFieldDataCriteria );
        putFieldDataCriteria.setSelectionFilter(selectionFilter );
                
        return putFieldDataRequest;
    }
}
