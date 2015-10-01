package com.ge.predix.solsvc.boot.spring;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mimosa.osacbmv3_3.DMDataSeq;
import org.mimosa.osacbmv3_3.DataEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.ge.dsp.pm.ext.entity.asset.Asset;
import com.ge.dsp.pm.ext.entity.field.fieldidentifier.FieldIdentifier;
import com.ge.dsp.pm.ext.entity.fielddata.FieldData;
import com.ge.dsp.pm.ext.entity.fielddata.OsaData;
import com.ge.dsp.pm.ext.entity.fieldidentifiervalue.FieldIdentifierValue;
import com.ge.dsp.pm.ext.entity.fieldselection.FieldSelection;
import com.ge.dsp.pm.ext.entity.selectionfilter.FieldSelectionFilter;
import com.ge.dsp.pm.ext.entity.solution.identifier.solutionidentifier.SolutionIdentifier;
import com.ge.dsp.pm.fielddatahandler.entity.fielddatacriteria.FieldDataCriteria;
import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.getfielddata.GetFieldDataResult;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataCriteria;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataRequest;
import com.ge.dsp.pm.fielddatahandler.entity.putfielddata.PutFieldDataResult;
import com.ge.predix.solsvc.fdh.router.boot.FdhRouterApplication;

/**
 * 
 * @author predix
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FdhRouterApplication.class)
@WebAppConfiguration
@IntegrationTest({"server.port=9092"})
public class HelloControllerIT {

    @SuppressWarnings("nls")
    private static final String SOLUTION_ID = "1001";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        
                                                                                                                                                                                                                                                                                                                                                                                                                                                            
    @Value("${local.server.port}")
    private int localServerPort;

	private URL base;
	private RestTemplate template;

	/**
	 * @throws Exception -
	 */
	@Before
	public void setUp() throws Exception {
		this.template = new TestRestTemplate();
	}

	/**
	 * @throws Exception -
	 */
	@SuppressWarnings("nls")
    @Test
	public void getHello() throws Exception {
		this.base = new URL("http://localhost:" + this.localServerPort + "/");
		ResponseEntity<String> response = this.template.getForEntity(this.base.toString(), String.class);
		assertThat(response.getBody(), startsWith("Greetings from Predix Boot!"));

		this.base = new URL("http://localhost:" + this.localServerPort + "/services/ping");
		response = this.template.getForEntity(this.base.toString(), String.class);
		assertThat(response.getBody(), startsWith("Greetings from CXF Bean Rest Service"));

		this.base = new URL("http://localhost:" + this.localServerPort + "/services/dynamicservice/dynamic");
		response = this.template.getForEntity(this.base.toString(), String.class);
		assertThat(response.getBody(), startsWith("Greetings from Self Registering Cloud Service"));

//		this.base = new URL("http://localhost:" + localServerPort + "/service/adapterservice/adapt/canary");
//		response = template.getForEntity(base.toString(), String.class);
//		assertThat(response.getBody(), startsWith("Chirp"));
		
//		FieldHandler fieldHandler = mock(FieldHandler.class);
//		List<Field> fields = new ArrayList<Field>();
//		Field field = new Field();
//		field.setFieldId("testField");
//		fields.add(field );
////		when(fieldHandler.findFieldAndStrategiesAndDetails(anyString())).thenReturn(fields);
//		this.base = new URL("http://localhost:" + localServerPort + "/service/fieldservice/fields/4");
//		response = template.getForEntity(base.toString(), String.class);
//		assertThat(response.getBody(), startsWith("[{\"tenantIdentifier"));
//		
//		this.base = new URL("http://localhost:" + localServerPort + "/service/navcontextservice/navcontexts/1");
//		response = template.getForEntity(base.toString(), String.class);
//		assertThat(response.getBody(), startsWith("[{\"navigationContextId"));
		
		
	}
	
	

	/**
	 * @throws MalformedURLException -
	 */
	@SuppressWarnings("nls")
    @Test
	public void testGetFieldData() throws MalformedURLException {
		
		GetFieldDataRequest request = getFieldDataRequest();
		
		this.base = new URL("http://localhost:" + this.localServerPort + "/service/fdhrouter/fielddatahandler/getfielddata");

		ResponseEntity<GetFieldDataResult> response = this.template.postForEntity(this.base.toString(), request, GetFieldDataResult.class);
		
		
		Assert.assertNotNull(response);
	}
	
	/**
	 * @throws MalformedURLException -
	 */
	@SuppressWarnings("nls")
    @Test
	public void testPutFieldData() throws MalformedURLException {
		
		PutFieldDataRequest request = putFieldDataRequest();
		
		this.base = new URL("http://localhost:" + this.localServerPort + "/service/fdhrouter/fielddatahandler/putfielddata");

		ResponseEntity<PutFieldDataResult> response = this.template.postForEntity(this.base.toString(), request, PutFieldDataResult.class);
		
		
		Assert.assertNotNull(response);
	}
	
	/**
     * @return
     */
    @SuppressWarnings("nls")
    private PutFieldDataRequest putFieldDataRequest()
    {
        PutFieldDataRequest putFieldDataRequest = new PutFieldDataRequest();
        FieldData fieldData = new FieldData();
        com.ge.dsp.pm.ext.entity.field.Field field = new com.ge.dsp.pm.ext.entity.field.Field();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId("TANK_SENSOR_HEIGHT");
        field.setFieldIdentifier(fieldIdentifier );
        fieldData.setField(field );
        DataEvent dataEvent = new DMDataSeq();
		OsaData osaData = new OsaData();
		osaData.setDataEvent(dataEvent);
        fieldData.setData(osaData );
        PutFieldDataCriteria fieldDataCriteria = new PutFieldDataCriteria();
        fieldDataCriteria.setFieldData(fieldData);
        putFieldDataRequest.getPutFieldDataCriteria().add(fieldDataCriteria );
        FieldSelectionFilter selectionFilter = new FieldSelectionFilter();
        FieldIdentifierValue fieldIdentifierValue = new FieldIdentifierValue();
        FieldIdentifier assetIdFieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId("/asset/assetId");
        fieldIdentifierValue.setFieldIdentifier(assetIdFieldIdentifier);
        fieldIdentifierValue.setValue("123");
        selectionFilter.getFieldIdentifierValue().add(fieldIdentifierValue);
        fieldDataCriteria.setSelectionFilter(selectionFilter );
        return putFieldDataRequest;
    }
	
    @SuppressWarnings("nls")
    private GetFieldDataRequest getFieldDataRequest()
    {
        GetFieldDataRequest getFieldDataRequest = new GetFieldDataRequest();
        SolutionIdentifier solutionIdentifier = new SolutionIdentifier();
        solutionIdentifier.setId(SOLUTION_ID);
        getFieldDataRequest.setSolutionIdentifier(solutionIdentifier );  
        FieldDataCriteria fieldDataCriteria = new FieldDataCriteria();
        FieldSelection fieldSelection = new FieldSelection();
        FieldIdentifier fieldIdentifier = new FieldIdentifier();
        fieldIdentifier.setId("MPG");
        fieldSelection.setFieldIdentifier(fieldIdentifier);
        fieldDataCriteria.getFieldSelection().add(fieldSelection );
        getFieldDataRequest.getFieldDataCriteria().add(fieldDataCriteria );
        return getFieldDataRequest;
    }
}
