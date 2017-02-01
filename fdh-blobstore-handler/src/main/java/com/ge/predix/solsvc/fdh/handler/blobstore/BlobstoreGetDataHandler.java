package com.ge.predix.solsvc.fdh.handler.blobstore;

import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ge.predix.entity.datafile.DataFile;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.predix.entity.fieldselection.FieldSelection;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.solsvc.blobstore.bootstrap.api.BlobstoreClient;
import com.ge.predix.solsvc.fdh.handler.GetDataHandler;

/**
 * This could be exposed by Rest and called directly. But for now it's not
 * registering with CXF. If a Spring Bean MicroComponent called by the Adh
 * Router
 * 
 * @author predix
 */
@Component
@SuppressWarnings("nls")
@ImportResource({ "classpath*:META-INF/spring/fdh-blobstore-handler-scan-context.xml" })
@Profile("blobstore")
public class BlobstoreGetDataHandler implements GetDataHandler {
	private static final Logger log = LoggerFactory.getLogger(BlobstoreGetDataHandler.class);

	@Autowired
	private BlobstoreClient blobstoreClient;

	/**
	 * 
	 */
	public BlobstoreGetDataHandler() {
		super();
	}

	/*******
	 * This section is for GetFieldData API
	 *******/
	@Override
	public GetFieldDataResult getData(GetFieldDataRequest request, Map<Integer, Object> modelLookupMap,
			List<Header> headers) {
		validateRequest(request);

		GetFieldDataResult result = new GetFieldDataResult();
		for (FieldDataCriteria criteria : request.getFieldDataCriteria()) {
			validateCriteria(criteria);
			for (FieldSelection fieldSelection : criteria.getFieldSelection()) {
				FieldIdentifier fieldIdentifier = fieldSelection.getFieldIdentifier();
				String fileName = fieldIdentifier.getName();
				try {
					DataFile file = blobstoreClient.getBlob(fileName);
					FieldData fieldData = new FieldData();
					fieldData.setData(file);
					result.getFieldData().add(fieldData);
				} catch (Exception e) {
					log.error("exception when retrieving object from blobstore : " + fileName, e);
					result.getErrorEvent().add(
							"Exception when retrieving object from blobstore : " + fileName + " - " + e.getMessage());
				}
			}
		}
		return result;
	}

	/**
	 * @param criteria
	 */
	private void validateCriteria(FieldDataCriteria criteria) {
		//
	}

	private void validateRequest(GetFieldDataRequest request) {
		if (request.getFieldDataCriteria() == null)
			throw new UnsupportedOperationException("No FieldDataCriteria");
		if (request.getFieldDataCriteria().size() == 0)
			throw new UnsupportedOperationException("No FieldDataCriteria array items");
		for (FieldDataCriteria criteria : request.getFieldDataCriteria()) {
			validateCriteria(criteria);
			for (FieldSelection fieldSelection : criteria.getFieldSelection()) {
				FieldIdentifier fieldIdentifier = fieldSelection.getFieldIdentifier();
				if (!FieldSourceEnum.PREDIX_BLOBSTORE.equals(fieldIdentifier.getSource())) {
					throw new UnsupportedOperationException("Invalid Source provided : " + fieldIdentifier.getSource());
				}
			}
		}
	}
}
