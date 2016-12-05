package com.ge.predix.solsvc.fdh.handler.timeseries;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.Header;
import org.mimosa.osacbmv3_3.DMDataSeq;
import org.mimosa.osacbmv3_3.DataEvent;
import org.mimosa.osacbmv3_3.OsacbmDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ge.predix.entity.asset.Asset;
import com.ge.predix.entity.asset.AssetTag;
import com.ge.predix.entity.assetfilter.AssetFilter;
import com.ge.predix.entity.engunit.EngUnit;
import com.ge.predix.entity.field.Field;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.fielddata.OsaData;
import com.ge.predix.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.predix.entity.fieldselection.FieldSelection;
import com.ge.predix.entity.filter.Filter;
import com.ge.predix.entity.getfielddata.GetFieldDataRequest;
import com.ge.predix.entity.getfielddata.GetFieldDataResult;
import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.entity.timeseriesfilter.AssetAwareTimeseriesFilter;
import com.ge.predix.entity.timeseriesfilter.TimeseriesFilter;
import com.ge.predix.entity.util.map.AttributeMap;
import com.ge.predix.solsvc.bootstrap.ams.common.AssetConfig;
import com.ge.predix.solsvc.bootstrap.ams.dto.Tag;
import com.ge.predix.solsvc.bootstrap.ams.factories.AssetFactory;
import com.ge.predix.solsvc.bootstrap.ams.factories.TagFactory;
import com.ge.predix.solsvc.fdh.handler.GetDataHandler;
import com.ge.predix.solsvc.restclient.impl.RestClient;
import com.ge.predix.solsvc.timeseries.bootstrap.config.DefaultTimeseriesConfig;
import com.ge.predix.solsvc.timeseries.bootstrap.factories.TimeseriesFactory;

/**
 * This could be exposed by Rest and called directly. But for now it's not
 * registering with CXF. If a Spring Bean MicroComponent called by the Adh
 * Router
 * 
 * @author predix
 */
@Component
@SuppressWarnings("nls")
@ImportResource({ 
	"classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
	"classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml",
		"classpath*:META-INF/spring/predix-websocket-client-scan-context.xml",
		"classpath*:META-INF/spring/fdh-timeseries-handler-scan-context.xml" })
@Profile("timeseries")
public class TimeseriesGetDataHandler implements GetDataHandler {
	private static final Logger log = LoggerFactory.getLogger(TimeseriesGetDataHandler.class);

	// @Autowired
	// private AdaptingHandler<AssetProperty, GetFieldDataResult,
	// GetFieldDataRequest> adaptingHandler;
	// @Autowired
	// private AdapterNameFactory adapterNameFactory;

	@Autowired
	private TimeseriesFactory timeseriesFactory;
	@Autowired
	private DefaultTimeseriesConfig timeseriesConfig;
	@Autowired
	private AssetConfig assetRestConfig;
	@Autowired
	private RestClient restClient;
	@Autowired
	private AssetFactory assetFactory;
	@Autowired
	private TagFactory tagFactory;

	/**
	 * 
	 */
	public TimeseriesGetDataHandler() {
		super();
	}

	/*******
	 * This section is for GetFieldData API
	 *******/

	@Override
	public GetFieldDataResult getData(GetFieldDataRequest request, Map<Integer, Model> modelLookupMap,
			List<Header> headers) {
		validateRequest(request);

		GetFieldDataResult result = new GetFieldDataResult();
		for (FieldDataCriteria criteria : request.getFieldDataCriteria()) {
			validateCriteria(criteria);
			// TODO convert to generic Model attribute lookup
			this.restClient.addZoneToHeaders(headers, this.assetRestConfig.getZoneId());
			Asset asset = lookupAsset(criteria, headers);
			this.restClient.addZoneToHeaders(headers, this.timeseriesConfig.getZoneId());

			if (asset != null) {
				AssetTag assetTag = null;
				String fieldUri = (String) criteria.getFieldSelection().get(0).getFieldIdentifier().getId();
				if (fieldUri.startsWith("/asset/assetTag/"))
					fieldUri = fieldUri.substring(16);
				if (fieldUri.startsWith("tag/"))
					fieldUri = fieldUri.substring(4);
				if (fieldUri.startsWith("/tag/"))
					fieldUri = fieldUri.substring(5);
				if (fieldUri.startsWith("classification/tag/"))
					fieldUri = fieldUri.substring(19);
				if (asset.getAssetTag() != null) {
					@SuppressWarnings("unchecked")
					Map<String, AssetTag> javaMap = asset.getAssetTag();
					for (Entry<String, AssetTag> entry : javaMap.entrySet()) {
						if (entry.getKey().equals(fieldUri)) {
							assetTag = entry.getValue();
							break;
						}

					}
				}
				if (assetTag != null) {
					String sourceTag = assetTag.getSourceTagId();
					if (sourceTag != null) {
						List<Double> dataPoints = getData(fieldUri, sourceTag, criteria,
								request.getExternalAttributeMap(), headers);
						adaptData(result, criteria, dataPoints, assetTag);
					}
				}
			} else {
				String fieldUri = (String) criteria.getFieldSelection().get(0).getFieldIdentifier().getId();
				List<Double> dataPoints = getData(fieldUri, null, criteria, request.getExternalAttributeMap(),
						headers);
				adaptData(result, criteria, dataPoints, null);
			}

			// ModelField modelField = (ModelField)
			// getMapEntry(request.getExternalAttributeMap(), "modelField");
		}
		return result;
	}

	@SuppressWarnings("unused")
	private XMLGregorianCalendar getXMLDate(String sourceDateTime) {
		try {
			DatatypeFactory f = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date dateTime = df.parse(sourceDateTime);
			f = DatatypeFactory.newInstance();
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTimeZone(TimeZone.getTimeZone("GMT"));
			cal.setTime(dateTime);
			XMLGregorianCalendar xmLGregorianCalendar = f.newXMLGregorianCalendar(cal);
			return xmLGregorianCalendar;
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException("convert to runtime exception", e);
		} catch (ParseException e) {
			throw new RuntimeException("convert to runtime exception", e);
		}
	}

	/**
	 * @param criteria
	 */
	private void validateCriteria(FieldDataCriteria criteria) {
		//
	}

	/**
	 * @param result
	 * @param criteria
	 * @param dataPoints
	 */
	private void adaptData(GetFieldDataResult result, FieldDataCriteria criteria, List<Double> dataPoints,
			AssetTag assetTag) {
		DataEvent dataEvent = null;
		FieldSelection projection = criteria.getFieldSelection().get(0);
		if (projection.getExpectedDataType() == null
				|| projection.getExpectedDataType().equals(OsacbmDataType.DM_DATA_SEQ.value()))
			dataEvent = new DMDataSeq();
		else
			throw new UnsupportedOperationException(
					"DataEvent type = " + projection.getExpectedDataType() + " not supported");

		if (projection.getResultId() != null && NumberUtils.isNumber(projection.getResultId()))
			dataEvent.setId(Long.parseLong(projection.getResultId()));
		if (dataPoints != null) {
			int i = 0;
			// dataPoints list contains the following
			// Timestamp as the first value = 1.433333E
			// Value as double = 10.0 (datapoint)
			// Quality = 3..
			// DEFECT: dataPoints must be array of array or list of lists, which
			// is not right now. TODO
			for (Double dataPoint : dataPoints) {
				if (i++ == 0) {
					// ((DMDataSeq) dataEvent).setXAxisStart(new
					// Double(dataPoint.getTimestamp()));
					((DMDataSeq) dataEvent).setXAxisStart(dataPoint);
					((DMDataSeq) dataEvent).getXAxisDeltas().add(0d);
					// if (dataPoint != null) {
					// // ((DMDataSeq) dataEvent).getValues().add(new
					// // Double(dataPoint.getValue().toString()));
					// //((DMDataSeq) dataEvent).getValues().add(dataPoint);
					// prevTimestamp = dataPoint.doubleValue();
					// }
				} else if (i == 2) {
					// ((DMDataSeq) dataEvent).getXAxisDeltas().add(new
					// Double(dataPoint.getTimestamp() - prevTimestamp));
					// ((DMDataSeq) dataEvent).getXAxisDeltas().add(dataPoint -
					// prevTimestamp);
					if (dataPoint != null)
						((DMDataSeq) dataEvent).getValues().add(dataPoint);
				}
			}
		}
		FieldData fieldData = new FieldData();
		fieldData.setResultId(projection.getResultId());
		OsaData osaData = new OsaData();
		osaData.setDataEvent(dataEvent);
		fieldData.setData(osaData);
		if (assetTag != null) {
			Tag tag = lookupTag(assetTag);
			if (tag != null) {
				EngUnit engUnit = new EngUnit();
				engUnit.setName(tag.getUom());
				fieldData.setEngUnit(engUnit);
			}
		}
		Field field = new Field();
		field.setFieldIdentifier(criteria.getFieldSelection().get(0).getFieldIdentifier());
		fieldData.getField().add(field);

		result.getFieldData().add(fieldData);
	}

	/**
	 * @param criteria
	 * @return
	 */
	private Asset lookupAsset(FieldDataCriteria criteria, List<Header> headers) {
		this.restClient.addZoneToHeaders(headers, this.assetRestConfig.getZoneId());

		Filter filter = criteria.getFilter();
		if (filter instanceof AssetAwareTimeseriesFilter) {
			AssetFilter assetFilterValue = ((AssetAwareTimeseriesFilter) filter).getAssetFilter();
			String assetUri = assetFilterValue.getFilterString();
			if (assetUri.startsWith("/asset/"))
				assetUri = assetUri.substring(7);
			Asset asset = this.assetFactory.getAsset(assetUri, headers);
			return asset;

		} else if (filter instanceof TimeseriesFilter) {
			return null;
		} else
			throw new UnsupportedOperationException("Unable to query for asset, filter=" + filter);
	}

	/**
	 * @param criteria
	 * @return
	 */
	private Tag lookupTag(AssetTag assetTag) {
		List<Header> headers = this.restClient.getSecureTokenForClientId();
		this.restClient.addZoneToHeaders(headers, this.assetRestConfig.getZoneId());
		String tagUri = assetTag.getTagUri();
		if (!StringUtils.isEmpty(tagUri)) {
			if (tagUri.startsWith("/tag/"))
				tagUri = tagUri.substring(7);
			Tag tag = this.tagFactory.getTag(tagUri, headers);
			return tag;

		}
		return null;
	}

	/**
	 * @param sourceTag
	 * @param criteria
	 * @param endTime
	 * @param map
	 * @param headers
	 * @return
	 * 
	 */
	private List<Double> getData(String assetTagKey, String sourceTag, FieldDataCriteria criteria,
			AttributeMap map, List<Header> headers) {
		if (criteria.getFilter() != null && (criteria.getFilter() instanceof AssetAwareTimeseriesFilter
				|| criteria.getFilter() instanceof TimeseriesFilter)) {
			return getDataFromFilter(assetTagKey, sourceTag, criteria.getFilter(), headers);
		}
		throw new UnsupportedOperationException("filter type=" + criteria.getFilter() + " not supported");
	}

	/**
	 * @param sourceTag
	 * @param filter
	 * @param headers
	 * @param tsBuilder
	 * @return
	 * @throws DatatypeConfigurationException
	 * @throws IOException
	 */

	/**
	 * @param assetTagKey
	 */
	private List<Double> getDataFromFilter(String assetTagKey, String sourceTag, Filter filter,
			List<Header> headers) {
		try {
			DatapointsQuery query = null;
			TimeseriesFilter tsFilter = null;

			if (filter instanceof AssetAwareTimeseriesFilter) {
				AssetAwareTimeseriesFilter assetTsFilter = (AssetAwareTimeseriesFilter) filter;
				tsFilter = assetTsFilter.getTimeseriesFilter();
			} else if (filter instanceof TimeseriesFilter) {
				tsFilter = (TimeseriesFilter) filter;
			}

			query = tsFilter.getDatapointsQuery();

			List<Double> dataPoints = doQuery(sourceTag, query, headers);

			return dataPoints;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param sourceTag
	 * @param tsBuilder
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	private List<Double> doQuery(String sourceTag, DatapointsQuery datapointsQuery, List<Header> headers)
			throws IOException {
		// make a call to get the datapoints
		DatapointsResponse response = this.timeseriesFactory.queryForDatapoints(datapointsQuery, headers);
		List<Object> datapoints = null;
		if (response == null || CollectionUtils.isEmpty(response.getTags())
				|| CollectionUtils.isEmpty(response.getTags().get(0).getResults())
				|| CollectionUtils.isEmpty(response.getTags().get(0).getResults().get(0).getValues())) {
			log.info("no datapoints" + " for " + sourceTag + " for query=" + datapointsQuery.toString());
			// if above response was null try by setting the start time to 24
			// hours ago
			datapointsQuery.setStart("24h-ago");
			datapointsQuery.setEnd(null);
			response = this.timeseriesFactory.queryForDatapoints(datapointsQuery, headers);
			if (response == null || CollectionUtils.isEmpty(response.getTags())
					|| CollectionUtils.isEmpty(response.getTags().get(0).getResults())
					|| CollectionUtils.isEmpty(response.getTags().get(0).getResults().get(0).getValues())) {
				// if still no datapoints are available return null
				return null;
			}
		}
		datapoints = (List<Object>) response.getTags().get(0).getResults().get(0).getValues().get(0);
		log.info("datapoints size=" + datapoints.size() + " for " + sourceTag + " for query="
				+ datapointsQuery.toString());

		// convert the List<Object> to List<Double> to suit the return type
		List<Double> dpList = null;
		if (!CollectionUtils.isEmpty(datapoints)) {
			dpList = new ArrayList<Double>();
			for (Object obj : datapoints) {
				if (obj instanceof Long) {
					dpList.add(((Long) obj).doubleValue());
				} else if (obj instanceof Integer) {
					dpList.add(((Integer) obj).doubleValue());
				} else {
					dpList.add((Double) obj);
				}
			}
		}
		return dpList;
	}

	private void validateRequest(GetFieldDataRequest request) {
		if (request.getFieldDataCriteria() == null)
			throw new UnsupportedOperationException("No FieldDataCriteria");
		if (request.getFieldDataCriteria().size() == 0)
			throw new UnsupportedOperationException("No FieldDataCriteria array items");
	}

	/**
	 * @param entity
	 *            to be wrapped into JSON response
	 * @return JSON response with entity wrapped
	 */
	protected Response handleResult(Object entity) {
		ResponseBuilder responseBuilder = Response.status(Status.OK);
		responseBuilder.type(MediaType.APPLICATION_JSON);
		responseBuilder.entity(entity);
		return responseBuilder.build();
	}

}
