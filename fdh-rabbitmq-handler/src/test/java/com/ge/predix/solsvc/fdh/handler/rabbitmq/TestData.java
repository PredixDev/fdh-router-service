package com.ge.predix.solsvc.fdh.handler.rabbitmq;

import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddata.PredixString;
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
        //fieldSelection.setExpectedDataType(OsacbmDataType.DM_DATA_SEQ.name());
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
    public static PutFieldDataRequest putFieldDataRequest()
    {
        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
       
        FieldData fieldData = new FieldData();
        com.ge.predix.entity.field.Field field = new com.ge.predix.entity.field.Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        
        fieldIdentifier.setSource(FieldSourceEnum.RABBITMQ_QUEUE.name());
        field.setFieldIdentifier(fieldIdentifier );
        fieldData.getField().add(field ); 
        
        String testMessage1 = "{\"messageId\": \"1453338376222\",\"body\": [{\"name\": \"Compressor-2015:CompressionRatio\",\"datapoints\": [[1453338376222,10,3],[1453338376222,10,1]],\"attributes\": {\"host\": \"server1\",\"customer\": \"Acme1\"}}]}"; // $$ //$NON-NLS-1$
        PredixString data = new PredixString();
        data.setString(testMessage1);
        fieldData.setData(data);
                       
        
        PutFieldDataCriteria putFieldDataCriteria = new PutFieldDataCriteria();
        putFieldDataCriteria.setFieldData(fieldData);
        putFieldDataRequest.getPutFieldDataCriteria().add(putFieldDataCriteria );
                
        return putFieldDataRequest;
    }
}
