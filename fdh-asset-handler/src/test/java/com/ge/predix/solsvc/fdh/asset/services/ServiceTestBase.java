package com.ge.predix.solsvc.fdh.asset.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ge.predix.solsvc.bootstrap.ams.common.AssetConfig;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.fdh.handler.asset.helper.FileUtils;
import com.ge.predix.solsvc.restclient.impl.RestClient;

/**
 * 
 * @author 212325745
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{
		"classpath*:META-INF/spring/predix-rest-client-scan-context.xml", 
		"classpath*:META-INF/spring/predix-rest-client-sb-properties-context.xml",
        "classpath*:META-INF/spring/ext-util-scan-context.xml",
        "classpath*:META-INF/spring/fdh-asset-handler-scan-context.xml",
        "classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml"

})
@ActiveProfiles("local")
public abstract class ServiceTestBase extends AbstractJUnit4SpringContextTests
{

    /**
     * 
     */
    @Autowired
    protected AssetConfig assetConfig;

    /**
     * 
     */
    @Mock
    protected RestClient restClient;

    @Autowired
    private JsonMapper jsonMapper;

    /**
     * @throws java.lang.Exception -
     */
    @Before
    public void setUp()
            throws Exception
    {
        // make sure the correct RestClient is wired to serviceBase
        // It gets changed by mock testing PredixAssetClient
        MockitoAnnotations.initMocks(this);
    }
    
    /**
     * @param clazz -
     * @param resourceName -
     * @return -
     * @throws IOException -
     */
    protected <T> List<T> loadFromResource(Class<T> clazz, String resourceName) throws IOException
    {
        String jsonString = FileUtils.readFile(resourceName);
        List<T> assets = this.jsonMapper.fromJsonArray(jsonString, clazz);
        return assets;

//        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//        InputStream input = classLoader.getResourceAsStream(resourceName);
//        ObjectMapper mapper = new ObjectMapper();
//        List<T> classifications = new ArrayList<>();
//        try
//        {
//            JsonNode outerNode = mapper.readTree(input);
//            List<JsonNode> elements = Lists.newArrayList(outerNode.getElements());
//            for (JsonNode node : elements)
//            {
//                T cl = mapper.readValue(node, clazz);
//                classifications.add(cl);
//            }
//        }
//        catch (IOException e)
//        {
//            classifications.clear();
//        }
        //return classifications;
    }

    /**
     * @param resourceName -
     * @return -
     */
    @SuppressWarnings("resource")
    protected String readTextFromResource(String resourceName)
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream(resourceName);
        return convertStreamToString(input);
    }

    /**
     * @param is -
     * @return -
     */
    @SuppressWarnings("nls")
    protected String convertStreamToString(java.io.InputStream is)
    {
        @SuppressWarnings("resource")
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        String str = s.hasNext() ? s.next() : "";
        s.close();
        return str;
    }
}
