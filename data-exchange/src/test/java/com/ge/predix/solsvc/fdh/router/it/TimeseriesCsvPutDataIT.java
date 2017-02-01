package com.ge.predix.solsvc.fdh.router.it;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.http.Header;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.ge.predix.entity.field.Field;
import com.ge.predix.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.predix.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.predix.entity.fielddata.FieldData;
import com.ge.predix.entity.filter.Filter;
import com.ge.predix.entity.putfielddata.PutFieldDataCriteria;
import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.handler.asset.AssetPutDataHandlerImpl;
import com.ge.predix.solsvc.fdh.handler.timeseries.TimeseriesPutDataHandler;
import com.ge.predix.solsvc.fdh.router.boot.FdhRouterApplication;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * Spins up Spring Boot and accesses the URLs of the Rest apis
 * 
 * @author predix
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { FdhRouterApplication.class, AssetPutDataHandlerImpl.class, TimeseriesPutDataHandler.class })
@WebAppConfiguration
@IntegrationTest({"server.port=9092"})
public class TimeseriesCsvPutDataIT{
	private static final Logger logger = LoggerFactory
			.getLogger(TimeseriesCsvPutDataIT.class);

	private static final String TEST_FILE = "src/test/resources/sample-test.csv"; //$NON-NLS-1$

	@Value("${local.server.port}")
	private int localServerPort;

	private URL base;
	private RestTemplate template;

	
	@Autowired
	private JsonMapper jsonMapper;
	
	@Autowired
	private RestClient restClient;

	/**
	 * @throws Exception
	 * -
	 */
	@SuppressWarnings("nls")
	@Before
	public void setUp() throws Exception {
		this.setTemplate(new TestRestTemplate());
		this.template.getMessageConverters().add(new FormHttpMessageConverter());
		this.template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		this.base = new URL(
				"http://localhost:" + this.localServerPort); //$NON-NLS-2$
	}

	/**
	 * @throws Exception
	 *             -
	 */
	@SuppressWarnings({ "nls" })
	@Test
	@Ignore
	public void getUploadCSV() throws Exception {
		
		List<Header> headers = this.restClient.getSecureTokenForClientId();
		MultiValueMap<String, String> multiHeaders = new LinkedMultiValueMap<String, String>();
		for(Header header:headers) {
			if(StringUtils.startsWithIgnoreCase(header.getName(),"authorization")){
				multiHeaders.add(header.getName(), header.getValue());
				break;
			}
		}
		
		multiHeaders.add("Content-Type", MediaType.MULTIPART_FORM_DATA);
		
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", new FileSystemResource(TEST_FILE));
		String body = this.jsonMapper.toJson(createNewPutRequest());
		logger.info("payload to the upload "+body);
		parts.add("putfielddata", body);
		
				
		HttpEntity<?> httpEntity = new HttpEntity<Object>(parts , multiHeaders);
		
		

		PutFieldDataResult response = this.template.postForObject(this.base + "/services/fdhrouter/fielddatahandler/putfielddatafile",
				httpEntity, PutFieldDataResult.class);
		assertThat(response.getErrorEvent(), empty());

	}

	private PutFieldDataRequest createNewPutRequest() {
		PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
		
		PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();
		List<Field> fields = new ArrayList<Field>();
		
		FieldData fieldData = new FieldData();
		Field field = new Field();
		FieldIdentifier fieldIdentifier = new FieldIdentifier();
		fieldIdentifier.setSource(FieldSourceEnum.PREDIX_TIMESERIES.name());
		field.setFieldIdentifier(fieldIdentifier);		
		fields.add(field);

		field = new Field();
		fieldIdentifier = new FieldIdentifier();
		fieldIdentifier.setSource("handler/webSocketHandler");
		field.setFieldIdentifier(fieldIdentifier);
		fields.add(field);
		
		fieldData.setField(fields);
		
		Filter selectionFilter = new Filter();
		fieldDataCriteria.setFilter(selectionFilter);
		fieldDataCriteria.setFieldData(fieldData);
		putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria);

		return putFieldDataRequest;
	}

	/**
	 * @return the template
	 */
	public RestTemplate getTemplate() {
		return this.template;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(RestTemplate template) {
		this.template = template;
	}
}