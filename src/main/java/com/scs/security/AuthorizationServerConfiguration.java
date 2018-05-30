package com.scs.security;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
	
	private static final Logger logger = Logger.getLogger(AuthorizationServerConfiguration.class);
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	@Qualifier("authenticationManagerBean")
	public AuthenticationManager authenticationManager;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
	    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
	    return bCryptPasswordEncoder;
	}
	
//	@Bean
//	public DataSource dataSource() {
//		DriverManagerDataSource dataSource = new DriverManagerDataSource();
//		dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
//		dataSource.setUrl("jdbc:oracle:thin:@10.10.10.212:1521:xe");
//		dataSource.setUsername("apidev");
//		dataSource.setPassword("apidev");
//
//		return dataSource;
//	}
	
	 @Bean
	 @Qualifier("tokenStore")
	 public TokenStore tokenStore() {
	       return new JdbcTokenStore(dataSource);
	    } 
	

	 @Override
     public void configure(AuthorizationServerSecurityConfigurer security)
             throws Exception {
         security.passwordEncoder(passwordEncoder);
   
     }
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		
		clients.jdbc(dataSource);
//		  .passwordEncoder(passwordEncoder) .withClient("AndroidYO")
//		  .authorizedGrantTypes("client_credentials")
//		  .authorities("ROLE_CLIENT") .scopes("trust")
//		  .resourceIds("oauth2-resource") .additionalInformation("1.0")
//		  .redirectUris("NA") .accessTokenValiditySeconds(300)
//		  .secret("tester").autoApprove(true) .and() .withClient("IOsOH")
//		  .authorizedGrantTypes("client_credentials")
//		  .authorities("ROLE_CLIENT") .scopes("trust")
//		  .resourceIds("oauth2-resource") .additionalInformation("1.0")
//		  .redirectUris("NA") .accessTokenValiditySeconds(300)
//		  .secret("tester").autoApprove(true) .and() .withClient("BLACKBERRYhehe")
//		  .authorizedGrantTypes("client_credentials")
//		  .authorities("ROLE_CLIENT") .scopes("trust")
//		  .resourceIds("oauth2-resource") .additionalInformation("1.0")
//		  .redirectUris("NA") .accessTokenValiditySeconds(300)
//		  .secret("tester").autoApprove(true);
	}
	
	

	 
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		    defaultTokenServices.setTokenStore(tokenStore());
		    defaultTokenServices.setSupportRefreshToken(false);
		    defaultTokenServices.setAccessTokenValiditySeconds(86400);
		    defaultTokenServices.setAuthenticationManager(authenticationManager);
		  endpoints.tokenServices(defaultTokenServices);
		 
		  
	}

}