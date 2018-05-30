package com.scs.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.scs.exception.ApiException;
import com.scs.util.SessionUtil;
import com.scs.util.StorageConstants;


public class MockResponseInterceptor extends HandlerInterceptorAdapter {
	

	private static final Logger logger = Logger.getLogger(MockResponseInterceptor.class);

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException, ApiException  {
		
		logger.debug("MOCK INTERCEPTOR RESPONSE START : mock flag : " + request.getHeader("mock"));
		//SessionUtil.setValue(request.getSession(), StorageConstants.mockPath, System.getProperty(ApiConstants.INIT_DIR)+"/servers/MobileServer/logs/mock");
		SessionUtil.setValue(request.getSession(), StorageConstants.MOCK_PATH, "D:/mock");

		if (request.getHeader("mock") == null || "false".equalsIgnoreCase(request.getHeader("mock"))) {
			SessionUtil.setValue(request.getSession(), StorageConstants.MOCK, null);
			logger.debug("MOCK INTERCEPTOR RESPONSE END");
			return true;
		} else if ("true".equalsIgnoreCase(request.getHeader("mock"))) {
			logger.debug("MOCK RESPONSE FILE");
			if(!SessionUtil.objectExists(request.getSession(), StorageConstants.MOCK)){
				SessionUtil.setValue(request.getSession(), StorageConstants.MOCK, true);
			}
			
			logger.debug("MOCK INTERCEPTOR RESPONSE END");
		}
		return true;
	}
		
    
}