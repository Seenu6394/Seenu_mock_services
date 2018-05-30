package com.scs.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scs.interceptor.ApiInterceptor;
import com.scs.interceptor.LoginInterceptor;
import com.scs.interceptor.MockResponseInterceptor;
import com.scs.interceptor.SessionInterceptor;
import com.scs.listener.SessionCreatedListener;
import com.scs.listener.SessionDestroyedListener;
import com.scs.util.ApiConstants;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.scs")
@PropertySource("classpath:config.properties")
public class ApplicationConfiguration extends WebMvcConfigurerAdapter  {
	
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.defaultContentType(MediaType.APPLICATION_JSON);
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(apiInterceptor()).addPathPatterns("/**");
		registry.addInterceptor(loginInterceptor()).addPathPatterns("/api/getCustomer");
		//registry.addInterceptor(mockResponseInterceptor()).addPathPatterns("/**");
		registry.addInterceptor(sessionInterceptor())
		.addPathPatterns("/api/get*","/api/balance","/api/mePay")
		.excludePathPatterns("/api/getCustomer");		
	}

	
	
	/* Interceptor Stack */

	@Bean
	ApiInterceptor apiInterceptor() {
		return new ApiInterceptor();
	}
	
	@Bean
	MockResponseInterceptor mockResponseInterceptor() {
		return new MockResponseInterceptor();
	}
	
	@Bean
	LoginInterceptor loginInterceptor() {
		return new LoginInterceptor();
	}
	
	@Bean
	SessionInterceptor sessionInterceptor() {
		return new SessionInterceptor();
	}
	
	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
	
//	private ClientHttpRequestFactory clientHttpRequestFactory() {
//		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
//		factory.setReadTimeout(180000);
//		factory.setConnectTimeout(180000);
//		return factory;
//	}
	
	@Bean
	public SessionCreatedListener sessionCreatedListener() {
	    return new SessionCreatedListener();
	}
	
	@Bean SessionDestroyedListener sessionDestroyedListener(){
		return new SessionDestroyedListener();
	}
	
	@Bean
	public ObjectMapper mapper(){
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		return mapper;
	}
	
	@Bean
	public ResourceBundleMessageSource messageSource(){
		
		ResourceBundleMessageSource msgSource = new ResourceBundleMessageSource();
		msgSource.setBasename(ApiConstants.APP_MESSAGES_RESOURCE_BASENAME);
		return msgSource;
	}
	
	

	@Bean
    public ViewResolver getViewResolver(){
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }
	
	@Bean
	public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		sessionFactory.setPackagesToScan(new String[] { "com.scs.entity.model"});
		sessionFactory.setHibernateProperties(getHibernateProperties());
		return sessionFactory;
	}
	 
	private Properties getHibernateProperties() {
		Properties properties = new Properties();
		properties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.Oracle10gDialect");
		properties.put(AvailableSettings.SHOW_SQL, false);
		properties.put(AvailableSettings.HBM2DDL_AUTO, "update");
		properties.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "org.springframework.orm.hibernate5.SpringSessionContext");
		return properties;
	}
	
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		dataSource.setUrl("jdbc:oracle:thin:@10.10.10.212:1521:xe");
		dataSource.setUsername("apidev");
		dataSource.setPassword("apidev");

		return dataSource;
	}
	
//	@Bean(name = "SCSDataSource" , destroyMethod="")
//	public DataSource dataSource() {
//	    JndiDataSourceLookup lookup = new JndiDataSourceLookup();
//        lookup.setResourceRef(true);
//	    return lookup.getDataSource("SCSDB");
//	}
	
	
	@Bean
	public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(sessionFactory);
		return txManager;
	}
	
	
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) { 
	        configurer.enable();
	}

	@Bean
    public CommonsMultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();

    }


}