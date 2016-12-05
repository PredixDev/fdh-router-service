package com.ge.predix.solsvc.fdh.handler.custom;

import org.mimosa.osacbmv3_3.DMReal;
import org.mimosa.osacbmv3_3.OsacbmDataType;

import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddata.OsaData;
import com.ge.predix.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.predix.entity.fieldidentifiervalue.FieldIdentifierValue;
import com.ge.predix.entity.fieldselection.FieldSelection;
import com.ge.predix.entity.filter.FieldFilter;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;


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
        
        FieldDataCriteria fieldDataCriteria = new FieldDataCriteria();
        
        FieldFilter fieldFilter = new FieldFilter();
        FieldSelection fieldSelection = new FieldSelection();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId("/tag/crank-frame-velocity");
        fieldIdentifier.setSource(FieldSourceEnum.PREDIX_TIMESERIES.name());
        fieldSelection.setFieldIdentifier(fieldIdentifier);
        fieldSelection.setExpectedDataType(OsacbmDataType.DM_DATA_SEQ.name());
        fieldDataCriteria.getFieldSelection().add(fieldSelection);
        fieldDataCriteria.setFilter(fieldFilter);

        //add FieldIdValue pair for assetId
        FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier assetIdFieldIdentifier = new FieldIdentifier();
        assetIdFieldIdentifier.setId("/asset/assetId");
        //assetIdFieldIdentifier.setSource(FieldSourceEnum.PREDIX_ASSET.name());
        fieldIdentifierValue.setFieldIdentifier(assetIdFieldIdentifier );
        fieldIdentifierValue.setValue("/asset/compressor-2015");
        fieldFilter.getFieldIdentifierValue().add(fieldIdentifierValue );
        
        //add FieldIdValue pair for time        
        FieldIdentifierValue startTimefieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier startTimeFieldIdentifier = new FieldIdentifier();
        startTimeFieldIdentifier.setId("startTime");
        startTimefieldIdentifierValue.setFieldIdentifier(startTimeFieldIdentifier);
        //fieldIdentifierValue.setValue("1438906239475");
        startTimefieldIdentifierValue.setValue("2015-08-01 11:00:00");
        fieldFilter.getFieldIdentifierValue().add(startTimefieldIdentifierValue);

        FieldIdentifierValue endTimefieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier endTimeFieldIdentifier = new FieldIdentifier();
        endTimeFieldIdentifier.setId("endTime");
        endTimefieldIdentifierValue.setFieldIdentifier(endTimeFieldIdentifier);
        //fieldIdentifierValue.setValue("1438906239475");
        endTimefieldIdentifierValue.setValue("2015-08-08 11:00:00");
        fieldFilter.getFieldIdentifierValue().add(endTimefieldIdentifierValue);
        
        getFieldDataRequest.getFieldDataCriteria().add(fieldDataCriteria);
        return getFieldDataRequest;
    }
    
    /**
     * @return -
     */
    @SuppressWarnings("nls")
    public static PutFieldDataRequest putFieldDataRequestWithFieldFilter()
    {
        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
        
        FieldFilter fieldFilter = new FieldFilter();
        
        // Asset to Query 
        FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier assetIdFieldIdentifier = new FieldIdentifier();
        assetIdFieldIdentifier.setId("assetId");
        fieldIdentifierValue.setFieldIdentifier(assetIdFieldIdentifier );
        fieldIdentifierValue.setValue("/asset/compressor-2015");
        
        fieldFilter.getFieldIdentifierValue().add(fieldIdentifierValue);

        // FIELD
        //==========================
        
        // Tag to change 
        FieldData fieldData = new FieldData();
        com.ge.predix.entity.field.Field field = new com.ge.predix.entity.field.Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
       // fieldIdentifier.setId("/asset/tag/tagattribute/crank-frame-dischargepressure/outputMinimum");
        fieldIdentifier.setId("/asset/tag/tagattribute/crank-frame-velocity/outputMinimum");        
        fieldIdentifier.setSource("PredixAsset"  );
        field.setFieldIdentifier(fieldIdentifier );
        
        OsaData crankFrameVelocityData = new OsaData();
        DMReal actualCrankFrameVelocity = new DMReal();
        actualCrankFrameVelocity.setValue(19.88);
        
        crankFrameVelocityData.setDataEvent(actualCrankFrameVelocity);
        fieldData.setData(crankFrameVelocityData);
        fieldData.getField().add(field );                
        
        PutFieldDataCriteria putFieldDataCriteria = new PutFieldDataCriteria();
        putFieldDataCriteria.setFieldData(fieldData);
        putFieldDataRequest.getPutFieldDataCriteria().add(putFieldDataCriteria );
        putFieldDataCriteria.setFilter(fieldFilter );
                
        return putFieldDataRequest;
    }
}
