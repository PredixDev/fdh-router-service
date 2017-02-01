package com.ge.predix.solsvc.fdh.router.boot;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

/**
 * 
 * Use Tomcat not Jetty.  CF has components and plugins that use Tomcat 
 * so in case these are needed by your app, using Tomcat instead of Jetty 
 * 
 * @author tturner
 *
 */
@ImportResource(
{
    "classpath*:META-INF/spring/predix-boot-scan-context.xml",
    "classpath*:META-INF/spring/predix-boot-cxf-context.xml"
       
})
public class PredixSpringBootInitializer
{

    /**
     * Ensure the Tomcat container comes up, not the Jetty one.
     * @return - the factory
     */
    @Bean
    public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory()
    {
        return new TomcatEmbeddedServletContainerFactory();
    }

    /**
     * Spin up a CXFServlet and register the url beyond which CXF will parse and direct traffic to
     * Predix in CF uses "services" plural as the standard URL.  
     * 
     * @return -
     */
    /**
     * @return -
     */
    @SuppressWarnings({"nls"})
    @Bean
    public ServletRegistrationBean servletRegistrationBean()
    {
        return new ServletRegistrationBean(new CXFServlet(), "/services/*");
    }

}
