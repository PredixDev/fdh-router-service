package com.ge.predix.solsvc.fdh.router.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.SpringBootWebSecurityConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;


/**
 * 
 * @author predix.adoption@ge.com -
 */
@Configuration
@EnableWebSecurity
@ConditionalOnProperty(prefix = "security.basic", name = "enabled", havingValue = "false")
public class PredixBootSecurityConfig extends WebSecurityConfigurerAdapter{
	@Autowired
	private SecurityProperties security;
	
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		/**
		 * 
		 */
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		if (this.security.isRequireSsl()) {
			http.requiresChannel().anyRequest().requiresSecure();
		}
		if (!this.security.isEnableCsrf()) {
			http.csrf().disable();
		}
		SpringBootWebSecurityConfiguration.configureHeaders(http.headers(),
				this.security.getHeaders());
		
		http.headers().addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy","script-src 'self'")); //$NON-NLS-1$ //$NON-NLS-2$
		
		
		
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		/**
		 * 
		 */
	}

}
