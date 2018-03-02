package com.ge.predix.solsvc.fdh.handler.blobstore;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.ge.predix.entity.putfielddata.PutFieldDataRequest;
import com.ge.predix.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.solsvc.ext.util.JsonMapper;

/**
 * 
 * 
 * @author 212421693
 */

@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = { BlobstorePutDataHandler.class})
@ContextConfiguration(locations =
{
		"classpath*:META-INF/spring/blobstore-bootstrap-scan-context.xml",
		"classpath*:META-INF/SPRING/TEST-fdh-blobstore-handler-properties-context.xml"
})
@ActiveProfiles({"local","blobstore"})
@Ignore
public class BlobstorePutHandlerTestHarness {

	private static final String TEST_FILE = "src/test/resources/sample-test.csv"; //$NON-NLS-1$
	
	private static Logger log = LoggerFactory.getLogger(BlobstorePutHandlerTestHarness.class);

	@Value("${test.data.exchange.url}")
	private String dataExchangeURL;
	
	@Value("${predix.oauth.proxyHost:null}")
	private String proxyHost;
	
	@Value("${predix.oauth.proxyPort:8080}")
	private String proxyPort;
	
	private URL base;
	private RestTemplate template;
	
	
	@Autowired
	private JsonMapper jsonMapper;
	
	@SuppressWarnings("nls")
	@Before
	public void setUp() throws Exception {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		if (this.proxyHost != null) {
		    Proxy proxy= new Proxy(Type.HTTP, new InetSocketAddress(this.proxyHost, Integer.parseInt(this.proxyPort)));
		    requestFactory.setProxy(proxy);
		}
		this.setTemplate(new RestTemplate(requestFactory));
		this.template.getMessageConverters().add(new FormHttpMessageConverter());
		this.template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		this.base = new URL(dataExchangeURL); //$NON-NLS-2$
	}
	/**
	 * 
	 */
	//@Test
	public void testInjection() {
		PutFieldDataRequest request = TestData.getPutFieldDataRequest();
		List<Header> headers = new ArrayList<Header>();//this.restClient.getSecureTokenForClientId();
		MultiValueMap<String, String> multiHeaders = new LinkedMultiValueMap<String, String>();
		for(Header header:headers) {
			if(StringUtils.startsWithIgnoreCase(header.getName(),"authorization")){
				multiHeaders.add(header.getName(), header.getValue());
				break;
			}
		}
		
		multiHeaders.add("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE);
		
		MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
		parts.add("file", new FileSystemResource(TEST_FILE));
		String body = this.jsonMapper.toJson(request);
		parts.add("putfielddata", body);
		
				
		HttpEntity<?> httpEntity = new HttpEntity<Object>(parts , multiHeaders);
		
		

		PutFieldDataResult response = this.template.postForObject(this.base + "/services/fdhrouter/fielddatahandler/putfielddatafile",
				httpEntity, PutFieldDataResult.class);
		log.info("Response : "+response.toString());
		assertThat(response.getErrorEvent(), empty());		
	}
	
	@Test
	public void test123() {
		
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
