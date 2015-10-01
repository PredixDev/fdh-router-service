package com.ge.predix.solsvc.fdh.router.boot;

import java.util.Arrays;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.web.context.support.StandardServletEnvironment;

/**
 * AdhRouter routes traffic to ADH Handlers. Adh Handlers implement the exact same API Interface and
 * can be called directly. But this microservice provides a single point of entry for Analytic Service
 * calls and then the calls fan out based on rules.
 * 
 * @author predix
 */
@Configuration
@EnableAutoConfiguration(exclude =
{
        DataSourceAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class,
        PersistenceExceptionTranslationAutoConfiguration.class
})
@ComponentScan
@ImportResource(
{
        "classpath*:META-INF/spring/predix-rest-client-scan-context.xml",
        "classpath*:META-INF/spring/ext-util-scan-context.xml",
        "classpath*:META-INF/spring/asset-bootstrap-client-scan-context.xml",
        "classpath*:META-INF/spring/timeseries-bootstrap-scan-context.xml",
        "classpath*:META-INF/spring/fdh-router-cxf-context.xml",
        "classpath*:META-INF/spring/fdh-router-scan-context.xml",
        "classpath*:META-INF/spring/fdh-asset-handler-scan-context.xml",
        "classpath*:META-INF/spring/fdh-timeseries-handler-scan-context.xml",
        "classpath*:META-INF/spring/fdh-custom-handler-scan-context.xml"
})
@PropertySource("classpath:application-default.properties")
public class FdhRouterApplication extends PredixSpringBootInitializer
{
    /**
     * @param args -
     */
    @SuppressWarnings(
    {
            "nls", "resource"
    })
    public static void main(String[] args)
    {
        Logger log = LoggerFactory.getLogger(FdhRouterApplication.class);

        SpringApplication springApplication = new SpringApplication(FdhRouterApplication.class);
        ApplicationContext ctx = springApplication.run(args);

        log.debug("Let's inspect the beans provided by Spring Boot:");

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames)
        {
            log.debug(beanName);
        }

        // log.info("Let's inspect the profiles provided by Spring Boot:");
        String profiles[] = ctx.getEnvironment().getActiveProfiles();
        for (int i = 0; i < profiles.length; i++)
            log.debug("profile=" + profiles[i]);

        log.info("Let's inspect the properties provided by Spring Boot:");
        MutablePropertySources propertySources = ((StandardServletEnvironment) ctx.getEnvironment())
                .getPropertySources();
        Iterator<org.springframework.core.env.PropertySource<?>> iterator = propertySources.iterator();
        while (iterator.hasNext())
        {
            Object propertySourceObject = iterator.next();
            if ( propertySourceObject instanceof org.springframework.core.env.PropertySource )
            {
                org.springframework.core.env.PropertySource<?> propertySource = (org.springframework.core.env.PropertySource<?>) propertySourceObject;
                // log.debug("propertySource=" + propertySource.getName() + " values=" + propertySource.getSource() + "class=" + propertySource.getClass());
            }
        }

    }

    /**
     * Add this bean or the @PropertySource above won't kick in
     * 
     * @return -
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer()
    {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
