package com.ge.predix.solsvc.fdh.handler.timeseries;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag;
import com.ge.predix.solsvc.ext.util.JsonMapper;

/**
 * 
 * 
 * @author 212421693
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{
		"classpath*:META-INF/spring/ext-util-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml", 
        "classpath*:META-INF/spring/predix-websocket-client-scan-context.xml",
        "classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml",
        "classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml"       
})
@IntegrationTest(
{
    "server.port=0"
})
@ComponentScan("com.ge.predix.solsvc.restclient")
@ActiveProfiles("local")
public class TimeseriesHandlerGetTest
{

    private static Logger          log              = LoggerFactory.getLogger(TimeseriesHandlerGetTest.class);

    
    @Autowired
    private JsonMapper jsonMapper;

    /**
     * 
     */
    @Test
    public void testInjection()
    {
    }
    
    /**
     * 
     */
    @SuppressWarnings("nls")
    @Test
    public void testDatapointsQueryObjectToJson()
    {
    	DatapointsQuery dpQuery = new DatapointsQuery();
    	Tag tag1 = new Tag();
    	List<Tag> tagList = new ArrayList<Tag>();
    	
    	
    	dpQuery.setStart("START");
    	dpQuery.setEnd("END");
    	
    	tag1.setName("MYTAG");
    	tagList.add(tag1);
    	dpQuery.setTags(tagList);
    	
    	String dpQueryStr = this.jsonMapper.toJson(dpQuery);
    	log.debug("datapoints query as json" + dpQueryStr);
    	
    	Assert.assertNotNull(dpQueryStr);
    	
    }
    
    /**
     * 
     */
    @SuppressWarnings("nls")
    @Test
    public void testDatapointsQueryObjectFromJson()
    {
    	String dpQueryStr = "{\"complexType\":\"DatapointsQuery\",\"start\":\"START\",\"end\":\"END\",\"tags\":[{\"name\":\"MYTAG\",\"limit\":0,\"aggregations\":[],\"groups\":[]}]}";
    	
    	DatapointsQuery dpQuery = this.jsonMapper.fromJson(dpQueryStr, DatapointsQuery.class);
    	
    	log.debug("datapoints query from json, start =" + dpQuery.getStart());
    	
    	Assert.assertEquals(dpQuery.getStart(), "START");
    }

}
