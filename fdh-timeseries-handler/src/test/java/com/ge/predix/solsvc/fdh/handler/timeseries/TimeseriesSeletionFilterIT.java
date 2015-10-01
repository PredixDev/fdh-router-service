package com.ge.predix.solsvc.fdh.handler.timeseries;
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

import com.ge.dsp.pm.ext.entity.timeselectionfilter.Datapoint;
import com.ge.dsp.pm.ext.entity.timeselectionfilter.IngestBody;
import com.ge.dsp.pm.ext.entity.timeselectionfilter.PredixTimeseriesIngestion;
import com.ge.dsp.pm.ext.entity.util.map.SimpleMap;
import com.ge.predix.solsvc.bootstrap.tsb.factories.TimeseriesFactory;
import com.google.gson.Gson;

/**
 * 
 * 
 * @author 212421693
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml", 
        "classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml",
        "classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml"       
})
@IntegrationTest(
{
    "server.port=0"
})
@ComponentScan("com.ge.predix.solsvc.restclient")
@ActiveProfiles("local")
public class TimeseriesSeletionFilterIT
{

    private static Logger          log              = LoggerFactory.getLogger(TimeseriesSeletionFilterIT.class);

    
    @Autowired
    private TimeseriesFactory timeseriesFactory;

    /**
     * 
     */
    @SuppressWarnings("nls")
    @Test
    public void testInjection()
    {
        PredixTimeseriesIngestion pti = new PredixTimeseriesIngestion();
        pti.setMessageId("testIngestion");
        IngestBody body = new IngestBody();
        body.setName("theTagName");
        Datapoint dataPoint = new Datapoint();
        dataPoint.getValue().add("time");
        dataPoint.getValue().add("value");
        body.getDatapoints().add(dataPoint);
        SimpleMap entry = new SimpleMap();
        entry.setKey("key");
        body.getAttributes().add(entry);
        pti.getBody().add(body);

        String ingestionRequest = new Gson().toJson(pti);
        log.debug(ingestionRequest);

    }

}
