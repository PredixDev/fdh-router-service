package com.ge.predix.solsvc.fdh.router;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.asset.Asset;
import com.ge.predix.entity.field.fieldidentifier.FieldSourceEnum;
import com.ge.predix.entity.model.Model;
import com.ge.predix.entity.util.map.Map;
import com.ge.predix.solsvc.bootstrap.ams.dto.Attribute;
import com.ge.predix.solsvc.bootstrap.ams.factories.AssetFactory;
import com.ge.predix.solsvc.fdh.handler.asset.AssetPutDataHandlerImpl;
import com.ge.predix.solsvc.fdh.handler.timeseries.TimeseriesPutDataHandler;

/**
 * @author tturner
 */
@SuppressWarnings(
{
        "nls"
})
@ActiveProfiles({"local"})
@ComponentScan
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {AssetPutFieldDataTest.class, AssetPutDataHandlerImpl.class})
public class AssetPutFieldDataTest extends BaseTest
{
    private static final Logger          log = LoggerFactory.getLogger(AssetPutFieldDataTest.class.getName());
    private static final String HTTP_PAYLOAD_JSON     = "application/json";
    private static final String CONTAINER_SERVER_PORT = "9092";

    
    @Autowired
    private AssetFactory assetFactory;
    /**
     * @throws Exception -
     */
    @Before
    public void onSetUp()
            throws Exception
    {
        //
    }

    /**
     * 
     */
    @After
    public void onTearDown()
    {
        //
    }
    

    /**
     * @throws JMSException -
     * @throws HttpException -
     * @throws IOException -
     * @throws JAXBException -
     */
    @Test
    public void testPut()
            throws  HttpException, IOException, JAXBException
    {
        Long solutionId = 1000l;
        String namespace = "asset";
        String attributeName = "attribute1";
        String fieldId = namespace + "/" + attributeName;
        String fieldName = namespace + "/" + attributeName;
        String fieldSource = FieldSourceEnum.PREDIX_ASSET.name();
       
        double rawDataValue = 52.1d;
        String assetId = "12345";
       
        Date now = new Date();
       
        List<Asset> assets = new ArrayList<Asset>();
        Asset asset = new Asset();
        asset.setAssetId("12345");
        asset.setAttributes(new Map());
        Attribute attribute = new Attribute(); 
        attribute.getValue().add("value");
        asset.getAttributes().put(attributeName,attribute );
        assets.add(asset );
        
        Mockito.when(this.assetFactory.getAssetsByFilter(Matchers.any(), Matchers.anyListOf(Header.class))).thenReturn(assets);
        Mockito.when(this.restClient.hasToken(Matchers.anyListOf(Header.class)))
        .thenReturn(true);
        Mockito.when(this.restClient.hasZoneId(Matchers.anyListOf(Header.class)))
        .thenReturn(true);
        
     /*   ProtocolVersion protoGet = new ProtocolVersion("HTTP", 1, 1);
        BasicStatusLine lineGet = new BasicStatusLine(protoGet, HttpStatus.SC_OK, "test reason for Get");
          HttpResponse responseGet = new BasicHttpResponse(lineGet);
        */
        CloseableHttpResponse responseGet = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(responseGet.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "test reason testNoModelForPostOnPolymporphicClassThatExists"));
      
        HttpEntity entityGet = Mockito.mock(HttpEntity.class);
        responseGet.setEntity(entityGet );
        String bodyGet = "[]";
        InputStream streamGet = new ByteArrayInputStream(bodyGet.getBytes());
        Mockito.when(entityGet.getContent()).thenReturn(streamGet);
        Mockito.when(entityGet.getContentLength()).thenReturn(new Long(bodyGet.length()));


       /* ProtocolVersion protoPut = new ProtocolVersion("HTTP", 1, 1);
        BasicStatusLine linePut = new BasicStatusLine(protoPut, HttpStatus.SC_NO_CONTENT, "test reason");
        HttpResponse responsePut = new BasicHttpResponse(linePut);*/
        
        CloseableHttpResponse responsePut = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(responsePut.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_NO_CONTENT, "test reason"));
     
       
        HttpEntity entityPut = Mockito.mock(HttpEntity.class);
        responsePut.setEntity(entityPut );
        Mockito.when(this.restClient.put(Matchers.anyString(), Matchers.anyString(), Matchers.anyListOf(Header.class), Matchers.anyInt(), Matchers.anyInt()))
        .thenReturn(responsePut);
        
        Asset model = new Asset();
        model.setAdditionalAttributes(new Map());
        model.setDescription("desc");
        model.getAdditionalAttributes().put("anAttribute", attribute);
        ArrayList<Model> models = new ArrayList<Model>();
        models.add(model);
        String bodyGet2 = this.jsonMapper.toJson(models);
        //String bodyGet2 = "[{\"complexType\":\"Model\",\"additionalAttributes\":{\"keys\":[\"key\"],\"values\":[\"value\"]}}]";
        //[{"complexType":"Asset","additionalAttributes":{"anAttribute":{"complexType":"Attribute","enumeration":[],"value":["value"]}},"description":"desc"}]
      //  BasicStatusLine lineGet2 = new BasicStatusLine(protoGet, HttpStatus.SC_OK, "test reason for Get2");
       // HttpResponse responseGet2 = new BasicHttpResponse(lineGet2);
        
        InputStream streamGet2 = new ByteArrayInputStream(bodyGet2.getBytes());
        
        CloseableHttpResponse responseGet2 = Mockito.mock(CloseableHttpResponse.class);
        Mockito.when(responseGet2.getStatusLine()).thenReturn(new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), HttpStatus.SC_OK, "test reason for Get2"));
        
      
        HttpEntity entityGet2 = Mockito.mock(HttpEntity.class);
        responseGet2.setEntity(entityGet2 );    
        Mockito.when(entityGet2.getContent()).thenReturn(streamGet2);
        Mockito.when(entityGet2.getContentLength()).thenReturn(new Long(bodyGet2.length()));
        
        Answer<?> answerGet = new Answer<Object>() {
            private int count = 1;

            @Override
            public Object answer(InvocationOnMock invocation) {
                if (this.count++ == 1)
                    return responseGet;

                return responseGet2;
            }
        };
        Mockito.when(this.restClient.get(Matchers.anyString(), Matchers.anyListOf(Header.class), Matchers.anyInt(), Matchers.anyInt()))
        .thenAnswer(answerGet);       
        //make the call
        
        Mockito.when(responseGet.getEntity()).thenReturn(entityGet);
        Mockito.when(responseGet2.getEntity()).thenReturn(entityGet2);
        putFieldData(solutionId, fieldId, fieldName, fieldSource, rawDataValue, assetId, now, log,
                HTTP_PAYLOAD_JSON, CONTAINER_SERVER_PORT);

    }

    

}
