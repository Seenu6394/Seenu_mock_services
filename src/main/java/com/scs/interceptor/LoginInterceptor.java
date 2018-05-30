package com.scs.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.scs.util.SessionUtil;
import com.scs.util.StorageConstants;

public class LoginInterceptor extends HandlerInterceptorAdapter {

private static final Logger logger = Logger.getLogger(LoginInterceptor.class);
	

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		logger.debug("LoginInterceptor  START");

//		if (SessionUtil.objectExists(request.getSession(), StorageConstants.user) ) {
//			User loggedInUser = SessionUtil.getValue(request.getSession(), StorageConstants.user);
//			if (Utility.checkNullEmpty(loggedInUser.getUserId())) {
//				logger.debug("SESSION ACTIVE FOR USER :" + loggedInUser.getUserId());
//				logger.debug("LoginInterceptor  END");
////				throw new ApiException(ErrorConstants.INVALIDREQUEST, "SESSION ACTIVE FOR USER");
//			}
//		} 
		// TODO To be removed in prod
		boolean isMock = SessionUtil.objectExists(request.getSession(), StorageConstants.MOCK);
		if (!isMock) {
			logger.debug(SessionUtil.getSessionId(request.getSession()));
			SessionUtil.clearAll(request);
			SessionUtil.getNewSession(request);
			MDC.put("sessionId", SessionUtil.getSessionId(request.getSession()));
		}
		logger.debug("LoginInterceptor  END");
		return true;
	}
	

}
