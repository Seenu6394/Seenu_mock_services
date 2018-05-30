package com.scs.security;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;

import com.scs.util.ApiConstants;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
	
	private static final Logger logger = Logger.getLogger(ResourceServerConfiguration.class);

	@Override
	public void configure(HttpSecurity http) throws Exception {
		logger.debug("----------------------- Inside ResourceServerConfiguration configure START---------------------------");
		  
		  http
		  .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER).and()
          .authorizeRequests()
//          .antMatchers("/api/**","/static/**","/images/**").permitAll().anyRequest().authenticated();
          .antMatchers(
        		  ApiConstants.API + ApiConstants.API_DOCS).permitAll()
          .anyRequest().authenticated()
          .and()
          .requestMatchers().antMatchers("/api/**").and().authorizeRequests().and().exceptionHandling()
          .accessDeniedHandler(new OAuth2AccessDeniedHandler());
		logger.debug("----------------------- Inside ResourceServerConfiguration configure END---------------------------");
	}

}